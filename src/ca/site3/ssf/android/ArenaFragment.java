package ca.site3.ssf.android;

import java.util.EnumSet;
import java.util.concurrent.BlockingQueue;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ca.site3.ssf.android.views.ColorPallet;
import ca.site3.ssf.android.views.GameControlsView;
import ca.site3.ssf.android.views.GameRoundsView;
import ca.site3.ssf.android.views.GamemasterMovesControlsView;
import ca.site3.ssf.android.views.RingView;
import ca.site3.ssf.gamemodel.ActionFactory;
import ca.site3.ssf.gamemodel.FireEmitter;
import ca.site3.ssf.gamemodel.FireEmitterChangedEvent;
import ca.site3.ssf.gamemodel.GameInfoRefreshEvent;
import ca.site3.ssf.gamemodel.GameState;
import ca.site3.ssf.gamemodel.GameStateChangedEvent;
import ca.site3.ssf.gamemodel.IGameModel.Entity;
import ca.site3.ssf.gamemodel.IGameModelEvent;
import ca.site3.ssf.gamemodel.PlayerAttackActionEvent;
import ca.site3.ssf.gamemodel.PlayerBlockActionEvent;
import ca.site3.ssf.gamemodel.PlayerHealthChangedEvent;
import ca.site3.ssf.gamemodel.RoundEndedEvent;
import ca.site3.ssf.gamemodel.RoundPlayTimerChangedEvent;

public class ArenaFragment extends Fragment {

	// TODO devices connect w/ battery status
	// Battery status - is this in the IOServer yet?
	// information messages from server to tablet
	// remove player moves, add ringmaster moves and sound effects
	
	String[] playersName = {"Player 1", "Player 2"};
	
	RingView ringView;
	ColorPallet colorPallet;
	GameControlsView gameControlsView;
	GameRoundsView roundsView;
	
	TextView roundTimer;
	TextView gameInfo;
	TextView[] playerName;
	TextView[] playerLastMove;
	
	FireEmitterChangedTask task;
	
