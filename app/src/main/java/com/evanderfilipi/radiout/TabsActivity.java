package com.evanderfilipi.radiout;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TabHost;

public class TabsActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        intent = new Intent().setClass(this, MainActivity.class);
        spec = tabHost.newTabSpec("Radio").setIndicator("", getResources().getDrawable(R.drawable.logo_radio)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, TwitterActivity.class);
        spec=tabHost.newTabSpec("Twitter").setIndicator("", getResources().getDrawable(R.drawable.logo_twitter)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, InstagramActivity.class);
        spec=tabHost.newTabSpec("Instagram").setIndicator("", getResources().getDrawable(R.drawable.logo_ig)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, FacebookActivity.class);
        spec=tabHost.newTabSpec("Facebook").setIndicator("", getResources().getDrawable(R.drawable.logo_facebook)).setContent(intent);
        tabHost.addTab(spec);

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }
}
