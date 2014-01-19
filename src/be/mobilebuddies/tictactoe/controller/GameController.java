package be.mobilebuddies.tictactoe.controller;

import java.util.Date;
import java.util.Random;

import be.mobilebuddies.tictactoe.model.CellValue;
import be.mobilebuddies.tictactoe.model.Grid;

/**
 * The controller for the Tic-Tac-Toe game. This controller takes care of all the logic to determine
 * how the game will behave, how difficult it is to beat the computer, the scores to be maintained, etc.
 * 
 * @author koen
 */
public class GameController {

	private Grid grid;
	private boolean gameEnded;
	private boolean computersTurn; 
	private int playerWins;
	private int computerWins;
	private Random randomValue;
	private Level level = Level.ADVANCED;
	
	/**
	 * creates a new controller, initialising some values, except the grid for the play field.
	 */
	public GameController() {
//		grid = new Grid().initialize();
		randomValue = new Random(new Date().getTime());
		gameEnded = false;
		computersTurn = false;
		playerWins = 0;
		computerWins = 0;
	}
	
	/** 
	 * assign a grid to the game controller
	 * @param grid the grid of the play field
	 * @return the came controller's instance with the grid assigned
	 */
	public GameController withGrid(Grid grid) {
		this.grid = grid.initialize();
		return this;
	}
	
	/**
	 * Applies the difficulty level of the game. This is an enum value:
	 * <ul>
	 * <li>BEGINNER
	 * <li>INTERMEDIATE
	 * <li>ADVANCED
	 * </ul> 
	 * 
	 * @param level the difficulty level.
	 * @return
	 */
	public GameController withLevel(Level level) {
		this.level = level;
		return this;
	}
	
	
	/**
	 * Determines who will start the game: the computer or you? This is based on a random value; 
	 * @return
	 */
	public CellValue whoStartsGame() {
		grid = grid.initialize();
		gameEnded = false;
		// decide who can start: computer or player
		computersTurn = computerStarts();
		return (computersTurn) ? CellValue.COMPUTER : CellValue.PLAYER;
	}
	
	/**
	 * Determines whether or not the computer can start the game. This is determined by a random value.
	 * 
	 * @return true if the computer starts the game, otherwise false
	 */
	private boolean computerStarts() {
		return randomValue.nextBoolean();
	}

	/**
	 * Makes the computer decide which cell of the grid to fill.
	 * If the game has already ended, the computer will not make a move.
	 * If the grid is fill, the game also ends.
	 * This is the sequence of checks the computer will do to decide which cell to fill:
	 * <ul>
	 * <li>can I win? (any level)
	 * <li>can I prevent winning? (advanced & intermediate level)
	 * <li>can I make 2 in a row, with another free space in that row to score 3? (advanced level only)
	 * <li>can I prevent the player from making 2 in a row? (advanced level only)
	 * <li>if not, fill a random cell (any level)
	 * </ul>
	 */
	public void doComputersMove() {
		if (!gameEnded) {
			if (grid.isFull()) {
				gameEnded = true;
			} else {
				boolean done = false;
				done = canComputerWin();
				if (done) {
					gameEnded = true;
					computerWins ++;
				} else {
					if (level == Level.ADVANCED || level == Level.INTERMEDIATE) {
						done = canComputerPreventWinning();	
					}
					if (!done && level == Level.ADVANCED) {
						done = canComputerMake2();
					}
					if (!done & level == Level.ADVANCED) {
						done = canComputerPrevent2();
					}
					if (!done) {
						if (makeRandomComputerMove()) {
							gameEnded = true;
							computerWins ++;
						} else {
							if (grid.isFull()) {
								gameEnded = true;
							}
						}
					}
					computersTurn = false;
				}				
			}
		}		
	}
	
