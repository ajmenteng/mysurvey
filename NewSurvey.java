package com.example.mysurvey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class NewSurvey extends Activity {
	
	DbAdapter dbHelper;
	
	
	Uri selectedImageUri;
	String  selectedPath1, selectedPath2, status;
	ImageView preview1,preview2;

	public String uploadBitmap(Bitmap bitmap, int photo){
		String ipath="";
		//prepare new name for images using datetime
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdfName = new SimpleDateFormat("yyyyMMddHHmmss");
		String imgName = sdfName.format(c.getTime());
		
		File imagePath = new File(Environment.getExternalStorageDirectory()+ "/mySurveyPhoto/img" + photo + "-" + imgName + ".jpg");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(imagePath);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
			ipath = imagePath.getPath();
			//Message.message(this,"This result has been successfully downloaded to "+ imagePath);
		} catch (FileNotFoundException e) {
			Log.e("GREC", e.getMessage(),e);
		} catch (IOException e) {
			Log.e("GREC", e.getMessage(),e);
		}
		return ipath;
	}
	
	public Bitmap resizeImage(String imgPath){
		Bitmap myBitmap = BitmapFactory.decodeFile(imgPath);
		final int maxSize = 200;
		int outWidth,outHeight;
		int inWidth = myBitmap.getWidth();
		int inHeight = myBitmap.getHeight();
		if(inWidth > inHeight){
			outWidth = maxSize;
			outHeight = (inHeight * maxSize) / inWidth;
		} else {
			outHeight = maxSize;
			outWidth = (inWidth * maxSize) / inHeight;
		}
		
		Bitmap resizedImg = Bitmap.createScaledBitmap(myBitmap, outWidth, outHeight, false);
		return resizedImg;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newsurvey);
		dbHelper = new DbAdapter(this);
				
		SharedPreferences prefs = getSharedPreferences("mySurvey", MODE_PRIVATE);
		String username = prefs.getString("username", null);
		
		//Check if there is any open survey created by this user
		//If yes, then the user should finish it first before creating another survey
		int sid = dbHelper.checkOpenSurvey(username);
		if(sid>0){
			Intent launchactivity= new Intent(getApplicationContext(), HelpOthers.class);                               
			startActivity(launchactivity);
			finish();
		}
		
		Button btPic1 = (Button)findViewById(R.id.pic1Upload);
		Button btPic2 = (Button)findViewById(R.id.pic2Upload);

		preview1 = (ImageView) findViewById(R.id.iView1);
		preview2 = (ImageView) findViewById(R.id.iView2);
		
		Button btLaunch = (Button)findViewById(R.id.btnLaunch);
			  
		btPic1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openGallery(10);
			}
		});
		
		btPic2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openGallery(11);
			}
		});
		
		btLaunch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v){
				//upload the file
				if(!selectedPath1.equals(null) && !selectedPath2.equals(null)){
					
					//get username via SharedPreferences
					SharedPreferences prefs = getSharedPreferences("mySurvey", MODE_PRIVATE);
					String username = prefs.getString("username", null);
					
					Bitmap fSrc1 = resizeImage(selectedPath1);
					Bitmap fSrc2 = resizeImage(selectedPath2);
					
					String fDst1 = uploadBitmap(fSrc1,1);
					String fDst2 = uploadBitmap(fSrc2,2);					
					
					Calendar c = Calendar.getInstance();
					String datecreated = dbHelper.getDatetimeNow();
					
					addSurvey(username, fDst1, fDst2, datecreated);
					
				}
			}			
		});		
	}
	
	
	
	public void openGallery(int req_code){
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent,"Select file to upload "), req_code);
	}
	 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if(data.getData() != null){
				selectedImageUri = data.getData();
			}
			if (requestCode == 10){
				selectedPath1 = getPath(selectedImageUri);
				preview1.setImageURI(selectedImageUri);
				//Message.message(this, selectedPath1);
			}
			if (requestCode == 11){
				selectedPath2 = getPath(selectedImageUri);
				preview2.setImageURI(selectedImageUri);
				//Message.message(this, selectedPath2);
				
			}
		}
	}
	
	public String getPath(Uri uri){
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		cursor.moveToFirst();
		String document_id = cursor.getString(0);
		document_id = document_id.substring(document_id.lastIndexOf(":")+1);
		cursor.close();
		
		cursor = getContentResolver().query(
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
		cursor.moveToFirst();
		String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
		cursor.close();
		
		return path;
	}	
	
	public void addSurvey(String uname, String photo1, String photo2, String datecreated){
		String status="open";		
		
     	long id=dbHelper.insertDataSurvey(uname, photo1, photo2, datecreated, status);
     	//Message.message(this, txt);
     	
     	if(id<0) {
     		Message.message(this, "Unsuccessful");
     	}
     	else {
     		Message.message(this, "Successfully created a survey");
     	}
     	
     	Intent launchLoginActivity= new Intent(getApplicationContext(), HelpOthers.class);                               
		startActivity(launchLoginActivity);	
		
		finish();
     	
	}
	public void exitSurvey(View v){
		finish();
	}
}
