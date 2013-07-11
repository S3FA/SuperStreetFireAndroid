package ca.site3.ssf.android;

import java.io.IOException;
import java.net.InetAddress;
import java.util.EnumSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import ca.site3.ssf.gamemodel.ActionFactory;
import ca.site3.ssf.gamemodel.FireEmitter;
import ca.site3.ssf.gamemodel.FireEmitterChangedEvent;
import ca.site3.ssf.gamemodel.GameState.GameStateType;
import ca.site3.ssf.gamemodel.IGameModel.Entity;
import ca.site3.ssf.gamemodel.IGameModelEvent;

public class SSFApplication extends Application {
	private static String LOG_TAG = SSFApplication.class.getName();

	public SSFApi api;
	EventTask task;

	int connectionHeartbeatTimout = 500;

	public static final String PREFS = "ssf_prefs";
	public static final String PREF_SERVER_ADDRESS = "server_address";
	public static final String PREF_SERVER_PORT = "server_port";
	public static final int DEFAULT_PORT = 31337;
	public static final String DEFAULT_IP = "192.168.100.2";
	
	/** timeout for the connection status */
	PendingIntent pintent;

	BlockingQueue<FireEmitterChangedEvent> fireEvents = new ArrayBlockingQueue<FireEmitterChangedEvent>(1024);

	SSFApplication instance;

	public SSFApplication getInstance() {
		if (instance == null)
			instance = this;
		return instance;
	}

