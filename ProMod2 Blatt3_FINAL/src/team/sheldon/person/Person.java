//Stephen Popp 250346, Seyma Nur Coban 250596, Florian Möhrle 250013

package team.sheldon.person;

import java.io.Serializable;

/**
 * 
 * Klasse für die Erstellung von Personen
 * 
 * @version 1.0 ADRELI_THREAD
 * 
 */

// Klasse Person speichert Personen mit den Attributen
@SuppressWarnings("serial")
public class Person implements Serializable {
	/**
	 * Über Konstruktor werden Daten gespeichert
	 */
	private String name;
	private String vname;
	private String anrede;
	private String strasse;
	private String plz;
	private String ort;
	private String telefon;
	private String fax;
	private String bem;

	public Person(String name, String vname, String anrede, String strasse,
			String plz, String ort, String telefon, String fax, String bem) {
		this.setName(name);
		this.setVname(vname);
		this.setAnrede(anrede);
		this.setStrasse(strasse);
		this.setPlz(plz);
		this.setOrt(ort);
		this.setTelefon(telefon);
		this.setFax(fax);
		this.setBem(bem);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVname() {
		return vname;
	}

	public void setVname(String vname) {
		this.vname = vname;
	}

	public String getAnrede() {
		return anrede;
	}

	public void setAnrede(String anrede) {
		this.anrede = anrede;
	}

	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

	public String getPlz() {
		return plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getOrt() {
		return ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getTelefon() {
		return telefon;
	}

	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getBem() {
		return bem;
	}

	public void setBem(String bem) {
		this.bem = bem;
	}
}
