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
import android.content.Intent;

public class CategoryAdapter extends BaseAdapter {
    public class ContentObj {
        String name;
        String idTitle;
        String type;
        String content;
        public ContentObj(String name, String idTitle, String type, String content) {
            this.name=name;
            this.idTitle=idTitle;
            this.type=type;
            this.content=content;
        }
    }

	public static String TAG = "HospitalTag";

	public ArrayList<ContentObj> categories;

	private Context context;
	private String path;

	public CategoryAdapter(Context context, String url_hospital_name, String path) {
		this.path = path;
        Log.d(TAG, path);
		this.context = context;
		loadGuidelines(url_hospital_name);
	}

	private void loadGuidelines(String url_hospital_name) {
		String guidelineJSON = "";
        InputStream is = null;
        Log.d(TAG, url_hospital_name);
		try {
            is = context.openFileInput(url_hospital_name);
		} catch (IOException e) {
			e.printStackTrace();
            return;
		}

		try {
            guidelineJSON = JSONUtils.convertStreamToString(is);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
		    JSONArray currentArray = new JSONArray(guidelineJSON);

            Log.d(TAG, "path " + path);
            for(String id : path.split("/+")) {
                if (path == "/") continue;

                Log.d(TAG, "id / " + id);

                for (int i = 0; i < currentArray.length(); i++) {
                    JSONObject o = currentArray.getJSONObject(i);
                    Log.d(TAG, "check (" + o.getString("idTitle") + "|" + id + ")");
                    if (o.getString("idTitle").equals(id)) {
                        currentArray = o.getJSONArray("children");
                        Log.d(TAG, "assigned" + o.getString("idTitle") + "/" + id);
                        break;
                    }
                }
                Log.d(TAG, "found id " + id + " with l " + currentArray.length());
                Log.d(TAG, "found toString " + currentArray.toString());
            }
            Log.d(TAG, "success");


            categories = new ArrayList<ContentObj>();
            for (int i = 0; i < currentArray.length(); i++) {
                JSONObject o = currentArray.getJSONObject(i);

                ContentObj c = new ContentObj(o.getString("title"),
                        o.getString("idTitle"),
                        o.getString("type"),
                        o.getString("type").equals("information") ? o.getString("content") : "");
                categories.add(c);
            }
		} catch (JSONException e) {
            Log.d(TAG, "failure");
			e.printStackTrace();
		}
	}

	@Override
	public int getCount() {
		return categories.size();
	}

	@Override
	public Object getItem(int position) {
		return (Object)categories.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getPathForPosition(int position) {
        return path + (path != "/" ? "/" : "") + categories.get(position).idTitle;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater
					.inflate(R.layout.hospital_row, parent, false);
		}
		TextView textView = (TextView) convertView.findViewById(R.id.name);
		textView.setText(categories.get(index).name);
		return convertView;
	}
}
