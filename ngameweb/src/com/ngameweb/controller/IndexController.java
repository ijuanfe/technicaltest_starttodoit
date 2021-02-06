package com.ngameweb.controller;

import java.util.ArrayList;
import java.util.Random;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;

import com.ngameweb.model.Spot;

public class IndexController extends BaseController {
	
	private static final long serialVersionUID = 1L;
	public int[] rowCounter;
	public int[] colCounter;
	private int diagPos;
	private int diagNeg;
	private String[][] internalGameboard;
	private int n;
	private int currentPlayer;
	private ArrayList<Spot> freeSpots;
	private ArrayList<Spot> freeSpotsCopy;
	EventListener<Event> btnListeners;
	Rows rows;
	Row row;
	private Grid grdGameBoard;
	private Button[][] btnGameBoard;
	private Label lblGameStatus;
	private int emptySpots;
	private Label lblInputNconfig;
	private Textbox txbInputNconfig;
	private Button btnEnterNConfig;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception { // This method is called when the page is rendered
		super.doAfterCompose(comp);
		initialState();
	}
	
	private void initialState() {
		
		// Important: this event listener should have its own separate class not an anonymous class (temporal implementation)
		EventListener<Event> nConfigListener = new EventListener<Event>() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				
				try {
					// 0. Configuring game board dimensions
					int userInput = Integer.parseInt(txbInputNconfig.getText());
					n = userInput;
					rowCounter = new int [n];
					colCounter = new int [n];
					diagPos = 0;
					diagNeg = 0;
					currentPlayer = 1; // Player 'X': 1, Player 'O': -1
					internalGameboard = new String [n][n];
					btnGameBoard = new Button[n][n];
					freeSpots = new ArrayList <Spot>();
					freeSpotsCopy = new ArrayList <Spot>();
					lblInputNconfig.setVisible(false);
					txbInputNconfig.setVisible(false);
					btnEnterNConfig.setVisible(false);
					
					// 1. This will store free spots of the game board
					freeSpotsConfig(n);
					
					// 2. Filling the game board with default symbols 
					defaultSymbolsConfig(n);
				} catch(Exception e) {
					lblInputNconfig.setValue("Enter a number:");
					//  Block of code to handle errors
				}
			}
			
		};
		btnEnterNConfig.addEventListener(Events.ON_CLICK, nConfigListener);
	}
	
	private void freeSpotsConfig(int n) {
		
		Spot gameSpot;
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				gameSpot = new Spot(i,j);
				freeSpots.add(gameSpot);
				freeSpotsCopy.add(gameSpot);
				btnGameBoard[i][j] = new Button();
				btnGameBoard[i][j].setLabel("-");
				btnGameBoard[i][j].setStyle("border: 25px solid transparent; font-weight: bold;"
						+ "font-size: xx-large; font-family: monospace;");
			}
		}
	}
	
	private void defaultSymbolsConfig(int n) {
		
		Random rand = new Random();
		emptySpots = (n*n) - 1; // Game board will have n^2 empty spots initially
		int index = 0;
		Spot someSpot;
		
		while (emptySpots != n-1) { // Until n empty spots to start the game (index from 0)
			
			index = rand.nextInt(freeSpotsCopy.size());
			someSpot = freeSpotsCopy.get(index);
			int auxRow = someSpot.getRow();
			int auxCol = someSpot.getCol();
			
			// If there is no winner then place a symbol in some spot
			if (!winnerSpotConfig(auxRow, auxCol, currentPlayer)) {
				
				freeSpotsCopy.remove(index);
				freeSpots.remove(index);
				if (currentPlayer == 1) {
					internalGameboard[auxRow][auxCol] = "X";
					btnGameBoard[auxRow][auxCol].setLabel("X");
					btnGameBoard[auxRow][auxCol].setDisabled(true);
					currentPlayer = -1;
				} else {
					internalGameboard[auxRow][auxCol] = "O";
					btnGameBoard[auxRow][auxCol].setLabel("O");
					btnGameBoard[auxRow][auxCol].setDisabled(true);
					currentPlayer = 1;
				}
				emptySpots -= 1;
			} else {
				freeSpotsCopy.remove(index);
			}
		}
		
		// Important: this event listener should have its own separate class not an anonymous class (temporal implementation)
		EventListener<Event> spotListener = new SerializableEventListener<Event>() {
            private static final long serialVersionUID = 1L;

            public void onEvent(Event event) throws Exception {
            	Button btnSpot = (Button) event.getTarget();
            	String move;
            	
            	if (currentPlayer == 1) {
            		
            		outerloop:
            		for (int i = 0; i < btnGameBoard.length; i++) {
            			for (int j = 0; j < btnGameBoard.length; j++) {
            				if (btnGameBoard[i][j].equals(btnSpot)) {
            					btnSpot.setLabel("X");
            					btnSpot.setDisabled(true);
            					currentPlayer = -1;
            					move = playerMove(i, j, 1);
            					emptySpots -= 1;
            					if (move.equals("X")) {
            	    		    	lblGameStatus.setValue("Player X won. Reload page to play again.");
            	    		    	disableGameBoard();
            	    		    	printGameBoardConsole();
            	    		    	System.out.println("Player X won");
            	    		    	break outerloop;
            	    		    } else if (emptySpots + 1 == 0) { // 0 counts as spot
            	            		lblGameStatus.setValue("Match drawn. Reload page to play again.");
            	            		System.out.println("Match drawn. Reload page to play again.");
            	            		printGameBoardConsole();
            	            		break outerloop;
            	            	}
            					printGameBoardConsole();
            					lblGameStatus.setValue("Player O's turn");
            					break outerloop;
            				}
            			}
					}
            	} else {
            		outerloop:
            		for (int i = 0; i < btnGameBoard.length; i++) {
            			for (int j = 0; j < btnGameBoard.length; j++) {
            				if (btnGameBoard[i][j].equals(btnSpot)) {
            					btnSpot.setLabel("O");
            					btnSpot.setDisabled(true);
            					move = playerMove(i, j, -1);
            					currentPlayer = 1;
            					emptySpots -= 1;
            					if (move.equals("O")) {
            	    		    	lblGameStatus.setValue("Player O won. Reload page to play again.");
            	    		    	disableGameBoard();
            	    		    	printGameBoardConsole();
            	    		    	System.out.println("Player O won");
            	    		    	break outerloop;
            	    		    } else if (emptySpots + 1 == 0) { // 0 counts as spot
            	            		lblGameStatus.setValue("Match drawn. Reload page to play again.");
            	            		System.out.println("Match drawn. Reload page to play again.");
            	            		printGameBoardConsole();
            	            		break outerloop;
            	            	}
            					printGameBoardConsole();
            					lblGameStatus.setValue("Player X's turn");
            					break outerloop;
            				}
            			}
					}
            	}
            }
        };
        
		// Adding buttons to the game board
        rows = new Rows();
        rows.setParent(grdGameBoard);
        
		for (int i = 0; i < n; i++) {
			row = new Row();
			row.setParent(rows);
			for (int j = 0; j < n; j++) {
				btnGameBoard[i][j].setParent(row);
				btnGameBoard[i][j].addEventListener(Events.ON_CLICK, spotListener);
			}
		}
		
		// Game started: player X starts first
		lblGameStatus.setValue("Player X's turn");
	}
	
	private boolean winnerSpotConfig(int row, int col, int player) {
		
		int move = player == 1 ? 1 : -1;
		rowCounter[row] += move;
		colCounter[col] += move;
		if (row == col) diagNeg += move;
		if (row == n - col - 1) diagPos += move;
		
		if (rowCounter[row] == n  || colCounter[col] == n  || diagNeg == n  || diagPos == n ||
			rowCounter[row] == -n || colCounter[col] == -n || diagNeg == -n || diagPos == -n) {
			// If there is a winner then do not count the spot
			rowCounter[row] -= move;
			colCounter[col] -= move;
			if (row == col) diagNeg -= move;
			if (row == n - col - 1) diagPos -= move;
			return true;
		} else {
			return false;
		}
	}
	
	private String playerMove(int row, int col, int player) {
		
		int move = player == 1 ? 1 : -1;
		rowCounter[row] += move;
		colCounter[col] += move;
		if (row == col) diagNeg += move;
		if (row == n - col - 1) diagPos += move;
		
		if (rowCounter[row] == n || colCounter[col] == n || diagNeg == n || diagPos == n) {
			
			internalGameboard[row][col] = "X";
			return "X";
		} else if (rowCounter[row] == -n || colCounter[col] == -n || diagNeg == -n || diagPos == -n) {
			
			internalGameboard[row][col] = "O";
			return "O";
		} else {
			if (player == 1)  {
				internalGameboard[row][col] = "X";
			} else {
				internalGameboard[row][col] = "O";
			}
			return "Tie";
		}
	}
	
	private void disableGameBoard() {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				btnGameBoard[i][j].setDisabled(true);
			}
		}
	}
	
	private void printGameBoardConsole() {
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				System.out.print(internalGameboard[i][j] + " ");
			}
			System.out.println();
		}
	}
}
