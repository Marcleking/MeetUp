package com.bouchardm.meetup;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.bouchardm.meetup.classes.Ami;
import com.bouchardm.meetup.classes.Personne;
import com.bouchardm.meetup.classes.network;
import com.bouchardm.meetup.sqlite.PersonneDataSource;

import android.os.Build;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class CreationMeetUp extends Activity implements OnDateSetListener, OnTimeSetListener{

	private final static int AJOUT_AMIS = 0;
	
	private Calendar m_dateDebut = Calendar.getInstance();
	private Calendar m_dateFin = Calendar.getInstance();
	private DateFormat m_DateFormat = DateFormat.getDateInstance();
	private DateFormat m_TimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
	private boolean debut;
	
	private String googleId;
	
	private String nomEvenement;
	private String lieuEvenement;
	private String duree;
	private String dateDebut;
	private String dateFin;
	private String heureDebut;
	private String heureFin;
	//private ArrayList<Ami> amis;
	private ArrayList<String> amis; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meet_up_creation);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			googleId = extras.getString("EXTRA_USER_ID");
		}
		
		m_dateDebut.set(Calendar.MINUTE, 0);
		m_dateFin.set(Calendar.MINUTE, 0);
		
		amis = null;
	}
	
	public void chercherAmis(View source)
	{
		this.startActivityForResult(new Intent(this, ListeAmis.class)
			.putExtra("EXTRA_USER_ID", googleId)
			.putExtra("EXTRA_MEETUP_AMI", amis)
			,AJOUT_AMIS );
	}
	
	public void onActivityResult(int p_requestCode, int p_resultCode, Intent p_data){
		switch(p_requestCode){
		case AJOUT_AMIS:
			if(p_resultCode == RESULT_OK){
				if(p_data.hasExtra("EXTRA_MEETUP_AMI")){
					amis = p_data.getExtras().getStringArrayList("EXTRA_MEETUP_AMI");
				}
			}
			break;
		}
	}
	
	public void creerEvenement(View source)
	{
		EditText eventName = (EditText)this.findViewById(R.id.nomEvenement);
		EditText eventLocation = (EditText)this.findViewById(R.id.lieuEvenement);
		EditText eventDuration = (EditText)this.findViewById(R.id.dureeEvenement);
		nomEvenement = eventName.getText().toString();
		lieuEvenement = eventLocation.getText().toString();
		duree = eventDuration.getText().toString();
		
		Log.i("Création MeetUp", "Nom : " + nomEvenement);
		Log.i("Création MeetUp", "Lieu : " + lieuEvenement);
		Log.i("Création MeetUp", "Duree : " + duree);
		Log.i("Création MeetUp", "Date debut : " + dateDebut);
		Log.i("Création MeetUp", "Heure debut : " + heureDebut);
		Log.i("Création MeetUp", "Date fin : " + dateFin);
		Log.i("Création MeetUp", "Heure fin : " + heureFin);
		
		if(		dateDebut != null && heureDebut != null && 
				dateFin != null && heureFin != null && 
				nomEvenement != null && nomEvenement.trim() != "" &&
				lieuEvenement != null && lieuEvenement.trim() != "" &&
				duree != null ){
			
			PersonneDataSource dataSource = new PersonneDataSource(this);
			dataSource.open();
			Personne usager = dataSource.getPersonne(googleId);
			dataSource.close();
			try{
				network.AsyncAddMeetUp ajoutMeetUp = new network.AsyncAddMeetUp();
				ajoutMeetUp.setOwner(usager.get_googleId());
				ajoutMeetUp.setSecurityNumber(usager.get_securityNumber());
				ajoutMeetUp.setName(nomEvenement);
				ajoutMeetUp.setLocation(lieuEvenement);
				ajoutMeetUp.setDuration(duree);
				ajoutMeetUp.setLowerDate(dateDebut);
				ajoutMeetUp.setLowerTime(heureDebut.split(":")[0]);
				ajoutMeetUp.setUpperDate(dateFin);
				ajoutMeetUp.setUpperTime(heureFin.split(":")[0]);
			
				String meetUpKey = ajoutMeetUp.execute((Void)null).get();
				
				if(amis != null){
					for(String ami : amis){
						network.AsyncAddFriendToMeetUp ajoutAmi = new network.AsyncAddFriendToMeetUp();
						ajoutAmi.setOwner(usager.get_googleId());
						ajoutAmi.setSecurityNumber(usager.get_securityNumber());
						ajoutAmi.setFriendId(ami);
						ajoutAmi.setMeetUpKey(meetUpKey);
						
						ajoutAmi.execute((Void)null);
					}
				}
			}
			catch(Exception e){}
			
			
			this.finish();
		}	
	}
	
	@SuppressLint("NewApi")
	public void onClickDate(View p_source){
		debutOuFin(p_source.getId());
		
		int annee = Calendar.getInstance().get(Calendar.YEAR);
		int mois = Calendar.getInstance().get(Calendar.MONTH);
		int jour = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		
		Calendar dateMinPossible = Calendar.getInstance();
		
		switch(p_source.getId()){
		case R.id.dateDebut:
			if(this.dateDebut != null && this.dateDebut != ""){
				annee = Integer.parseInt(this.dateDebut.split("-")[0]);
				mois = Integer.parseInt(this.dateDebut.split("-")[1]) - 1;
				jour = Integer.parseInt(this.dateDebut.split("-")[2]);
			}
			break;
		case R.id.dateFin:
			if(this.dateFin != null && this.dateFin != ""){
				annee = Integer.parseInt(this.dateFin.split("-")[0]);
				mois = Integer.parseInt(this.dateFin.split("-")[1]) - 1;
				jour = Integer.parseInt(this.dateFin.split("-")[2]);
			}
			dateMinPossible = this.m_dateDebut;
			break;
		}
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			DatePickerDialog picker = new DatePickerDialog(this, this, annee, mois,jour);
			picker.getDatePicker().setMinDate(dateMinPossible.getTimeInMillis());
			picker.show();
		}
		else
		{
			// TODO : Gestion de date minimum pour < API 11
			new DatePickerDialog(this, this, annee, mois,jour).show();
		}
	}
	
	public void onClickTime(View p_source){
		debutOuFin(p_source.getId());
		
		int heure = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int minute = 0;
		
		switch(p_source.getId()){
		case R.id.heureDebut:
			if(this.heureDebut != null && this.heureDebut != "" ){
				heure = Integer.parseInt(this.heureDebut.split(":")[0]);
				minute = Integer.parseInt(this.heureDebut.split(":")[1]);
			}
			break;
		case R.id.heureFin:
			if(this.heureFin != null && this.heureFin != "" ){
				heure = Integer.parseInt(this.heureFin.split(":")[0]);
				minute = Integer.parseInt(this.heureFin.split(":")[1]);
			}
			break;
		}
		
		// TODO : Gestion des cas d'exception pour temps minimum > temps maximum et vice versa
		boolean is24HourFormat = android.text.format.DateFormat.is24HourFormat(this);
		new TimePickerDialog(
				this,this,
				heure,
				minute,
				is24HourFormat).show();
	}

	@Override
	public void onTimeSet(TimePicker p_view, int p_heure, int p_minutes) {
		if(debut){
			m_dateDebut.set(Calendar.HOUR_OF_DAY, p_heure);
			m_dateDebut.set(Calendar.MINUTE, 0);
			this.heureDebut = m_TimeFormat.format(m_dateDebut.getTime());
			updateButtonText(R.id.heureDebut,this.heureDebut);
		}
		else
		{
			m_dateFin.set(Calendar.HOUR_OF_DAY, p_heure);
			m_dateFin.set(Calendar.MINUTE, 0);
			this.heureFin = m_TimeFormat.format(m_dateFin.getTime());
			updateButtonText(R.id.heureFin,this.heureFin);
		}
	}

	@Override
	public void onDateSet(DatePicker p_view, int p_year, int p_month, int p_day) {
		if(debut){
			m_dateDebut.set(Calendar.YEAR, p_year);
			m_dateDebut.set(Calendar.MONTH, p_month);
			m_dateDebut.set(Calendar.DAY_OF_MONTH,p_day);
			this.dateDebut = m_DateFormat.format(m_dateDebut.getTime());
			updateButtonText(R.id.dateDebut,this.dateDebut);
		}
		else{
			m_dateFin.set(Calendar.YEAR, p_year);
			m_dateFin.set(Calendar.MONTH, p_month);
			m_dateFin.set(Calendar.DAY_OF_MONTH,p_day);
			this.dateFin = m_DateFormat.format(m_dateFin.getTime());
			updateButtonText(R.id.dateFin,this.dateFin);
		}
	}
	
	private void updateButtonText(int buttonId, String texte){
		Button bouton = (Button)this.findViewById(buttonId);
		bouton.setText(texte);
	}
	
	private void debutOuFin(int id_source){
		
		if(id_source == R.id.dateDebut || id_source == R.id.heureDebut){
			debut = true;
		}
		else if(id_source == R.id.dateFin || id_source == R.id.heureFin){
			debut = false;
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString("dateDebut", m_DateFormat.format(m_dateDebut.getTime()));
		savedInstanceState.putString("heureDebut", m_TimeFormat.format(m_dateDebut.getTime()));
		savedInstanceState.putString("dateFin", m_DateFormat.format(m_dateFin.getTime()));
		savedInstanceState.putString("heureFin", m_TimeFormat.format(m_dateFin.getTime()));
		savedInstanceState.putStringArrayList("amis", this.amis);
	}
	
	public void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		
		this.amis = savedInstanceState.getStringArrayList("amis");
		
		m_dateDebut.set(
				Integer.parseInt(savedInstanceState.getString("dateDebut").split("-")[0]), 
				Integer.parseInt(savedInstanceState.getString("dateDebut").split("-")[1]) - 1, 
				Integer.parseInt(savedInstanceState.getString("dateDebut").split("-")[2]), 
				Integer.parseInt(savedInstanceState.getString("heureDebut").split(":")[0]), 
				Integer.parseInt(savedInstanceState.getString("heureDebut").split(":")[1]));
		
		m_dateFin.set(
				Integer.parseInt(savedInstanceState.getString("dateFin").split("-")[0]), 
				Integer.parseInt(savedInstanceState.getString("dateFin").split("-")[1]) - 1, 
				Integer.parseInt(savedInstanceState.getString("dateFin").split("-")[2]), 
				Integer.parseInt(savedInstanceState.getString("heureFin").split(":")[0]), 
				Integer.parseInt(savedInstanceState.getString("heureFin").split(":")[1]));
		
		this.dateDebut = savedInstanceState.getString("dateDebut");
		this.dateFin = savedInstanceState.getString("dateFin");
		this.heureDebut = savedInstanceState.getString("heureDebut");
		this.heureFin = savedInstanceState.getString("heureFin");
		
		updateButtonText(R.id.dateDebut,savedInstanceState.getString("dateDebut"));
		updateButtonText(R.id.heureDebut,savedInstanceState.getString("heureDebut"));
		updateButtonText(R.id.dateFin,savedInstanceState.getString("dateFin"));
		updateButtonText(R.id.heureFin,savedInstanceState.getString("heureFin"));
	}
}
