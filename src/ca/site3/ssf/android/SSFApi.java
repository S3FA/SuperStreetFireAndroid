package ca.site3.ssf.android;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import ca.site3.ssf.guiprotocol.StreetFireGuiClient;

public class SSFApi {
	public static final String LOG_TAG = SSFApi.class.getName();
    StreetFireGuiClient client;
    Context context;
    
    public SSFApi(String address, int clientPort, Context context) {
    	InetAddress clientAddress = null;
		try {
			clientAddress = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		this.context = context;
		client = new StreetFireGuiClient(clientAddress, clientPort, false);
		(new ConnectTask()).execute();
    }
    
    public StreetFireGuiClient getClient() {
    	return client;
    }
    
    public void queryRefresh() {
    	if (client != null && client.isConnected()) {
    		(new GameRefreshTask()).execute();
    	}
    }
    
    private class ConnectTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... arg0) {
			try {
				client.connect();
				Log.i(LOG_TAG, "isConnected: " + client.isConnected());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
    }

	private class GameRefreshTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... arg0) {
			try {
				client.queryGameInfoRefresh();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return null;
	}
}
}