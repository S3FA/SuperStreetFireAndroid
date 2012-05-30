package ca.site3.ssf.android;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.site3.ssf.android.R;

public class ServerFragment extends Fragment {
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.debug, null);
		
		return view;
	}

}