//Stephen Popp 250346, Seyma Nur Coban 250596, Florian Möhrle 250013

package team.sheldon.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import team.sheldon.person.Person;

/**
 * Klasse die eine Verbindung zu einem Client representiert und die connection
 * handled.
 */
public class ClientConnectionThread extends Thread {

	private Socket socket;
	private Logger logger;

	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;

	public ClientConnectionThread(Socket socket, Logger logger) {
		this.socket = socket;
		this.logger = logger;
	}

	@Override
	public void run() {

		try {
			InputStreamReader inputStream = new InputStreamReader(
					socket.getInputStream());
			final BufferedReader in = new BufferedReader(inputStream);
			final BufferedWriter out = new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream()));

			objectOutputStream = new ObjectOutputStream(
					socket.getOutputStream());
			objectInputStream = new ObjectInputStream(socket.getInputStream());

			String s;
			while (socket.isConnected()) {
				while ((s = in.readLine()) != null) {
					logger.log(socket, "Nachricht erhalten: " + s);
					handleMessage(s, socket, out);
				}
			}
		} catch (Exception e) {
			logger.log(socket, "Fehler in handle message" + e.getMessage());
			e.printStackTrace();
		}
		logger.log(socket,
				"<-- LocalAddress abgemeldet: " + socket.getLocalAddress());
		logger.log(socket,
				"<-- InetAddress abgemeldet: " + socket.getInetAddress());
	}

	private void handleMessage(String message, Socket socket,
			BufferedWriter ausgang) throws IOException, ClassNotFoundException {
		switch (message) {
		case "1":
			logger.log(socket, "1");
			break;
		case "2":
			logger.log(socket, "2");
			break;

		case "3":
			logger.log(socket, "3");
			@SuppressWarnings("unchecked")
			List<Person> personen = (List<Person>) objectInputStream
					.readObject();
			// Cast (Zaubert das Object in eine Liste von Prs
			logger.log(socket, "Personen vom Client bekommen. Anzahl: "
					+ personen.size());
			save(personen, socket);

			break;

		case "4":
			logger.log(socket, "4");
			List<Person> geladenePersonen = laden(socket);

			objectOutputStream.flush();
			objectOutputStream.writeObject(geladenePersonen);
			objectOutputStream.flush(); // flushd (wie beim stuff das gewritete
			// "durch"
			logger.log(socket, "Schicke Personen an den Client. Anzahl: "
					+ geladenePersonen.size());
			logger.log(socket, "Flushing stream ...");
			// objectPost.close();
			break;

		case "5":
			logger.log(socket, "5");
			break;

		case "6":
			logger.log(socket, "6");
			String filename = (String) objectInputStream.readObject();
			logger.log(socket, "Zu loeschende Datei: " + filename);
			deleteFile(filename);
			break;

		case "7":
			logger.log(socket, "7");
			socket.close();
			break;

		default:
			logger.log(socket, "Nicht erkennbar :" + message);
			break;

		}
	}

	private final void save(List<Person> personen, Socket socket) {
		try {
			File f = new File("adreli.csv");
			if (!f.exists()) {
				logger.log(socket, "Datei existiert nicht, wird erstellt.");
				f.createNewFile();
			}
		}

		catch (Exception e) {
			logger.log(socket, "Fehler: " + e.getMessage());
		}// Ende Datei erstellen

		// In Datei schreiben
		try {
			FileWriter datei;

			/*
			 * Alle Daten (bzw. Personen) der ArraysList in die Datei
			 * untereinander speichern
			 */
			for (Person p : personen) {
				datei = new FileWriter("adreli.csv", true);
				datei.write(p.getName() + ";" + p.getVname() + ";"
						+ p.getAnrede() + ";" + p.getStrasse() + ";"
						+ p.getPlz() + ";" + p.getOrt() + ";" + p.getTelefon()
						+ ";" + p.getFax() + ";" + p.getBem());
				datei.append(System.getProperty("line.separator"));
				datei.close();// Writer-Stream wieder schliessen
			}
		} catch (Exception e) {
			logger.log(socket, "Fehler: " + e.getMessage());
		}

		logger.log(socket, "Personen wurden in Datei gespeichert");
		// Ende in Datei schreiben

	}// Ende save()-Methode

	private final List<Person> laden(Socket socket) {
		logger.log(socket, "Personen werden geladen ...");
		List<Person> daten = new ArrayList<Person>();
		// ArrayList wird gefüllt

		try {
			// eindim. Array deklarieren als Zwischenspeicher
			String[] liste = null;

			FileReader fr = new FileReader("adreli.csv");
			BufferedReader br = new BufferedReader(fr);

			String zeile = null;
			while ((zeile = br.readLine()) != null) {
				liste = zeile.split(";");
				Person p = new Person(liste[0], liste[1], liste[2], liste[3],
						liste[4], liste[5], liste[6], liste[7], liste[8]);
				daten.add(p);
			}

			fr.close();
			br.close();

		} catch (Exception e) {
			logger.log(socket, "Fehler: " + e.getMessage());
		}
		logger.log(socket, "Personen geladen, Anzahl: " + daten.size());
		return daten;

	}

	private void deleteFile(String filename) {
		// TODO Exception Handling
		File file = new File(filename);
		if (file.delete()) {
			System.out.println("Datei: " + filename + " wurde geloescht.");
		} else {
			System.out.println("Loeschen der Datei " + filename
					+ " hat leider nicht geklappt.");
		}
	}

}