	private class EventTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			Log.i(LOG_TAG, "EventTask started");
			while (true) {
				try {
					IGameModelEvent event = api.getClient().getEventQueue().take();
					handleGameModelEvent(event);
				} catch (InterruptedException ex) {
					Log.e(LOG_TAG,
							"DevGUI interrupted while waiting for game model event");
					ex.printStackTrace();
				}
			}
		}
	}

	private void handleGameModelEvent(final IGameModelEvent event) {
		Intent intent = null;
		sendBroadcast(new Intent(Intents.CONNECTED));
		switch (event.getType()) {
		case GAME_INFO_REFRESH:
			intent = new Intent(Intents.EVENT_GAME_INFO_REFRESH);
			break;
		// case 545444
		case GAME_STATE_CHANGED:
			intent = new Intent(Intents.EVENT_GAME_STATE_CHANGED);
			break;
		case MATCH_ENDED:
			// DevGUIMainWindow.this.onMatchEnded((MatchEndedEvent)event);
			break;
		case PLAYER_ATTACK_ACTION:
			intent = new Intent(Intents.EVENT_PLAYER_ATTACK_ACTION);
			break;
		case PLAYER_BLOCK_ACTION:
			intent = new Intent(Intents.EVENT_PLAYER_BLOCK_ACTION);
			break;
		case PLAYER_HEALTH_CHANGED:
			intent = new Intent(Intents.EVENT_PLAYER_HEALTH_CHANGE);
			break;
		case PLAYER_ACTION_POINTS_CHANGED:
			intent = new Intent(Intents.EVENT_PLAYER_ACTION_POINTS_CHANGED);
			break;
		case ROUND_ENDED:
			intent = new Intent(Intents.EVENT_ROUND_ENDED);
			break;
		case ROUND_BEGIN_TIMER_CHANGED:
		case ROUND_PLAY_TIMER_CHANGED:
			intent = new Intent(Intents.EVENT_TIMER_CHANGE);
			break;
		case FIRE_EMITTER_CHANGED:
			fireEvents.offer((FireEmitterChangedEvent) event);
		default:
			break;
		}
		if (intent != null) {
			intent.putExtra(Intents.EXTRA_EVENT, event);
			sendBroadcast(intent);
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		IntentFilter filter = new IntentFilter(Intents.CONNECT);
		registerReceiver(onConnect, filter);
		filter = new IntentFilter(Intents.REFRESH);
		registerReceiver(onRefresh, filter);
		filter = new IntentFilter();
		filter.addAction(Intents.INIT_NEXT);
		filter.addAction(Intents.KILL_GAME);
		filter.addAction(Intents.PAUSE_TOGGLE);
		filter.addAction(Intents.TEST_SYSTEM);
		registerReceiver(onInitState, filter);
		filter = new IntentFilter(Intents.EVENT_TOUCH_FIRE_EMITTER);
		registerReceiver(onFlameTouch, filter);
		filter = new IntentFilter(Intents.EVENT_GAMEMASTER_MOVE);
		registerReceiver(onGamemasterAction, filter);
		registerReceiver(checkConnectionStatus, new IntentFilter(
				Intents.CHECK_CONNECTION_STATUS));
	}
	
	public void startTimeout() {
		if (pintent == null) {
			pintent = PendingIntent.getBroadcast(this, 0, new Intent(
					Intents.CHECK_CONNECTION_STATUS), 0);
			AlarmManager manager = (AlarmManager) (this
					.getSystemService(Context.ALARM_SERVICE));
			manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + connectionHeartbeatTimout,
					connectionHeartbeatTimout, pintent);
		}
	}
	
	public void stopTimeout() {
		AlarmManager manager = (AlarmManager) (this
				.getSystemService(Context.ALARM_SERVICE));
		manager.cancel(pintent);
		pintent = null;
	}

	private BroadcastReceiver checkConnectionStatus = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			sendBroadcast(new Intent(Intents.REFRESH));
			
			if (api != null && api.client != null) {
				// FIXME api.client.isConnected() will return true even if the
				// server is closed
				if (api.client.isConnected()) {
					sendBroadcast(new Intent(Intents.CONNECTED));
				} else {
					sendBroadcast(new Intent(Intents.NOT_CONNECTED));
				}
			} else {
				sendBroadcast(new Intent(Intents.NOT_CONNECTED));
			}
		}
	};

	private BroadcastReceiver onConnect = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(SSFApplication.this);
				String address = prefs.getString(PREF_SERVER_ADDRESS, DEFAULT_IP);
				if (address.length() == 0)
					address = DEFAULT_IP;
				int port = prefs.getInt(PREF_SERVER_PORT, DEFAULT_PORT);
				if (port == 0)
					port = DEFAULT_PORT;
				Log.e(LOG_TAG, "address: " + InetAddress.getByName(address) + " port: "
						+ port);
				api = new SSFApi(address, port, getBaseContext());
				api.queryRefresh();
				if (api.client.isConnected()) {
					sendBroadcast(new Intent(Intents.CONNECTED));
				} else {
					sendBroadcast(new Intent(Intents.NOT_CONNECTED));
				}
			} catch (Exception e) {
				e.printStackTrace();
				sendBroadcast(new Intent(Intents.NOT_CONNECTED));
			}

			fireEvents.clear();
			task = new EventTask();
			task.execute();
		}
	};

	private BroadcastReceiver onInitState = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				if (api != null && api.client != null) {
					if (intent.getAction() == Intents.INIT_NEXT) {
						GameStateType nextState = (GameStateType) intent.getExtras().get(
								Intents.EXTRA_STATE);
						api.client.initiateNextState(nextState);
					} else if (intent.getAction() == Intents.PAUSE_TOGGLE) {
						api.client.togglePauseGame();
					} else if (intent.getAction() == Intents.KILL_GAME) {
						api.client.killGame();
					} else if (intent.getAction() == Intents.TEST_SYSTEM) {
						api.client.testSystem();
					} else if (intent.getAction() == Intents.STOP) {
						// FIXME actually ask the server to stop
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private BroadcastReceiver onFlameTouch = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				Bundle extras = intent.getExtras();
				EnumSet<Entity> contributors = (EnumSet<Entity>) extras
						.get(Intents.EXTRA_TOUCH_CONTRIBUTORS);
				FireEmitter.Location location = (FireEmitter.Location) extras
						.get(Intents.EXTRA_FIRE_EMITTER_LOCATION);
				int index = extras.getInt(Intents.EXTRA_FIRE_EMITTER_INDEX);
				if (api != null && api.client != null && api.client.isConnected()) {
					api.client.activateEmitter(location, index, 1, contributors);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	private BroadcastReceiver onGamemasterAction = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				Bundle extras = intent.getExtras();
				ActionFactory.ActionType action = (ActionFactory.ActionType) extras
						.get(Intents.EXTRA_ACTION_TYPE);
				boolean leftHand = extras.getBoolean(Intents.EXTRA_LEFT_HAND);
				boolean rightHand = extras.getBoolean(Intents.EXTRA_RIGHT_HAND);
				if (api != null && api.client != null && api.client.isConnected()) {
					api.client.executeRingmasterAction(action, leftHand, rightHand);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	private BroadcastReceiver onRefresh = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (api != null) {
				api.queryRefresh();
			}
		}
	};
}
