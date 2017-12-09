package wolf.sheep;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * class that sets up the game in its mode - sheep or wolf
 * @author anitageorge and tanveenkaur
 *
 */
public class Game extends JPanel implements Runnable {
	
	public static final String SHEEP = "Sheep";
	public static final String WOLF = "Wolf";
	public static final int INITIAL_SHEEP_X = 7;
	public static final int INITIAL_SHEEP_Y = 1;
	public static final int INITIAL_WOLF_X = 0;
	public static final int INITIAL_WOLF_1_Y = 0;
	public static final int INITIAL_WOLF_2_Y = 2;
	public static final int INITIAL_WOLF_3_Y = 4;
	public static final int INITIAL_WOLF_4_Y = 6;
	
	public static int turns;
	
	private JPanel boardPanel, cellPanel, statusPanel, panelClicked, panelReleased, currentPanel;
	private JPanel cells[][];
	private GridLayout layout;
	private Color black, orange;
	
	private JLabel timerLabel; 
	
	private List<JPanel> legalPanels, blockedPanels;
	
	private Circle sheep;
	private ArrayList<Circle> wolves;
	
	private String currentpos;
	
	private Timer timer;
	private long sec, min, hr;
	
	private String player;
	
	private JLabel playerStatusLabel;
	
	private static boolean gameOver, sheepWin, wolfWin;
	
	/**
	 * Constructor the set up the GUI of the Game
	 * @param playerStatus
	 */
	public Game(String playerStatus) {
		
		black = Color.BLACK;
		orange = new Color(255, 128, 0);
		
		timerLabel = new JLabel();
		timerLabel.setHorizontalAlignment(JLabel.RIGHT);
		
		cells = new JPanel[8][8];
		
		layout = new GridLayout(8, 8);
		boardPanel = new JPanel(layout);
		boardPanel.setBounds(20, 20, 164, 164);
		boardPanel.setName("board");
		int index = 0;
		for(int i = 0;i < 8; i++) {
			for(int j = 0;j < 8; j++) {
				cellPanel = new JPanel();
				cellPanel.setName(i+" "+j);
				cellPanel.setOpaque(true);
				cellPanel.setPreferredSize(new Dimension(40, 40));
				cellPanel.setMinimumSize(cellPanel.getPreferredSize());
				
				if((i % 2 == 0 && index % 2 == 0) || (i % 2 != 0 && index % 2 != 0)) {
					cellPanel.setBackground(black);
				} else if((j % 2 == 0 && index % 2 == 0) || (j % 2 != 0 && index % 2 != 0)) {
					cellPanel.setBackground(orange);
				}
				index++;
				cells[i][j] = cellPanel;
				boardPanel.add(cellPanel);
			}
		}
		
		addListenerstoPanels();
		
		sheep = new Circle(Color.YELLOW);
		wolves = new ArrayList<>();
		for(int i = 0; i < 4; i++)
			wolves.add(new Circle(Color.GREEN));
		
		cells[INITIAL_SHEEP_X][INITIAL_SHEEP_Y].add(sheep);
		cells[INITIAL_WOLF_X][INITIAL_WOLF_1_Y].add(wolves.get(0));
		cells[INITIAL_WOLF_X][INITIAL_WOLF_2_Y].add(wolves.get(1));
		cells[INITIAL_WOLF_X][INITIAL_WOLF_3_Y].add(wolves.get(2));
		cells[INITIAL_WOLF_X][INITIAL_WOLF_4_Y].add(wolves.get(3));
		blockedPanels = new ArrayList<>();
		setBlockedPanels();
		
		legalPanels = new ArrayList<>();
		
		Thread timerThread = new Thread(this);
		timerThread.setName("timerThread");
		timerThread.start();
		
		statusPanel = new JPanel();
		player = playerStatus;
		String iconColor = player.equals(SHEEP) ? "Yellow" : "Green";
		JLabel playerLabel = new JLabel("Your Icon Color: " + iconColor);
		playerLabel.setSize(300,20);
		playerStatusLabel = new JLabel("First Turn : Sheep");
		playerStatusLabel.setSize(350,20);
		statusPanel.add(playerStatusLabel);
		statusPanel.add(playerLabel);
		currentpos = "";
		add(timerLabel, BorderLayout.NORTH);
		add(boardPanel, BorderLayout.CENTER);
		add(statusPanel, BorderLayout.SOUTH);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int height = (int) ((screenSize.height - getHeight()) / 2);
		int width = (int) ((screenSize.width - getWidth()) / 2);
		setLocation(width, height);
		setPreferredSize(new Dimension(450, 250));
		setVisible(true);
	}
	
