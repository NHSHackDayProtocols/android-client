package com.nhshackday.trustyprotocolsandroid;

import java.io.InputStream;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.app.ListActivity;
import android.content.Intent;

public class HospitalSelectionActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hospital_selection);
        setListAdapter(new HospitalAdapter(getResources().openRawResource(R.raw.hospitals)));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, CategoryActivity.class);
		startActivity(intent);
		super.onListItemClick(l, v, position, id);
	}
}
