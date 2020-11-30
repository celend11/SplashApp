package splashapp.android.nttd.cas.com.new_splash_app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import CTOS.CtCtms;
import CTOS.CtSystem;
import castles.ctms.module.commonbusiness.DownloadInfo;
import castles.ctms.module.commonbusiness.IAgentCallback;
import castles.ctms.module.commonbusiness.IStatusCallback;
import splashapp.android.nttd.cas.com.new_splash_app.Util.FileUtil;
import splashapp.android.nttd.cas.com.new_splash_app.Util.JsonFileUtil;
import splashapp.android.nttd.cas.com.new_splash_app.Util.SystemUtil;
import splashapp.android.nttd.cas.com.new_splash_app.constants.CommonConst;
import splashapp.android.nttd.cas.com.new_splash_app.constants.SystemDefine;
import splashapp.android.nttd.cas.com.new_splash_app.view.DialogView;


/**
 * @author Administrator
 */
public class MainActivity extends AppCompatActivity implements IStatusCallback{
    private static final String TAG = MainActivity.class.getSimpleName();
    public static long timeout = 30 * 60 * 1000;

    private DialogView dialogView;
    private ImageButton imageButton;
    private TextView tvVersionName;
    private CtCtms ctCtms;
    private String agentPackageName = "package:jp.co.nttdata.dlc.agent";

    private int type = 0;
    private String updateName = "";
    private boolean isShowNavBarView = false;

    private String GetUpdateTypeName(int type){
        String updateName = "";
        switch (type){
            case CommonConst.FileType.FILE_TYPE_SMF:
                updateName = "Updating(SMF)....";
                break;
            case CommonConst.FileType.FILE_TYPE_APK:
                updateName = "Updating(Application)....";
                break;
            case CommonConst.FileType.FILE_TYPE_OTA:
                updateName = "Updating(OS)....";
                break;
            case CommonConst.FileType.FILE_STYPE_EMV:
            case CommonConst.FileType.FILE_STYPE_EMVCL:
            case  CommonConst.FileType.FILE_TYPE_AME:
            case CommonConst.FileType.FILE_TYPE_CMF:
            case CommonConst.FileType.FILE_TYPE_FREE:
            case CommonConst.FileType.FILE_TYPE_LYX:
            case CommonConst.FileType.FILE_TYPE_PRM:
            case CommonConst.FileType.FILE_TYPE_SBL:
            case CommonConst.FileType.FILE_TYPE_SME:
            case CommonConst.FileType.FILE_TYPE_UPDATELIST:
                updateName = "Updating(Patch)....";
                break;
            default:
                updateName = "";
                break;
        }
        return updateName;
    }

//    private BroadcastReceiver packageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String packageName = intent.getDataString();
//            String action = intent.getAction();
//            if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
//                Log.e(TAG, "onReceive:added/ " + packageName);
//            } else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
//                Log.e(TAG, "onReceive:reinstall/ " + packageName);
//                if (agentPackageName.equals(packageName)) {
//                    Log.e(TAG, "start_DLA");
//                    handler.sendEmptyMessage(8);
//                }
//            } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
//                Log.e(TAG, "onReceive:remove/ " + packageName);
//            }
//        }
//    };


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //msg.what = 11
            switch (msg.what) {
                case 1:
                    //startTiming
                    dialogView.startTiming();
                    break;
                case 2:
                    //stopTiming
                    dialogView.stopTiming();
                    break;
                case 3:
//                    System.exit(0);
                    skipToLaunch();
                    break;
                case 5:
                    //start callback

                    break;
                case 6:
                    //timeout
                    if (dialog_outtime == null) {
                        initDialogOuttime();
                    }
                    if (!isFinishing()) {
                        if (!dialog_outtime.isShowing()) {
                            dialog_outtime.show();
                        }
                    }
                    break;
                case 8:
                    if (isAviliblePackageName(MainActivity.this, CommonConst.CTMS_AGENT_PACKAGE_NAME)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            if(ctCtms!=null){
                                ctCtms.registerCallback(MainActivity.this);
                            }
                            (MainActivity.this).startForegroundService(getStartCTMSInstallIntent());
                        }else {
                            (MainActivity.this).startService(getStartCTMSInstallIntent());
                        }
//                        checkCtms();
                        handler.sendEmptyMessage(5);
                    } else {
//                        Toast.makeText(MainActivity.this,"jp.co.nttdata.dlc.agent is not avilible.",Toast.LENGTH_LONG).show();
                        handler.sendEmptyMessage(3);
                    }
                    break;
                case 9:
//                    registerPackageReceiver();
                    break;
                case 10:
                    try {
                        //取得資訊
                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
                        String strVersionName = pInfo.versionName;
                        tvVersionName.setText(strVersionName);

                    } catch (PackageManager.NameNotFoundException x) {
                    }
                    break;
                case 11:
                    randomTime();
                    break;
                default:
                    break;
            }
        }
    };

    private AlertDialog dialog_back;
    private AlertDialog dialog_outtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String strDeivceModel = SystemUtil.getDeviceModel(SystemDefine.d_SYSTEM_COMMAND_GETPROP, SystemDefine.d_SYSTEM_COMMAND_RO_OEM__DEVICE);
        dialogView = findViewById(R.id.dialogView);
        imageButton = findViewById(R.id.imageButton);
        imageButton.setBackground(null);
        tvVersionName = findViewById(R.id.tv_versionName);
        ctCtms = ((MyApp)getApplication()).getCtCtms();
        initNavBarView();
        if(strDeivceModel.equals(SystemDefine.d_MODEL_S1E)){
            handler.sendEmptyMessage(11);
        }
