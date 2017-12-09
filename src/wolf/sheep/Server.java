package wolf.sheep;


import java.awt.FlowLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JLabel;


/**
 * @author anitageorge and tanveenkaur
 * Server class to start the game server
 */
public class Server{
	
	/**
	 * Constructor to set up the GUI for the server
	 */
	public Server() {
		System.out.println(" Game Server started.");
		JFrame frame = new JFrame("Game Server");
		frame.setLayout(new FlowLayout(FlowLayout.CENTER));
		JLabel label = new JLabel("Game Server is running");
		label.setSize(300, 50);
		frame.add(label);
		frame.setSize(350, 150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	/**
	 * Method to run the server 
	 * Server does a double accept
	 * assigns both incoming client requests to a disposable server. 
	 */
	public void runGameServer() {
		ServerSocket gameServer;
		Socket client, client2;
		try {
			gameServer = new ServerSocket(6000, 100);
			while(true) {
				client = gameServer.accept();
				client2 = gameServer.accept();
				DisposableServer disposableServer = new DisposableServer(client, client2);
				disposableServer.start();
			}
		} catch (IOException e) {
		}
	}
	/**
	 * Main method to start the server
	 * @param args
	 */
	public static void main(String[] args) {
		Server server = new Server();
		server.runGameServer();
	}
	
	/**
	 * Inner class to handle sheep and wolf by thread
	 * @author anitageorge and tanveenkaur
	 *
	 */
	class DisposableServer extends Thread {
		
		ObjectOutputStream sheep_out, wolf_out;
		ObjectInputStream sheep_in, wolf_in;
		InetAddress sheep_address;
		int sheepPort;
		Socket sheep, wolf;
		String username, password;
		User sheep_user, wolf_user;
		
		/**
		 * Constructor to store and establish an input and output stream with both the clients
		 * @param s
		 * @param w
		 */
		public DisposableServer(Socket s, Socket w) {
			sheep = s;
			wolf = w;
			try {
				sheep_out = new ObjectOutputStream(sheep.getOutputStream());
				wolf_out = new ObjectOutputStream(wolf.getOutputStream());
				sheep_in = new ObjectInputStream(sheep.getInputStream());
				wolf_in = new ObjectInputStream(wolf.getInputStream());
				sheep_address = sheep.getInetAddress();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Method to send each client their status
		 */
		@Override
		public void run() {
			try {
				sheep_out.writeObject(Game.SHEEP);
				wolf_out.writeObject(Game.WOLF);
				wolf_out.writeObject(sheep_address);
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
}
