/**
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bouchardm.meetup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.bouchardm.meetup.classes.Personne;
import com.bouchardm.meetup.classes.network;
import com.bouchardm.meetup.service.NotificationService;
import com.bouchardm.meetup.sqlite.PersonneDataSource;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.google.api.services.calendar.*;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.IntentSender.SendIntentException;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Contacts.People;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ConnectionActivity extends SherlockFragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, View.OnClickListener {

	private static final String TAG = "android-plus-quickstart";

    private NotificationService.WordServiceBinder m_Binder;

	private static final int STATE_DEFAULT = 0;
	private static final int STATE_SIGN_IN = 1;
	private static final int STATE_IN_PROGRESS = 2;

	private static final int RC_SIGN_IN = 0;

	private static final int DIALOG_PLAY_SERVICES_ERROR = 0;

	private static final String SAVED_PROGRESS = "sign_in_progress";

	// GoogleApiClient wraps our service connection to Google Play services and
	// provides access to the users sign in state and Google's APIs.
	private GoogleApiClient mGoogleApiClient;

	// We use mSignInProgress to track whether user has clicked sign in.
	// mSignInProgress can be one of three values:
	//
	// STATE_DEFAULT: The default state of the application before the user
	// has clicked 'sign in', or after they have clicked
	// 'sign out'. In this state we will not attempt to
	// resolve sign in errors and so will display our
	// Activity in a signed out state.
	// STATE_SIGN_IN: This state indicates that the user has clicked 'sign
	// in', so resolve successive errors preventing sign in
	// until the user has successfully authorized an account
	// for our app.
	// STATE_IN_PROGRESS: This state indicates that we have started an intent to
	// resolve an error, and so we should not start further
	// intents until the current intent completes.
	private int mSignInProgress;

	// Used to store the PendingIntent most recently returned by Google Play
	// services until the user clicks 'sign in'.
	private PendingIntent mSignInIntent;

	// Used to store the error code most recently returned by Google Play
	// services
	// until the user clicks 'sign in'.
	private int mSignInError;

	private SignInButton mSignInButton;
	
	private DrawerLayout mDrawer;
	private ListView mLeftDrawerList;
	private ListView mRightDrawerList;
	private String[] mLeftMenuItems;
	private String[] mRightMenuItems;
	private ActionBarDrawerToggle mLeftDrawerToggle;
	private ActionBarDrawerToggle mRightDrawerToggle;
	private CharSequence mTitle;
	private CharSequence mDrawerTitle;
	
	private Person user;
	private Personne usager;
	
	private Fragment activeFragment;
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
			selectItem(position);
		}
	}
	
	private class DrawerItemClickListenerRight implements ListView.OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
			filtreAmi(position);
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mGoogleApiClient = buildGoogleApiClient();
		
		if (!mGoogleApiClient.isConnected()) {
			setContentView(R.layout.activity_connection);
		} else {
			setContentView(R.layout.activity_main);
		}
		
		mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
		mSignInButton.setOnClickListener(this);
	}
	
	

	private GoogleApiClient buildGoogleApiClient() {
		// When we build the GoogleApiClient we specify where connected and
		// connection failed callbacks should be returned, which Google APIs our
		// app uses and which OAuth 2.0 scopes our app requests.
		return new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API, null)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
		
	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}
	
	private void selectItem(int position){
		FragmentManager fragmentManager;
		switch(position){
		case 0:
			Fragment accueilFragment = new FragmentAccueil();
			activeFragment = accueilFragment;
			((FragmentAccueil) accueilFragment).setmGoogleApiClient(mGoogleApiClient);
			fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame,accueilFragment,"fragment").commit();
			break;
		// Horaire
		case 1:
			Fragment horaireFragment = new FragmentHoraire();
			((FragmentHoraire)horaireFragment).setmGoogleApiClient(mGoogleApiClient);
			fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame,horaireFragment,"fragment").commit();
			//this.startActivity(new Intent(this, Horaire.class));
			break;
		// MeetUp
		case 2:
			Fragment meetUpFragment = new FragmentMeetUp();
			((FragmentMeetUp)meetUpFragment).setUsager(usager);
			fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame,meetUpFragment,"fragment").commit();
			//this.startActivity(new Intent(this, MeetUp.class));
			break;
		// Amis
		case 3:
			//this.startActivity(new Intent(this, Amis.class));
			Fragment friendFragment = new FragmentAmis();
			((FragmentAmis) friendFragment).setmGoogleApiClient(mGoogleApiClient);
			fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, friendFragment,"fragment").commit();
			break;
		// Param�tre
		 case 4:
		 	friendFragment = new FragmentParametre();
		 	((FragmentParametre) friendFragment).setmGoogleApiClient(mGoogleApiClient);
		 	fragmentManager = getSupportFragmentManager();
		 	fragmentManager.beginTransaction().replace(R.id.content_frame, friendFragment,"fragment").commit();
			
			break;
		// Déconnexion
		case 5:
			
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();
			break;
		}
		
		mDrawer.closeDrawer(mLeftDrawerList);
	}
	
	private void filtreAmi(int position) {
		Fragment horaireFragment = new FragmentAccueil();
		
		// on ajoute l'ami � filtr�
		Bundle bundle = new Bundle();
		bundle.putString("filtre", mRightMenuItems[position]);
		horaireFragment.setArguments(bundle);
		
		activeFragment = horaireFragment;
		((FragmentAccueil) horaireFragment).setmGoogleApiClient(mGoogleApiClient);
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame,horaireFragment,"fragment").commit();
		
		mDrawer.closeDrawer(mRightDrawerList);
	}
	
	@Override
	public void onClick(View v) {
		if (!mGoogleApiClient.isConnecting()) {
			// We only process button clicks when GoogleApiClient is not
			// transitioning
			// between connected and not connected.
			switch (v.getId()) {
			case R.id.sign_in_button:
				resolveSignInError();
				break;
			}
		}
	}
	
	private void initMainActivity(){
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
		
		mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		initLeftMenu();
		
		new AsyncHttpGetListAmi().execute(usager.get_googleId());
		
		
		Fragment horaireFragment = new FragmentAccueil();
		activeFragment = horaireFragment;
		((FragmentAccueil) horaireFragment).setmGoogleApiClient(mGoogleApiClient);
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame,horaireFragment,"fragment").commit();
		
		mTitle = mDrawerTitle = getTitle();
		mLeftDrawerToggle = new ActionBarDrawerToggle(
				this,
				mDrawer,
				R.drawable.ic_drawer,
				R.string.drawer_open,
				R.string.drawer_close
				){
			public void onDrawerClosed(View view){
				getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}	
			public void onDrawerOpened(View view){
				getSupportActionBar().setTitle(mDrawerTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		
		// cr�ation du service
		
		
		Intent serviceNotification = new Intent(this, NotificationService.class);
		serviceNotification.putExtra("idGoogle", usager.get_googleId());
		serviceNotification.putExtra("passwordGoogle", usager.get_securityNumber());
		this.getApplicationContext().startService(serviceNotification);
		
		if (m_Binder != null) {
			m_Binder.requestToStart();
		}
	}
	
	private void initLeftMenu() {
		this.mLeftMenuItems = getResources().getStringArray(
				R.array.left_menu_items);
		String name;
		if(user.getName().hasFormatted())
			name = user.getName().getFormatted();
		else
			name = user.getDisplayName();
		this.mLeftMenuItems[0] = name;
		this.mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		this.mLeftDrawerList = (ListView) findViewById(R.id.left_drawer);
		
		this.mLeftDrawerList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, this.mLeftMenuItems));
		this.mLeftDrawerList.setOnItemClickListener(new DrawerItemClickListener());
	}
	
	private void initRightMenu(String[] listAmi) {
		if(listAmi != null && listAmi.length > 0){
			this.mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
			this.mRightDrawerList = (ListView) findViewById(R.id.right_drawer);
			
			this.mRightDrawerList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, listAmi));
			this.mRightDrawerList.setOnItemClickListener(new DrawerItemClickListenerRight());
		}
	}
	
	/**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if(mGoogleApiClient.isConnected()){
        	mLeftDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mLeftDrawerToggle.onConfigurationChanged(newConfig);
    }
	
	/*
	 * onConnected is called when our Activity successfully connects to Google
	 * Play services. onConnected indicates that an account was selected on the
	 * device, that the selected account has granted any requested permissions
	 * to our app and that we were able to establish a service connection to
	 * Google Play services.
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		// Reaching onConnected means we consider the user signed in.
		Log.i(TAG, "onConnected");
		user = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
		
		String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
		
		// V�rification de l'existance de l'usager dans la BD locale

		PersonneDataSource dataSource = new PersonneDataSource(this);
		dataSource.open();
		
		if(!dataSource.userExisting(email)){
			Log.i(TAG,"Ajout de l'utilisateur");
			
			Personne nouvelUsager = new Personne(email);
			
			// Ajout de l'usager dans la BD locale
			int newId = dataSource.insert(nouvelUsager);
			
			Log.i(TAG,"New id : " + newId);
			
			// Ajout de l'usager dans le WebService
			network.AsyncAddUser ajoutUsager = new network.AsyncAddUser();
			ajoutUsager.setP_user(user);
			ajoutUsager.setEmail(email);
			ajoutUsager.setP_sha1(nouvelUsager.get_securityNumber());
			ajoutUsager.execute((Void)null);
		}
		
		
		usager = dataSource.getPersonne(email);
		dataSource.close();
		
		// this.startActivity(new Intent(this, MainActivity.class));
		setContentView(R.layout.activity_main);
		
		initMainActivity();
		// Indicate that the sign in process is complete.
		mSignInProgress = STATE_DEFAULT;
	}
	
	/*
	 * onConnectionFailed is called when our Activity could not connect to
	 * Google Play services. onConnectionFailed indicates that the user needs to
	 * select an account, grant permissions or resolve an error in order to sign
	 * in.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Refer to the javadoc for ConnectionResult to see what error codes
		// might
		// be returned in onConnectionFailed.
		Log.i(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
				+ result.getErrorCode());

		if (mSignInProgress != STATE_IN_PROGRESS) {
			// We do not have an intent in progress so we should store the
			// latest
			// error resolution intent for use when the sign in button is
			// clicked.
			mSignInIntent = result.getResolution();
			mSignInError = result.getErrorCode();

			if (mSignInProgress == STATE_SIGN_IN) {
				// STATE_SIGN_IN indicates the user already clicked the sign in
				// button
				// so we should continue processing errors until the user is
				// signed in
				// or they click cancel.
				resolveSignInError();
			}
		}

		// In this sample we consider the user signed out whenever they do not
		// have
		// a connection to Google Play services.
		onSignedOut();
	}

	/*
	 * Starts an appropriate intent or dialog for user interaction to resolve
	 * the current error preventing the user from being signed in. This could be
	 * a dialog allowing the user to select an account, an activity allowing the
	 * user to consent to the permissions being requested by your app, a setting
	 * to enable device networking, etc.
	 */
	private void resolveSignInError() {
		if (mSignInIntent != null) {
			// We have an intent which will allow our user to sign in or
			// resolve an error. For example if the user needs to
			// select an account to sign in with, or if they need to consent
			// to the permissions your app is requesting.

			try {
				// Send the pending intent that we stored on the most recent
				// OnConnectionFailed callback. This will allow the user to
				// resolve the error currently preventing our connection to
				// Google Play services.
				mSignInProgress = STATE_IN_PROGRESS;
				startIntentSenderForResult(mSignInIntent.getIntentSender(),
						RC_SIGN_IN, null, 0, 0, 0);
			} catch (SendIntentException e) {
				Log.i(TAG,
						"Sign in intent could not be sent: "
								+ e.getLocalizedMessage());
				// The intent was canceled before it was sent. Attempt to
				// connect to
				// get an updated ConnectionResult.
				mSignInProgress = STATE_SIGN_IN;
				mGoogleApiClient.connect();
			}
		} else {
			// Google Play services wasn't able to provide an intent for some
			// error types, so we show the default Google Play services error
			// dialog which may still start an intent on our behalf if the
			// user can resolve the issue.
			showDialog(DIALOG_PLAY_SERVICES_ERROR);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case RC_SIGN_IN:
			if (resultCode == RESULT_OK) {
				// If the error resolution was successful we should continue
				// processing errors.
				mSignInProgress = STATE_SIGN_IN;
			} else {
				// If the error resolution was not successful or the user
				// canceled,
				// we should stop processing errors.
				mSignInProgress = STATE_DEFAULT;
			}

			if (!mGoogleApiClient.isConnecting()) {
				// If Google Play services resolved the issue with a dialog then
				// onStart is not called so we need to re-attempt connection
				// here.
				mGoogleApiClient.connect();
			}
			break;
		}
	}

	private void onSignedOut() {
		Log.i(TAG, "onSignedOut");
		
		setContentView(R.layout.activity_connection);

		mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
		mSignInButton.setOnClickListener(this);
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// The connection to Google Play services was lost for some reason.
		// We call connect() to attempt to re-establish the connection or get a
		// ConnectionResult that we can attempt to resolve.
		mGoogleApiClient.connect();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_PLAY_SERVICES_ERROR:
			if (GooglePlayServicesUtil.isUserRecoverableError(mSignInError)) {
				return GooglePlayServicesUtil.getErrorDialog(mSignInError,
						this, RC_SIGN_IN,
						new DialogInterface.OnCancelListener() {
							@Override
							public void onCancel(DialogInterface dialog) {
								Log.e(TAG,
										"Google Play services resolution cancelled");
								mSignInProgress = STATE_DEFAULT;
							}
						});
			} else {
				return new AlertDialog.Builder(this)
						.setMessage(R.string.play_services_error)
						.setPositiveButton(R.string.close,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Log.e(TAG,
												"Google Play services error could not be "
														+ "resolved: "
														+ mSignInError);
										mSignInProgress = STATE_DEFAULT;
									}
								}).create();
			}
		default:
			return super.onCreateDialog(id);
		}
	}
	
	public class AsyncHttpGetListAmi extends AsyncTask<String, Void, ArrayList<String>> {
	    public String getHttpRequest(String url){
	        InputStream inputStream = null;
	        String reponse = "";
	        try {
	            HttpClient httpclient = new DefaultHttpClient();
	            
	            HttpResponse response = httpclient.execute(new HttpGet(url));
	            inputStream = response.getEntity().getContent();
	            reponse = convertInputStreamToString(inputStream);
	 
	        } catch (Exception e) {
	        	reponse = e.getMessage();
	        }
	 
	        return reponse;
	    }
		
	    @Override
	    protected ArrayList<String> doInBackground(String... id) {
	    	
	    	ArrayList<String> reponse = new ArrayList<String>();
	    	
	    	for (int i = 0; i < id.length; i++) {
	    		reponse.add(getHttpRequest("http://www.appmeetup.appspot.com/get-friends?username="+id[i]+"&withInfo=1"));
	    	}
	    	
	        return reponse;
	    }
	    
	    @Override
	    protected void onPostExecute(ArrayList<String> result) {
	    	String[] listeAmi = null;
	    	String[] usernameAmi = null;
	    	for (int i = 0; i < result.size(); i++) {
	    		try {
					JSONObject reponse = new JSONObject(result.get(i));
					JSONArray listeAmiReponse = reponse.getJSONArray("amis");
					
					listeAmi = new String[listeAmiReponse.length()];
					usernameAmi = new String[listeAmiReponse.length()];
					
					for (int j = 0; j < listeAmiReponse.length(); j++) {
						JSONObject unAmi = listeAmiReponse.getJSONObject(j);
						listeAmi[i] = unAmi.getString("prenom") +" "+ unAmi.getString("nom");
						usernameAmi[i] = unAmi.getString("username");
					}
					
				} catch (JSONException e) {}
	    		
	    	}
	    	
	    	mRightMenuItems = usernameAmi;
	    	initRightMenu(listeAmi);
	    }
	}
	
	public String convertInputStreamToString(InputStream inputStream) throws IOException{
	    BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
	    String line = "";
	    String result = "";
	    while((line = bufferedReader.readLine()) != null)
	        result += line;
	
	    inputStream.close();
	    return result;
	
	}
	
	 private class WordServiceConnection implements ServiceConnection{
	    	
		@Override	
		public void onServiceConnected(ComponentName p_name, IBinder p_service) {
			// Interface publique du service.
			m_Binder = (NotificationService.WordServiceBinder) p_service;
		}
		@Override  
		public void onServiceDisconnected(ComponentName p_name) {
			m_Binder = null;
		}
    }
}
