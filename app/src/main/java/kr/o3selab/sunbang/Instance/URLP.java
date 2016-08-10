package kr.o3selab.sunbang.Instance;

/**
 * Created by duvee on 2016-08-08.
 */
public class URLP {
    public static final String BASE_URL;
    public static final String API_URL;
    public static final String VERSION;
    public static final String API_DIR;
    public static final String SEND_USER_DATA;
    public static final String MAIN_IMAGE_LIST;
    public static final String MAIN_NOTCIE_LIST;
    public static final String MAIN_ROOM_LIST;
    public static final String NOTICE_LIST_DOCUMENT_COUNT;
    public static final String NOTICE_LIST_DOCUMENT_LIMIT;
    public static final String NOTICE_DOCUMENT;
    public static final String SEARCH_RESULT_DATA;

    public static final String PARAM_MODULE_SRL;
    public static final String PARAM_DOCUMENT_SRL;


    static {
        BASE_URL = "http://sunbang.o3selab.kr/";
        API_DIR = "script/";
        API_URL = BASE_URL + API_DIR;
        VERSION = BASE_URL + "version.txt";
        SEND_USER_DATA = API_URL + "sendUserData.php";
        MAIN_IMAGE_LIST = API_URL + "getMainImageLink.php";
        MAIN_NOTCIE_LIST = API_URL + "getMainNoticeListData.php";
        MAIN_ROOM_LIST = API_URL + "getMainRoomListData.php";
        NOTICE_LIST_DOCUMENT_COUNT = API_URL + "getNoticeListCount.php";
        NOTICE_LIST_DOCUMENT_LIMIT = API_URL + "getNoticeListData.php";
        NOTICE_DOCUMENT = API_URL + "getNoticeContent.php";
        SEARCH_RESULT_DATA = API_URL + "getSearchResultData.php";

        PARAM_MODULE_SRL = "module_srl=";
        PARAM_DOCUMENT_SRL = "document_srl=";

    }
}
