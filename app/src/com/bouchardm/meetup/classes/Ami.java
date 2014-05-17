package com.bouchardm.meetup.classes;

public class Ami {
	private String m_id;
	private String m_nom;
	private String m_prenom;
	
	public Ami(String p_id, String p_nom, String p_prenom){
		this.m_id = p_id;
		this.m_nom = p_nom;
		this.m_prenom = p_prenom;
	}

	public String get_id() {
		return m_id;
	}

	public void set_id(String m_id) {
		this.m_id = m_id;
	}

	public String get_nom() {
		return m_nom;
	}

	public void set_nom(String m_nom) {
		this.m_nom = m_nom;
	}

	public String get_prenom() {
		return m_prenom;
	}

	public void set_prenom(String m_prenom) {
		this.m_prenom = m_prenom;
	}
	
	public static String AmiToString(Ami ami){
		return ami.m_id + ";" + ami.m_nom + ";" + ami.m_prenom;
	}
	
	public static Ami StringToAmi(String parse){
		return new Ami(parse.split(";")[0], parse.split(";")[1], parse.split(";")[2]);
	}
}


