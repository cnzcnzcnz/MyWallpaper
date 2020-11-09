package com.abstraksi.kimetsunoyaiba.service;

import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;
import java.io.File;

import com.abstraksi.kimetsunoyaiba.data.pref.PreferencesHelper;
import com.abstraksi.kimetsunoyaiba.utils.Utils;

public class MaterialLiveWallpaperService extends WallpaperService {

    private static final String TAG = "MaterialLiveWallpaperSe";

    @Override
    public Engine onCreateEngine() {
        try {
            PreferencesHelper preferencesHelper = new PreferencesHelper(this);
            String pathWallpaperName = preferencesHelper.getString(PreferencesHelper.Companion.getWALLPAPER_PATH());
            File imageFile = new File(pathWallpaperName);
            byte[] imgByte = Utils.INSTANCE.readFileToByte(this,  imageFile);
            Movie movie = Movie.decodeByteArray(imgByte, 0, imgByte.length);
            return new GifWallpaperEngine(movie);
        } catch (Exception e) {
            Log.i(TAG, "onCreateEngine: File not found! "+e);
        }
        return null;
    }

    private class GifWallpaperEngine extends Engine {
        private final int frameDuration = 20;
        private SurfaceHolder holder;
        private Movie movie;
        private boolean visible;
        private Handler handler;

        public GifWallpaperEngine(Movie movie) {
            this.movie = movie;
            handler = new Handler();
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            this.holder = surfaceHolder;
        }

        private Runnable drawGif = new Runnable() {
            @Override
            public void run() {
                draw();
            }
        };

        private void draw() {
            if(visible){
                try {
                    Canvas canvas = holder.lockCanvas();
                    canvas.save();

                    float scaleX =  ((float)canvas.getHeight()) / ((float)movie.height());
                    float scaleY =  ((float)canvas.getWidth()) / ((float)movie.width());

                    canvas.scale(scaleY, scaleX);
                    movie.draw(canvas, 0, 0);
                    canvas.restore();
                    holder.unlockCanvasAndPost(canvas);
                    int duration = movie.duration() > 0 ? movie.duration() : 1;
                    movie.setTime((int) (System.currentTimeMillis() % duration));

                    handler.removeCallbacks(drawGif);
                    handler.postDelayed(drawGif, frameDuration);
                } catch (Exception e) {
                    Log.e(TAG, "Error : "+e);
                }
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if(visible){
                handler.post(drawGif);
            }else{
                handler.removeCallbacks(drawGif);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(drawGif);
        }
    }
}