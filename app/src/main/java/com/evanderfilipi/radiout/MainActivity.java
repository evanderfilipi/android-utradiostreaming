package com.evanderfilipi.radiout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements View.OnClickListener{

    ViewPager viewPager;
    Intent serviceIntent;
    Button play, stop;
    ImageView dots;
    Animation animimg, animimg2;
    ProgressDialog pdBuff = null;
    private static boolean isStreaming = false;
    boolean mBufferBroadcastIsRegistered;
    String[] imgUrl = {"http://utradio.ut.ac.id/storage/banners/Banner%20UT%20Morning.jpg",
            "http://utradio.ut.ac.id/storage/banners/Banner%20UT%20Daytime.jpg",
            "http://utradio.ut.ac.id/storage/banners/Banner%20UT%20Afternoon.jpg",
            "http://utradio.ut.ac.id/storage/banners/Banner%20UT%20Late%20Night.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dots = (ImageView)findViewById(R.id.dotimage);
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        PagerAdapter adapter = new CustomAdapter(MainActivity.this, imgUrl);
        viewPager.setAdapter(adapter);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(), 1000,8000);
        play = (Button)findViewById(R.id.btnplay);
        stop = (Button)findViewById(R.id.btnstop);
        animimg = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
        animimg2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha2);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        dots.setBackgroundResource(R.drawable.dot_1);
                        break;
                    case 1:
                        dots.setBackgroundResource(R.drawable.dot_2);
                        break;
                    case 2:
                        dots.setBackgroundResource(R.drawable.dot_3);
                        break;
                    case 3:
                        dots.setBackgroundResource(R.drawable.dot_4);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        play.setOnClickListener(this);
        stop.setOnClickListener(this);
        serviceIntent = new Intent(this, StreamService.class);
        isStreaming = Utils.getDataBooleanFromSP(this, Utils.IS_STREAM);
        if (isStreaming) {
            play.setVisibility(View.INVISIBLE);
            stop.setVisibility(View.VISIBLE);
        } else {
            play.setVisibility(View.VISIBLE);
            stop.setVisibility(View.INVISIBLE);
        }
    }
    public class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(viewPager.getCurrentItem() == 0){
                        viewPager.setCurrentItem(1);
                        dots.setBackgroundResource(R.drawable.dot_2);
                    } else if (viewPager.getCurrentItem() == 1){
                        viewPager.setCurrentItem(2);
                        dots.setBackgroundResource(R.drawable.dot_3);
                    } else if (viewPager.getCurrentItem() == 2) {
                        viewPager.setCurrentItem(3);
                        dots.setBackgroundResource(R.drawable.dot_4);
                    } else {
                        viewPager.setCurrentItem(0);
                        dots.setBackgroundResource(R.drawable.dot_1);
                    }
                }
            });
        }
    }

    public void onClick (View view) {
        if (view == play) {
            Log.d("playStatus", "" + isStreaming);
            startStreaming();
            play.setVisibility(View.INVISIBLE);
            stop.setVisibility(View.VISIBLE);
            Utils.setDataBooleanToSP(this, Utils.IS_STREAM, true);
        }else if (view == stop) {
            Toast.makeText(this, "Radio Streaming: Off", Toast.LENGTH_SHORT).show();
            stopStreaming();
            play.setVisibility(View.VISIBLE);
            stop.setVisibility(View.INVISIBLE);
            isStreaming = false;
            Utils.setDataBooleanToSP(this, Utils.IS_STREAM, false);
        }
    }

    protected void onPause() {
        super.onPause();
        if (mBufferBroadcastIsRegistered) {
            unregisterReceiver(broadcastBufferReceiver);
            mBufferBroadcastIsRegistered = false;
        }

    }

    protected void onResume() {
        super.onResume();
        if (!mBufferBroadcastIsRegistered) {
            registerReceiver(broadcastBufferReceiver, new IntentFilter(
                    StreamService.BROADCAST_BUFFER));
            mBufferBroadcastIsRegistered = true;
        }
    }

    private void startStreaming() {
        stopStreaming();
        try {
            startService(serviceIntent);
        } catch (Exception e) {
        }

    }

    private void stopStreaming() {
        try {
            stopService(serviceIntent);
        } catch (Exception e) {

        }
    }

    private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            showProgressDialog(bufferIntent);
        }
    };

    private void showProgressDialog(Intent bufferIntent) {
        String bufferValue = bufferIntent.getStringExtra("buffering");
        int bufferIntValue = Integer.parseInt(bufferValue);
        switch (bufferIntValue) {
            case 0:
                if (pdBuff != null) {
                    pdBuff.dismiss();
                }
                Toast.makeText(this, "Radio Streaming: On", Toast.LENGTH_SHORT).show();
                break;

            case 1:
                pdBuff = ProgressDialog.show(MainActivity.this, "",
                        "Memulai Streaming...", true);
                break;
            case 2:
                CountDownTimer bufftimer = new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        pdBuff.dismiss();
                        play.setVisibility(View.VISIBLE);
                        stop.setVisibility(View.INVISIBLE);
                        Utils.setDataBooleanToSP(MainActivity.this, Utils.IS_STREAM, false);
                    }
                };
                bufftimer.start();
                break;
        }
    }
    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setMessage("Anda yakin ingin keluar?")
                .setCancelable(false)
                .setPositiveButton("Iya", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
    }
}
