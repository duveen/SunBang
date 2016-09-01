package kr.o3selab.sunbang.Instance;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.kakao.kakaolink.KakaoLink;

import java.util.HashMap;
import java.util.LinkedList;

import kr.o3selab.sunbang.Activity.LoadingActivity;

/**
 * Created by samgi.park on 2016-07-29.
 */
public class DB {

    //개발자 모드
    public static boolean debug = true;

    //버전 정보
    public static double version = 1.0007;

    //저장 정보
    public static String DEVICE_ID = "device_id";
    public static String PHONE_NUMBER = "phone";
    public static String device_id = "";
    public static String phone_number = "";

    //모듈 정보
    public static Integer NOTICE_MODULE = 139;
    public static Integer ROOM_MODULE = 171;

    //컨텍스트, 액티비티
    public static Context context;
    public static AppCompatActivity activity;

    //메인 이미지 슬라이더
    public static HashMap<String, String> mainImages;
    public static LinkedList<LoadingActivity.RoomContent> roomList;

    // 위치 정보
    public static HashMap<String, Double> sLocationLat;
    public static HashMap<String, Double> sLocationLng;
    public static final String MAIN_BUILDING = "sMainBuilding";
    public static final String ENGINEERING_BUILDING = "sEngineeringBuilding";
    public static final String WONHWAKWAN = "sWonhwakwan";
    public static final String NATURE_SCIENCE_BUILDING = "sNatureScienceBuilding";
    public static final String LIBERAL_ARTS_BUILDING = "sLiberalArtsBuilding";
    public static final String HOSPITAL_BUILDING = "sHospitalBuilding";
    public static void updateLocation() {
        sLocationLat.put("sMainBuilding", 36.80022619356785);
        sLocationLng.put("sMainBuilding", 127.07495224085592);
        sLocationLat.put("sEngineeringBuilding", 36.80014881099762);
        sLocationLng.put("sEngineeringBuilding", 127.07259066506715);
        sLocationLat.put("sWonhwakwan", 36.800121110489364);
        sLocationLng.put("sWonhwakwan", 127.07722679752683);
        sLocationLat.put("sNatureScienceBuilding", 36.798748938187586);
        sLocationLng.put("sNatureScienceBuilding", 127.07404319502567);
        sLocationLat.put("sLiberalArtsBuilding", 36.798779336811386);
        sLocationLng.put("sLiberalArtsBuilding", 127.07585283529149);
        sLocationLat.put("sHospitalBuilding", 36.79914030750248);
        sLocationLng.put("sHospitalBuilding", 127.07851439634625);
    }
    public static String getLocationName(String location) {
        switch(location) {
            case MAIN_BUILDING:
                return "본관";
            case ENGINEERING_BUILDING:
                return "공학관";
            case WONHWAKWAN:
                return "원화관";
            case NATURE_SCIENCE_BUILDING:
                return "자연관";
            case LIBERAL_ARTS_BUILDING:
                return "인문관";
            case HOSPITAL_BUILDING:
                return "보건관";
        }

        return null;
    }
    public static final String DEFAULT_BUILDING = "dBuilding";
    public static String defaultBuilding = MAIN_BUILDING;


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



}
