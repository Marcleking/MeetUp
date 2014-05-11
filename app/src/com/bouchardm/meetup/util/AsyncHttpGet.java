package com.bouchardm.meetup.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.os.AsyncTask;

public class AsyncHttpGet extends AsyncTask<String, Void, ArrayList<String>> {
    public static String getHttpRequest(String url){
        InputStream inputStream = null;
        String reponse = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            
            HttpResponse response = httpclient.execute(new HttpGet(url));
            inputStream = response.getEntity().getContent();
            reponse = convertInputStreamToString(inputStream);
 
        } catch (Exception e) {
        	reponse = e.getMessage();
        }
 
        return reponse;
    }
	
    @Override
    protected ArrayList<String> doInBackground(String... url) {
    	
    	ArrayList<String> reponse = new ArrayList<String>();
    	
    	for (int i = 0; i < url.length; i++) {
    		reponse.add(getHttpRequest(url[i]));
    	}
    	
        return reponse;
    }
    

	private static String convertInputStreamToString(InputStream inputStream) throws IOException{
	    BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
	    String line = "";
	    String result = "";
	    while((line = bufferedReader.readLine()) != null)
	        result += line;
	
	    inputStream.close();
	    return result;
	
	}
}
