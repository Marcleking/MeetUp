package com.bouchardm.meetup.classes;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

import com.bouchardm.meetup.FragmentMeetUp;
import com.bouchardm.meetup.FragmentMeetUp.LigneMonEvenementAdapter;
import com.bouchardm.meetup.FragmentMeetUp.RowInvitationModel;
import com.bouchardm.meetup.FragmentMeetUp.RowMonEvenementModel;
import com.bouchardm.meetup.ListeAmis.RowModel;
import com.bouchardm.meetup.ListeAmis;
import com.bouchardm.meetup.R;
import com.bouchardm.meetup.util.JSONParser;
import com.bouchardm.meetup.classes.MeetUp;
import com.google.android.gms.plus.model.people.Person;

public class network {
	
	private final static String WEB_SERVICE_URL = "appmeetup.appspot.com";
	
	public static class AsyncAddUser extends AsyncTask<Void,Void, String>{
		private String p_sha1;
		private Person p_user;
		
		public String message;
		
		private HttpClient m_ClientHttp = new DefaultHttpClient();
		
		public String getP_sha1() {
			return p_sha1;
		}

		public void setP_sha1(String p_sha1) {
			this.p_sha1 = p_sha1;
		}

		public Person getP_user() {
			return p_user;
		}

		public void setP_user(Person p_user) {
			this.p_user = p_user;
		}

		@Override
		protected void onPreExecute(){
			Log.i("AddUser","preExecute");
		}
		
		@Override
		protected String doInBackground(Void... unused) {
			
			String googleKey = "";
			
			try{
				URI uri = new URI("HTTP",WEB_SERVICE_URL, "/add-user", 
						"username=" + p_user.getId() + 
						"&password=" + p_sha1 +
						"&nom=" + p_user.getName().getFamilyName() + 
						"&prenom=" + p_user.getName().getGivenName() , null );
				
				Log.i("AddUser", "URL : " + uri.toString());
				HttpGet getMethod = new HttpGet(uri);
				
				String body = m_ClientHttp.execute(getMethod, new BasicResponseHandler());
				Log.i("AddUser", "Résultat : " + body);
				
				googleKey = JSONParser.parseSingleString(body, "key");
			}
			catch(Exception e){
				Log.i("AddUser", "Erreur : " + e.getMessage());
			}
			
			return googleKey;
		}
		
		@Override
		protected void onPostExecute(String gKey){
			Log.i("AddUser", "PostExecute : " + gKey);
		}
		
	}
	/*
	 * @author Francis Ouellet
	 * 
	 * */
	public static class AsyncGetFriends extends AsyncTask<Void, Void, ArrayList<Ami>>{
		
		private String user_id;
		private ListeAmis activityToUpdate;
		
		public String getUser_id() {
			return user_id;
		}

		public void setUser_id(String user_id) {
			this.user_id = user_id;
		}

		public ListeAmis getActivityToUpdate() {
			return activityToUpdate;
		}

		public void setActivityToUpdate(ListeAmis activityToUpdate) {
			this.activityToUpdate = activityToUpdate;
		}

		private HttpClient m_ClientHttp = new DefaultHttpClient();
		
		@Override
		protected ArrayList<Ami> doInBackground(Void... unused) {
			ArrayList<Ami> liste = null;
			
			try{
				URI uri = new URI("http",WEB_SERVICE_URL,"/get-friends", "username=" + user_id.toString() + "&withInfo=1", null);
				HttpGet getMethod = new HttpGet(uri);
				
				String body = m_ClientHttp.execute(getMethod, new BasicResponseHandler());
				liste = JSONParser.parseFriendList(body);
			}
			catch(Exception e){
				Log.i("GetFriendsError",e.getCause().toString());
			}
			
			return liste;
		}
		
		@Override
		protected void onPostExecute(ArrayList<Ami> liste){
			activityToUpdate.setM_Tokens(liste);
			for(Ami ami: liste){
				activityToUpdate.getM_RowModels().add(new RowModel(ami.get_nom() + " " + ami.get_prenom(),ami.get_id(),false));
			}
			
			if(activityToUpdate.getIntent().getExtras() != null && 
					activityToUpdate.getIntent().getExtras().containsKey("EXTRA_MEETUP_AMI") && 
					activityToUpdate.getIntent().getExtras().getStringArrayList("EXTRA_MEETUP_AMI") != null){ 
				for(RowModel ligne : activityToUpdate.getM_RowModels())
				{
					for(String ami : activityToUpdate.getIntent().getExtras().getStringArrayList("EXTRA_MEETUP_AMI")){
						if (ligne.getKey().equals(ami)){
							ligne.setIsActivate(true);
						}
					}
				}
			}
			
		}
	}
	
