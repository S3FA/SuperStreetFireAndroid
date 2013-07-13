package ca.site3.ssf.android.views;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import ca.site3.ssf.android.R;
import ca.site3.ssf.gamemodel.RoundEndedEvent;
import ca.site3.ssf.gamemodel.RoundEndedEvent.RoundResult;

public class GameRoundsView extends LinearLayout {
	private static final int NUM_ROUNDS = 3;
	public int currentRound = 1;

	private View[] views;
	private RoundEndedEvent.RoundResult[] roundResults;
	
	private static String LOG_TAG = GameRoundsView.class.getName();

	public GameRoundsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.setOrientation(HORIZONTAL);
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		roundResults = new RoundEndedEvent.RoundResult[NUM_ROUNDS];
		views = new View[NUM_ROUNDS];
		
		for (int i = 0; i < NUM_ROUNDS; i++) {
			views[i] = inflater.inflate(R.layout.game_round, null);

			this.addView(views[i]);
		}
		
		updateCurrentRoundDisplay();
	}
	
	private void updateCurrentRoundDisplay() {
		for (int i = 0; i < NUM_ROUNDS; i++) {
			views[i].findViewById(R.id.border).setBackgroundResource(i == currentRound - 1 ? R.drawable.round_item_active_bg : R.drawable.round_item_bg);
		}
	}
	
	public void setCurrentRound(int newRound) {
		currentRound = newRound;
		updateCurrentRoundDisplay();
	}
	
	public void setRounds(List<RoundResult> newRoundResults) {
		clearRoundResults();
		int i = 0;
		for (RoundResult result : newRoundResults) {
			handleRound(result, i);
			i++;
		}
		currentRound = i + 1;
		updateCurrentRoundDisplay();
	}
	
	public void clearRoundResults() {
		for (int i = 0; i < NUM_ROUNDS; i++) {
			views[i].findViewById(R.id.color).setBackgroundResource(R.drawable.round_item_none);
			views[i].findViewById(R.id.border).setBackgroundResource(R.drawable.round_item_bg);
		}
		roundResults = new RoundEndedEvent.RoundResult[NUM_ROUNDS];
	}
	
	public void handleRoundEndedEvent(RoundEndedEvent event) {
		RoundResult roundResult = event.getRoundResult();
		roundResults[event.getRoundNumber() - 1] = roundResult;
		handleRound(roundResult, event.getRoundNumber() - 1);
		currentRound++;
		updateCurrentRoundDisplay();
	}
	
	public void handleRound(RoundResult result, int roundNumber) {
		View colorView = views[roundNumber].findViewById(R.id.color);
		switch (result) {
		case PLAYER1_VICTORY:
			colorView.setBackgroundResource(R.drawable.round_item_one);
			break;
		case PLAYER2_VICTORY:
			colorView.setBackgroundResource(R.drawable.round_item_two);
			break;
		case TIE:
			colorView.setBackgroundResource(R.drawable.round_item_tie);
			break;
		}
	}

}