package com.bouchardm.meetup.service;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import com.bouchardm.meetup.ConnectionActivity;
import com.bouchardm.meetup.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class NotificationService extends Service {
	public final static String WORD_UPDATE = "com.bouchardm.meetup.service.WORD_UPDATE";
	
	public final static String EXTRA_WORDS = "lesMots";
	
	ArrayList<String>   m_Words;
	WordRunner          m_Runner;
	
	@Override
	public void onCreate() {
		super.onCreate();
		// Liste de mots conservés par le service.
		m_Words = new ArrayList<String>();
		// Objet permettant de faire un traitement lourd.
		m_Runner = new WordRunner();
		// Démarrage du traitement lourd dans un autre Thread.
		new Thread(m_Runner).start();
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
	
	// Classe interne pour le traitement lourd
	// =======================================
	private class WordRunner implements Runnable {
		private NotificationManager m_NotificationMgr;
		private final static int NOTIF_ID = 1234;
		private AtomicBoolean isRunning = new AtomicBoolean(true);
		
		public void requestToStop() {
			this.isRunning.set(false);
		}
		
		@Override
		public void run() {
			m_NotificationMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			int i = 0;
			
			// Ça roule tant que la méthode "requestToStop()" de la classe courante n'est pas appelée.
			while (isRunning.get())
		    {
		       try {
		    	   synchronized (m_Words) {
		    		   // Ajout d'un nouveau mot.
		    		   m_Words.add("Word " + i++);
		    		   // Méthode local pour faire du broadcast.
		    		   sendWordBroadcast();
		    		   // Méthode local pour envoyer une notification.
		    		   sendNotification();
		    	   }
		    	   // Un petite pause de 5 secondes entre la génération de mots.
	    		   Thread.sleep(5000);
	    		   
				} catch (Exception e) {
					Log.e("WordService", Log.getStackTraceString(e));
				}
		    }
		}
		
		private void sendWordBroadcast() {
			Intent intent = new Intent(WORD_UPDATE);
			intent.putExtra(EXTRA_WORDS, m_Words);
			NotificationService.this.sendBroadcast(intent);
		}

		@SuppressWarnings("deprecation")
		private void sendNotification() {
			String title = "TITRE";
			Notification note = new Notification(R.drawable.ic_launcher, title, System.currentTimeMillis());
			
			Intent i = new Intent(NotificationService.this, ConnectionActivity.class);
			i.setAction("android.intent.action.MAIN");
			i.addCategory("android.intent.category.LAUNCHER");
			
			PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, i, 0);

			note.setLatestEventInfo(NotificationService.this, title, "NOTIFICATION LOL", pendingIntent);
			
			m_NotificationMgr.notify(NOTIF_ID, note);
			
			Toast.makeText(getApplicationContext(), "TEEEEESSTE", Toast.LENGTH_LONG).show();
		}
	}
	
	// Classe interne pour permettre à l'activité de communiquer avec le service
	// =========================================================================
	public class WordServiceBinder extends Binder {
		
		// Retourne une copie de la liste des mots.
		public ArrayList<String> getWords() {
			synchronized (m_Words) {
				return new ArrayList<String>(m_Words);
			}
		}
		
		public void requestToStop() {
			NotificationService.this.requestToStop();
		}
	}
	
}