	/**
	 * Method to display whose turn is next
	 */
	public void setStatusLabel() {
		if(turns % 2 == 0) {
			playerStatusLabel.setText("Current turn: Sheep");
		} else {
			playerStatusLabel.setText("Current turn: Wolf");
		}
	}
	
	/**
	 * Method to compute all the legal moves that can be made by the player
	 * @param i
	 * @param j
	 */
	public void computeLegalMoves(int i, int j) {
		setBlockedPanels();
		legalPanels = new ArrayList<JPanel>();
		JPanel p1,p2,p3,p4;
		if(player.equals(SHEEP) && SHEEP.equals(currentPanel.getName())) {
			p1 = i < 7 ? (j > 0 ? cells[i+1][j-1] : null) : null;
			p2 = i < 7 ? (j < 7 ? cells[i+1][j+1] : null) : null;
			p3 = i > 0 ? (j > 0 ? cells[i-1][j-1] : null) : null;
			p4 = i > 0 ? (j < 7 ? cells[i-1][j+1] : null) : null;
			legalPanels.add(p1);
			legalPanels.add(p2);
			legalPanels.add(p3);
			legalPanels.add(p4);
		} else if(player.equals(WOLF) && WOLF.equals(currentPanel.getName())) {
			p1 = i < 7 ? (j > 0 ? cells[i+1][j-1] : null) : null;
			p2 = i < 7 ? (j < 7 ? cells[i+1][j+1] : null) : null;
			legalPanels.add(p1);
			legalPanels.add(p2);
		}
		
		legalPanels.removeAll(blockedPanels);
	}
	
	/**
	 * Method to find the panels that cannot be moved in
	 */
	public void setBlockedPanels() {
		blockedPanels = new ArrayList<JPanel>();
		JPanel p1,p;
		p1 = (JPanel) sheep.getParent();
		blockedPanels.add(p1);
		for(JPanel wolf: wolves) {
			p = (JPanel) wolf.getParent();
			blockedPanels.add(p);
		}
	}
	
	/**
	 * Method to check if the game is completed
	 * @return
	 */
	public boolean isGameOver() {
		checkGameWin();
		gameOver = sheepWin || wolfWin;
		
		return gameOver;
	}
	
	/**
	 * Getter method to return the sheep win status
	 * @return
	 */
	public boolean getSheepWin() {
		return sheepWin;
	}
	/**
	 * Getter method to return the wolf win status
	 * @return
	 */
	public boolean getWolfWin() {
		return wolfWin;
	}
	
	/**
	 * Method to check if the game is won by sheep or wolf
	 */
	public void checkGameWin() {
		JPanel sheepPanel = (JPanel) sheep.getParent();
		Point sheepPos = findPos(sheepPanel);
		
		//checking if the sheep is cornered by wolves from all sides
		if(isSheepBlocked(sheepPos)) {
			sheepWin = false;
			wolfWin = true;
			return;
		}
		
		//worst case sheep reaches the opposite side of the board - then sheep wins
		if(sheepPos.x == INITIAL_WOLF_X) {
			sheepWin = true;
			wolfWin = false;
			return;
		}
		
		//checking if sheep has passed the wolf
		if(checkSheepWon(sheepPos)) {
			sheepWin = true;
			wolfWin = false;
			return;
		}  
	
	}
	
