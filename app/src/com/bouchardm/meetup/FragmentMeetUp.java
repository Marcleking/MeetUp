package com.bouchardm.meetup;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.bouchardm.meetup.classes.Personne;
import com.bouchardm.meetup.classes.network;
import com.bouchardm.meetup.classes.MeetUp;
import com.bouchardm.meetup.sqlite.PersonneDataSource;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;


import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentMeetUp extends ListFragment implements View.OnClickListener {

	/**
	 * Attributs de la view
	 */
	private ArrayList<RowInvitationModel> m_RowInvitationModels = new ArrayList<RowInvitationModel>();
	private LigneInvitationAdapter m_invitationAdapter;
	
	private ArrayList<RowMonEvenementModel> m_RowMonEvenementModels = new ArrayList<RowMonEvenementModel>();
	private LigneMonEvenementAdapter m_monEvenementAdapter;
	
	private View rootView;
	private Button btnAjoutMeetUp;
	
	private Personne usager;
	
	private ListView listeMesMeetUp;
	private ListView listeMesInvitations;
	
	private ArrayList<MeetUp> mesMeetUp = null;
	private ArrayList<MeetUp> mesInvitations = null;
	
	public FragmentMeetUp(){}
	
	public Personne getUsager() {
		return usager;
	}

	public void setUsager(Personne usager) {
		this.usager = usager;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		if(savedInstanceState != null){
			usager = Personne.ParseStringToPerson(savedInstanceState.getString("usager"));
		}
		
		rootView = inflater.inflate(R.layout.meet_up_accueil, container, false);
		btnAjoutMeetUp = (Button)rootView.findViewById(R.id.btnAjoutMeetUp);
		btnAjoutMeetUp.setOnClickListener(this);
		
		listeMesMeetUp = (ListView)rootView.findViewById(R.id.listeMesMeetUp);
		listeMesInvitations = (ListView)rootView.findViewById(R.id.listeDemandesMeetUp);
		
		GetMyEvents();
		Log.i("Test",MeetUp.ParseMeetUpToString(mesMeetUp.get(0)));
		
		if(this.mesInvitations != null && this.mesInvitations.size() > 0){
			
			m_invitationAdapter = new LigneInvitationAdapter();
			listeMesInvitations.setAdapter(m_invitationAdapter);
			
			for (MeetUp token : this.mesInvitations) {
				m_RowInvitationModels.add(new RowInvitationModel(token.get_nom(), "Choisissez votre participation..."));
			}
		}
		if(this.mesMeetUp != null && this.mesMeetUp.size() > 0){
			
			this.m_monEvenementAdapter = new LigneMonEvenementAdapter();
			this.listeMesMeetUp.setAdapter(m_monEvenementAdapter);
			
			for(MeetUp token : this.mesMeetUp){
				this.m_RowMonEvenementModels.add(new RowMonEvenementModel(token.get_nom(),token.get_invites().size() + " invités participent."));
				//this.m_RowMonEvenementModels.add(new RowMonEvenementModel(token.get_nom(),""));
			}
		}
		
		// Déplacé dans onViewCreated
		//this.registerForContextMenu(this.getListView()); 
		
		
		
		return rootView;
	}
	
	@Override
	public void onViewCreated(View v, Bundle savedInstanceState){
		super.onViewCreated(v, savedInstanceState);
		this.registerForContextMenu(listeMesMeetUp);
		this.registerForContextMenu(listeMesInvitations); 
	}
	
	private void GetMyEvents(){
		network.AsyncGetMyMeetUp asyncGetMeetUp = new network.AsyncGetMyMeetUp();
		asyncGetMeetUp.setUsername(usager.get_googleId());
		asyncGetMeetUp.setSecurityNumber(usager.get_securityNumber());
		try {
			mesMeetUp = asyncGetMeetUp.execute((Void)null).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	private void GetMyInvitations(){
		
	}
	
	/**
	 * Création d'un context menu
	 */
	@Override
	public void onCreateContextMenu(android.view.ContextMenu menu, View v, android.view.ContextMenu.ContextMenuInfo menuInfo)
	{
		switch(v.getId()){
		case R.id.listeDemandesMeetUp:
			getActivity().getMenuInflater().inflate(R.menu.meet_up, menu);
			break;
		case R.id.listeMesMeetUp:
			getActivity().getMenuInflater().inflate(R.menu.mon_meet_up, menu);
			break;
		}
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
				m_RowInvitationModels.get(info.position).setParticipation("Ne participe pas");
				m_invitationAdapter = new LigneInvitationAdapter();
				this.setListAdapter(m_invitationAdapter);
				return true;
			case R.id.menu_peutEtre:
				m_RowInvitationModels.get(info.position).setParticipation("Participe peut-être");
				m_invitationAdapter = new LigneInvitationAdapter();
				this.setListAdapter(m_invitationAdapter);
				return true;
			case R.id.menu_participe:
				m_RowInvitationModels.get(info.position).setParticipation("Participe !");
				m_invitationAdapter = new LigneInvitationAdapter();
				this.setListAdapter(m_invitationAdapter);
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
		p_outState.putParcelableArrayList("rowModel", m_RowInvitationModels);
		
		p_outState.putString("usager", Personne.ParsePersonneToString(usager));
		
		if(this.mesInvitations != null)
		{
			ArrayList<String> parseMesInvitations = new ArrayList<String>();
			for(MeetUp meetUp:this.mesInvitations){
				parseMesInvitations.add(MeetUp.ParseMeetUpToString(meetUp));
			}
			p_outState.putStringArrayList("mesInvitations", parseMesInvitations);
		}
		
		if(this.mesMeetUp != null){
			ArrayList<String> parseMesMeetUp = new ArrayList<String>();
			for(MeetUp meetUp : this.mesMeetUp){
				parseMesMeetUp.add(MeetUp.ParseMeetUpToString(meetUp));
			}
			p_outState.putStringArrayList("mesMeetUp", parseMesMeetUp);
		}
		
	}
	
	/**
	 * Gestion de la restauration des données (lors de la rotation)
	 */
	public void onRestoreInstanceState(Bundle p_state) 
	{
		if (p_state != null) {
			m_RowInvitationModels = p_state.getParcelableArrayList("rowModel");
			
			usager = Personne.ParseStringToPerson(p_state.getString("usager"));
			
			this.mesInvitations = new ArrayList<MeetUp>();
			for(String parseMeetUp:p_state.getStringArrayList("mesInvitations")){
				this.mesInvitations.add(MeetUp.ParseStringToMeetUp(parseMeetUp));
			}
			
			this.mesMeetUp = new ArrayList<MeetUp>();
			for(String parseMeetUp:p_state.getStringArrayList("mesMeetUp")){
				this.mesMeetUp.add(MeetUp.ParseStringToMeetUp(parseMeetUp));
			}
			
			m_invitationAdapter = new LigneInvitationAdapter();
			listeMesMeetUp.setAdapter(m_invitationAdapter);
			//this.setListAdapter(m_invitationAdapter);
		}
	}
	
	/**
	 * Gestion du clic sur le bouton d'ajout d'horaire => affiche un pop-up
	 * @param source
	 */
	public void btnAjoutMeetUp(View source)
	{
		getActivity().startActivity(new Intent(rootView.getContext(), CreationMeetUp.class)
			.putExtra("EXTRA_USER_ID",usager.get_googleId()));
	}
	
	/**
	 * Handler pour la gestion du pop-up d'ajout d'horaire
	 * @author Marcleking
	 *
	 *//*
	public class BtnSetHandler implements DialogInterface.OnClickListener
	{
		*//**
		 * Attributs
		 *//*
		private EditText m_txtHoraire;
		
		*//**
		 * Constructeur
		 * @param p_txtHoraire
		 *//*
		public BtnSetHandler (EditText p_txtHoraire)
		{
			this.m_txtHoraire = p_txtHoraire;
		}
		
		*//**
		 * Gestion de l'enregistrement de l'horaire => actualise la liste
		 *//*
		@Override
		public void onClick(DialogInterface dialog, int which) {
			m_Tokens.add(m_txtHoraire.getText().toString());
			m_RowInvitationModels.add(new RowInvitationModel(m_txtHoraire.getText().toString(), "Participe!"));
			m_invitationAdapter.notifyDataSetChanged();
		}
		
		
	}*/
	
	/**
	 * Gestion du clic sur un horaire (activation/désactivation de l'horaire)
	 */
	@Override
	public void onListItemClick(ListView p_l, View p_row, int p_position, long p_id) {
		RowInvitationModel model = m_RowInvitationModels.get(p_position);
		
	}
	
	/**
	 * Adapter pour la gestion de chaque entrée de la liste des invitations à un MeetUp
	 * @author Marcleking
	 *
	 */
	public class LigneInvitationAdapter extends ArrayAdapter<MeetUp> {
		/**
		 * Contructeur
		 */
		public LigneInvitationAdapter() {
			super(rootView.getContext(), R.layout.meet_up_ligne, R.id.lblMeetUp, mesInvitations);
		}
		
		/**
		 * Retourne une ligne
		 */
		@Override
		public View getView(int p_Position, View p_Row, ViewGroup p_List) {
			View row = super.getView(p_Position, p_Row, p_List);
			
			RowInvitationModel model = m_RowInvitationModels.get(p_Position);
			
			TextView lblContent = (TextView) row.findViewById(R.id.lblMeetUp);
			lblContent.setText(model.getContent());
			
			TextView lblParticipation = (TextView) row.findViewById(R.id.lblParticipation);
			lblParticipation.setText(model.getParticipation());
			
			return row;
		}
	}
	
	
	/**
	 * Class qui représente une ligne d'invitation à un MeetUp
	 * @author Marcleking
	 *
	 */
	public static class RowInvitationModel implements Parcelable{
		private String m_Content;
		private String m_participation;
		
		public RowInvitationModel(String content, String participation) {
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
		
		public static final Parcelable.Creator<RowInvitationModel> CREATOR = new Parcelable.Creator<RowInvitationModel>() 
		{
	        public RowInvitationModel createFromParcel(Parcel in) {
	            return new RowInvitationModel(in.readString(), in.readString());
	        }

	        public RowInvitationModel[] newArray(int size) {
	            return new RowInvitationModel[size];
	        }
	    };
	}
	
	/**
	 * Adapter pour la gestion de chaque entrée de la liste des invitations à un MeetUp
	 * @author Marcleking
	 *
	 */
	public class LigneMonEvenementAdapter extends ArrayAdapter<MeetUp> {
		/**
		 * Contructeur
		 */
		public LigneMonEvenementAdapter() {
			super(rootView.getContext(), R.layout.meet_up_ligne, R.id.lblMeetUp, mesMeetUp);
		}
		
		/**
		 * Retourne une ligne
		 */
		@Override
		public View getView(int p_Position, View p_Row, ViewGroup p_List) {
			View row = super.getView(p_Position, p_Row, p_List);
			
			RowMonEvenementModel model = m_RowMonEvenementModels.get(p_Position);
			
			TextView lblContent = (TextView) row.findViewById(R.id.lblMeetUp);
			lblContent.setText(model.getContent());
			
			TextView lblParticipation = (TextView) row.findViewById(R.id.lblParticipation);
			lblParticipation.setText(model.getParticipation());
			
			return row;
		}
	}
	
	
	/**
	 * Class qui représente une ligne d'invitation à un MeetUp
	 * @author Marcleking
	 *
	 */
	public static class RowMonEvenementModel implements Parcelable{
		private String m_Content;
		private String m_participation;
		
		public RowMonEvenementModel(String content, String participation) {
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
		
		public static final Parcelable.Creator<RowMonEvenementModel> CREATOR = new Parcelable.Creator<RowMonEvenementModel>() 
		{
	        public RowMonEvenementModel createFromParcel(Parcel in) {
	            return new RowMonEvenementModel(in.readString(), in.readString());
	        }

	        public RowMonEvenementModel[] newArray(int size) {
	            return new RowMonEvenementModel[size];
	        }
	    };
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnAjoutMeetUp:
			btnAjoutMeetUp(v);
			break;
		}
	}

}
