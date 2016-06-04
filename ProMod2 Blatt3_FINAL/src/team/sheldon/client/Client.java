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

import team.sheldon.person.Person;

public class Client {

	// Scanner
	private Scanner scanInt;
	private Scanner scanString;

	// Variablen
	private int auswahl;

	// Daten
	private List<Person> daten = new LinkedList<>();
	private Socket socket;
	private InetSocketAddress address;
	private BufferedWriter writer;
	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;
	private boolean error;

	public InetSocketAddress getAddress() {
		return address;
	}

	public void start(String ip, int port) {
		error = false;
		address = new InetSocketAddress(ip, port);
		socket = new Socket();

		try {
			socket.connect(address);

			System.out.println("Initilize writer ...");
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			objectInputStream = new ObjectInputStream(socket.getInputStream());
			System.out.println("Initilize output stream ...");
			objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		} catch (final IOException e2) {
			e2.printStackTrace();
			error = true;
		}
	}

	public boolean isError() {
		return error;
	}

	public List<Person> getPersonen() {
		return new LinkedList<Person>(daten);
	}

	protected void sendeCode(int code) {
		try {
			System.out.println("Sende Nachricht: " + code);
			writer.write(Integer.toString(code));
			writer.newLine();
			writer.flush();
		} catch (final Exception e) {
			System.out.println("Fehler in sende code: " + e.getMessage());
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
		} catch (final Exception e) {
			System.out.println("Fehler in Menue: " + e.getMessage());
		}

		switch (auswahl) {
		case 1:
			break;
		case 2:
			anzeigen();
			break;
		case 3:
			save();
			break;
		case 4:
			laden();
			break;
		case 5:
			System.out.println("Sortierung...");
			sort();
			break;
		case 6:
			loeschen();
			break;
		case 7:
			disconnect();
			break;
		default:
			System.out.println("Fehler bei der Eingabe");
			break;
		}
	}

	public void disconnect() {
		sendeCode(7);
		try {
			writer.close();
			socket.close();
		} catch (final IOException e) {
			System.out.println("Problem beim disconnect");
			e.printStackTrace();
		}
	}

	public void aufnehmen(String name, String vname, String anrede, String strasse, String plz, String ort,
			String telefon, String fax, String bem) {
		final Person neuePerson = new Person(name, vname, anrede, strasse, plz, ort, telefon, fax, bem);
		daten.add(neuePerson);
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
			for (final Person p : daten) {
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
		} catch (final NullPointerException npex) {
			System.out.println();
			System.out.println("Noch keine Datensätze vorhanden!");
			System.out.println();
		} catch (final Exception e) {
			System.out.println("Fehler bei der Anzeige: " + e.getMessage());
			System.out.println();
		}
	}

	/**
	 * Diese Methode schickt beim Speichern die LinkedList vom View-Thread an
	 * den Write-Thread
	 */
	void save() {
		sendeCode(3);
		try {
			System.out.println("Schicke Personen yum speichern. Anzahl: " + daten.size());
			objectOutputStream.flush();
			objectOutputStream.writeObject(daten);
			objectOutputStream.flush();
			System.out.println("Gesendet.");
		} catch (final IOException e) {
			System.out.println("Fehler: " + e.getMessage());
		}
	}

	/**
	 * Diese Methode empfängt beim Laden die LinkedList von Write
	 */
	@SuppressWarnings("unchecked")
	protected void laden() {
		sendeCode(4);
		System.out.println("Laden von den Personen ...");
		try {
			daten = (List<Person>) objectInputStream.readObject();
			System.out.println("Erfolgreich geladen. Anzahl: " + daten.size());
		} catch (final Exception e) {
			System.out.println("Fehler beim laden: " + e.getMessage());
		}
	}

	protected void loeschen() {
		sendeCode(6);
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
		final String filename = scanString.nextLine();
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
		} catch (final NullPointerException npex) {
			System.out.println("Noch keine Datensätze vorhanden!");
			System.out.println();
		} catch (final Exception e) {
			System.out.println("Fehler: " + e.getMessage());
		}
	}// Ende Sortieren

	public String getIp() {
		return address != null ? address.getHostName() : "";
	}

	public String getPort() {
		return address != null ? String.valueOf(address.getPort()) : "";
	}
}
