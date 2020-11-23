package splashapp.android.nttd.cas.com.new_splash_app;

import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by Church on 2018/8/2.
 *
 * @author Church
 */
public class MainMediaFragment extends Fragment implements TextureView.SurfaceTextureListener {

    private TextureView mTextureView;
    private MediaPlayer mMediaPlayer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mTextureView = new TextureView(getContext());
        return mTextureView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextureView.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(final SurfaceTexture surface, int width, int height) {
        try {
            AssetFileDescriptor openFd = getContext().getAssets().openFd("welcome.mp4");
            FileDescriptor fileDescriptor = openFd.getFileDescriptor();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(fileDescriptor,openFd.getStartOffset(),openFd.getLength());
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(mp -> {
                Surface mySurface = new Surface(surface);
                mp.setSurface(mySurface);
                mp.setLooping(true);
                mp.start();
            });

        } catch (IOException e) {
            Toast.makeText(getContext(),"Media file playback failed", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * media size changed
     * @param surface
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    /**
     * media destroyed
     * @param surface
     * @return
     */
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    /**
     * media update
     * @param surface
     */
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMediaPlayer!=null){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
