package com.bouchardm.meetup.classes;

import java.net.URI;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

import com.bouchardm.meetup.util.JSONParser;
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
	public static class AsyncGetFriends extends AsyncTask<Void, Void, ArrayList<String>>{
		
		private String user_id;
		
		public String getUser_id() {
			return user_id;
		}

		public void setUser_id(String user_id) {
			this.user_id = user_id;
		}

		private HttpClient m_ClientHttp = new DefaultHttpClient();
		
		@Override
		protected ArrayList<String> doInBackground(Void... unused) {
			ArrayList<String> liste = null;
			
			try{
				URI uri = new URI("http",WEB_SERVICE_URL,"/get-friends", "username=" + user_id.toString(), null);
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
		protected void onPostExecute(ArrayList<String> liste){
			String message = "Liste d'amis : ";
			if(liste != null){
				for(int i = 0; i < liste.size(); i++){
					message += liste.get(i) + ", ";
				}
			}
			Log.i("GetFriends",message);
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
				
				HttpGet getMethod = new HttpGet(uri);
				
				String body = m_ClientHttp.execute(getMethod,new BasicResponseHandler());
				message = JSONParser.parseSingleString(body, "message");
			}
			catch(Exception e){
				Log.i("AddMeetUpError",e.getCause().toString());
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
			
			// Trouver la personne ajoutée
			
			// Envoyer la demande à la personne trouvée
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json){
			
		}
	}*/
}
