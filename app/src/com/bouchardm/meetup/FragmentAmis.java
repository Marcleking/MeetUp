package com.bouchardm.meetup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bouchardm.meetup.classes.Personne;
import com.bouchardm.meetup.classes.network;
import com.bouchardm.meetup.util.AsyncHttpGet;
import com.bouchardm.meetup.sqlite.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentAmis extends Fragment implements View.OnClickListener {
	
	/**
	 * Attributs
	 */
	private ExpandListAdapter ExpAdapter;
	private ArrayList<ListeGroupeModel> ExpListItems;
	private ExpandableListView ExpandList;
	
	private ArrayList<ListeGroupeModel> listeGroupe;
	private ArrayList<ListeAmiModel> listeAmi;
	
	private View rootView;
	
	private Button btnAjoutAmi;
	private Button btnAjoutGroupe;
	
	private Personne usager;
	
	public GoogleApiClient mGoogleApiClient;
	
	public GoogleApiClient getmGoogleApiClient() {
		return mGoogleApiClient;
	}

	public void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
		this.mGoogleApiClient = mGoogleApiClient;
	}

	public FragmentAmis(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.amis, container, false);
		
		ExpandList = (ExpandableListView) rootView.findViewById(R.id.ExpList);
		
		ExpListItems = SetStandardGroups();
		
		PersonneDataSource dataSource = new PersonneDataSource(getActivity());
		dataSource.open();
		usager = dataSource.getPersonne(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getId());
		dataSource.close();
		
		new AsyncGetAmi().execute("http://www.appmeetup.appspot.com/get-friends?username=" + usager.get_googleId());
		
		ExpAdapter = new ExpandListAdapter(rootView.getContext(), ExpListItems);
        ExpandList.setAdapter(ExpAdapter);
        
        this.registerForContextMenu(ExpandList);
		
        this.btnAjoutAmi = (Button)rootView.findViewById(R.id.btnAjoutAmis);
        this.btnAjoutAmi.setOnClickListener(this);
        this.btnAjoutGroupe = (Button)rootView.findViewById(R.id.btnAjoutGroupe);
        this.btnAjoutGroupe.setOnClickListener(this);
        
		return rootView;
	}

	
    /**
     * Gestion du context menu sur la liste
     */
    @Override
    public void onCreateContextMenu(android.view.ContextMenu menu, View v, android.view.ContextMenu.ContextMenuInfo menuInfo)
    {
    	super.onCreateContextMenu(menu, v, menuInfo);

    	  ExpandableListView.ExpandableListContextMenuInfo info =
    	    (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;

    	  //On va chercher les infos sur la ligne s�lectionner
    	  int type = ExpandableListView.getPackedPositionType(info.packedPosition);
    	  int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);
    	  int child = ExpandableListView.getPackedPositionChild(info.packedPosition);

    	  // On g�re que le context menu est cr�� pour les bonne ligne
    	  if (ExpListItems.get(group).getName() != "Demandes d'ami"){
	    	  if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
	    		  getActivity().getMenuInflater().inflate(R.menu.menu_ami, menu);
	    	  } else if (ExpListItems.get(group).getName() != "Mes amis") {
	    		  getActivity().getMenuInflater().inflate(R.menu.menu_groupe, menu);
	    	  }
    	  } else {
    		  if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD)
    			  getActivity().getMenuInflater().inflate(R.menu.menu_demande_ami, menu);
    	  }
    }
    
    @Override
    public void onClick(View v){
    	switch(v.getId()){
    	case R.id.btnAjoutAmis:
    		ajoutAmi(v);
    		break;
    	case R.id.btnAjoutGroupe:
    		ajoutGroupe(v);
    		break;
    	}
    }
    
    /**
     * Gestion de la selection d'un item dans le context menu
     */
    @Override
    public boolean onContextItemSelected(android.view.MenuItem item)
    {
    	ExpandableListView.ExpandableListContextMenuInfo info =
    		    (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
    	
    	//On va chercher les infos de la ligne
		int type = ExpandableListView.getPackedPositionType(info.packedPosition);
		int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);
		int child = ExpandableListView.getPackedPositionChild(info.packedPosition);
		
		//Si c'est un ami qui est s�lectionner
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD)
		{
			View ajoutGroupe = getActivity().getLayoutInflater().inflate(R.layout.amis_ajout_groupe, null);
			EditText txtGroupe = (EditText) ajoutGroupe.findViewById(R.id.btnAjoutGroupeText);
			
			BtnSetHandlerChangerAmiGroupe handlerHoraire = 
					new BtnSetHandlerChangerAmiGroupe(txtGroupe, group, child);
			
			switch(item.getItemId())
			{
				case R.id.menu_supprimerAmi:
					//Supprimer un ami
					ExpListItems.get(group).getItems().remove(child);
					ExpAdapter = new ExpandListAdapter(rootView.getContext(), ExpListItems);
					ExpandList.setAdapter(ExpAdapter);
					return true;
				case R.id.menu_changerGroupe:
					//On demande � l'utilisateur dans quel groupe il veut envoyer sont ami
					new AlertDialog.Builder(rootView.getContext())
						.setTitle("Entrez le nom du groupe")
						.setView(ajoutGroupe)
						.setNegativeButton("Annuler", null)
						.setPositiveButton("Changer", handlerHoraire)
						.show();
					
					return true;
				case R.id.menu_accepter:
					// Quand un utilisateur accepte un ami il est automatiquement placer dans Mes amis
					
					// on accepter l'ami sur le web service
					new AsyncHttpGet().execute("http://www.appmeetup.appspot.com/add-friend?moi=" + usager.get_googleId() + "&password=" + usager.get_securityNumber() + "&ajoute="+ExpListItems.get(group).getItem(child).getName());
					
					// on met � jour la liste
					ListeAmiModel amiTempo = FragmentAmis.this.ExpListItems.get(group).getItem(child);
					FragmentAmis.this.ExpListItems.get(group).getItems().remove(child);
					
					//Parcours de tout les groupes
					ArrayList<ListeGroupeModel> toutLesGroupes = FragmentAmis.this.ExpListItems;
					
					for (ListeGroupeModel expandListGroup : toutLesGroupes) {
						//Si le group est le m�me que celui entr� on met l'utilisateur dans ce groupe
						if(expandListGroup.getName().toString().equalsIgnoreCase("Mes amis")) {
							
							ArrayList<ListeAmiModel> listeAmiTempo = expandListGroup.getItems();
							listeAmiTempo.add(amiTempo);
							expandListGroup.setItems(listeAmiTempo);
							
						}
					}
					
					FragmentAmis.this.ExpListItems = toutLesGroupes;
					
					//Actualisation
					ExpAdapter = new ExpandListAdapter(rootView.getContext(), FragmentAmis.this.ExpListItems);
					ExpandList.setAdapter(ExpAdapter);
					
					
					
					return true;
				case R.id.menu_refuser:
					//Supprimer un n'ami
					ExpListItems.get(group).getItems().remove(child);
					ExpAdapter = new ExpandListAdapter(rootView.getContext(), ExpListItems);
					ExpandList.setAdapter(ExpAdapter);
					return true;
			  }
		//Si c'est un groupe qui a �t� s�lectionn�
		} else {
			switch(item.getItemId())
			{
				case R.id.menu_supprimerGroupe:
					//Supprimer un group
					ExpListItems.remove(group);
					ExpAdapter = new ExpandListAdapter(rootView.getContext(), ExpListItems);
					ExpandList.setAdapter(ExpAdapter);
					return true;
				case R.id.menu_modifier:
			  		
			  		View ajoutGroupe = getActivity().getLayoutInflater().inflate(R.layout.amis_ajout_groupe, null);
					EditText txtGroupe = (EditText) ajoutGroupe.findViewById(R.id.btnAjoutGroupeText);
					
					BtnSetHandlerChangerNomGroupe handlerHoraire = new BtnSetHandlerChangerNomGroupe(txtGroupe, group);
					
					//On demande � l'utilisateur le nouveau nom du groupe
					new AlertDialog.Builder(rootView.getContext())
						.setTitle("Entrez le nouveau nom du groupe")
						.setView(ajoutGroupe)
						.setNegativeButton("Annuler", null)
						.setPositiveButton("Changer", handlerHoraire)
						.show();
			  		
			  		return true;
			}
		}
    	return super.onContextItemSelected(item);
    }
    
    
    
    /**
	 * Gestion de l'enregistrement des donn�es (lors de la rotation)
	 */
	@Override
	public void onSaveInstanceState(Bundle p_outState) 
	{
		super.onSaveInstanceState(p_outState);
		
		p_outState.putParcelableArrayList("listeGroupe", ExpListItems);
	}
	
	/**
	 * Gestion de la restauration des donn�es (lors de la rotation)
	 */
	public void onRestoreInstanceState(Bundle p_state) 
	{
		
		if (p_state != null) {
			ExpListItems = p_state.getParcelableArrayList("listeGroupe");
			
			ExpAdapter = new ExpandListAdapter(rootView.getContext(), ExpListItems);
	        ExpandList.setAdapter(ExpAdapter);
		}
	}
    
    public ArrayList<ListeGroupeModel> SetStandardGroups() {
    	listeGroupe = new ArrayList<ListeGroupeModel>();
    	listeAmi = new ArrayList<ListeAmiModel>();
    	
    	ListeGroupeModel gru1 = new ListeGroupeModel();
        gru1.setName("Demandes d'ami");
        gru1.setItems(listeAmi);
        listeAmi = new ArrayList<ListeAmiModel>();
        
        ListeGroupeModel gru2 = new ListeGroupeModel();
        gru2.setName("Mes amis");
        gru2.setItems(listeAmi);
        listeGroupe.add(gru1);
        listeGroupe.add(gru2);
        
        return listeGroupe;
    }
    
	public static String convertInputStreamToString(InputStream inputStream) throws IOException{
	    BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
	    String line = "";
	    String result = "";
	    while((line = bufferedReader.readLine()) != null)
	        result += line;
	
	    inputStream.close();
	    return result;
	
	}
    
    /**
     * Gestion de l'ajout de groupe
     * @param source
     */
    public void ajoutGroupe(View source)
    {
    	View ajoutGroupe = getActivity().getLayoutInflater().inflate(R.layout.amis_ajout_groupe, null);
		EditText txtGroupe = (EditText) ajoutGroupe.findViewById(R.id.btnAjoutGroupeText);
		
		BtnSetHandlerGroupe handlerHoraire = new BtnSetHandlerGroupe(txtGroupe);
		
		//On demande � l'utilisateur le nom du groupe
		new AlertDialog.Builder(rootView.getContext())
			.setTitle("Ajouter un groupe")
			.setView(ajoutGroupe)
			.setNegativeButton("Annuler", null)
			.setPositiveButton("Ajouter", handlerHoraire)
			.show();
    }
    
	/**
	 * Handler pour la gestion du pop-up d'ajout de groupe
	 * @author Marcleking
	 *
	 */
	public class BtnSetHandlerGroupe implements DialogInterface.OnClickListener
	{
		/**
		 * Attributs
		 */
		private EditText m_txtGroupe;
		
		/**
		 * Constructeur
		 * @param p_txtHoraire
		 */
		public BtnSetHandlerGroupe (EditText p_txtGroupe)
		{
			this.m_txtGroupe = p_txtGroupe;
		}
		
		/**
		 * Gestion de l'enregistrement du groupe => actualise la liste
		 */
		@Override
		public void onClick(DialogInterface dialog, int which) {
			listeGroupe = new ArrayList<ListeGroupeModel>();
	    	listeAmi = new ArrayList<ListeAmiModel>();
	    	
	        ListeGroupeModel gru1 = new ListeGroupeModel();
	        gru1.setName(m_txtGroupe.getText().toString());
	        
	        gru1.setItems(listeAmi);
	        
	        ExpListItems.add(gru1);
	        
	        //Actualisation
	        ExpAdapter = new ExpandListAdapter(rootView.getContext(), ExpListItems);
	        ExpandList.setAdapter(ExpAdapter);
		}
	}
    
    /**
     * Gestion de l'ajout d'ami
     * @param source
     */
    public void ajoutAmi(View source)
    {
        View ajoutGroupe = getActivity().getLayoutInflater().inflate(R.layout.amis_ajout_groupe, null);
		EditText txtGroupe = (EditText) ajoutGroupe.findViewById(R.id.btnAjoutGroupeText);
		
		BtnSetHandlerAmi handlerHoraire = new BtnSetHandlerAmi(txtGroupe);
		
		//On demande le nom de l'ami � l'utilisateur
		new AlertDialog.Builder(source.getContext())
			.setTitle("Ajouter un ami")
			.setView(ajoutGroupe)
			.setNegativeButton("Annuler", null)
			.setPositiveButton("Ajouter", handlerHoraire)
			.show();
    }
    
    /******************
     * Les handlers
     *****************/
    
    /**
	 * Handler pour la gestion du pop-up d'ajout de groupe
	 * @author Marcleking
	 *
	 */
	public class BtnSetHandlerAmi implements DialogInterface.OnClickListener
	{
		/**
		 * Attributs
		 */
		private EditText m_txtGroupe;
		
		/**
		 * Constructeur
		 * @param p_txtHoraire
		 */
		public BtnSetHandlerAmi (EditText p_txtGroupe)
		{
			this.m_txtGroupe = p_txtGroupe;
		}
		
		/**
		 * Gestion de l'enregistrement du groupe => actualise la liste
		 */
		@Override
		public void onClick(DialogInterface dialog, int which) {
	        //Ajout du nouvel ami dans le web service
	        new AsyncHttpGet().execute("http://appmeetup.appspot.com/ask-friend?moi=" + usager.get_googleId() + "&password=" + usager.get_securityNumber() + "&demande="+m_txtGroupe.getText().toString());
		}
	}
	
	/**
	 * Handler pour la gestion du pop-up d'ajout de groupe
	 * @author Marcleking
	 *
	 */
	public class BtnSetHandlerChangerNomGroupe implements DialogInterface.OnClickListener
	{
		/**
		 * Attributs
		 */
		private EditText m_txtGroupe;
		private int m_ancienGroupe;
		
		/**
		 * Constructeur
		 * @param p_txtHoraire
		 */
		public BtnSetHandlerChangerNomGroupe (EditText p_txtGroupe, int ancienGroupe)
		{
			this.m_txtGroupe = p_txtGroupe;
			this.m_ancienGroupe = ancienGroupe;
		}
		
		/**
		 * Gestion de du changement de nom du groupe => actualise la liste
		 */
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			String nomGroupe = m_txtGroupe.getText().toString();
			
			if (!nomGroupe.equalsIgnoreCase("Demandes d'ami") && !nomGroupe.equalsIgnoreCase("Mes amis") &&
				!nomGroupe.trim().equalsIgnoreCase(""))
			{
				//Changement du nom
				ExpListItems.get(m_ancienGroupe).setName(nomGroupe);
				
				//Actualisation
				ExpAdapter = new ExpandListAdapter(rootView.getContext(), ExpListItems);
				ExpandList.setAdapter(ExpAdapter);
			} else {
				Toast.makeText(rootView.getContext(), "Vous ne pouvez pas mettre ce nom de groupe", Toast.LENGTH_LONG).show();
			}
		}
	}
    
    /**
	 * Handler pour la gestion du pop-up de changement de groupe
	 * @author Marcleking
	 *
	 */
	public class BtnSetHandlerChangerAmiGroupe implements DialogInterface.OnClickListener
	{
		/**
		 * Attributs
		 */
		private EditText m_txtGroupe;
		private int m_ancienGroupe;
		private int m_ancienAmi;
		
		/**
		 * Constructeur
		 * @param p_txtHoraire
		 */
		public BtnSetHandlerChangerAmiGroupe (EditText p_txtGroupe, int ancienGroupe, int ancienAmi)
		{
			this.m_txtGroupe = p_txtGroupe;
			this.m_ancienGroupe = ancienGroupe;
			this.m_ancienAmi = ancienAmi;
		}
		
		/**
		 * Gestion de la modification du groupe => actualise la liste
		 */
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			String nomGroupe = this.m_txtGroupe.getText().toString();
			
			if (!nomGroupe.equalsIgnoreCase("Demandes d'ami") && !nomGroupe.equalsIgnoreCase("Mes amis") &&
				!nomGroupe.trim().equalsIgnoreCase(""))
			{
				
				ListeAmiModel amiTempo = FragmentAmis.this.ExpListItems.get(this.m_ancienGroupe).getItem(this.m_ancienAmi);
				FragmentAmis.this.ExpListItems.get(this.m_ancienGroupe).getItems().remove(this.m_ancienAmi);
				
				boolean trouve = false;
			
				//Parcours de tout les groupes
				ArrayList<ListeGroupeModel> toutLesGroupes = FragmentAmis.this.ExpListItems;
				
				for (ListeGroupeModel expandListGroup : toutLesGroupes) {
					//Si le group est le m�me que celui entr� on met l'utilisateur dans ce groupe
					if(expandListGroup.getName().toString().equalsIgnoreCase(nomGroupe) && !trouve) {
						
						ArrayList<ListeAmiModel> listeAmiTempo = expandListGroup.getItems();
						listeAmiTempo.add(amiTempo);
						expandListGroup.setItems(listeAmiTempo);
						
						trouve = true;
					}
				}
				//Si on n'a pas trouver le groupe on le rajoute et on met l'user dedans
				if(!trouve) {
					ArrayList<ListeAmiModel> listeAmi = new ArrayList<ListeAmiModel>();
			    	listeAmi.add(amiTempo);
			    	
			        ListeGroupeModel gru1 = new ListeGroupeModel();
			        gru1.setName(nomGroupe);
			        
			        gru1.setItems(listeAmi);
			        
			        ExpListItems.add(gru1);
			       
				} else {
					ExpListItems = toutLesGroupes;
				}
				//Actualisation
				ExpAdapter = new ExpandListAdapter(rootView.getContext(), FragmentAmis.this.ExpListItems);
				ExpandList.setAdapter(ExpAdapter);
			} else {
				Toast.makeText(rootView.getContext(), "Vous ne pouvez pas mettre votre ami dans ce groupe", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	/**
	 * Adapter pour la liste d'ami	
	 * @author Marcleking
	 *
	 */
    public class ExpandListAdapter extends BaseExpandableListAdapter {

    	private Context context;
    	private ArrayList<ListeGroupeModel> groups;
    	
    	/**
    	 * Contructeur
    	 * @param context
    	 * @param groups
    	 */
    	public ExpandListAdapter(Context context, ArrayList<ListeGroupeModel> groups) {
    		this.context = context;
    		this.groups = groups;
    	}
    	
    	public void addItem(ListeAmiModel item, ListeGroupeModel group) {
    		if (!groups.contains(group)) {
    			groups.add(group);
    		}
    		int index = groups.indexOf(group);
    		ArrayList<ListeAmiModel> ch = groups.get(index).getItems();
    		ch.add(item);
    		groups.get(index).setItems(ch);
    	}
    	
    	public Object getChild(int groupPosition, int childPosition) {
    		ArrayList<ListeAmiModel> chList = groups.get(groupPosition).getItems();
    		return chList.get(childPosition);
    	}

    	public long getChildId(int groupPosition, int childPosition) {
    		return childPosition;
    	}

    	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view,
    			ViewGroup parent) {
    		ListeAmiModel child = (ListeAmiModel) getChild(groupPosition, childPosition);
    		if (view == null) {
    			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    			view = infalInflater.inflate(R.layout.amis_ligne_amis, null);
    		}
    		TextView ami = (TextView) view.findViewById(R.id.amiItem);
    		if(child != null) {
    			ami.setText(child.getName().toString());
    			ami.setTag(child.getTag());
    		}
    		return view;
    	}

    	public int getChildrenCount(int groupPosition) {
    		ArrayList<ListeAmiModel> chList = groups.get(groupPosition).getItems();
    		return chList.size();
    	}

    	public Object getGroup(int groupPosition) {
    		return groups.get(groupPosition);
    	}

    	public int getGroupCount() {
    		return groups.size();
    	}

    	public long getGroupId(int groupPosition) {
    		return groupPosition;
    	}

    	public View getGroupView(int groupPosition, boolean isLastChild, View view,
    			ViewGroup parent) {
    		ListeGroupeModel group = (ListeGroupeModel) getGroup(groupPosition);
    		if (view == null) {
    			LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    			view = inf.inflate(R.layout.amis_groupe_amis, null);
    		}
    		TextView ami = (TextView) view.findViewById(R.id.groupeAmi);
    		ami.setText(group.getName());
    		return view;
    	}

    	public boolean hasStableIds() {
    		return true;
    	}

    	public boolean isChildSelectable(int arg0, int arg1) {
    		return true;
    	}
    }
    
    /**
     * Model pour une liste d'ami
     * @author Marcleking
     *
     */
    public static class ListeAmiModel implements Parcelable {

    	private String Name;
    	private String Tag;
    	
    	public ListeAmiModel(String nom, String tag)
    	{
    		this.Name = nom;
    		this.Tag = tag;
    	}
    	
    	public String getName() {
    		return Name;
    	}
    	public void setName(String Name) {
    		this.Name = Name;
    	}
    	public String getTag() {
    		return Tag;
    	}
    	public void setTag(String Tag) {
    		this.Tag = Tag;
    	}
		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(Name);
			dest.writeString(Tag);
		}
    }
    
    public static final Parcelable.Creator<ListeAmiModel> CREATOR = new Parcelable.Creator<ListeAmiModel>() 
	{
        public ListeAmiModel createFromParcel(Parcel in) {
            return new ListeAmiModel(in.readString(), in.readString());
        }

        public ListeAmiModel[] newArray(int size) {
            return new ListeAmiModel[size];
        }
    };
    
    /**
     * Model pour une liste de groupe
     * @author Marcleking
     *
     */
    public static class ListeGroupeModel implements Parcelable{
    	 
    	private String Name;
    	private ArrayList<ListeAmiModel> Items;
    	
    	public String getName() {
    		return Name;
    	}
    	public void setName(String name) {
    		this.Name = name;
    	}
    	public ArrayList<ListeAmiModel> getItems() {
    		return Items;
    	}
    	public ListeAmiModel getItem(int id) {
    		return Items.get(id);
    	}
    	public void setItems(ArrayList<ListeAmiModel> Items) {
    		this.Items = Items;
    	}
		@Override
		public int describeContents() {
			return 0;
		}
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(Name);
			dest.writeSerializable(Items);
			
		}
    }
    
    public static final Parcelable.Creator<ListeGroupeModel> CREATOR2 = new Parcelable.Creator<ListeGroupeModel>() 
	{
        public ListeGroupeModel createFromParcel(Parcel in) {
        	ListeGroupeModel group = new ListeGroupeModel();
        	group.setName(in.readString());
        	group.setItems((ArrayList<ListeAmiModel>)in.readSerializable());
            return new ListeGroupeModel();
        }

        public ListeGroupeModel[] newArray(int size) {
            return new ListeGroupeModel[size];
        }
    };
    
    
    /******************
     * Call asyncrone
     * @author Marcleking
     *
     */
    public class AsyncGetAmi extends AsyncTask<String, Void, ArrayList<String>> {
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
	    protected ArrayList<String> doInBackground(String... url) {
	    	
	    	ArrayList<String> reponse = new ArrayList<String>();
	    	
	    	for (int i = 0; i < url.length; i++) {
	    		reponse.add(getHttpRequest(url[i]));
	    	}
	    	
	        return reponse;
	    }
	    
	    @Override
	    protected void onPostExecute(java.util.ArrayList<String> result) {

			
	        ExpandList = (ExpandableListView) getView().findViewById(R.id.ExpList);
			listeGroupe = new ArrayList<ListeGroupeModel>();
	    	listeAmi = new ArrayList<ListeAmiModel>();
	    	
	    	for (String info : result) {
	    		try {
	    			JSONObject mesAmis = new JSONObject(info);
					JSONArray demandeAmi = mesAmis.getJSONArray("demande");
					JSONArray ami = mesAmis.getJSONArray("amis");
					
					ListeGroupeModel groupeDemande = new ListeGroupeModel();
					groupeDemande.setName("Demandes d'ami");
					for (int i = 0; i < demandeAmi.length(); i++) {
						ListeAmiModel unAmi = new ListeAmiModel(demandeAmi.getString(i), null);
				        listeAmi.add(unAmi);
					}
					groupeDemande.setItems(listeAmi);
					
					listeAmi = new ArrayList<ListeAmiModel>();
					
					ListeGroupeModel amiGroupe = new ListeGroupeModel();
					amiGroupe.setName("Mes amis");
					for (int i = 0; i < ami.length(); i++) {
						ListeAmiModel unAmi = new ListeAmiModel(ami.getString(i), null);
				        listeAmi.add(unAmi);
					}
					amiGroupe.setItems(listeAmi);
					
					listeGroupe.add(groupeDemande);
					listeGroupe.add(amiGroupe);
	    		} catch (Exception e) {}
	    	}
	    	
	    	ExpAdapter = new ExpandListAdapter(rootView.getContext(), listeGroupe);
	        ExpandList.setAdapter(ExpAdapter);
	        
	        FragmentAmis.this.ExpListItems = listeGroupe;
	    	
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
