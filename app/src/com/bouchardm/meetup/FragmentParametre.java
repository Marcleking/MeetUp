package com.bouchardm.meetup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bouchardm.meetup.classes.Personne;
import com.bouchardm.meetup.classes.network;
import com.bouchardm.meetup.util.AsyncHttpGet;
import com.bouchardm.meetup.sqlite.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class FragmentParametre extends Fragment implements View.OnClickListener, OnTimeSetListener {
	
	private String witchTimeSet;
	private Button btnHeureMin;
	private Button btnHeureMax;
	private Button btnReadNotification;
	private View rootView;
	
	private Personne usager;
	
	public GoogleApiClient mGoogleApiClient;
	
	// Identifiant unique pour la notification.
	private static final int ID_NOTIF = 124325;
	
	// Clé pour l'information attachée à l'intention.
	public static final String EXTRA_INFO = "message";

	// Gestionnaire de notifications.
	private NotificationManager notifMgr;
	
	// Pour récupérer le texte de la notification.
	private EditText txtNotif;
	
	
	
	public GoogleApiClient getmGoogleApiClient() {
		return mGoogleApiClient;
	}

	public void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
		this.mGoogleApiClient = mGoogleApiClient;
	}

	public FragmentParametre(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.parametre, container, false);

		PersonneDataSource dataSource = new PersonneDataSource(getActivity());
		dataSource.open();
		usager = dataSource.getPersonne(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getId());
		dataSource.close();
		
		btnHeureMin = (Button) rootView.findViewById(R.id.btnHeureMin);
		btnHeureMin.setOnClickListener(this);
		btnHeureMin.setText(String.valueOf(usager.getM_heureMin())+":00");
		
		btnHeureMax = (Button) rootView.findViewById(R.id.btnHeureMax);
		btnHeureMax.setOnClickListener(this);
		btnHeureMax.setText(String.valueOf(usager.getM_heureMax())+":00");
        
		btnReadNotification = (Button) rootView.findViewById(R.id.btnReadNotification);
		btnReadNotification.setOnClickListener(this);
		
		this.notifMgr = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		
		
		return rootView;
	}

	
    
    
    @Override
    public void onClick(View v){
    	switch (v.getId()) {
	    	case R.id.btnHeureMin:
	    		clickHeureMin(v);
	    		break;
	    	case R.id.btnHeureMax:
	    		clickHeureMax(v);
	    		break;
	    	case R.id.btnReadNotification:
	    		clickReadNotification(v);
	    		break;
    	}
    }
    
    public void clickHeureMin(View v) {
    	witchTimeSet = "min";
    	new TimePickerDialog(getActivity(),this,
    			usager.getM_heureMin(),
    			0,
    			true).show();
    }
    
    public void clickHeureMax(View v) {
    	witchTimeSet = "max";
    	new TimePickerDialog(getActivity(),this,
    			usager.getM_heureMax(),
    			0,
    			true).show();
    }
    
    @SuppressWarnings("deprecation")
	public void clickReadNotification(View v) {
    	// Texte dans "status bar", titre et texte de la notification.
//    	try {
//			String statusBarNotif = "statutBaR";
//			String titreNotif = "titre notification";
//			String texteNotif = "meetUp";
//	
//			// Création d'un nouvelle notification.
//			Notification notif = new Notification(R.drawable.ic_launcher, statusBarNotif, System.currentTimeMillis());
//			// Pour faire disparaître la notification lorsque l'utilisateur la clique.
//			notif.flags |= Notification.FLAG_AUTO_CANCEL;
//			
//			// Création d'une intention de retour lorsqu'on clique sur la notification.
//			Intent i = new Intent(getActivity(), ConnectionActivity.class);
//			// Ajout d'information dans l'intention.
//			i.putExtra(EXTRA_INFO, texteNotif);
//			// Création d'une nouvelle intention en suspens.
//			PendingIntent pi = PendingIntent.getActivity(getActivity(), 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
//
//			// Configuration de la notification.
//			notif.setLatestEventInfo(getActivity(), titreNotif, texteNotif, pi);
//			// Envoie de la notification.
//			this.notifMgr.notify(ID_NOTIF, notif);
//    	} catch (Exception e) {
//    		Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
//    	}
    	
    	new AsyncHttpGet().execute("http://appmeetup.appspot.com/add-notif?username="+usager.get_googleId()+"&notif=Notification!");
    	
    	
    	
    	
    }

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		if (witchTimeSet == "min") {
			btnHeureMin.setText(String.valueOf(hourOfDay) + ":00");
			usager.setM_heureMin(hourOfDay);
		} else if (witchTimeSet == "max") {
			btnHeureMax.setText(String.valueOf(hourOfDay) + ":00");
			usager.setM_heureMax(hourOfDay);
		}
		usager.update(getActivity());
	}
   

}
