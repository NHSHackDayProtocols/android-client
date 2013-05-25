package com.nhshackday.trustyprotocolsandroid;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.app.ListActivity;
import android.widget.ArrayAdapter;

public class HospitalSelectionActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hospital_selection);

        ArrayAdapter adapter = new ArrayAdapter(this,
                R.layout.hospital_row,
                R.id.text1,
                new String[]{"Hospital1", "Hostpital2"});
        setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hospital_selection, menu);
		return true;
	}

}
