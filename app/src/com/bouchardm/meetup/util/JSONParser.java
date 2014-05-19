package com.bouchardm.meetup.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bouchardm.meetup.classes.Ami;
import com.bouchardm.meetup.classes.MeetUp;

import android.util.Log;
public class JSONParser {
  
	private static final String MSG_RESULT = "result";
	private static final String MSG_SUCCESS = "success";
	private static final String MSG_ERROR = "error";
	
	public static ArrayList<Ami> parseFriendList(String p_body) throws JSONException{
		ArrayList<Ami> listeAmis = null;
		JSONObject json = new JSONObject(p_body);
		
		if(json.getString(MSG_RESULT).equals(MSG_SUCCESS)){
			listeAmis = new ArrayList<Ami>();
			JSONArray tab = json.getJSONArray("amis");
			for (int i = 0; i < tab.length(); i++) {
				JSONObject ami = tab.getJSONObject(i);
				listeAmis.add(new Ami(
						ami.getString("username"),
						ami.getString("nom"),
						ami.getString("prenom")));				
			}
		}
		else
		{
			Log.w("parseListePersonne", "No success from web service : " + p_body);
		}
		
		return listeAmis;
	}
	
	public static ArrayList<MeetUp> parseMeetUpList(String p_body) throws JSONException{
		ArrayList<MeetUp> listeMeetUp = null;
		JSONObject json = new JSONObject(p_body);
		
		if(json.getString(MSG_RESULT).equals(MSG_SUCCESS)){
			listeMeetUp = new ArrayList<MeetUp>();
			JSONArray tab = json.getJSONArray("listMeetUp");
			for(int i = 0; i < tab.length(); i++){
				listeMeetUp.add(new MeetUp());
				JSONObject meetUp = tab.getJSONObject(i);
				if(meetUp != null){
					listeMeetUp.get(i).set_nom(meetUp.getString("nom"));
					listeMeetUp.get(i).set_lieu(meetUp.getString("lieu"));
					listeMeetUp.get(i).set_duree(meetUp.getInt("duree"));
					listeMeetUp.get(i).set_heureMin(meetUp.getInt("heureMin"));
					listeMeetUp.get(i).set_heureMax(meetUp.getInt("heureMax"));
					listeMeetUp.get(i).set_dateMin(meetUp.getString("dateMin"));
					listeMeetUp.get(i).set_dateMax(meetUp.getString("dateMax"));
					listeMeetUp.get(i).set_id(meetUp.getString("key"));
					ArrayList<Ami> amis = new ArrayList<Ami>();
					JSONArray participants = meetUp.getJSONArray("participant");
					if(participants != null && participants.length() > 0){
						for(int j = 0; j < participants.length(); j++){
							JSONObject participant = participants.getJSONObject(j);
							
							amis.add(new Ami());
							amis.get(j).set_id(participant.getString("username"));
							amis.get(j).set_nom(participant.getString("nom"));
							amis.get(j).set_prenom(participant.getString("prenom"));
						}
					}
					listeMeetUp.get(i).set_invites(amis);
				}
			}
		}
		
		
		return listeMeetUp;
	}
	
	public static MeetUp parseMeetUp(String p_body) throws JSONException{
		MeetUp meetup = null;
		
		JSONObject json = new JSONObject(p_body);
		if(json.getString(MSG_RESULT).equals(MSG_SUCCESS)){
			meetup = new MeetUp();
			JSONObject info = json.getJSONObject("info");
			meetup.set_nom(info.getString("nom"));
			meetup.set_lieu(info.getString("lieu"));
			meetup.set_duree(info.getInt("duree"));
			meetup.set_heureMin(info.getInt("heureMin"));
			meetup.set_heureMax(info.getInt("heureMax"));
			meetup.set_dateMin(info.getString("dateMin"));
			meetup.set_dateMax(info.getString("dateMax"));
			meetup.set_id(info.getString("key"));
			ArrayList<Ami> amis = new ArrayList<Ami>();
			JSONArray participants = info.getJSONArray("listeParticipant");
			if(participants != null && participants.length() > 0){
				for(int i = 0; i < participants.length(); i++){
					JSONObject participant = participants.getJSONObject(i);
					amis.add(new Ami());
					amis.get(i).set_id(participant.getString("username"));
					amis.get(i).set_nom(participant.getString("nom"));
					amis.get(i).set_prenom(participant.getString("prenom"));
				}
			}
		}
		
		return meetup;
	}
	
	public static ArrayList<String> parseSingleArrayString(String p_body, String nodeName) throws JSONException{
		ArrayList<String> listeResultat = null;
		JSONObject json = new JSONObject(p_body);
		
		if(json.getString(MSG_RESULT).equals(MSG_SUCCESS)){
			listeResultat = new ArrayList<String>();
			JSONArray tab = json.getJSONArray(nodeName);
			for(int i = 0; i < tab.length(); i++){
				listeResultat.add(tab.getString(i));
			}
		}
		return listeResultat;
	}
	
	public static String parseSingleString(String p_body, String nodeName) throws JSONException{
		String message = "";
		JSONObject json = new JSONObject(p_body);
		
		if(json.getString(MSG_RESULT).equals(MSG_SUCCESS))
			message = json.getString(nodeName);
		else
			message = json.getString("message");
		
		return message;
	}
}