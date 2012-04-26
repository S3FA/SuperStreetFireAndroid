package ca.site3.ssf.android;

import com.superstreetfire.android.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ServerDebugFragment extends Fragment {
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.debug, null);
		
		return view;
	}

}