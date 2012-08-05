package ca.site3.ssf.android.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import ca.site3.ssf.android.R;
import ca.site3.ssf.gamemodel.RoundEndedEvent;

public class GameRoundsView extends LinearLayout {
	private static final int numberRounds = 3;
	public int currentRound = 1;

	private View[] views;
	private RoundEndedEvent.RoundResult[] roundResults;

	// FIXME save the rounds state
	public GameRoundsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.setOrientation(HORIZONTAL);
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		roundResults = new RoundEndedEvent.RoundResult[numberRounds];
		views = new View[numberRounds];
		
		for (int i = 0; i < numberRounds; i++) {
			views[i] = inflater.inflate(R.layout.game_round, null);
			
			views[i].findViewById(R.id.border).setBackgroundColor(getResources().getColor(
					i == currentRound - 1 ? R.color.round_border_active : R.color.transparent));
			
			this.addView(views[i]);
		}
	}
	
	private void updateCurrentRoundDisplay() {
		for (int i = 0; i < numberRounds; i++) {
			views[i].findViewById(R.id.border).setBackgroundColor(getResources().getColor(
					i == currentRound - 1 ? R.color.round_border_active : R.color.transparent));
		}
	}
	
	public void setCurrentRound(int newRound) {
		currentRound = newRound;
		updateCurrentRoundDisplay();
	}
	
	public void clearRoundResults() {
		for (int i = 0; i < numberRounds; i++) {
			views[i].findViewById(R.id.color).setBackgroundColor(getResources().getColor(R.color.round_background));
			views[i].findViewById(R.id.border).setBackgroundColor(getResources().getColor(R.color.transparent));
		}
		roundResults = new RoundEndedEvent.RoundResult[numberRounds];
	}
	
	public void handleRoundEndedEvent(RoundEndedEvent event) {
		roundResults[event.getRoundNumber() - 1] = event.getRoundResult();
		View colorView = views[currentRound].findViewById(R.id.color);
		switch (event.getRoundResult()) {
		case PLAYER1_VICTORY:
			colorView.setBackgroundColor(getResources().getColor(R.color.player_one));
			break;
		case PLAYER2_VICTORY:
			colorView.setBackgroundColor(getResources().getColor(R.color.player_two));
			break;
		case TIE:
			// FIXME make tie image
			break;
		}
		currentRound++;
		updateCurrentRoundDisplay();
	}

}