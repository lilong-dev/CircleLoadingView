package com.ll.circleloadingview;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;

import com.larswerkman.lobsterpicker.LobsterPicker;
import com.larswerkman.lobsterpicker.OnColorListener;
import com.larswerkman.lobsterpicker.sliders.LobsterShadeSlider;

public class MainActivity extends AppCompatActivity {
    private CircleLoadingView mLoadingView;
    private int progress;
    private HandlerThread thread;
    private Handler mHandler;

    private LobsterShadeSlider borderColorPicker;
    private LobsterShadeSlider viewBgColorPicker;
    private LobsterShadeSlider progressBgColorPicker;

    private SeekBar mSeekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.init();



        thread  = new HandlerThread("handler_thread");
        thread.start();
        mHandler = new Handler(thread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(progress == 100){
                    mHandler.removeMessages(0);
                    return;
                }
                progress++;
                mLoadingView.setPercent(progress);
                mHandler.sendEmptyMessageDelayed(0,1000);
            }
        };
       mHandler.sendEmptyMessage(0);
    }

    private void init() {
        mLoadingView = (CircleLoadingView) findViewById(R.id.circleLoadingView);
        borderColorPicker = (LobsterShadeSlider) findViewById(R.id.border_color_pick);
        viewBgColorPicker = (LobsterShadeSlider) findViewById(R.id.view_bg_color_pick);
        progressBgColorPicker = (LobsterShadeSlider) findViewById(R.id.progress_bg_color_pick);
        mSeekBar = (SeekBar) findViewById(R.id.border_size_pick);

        borderColorPicker.addOnColorListener(new OnColorListener() {
            @Override
            public void onColorChanged(@ColorInt int color) {
                mLoadingView.setBorderColor(color);
            }

            @Override
            public void onColorSelected(@ColorInt int color) {

            }
        });
        viewBgColorPicker.addOnColorListener(new OnColorListener() {
            @Override
            public void onColorChanged(@ColorInt int color) {
                mLoadingView.setViewBgColor(color);
            }

            @Override
            public void onColorSelected(@ColorInt int color) {

            }
        });
        progressBgColorPicker.addOnColorListener(new OnColorListener() {
            @Override
            public void onColorChanged(@ColorInt int color) {
                mLoadingView.setProgressBgColor(color);
            }

            @Override
            public void onColorSelected(@ColorInt int color) {

            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
               mLoadingView.setBorderWidth(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(0);
    }

}
