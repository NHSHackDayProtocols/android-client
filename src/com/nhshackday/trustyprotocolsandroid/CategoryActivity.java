package com.nhshackday.trustyprotocolsandroid;

import java.io.InputStream;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.app.ListActivity;


public class CategoryActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hospital_selection);
        setListAdapter(new CategoryAdapter(getResources().openRawResource(R.raw.hospitals)));
	}
}
