package ca.site3.ssf.android.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.site3.ssf.android.Intents;
import ca.site3.ssf.android.R;
import ca.site3.ssf.gamemodel.GameState;
import ca.site3.ssf.gamemodel.GameState.GameStateType;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class GameControlsView extends LinearLayout {
	public OnPlayerActionSelect onPlayerActionSelect;
	
	Button nextStateButton1;
	Button nextStateButton2;
	Button killButton;
	Button pauseButton;
	Button testSystem;
	
	public List<GameStateType> nextStates = new ArrayList<GameStateType>(2);

	public GameControlsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View view = inflater.inflate(R.layout.controls, null);
		
		this.setOrientation(HORIZONTAL);
		
		OnClickListener onButtonClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onPlayerActionSelect == null) return;
				onPlayerActionSelect.onAction((String)v.getTag());
			}
		};
		
		nextStateButton1 = (Button)view.findViewById(R.id.next_state_1);
		nextStateButton1.setText(R.string.next_state);
		nextStateButton1.setTag(Intents.INIT_NEXT_1);
		nextStateButton1.setOnClickListener(onButtonClick);
		
		nextStateButton2 = (Button)view.findViewById(R.id.next_state_2);
		nextStateButton2.setText(R.string.next_state);
		nextStateButton2.setTag(Intents.INIT_NEXT_2);
		nextStateButton2.setOnClickListener(onButtonClick);
		
		killButton = (Button)view.findViewById(R.id.kill_game);
		killButton.setText(R.string.kill_game);
		killButton.setTag(Intents.KILL_GAME);
		killButton.setOnClickListener(onButtonClick);
		
		pauseButton = (Button)view.findViewById(R.id.pause_toggle);
		pauseButton.setText(R.string.pause);
		pauseButton.setTag(Intents.PAUSE_TOGGLE);
		pauseButton.setOnClickListener(onButtonClick);
		
		testSystem = (Button)view.findViewById(R.id.test_system);
		testSystem.setText(R.string.test_system);
		testSystem.setTag(Intents.TEST_SYSTEM);
		testSystem.setOnClickListener(onButtonClick);

		this.addView(view);
	}
	
	public abstract class OnPlayerActionSelect {
		public abstract void onAction(String action);
	}

	public void handleStateChange(GameState.GameStateType stateType) {
		if (stateType.canBePausedOrUnpaused()) {
			if (stateType == GameState.GameStateType.PAUSED_STATE) {
				this.pauseButton.setText("Unpause");
			}
			else {
				this.pauseButton.setText("Pause");
			}
			this.pauseButton.setEnabled(true);
		} else {
			this.pauseButton.setEnabled(false);
		}
		
		this.killButton.setEnabled(stateType.isKillable());

		if (stateType.isGoToNextStateControllable()) {
			List<GameStateType> nextGoToStates = stateType.nextControllableGoToStates();
			assert(nextGoToStates != null);
			assert(nextGoToStates.size() <= 2);
			
			this.nextStates = new ArrayList<GameStateType>(nextGoToStates);	
			Collections.copy(this.nextStates, nextGoToStates);
			
			switch (nextGoToStates.get(0)) {
			
				case RINGMASTER_STATE:
					this.nextStateButton1.setText("Enter Ringmaster State");
					break;
					
				case ROUND_BEGINNING_STATE:
					this.nextStateButton1.setText("Begin Round");
					break;
				
				default:
					assert(false);
					return;
			}
			this.nextStateButton1.setEnabled(true);
			
			if (nextGoToStates.size() == 2) {
				switch (nextGoToStates.get(1)) {
				case TEST_ROUND_STATE:
					this.nextStateButton2.setText("Test Round");
					break;
				default:
					assert(false);
					break;
				}
				this.nextStateButton2.setVisibility(View.VISIBLE); //true
				this.nextStateButton2.setEnabled(true);
			}
			else {
				this.nextStateButton2.setVisibility(View.GONE); //false
				this.nextStateButton2.setEnabled(false);
			}
		}
		else {
			this.nextStateButton1.setEnabled(false);
			this.nextStateButton2.setVisibility(View.GONE); //false
			this.nextStateButton2.setEnabled(false);
		}
	}
}