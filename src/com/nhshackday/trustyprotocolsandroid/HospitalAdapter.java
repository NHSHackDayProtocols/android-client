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

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.content.Context;

    import java.util.Comparator;

public class HospitalAdapter extends BaseAdapter {
    public static String TAG = "HospitalTag";
    public ArrayList<Hospital> hospitalNames;
    private Context context;

     
    public class HospitalComparable implements Comparator<Hospital>{
            @Override
            public int compare(Hospital o1, Hospital o2) {
                return o1.name.compareTo(o2.name);
            }
    }

    class Hospital {
        String name;
        boolean updated;
        public Hospital(String name, boolean updated) {
            this.name = name;
            this.updated = updated;
        }
    }

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
			hospitalNames = new ArrayList<Hospital>();
			for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject o = jsonArray.getJSONObject(i);
				hospitalNames.add(new Hospital(o.getString("name"), (o.has("updated") && o.getBoolean("updated"))));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        Collections.sort(hospitalNames, new HospitalAdapter.HospitalComparable());
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
        Hospital h  = hospitalNames.get(index);
        if (h.updated) {
            textView.setText(h.name + " (updated)");
            textView.setTextColor(Color.RED);
        } else {
            textView.setText(h.name);
        }

        //if (h.updated) {
        //    Log.d("TAG", " here " + h.name + " updated " + h.updated);
        //    textView.setTextAppearance(context, 1);
        //} else {
        //    textView.setTextAppearance(context, 0);
        //}

        return convertView;
	}

    public String getHospitalNameByPosition(int position) {
        return this.hospitalNames.get(position).name;
    }
}