	/**
	 * check whether the computer can win the game (already have 2 in a row and
	 * can score a 3rd one). If so, the move will be made.
	 * 
	 * @return true if the winning move is made, otherwise false
	 */
	public boolean canComputerWin() {
		// row per row: can we score 3 in a row?
		boolean canWin = false;
		canWin = canMake3Horizontal(CellValue.COMPUTER);
		if (!canWin) {
			// column per column: can we score 3 in a column?
			canWin = canMake3Vertical(CellValue.COMPUTER);
		}
		if (!canWin) {
			canWin = canMake3DiagonalUp(CellValue.COMPUTER);
		}
		if (!canWin) {
			canWin = canMake3DiagonalDown(CellValue.COMPUTER);
		}
		
		return canWin;
	}
	
	/**
	 * Checks whether the specified player can score 3 in a row, horizontally. 
	 * If so, the computer will fill that space, either scoring
	 * 3 himself or preventing the player from scoring 3 in a row.
	 * 
	 * @return if a cell is filled, otherwise false
	 */
	private boolean canMake3Horizontal(CellValue who) {
		boolean canWin = false;
		for (int row = 0; row < grid.getRows(); row ++) {
			for (int col = 0; col + 2 < grid.getCols(); col ++) {
				// from the current cell: look 2 cells ahead (if possible)
				canWin = canMake3(who, row, col, row, col + 1, row, col + 2);

				if (canWin) {
					break;
				}
			}
			if (canWin) {
				break;
			}
		}
		return canWin;
	}

	/**
	 * Checks whether the specified player can score 3 in a row, vertically. 
	 * If so, the computer will fill that space, either scoring
	 * 3 himself or preventing the player from scoring 3 in a row.
	 *  
	 * @return true if a cell is filled, otherwise false
	 */
	private boolean canMake3Vertical(CellValue who) {
		boolean canWin = false;
		for (int col = 0; col < grid.getCols(); col ++) {
			for (int row = 0; row + 2 < grid.getRows(); row ++) {
				// from the current cell: look 2 cells ahead (if possible)
					canWin = canMake3(who, row, col, row + 1, col, row + 2, col);

				if (canWin) {
					break;
				}
			}
			if (canWin) {
				break;
			}
		}
		return canWin;
	}
	
	/**
	 * Checks whether the specified player can score 3 in a row, in whatever direction (horizontally, 
	 * vertically, diagonal up or diagonal down). If so, the computer will fill that space, either scoring
	 * 3 himself or preventing the player from scoring 3 in a row.
	 * 
	 * @param who for whom the check of 3 in a row has to be made
	 * @param curRow row of the current cell
	 * @param curCol column of the current cell
	 * @param nextRow row the next cell
	 * @param nextCol column of the next cell
	 * @param next2Row row of the 3rd cell
	 * @param next2Col column of the 3rd cell
	 * 
	 * @return true if a cell has been filled (either scoring 3 or preventing 3), otherwise false
	 */
	private boolean canMake3(CellValue who, int curRow, int curCol, int nextRow, int nextCol, int next2Row, int next2Col) {
		boolean cellFilled = false;
		if (grid.getValue(curRow, curCol) == who) {
			// X _ X
			if (grid.getValue(nextRow, nextCol) == CellValue.EMPTY && grid.getValue(next2Row, next2Col) == who) {
				// Fill computer here! to win or to prevent winning!
				grid.setValue(nextRow, nextCol, CellValue.COMPUTER);
				cellFilled = true; 
			} else {
				// X X _
				if (grid.getValue(nextRow, nextCol) == who && grid.getValue(next2Row, next2Col) == CellValue.EMPTY) {
					// Fill computer here! to win or to prevent winning!
					grid.setValue(next2Row, next2Col, CellValue.COMPUTER);
					cellFilled = true;
				}
			}
		} else {
			if (grid.getValue(curRow, curCol) == CellValue.EMPTY) {
				// _ X X
				if (grid.getValue(nextRow, nextCol) == who && grid.getValue(next2Row, next2Col) == who) {
					// Fill computer here! to win or to prevent winning!
					grid.setValue(curRow, curCol, CellValue.COMPUTER);
					cellFilled = true;
				}
			} else {
				// go to the next cell;
			}
		}
		return cellFilled;
	}
	
