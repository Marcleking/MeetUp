package com.bouchardm.meetup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;

public class MainActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener,
View.OnClickListener {

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
	private ListView mRightDrawerList;
	private String[] mRightMenuItems;
	//private CustomActionBarDrawerToggle mLeftDrawerToggle;	

	private GoogleApiClient mGoogleApiClient;
	
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i("test","onCreate");
		
		initLeftMenu();
		
		mGoogleApiClient = buildGoogleApiClient();
		
		/* Affichage d'un horaire
		setContentView(R.layout.horaire);
		m_adapter = new LigneAdapter();
		this.setListAdapter(m_adapter);1
		
		m_Tokens.add("École");
		m_Tokens.add("Travail");
		
		for (String token : m_Tokens) {
			m_RowModels.add(new RowModel(token, false));
		}
		*/
	}
	
	private GoogleApiClient buildGoogleApiClient() {
	    // When we build the GoogleApiClient we specify where connected and
	    // connection failed callbacks should be returned, which Google APIs our
	    // app uses and which OAuth 2.0 scopes our app requests.
	    return new GoogleApiClient.Builder(this)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .addApi(Plus.API, null)
	        .addScope(Plus.SCOPE_PLUS_LOGIN)
	        .build();
	  }
	
	@Override
	protected void onResume(){
		super.onResume();
		Log.i("test","onResume");
		initLeftMenu();
		initRightMenu();
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
	
	public void disconnect(View source){
		mGoogleApiClient.disconnect();
		this.startActivity(new Intent(this, ConnectionActivity.class));
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
	}
	
	private void initRightMenu(){
		this.mRightMenuItems = getResources().getStringArray(R.array.right_menu_items);
		this.mDrawer = (DrawerLayout)findViewById(R.id.drawer_layout);
		this.mRightDrawerList = (ListView) findViewById(R.id.right_drawer);
		
		this.mRightDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.mRightMenuItems));
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
}


