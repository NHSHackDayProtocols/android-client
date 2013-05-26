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
import android.content.Intent;
import android.view.View;
import android.widget.ListView;

public class CategoryActivity extends ListActivity {

    String path = "/";
    String url_hospital_name = "/";
    CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_selection);
        Intent intent = getIntent();
        url_hospital_name = intent.getStringExtra("hospital_name");
        path = "/";
        if (intent.hasExtra("path")) {
            path = intent.getStringExtra("path");
        }
        if (intent.hasExtra("title")) {
            setTitle(intent.getStringExtra("title"));
        }
        new DownloadGuidlineTask().execute(url_hospital_name);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        CategoryAdapter.ContentObj o = (CategoryAdapter.ContentObj)adapter.getItem(position);
        
        if (o.type.equals("information")) {
            Intent informationIntent = new Intent(this, ContentActivity.class);
            informationIntent.putExtra("title", o.name);
            informationIntent.putExtra("content", o.content);
            startActivity(informationIntent);
            super.onListItemClick(l, v, position, id);
            return;
        }

        Intent intent = new Intent(this, CategoryActivity.class);
		intent.putExtra("hospital_name", url_hospital_name);
		intent.putExtra("title", ((CategoryAdapter.ContentObj)adapter.getItem(position)).name);
		intent.putExtra("path", adapter.getPathForPosition(position));
        startActivity(intent);
        super.onListItemClick(l, v, position, id);
    }

    private class DownloadGuidlineTask extends AsyncTask<String, Void, Boolean> {
        private String url_hospital_name;
        protected Boolean doInBackground(String... v) {
            url_hospital_name = v[0].replace(" ", "%20");

            if (new File(getFilesDir(), url_hospital_name).exists()) {
                return true;
            }

            int read = 0, content = 0;
            Log.d("ANDY", "http://176.9.18.121:4000/services/getHospitalProtocols/" + url_hospital_name);
            URL url = null;
            try {
                url = new URL(
                        "http://176.9.18.121:4000/services/getHospitalProtocols/" + url_hospital_name);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            }
            HttpURLConnection urlConnection = null;

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(
                        urlConnection.getInputStream());
                FileOutputStream fos = openFileOutput(url_hospital_name,
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
            CategoryActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (success) {
                        CategoryActivity.this.adapter = new CategoryAdapter(CategoryActivity.this,
                                DownloadGuidlineTask.this.url_hospital_name, CategoryActivity.this.path);
                        setListAdapter(CategoryActivity.this.adapter);
                    } else {
                        Toast.makeText(CategoryActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
