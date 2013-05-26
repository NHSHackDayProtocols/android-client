package com.nhshackday.trustyprotocolsandroid;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONException;

import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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
        setTitle("Hospitals");
	}

	@Override
    protected void onResume () {
		new DownloadHospitalsTask().execute();
        super.onResume();
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

        private void downloadAndCompare() {
			int read = 0, content = 0;
			URL url = null;
			try {
				url = new URL("http://corbett.li:4000/services/hospitalList");
			} catch (MalformedURLException e) {
				e.printStackTrace();
                Log.d("TAG", "list error()");
				return;
			}

			HttpURLConnection urlConnection = null;

            String hospitalJSON = "",
                   newHospitalsJSON = "";

			try {
				urlConnection = (HttpURLConnection) url.openConnection();
	            hospitalJSON = JSONUtils.convertStreamToString(HospitalSelectionActivity.this.openFileInput("hospitals.json"));

				BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
				String line = reader.readLine();
				newHospitalsJSON = line;
				while((line=reader.readLine())!=null){
					newHospitalsJSON+=line;
				}

	            Log.d("TAG2", "" + newHospitalsJSON.length());
	            Log.d("TAG", "double opening ()");
			} catch (IOException e) {
				e.printStackTrace();
                Log.d("TAG", "download error()");
				return;
			} finally {
				urlConnection.disconnect();
			}

            Log.d("TAG", "should now compare");
            try {
                boolean shouldUpdate = false;
                JSONArray oldHospitals = new JSONArray(hospitalJSON),
                          newHospitals = new JSONArray(newHospitalsJSON),
                          updatedHospitals = new JSONArray();

                ArrayList<String> listOfChangedGuidelines = new ArrayList<String>();
                String updateHospitals = "";

                for (int i = 0; i < oldHospitals.length(); i++) {
                    JSONObject updatedHospital = new JSONObject();
                    JSONObject oldHospital = oldHospitals.getJSONObject(i);
                    JSONObject newHospital = newHospitals.getJSONObject(i);
                    if (oldHospital.getString("name").equals(newHospital.getString("name")) &&
                            oldHospital.getLong("dateModified") < newHospital.getLong("dateModified") ) {
                        shouldUpdate = true;
                    }
                    listOfChangedGuidelines.add(newHospital.getString("name"));
                    updateHospitals += newHospital.getString("name") + ", ";
                    updatedHospital.put("name", newHospital.getString("name"));
                    updatedHospital.put("dateModified", newHospital.getLong("dateModified"));
                    updatedHospital.put("lat", newHospital.getLong("lat"));
                    updatedHospital.put("lng", newHospital.getLong("lng"));
                    updatedHospitals.put(updatedHospital);
                }

                if (shouldUpdate) {
                	NotificationCompat.Builder mBuilder =
                	        new NotificationCompat.Builder(HospitalSelectionActivity.this)
                	        .setSmallIcon(android.R.drawable.stat_notify_sync)
                	        .setContentTitle("Guidelines updated")
                	        .setContentText("Guidelines for " + updateHospitals + " updated");
                	// Creates an explicit intent for an Activity in your app
                	Intent resultIntent = new Intent(HospitalSelectionActivity.this, HospitalSelectionActivity.class);

                	// The stack builder object will contain an artificial back stack for the
                	// started Activity.
                	// This ensures that navigating backward from the Activity leads out of
                	// your application to the Home screen.
                	TaskStackBuilder stackBuilder = TaskStackBuilder.create(HospitalSelectionActivity.this);
                	// Adds the back stack for the Intent (but not the Intent itself)
                	stackBuilder.addParentStack(HospitalSelectionActivity.class);
                	// Adds the Intent that starts the Activity to the top of the stack
                	stackBuilder.addNextIntent(resultIntent);
                	PendingIntent resultPendingIntent =
                	        stackBuilder.getPendingIntent(
                	            0,
                	            PendingIntent.FLAG_UPDATE_CURRENT
                	        );
                	mBuilder.setContentIntent(resultPendingIntent);

                    mBuilder.setAutoCancel(true);

                	NotificationManager mNotificationManager =
                	    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                	// mId allows you to update the notification later on.
                	mNotificationManager.notify(0xdeadbeef, mBuilder.build());

                    Context c = (Context)HospitalSelectionActivity.this;
                
                    File base = c.getFilesDir();
                    for (String hostpitalName : listOfChangedGuidelines) {
                        File guidelineFile = new File(base, hostpitalName.replace(" ", "%20"));
                        if (guidelineFile.exists() && !guidelineFile.delete()){
                            HospitalSelectionActivity.this.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(HospitalSelectionActivity.this, "Error while deleting old guideline", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }

                    File hospitalsFile = new File(base, "hospitals.json");
                    if(!hospitalsFile.delete()) {
                        Log.d("TAG", "error while deleting");
                    }

                    try {
						if(!hospitalsFile.createNewFile()) {
						    Log.d("TAG", "error while creating");
						}
					} catch (IOException e) {
                        Log.d("TAG", "error while creating io");
						e.printStackTrace();
					}

                    BufferedWriter writer;
					try {
						writer = new BufferedWriter(new FileWriter(hospitalsFile));
	            		writer.write(updatedHospitals.toString());
Log.d("T", "saving" + updatedHospitals.toString());
                        writer.close();

					} catch (IOException e) {
                        Log.d("TAG", "error while writer");
						e.printStackTrace();
					}
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

		protected Boolean doInBackground(Void... v) {
			if (new File(getFilesDir(), "hospitals.json").exists()) {
                HospitalSelectionActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HospitalSelectionActivity.this.adapter = new HospitalAdapter(HospitalSelectionActivity.this);
                        setListAdapter(HospitalSelectionActivity.this.adapter);
                    }
                });

                Log.d("TAG", "downloadAndCompare()");
                downloadAndCompare();

				return true;
			}

			int read = 0, content = 0;
			URL url = null;
			try {
				url = new URL("http://corbett.li:4000/services/hospitalList");
			} catch (MalformedURLException e) {
				e.printStackTrace();

                HospitalSelectionActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(HospitalSelectionActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
                    }
                });

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

                HospitalSelectionActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(HospitalSelectionActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
                    }
                });

				return false;
			} finally {
				urlConnection.disconnect();
			}

			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			HospitalSelectionActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (success) {
		                HospitalSelectionActivity.this.adapter = new HospitalAdapter(HospitalSelectionActivity.this);
						setListAdapter(new HospitalAdapter(HospitalSelectionActivity.this));
					}
				}
			});
		}
	}

}
