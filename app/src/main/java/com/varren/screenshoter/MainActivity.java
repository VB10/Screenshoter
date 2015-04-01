package com.varren.screenshoter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class MainActivity extends ActionBarActivity {
    private static final Bitmap.CompressFormat COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    private static final int COMPRESSION_QUALITY = 50; // from 0 to 100 works only for JPG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onTestButtonClick(View v) {
        if (v.getVisibility() == View.VISIBLE) {
            v.setVisibility(View.INVISIBLE);
        } else {
            v.setVisibility(View.VISIBLE);
        }
    }

    public void onResetButtonClick(View v) {
        ViewGroup root = (ViewGroup) findViewById(R.id.rootView);
        for (int i = 0; i < root.getChildCount(); i++) {
            root.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }

    public void onShareClick(View v) {

        makeScreenShot(findViewById(R.id.rootView));

        Uri uri = Uri.parse("content://" +
                getResources().getString(R.string.app_package) + "/" +
                // TODO REED INFO MSG 1
                // "System.currentTimeMillis() +" will be useful for google+
                // because google+ caches previous screenshot with the same path.
                // you have to give him new name each time to refresh preview
                System.currentTimeMillis() +
                ScreenshotContentProvider.FILE_NAME);

        Log.e("MA.onShareClick", "Sharing: " + uri.toString());
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Some text here");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType(ScreenshotContentProvider.MIME_TYPE);

        startActivity(Intent.createChooser(shareIntent, "Share Screen"));
    }

    public void makeScreenShot(View root) {
        //i have different code here in my project
        //it is just a test case

        Bitmap screenshot = Bitmap.createBitmap(root.getWidth(), root.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(screenshot);
        root.draw(canvas);

        OutputStream fout = null;
        try {

            File imageFile = ScreenshotContentProvider.getFile(this);
            Log.e("MA.makeScreenShot", "Saving File to: " + imageFile.toString());

            fout = new FileOutputStream(imageFile);
            screenshot.compress(COMPRESS_FORMAT, COMPRESSION_QUALITY, fout);

            fout.flush();
            fout.close();

        } catch (Exception e) {
            Log.e("MainActivity", "Exception in makeScreenShot");
            e.printStackTrace();
        }

        screenshot.recycle();

    }
}
