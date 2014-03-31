package com.bouchardm.meetup;

import java.util.ArrayList;


import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentMeetUp extends ListFragment implements View.OnClickListener {

	/**
	 * Attributs de la view
	 */
	private ArrayList<String> m_Tokens = new ArrayList<String>();
	private ArrayList<RowModel> m_RowModels = new ArrayList<RowModel>();
	private LigneAdapter m_adapter;
	
	private View rootView;
	private Button btnAjoutMeetUp;
	
	public FragmentMeetUp(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.meet_up_accueil, container, false);
		btnAjoutMeetUp = (Button)rootView.findViewById(R.id.btnAjoutMeetUp);
		btnAjoutMeetUp.setOnClickListener(this);
		
		m_adapter = new LigneAdapter();
		this.setListAdapter(m_adapter);
		
		m_Tokens.add("Rencontre trop cool");
		m_Tokens.add("Rencontre trop pas cool");
		
		for (String token : m_Tokens) {
			m_RowModels.add(new RowModel(token, "Choisissez votre participation..."));
		}
		
		this.registerForContextMenu(this.getListView());
		
		return rootView;
	}
	
	/**
	 * Création d'un context menu
	 */
	@Override
	public void onCreateContextMenu(android.view.ContextMenu menu, View v, android.view.ContextMenu.ContextMenuInfo menuInfo)
	{
		getActivity().getMenuInflater().inflate(R.menu.meet_up, menu);
	}
	
	/**
	 * Gestion du clic sur le context menu
	 */
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item)
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		
		switch(item.getItemId())
		{
			case R.id.menu_nePasParticiper:
				m_RowModels.get(info.position).setParticipation("Ne participe pas");
				m_adapter = new LigneAdapter();
				this.setListAdapter(m_adapter);
				return true;
			case R.id.menu_peutEtre:
				m_RowModels.get(info.position).setParticipation("Participe peut-être");
				m_adapter = new LigneAdapter();
				this.setListAdapter(m_adapter);
				return true;
			case R.id.menu_participe:
				m_RowModels.get(info.position).setParticipation("Participe !");
				m_adapter = new LigneAdapter();
				this.setListAdapter(m_adapter);
				return true;
		}
		return super.onContextItemSelected(item);
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
	 * Gestion du clic sur le bouton d'ajout d'horaire => affiche un pop-up
	 * @param source
	 */
	public void btnAjoutMeetUp(View source)
	{
		getActivity().startActivity(new Intent(rootView.getContext(), CreationMeetUp.class));
	}
	
	/**
	 * Handler pour la gestion du pop-up d'ajout d'horaire
	 * @author Marcleking
	 *
	 */
	public class BtnSetHandler implements DialogInterface.OnClickListener
	{
		/**
		 * Attributs
		 */
		private EditText m_txtHoraire;
		
		/**
		 * Constructeur
		 * @param p_txtHoraire
		 */
		public BtnSetHandler (EditText p_txtHoraire)
		{
			this.m_txtHoraire = p_txtHoraire;
		}
		
		/**
		 * Gestion de l'enregistrement de l'horaire => actualise la liste
		 */
		@Override
		public void onClick(DialogInterface dialog, int which) {
			m_Tokens.add(m_txtHoraire.getText().toString());
			m_RowModels.add(new RowModel(m_txtHoraire.getText().toString(), "Participe!"));
			m_adapter.notifyDataSetChanged();
		}
		
		
	}
	
	/**
	 * Gestion du clic sur un horaire (activation/désactivation de l'horaire)
	 */
	@Override
	public void onListItemClick(ListView p_l, View p_row, int p_position, long p_id) {
		RowModel model = m_RowModels.get(p_position);
		
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
			super(rootView.getContext(), R.layout.meet_up_ligne, R.id.lblMeetUp, m_Tokens);
		}
		
		/**
		 * Retourne une ligne
		 */
		@Override
		public View getView(int p_Position, View p_Row, ViewGroup p_List) {
			View row = super.getView(p_Position, p_Row, p_List);
			
			RowModel model = m_RowModels.get(p_Position);
			
			TextView lblContent = (TextView) row.findViewById(R.id.lblMeetUp);
			lblContent.setText(model.getContent());
			
			TextView lblParticipation = (TextView) row.findViewById(R.id.lblParticipation);
			lblParticipation.setText(model.getParticipation());
			
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
		private String m_participation;
		
		public RowModel(String content, String participation) {
			this.m_Content = content;
			this.m_participation = participation;
		}
		
		public String getContent() {
			return m_Content;
		}
		
		public void setContent(String content) {
			this.m_Content = content;
		}
		
		public String getParticipation() {
			return m_participation;
		}
		
		public void setParticipation(String participation) {
			this.m_participation = participation;
		}

		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(m_Content);
			dest.writeString(m_participation);
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
		case R.id.btnAjoutMeetUp:
			btnAjoutMeetUp(v);
			break;
		}
	}

}
