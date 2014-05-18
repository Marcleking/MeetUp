package com.bouchardm.meetup.classes;

import java.util.ArrayList;
import android.util.Log;

public class MeetUp {

	private final static String ID_NON_DEFINI = "";
	
	private String m_nom;
	private String m_lieu;
	private int m_duree;
	private int m_heureMin;
	private int m_heureMax;
	private String m_dateMin;
	private String m_dateMax;
	private ArrayList<Ami> m_invites;
	private String m_id;
	
	public MeetUp(){}
	
	public MeetUp(String p_nom, String p_lieu, int p_duree, int p_heureMin, int p_heureMax, String p_dateMin, String p_dateMax, ArrayList<Ami> p_invites){
		this.m_nom = p_nom;
		this.m_lieu = p_lieu;
		this.m_duree = p_duree;
		this.m_heureMin = p_heureMin;
		this.m_heureMax = p_heureMax;
		this.m_dateMin = p_dateMin;
		this.m_dateMax = p_dateMax;
		this.m_invites = p_invites;
		this.m_id = MeetUp.ID_NON_DEFINI;
	}
	
	public MeetUp(MeetUp copie_meetup){
		this.m_nom = copie_meetup.get_nom();
		this.m_lieu = copie_meetup.get_lieu();
		this.m_duree = copie_meetup.get_duree();
		this.m_heureMin = copie_meetup.get_heureMin();
		this.m_heureMax = copie_meetup.get_heureMax();
		this.m_dateMin = copie_meetup.get_dateMin();
		this.m_dateMax = copie_meetup.get_dateMax();
		this.m_invites = copie_meetup.get_invites();
		this.m_id = copie_meetup.get_id();
	}
	
	public static String ParseMeetUpToString(MeetUp meetup){
		String friendParse = "";
		if(meetup.get_invites() != null){
			for(Ami ami : meetup.m_invites){
				friendParse += Ami.AmiToString(ami) + "&";
			}
		}
		
		return meetup.get_nom() + ";" + meetup.get_lieu() + ";" + meetup.get_duree() + ";" + meetup.get_heureMin() + ";" + 
		meetup.get_heureMax() + ";" + meetup.get_dateMin() + ";" + meetup.get_dateMax() + ";" + meetup.get_id() + "#" + friendParse;
	}
	
	public static MeetUp ParseStringToMeetUp(String parse){
		MeetUp meetup = null;
		
		String infosMeetUp = parse.split("#")[0];
		String listeInvites = parse.split("#")[1];
		
		meetup = new MeetUp();
		meetup.set_nom(infosMeetUp.split(";")[0]);
		meetup.set_lieu(infosMeetUp.split(";")[1]);
		meetup.set_duree(Integer.parseInt(infosMeetUp.split(";")[2]));
		meetup.set_heureMin(Integer.parseInt(infosMeetUp.split(";")[3]));
		meetup.set_heureMax(Integer.parseInt(infosMeetUp.split(";")[4]));
		meetup.set_dateMin(infosMeetUp.split(";")[5]);
		meetup.set_dateMax(infosMeetUp.split(";")[6]);
		meetup.set_id(infosMeetUp.split(";")[7]);
		meetup.set_invites(new ArrayList<Ami>());
		
		if(listeInvites != "")
		{
			for(String invite:listeInvites.split("&")){
				meetup.get_invites().add(Ami.StringToAmi(invite));
			}
		}
		return meetup;
	}
	
	public String get_nom() {
		return m_nom;
	}

	public void set_nom(String m_nom) {
		this.m_nom = m_nom;
	}

	public String get_lieu() {
		return m_lieu;
	}

	public void set_lieu(String m_lieu) {
		this.m_lieu = m_lieu;
	}

	public int get_duree() {
		return m_duree;
	}

	public void set_duree(int m_duree) {
		this.m_duree = m_duree;
	}

	public int get_heureMin() {
		return m_heureMin;
	}

	public void set_heureMin(int m_heureMin) {
		this.m_heureMin = m_heureMin;
	}

	public int get_heureMax() {
		return m_heureMax;
	}

	public void set_heureMax(int m_heureMax) {
		this.m_heureMax = m_heureMax;
	}

	public String get_dateMin() {
		return m_dateMin;
	}

	public void set_dateMin(String m_dateMin) {
		this.m_dateMin = m_dateMin;
	}

	public String get_dateMax() {
		return m_dateMax;
	}

	public void set_dateMax(String m_dateMax) {
		this.m_dateMax = m_dateMax;
	}

	public ArrayList<Ami> get_invites() {
		return m_invites;
	}

	public void set_invites(ArrayList<Ami> m_invites) {
		this.m_invites = m_invites;
	}

	public String get_id() {
		return m_id;
	}

	public void set_id(String m_id) {
		this.m_id = m_id;
	}
}
