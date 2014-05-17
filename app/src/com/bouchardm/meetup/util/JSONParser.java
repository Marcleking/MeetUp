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