package kr.o3selab.sunbang.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import kr.o3selab.sunbang.Instance.DB;
import kr.o3selab.sunbang.Instance.JsonHandler;
import kr.o3selab.sunbang.Instance.SunbangProgress;
import kr.o3selab.sunbang.Instance.ThreadGroupHandler;
import kr.o3selab.sunbang.Instance.URLP;
import kr.o3selab.sunbang.Layout.AllFindRoomListItemView;
import kr.o3selab.sunbang.Layout.AllFindRoomListView;
import kr.o3selab.sunbang.R;

public class AllFindRoomActivity extends AppCompatActivity implements MapView.POIItemEventListener,
        DialogInterface.OnClickListener, LocationListener {
    public ProgressDialog pd;
    public MapView mapView;
    public ImageView undoIc;
    public ImageView selectIc;
    public ImageView settingsIc;

    public double lat;
    public double lng;

    public boolean listFlag = false;

    AlertDialog.Builder adb;

    public final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    public String[] menus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_find_room);

        DB.activity = this;
        DB.context = this;

        List<String> menuList = setListMenu();

        menus = new String[menuList.size()];
        menus = menuList.toArray(menus);

        // 뒤로가기 아이콘
        undoIc = (ImageView) findViewById(R.id.activity_all_find_ic_undo);
        undoIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllFindRoomActivity.this.finish();
            }
        });

        // 설정 아이콘
        settingsIc = (ImageView) findViewById(R.id.activity_all_find_ic_setting);
        settingsIc.setVisibility(View.GONE);


        // 메뉴바 설정
        adb = new AlertDialog.Builder(this);
        adb.setTitle("메뉴");
        adb.setItems(menus, this);

        selectIc = (ImageView) findViewById(R.id.activity_all_find_ic_select);
        selectIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adb.show();
            }
        });

        // 프로그래스바
        pd = new SunbangProgress(this);

        getPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();

        DB.activity = this;
        DB.context = this;

        if (!listFlag) getLoadMap();
        else new Thread(new GetRoomListByOrder(1)).start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mapView != null) {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            mapView.setShowCurrentLocationMarker(false);
        }

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.activity_all_find_room_container);
        mapViewContainer.removeAllViews();
    }


    // =======================================
    //   퍼미션 권한 획득
    // =======================================
    public void getPermission() {
        if (ContextCompat.checkSelfPermission(AllFindRoomActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(AllFindRoomActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(AllFindRoomActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                DB.sendToast("지도를 불러오기 위한 권한 입니다. 권한이 없으면 프로그램이 실행이 되지 않습니다.", 1);
            }

            ActivityCompat.requestPermissions(AllFindRoomActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }


    // =======================================
    //   퍼미션 획득 결과 핸들러
    // =======================================
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    DB.sendToast("권한이 없습니다. 다시 실행 해주세요!", 2);
                    AllFindRoomActivity.this.finish();
                }
                return;
            }
        }
    }


    // =======================================
    //   지도 호출 메소드
    // =======================================
    public void getLoadMap() {
        try {
            updateMenuList(false);
            settingsIc.setVisibility(View.GONE);

            ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.activity_all_find_room_container);
            mapViewContainer.removeAllViews();
            mapViewContainer.removeAllViewsInLayout();

            mapView = new MapView(this);
            mapView.setDaumMapApiKey(DB.mapApiKey);
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(36.80024647035301, 127.07494945930536), true);
            mapView.setZoomLevel(-1, true);
            mapView.setPOIItemEventListener(this);
            mapView.zoomIn(true);
            mapView.zoomOut(true);

            mapViewContainer.addView(mapView);

            Thread getLocationPoint = new Thread(new GetLocationPoint());
            getLocationPoint.start();

            Thread[] findRoomGroup = {getLocationPoint};

            ThreadGroupHandler threadGroupHandler = new ThreadGroupHandler(findRoomGroup, pd);
            threadGroupHandler.start();

        } catch (Exception e) {
            // DB.sendToast(e.getMessage(), 2);
        }
    }


    // =======================================
    //   위치 변경 콜백 메소드
    // =======================================
    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        this.lat = lat;
        this.lng = lng;
    }


    // =======================================
    //   지도 마커 불러오기
    // =======================================
    public class GetLocationPoint implements Runnable {
        @Override
        public void run() {
            try {
                String param = URLP.PARAM_MODULE_SRL + DB.ROOM_MODULE;
                String result = new JsonHandler(URLP.MAP_ALL_LOCATION, param).execute().get();

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                for (int i = 0; i < jsonArray.length(); i = i + 2) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    final String srl = obj.getString("srl");

                    final Double lat = obj.getDouble("value");

                    obj = jsonArray.getJSONObject(i + 1);
                    final Double lng = obj.getDouble("value");

                    final String title = getTitle(srl);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MapPOIItem marker = new MapPOIItem();
                            marker.setItemName(title);
                            marker.setTag(Integer.parseInt(srl));
                            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(lat, lng));
                            marker.setShowCalloutBalloonOnTouch(true);
                            marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                            marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                            marker.setCustomImageResourceId(R.drawable.map_icon);
                            marker.setCustomSelectedImageResourceId(R.drawable.map_icon_selected);
                            marker.setCustomImageAutoscale(true);
                            marker.setCustomImageAnchor(0.5f, 1.0f);

                            mapView.addPOIItem(marker);
                        }
                    });
                }
            } catch (Exception e) {
                DB.sendToast("ErrorCode 12: " + e.getMessage(), 2);
            }
        }
    }


    // =======================================
    //   제목 가져오기
    // =======================================
    public String getTitle(String srl) {
        String title = "";
        try {
            String param = URLP.PARAM_DOCUMENT_SRL + srl;
            String result = new JsonHandler(URLP.MAP_ALL_LOCATION_GET_TITLE, param).execute().get();

            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            JSONObject obj = jsonArray.getJSONObject(0);
            title = obj.getString("link");
        } catch (Exception e) {
            DB.sendToast("ErrorCode 13: " + e.getMessage(), 2);
        }
        return title;
    }


    // =======================================
    //   말풍선 클릭시 호출되는 콜백 메소드
    // =======================================
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        MapPOIItem item = mapPOIItem;

        Integer tag = item.getTag();

        Intent intent = new Intent(AllFindRoomActivity.this, RoomActivity.class);
        intent.putExtra("srl", tag + "");
        startActivity(intent);
    }


    // =======================================
    //   메뉴 버튼 클릭 이벤트 메소드
    // =======================================
    @Override
    public void onClick(DialogInterface dialog, int which) {

        String clickedItemValue = Arrays.asList(menus).get(which);
        switch (clickedItemValue) {
            case "내 위치 보기 ON":
                // GPS 정보
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (!enabled) {
                    new AlertDialog.Builder(AllFindRoomActivity.this)
                            .setTitle("알림")
                            .setMessage("GPS가 비활성화 되어 있습니다. 활성화 하시겠습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);

                                    mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
                                }
                            })
                            .setNegativeButton("아니오", null)
                            .show();
                }
                break;

            case "내 위치 보기 OFF":
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                mapView.setShowCurrentLocationMarker(false);
                break;

            case "리스트로 보기":
                new Thread(new GetRoomListByOrder(1)).start();
                break;

            case "지도로 보기":
                getLoadMap();
                break;
        }
    }


    // =======================================
    //   리스트 정보 불러오기 메소드
    // =======================================
    public class GetRoomListByOrder implements Runnable {

        // 타입 정의
        // 1. 일반
        // 2. 거리 순
        // 3. 가격 순
        //
        int type;

        public GetRoomListByOrder(int type) {
            this.type = type;
        }

        @Override
        public void run() {
            final HashMap<String, String> roomData = new HashMap<>();

            try {
                String listParam = URLP.PARAM_MODULE_SRL + DB.ROOM_MODULE;
                String listResult = new JsonHandler(URLP.FIND_ROOM_LIST, listParam).execute().get();

                JSONObject listJSONObject = new JSONObject(listResult);
                JSONArray listJSONArray = listJSONObject.getJSONArray("result");

                for (int i = 0; i < listJSONArray.length(); i++) {
                    String roomSrl = listJSONArray.getJSONObject(i).getString("srl");

                    String roomParam = URLP.PARAM_DOCUMENT_SRL + roomSrl;
                    String roomResult = new JsonHandler(URLP.FIND_ROOM_INFO, roomParam).execute().get();

                    JSONObject roomJSONObject = new JSONObject(roomResult);
                    JSONArray roomJSONArray = roomJSONObject.getJSONArray("result");

                    String result = roomJSONArray.getJSONObject(0).getString("info");

                    roomData.put(roomSrl, result);
                }

            } catch (Exception e) {
                DB.sendToast("ErrorCode 28: " + e.getMessage(), 2);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    drawListContent(roomData);
                }
            });
        }
    }


    // =======================================
    //   리스트 정보 다시 그리는 메소드
    // =======================================
    public void drawListContent(final HashMap<String, String> roomData) {
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.activity_all_find_room_container);
        mapViewContainer.removeAllViews();

        updateMenuList(true);
        settingsIc.setVisibility(View.VISIBLE);
        settingsIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] value = {"건물 설정", "정렬 방식"};
                AlertDialog.Builder builder = new AlertDialog.Builder(AllFindRoomActivity.this);

                builder.setTitle("설정");
                builder.setItems(value, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedText = Arrays.asList(value).get(which);

                        switch(selectedText) {
                            case "건물 설정":
                                showSelectBuildingDialog(roomData);
                                break;
                            case "정렬 방식":
                                DB.sendToast("정렬 방식 변경", 1);
                                break;
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        ScrollView scrollView = new AllFindRoomListView(AllFindRoomActivity.this);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mapViewContainer.addView(scrollView);

        LinearLayout parentLayout = (LinearLayout) scrollView.findViewById(R.id.activity_all_find_list_layout);

        Iterator<String> itr = roomData.keySet().iterator();

        while (itr.hasNext()) {
            final String roomSrl = itr.next();

            LinearLayout linearLayout = new AllFindRoomListItemView(AllFindRoomActivity.this, roomData.get(roomSrl));
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AllFindRoomActivity.this, RoomActivity.class);
                    intent.putExtra("srl", roomSrl);

                    startActivity(intent);
                }
            });
            parentLayout.addView(linearLayout);
        }
    }


    // =======================================
    //   건물 선택 다이얼로그
    // =======================================
    public void showSelectBuildingDialog(final HashMap<String, String> roomData) {
        AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(AllFindRoomActivity.this);

        alertdialogbuilder.setTitle("설정하고 싶은 건물을 입력하세요!");
        alertdialogbuilder.setItems(DB.locationValue, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String korSelectedText = Arrays.asList(DB.locationValue).get(which);
                String engSelectedText = DB.getLocationEngName(korSelectedText);

                DB.defaultBuilding = engSelectedText;

                SharedPreferences.Editor editor = DB.getEditor();
                editor.putString(DB.DEFAULT_BUILDING, engSelectedText);
                editor.commit();

                drawListContent(roomData);
            }
        });

        AlertDialog dialog = alertdialogbuilder.create();

        dialog.show();
    }


    // =======================================
    //   리스트 메뉴 아이템 설정
    // =======================================
    public List<String> setListMenu() {
        List<String> menuList = new ArrayList<>();
        menuList.add("내 위치 보기 ON");
        menuList.add("내 위치 보기 OFF");
        if (!listFlag) menuList.add("리스트로 보기");
        else menuList.add("지도로 보기");
        if (DB.debug) menuList.add("내 좌표 보기");

        return menuList;
    }


    // =======================================
    //   리스트 메뉴 업데이트
    // =======================================
    public void updateMenuList(boolean flag) {
        listFlag = flag;
        List<String> menuList = setListMenu();
        this.menus = new String[menuList.size()];
        menus = menuList.toArray(menus);
        adb.setItems(menus, AllFindRoomActivity.this);
    }


    // =======================================
    //   미사용 콜백 메소드
    // =======================================
    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
