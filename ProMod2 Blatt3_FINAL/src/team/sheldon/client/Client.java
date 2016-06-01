//Stephen Popp 250346, Seyma Nur Coban 250596, Florian Möhrle 250013

package team.sheldon.client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import team.sheldon.person.Person;

public class Client {

	// Scanner
	private Scanner scanInt;
	private Scanner scanString;

	// Variablen
	private int auswahl;

	// Daten
	private List<Person> daten;
	private Socket socket;
	private InetSocketAddress address;
	private BufferedWriter writer;
	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;

	public static void main(String[] args) {
		Client client = new Client();
		client.start();
	}

	private void start() {
		address = new InetSocketAddress("141.28.4.4", 56789);
		socket = new Socket();

		try {
			socket.connect(address);

			System.out.println("Initilize writer ...");
			writer = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));
			objectInputStream = new ObjectInputStream(socket.getInputStream());
			System.out.println("Initilize output stream ...");
			objectOutputStream = new ObjectOutputStream(
					socket.getOutputStream());
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			System.exit(0);
		}// geht an accept()

		System.out.println("Starte Menu ...");
		for (;;) {
			menue();
		}
	}

	private void menue() {

		scanInt = new Scanner(System.in);

		System.out.println("-------ADRELI - Adressverwaltung-------");
		System.out.println("\tWollen Sie...");
		System.out.println();
		System.out.println("eine neue Person aufnehmen:......> 1");
		System.out.println("Records auflisten:...............> 2");
		System.out.println("Records in einer Datei sichern:..> 3");
		System.out.println("Records in einer Datei laden:....> 4");
		System.out.println("Sortieren:.......................> 5");
		System.out.println("Eine Datei löschen:.............> 6");
		System.out.println("Das Programm verlassen:..........> 7");

		System.out.println();// Abstand
		System.out.print("Ihre Auswahl: ");

		auswahl = scanInt.nextInt();

		// Eingabe wird gesendet
		try {
			System.out.println("Sende Nachricht: " + auswahl);
			writer.write(Integer.toString(auswahl));
			writer.newLine();
			writer.flush();
		} catch (Exception e) {
			System.out.println("Fehler in Menue: " + e.getMessage());
		}

		switch (auswahl) {
		case 1:
			aufnehmen();
			break;
		case 2:
			anzeigen();
			break;
		case 3:
			speichern();
			break;
		case 4:
			laden();
			break;
		case 5:
			System.out.println("Sortierung...");
			sort();
			break;
		case 6:
			löschen(filenameAufnehmen());
			break;
		case 7:
			try {
				writer.close();
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Das Programm wird verlassen");
			System.exit(-1);

		default:
			System.out.println("Fehler bei der Eingabe");
			break;
		}
	}

	public void aufnehmen() {
		// Scanner initialisieren
		scanInt = new Scanner(System.in);
		scanString = new Scanner(System.in);

		daten = new LinkedList<Person>();

		try {
			// NAME
			Matcher name_ok;
			String name;
			do {
				Pattern pattern = Pattern.compile("[A-Z][a-z]*");
				System.out.print("Name: ");
				name = scanString.nextLine();
				// Name auf richtige Eingabe pruefen
				name_ok = pattern.matcher(name);
				if (name_ok.matches() == false) {
					System.out.println("Bitte Namen korrekt eingeben!");
					System.out.println("Bsp: Mustermann");
				}

			} while (name_ok.matches() == false);
			// solange ausführen bis Name korrekt

			// VORNAME
			Matcher vname_ok;
			String vname;
			do {
				Pattern pattern = Pattern.compile("[A-Z][a-z]*");
				System.out.print("Vorname: ");
				vname = scanString.nextLine();
				// Vorname auf richtige Eingabe pruefen
				vname_ok = pattern.matcher(vname);
				if (vname_ok.matches() == false) {
					System.out.println("Bitte Vornamen korrekt eingeben!");
					System.out.println("Bsp: Max");
				}
			} while (vname_ok.matches() == false);
			// solange ausführen bis Vorname korrekt

			// ANREDE
			Matcher anrede_ok;
			String anrede;
			do {
				Pattern pattern = Pattern.compile("Herr|Frau");
				System.out.print("Anrede: ");
				anrede = scanString.nextLine();
				// Anrede auf richtige Eingabe pruefen
				anrede_ok = pattern.matcher(anrede);
				if (anrede_ok.matches() == false) {
					System.out.println("Bitte Anrede korrekt eingeben!");
					System.out.println("Bsp: Herr oder Frau");
				}
			} while (anrede_ok.matches() == false);
			// solange ausführen bis Anrede korrekt

			// STRASSE
			Matcher strasse_ok;
			String strasse;
			do {
				Pattern pattern = Pattern
						.compile("[A-Z][a-z]*( [A-Z][a-z]*)*\\.? [1-9][0-9]*[a-z]?");
				System.out.print("Strasse: ");
				strasse = scanString.nextLine();
				// Strasse auf richtige Eingabe pruefen
				strasse_ok = pattern.matcher(strasse);
				if (strasse_ok.matches() == false) {
					System.out.println("Bitte Strasse korrekt eingeben!");
					System.out.println("Bsp: Musterstrasse 12");
				}
			} while (strasse_ok.matches() == false);
			// solange ausführen bis Strasse korrekt

			// PLZ
			Matcher plz_ok;
			String plz;
			do {
				Pattern pattern = Pattern.compile("[0-9]{5}");
				System.out.print("PLZ: ");
				plz = scanString.nextLine();
				// PLZ auf richtige Eingabe pruefen
				plz_ok = pattern.matcher(plz);
				if (plz_ok.matches() == false) {
					System.out.println("Bitte PLZ korrekt eingeben!");
					System.out.println("Bsp: D78120");
				}
			} while (plz_ok.matches() == false);
			// solange ausführen bis PLZ korrekt

			// ORT
			Matcher ort_ok;
			String ort;
			do {
				Pattern pattern = Pattern
						.compile("[A-Z][a-z]*\\s[A-Za-z]*\\s[A-Za-z]*|[A-Z][a-z]*");
				System.out.print("Ort: ");
				ort = scanString.nextLine();
				// Ort auf richtige Eingabe prüfen
				ort_ok = pattern.matcher(ort);
				if (ort_ok.matches() == false) {
					System.out.println("Bitte Ort korrekt eingeben!");
					System.out.println("Bsp: Furtwangen");
				}
			} while (ort_ok.matches() == false);
			// solange ausführen bis Ort korrekt

			// TELEFON
			Matcher telefon_ok;
			String telefon;
			do {
				Pattern pattern = Pattern.compile("[0-9].{3,14}");
				System.out.print("Telefon: ");
				telefon = scanString.nextLine();
				// Telefon auf richtige Eingabe prüfen
				telefon_ok = pattern.matcher(telefon);
				if (telefon_ok.matches() == false) {
					System.out.println("Bitte Telefonnr. korrekt eingeben!");
					System.out.println("Bsp: 0234-343232");
				}
			} while (telefon_ok.matches() == false);
			// solange ausführen bis Telefon korrekt

			// FAX
			Matcher fax_ok;
			String fax;
			do {
				Pattern pattern = Pattern.compile("[0-9].{3,14}");
				System.out.print("Fax: ");
				fax = scanString.nextLine();
				// Fax auf richtige Eingabe prüfen
				fax_ok = pattern.matcher(fax);
				if (fax_ok.matches() == false) {
					System.out.println("Bitte Fax korrekt eingeben!");
					System.out.println("Bsp: 0234-343232");
				}
			} while (fax_ok.matches() == false);
			// solange ausführen bis Fax korrekt

			// BEMERKUNG
			Matcher bem_ok;
			String bem;
			do {
				Pattern pattern = Pattern.compile("[^;]{4,300}");
				System.out.print("Bemerkung: ");
				bem = scanString.nextLine();
				// Bemerkung auf richtige Eingabe prüfen
				bem_ok = pattern.matcher(bem);
				if (bem_ok.matches() == false) {
					System.out.println("Bitte Bemerkung korrekt eingeben!");
					System.out.println("Mind 4 und max 300 zeichen");
				}
			} while (bem_ok.matches() == false);
			// solange ausführen bis Bemerkung korrekt

			System.out.println(); // Abstand

			// Abfragen ob Eingabe stimmt
			System.out.print("Stimmts? J/N : ");
			String a = scanString.nextLine().toUpperCase();
			if (a.equals("J")) {
				Person p = new Person(name, vname, anrede, strasse, plz, ort,
						telefon, fax, bem);
				daten.add(p);
			} else if (a.equals("N")) {
				aufnehmen();
				return;
			} else {
				System.out.println("Falsche Eingabe. J oder N!");
				return;
			}

			// Abfragen ob noch eine Person aufgenommen werden soll
			System.out.print("Noch eine Person? J/N : ");
			String b = scanString.nextLine().toUpperCase();
			if (b.equals("J")) {
				// neue aufnehmen
				aufnehmen();
			} else if (b.equals("N")) {
				// keine aufnehmen-->zurueck zu main()
				System.out.println();// Abstand
				return;
			} else {
				System.out.println("Falsche Eingabe. J oder N!");
				return;
			}
		}

		catch (Exception e) {
			System.out.println(); // Abstand
			System.out.println("Falsche Eingabe");
			e.printStackTrace();
			System.out.println(); // Abstand
		}
	}

	/**
	 * Diese Methode zeigt die im Puffer gespeicherten Datensätze an
	 */
	public void anzeigen() {
		// Scanner initialisieren
		scanInt = new Scanner(System.in);
		scanString = new Scanner(System.in);

		int zaehler = 1;

		try {
			// ArrayList ausgeben
			// Soviele Personen p in daten vorhanden sind wird ausgegeben
			for (Person p : daten) {
				System.out.println("----------------------");
				System.out.println(zaehler + ". Datensatz");
				System.out.println();// Abstand
				System.out.println("Name: " + p.getName());
				System.out.println("Vorname: " + p.getVname());
				System.out.println("Anrede: " + p.getAnrede());
				System.out.println("Strasse: " + p.getStrasse());
				System.out.println("PLZ: " + p.getPlz());
				System.out.println("Ort: " + p.getOrt());
				System.out.println("Telefon: " + p.getTelefon());
				System.out.println("Fax: " + p.getFax());
				System.out.println("Bemerkung: " + p.getBem());
				System.out.println("----------------------");
				System.out.println("Nächster Datensatz: <ENTER> drücken!");
				scanString.nextLine();
				zaehler++;
			}
		} catch (NullPointerException npex) {
			System.out.println();
			System.out.println("Noch keine Datensätze vorhanden!");
			System.out.println();
		} catch (Exception e) {
			System.out.println("Fehler bei der Anzeige: " + e.getMessage());
			System.out.println();
		}
	}

	/**
	 * Diese Methode schickt beim Speichern die LinkedList vom View-Thread an
	 * den Write-Thread
	 */
	private void speichern() {
		try {
			System.out.println("Schicke Personen yum speichern. Anzahl: "
					+ daten.size());
			objectOutputStream.flush();
			objectOutputStream.writeObject(daten);
			objectOutputStream.flush();
			System.out.println("Gesendet.");
		} catch (IOException e) {
			System.out.println("Fehler: " + e.getMessage());
		}
	}

	/**
	 * Diese Methode empfängt beim Laden die LinkedList von Write
	 */
	@SuppressWarnings("unchecked")
	private void laden() {
		System.out.println("Laden von den Personen ...");
		try {
			daten = (List<Person>) objectInputStream.readObject();
			System.out.println("Erfolgreich geladen. Anzahl: " + daten.size());
		} catch (Exception e) {
			System.out.println("Fehler beim laden: " + e.getMessage());
		}
	}

	private void löschen(String filename) {
		try {
			System.out.println("Schicke Dateiname zum loeschen: " + filename);
			objectOutputStream.flush();
			objectOutputStream.writeObject(filename);
			objectOutputStream.flush();
			System.out.println("Filename Gesendet.");
		} catch (IOException e) {
			System.out.println("Fehler: " + e.getMessage());
		}
	}

	// ArrayList sortieren
	// Innere Klasse Sortieren
	class Sortieren implements Comparator<Person> {
		@Override
		public int compare(Person p1, Person p2) {
			return p1.getName().compareTo(p2.getName());
		}
	}

	private String filenameAufnehmen() {
		scanString = new Scanner(System.in);
		String filename = scanString.nextLine();
		return filename;
	}

	// Methode Sortieren
	/**
	 * Diese Methode sortiert die Datensätze nach Name
	 */
	public void sort() {

		try {
			// Sortieren aufrufen
			Collections.sort(daten, new Sortieren());
			System.out.println("erfolgreich sortiert!");
			System.out.println();// Abstand
		} catch (NullPointerException npex) {
			System.out.println("Noch keine Datensätze vorhanden!");
			System.out.println();
		} catch (Exception e) {
			System.out.println("Fehler: " + e.getMessage());
		}
	}// Ende Sortieren
}
