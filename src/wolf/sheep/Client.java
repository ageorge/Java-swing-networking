package wolf.sheep;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * @author anitageorge and tanveenkaur
 * Client class that displays the user profile and the game setup
 */
public class Client extends JFrame {
	private User user;
	private String player, username, password;
	private Socket client,socket,db_client;
	private ObjectInputStream input;
	private DataInputStream db_input;
	private ObjectOutputStream output;
	private DataOutputStream db_output;
	private static int port = 7000;
	private static ServerSocket sheep_server;
	
	private JPanel homePanel, loginPanel, registerPanel, firstPanel;
	
	private boolean gameDone, sheepWin, wolfWin;
	
	private final Dimension clientWindowSize = new Dimension(350, 450);
	
	private JButton startGameButton, loginButton, registerButton, loginchoiceButton, registerchoiceButton;
	
	private JLabel statusLabel;
	
	private Sheep sheep;
	private Wolf wolf;
	
	private final String serverAddress = "10.12.54.245";
	
	/**
	 * Constructor to set up the frame with the first Page
	 */
	public Client() {
		user = new User();
		firstPanel = new JPanel();
		homePanel = new JPanel();
		homePanel.setBackground(Color.pink);
		homePanel.setSize(clientWindowSize);
		startGameButton = new JButton("Start Game");
		
		loginPanel = new JPanel();
		loginPanel.setSize(clientWindowSize);
		registerPanel = new JPanel();
		registerPanel.setSize(clientWindowSize);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int height = (int) ((screenSize.height - getHeight()) / 2);
		int width = (int) ((screenSize.width - getWidth()) / 2);
		setLocation(width, height);
		
		JLabel label = new JLabel("Welcome to the Gaming World!");
		label.setSize(250, 100);
		label.setHorizontalAlignment(JLabel.CENTER);
		setUpLoginPage();
		setRegistrationPage();
		loginchoiceButton = new JButton("Already a user, Sign In!");
		loginchoiceButton.setSize(250, 100);
		loginchoiceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setContentPane(loginPanel);
			}
		});
		
		registerchoiceButton = new JButton("New User, Register Here!");
		registerchoiceButton.setSize(250, 100);
		registerchoiceButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setContentPane(registerPanel);
			}
		});
		statusLabel = new JLabel("");
		statusLabel.setSize(300,50);
		
		firstPanel.add(label);
		firstPanel.add(loginchoiceButton);
		firstPanel.add(registerchoiceButton);
		add(firstPanel, BorderLayout.CENTER);
		add(statusLabel, BorderLayout.SOUTH);
		
		
		setTitle("Game World");
		
		pack();
		setResizable(false);
		
		setSize(clientWindowSize);
		setPreferredSize(clientWindowSize);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);	
		connectToDBServer();
	}
	
	/**
	 * Method to set up the login Page
	 */
	public void setUpLoginPage() {
		
		JLabel usernameLabel = new JLabel("Username");
		JLabel passwordLabel = new JLabel("Password");
		JTextField userfield = new JTextField(20);
		JPasswordField passwordField = new JPasswordField(20);
		loginButton = new JButton("Login");
		
		loginPanel.add(usernameLabel);
		loginPanel.add(userfield);
		loginPanel.add(passwordLabel);
		loginPanel.add(passwordField);
		loginPanel.add(loginButton);
		
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				username = userfield.getText();
				password = String.valueOf(passwordField.getPassword());
				if(username.equals("") || password.equals("")) {
					JOptionPane.showMessageDialog(null, "Username or password cannot be empty", "Login Error", JOptionPane.ERROR_MESSAGE);
				} else {
					
					try {
						if(db_client.isClosed()) {
							connectToDBServer();
						}
						db_output.writeUTF("login");
						db_output.writeUTF(username);
						db_output.writeUTF(password);
						boolean res = db_input.readBoolean();
						if(!res) {
							System.out.println("Invalid user");
							JOptionPane.showMessageDialog(loginPanel, "Invalid username/Password", "Invalid User", JOptionPane.ERROR_MESSAGE);
						} else {
							setTitle("Connecting to the Game....");
							user.setUsername(username);
							setUpHomePage();
							setContentPane(homePanel);
							user.setPassword(password);
							long date = System.currentTimeMillis();
							Date lastlogin = new Date(date);
							user.setLastLogin(lastlogin);
							System.out.println("user is validated last login: " + lastlogin);
							runClient();
						}
						db_client.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					} 
				}
			}
		});
	}
	
	/**
	 * Method to set up the registration page
	 */
	public void setRegistrationPage() {
		JLabel usernameLabel = new JLabel("Username");
		JLabel passwordLabel = new JLabel("Password");
		JTextField userfield = new JTextField(20);
		JPasswordField passwordField = new JPasswordField(20);
		registerButton = new JButton("Register");
		
		registerPanel.add(usernameLabel);
		registerPanel.add(userfield);
		registerPanel.add(passwordLabel);
		registerPanel.add(passwordField);
		registerPanel.add(registerButton);
		
		registerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				username = userfield.getText();
				password = String.valueOf(passwordField.getPassword());
				if(username.equals("") || password.equals("")) {
					JOptionPane.showMessageDialog(null, "Username or password cannot be empty", "Registration Error", JOptionPane.ERROR_MESSAGE);
				} else {
					
					try {
						if(db_client.isClosed()) {
							connectToDBServer();
						}
						db_output.writeUTF("register");
						db_output.writeUTF(username);
						db_output.writeUTF(password);
						boolean res = db_input.readBoolean();
						if(!res) {
							System.out.println("Invalid username, Try again");
							JOptionPane.showMessageDialog(loginPanel, "Invalid username, Try again", "Registration Error", JOptionPane.ERROR_MESSAGE);
						} else {
							user.setUsername(username);
							setUpHomePage();
							user.setPassword(password);
							long date = System.currentTimeMillis();
							Date lastlogin = new Date(date);
							user.setLastLogin(lastlogin);
							System.out.println("user is validated last login: " + lastlogin);
							setContentPane(homePanel);
							JOptionPane.showMessageDialog(null,"You are now registered with Game World!", "Registration Success", JOptionPane.INFORMATION_MESSAGE);
							runClient();
						}
						db_client.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					} 
				}
			}
		});
	}
	
	/**
	 * Method to set up the Home Page
	 */
	public void setUpHomePage() {
		statusLabel.setText("Game is starting. Please wait....");
		startGameButton.setEnabled(false);
		JLabel userLabel = new JLabel("Welcome! "+user.getUsername());
		homePanel.setLayout(new BorderLayout(5, 5));
		homePanel.add(statusLabel, BorderLayout.SOUTH);
		JPanel topPanel = new JPanel();
		topPanel.setBackground(Color.pink);
		topPanel.add(userLabel);
		
		JButton logout = new JButton("Logout");
		topPanel.add(logout);
		logout.addActionListener(new  ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setTitle("Game World");
				setContentPane(firstPanel);
			}
		});
		homePanel.add(topPanel, BorderLayout.NORTH);
		if(user.getUsername().equals("")) {
			userLabel.setText("Welcome Guest!");
		} else {
			userLabel.setText("Welcome " + user.getUsername());
		}
		statusLabel.setSize(200,50);
	}
	/**
	 * Method to connect to the Home Page
	 */
	public void runClient() {
		try {
			user.setGamesPlayed(1);
			statusLabel.setText("Connecting to the game. Please wait......");
			client = new Socket(serverAddress,6000);
			input = new ObjectInputStream(client.getInputStream());
			output = new ObjectOutputStream(client.getOutputStream());
			setTitle("Connected: "+ client.getLocalAddress() + ":" + client.getLocalPort());
			player = (String) input.readObject();
			setTitle("Connected As: "+ player);
			if(player.equals(Game.SHEEP)) {
				startSheepMode();
			} else {
				startWolfMode();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Method to play the game as a sheep player
	 */
	public void startSheepMode() {
		try {
			if(sheep_server == null)
				sheep_server = new ServerSocket(port, 100);
			System.out.println("Sheep Mode:");
			socket = sheep_server.accept();
			sheep = new Sheep(socket);
			sheep.start();
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
	/**
	 * Method to play the game as a wolf player
	 */
	public void startWolfMode() {
		System.out.println("Wolf Mode:");
		try {
			if(player.equals(Game.WOLF)) {
				InetAddress address = (InetAddress) input.readObject();
				wolf = new Wolf(address, port);
				client.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
	/**
	 * Method to update the user details after winning
	 */
	public void updateUser() {
		try {
			connectToDBServer();
			db_output.writeUTF("update");
			db_output.writeUTF(username);
			db_output.writeUTF(String.valueOf(user.getLastLogin()));
			db_output.writeInt(1);
			db_output.writeInt(user.getWins());
			db_output.writeInt(user.getLoss()); 
			db_client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * method to connect to the database server
	 */
	public void connectToDBServer() {
		try {
			db_client = new Socket(serverAddress,5000);
			System.out.println("connected to db server");
			db_input = new DataInputStream(db_client.getInputStream());
			db_output = new DataOutputStream(db_client.getOutputStream());
			System.out.println("streams received");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Main Method to start the client
	 * @param args
	 */
	public static void main(String[] args) {
		new Client();
	}
	
	/**
	 * 
	 * @author anitageorge and tanveenkaur
	 * Inner class to handle sheep movements
	 *
	 */
	class Sheep extends Thread {
		
		Socket sheep_client;
		DataInputStream sheep_in;
		DataOutputStream sheep_out;
		Game sheep_mode;
		
		/**
		 * Constructor that accepts the connection from wolf client and writes a start to the wolf client
		 * @param s
		 */
		public Sheep(Socket s) {
			sheep_client = s;
			try {
				System.out.println("Sheep Mode: Wolf client received");
				sheep_in = new DataInputStream(sheep_client.getInputStream());
				sheep_out = new DataOutputStream(sheep_client.getOutputStream());
				System.out.println("Sheep Mode: Streams recieved");
				sheep_out.writeUTF("start");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Run method to setup the Game in sheep mode
		 * 1. makes a move
		 * 2. checks if game is won
		 * 3. waits for the move from wolf
		 * 4. checks if game is won
		 */
		@Override
		public void run() {
			System.out.println("Sheep moves first");
			String move, lastpos = "";
			sheep_mode = new Game(Game.SHEEP);
			System.out.println("sheep_mode = " + sheep_mode);
			setContentPane(sheep_mode);
			setTitle("Player: " + player);
			try {
				gameDone = false;
				while(!gameDone) {
					System.out.println("Waiting for sheep move");
					move = sheep_mode.getCurrentMove();
					while(move == null || move.equals(lastpos)) {
						move = sheep_mode.getCurrentMove();
					}
					lastpos = move;
					sheep_out.writeUTF(move);
					gameDone = sheep_mode.isGameOver();
					
					if(!gameDone) {
						move = sheep_in.readUTF();
						sheep_mode.makeOpponentMove(move);
						gameDone = sheep_mode.isGameOver();
					} 
				}
				sheepWin = sheep_mode.getSheepWin();
				wolfWin = sheep_mode.getWolfWin();
			} catch (EOFException e) {
				JOptionPane.showMessageDialog(null, "Opponent has quit the game!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
				setContentPane(homePanel);
			} catch (SocketException e) {
				JOptionPane.showMessageDialog(null, "Opponent has quit the game!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
				setContentPane(homePanel);
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
			if(sheepWin) {
				user.setWins(1);
				JOptionPane.showMessageDialog(null, "Congratulations you have Won the Game!", "Game Won", JOptionPane.INFORMATION_MESSAGE);
			} else if(wolfWin) {
				user.setLoss(1);
				JOptionPane.showMessageDialog(null, "Sorry, you lost the game!", "Game Lost", JOptionPane.INFORMATION_MESSAGE);
			} 
			System.out.println("Updating user information");
			updateUser();
			startGameButton.setEnabled(true);
			statusLabel.setVisible(false);
			setContentPane(homePanel);
		}
	}
	
	/**
	 * 
	 * @author anitageorge and tanveenkaur
	 * Inner class to handle wolf movements
	 */
	class Wolf extends Thread {
		
		Socket wolf_client;
		DataInputStream wolf_in;
		DataOutputStream wolf_out;
		Game wolf_mode;
		
		/**
		 * Constructor that sends a request to the sheep server to play the game
		 * @param s
		 */
		public Wolf(InetAddress address, int portnumber) {
			try {
				wolf_client = new Socket(address.getHostAddress(), portnumber);
				System.out.println("Wolf mode: sheep connection sucessful with host address: " + address.getHostAddress());
				wolf_in = new DataInputStream(wolf_client.getInputStream());
				wolf_out = new DataOutputStream(wolf_client.getOutputStream());
				String message = wolf_in.readUTF();
				if(message.equals("start")) {
					start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Run method to set the game in wolf mode
		 * 1. waits for sheep movement
		 * 2. checks if game is won
		 * 3. makes a move
		 * 4. checks if game is won
		 */
		@Override
		public void run() {
			System.out.println("starting Game....");
			wolf_mode = new Game(Game.WOLF);
			System.out.println("wolf mode: " + wolf_mode);
			setContentPane(wolf_mode);
			setTitle("Player: "+player);
			String move, lastpos = "";
			try {
				gameDone = false;
				while(!gameDone) {
					move = wolf_in.readUTF();
					wolf_mode.makeOpponentMove(move);

					gameDone = wolf_mode.isGameOver();
					if(gameDone) {
						sheepWin = wolf_mode.getSheepWin();
						wolfWin = wolf_mode.getWolfWin();
						System.out.println("Game Over");
						break;
					}
					
					move = wolf_mode.getCurrentMove();
					while(move == null || move.equals(lastpos)) {
						sleep(1000);
						move = wolf_mode.getCurrentMove();
					}
					lastpos = move;
					wolf_out.writeUTF(move);
					gameDone = wolf_mode.isGameOver();
				}
				sheepWin = wolf_mode.getSheepWin();
				wolfWin = wolf_mode.getWolfWin();
			} catch (EOFException e) {
				JOptionPane.showMessageDialog(null, "Opponent has quit the game!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
				setContentPane(homePanel);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			if(sheepWin) {
				user.setWins(1); 
				JOptionPane.showMessageDialog(null, "Sorry, you lost the game!", "Game Lost", JOptionPane.INFORMATION_MESSAGE);
			} else if(wolfWin) {
				user.setLoss(1);
				JOptionPane.showMessageDialog(null, "Congratulations you have Won the Game!", "Game Won", JOptionPane.INFORMATION_MESSAGE);
			} 
			System.out.println("Updating user information");
			updateUser();
			startGameButton.setEnabled(true);
			statusLabel.setVisible(false);
			setContentPane(homePanel);
		}
	}
}
