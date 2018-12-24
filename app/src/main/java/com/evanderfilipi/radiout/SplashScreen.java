package com.evanderfilipi.radiout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SplashScreen extends Activity {

    ImageView splash;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
    Thread splashTread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        StartAnimations();
    }
    private void StartAnimations() {
        Animation anims = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anims.reset();
        RelativeLayout l=(RelativeLayout) findViewById(R.id.rlsplash);
        l.clearAnimation();
        l.startAnimation(anims);
        splash = (ImageView)findViewById(R.id.splashScreen);
        Animation anim = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.translate);
        anim.reset();
        splash.startAnimation(anim);

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 2000) {
                        sleep(100);
                        waited += 100;
                    }
                    Intent intent = new Intent(SplashScreen.this,
                            TabsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    SplashScreen.this.finish();
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    SplashScreen.this.finish();
                }

            }
        };
        splashTread.start();

    }
}
