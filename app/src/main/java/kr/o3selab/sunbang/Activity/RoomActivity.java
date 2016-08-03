
package kr.o3selab.sunbang.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Vector;

import kr.o3selab.sunbang.Instance.DB;
import kr.o3selab.sunbang.R;

public class RoomActivity extends AppCompatActivity {

    public final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public ProgressDialog pd;

    // UI 컨텐츠
    public SliderLayout roomImageLayout;
    public TextView roomTopTitle;
    public TextView roomTopDeposit;
    public TextView roomTopMonthly;
    public TextView roomTopSubTitle;
    public MapView mapView;

    // 데이터
    public boolean mapFlag = false;
    public String roomSrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        // DB 정보 업데이트
        DB.context = this;
        DB.activity = this;

        // 인텐트 값 수신
        Intent intent = getIntent();
        roomSrl = intent.getStringExtra("srl");

        // 지도 퍼미션 권한 획득
        getPermission();

        // 로딩창 생성
        pd = new ProgressDialog(this);
        pd.setMessage("방 정보를 불러오고 있습니다.");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);

        // UI 컨텐츠 로딩..
        roomImageLayout = (SliderLayout) findViewById(R.id.main_room_image_slider);
        roomTopTitle = (TextView) findViewById(R.id.activity_room_top_title);
        roomTopDeposit = (TextView) findViewById(R.id.activity_room_top_deposit);
        roomTopMonthly = (TextView) findViewById(R.id.activity_room_top_monthly);
        roomTopSubTitle = (TextView) findViewById(R.id.activity_room_top_subtitle);


        // 데이터 로딩..
        new GetRoomImagesSliderData().execute();
        new GetRoomContentData().execute();
        new GetRoomOptionalData().execute();


    }

    // =======================================
    //   퍼미션 권한 획득
    // =======================================
    public void getPermission() {
        if (ContextCompat.checkSelfPermission(RoomActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(RoomActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                DB.sendToast("지도를 불러오기 위한 권한 입니다. 권한이 없으면 프로그램이 실행이 되지 않습니다.", 1);
            }

            ActivityCompat.requestPermissions(RoomActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            getLoadMap();
            mapFlag = true;
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
                    mapFlag = true;
                } else {
                    DB.sendToast("권한이 없습니다. 다시 실행 해주세요!", 2);
                    RoomActivity.this.finish();
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
            mapView.zoomIn(true);
            mapView.zoomOut(true);

            ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.activity_room_map);
            mapViewContainer.addView(mapView);
        } catch (Exception e) {
            DB.sendToast(e.getMessage(), 2);
        }

    }


    // =======================================
    //   방 사진 로딩 핸들러
    // =======================================
    public class GetRoomImagesSliderData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("http://sunbang.o3selab.kr/script/getRoomImageData.php?srl="+roomSrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("euc-kr")));

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();

                final Vector<String> images = new Vector<String>();

                JSONObject jsonObject = new JSONObject(sb.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);

                    final String fileLocation = obj.getString("file");

                    String fileUrl = "http://sunbang.o3selab.kr/" + fileLocation.substring(2, fileLocation.length());
                    images.add(fileUrl);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Vector<String> mainImages = images;

                        for (int i = 0; i < mainImages.size(); i++) {

                            DefaultSliderView textSliderView = new DefaultSliderView(RoomActivity.this);
                            textSliderView
                                    .image(mainImages.get(i))
                                    .setScaleType(BaseSliderView.ScaleType.Fit)
                                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                        @Override
                                        public void onSliderClick(BaseSliderView slider) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(RoomActivity.this, "이미지 사진 클릭", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                            roomImageLayout.addSlider(textSliderView);
                        }

                        roomImageLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
                        roomImageLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                        roomImageLayout.setCustomAnimation(new DescriptionAnimation());
                    }
                });

            } catch (Exception e) {
                DB.sendToast("에러", 2);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }


    // =======================================
    //   방 정보 로딩 핸들러 - 1
    // =======================================
    public class GetRoomContentData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL("http://sunbang.o3selab.kr/script/getRoomContentData.php?module="+DB.room+"&id="+roomSrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("euc-kr")));

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();

                JSONObject jsonObject = new JSONObject(sb.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                JSONObject obj = jsonArray.getJSONObject(0);
                final String title = obj.getString("title");
                final String content = obj.getString("content");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        roomTopTitle.setText(title);
                        // 콘텐트 뷰 추후 추가
                    }
                });

            } catch (Exception e) {
                DB.sendToast("에러 발생", 2);
            }


            return null;
        }

    }

    // =======================================
    //   방 정보 로딩 핸들러 - 2
    // =======================================
    public class GetRoomOptionalData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                URL url = new URL("http://sunbang.o3selab.kr/script/getRoomOptionalData.php?srl="+roomSrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("euc-kr")));

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();

                String value;
                JSONObject obj;

                JSONObject jsonObject = new JSONObject(sb.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                // 부제목
                obj = jsonArray.getJSONObject(0);
                value = obj.getString("value");
                final String subTitle = value;

                // 보증금
                obj = jsonArray.getJSONObject(4);
                value = obj.getString("value");
                final String deposit = value;

                // 월세
                obj = jsonArray.getJSONObject(5);
                value = obj.getString("value");
                final String monthly = value;

                // 경도
                obj = jsonArray.getJSONObject(15);
                value = obj.getString("value");
                final Double lat = Double.parseDouble(value);

                // 위도
                obj = jsonArray.getJSONObject(16);
                value = obj.getString("value");
                final Double lng = Double.parseDouble(value);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        roomTopSubTitle.setText(subTitle);
                        roomTopDeposit.setText(deposit);
                        roomTopMonthly.setText(monthly);
                        if(mapFlag) {
                            MapPoint point = MapPoint.mapPointWithGeoCoord(lat, lng);
                            mapView.setMapCenterPointAndZoomLevel(point, -1, true);

                            MapPOIItem marker = new MapPOIItem();
                            marker.setTag(0);
                            marker.setMapPoint(point);
                            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                            marker.setItemName(roomTopTitle.getText().toString());
                            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

                            mapView.addPOIItem(marker);
                        }
                    }
                });

            } catch (Exception e) {
                DB.sendToast("에러 발생", 2);
            }


            return null;
        }

    }

}