	/**
	 * Checks whether the specified player can score 3 in a row, diagonally down 
	 * (from top left to bottom right). 
	 * If so, the computer will fill that space, either scoring 
	 * 3 himself or preventing the player from scoring 3 in a row.
	 * 
	 * @return true if a cell cell is filled, otherwise false
	 */
	private boolean canMake3DiagonalDown(CellValue who) {
		boolean canWin = false;
		for (int row = 0; row + 2 < grid.getRows(); row ++) {
			for (int col = 0; col + 2 < grid.getCols(); col ++) {
				canWin = canMake3(who, row, col, row + 1, col + 1, row + 2, col + 2);
				if (canWin) {
					break;
				}
			}
			if (canWin) {
				break;
			}
		}
		return canWin;
	}

	/**
	 * Checks whether the specified player can score 3 in a row, diagonally up 
	 * (from bottom left to top right). 
	 * If so, the computer will fill that space, either scoring
	 * 3 himself or preventing the player from scoring 3 in a row.
	 * 
	 * @return true if a cell is filled, otherwise false
	 */
	private boolean canMake3DiagonalUp(CellValue who) {
		boolean canWin = false;
		for (int row = grid.getRows() - 1; row - 2 >= 0; row --) {
			for (int col = 0; col + 2 < grid.getCols(); col ++) {
				canWin = canMake3(who, row, col, row - 1, col + 1, row - 2, col + 2);
				if (canWin) {
					break;
				}
			}
			if (canWin) {
				break;
			}
		}
		return canWin;
	}
	
		
	/**
	 * check whether the computer can avoid the player from winning the game
	 * (player has 2 in a row and can score a 3rd one). If so, the move will be 
	 * made.
	 * 
	 * @return true if the computer have prevented the player from winning,  
	 * otherwise false
	 */
	public boolean canComputerPreventWinning() {
		// row per row: can we score 3 in a row?
		boolean canPrevent = false;
		canPrevent = canMake3Horizontal(CellValue.PLAYER);
		if (!canPrevent) {
			// column per column: can we score 3 in a column?
			canPrevent = canMake3Vertical(CellValue.PLAYER);
		}
		if (!canPrevent) {
			canPrevent = canMake3DiagonalUp(CellValue.PLAYER);
		}
		if (!canPrevent) {
			canPrevent = canMake3DiagonalDown(CellValue.PLAYER);
		}
		
		return canPrevent;
	}
	
	/**
	 * check whether the computer can make at least 2 in a row, as a preparation
	 * for 3 in a row. If so, the move will be made. 
	 * <p>
	 * A cell will not be picked if there is no 3rd cell free to complete 3 in a row.
	 * E.g.:
	 * <ul>
	 * <li><pre>O _ _</pre>
	 * <li><pre>_ O _</pre>
	 * <li><pre>_ _ O</pre>
	 * </ul>
	 * <p>But not: 
	 * <pre>X O _</pre>
	 * 
	 * @return true if the computer has made 2 in a row, otherwise false
	 */
	public boolean canComputerMake2() {
		
		boolean canMake2 = false;
		canMake2 = canMake2Horizontal(CellValue.COMPUTER);
		if (!canMake2) {
			canMake2 = canMake2Vertical(CellValue.COMPUTER);
		}
		if (!canMake2) {
			canMake2 = canMake2DiagonalDown(CellValue.COMPUTER);
		}
		if (!canMake2) {
			canMake2 = canMake2DiagonalUp(CellValue.COMPUTER);
		}
		
		return canMake2;
	}

