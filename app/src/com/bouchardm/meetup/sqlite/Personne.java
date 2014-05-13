package com.bouchardm.meetup.sqlite;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Random;

public class Personne {
	
	public static final int ID_NON_DEFINI = -1;
	public static final String SECURITY_NUMBER_NON_DEFINI = "";
	// Id de la personne dans la BD locale
	private int m_id;
	
	// Identifiant Google de la personne
	private String m_googleId;
	
	// Numéro de sécurité généré pour la personne
	private String m_SecurityNumber;
	
	public Personne(){
		this("");
	}
	
	public Personne(String p_googleId){
		this.m_googleId = p_googleId;
		if(!p_googleId.equals(""))
			this.m_SecurityNumber = generateSecurityNumber(p_googleId);
		else
			this.m_SecurityNumber = this.SECURITY_NUMBER_NON_DEFINI; 
		this.m_id = this.ID_NON_DEFINI;
	}
	
	private String generateSecurityNumber(String googleId){
		String sha1 = "";
		Random rn = new Random();
		googleId += rn.nextInt();
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
}
