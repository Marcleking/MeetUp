package com.bouchardm.meetup.classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.google.android.gms.plus.model.people.Person;

public class network {
	
	public static String addUser(Person p_user, String p_sha1){
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://appmeetup.appspot.com/add-user?username=" + p_user.getId() + 
				"&password=" + p_sha1 +
				"&nom=" + p_user.getName().getFamilyName() + 
				"&prenom=" + p_user.getName().getGivenName());
		try{
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if(statusCode == 200){
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while((line = reader.readLine()) != null){
					builder.append(line);
				}
			}
			else{
				Log.e("AddUser", "Failed to add user");
			}
		}
		catch (ClientProtocolException e){
			e.printStackTrace();
		}
		catch (IOException e){
			e.printStackTrace();
		}
		
		String message = "";
		try{
			JSONArray jsonArray = new JSONArray (builder.toString());
			for (int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				message = jsonObject.getString("result");
			}
		}
		catch(Exception e){
			message = "error";
			e.printStackTrace();
		}
		return message;
	}
	
}
