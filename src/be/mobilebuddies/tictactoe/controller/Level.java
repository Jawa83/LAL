package be.mobilebuddies.tictactoe.controller;

/**
 * The difficulty level of the Tic-Tac-Toe game.
 *  
 * @author koen
 *
 */
public enum Level {
	BEGINNER(1,     "Beginner"), 
	INTERMEDIATE(2, "Intermediate"), 
	ADVANCED(3,     "Advanced");
	
	private final int value;
	private final String text;
	
	Level(int value, String text) {
		this.value = value;
		this.text = text;
	}
	
	public static Level getByValue(int value) {
		Level found = null;
		for (Level l : Level.values()) {
			if (value == l.value) {
				found = l;
			}
		}
		return found;
	}
	
	public int getLevelValue() {
		return this.value;
	}
	
	public String getLevelText() {
		return this.text;
	}
}
