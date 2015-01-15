package com.example.cam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


public class MainActivity extends Activity {
    private String firstPhotoPath;
    private String secondPhotoPath;
    private ImageView myImage1;
    private ImageView myImage2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        firstPhotoPath = intent.getStringExtra(CamTestActivity.FIRST_PHOTO_PATH);
        secondPhotoPath = intent.getStringExtra(CamTestActivity.SECOND_PHOTO_PATH);

        setContentView(R.layout.activity_main);

        BitmapFactory.Options options;
        options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap1 = BitmapFactory.decodeFile(firstPhotoPath,options);
        myImage1 = (ImageView) findViewById(R.id.image_1);
        myImage1.setImageBitmap(bitmap1);

        Bitmap bitmap2 = BitmapFactory.decodeFile(secondPhotoPath,options);
        myImage2 = (ImageView) findViewById(R.id.image_2);
        myImage2.setImageBitmap(bitmap2);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
