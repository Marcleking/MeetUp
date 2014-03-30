package com.bouchardm.meetup;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

public class Amis extends Activity {
	
	/**
	 * Attributs
	 */
	private ExpandListAdapter ExpAdapter;
	private ArrayList<ListeGroupeModel> ExpListItems;
	private ExpandableListView ExpandList;
	
	private ArrayList<ListeGroupeModel> listeGroupe;
	private ArrayList<ListeAmiModel> listeAmi;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amis);
        ExpandList = (ExpandableListView) findViewById(R.id.ExpList);
        //Données hard-coded
        ExpListItems = SetStandardGroups();
        
        ExpAdapter = new ExpandListAdapter(Amis.this, ExpListItems);
        ExpandList.setAdapter(ExpAdapter);
        
        this.registerForContextMenu(ExpandList);
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

    	  //On va chercher les infos sur la ligne sélectionner
    	  int type = ExpandableListView.getPackedPositionType(info.packedPosition);
    	  int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);
    	  int child = ExpandableListView.getPackedPositionChild(info.packedPosition);

    	  // On gère que le context menu est créé pour les bonne ligne
    	  if (ExpListItems.get(group).getName() != "Demande d'amitié" && 
    			  ExpListItems.get(group).getName() != "Vos demandes d'amitié"){
	    	  if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
	    		  getMenuInflater().inflate(R.menu.menu_ami, menu);
	    	  } else {
	    		  getMenuInflater().inflate(R.menu.menu_groupe, menu);
	    	  }
    	  } else {
    		  if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD)
    			  getMenuInflater().inflate(R.menu.menu_demande_ami, menu);
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
		
		//Si c'est un ami qui est sélectionner
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD)
		{
			View ajoutGroupe = getLayoutInflater().inflate(R.layout.amis_ajout_groupe, null);
			EditText txtGroupe = (EditText) ajoutGroupe.findViewById(R.id.btnAjoutGroupeText);
			
			BtnSetHandlerChangerAmiGroupe handlerHoraire = 
					new BtnSetHandlerChangerAmiGroupe(txtGroupe, group, child);
			
			switch(item.getItemId())
			{
				case R.id.menu_supprimerAmi:
					//Supprimer un n'ami
					ExpListItems.get(group).getItems().remove(child);
					ExpAdapter = new ExpandListAdapter(Amis.this, ExpListItems);
					ExpandList.setAdapter(ExpAdapter);
					return true;
				case R.id.menu_changerGroupe:
					//On demande à l'utilisateur dans quel groupe il veut envoyé sont ami
					new AlertDialog.Builder(this)
						.setTitle("Entrez le nom du groupe")
						.setView(ajoutGroupe)
						.setNegativeButton("Annuler", null)
						.setPositiveButton("Changer", handlerHoraire)
						.show();
					
					return true;
				case R.id.menu_accepter:
					//On demande à l'utilisateur dans quel groupe il veut envoyé sont ami
					new AlertDialog.Builder(this)
						.setTitle("Entrez le nom du groupe")
						.setView(ajoutGroupe)
						.setNegativeButton("Annuler", null)
						.setPositiveButton("Changer", handlerHoraire)
						.show();
					return true;
				case R.id.menu_refuser:
					//Supprimer un n'ami
					ExpListItems.get(group).getItems().remove(child);
					ExpAdapter = new ExpandListAdapter(Amis.this, ExpListItems);
					ExpandList.setAdapter(ExpAdapter);
					return true;
			  }
		//Si c'est un groupe qui à été sélectionner
		} else {
			switch(item.getItemId())
			{
				case R.id.menu_supprimerGroupe:
					//Supprimer un group
					ExpListItems.remove(group);
					ExpAdapter = new ExpandListAdapter(Amis.this, ExpListItems);
					ExpandList.setAdapter(ExpAdapter);
					return true;
				case R.id.menu_modifier:
			  		
			  		View ajoutGroupe = getLayoutInflater().inflate(R.layout.amis_ajout_groupe, null);
					EditText txtGroupe = (EditText) ajoutGroupe.findViewById(R.id.btnAjoutGroupeText);
					
					BtnSetHandlerChangerNomGroupe handlerHoraire = new BtnSetHandlerChangerNomGroupe(txtGroupe, group);
					
					//On demande à l'utilisateur le nouveau nom du groupe
					new AlertDialog.Builder(this)
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
	 * Gestion de l'enregistrement des données (lors de la rotation)
	 */
	@Override
	protected void onSaveInstanceState(Bundle p_outState) 
	{
		super.onSaveInstanceState(p_outState);
		
		p_outState.putParcelableArrayList("listeGroupe", ExpListItems);
	}
	
	/**
	 * Gestion de la restauration des données (lors de la rotation)
	 */
	@Override
	protected void onRestoreInstanceState(Bundle p_state) 
	{
		if (p_state != null) {
			ExpListItems = p_state.getParcelableArrayList("listeGroupe");
			
			ExpAdapter = new ExpandListAdapter(Amis.this, ExpListItems);
	        ExpandList.setAdapter(ExpAdapter);
		}
	}
    
    public ArrayList<ListeGroupeModel> SetStandardGroups() {
    	
    	// Donnée hard-coded
    	listeGroupe = new ArrayList<ListeGroupeModel>();
    	listeAmi = new ArrayList<ListeAmiModel>();
    	ListeGroupeModel gru0 = new ListeGroupeModel();
    	gru0.setName("Demande d'amitié");
    	listeAmi.add(new ListeAmiModel("Jeff la patate", null));
    	gru0.setItems(listeAmi);
    	listeAmi = new ArrayList<ListeAmiModel>();
    	
    	ListeGroupeModel gru1 = new ListeGroupeModel();
        gru1.setName("Vos demandes d'amitié");
        ListeAmiModel ch1_1 = new ListeAmiModel("LCD la lumière - Accepter", null);
        listeAmi.add(ch1_1);
        ListeAmiModel ch1_2 = new ListeAmiModel("Judith la bizarre - En attente", null);
        listeAmi.add(ch1_2);
        ListeAmiModel ch1_3 = new ListeAmiModel("Gilles l'ancien - Refusé", null);
        listeAmi.add(ch1_3);
        gru1.setItems(listeAmi);
        listeAmi = new ArrayList<ListeAmiModel>();
        
        ListeGroupeModel gru2 = new ListeGroupeModel();
        gru2.setName("Famille");
        ListeAmiModel ch2_1 = new ListeAmiModel("Francis le pas vite", null);
        listeAmi.add(ch2_1);
        ListeAmiModel ch2_2 = new ListeAmiModel("Moman", null);
        listeAmi.add(ch2_2);
        ListeAmiModel ch2_3 = new ListeAmiModel("Popa", null);
        listeAmi.add(ch2_3);
        gru2.setItems(listeAmi);
        listeGroupe.add(gru0);
        listeGroupe.add(gru1);
        listeGroupe.add(gru2);
        
        return listeGroupe;
    }
    
    /**
     * Gestion de l'ajout de groupe
     * @param source
     */
    public void ajoutGroupe(View source)
    {
    	View ajoutGroupe = getLayoutInflater().inflate(R.layout.amis_ajout_groupe, null);
		EditText txtGroupe = (EditText) ajoutGroupe.findViewById(R.id.btnAjoutGroupeText);
		
		BtnSetHandlerGroupe handlerHoraire = new BtnSetHandlerGroupe(txtGroupe);
		
		//On demande à l'utilisateur le nom du groupe
		new AlertDialog.Builder(this)
			.setTitle("Ajouté un groupe")
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
	        ExpAdapter = new ExpandListAdapter(Amis.this, ExpListItems);
	        ExpandList.setAdapter(ExpAdapter);
		}
	}
    
    /**
     * Gestion de l'ajout d'ami
     * @param source
     */
    public void ajoutAmi(View source)
    {
        View ajoutGroupe = getLayoutInflater().inflate(R.layout.amis_ajout_groupe, null);
		EditText txtGroupe = (EditText) ajoutGroupe.findViewById(R.id.btnAjoutGroupeText);
		
		BtnSetHandlerAmi handlerHoraire = new BtnSetHandlerAmi(txtGroupe);
		
		//On demande le nom de l'ami à l'utilisateur
		new AlertDialog.Builder(this)
			.setTitle("Ajouté un ami")
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
			//Création d'une nouvelle liste d'ami
	    	listeAmi = new ArrayList<ListeAmiModel>();
	        
	        //On va chercher tout les amis du groupe
	        ArrayList<ListeAmiModel> toutLesAmis = ExpListItems.get(0).getItems();
	        for (ListeAmiModel ami : toutLesAmis) {
	        	//On rajoute tout les amis du groupe dans la liste d'ami (pour ne pas les perdre)
	        	listeAmi.add(ami);
			}
	        
	        //Création d'un nouvel ami
	        ListeAmiModel nouvelAmi = new ListeAmiModel(m_txtGroupe.getText().toString(), null);
	        listeAmi.add(nouvelAmi);
	        
	        //On met à jour la liste d'ami dans le groupe
	        ExpListItems.get(0).setItems(listeAmi);
	        
	        //On rafraichit l'affichage
	        ExpAdapter = new ExpandListAdapter(Amis.this, ExpListItems);
	        ExpandList.setAdapter(ExpAdapter);
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
			
			if (!nomGroupe.equalsIgnoreCase("Demande d'amitié") && 
				!nomGroupe.equalsIgnoreCase("Vos demandes d'amitié") &&
				!nomGroupe.trim().equalsIgnoreCase(""))
			{
				//Changement du nom
				ExpListItems.get(m_ancienGroupe).setName(nomGroupe);
				
				//Actualisation
				ExpAdapter = new ExpandListAdapter(Amis.this, ExpListItems);
				ExpandList.setAdapter(ExpAdapter);
			} else {
				Toast.makeText(getApplicationContext(), "Vous ne pouvez pas mettre ce nom de groupe", Toast.LENGTH_LONG).show();
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
			
			String nomGroupe = m_txtGroupe.getText().toString();
			
			if (!nomGroupe.equalsIgnoreCase("Demande d'amitié") && 
				!nomGroupe.equalsIgnoreCase("Vos demandes d'amitié") &&
				!nomGroupe.trim().equalsIgnoreCase(""))
			{
				ListeAmiModel amiTempo = ExpListItems.get(m_ancienGroupe).getItem(m_ancienAmi);
				ExpListItems.get(m_ancienGroupe).getItems().remove(m_ancienAmi);
				
				boolean trouve = false;
		        
				//Parcours de tout les groupes
				ArrayList<ListeGroupeModel> toutLesGroupes = ExpListItems;
				for (ListeGroupeModel expandListGroup : toutLesGroupes) {
					//Si le group est le même que celui entré on met l'utilisateur dans ce groupe
					if(expandListGroup.getName().toString().equalsIgnoreCase(nomGroupe) && !trouve) {
						
						ArrayList<ListeAmiModel> listeAmiTempo = expandListGroup.getItems();
						listeAmiTempo.add(amiTempo);
						expandListGroup.setItems(listeAmiTempo);
						
						trouve = true;
					}
				}
				//Si on n'a pas trouver le groupe on le rajoute et on met l'user dedans
				if(!trouve) {
					listeGroupe = new ArrayList<ListeGroupeModel>();
			    	listeAmi = new ArrayList<ListeAmiModel>();
			    	listeAmi.add(amiTempo);
			    	
			        ListeGroupeModel gru1 = new ListeGroupeModel();
			        gru1.setName(nomGroupe);
			        
			        gru1.setItems(listeAmi);
			        
			        ExpListItems.add(gru1);
			       
				} else {
					ExpListItems = toutLesGroupes;
				}
				
				//Actualisation
				ExpAdapter = new ExpandListAdapter(Amis.this, ExpListItems);
				ExpandList.setAdapter(ExpAdapter);
			} else {
				Toast.makeText(getApplicationContext(), "Vous ne pouvez pas mettre votre ami dans ce groupe", Toast.LENGTH_LONG).show();
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


}
