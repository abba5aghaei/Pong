/**INOG
 * @author abba5aghaei
 * Created on 11/09/1396
 */

package pong;

import javafx.application.Application;
import javafx.stage.Stage;
import pong.model.Network;
import pong.view.StartDialog;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;

public class Main extends Application {

	private Network network;
	private Pong pong;

	@Override
	public void start(Stage primaryStage) {
		network = new Network();
		pong = new Pong();
		pong.setNetwork(network);
		network.setPong(pong);
		network.setStage(primaryStage);
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/Dialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			Scene scene = new Scene(page);
			primaryStage.setScene(scene);
			StartDialog dialog = loader.getController();
			dialog.setPong(pong);
			dialog.setStage(primaryStage);
			dialog.setNetwork(network);
			primaryStage.setResizable(false);
			primaryStage.setTitle("پینگ ینگ");
			primaryStage.getIcons().addAll(new Image(Pong.class.getResourceAsStream("view/icon.png")));
			primaryStage.show();
		} catch (Exception e) {
			System.out.println("Error in main: "+e.getMessage());
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
