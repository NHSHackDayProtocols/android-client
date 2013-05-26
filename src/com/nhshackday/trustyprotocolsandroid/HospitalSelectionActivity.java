package com.nhshackday.trustyprotocolsandroid;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.app.ListActivity;
import android.content.Context;
import android.widget.Toast;
import android.view.View;
import android.widget.ListView;
import android.content.Intent;

public class HospitalSelectionActivity extends ListActivity {

    HospitalAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hospital_selection);
		new DownloadHospitalsTask().execute();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.hospital_selection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refresh() {
        Log.d("HAS", "refresh");
        for(File f :  getFilesDir().listFiles() ) {
            Log.d("HAS", f.toString());
            f.delete();
        }
        finish();
    }
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, CategoryActivity.class);
		intent.putExtra("hospital_name", adapter.getHospitalNameByPosition(position));
		startActivity(intent);
		super.onListItemClick(l, v, position, id);
	}

	private class DownloadHospitalsTask extends AsyncTask<Void, Void, Boolean> {
		protected Boolean doInBackground(Void... v) {
			if (new File(getFilesDir(), "hospitals.json").exists()) {
				return true;
			}

			int read = 0, content = 0;
			URL url = null;
			try {
				url = new URL("http://corbett.li:4000/services/hospitalList");
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return false;
			}
			HttpURLConnection urlConnection = null;

			try {
				urlConnection = (HttpURLConnection) url.openConnection();
				InputStream in = new BufferedInputStream(
						urlConnection.getInputStream());
				FileOutputStream fos = openFileOutput("hospitals.json",
						Context.MODE_PRIVATE);
				byte[] bytes = new byte[4096];
				while ((read = in.read(bytes)) != -1) {
                    content += read;
					fos.write(bytes, 0, read);
				}
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} finally {
				urlConnection.disconnect();
			}
			return (content > 0);
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			HospitalSelectionActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (success) {
		                HospitalSelectionActivity.this.adapter = new HospitalAdapter(HospitalSelectionActivity.this);
						setListAdapter(new HospitalAdapter(HospitalSelectionActivity.this));
					} else {
						Toast.makeText(HospitalSelectionActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
					}
				}
			});
		}
	}
}
