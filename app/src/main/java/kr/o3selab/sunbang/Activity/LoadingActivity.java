package kr.o3selab.sunbang.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Vector;

import kr.o3selab.sunbang.Instance.DB;
import kr.o3selab.sunbang.MainActivity;
import kr.o3selab.sunbang.R;

public class LoadingActivity extends AppCompatActivity implements DialogInterface.OnClickListener {
    public final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        DB.context = this;
        DB.activity = this;
        try {
            getPermission();
        } catch (Exception e) {
            DB.sendToast(e.getMessage(), 2);
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

        switch(i) {
            case AlertDialog.BUTTON_POSITIVE:
                Toast.makeText(this, "계속 진행합니다.", Toast.LENGTH_SHORT).show();
                Thread th = new Thread(new GetVersionData());
                th.start();
                break;
            case AlertDialog.BUTTON_NEGATIVE:
                Toast.makeText(this, "프로그램을 종료합니다.", Toast.LENGTH_SHORT).show();
                this.finish();
                break;
            default:
                Toast.makeText(this, "에러", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onBackPressed() { }


    // =======================================
    //   퍼미션 권한 획득
    // =======================================
    public void getPermission() {
        if (ContextCompat.checkSelfPermission(LoadingActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(LoadingActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(LoadingActivity.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(LoadingActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                DB.sendToast("슬라이더 이미지를 불러오기 위해 필요한 권한입니다.", 2);
            }

            ActivityCompat.requestPermissions(LoadingActivity.this,
                    new String[]{
                             Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ,Manifest.permission.READ_EXTERNAL_STORAGE
                            ,Manifest.permission.READ_PHONE_STATE
                    },

                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            NetworkCheck();
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
                    NetworkCheck();
                } else {
                    DB.sendToast("권한을 획득하지 못해 프로그램을 종료합니다. 다시 실행시켜주세요!", 2);
                    LoadingActivity.this.finish();
                }
                return;
            }
        }
    }


    // =======================================
    //   네트워크 체크
    // =======================================
    public void NetworkCheck() {

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


        Thread thread = new Thread() {
            @Override
            public void run() {
                while(true) {
                    if(DB.firstData) {
                        Intent i = new Intent(LoadingActivity.this, MainActivity.class);
                        startActivity(i);
                        LoadingActivity.this.finish();
                        break;
                    } else {
                        try {
                            Thread.currentThread().sleep(1000);
                        } catch (InterruptedException e) {  }
                    }
                }
            }
        };

        thread.start();
    }


    // =======================================
    //   버전 정보 확인
    // =======================================
    public class GetVersionData implements Runnable {
        @Override
        public void run() {
            try {
                getPhoneData();

                URL url = new URL("http://sunbang.o3selab.kr/version.txt");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("euc-kr")));

                if( DB.version != Double.parseDouble(br.readLine())) {
                    // DB.sendToast("버전 이 다름", 1);
                    LoadingActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(LoadingActivity.this)
                                    .setTitle("업데이트 필요!")
                                    .setMessage("프로그램을 최신 버전으로 업데이트 해주셔야 사용 가능합니다!")
                                    .setCancelable(false)
                                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            LoadingActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                    try {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                    } catch (android.content.ActivityNotFoundException anfe) {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                    }
                                                    LoadingActivity.this.finish();
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            LoadingActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(LoadingActivity.this, "프로그램을 종료합니다!", Toast.LENGTH_SHORT).show();
                                                    LoadingActivity.this.finish();
                                                }
                                            });
                                        }
                                    })
                                    .show();
                        }
                    });
                } else {
                    // DB.sendToast("버전이 일치함", 1);
                    Thread th = new Thread(new GetMainImageData());
                    th.start();
                }

            } catch ( Exception e ) { }
        }
    }


    // =======================================
    //   휴대폰 정보 수집
    // =======================================
    public void getPhoneData() {
        TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);

        String mID = Settings.Secure.getString(LoadingActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String mPhoneNumber = tMgr.getLine1Number();

        SharedPreferences sharedPreferences = DB.getSharedPreferences();
        if(!sharedPreferences.getString(DB.DEVICE_ID, "").equals(mID)) {

            SharedPreferences.Editor editor = DB.getEditor();
            editor.putString(DB.DEVICE_ID, mID);
            editor.putString(DB.PHONE_NUMBER, mPhoneNumber);
            editor.commit();

            sendPhoneData(mID, mPhoneNumber);
        }

        DB.device_id = mID;
        DB.phone_number = mPhoneNumber;
    }


    // =======================================
    //   휴대폰 정보 전송
    // =======================================
    public void sendPhoneData(String mID, String mPhoneNumber) {
        try {
            URL url = new URL("http://sunbang.o3selab.kr/script/sendUserData.php?mId="+mID+"&phone="+mPhoneNumber);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            InputStream is = con.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("euc-kr")));
            if(br.readLine().equals("TRUE")) {
            } else {
            }
            br.close();

        } catch (Exception e) {

        }
    }


    // =======================================
    //   메인 이미지 수집
    // =======================================
    public class GetMainImageData implements Runnable {
        @Override
        public void run() {
            try {
                // =======================================
                //   메인 이미지 수집
                // =======================================
                // DB.sendToast("이미지 로딩중", 1);
                Vector<String> mainImages = new Vector<>();

                URL url = new URL("http://sunbang.o3selab.kr/script/getMainImageLink.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8")));

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = br.readLine()) != null) {
                    sb.append(line);
                }
                JSONObject jsonObject = new JSONObject(sb.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String link = obj.getString("link");

                    mainImages.add(link);
                }

                DB.mainImages = mainImages;
                DB.firstData = true;

            } catch (MalformedURLException e) {
                DB.sendToast("연결 실패." + e.getMessage(), 2);
            } catch (IOException e) {
                DB.sendToast("서버 접속 실패." + e.getMessage(), 2);
            } catch (JSONException e) {
                DB.sendToast("데이터 오류" + e.getMessage(), 2);
            }
        }
    }

}
