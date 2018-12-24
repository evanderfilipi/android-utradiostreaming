package com.evanderfilipi.radiout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class InstagramActivity extends Activity {
    WebView ig;
    ProgressBar progressBar;
    SwipeRefreshLayout mySRL;
    TextView notifTxt;
    CountDownTimer timerweb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram);
        ig = (WebView)findViewById(R.id.webview2);
        notifTxt = (TextView)findViewById(R.id.notifweb2);
        notifTxt.setVisibility(View.GONE);
        progressBar = (ProgressBar)findViewById(R.id.progressBar2);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF0000FF, android.graphics.PorterDuff.Mode.MULTIPLY);
        mySRL = (SwipeRefreshLayout)findViewById(R.id.swipeContainer2);
        mySRL.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        timerweb = new CountDownTimer(6000, 6000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                ig.setVisibility(View.VISIBLE);
            }
        };
        ig.getSettings().setJavaScriptEnabled(true);
        ig.loadUrl("https://www.instagram.com/UnivTerbuka/");
        ig.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                progressBar.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                mySRL.setRefreshing(false);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                timerweb.cancel();
                ig.setVisibility(View.GONE);
                notifTxt.setVisibility(View.VISIBLE);
            }
        });

        mySRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                timerweb.start();
                notifTxt.setVisibility(View.GONE);
                ig.reload();
            }
        });
    }
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && ig.canGoBack()) {
            ig.goBack();
            return true;
        } else {
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
        return super.onKeyDown(keyCode, event);
    }
}
