package com.bouchardm.meetup.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
  
	private static final String MSG_KEY = "key";
	private static final String MSG_RESULT = "result";
	private static final String MSG_SUCCESS = "success";
	private static final String MSG_ERROR = "error";
	
	public static String parseGoogleKey(String p_body) throws JSONException {
		String googleKey = "";
		JSONObject json = new JSONObject(p_body);
		
		if(json.getString(MSG_RESULT).equals(MSG_SUCCESS)){
			//JSONObject obj = json.getJSONObject("key");
			googleKey = json.getString(MSG_KEY);
		}
		else
		{
			googleKey = json.getString("message");
		}
		
		return googleKey;
	}
	
}