	/**
	 * Checks whether the computer can prevent the player from making 2 in a row, as a preparation for
	 * 3 in a row. If so, the move will be made.
	 * <p>
	 * A cell will not be picked if there is no 3rd cell free to complete 3 in a row.
	 * E.g.:
	 * <ul>
	 * <li><code>O _ _</code>
	 * <li><code>_ O _</code>
	 * <li><code>_ _ O</code>
	 * </ul>
	 * <p>But not: 
	 * <code>O X _</code>
	 * 
	 * @return true if the computer has prevented the player from making 2 in a row, otherwise false 
	 */
	public boolean canComputerPrevent2() {
		boolean canPrevent2 = false;

		canPrevent2 = canMake2Horizontal(CellValue.PLAYER);
		if (!canPrevent2) {
			canPrevent2 = canMake2Vertical(CellValue.PLAYER);
		}
		if (!canPrevent2) {
			canPrevent2 = canMake2DiagonalDown(CellValue.PLAYER);
		}
		if (!canPrevent2) {
			canPrevent2 = canMake2DiagonalUp(CellValue.PLAYER);
		}
		
		
		return canPrevent2;
	}
	
	/**
	 * checks whether the specified player can make 2 cells in a row horizontally, 
	 * with as a preparation for 3 in a row.
	 * <p>
	 * If so, the move will be made, either to prepare 3 in a row for the computer 
	 * or to prevent the player from preparing 3 in a row.
	 *  
	 * @param who the player to check 2 in a row horizontally
	 * @return true if a cell was filled otherwise false
	 */
	private boolean canMake2Horizontal(CellValue who) {
		boolean canMake2 = false;
		for (int row = 0; row < grid.getRows(); row ++) {
			for (int col = 0; col + 2 < grid.getCols(); col ++) {
				// from the current cell: look 2 cells ahead (if possible)
				canMake2 = canMake2(who, row, col, row, col + 1, row, col + 2);

				if (canMake2) {
					break;
				}
			}
			if (canMake2) {
				break;
			}
		}
		return canMake2;
	}
	/**
	 * checks whether the specified player can make 2 cells in a row vertically, 
	 * with as a preparation for 3 in a row.
	 * <p>
	 * If so, the move will be made, either to prepare 3 in a row for the computer 
	 * or to prevent the player from preparing 3 in a row.
	 *  
	 * @param who the player to check 2 in a row vertically
	 * @return true if a cell was filled otherwise false
	 */
	
	private boolean canMake2Vertical(CellValue who) {
		boolean canMake2 = false;
		for (int col = 0; col < grid.getCols(); col ++) {
			for (int row = 0; row + 2 < grid.getRows(); row ++) {
				// from the current cell: look 2 cells ahead (if possible)
				canMake2 = canMake2(who, row, col, row + 1, col, row + 2, col);

				if (canMake2) {
					break;
				}
			}
			if (canMake2) {
				break;
			}
		}
		return canMake2;

	}
	
	/**
	 * checks whether the specified player can make 2 cells in a row diagonally up 
	 * (from bottom left to top right), with as a preparation for 3 in a row.
	 * <p>
	 * If so, the move will be made, either to prepare 3 in a row for the computer 
	 * or to prevent the player from preparing 3 in a row.
	 *  
	 * @param who the player to check 2 in a row diagonally up
	 * @return true if a cell was filled otherwise false
	 */
	private boolean canMake2DiagonalUp(CellValue who) {
		boolean canMake2 = false;
		for (int row = grid.getRows() - 1; row - 2 >= 0; row --) {
			for (int col = 0; col + 2 < grid.getCols(); col ++) {
				canMake2 = canMake2(who, row, col, row - 1, col + 1, row - 2, col + 2);
				if (canMake2) {
					break;
				}
			}
			if (canMake2) {
				break;
			}
		}
		return canMake2;
	}
	
	/**
	 * checks whether the specified player can make 2 cells in a row diagonally down 
	 * (from top left to bottom right), with as a preparation for 3 in a row.
	 * <p>
	 * If so, the move will be made, either to prepare 3 in a row for the computer 
	 * or to prevent the player from preparing 3 in a row.
	 *  
	 * @param who the player to check 2 in a row
	 * @return true if a cell was filled otherwise false
	 */
	private boolean canMake2DiagonalDown(CellValue who) {
		boolean canMake2 = false;
		for (int row = 0; row + 2 < grid.getRows(); row ++) {
			for (int col = 0; col + 2 < grid.getCols(); col ++) {
				canMake2 = canMake2(who, row, col, row + 1, col + 1, row + 2, col + 2);
				if (canMake2) {
					break;
				}
			}
			if (canMake2) {
				break;
			}
		}
		return canMake2;
	}

