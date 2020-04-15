package com.example.coronamap.data

import android.net.Uri
import android.util.Log
import com.example.coronamap.model.CoronaLocationModel
import com.example.coronamap.model.CoronaMapModel
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


object LocationData {

    val locations: ArrayList<String> = arrayListOf("busan", "chungbuk", "chungnam", "daegu", "daejeon", "gangwon", "gwangju", "gyeongbuk", "gyeonggi", "gyeongnam", "incheon", "jeju", "jeonbuk", "jeonnam", "sejong", "seoul", "ulsan")
    val locationsForXandY: Map<Int, ArrayList<Double>> = mapOf(
            Pair(0, arrayListOf(35.17944, 129.07556)),
            Pair(1, arrayListOf(36.64389, 127.48944)),
            Pair(2, arrayListOf(36.27639, 126.91111)),
            Pair(3, arrayListOf(35.87222, 128.60250)),
            Pair(4, arrayListOf(36.35111, 127.38500)),
            Pair(5, arrayListOf(37.75000, 128.88333)),
            Pair(6, arrayListOf(37.41750, 127.25639)),
            Pair(7, arrayListOf(36.35306, 128.69722)),
            Pair(8, arrayListOf(37.54167, 127.20972)),
            Pair(9, arrayListOf(35.27500, 128.40833)),
            Pair(10, arrayListOf(37.456111, 126.705278)),
            Pair(11, arrayListOf(33.566667, 126.166667)),
            Pair(12, arrayListOf(35.82500, 127.15000)),
            Pair(13, arrayListOf(35.01611, 126.71083)),
            Pair(14, arrayListOf(36.48750, 127.28167)),
            Pair(15, arrayListOf(37.56667, 126.97806)),
            Pair(16, arrayListOf(35.53889, 129.31667)))
    val certifiedList: ArrayList<Int> = arrayListOf()
    val deIsolatedList: ArrayList<Int> = arrayListOf()
    val deadList: ArrayList<Int> = arrayListOf()
    val percentageList: ArrayList<Float> = arrayListOf()


    fun getData() {
        GlobalScope.launch(Dispatchers.Default) {
            val domain = "https://api.dropper.tech/covid19/status/korea"
            var builder: Uri.Builder = Uri.parse(domain).buildUpon()

            requestLocation(builder, locations)
        }
    }

    private fun requestLocation(builder: Uri.Builder, locations: ArrayList<String>) {
        builder.apply {
            val realm = Realm.getDefaultInstance()
            for (i in locations.indices) {
                appendQueryParameter("locale", locations[i])
                val requestURL = URL(builder.build().toString())
                val connection: HttpsURLConnection = requestURL.openConnection() as HttpsURLConnection
                connection.setRequestProperty("APIKey", "8326b88ce7ef6907d1f48c63dcb854282625e639b92ba4b257e199dfb495bdf2")

                val getLocationsData = when(connection.responseCode) {
                    200 -> {
                        val reader = InputStreamReader(connection.inputStream, "UTF-8")
                        val bufferedReader = BufferedReader(reader)
                        val buffer = StringBuffer()

                        var str: String? = ""

                        while ({ str = bufferedReader.readLine(); str }() != null) {
                            buffer.append(str)
                        }
                        buffer.toString()
                    }
                    else -> {
                        "error"
                    }
                }
                val jsonArray: JSONArray = JSONObject(getLocationsData).getJSONArray("data")

                for(i in 0 until jsonArray.length()) {
                    var jsonObject: JSONObject = jsonArray.getJSONObject(i)
                    certifiedList.add(jsonObject.getInt("certified"))
                    /*isolatedList.add(jsonObject.getInt("isolated"))*/
                    deIsolatedList.add(jsonObject.getInt("deisolated"))
                    deadList.add(jsonObject.getInt("dead"))
                    percentageList.add(jsonObject.getDouble("percentage").toFloat())

                }

//                Log.e("certified Async", certifiedList[i].toString())
//                Log.e("deIsolatedList Async", deIsolatedList[i].toString())
//                Log.e("deadList Async", deadList[i].toString())
//                Log.e("percentageList Async", percentageList[i].toString())

                var coronaLocationModel: CoronaLocationModel? = null
                var getData = realm.where(CoronaLocationModel::class.java).findAll()
                when (getData.isEmpty()) {
                    true -> {
                        realm.executeTransaction { realm ->
                            coronaLocationModel = realm.createObject<CoronaLocationModel>(CoronaLocationModel::class.java, locations[i])
                            coronaLocationModel?.certified = certifiedList[i]
                            coronaLocationModel?.deisolated = deIsolatedList[i]
                            coronaLocationModel?.dead = deadList[i]
                            coronaLocationModel?.percentage = percentageList[i]
                        }
//                        Log.e("Empty", "SUCCESS")
                    }
                    false -> {
                        realm.executeTransaction { realm ->
                            val updateData = realm.where(CoronaLocationModel::class.java).equalTo("synthesize", locations[i]).findFirst()
                            updateData?.locationX = locationsForXandY.getValue(i)[0]
                            updateData?.locationY = locationsForXandY.getValue(i)[1]
                        }
                    }
                }
            }
            realm.close()
//            Log.e("DB SAVE" , "SUCCESS")
        }
    }
}