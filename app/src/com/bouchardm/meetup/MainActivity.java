package com.bouchardm.meetup;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.os.Parcel;
import android.os.Parcelable;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import com.bouchardm.meetup.menuUtil.*;

public class MainActivity extends FragmentActivity  {

	/**
	 * Attributs de la view
	 */
	//private ArrayList<String> m_Tokens = new ArrayList<String>();
	//private ArrayList<RowModel> m_RowModels = new ArrayList<RowModel>();
	//private LigneAdapter m_adapter;
	
	/**
	 * Création de la view
	 */
	
	private DrawerLayout mDrawer;
	private ListView mLeftDrawerList;
	private String[] mLeftMenuItems;
	//private CustomActionBarDrawerToggle mLeftDrawerToggle;	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i("test","onCreate");
		
		initLeftMenu();
		
		/* Affichage d'un horaire
		setContentView(R.layout.horaire);
		m_adapter = new LigneAdapter();
		this.setListAdapter(m_adapter);
		
		m_Tokens.add("École");
		m_Tokens.add("Travail");
		
		for (String token : m_Tokens) {
			m_RowModels.add(new RowModel(token, false));
		}
		*/
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		Log.i("test","onResume");
		initLeftMenu();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	private void initLeftMenu(){
		this.mLeftMenuItems = getResources().getStringArray(R.array.left_menu_items);
		this.mDrawer = (DrawerLayout)findViewById(R.id.drawer_layout);
		this.mLeftDrawerList = (ListView) findViewById(R.id.left_drawer);
		
		this.mLeftDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.mLeftMenuItems));
		
		this.mLeftDrawerList.setOnItemClickListener(new DrawerItemClickListener());
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView parent, View view, int position,long id) {
			
			if(position == 0){
				setContentView(R.layout.activity_main);
			}
			else if (position == 1){
				setContentView(R.layout.activity_connection);
			}
			
		}
	}
	
	
	
	
	
//	/**
//	 * Gestion de l'enregistrement des données (lors de la rotation)
//	 */
//	@Override
//	protected void onSaveInstanceState(Bundle p_outState) 
//	{
//		super.onSaveInstanceState(p_outState);
//		p_outState.putParcelableArrayList("rowModel", m_RowModels);
//	}
//	
//	/**
//	 * Gestion de la restauration des données (lors de la rotation)
//	 */
//	@Override
//	protected void onRestoreInstanceState(Bundle p_state) 
//	{
//		if (p_state != null) {
//			m_RowModels = p_state.getParcelableArrayList("rowModel");
//		}
//		this.setListAdapter(new LigneAdapter());
//	}
//	
//	/**
//	 * Gestion du clic sur le bouton d'ajout d'horaire => affiche un pop-up
//	 * @param source
//	 */
//	public void btnAjoutHoraire(View source)
//	{
//		
//		View ajoutHoraire = getLayoutInflater().inflate(R.layout.ajout_horaire, null);
//		EditText txtHoraire = (EditText) ajoutHoraire.findViewById(R.id.btnAjoutHoraireText);
//		
//		BtnSetHandler handlerHoraire = new BtnSetHandler(txtHoraire);
//		
//		new AlertDialog.Builder(this)
//			.setTitle("Ajout d'un horaire")
//			.setView(ajoutHoraire)
//			.setNegativeButton("Annuler", null)
//			.setPositiveButton("Ajouter", handlerHoraire)
//			.show();
//		
//	}
//	
//	/**
//	 * Handler pour la gestion du pop-up d'ajout d'horaire
//	 * @author Marcleking
//	 *
//	 */
//	private class BtnSetHandler implements DialogInterface.OnClickListener
//	{
//		/**
//		 * Attributs
//		 */
//		private EditText m_txtHoraire;
//		
//		/**
//		 * Constructeur
//		 * @param p_txtHoraire
//		 */
//		public BtnSetHandler (EditText p_txtHoraire)
//		{
//			this.m_txtHoraire = p_txtHoraire;
//		}
//		
//		/**
//		 * Gestion de l'enregistrement de l'horaire => actualise la liste
//		 */
//		@Override
//		public void onClick(DialogInterface dialog, int which) {
//			m_Tokens.add(m_txtHoraire.getText().toString());
//			m_RowModels.add(new RowModel(m_txtHoraire.getText().toString(), true));
//			m_adapter.notifyDataSetChanged();
//		}
//		
//	}

	

//	/**
//	 * Gestion du clic sur un horaire (activation/désactivation de l'horaire)
//	 */
//	@Override
//	protected void onListItemClick(ListView p_l, View p_row, int p_position, long p_id) {
//		RowModel model = m_RowModels.get(p_position);
//		
//		model.setIsActivate(!model.isActivate());
//		
//		ImageView icon = (ImageView) p_row.findViewById(R.id.img_selection);
//		if (model.isActivate()) {
//			icon.setImageResource(R.drawable.ok);
//		} else {
//			icon.setImageResource(R.drawable.delete);
//		}
//	}
	
	/**
	 * Adapter pour la gestion de chaque entrée de la liste
	 * @author Marcleking
	 *
	 */
//	public class LigneAdapter extends ArrayAdapter<String> {
//		/**
//		 * Contructeur
//		 */
//		public LigneAdapter() {
//			super(MainActivity.this, R.layout.row_horaire, R.id.lbl_content, m_Tokens);
//		}
//		
//		/**
//		 * Retourne une ligne
//		 */
//		@Override
//		public View getView(int p_Position, View p_Row, ViewGroup p_List) {
//			View row = super.getView(p_Position, p_Row, p_List);
//			
//			RowModel model = m_RowModels.get(p_Position);
//			
//			TextView lblContent = (TextView) row.findViewById(R.id.lbl_content);
//			lblContent.setText(model.getContent());
//			
//			ImageView icon = (ImageView) row.findViewById(R.id.img_selection);
//			if (model.isActivate()) {
//				icon.setImageResource(R.drawable.ok);
//			} else {
//				icon.setImageResource(R.drawable.delete);
//			}
//			
//			return row;
//		}
//	}
	
	
	/**
	 * Class qui représente une ligne
	 * @author Marcleking
	 *
	 */
//	public static class RowModel implements Parcelable{
//		private String m_Content;
//		private boolean m_isActivate;
//		
//		public RowModel(String content, boolean activate) {
//			this.m_Content = content;
//			this.m_isActivate = activate;
//		}
//		
//		public String getContent() {
//			return m_Content;
//		}
//		
//		public void setContent(String content) {
//			this.m_Content = content;
//		}
//		
//		public boolean isActivate() {
//			return m_isActivate;
//		}
//		
//		public void setIsActivate(boolean isActivate) {
//			this.m_isActivate = isActivate;
//		}
//
//		@Override
//		public int describeContents() {
//			// TODO Auto-generated method stub
//			return 0;
//		}
//
//		@Override
//		public void writeToParcel(Parcel dest, int flags) {
//			dest.writeString(m_Content);
//			dest.writeByte((byte) (m_isActivate ? 1 : 0));  
//		}
//	}
	
//	public static final Parcelable.Creator<RowModel> CREATOR = new Parcelable.Creator<RowModel>() 
//	{
//        public RowModel createFromParcel(Parcel in) {
//            return new RowModel(in.readString(), in.readByte() != 0);
//        }
//
//        public RowModel[] newArray(int size) {
//            return new RowModel[size];
//        }
//    };
    
}


