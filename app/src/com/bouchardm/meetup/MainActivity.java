package com.bouchardm.meetup;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class MainActivity extends Activity {
	
	/**
	 * Création de la view 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void viewHoraire (View source)
	{
		this.startActivity(new Intent(this, Horaire.class));
	}
	
	public void viewMeetUp (View source)
	{
		this.startActivity(new Intent(this, MeetUp.class));
	}
	
	public void viewAmis(View source)
	{
		this.startActivity(new Intent(this, Amis.class));
	}
	
	

}
