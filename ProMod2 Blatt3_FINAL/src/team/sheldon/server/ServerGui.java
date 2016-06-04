package team.sheldon.server;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import team.sheldon.gemeinsam.DecisionDialog;

public class ServerGui extends Frame {

	private static final String MY_SQL = "MySQL";

	private static final String CSV = "CSV";

	private static final String DEFAULT_PORT = "56789";

	private static final long serialVersionUID = 1L;

	private static boolean isStarted;
	private static int anzahlClient = 0;
	private static String selectedFilePath = "";

	private static Label clientCountLabel;

	private static TextField portInput;

	private Button startStopButton;

	private Button filePicker;

	private Choice datenbasisCombo;

	public static void main(String[] args) {
		final ServerGui serverGui = new ServerGui();
	}

	public static void incrementClients() {
		anzahlClient++;
		clientCountLabel.setText(String.valueOf(anzahlClient));
	}

	public static void decrementClients() {
		anzahlClient--;
		clientCountLabel.setText(String.valueOf(anzahlClient));
	}

	private static String getSelectedFilePath() {
		return selectedFilePath;
	}

	private static void setSelectedFilePath(String filePath) {
		selectedFilePath = filePath;
	}

	public ServerGui() {
		super("ADRELI Server Administration");

		// Add a listener to this frame to close the application as soon as the
		// frame is closed.
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				handleBeenden();
			}
		});

		// Set size of window
		setSize(550, 230);
		setLocation(400, 300);
		// No Layout Manager, we do it by hand
		setLayout(null);

		createConnectedClientsLabel();

		createPortInput();
		createStartStopButton();
		createDatenbasisPart();

		setVisible(true);
	}

	private void createConnectedClientsLabel() {
		final Label clients = new Label("Connected Clients");
		clients.setBounds(30, 30, 150, 20);
		clients.setVisible(true);
		add(clients);

		clientCountLabel = new Label("0");
		clientCountLabel.setBounds(180, 30, 100, 20);
		clientCountLabel.setVisible(true);
		add(clientCountLabel);
	}

	private void createPortInput() {
		final Label portLabel = new Label();
		portLabel.setText("Port");
		portLabel.setBounds(30, 60, 100, 20);
		portLabel.setVisible(true);
		add(portLabel);

		portInput = new TextField();
		portInput.setText(DEFAULT_PORT);
		portInput.setBounds(180, 60, 100, 20);
		portInput.setVisible(true);
		add(portInput);
	}

	private void createDatenbasisPart() {
		final Label datenbasis = new Label("Datenbasis");
		datenbasis.setBounds(30, 90, 100, 20);
		datenbasis.setVisible(true);
		add(datenbasis);

		final Label databasisInfo = new Label("Bitte eine Datei waehlen");
		databasisInfo.setBounds(180, 120, 400, 20);
		databasisInfo.setVisible(true);
		add(databasisInfo);

		datenbasisCombo = new Choice();
		datenbasisCombo.setBounds(180, 90, 100, 20);
		datenbasisCombo.add(CSV);
		datenbasisCombo.add(MY_SQL);
		datenbasisCombo.setVisible(true);
		add(datenbasisCombo);

		filePicker = new Button("Datei waehlen");
		filePicker.setBounds(290, 90, 90, 20);
		filePicker.setVisible(true);
		add(filePicker);

		final FileDialog fileDialog = new FileDialog(this, "CSV Datei waehlen", FileDialog.LOAD);
		fileDialog.setSize(300, 300);

		filePicker.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileDialog.setVisible(true);

				final String directory = fileDialog.getDirectory();
				final String file = fileDialog.getFile();

				if (file != null) {
					final String filePath = directory + file;
					setSelectedFilePath(filePath);
					databasisInfo.setText("Datei: " + selectedFilePath);
				}
			}
		});

		datenbasisCombo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				final String selection = (String) e.getItem();
				if (selection.equals(MY_SQL)) {
					filePicker.setEnabled(false);
					startStopButton.setEnabled(false);
					databasisInfo.setText("Not yet implemented");
				} else if (selection.equals(CSV)) {
					filePicker.setEnabled(true);
					startStopButton.setEnabled(true);
					databasisInfo.setText(
							!selectedFilePath.isEmpty() ? "Datei: " + selectedFilePath : "Bitte eine Datei waehlen");
				}
			}
		});
	}

	private void createStartStopButton() {
		startStopButton = new Button("start server");
		startStopButton.setBounds(30, 150, 200, 30);

		final Label errorLabel = new Label();
		errorLabel.setVisible(true);
		errorLabel.setBounds(30, 180, 200, 30);

		final Label statusLabel = new Label("Status: offline");
		statusLabel.setForeground(Color.RED);
		statusLabel.setVisible(true);
		statusLabel.setBounds(240, 150, 100, 30);
		add(statusLabel);

		startStopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isStarted) {
					handleBeenden();
				} else {
					boolean valid = true;
					errorLabel.setText("");
					if (getSelectedFilePath().isEmpty()) {
						valid = false;
						errorLabel.setText("File cant be empty. ");
					}

					if (!portInput.getText().matches("[1-9][0-9]{4}")) {
						valid = false;
						errorLabel.setText(errorLabel.getText() + "Port must be a valid port. ");
					}

					if (valid) {
						isStarted = true;
						portInput.setEnabled(false);
						filePicker.setEnabled(false);
						datenbasisCombo.setEnabled(false);
						startStopButton.setLabel("Server beenden");
						final Server server = new Server(Integer.parseInt(portInput.getText()), selectedFilePath);
						server.start();
						statusLabel.setText("Status: online");
						statusLabel.setForeground(Color.GREEN);
					}
				}

			}

		});

		add(errorLabel);
		add(startStopButton);
	}

	private void handleBeenden() {
		final DecisionDialog dialog;
		if (anzahlClient > 0) {
			dialog = new DecisionDialog(null, "Wirklich beenden?", "Es sind noch " + anzahlClient + " verbunden.");
		} else {
			dialog = new DecisionDialog(null, "Wirklich beenden?", "Wollen Sie wirklich beenden?");
		}

		if (dialog.getResult()) {
			System.exit(1);
		}
	}
}
