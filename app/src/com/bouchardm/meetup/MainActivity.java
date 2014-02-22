package com.bouchardm.meetup;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ListActivity {
	
	/**
	 * Attributs de la view
	 */
	private ArrayList<String> m_Tokens = new ArrayList<String>();
	private ArrayList<RowModel> m_RowModels = new ArrayList<RowModel>();
	private LigneAdapter m_adapter;
	
	/**
	 * Création de la view
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.horaire);
		
		m_adapter = new LigneAdapter();
		this.setListAdapter(m_adapter);
		
		m_Tokens.add("École");
		m_Tokens.add("Travail");
		
		for (String token : m_Tokens) {
			m_RowModels.add(new RowModel(token, false));
		}
	}
	
	/**
	 * Gestion de l'enregistrement des données (lors de la rotation)
	 */
	@Override
	protected void onSaveInstanceState(Bundle p_outState) 
	{
		super.onSaveInstanceState(p_outState);
		p_outState.putParcelableArrayList("rowModel", m_RowModels);
	}
	
	/**
	 * Gestion de la restauration des données (lors de la rotation)
	 */
	@Override
	protected void onRestoreInstanceState(Bundle p_state) 
	{
		if (p_state != null) {
			m_RowModels = p_state.getParcelableArrayList("rowModel");
		}
		this.setListAdapter(new LigneAdapter());
	}
	
	/**
	 * Gestion du clic sur le bouton d'ajout d'horaire => affiche un pop-up
	 * @param source
	 */
	public void btnAjoutHoraire(View source)
	{
		View ajoutHoraire = getLayoutInflater().inflate(R.layout.ajout_horaire, null);
		EditText txtHoraire = (EditText) ajoutHoraire.findViewById(R.id.btnAjoutHoraireText);
		
		BtnSetHandler handlerHoraire = new BtnSetHandler(txtHoraire);
		
		new AlertDialog.Builder(this)
			.setTitle("Ajout d'un horaire")
			.setView(ajoutHoraire)
			.setNegativeButton("Annuler", null)
			.setPositiveButton("Ajouter", handlerHoraire)
			.show();
	
	}
	
	/**
	 * Handler pour la gestion du pop-up d'ajout d'horaire
	 * @author Marcleking
	 *
	 */
	private class BtnSetHandler implements DialogInterface.OnClickListener
	{
		/**
		 * Attributs
		 */
		private EditText m_txtHoraire;
		
		/**
		 * Constructeur
		 * @param p_txtHoraire
		 */
		public BtnSetHandler (EditText p_txtHoraire)
		{
			this.m_txtHoraire = p_txtHoraire;
		}
		
		/**
		 * Gestion de l'enregistrement de l'horaire => actualise la liste
		 */
		@Override
		public void onClick(DialogInterface dialog, int which) {
			m_Tokens.add(m_txtHoraire.getText().toString());
			m_RowModels.add(new RowModel(m_txtHoraire.getText().toString(), true));
			m_adapter.notifyDataSetChanged();
		}
		
		
	}

	/**
	 * Menu contextuel
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * Gestion du clic sur un horaire (activation/désactivation de l'horaire)
	 */
	@Override
	protected void onListItemClick(ListView p_l, View p_row, int p_position, long p_id) {
		RowModel model = m_RowModels.get(p_position);
		
		model.setIsActivate(!model.isActivate());
		
		ImageView icon = (ImageView) p_row.findViewById(R.id.img_selection);
		if (model.isActivate()) {
			icon.setImageResource(R.drawable.ok);
		} else {
			icon.setImageResource(R.drawable.delete);
		}
	}
	
	/**
	 * Adapter pour la gestion de chaque entrée de la liste
	 * @author Marcleking
	 *
	 */
	public class LigneAdapter extends ArrayAdapter<String> {
		/**
		 * Contructeur
		 */
		public LigneAdapter() {
			super(MainActivity.this, R.layout.row_horaire, R.id.lbl_content, m_Tokens);
		}
		
		/**
		 * Retourne une ligne
		 */
		@Override
		public View getView(int p_Position, View p_Row, ViewGroup p_List) {
			View row = super.getView(p_Position, p_Row, p_List);
			
			RowModel model = m_RowModels.get(p_Position);
			
			TextView lblContent = (TextView) row.findViewById(R.id.lbl_content);
			lblContent.setText(model.getContent());
			
			ImageView icon = (ImageView) row.findViewById(R.id.img_selection);
			if (model.isActivate()) {
				icon.setImageResource(R.drawable.ok);
			} else {
				icon.setImageResource(R.drawable.delete);
			}
			
			return row;
		}
	}
	
	
	/**
	 * Class qui représente une ligne
	 * @author Marcleking
	 *
	 */
	public static class RowModel implements Parcelable{
		private String m_Content;
		private boolean m_isActivate;
		
		public RowModel(String content, boolean activate) {
			this.m_Content = content;
			this.m_isActivate = activate;
		}
		
		public String getContent() {
			return m_Content;
		}
		
		public void setContent(String content) {
			this.m_Content = content;
		}
		
		public boolean isActivate() {
			return m_isActivate;
		}
		
		public void setIsActivate(boolean isActivate) {
			this.m_isActivate = isActivate;
		}

		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(m_Content);
			dest.writeByte((byte) (m_isActivate ? 1 : 0));  
		}
	}
	
	public static final Parcelable.Creator<RowModel> CREATOR = new Parcelable.Creator<RowModel>() 
	{
        public RowModel createFromParcel(Parcel in) {
            return new RowModel(in.readString(), in.readByte() != 0);
        }

        public RowModel[] newArray(int size) {
            return new RowModel[size];
        }
    };

}