	/**
	 * Method to check if the sheep has won
	 * @param p
	 * @return
	 */
	public boolean checkSheepWon(Point p) {
		//if the index of the sheep has passed the other wolves then the sheep wins
		List<JPanel> remainingwolves = new ArrayList<>();
		boolean check = false;
		for(JPanel wolf: wolves) {
			JPanel parent = (JPanel) wolf.getParent();
			Point pos = findPos(parent);
			if((p.x+1) <= pos.x ) {
				check = true;
				if(pos.x < p.x) {
					if(pos.y == (p.y+1) || pos.y == (p.y-1)) {
						remainingwolves.add(parent);
					}
				} 
			}
		}
		
		if(remainingwolves.size() == 0 && check) {
			return true;
		}
		
		return false;
		
	}
	
	/**
	 * Method to check if the wolf has won 
	 * @param p
	 * @return
	 */
	public boolean isSheepBlocked(Point p) {
		boolean res = true;
		List<JPanel> panels = computeLegalMovesforsheep(p.x, p.y);
		
		//if sheep is still able to move then game continues
		for(JPanel pa: panels) {
			if(pa != null) {
				res = false;
				break;
			}
		}
		
		return res;
	}
	
	/**
	 * Method to find out the legal moves a sheep has made
	 * @param i
	 * @param j
	 * @return
	 */
	public List<JPanel> computeLegalMovesforsheep(int i, int j) {
		setBlockedPanels();
		List<JPanel> availablePanels = new ArrayList<JPanel>();
		JPanel p1,p2,p3,p4;
		p1 = i < 7 ? (j > 0 ? cells[i+1][j-1] : null) : null;
		p2 = i < 7 ? (j < 7 ? cells[i+1][j+1] : null) : null;
		p3 = i > 0 ? (j > 0 ? cells[i-1][j-1] : null) : null;
		p4 = i > 0 ? (j < 7 ? cells[i-1][j+1] : null) : null;
		availablePanels.add(p1);
		availablePanels.add(p2);
		availablePanels.add(p3);
		availablePanels.add(p4);
		
		availablePanels.removeAll(blockedPanels);
		
		return availablePanels;
	}
	
	/**
	 * Method to get the currentMove made by the player
	 * @return
	 */
	public synchronized String getCurrentMove() {
		while(currentpos == null || currentpos.equals("")) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return currentpos;
	}
	
