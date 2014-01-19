package be.mobilebuddies.tictactoe.controller;

/**
 * The difficulty level of the Tic-Tac-Toe game.
 *  
 * @author koen
 *
 */
public enum Level {
	BEGINNER(1), INTERMEDIATE(2), ADVANCED(3);
	private final int value;
	Level(int value) {
		this.value = value;
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
}
