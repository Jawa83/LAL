package be.mobilebuddies.tictactoe.view.simple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import be.mobilebuddies.tictactoe.controller.GameController;
import be.mobilebuddies.tictactoe.controller.Level;
import be.mobilebuddies.tictactoe.model.CellValue;
import be.mobilebuddies.tictactoe.model.Grid;

public class PlayTicTacToe {

	private GameController ctrl;
	
	private PlayTicTacToe() {
		ctrl = new GameController().withGrid(new Grid().initialize()).withLevel(Level.INTERMEDIATE);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PlayTicTacToe().play();

	}
	
	private void play() {
		InputStreamReader read = new InputStreamReader(System.in);
	    BufferedReader in = new BufferedReader(read);
	    System.out.print("Choose level [1=Beginner, 2=Intermediate, 3=Advanced]: ");
	    Level level;
	    try {
			String strLevel = in.readLine();
			int intLevel = Integer.parseInt(strLevel);
			level = Level.getByValue(intLevel);
			if (level == null) {
				System.err.println("Wrong level specified. Assuming advanced level");
				level = Level.ADVANCED;
			}
		} catch (IOException e1) {
			System.err.println("wrong level specified. Assuming advanced level");
			level = Level.ADVANCED;
		}
	    ctrl.withLevel(level);
	    System.out.println ("playing level: " + level);
		boolean stopPlaying = false;
		while (!stopPlaying) {
			CellValue who = ctrl.whoStartsGame();
			if (who == CellValue.COMPUTER) {
				System.out.println("Computer starts");
				ctrl.doComputersMove();
			} else {
				System.out.println("You start");
			}
			System.out.println (ctrl);
			while (!ctrl.isGameEnded()) {
				boolean validMove = false;
				while (!validMove) {
					try {
					    System.out.print("row: ");
					    int row = Integer.parseInt(in.readLine());
					    System.out.print("Column: ");
						int col = Integer.parseInt(in.readLine());
						validMove = ctrl.fillCell(row, col);
						if (!validMove) {
							System.out.println("Wrong cell specified");
						}
					} catch (Exception e) {
						System.out.println ("Wrong value specified");
					}
				}
				System.out.println(ctrl);
				if (!ctrl.isGameEnded()) {
					ctrl.doComputersMove();
					System.out.println (ctrl);
				}
			}
			System.out.print("Play another game? [Y/N]");
			String choice;
			try {
				choice = in.readLine();
				if (!choice.toUpperCase().startsWith("Y")) {
					stopPlaying = true;
				}
			} catch (IOException e) {
				// ignore
			}
		}
		System.out.println ("Thank you for playing - Bye!");
	}
}
