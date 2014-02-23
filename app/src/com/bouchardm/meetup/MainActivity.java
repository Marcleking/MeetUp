package com.bouchardm.meetup;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import com.bouchardm.meetup.menuUtil.*;

public class MainActivity extends FragmentActivity  {

	private static final int LOGIN_REQUEST = 0;
	private GoogleApiClient mGoogleApiClient;
	
	private DrawerLayout mDrawer;
	private ListView mLeftDrawerList;
	private String[] mLeftMenuItems;
	//private CustomActionBarDrawerToggle mLeftDrawerToggle;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent connection = new Intent(this,ConnectionActivity.class);
		this.startActivity(connection);
		
		LoggedPerson user = ((LoggedPerson)getApplicationContext());
		mGoogleApiClient = user.getClient();
		
		mDrawer = (DrawerLayout)findViewById(R.id.drawer_layout);
		
		initLeftMenu();
		
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	private void initLeftMenu(){
		NsMenuAdapter mAdapter = new NsMenuAdapter(this);
		
		
		
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView parent, View view, int position,long id) {

			// Highlight the selected item, update the title, and close the drawer
			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			setTitle("......");

			String text= "menu click... should be implemented";
			//Toast.makeText(MainActivity.this, text , Toast.LENGTH_LONG).show();
			mDrawer.closeDrawer(mDrawerList);

		}
	}
}

