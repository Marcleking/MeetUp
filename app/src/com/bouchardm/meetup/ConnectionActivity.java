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

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusClient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ConnectionActivity extends SherlockFragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, View.OnClickListener {

	private static final String TAG = "android-plus-quickstart";

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
	private Button mSignOutButton;

	private DrawerLayout mDrawer;
	private ListView mLeftDrawerList;
	private String[] mLeftMenuItems;
	private ActionBarDrawerToggle mLeftDrawerToggle;
	private CharSequence mTitle;
	private CharSequence mDrawerTitle;
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
			selectItem(position);
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

		if (savedInstanceState != null) {
			mSignInProgress = savedInstanceState.getInt(SAVED_PROGRESS,
					STATE_DEFAULT);
		}
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
		
		switch(position){
		// Horaire
		case 1:
			this.startActivity(new Intent(this, Horaire.class));
			break;
		// MeetUp
		case 2:
			this.startActivity(new Intent(this, MeetUp.class));
			break;
		// Amis
		case 3:
			this.startActivity(new Intent(this, Amis.class));
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

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(SAVED_PROGRESS, mSignInProgress);
	}
	
	private void initMainActivity(){
		mDrawer = (DrawerLayout)findViewById(R.id.drawer_layout);
		mDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		initLeftMenu();
		
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
	}
	
	private void initLeftMenu() {
		this.mLeftMenuItems = getResources().getStringArray(
				R.array.left_menu_items);
		String name;
		if(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getName().hasFormatted())
			name = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getName().getFormatted();
		else
			name = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getDisplayName();
		this.mLeftMenuItems[0] = name;
		this.mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		this.mLeftDrawerList = (ListView) findViewById(R.id.left_drawer);
		
		this.mLeftDrawerList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, this.mLeftMenuItems));
		this.mLeftDrawerList.setOnItemClickListener(new DrawerItemClickListener());
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

		// this.startActivity(new Intent(this, MainActivity.class));
		setContentView(R.layout.activity_main);
		initMainActivity();
		// Indicate that the sign in process is complete.
		mSignInProgress = STATE_DEFAULT;
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
}
