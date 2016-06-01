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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import team.sheldon.person.Person;

public class Server {

	FileWriter loggerFile;

	public Server() {
		try {
			loggerFile = new FileWriter("adrelilog.txt", true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	private static final int PORT = 56789;

	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;

	public static void main(String[] args) throws IOException {
		Server serverInstance = new Server();
		serverInstance.start();
	}

	private synchronized final void log(Socket socket, String message) {

		String eNet = socket.getInetAddress().toString();
		System.out.println(eNet + ">> " + message);
		try {
			loggerFile
					.write(new Date().toString() + ";" + eNet + ";" + message);
			loggerFile.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void start() throws IOException {
		System.out.println(">>> Server wird gestartet");

		ServerSocket myServerSocket = new ServerSocket(PORT);

		while (true) { // start infinite loop
			final Socket verbindungZumClient = myServerSocket.accept();

			// accept() blocked und liefert Client-Socket

			// zurück; von dem kann gelesen werden,
			// wenn's ein schreibender Client ist

			log(verbindungZumClient, "--> Local Address angemeldet: "
					+ verbindungZumClient.getLocalAddress()); // mit
																// wem?
			log(verbindungZumClient, "--> InetAddress angemeldet: "
					+ verbindungZumClient.getInetAddress());

			try {
				InputStreamReader inputStream = new InputStreamReader(
						verbindungZumClient.getInputStream());
				final BufferedReader in = new BufferedReader(inputStream);
				final BufferedWriter out = new BufferedWriter(
						new OutputStreamWriter(
								verbindungZumClient.getOutputStream()));

				objectOutputStream = new ObjectOutputStream(
						verbindungZumClient.getOutputStream());
				objectInputStream = new ObjectInputStream(
						verbindungZumClient.getInputStream());

				String s;
				while (verbindungZumClient.isConnected()) {
					while ((s = in.readLine()) != null) {
						log(verbindungZumClient, "Nachricht erhalten: " + s);
						handleMessage(s, verbindungZumClient, out);
					}
				}
			} catch (Exception e) {
				log(verbindungZumClient,
						"Fehler in handle message" + e.getMessage());
				e.printStackTrace();
			}
			log(verbindungZumClient, "<-- LocalAddress abgemeldet: "
					+ verbindungZumClient.getLocalAddress());
			log(verbindungZumClient, "<-- InetAddress abgemeldet: "
					+ verbindungZumClient.getInetAddress());
			// verbindungZumClient.close();
		}
	} // end infinite loop

	private void handleMessage(String message, Socket socket,
			BufferedWriter ausgang) throws IOException, ClassNotFoundException {
		switch (message) {

		case "1":
			log(socket, "1");
			break;

		case "2":
			log(socket, "2");
			break;

		case "3":
			log(socket, "3");
			@SuppressWarnings("unchecked")
			List<Person> personen = (List<Person>) objectInputStream
					.readObject();
			// Cast (Zaubert das Object in eine Liste von Prs
			log(socket,
					"Personen vom Client bekommen. Anzahl: " + personen.size());
			save(personen, socket);

			break;

		case "4":
			log(socket, "4");
			List<Person> geladenePersonen = laden(socket);

			objectOutputStream.flush();
			objectOutputStream.writeObject(geladenePersonen);
			objectOutputStream.flush(); // flushd (wie beim stuff das gewritete
			// "durch"
			log(socket, "Schicke Personen an den Client. Anzahl: "
					+ geladenePersonen.size());
			log(socket, "Flushing stream ...");
			// objectPost.close();
			break;

		case "5":
			log(socket, "5");
			break;

		case "6":
			log(socket, "6");
			break;
		case "7":
			log(socket, "7");
			break;

		default:
			log(socket, "Nicht erkennbar :" + message);
			break;

		}
	}

	private final void save(List<Person> personen, Socket socket) {
		try {
			File f = new File("adreli.csv");
			if (!f.exists()) {
				log(socket, "Datei existiert nicht, wird erstellt.");
				f.createNewFile();
			}
		}

		catch (Exception e) {
			log(socket, "Fehler: " + e.getMessage());
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
			log(socket, "Fehler: " + e.getMessage());
		}

		log(socket, "Personen wurden in Datei gespeichert");
		// Ende in Datei schreiben

	}// Ende save()-Methode

	private final List<Person> laden(Socket socket) {
		log(socket, "Personen werden geladen ...");
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
			log(socket, "Fehler: " + e.getMessage());
		}
		log(socket, "Personen geladen, Anzahl: " + daten.size());
		return daten;

	}

}
