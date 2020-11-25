package splashapp.android.nttd.cas.com.new_splash_app;

import android.app.Application;
import android.util.Log;

import CTOS.CtCtms;
import splashapp.android.nttd.cas.com.new_splash_app.logutil.CrashHandler;

/**
 * Created by Church on 2018/8/24.
 *
 * @author Church
 */
public class MyApp extends Application {
    private static String TAG= MyApp.class.getSimpleName();
    private static CtCtms initCtCtms;
    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        initCtCtms = new CtCtms(this);
    }
    public static CtCtms getCtCtms(){
        return initCtCtms;
    }
}
