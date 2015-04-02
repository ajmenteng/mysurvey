/*
# Script Name 	= DbAdapter.java
# Author		= John W Chandra
# Desc			= Database adapter for mobile survey application 
*/
package com.example.mysurvey;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbAdapter {

	DbHelper helper;

	public DbAdapter(Context context) {
		helper = new DbHelper(context);		
	}

	public long insertDataLogin(String uname, String paswd, String dob, String gender) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(DbHelper.UNAME, uname);
		contentValues.put(DbHelper.PASWD, paswd);
		contentValues.put(DbHelper.DOB, dob);
		contentValues.put(DbHelper.GENDER, gender);
		long id = db.insert(DbHelper.TABLE_USER, null, contentValues);
		db.close();
		return id;
	}
	
	public long insertDataSurvey(String uname, String photo1, String photo2, String datecreated, String status) {
		SQLiteDatabase db2 = helper.getWritableDatabase();		
		ContentValues cvSurvey = new ContentValues();
		cvSurvey.put(DbHelper.OWNER, uname);
		cvSurvey.put(DbHelper.PHOTO1, photo1);
		cvSurvey.put(DbHelper.PHOTO2, photo2);
		cvSurvey.put(DbHelper.DATECREATED, datecreated);
		cvSurvey.put(DbHelper.DATEFINISHED, "");
		cvSurvey.put(DbHelper.STATUS, "open");
		long id = db2.insert(DbHelper.TABLE_SURVEY,null,cvSurvey);
		db2.close();
		return id;
	}

	public int getLogin(String uname, String paswd) {
		SQLiteDatabase db = helper.getWritableDatabase();
		int userid=0;
		String[] columns = { DbHelper.UID };
		String[] selectionArgs = { uname, paswd };
		Cursor cursor = db.query(DbHelper.TABLE_USER, columns, 
				DbHelper.UNAME + "=? AND "+ DbHelper.PASWD + "=?",
				selectionArgs, null, null, null, null);
		if(cursor.getCount()>0){
			cursor.moveToNext();
			int index0=cursor.getColumnIndex(DbHelper.UID);
			userid=cursor.getInt(index0);
		}
		cursor.close();
		db.close();
		return userid;
	}
	
	public String getSurvey(String uname) {
		String cSurvey="";
		SQLiteDatabase db = helper.getWritableDatabase();
		
		String str = "SELECT _id, photo1, photo2 FROM SURVEY WHERE status='open' AND owner!='" + uname +"' AND " +
				"_id NOT IN (SELECT sid FROM USERSURVEY WHERE voter='" + uname +"') ORDER BY RANDOM() LIMIT 1";
		Cursor cursor = db.rawQuery(str, null);
		
		if(cursor.getCount()>0){
			cursor.moveToNext();
			int iSID = cursor.getColumnIndex(DbHelper.SID);
			int sid = cursor.getInt(iSID);
			
			int iPhoto1 = cursor.getColumnIndex(DbHelper.PHOTO1);
			String photo1 = cursor.getString(iPhoto1);
			
			int iPhoto2 = cursor.getColumnIndex(DbHelper.PHOTO2);
			String photo2 = cursor.getString(iPhoto2);
			
			cSurvey = sid + "," + photo1 + "," + photo2;			
		}
		cursor.close();
		db.close();
		return cSurvey;
	}
	
	public int getCurrentSurvey(String uname, int vote) {
		int cSurvey=0;
		Cursor cursor = null;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		String str = "SELECT COUNT(*) AS \"subtotal\" FROM USERSURVEY WHERE voter!='" + uname + "' AND " +
				"sid IN (SELECT _id FROM SURVEY WHERE status='open' AND owner='" + uname +"') AND vote=" + vote;
		cursor = db.rawQuery(str, null);
		while(cursor.moveToNext()){
			int stIndex = cursor.getColumnIndex("subtotal");
			cSurvey = cursor.getInt(stIndex);
		}
		db.close();
		cursor.close();
		return cSurvey;
	}
	
	public String getCSDate(String uname) {
		String cSurvey="";
		Cursor cursor = null;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		String str = "SELECT datecreated FROM SURVEY WHERE status='open' AND owner='" + uname +"'";
		cursor = db.rawQuery(str, null);
		while(cursor.moveToNext()){
			int csdIndex = cursor.getColumnIndex("datecreated");
			cSurvey = cursor.getString(csdIndex);
		}
		db.close();
		cursor.close();
		return cSurvey;
	}
	
	public String getDateNow(){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");
		String dateNow = sdfNow.format(c.getTime());
		return dateNow;
	}
	
	public String getDatetimeNow(){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String dateNow = sdfNow.format(c.getTime());
		return dateNow;
	}
	
	public long insertDataUserSurvey(String voter, String sid, String vote, String datevoted) {
		SQLiteDatabase db = helper.getWritableDatabase();		
		ContentValues cvUserSurvey = new ContentValues();
		cvUserSurvey.put(DbHelper.VOTER, voter);
		cvUserSurvey.put(DbHelper.SURVEYID, sid);
		cvUserSurvey.put(DbHelper.VOTE, vote);
		cvUserSurvey.put(DbHelper.DATEVOTED, datevoted);
		long id = db.insert(DbHelper.TABLE_USERSURVEY,null,cvUserSurvey);
		db.close();
		return id;
	}
	
	public int checkOpenSurvey(String owner){
		int sid=0;
		SQLiteDatabase db = helper.getWritableDatabase();
		String str = "SELECT _id FROM SURVEY WHERE owner='" + owner + "' AND status='open' " +
				"AND owner='" + owner + "'";
		Cursor cursor = db.rawQuery(str, null);
		
		if(cursor.getCount()>0){
			cursor.moveToNext();
			int iSID = cursor.getColumnIndex(DbHelper.SID);
			sid = cursor.getInt(iSID);
		} 
		cursor.close();
		db.close();
		return sid;
	}
	
	public int checkLastSurvey(String owner){
		int sid=0;
		SQLiteDatabase db = helper.getWritableDatabase();
		String str = "SELECT _id FROM SURVEY WHERE owner='" + owner + "' AND datecreated = " +
				"(SELECT MAX(datecreated) FROM SURVEY WHERE owner='" + owner + "')";
		Cursor cursor = db.rawQuery(str, null);
		
		if(cursor.getCount()>0){
			cursor.moveToNext();
			int iSID = cursor.getColumnIndex(DbHelper.SID);
			sid = cursor.getInt(iSID);
		} 
		cursor.close();
		db.close();
		return sid;
	}
	
	public int countKarma(String voter){
		SQLiteDatabase db = helper.getWritableDatabase();
		String str = "SELECT sid FROM USERSURVEY WHERE voter='" + voter + "'";
		Cursor cursor = db.rawQuery(str, null);
		int karma = cursor.getCount();
		cursor.close();
		db.close();
		return karma;
	}
	
	public int getSummaryGender(String uname, String gender, int vote) {
		int stMale=0;
		SQLiteDatabase db = helper.getWritableDatabase();
		String str = "SELECT COUNT(US.sid) AS \"subtotal\" FROM USERSURVEY US, SURVEY S, USER U " +
				"WHERE US.sid=S._id " +
				"AND US.voter = U.uname " +
				//"AND S.datecreated=MAX(datecreated) " +
				//"AND S.status='open' " +
				"AND datecreated = " + 
				"(SELECT MAX(datecreated) FROM SURVEY WHERE owner='" + uname + "')" +
				"AND S.owner='" + uname +"' " +
				"AND U.gender='" + gender + "' " +
				"AND U.uname!='" + uname + "' " +
				"AND US.voter!='" + uname + "' " +
				"AND US.vote=" + vote;
		Cursor cursor = db.rawQuery(str, null);
		if(cursor.getCount()>0){
			cursor.moveToNext();
			int stGenderIndex = cursor.getColumnIndex("subtotal");
			stMale = cursor.getInt(stGenderIndex);
		}
		cursor.close();
		db.close();
		return stMale;
	}
	

	public int getSummaryAge(String uname, int age, int vote) {
		int stAge=0;
		String ageRange="";
		
		if(age==2035){
			ageRange="((strftime('%Y', 'now') - strftime('%Y',U.dob)) >= 20) AND ((strftime('%Y', 'now') - strftime('%Y',U.dob)) <= 35) ";
		} else {
			if(age==20)
				ageRange="((strftime('%Y', 'now') - strftime('%Y',U.dob)) < 20) ";
			else
				ageRange="((strftime('%Y', 'now') - strftime('%Y',U.dob)) > 35) ";
		}
		
		SQLiteDatabase db = helper.getWritableDatabase();
		String str = "SELECT COUNT(US.sid) AS \"subtotal\" FROM USERSURVEY US, SURVEY S, USER U " +
				"WHERE US.sid=S._id " +
				"AND US.voter = U.uname " +
				"AND datecreated = " + 
				"(SELECT MAX(datecreated) FROM SURVEY WHERE owner='" + uname + "') " +
				"AND S.owner='" + uname +"' " +
				"AND U.uname!='" + uname + "' " +
				"AND " + ageRange + " " +
				"AND US.voter!='" + uname + "' " +
				"AND US.vote=" + vote;
		Cursor cursor = db.rawQuery(str, null);
		if(cursor.getCount()>0){
			cursor.moveToNext();
			int stAgeIndex = cursor.getColumnIndex("subtotal");
			stAge = cursor.getInt(stAgeIndex);	
		}
		cursor.close();
		db.close();
		return stAge;
	}
	
	public String getSummaryPhoto(String uname) {
		String sSurvey="";
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		String str = "SELECT _id, photo1, photo2 FROM SURVEY WHERE " +
				"datecreated IN " + 
				"(SELECT MAX(datecreated) FROM SURVEY WHERE owner='" + uname + "')" +
				" AND owner='" + uname + "' LIMIT 1";
		Cursor cursor = db.rawQuery(str, null);
		
		if(cursor.getCount()>0){
			cursor.moveToNext();
			int iSID = cursor.getColumnIndex(DbHelper.SID);
			int sid = cursor.getInt(iSID);
			
			int iPhoto1 = cursor.getColumnIndex(DbHelper.PHOTO1);
			String photo1 = cursor.getString(iPhoto1);
			
			int iPhoto2 = cursor.getColumnIndex(DbHelper.PHOTO2);
			String photo2 = cursor.getString(iPhoto2);
			
			sSurvey = sid + "," + photo1 + "," + photo2;
		}
		cursor.close();
		db.close();
		return sSurvey;
	}
	
	public void completeSurvey(String uname){
		SQLiteDatabase db = helper.getWritableDatabase();
		String datefinished = getDatetimeNow();
		
		ContentValues cv = new ContentValues();
		cv.put(DbHelper.STATUS,"close");
		cv.put(DbHelper.DATEFINISHED, datefinished);
		db.update(DbHelper.TABLE_SURVEY, cv, DbHelper.STATUS + "='open' AND " + 
				DbHelper.OWNER + "='" + uname + "'", null);
		db.close();
	}
	
	public String getSummaryTotal(String uname){
		int total;
		String dc,df;
		dc="";
		df="";
		int sid = checkLastSurvey(uname);
		String sumTotal="";
		SQLiteDatabase db = helper.getWritableDatabase();
		
		String str = "SELECT COUNT(*) AS total, S.datecreated, S.datefinished " +
				"FROM SURVEY S, USERSURVEY US WHERE S._id=US.sid AND S._id=" + sid;
		
		Cursor cursor = db.rawQuery(str, null);
		if(cursor.getCount()>0){
			cursor.moveToNext();
			int itotal = cursor.getColumnIndex("total");
			total = cursor.getInt(itotal);
			
			int idc = cursor.getColumnIndex(DbHelper.DATECREATED);
			dc = cursor.getString(idc);
			if(dc=="") dc="-";
			
			int idf = cursor.getColumnIndex(DbHelper.DATEFINISHED);
			df = cursor.getString(idf);
			if(df==null) df="-";
			
			sumTotal = total + "," + dc + "," + df;			
		} 
		cursor.close();
		db.close();
		return sumTotal;
		
	}
	
	static class DbHelper extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "mysurveydb";
		private static final int DATABASE_VERSION = 24;
		private static final String TABLE_USER = "USER";
		private static final String UID = "_id";
		private static final String UNAME = "uname";
		private static final String PASWD = "paswd";
		private static final String DOB = "dob";
		private static final String GENDER = "gender";
		private static final String CREATE_TABLE_USER = "CREATE TABLE "
				+ TABLE_USER + " (" + UID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + UNAME
				+ " VARCHAR(255), " + PASWD + " VARCHAR(255), " + DOB
				+ " DATE, " + GENDER + " CHAR(20));";
		private static final String DROP_TABLE_USER = "DROP TABLE IF EXISTS "
				+ TABLE_USER;

		private static final String TABLE_SURVEY = "SURVEY";
		private static final String SID = "_id";
		private static final String OWNER = "owner";
		private static final String PHOTO1 = "photo1";
		private static final String PHOTO2 = "photo2";
		private static final String DATECREATED = "datecreated";
		private static final String DATEFINISHED = "datefinished";
		private static final String STATUS = "status";
		private static final String REPORTED = "reported";
		private static final String CREATE_TABLE_SURVEY = "CREATE TABLE "
				+ TABLE_SURVEY + " (" + SID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + OWNER + " TEXT, " + PHOTO1
				+ " TEXT, " + PHOTO2 + " TEXT, " + DATECREATED
				+ " DATETIME, " + DATEFINISHED + " DATETIME, " + STATUS + " TEXT, " + REPORTED + " INTEGER);";

		private static final String DROP_TABLE_SURVEY = "DROP TABLE IF EXISTS "
				+ TABLE_SURVEY;
		
		private static final String TABLE_USERSURVEY = "USERSURVEY";
		private static final String USID = "_id";
		private static final String VOTER = "voter";
		private static final String SURVEYID = "sid";
		private static final String VOTE = "vote"; //photo number (1 or 2)
		private static final String DATEVOTED = "DATEVOTED";
		private static final String CREATE_TABLE_USERSURVEY = "CREATE TABLE "
				+ TABLE_USERSURVEY + " (" + USID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + VOTER + " TEXT, " 
				+ SURVEYID + " INTEGER, " + VOTE + " INTEGER, " + DATEVOTED + " DATE);";
	
		private static final String DROP_TABLE_USERSURVEY = "DROP TABLE IF EXISTS "
				+ TABLE_USERSURVEY;
		
		private Context context;

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			this.context = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(CREATE_TABLE_USER);
				db.execSQL(CREATE_TABLE_SURVEY);
				db.execSQL(CREATE_TABLE_USERSURVEY);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				Message.message(context, "" + e);
			}

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			try {
				//Message.message(context, "onUpgrade called");
				db.execSQL(DROP_TABLE_USER);
				db.execSQL(DROP_TABLE_SURVEY);
				db.execSQL(DROP_TABLE_USERSURVEY);
				onCreate(db);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				Message.message(context, "" + e);
			}

		}
	}

}
