package com.example.mysurvey;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HelpOthers extends Activity {
	DbAdapter dbHelper;
	ImageView preview1,preview2;
	TextView tvKarma,tvStartDate,tvOption1,tvOption2,tvTotal;
	
	public void reloadImages(){
		//get username via SharedPreferences
		SharedPreferences prefs = getSharedPreferences("mySurvey", MODE_PRIVATE);
		String username = prefs.getString("username", null);
		
		dbHelper = new DbAdapter(this);
		
		int karma = dbHelper.countKarma(username);
		tvKarma = (TextView) findViewById(R.id.tvKarma);
		tvKarma.setText("Your karma = " + karma);
		
		String csDate = dbHelper.getCSDate(username);
		tvStartDate = (TextView) findViewById(R.id.tvStartDate);
		tvStartDate.setText("Started at " + csDate);
		
		int currOpt1 = dbHelper.getCurrentSurvey(username,1);
		tvOption1 = (TextView) findViewById(R.id.tvOption1);
		tvOption1.setText(Integer.toString(currOpt1));		
		
		int currOpt2 = dbHelper.getCurrentSurvey(username,2);
		tvOption2 = (TextView) findViewById(R.id.tvOption2);
		tvOption2.setText(Integer.toString(currOpt2));
		
		int total = currOpt1 + currOpt2;
		tvTotal = (TextView) findViewById(R.id.tvTotal);
		tvTotal.setText(Integer.toString(total));
		
		String cSurvey = dbHelper.getSurvey(username);
		
		
		if (cSurvey != ""){
			String[] sItem = cSurvey.split(",");
						
			preview1 = (ImageView) findViewById(R.id.iView1);
			preview2 = (ImageView) findViewById(R.id.iView2);
			
			Bitmap bmp1 = BitmapFactory.decodeFile(sItem[1]);
			Bitmap bmp2 = BitmapFactory.decodeFile(sItem[2]);
			
			preview1.setImageBitmap(bmp1);
			preview2.setImageBitmap(bmp2);		
		}
		else
			Message.message(this, "Thank you! You have finished all the surveys!");
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.helpothers);
		
		reloadImages();
	}
	
	public void iView1Clicked(View v) {
		//get username via SharedPreferences
		SharedPreferences prefs = getSharedPreferences("mySurvey", MODE_PRIVATE);
		String username = prefs.getString("username", null);
		
		dbHelper = new DbAdapter(this);
		String cSurvey = dbHelper.getSurvey(username);
		if(cSurvey!=""){
			String[] sItem = cSurvey.split(",");
			String datevoted = dbHelper.getDateNow();
			
			long id = dbHelper.insertDataUserSurvey(username, sItem[0], "1", datevoted);		
			reloadImages();
		}
	}
	
	public void iView2Clicked(View v) {
		//get username via SharedPreferences
		SharedPreferences prefs = getSharedPreferences("mySurvey", MODE_PRIVATE);
		String username = prefs.getString("username", null);
		
		dbHelper = new DbAdapter(this);
		String cSurvey = dbHelper.getSurvey(username);
		if(cSurvey!=""){
			String[] sItem = cSurvey.split(",");
			String datevoted = dbHelper.getDateNow();
			
			long id = dbHelper.insertDataUserSurvey(username, sItem[0], "2", datevoted);
			reloadImages();
		}
	}
	
	public void finishSurvey(View v){
		Intent finishActivity= new Intent(getApplicationContext(), SummaryResult.class);   
		
		startActivity(finishActivity);	
		finish();
	}

	public void exitSurvey(View v){
		finish();
	}
}
