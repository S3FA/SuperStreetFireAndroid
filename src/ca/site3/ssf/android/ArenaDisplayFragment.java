package ca.site3.ssf.android;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.superstreetfire.android.R;

public class ArenaDisplayFragment extends Fragment {
	
	RingView ringView;
	ColorPallet colorPallet;
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.arena, null);
		
		ringView = (RingView) view.findViewById(R.id.ring_view);
		ringView.invalidate();
		
		colorPallet = (ColorPallet) view.findViewById(R.id.color_pallet);
		colorPallet.onColorSelect = colorPallet.new OnColorSelect() {
			@Override
			public void onSelect(int newColor) {
				ringView.setColor(newColor);
			}
		};
		colorPallet.onDrawModeChange = colorPallet.new OnDrawModeChange() {
			@Override
			public void onModeChange(boolean isInDrawMode) {
				ringView.isInDrawMode = isInDrawMode;
			}
		};
		
		return view;
	}

}