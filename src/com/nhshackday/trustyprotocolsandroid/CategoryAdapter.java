package com.nhshackday.trustyprotocolsandroid;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONException;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.content.Context;

public class CategoryAdapter extends BaseAdapter {
	public static String TAG = "HospitalTag";
	public ArrayList<String> categories;
	private Context context;

	public CategoryAdapter(Context context) {
		this.context = context;
		loadGuidelines();
	}

	private void loadGuidelines() {
		if (!new File(context.getFilesDir(), "guideline.json").exists()) {
			Log.d(TAG, "downloading now");
			URL url;
			try {
				url = new URL(
						"http://176.9.18.121:4000/static_test/infection.json");
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return;
			}
			HttpURLConnection urlConnection = null;
			
			try {
				urlConnection = (HttpURLConnection) url.openConnection();
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				FileOutputStream fos = context.openFileOutput("guideline.json", Context.MODE_PRIVATE);
				int read = 0;
				byte[] bytes = new byte[4096];
				while ((read = in.read(bytes)) != -1) {
					fos.write(bytes, 0, read);
				}
				fos.close();
				Log.d(TAG, "downloading now");


			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				urlConnection.disconnect();
			}
		}

		String guidelineJSON = "";
		try {
            guidelineJSON = JSONUtils.convertStreamToString(context.openFileInput("guideline.json"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(guidelineJSON);
			categories = new ArrayList<String>();
			for (int i = 0; i < jsonArray.length(); i++) {
				categories.add(jsonArray.getJSONObject(i).getString("title"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getCount() {
		return categories.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater
					.inflate(R.layout.hospital_row, parent, false);
		}
		TextView textView = (TextView) convertView.findViewById(R.id.name);
		textView.setText(categories.get(index));
		return convertView;
	}
}
