package wolf.sheep;

import java.io.Serializable;
import java.sql.Date;
/**
 * class to handle the user details
 * @author anitageorge and tanveenkaur
 *
 */
public class User implements Serializable {
	String username;
	String password;
	Date accountCreationDate;
	Date lastLogin;
	int gamesPlayed;
	int wins;
	int loss;
	
	/**
	 * Default constructor
	 */
	public User() {
		username = "";
		password = "";
		accountCreationDate = new Date(System.currentTimeMillis());
		lastLogin = accountCreationDate;
		gamesPlayed = 0;
		wins = 0;
		loss = 0;
	}
	
	/**
	 * Constructor for New Users
	 * @param username
	 * @param password
	 */
	public User(String username, String password) {
		this.username = username;
		this.password = password;
		accountCreationDate = new Date(System.currentTimeMillis());
		lastLogin = accountCreationDate;
		gamesPlayed = 0;
		wins = 0;
		loss = 0;
	}
	
	/**
	 * Parameterized Constructor
	 * @param username
	 * @param password
	 * @param accountCreationDate
	 * @param lastLogin
	 * @param wins
	 * @param loss
	 */
	public User(String username, String password, Date accountCreationDate, Date lastLogin, int gamesPlayed, int wins, int loss) {
		this.username = username;
		this.password = password;
		this.accountCreationDate = accountCreationDate;
		this.lastLogin = lastLogin;
		this.gamesPlayed = gamesPlayed;
		this.wins = wins;
		this.loss = loss;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the accountCreationDate
	 */
	public Date getAccountCreationDate() {
		return accountCreationDate;
	}
	/**
	 * @param accountCreationDate the accountCreationDate to set
	 */
	public void setAccountCreationDate(Date accountCreationDate) {
		this.accountCreationDate = accountCreationDate;
	}
	/**
	 * @return the lastLogin
	 */
	public Date getLastLogin() {
		return lastLogin;
	}
	/**
	 * @param lastLogin the lastLogin to set
	 */
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
	/**
	 * @return the gamesPlayed
	 */
	public int getGamesPlayed() {
		return gamesPlayed;
	}

	/**
	 * @param gamesPlayed the gamesPlayed to set
	 */
	public void setGamesPlayed(int gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}

	/**
	 * @return the wins
	 */
	public int getWins() {
		return wins;
	}
	/**
	 * @param wins the wins to set
	 */
	public void setWins(int wins) {
		this.wins = wins;
	}
	/**
	 * @return the loss
	 */
	public int getLoss() {
		return loss;
	}
	/**
	 * @param loss the loss to set
	 */
	public void setLoss(int loss) {
		this.loss = loss;
	}
	
	
	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", accountCreationDate=" + accountCreationDate
				+ ", lastLogin=" + lastLogin + ", gamesPlayed=" + gamesPlayed + ", wins=" + wins + ", loss=" + loss
				+ "]";
	}
}