	GamemasterMovesControlsView gamemasterControls;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.arena, null);

		ringView = (RingView) view.findViewById(R.id.ring_view);
		ringView.invalidate();
		
		ringView.onEmitterTouch = ringView.new OnEmitterTouch() {
			@Override
			public void onEmitterTouch(RingView.Emitter emitter, FireEmitter.Location location, int index) {
				// FIXME make this an intent
				Intent flameTouch = new Intent(Intents.EVENT_TOUCH_FIRE_EMITTER);
				EnumSet<Entity> contributors = EnumSet.noneOf(Entity.class);
				contributors.add(Entity.PLAYER1_ENTITY);
				flameTouch.putExtra(Intents.EXTRA_TOUCH_CONTRIBUTORS, contributors);
				flameTouch.putExtra(Intents.EXTRA_FIRE_EMITTER_LOCATION, location);
				flameTouch.putExtra(Intents.EXTRA_FIRE_EMITTER_INDEX, index);
				getActivity().sendBroadcast(flameTouch);
			}
		};

		colorPallet = (ColorPallet) view.findViewById(R.id.color_pallet);
		colorPallet.onColorSelect = colorPallet.new OnColorSelect() {
			@Override
			public void onSelect(int newColor) {
				ringView.setColor(newColor);
			}
		};
		colorPallet.onDrawModeChange = colorPallet.new OnDrawModeChange() {
			@Override
			public void onModeChange(boolean isInDrawMode) {
				ringView.isInDrawMode = isInDrawMode;
			}
		};

		gameControlsView = (GameControlsView) view
				.findViewById(R.id.game_controls);
		gameControlsView.onPlayerActionSelect = gameControlsView.new OnPlayerActionSelect() {
			@Override
			public void onAction(String intentString) {
				Intent intent = new Intent(intentString);
				if (intentString == Intents.INIT_NEXT_1) {
					if (gameControlsView.nextStates.size() > 0) {
						intent = new Intent(Intents.INIT_NEXT);
						intent.putExtra(Intents.EXTRA_STATE, gameControlsView.nextStates.get(0));
					}
				}
				else if (intentString == Intents.INIT_NEXT_2) {
					if (gameControlsView.nextStates.size() > 1) {
						intent = new Intent(Intents.INIT_NEXT);
						intent.putExtra(Intents.EXTRA_STATE, gameControlsView.nextStates.get(1));
					}
				}
				getActivity().sendBroadcast(intent);
			}
		};
		
		roundTimer = (TextView)view.findViewById(R.id.round_timer);
		
		gameInfo = (TextView)view.findViewById(R.id.game_info);
		
		playerLastMove = new TextView[2];
		playerLastMove[0] = (TextView)view.findViewById(R.id.player_1_info);
		playerLastMove[1] = (TextView)view.findViewById(R.id.player_2_info);
		
		playerName = new TextView[2];
		playerName[0] = (TextView)view.findViewById(R.id.player_1_name);
		playerName[1] = (TextView)view.findViewById(R.id.player_2_name);
		
		playerName[0].setText(playersName[0]);
		playerName[1].setText(playersName[1]);
		
		gamemasterControls = (GamemasterMovesControlsView)view.findViewById(R.id.gamemaster_moves);
		gamemasterControls.onPlayerActionSelect = gamemasterControls.new OnPlayerActionSelect() {
			@Override
			public void onAction(ActionFactory.ActionType action, boolean leftHand, boolean rightHand) {
				Intent intent = new Intent(Intents.EVENT_GAMEMASTER_MOVE);
				intent.putExtra(Intents.EXTRA_ACTION_TYPE, action);
				intent.putExtra(Intents.EXTRA_LEFT_HAND, leftHand);
				intent.putExtra(Intents.EXTRA_RIGHT_HAND, leftHand);
				getActivity().sendBroadcast(intent);
			}
		};
		
		roundsView = (GameRoundsView) view.findViewById(R.id.rounds);
		
		task = new FireEmitterChangedTask();
		task.execute();

		return view;
	}
	
	public void handleGameModelEvent(final IGameModelEvent event) {
		switch (event.getType()) {
			case PLAYER_HEALTH_CHANGED:
				PlayerHealthChangedEvent newEvent = (PlayerHealthChangedEvent)event;
				ringView.playerHealth[newEvent.getPlayerNum() - 1] = newEvent.getNewLifePercentage();
				ringView.invalidate();
				break;
			case ROUND_PLAY_TIMER_CHANGED:
				RoundPlayTimerChangedEvent roundEvent = (RoundPlayTimerChangedEvent)event;
				updateTimer(roundEvent.getTimeInSecs());
				break;
			case GAME_INFO_REFRESH:
				GameInfoRefreshEvent refreshEvent = (GameInfoRefreshEvent)event;
				if (refreshEvent.getRoundInPlayTimer() > 0) {
					roundTimer.setText("" + refreshEvent.getRoundInPlayTimer());
				} else {
					roundTimer.setText("");
				}
				ringView.playerHealth[0] = refreshEvent.getPlayer1Health();
				ringView.playerHealth[1] = refreshEvent.getPlayer2Health();
				handleStateChange(refreshEvent.getCurrentGameState());
				updateTimer(refreshEvent.getRoundInPlayTimer());
				ringView.postInvalidate();
				break;
			case PLAYER_BLOCK_ACTION:
				PlayerBlockActionEvent blockAction = (PlayerBlockActionEvent)event;
				playerLastMove[blockAction.getPlayerNum() - 1].setText(String.format("BLOCK"));
				break;
			case PLAYER_ATTACK_ACTION:
				PlayerAttackActionEvent attackAction = (PlayerAttackActionEvent)event;
				playerLastMove[attackAction.getPlayerNum() - 1].setText(attackAction.getAttackType().toString());
				break;
			case GAME_STATE_CHANGED:
				GameStateChangedEvent stateChanged = (GameStateChangedEvent)event;
				handleStateChange(stateChanged.getNewState());
				break;
			case ROUND_ENDED:
				RoundEndedEvent roundEndedEvent = (RoundEndedEvent)event;
				roundsView.handleRoundEndedEvent(roundEndedEvent);
				break;
			case MATCH_ENDED:
				// reset names
				playerName[0].setText(playersName[0]);
				playerName[1].setText(playersName[1]);
				// clear last moves
				playerLastMove[0].setText("");
				playerLastMove[1].setText("");
				break;
			default:
				break;
		}				
	}
	
	public void updateTimer(int time) {
		if (time > 0) {
			roundTimer.setText("" + time);
		} else {
			roundTimer.setText("");
		}
	}
	
	public void handleStateChange(GameState.GameStateType stateType) {
		gameControlsView.handleStateChange(stateType);
		switch (stateType) {
			case ROUND_IN_PLAY_STATE:
			case TIE_BREAKER_ROUND_STATE:
			case TEST_ROUND_STATE:
				this.setEnableActionControls(true, false);
				break;
				
			case RINGMASTER_STATE:
				this.setEnableActionControls(false, true);
				break;
				
			default:
				this.setEnableActionControls(false, false);
				break;
		}
	}
	
    private class FireEmitterChangedTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			Log.e("ssg", "starting consumer");
			BlockingQueue<FireEmitterChangedEvent> q = ((SSFApplication)getActivity().getApplication()).getInstance().fireEvents;
			try {
				while (true) {
					Log.e("ssg", "emitter change consumed");
					ringView.handleFireEmitterEvent(q.take());
				}
			} catch (InterruptedException e) {
				Log.e("ssf", "consumer died");
				Log.e("ssf", e.toString());
			}
			return null;
		}
    }
    
	private void setEnableActionControls(boolean enabledPlayerControls, boolean enabledRingmasterControls) {
		this.gamemasterControls.setVisibility(enabledRingmasterControls ? View.VISIBLE : View.GONE);
		this.colorPallet.setVisibility(enabledRingmasterControls ? View.VISIBLE : View.GONE);
	}
}