package be.mobilebuddies.tictactoe.model;

/**
 * Enum representing the value of the cells of the play field: empty, player or computer.
 * @author koen
 *
 */
public enum CellValue {
	EMPTY(' '), PLAYER('X'), COMPUTER('O');
	private char value;
	
	private CellValue(char value) {
		this.value = value;
	}
	
	public String toString() {
		return Character.toString(value);
	}
}
