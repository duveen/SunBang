
package kr.o3selab.sunbang.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import kr.o3selab.sunbang.Instance.DB;
import kr.o3selab.sunbang.Instance.JsonHandler;
import kr.o3selab.sunbang.Instance.SunbangProgress;
import kr.o3selab.sunbang.Instance.ThreadGroupHandler;
import kr.o3selab.sunbang.Instance.URLP;
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
    public TextView roomTopMonthOrSeme;
    public TextView roomTopMoney;
    public TextView roomTopSubTitle;

    public TextView roomTopRoomType;
    public TextView roomTopRoomFloor;
    public TextView roomTopMonthlyMoney;

    public TextView roomInfoRoomType;
    public TextView roomInfoRoomSize;
    public TextView roomInfoDeposit;
    public TextView roomInfoMonthOrSeme;
    public TextView roominfoMoney;
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
        pd = new SunbangProgress(this);

        // UI 컨텐츠 로딩..
        roomImageLayout = (SliderLayout) findViewById(R.id.main_room_image_slider);

        roomTopNumber = (AutofitTextView) findViewById(R.id.room_activity_title);
        roomTopUndoButton = (ImageView) findViewById(R.id.room_activity_ic_undo);
        roomTopTitle = (TextView) findViewById(R.id.activity_room_top_title);
        roomTopDeposit = (TextView) findViewById(R.id.activity_room_top_deposit);
        roomTopMonthOrSeme = (TextView) findViewById(R.id.activity_room_top_monthorseme);
        roomTopMoney = (TextView) findViewById(R.id.activity_room_top_money);
        roomTopSubTitle = (TextView) findViewById(R.id.activity_room_top_subtitle);

        roomTopRoomType = (TextView) findViewById(R.id.activity_room_top_room_type);
        roomTopRoomFloor = (TextView) findViewById(R.id.activity_room_top_floor);
        roomTopMonthlyMoney = (TextView) findViewById(R.id.activity_room_top_monthly_money);

        roomInfoRoomType = (TextView) findViewById(R.id.activity_room_info_type);
        roomInfoRoomSize = (TextView) findViewById(R.id.activity_room_info_size);
        roomInfoDeposit = (TextView) findViewById(R.id.activity_room_info_deposit);
        roomInfoMonthOrSeme = (TextView) findViewById(R.id.activity_room_info_monthorseme);
        roominfoMoney = (TextView) findViewById(R.id.activity_room_info_money);
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

        Thread getRoomimagesSliderData = new Thread(new GetRoomImagesSliderData());
        getRoomimagesSliderData.start();

        Thread getRoomContentData = new Thread(new GetRoomContentData());
        getRoomContentData.start();

        Thread getRoomOptionalData = new Thread(new GetRoomOptionalData());
        getRoomOptionalData.start();

        Thread getAverageRatingData = new Thread(new GetAverageRatingData());
        getAverageRatingData.start();

        Thread getPersonalRatingData = new Thread(new GetPersonalRatingData());
        getPersonalRatingData.start();

        Thread getEvaluateData = new Thread(new GetEvaluateData());
        getEvaluateData.start();

        Thread[] roomGroup = {
                getRoomimagesSliderData,
                getRoomContentData,
                getRoomOptionalData,
                getAverageRatingData,
                getPersonalRatingData,
                getEvaluateData
        };

        ThreadGroupHandler threadGroupHandler = new ThreadGroupHandler(roomGroup, pd);
        threadGroupHandler.start();


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

                new Thread(new SendEvaluateData(evaluateCommitButton, content)).start();
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
                                new Thread(new SendRatingData(ratingBar.getRating(), ratingSendButton, ratingSendButton.getText())).start();
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
    public class GetRoomImagesSliderData implements Runnable {

        @Override
        public void run() {
            try {
                String param = URLP.PARAM_DOCUMENT_SRL + roomSrl;
                String result = new JsonHandler(URLP.ROOM_IMAGE_LIST, param).execute().get();

                final ArrayList<String> images = new ArrayList<>();

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);

                    final String fileLocation = obj.getString("file");

                    String fileUrl = URLP.BASE_URL + fileLocation.substring(2, fileLocation.length());
                    images.add(fileUrl);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final ArrayList<String> mainImages = images;

                        for (int i = 0; i < mainImages.size(); i++) {
                            final int position = i;
                            DefaultSliderView textSliderView = new DefaultSliderView(RoomActivity.this);
                            textSliderView
                                    .image(mainImages.get(i))
                                    .setScaleType(BaseSliderView.ScaleType.Fit)
                                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                        @Override
                                        public void onSliderClick(BaseSliderView slider) {
                                            Intent intent = new Intent(RoomActivity.this, RoomImageActivity.class);
                                            intent.putExtra("url", mainImages.get(position));
                                            startActivity(intent);
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
                DB.sendToast("ErrorCode 15: " + e.getMessage(), 2);
            }
        }
    }


    // =======================================
    //   방 정보 로딩 핸들러 - 1
    // =======================================
    public class GetRoomContentData implements Runnable {
        @Override
        public void run() {
            try {
                String param = URLP.PARAM_DOCUMENT_SRL + roomSrl;
                String result = new JsonHandler(URLP.ROOM_CONTENT_DATA, param).execute().get();

                JSONObject jsonObject = new JSONObject(result);
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
                DB.sendToast("ErrorCode 16: " + e.getMessage(), 2);
            }
        }
    }


    // =======================================
    //   방 정보 로딩 핸들러 - 2
    // =======================================
    public class GetRoomOptionalData implements Runnable {
        @Override
        public void run() {

            try {
                String param = URLP.PARAM_DOCUMENT_SRL + roomSrl;
                String result = new JsonHandler(URLP.ROOM_OPTIONAL_DATA, param).execute().get();

                String value;
                JSONObject obj;

                JSONObject jsonObject = new JSONObject(result);
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
                final String monthorseme = value;

                // 금액
                obj = jsonArray.getJSONObject(6);
                value = obj.getString("value");
                final String money = value;

                // 관리비 포함 항목
                obj = jsonArray.getJSONObject(7);
                value = obj.getString("value");
                final String includeAdmin = getTokenString(value);

                // 평균 관리비
                obj = jsonArray.getJSONObject(8);
                value = obj.getString("value");
                final String monthlyMoney = value;

                // 난방
                obj = jsonArray.getJSONObject(9);
                value = obj.getString("value");
                final String fireType = value;

                // 엘리베이터
                obj = jsonArray.getJSONObject(10);
                value = obj.getString("value");
                final String elevator = value;

                // 주차 여부
                obj = jsonArray.getJSONObject(11);
                value = obj.getString("value");
                final String parking = value;

                // 입주 가능 월
                obj = jsonArray.getJSONObject(12);
                value = obj.getString("value");
                final String into = value;

                // 최대 인원
                obj = jsonArray.getJSONObject(13);
                value = obj.getString("value");
                final String maxPeople = value;

                // 옵션 품목
                obj = jsonArray.getJSONObject(14);
                value = obj.getString("value");
                final String optional = getTokenString(value);

                // 연락처
                obj = jsonArray.getJSONObject(15);
                value = obj.getString("value");
                final String phone = getPhoneTokenString(value);

                // 경도
                obj = jsonArray.getJSONObject(16);
                value = obj.getString("value");
                final Double lat = Double.parseDouble(value);

                // 위도
                obj = jsonArray.getJSONObject(17);
                value = obj.getString("value");
                final Double lng = Double.parseDouble(value);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        roomTopSubTitle.setText(subTitle);
                        roomTopDeposit.setText(deposit);
                        roomTopMonthOrSeme.setText(monthorseme);
                        roomTopMoney.setText(money);

                        roomTopRoomType.setText(roomType);
                        roomTopRoomFloor.setText(floor);
                        roomTopMonthlyMoney.setText(monthlyMoney);

                        roomInfoRoomType.setText(roomType);
                        roomInfoRoomSize.setText(roomSize);
                        roomInfoDeposit.setText(deposit);
                        roomInfoMonthOrSeme.setText(monthorseme);
                        roominfoMoney.setText(money);
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
                                                new Thread(new SendContactLog(1, phone)).start();
                                            }
                                        })
                                        .setNegativeButton("문자", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                new Thread(new SendContactLog(2, phone)).start();
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
                DB.sendToast("ErrorCode 17: " + e.getMessage(), 2);
            }
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
    public class SendContactLog implements Runnable {

        public Integer type;
        public String phone;

        public SendContactLog(Integer type, String phone) {
            this.type = type;
            this.phone = phone;
        }

        @Override
        public void run() {
            // 문의 버튼 클릭 핸들러
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String param = URLP.PARAM_DOCUMENT_SRL + roomSrl + "&phone=" + DB.phone_number;
                        String result = new JsonHandler(URLP.ROOM_SEND_CONTACT_LOG, param).execute().get();
                    } catch (Exception e) {
                        DB.sendToast("ErrorCode 18: " + e.getMessage(), 2);
                    }
                }
            });

            th.start();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
                }
            });
        }
    }


    // =======================================
    //   별점평가 전송 핸들러
    // =======================================
    public class SendRatingData implements Runnable {
        public Float rating;
        public TextView sendButton;
        public CharSequence text;

        public SendRatingData(Float rating, TextView sendButton, CharSequence text) {
            this.rating = rating;
            this.sendButton = sendButton;
            this.text = text;
        }

        @Override
        public void run() {

            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sendButton.setEnabled(false);
                    }
                });

                String param;
                String url = "";

                param = URLP.PARAM_DOCUMENT_SRL + roomSrl + "&phone=" + DB.phone_number + "&rate=" + rating;
                switch ((String) text) {
                    case "제출":
                        url = URLP.ROOM_SEND_FISRT_RATING_DATA;
                        break;
                    case "수정":
                        url = URLP.ROOM_SEND_MOD_RATING_DATA;
                        break;
                }

                String result = new JsonHandler(url, param).execute().get();

                if (result.contains("TRUE")) {
                    DB.sendToast("제출완료", 1);
                } else {
                    DB.sendToast("제출실패", 2);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sendButton.setEnabled(true);
                    }
                });

                new Thread(new GetAverageRatingData()).start();
                new Thread(new GetPersonalRatingData()).start();

            } catch (Exception e) {
                DB.sendToast("ErrorCode 19: " + e.getMessage(), 2);
            }
        }
    }


    // =======================================
    //   평균 별점정보 불러오기 핸들러
    // =======================================
    public class GetAverageRatingData implements Runnable {
        @Override
        public void run() {
            try {
                String param = URLP.PARAM_DOCUMENT_SRL + roomSrl;
                String result = new JsonHandler(URLP.ROOM_AVERAGE_RATING_DATA, param).execute().get();

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                JSONObject obj = jsonArray.getJSONObject(0);

                final Double rate = Math.ceil(obj.getDouble("rate") / 0.5) * 0.5;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ratingAverage.setText("평균 : " + rate);
                    }
                });

            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ratingAverage.setText("평가없음");
                    }
                });
            }
        }
    }


    // =======================================
    //   개인 별점정보 불러오기 핸들러
    // =======================================
    public class GetPersonalRatingData implements Runnable {
        @Override
        public void run() {
            try {
                String param = URLP.PARAM_DOCUMENT_SRL + roomSrl + "&phone=" + DB.phone_number;
                String result = new JsonHandler(URLP.ROOM_PERSONAL_RATING_DATA, param).execute().get();
                Log.d("e", result);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                JSONObject obj;

                try {
                    obj = jsonArray.getJSONObject(0);
                } catch (Exception e) {
                    return;
                }

                final Float pRate = Float.parseFloat(obj.getDouble("rate") + "");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ratingBar.setRating(pRate);
                        ratingSendButton.setText("수정");
                    }
                });
            } catch (Exception e) {
                DB.sendToast("ErrorCode 20: " + e.getMessage(), 2);
            }
        }
    }


    // =======================================
    //   평가하기 전송 핸들러
    // =======================================
    public class SendEvaluateData implements Runnable {

        public Button button;
        public String params;

        public SendEvaluateData(Button b, String params) {
            this.button = b;
            this.params = params;
        }

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    button.setEnabled(false);
                }
            });

            try {
                String data = URLEncoder.encode(params, "UTF-8");
                String param = URLP.PARAM_DOCUMENT_SRL + roomSrl + "&phone=" + DB.phone_number + "&data=" + data;
                String result = new JsonHandler(URLP.ROOM_SEND_COMMENT_DATA, param).execute().get();

                if (result.contains("TRUE")) {
                    // 전송 성공 목록 초기화
                } else {
                    throw new Exception("전송실패");
                }

                new Thread(new GetEvaluateData()).start();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        evaluateTextField.setText("");
                        button.setEnabled(true);
                    }
                });
            } catch (Exception e) {
                DB.sendToast("ErrorCode 21: " + e.getMessage(), 2);
            }
        }
    }


    // =======================================
    //   한줄평가 리스트 로딩 핸들러
    // =======================================
    public class GetEvaluateData implements Runnable {
        @Override
        public void run() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    evaluateContentLayout.removeAllViews();
                }
            });

            try {
                String param = URLP.PARAM_DOCUMENT_SRL + roomSrl;
                String result = new JsonHandler(URLP.ROOM_EVALUATE_DATA, param).execute().get();

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);

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
                DB.sendToast("ErrorCode 22: " + e.getMessage(), 2);
            }
        }
    }


    // =======================================
    //   한줄평가 리스트 로딩 메소드
    // =======================================
    public void getEvaluateDataMethod() {
        new Thread(new GetEvaluateData()).start();
    }
}
