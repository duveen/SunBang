
package kr.o3selab.sunbang.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import java.util.StringTokenizer;
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

    public TextView roomTopRoomType;
    public TextView roomTopRoomFloor;
    public TextView roomTopMonthlyMoney;

    public TextView roomInfoRoomType;
    public TextView roomInfoRoomSize;
    public TextView roomInfoDeposit;
    public TextView roomInfoMonthlyMoney;
    public TextView roomInfoIncludeAdmin;
    public TextView roomInfoFireType;
    public TextView roomInfoElevator;
    public TextView roomInfoParking;
    public TextView roomInfoIntoMonth;
    public TextView roomInfoMaxPeople;
    public TextView roomInfoOptional;

    public TextView roomDetailContent;

    public FrameLayout contactLayout;

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

        roomTopRoomType = (TextView) findViewById(R.id.activity_room_top_room_type);
        roomTopRoomFloor = (TextView) findViewById(R.id.activity_room_top_floor);
        roomTopMonthlyMoney = (TextView) findViewById(R.id.activity_room_top_monthly_money);

        roomInfoRoomType = (TextView) findViewById(R.id.activity_room_info_type);
        roomInfoRoomSize = (TextView) findViewById(R.id.activity_room_info_size);
        roomInfoDeposit = (TextView) findViewById(R.id.activity_room_info_deposit);
        roomInfoMonthlyMoney = (TextView) findViewById(R.id.activity_room_info_monthly);
        roomInfoIncludeAdmin = (TextView) findViewById(R.id.activity_room_info_include_admin);
        roomInfoFireType = (TextView) findViewById(R.id.activity_room_info_fire_type);
        roomInfoElevator = (TextView) findViewById(R.id.activity_room_info_elevator);
        roomInfoParking = (TextView) findViewById(R.id.activity_room_info_parking);
        roomInfoIntoMonth = (TextView) findViewById(R.id.activity_room_info_into);
        roomInfoMaxPeople = (TextView) findViewById(R.id.activity_room_info_max_people);
        roomInfoOptional = (TextView) findViewById(R.id.activity_room_info_option);

        roomDetailContent = (TextView) findViewById(R.id.activity_room_detail_content);

        contactLayout = (FrameLayout) findViewById(R.id.activity_room_contact);

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
            mapView.zoomOut(false);
            mapView.zoomIn(false);

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
                        roomDetailContent.setText(Html.fromHtml(content));
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

                // 방 타입
                obj = jsonArray.getJSONObject(1);
                value = obj.getString("value");
                final String roomType = value;

                // 층수
                obj = jsonArray.getJSONObject(2);
                value = obj.getString("value");
                final String floor = value;

                // 방 사이즈
                obj = jsonArray.getJSONObject(3);
                value = obj.getString("value");
                final String roomSize = value;

                // 보증금
                obj = jsonArray.getJSONObject(4);
                value = obj.getString("value");
                final String deposit = value;

                // 월세 or 학기
                obj = jsonArray.getJSONObject(5);
                value = obj.getString("value");
                final String monthly = value;

                // 관리비 포함 항목
                obj = jsonArray.getJSONObject(6);
                value = obj.getString("value");
                final String includeAdmin = getTokenString(value);

                // 평균 관리비
                obj = jsonArray.getJSONObject(7);
                value = obj.getString("value");
                final String monthlyMoney = value;

                // 난방
                obj = jsonArray.getJSONObject(8);
                value = obj.getString("value");
                final String fireType = value;

                // 엘리베이터
                obj = jsonArray.getJSONObject(9);
                value = obj.getString("value");
                final String elevator = value;

                // 주차 여부
                obj = jsonArray.getJSONObject(10);
                value = obj.getString("value");
                final String parking = value;

                // 입주 가능 월
                obj = jsonArray.getJSONObject(11);
                value = obj.getString("value");
                final String into = value;

                // 최대 인원
                obj = jsonArray.getJSONObject(12);
                value = obj.getString("value");
                final String maxPeople = value;

                // 옵션 품목
                obj = jsonArray.getJSONObject(13);
                value = obj.getString("value");
                final String optional = getTokenString(value);

                // 연락처
                obj = jsonArray.getJSONObject(14);
                value = obj.getString("value");
                final String phone = getPhoneTokenString(value);

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

                        roomTopRoomType.setText(roomType);
                        roomTopRoomFloor.setText(floor);
                        roomTopMonthlyMoney.setText(monthlyMoney);

                        roomInfoRoomType.setText(roomType);
                        roomInfoRoomSize.setText(roomSize);
                        roomInfoDeposit.setText(deposit);
                        roomInfoMonthlyMoney.setText(monthly);
                        roomInfoIncludeAdmin.setText(includeAdmin);
                        roomInfoFireType.setText(fireType);
                        roomInfoElevator.setText(elevator);
                        roomInfoParking.setText(parking);
                        roomInfoIntoMonth.setText(into);
                        roomInfoMaxPeople.setText(maxPeople);
                        roomInfoOptional.setText(optional);

                        contactLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new AlertDialog.Builder(RoomActivity.this)
                                        .setMessage("연락 방법을 선택해주세요.")
                                        .setPositiveButton("전화", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Uri uri= Uri.parse("tel:" + phone);
                                                Intent i= new Intent(Intent.ACTION_DIAL,uri);
                                                startActivity(i);

                                            }
                                        })
                                        .setNegativeButton("문자", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Uri uri= Uri.parse("smsto:" + phone);
                                                Intent i= new Intent(Intent.ACTION_SENDTO,uri);
                                                i.putExtra("sms_body", "선방앱에서 보고 연락드립니다!");
                                                startActivity(i);
                                            }
                                        })
                                        .show();
                            }
                        });

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

    // =======================================
    //   토큰 분리 메소드
    // =======================================
    public String getTokenString(String line) {

        StringTokenizer str = new StringTokenizer(line, "|@|");
        int count = str.countTokens();
        String parseString = new String();
        for(int i = 0; i < count; i++) {
            if(i == count - 1) {
                parseString = parseString + str.nextToken();
            } else {
                parseString = parseString + str.nextToken() + ", ";
            }
        }

        return parseString;
    }

    // =======================================
    //   연락처 분리 메소드
    // =======================================
    public String getPhoneTokenString(String line) {
        StringTokenizer str = new StringTokenizer(line, "|@|");
        String parseString = str.nextToken() + str.nextToken() + str.nextToken();

        return parseString;
    }
}
