package com.example.coronamap.main

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.example.coronamap.R

import com.example.coronamap.model.CoronaMapModel

import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapView
import net.daum.mf.map.api.MapPoint

import java.security.MessageDigest

import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection.HTTP_OK
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class MainActivity : AppCompatActivity(), MapView.MapViewEventListener, MapView.POIItemEventListener {
    private val results by lazy { Realm.getDefaultInstance().where(CoronaMapModel::class.java).findAll() }

    private val mapView by lazy {
        val mapView = MapView(this)
        mapView.setDaumMapApiKey("4d2275ee6b9b8f3088a50f11e8d11392")
        val mapPoint = MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633)
        mapView.setMapCenterPoint(mapPoint, true)
        mapView.setMapViewEventListener(this)
        mapView.setPOIItemEventListener(this)
        mapView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * 해쉬 키 캆 얻기
         */
        //getHash();

        /**
         * MapView 기본 세팅
         */
        map_view.addView(mapView)

        updateMarker()
    }

    /**
     * DataBase 에 저장된 위치 값 마커로 표현하기
     *
     */
    private fun updateMarker() {
        mapView.removeAllPOIItems()
        if (results.isEmpty()) return

        val pois = results.mapNotNull {
            //더블로 변환 , 못하면 널 리턴
            val latitude = it.locationX ?: return@mapNotNull null
            val longitude = it.locationY ?: return@mapNotNull null
            val address = it.keyword
            //apply 는? apply
            MapPOIItem().apply {
                itemName = "$address"
                tag = 0
                mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude)
                markerType = MapPOIItem.MarkerType.RedPin
            }
        }
        mapView.addPOIItems(pois.toTypedArray())
    }