	public static class AsyncAddMeetUp extends AsyncTask<Void, Void, String>{
		private String owner;
		private String securityNumber;
		private String name;
		private String location;
		private String duration;
		private String lowerDate;
		private String lowerTime;
		private String upperDate;
		private String upperTime;
		
		private HttpClient m_ClientHttp = new DefaultHttpClient();
		
		@Override
		protected String doInBackground(Void... unused) {
			String meetUpKey = "";
			
			try{
				URI uri = new URI("http",WEB_SERVICE_URL,"/add-meetUp",
						"moi=" + this.owner +
						"&password=" + this.securityNumber +
						"&nom=" + this.name +
						"&lieu=" + this.location +
						"&duree=" + this.duration +
						"&heureMin=" + this.lowerTime +
						"&heureMax=" + this.upperTime +
						"&dateMin=" + this.lowerDate +
						"&dateMax=" + this.upperDate, null);
				HttpGet getMethod = new HttpGet(uri);
				
				Log.i("AddMeetUp","URI : " + uri);
				
				String body = m_ClientHttp.execute(getMethod,new BasicResponseHandler());
				meetUpKey = JSONParser.parseSingleString(body, "key");
			}
			catch(Exception e){
				Log.i("AddMeetUpError",e.getMessage());
			}
			return meetUpKey;
		}
		
		public String getOwner() {
			return owner;
		}

		public void setOwner(String owner) {
			this.owner = owner;
		}

		public String getSecurityNumber() {
			return securityNumber;
		}

		public void setSecurityNumber(String securityNumber) {
			this.securityNumber = securityNumber;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public String getDuration() {
			return duration;
		}

		public void setDuration(String duration) {
			this.duration = duration;
		}

		public String getLowerDate() {
			return lowerDate;
		}

		public void setLowerDate(String lowerDate) {
			this.lowerDate = lowerDate;
		}

		public String getLowerTime() {
			return lowerTime;
		}

		public void setLowerTime(String lowerTime) {
			this.lowerTime = lowerTime;
		}

		public String getUpperDate() {
			return upperDate;
		}

		public void setUpperDate(String upperDate) {
			this.upperDate = upperDate;
		}

		public String getUpperTime() {
			return upperTime;
		}

		public void setUpperTime(String upperTime) {
			this.upperTime = upperTime;
		}

		
	}
	
	public static class AsyncModifierMeetUp extends AsyncTask<Void, Void, String>{
		private String username;
		private String securityNumber;
		private String meetUpKey;
		private String nom;
		private String lieu;
		private String duree;
		private String dateMin;
		private String heureMin;
		private String dateMax;
		private String heureMax;
		
		private HttpClient m_ClientHttp = new DefaultHttpClient();
		
		@Override
		protected String doInBackground(Void... unused) {
			String message = "";
			
			try{
				URI uri = new URI("http",WEB_SERVICE_URL,"/edit-meetUp",
						"moi=" + this.username +
						"&password=" + this.securityNumber +
						"&meetUp=" + this.meetUpKey +
						"&nom=" + this.nom +
						"&lieu=" + this.lieu +
						"&duree=" + this.duree +
						"&heureMin=" + this.heureMin +
						"&heureMax=" + this.heureMax +
						"&dateMin=" + this.dateMin +
						"&dateMax=" + this.dateMax, null);
				Log.i("ModifierMeetUp",uri.toString());
				HttpGet getMethod = new HttpGet(uri);
				
				String body = m_ClientHttp.execute(getMethod,new BasicResponseHandler());
				message = JSONParser.parseSingleString(body, "message");
			}
			catch(Exception e){
				Log.i("ModifierMeetUpError",e.getMessage());
			}
			return message;
		}
		
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getSecurityNumber() {
			return securityNumber;
		}
		public void setSecurityNumber(String securityNumber) {
			this.securityNumber = securityNumber;
		}
		public String getMeetUpKey() {
			return meetUpKey;
		}
		public void setMeetUpKey(String meetUpKey) {
			this.meetUpKey = meetUpKey;
		}
		public String getNom() {
			return nom;
		}
		public void setNom(String nom) {
			this.nom = nom;
		}
		public String getLieu() {
			return lieu;
		}
		public void setLieu(String lieu) {
			this.lieu = lieu;
		}
		public String getDuree() {
			return duree;
		}
		public void setDuree(String duree) {
			this.duree = duree;
		}
		public String getDateMin() {
			return dateMin;
		}
		public void setDateMin(String dateMin) {
			this.dateMin = dateMin;
		}
		public String getHeureMin() {
			return heureMin;
		}
		public void setHeureMin(String heureMin) {
			this.heureMin = heureMin;
		}
		public String getDateMax() {
			return dateMax;
		}
		public void setDateMax(String dateMax) {
			this.dateMax = dateMax;
		}
		public String getHeureMax() {
			return heureMax;
		}
		public void setHeureMax(String heureMax) {
			this.heureMax = heureMax;
		}
	}
	
