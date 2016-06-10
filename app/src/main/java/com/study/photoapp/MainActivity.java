package com.study.photoapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class MainActivity extends AppCompatActivity {
    String TAG = this.getClass().getName();
    static final int PICK_REQUEST = 100;
    ImageView img;
    InputStream is;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(R.id.img);
    }

    //사진앱을 띄우자!! 단 선택한 이미지 정보를 가져와야 한다...
    public void pick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_REQUEST);
    }

    //사진 선택시, 그 결과를 가져올 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_REQUEST && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "사진을 선택해서 가져옴");

            //인텐트에서 사진정보 추출
            Uri uri = data.getData();
            img.setImageURI(uri);

            //파일명을 얻어오자!!
            Log.d(TAG, "uri.getPath() = "+uri.getPath());
            Log.d(TAG, "uri.toString() = "+uri.toString());

            //이미지 실제 경로 얻기
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            Log.d(TAG, filePath);

            file = new File(filePath);

            try {
                is=this.getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    public void upload(View view) {
        MyAsync myAsync = new MyAsync(is);
        myAsync.execute("http://192.168.43.30:9090/rest/gallery"
                , "제목 테스트입니다", file.getName());
    }
}









