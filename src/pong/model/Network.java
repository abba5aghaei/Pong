package pong.model;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javafx.application.Platform;
import javafx.stage.Stage;
import pong.Pong;

public class Network {

	private ServerSocket serverSocket;
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	private Pong pong;
	private Stage stage;
	private String ip;
	private int port;

	public Network() {
		ip = "localhost";
		port = 5000;
	}

	public void createServer() {
		Thread server = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					serverSocket = new ServerSocket(port);
					socket = serverSocket.accept();
					go();
					in = new DataInputStream(socket.getInputStream());
					out = new DataOutputStream(socket.getOutputStream());
					listen();
				} catch (IOException e) {
					System.out.println("Error: " + e.getMessage());
				}
			}
		});
		server.start();
	}

	public void createClient() {
		Thread client = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					socket = new Socket(ip, port);
					go();
					in = new DataInputStream(socket.getInputStream());
					out = new DataOutputStream(socket.getOutputStream());
					listen();
				} catch (IOException e) {
					System.out.println("Error: " + e.getMessage());
				}
			}
		});
		client.start();
	}

	public void send(String msg) {
		try {
			out.writeUTF(msg);
			System.out.println("Packet Sended: " + msg);
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private void listen() {
		String packet;
		try {
			while (socket != null) {
				packet = in.readUTF();
				System.out.println("Packet Recieved: " + packet);
				pong.process(packet);
			}
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	protected void go() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				pong.showGame();
				stage.close();
			}
		});
	}

	public void setPong(Pong p) {
		pong = p;
	}

	public void setStage(Stage s) {
		stage = s;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public ServerSocket getServerSocket() {
		return serverSocket;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
}
