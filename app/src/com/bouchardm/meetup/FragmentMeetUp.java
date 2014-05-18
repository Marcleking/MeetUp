package com.bouchardm.meetup;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.bouchardm.meetup.classes.Ami;
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
	public final static int CREATE_MEETUP = 0;
	public final static int UPDATE_MEETUP = 1;
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
	
	//private ArrayList<MeetUp> mesMeetUp = new ArrayList<MeetUp>();
	//private ArrayList<MeetUp> mesInvitations = new ArrayList<MeetUp>();
	
	public FragmentMeetUp(){}
	
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
		
		this.mesInvitations = new ArrayList<MeetUp>();
		this.mesMeetUp = new ArrayList<MeetUp>();
		
		GetMyEvents();
		GetMyInvitations();
		GetMyAcceptedInvitations();
		
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
		asyncGetMeetUp.setFragmentToUpdate(this);
		asyncGetMeetUp.execute((Void)null);
	}
	
	private void GetMyInvitations(){
		network.AsyncGetMyMeetUpInvitations asyncGetInvitations = new network.AsyncGetMyMeetUpInvitations();
		asyncGetInvitations.setUsername(usager.get_googleId());
		asyncGetInvitations.setSecurityNumber(usager.get_securityNumber());
		asyncGetInvitations.setFragmentToUpdate(this);
		asyncGetInvitations.execute((Void)null);
	}
	
	private void GetMyAcceptedInvitations(){
		network.AsyncGetMeetUpAccepted acceptedMeetUp = new network.AsyncGetMeetUpAccepted();
		acceptedMeetUp.setUsername(usager.get_googleId());
		acceptedMeetUp.setFragmentToUpdate(this);
		acceptedMeetUp.execute((Void)null);		
	}
	
	/**
	 * Cr�ation d'un context menu
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
				
				network.AsyncDeclineMeetUpParticipation asyncDecline = new network.AsyncDeclineMeetUpParticipation();
				asyncDecline.setUsername(usager.get_googleId());
				asyncDecline.setSecurityNumber(usager.get_securityNumber());
				asyncDecline.setMeetUpId(this.mesInvitations.get(info.position).get_id());
				asyncDecline.execute((Void)null);
				
				this.mesInvitations.remove(info.position);
				this.m_RowInvitationModels.remove(info.position);
				
				m_invitationAdapter.notifyDataSetChanged();
				return true;
			/*case R.id.menu_peutEtre:
				m_RowInvitationModels.get(info.position).setParticipation("Participe peut-être");
				m_invitationAdapter = new LigneInvitationAdapter();
				this.setListAdapter(m_invitationAdapter);
				return true;*/
			case R.id.menu_participe:
				
				network.AsyncAcceptMeetUpParticipation asyncAccept = new network.AsyncAcceptMeetUpParticipation();
				asyncAccept.setUsername(usager.get_googleId());
				asyncAccept.setSecurityNumber(usager.get_securityNumber());
				asyncAccept.setMeetUpKey(this.mesInvitations.get(info.position).get_id());
				asyncAccept.execute((Void)null);
				
				m_RowInvitationModels.get(info.position).setParticipation(getResources().getString(R.string.participe));
				m_invitationAdapter.notifyDataSetChanged();
				return true;
			case R.id.menu_modifier:
				
				MeetUp meetUpModifie = this.mesMeetUp.get(info.position);
				
				getActivity().startActivityForResult(new Intent(rootView.getContext(), CreationMeetUp.class)
					.putExtra("EXTRA_USER_ID",usager.get_googleId())
					.putExtra("EXTRA_BUTTON_TEXT", "Modifier le MeetUp")
					.putExtra("EXTRA_MEETUP_A_MODIFIER", MeetUp.ParseMeetUpToString(meetUpModifie)),FragmentMeetUp.UPDATE_MEETUP);
				
				return true;
			case R.id.menu_supprimer:
				
				network.AsyncDeleteMeetUp deleteMeetUp = new network.AsyncDeleteMeetUp();
				deleteMeetUp.setUsername(usager.get_googleId());
				deleteMeetUp.setSecurityNumber(usager.get_securityNumber());
				deleteMeetUp.setMeetUpId(this.mesMeetUp.get(info.position).get_id());
				deleteMeetUp.execute((Void)null);
				
				this.mesMeetUp.remove(info.position);
				m_RowMonEvenementModels.remove(info.position);
				this.m_monEvenementAdapter.notifyDataSetChanged();
				
				return true;
			case R.id.menu_voir_participants:
				
				ArrayList<Ami> participants = this.mesMeetUp.get(info.position).get_invites();
				
				ArrayList<String> participantsParse = new ArrayList<String>();
				for(Ami participant:participants){
					participantsParse.add(Ami.AmiToString(participant));
				}
				
				getActivity().startActivity(new Intent(rootView.getContext(), ListeParticipants.class)
					.putStringArrayListExtra("EXTRA_PARTICIPANTS", participantsParse));
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
		getActivity().startActivityForResult(new Intent(rootView.getContext(), CreationMeetUp.class)
			.putExtra("EXTRA_USER_ID",usager.get_googleId()).putExtra("EXTRA_BUTTON_TEXT", "Cr�er le MeetUp"),FragmentMeetUp.CREATE_MEETUP);
	}
	
	public void onActivityResult(int p_requestCode, int p_resultCode, Intent p_data){
		switch(p_requestCode){
		case CREATE_MEETUP:
			if(p_resultCode == getActivity().RESULT_OK){
				this.m_monEvenementAdapter.notifyDataSetChanged();
			}
			break;
		case UPDATE_MEETUP:
			if(p_resultCode == getActivity().RESULT_OK){
				this.m_monEvenementAdapter.notifyDataSetChanged();
			}
			break;
		}
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
			
			((TextView)row.findViewById(R.id.lblLieu)).setText(model.getLieu());
			((TextView)row.findViewById(R.id.lblDateDebut)).setText(model.getDateMin());
			((TextView)row.findViewById(R.id.lblHeureDebut)).setText(model.getHeureMin());
			((TextView)row.findViewById(R.id.lblDateFin)).setText(model.getDateMax());
			((TextView)row.findViewById(R.id.lblHeureFin)).setText(model.getHeureMax());
			
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
		private String m_lieu;
		private String m_DateMin;
		private String m_HeureMin;
		private String m_DateMax;
		private String m_HeureMax;
		
		
		public RowInvitationModel(String content, String participation, String lieu, 
				String dateMin, String heureMin, String dateMax, String heureMax) {
			this.m_Content = content;
			this.m_participation = participation;
			this.m_lieu = lieu;
			this.m_DateMin = dateMin;
			this.m_HeureMin = heureMin;
			this.m_DateMax = dateMax;
			this.m_HeureMax = heureMax;
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

		public String getLieu() {
			return m_lieu;
		}

		public void setLieu(String m_lieu) {
			this.m_lieu = m_lieu;
		}
		
		public String getDateMin() {
			return m_DateMin;
		}

		public void setDateMin(String m_DateMin) {
			this.m_DateMin = m_DateMin;
		}

		public String getHeureMin() {
			return m_HeureMin;
		}

		public void setHeureMin(String m_HeureMin) {
			this.m_HeureMin = m_HeureMin;
		}

		public String getDateMax() {
			return m_DateMax;
		}

		public void setDateMax(String m_DateMax) {
			this.m_DateMax = m_DateMax;
		}

		public String getHeureMax() {
			return m_HeureMax;
		}

		public void setHeureMax(String m_HeureMax) {
			this.m_HeureMax = m_HeureMax;
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
			dest.writeString(m_lieu);
			dest.writeString(m_DateMin);
			dest.writeString(m_HeureMin);
			dest.writeString(m_DateMax);
			dest.writeString(m_HeureMax);
		}
		
		public static final Parcelable.Creator<RowInvitationModel> CREATOR = new Parcelable.Creator<RowInvitationModel>() 
		{
	        public RowInvitationModel createFromParcel(Parcel in) {
	            return new RowInvitationModel(in.readString(), in.readString(),in.readString(),in.readString(),in.readString(),
	            		in.readString(),in.readString());
	        }

	        public RowInvitationModel[] newArray(int size) {
	            return new RowInvitationModel[size];
	        }
	    };
	}
	
	/**
	 * Adapter pour la gestion de chaque entrée de la liste des invitations � un MeetUp
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
			
			((TextView)row.findViewById(R.id.lblLieu)).setText(model.getLieu());
			((TextView)row.findViewById(R.id.lblDateDebut)).setText(model.getDateMin());
			((TextView)row.findViewById(R.id.lblHeureDebut)).setText(model.getHeureMin());
			((TextView)row.findViewById(R.id.lblDateFin)).setText(model.getDateMax());
			((TextView)row.findViewById(R.id.lblHeureFin)).setText(model.getHeureMax());
			
			return row;
		}
	}
	
	
	/**
	 * Class qui représente une ligne d'invitation � un MeetUp
	 * @author Marcleking
	 *
	 */
	public static class RowMonEvenementModel implements Parcelable{
		private String m_Content;
		private String m_participation;
		private String m_lieu;
		private String m_DateMin;
		private String m_HeureMin;
		private String m_DateMax;
		private String m_HeureMax;
		
		public RowMonEvenementModel(String content, String participation, String lieu, 
				String dateMin, String heureMin, String dateMax, String heureMax) {
			this.m_Content = content;
			this.m_participation = participation;
			this.m_lieu = lieu;
			this.m_DateMin = dateMin;
			this.m_HeureMin = heureMin;
			this.m_DateMax = dateMax;
			this.m_HeureMax = heureMax;
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

		public String getLieu() {
			return m_lieu;
		}

		public void setLieu(String m_lieu) {
			this.m_lieu = m_lieu;
		}

		public String getDateMin() {
			return m_DateMin;
		}

		public void setDateMin(String m_DateMin) {
			this.m_DateMin = m_DateMin;
		}

		public String getHeureMin() {
			return m_HeureMin;
		}

		public void setHeureMin(String m_HeureMin) {
			this.m_HeureMin = m_HeureMin;
		}

		public String getDateMax() {
			return m_DateMax;
		}

		public void setDateMax(String m_DateMax) {
			this.m_DateMax = m_DateMax;
		}

		public String getHeureMax() {
			return m_HeureMax;
		}

		public void setHeureMax(String m_HeureMax) {
			this.m_HeureMax = m_HeureMax;
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
			dest.writeString(m_lieu);
			dest.writeString(m_DateMin);
			dest.writeString(m_HeureMin);
			dest.writeString(m_DateMax);
			dest.writeString(m_HeureMax);
		}
		
		public static final Parcelable.Creator<RowMonEvenementModel> CREATOR = new Parcelable.Creator<RowMonEvenementModel>() 
		{
	        public RowMonEvenementModel createFromParcel(Parcel in) {
	            return new RowMonEvenementModel(in.readString(), in.readString(), in.readString(), in.readString(),
	            		in.readString(), in.readString(), in.readString());
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
	
	public Personne getUsager() {
		return usager;
	}

	public void setUsager(Personne usager) {
		this.usager = usager;
	}

	public ArrayList<MeetUp> getMesMeetUp() {
		return mesMeetUp;
	}

	public void setMesMeetUp(ArrayList<MeetUp> mesMeetUp) {
		this.mesMeetUp = mesMeetUp;
	}

	public ArrayList<MeetUp> getMesInvitations() {
		return mesInvitations;
	}

	public void setMesInvitations(ArrayList<MeetUp> mesInvitations) {
		this.mesInvitations = mesInvitations;
	}

	public ArrayList<RowInvitationModel> getRowInvitationModels() {
		return m_RowInvitationModels;
	}

	public void setRowInvitationModels(
			ArrayList<RowInvitationModel> m_RowInvitationModels) {
		this.m_RowInvitationModels = m_RowInvitationModels;
	}

	public ArrayList<RowMonEvenementModel> getRowMonEvenementModels() {
		return m_RowMonEvenementModels;
	}

	public void setRowMonEvenementModels(
			ArrayList<RowMonEvenementModel> m_RowMonEvenementModels) {
		this.m_RowMonEvenementModels = m_RowMonEvenementModels;
	}

	public LigneInvitationAdapter get_invitationAdapter() {
		return m_invitationAdapter;
	}

	public void set_invitationAdapter(LigneInvitationAdapter m_invitationAdapter) {
		this.m_invitationAdapter = m_invitationAdapter;
	}

	public LigneMonEvenementAdapter get_monEvenementAdapter() {
		return m_monEvenementAdapter;
	}

	public void set_monEvenementAdapter(
			LigneMonEvenementAdapter m_monEvenementAdapter) {
		this.m_monEvenementAdapter = m_monEvenementAdapter;
	}

	public ListView getListeMesMeetUp() {
		return listeMesMeetUp;
	}

	public void setListeMesMeetUp(ListView listeMesMeetUp) {
		this.listeMesMeetUp = listeMesMeetUp;
	}

	public ListView getListeMesInvitations() {
		return listeMesInvitations;
	}

	public void setListeMesInvitations(ListView listeMesInvitations) {
		this.listeMesInvitations = listeMesInvitations;
	}

}
