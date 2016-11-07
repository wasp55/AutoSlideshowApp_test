package jp.techacademy.nanami.yeates.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.Timer;
import java.lang.String;

import java.lang.Runnable;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<Long> array_image = new ArrayList<>();

    ImageView imageView;

    Button play_btn;
    Button prev_btn;
    Button next_btn;

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    int mPosition = 0;
    boolean mSlideshow = false;

    class MainTimerTask extends TimerTask {
        @Override
        public void run() {
            if (mSlideshow) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        movePosition(1);
                        Log.d("javatest", String.valueOf("handler"));
                    }
                });
            }
        }
    }

    Timer mTimer = new Timer();
    TimerTask mTimerTask = new MainTimerTask();
    Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);

        play_btn = (Button) findViewById(R.id.play_btn);
        play_btn.setOnClickListener(this);


        prev_btn = (Button) findViewById(R.id.prev_btn);
        prev_btn.setOnClickListener(this);

        next_btn = (Button) findViewById(R.id.next_btn);
        next_btn.setOnClickListener(this);



        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo() {

        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (cursor.moveToFirst()) {
            do {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long id = cursor.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                array_image.add(id);

                for (int i = 0; i < array_image.size(); i++) {
                    imageView.setImageURI(ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, array_image.get(mPosition)));
                }


            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void movePosition(int move) {
        mPosition += move;
        if (mPosition >= array_image.size()) {
            mPosition = 0;
        } else if (mPosition < 0) {
            mPosition = array_image.size() - 1;
        }
        imageView.setImageURI(ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, array_image.get(mPosition)));
    }

    @Override
    public void onClick(View v) {
        mTimer = new Timer();
        mTimerTask = new MainTimerTask();

        mTimer.schedule(mTimerTask,100, 100);



    }

}



