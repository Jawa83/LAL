package be.mobilebuddies.tictactoe;

import be.mobilebuddies.tictactoe.controller.Level;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	
	public final static String SELECTED_LEVEL = "com.example.myfirstapp.SELECTED_LEVEL";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		
		setContentView(R.layout.activity_main);
		
		LinearLayout ll = (LinearLayout)findViewById(R.id.main_layout);
		
		ImageView iv = new ImageView(this);
		iv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		iv.setImageResource(R.drawable.angry_tictactoe_birds_logo);
		ll.addView(iv);
		
		// Create a button for each value in the enum Level
		// Each of these buttons starts a game with the corresponding 'level'
		for (Level level : Level.values()) {
			Button bt = new Button(this);
			bt.setText(level.getLevelText());
			bt.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			bt.setId(level.getLevelValue());
			
			bt.setOnClickListener(new View.OnClickListener() {
			    public void onClick(View v) {
			        startGame(v);
			    }
			});			
			
			ll.addView(bt);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/** 
	 * Called when the user clicks a 'Level' button 
	 * Starts a game with the level corresponding to the selected button
	 * */
	public void startGame(View view) {
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra(SELECTED_LEVEL, view.getId());
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_settings:
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
	
}
