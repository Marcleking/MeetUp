package com.bouchardm.meetup;

import java.util.ArrayList;

import android.app.ListActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bouchardm.meetup.classes.Ami;

public class ListeParticipants extends ListActivity{
	
	private ArrayList<Ami> listeAmis;
	
	
	private class LigneAdapter extends ArrayAdapter<Ami>{
		public LigneAdapter() {
			super(ListeParticipants.this, R.layout.row_participant, R.id.lbl_content, listeAmis);
		}
		/*
		public View getView(int p_position, View p_row, ViewGroup p_list){
			View row = super.getView(p_position, p_row, p_list);
		}*/
	}
	
}
