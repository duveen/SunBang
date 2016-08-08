package kr.o3selab.sunbang.Instance;

import android.os.AsyncTask;

/**
 * Created by duvee on 2016-08-08.
 */
public class URLP {
    public static final String BASE_URL;
    public static final String API_URL;
    public static final String VERSION;
    public static final String API_DIR;
    public static final String SEND_USER_DATA;
    public static final String GET_MAIN_IMAGE_LINK;

    static {
        BASE_URL = "http://sunbang.o3selab.kr/";
        API_DIR = "script/";
        API_URL = BASE_URL + API_DIR;
        VERSION = BASE_URL + "version.txt";
        SEND_USER_DATA = API_URL + "sendUserData.php";
        GET_MAIN_IMAGE_LINK = API_URL + "getMainImageLink.php";

    }
}