	public static class AsyncAddFriendToMeetUp extends AsyncTask<Void, Void, String>{
		private String owner;
		private String securityNumber;
		private String friendId;
		private String meetUpKey;
		
		private HttpClient m_ClientHttp = new DefaultHttpClient();
		
		@Override
		protected String doInBackground(Void... unused) {
			String message = "";
			
			try{
				URI uri = new URI("http",WEB_SERVICE_URL,"/invite-friend",
						"moi=" + this.owner +
						"&password=" + this.securityNumber +
						"&ami=" + friendId +
						"&meetUp=" + meetUpKey, null);
				Log.i("AddFriendToMeetUp",uri.toString());
				HttpGet getMethod = new HttpGet(uri);
				
				String body = m_ClientHttp.execute(getMethod,new BasicResponseHandler());
				message = JSONParser.parseSingleString(body, "message");
			}
			catch(Exception e){
				Log.i("AddFriendToMeetUpError",e.getMessage());
			}
			return message;
		}
		
		public String getOwner() {
			return owner;
		}
		public void setOwner(String owner) {
			this.owner = owner;
		}
		public String getSecurityNumber() {
			return securityNumber;
		}
		public void setSecurityNumber(String securityNumber) {
			this.securityNumber = securityNumber;
		}
		public String getFriendId() {
			return friendId;
		}
		public void setFriendId(String friendId) {
			this.friendId = friendId;
		}
		public String getMeetUpKey() {
			return meetUpKey;
		}
		public void setMeetUpKey(String meetUpKey) {
			this.meetUpKey = meetUpKey;
		}
	}
	
	public static class AsyncGetMyMeetUp extends AsyncTask<Void, Void, ArrayList<MeetUp>>{
		private String username;
		private String securityNumber;
		private FragmentMeetUp fragmentToUpdate;
		
		private HttpClient m_ClientHttp = new DefaultHttpClient();
		
		@Override
		protected ArrayList<MeetUp> doInBackground(Void... unused) {
			ArrayList<MeetUp> meetUp = null;
			
			try{
				URI uri = new URI("http",WEB_SERVICE_URL,"/list-meetUp",
						"moi=" + this.username +
						"&password=" + this.securityNumber +
						"&withInfo=1"
						, null);
				Log.i("GetMeetUp", uri.toString());
				HttpGet getMethod = new HttpGet(uri);
				
				String body = m_ClientHttp.execute(getMethod,new BasicResponseHandler());
				meetUp = JSONParser.parseMeetUpList(body);
			}
			catch(Exception e){
				Log.i("GetMyMeetUpsError",e.getMessage());
			}
			return meetUp;
		}
		
