package com.bouchardm.meetup.classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.bouchardm.meetup.util.JSONParser;
import com.google.android.gms.plus.model.people.Person;

public class network {
	
	public static class addUser extends AsyncTask<Void,Void, String>{
		public String p_sha1;
		public Person p_user;
		
		public String message;
		
		public HttpClient m_ClientHttp = new DefaultHttpClient();
		
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
			
			String serviceCall = "/add-user?username=" + p_user.getId() + 
					"&password=" + p_sha1 +
					"&nom=" + p_user.getName().getFamilyName() + 
					"&prenom=" + p_user.getName().getGivenName();
			
			try{
				URI uri = new URI("HTTP","appmeetup.appspot.com", "/add-user", 
						"username=" + p_user.getId() + 
						"&password=" + p_sha1 +
						"&nom=" + p_user.getName().getFamilyName() + 
						"&prenom=" + p_user.getName().getGivenName() , null );
				
				Log.i("AddUser", "URL : " + uri.toString());
				HttpGet getMethod = new HttpGet(uri);
				
				String body = m_ClientHttp.execute(getMethod, new BasicResponseHandler());
				Log.i("AddUser", "Résultat : " + body);
				
				googleKey = JSONParser.parseGoogleKey(body);
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
