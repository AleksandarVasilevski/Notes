package com.aleksandarvasilevski.notes;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    private ImageView rateApp;
    private TextView webSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        rateApp = (ImageView) findViewById(R.id.rate_app);
        webSite = (TextView)findViewById(R.id.web_site);

        rateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openMarketIntent = new Intent(Intent.ACTION_VIEW);
                openMarketIntent.setData(Uri.parse("market://details?id=com.aleksandarvasilevski.notes"));
                startActivity(openMarketIntent);
            }
        });

        webSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openWebIntent = new Intent(Intent.ACTION_VIEW);
                openWebIntent.setData(Uri.parse("http://www.aleksandarvasilevski.com"));
                startActivity(openWebIntent);
            }
        });
    }
}
