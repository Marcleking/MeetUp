package com.bouchardm.meetup;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bouchardm.meetup.classes.Ami;

public class ListeParticipants extends ListActivity{
	
	private ArrayList<Ami> listeAmis;
	private LigneAdapter m_adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meet_up_voir_participants);
		
		if(getIntent() != null){
			listeAmis = new ArrayList<Ami>();
			ArrayList<String> parse = (ArrayList<String>)getIntent().getExtras().getStringArrayList("EXTRA_PARTICIPANTS");
			for(String singleParse:parse){
				listeAmis.add(Ami.StringToAmi(singleParse));
			}
		}
		
		if(listeAmis != null && listeAmis.size() > 0){
			m_adapter = new LigneAdapter();
			this.setListAdapter(m_adapter);
		}
		
	}
	
	private class LigneAdapter extends ArrayAdapter<Ami>{
		public LigneAdapter() {
			super(ListeParticipants.this, R.layout.row_participant, R.id.lbl_content, listeAmis);
		}
		
		public View getView(int p_position, View p_row, ViewGroup p_list){
			View row = super.getView(p_position, p_row, p_list);
			
			TextView lblContent = (TextView)row.findViewById(R.id.lbl_content);
			lblContent.setText(listeAmis.get(p_position).get_prenom() + listeAmis.get(p_position).get_nom());
			
			return row;
		}
	}
	
}
