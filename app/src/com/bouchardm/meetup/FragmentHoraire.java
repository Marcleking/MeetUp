package com.bouchardm.meetup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bouchardm.meetup.FragmentAmis.ExpandListAdapter;
import com.bouchardm.meetup.FragmentAmis.ListeAmiModel;
import com.bouchardm.meetup.FragmentAmis.ListeGroupeModel;
import com.bouchardm.meetup.Horaire.LigneAdapter;
import com.bouchardm.meetup.Horaire.RowModel;
import com.google.api.services.calendar.Calendar.Calendars;
import com.bouchardm.meetup.util.*;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.annotation.SuppressLint;
import android.app.AlertDialog;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentHoraire extends ListFragment implements View.OnClickListener {
	/**
	 * Attributs de la view
	 */
	private ArrayList<String> m_Tokens = new ArrayList<String>();
	private ArrayList<RowModel> m_RowModels = new ArrayList<RowModel>();
	private LigneAdapter m_adapter;
	
	private View rootView;
	
	public FragmentHoraire(){}
	
	public static final String[] EVENT_PROJECTION = new String[] {
		CalendarContract.Calendars._ID,                           // 0
		CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
		CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
		CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
	};
	
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
	private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
	private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
	
	
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.horaire, container, false);
		
		m_adapter = new LigneAdapter();
		this.setListAdapter(m_adapter);
		
		// TODO : faire en sorte que sa soit le bon username
		new AsyncGetHoraire().execute("http://www.appmeetup.appspot.com/get-calendars?username=Marc");
		
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
	
	/**
	 * Gestion du clic sur un horaire (activation/désactivation de l'horaire)
	 */
	@Override
	public void onListItemClick(ListView p_l, View p_row, int p_position, long p_id) {
		
		RowModel model = m_RowModels.get(p_position);
		
		model.setIsActivate(!model.isActivate());
		
		ImageView icon = (ImageView) p_row.findViewById(R.id.img_selection);
		if (model.isActivate()) {
			icon.setImageResource(R.drawable.ok);
		} else {
			icon.setImageResource(R.drawable.delete);
		}
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
			super(getActivity(), R.layout.horaire_ligne, R.id.lbl_content, m_Tokens);
		}
		
		/**
		 * Retourne une ligne
		 */
		@Override
		public View getView(int p_Position, View p_Row, ViewGroup p_List) {
			View row = super.getView(p_Position, p_Row, p_List);
			
			RowModel model = m_RowModels.get(p_Position);
			
			TextView lblContent = (TextView) row.findViewById(R.id.lbl_content);
			lblContent.setText(model.getContent());
			
			ImageView icon = (ImageView) row.findViewById(R.id.img_selection);
			if (model.isActivate()) {
				icon.setImageResource(R.drawable.ok);
			} else {
				icon.setImageResource(R.drawable.delete);
			}
			
			return row;
		}
	}
	
	
	/**
	 * Class qui représente une ligne
	 * @author Marcleking
	 *
	 */
	public static class RowModel implements Parcelable{
		private String m_Content;
		private boolean m_isActivate;
		private String m_id;
		
		public RowModel(String content, boolean activate, String id) {
			this.m_Content = content;
			this.m_id = id;
			this.setIsActivate(activate);
		}
		
		public String getContent() {
			return m_Content;
		}
		
		public void setContent(String content) {
			this.m_Content = content;
		}
		
		public boolean isActivate() {
			return m_isActivate;
		}
		
		public void setIsActivate(boolean isActivate) {
			this.m_isActivate = isActivate;
			if (isActivate) {
				// TODO : faire en sorte que sa soit avec le bon username
				
				
				
				new AsyncHttpGet().execute("http://www.appmeetup.appspot.com/add-calendar?moi=Marc&password=motDePasse&ajoute="+m_id);
			} else {
				// TODO : faire en sorte que sa soit avec le bon username
				new AsyncHttpGet().execute("http://www.appmeetup.appspot.com/delete-calendar?moi=Marc&password=motDePasse&retire="+m_id);
			}
		}
		
		public String getCalId() {
			return m_id;
		}

		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(m_Content);
			dest.writeByte((byte) (m_isActivate ? 1 : 0));
			dest.writeString(m_id);
		}
	}
	
	public static final Parcelable.Creator<RowModel> CREATOR = new Parcelable.Creator<RowModel>() 
	{
        public RowModel createFromParcel(Parcel in) {
            return new RowModel(in.readString(), in.readByte() != 0, in.readString());
        }

        public RowModel[] newArray(int size) {
            return new RowModel[size];
        }
    };

	@Override
	public void onClick(View v) {

		
	}
	
	/******************
     * Call asyncrone
     * @author Marcleking
     *
     */
    @SuppressLint("NewApi")
	public class AsyncGetHoraire extends AsyncTask<String, Void, ArrayList<String>> {
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
	    protected ArrayList<String> doInBackground(String... url) {
	    	
	    	ArrayList<String> reponse = new ArrayList<String>();
	    	
	    	for (int i = 0; i < url.length; i++) {
	    		reponse.add(getHttpRequest(url[i]));
	    	}
	    	
	        return reponse;
	    }
	    
	    @Override
	    protected void onPostExecute(java.util.ArrayList<String> result) {
	    	
	    	
	    	
	    	/////////////////////////////////////////////////////////////////
	    	try {
				//getApplicationContext().getContentResolver() 
				Cursor cur = null;
				ContentResolver cr = getActivity().getContentResolver();
				Uri uri = CalendarContract.Calendars.CONTENT_URI;   
				String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
				                        + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";
				String[] selectionArgs = new String[] {"marcantoine.bouchardm@gmail.com", "com.google"}; 
				// Submit the query and get a Cursor object back. 
				cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
				
				
				
				// Use the cursor to step through the returned records
				while (cur.moveToNext()) {
				    long calID = 0;
				    String displayName = null;
				    String accountName = null;
				    String ownerName = null;
				      
				    // Get the field values
				    calID = cur.getLong(PROJECTION_ID_INDEX);
				    displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
				    accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
				    ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

				    // Do something with the values...
				    m_Tokens.add(displayName);
				    
				    boolean isActivate = false;
				    
				    
				    for (String reponse : result) {
				    	JSONObject info = new JSONObject(reponse);
				    	Object lesCals = info.get("calendars");
			    		JSONArray calendriers = new JSONArray(lesCals.toString());
			    		
				    	for (int i = 0; i < calendriers.length(); i++){
				    		if (calendriers.get(i).toString().equalsIgnoreCase(ownerName.toString())) {
				    			isActivate = true;
				    		}
				    	}
				    }
				    
				    
				    m_RowModels.add(new RowModel(displayName, isActivate, ownerName));
				    
				    // ownerName = vrai id du calendar
				    // Toast.makeText(getActivity(), ownerName, Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {}
	    	
	    	/////////////////////////////////////////////////////////////////

	    	
			
			m_adapter.notifyDataSetChanged();
	    }
	    

		private String convertInputStreamToString(InputStream inputStream) throws IOException{
		    BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
		    String line = "";
		    String result = "";
		    while((line = bufferedReader.readLine()) != null)
		        result += line;
		
		    inputStream.close();
		    return result;
		
		}
	}

}
