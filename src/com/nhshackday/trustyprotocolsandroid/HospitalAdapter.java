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
import java.util.Collections;

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


public class HospitalAdapter extends BaseAdapter {
    public static String TAG = "HospitalTag";
    public ArrayList<String> hospitalNames;
    private Context context;

    public HospitalAdapter(Context context) {
        this.context = context;
        parseHospitalJSON();
    }

    private void parseHospitalJSON() {
		String hospitalJSON = "";
		try {
            hospitalJSON = JSONUtils.convertStreamToString(context.openFileInput("hospitals.json"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONArray jsonArray = null;
		try {
            if (hospitalJSON.length() == 0) {
                return;
            }
			jsonArray = new JSONArray(hospitalJSON);
			hospitalNames = new ArrayList<String>();
			for (int i = 0; i < jsonArray.length(); i++) {
				hospitalNames.add(jsonArray.getJSONObject(i).getString("name"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        Collections.sort(hospitalNames);
    }
	
	@Override
	public int getCount() {
		return hospitalNames.size();
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
            convertView = inflater.inflate(R.layout.hospital_row, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.name);
        textView.setText(hospitalNames.get(index));
        return convertView;
	}

    public String getHospitalNameByPosition(int position) {
        return this.hospitalNames.get(position);
    }
}
