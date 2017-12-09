package wolf.sheep;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * class that establishes the database connection with the MySql server and performs actions on the database
 * @author anitageorge and tanveenkaur
 *
 */
public class DatabaseConnection {
	
	private boolean res;
	private Connection con;
	private Statement st;
	private ResultSet result;
	private User user;
	
	private JFrame frame;
	private JPanel frontPanel;
	
	/**
	 * Constructor to setup the database server GUI
	 */
	public DatabaseConnection() {
		frame = new JFrame("Database Server");
		JLabel label = new JLabel("Database Server is running");
		label.setHorizontalAlignment(JLabel.CENTER);
		frontPanel = new JPanel();
		frontPanel.add(label);
		frame.add(frontPanel);
		frame.setSize(350, 150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * Method to connect to the MySql server
	 * @return
	 */
	public boolean connectToDB() {
		String url = "jdbc:mysql://localhost:3306/Game";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url,"root","root");
			res  = true;
			st = con.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	/**
	 * Method to add a new user to the table
	 * @param user
	 * @return
	 */
	public boolean addNewUser(User user) {
		res = false;
		String query = "INSERT INTO user VALUES (?,?,?,?,?,?,?)";
		int rows = 0;
		try {
			PreparedStatement pst = con.prepareStatement(query);
			pst.setString(1, user.getUsername());
			pst.setString(2, user.getPassword());
			pst.setDate(3, user.getAccountCreationDate());
			pst.setDate(4, user.getLastLogin());
			pst.setInt(5, user.getGamesPlayed());
			pst.setInt(6, user.getWins());
			pst.setInt(7, user.getLoss());
			
			rows = pst.executeUpdate();
			
			if(rows == 1)
				res = true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * Method to validate the user - username and password
	 * @param username
	 * @param password
	 * @return
	 */
	public User validateUser(String username, String password) {
		user = null;
		String query = "SELECT * FROM user where username = '" + username + "'";
		try {
			result = st.executeQuery(query);
			while(result.next()) {
				if(result.getString("password").equals(password)) {
					user = new User(result.getString("username"), result.getString("password"), result.getDate("account_creation_date"), result.getDate("last_login"), result.getInt("num_games_played"), result.getInt("wins"), result.getInt("loss"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return user;
	}
	/**
	 * Method to retrieve the user details
	 * @param username
	 * @return
	 */
	public User getUserDetails(String username) {
		user = null;
		String query = "SELECT * FROM user where username = '" + username + "'";
		try {
			result = st.executeQuery(query);
			while(result.next()) {
				user = new User(result.getString("username"), result.getString("password"), result.getDate("account_creation_date"), result.getDate("last_login"), result.getInt("num_games_played"), result.getInt("wins"), result.getInt("loss"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return user;
	}
	
	/**
	 * Method to update the user details
	 * @param user
	 * @return
	 */
	public boolean updateUserData(User user) {
		res = false;
		String query = "UPDATE user SET last_login = ?, num_games_played = ?, wins = ?, loss = ? where username = ?";
		
		int rows = 0;
		try {
			PreparedStatement pst = con.prepareStatement(query);
			pst.setDate(1, user.getLastLogin());
			pst.setInt(2, user.getGamesPlayed());
			pst.setInt(3, user.getWins());
			pst.setInt(4, user.getLoss());
			pst.setString(5, user.getUsername());
			
			rows = pst.executeUpdate();
			
			if(rows == 1)
				res = true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	/**
	 * Method to start the database server
	 */
	public void startServer() {
		ServerSocket dbServer;
		Socket socket;
		System.out.println("Database server started");
		try {
			dbServer = new ServerSocket(5000, 100);
			while(true) {
				socket = dbServer.accept();
				System.out.println("Received a socket");
				DatabaseServer server = new DatabaseServer(socket);
				server.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to start the class
	 * @param args
	 */
	public static void main(String[] args) {
		DatabaseConnection dbconnection = new DatabaseConnection();
		dbconnection.startServer();
	}
	
	/**
	 * Class to handle the database requests from the clients
	 * @author anitageorge and tanveenkaur
	 *
	 */
	class DatabaseServer extends Thread {
		
		Socket dbclient;
		DataInputStream db_in;
		DataOutputStream db_out;
		User updatedUser;
		/**
		 * Constructor that accepts a socket from the client 
		 * @param socket
		 */
		public DatabaseServer(Socket socket) {
			connectToDB();
			dbclient = socket;
			try {
				System.out.println("received client socket");
				db_in = new DataInputStream(dbclient.getInputStream());
				db_out = new DataOutputStream(dbclient.getOutputStream());
				System.out.println("received client streams");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		/**
		 * method to handle the various requests of the client
		 * @param str
		 * @throws ClassNotFoundException
		 * @throws IOException
		 */
		public void action(String str) throws ClassNotFoundException, IOException {
			String username;
			String password;
			switch(str) {
			case "login":
				username = db_in.readUTF();
				password = db_in.readUTF();
				updatedUser = validateUser(username, password);
				System.out.println("User validated: " + username);
				if(updatedUser!=null) {
					db_out.writeBoolean(true);
				} else {
					db_out.writeBoolean(false);
				}
				break;
			case "register":
				username = db_in.readUTF();
				password = db_in.readUTF();
				updatedUser = new User(username, password);
				res = addNewUser(updatedUser);
				db_out.writeBoolean(res);
				break;
			case "update":
				username = db_in.readUTF();
				updatedUser = getUserDetails(username);
				updatedUser.setLastLogin(Date.valueOf(db_in.readUTF()));
				updatedUser.setGamesPlayed(updatedUser.getGamesPlayed() + db_in.readInt());
				updatedUser.setWins(db_in.readInt());
				updatedUser.setLoss(db_in.readInt());
				res = updateUserData(updatedUser);
				if(res) {
					System.out.println("user details updated: " + username);
				} else {
					System.out.println("not updated");
				}
				break;
			}
		}
		
		/**
		 * Method to get the process the client request
		 */
		@Override
		public void run() {
			String message;
			try {
				message = db_in.readUTF();
				System.out.println("Recieved a connection from client with message: " + message);
				action(message);
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