//
//    class SaveTask : AsyncTask<Any, Void, MainActivity>() {
//        override fun doInBackground(vararg params: Any?): MainActivity {
//            val latitude = params[0] as Double
//            val longitude = params[1] as Double
//            val address = MapTask().executeOnExecutor(THREAD_POOL_EXECUTOR, latitude, longitude)
//
//            Realm.getDefaultInstance().executeTransaction { realm ->
//                val maxId = realm.where<CoronaMapModel>(CoronaMapModel::class.java).max("id")
//
//                val nextId = if (maxId == null) 1 else maxId.toInt() + 1
//
//                val coronaMapModel = realm.createObject<CoronaMapModel>(CoronaMapModel::class.java, nextId)
//                coronaMapModel.locationX = latitude.toString()
//                coronaMapModel.locationY = longitude.toString()
//                coronaMapModel.keyword = address.toString()
//            }
//
//            return (params[2] as MainActivity)
//        }
//
//
//        override fun onPostExecute(result: MainActivity) {
//            result.updateMarker()
//        }
//    }
//
//
//    class MapTask : AsyncTask<Any, Void, String>() {
//        private var resultString: String? = ""
//        override fun doInBackground(vararg params: Any?): String? {
//            Log.d("task : ", "task")
//            val domain = "https://dapi.kakao.com/v2/local/geo/coord2address.json"
//            var builder: Uri.Builder = Uri.parse(domain).buildUpon()
//
//            builder = addCurrentLocationQueryAt(builder, params[0] as Double, params[1] as Double)
//
//            val requestURL = URL(builder.build().toString())
//
//            val connection: HttpsURLConnection = requestURL.openConnection() as HttpsURLConnection
//            connection.setRequestProperty("Authorization", "KakaoAK c9ac7f985bf8eb720188b94251d4cf66")
//
//            return when (connection.responseCode) {
//                HTTP_OK -> {
//                    val reader = InputStreamReader(connection.inputStream, "UTF-8")
//                    val bufferedReader = BufferedReader(reader)
//                    val buffer = StringBuffer()
//
//                    var str: String? = ""
//
//                    while (str != null) {
//                        str = bufferedReader.readLine()
//                    }
//                    resultString = buffer.toString()
//                    return resultString
//                }
//                else -> return "error"
//            }
//        }
//
//        override fun onPostExecute(resultString: String?) {
//            super.onPostExecute(resultString)
//
//            handleSearchResult(resultString)
//        }
//
//        private fun handleSearchResult(resultString: String?): String? {
//            val jsonArray = JSONObject(resultString).getJSONArray("documents")
//            var getAddress: String? = ""
//            //let null 이 아닌 경우에만 실행
//            jsonArray.getJSONObject(jsonArray.length() - 1)?.let { jsonObject ->
//                getAddress = jsonObject.optString("address_name")
//            }
//            return getAddress
//        }
//
//        private fun addCurrentLocationQueryAt(builder: Uri.Builder, latitude: Double, longitude: Double): Uri.Builder {
//            builder.apply {
//                appendQueryParameter("x", "$latitude")
//                appendQueryParameter("y", "$longitude")
//            }
//            return builder
//        }
//    }

    //////////// implements MapView.MapViewEventListener
    override fun onMapViewInitialized(mapView: MapView) {
        updateMarker()
    }

    override fun onMapViewLongPressed(mapView: MapView, mapPoints: MapPoint) {
        /**
         * LongPress 이벤트 콜백 시 해당 위치 BlueMarker 로 표시
         */
        var latitude = mapPoints.mapPointGeoCoord.latitude
        var longitude = mapPoints.mapPointGeoCoord.longitude

        val point = MapPOIItem().apply {
            itemName = "X:$latitude Y:$longitude"
            tag = 0
            mapPoint = MapPoint.mapPointWithCONGCoord(latitude, longitude)
            markerType = MapPOIItem.MarkerType.BluePin
        }

        mapView.addPOIItem(point)
        /**
         * DataBase 여부 AlertDialog
         */
        AlertDialog.Builder(this)
                .setTitle("위치 저장 여부")
                .setMessage("X : $latitude Y : $longitude \n위치를 저장 하시겠습니까?")
                .setPositiveButton("OK") { _, _ ->
                    getAddress(latitude, longitude, completion = { address ->
                        savePOI(latitude, longitude, address, completion = { mapPOI ->
                            updateMarker()
                        })
                    })
                }
                .setNegativeButton("CANCEL") { _, _ -> mapView.removePOIItem(point) }
                .create()
                .show()
    }

    private fun getAddress(latitude: Double, longitude: Double, completion: (address: String) -> Unit) {
        var resultAddress = ""
        GlobalScope.launch(Dispatchers.Default) {
            fun addCurrentLocationQueryAt(builder: Uri.Builder, latitude: Double, longitude: Double): Uri.Builder {
                builder.apply {
                    appendQueryParameter("x", "$latitude")
                    appendQueryParameter("y", "$longitude")
                }
                return builder
            }
            val domain = "https://dapi.kakao.com/v2/local/geo/coord2address.json"
            var builder: Uri.Builder = Uri.parse(domain).buildUpon()

            builder = addCurrentLocationQueryAt(builder, longitude, latitude)

            val requestURL = URL(builder.build().toString())

            val connection: HttpsURLConnection = requestURL.openConnection() as HttpsURLConnection
            connection.setRequestProperty("Authorization", "KakaoAK c9ac7f985bf8eb720188b94251d4cf66")

            val address = when (connection.responseCode) {
                HTTP_OK -> {
                    val reader = InputStreamReader(connection.inputStream, "UTF-8")
                    val bufferedReader = BufferedReader(reader)
                    val buffer = StringBuffer()

                    var str: String? = ""

                    while ({ str = bufferedReader.readLine(); str }() != null) {
                        buffer.append(str)
                    }
                    buffer.toString()
                }
                else -> "error"
            }
            Log.e("!!!", address)

//            val jsonObject = JSONObject(address)
//            Log.e("jsonObject", jsonObject.toString())
//
//            val upperArray = jsonObject.getJSONArray("documents")
//            Log.e("upperArray", upperArray.toString())
//
//            val upperObject = upperArray.getJSONObject(0)
//            Log.e("upperObject", upperObject.toString())
//
//            val a = upperObject.getJSONObject("address")
//            Log.e("a", a.toString())
//
//            resultAddress = a.getString("address_name")

            resultAddress = JSONObject(address).getJSONArray("documents").getJSONObject(0).getJSONObject("address").getString("address_name")
            Log.e("resultAddress", resultAddress)
            GlobalScope.launch(Dispatchers.Main) {
                completion(resultAddress)
            }
        }
    }

    private fun savePOI(latitude: Double, longitude: Double, address: String, completion: (savedModel: CoronaMapModel) -> Unit) {
        //default, io 백그라운드 & main 메인스레드
        var coronaMapModel: CoronaMapModel? = null
        Realm.getDefaultInstance().executeTransaction { realm ->
            val maxId = realm.where<CoronaMapModel>(CoronaMapModel::class.java).max("id")
            val nextId = if (maxId == null) 1 else maxId.toInt() + 1

            coronaMapModel = realm.createObject<CoronaMapModel>(CoronaMapModel::class.java, nextId)
            coronaMapModel?.locationX = latitude
            coronaMapModel?.locationY = longitude
            coronaMapModel?.keyword = address
        }
        GlobalScope.launch(Dispatchers.Main) {
            // 여기는 메인 스레드
            completion(Realm.getDefaultInstance().copyFromRealm(coronaMapModel!!))
        }
    }

    override fun onMapViewCenterPointMoved(mapView: MapView, mapPoint: MapPoint) { /** do nothing **/ }
    override fun onMapViewZoomLevelChanged(mapView: MapView, i: Int) { /** do nothing **/ }
    override fun onMapViewSingleTapped(mapView: MapView, mapPoint: MapPoint) { /** do nothing **/ }
    override fun onMapViewDoubleTapped(mapView: MapView, mapPoint: MapPoint) { /** do nothing **/ }
    override fun onMapViewDragStarted(mapView: MapView, mapPoint: MapPoint) { /** do nothing **/ }
    override fun onMapViewDragEnded(mapView: MapView, mapPoint: MapPoint) { /** do nothing **/ }
    override fun onMapViewMoveFinished(mapView: MapView, mapPoint: MapPoint) { /** do nothing **/ }

    ////////////  implements MapView.POIItemEventListener
    override fun onPOIItemSelected(mapView: MapView, mapPOIItem: MapPOIItem) {
        AlertDialog.Builder(this)
                .setTitle("삭제 여부")
                .setMessage("X : " + mapPOIItem.mapPoint.mapPointGeoCoord.latitude + ", \n Y : " + mapPOIItem.mapPoint.mapPointGeoCoord.longitude + "\n 위치를 삭제하시겠습니까?")
                .setPositiveButton("삭제") { _, _ ->
                    Realm.getDefaultInstance().executeTransaction { realm ->
                        val coordinate = mapPOIItem.mapPoint.mapPointGeoCoord
                        val findResult = realm.where<CoronaMapModel>(CoronaMapModel::class.java)
                                .equalTo("locationX", coordinate.latitude.toString())
                                .equalTo("locationY", coordinate.longitude.toString())
                                .findAll()
                        findResult.deleteAllFromRealm()
                    }
                    updateMarker()
                    Toast.makeText(applicationContext, "성공", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("취소") { _, _ -> }
                .create()
                .show()
    }

    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView, mapPOIItem: MapPOIItem) {}
    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView, mapPOIItem: MapPOIItem, calloutBalloonButtonType: MapPOIItem.CalloutBalloonButtonType) {}
    override fun onDraggablePOIItemMoved(mapView: MapView, mapPOIItem: MapPOIItem, mapPoint: MapPoint) {}

    /***************
     * API 를 위한 키 값 얻어오는 함수
     */
    private fun getHash() {
        try {
            val packageInfoCompat = packageManager.getPackageInfo("com.example.coronamap", PackageManager.GET_SIGNATURES)

            for (signature in packageInfoCompat.signatures) {

                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("hash", "key : " + Base64.encodeToString(md.digest(), Base64.DEFAULT))

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