	/**
	 * checks whether the specified player can make 2 cells in a row in whatever direction, 
	 * with as a preparation for 3 in a row.
	 * <p>
	 * If so, the move will be made, either to prepare 3 in a row for the computer 
	 * or to prevent the player from preparing 3 in a row.
	 *  
	 * @param who the player to check 2 in a row
	 * @param curRow row of the current cell
	 * @param curCol column of the current cell
	 * @param nextRow row the next cell
	 * @param nextCol column of the next cell
	 * @param next2Row row of the 3rd cell
	 * @param next2Col column of the 3rd cell
	 * @return true if a cell was filled otherwise false
	 */
	private boolean canMake2(CellValue who, int curRow, int curCol, int nextRow, int nextCol, int next2Row, int next2Col) {
		boolean cellFilled = false;
		if (grid.getValue(curRow, curCol) == CellValue.EMPTY) {
			// _ _ X
			if (grid.getValue(nextRow, nextCol) == CellValue.EMPTY && grid.getValue(next2Row, next2Col) == who) {
				grid.setValue(curRow, curCol, CellValue.COMPUTER);
				cellFilled = true; 
			} else {
				// _ X _
				if (grid.getValue(nextRow, nextCol) == who && grid.getValue(next2Row, next2Col) == CellValue.EMPTY) {
					grid.setValue(curRow, curCol, CellValue.COMPUTER);
					cellFilled = true;
				}
			}
		} else {
			if (grid.getValue(curRow, curCol) == who) {
				// X _ _ 
				if (grid.getValue(nextRow, nextCol) == CellValue.EMPTY && grid.getValue(next2Row, next2Col) == CellValue.EMPTY) {
					grid.setValue(nextRow, nextCol, CellValue.COMPUTER);
					cellFilled = true;
				}
			} else {
				// go to the next cell;
			}
		}
		return cellFilled;		
	}
	
	/**
	 * The computer will determine where a free place can be found (at random) 
	 * and will mark that place on the grid.
	 * The only check happening here is whether the computer by accident scored 3 in a row... 
	 * @return true if the computer by accident won the game, otherwise false
	 */
	private boolean makeRandomComputerMove() {
		boolean done = false;
		boolean won = false;
		while (!done) {
			int row = randomValue.nextInt(grid.getRows());
			int col = randomValue.nextInt(grid.getCols());
			if (grid.isEmpty(row, col)) {
				grid.setValue(row, col, CellValue.COMPUTER);
				done = true;
				if (detect3InARow()) {
					// Computer did the last move, so I assume computer won
					won = true;
				}
			}
		}
		return won;
	}
	
	/**
	 * Checks whether the specified cell is empty. If so, the cell will be marked, 
	 * otherwise the player has to specify another cell.
	 * 
	 * @param row the row of the cell to be filled
	 * @param col the column of the cell to be filled
	 * @return true if the specified cell was valid, otherwise false
	 */
	public boolean fillCell (int row, int col) {
		boolean isValid = false;
		if (grid.isEmpty(row, col)) {
			grid.setValue(row, col, CellValue.PLAYER);
			isValid = true;
			this.computersTurn = true;
			gameEnded = detect3InARow();
			if (gameEnded) {
				playerWins ++;
			}
		}
		return isValid;
	}
	
	/**
	 * detects whether there are 3 in a row, no matter from whom.
	 * @return true if there is a winner, otherwise false
	 */
	public boolean detect3InARow() {
		boolean winner = false;
		winner = has3Horizontal();
		if (!winner) {
			winner = has3Vertical();
		}
		if (!winner) {
			winner = has3DiagonalUp();
		}
		if (!winner) {
			winner = has3DiagonalDown();
		}
		return winner;
	}
	
