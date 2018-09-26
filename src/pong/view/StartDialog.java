package pong.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import pong.Pong;
import pong.model.Network;

public class StartDialog {

	private Stage stage;
	private Pong pong;
	private Network network;
	@FXML
	private Label type_label;
	@FXML
	private Label port_label;
	@FXML
	private Label ip_label;
	@FXML
	private Label waitLabel;
	@FXML
	private Button start_button;
	@FXML
	private ProgressIndicator waiter;
	@FXML
	private ToggleButton type;
	@FXML
	private TextField PORT;
	@FXML
	private TextField IP;
	@FXML
	private RadioButton server_radio;
	@FXML
	private RadioButton client_radio;

	public void initialize() {
		ToggleGroup group = new ToggleGroup();
		server_radio.setToggleGroup(group);
		client_radio.setToggleGroup(group);
	}

	public void setNetwork(Network n) {
		network = n;
	}

	public void setStage(Stage s) {
		stage = s;
	}

	public void setPong(Pong p) {
		pong = p;
	}

	public void networkHandller() {
		if (type.isSelected()) {
			type.setText("شبکه");
			port_label.setDisable(false);
			ip_label.setDisable(false);
			PORT.setDisable(false);
			IP.setDisable(false);
			server_radio.setDisable(false);
			client_radio.setDisable(false);
		} else {
			type.setText("آفلاین");
			port_label.setDisable(true);
			ip_label.setDisable(true);
			PORT.setDisable(true);
			IP.setDisable(true);
			server_radio.setDisable(true);
			client_radio.setDisable(true);
		}
	}

	public void cancel() {
		try {
			if (network.getServerSocket() != null)
				if (!(network.getServerSocket().isClosed()))
					network.getServerSocket().close();
			if (network.getSocket() != null)
				if (!(network.getSocket().isClosed()))
					network.getSocket().close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		stage.close();
	}

	public void startGame() {
		if (type.isSelected()) {
			changeVisibility(true);
			network.setIp(IP.getText());
			network.setPort(Integer.parseInt(PORT.getText()));
			if (server_radio.isSelected()) {
				pong.setPlayer(true);
				network.createServer();
			} else {
				pong.setPlayer(false);
				network.createClient();
			}
		} else {

		}
	}

	private void changeVisibility(boolean b) {
		waiter.setVisible(b);
		waitLabel.setVisible(b);
		IP.setDisable(b);
		PORT.setDisable(b);
		client_radio.setDisable(b);
		server_radio.setDisable(b);
		type.setDisable(b);
		ip_label.setDisable(b);
		port_label.setDisable(b);
		start_button.setDisable(b);
	}
}
