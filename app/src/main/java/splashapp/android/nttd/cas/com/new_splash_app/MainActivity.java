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
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import splashapp.android.nttd.cas.com.new_splash_app.Util.JsonFileUtil;
import splashapp.android.nttd.cas.com.new_splash_app.Util.SystemUtil;
import splashapp.android.nttd.cas.com.new_splash_app.constants.SystemDefine;
import splashapp.android.nttd.cas.com.new_splash_app.view.DialogView;


/**
 * @author Administrator
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static String VENDOR_ACTION = "jp.co.nttdata.dlc.agent.broadcast.vendor";
    public static long timeout = 30 * 60 * 1000;
    //    public static long timeout=5*1000;
    private static final String VERSION_NAME = "1.2.0";

    private DialogView dialogView;
    private ImageButton imageButton;
    private TextView tvVersionName;

    private String agentPackageName = "package:jp.co.nttdata.dlc.agent";
    private BroadcastReceiver packageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String packageName = intent.getDataString();
            String action = intent.getAction();
            if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                Log.e(TAG, "onReceive:added/ " + packageName);
            } else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
                Log.e(TAG, "onReceive:reinstall/ " + packageName);
                if (agentPackageName.equals(packageName)) {
                    Log.e(TAG, "start_DLA");
                    handler.sendEmptyMessage(8);
                }
            } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                Log.e(TAG, "onReceive:remove/ " + packageName);
            }
        }
    };

    private BroadcastReceiver vendorInitBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case 1:
                    Log.d(TAG, "vendor service execute success callback");
                    handler.sendEmptyMessage(2);
                    handler.sendEmptyMessage(3);
                    break;
                default:
                    Log.d(TAG, "vendor service error callback");
                    handler.sendEmptyMessage(3);
                    break;
            }
        }
    };

    private String VENDOR_MODEL_KEY = "vendor_model_key";
    private int VENDOR_MODEL_INIT = 1;
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
                    String currentVersionName = MainActivity.getCurrentVersionName(MainActivity.this);
                    Log.d(TAG, "currentVersionName: " + currentVersionName);
                    //call service
                    if (currentVersionName.compareTo(VERSION_NAME) >= 0) {
                        Intent intent = new Intent();
                        intent.setClassName("zz.castles.zzservice", "zz.castles.zzservice.MyService");
                        startService(intent);
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //timeout/vendor callback
                    skipToLaunch();
                    break;
                case 5:
                    //start initvendor
                    Intent intentInitVendor = new Intent();
                    intentInitVendor.setClassName("jp.co.nttdata.dlc.agent", "jp.co.nttdata.dlc.agent.VendorService");
                    intentInitVendor.setAction("jp.co.nttdata.dlc.agent.vendor.init");
                    intentInitVendor.putExtra(VENDOR_MODEL_KEY, VENDOR_MODEL_INIT);
                    PendingIntent piInitVendor = PendingIntent.getBroadcast(MainActivity.this, 0, new Intent(VENDOR_ACTION), PendingIntent.FLAG_CANCEL_CURRENT);
                    intentInitVendor.putExtra("vendor_init_callback", piInitVendor);
                    startService(intentInitVendor);
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
                case 7:
                    Intent ctCtmsIntent = new Intent();
                    ctCtmsIntent.setClassName("android.ctms_service", "android.ctms_service.CTMS_Service");
                    startService(ctCtmsIntent);
                    break;
                case 8:
                    if (isAviliblePackageName(MainActivity.this, "jp.co.nttdata.dlc.agent")) {
                        handler.sendEmptyMessage(5);
                    } else {
//                        Toast.makeText(MainActivity.this,"jp.co.nttdata.dlc.agent is not avilible.",Toast.LENGTH_LONG).show();
                        handler.sendEmptyMessage(3);
                    }
                    break;
                case 9:
                    registerPackageReceiver();
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
        registerReceiver(vendorInitBroadcast, new IntentFilter(VENDOR_ACTION));
        if(strDeivceModel.equals(SystemDefine.d_MODEL_S1E)){
            handler.sendEmptyMessage(11);
        }
        handler.sendEmptyMessage(9);
        handler.sendEmptyMessage(1);
        handler.sendEmptyMessage(7);
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

        ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), new ThreadPoolExecutor.AbortPolicy());
        singleThreadPool.execute(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "singleThreadPool_start");
            int status = 1;
            long intervalTime = 1000;
            CtCtms ctCtms = new CtCtms();
            do {
                try {
                    Thread.sleep(intervalTime);
                    status = ctCtms.getBootInstatllStatus();
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            } while (status != 0);
            handler.sendEmptyMessage(8);
        });
        singleThreadPool.shutdown();
    }

    public void registerPackageReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REPLACED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addDataScheme("package");
        registerReceiver(packageReceiver, filter);
    }

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


        try {
            PackageManager packageManager = getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(searchDefauleHome(MainActivity.this));
            intent.putExtra("UpdateStatus", true);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "skipToLaunch: " + e.getMessage());
        } finally {
            System.exit(0);
        }


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
        Log.e(TAG, "onDestroy");
        unregisterReceiver(vendorInitBroadcast);
        if (packageReceiver != null) {
            unregisterReceiver(packageReceiver);
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
}
