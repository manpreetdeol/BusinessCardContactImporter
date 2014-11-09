
package com.example.businesscardimporter;



import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.businesscardimporter.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements NoticeDialogFragment.NoticeDialogListener {

	public static final int MEDIA_TYPE_IMAGE = 1;
	File pictureFile;
	static String imageFileName;
	
	Camera camera;
	CameraPreview mPreview;
	Button captureButton;
	PackageManager pkmgr;
	protected byte[] fileData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		
		setupActionBar();
		
		captureButton = (Button) findViewById(R.id.button_capture);
		Context context = null;
		
//		camera.release();
//		camera = null;		
		
		//1- check if the device has a camera
		boolean hasCamera = checkCameraHardware(context);
		
		//2- if it does, get an instance of Camera class
		if(hasCamera) {
			camera = getCameraInstance();
		}
		else {
			System.out.println("No camera in this device");
		}
		
		//3- Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, camera); 
		
		//4- add the preview class to the frame layout container
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        
        
        //5- Add a listener to the Capture button
//       captureButton.setRotation(90);
        captureButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get an image from the camera
                    camera.takePicture(null, null, mPicture);
                }
            }
        );
	}
	
	//In order to receive data in a JPEG format, you must implement an Camera.PictureCallback 
	// interface to receive the image data and write it to a file
	private PictureCallback mPicture = new PictureCallback() {		
		
	    @Override
	    public void onPictureTaken(byte[] data, Camera camera) {

	    	fileData = data;
	        pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
	        if (pictureFile == null){
	            return;
	        }
	        else {
	        	DialogFragment dialog = new NoticeDialogFragment();
	             dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
	        }
	        

//	        try {
//	            FileOutputStream fos = new FileOutputStream(pictureFile);
//	            fos.write(data);
//	            fos.close();
//	        } catch (Exception e) {
//	           
//	        } 
	        
//	        camera.release();
	    }
	};
	
	/** Create a file Uri for saving an image or video */
//	private static Uri getOutputMediaFileUri(int type){
//	      return Uri.fromFile(getOutputMediaFile(type));
//	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
	    System.out.println(mediaStorageDir);
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            System.out.println("failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name	    
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    imageFileName = mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".png";
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(imageFileName);
	    } else {
	        return null;
	    }

	    return mediaFile;
	}

	// getting access to the underlying camera
	public static Camera getCameraInstance() {
		Camera camera = null;
		
		try {
			camera = Camera.open();
		} catch(Exception e) {
			camera.release();
			System.out.println("Camera not available or access not granted");
		}
		return camera;
	}

	// returns true, if the device has camera, else, false
	private boolean checkCameraHardware(Context context) {
		
//		pkmgr  = context.getPackageManager();
		
		try {
			if(getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
				return true;
			}
			else {
				return false;
			}
		} catch (Exception e) {
			System.out.println("Error while checking camera hardware");
		}		
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		
		System.out.println("I came to save file");
		try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(fileData);
            fos.close();
            
            Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_SHORT).show();
            
            PreProcessing.inputForSmoothing(imageFileName);

        } catch (Exception e) {
           System.out.println(e.getStackTrace());
        }
		
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		System.out.println("I came to not to save file");
		
		// if the user does not want to save the image, offer him to click another image
	    Intent intent = getIntent();
	    camera.release();
	    finish();
	    startActivity(intent);
		
	}

}
