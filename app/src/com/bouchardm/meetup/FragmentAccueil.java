package com.bouchardm.meetup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bouchardm.meetup.classes.Personne;
import com.bouchardm.meetup.sqlite.PersonneDataSource;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentAccueil extends ListFragment implements View.OnClickListener {
	/**
	 * Attributs de la view
	 */
	private ArrayList<String> m_Tokens = new ArrayList<String>();
	private ArrayList<RowModel> m_RowModels = new ArrayList<RowModel>();
	private LigneAdapter m_adapter;
	private String m_filtre = "";
	private View rootView;
	private Personne usager;
	
	public GoogleApiClient mGoogleApiClient;
	
	public GoogleApiClient getmGoogleApiClient() {
		return mGoogleApiClient;
	}

	public void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
		this.mGoogleApiClient = mGoogleApiClient;
	}
	public FragmentAccueil(){}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.accueil, container, false);
		
		PersonneDataSource dataSource = new PersonneDataSource(getActivity());
		dataSource.open();
		usager = dataSource.getPersonne(Plus.AccountApi.getAccountName(mGoogleApiClient));
		dataSource.close();
		
		Bundle bundle = this.getArguments();
		if(bundle != null){
			m_filtre = (String)bundle.get("filtre");
			Button btnShowAll = (Button)rootView.findViewById(R.id.afficherTout);
			btnShowAll.setOnClickListener(this);
			btnShowAll.setVisibility(View.VISIBLE);
		}
	
		m_adapter = new LigneAdapter();
		this.setListAdapter(m_adapter);
		
		new AsyncGetCalendarsFriends().execute(usager.get_googleId());
		
		return rootView;
	}
	
	/**
	 * Gestion de l'enregistrement des données (lors de la rotation)
	 */
	@Override
	public void onSaveInstanceState(Bundle p_outState) 
	{
		super.onSaveInstanceState(p_outState);
		p_outState.putParcelableArrayList("rowModel", m_RowModels);
		p_outState.putStringArrayList("token", m_Tokens);
	}
	
	/**
	 * Gestion de la restauration des données (lors de la rotation)
	 */
	public void onRestoreInstanceState(Bundle p_state) 
	{
		if (p_state != null) {
			m_RowModels = p_state.getParcelableArrayList("rowModel");
			m_Tokens = p_state.getStringArrayList("token");
			
			m_adapter = new LigneAdapter();
			this.setListAdapter(m_adapter);
		}
	}
	
	public void showAll(View v) {
		m_filtre = "";
		m_Tokens = new ArrayList<String>();
		m_RowModels = new ArrayList<RowModel>();
		
		m_adapter = new LigneAdapter();
		this.setListAdapter(m_adapter);
		
		new AsyncGetCalendarsFriends().execute(usager.get_googleId());
		
		Button btnShowAll = (Button)rootView.findViewById(R.id.afficherTout);
		btnShowAll.setVisibility(View.GONE);
	}
	
	
	/**
	 * Adapter pour la gestion de chaque entrée de la liste
	 * @author Marcleking
	 *
	 */
	public class LigneAdapter extends ArrayAdapter<String> {
		/**
		 * Contructeur
		 */
		
		public LigneAdapter() {
			super(getActivity(), R.layout.dispo_ligne, R.id.lbl_nom, m_Tokens);
		}
		
		/**
		 * Retourne une ligne
		 */
		@Override
		public View getView(int p_Position, View p_Row, ViewGroup p_List) {
			View row = super.getView(p_Position, p_Row, p_List);
			
			RowModel model = m_RowModels.get(p_Position);
			
			TextView nom = (TextView) row.findViewById(R.id.lbl_nom);
			nom.setText(model.getNom());
			
			TextView dispo = (TextView) row.findViewById(R.id.lbl_dispo);
			dispo.setText(model.getDispo());
			
			return row;
		}
	}
	
	
	/**
	 * Class qui représente une ligne
	 * @author Marcleking
	 *
	 */
	public static class RowModel implements Parcelable{
		private String m_nom;
		private String m_dispo;
		
		public RowModel(String nom, String dispo) {
			this.m_nom = nom;
			this.m_dispo = dispo;
		}
		
		public String getNom() {
			return m_nom;
		}
		
		public void setNom(String nom) {
			this.m_nom = nom;
		}
		
		public String getDispo() {
			return m_dispo;
		}
		
		public void setDispo(String dispo) {
			this.m_dispo = dispo;
		}
		

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(m_nom);
			dest.writeString(m_dispo);
		}
	}
	
	public static final Parcelable.Creator<RowModel> CREATOR = new Parcelable.Creator<RowModel>() 
	{
        public RowModel createFromParcel(Parcel in) {
            return new RowModel(in.readString(), in.readString());
        }

        public RowModel[] newArray(int size) {
            return new RowModel[size];
        }
    };

	@Override
	public void onClick(View v) {
		switch(v.getId()){
	    	case R.id.afficherTout:
	    		showAll(v);
	    		break;
	    }
		
	}
	
	
	
	
	public static String getMoments(String id){
        InputStream inputStream = null;
        String mesEvents = "";
        String eventsAmi = "";
        try {
        	// Allez chercher mes events
            HttpClient httpclient = new DefaultHttpClient();
            
            HttpResponse response = httpclient.execute(
            		new HttpGet(
            				"https://www.googleapis.com/calendar/v3/calendars/"+id+"/events?alwaysIncludeEmail=true&orderBy=startTime&showDeleted=false&showHiddenInvitations=false&singleEvents=true&" +
            						"timeMin="+new SimpleDateFormat("yyyy-MM-dd'T'HH").format(new java.util.Date())+"%3A00%3A00-04%3A00&key=AIzaSyAXL_IWNweDZe-SxHrOqLUrhKIG76DNX5w"
            				)
            		);
            inputStream = response.getEntity().getContent();
            mesEvents = convertInputStreamToString(inputStream);
 
        } catch (Exception e) {
        	mesEvents = e.getMessage();
        }
 
        return mesEvents;
    }
    
	
    private class getMomentsLibres extends AsyncTask<ArrayList<String>, Void, ArrayList<ArrayList<String>>> {
    	private int hrsMin = usager.getM_heureMin();
    	private int hrsMax = usager.getM_heureMax();
    	private int nbJourDansLeFuture = 1;
    	
    	public void setHrsMin(int hrsMin) {
    		this.hrsMin = hrsMin;
    	}
    	
    	public void setHrsMax(int hrsMax) {
    		this.hrsMax = hrsMax;
    	}
    	
    	public void setNbJourDansLeFuture(int nbJourDansLeFuture) {
    		this.nbJourDansLeFuture = nbJourDansLeFuture;
    	}
    	
        @Override
        protected ArrayList<ArrayList<String>> doInBackground(ArrayList<String>... personne) {
        	
        	ArrayList<ArrayList<String>> events = new ArrayList<ArrayList<String>>();
        	
        	for (int i = 0; i < personne.length; i++) {
        		
        		ArrayList<String> unePersonne = new ArrayList<String>();
        		
        			
        		unePersonne.add(personne[i].get(0));// Ça c'est le nom d'une personne
        		
        		for (int j = 1; j < personne[i].size(); j++) {
        			unePersonne.add(getMoments(personne[i].get(j)));
        		}
        		
        		events.add(unePersonne);
        		
        	}
        	
            return events;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ArrayList<ArrayList<String>> result) {
        	
            try {
	            // on créé un array pour une semaine, 1 heure chaque
	            ArrayList<ArrayList<java.util.Date>> listeHeures = new ArrayList<ArrayList<java.util.Date>>();
	            
	            // Parcours de tout les heures qu'on veux allez voir dans le futur
	            for(int i = 0; i < this.nbJourDansLeFuture * 24; i++) {
	            	java.util.Date debut = new java.util.Date();
	            	debut.setHours(debut.getHours()+i);
	            	
	            	Calendar calendar = Calendar.getInstance();
	                calendar.setTime(debut);
	                calendar.set(Calendar.MILLISECOND, 0);
	                calendar.set(Calendar.SECOND, 0);
	                calendar.set(Calendar.MINUTE, 0);
	                
	                debut.setTime(calendar.getTimeInMillis());
	                
		            java.util.Date fin = new java.util.Date();
		            fin.setHours(fin.getHours()+i+1);
		            
		            calendar = Calendar.getInstance();
	                calendar.setTime(fin);
	                calendar.set(Calendar.MILLISECOND, 0);
	                calendar.set(Calendar.SECOND, 0);
	                calendar.set(Calendar.MINUTE, 0);
	                
	                fin.setTime(calendar.getTimeInMillis());
	            	
	            	ArrayList<java.util.Date> info = new ArrayList<java.util.Date>();
	            	info.add(debut);
	            	info.add(fin);
	            	
	            	// si le moment est entre les heures minumum et maximum
	            	if (debut.getHours() >= this.hrsMin && fin.getHours() <= this.hrsMax && debut.getHours() <= this.hrsMax && fin.getHours() >= this.hrsMin) {
	            		listeHeures.add(info);
	            	} else if (this.hrsMax + this.hrsMin == 0) {
	            		listeHeures.add(info);
	            	}
	            }
	            
	            // on parcours tout les amis
	            for (int k = 0; k < result.size(); k++) {
	            	
	            	// on cree un array vide
		            ArrayList<ArrayList<java.util.Date>> listeDispos = new ArrayList<ArrayList<java.util.Date>>();
		            
            		ArrayList<String> unAmi = result.get(k);
            		String nomAmi = unAmi.get(0);
            		
	            	// on parcours tout les calendrier d'un ami
            		for (String unCal : unAmi) {
            			// si c'est le nom on skip
            			if (unCal.equals(unAmi.get(0))) {
            				continue;
            			}
            			
            			// on parcours toute les heures
		            	for (int i = 0; i < listeHeures.size(); i++) {
		            		Boolean valCritique = true;
		            		
		            		ArrayList<java.util.Date> unMoment = listeHeures.get(i);
		            	
		            		JSONObject jObject = new JSONObject(unCal);
		    				JSONArray listEvent = jObject.getJSONArray("items");
		    				
		    				// on parcours tout les events de l'ami
		    				for(int j = 0 ; j < listEvent.length(); j++){
		    					JSONObject unEvent = new JSONObject(listEvent.getString(j));
		    					String debutJson = new JSONObject(unEvent.getString("start")).getString("dateTime");
		    					String finJson = new JSONObject(unEvent.getString("end")).getString("dateTime");
		    					
		    					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		    					java.util.Date debut = formatter.parse(debutJson);
		    					java.util.Date fin = formatter.parse(finJson);
		    					
		    					// si le debut de l'event est plus petit ou egual au début de l'array et que la fin de l'event est plus grande ou égual à la fin de l'array		
		    					if (debut.compareTo(unMoment.get(0)) <= 0 && fin.compareTo(unMoment.get(1)) >= 0) {
		    						valCritique = false;
		    						break;
		    					}
		    				}
		    				
		    				if (valCritique) {
			            		if (listeDispos.size() == 0) {
			            			listeDispos.add(unMoment);
			            		}
			            		// si la fin de l'event precedent est égual au début de l'event actuel
			            		else if (listeDispos.get(listeDispos.size() - 1).get(1).equals(unMoment.get(0))) {
			            			// on augmente la fin d'une heure
			            			listeDispos.get(listeDispos.size() - 1).set(1, unMoment.get(1));
			            		}
			            		// Si la fin de l'event precedent est plus petit que le debut de l'event actuel
			            		else {
			            			// on ajoute l'event
			            			listeDispos.add(unMoment);
			            		}	
			            	}
	            		}
	            	}
	            	
            		for (int i = 0; i<listeDispos.size(); i++) {
    	            	ArrayList<Date> uneDispo = listeDispos.get(i);
    	            	m_Tokens.add(uneDispo.toString());
    	            	
    	            	java.util.Date today = new java.util.Date();
    	            	
    	            	String dispo = "";
    	            	Formatter format = new Formatter();
    	            	
    	            	if (uneDispo.get(0).getDay() == today.getDay() && uneDispo.get(0).getHours() == today.getHours()) {
    	            		
    	            		dispo = "Aujourd'hui jusqu'à " + format.format("%tR", uneDispo.get(1));
    	            	} else {
    	            		String debut = format.format("%tR", uneDispo.get(0)).toString();
    	            		format = new Formatter();
    	            		String fin = format.format("%tR", uneDispo.get(1)).toString();
    	            		format = new Formatter();
    	            		dispo = "Le : " + format.format("%tD", uneDispo.get(0)) + " de " + debut + " à " + fin;
    	            	}
    	            	
    		    		m_RowModels.add(new RowModel(nomAmi + " est disponible :", dispo));
    	            }
	            }
			} catch (Exception e) {}
            m_adapter.notifyDataSetChanged();
       }
    }
    
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
 
    } 
    
    public class AsyncGetCalendarsFriends extends AsyncTask<String, Void, ArrayList<String>> {
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
        		reponse.add(getHttpRequest("http://www.appmeetup.appspot.com/get-friends?username="+id[i]));
        	}
        	
            return reponse;
        }
        
        @Override
        protected void onPostExecute(ArrayList<String> result) {
        	for (String strInfo : result) {
        		try {
					JSONObject info = new JSONObject(strInfo);
					
					JSONArray amis = info.getJSONArray("amis");
					
					for (int i = 0; i<amis.length(); i++) {
						if (m_filtre.equals("") || m_filtre.equals(amis.getString(i))) {
							new AsyncGetCalendars().execute(amis.getString(i));
						}
					}
					
					
				} catch (JSONException e) {}
        	}
        }
    }
    
    public class AsyncGetCalendars extends AsyncTask<String, Void, ArrayList<String>> {
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
        		reponse.add(getHttpRequest("http://www.appmeetup.appspot.com/get-calendars?username="+id[i]));
        	}
        	
            return reponse;
        }
        
        @Override
        protected void onPostExecute(ArrayList<String> result) {
        	
        	for (String strInfo : result) {
        		try {
					JSONObject info = new JSONObject(strInfo);
					
					JSONArray cals = info.getJSONArray("calendars");
					
					for (int i = 0; i < cals.length(); i++) {
						
						getMomentsLibres moments = new getMomentsLibres();
						
						ArrayList<String> personne = new ArrayList<String>();
						personne.add(info.getString("first_name") + " " + info.getString("last_name"));
						personne.add(cals.getString(i));
				        
				        moments.execute(personne);
					}
					
				} catch (JSONException e) {}
        	}
        }
    }
}
