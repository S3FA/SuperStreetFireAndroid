package ca.site3.ssf.android;


public class Intents {
	private static final String PREFIX = "ca.site3.ssf.android.intents.";
	private static final String PREFIX_EXTRA = "ca.site3.ssf.android.intents.extras.";
	
	public static final String PLAYER_ACTION = PREFIX + "PLAYER_ACTION";
	public static final String INIT_NEXT = PREFIX + "INIT_NEXT_STEP";
	public static final String INIT_NEXT_1 = PREFIX + "INIT_NEXT_1";
	public static final String INIT_NEXT_2 = PREFIX + "INIT_NEXT_2";
	public static final String KILL_GAME = PREFIX + "KILL_GAME";
	public static final String PAUSE_TOGGLE = PREFIX + "PAUSE_TOGGLE";
	public static final String TEST_SYSTEM = PREFIX + "TEST_SYSTEM";
	public static final String STOP = PREFIX + "STOP";
	public static final String CONNECT = PREFIX + "CONNECT";
	public static final String CONNECTED = PREFIX + "CONNECTED";
	public static final String NOT_CONNECTED = PREFIX + "NOT_CONNECTED";
	public static final String CHECK_CONNECTION_STATUS = PREFIX + "CHECK_CONNECTION_STATUS";
	public static final String REFRESH = PREFIX + "REFRESH";
	public static final String TOUCH_EMITTER = PREFIX + "TOUCH_EMITTER";
	public static final String EVENT_FIRE_EMITTER_CHANGED = PREFIX + "FIRE_EMITTER_CHANGED";
	public static final String EVENT_PLAYER_HEALTH_CHANGE = PREFIX + "EVENT_PLAYER_HEALTH_CHANGE";
	public static final String EVENT_TIMER_CHANGE = PREFIX + "EVENT_TIMER_CHANGE";
	public static final String EVENT_ROUND_ENDED = PREFIX + "EVENT_ROUND_ENDED";
	public static final String EVENT_TOUCH_FIRE_EMITTER = PREFIX + "EVENT_TOUCH_FIRE_EMITTER";
	public static final String EVENT_GAME_INFO_REFRESH = PREFIX + "EVENT_GAME_INFO_REFRESH";
	public static final String EVENT_GAMEMASTER_MOVE = PREFIX + "EVENT_GAMEMASTER_MOVE";
	public static final String EVENT_PLAYER_ATTACK_ACTION = PREFIX + "EVENT_PLAYER_ATTACK_ACTION";
	public static final String EVENT_PLAYER_BLOCK_ACTION = PREFIX + "EVENT_PLAYER_BLOCK_ACTION";
	public static final String EVENT_GAME_STATE_CHANGED = PREFIX + "GAME_STATE_CHANGED";
	
	public static final String EXTRA_PLAYER_NUM = PREFIX_EXTRA + "PLAYER_NUM";
	public static final String EXTRA_ACTION_TYPE = PREFIX_EXTRA + "EXTRA_ACTION_TYPE";
	public static final String EXTRA_EVENT = PREFIX_EXTRA + "EXTRA_EVENT";
	public static final String EXTRA_TOUCH_CONTRIBUTORS = PREFIX_EXTRA + "EXTRA_TOUCH_CONTRIBUTORS";
	public static final String EXTRA_FIRE_EMITTER_LOCATION = PREFIX_EXTRA + "EXTRA_FIRE_EMITTER_LOCATION";
	public static final String EXTRA_FIRE_EMITTER_INDEX = PREFIX_EXTRA + "EXTRA_FIRE_EMITTER_INDEX";
	public static final String EXTRA_RIGHT_HAND = PREFIX + "EXTRA_RIGHT_HAND";
	public static final String EXTRA_LEFT_HAND = PREFIX + "EXTRA_LEFT_HAND";
	public static final String EXTRA_STATE = PREFIX + "EXTRA_STATE";
}