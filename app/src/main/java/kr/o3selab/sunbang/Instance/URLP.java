package kr.o3selab.sunbang.Instance;

public class URLP {
    public static final String BASE_URL;
    public static final String API_URL;
    public static final String VERSION;
    public static final String API_DIR;
    public static final String SEND_USER_DATA;
    public static final String MAIN_IMAGE_LIST;
    public static final String MAIN_NOTCIE_LIST;
    public static final String MAIN_ROOM_LIST;
    public static final String MAIN_ROOM_CONTENT;
    public static final String MAIN_ROOM_CONTENT_LIST;
    public static final String FIND_ROOM_LIST;
    public static final String FIND_ROOM_LIST_WITH_LAT_LNG;
    public static final String FIND_ROOM_INFO;
    public static final String NOTICE_LIST_DOCUMENT_COUNT;
    public static final String NOTICE_LIST_DOCUMENT_LIMIT;
    public static final String NOTICE_DOCUMENT;
    public static final String SEARCH_RESULT_DATA;
    public static final String MAP_ALL_LOCATION;
    public static final String MAP_ALL_LOCATION_GET_TITLE;
    public static final String ROOM_IMAGE_LIST;
    public static final String ROOM_CONTENT_DATA;
    public static final String ROOM_OPTIONAL_DATA;
    public static final String ROOM_SEND_CONTACT_LOG;
    public static final String ROOM_AVERAGE_RATING_DATA;
    public static final String ROOM_PERSONAL_RATING_DATA;
    public static final String ROOM_EVALUATE_DATA;
    public static final String ROOM_SEND_COMMENT_DATA;
    public static final String ROOM_SEND_FISRT_RATING_DATA;
    public static final String ROOM_SEND_MOD_RATING_DATA;
    public static final String ROOM_SEND_DELETE_COMMENT;


    public static final String PARAM_MODULE_SRL;
    public static final String PARAM_DOCUMENT_SRL;


    static {
        BASE_URL = "http://sunbang.o3selab.kr/";
        API_DIR = "script/";
        API_URL = BASE_URL + API_DIR;
        VERSION = BASE_URL + "version.txt";
        SEND_USER_DATA = API_URL + "sendUserData";
        MAIN_IMAGE_LIST = API_URL + "getMainImageLink";
        MAIN_NOTCIE_LIST = API_URL + "getMainNoticeListData";
        MAIN_ROOM_LIST = API_URL + "getMainRoomListData";
        MAIN_ROOM_CONTENT_LIST = API_URL + "getMainRoomContentList";
        MAIN_ROOM_CONTENT = API_URL + "getMainRoomContentData";
        FIND_ROOM_LIST = API_URL + "getFindRoomList";
        FIND_ROOM_LIST_WITH_LAT_LNG = API_URL + "getFindRoomListWithLatLng";
        FIND_ROOM_INFO = API_URL + "getFindRoomInfo";
        NOTICE_LIST_DOCUMENT_COUNT = API_URL + "getNoticeListCount";
        NOTICE_LIST_DOCUMENT_LIMIT = API_URL + "getNoticeListData";
        NOTICE_DOCUMENT = API_URL + "getNoticeContent";
        SEARCH_RESULT_DATA = API_URL + "getSearchResultData";
        MAP_ALL_LOCATION = API_URL + "getAllMapLocation";
        MAP_ALL_LOCATION_GET_TITLE = API_URL + "getDocumentTitle";
        ROOM_IMAGE_LIST = API_URL + "getRoomImageData";
        ROOM_CONTENT_DATA = API_URL + "getRoomContentData";
        ROOM_OPTIONAL_DATA = API_URL + "getRoomOptionalData";
        ROOM_SEND_CONTACT_LOG = API_URL + "sendContactLog";
        ROOM_AVERAGE_RATING_DATA = API_URL + "getAverageRatingData";
        ROOM_PERSONAL_RATING_DATA = API_URL + "getPersonalRatingData";
        ROOM_EVALUATE_DATA = API_URL + "getEvaluateData";
        ROOM_SEND_COMMENT_DATA = API_URL + "sendCommentData";
        ROOM_SEND_FISRT_RATING_DATA = API_URL + "sendFirstRatingData";
        ROOM_SEND_MOD_RATING_DATA = API_URL + "sendModRatingData";
        ROOM_SEND_DELETE_COMMENT = API_URL + "sendDeleteComment";


        PARAM_MODULE_SRL = "module_srl=";
        PARAM_DOCUMENT_SRL = "document_srl=";
    }
}
