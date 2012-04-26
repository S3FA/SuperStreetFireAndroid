package ca.site3.ssf.android;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.EnumSet;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import ca.site3.ssf.gamemodel.IGameModel.Entity;
import ca.site3.ssf.guiprotocol.StreetFireGuiClient;

import com.superstreetfire.android.R;

public class SSFActivity extends Activity {
    ArenaDisplayFragment arenaDisplayFragment;
    ServerDebugFragment serverDebugFragment;
    PrefsFragment prefsFragment;
    
    NyanView nyanView;
    
    StreetFireGuiClient client;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        SharedPreferences prefs = getSharedPreferences("", MODE_PRIVATE);
        String test = prefs.getString("server_address", null);
        
        arenaDisplayFragment = new ArenaDisplayFragment();
        serverDebugFragment = new ServerDebugFragment();
        prefsFragment = new PrefsFragment();
        
        nyanView = new NyanView(getBaseContext(), null);
//        this.arenaDisplayFragment.addView(nyanView);
        
        final ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        bar.addTab(bar.newTab()
                .setText(R.string.tab_arena)
                .setTabListener(new TabListener(arenaDisplayFragment, "Arena")));
        bar.addTab(bar.newTab()
                .setText(R.string.tab_debug)
                .setTabListener(new TabListener(serverDebugFragment, "Debug")));
        
        bar.addTab(bar.newTab()
                .setText(R.string.tab_prefs)
                .setTabListener(new TabListener(prefsFragment, "Prefs")));

        if (savedInstanceState != null) {
            bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
        
        connectToServer();
    }

    private void connectToServer() {
        InetAddress clientAddress = null;
        int clientPort = 31337;
		try {
			clientAddress = InetAddress.getByName("192.168.1.8");
		} catch (UnknownHostException e) {
			Log.e("superstreetfire", "", e);
		}
		client = new StreetFireGuiClient(clientAddress, clientPort);
		
		try {
			client.connect();
		} catch (IOException e) {
			Log.e("superstreetfire", "Could not connect to IOServer", e);
		}
		
		Log.e("superstreetfire", "isConnected: " + client.isConnected());
		
        EnumSet<Entity> contributors = EnumSet.of(Entity.PLAYER1_ENTITY);
//        try {
//			client.activateEmitter(ca.site3.ssf.gamemodel.FireEmitter.Location.LEFT_RAIL, 0, 1, contributors);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
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
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            ft.remove(mFragment);
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {}
    }
}