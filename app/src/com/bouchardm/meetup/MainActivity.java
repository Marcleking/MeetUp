package com.bouchardm.meetup;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.os.Parcel;
import android.os.Parcelable;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import android.app.Activity;
import android.content.Intent;
import android.view.View;


import java.util.ArrayList;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import com.bouchardm.meetup.menuUtil.*;

public class MainActivity extends FragmentActivity  {

	/**
	 * Attributs de la view
	 */
	//private ArrayList<String> m_Tokens = new ArrayList<String>();
	//private ArrayList<RowModel> m_RowModels = new ArrayList<RowModel>();
	//private LigneAdapter m_adapter;
	
	/**
	 * Création de la view
	 */
	
	private DrawerLayout mDrawer;
	private ListView mLeftDrawerList;
	private String[] mLeftMenuItems;
	//private CustomActionBarDrawerToggle mLeftDrawerToggle;	


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i("test","onCreate");
		
		initLeftMenu();
		
		/* Affichage d'un horaire
		setContentView(R.layout.horaire);
		m_adapter = new LigneAdapter();
		this.setListAdapter(m_adapter);
		
		m_Tokens.add("École");
		m_Tokens.add("Travail");
		
		for (String token : m_Tokens) {
			m_RowModels.add(new RowModel(token, false));
		}
		*/
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		Log.i("test","onResume");
		initLeftMenu();
	}

	@Override
	protected void onStart() {
		super.onStart();
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
	
	

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	private void initLeftMenu(){
		this.mLeftMenuItems = getResources().getStringArray(R.array.left_menu_items);
		this.mDrawer = (DrawerLayout)findViewById(R.id.drawer_layout);
		this.mLeftDrawerList = (ListView) findViewById(R.id.left_drawer);
		
		this.mLeftDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.mLeftMenuItems));
		
		this.mLeftDrawerList.setOnItemClickListener(new DrawerItemClickListener());
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView parent, View view, int position,long id) {
			
			if(position == 0){
				setContentView(R.layout.activity_main);
			}
			else if (position == 1){
				setContentView(R.layout.activity_connection);
			}
			
		}
	}
}


