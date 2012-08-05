package ca.site3.ssf.android;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import ca.site3.ssf.guiprotocol.StreetFireGuiClient;

public class SSFApi {
	public static final String LOG_TAG = SSFApi.class.getName();
    StreetFireGuiClient client;
    
    public SSFApi(String address, int clientPort) {
    	InetAddress clientAddress = null;
		try {
			clientAddress = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		client = new StreetFireGuiClient(clientAddress, clientPort);
		(new ConnectTask()).execute();
    }
    
    public StreetFireGuiClient getClient() {
    	return client;
    }
    
    public void queryRefresh() {
    	Log.e("ssf", "queryRefresh()");
    	if (client != null && client.isConnected()) {
    		(new GameRefreshTask()).execute();
    	}
    }
    
    private class ConnectTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... arg0) {
			try {
				client.connect();
				Log.e(LOG_TAG, "isConnected: " + client.isConnected());
				// FIXME need user feedback on connection status
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(Void nothing) {
			
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