package splashapp.android.nttd.cas.com.new_splash_app.constants;

public class CommonConst {

    public static String CTMS_AGENT_PACKAGE_NAME = "castles.ctms.service";
    public static String CTMS_AGENT_SERVICE = "castles.ctms.service.CtmsApiService";

    public static String ACTION_TYPE_KEY = "actionType";
    public static int SERVICE_ACTION_TYPE_START_POLLING_PROCESS = 0;
    public static int SERVICE_ACTION_TYPE_START_INSTALL_PROCESS = 1;
    public static int SERVICE_ACTION_TYPE_START_TAMPER_PROCESS = 2;
    public static int SERVICE_ACTION_TYPE_START_SPLASH_PROCESS = 3;

    public final static int SUCCESS = 0x0000;
    public final static int DOING = 0x0001;
    public final static int INSTALL_COMPLETE = 0x0FFF;
    public final static int START = 0x1000;

    public static final int ENABLE_CONFIG_CTMS_NOT_ENABLE_VALUE = 0;
    public static final int ENABLE_CONFIG_CTMS_ENABLE_VALUE = 1;
    public static final String TERMINAL_SN_EMPTY = "0000000000000000";

    public class FileType{
        public static final int FILE_TYPE_UPDATELIST = 0xFF;
        public static final int FILE_TYPE_OTA = 0xFE;
        public static final int FILE_TYPE_APK = 0xFD;
        public static final int FILE_TYPE_SMF = 0xFC;
        public static final int FILE_TYPE_CMF = 0xFB;
        public static final int FILE_TYPE_SBL = 0xFA;
        public static final int FILE_TYPE_SME = 0xF9;
        public static final int FILE_TYPE_AME = 0xF8;
        public static final int FILE_TYPE_LYX = 0xF7;
        public static final int FILE_TYPE_FREE = 0xF6;
        //    public static final int UPDATE_DATA_TYPE_FW = 0x00;
//    public static final int UPDATE_DATA_TYPE_AP = 0x01;
//    public static final int UPDATE_DATA_TYPE_FILE = 0x02;
//    public static final int UPDATE_DATA_TYPE_SHARE_LIB = 0x04;
//    public static final int UPDATE_DATA_TYPE_AP_LIB = 0x05;
        public static final int FILE_TYPE_PRM = 0x07;

        public static final int FILE_STYPE_EMV = 37;
        public static final int FILE_STYPE_EMVCL = 38;
    }

    //Status Bar
    public static final String StatusBarFilePath = "/data/CastlesFile/StatusBarView.json";
    public static final String StatusBarDefaultFlag = "{\"StatusBarView\":false}";
    // Navigation Bar
    public static final String NavBarIconFilePath = "/data/CastlesFile/NavBarIcon.json";
    public static final String NavBarIconDefaultFlag = "{\"Home\":true,\"Back\":true,\"Search\":true,\"NavBarView\":true}";

    public static final String SATRUN_REMOVE_NAVIGATION_BAR = "SATRUN_REMOVE_NAVIGATION_BAR";
    public static final String SATRUN_SHOW_NAVIGATION_BAR = "SATRUN_SHOW_NAVIGATION_BAR";

}
