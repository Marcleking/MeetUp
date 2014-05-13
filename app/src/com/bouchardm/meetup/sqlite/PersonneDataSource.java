package com.bouchardm.meetup.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PersonneDataSource {
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
	
	public PersonneDataSource(Context p_Context) {
        m_Helper = new PersonDbHelper(p_Context);
    }
	
	public void open() {
        m_Db = this.m_Helper.getWritableDatabase();
    }
	
	public void close() {
        m_Db.close();
    }
	
	public int insert(Personne p_personne){
		ContentValues row = personToContentValues(p_personne);
		int newId = (int) m_Db.insert(TABLE_NAME, null, row);
		p_personne.setId(newId);
		return newId;
	}
	
	private ContentValues personToContentValues(Personne p_Person) {
        ContentValues row = new ContentValues();
        row.put(COL_GOOGLEID, p_Person.get_googleId());
		row.put(COL_SECURITYNUMBER, p_Person.get_securityNumber());
        return row;
    }
	
	public boolean userExisting(String p_googleId){
		Cursor c = m_Db.query(TABLE_NAME, null, null, null, null, null, null);
		boolean found = false;
		c.moveToFirst();
		while(!c.isAfterLast() && !found) {
        	if(c.getString(IDX_GOOGLEID).equals(p_googleId))
				found = true;
			c.moveToNext();
        }
		return found;
		
		/*
		Cursor c = m_Db.query(TABLE_NAME, null, COL_GOOGLEID + "=" + p_googleId, null, null, null, null);
		if(c.moveToFirst()){
			Log.i("Check user", "User exists");
			return true;
		}
		else{
			Log.i("Check user", "User does not exists");
			return false;
		}*/
	}
	
	public Personne getPersonne(String p_googleId){
		Cursor c = m_Db.query(TABLE_NAME, null, null, null, null, null, null);
		boolean found = false;
		Personne usager = null;
		c.moveToFirst();
		while(!c.isAfterLast() && !found){
			if(c.getString(IDX_GOOGLEID).equals(p_googleId))
				usager = cursorToPersonne(c);
			c.moveToNext();
		}
		return usager;
	}
	
	
	
	public void listAll(){
		Cursor c = m_Db.query(
	            TABLE_NAME, null, null, null, null, null, null);
	        c.moveToFirst();
	        while(!c.isAfterLast()) {
	        	Log.i("List All", c.getString(IDX_ID) + " : " + c.getString(IDX_GOOGLEID));
	        	c.moveToNext();
	        }
	}
	
	public void removeAll() {
        m_Db.delete(TABLE_NAME, null, null);
    }
	
	private Personne cursorToPersonne(Cursor c){
		Personne p = new Personne();
		p.setId(				c.getInt(	IDX_ID));
		p.set_googleId(			c.getString(IDX_GOOGLEID));
		p.set_securityNumber( 	c.getString(IDX_SECURITYNUMBER));
		return p;
	}
	
	private static class PersonDbHelper extends SQLiteOpenHelper{
		public PersonDbHelper (Context p_Context){
			super(p_Context, "person.sqlite", null, DB_VERSION);
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
