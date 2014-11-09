package com.example.businesscardimporter;

import com.example.businesscardimporter.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends Activity {

	Button capture;
	Button importimage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
//		capture = (Button) findViewById(R.id.capture_button);
//		capture.setBackgroundResource(R.drawable.capture);
		
//		importimage = (Button) findViewById(R.id.import_button);
//		importimage.setBackgroundResource(R.drawable.importimage);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	public void captureImage(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

}