		@Override
		protected void onPostExecute(ArrayList<MeetUp> listeMeetUp){
			fragmentToUpdate.setMesMeetUp(listeMeetUp);
			fragmentToUpdate.set_monEvenementAdapter(fragmentToUpdate.new LigneMonEvenementAdapter());
			fragmentToUpdate.getListeMesMeetUp().setAdapter(fragmentToUpdate.get_monEvenementAdapter());
			
			for(MeetUp token : fragmentToUpdate.getMesMeetUp()){
				String message;
				if(token.get_invites().size() != 1)
					message = " invités participents.";
				else
					message = " invité participe.";
				fragmentToUpdate.getRowMonEvenementModels().add(new RowMonEvenementModel(
						token.get_nom(),
						token.get_invites().size() + message, 
						token.get_lieu(),
						token.get_dateMin(),
						token.get_heureMin()+"",
						token.get_dateMax(),
						token.get_heureMax()+""));
			}
			
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getSecurityNumber() {
			return securityNumber;
		}

		public void setSecurityNumber(String securityNumber) {
			this.securityNumber = securityNumber;
		}

		public FragmentMeetUp getFragmentToUpdate() {
			return fragmentToUpdate;
		}

		public void setFragmentToUpdate(FragmentMeetUp fragmentToUpdate) {
			this.fragmentToUpdate = fragmentToUpdate;
		}
	}
	
	public static class AsyncDeleteMeetUp extends AsyncTask<Void, Void, String>{

		private String username;
		private String securityNumber;
		private String meetUpId;
		
		private HttpClient m_ClientHttp = new DefaultHttpClient();
		
		@Override
		protected String doInBackground(Void... params) {
			String message = "";
			
			try{
				URI uri = new URI("http",WEB_SERVICE_URL,"/delete-meetUp",
						"moi=" + username +
						"&password=" + securityNumber +
						"&supprime=" + meetUpId, null);
				HttpGet getMethod = new HttpGet(uri);
				String body = m_ClientHttp.execute(getMethod,new BasicResponseHandler());
				message = JSONParser.parseSingleString(body, "message");
			}
			catch(Exception e){Log.i("GetMyMeetUpsError",e.getMessage());}
			
			return message;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getSecurityNumber() {
			return securityNumber;
		}

		public void setSecurityNumber(String securityNumber) {
			this.securityNumber = securityNumber;
		}

		public String getMeetUpId() {
			return meetUpId;
		}

		public void setMeetUpId(String meetUpId) {
			this.meetUpId = meetUpId;
		}
		
	}
	
	public static class AsyncGetMyMeetUpInvitations extends AsyncTask<Void, Void, ArrayList<String>>{
		private String username;
		private String securityNumber;
		private FragmentMeetUp fragmentToUpdate;
		
		private HttpClient m_ClientHttp = new DefaultHttpClient();
		
		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			ArrayList<String> listeMeetUpKey = null;
			
			try{
				URI uri = new URI("http",WEB_SERVICE_URL,"/get-list-demande-meetUp",
						"username=" + username +
						"&password=" + securityNumber,
						null);
				HttpGet getMethod = new HttpGet(uri);
				String body = m_ClientHttp.execute(getMethod,new BasicResponseHandler());
				listeMeetUpKey = JSONParser.parseSingleArrayString(body, "demandes");
			}
			catch(Exception e){Log.i("GetMyMeetUpInvitationsError",e.getMessage());}
			
			return listeMeetUpKey;
		}

		@Override
		protected void onPostExecute(ArrayList<String> keys){
			
			if(fragmentToUpdate.getMesInvitations() == null)
				fragmentToUpdate.setMesInvitations(new ArrayList<MeetUp>());
			
			fragmentToUpdate.set_invitationAdapter(fragmentToUpdate.new LigneInvitationAdapter());
			fragmentToUpdate.getListeMesInvitations().setAdapter(fragmentToUpdate.get_invitationAdapter());
			
			for(String key:keys){
				network.AsyncGetMeetUpInformations asyncGetInformations = new network.AsyncGetMeetUpInformations();
				asyncGetInformations.setUsername(fragmentToUpdate.getUsager().get_googleId());
				asyncGetInformations.setSecurityNumber(fragmentToUpdate.getUsager().get_securityNumber());
				asyncGetInformations.setMeetUpKey(key);
				asyncGetInformations.setFragmentToUpdate(fragmentToUpdate);
				asyncGetInformations.setAccepted(false);
				asyncGetInformations.execute((Void)null);
			}
		}
		
		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getSecurityNumber() {
			return securityNumber;
		}

		public void setSecurityNumber(String securityNumber) {
			this.securityNumber = securityNumber;
		}

		public FragmentMeetUp getFragmentToUpdate() {
			return fragmentToUpdate;
		}

		public void setFragmentToUpdate(FragmentMeetUp fragmentToUpdate) {
			this.fragmentToUpdate = fragmentToUpdate;
		}
		
	}
	
