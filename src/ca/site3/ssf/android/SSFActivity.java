package ca.site3.ssf.android;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import ca.site3.ssf.gamemodel.IGameModelEvent;

public class SSFActivity extends Activity {
	public static final String LOG_TAG = SSFActivity.class.getName();
	
    ArenaFragment arenaDisplayFragment;
    TextFragment textFragment;
    
    View connectionStatusView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, 
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        setContentView(R.layout.main);
        
        arenaDisplayFragment = new ArenaFragment();
        textFragment = new TextFragment();
        
        final ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        bar.addTab(bar.newTab()
                .setText(R.string.tab_arena)
                .setTabListener(new TabListener(arenaDisplayFragment, "Arena")));
        bar.addTab(bar.newTab()
                .setText(R.string.tab_text)
                .setTabListener(new TabListener(textFragment, "Text")));

        if (savedInstanceState != null) {
            bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
    }

	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }
    
    private class TabListener implements ActionBar.TabListener {
        private Fragment mFragment;
        private String title;

        public TabListener(Fragment fragment, String title) {
            mFragment = fragment;
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            ft.add(R.id.viewer, mFragment, title);
            if (((SSFApplication)getApplication()).api != null) {
            	((SSFApplication)getApplication()).api.queryRefresh();
            }
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            ft.remove(mFragment);
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {}
    }
    
    public void onResume() {
    	super.onResume();
    	IntentFilter filter = new IntentFilter();
    	filter.addAction(Intents.EVENT_PLAYER_HEALTH_CHANGE);
    	filter.addAction(Intents.EVENT_PLAYER_ATTACK_ACTION);
    	filter.addAction(Intents.EVENT_PLAYER_BLOCK_ACTION);
    	filter.addAction(Intents.EVENT_TIMER_CHANGE);
    	filter.addAction(Intents.EVENT_GAME_INFO_REFRESH);
    	filter.addAction(Intents.EVENT_GAME_STATE_CHANGED);
    	registerReceiver(onGameEvent, filter);
    	filter = new IntentFilter();
    	filter.addAction(Intents.CONNECTED);
    	filter.addAction(Intents.NOT_CONNECTED);
    	registerReceiver(onConnectionStatus, filter);
    	sendBroadcast(new Intent(Intents.REFRESH));

        // Try to connect to the saved server when the application is opened
        sendBroadcast(new Intent(Intents.CONNECT));
    }
    
    public void onPause() {
    	super.onPause();
    	unregisterReceiver(onGameEvent);
    }

    private BroadcastReceiver onGameEvent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	arenaDisplayFragment.handleGameModelEvent((IGameModelEvent) intent.getExtras().get(Intents.EXTRA_EVENT));
        }
    };
    
    private BroadcastReceiver onConnectionStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	boolean connected = intent.getAction().equals(Intents.CONNECTED);
        	setConnectionStatus(connected);
        }
    };
    
    public void setConnectionStatus(boolean connected) {
    	if (connectionStatusView != null) {
    		connectionStatusView.findViewById(R.id.connection_status_color).setBackgroundColor(getResources().getColor(connected ? R.color.status_connected : R.color.status_disconnected));
    		((TextView) connectionStatusView.findViewById(R.id.connection_status_text)).setText(connected ? R.string.status_connected : R.string.status_disconnected);
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.arena, menu);
        connectionStatusView = menu.findItem(R.id.connection_status).getActionView();
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.connect:
            	DialogFragment newFragment = new ServerDialogFragment();
                newFragment.show(getFragmentManager(), "dialog");
                return true;
            case R.id.refresh:
            	sendBroadcast(new Intent(Intents.REFRESH));
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
