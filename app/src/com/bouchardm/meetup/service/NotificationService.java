package com.bouchardm.meetup.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bouchardm.meetup.ConnectionActivity;
import com.bouchardm.meetup.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class NotificationService extends Service {
	public final static String WORD_UPDATE = "com.bouchardm.meetup.service.WORD_UPDATE";
	private String idGoogle;
	private String passwordGoogle;
	WordRunner     m_Runner;
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
			
		if (idGoogle == null && passwordGoogle == null) {
			idGoogle = intent.getStringExtra("idGoogle");
			passwordGoogle = intent.getStringExtra("passwordGoogle");
		}
		
		// Objet permettant de faire un traitement lourd.
		
		m_Runner = new WordRunner(idGoogle, passwordGoogle);
		
		// Démarrage du traitement lourd dans un autre Thread.
		new Thread(m_Runner).start();
		
		return START_REDELIVER_INTENT;
	}
	
	
	
	@Override
	public IBinder onBind(Intent p_intent) {
		// Objet permettant à l'activité de communiquer avec le service.
		return new WordServiceBinder();
	}
	
	// Appelé par le Binder (ci-dessous).
	private void requestToStop() {
		if (m_Runner != null) {
			// Arrêt de la boucle infinie qui génère des mots.
			m_Runner.requestToStop();
			m_Runner = null;
		}
	}
	
	private void requestToStart() {
		this.onCreate();
	}
	
	// Classe interne pour le traitement lourd
	// =======================================
	private class WordRunner implements Runnable {
		private NotificationManager m_NotificationMgr;
		private int NOTIF_ID = 1234;
		private String idGoogle;
		private String passwordGoogle;
		
		private AtomicBoolean isRunning = new AtomicBoolean(true);
		
		public void requestToStop() {
			this.isRunning.set(false);
		}
		
		public void requestToStart() {
			this.isRunning.set(true);
		}
		
		public WordRunner(String idGoogle, String passwordGoogle) {
			this.idGoogle = idGoogle;
			this.passwordGoogle = passwordGoogle;
		}
		
		@Override
		public void run() {
			m_NotificationMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			int i = 0;
			
			// Ça roule tant que la méthode "requestToStop()" de la classe courante n'est pas appelée.
			while (isRunning.get())
		    {
		       try {
		    	   sendNotification();
		    	   // Un petite pause de 5 secondes entre la génération de mots.
	    		   Thread.sleep(60000);
	    		   
				} catch (Exception e) {
					Log.e("WordService", Log.getStackTraceString(e));
				}
		    }
		}
		
		private void sendWordBroadcast() {
			Intent intent = new Intent(WORD_UPDATE);
			NotificationService.this.sendBroadcast(intent);
		}

		@SuppressWarnings("deprecation")
		private void sendNotification() {
			new AsyncHttpGetNotification().execute("http://appmeetup.appspot.com/read-notif?moi="+this.idGoogle+"&password="+this.passwordGoogle+"");
		}
		
		public class AsyncHttpGetNotification extends AsyncTask<String, Void, String> {
		    public String getHttpRequest(String url){
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
		    protected String doInBackground(String... url) {
		        return getHttpRequest(url[0]);
		    }
		    
		    @Override
		    protected void onPostExecute(String result) {
		    	
		    	try {
					JSONObject info = new JSONObject(result);
					JSONArray listeNotification = info.getJSONArray("notif");
					
					if (listeNotification.length() > 0) {
						for (int i = 0; i < listeNotification.length(); i++) {
							String title = "MeetUp";
							Notification note = new Notification(R.drawable.ic_launcher, title, System.currentTimeMillis());
							note.flags |= Notification.FLAG_AUTO_CANCEL;
							note.defaults |= Notification.DEFAULT_SOUND;
							note.defaults |= Notification.DEFAULT_VIBRATE;
							
							
							Intent intent = new Intent(NotificationService.this, ConnectionActivity.class);
							intent.setAction("android.intent.action.MAIN");
							intent.addCategory("android.intent.category.LAUNCHER");
							
							PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, intent, 0);
							
							note.setLatestEventInfo(NotificationService.this, title, listeNotification.getString(i), pendingIntent);
							
							m_NotificationMgr.notify(NOTIF_ID++, note);
						}
					}
				} catch (JSONException e) {}
		    	
		    }
		    

			private String convertInputStreamToString(InputStream inputStream) throws IOException{
			    BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
			    String line = "";
			    String result = "";
			    while((line = bufferedReader.readLine()) != null)
			        result += line;
			
			    inputStream.close();
			    return result;
			
			}
		}
	}
	
	// Classe interne pour permettre à l'activité de communiquer avec le service
	// =========================================================================
	public class WordServiceBinder extends Binder {
		public void requestToStop() {
			NotificationService.this.requestToStop();
		}
		
		public void requestToStart() {
			NotificationService.this.requestToStart();
		}
	}
	
	
	
	
}

