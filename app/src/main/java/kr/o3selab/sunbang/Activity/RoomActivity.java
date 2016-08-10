
package kr.o3selab.sunbang.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.StringTokenizer;
import java.util.Vector;

import kr.o3selab.sunbang.Instance.DB;
import kr.o3selab.sunbang.Layout.RoomEvaluateContent;
import kr.o3selab.sunbang.R;
import me.grantland.widget.AutofitTextView;

public class RoomActivity extends AppCompatActivity {

    public final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public ProgressDialog pd;

    // UI 컨텐츠
    public SliderLayout roomImageLayout;

    public AutofitTextView roomTopNumber;
    public ImageView roomTopUndoButton;

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

    public EditText evaluateTextField;
    public Button evaluateCommitButton;
    public LinearLayout evaluateContentLayout;

    public RatingBar ratingBar;
    public TextView ratingAverage;
    public TextView ratingSendButton;

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

        // 로딩창 생성
        pd = new ProgressDialog(this);
        pd.setMessage("방 정보를 불러오고 있습니다.");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);

        // UI 컨텐츠 로딩..
        roomImageLayout = (SliderLayout) findViewById(R.id.main_room_image_slider);

        roomTopNumber = (AutofitTextView) findViewById(R.id.room_activity_title);
        roomTopUndoButton = (ImageView) findViewById(R.id.room_activity_ic_undo);
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

        evaluateTextField = (EditText) findViewById(R.id.acivity_room_evaluate_edittext);
        evaluateCommitButton = (Button) findViewById(R.id.activity_room_evaluate_commit);
        evaluateContentLayout = (LinearLayout) findViewById(R.id.acivity_room_evaluate_content);

        ratingBar = (RatingBar) findViewById(R.id.activity_room_rating_bar);
        ratingAverage = (TextView) findViewById(R.id.activity_room_rating_average);
        ratingSendButton = (TextView) findViewById(R.id.activity_room_rating_send_button);

        contactLayout = (FrameLayout) findViewById(R.id.activity_room_contact);


        // 데이터 로딩..
        new GetRoomImagesSliderData().execute();
        new GetRoomContentData().execute();
        new GetRoomOptionalData().execute();
        new GetAverageRatingData().execute();
        new GetPersonalRatingData().execute();
        new GetEvaluateData().execute();


        // 버튼 핸들러
        evaluateCommitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = evaluateTextField.getText().toString();

                if (content.contains(";") || content.contains("씨발") || content.contains("쓰레기")) {
                    DB.sendToast("경고! 특정한 특수문자나 욕설은 작성하실 수 없습니다!", 1);
                    return;
                }

                if (content.length() < 5) {
                    DB.sendToast("5글자 이상 작성해주셔야 합니다!", 1);
                    return;
                }

                new SendEvaluateData(evaluateCommitButton).execute(content);
            }
        });

        roomTopUndoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoomActivity.this.finish();
            }
        });

        roomTopNumber.setText("방 번호 : " + roomSrl);

        ratingSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(RoomActivity.this)
                        .setTitle("알림")
                        .setMessage("별점을 제출하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new SendRatingData(ratingBar.getRating(), ratingSendButton, ratingSendButton.getText()).execute();
                            }
                        })
                        .setNegativeButton("아니오", null)
                        .show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 지도 퍼미션 권한 획득
        getPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.activity_room_map);
        mapViewContainer.removeAllViews();
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
            MapView.setMapTilePersistentCacheEnabled(true);
            mapView.zoomOut(false);
            mapView.zoomIn(false);

            ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.activity_room_map);
            mapViewContainer.addView(mapView);
        } catch (Exception e) {
            //DB.sendToast(e.getMessage(), 2);
        }
    }


    // =======================================
    //   방 사진 로딩 핸들러
    // =======================================
    public class GetRoomImagesSliderData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("http://sunbang.o3selab.kr/script/getRoomImageData.php?srl=" + roomSrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("euc-kr")));

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
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
                URL url = new URL("http://sunbang.o3selab.kr/script/getRoomContentData.php?module=" + DB.ROOM_MODULE + "&id=" + roomSrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("euc-kr")));

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
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
                URL url = new URL("http://sunbang.o3selab.kr/script/getRoomOptionalData.php?srl=" + roomSrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("euc-kr")));

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
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
                                                sendContactLog(1, phone);
                                            }
                                        })
                                        .setNegativeButton("문자", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                sendContactLog(2, phone);
                                            }
                                        })
                                        .show();
                            }
                        });

                        if (mapFlag) {

                            MapPoint point = MapPoint.mapPointWithGeoCoord(lat, lng);
                            mapView.setMapCenterPointAndZoomLevel(point, -1, true);

                            MapPOIItem marker = new MapPOIItem();
                            marker.setTag(0);
                            marker.setMapPoint(point);
                            marker.setItemName(roomTopTitle.getText().toString());
                            marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                            marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                            marker.setCustomImageResourceId(R.drawable.map_icon);
                            marker.setCustomSelectedImageResourceId(R.drawable.map_icon_selected);
                            marker.setCustomImageAutoscale(true);
                            marker.setCustomImageAnchor(0.5f, 1.0f);

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
        for (int i = 0; i < count; i++) {
            if (i == count - 1) {
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


    // =======================================
    //   문의하기 기록 남기기
    // =======================================
    public void sendContactLog(Integer type, String phone) {
        try {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL("http://sunbang.o3selab.kr/script/sendContactLog.php?srl=" + roomSrl + "&pn=" + DB.phone_number);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();

                        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.forName("euc-kr")));
                        String line;
                        if (!(line = br.readLine()).equals("TRUE")) {
                            DB.sendToast("예외발생! 관리자에게 문의해주세요!", 2);
                        }
                        br.close();
                    } catch (Exception e) {
                        DB.sendToast("예외발생! 관리자에게 문의해주세요!", 2);
                    }
                }
            });

            th.start();


            switch (type) {
                case 1:
                    Uri uri = Uri.parse("tel:" + phone);
                    Intent i = new Intent(Intent.ACTION_DIAL, uri);
                    startActivity(i);
                    break;
                case 2:
                    Uri uri2 = Uri.parse("smsto:" + phone);
                    Intent i2 = new Intent(Intent.ACTION_SENDTO, uri2);
                    i2.putExtra("sms_body", "선방앱에서 보고 연락드립니다!");
                    startActivity(i2);
                    break;
            }

        } catch (Exception e) {
            DB.sendToast(e.getMessage(), 2);
        }
    }


    // =======================================
    //   별점평가 전송 핸들러
    // =======================================
    public class SendRatingData extends AsyncTask<Void, Void, Void> {
        public Float rating;
        public TextView sendButton;
        public CharSequence text;

        public SendRatingData(Float rating, TextView sendButton, CharSequence text) {
            this.rating = rating;
            this.sendButton = sendButton;
            this.text = text;
        }

        @Override
        protected void onPreExecute() {
            sendButton.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {

                URL url = null;

                switch((String) text) {
                    case "제출":
                        url = new URL("http://sunbang.o3selab.kr/script/sendFirstRatingData.php?" +
                                "srl=" + roomSrl +
                                "&pn=" + DB.phone_number +
                                "&rate=" + rating);
                        break;
                    case "수정":
                        url = new URL("http://sunbang.o3selab.kr/script/sendModRatingData.php?" +
                                "rate=" + rating +
                                "&pn=" + DB.phone_number);
                        break;
                }

                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.forName("euc-kr")));
                if (br.readLine().contains("TRUE")) {
                    DB.sendToast("제출완료", 1);
                } else {
                    DB.sendToast("제출실패", 2);
                }

            } catch (Exception e) {
                DB.sendToast("제출실패:" + e.getMessage(), 2);
                //e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            sendButton.setEnabled(true);
            new GetAverageRatingData().execute();
            new GetPersonalRatingData().execute();
        }
    }


    // =======================================
    //   평균 별점정보 불러오기 핸들러
    // =======================================
    public class GetAverageRatingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            try {
                URL url = new URL("http://sunbang.o3selab.kr/script/getAverageRatingData.php?srl=" + roomSrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.forName("euc-kr")));

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = br.readLine()) != null) {
                    sb.append(line);
                }

                JSONObject jsonObject = new JSONObject(sb.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                JSONObject obj = jsonArray.getJSONObject(0);

                final Double rate = Math.ceil(obj.getDouble("rate")/0.5)*0.5;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() { ratingAverage.setText("평균 : " + rate); }
                });

            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ratingAverage.setText("평가없음");
                    }
                });
            }
            return null;
        }
    }


    // =======================================
    //   개인 별점정보 불러오기 핸들러
    // =======================================
    public class GetPersonalRatingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL("http://sunbang.o3selab.kr/script/getPersonalRatingData.php?srl=" + roomSrl + "&pn=" + DB.phone_number);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.forName("euc-kr")));

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                JSONObject jsonObject = new JSONObject(sb.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                JSONObject obj = jsonArray.getJSONObject(0);

                final Float pRate = Float.parseFloat(obj.getDouble("rate") + "");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ratingBar.setRating(pRate);
                        ratingSendButton.setText("수정");
                    }
                });
            } catch (Exception e) {

            }

            return null;
        }
    }


    // =======================================
    //   평가하기 전송 핸들러
    // =======================================
    public class SendEvaluateData extends AsyncTask<String, Void, Void> {

        public Button button;

        public SendEvaluateData(Button b) {
            this.button = b;
        }

        @Override
        protected void onPreExecute() {
            button.setEnabled(false);
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                String data = URLEncoder.encode(params[0], "UTF-8");

                URL url = new URL("http://sunbang.o3selab.kr/script/sendCommentData.php?" +
                        "srl=" + roomSrl + "&" +
                        "phone=" + DB.phone_number + "&" +
                        "data=" + data);

                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.forName("euc-kr")));
                String line = br.readLine();

                if (line.equals("TRUE")) {
                    // 전송 성공 목록 초기화
                } else {
                    throw new Exception("전송실패");
                }

            } catch (Exception e) {
                DB.sendToast(e.getMessage(), 2);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new GetEvaluateData().execute();
            evaluateTextField.setText("");
            button.setEnabled(true);
        }

    }


    // =======================================
    //   한줄평가 리스트 로딩 핸들러
    // =======================================
    public class GetEvaluateData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    evaluateContentLayout.removeAllViews();
                }
            });

            try {
                URL url = new URL("http://sunbang.o3selab.kr/script/getEvaluateData.php?srl=" + roomSrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.forName("utf-8")));

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                JSONObject jsonObject = new JSONObject(sb.toString());
                JSONArray result = jsonObject.getJSONArray("result");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject obj = result.getJSONObject(i);

                    final Integer cId = obj.getInt("id");
                    final String cName = obj.getString("phone");
                    final String cData = obj.getString("data");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayout layout = new RoomEvaluateContent(RoomActivity.this, cId, cName, cData);
                            evaluateContentLayout.addView(layout);
                        }
                    });
                }
            } catch (Exception e) {
                DB.sendToast(e.getMessage(), 2);
            }
            return null;
        }
    }


    // =======================================
    //   한줄평가 리스트 로딩 메소드
    // =======================================
    public void getEvaluateDataMethod() {
        new GetEvaluateData().execute();
    }
}
