package ca.site3.ssf.android;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class TextFragment extends Fragment {
	WebView text;
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.text, null);

		text = (WebView)view.findViewById(R.id.text);
		text.loadUrl("file:///android_asset/text.html"); 
		
		return view;
	}

}