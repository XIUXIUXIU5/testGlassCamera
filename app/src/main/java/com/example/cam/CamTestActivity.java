package com.example.cam;

/**
 * @author Jose Davis Nidhin
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CamTestActivity extends Activity {
	private static final String TAG = "CamTestActivity";

    public final static String FIRST_PHOTO_PATH = "com.ozcanlab.rdt.FIRST_PHOTO_PATH";
    public final static String SECOND_PHOTO_PATH = "com.ozcanlab.rdt.SECOND_PHOTO_PATH";


    Preview preview;
	Button buttonClick;
	Camera camera;
	Activity act;
	Context ctx;

    private int numOfPictureTaken = 0;

    private static String firstPhotoPath;
    private static String secondPhotoPath;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		act = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.main);

		preview = new Preview(this, (SurfaceView)findViewById(R.id.surfaceView));
		preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		((FrameLayout) findViewById(R.id.layout)).addView(preview);
		preview.setKeepScreenOn(true);

		preview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				camera.takePicture(shutterCallback, rawCallback, jpegCallback);
			}
		});

		Toast.makeText(ctx, getString(R.string.take_photo_help), Toast.LENGTH_LONG).show();

		//		buttonClick = (Button) findViewById(R.id.btnCapture);
		//		
		//		buttonClick.setOnClickListener(new OnClickListener() {
		//			public void onClick(View v) {
		////				preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
		//				camera.takePicture(shutterCallback, rawCallback, jpegCallback);
		//			}
		//		});
		//		
		//		buttonClick.setOnLongClickListener(new OnLongClickListener(){
		//			@Override
		//			public boolean onLongClick(View arg0) {
		//				camera.autoFocus(new AutoFocusCallback(){
		//					@Override
		//					public void onAutoFocus(boolean arg0, Camera arg1) {
		//						//camera.takePicture(shutterCallback, rawCallback, jpegCallback);
		//					}
		//				});
		//				return true;
		//			}
		//		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		int numCams = Camera.getNumberOfCameras();
		if(numCams > 0){
			try{
				camera = Camera.open(0);
				camera.startPreview();
				preview.setCamera(camera);
			} catch (RuntimeException ex){
				Toast.makeText(ctx, getString(R.string.camera_not_found), Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	protected void onPause() {
		if(camera != null) {
			camera.stopPreview();
			preview.setCamera(null);
			camera.release();
			camera = null;
		}
		super.onPause();
	}

	private void resetCam() {
		camera.startPreview();
		preview.setCamera(camera);
	}

	private void refreshGallery(File file) {
		Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mediaScanIntent.setData(Uri.fromFile(file));
		sendBroadcast(mediaScanIntent);
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			//			 Log.d(TAG, "onShutter'd");
		}
	};

	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			//			 Log.d(TAG, "onPictureTaken - raw");
		}
	};

	PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

            SaveImageTask saveImage = new SaveImageTask();
            saveImage.execute(data);
            while (saveImage.isLocked())
                continue;

            //reset the camera and take another photo
            if (numOfPictureTaken == 1) {
                resetCam();
            }


            //invoke the confirmation activity
            else if (numOfPictureTaken == 2){

                Intent intent = new Intent(CamTestActivity.this, MainActivity.class);
                intent.putExtra(FIRST_PHOTO_PATH, firstPhotoPath);
                intent.putExtra(SECOND_PHOTO_PATH, secondPhotoPath);
                startActivity(intent);
            }




		}
	};

	private class SaveImageTask extends AsyncTask<byte[], Void, Void> {


        public boolean locked;

        public boolean isLocked(){
            return locked;
        }


        @Override
        protected void onPostExecute(Void v)
        {

            super.onPostExecute(null);
        }

        @Override
        protected void onPreExecute (){
            super.onPreExecute();
            locked = true;

        }
		@Override
		protected Void doInBackground(byte[]... data) {
			FileOutputStream outStream = null;

			// Write to SD Card

			try {
                File sdCard = Environment.getExternalStorageDirectory();
				File dir = new File (sdCard.getAbsolutePath() + "/camtest");
				dir.mkdirs();				

				String fileName = String.format("%d.jpg", System.currentTimeMillis());
				File outFile = new File(dir, fileName);


                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);

				outStream.flush();
				outStream.close();

				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());
                if (numOfPictureTaken == 0)
                    firstPhotoPath = new String(outFile.getAbsolutePath());

                else if (numOfPictureTaken == 1)
                {
                    secondPhotoPath = new String(outFile.getAbsolutePath());
                }
				refreshGallery(outFile);

                numOfPictureTaken++;
                locked = false;

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			return null;
		}

	}
}


