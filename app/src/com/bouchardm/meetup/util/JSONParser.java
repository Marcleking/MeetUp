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
	
	public static ArrayList<String> parseFriendList(String p_body) throws JSONException{
		ArrayList<String> listeAmis = null;
		JSONObject json = new JSONObject(p_body);
		
		if(json.getString(MSG_RESULT).equals(MSG_SUCCESS)){
			listeAmis = new ArrayList<String>();
			JSONArray tab = json.getJSONArray("amis");
			for (int i = 0; i < tab.length(); i++) {
				listeAmis.add(tab.get(i).toString());				
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