package kr.o3selab.sunbang.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;

import kr.o3selab.sunbang.Instance.DB;
import kr.o3selab.sunbang.Instance.JsonHandler;
import kr.o3selab.sunbang.Instance.URLP;
import kr.o3selab.sunbang.MainActivity;
import kr.o3selab.sunbang.R;

public class LoadingActivity extends AppCompatActivity implements DialogInterface.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        DB.context = this;
        DB.activity = this;

        try {
            getPermission();
        } catch (Exception e) {
            DB.sendToast("ErrorCode 1: " + e.getMessage(), 2);
        }

    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

        switch (i) {
            case AlertDialog.BUTTON_POSITIVE:
                DB.sendToast("계속 진행합니다.", 1);
                Thread th = new Thread(new GetVersionData());
                th.start();
                break;
            case AlertDialog.BUTTON_NEGATIVE:
                DB.sendToast("프로그램을 종료합니다.", 2);
                this.finish();
                break;
            default:
                DB.sendToast("ErrorCode 2:", 2);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        DB.activity = this;
        DB.context = this;
    }

    @Override
    public void onBackPressed() {
    }

    // =======================================
    //   퍼미션 권한 획득
    // =======================================
    public void getPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                networkCheck();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                DB.sendToast("권한을 획득하지 못해 프로그램을 종료합니다. 다시 실행시켜주세요!", 2);
                LoadingActivity.this.finish();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("메인 화면 구성을 위해서 필요한 권한 입니다.")
                .setDeniedMessage("권한이 없습니다. 프로그램을 다시 실행 해주세요!")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE)
                .check();
    }


    // =======================================
    //   네트워크 체크
    // =======================================
    public void networkCheck() {

        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); // 3G나 LTE등 데이터 네트워크에 연결된 상태
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); // 와이파이에 연결된 상태

        if (wifi.isConnected()) { // 와이파이에 연결된 경우
            Thread th = new Thread(new GetVersionData());
            th.start();
        } else if (mobile.isConnected()) { // 데이터 네트워크에 연결된 경우
            new AlertDialog.Builder(this)
                    .setTitle("경고")
                    .setMessage("현재 스마트폰이 3G/LTE 데이터 네트워크에 연결되어 있습니다. " +
                            "계속 진행시 데이터 사용료가 부과 될 수 있습니다. 계속 진행하시겠습니까?")
                    .setPositiveButton("네", this)
                    .setNegativeButton("아니오", this)
                    .setCancelable(false)
                    .show();
        } else { // 인터넷에 연결되지 않은 경우
            new AlertDialog.Builder(this)
                    .setTitle("경고")
                    .setMessage("이 프로그램은 데이터 네트워크/와이파이 환경에서만 사용 가능합니다. 프로그램을 종료합니다.")
                    .setNegativeButton("네", this)
                    .setCancelable(false)
                    .show();
        }
    }


    // =======================================
    //   버전 정보 확인
    // =======================================
    public class GetVersionData implements Runnable {
        @Override
        public void run() {
            try {
                URL url = new URL(URLP.VERSION);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Content-type", "text/plain");
                con.setConnectTimeout(3000);
                con.setReadTimeout(3000);

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.forName("euc-kr")));

                if (!br.readLine().contains(DB.version + "")) {
                    // 버전이 틀릴 경우
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(LoadingActivity.this)
                                    .setTitle("업데이트 필요!")
                                    .setMessage("프로그램을 최신 버전으로 업데이트 해주셔야 사용 가능합니다!")
                                    .setCancelable(false)
                                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                            try {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                            } catch (android.content.ActivityNotFoundException anfe) {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                            }
                                            LoadingActivity.this.finish();
                                        }
                                    })
                                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            DB.sendToast("프로그램을 종료합니다!", 1);
                                            LoadingActivity.this.finish();
                                        }
                                    })
                                    .show();
                        }
                    });
                } else {
                    // 버전이 일치 할 경우
                    getPhoneData();
                }
            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(LoadingActivity.this)
                                .setTitle("알림")
                                .setMessage("서버가 점검중 입니다.\n잠시 후 다시 시작해주세요!")
                                .setCancelable(false)
                                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        android.os.Process.killProcess(android.os.Process.myPid());
                                    }
                                })
                                .show();
                    }
                });
            }
        }
    }


    // =======================================
    //   휴대폰 정보 수집
    // =======================================
    public void getPhoneData() {
        try {
            TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

            String mID = Settings.Secure.getString(LoadingActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
            String mPhoneNumber = tMgr.getLine1Number();

            if (mPhoneNumber.contains("+82")) {
                mPhoneNumber = mPhoneNumber.replace("+82", "0");
            }

            SharedPreferences sharedPreferences = DB.getSharedPreferences();
            if (!sharedPreferences.getString(DB.DEVICE_ID, "").equals(mID) ||
                    !sharedPreferences.getString(DB.DEVICE_ID, "").equals(mPhoneNumber)) {

                SharedPreferences.Editor editor = DB.getEditor();
                editor.putString(DB.DEVICE_ID, mID);
                editor.putString(DB.PHONE_NUMBER, mPhoneNumber);
                editor.commit();

                Thread th = new Thread(new SendPhoneData(mID, mPhoneNumber));
                th.start();
            }

            DB.device_id = mID;
            DB.phone_number = mPhoneNumber;

            Thread th = new Thread(new GetMainImageData());
            th.start();
        } catch (Exception e) {
            DB.sendToast("Error Code 3: " + e.getMessage(), 2);
            DB.sendToast("프로그램을 종료합니다.", 2);

            LoadingActivity.this.finish();
        }
    }


    // =======================================
    //   휴대폰 정보 전송
    // =======================================
    public class SendPhoneData implements Runnable {

        private String mID;
        private String mPhoneNumber;

        public SendPhoneData(String mID, String mPhoneNumber) {
            this.mID = mID;
            this.mPhoneNumber = mPhoneNumber;
        }

        @Override
        public void run() {
            try {
                String param = "mId=" + mID + "&phone=" + mPhoneNumber;
                String result = new JsonHandler(URLP.SEND_USER_DATA, param).execute().get();
                if (!result.equals("TRUE")) {
                    Log.e("error", result);
                }
            } catch (Exception e) {
                DB.sendToast("ErrorCode 4: " + e.getMessage(), 2);
            }
        }
    }


    // =======================================
    //   메인 이미지 수집
    // =======================================
    public class GetMainImageData implements Runnable {

        @Override
        public void run() {
            try {
                HashMap<String, String> mainImages = new HashMap<>();

                String result = new JsonHandler(URLP.MAIN_IMAGE_LIST, null).execute().get();

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String link = obj.getString("link");
                    String srl = obj.getString("srl");

                    mainImages.put(link, srl);
                }

                DB.mainImages = mainImages;

                Thread th = new Thread(new GetMainRoomContentData());
                th.start();

            } catch (Exception e) {
                DB.sendToast("Error Code 5: " + e.getMessage(), 2);
            }
        }
    }


    // =======================================
    //   우선 순위 별 원룸 리스트 로딩 및 카카오 스킴 플래그 체크
    // =======================================
    public class GetMainRoomContentData implements Runnable {
        @Override
        public void run() {
            try {
                LinkedList<RoomContent> roomList = new LinkedList<>();

                String param = URLP.PARAM_MODULE_SRL + DB.ROOM_MODULE;
                String result = new JsonHandler(URLP.MAIN_ROOM_CONTENT_LIST, param).execute().get();
                Log.d("No.1", result);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject obj = jsonArray.getJSONObject(i);
                    Integer roomSrl = obj.getInt("document_srl");

                    param = URLP.PARAM_DOCUMENT_SRL + roomSrl;
                    result = new JsonHandler(URLP.MAIN_ROOM_CONTENT, param).execute().get();
                    Log.d("No.2", result);
                    JSONObject dJsonObject = new JSONObject(result);
                    JSONArray dJsonArray = dJsonObject.getJSONArray("result");

                    JSONObject value = dJsonArray.getJSONObject(0);
                    result = value.getString("result");

                    StringTokenizer str = new StringTokenizer(result, "|@|");
                    final String nickName = str.nextToken();
                    final String title = str.nextToken();
                    final String image = URLP.BASE_URL + str.nextToken().substring(2);
                    final String money = str.nextToken() + " / " + str.nextToken() + " " + str.nextToken() + " 만원";

                    param = URLP.PARAM_DOCUMENT_SRL + roomSrl;
                    result = new JsonHandler(URLP.ROOM_AVERAGE_RATING_DATA, param).execute().get();
                    Log.d("No.3", result);
                    dJsonObject = new JSONObject(result);
                    dJsonArray = dJsonObject.getJSONArray("result");

                    value = dJsonArray.getJSONObject(0);
                    String rate;

                    try {
                        if (value.getString("rate").equals("null"))
                            rate = "★ 평가없음";
                        else rate = "★ " + value.getString("rate");
                    } catch (Exception e) {
                        rate = "★ 평가없음";
                    }

                    roomList.add(new RoomContent(roomSrl + "", nickName, title, image, money, rate));
                }

                DB.roomList = roomList;


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = LoadingActivity.this.getIntent();
                        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                            Uri uri = intent.getData();
                            String temp_room_srl = uri.getQueryParameter("room_srl");
                            intent = new Intent(LoadingActivity.this, MainActivity.class);
                            intent.putExtra("room_flag", true);
                            intent.putExtra("room_srl", temp_room_srl);
                            startActivity(intent);
                            LoadingActivity.this.finish();
                        } else {
                            intent = new Intent(LoadingActivity.this, MainActivity.class);
                            intent.putExtra("room_flag", false);
                            startActivity(intent);
                            LoadingActivity.this.finish();
                        }
                    }
                });
            } catch (Exception e) {
                DB.sendToast("ErrorCode 21:" + e.getMessage(), 2);
            }
        }
    }


    // =======================================
    //   방 정보 클래스
    // =======================================
    public class RoomContent {
        public String nick_name;
        public String title;
        public String image;
        public String money;
        public String rate;
        public String roomSrl;

        public RoomContent(String roomSrl, String nick_name, String title, String image, String money, String rate) {
            this.nick_name = nick_name;
            this.title = title;
            this.image = image;
            this.money = money;
            this.rate = rate;
            this.roomSrl = roomSrl;
        }

        @Override
        public String toString() {
            return nick_name + ", " + title + ", " + image + ", " + money + ", " + rate;
        }
    }
}