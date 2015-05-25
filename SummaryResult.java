package com.example.mysurvey;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SummaryResult extends Activity {
	DbAdapter dbHelper;
	ImageView preview1,preview2;
	TextView tvMale1,tvFemale1,tvMale2,tvFemale2;
	TextView tvlt20,tvbe2035,tvab35;
	TextView tvlt202,tvbe20352,tvab352;
	TextView tvTotal,tvStartdate,tvEnddate;
	
	public void reloadImages(){
		//get username via SharedPreferences
		SharedPreferences prefs = getSharedPreferences("mySurvey", MODE_PRIVATE);
		String username = prefs.getString("username", null);
		
		dbHelper = new DbAdapter(this);			
		
		String cSurvey = dbHelper.getSummaryPhoto(username);		
		
		if (cSurvey != null){
			String[] sItem = cSurvey.split(",");
			
			preview1 = (ImageView) findViewById(R.id.iView1);
			preview2 = (ImageView) findViewById(R.id.iView2);
			
			Bitmap bmp1 = BitmapFactory.decodeFile(sItem[1]);
			Bitmap bmp2 = BitmapFactory.decodeFile(sItem[2]);
			
			preview1.setImageBitmap(bmp1);
			preview2.setImageBitmap(bmp2);		
		}
		else
			Message.message(this, "Thank you!");
	}
	
	public Bitmap takeScreenshot(){
		View rootView = findViewById(android.R.id.content).getRootView();
		rootView.setDrawingCacheEnabled(true);
		return rootView.getDrawingCache();
	}
	
	public void saveBitmap(Bitmap bitmap){
		//prepare new name for images using datetime
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdfName = new SimpleDateFormat("yyyyMMddHHmmss");
		String imgName = sdfName.format(c.getTime());
		
		File imagePath = new File(Environment.getExternalStorageDirectory()+ "/mySurveyPhoto/summary-" + imgName + ".jpg");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(imagePath);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
			Message.message(this,"This result has been successfully downloaded to "+ imagePath.getPath());
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			Log.e("GREC", e.getMessage(),e);
		} catch (IOException e) {
			Log.e("GREC", e.getMessage(),e);
		}
	}

	public void captureScreen(View v){
		Bitmap bitmap = takeScreenshot();
		saveBitmap(bitmap);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.summary);

		dbHelper = new DbAdapter(this);
		String gender;
		int photo,age;
		
		SharedPreferences prefs = getSharedPreferences("mySurvey", MODE_PRIVATE);
		String username = prefs.getString("username", null);
		
		dbHelper.completeSurvey(username);
		
		tvMale1 = (TextView) findViewById(R.id.tvMale1);
		tvFemale1 = (TextView) findViewById(R.id.tvFemale1);
		
		tvMale2 = (TextView) findViewById(R.id.tvMale2);		
		tvFemale2 = (TextView) findViewById(R.id.tvFemale2);
		
		tvlt20 = (TextView) findViewById(R.id.tvlt20);
		tvbe2035 = (TextView) findViewById(R.id.tvbe2035);
		tvab35 = (TextView) findViewById(R.id.tvab35);
		
		tvlt202 = (TextView) findViewById(R.id.tvlt202);
		tvbe20352 = (TextView) findViewById(R.id.tvbe20352);
		tvab352 = (TextView) findViewById(R.id.tvab352);
		
		tvTotal = (TextView) findViewById(R.id.tvTotal);
		tvStartdate = (TextView) findViewById(R.id.tvStartdate);
		tvEnddate = (TextView) findViewById(R.id.tvEnddate);
		
		photo=1;
		gender="Male";
		int picMale1 = dbHelper.getSummaryGender(username,gender,photo);
		tvMale1.setText(Integer.toString(picMale1));
		
		gender="Female";
		int picFemale1 = dbHelper.getSummaryGender(username,gender,photo);
		tvFemale1.setText(Integer.toString(picFemale1));
			
		age=20;
		int picAge20 = dbHelper.getSummaryAge(username, age, photo);
		tvlt20.setText(Integer.toString(picAge20));
		
		age=2035;
		int picAge2035 = dbHelper.getSummaryAge(username, age, photo);
		tvbe2035.setText(Integer.toString(picAge2035));
		
		age=35;
		int picAge35 = dbHelper.getSummaryAge(username, age, photo);
		tvab35.setText(Integer.toString(picAge35));
		
		//=====================================

		photo=2;
		gender="Male";
		int picMale2 = dbHelper.getSummaryGender(username,gender,photo);
		tvMale2.setText(Integer.toString(picMale2));
		
		gender="Female";
		int picFemale2 = dbHelper.getSummaryGender(username,gender,photo);
		tvFemale2.setText(Integer.toString(picFemale2));
		

		age=20;
		int picAge202 = dbHelper.getSummaryAge(username, age, photo);
		tvlt202.setText(Integer.toString(picAge202));
		
		age=2035;
		int picAge20352 = dbHelper.getSummaryAge(username, age, photo);
		tvbe20352.setText(Integer.toString(picAge20352));
		
		age=35;
		int picAge352 = dbHelper.getSummaryAge(username, age, photo);
		tvab352.setText(Integer.toString(picAge352));

		
		String sumTotal = dbHelper.getSummaryTotal(username);
		if(sumTotal!=""){
			String[] sItem = sumTotal.split(",");
			tvTotal.setText(sItem[0]);	
			tvStartdate.setText(sItem[1]);
			tvEnddate.setText(sItem[2]);
		}
		
		reloadImages();
		
		
		
	}
	
	public void exitSurvey(View v){
		finish();
	}

	public void newSurvey(View v){
		Intent launchactivity= new Intent(getApplicationContext(), NewSurvey.class);                               
		startActivity(launchactivity);	
		finish();
	}
	
	public String saveBitmap2(Bitmap bitmap){
		//prepare new name for images using datetime
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdfName = new SimpleDateFormat("yyyyMMddHHmmss");
		String imgName = sdfName.format(c.getTime());
		
		File imagePath = new File(Environment.getExternalStorageDirectory()+ "/mySurveyPhoto/summary-" + imgName + ".jpg");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(imagePath);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
			Message.message(this,"This result has also been successfully downloaded to "+ imagePath.getPath());
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			Log.e("GREC", e.getMessage(),e);
		} catch (IOException e) {
			Log.e("GREC", e.getMessage(),e);
		}
		
		return imagePath.getParent();
		
	}
	
	public void shareResult(View v){
		Bitmap bitmap = takeScreenshot();

		String path = saveBitmap2(bitmap);
		
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		Uri screenshotUri = Uri.parse(path);
		 
		sharingIntent.setType("image/jpg");
		sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
		startActivity(Intent.createChooser(sharingIntent, "Share image using"));
	}

}
