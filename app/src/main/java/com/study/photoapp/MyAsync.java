package com.study.photoapp;

import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MyAsync extends AsyncTask<String, Void , String>{
    URL url;
    HttpURLConnection con;
    String charset="utf-8";
    File binaryFile;
    String boundary = Long.toHexString(System.currentTimeMillis());
    String CRLF = "\r\n"; // Line separator required by multipart/form-data.
    InputStream is; //유저가 선택한 겔러리 이미지에 대한 스트림

    public MyAsync(InputStream is) {
        this.is =is;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            url= new URL(params[0]);
            con=(HttpURLConnection)url.openConnection();
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
            writer.append(CRLF).append(params[1]).append(CRLF).flush();

// Send binary file.

            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"myFile\"; filename=\"" + params[2] + "\"").append(CRLF);
            writer.append("Content-Type: " + HttpURLConnection.guessContentTypeFromName(params[2])).append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();

            //얻어온 입력스트림의 데이터를 output 스트림에 편승시키자!!
            byte[] buff = new byte[1024*4];

            int read=-1;
            while(true){
                read=is.read(buff); //배열을 사용하여 한꺼번에 읽어들이는 경우 반환되는
                                            //데이터는 읽어들인 데이터가 아니다!!
                if(read==-1)break;
                output.write(buff, 0, read);
            }
            is.close();
            output.flush(); // Important before continuing with writer!
            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.


            // End of multipart/form-data.
            writer.append("--" + boundary + "--").append(CRLF).flush();
            writer.close();

            int code=0;
            code=con.getResponseCode();
            //System.out.println(code);

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
