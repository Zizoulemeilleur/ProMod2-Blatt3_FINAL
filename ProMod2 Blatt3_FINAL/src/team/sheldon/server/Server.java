package team.sheldon.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

	private final Logger logger;
	private final int port;
	private final String filename;

	public Server(int port, String filename) {
		this.port = port;
		this.filename = filename;
		logger = new Logger("adrelilog.txt");
	}

	@Override
	public void run() {
		System.out.println(">>> Server wird gestartet");
		ServerSocket myServerSocket;
		try {
			final InetSocketAddress myInetSocketAddress = new InetSocketAddress(
					"localhost", port);
			myServerSocket = new ServerSocket();
			myServerSocket.bind(myInetSocketAddress);

			while (true) { // start infinite loop
				final Socket verbindungZumClient = myServerSocket.accept();
				ServerGui.incrementClients();
				logger.log(
						verbindungZumClient,
						"--> Local Address angemeldet: "
								+ verbindungZumClient.getLocalAddress());
				logger.log(verbindungZumClient, "--> InetAddress angemeldet: "
						+ verbindungZumClient.getInetAddress());
				new ClientConnectionThread(verbindungZumClient, logger,
						filename).start();
			}
		} catch (final IOException e) {
			// Sockets can throw errors.
			System.out.println("Something went wrong");
			e.printStackTrace();
		}
	} // end
		// infinite
		// loop

}