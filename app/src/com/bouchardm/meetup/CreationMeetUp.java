package com.bouchardm.meetup;

import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;

public class CreationMeetUp extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meet_up_creation);
	}
	
	public void checherAmis(View source)
	{
		//this.startActivity(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI));
		this.startActivity(new Intent(this, ListeAmis.class));
	}
	
	public void creeEvenement(View source)
	{
		this.finish();
	}
	
}
