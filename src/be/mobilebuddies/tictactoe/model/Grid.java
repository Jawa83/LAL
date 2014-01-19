package be.mobilebuddies.tictactoe.model;

/**
 * The model of the Tic-Tac-Toe game. It consists of a grid of a specified size
 * (number of rows by a number of columns). The default grid size is 3x3.
 * <p>
 * Cells of the grid can have 3 different values, all represented by the FieldStatus
 * enum.
 * @author koen
 *
 */
public class Grid {
	private static final int DEFAULT_ROWS = 3;
	private static final int DEFAULT_COLS = 3;
	private int rows;
	private int cols;
	
	private CellValue playField[][]; 
	private boolean cellChanged[][];
	private boolean winningCell[][];
	
	/**
	 * creates a new tic tac toe grid with the default size of 3x3.
	 */
	public Grid() {
		rows = DEFAULT_ROWS;
		cols = DEFAULT_COLS;
	}
	
	public Grid(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
	}
	
	/**
	 * (re-)initialises the grid, making all cells empty again.
	 * @return the (re-)initialised grid
	 */
	public Grid initialize() {
		playField = new CellValue[rows][cols];
		cellChanged = new boolean[rows][cols];
		winningCell = new boolean[rows][cols];
		for (int row = 0; row < rows; row ++) {
			for (int col = 0; col < cols; col ++) {
				playField [row][col] = CellValue.EMPTY;
				cellChanged[row][col] = false;
			}
		}
		return this;
	}
	
	/**
	 * returns the row count of the grid
	 * @return row count of the grid
	 */
	public int getRows() {
		return rows;
	}
	
	/**
	 * returns the column count of the grid.
	 * @return column count of the grid
	 */
	public int getCols() {
		return cols;
	}
	
	/**
	 * checks if the cell at the specified position is empty.
	 * <br>
	 * This is a zero-based value! Top left is 0,0. 
	 * @param row the row number of the cell to check
	 * @param col the column number to check
	 * @return true if the cell is empty otherwise false
	 */
	public boolean isEmpty (int row, int col) {
		if (isValidCell(row, col)) {
			return (playField[row][col] == CellValue.EMPTY) ? true : false;
		}
		// will never happen because a runtime exception will be thrown if the cell is invalid
		return false;
	}
	
	/**
	 * returns the value of the cell at the specified position
	 * @param row the row number to check
	 * @param col the column number to check
	 * @return the value of the specified cell, as FieldStatus enum
	 */
	public CellValue getValue(int row, int col) {
		if (isValidCell(row, col)) {
			return playField[row][col];
		}
		// will never be returned because a runtime exception will be thrown if the cell is invalid
		return null;
	}
	
	/**
	 * sets the value of the cell to the specified value
	 * <p>
	 * Will either be:
	 * <ul>
	 * <li>FieldStatus.EMPTY
	 * <li>FieldStatus.COMPUTER
	 * <li>FieldStatus.ME
	 * </ul>
	 * @param row the row of the cell to be filled in
	 * @param col the column of the cell to be filled in
	 * @param value the value of the cell
	 */
	public void setValue(int row, int col, CellValue value) {
		if (isValidCell(row, col)) {
			playField[row][col] = value;
			cellChanged[row][col] = true;
		}
	}
	
	/**
	 * Indicates that the changes for this cell have been persisted to the game world
	 * Needed to track correctly which cells have been changed since the last update
	 */
	public void setCellUpdated(int row, int col) {
		cellChanged[row][col] = false;
	}
	
	/**
	 * Checks if a cell has been changed since the last update
	 * @param row
	 * @param col
	 * @return
	 */
	public boolean isCellChanged(int row, int col) {
		return cellChanged[row][col];
	}
		
	/**
	 * Indicates that a certain cell is a part of the winning (3-in-a-row) combination
	 * @param row
	 * @param col
	 */
	public void setWinningCell(int row, int col) {
		winningCell[row][col] = true;
	}	
	
	/**
	 * Checks whether a certain cell is a part of the winning (3-in-a-row) combination
	 * @param row
	 * @param col
	 * @return
	 */
	public boolean isWinningCell(int row, int col) {
		return winningCell[row][col];
	}
	
	private boolean isValidCell(int row, int col) {
		if (row < 0 || row >= rows) {
			throw new IllegalArgumentException("row number is invalid: " + row);
		}
		if (col < 0 || col >= cols) {
			throw new IllegalArgumentException("column number is invalid: " + col);
		}
		return true;
		
	}
	
	/**
	 * determines whether all the cells in the grid are full.
	 * 
	 * @return true if all cells are taken, otherwise false
	 */
	public boolean isFull() {
		boolean full = true;
		for (int row = 0; row < rows; row ++) {
			for (int col = 0; col < cols; col ++) {
				if (playField[row][col] == CellValue.EMPTY) {
					full = false;
					break;
				}
				if (!full) {
					break;
				}
			}
		}
		return full;
	}
	
	/**
	 * returns a simple string representation of the tic tac toe grid.
	 */
	public String toString() {
		StringBuffer side = new StringBuffer(" +");
		StringBuffer colNums = new StringBuffer("  ");
		for (int col = 0; col < cols; col ++) {
			side.append("-+");
			colNums.append(col);
			colNums.append(" ");
		}
		side.append("\n");
		colNums.append("\n");
		StringBuffer buf = new StringBuffer(colNums);
		buf.append(side);
		for (int row = 0; row < rows; row ++) {
			buf.append(row);
			buf.append("|");
			for (int col = 0; col < cols; col ++) {
				buf.append(playField[row][col]);
				buf.append("|");
			}
			buf.append("\n");
			buf.append(side);
		}
		return buf.toString();
	}
}
