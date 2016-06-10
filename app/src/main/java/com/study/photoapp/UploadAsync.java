package com.study.photoapp;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * Created by zino on 2016-06-10.
 */
public class UploadAsync extends AsyncTask<String, Void, String> {
    String TAG=this.getClass().getName();
    String param = "value";
    String charset = "utf-8";
    File binaryFile;
    String boundary = Long.toHexString(System.currentTimeMillis());
    String CRLF = "\r\n"; // Line separator required by multipart/form-data.
    URL url;
    HttpURLConnection con;

    String requestPath;
    String title;
    String filePath;
    InputStream is;
    Context context;

    public UploadAsync(Context context, Uri uri){
        this.context=context;

        try {
            is = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        requestPath=params[0];
        title=params[1];


        try {
            url = new URL(requestPath);
            con = (HttpURLConnection) url.openConnection();

            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Connection", "keep-alive");
            con.setRequestProperty("Cache-Control", "max-age=0");
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            OutputStream output = con.getOutputStream();

            con.connect();

            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);

            // Send normal param.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"title\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
            writer.append(CRLF).append(title).append(CRLF).flush();


            // Send binary file.

            binaryFile = new File(filePath);
            Log.d(TAG, "binaryFile path is "+binaryFile.getAbsolutePath());

            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"myFile\"; filename=\"" + binaryFile.getName() + "\"").append(CRLF);
            writer.append("Content-Type: " + HttpURLConnection.guessContentTypeFromName(binaryFile.getName())).append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();

            //FileInputStream fis = new FileInputStream(binaryFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = is.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            is.close();
            output.flush(); // Important before continuing with writer!
            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.


            // End of multipart/form-data.
            writer.append("--" + boundary + "--").append(CRLF).flush();
            writer.close();

            int code = 0;
            code = con.getResponseCode();
            System.out.println(code);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
