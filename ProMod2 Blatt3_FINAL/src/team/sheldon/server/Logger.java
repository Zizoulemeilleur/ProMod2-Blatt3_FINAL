//Stephen Popp 250346, Seyma Nur Coban 250596, Florian Möhrle 250013

package team.sheldon.server;

import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

public class Logger {

	FileWriter loggerFile;

	public Logger(String filename) {
		try {
			loggerFile = new FileWriter(filename, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized final void log(Socket socket, String message) {

		String eNet = socket.getInetAddress().toString();
		System.out.println(eNet + ">> " + message);
		try {
			loggerFile
					.write(new Date().toString() + ";" + eNet + ";" + message);
			loggerFile.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