	/**
	 * detects whether there are 3 in a row, horizontally, no matter from whom.
	 * @return true if there is a winner, otherwise false
	 */
	private boolean has3Horizontal() {
		boolean winner = false;
		for (int row = 0; row < grid.getRows(); row ++) {
			for (int col = 0; col < grid.getCols() -2; col ++) {
				if (grid.getValue(row, col) != CellValue.EMPTY) {
					if (grid.getValue(row, col) == grid.getValue(row, col + 1) && grid.getValue(row, col) == grid.getValue(row, col + 2)) {
						winner = true;
						grid.setWinningCell(row, col);
						grid.setWinningCell(row, col + 1);
						grid.setWinningCell(row, col + 2);
						break;
					}
				}
			}
			if (winner) {
				break;
			}
		}
		return winner;
	}
	
	/**
	 * detects whether there are 3 in a row, vertically, no matter from whom
	 * @return true if there is a winner, otherwise false
	 */
	private boolean has3Vertical() {
		boolean winner = false;
		for (int col = 0; col < grid.getCols(); col ++) {
			for (int row = 0; row < grid.getRows() -2; row ++) {
				if (grid.getValue(row, col) != CellValue.EMPTY) {
					if (grid.getValue(row, col) == grid.getValue(row + 1, col) && grid.getValue(row, col) == grid.getValue(row + 2, col)) {
						winner = true;
						grid.setWinningCell(row, col);
						grid.setWinningCell(row + 1, col);
						grid.setWinningCell(row + 2, col);
						break;
					}
				}
			}
			if (winner) {
				break;
			}
		}
		return winner;
		
	}
	
	/**
	 * detects whether there are 3 in a row, diagonally up (bottom left to top right), no matter from whom.
	 * @return true if there is a winner, otherwise false
	 */
	private boolean has3DiagonalUp() {
		boolean winner = false;
		for (int row = grid.getRows() - 1; row - 2 >= 0; row --) {
			for (int col = 0; col + 2 < grid.getCols(); col ++) {
				if (grid.getValue(row, col) != CellValue.EMPTY) {
					if (grid.getValue(row, col) == grid.getValue(row - 1, col + 1) && grid.getValue(row, col) == grid.getValue(row -2, col + 2)) {
						winner = true;
						grid.setWinningCell(row, col);
						grid.setWinningCell(row - 1, col + 1);
						grid.setWinningCell(row - 2, col + 2);
						break;
					}
				}
			}
			if (winner) {
				break;
			}
		}
		
		return winner;
	}
	
	/**
	 * detects whether there are 3 in a row, diagonally down (top left to bottom right), no matter from whom.
	 * @return true if there is a winner, otherwise false
	 */
	private boolean has3DiagonalDown() {
		boolean winner = false;
		for (int row = 0; row < grid.getRows() - 2; row ++) {
			for (int col = 0; col < grid.getCols() - 2; col ++) {
				if (grid.getValue(row, col) != CellValue.EMPTY) {
					if (grid.getValue(row, col) == grid.getValue(row + 1, col + 1) && grid.getValue(row, col) == grid.getValue(row + 2, col + 2)) {
						winner = true;
						grid.setWinningCell(row, col);
						grid.setWinningCell(row + 1, col + 1);
						grid.setWinningCell(row + 2, col + 2);
						break;
					}
				}
			}
			if (winner) {
				break;
			}
		}
		return winner;	
	}
	
	/**
	 * returns the status of the game: true if ended, otherwise false.
	 * @return true if the game has ended, otherwise false
	 */
	public boolean isGameEnded() {
		return gameEnded;
	}
	
	public boolean isComputersTurn() {
		return this.computersTurn;
	}

	public Grid getGrid() {
		return this.grid;
	}
	
	/**
	 * returns a string representation of the controller. This is the status of the grid (like the string
	 * representation of the Grid class). If the game has ended, the score is shown.
	 */
	public String toString() {
		StringBuffer strBuf = new StringBuffer(grid.toString());
		if (gameEnded) {
			strBuf.append("\nGame ended. Score: You ");
			strBuf.append(playerWins);
			strBuf.append(" - Computer ");
			strBuf.append(computerWins);
		}
		return strBuf.toString();
	}
}
