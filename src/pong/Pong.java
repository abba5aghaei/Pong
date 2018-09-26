package pong;

import java.awt.Robot;
import java.awt.AWTException;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import pong.model.Network;
import pong.view.Controller;

public class Pong {

	private Stage stage;
	private AnchorPane pane;
	private Label ball;
	private Label rightRacket;
	private Label leftRacket;
	private Label watch;
	private Label rightScore;
	private Label leftScore;
	private AnchorPane settingPane;
	private TextField goalField;
	private Slider speedMetter;
	private AnchorPane congratulationPane;
	private Label congratulationLabel;
	private Robot robot;
	private Movement movement;
	private Network network;
	private Shake shake;
	private Timer timer;
	private Font font;
	private double rightY;
	private double leftY;
	private double ownY;
	private double oppY;
	private double ballX;
	private double ballY;
	private int goalScore;
	private int left_score;
	private int right_score;
	private int second;
	private int bsecond;
	private int minute;
	private int bminute;
	private boolean gamePause;
	private boolean stick;
	private boolean turn;
	private boolean server;
	private boolean client;
	private boolean player;

	public void initialize() {
		server = true;
		client = false;
		gamePause = false;
		turn = false;
		goalScore = 15;
		left_score = 0;
		right_score = 0;
		second = 0;
		bsecond = 0;
		minute = 0;
		bminute = 0;
		timer = new Timer();
		timer.start();
		timer.suspend();
		stage = new Stage();
		EscapeHandler handler = new EscapeHandler();
		stage.addEventHandler(KeyEvent.KEY_RELEASED, handler);
		if (player == server) {
			setMouseMoveFor(leftRacket);
			stick = false;
		} else if (player == client) {
			setMouseMoveFor(rightRacket);
			stick = true;
			movement = new Movement();
			movement.start();
			movement.suspend();
		}
		try {
			robot = new Robot();
			font = Font.loadFont(getClass().getResource("digital-7.ttf").toExternalForm(), 60.0);
			watch.setFont(font);
			font = Font.loadFont(getClass().getResource("digital-7.ttf").toExternalForm(), 90.0);
			leftScore.setFont(font);
			rightScore.setFont(font);
		} catch (AWTException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void process(String command) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (command.startsWith("MoveTo")) {
					oppY = Double.parseDouble(command.substring(7, command.length()));
					if (player == client)
						leftRacket.setLayoutY(oppY);
					else if (player == server)
						rightRacket.setLayoutY(oppY);
				} else if (command.equals("scoreClient")) {
					stick = true;
					timer.suspend();
					right_score++;
					if (right_score < 10) {
						rightScore.setText("0" + right_score);
					} else {
						rightScore.setText(String.valueOf(right_score));
					}
					turn = server;
					flash(leftRacket);
					flash(ball);
					checkForGoal();
				} else if (command.equals("scoreServer")) {
					stick = true;
					timer.suspend();
					left_score++;
					if (left_score < 10) {
						leftScore.setText("0" + left_score);
					} else {
						leftScore.setText(String.valueOf(left_score));
					}
					turn = client;
					flash(rightRacket);
					flash(ball);
					checkForGoal();
				} else if (command.startsWith("BallXTo")) {
					ballX = Double.parseDouble(command.substring(8, command.length()));
					ball.setLayoutX(ballX);
				} else if (command.startsWith("BallYTo")) {
					ballY = Double.parseDouble(command.substring(8, command.length()));
					ball.setLayoutY(ballY);
				} else if (command.equals("pause")) {
					gamePause = true;
					settingPane.setVisible(true);
					if (player == client) {
						movement.suspend();
					}
					timer.suspend();
				} else if (command.equals("resume")) {
					settingPane.setVisible(false);
					timer.resume();
					gamePause = false;
					if (player == client) {
						movement.resume();
					}
				} else if (command.equals("exit")) {
					exit();
				} else if (command.startsWith("speed")) {
					movement.setSpeed(Double.parseDouble(command.substring(6, command.length())));
				} else if (command.startsWith("goal")) {
					goalScore = Integer.parseInt(command.substring(5, command.length()));
				} else if (command.equals("shot")) {
					stick = false;
					timer.resume();
					if (!timer.isAlive()) {
						timer.stop();
						timer = new Timer();
						timer.start();
					}
					if (player == client) {
						movement.resume();
						if (!movement.isAlive()) {
							movement.stop();
							movement = new Movement();
							movement.start();
						}
					}
				}
			}
		});
	}

	public void shot() {
		if (player == client) {
			if (turn == client) {
				if (stick) {
					network.send("shot");
					stick = false;
					timer.resume();
					if (!timer.isAlive()) {
						timer.stop();
						timer = new Timer();
						timer.start();
					}
					movement = new Movement();
					movement.start();
				}
			}
		} else if (player == server) {
			if (turn == server) {
				if (stick) {
					network.send("shot");
					stick = false;
					timer.resume();
					if (!timer.isAlive()) {
						timer.stop();
						timer = new Timer();
						timer.start();
					}
				}
			}
		}
	}

	public void increaseScore(boolean p) {
		stickBallTo(p);
		stick = true;
		timer.suspend();
		if (p) {
			left_score++;
			if (left_score < 10) {
				leftScore.setText("0" + left_score);
			} else {
				leftScore.setText(String.valueOf(left_score));
			}
			turn = client;
			flash(rightRacket);
		} else {
			right_score++;
			if (right_score < 10) {
				rightScore.setText("0" + right_score);
			} else {
				rightScore.setText(String.valueOf(right_score));
			}
			turn = server;
			flash(leftRacket);
		}
		flash(ball);
		checkForGoal();
	}

	private void checkForGoal() {
		if (player == client) {
			movement.stop();
		}
		timer.stop();
		if (right_score >= goalScore) {
			gamePause = true;
			congratulationLabel.setText("Congratulation Client is Win!");
			congratulationPane.setVisible(true);
			robot.mouseMove(680, 400);
		} else if (left_score >= goalScore) {
			gamePause = true;
			congratulationLabel.setText("Congratulation Server is Win!");
			congratulationPane.setVisible(true);
			robot.mouseMove(680, 400);
		}
	}

	public void showSetting() {
		gamePause = true;
		network.send("pause");
		if (player == client) {
			movement.suspend();
		}
		timer.suspend();
		settingPane.setVisible(true);
	}

	private void stickBallTo(boolean p) {
		if (p == server) {
			ball.setLayoutX(1225);
			ball.setLayoutY(rightRacket.getLayoutY() + 60);
		} else {
			ball.setLayoutX(55);
			ball.setLayoutY(leftRacket.getLayoutY() + 60);
		}
		network.send("BallXTo:" + ball.getLayoutX());
		network.send("BallYTo:" + ball.getLayoutY());
	}

	private void flash(Label l) {
		FadeTransition flasher = new FadeTransition();
		flasher.setNode(l);
		flasher.setFromValue(0);
		flasher.setToValue(100);
		flasher.setCycleCount(5);
		flasher.play();
	}

	private void setMouseMoveFor(Label racket) {
		shake = new Shake(racket);
		pane.addEventHandler(MouseEvent.MOUSE_MOVED, shake);
	}

	public void apply() {
		movement.setSpeed(speedMetter.getValue());
		goalScore = Integer.parseInt(goalField.getText());
		network.send("speed:" + speedMetter.getValue());
		network.send("goal:" + goalField.getText());
	}

	public void close() {
		network.send("resume");
		timer.resume();
		if (!timer.isAlive()) {
			timer.stop();
			timer = new Timer();
			timer.start();
		}
		if (player == client) {
			movement.resume();
			if (!movement.isAlive()) {
				movement.stop();
				movement = new Movement();
				movement.start();
			}
		}
		settingPane.setVisible(false);
		gamePause = false;
	}

	public void restart() {
		gamePause = false;
		stick = true;
		turn = true;
		goalScore = 15;
		left_score = 0;
		right_score = 0;
		second = 0;
		bsecond = 0;
		minute = 0;
		bminute = 0;
		congratulationPane.setVisible(false);
		watch.setText("00:00");
		rightScore.setText("00");
		leftScore.setText("00");
		try {
			if (timer.isAlive())
				timer.stop();
			timer = new Timer();
			timer.start();
			timer.suspend();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			if (player == client) {
				if (movement.isAlive())
					movement.stop();
				movement = new Movement();
				movement.start();
				movement.suspend();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void minimize() {
		stage.setIconified(true);
	}

	public void exit() {
		try {
			if (player == client)
				if (movement.isAlive()) {
					movement.stop();
					movement.destroy();
				}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			network.send("exit");
			System.exit(0);
		}
	}

	public void showGame() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/Graphic.fxml"));
			AnchorPane root = (AnchorPane) loader.load();
			Scene scene = new Scene(root);
			Controller controller = loader.getController();
			controller.setPong(this);
			initialize();
			stage.setScene(scene);
			stage.setTitle("پینگ پنگ");
			stage.getIcons().addAll(new Image(Pong.class.getResourceAsStream("view/icon.png")));
			stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
			stage.setFullScreenExitHint("");
			stage.setFullScreen(true);
			stage.show();
		} catch (Exception e) {
			System.out.println("Error in game loading: " + e.getMessage());
		}
	}

	public void setNetwork(Network n) {
		network = n;
	}

	public void setPlayer(boolean p) {
		player = p;
	}

	public void setPane(AnchorPane pane) {
		this.pane = pane;
	}

	public void setBall(Label b) {
		ball = b;
	}

	public void setRightRacket(Label rightRacket) {
		this.rightRacket = rightRacket;
	}

	public void setLeftRacket(Label leftRacket) {
		this.leftRacket = leftRacket;
	}

	public void setWatch(Label watch) {
		this.watch = watch;
	}

	public void setLeftScore(Label leftScore) {
		this.leftScore = leftScore;
	}

	public void setRightScore(Label rightScore) {
		this.rightScore = rightScore;
	}

	public void setSettingPane(AnchorPane settingPane) {
		this.settingPane = settingPane;
	}

	public void setGoalField(TextField goalField) {
		this.goalField = goalField;
	}

	public void setSpeedMetter(Slider speedMetter) {
		this.speedMetter = speedMetter;
	}

	public void setCongratulationPane(AnchorPane congratulationPane) {
		this.congratulationPane = congratulationPane;
	}

	public void setCongratulationLabel(Label congratulationLabel) {
		this.congratulationLabel = congratulationLabel;
	}

	public class Shake implements EventHandler<MouseEvent> {

		private Label racket;

		public Shake(Label r) {
			racket = r;
		}

		@Override
		public void handle(MouseEvent me) {
			ownY = me.getY();
			racket.setLayoutY(ownY);
			network.send("MoveTo:" + ownY);
			if (stick) {
				if (player == server) {
					if (turn == server) {
						ball.setLayoutY(ownY + 60);
						network.send("BallXTo:" + ball.getLayoutX());
						network.send("BallYTo:" + (ownY + 60));
					}
				} else if (player == client) {
					if (turn == client) {
						ball.setLayoutY(ownY + 60);
						network.send("BallXTo:" + ball.getLayoutX());
						network.send("BallYTo:" + (ownY + 60));
					}
				}
			}
			if (ownY < 50)
				robot.mouseMove(1290, 50);
			else if (ownY > 518)
				robot.mouseMove(1290, 518);
		}
	}

	public class EscapeHandler implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent ke) {
			KeyCode key = ke.getCode();
			if (key.equals(KeyCode.ESCAPE)) {
				robot.mouseMove(680, 384);
				if (gamePause)
					close();
				else
					showSetting();
			}
		}
	}

	public class Movement extends Thread {

		private int direction;
		private double x, y, dx, dy;
		private int ms;

		public Movement() {
			direction = 3;
			dx = 1;
			dy = 1;
			ms = 5;
		}

		@Override
		public void run() {
			try {
				while (true) {
					move();
					sleep(ms);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public void move() {
			switch (direction) {
			case 1: {
				dx = 1;
				dy = -1;
				break;
			}
			case 2: {
				dx = -1;
				dy = -1;
				break;
			}
			case 3: {
				dx = -1;
				dy = 1;
				break;
			}
			case 4: {
				dx = 1;
				dy = 1;
				break;
			}
			}
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					x = ball.getLayoutX();
					y = ball.getLayoutY();
					rightY = rightRacket.getLayoutY();
					leftY = leftRacket.getLayoutY();
					ball.setLayoutX(x + dx);
					ball.setLayoutY(y + dy);
					network.send("BallXTo:" + (x + dx));
					network.send("BallYTo:" + (y + dy));
				}
			});
			if (y <= 50) {
				if (direction == 1)
					direction = 4;
				else if (direction == 2)
					direction = 3;
			} else if (y >= 638) {
				if (direction == 4)
					direction = 1;
				else if (direction == 3)
					direction = 2;
			} else if (x <= -80) {
				if (direction == 2)
					direction = 1;
				else if (direction == 3)
					direction = 4;
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						ball.setLayoutX(0);
						increaseScore(client);
						network.send("scoreClient");
					}
				});
				movement.stop();
			} else if (x >= 1360) {
				if (direction == 1)
					direction = 2;
				else if (direction == 4)
					direction = 3;
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						ball.setLayoutX(1280);
						increaseScore(server);
						network.send("scoreServer");
					}
				});
				movement.stop();
			} else if (leftHit()) {
				if (direction == 2)
					direction = 1;
				else if (direction == 3)
					direction = 4;
			} else if (rightHit()) {
				if (direction == 1)
					direction = 2;
				else if (direction == 4)
					direction = 3;
			}
		}

		private boolean rightHit() {
			if (x + 80 >= 1300)
				if (y >= rightY + 5 && y <= rightY + 195)
					return true;
			return false;
		}

		private boolean leftHit() {
			if (x <= 60)
				if (y >= leftY + 5 && y <= leftY + 195)
					return true;
			return false;
		}

		public void setSpeed(double value) {
			ms = 11 - (int) value;
		}
	}

	public class Timer extends Thread {

		@Override
		public void run() {
			while (true) {
				increaseTimer();
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void increaseTimer() {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					second++;
					if (second > 9) {
						second = 0;
						bsecond++;
						if (bsecond > 5) {
							bsecond = 0;
							minute++;
							if (minute > 9) {
								minute = 0;
								bminute++;
							}
						}
					}
					watch.setText(bminute + "" + minute + "" + ":" + bsecond + "" + second);
				}
			});
		}
	}
}