	/**
	 * Method to find out which panel has been clicked by the user
	 */
	public void findClickedPanel() {
		Component components[];
		if(panelClicked.getComponentCount() > 0) {
			components = panelClicked.getComponents();
			for(Component p: components) {
				JPanel panel = (JPanel) p;
				if(panel.getName().equals(Game.SHEEP)) {
					currentPanel = sheep;
					break;
				} else {
					if(wolves.contains(panel)) {
						currentPanel = wolves.get(wolves.indexOf(panel));
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Method to add mouse listerners to the cell panels
	 */
	public void addListenerstoPanels() {
		
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				cells[i][j].addMouseListener(new MouseAdapter() {
					
					@Override
					public void mousePressed(MouseEvent e) {
						panelClicked = (JPanel)e.getComponent();
						findClickedPanel();
						if(currentPanel != null && !player.equals(currentPanel.getName())) {
							JOptionPane.showMessageDialog(null, "Current Object clicked is not " + player, "Invalid Object Clicked", JOptionPane.ERROR_MESSAGE);
						}
						Point pos = findPos(panelClicked);
						computeLegalMoves(pos.x, pos.y);
					}
					
					@Override
					public void mouseReleased(MouseEvent e) {
						
						if(panelReleased.getBackground() == orange) {
							JOptionPane.showMessageDialog(null, player + " can only move diagonally", "Invalid Move", JOptionPane.ERROR_MESSAGE);
						} else {
							boolean flag = (turns % 2 == 0);
							if((flag && player.equals(SHEEP))||(!flag && player.equals(WOLF))) {
								move(panelReleased);
							} else {
								JOptionPane.showMessageDialog(null, "This is not your turn. Please wait.", "Invalid Turn", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
					
					@Override
					public void mouseEntered(MouseEvent e) {
						panelReleased = (JPanel) e.getComponent();
					}
					
				});
			}
		}
	}
	
	/**
	 * Method to make the move from one cell to another
	 * @param panel
	 */
	public void move(JPanel panel) {
		if(legalPanels.contains(panelReleased)) {
			panelReleased.setBackground(Color.blue);
		}
		else {
			JOptionPane.showMessageDialog(null, "This is an Illegal move for a " + player, "Illegal Move", JOptionPane.ERROR_MESSAGE);
		}
		
		if(SHEEP.equals(currentPanel.getName()) || WOLF.equals(currentPanel.getName())) {
			if(panelReleased.getBackground() == Color.blue) {
				panelClicked.remove(currentPanel);
				repaint();
				panelReleased.setBackground(black);
				panelReleased.add(currentPanel);
				Point to_Point = findPos(panelReleased);
				Point from_Point = findPos(panelClicked);
				currentpos = from_Point.x + " " + from_Point.y + " " + to_Point.x + " " + to_Point.y;
				System.out.println("current pos =" + currentpos);
				turns++;
				setStatusLabel();
			}
		}
	}
	
	/**
	 * Method to update the opponents move on the current players screen
	 * @param str
	 * @return
	 */
	public boolean makeOpponentMove(String str) {
		String s[] = str.split(" ");
		int x, y, a, b;
		x = Integer.parseInt(s[0]);
		y = Integer.parseInt(s[1]);
		a = Integer.parseInt(s[2]);
		b = Integer.parseInt(s[3]);
		if(cells[x][y].getComponentCount() > 0) {
			JPanel fromPanel = (JPanel) cells[x][y].getComponent(0);
			JPanel toPanel = cells[a][b];
			if(fromPanel != null) {
				toPanel.add(fromPanel);
				cells[x][y].remove(fromPanel);
				turns++;
				setStatusLabel();
				repaint();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Method to find the position of the panel on the board
	 * @param p
	 * @return
	 */
	public Point findPos(JPanel p) {
		Point point = null;
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(cells[i][j] == p) {
					point = new Point(i, j);
					break;
				}
			}
		}
		return point;
	}

	/**
	 * Method to handle the timer on the screen
	 */
	@Override
	public void run() {
		timer = new Timer(0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String hour = "", minute = "", seconds = "";
				if(min == 59 && sec == 59) {
					hr++;
					min = 0;
					sec = 0;
					sec++;
				} else if(sec == 59) {
					min++;
					sec = 0;
					sec++;
				} else {
					sec++;
				}
				hour = hr < 10 ? ("0"+hr) : (""+hr);
				minute = min < 10 ? ("0"+min) : (""+min);
				seconds = sec < 10 ? ("0"+sec) : (""+sec);
				timerLabel.setText("Time Elapsed: "+hour+":"+minute+":"+seconds);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		timer.start();
	}
	
	/**
	 * Class to generate the moving objects on the board 
	 * @author anitageorge and tanveenkaur
	 *
	 */
	class Circle extends JPanel {
		
		/**
		 * Constructor to initialize the background color of the moving objects
		 * @param color
		 */
		public Circle(Color color) {
			setOpaque(false);
			setBackground(color);
			if(color == Color.GREEN) {
				setName(Game.WOLF);
			}
			else if(color == Color.YELLOW) {
				setName(Game.SHEEP);
			}
		}
		
		/**
		 * Method to get the preferred size of the panel in which to place the object
		 */
		@Override
		public Dimension getPreferredSize() {
			return cells[0][0].getPreferredSize();
		}
		
		/**
		 * Method to draw the circle object 
		 */
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
	        Graphics2D g2d = (Graphics2D) g.create();
	        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        int dim = Math.max(getWidth() - 7, getHeight() - 7);
	        g2d.setColor(getBackground());
	        g2d.fillOval(3, 0, dim, dim);
	        g2d.dispose();
		}
	}
}
