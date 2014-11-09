package com.example.businesscardimporter;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder mHolder;
	private Camera mCamera;
	
	public CameraPreview(Context context, Camera camera) {
		super(context);
		mCamera = camera;
		
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);		
		
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
		
		// preview surface does not exist
		if(mHolder.getSurface() == null) {
			return;
		}
		
		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch(Exception e) {
			
		}
		
		// set preview size and make any resize, rotate or
        // reformatting changes here
		

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch(Exception e) {
        	System.out.println("error occured while changing the preview");
        }
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// Surface has been created, now tell the camera where to draw the preview
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch(Exception e) {
//			Log.d(TAG, "Error setting camera preview: " + e.getMessage());
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.release();
		
	}

	
}
