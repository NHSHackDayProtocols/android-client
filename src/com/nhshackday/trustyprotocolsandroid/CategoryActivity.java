package com.nhshackday.trustyprotocolsandroid;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

public class CategoryActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hospital_selection);
		new DownloadGuidlineTask().execute();
	}

	private class DownloadGuidlineTask extends AsyncTask<Void, Void, Boolean> {
		protected Boolean doInBackground(Void... v) {
			if (new File(getFilesDir(), "guideline.json").exists()) {
				return true;
			}

			int read = 0;
			URL url = null;
			try {
				url = new URL(
						"http://176.9.18.121:4000/static_test/infection.json");
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return false;
			}
			HttpURLConnection urlConnection = null;

			try {
				urlConnection = (HttpURLConnection) url.openConnection();
				InputStream in = new BufferedInputStream(
						urlConnection.getInputStream());
				FileOutputStream fos = openFileOutput("guideline.json",
						Context.MODE_PRIVATE);
				byte[] bytes = new byte[4096];
				while ((read = in.read(bytes)) != -1) {
					fos.write(bytes, 0, read);
				}
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} finally {
				urlConnection.disconnect();
			}
			return (read > 0);
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			CategoryActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (success) {
						setListAdapter(new CategoryAdapter(CategoryActivity.this));
					} else {
						Toast.makeText(CategoryActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
					}
				}
			});
		}
	}

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    Intent intent = new Intent(this, CategoryActivity.class);
    startActivity(intent);
    super.onListItemClick(l, v, position, id);
  }
}
