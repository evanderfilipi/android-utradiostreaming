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

public class TwitterActivity extends Activity {
    WebView tw;
    ProgressBar progressBar;
    SwipeRefreshLayout mySRL;
    CountDownTimer timerweb;
    TextView notifTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter);
        tw = (WebView)findViewById(R.id.webview1);
        notifTxt = (TextView)findViewById(R.id.notifweb1);
        notifTxt.setVisibility(View.GONE);
        progressBar = (ProgressBar)findViewById(R.id.progressBar1);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF0000FF, android.graphics.PorterDuff.Mode.MULTIPLY);
        mySRL = (SwipeRefreshLayout)findViewById(R.id.swipeContainer);
        mySRL.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        timerweb = new CountDownTimer(6000, 6000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                tw.setVisibility(View.VISIBLE);
            }
        };
        tw.getSettings().setJavaScriptEnabled(true);
        tw.loadUrl("https://twitter.com/UnivTerbuka");
        tw.setWebViewClient(new WebViewClient() {
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
                tw.setVisibility(View.GONE);
                notifTxt.setVisibility(View.VISIBLE);
            }
        });

        mySRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                timerweb.start();
                notifTxt.setVisibility(View.GONE);
                tw.reload();
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && tw.canGoBack()) {
            tw.goBack();
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