//        handler.sendEmptyMessage(9);
        handler.sendEmptyMessage(1);
        handler.sendEmptyMessageDelayed(6, timeout);
        handler.sendEmptyMessage(10);
        imageButton.setOnClickListener(v -> {
            if (dialog_back == null) {
                initDialogBack();
            }
            if (!isFinishing()) {
                if (!dialog_back.isShowing()) {
                    dialog_back.show();
                }
            }
        });

        handler.sendEmptyMessageDelayed(8,4000);
//        handler.sendEmptyMessage(8);

    }

    private void checkCtms(){
        if(ctCtms.getCTMSStatus() != CommonConst.ENABLE_CONFIG_CTMS_ENABLE_VALUE){
            Log.d(TAG,"CTMS_ENABLE == false");
            handler.sendEmptyMessageDelayed(3,1000);
            return;
        }else{
            Log.d(TAG,"CTMS_ENABLE == true");
        }
        String json = ctCtms.getAllConfig();
        if(TextUtils.isEmpty(json)){
            handler.sendEmptyMessageDelayed(3,1000);
            return;
        }else{
            try {
                JSONObject jsonObject = new JSONObject(json);
                Log.d(TAG,"jsonObject == " +json);
                String sn = jsonObject.getString("Serial_Number");
                if(TextUtils.isEmpty(sn)){
                    Log.d(TAG,"sn == null");
                    handler.sendEmptyMessageDelayed(3,1000);
                    return;
                }else{
                    if(sn.equals(CommonConst.TERMINAL_SN_EMPTY)){
                        Log.d(TAG,"sn == " + CommonConst.TERMINAL_SN_EMPTY);
                        handler.sendEmptyMessageDelayed(3,1000);
                        return;
                    }
                }
            }catch (Exception exception){
                Log.d(TAG,exception.toString());
                handler.sendEmptyMessageDelayed(3,1000);
                return;
            }


        }
    }

    //get the navbar status send broadcast to hide navbar
    public void initNavBarView(){
        isShowNavBarView = getNavBarView();
        Log.d(TAG,"isShowNavBarView == " + isShowNavBarView);
        Intent intent = new Intent();
        intent.setAction(CommonConst.SATRUN_REMOVE_NAVIGATION_BAR);
        sendBroadcast(intent);
    }

