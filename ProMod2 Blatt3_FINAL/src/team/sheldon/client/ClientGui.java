package team.sheldon.client;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import team.sheldon.gemeinsam.DecisionDialog;
import team.sheldon.person.Person;

public class ClientGui extends Frame {

	private static final long serialVersionUID = 1L;

	private MenuBar menuBar;
	private Menu fileMenu, personenMenu;
	private MenuItem verbinden, trennen, aufnehmen, anzeigen, speichern, laden,
			sortieren, loeschen;

	public static boolean connected = false;
	public static boolean error = false;

	private final Client client;
	private int pageCounter;

	public static void main(String[] args) {
		final ClientGui gui = new ClientGui();
		gui.setVisible(true);
		gui.render();
	}

	/**
	 * Constructor.
	 */
	public ClientGui() {
		super("ADRELI");
		this.setLocation(300, 300);
		setSize(500, 500);
		setLayout(null);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				beenden();
			}
		});

		client = new Client();
		renderMenu();
		setVisible(true);
	}

	private void renderMenu() {
		menuBar = new MenuBar();

		fileMenu = new Menu("Datei");
		personenMenu = new Menu("Personen");

		verbinden = new MenuItem("Verbinden");
		trennen = new MenuItem("Trennen");
		aufnehmen = new MenuItem("Aufnehmen");
		anzeigen = new MenuItem("Anzeigen");
		speichern = new MenuItem("Speichern");
		laden = new MenuItem("Laden");
		sortieren = new MenuItem("Sortieren");
		loeschen = new MenuItem("Datei löschen");

		fileMenu.add(verbinden);
		fileMenu.addSeparator();
		fileMenu.add(trennen);
		personenMenu.add(aufnehmen);
		personenMenu.add(anzeigen);
		personenMenu.add(speichern);
		personenMenu.add(laden);
		personenMenu.add(sortieren);
		personenMenu.addSeparator();
		personenMenu.add(loeschen);

		menuBar.add(fileMenu);
		menuBar.add(personenMenu);

		setMenuBar(menuBar);

		if (connected == false) {
			trennen.setEnabled(false);
			aufnehmen.setEnabled(false);
			anzeigen.setEnabled(false);
			speichern.setEnabled(false);
			laden.setEnabled(false);
			sortieren.setEnabled(false);
			loeschen.setEnabled(false);
		} else {
			verbinden.setEnabled(false);
		}

		verbinden.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				connect();
			}
		});

		trennen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				disconnect();
			}
		});

		aufnehmen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				personAufnehmen();
			}
		});

		anzeigen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				personenAnzeigen();
			}
		});

		speichern.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				save();
			}
		});

		laden.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				load();
			}
		});

		sortieren.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				sort();
			}
		});

		loeschen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				delete();
			}
		});
	}

	/**
	 * rendert die GUI
	 */
	public void render() {
		removeAll();

		if (connected) {
			setHeading("Verbunden mit: " + client.getAddress().toString());
			createPopupMenu();
		} else {
			setHeading("Adreli Gui");
			final Label infoLabel = new Label("....aktuell nicht verbunden");
			infoLabel.setBounds(40, 80, 400, 50);
			infoLabel.setVisible(true);

			add(infoLabel);
		}

	}

	private void createPopupMenu() {
		final JPopupMenu popupMenu = new JPopupMenu();

		final JMenuItem aufnehmenItem = new JMenuItem("Person Aufnehmen");
		final JMenuItem anzeigenItem = new JMenuItem("Personen Anzeigen");
		final JMenuItem speichernItem = new JMenuItem("Personen Speichern");
		final JMenuItem ladenItem = new JMenuItem("Personen Laden");
		final JMenuItem sortierenItem = new JMenuItem("Personen Sortieren");
		final JMenuItem loeschenItem = new JMenuItem("Datei loeschen");

		popupMenu.add(aufnehmenItem);
		popupMenu.add(anzeigenItem);
		popupMenu.add(speichernItem);
		popupMenu.add(ladenItem);
		popupMenu.add(sortierenItem);
		popupMenu.add(loeschenItem);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent me) {
				if (me.isPopupTrigger()) {
					popupMenu.show(me.getComponent(), me.getX(), me.getY());
				}
			}
		});

		aufnehmenItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				personAufnehmen();
			}
		});

		anzeigenItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				personenAnzeigen();
			}
		});

		speichernItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				save();
			}
		});

		ladenItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				load();
			}
		});

		sortierenItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				sort();
			}
		});

		loeschenItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				delete();
			}
		});
	}

	/**
	 * setzt den Titel der GUI
	 * 
	 * @param title
	 *            Der Titel
	 */
	public void setHeading(String title) {
		final Label heading = new Label(title);
		heading.setBounds(40, 70, 350, 25);
		final Font headfont = new Font("Arial", Font.BOLD, 18);
		heading.setFont(headfont);
		heading.setVisible(true);

		add(heading);
	}

	private void beenden() {
		if (new DecisionDialog(null, "Beenden", "Wirklich Beenden?")
				.getResult()) {
			dispose();
			System.exit(0);
		}
	}

	/**
	 * Rendert die Verbindung GUI
	 */
	public void connect() {
		removeAll();

		setHeading("Verbinden");

		final Label portLabel = new Label("Port: ");
		portLabel.setBounds(50, 120, 140, 20);
		portLabel.setVisible(true);
		add(portLabel);

		final TextField portInput = new TextField();
		portInput.setBounds(260, 120, 130, 20);
		portInput.setText("56789");
		portInput.setVisible(true);
		this.add(portInput);

		final Label ipLabel = new Label("IP Adresse: ");
		ipLabel.setBounds(50, 150, 140, 20);
		ipLabel.setVisible(true);
		this.add(ipLabel);

		final TextField ipInput = new TextField();
		ipInput.setBounds(260, 150, 130, 20);
		ipInput.setText("localhost");
		ipInput.setVisible(true);
		this.add(ipInput);

		final Button connectButton = new Button("Verbindung herstellen");
		connectButton.setBounds(40, 250, 200, 30);
		connectButton.setVisible(true);
		this.add(connectButton);

		final Label portStatusLabel = new Label("Eingabe falsch!");
		final Label ipStatusLabel = new Label("Eingabe falsch!");

		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {

				boolean error = false;
				portStatusLabel.setVisible(false);
				ipStatusLabel.setVisible(false);

				if (!portInput.getText().matches("[1-9][0-9]{4}")) {
					portStatusLabel.setBounds(400, 120, 100, 20);
					portStatusLabel.setForeground(Color.RED);
					add(portStatusLabel);
					portStatusLabel.setVisible(true);
					error = true;
				}

				if (ipInput.getText().matches(
						"(([1-9][0-9]?[0-9]?)."
								+ "([1-9][0-9]?[0-9]?).([1-9][0-9]?[0-9]?)."
								+ "([1-9][0-9]?[0-9]?))|localhost") == false) {
					ipStatusLabel.setBounds(400, 150, 100, 20);
					ipStatusLabel.setForeground(Color.RED);
					add(ipStatusLabel);
					ipStatusLabel.setVisible(true);
					error = true;
				}

				if (!error) {
					client.start(ipInput.getText(),
							Integer.parseInt(portInput.getText()));

					if (!client.isError()) {
						connected = true;
						render();
						renderMenu();
					} else {
						JOptionPane.showMessageDialog(null,
								"Verbindung konnte nicht hergestellt werden",
								"Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
						connect();
					}

				}
			}
		});
	}

	/**
	 * Restarts the GUI
	 */
	public void restart() {
		dispose();
		new ClientGui().render();
	}

	/**
	 * Öffnet den Dialog ob disconnected werden soll
	 */
	public void disconnect() {
		final DecisionDialog dialog = new DecisionDialog(null,
				"Verbindungsabbau",
				"Wirklich die Verbindung zum Server trennen?");

		if (dialog.getResult()) {
			client.disconnect();
			restart();
		}
	}

	/**
	 * Nimmt die Person auf
	 */
	public void personAufnehmen() {
		removeAll();
		setHeading("Person aufnehmen");

		final Label nameLabel = new Label("Name: ");
		nameLabel.setBounds(50, 120, 140, 20);
		nameLabel.setVisible(true);
		add(nameLabel);

		final TextField nameInput = new TextField();
		nameInput.setBounds(260, 120, 160, 20);
		nameInput.setVisible(true);
		add(nameInput);

		final Label vnameLabel = new Label("Vorname: ");
		vnameLabel.setBounds(50, 150, 140, 20);
		vnameLabel.setVisible(true);
		add(vnameLabel);

		final TextField vnameInput = new TextField();
		vnameInput.setBounds(260, 150, 160, 20);
		vnameInput.setVisible(true);
		add(vnameInput);

		final Label anredeLabel = new Label("Anrede: ");
		anredeLabel.setBounds(50, 180, 140, 20);
		anredeLabel.setVisible(true);
		add(anredeLabel);

		final TextField anredeText = new TextField();
		anredeText.setBounds(260, 180, 160, 20);
		anredeText.setVisible(true);
		add(anredeText);

		final Label strasseLabel = new Label("Strasse: ");
		strasseLabel.setBounds(50, 210, 140, 20);
		strasseLabel.setVisible(true);
		add(strasseLabel);

		final TextField strasseText = new TextField();
		strasseText.setBounds(260, 210, 160, 20);
		strasseText.setVisible(true);
		add(strasseText);

		final Label plzLabel = new Label("PLZ: ");
		plzLabel.setBounds(50, 240, 140, 20);
		plzLabel.setVisible(true);
		add(plzLabel);

		final TextField plzText = new TextField();
		plzText.setBounds(260, 240, 160, 20);
		plzText.setVisible(true);
		add(plzText);

		final Label ortLabel = new Label("Ort: ");
		ortLabel.setBounds(50, 270, 140, 20);
		ortLabel.setVisible(true);
		add(ortLabel);

		final TextField ortText = new TextField();
		ortText.setBounds(260, 270, 160, 20);
		ortText.setVisible(true);
		add(ortText);

		final Label telLabel = new Label("Telefon: ");
		telLabel.setBounds(50, 300, 140, 20);
		telLabel.setVisible(true);
		add(telLabel);

		final TextField telInput = new TextField();
		telInput.setBounds(260, 300, 160, 20);
		telInput.setVisible(true);
		add(telInput);

		final Label faxLabel = new Label("Fax: ");
		faxLabel.setBounds(50, 330, 140, 20);
		faxLabel.setVisible(true);
		add(faxLabel);

		final TextField faxInput = new TextField();
		faxInput.setBounds(260, 330, 160, 20);
		faxInput.setVisible(true);
		add(faxInput);

		final Label bemLabel = new Label("Bemerkung: ");
		bemLabel.setBounds(50, 360, 140, 20);
		bemLabel.setVisible(true);
		add(bemLabel);

		final TextField bemText = new TextField();
		bemText.setBounds(260, 360, 160, 20);
		bemText.setVisible(true);
		add(bemText);

		final Button aufnehmenButton = new Button("Person aufnehmen");
		aufnehmenButton.setBounds(50, 390, 140, 30);
		aufnehmenButton.setVisible(true);
		add(aufnehmenButton);

		final Label nameStatus = new Label("Falsche Eingabe. Bsp.: "
				+ "Mustermann");
		final Label vnameStatus = new Label("Falsche Eingabe. Bsp.: Max");
		final Label anredeStatus = new Label("Falsche Eingabe. Bsp.: "
				+ "Herr oder Frau");
		final Label strasseStatus = new Label("Falsche Eingabe. Bsp.: "
				+ "Strasse. 2");
		final Label plzStatus = new Label("Falsche Eingabe. Bsp.: 78120");
		final Label ortStatus = new Label("Falsche Eingabe. Bsp.: "
				+ "Furtwangen");
		final Label telStatus = new Label("Falsche Eingabe. Bsp.: 123456");
		final Label faxStatus = new Label("Falsche Eingabe. Bsp.: 234567");
		final Label bemStatus = new Label("Falsche Eingabe. Bsp.: "
				+ "keine Bemerkung");

		aufnehmenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				boolean error = false;

				nameStatus.setVisible(false);
				vnameStatus.setVisible(false);
				anredeStatus.setVisible(false);
				strasseStatus.setVisible(false);
				plzStatus.setVisible(false);
				ortStatus.setVisible(false);
				telStatus.setVisible(false);
				faxStatus.setVisible(false);
				bemStatus.setVisible(false);

				Matcher name_ok;
				final Pattern pattern_name = Pattern.compile("[A-Zƒ÷‹]"
						+ "[a-z‰ˆ¸ﬂ]+");
				name_ok = pattern_name.matcher(nameInput.getText());
				if (name_ok.matches() == false) { // wenn falsch
					error = true;
					nameStatus.setBounds(430, 120, 220, 20);
					nameStatus.setForeground(Color.RED);
					add(nameStatus);
					nameStatus.setVisible(true);
				}

				Matcher vname_ok;
				final Pattern pattern_vname = Pattern.compile("[A-Zƒ÷‹]"
						+ "[a-z‰ˆ¸ﬂ]+");
				vname_ok = pattern_vname.matcher(vnameInput.getText());
				if (vname_ok.matches() == false) { // wenn falsch
					error = true;
					vnameStatus.setBounds(430, 150, 220, 20);
					vnameStatus.setForeground(Color.RED);
					add(vnameStatus);
					vnameStatus.setVisible(true);
				}

				Matcher anrede_ok;
				final Pattern pattern_anrede = Pattern.compile("Herr|Frau");
				anrede_ok = pattern_anrede.matcher(anredeText.getText());
				if (anrede_ok.matches() == false) { // wenn falsch
					error = true;
					anredeStatus.setBounds(430, 180, 220, 20);
					anredeStatus.setForeground(Color.RED);
					add(anredeStatus);
					anredeStatus.setVisible(true);
				}

				Matcher strasse_ok;
				final Pattern pattern_strasse = Pattern
						.compile("[a-zA-Z‰ˆ¸ƒ÷‹ \\.]+ [0-9]+");
				strasse_ok = pattern_strasse.matcher(strasseText.getText());
				if (strasse_ok.matches() == false) { // wenn falsch
					error = true;
					strasseStatus.setBounds(430, 210, 220, 20);
					strasseStatus.setForeground(Color.RED);
					add(strasseStatus);
					strasseStatus.setVisible(true);
				}

				Matcher plz_ok;
				final Pattern pattern_plz = Pattern.compile("[1-9][0-9]{4}");
				plz_ok = pattern_plz.matcher(plzText.getText());
				if (plz_ok.matches() == false) { // wenn falsch
					error = true;
					plzStatus.setBounds(430, 240, 220, 20);
					plzStatus.setForeground(Color.RED);
					add(plzStatus);
					plzStatus.setVisible(true);
				}

				Matcher ort_ok;
				final Pattern pattern_ort = Pattern.compile("[A-Zƒ÷‹]"
						+ "[a-z‰ˆ¸ƒ÷‹ﬂ]+");
				ort_ok = pattern_ort.matcher(ortText.getText());
				if (ort_ok.matches() == false) { // wenn falsch
					error = true;
					ortStatus.setBounds(430, 270, 220, 20);
					ortStatus.setForeground(Color.RED);
					add(ortStatus);
					ortStatus.setVisible(true);
				}

				Matcher tel_ok;
				final Pattern pattern_tel = Pattern.compile("[0-9]{3,14}");
				tel_ok = pattern_tel.matcher(telInput.getText());
				if (tel_ok.matches() == false) { // wenn falsch
					error = true;
					telStatus.setBounds(430, 300, 220, 20);
					telStatus.setForeground(Color.RED);
					add(telStatus);
					telStatus.setVisible(true);
				}

				Matcher fax_ok;
				final Pattern pattern_fax = Pattern.compile("[0-9]{3,14}");
				fax_ok = pattern_fax.matcher(faxInput.getText());
				if (fax_ok.matches() == false) { // wenn falsch
					error = true;
					faxStatus.setBounds(430, 330, 220, 20);
					faxStatus.setForeground(Color.RED);
					add(faxStatus);
					faxStatus.setVisible(true);
				}

				Matcher bem_ok;
				final Pattern pattern_bem = Pattern.compile("[^;]{4,300}");
				bem_ok = pattern_bem.matcher(bemText.getText());
				if (bem_ok.matches() == false) { // wenn falsch
					error = true;
					bemStatus.setBounds(430, 360, 220, 20);
					bemStatus.setForeground(Color.RED);
					add(bemStatus);
					bemStatus.setVisible(true);
				}

				if (!error) {
					client.aufnehmen(nameInput.getText(), vnameInput.getText(),
							anredeText.getText(), strasseText.getText(),
							plzText.getText(), ortText.getText(),
							telInput.getText(), faxInput.getText(),
							bemText.getText());

					final DecisionDialog weitere = new DecisionDialog(null,
							"Person aufnehmen",
							"Person aufgenommen! Weiter Person aufnehmen?");

					if (weitere.getResult()) {
						personAufnehmen();
					} else {
						render();
					}
				}
			}
		});
	}

	/**
	 * Zeigt die Personen an
	 */
	public void personenAnzeigen() {
		removeAll();
		setHeading("Records anzeigen");

		pageCounter = 0;

		if (client.getPersonen().isEmpty()) {
			JOptionPane.showMessageDialog(null,
					"Es sind noch keine Personen aufgenommen oder geladen",
					"Info", JOptionPane.INFORMATION_MESSAGE);
			render();
		} else {
			final Label nameLabel = new Label("Name: ");
			nameLabel.setBounds(50, 120, 140, 20);
			nameLabel.setVisible(true);
			add(nameLabel);

			final TextField nameInput = new TextField();
			nameInput.setBounds(260, 120, 160, 20);
			nameInput.setVisible(true);
			add(nameInput);

			final Label vnameLabel = new Label("Vorname: ");
			vnameLabel.setBounds(50, 150, 140, 20);
			vnameLabel.setVisible(true);
			add(vnameLabel);

			final TextField vnameInput = new TextField();
			vnameInput.setBounds(260, 150, 160, 20);
			vnameInput.setVisible(true);
			add(vnameInput);

			final Label anredeLabel = new Label("Anrede: ");
			anredeLabel.setBounds(50, 180, 140, 20);
			anredeLabel.setVisible(true);
			add(anredeLabel);

			final TextField anredeInput = new TextField();
			anredeInput.setBounds(260, 180, 160, 20);
			anredeInput.setVisible(true);
			add(anredeInput);

			final Label strasseLabel = new Label("Strasse: ");
			strasseLabel.setBounds(50, 210, 140, 20);
			strasseLabel.setVisible(true);
			add(strasseLabel);

			final TextField strasseInput = new TextField();
			strasseInput.setBounds(260, 210, 160, 20);
			strasseInput.setVisible(true);
			add(strasseInput);

			final Label plzLabel = new Label("PLZ: ");
			plzLabel.setBounds(50, 240, 140, 20);
			plzLabel.setVisible(true);
			add(plzLabel);

			final TextField plzInput = new TextField();
			plzInput.setBounds(260, 240, 160, 20);
			plzInput.setVisible(true);
			add(plzInput);

			final Label ortLabel = new Label("Ort: ");
			ortLabel.setBounds(50, 270, 140, 20);
			ortLabel.setVisible(true);
			add(ortLabel);

			final TextField ortInput = new TextField();
			ortInput.setBounds(260, 270, 160, 20);
			ortInput.setVisible(true);
			add(ortInput);

			final Label telLabel = new Label("Telefon: ");
			telLabel.setBounds(50, 300, 140, 20);
			telLabel.setVisible(true);
			add(telLabel);

			final TextField telInput = new TextField();
			telInput.setBounds(260, 300, 160, 20);
			telInput.setVisible(true);
			add(telInput);

			final Label faxLabel = new Label("Fax: ");
			faxLabel.setBounds(50, 330, 140, 20);
			faxLabel.setVisible(true);
			add(faxLabel);

			final TextField faxInput = new TextField();
			faxInput.setBounds(260, 330, 160, 20);
			faxInput.setVisible(true);
			add(faxInput);

			final Label bemLabel = new Label("Bemerkung: ");
			bemLabel.setBounds(50, 360, 140, 20);
			bemLabel.setVisible(true);
			add(bemLabel);

			final TextField bemInput = new TextField();
			bemInput.setBounds(260, 360, 160, 20);
			bemInput.setVisible(true);
			add(bemInput);

			final Button naechster_btn = new Button("Nächster Datensatz");
			naechster_btn.setBounds(50, 390, 200, 30);
			naechster_btn.setVisible(true);
			add(naechster_btn);

			final Button zurück_btn = new Button("Zurück");
			zurück_btn.setBounds(300, 390, 150, 30);
			zurück_btn.setVisible(true);
			add(zurück_btn);

			final List<Person> personen = client.getPersonen();
			final Label person;
			person = new Label((pageCounter + 1) + " / " + personen.size());
			person.setBounds(300, 395, 50, 20);
			person.setVisible(true);
			add(person);

			nameInput.setText(personen.get(0).getName());
			nameInput.setEditable(false);
			vnameInput.setText(personen.get(0).getVname());
			vnameInput.setEditable(false);
			anredeInput.setText(personen.get(0).getAnrede());
			anredeInput.setEditable(false);
			strasseInput.setText(personen.get(0).getStrasse());
			strasseInput.setEditable(false);
			plzInput.setText(personen.get(0).getPlz());
			plzInput.setEditable(false);
			ortInput.setText(personen.get(0).getOrt());
			ortInput.setEditable(false);
			telInput.setText(personen.get(0).getTelefon());
			telInput.setEditable(false);
			faxInput.setText(personen.get(0).getFax());
			faxInput.setEditable(false);
			bemInput.setText(personen.get(0).getBem());
			bemInput.setEditable(false);

			zurück_btn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent ae) {
					if (pageCounter > 0) {
						pageCounter--;
						person.setText(pageCounter + " / " + personen.size());

						nameInput.setText(personen.get(pageCounter).getName());
						vnameInput
								.setText(personen.get(pageCounter).getVname());
						anredeInput.setText(personen.get(pageCounter)
								.getAnrede());
						strasseInput.setText(personen.get(pageCounter)
								.getStrasse());
						plzInput.setText(personen.get(pageCounter).getPlz());
						ortInput.setText(personen.get(pageCounter).getOrt());
						telInput.setText(personen.get(pageCounter).getTelefon());
						faxInput.setText(personen.get(pageCounter).getFax());
						bemInput.setText(personen.get(pageCounter).getBem());

						if (pageCounter == 0) {
							zurück_btn.setEnabled(false);
						}
					}

				}
			});

			naechster_btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {

					zurück_btn.setEnabled(true);

					if ((pageCounter + 1) < personen.size()) {
						addZaehler();
						person.setText((pageCounter + 1) + " / "
								+ personen.size());
					}

					nameInput.setText(personen.get(pageCounter).getName());
					vnameInput.setText(personen.get(pageCounter).getVname());
					anredeInput.setText(personen.get(pageCounter).getAnrede());
					strasseInput
							.setText(personen.get(pageCounter).getStrasse());
					plzInput.setText(personen.get(pageCounter).getPlz());
					ortInput.setText(personen.get(pageCounter).getOrt());
					telInput.setText(personen.get(pageCounter).getTelefon());
					faxInput.setText(personen.get(pageCounter).getFax());
					bemInput.setText(personen.get(pageCounter).getBem());

					if ((pageCounter + 1) == personen.size()) {
						naechster_btn.setEnabled(false);
					}
				}
			});
		}
	}

	/**
	 * Erhöht den PageCounter
	 */
	public void addZaehler() {
		pageCounter++;
	}

	/**
	 * Speichert die aufgenommenen Personen
	 */
	public void save() {
		client.save();

		removeAll();
		setHeading("Records speichern");

		final Label speichern = new Label("Speichern erfolgreich.");
		speichern.setBounds(50, 80, 500, 50);
		speichern.setVisible(true);
		add(speichern);
	}

	/**
	 * Ladet die aufgenommenen Personen
	 */
	public void load() {
		client.laden();

		removeAll();
		setHeading("Records laden");

		final Label laden = new Label("Die Records wurden geladen. Anzahl: "
				+ client.getPersonen().size());
		laden.setBounds(50, 80, 500, 50);
		laden.setVisible(true);
		add(laden);
	}

	/**
	 * Sortiert aufgenommene Personen
	 */
	public void sort() {
		client.sort();

		removeAll();
		setHeading("Records sortieren");

		final Label sort = new Label("Sortieren erfolgreich");
		sort.setBounds(50, 80, 500, 50);
		sort.setVisible(true);
		add(sort);
	}

	/**
	 * Löscht die Datenbankdatei
	 */
	public void delete() {
		client.loeschen();

		removeAll();
		setHeading("Datei loeschen");

		final Label delete = new Label("Löschen erfolgreich");
		delete.setBounds(50, 80, 500, 50);
		delete.setVisible(true);
		add(delete);
	}

}