	public static class AsyncGetMeetUpInformations extends AsyncTask<Void, Void, MeetUp>{
		
		private String username;
		private String securityNumber;
		private String meetUpKey;
		private Boolean getAccepted;
		
		private FragmentMeetUp fragmentToUpdate;
		
		private HttpClient m_ClientHttp = new DefaultHttpClient();
		
		@Override
		protected MeetUp doInBackground(Void... unused) {
			MeetUp meetup = null;
			
			try{
				URI uri = new URI("http",WEB_SERVICE_URL,"/info-meetUp",
						"meetUp=" + meetUpKey, null);
				Log.i("GetMeetUpInformation", uri.toString());
				HttpGet getMethod = new HttpGet(uri);
				String body = m_ClientHttp.execute(getMethod,new BasicResponseHandler());
				meetup = JSONParser.parseMeetUp(body);
			}
			catch(Exception e){Log.i("GetMeetUpInformationError",e.getMessage());}
			
			return meetup;
		}
		
		@Override
		protected void onPostExecute(MeetUp meetup){
			
			if(fragmentToUpdate.getMesInvitations() == null)
				fragmentToUpdate.setMesInvitations(new ArrayList<MeetUp>());
			
			fragmentToUpdate.getMesInvitations().add(new MeetUp(meetup));
			int messageId;
			if(getAccepted)
				messageId = R.string.participe;
			else
				messageId = R.string.choisirParticipation;
			
			fragmentToUpdate.getRowInvitationModels().add(new RowInvitationModel(
					meetup.get_nom(), 
					fragmentToUpdate.getResources().getString(messageId),
					meetup.get_lieu(),
					meetup.get_dateMin(),
					meetup.get_heureMin()+"h",
					meetup.get_dateMax(),
					meetup.get_heureMax()+"h"));
			
			fragmentToUpdate.get_invitationAdapter().notifyDataSetChanged();
		}
		
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getSecurityNumber() {
			return securityNumber;
		}
		public void setSecurityNumber(String securityNumber) {
			this.securityNumber = securityNumber;
		}
		public String getMeetUpKey() {
			return meetUpKey;
		}
		public void setMeetUpKey(String meetUpKey) {
			this.meetUpKey = meetUpKey;
		}

		public FragmentMeetUp getFragmentToUpdate() {
			return fragmentToUpdate;
		}

		public void setFragmentToUpdate(FragmentMeetUp fragmentToUpdate) {
			this.fragmentToUpdate = fragmentToUpdate;
		}

		public Boolean getAccepted() {
			return getAccepted;
		}

		public void setAccepted(Boolean getAccepted) {
			this.getAccepted = getAccepted;
		}
		
	}
	
	public static class AsyncAcceptMeetUpParticipation extends AsyncTask<Void, Void, String>{
		private String username;
		private String securityNumber;
		private String meetUpKey;
		
		private HttpClient m_ClientHttp = new DefaultHttpClient();
		
		@Override
		protected String doInBackground(Void... unused) {
			String message = "";
			
			try{
				URI uri = new URI("http",WEB_SERVICE_URL,"/accept-meetUp",
						"moi=" + username +
						"&password=" + securityNumber +
						"&meetUp=" + meetUpKey, null);
				
				HttpGet getMethod = new HttpGet(uri);
				String body = m_ClientHttp.execute(getMethod,new BasicResponseHandler());
				message = JSONParser.parseSingleString(body, "message");
			}
			catch(Exception e){Log.i("GetAcceptMeetUpParticipationError",e.getMessage());}
			
			return message;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getSecurityNumber() {
			return securityNumber;
		}

		public void setSecurityNumber(String securityNumber) {
			this.securityNumber = securityNumber;
		}

		public String getMeetUpKey() {
			return meetUpKey;
		}

		public void setMeetUpKey(String meetUpKey) {
			this.meetUpKey = meetUpKey;
		}
	}
	
	public static class AsyncDeclineMeetUpParticipation extends AsyncTask<Void, Void, String>{
		private String username;
		private String securityNumber;
		private String meetUpId;
		
		private HttpClient m_ClientHttp = new DefaultHttpClient();
		
