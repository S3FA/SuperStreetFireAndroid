package ca.site3.ssf.android;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ca.site3.ssf.android.R;

/**
 * Shows a dialog where you can enter the SSF server's IP and save them to settings.
 * Also asks for the application to connect.
 *
 */
public class ServerDialogFragment extends DialogFragment {
	
	SharedPreferences prefs;
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.debug, null);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
		
		getDialog().setTitle("Connect to Server");

		final TextView viewAddress = (TextView) view.findViewById(R.id.server_address);
		final TextView viewPort = (TextView) view.findViewById(R.id.server_port);
		
		viewAddress.setText(prefs.getString(SSFApplication.PREF_SERVER_ADDRESS, SSFApplication.DEFAULT_IP));
		viewPort.setText("" + prefs.getInt(SSFApplication.PREF_SERVER_PORT, SSFApplication.DEFAULT_PORT));
		
		Button button = (Button) view.findViewById(R.id.connect_button);
		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = prefs.edit();
		        int port = 0;
		        try {
		        	port = Integer.parseInt(viewPort.getText().toString());
		        } catch (Exception e) {}
		        
		        editor.putString(SSFApplication.PREF_SERVER_ADDRESS, viewAddress.getText().toString());
		        editor.putInt(SSFApplication.PREF_SERVER_PORT, port);
		        
		        editor.commit();
		        
		        Intent connect = new Intent(Intents.CONNECT);
		        getActivity().sendBroadcast(connect);
		        getDialog().cancel();
			}
		});
		
		return view;
	}

}