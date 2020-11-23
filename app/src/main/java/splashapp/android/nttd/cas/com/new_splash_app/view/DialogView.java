package splashapp.android.nttd.cas.com.new_splash_app.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import splashapp.android.nttd.cas.com.new_splash_app.R;


/**
 * Created by Church on 2018/8/6.
 *
 * @author Church
 */
public class DialogView extends LinearLayout {
    private TextView title;
    private TextView time;
    private TextView message;
    private ProgressBar progressBar;
    private View myView;

    @SuppressLint("HandlerLeak")
    private Handler manhandle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    currentSecond = currentSecond + 1000;
                    time.setText(getFormatHMS(currentSecond));
                    manhandle.sendEmptyMessage(2);
                    break;
                case 2:
                    //start
                    if (!isPause) {
                        manhandle.sendEmptyMessageDelayed(1,1000);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private boolean isPause = false;
    private long currentSecond = 0;

    public DialogView(Context context) {
        super(context);
        LayoutInflater mInflater = LayoutInflater.from(context);
        View myView = mInflater.inflate(R.layout.dialog_layout, null);
        addView(myView);
        initView();
    }

    public DialogView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater mInflater = LayoutInflater.from(context);
        myView = mInflater.inflate(R.layout.dialog_layout, null);
        addView(myView);
        initView();
    }

    public DialogView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater mInflater = LayoutInflater.from(context);
        View myView = mInflater.inflate(R.layout.dialog_layout, null);
        addView(myView);
        initView();
    }

    private void initView() {
        title=myView.findViewById(R.id.title);
        time=myView.findViewById(R.id.time);
        message=myView.findViewById(R.id.message);
        progressBar=myView.findViewById(R.id.progressBar);
    }

    public void startTiming(){
        manhandle.sendEmptyMessage(2);
    }

    public void stopTiming(){
        isPause=true;
    }

    @SuppressLint("DefaultLocale")
    public static String getFormatHMS(long time){
        time=time/1000;
//        int s= (int) (time%60);
//        int m= (int) (time/60);
//        int h=(int) (time/3600);
//        return String.format("%02d:%02d:%02d",h,m,s);

        int interval=60;
        int secondTime = 0;
        int minuteTime = 0;
        int hourTime = 0;
        if(time >= interval) {
            minuteTime = (int)(time / 60);
            secondTime = (int)(time % 60);
            if(minuteTime >= interval) {
                hourTime = minuteTime / 60;
                minuteTime = minuteTime % 60;
            }
        }else {
            secondTime= (int) time;
        }
        return String.format("%02d:%02d:%02d",hourTime,minuteTime,secondTime);
    }

}
