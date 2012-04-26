package ca.site3.ssf.android;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.superstreetfire.android.R;

public class NyanView extends LinearLayout {
	
	public NyanView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.setOrientation(VERTICAL);
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View view = inflater.inflate(R.layout.nyan, null);
		
		this.addView(view);
	}
}