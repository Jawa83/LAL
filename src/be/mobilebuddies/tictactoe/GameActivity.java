package be.mobilebuddies.tictactoe;

import be.mobilebuddies.tictactoe.controller.GameController;
import be.mobilebuddies.tictactoe.controller.Level;
import be.mobilebuddies.tictactoe.model.CellValue;
import be.mobilebuddies.tictactoe.model.Grid;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class GameActivity extends Activity {
		
	private static final int EMPTY_CELL_IMG_RESOURCE = R.drawable.tnt;
	private static final int PLAYER_CELL_IMG_RESOURCE = R.drawable.red;
	private static final int COMPUTER_CELL_IMG_RESOURCE = R.drawable.green;
	private static final int BACKGROUND_IMG_RESOURCE = R.drawable.background;
	
	private static final long SHOW_CELL_ANIM_DURATION = 700;
	
	private static boolean isComputerOpponent = true;
	private static GameController gc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		gc = new GameController().withGrid(new Grid().initialize()).withLevel(Level.INTERMEDIATE);
		Grid grid = gc.getGrid();
		
		TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
		TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
		TableLayout tableLayout = new TableLayout(this);
		tableLayout.setLayoutParams(tableParams);
		tableLayout.setGravity(Gravity.CENTER);		

		for (int row = 0; row < grid.getRows(); row++) {
			TableRow tableRow = new TableRow(this);
			tableRow.setLayoutParams(tableParams);
			tableRow.setGravity(Gravity.CENTER);

			for (int column = 0; column < grid.getCols(); column++) {
				ImageView v = new ImageView(this);			
				v.setLayoutParams(rowParams);
				v.setImageResource(EMPTY_CELL_IMG_RESOURCE); 
				v.setId(calculateId(row, column));
				
				v.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (!gc.isComputersTurn() && !gc.isGameEnded()) {
							gc.fillCell(getRowFromId(v.getId()), getColumnFromId(v.getId()));
							updateGrid();    // repaint
							// When playing against a computer opponent,
							// calculate the computers move
							if (!gc.isGameEnded() && isComputerOpponent && gc.isComputersTurn()) {												        	
								// add a delay to the computers move
								// this allows for the animation on the players move to finish
								Handler handler = new Handler(); 
								handler.postDelayed(new Runnable() { 
									public void run() { 
										gc.doComputersMove(); 
										updateGrid();
										if (gc.isGameEnded()) {
											gameEnded();
										}
									} 
								}, SHOW_CELL_ANIM_DURATION);														
							}
							if (gc.isGameEnded()) {
								gameEnded();
							}							
						}
						return true;
					}
				});				
				
				tableRow.addView(v);
			}
			tableLayout.addView(tableRow);
		}		
				
		tableLayout.setBackgroundResource(BACKGROUND_IMG_RESOURCE);
		setContentView(tableLayout);
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_refresh:
		    Intent intent = getIntent();
		    finish();
		    startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Repaint the grid to the latest status	
	 * 
	 */
	private void updateGrid() {
		Grid grid = gc.getGrid();
		for (int row = 0; row < grid.getRows(); row++) {
			for (int column = 0; column < grid.getCols(); column++) {
				if (grid.isCellChanged(row, column)) {
					grid.setCellUpdated(row,column);
					ImageView v = (ImageView)findViewById(calculateId(row, column));	
					// animation
					ScaleAnimation anim = new ScaleAnimation(0, 1, 0, 1);
					anim.setInterpolator(new LinearInterpolator());
					anim.setRepeatCount(0);
					anim.setDuration(SHOW_CELL_ANIM_DURATION);
					v.startAnimation(anim);
					v.setImageResource(getImageResourceByCellValue(grid.getValue(row, column)));
				}
			}
		}
	}

	private void gameEnded() {
		Grid grid = gc.getGrid();
		gc.detect3InARow(); // needed to make sure the 'winning cells' are indicated
		for (int row = 0; row < grid.getRows(); row++) {
			for (int column = 0; column < grid.getCols(); column++) {
				if (grid.isWinningCell(row, column)) {
					ImageView v = (ImageView)findViewById(calculateId(row, column));	
					// animation
					RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, 
							Animation.RELATIVE_TO_SELF, 0.5f, 
							Animation.RELATIVE_TO_SELF, 0.5f); 
					anim.setInterpolator(new LinearInterpolator());
					anim.setRepeatCount(Animation.INFINITE);
					anim.setDuration(SHOW_CELL_ANIM_DURATION);
					v.startAnimation(anim);				
				}
			}
		}
	}
	
	/**
	 * Determine the correct image depending on the value of the cell 
	 * (PLAYER, COMPUTER, EMPTY)
	 * @param value
	 * @return
	 */
	private int getImageResourceByCellValue(CellValue value) {
		switch (value) {
			case PLAYER:
				return PLAYER_CELL_IMG_RESOURCE;
			case COMPUTER:
				return COMPUTER_CELL_IMG_RESOURCE;
			default:
				return EMPTY_CELL_IMG_RESOURCE;
		}
	}

	/**
	 * Quick and dirty way to find the correct view (ie. image on row 2 column 3 will have id '2003')
	 * @param row
	 * @param col
	 * @return id
	 */
	private int calculateId(int row, int col) {
		return row*1000 + col;
	}
	
	/**
	 * calculate the row number based on the id
	 * @param id
	 * @return
	 */
	private int getRowFromId(int id) {
		return (int) Math.floor(id / 1000);
	}
	
	/**
	 * calculate the column number based on the id
	 * @param id
	 * @return
	 */
	private int getColumnFromId(int id) {
		return id % 1000;
	}
	
}

