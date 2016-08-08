package kr.o3selab.sunbang.Instance;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by samgi.park on 2016-07-29.
 */
public class DB {
    //버전 정보
    public static double version = 1.0006;

    //저장 정보
    public static String DEVICE_ID = "device_id";
    public static String PHONE_NUMBER = "phone";
    public static String device_id = "";
    public static String phone_number = "";

    //모듈 정보
    public static Integer notice = 139;
    public static Integer room = 171;

    //로딩 데이터 플래그
    public static boolean firstData = false;

    //컨텍스트, 액티비티
    public static Context context;
    public static AppCompatActivity activity;

    //메인 이미지 슬라이더
    public static HashMap<String, String> mainImages;

    //다음 지도 apiKey
    public static String mapApiKey = "1011c8a2c4d75d3594f6f3e1fc22901f";

    //토스트 전송(쓰레드 가능)
    public static void sendToast(final String msg, final int type) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (type) {
                    case 1:
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                        break;
                }

            }
        });

    }

    //설정 정보
    public static String MY_SHARED_PREF = "my_shared";
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences getSharedPreferences() {
        sharedPreferences = context.getSharedPreferences(MY_SHARED_PREF, context.MODE_PRIVATE);
        return sharedPreferences;
    }
    public static SharedPreferences.Editor editor;
    public static SharedPreferences.Editor getEditor() {
        editor = getSharedPreferences().edit();
        return editor;
    }

    //베이스
    public final static String BASE_URL = "http://sunbang.o3selab.kr";
}
