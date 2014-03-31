package com.bouchardm.meetup.classes;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class personne {
	private final static int DB_VERSION = 1;
	private final static String TABLE_NAME = "person";
	
	private static final String COL_ID = "_id";
	private static final String COL_GOOGLEID = "googleid";
	private static final String COL_SECURITYNUMBER = "securitynumber";
	
	private static final int IDX_ID = 0;
	private static final int IDX_GOOGLEID = 1;
	private static final int IDX_SECURITYNUMBER = 2;
	
	private PersonDbHelper m_Helper;
	private SQLiteDatabase m_Db;
	
	public personne(Context p_Context){
		m_Helper = new PersonDbHelper(p_Context);
	}
	
	public void open(){
		m_Db = this.m_Helper.getWritableDatabase();
	}
	
	public void close(){
		m_Db.close();
	}
	
	public int insert(String googleId){
		ContentValues row = new ContentValues();
		row.put(COL_GOOGLEID, googleId);
		row.put(COL_SECURITYNUMBER, generateSecurityNumber(googleId));
		int newId = (int) m_Db.insert(TABLE_NAME, null, row);
		return newId;
	}
	
	public String getConnectedPersonSecurity(String p_googleId){
		Cursor c = m_Db.query(
				TABLE_NAME, null, COL_GOOGLEID + "=" + p_googleId, null, null, null, null );
		c.moveToFirst();
		if(!c.isAfterLast()){
			return c.getString(IDX_SECURITYNUMBER);
		}
		else
		{
			return null;
		}
	}
	
	public boolean userExisting(String p_googleId){
		Cursor c = m_Db.query(TABLE_NAME, null, COL_GOOGLEID + "=" + p_googleId, null, null, null, null);
		c.moveToFirst();
		if(c.getColumnCount() == 0){
			return false;
		}
		else{
			return true;
		}
	}
	
	private String generateSecurityNumber(String googleId){
		String sha1 = "";
		Random rn = new Random();
		googleId += rn.nextInt();
		try{
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(googleId.getBytes("UTF-8"));
			sha1 = byteToHex(crypt.digest());
		}
		catch(NoSuchAlgorithmException e)
	    {
	        e.printStackTrace();
	    }
	    catch(UnsupportedEncodingException e)
	    {
	        e.printStackTrace();
	    }
		
		return sha1;
	}
	
	private String byteToHex(final byte[] hash)
	{
	    Formatter formatter = new Formatter();
	    for (byte b : hash)
	    {
	        formatter.format("%02x", b);
	    }
	    String result = formatter.toString();
	    formatter.close();
	    return result;
	}
	
	private static class PersonDbHelper extends SQLiteOpenHelper{
		public PersonDbHelper (Context p_Context){
			super(p_Context, "personne.sqlite", null, DB_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase p_db){
			p_db.execSQL(
					"create table " + TABLE_NAME 
					+ " (_id integer primary key autoincrement, " 
					+ "googleid text, securitynumber text)");
		}
		
		@Override
		public void onUpgrade (SQLiteDatabase p_db, int p_oldVersion, int p_newVersion){
			p_db.execSQL("drop table if exists " + TABLE_NAME);
			this.onCreate(p_db);
		}
	}
}
