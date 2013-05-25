package com.nhshackday.trustyprotocolsandroid;

import java.io.InputStream;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.app.ListActivity;

public class HospitalSelectionActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hospital_selection);
        setListAdapter(new HospitalAdapter(getResources().openRawResource(R.raw.hospitals)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hospital_selection, menu);
		return true;
	}

}
