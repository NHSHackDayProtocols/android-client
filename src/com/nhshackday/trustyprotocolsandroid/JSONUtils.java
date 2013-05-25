package com.nhshackday.trustyprotocolsandroid;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class JSONUtils {
	public static String convertStreamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = new BufferedInputStream(inputStream);
        byte[] buffer = new byte[4096]; 
        try {
            while (in.read(buffer) != -1) {
                out.write(buffer);
            }
        } finally {
            out.close();
            in.close();
        }
        return out.toString("UTF-8");
    }
}