//    public void registerPackageReceiver() {
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.intent.action.PACKAGE_ADDED");
//        filter.addAction("android.intent.action.PACKAGE_REPLACED");
//        filter.addAction("android.intent.action.PACKAGE_REMOVED");
//        filter.addDataScheme("package");
//        registerReceiver(packageReceiver, filter);
//    }

    public void randomTime() {
        if (SystemProperties.getBoolean("persist.ctos.sys.time.random", false)) {
            return;
        }

        JsonFileUtil jfile;
        jfile = new JsonFileUtil();
        JSONArray jarray = jfile.getOperationJson();
        JSONObject jobj;
        int time = 0, mode = 0;

        try {
            jobj = jarray.getJSONObject(1);
            time = jobj.getInt("RebootTime");
            jobj = jarray.getJSONObject(2);
            String temp = jobj.getString("RefreshMethod");
            if (temp.equals("Poweroff")) {
                mode = 2;
            } else if (temp.equals("reboot")) {
                mode = 1;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }

        final int random = new Random().nextInt(60);
        CtSystem system = new CtSystem();
        system.setAutoRebootTimeEx(time, random, true, mode);

        SystemProperties.set("persist.ctos.sys.time.random", "1");
    }

    public void initDialogBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("EXIT");
        builder.setMessage("Exit loading data immediately ?");
        builder.setPositiveButton("YES", (dialog, which) -> {
            handler.sendEmptyMessage(3);
        });
        builder.setNegativeButton("NO", (dialog, which) -> {
            dialog.dismiss();
        });
        dialog_back = builder.create();
    }

    public void initDialogOuttime() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("TIMEOUT");
        builder.setMessage("Waiting for data loading ?");
        builder.setPositiveButton("YES", (dialog, which) -> {
            handler.sendEmptyMessageDelayed(6, timeout);
        });
        builder.setNegativeButton("NO", (dialog, which) -> {
            handler.sendEmptyMessage(3);
        });
        dialog_outtime = builder.create();
    }

    private void skipToLaunch() {
//        Intent skip = new Intent();
//        skip.setAction(Intent.ACTION_MAIN);
//        skip.addCategory(Intent.CATEGORY_HOME);
//        startActivity(skip);
//        System.exit(0);
        //                android.os.Process.killProcess(android.os.Process.myPid());
        onDestroy();
//        try {
//            PackageManager packageManager = getPackageManager();
//            Intent intent = packageManager.getLaunchIntentForPackage(searchDefauleHome(MainActivity.this));
//            intent.putExtra("UpdateStatus", true);
//            startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TAG, "skipToLaunch: " + e.getMessage());
//        } finally {
            System.exit(0);
//        }
    }

    private String searchDefauleHome(Context ctx) {
        PackageManager localPackageManager = ctx.getPackageManager();
        List<IntentFilter> intentList = new ArrayList<IntentFilter>();
        List<ComponentName> prefActList = new ArrayList<ComponentName>();
        localPackageManager.getPreferredActivities(intentList, prefActList, null);

        int size = prefActList.size();
        ComponentName localComponentName = null;
        IntentFilter localIntentFilter;

        for (int i = 0; i < size; i++) {
            localIntentFilter = intentList.get(i);

            if (localIntentFilter.hasAction(Intent.ACTION_MAIN) && localIntentFilter.hasCategory(Intent.CATEGORY_HOME)) {
                localComponentName = prefActList.get(i);
                break;
            }
        }

        if (localComponentName != null) {
            String pn = localComponentName.getPackageName();
            return pn;
        }

        return "";
    }

    private static boolean isAviliblePackageName(Context context, String packageName) {

        final PackageManager packageManager = context.getPackageManager();

        List<PackageInfo> pinfo = packageManager.getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        for (int i = 0; i < pinfo.size(); i++) {
            if (packageName.equals(pinfo.get(i).packageName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isShowNavBarView){
            Intent navbarIntent = new Intent();
            navbarIntent.setAction(CommonConst.SATRUN_SHOW_NAVIGATION_BAR);
            sendBroadcast(navbarIntent);
        }
        Log.e(TAG, "onDestroy");
//        if (packageReceiver != null) {
//            unregisterReceiver(packageReceiver);
//        }
        if(ctCtms!=null){
            ctCtms.unregisterCallback(this);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    protected void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_HOME:
                if (dialog_back == null) {
                    initDialogBack();
                }
                if (!isFinishing()) {
                    if (!dialog_back.isShowing()) {
                        dialog_back.show();
                    }
                }

                break;

        }

        return true;
    }


    public static String getCurrentVersionName(Context context) {
        PackageInfo pinfoSplash;
        try {
            pinfoSplash = context.getPackageManager().getPackageInfo("zz.castles.zzservice", 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "packageName not found: ");
            return "";
        }
        return pinfoSplash.versionName;
    }

    private boolean getNavBarView() {
        if (!"".equals(FileUtil.read_file(CommonConst.NavBarIconFilePath))) {
            try {
                JSONObject NavBarIcon = new JSONObject(FileUtil.read_file(CommonConst.NavBarIconFilePath));
                return NavBarIcon.getBoolean("NavBarView");
            } catch (Exception e) {
                Log.d(TAG, "Error : " + e);
                return true;
            }
        } else {
            FileUtil.write_file(CommonConst.NavBarIconFilePath, CommonConst.NavBarIconDefaultFlag);
        }
        return true;
    }

    private Intent getStartCTMSInstallIntent(){
        Intent intent = new Intent();
        intent.setClassName("castles.ctms.service", "castles.ctms.service.CtmsApiService");
        intent.setData(Uri.parse("content://ctms_agent/alarm/splash"));
        intent.setAction("castles.android.SPLASH");
        intent.putExtra(CommonConst.ACTION_TYPE_KEY, CommonConst.SERVICE_ACTION_TYPE_START_SPLASH_PROCESS);
        return intent;
    }

    @Override
    public void readyCallback(){

        Log.d(TAG, "readyCallback");
        handler.sendEmptyMessage(3);

    }
    @Override
    public void connectCallback(int i){

        Log.d(TAG, "connectCallback == "+i);
        handler.sendEmptyMessage(3);

    }
    @Override
    public void getTerminalInfoCallback(int i){

        Log.d(TAG, "getTerminalInfoCallback == "+i);
        handler.sendEmptyMessage(3);

    }

    @Override
    public void downloadCallback(int i, DownloadInfo downloadInfo){

        Log.d(TAG, "downloadCallback == "+i);
        handler.sendEmptyMessage(3);

    }

    @Override
    public void diagnosticCallback(int i){

        Log.d(TAG, "diagnosticCallback == "+i);
        handler.sendEmptyMessage(3);

    }

    @Override
    public void installCallback(int i, castles.ctms.module.commonbusiness.PackageInfo packageInfo) {

        Log.d(TAG, "installCallback == "+i);
//        Log.d(TAG,packageInfo.fileName+"======"+packageInfo.type);
        if( i == CommonConst.START){
            if(type == 0 || type!= packageInfo.type){
                type = packageInfo.type;
                String update = GetUpdateTypeName(type);
                if(!TextUtils.isEmpty(update) && !updateName.equals(update)){
                    updateName = update;
                    runOnUiThread(() -> {
                        dialogView.appendMessage(updateName);
                    });
                }
            }
        }else  if(i == CommonConst.INSTALL_COMPLETE){
            handler.sendEmptyMessageDelayed(3,5000);
        }else{
            if(packageInfo == null){
                handler.sendEmptyMessage(3);
            }
        }

    }
}
