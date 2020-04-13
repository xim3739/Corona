//package com.example.coronamap;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.DialogInterface;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.content.pm.Signature;
//import android.os.Bundle;
//import android.util.Base64;
//import android.util.Log;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import net.daum.mf.map.api.MapPOIItem;
//import net.daum.mf.map.api.MapView;
//import net.daum.mf.map.api.MapPoint;
//
//import java.security.MessageDigest;
//
//
//import io.realm.Realm;
//import io.realm.RealmConfiguration;
//import io.realm.RealmResults;
//
//public class MainActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener {
//    Realm realm;
//    MapPOIItem saveMapPOIItem;
//    RealmResults<CoronaMapModel> results;
//    MapView mapView;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        /**
//         * 해쉬 키 캆 얻기
//         */
//        //getHash();
//
//        /**
//         * Realm 세팅
//         */
//        Realm.init(this);
//        RealmConfiguration config = new RealmConfiguration.Builder()
//                .deleteRealmIfMigrationNeeded()
//                .build();
//        Realm.setDefaultConfiguration(config);
//        realm = Realm.getDefaultInstance();
//
//        results = realm.where(CoronaMapModel.class).findAll();
//
//        /**
//         * MapView 기본 세팅
//         */
//        mapView = new MapView(this);
//        mapView.setDaumMapApiKey("4d2275ee6b9b8f3088a50f11e8d11392");
//        ViewGroup mapViewGroup = findViewById(R.id.map_view);
//        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633);
//        mapView.setMapCenterPoint(mapPoint, true);
//        mapViewGroup.addView(mapView);
//
//        updateMarker();
//
//        /**
//         * MapView 이벤트 등록
//         */
//        mapView.setMapViewEventListener(this);
//        mapView.setPOIItemEventListener(this);
//
//    }
//
//    /**
//     * realm close
//     */
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        realm.close();
//    }
//
//    /**
//     * DataBase 에 저장된 위치 값 마커로 표현하기
//     *
//     */
//    private void updateMarker() {
//        mapView.removeAllPOIItems();
//
//        if (results.isEmpty()) return;
//
//        for(int i = 0; i < results.size(); i++) {
//
//            double locationX = Float.parseFloat(results.get(i).getLocationX());
//            double locationY = Float.parseFloat(results.get(i).getLocationY());
//
//            MapPoint saveMapPoint = MapPoint.mapPointWithGeoCoord(locationX, locationY);
//
//            saveMapPOIItem = new MapPOIItem();
//            saveMapPOIItem.setItemName("X:" + locationX + " Y:" + locationY);
//            saveMapPOIItem.setTag(0);
//            saveMapPOIItem.setMapPoint(saveMapPoint);
//            saveMapPOIItem.setMarkerType(MapPOIItem.MarkerType.RedPin);
//            mapView.addPOIItem(saveMapPOIItem);
//        }
//    }
//
//    /**
//     * mapView 클릭 이벤트
//     */
//
//    /**
//     * 이벤트 콜백 시 MapView Initialized 초기화 작업
//     * @param mapView
//     */
//    @Override
//    public void onMapViewInitialized(MapView mapView) {
//        updateMarker();
//        Log.e("Marker Click Event", "click");
//    }
//
//    @Override
//    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
//
//    }
//
//    @Override
//    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
//
//    }
//
//    @Override
//    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
//        Log.e("Marker Click Event", "SingleTapped");
//    }
//
//    @Override
//    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
//        Log.e("Marker Click Event", "DoubleTapped");
//    }
//
//    /**
//     * LongPress 이벤트 콜백 시 해당 초기 위치 BlueMarker 로 표시 후 Alert 창에서 DataBase 등록 여부 확인 후 등록 후 RedMarker 로 표시, 미등록시 해당 Marker 삭제
//     * @param mapView
//     * @param mapPoint
//     */
//    @Override
//    public void onMapViewLongPressed(final MapView mapView, final MapPoint mapPoint) {
//        /**
//         * LongPress 이벤트 콜백 시 해당 위치 BlueMarker 로 표시
//         */
//        final MapPOIItem mapPOIItem = new MapPOIItem();
//        mapPOIItem.setItemName("" + mapPoint.getMapPointGeoCoord().latitude + ", " + mapPoint.getMapPointGeoCoord().longitude);
//        mapPOIItem.setTag(0);
//        mapPOIItem.setMapPoint(mapPoint);
//        mapPOIItem.setMarkerType(MapPOIItem.MarkerType.BluePin);
//        mapView.addPOIItem(mapPOIItem);
//        /**
//         * DataBase 여부 AlertDialog
//         */
//        new AlertDialog.Builder(this).setTitle("위치 저장 여부")
//                .setMessage("X : "+mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude+", \n Y : "+mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude + "\n 위치를 저장 하시겠습니까?")
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        realm.executeTransaction(new Realm.Transaction() {
//                            @Override
//                            public void execute(Realm realm) {
//                                Number maxId = realm.where(CoronaMapModel.class).max("id");
//                                //Log.e("MAX_ID : ", "" + maxId);
//
//                                int nextId = maxId == null ? 1 : maxId.intValue() + 1;
//
//                                CoronaMapModel coronaMapModel = realm.createObject(CoronaMapModel.class, nextId);
//                                coronaMapModel.setLocationX(String.valueOf(mapPoint.getMapPointGeoCoord().latitude));
//                                coronaMapModel.setLocationY(String.valueOf(mapPoint.getMapPointGeoCoord().longitude));
//                            }
//                        });
//                        updateMarker();
//                        Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        mapView.removePOIItem(mapPOIItem);
//                    }
//                })
//                .create()
//                .show();
//    }
//
//    @Override
//    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
//
//    }
//
//    @Override
//    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
//
//    }
//
//    @Override
//    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
//
//    }
//
//    /**
//     * 마커 클릭 이벤트
//     */
//
//    @Override
//    public void onPOIItemSelected(final MapView mapView, final MapPOIItem mapPOIItem) {
//        Log.e("Marker Click Event", "POIItemSelected");
//        new AlertDialog.Builder(this)
//                .setTitle("삭제 여부")
//                .setMessage("X : "+mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude+", \n Y : "+mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude + "\n 위치를 삭제하시겠습니까?")
//                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        realm.executeTransaction(new Realm.Transaction() {
//                            @Override
//                            public void execute(Realm realm) {
//                                MapPoint.GeoCoordinate coordinate = mapPOIItem.getMapPoint().getMapPointGeoCoord();
//                                RealmResults<CoronaMapModel> findResult =  realm.where(CoronaMapModel.class)
//                                        .equalTo("locationX", String.valueOf(coordinate.latitude))
//                                        .equalTo("locationY", String.valueOf(coordinate.longitude))
//                                        .findAll();
//                                findResult.deleteAllFromRealm();
//
//                            }
//                        });
//                        updateMarker();
//                        Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
//                .create()
//                .show();
//    }
//
//    @Override
//    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
//        Log.e("Marker Click Event", "onCalloutBalloonOfPOIItemTouched");
//
//    }
//
//    @Override
//    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
//        Log.e("Marker Click Event", "onCalloutBalloonOfPOIItemTouched");
//    }
//
//    @Override
//    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
//        Log.e("Marker Click Event", "onDraggablePOIItemMoved");
//    }
//
//    /***************
//     * API 를 위한 키 값 얻어오는 함수
//     */
//    private void getHash() {
//
//        try {
//
//            PackageInfo packageInfoCompat = getPackageManager().getPackageInfo("com.example.coronamap", PackageManager.GET_SIGNATURES);
//
//            for(Signature signature : packageInfoCompat.signatures) {
//
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.e("hash", "key : " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
//
//            }
//
//        } catch (Exception e){
//
//            e.printStackTrace();
//
//        }
//    }
//}
