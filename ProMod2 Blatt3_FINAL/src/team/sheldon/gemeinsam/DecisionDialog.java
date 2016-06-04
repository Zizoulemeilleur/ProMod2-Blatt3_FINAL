package team.sheldon.gemeinsam;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DecisionDialog extends Dialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private final Button yesButton;
	private final Button noButton;
	private boolean yesClicked;

	public DecisionDialog(Dialog owner, String title, String msg) {
		super(owner, title, true);

		setSize(280, 180);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(null);

		final Label label = new Label(msg);
		label.setSize(200, 40);
		label.setLocation(20, 50);
		label.setAlignment(Label.CENTER);
		add(label);

		yesButton = new Button("Ja");
		yesButton.setSize(70, 25);
		yesButton.setLocation(70, 100);
		yesButton.addActionListener(this);

		noButton = new Button("Nein");
		noButton.setSize(70, 25);
		noButton.setLocation(160, 100);
		noButton.addActionListener(this);

		add(yesButton);
		add(noButton);

		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(yesButton)) {
			yesClicked = true;
		}
		setVisible(false);
		dispose();
	}

	public boolean getResult() {
		return yesClicked;
	}

}
