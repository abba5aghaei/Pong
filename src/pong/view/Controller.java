package pong.view;

import pong.Pong;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

public class Controller {
	
	private Pong pong;
	@FXML
	private AnchorPane pane;
	@FXML
	private Line dash;
	@FXML
	private Label ball;
	@FXML
	private Label rightRacket;
	@FXML
	private Label leftRacket;
	@FXML
	private Label watch;
	@FXML
	private Label rightScore;
	@FXML
	private Label leftScore;
	@FXML
	private AnchorPane settingPane;
	@FXML
	private TextField goalField;
	@FXML
	private Slider speedMetter;
	@FXML
	private AnchorPane congratulationPane;
	@FXML
	private Label congratulationLabel;
	
	public void initialize() {
		dash.getStrokeDashArray().add(20d);
	}
	
	public void setPong(Pong p) {
		pong = p;
		pong.setBall(ball);
		pong.setCongratulationLabel(congratulationLabel);
		pong.setCongratulationPane(congratulationPane);
		pong.setGoalField(goalField);
		pong.setLeftRacket(leftRacket);
		pong.setLeftScore(leftScore);
		pong.setPane(pane);
		pong.setRightRacket(rightRacket);
		pong.setRightScore(rightScore);
		pong.setSettingPane(settingPane);
		pong.setSpeedMetter(speedMetter);
		pong.setWatch(watch);
	}

	public void shot() {
		pong.shot();
	}

	public void showSetting() {
		pong.showSetting();
	}

	public void apply() {
		pong.apply();
	}

	public void close() {
		pong.close();
	}

	public void restart() {
		pong.restart();
	}

	public void minimize() {
		pong.minimize();
	}

	public void exit() {
		pong.exit();
	}
}