		@Override
		protected String doInBackground(Void... unused) {
			String message = "";
			
			try{
				URI uri = new URI("http",WEB_SERVICE_URL,"/refuse-meetUp",
						"moi=" + username +
						"&password=" + securityNumber +
						"&meetUp=" + meetUpId, null);
				Log.i("DeclineMeetUpParticipation",uri.toString());
				HttpGet getMethod = new HttpGet(uri);
				String body = m_ClientHttp.execute(getMethod,new BasicResponseHandler());
				message = JSONParser.parseSingleString(body, "message");
			}
			catch(Exception e){Log.i("GetDeclineMeetUpParticipationError",e.getMessage());}
			
			return message;
		}
		
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getSecurityNumber() {
			return securityNumber;
		}
		public void setSecurityNumber(String securityNumber) {
			this.securityNumber = securityNumber;
		}
		public String getMeetUpId() {
			return meetUpId;
		}
		public void setMeetUpId(String meetUpId) {
			this.meetUpId = meetUpId;
		}
	}
	
	public static class AsyncGetMeetUpAccepted extends AsyncTask<Void, Void, ArrayList<String>>{
		private String username;
		private FragmentMeetUp fragmentToUpdate;
		private HttpClient m_ClientHttp = new DefaultHttpClient();
		
		@Override
		protected ArrayList<String> doInBackground(Void... unused) {
			ArrayList<String> message = null;
			
			try{
				URI uri = new URI("http",WEB_SERVICE_URL,"/get-user-info",
						"username=" + username, null);
				
				HttpGet getMethod = new HttpGet(uri);
				String body = m_ClientHttp.execute(getMethod,new BasicResponseHandler());
				message = JSONParser.parseSingleArrayString(body, "listMeetUp");
			}
			catch(Exception e){Log.i("GetAcceptMeetUpParticipationError",e.getMessage());}
			
			return message;
		}
		
		@Override
		protected void onPostExecute(ArrayList<String> keys){
			if(fragmentToUpdate.getMesInvitations() == null)
				fragmentToUpdate.setMesInvitations(new ArrayList<MeetUp>());
			
			fragmentToUpdate.set_invitationAdapter(fragmentToUpdate.new LigneInvitationAdapter());
			fragmentToUpdate.getListeMesInvitations().setAdapter(fragmentToUpdate.get_invitationAdapter());
			
			for(String key:keys){
				network.AsyncGetMeetUpInformations asyncGetInformations = new network.AsyncGetMeetUpInformations();
				asyncGetInformations.setUsername(fragmentToUpdate.getUsager().get_googleId());
				asyncGetInformations.setSecurityNumber(fragmentToUpdate.getUsager().get_securityNumber());
				asyncGetInformations.setMeetUpKey(key);
				asyncGetInformations.setFragmentToUpdate(fragmentToUpdate);
				asyncGetInformations.setAccepted(true);
				asyncGetInformations.execute((Void)null);
			}
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public FragmentMeetUp getFragmentToUpdate() {
			return fragmentToUpdate;
		}

		public void setFragmentToUpdate(FragmentMeetUp fragmentToUpdate) {
			this.fragmentToUpdate = fragmentToUpdate;
		}
	}
	
	/*public static class askForFriend extends AsyncTask<String,String, JSONObject>{
		
		public Person p_user;
		public String p_nom;
		public String p_prenom;
		
		public Person getP_user() {
			return p_user;
		}
		public void setP_user(Person p_user) {
			this.p_user = p_user;
		}
		public String getP_nom() {
			return p_nom;
		}
		public void setP_nom(String p_nom) {
			this.p_nom = p_nom;
		}
		public String getP_prenom() {
			return p_prenom;
		}
		public void setP_prenom(String p_prenom) {
			this.p_prenom = p_prenom;
		}
		
		@Override
		protected JSONObject doInBackground(String... params) {
			// Aller chercher toutes les personnes sur le service
			JSONParser parser = new JSONParser();
			String url = "http://appmeetup.appspot.com/get-users?username=" + p_user.getId();
			JSONObject json = parser.getJSONFromUrl(url);
			
			try {
				JSONArray jsonArray = new JSONArray(json.toString());
				
				boolean found = false;
				int i = 0;
				while( i < jsonArray.length() && !found){
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					if(jsonObject.getString("nom") == p_nom && jsonObject.getString("prenom") == p_prenom)
						found = true;
					i++;
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			// Trouver la personne ajout�e
			
			// Envoyer la demande � la personne trouv�e
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json){
			
		}
	}*/
}
