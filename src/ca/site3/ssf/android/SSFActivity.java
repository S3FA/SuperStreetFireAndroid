package ca.site3.ssf.android;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SSFActivity extends Activity {
    ArenaFragment arenaDisplayFragment;
    ServerFragment serverDebugFragment;
    
    public SSFApi ssfApi;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        SharedPreferences prefs = getPreferences(Activity.MODE_PRIVATE);
        String address = prefs.getString("server_address", "10.0.1.3");
        int port = prefs.getInt("server_port", 31337);
        
        ssfApi = new SSFApi(address, port);
        
        arenaDisplayFragment = new ArenaFragment();
        serverDebugFragment = new ServerFragment();
        
        final ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        bar.addTab(bar.newTab()
                .setText(R.string.tab_arena)
                .setTabListener(new TabListener(arenaDisplayFragment, "Arena")));
        bar.addTab(bar.newTab()
                .setText(R.string.tab_server)
                .setTabListener(new TabListener(serverDebugFragment, "Server")));

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
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            ft.remove(mFragment);
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {}
    }
}
