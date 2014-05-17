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
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;

import android.content.Context;
import android.content.DialogInterface;

import android.support.v4.app.Fragment;
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
	private View rootView;
	
	private Personne usager;
	
	public GoogleApiClient mGoogleApiClient;
	
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
