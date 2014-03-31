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

public class ListeAmis extends ListActivity {
	/**
	 * Attributs de la view
	 */
	private ArrayList<String> m_Tokens = new ArrayList<String>();
	private ArrayList<RowModel> m_RowModels = new ArrayList<RowModel>();
	private LigneAdapter m_adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meet_up_creation_liste_amis);
		
		m_adapter = new LigneAdapter();
		this.setListAdapter(m_adapter);
		
		m_Tokens.add("Stephane la banane");
		m_Tokens.add("Françis la réglisse");
		m_Tokens.add("Marc-Antoine le dieu parmit les vivants");
		
		for (String token : m_Tokens) {
			m_RowModels.add(new RowModel(token, false));
		}
	}
	
	public void okAmis(View source)
	{
		this.finish();
	}
	
	/**
	 * Gestion de l'enregistrement des données (lors de la rotation)
	 */
	@Override
	protected void onSaveInstanceState(Bundle p_outState) 
	{
		super.onSaveInstanceState(p_outState);
		p_outState.putParcelableArrayList("rowModel", m_RowModels);
		p_outState.putStringArrayList("token", m_Tokens);
	}
	
	/**
	 * Gestion de la restauration des données (lors de la rotation)
	 */
	@Override
	protected void onRestoreInstanceState(Bundle p_state) 
	{
		if (p_state != null) {
			m_RowModels = p_state.getParcelableArrayList("rowModel");
			m_Tokens = p_state.getStringArrayList("token");
			
			m_adapter = new LigneAdapter();
			this.setListAdapter(m_adapter);
		}
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
			super(ListeAmis.this, R.layout.row_amis, R.id.lbl_content, m_Tokens);
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
