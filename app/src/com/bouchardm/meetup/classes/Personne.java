package com.bouchardm.meetup.classes;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Random;

import com.bouchardm.meetup.sqlite.PersonneDataSource;

import android.content.Context;

public class Personne {
	
	public static final int ID_NON_DEFINI = -1;
	public static final String SECURITY_NUMBER_NON_DEFINI = "";
	// Id de la personne dans la BD locale
	private int m_id;
	
	// Identifiant Google de la personne
	private String m_googleId;
	
	// Numéro de sécurité généré pour la personne
	private String m_SecurityNumber;
	
	// Settings
	private int m_heureMin;
	private int m_heureMax;
	
	public Personne(){
		this("");
	}
	
	public Personne(String p_googleId){
		this.m_googleId = p_googleId;
		if(!p_googleId.equals(""))
			this.m_SecurityNumber = generateSecurityNumber(p_googleId);
		else
			this.m_SecurityNumber = Personne.SECURITY_NUMBER_NON_DEFINI; 
		
		this.m_heureMin = 8;
		this.m_heureMax = 20;
		
		this.m_id = Personne.ID_NON_DEFINI;
	}

	public static String ParsePersonneToString(Personne personne){
		return personne.getId() + ";" + personne.get_googleId() + ";" + personne.get_securityNumber();
	}
	
	public static Personne ParseStringToPerson(String parse){
		Personne personne = new Personne();
		personne.setId(Integer.parseInt(parse.split(";")[0]));
		personne.set_googleId(parse.split(";")[1]);
		personne.set_securityNumber(parse.split(";")[2]);
		return personne;
	}
	
	private String generateSecurityNumber(String googleId){
		String sha1 = "";
		googleId += "jesuisunmotdepassebcptropsecuritairelol";
		try{
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(googleId.getBytes("UTF-8"));
			sha1 = byteToHex(crypt.digest());
		}
		catch(NoSuchAlgorithmException e)
	    {
	        e.printStackTrace();
	    }
	    catch(UnsupportedEncodingException e)
	    {
	        e.printStackTrace();
	    }
		
		return sha1;
	}
	
	private String byteToHex(final byte[] hash)
	{
	    Formatter formatter = new Formatter();
	    for (byte b : hash)
	    {
	        formatter.format("%02x", b);
	    }
	    String result = formatter.toString();
	    formatter.close();
	    return result;
	}
	
	public void update(Context c) {
		PersonneDataSource pds = new PersonneDataSource(c);
		pds.open();
		pds.update(this);
		pds.close();
	}

	public String get_googleId() {
		return m_googleId;
	}

	public void set_googleId(String m_googleId) {
		this.m_googleId = m_googleId;
	}

	public String get_securityNumber() {
		return m_SecurityNumber;
	}
	
	public void set_securityNumber(String m_securityNumber) {
		this.m_SecurityNumber = m_securityNumber;
	}

	public int getId() {
		return m_id;
	}

	public void setId(int id) {
		this.m_id = id;
	}
	public int getM_heureMin() {
		return m_heureMin;
	}

	public void setM_heureMin(int m_heureMin) {
		this.m_heureMin = m_heureMin;
	}

	public int getM_heureMax() {
		return m_heureMax;
	}

	public void setM_heureMax(int m_heureMax) {
		this.m_heureMax = m_heureMax;
	}

}