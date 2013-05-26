package com.nhshackday.trustyprotocolsandroid;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;

public class ContentActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");

        setTitle(title);

        WebView webview = new WebView(this);
        webview.loadData(content, "text/html", null);
        setContentView(webview);
	}
}
