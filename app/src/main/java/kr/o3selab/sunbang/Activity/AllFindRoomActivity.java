package kr.o3selab.sunbang.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import kr.o3selab.sunbang.Instance.DB;
import kr.o3selab.sunbang.R;

public class AllFindRoomActivity extends AppCompatActivity {
    public ProgressDialog pd;
    public MapView mapView;
    public ImageView undoIc;
    public final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_find_room);

        DB.activity = this;
        DB.context = this;

        undoIc = (ImageView) findViewById(R.id.activity_all_find_ic_undo);
        undoIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllFindRoomActivity.this.finish();
            }
        });

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);

    }

    @Override
    protected void onResume() {
        super.onResume();

        DB.activity = this;
        DB.context = this;

        getPermission();


    }

    @Override
    protected void onPause() {
        super.onPause();

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.activity_all_find_room_map_view);
        mapViewContainer.removeAllViews();
    }

    // =======================================
    //   퍼미션 권한 획득
    // =======================================
    public void getPermission() {
        if (ContextCompat.checkSelfPermission(AllFindRoomActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(AllFindRoomActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                DB.sendToast("지도를 불러오기 위한 권한 입니다. 권한이 없으면 프로그램이 실행이 되지 않습니다.", 1);
            }

            ActivityCompat.requestPermissions(AllFindRoomActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            getLoadMap();
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
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLoadMap();
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
            mapView = new MapView(this);
            mapView.setDaumMapApiKey(DB.mapApiKey);
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(36.80024647035301, 127.07494945930536), true);
            mapView.setZoomLevel(-1, true);
            mapView.setMapTilePersistentCacheEnabled(true);
            mapView.zoomIn(true);
            mapView.zoomOut(true);

            ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.activity_all_find_room_map_view);
            mapViewContainer.addView(mapView);

            new GetLocationPoint().execute();
        } catch (Exception e) {
            DB.sendToast(e.getMessage(), 2);
        }
    }


    // =======================================
    //   지도 마커 불러오기
    // =======================================
    public class GetLocationPoint extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                URL url = new URL("http://sunbang.o3selab.kr/script/getAllMapLocation.php?module_srl=" + DB.room);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.forName("euc-kr")));

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();

                JSONObject jsonObject = new JSONObject(sb.toString());
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
                            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                            mapView.addPOIItem(marker);
                        }
                    });

                }


            } catch (Exception e) {
                DB.sendToast(e.getMessage(), 2);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
        }
    }


    // =======================================
    //   제목 가져오기
    // =======================================
    public String getTitle(String srl) {
        String title = "";
        try {
            URL url = new URL("http://sunbang.o3selab.kr/script/getDocumentTitle.php?id=" + srl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.forName("utf-8")));

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            br.close();

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            JSONObject obj = jsonArray.getJSONObject(0);
            title = obj.getString("link");

        } catch (Exception e) {
            DB.sendToast(e.getMessage(), 2);
        }

        return title;
    }
}
