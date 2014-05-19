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

import com.bouchardm.meetup.classes.Ami;
import com.bouchardm.meetup.classes.network;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ListeAmis extends ListActivity {
	/**
	 * Attributs de la view
	 */
	private ArrayList<Ami> m_Tokens = null;
	private ArrayList<RowModel> m_RowModels = null;
	private LigneAdapter m_adapter;
	
	private String googleId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meet_up_creation_liste_amis);
		
		m_Tokens = new ArrayList<Ami>();
		m_RowModels = new ArrayList<RowModel>();
		
		m_adapter = new LigneAdapter();
		this.setListAdapter(m_adapter);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			googleId = extras.getString("EXTRA_USER_ID");
		}
		
		
		network.AsyncGetFriends getFriendList = new network.AsyncGetFriends();
		getFriendList.setUser_id(googleId);
		getFriendList.setActivityToUpdate(this);
		getFriendList.execute();
		
		
		
	}
	
	public void okAmis(View source)
	{
		ArrayList<Ami> amis = null;
		if(m_RowModels.size() != 0){
			amis = new ArrayList<Ami>();
			for(RowModel ligne : m_RowModels){
				if(ligne.isActivate()){
					amis.add(new Ami(
							ligne.getKey(),
							ligne.getContent().split(" ")[0],
							ligne.getContent().split(" ")[1]));
				}
			}
		}
		
		ArrayList<String> parseAmis = null;
		if(amis.size() != 0){
			parseAmis = new ArrayList<String>();
			for(Ami ami: amis){
				parseAmis.add(Ami.AmiToString(ami));
			}
		}
		
		// TODO : Renvoyer des objets "Ami" plutôt qu'une liste de username
		Intent i = new Intent();
		i.putExtra("EXTRA_MEETUP_AMI", parseAmis);
		this.setResult(RESULT_OK, i);
		this.finish();
	}
	
	/**
	 * Gestion de l'enregistrement des données (lors de la rotation)
	 */
	@Override
	protected void onSaveInstanceState(Bundle p_outState) 
	{
		super.onSaveInstanceState(p_outState);
		p_outState.putParcelableArrayList("rowModel", m_RowModels);
		
		ArrayList<String> parseToken = new ArrayList<String>();
		
		for(Ami ami: m_Tokens){
			parseToken.add(Ami.AmiToString(ami));
		}
		
		p_outState.putStringArrayList("token", parseToken);
	}
	
	/**
	 * Gestion de la restauration des données (lors de la rotation)
	 */
	@Override
	protected void onRestoreInstanceState(Bundle p_state) 
	{
		if (p_state != null) {
			m_RowModels = p_state.getParcelableArrayList("rowModel");
			ArrayList<String> parseTokens = p_state.getStringArrayList("token");
			
			for(String token:parseTokens){
				this.m_Tokens.add(Ami.StringToAmi(token));
			}
			
			m_adapter = new LigneAdapter();
			this.setListAdapter(m_adapter);
		}
	}
	
	/**
	 * Gestion du clic sur un horaire (activation/désactivation de l'horaire)
	 */
	@Override
	protected void onListItemClick(ListView p_l, View p_row, int p_position, long p_id) {
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
	public class LigneAdapter extends ArrayAdapter<Ami> {
		/**
		 * Contructeur
		 */
		public LigneAdapter() {
			super(ListeAmis.this, R.layout.row_amis, R.id.lbl_content, m_Tokens);
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
		private String m_key;
		private boolean m_isActivate;
		
		public RowModel(String content, String key, boolean activate) {
			this.m_Content = content;
			this.m_key = key;
			this.m_isActivate = activate;
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
		}

		public String getKey() {
			return m_key;
		}

		public void setKey(String m_key) {
			this.m_key = m_key;
		}

		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(m_Content);
			dest.writeString(m_key);
			dest.writeByte((byte) (m_isActivate ? 1 : 0));  
		}
		
		public static final Parcelable.Creator<RowModel> CREATOR = new Parcelable.Creator<RowModel>() 
		{
	        public RowModel createFromParcel(Parcel in) {
	            return new RowModel(in.readString(), in.readString(), in.readByte() != 0);
	        }

	        public RowModel[] newArray(int size) {
	            return new RowModel[size];
	        }
	    };
	}


	public ArrayList<Ami> getM_Tokens() {
		return m_Tokens;
	}

	public void setM_Tokens(ArrayList<Ami> m_Tokens) {
		this.m_Tokens = m_Tokens;
	}

	public ArrayList<RowModel> getM_RowModels() {
		return m_RowModels;
	}

	public void setM_RowModels(ArrayList<RowModel> m_RowModels) {
		this.m_RowModels = m_RowModels;
	}

	public LigneAdapter getM_adapter() {
		return m_adapter;
	}

	public void setM_adapter(LigneAdapter m_adapter) {
		this.m_adapter = m_adapter;
	}
}
