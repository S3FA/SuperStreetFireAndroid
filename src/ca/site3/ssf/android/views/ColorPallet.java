package ca.site3.ssf.android.views;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import ca.site3.ssf.android.R;

public class ColorPallet extends LinearLayout {
	
	int[] colors = {R.color.ringmaster, R.color.player_one, R.color.player_two};
	int[] colorNames = {R.string.ringmaster, R.string.player_one, R.string.player_two};
	ViewRow[] views;
	
	ToggleButton buttonToggleDrawMode;
	
	public OnColorSelect onColorSelect;
	public OnDrawModeChange onDrawModeChange;

	public ColorPallet(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.setOrientation(VERTICAL);
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View view = inflater.inflate(R.layout.color_pallet_button, null);
		
		buttonToggleDrawMode = (ToggleButton) view.findViewById(R.id.toggle_pallet);
		buttonToggleDrawMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onDrawModeChange == null) return;
				onDrawModeChange.onModeChange(((ToggleButton)v).isChecked());
			}
		});
		
		View.OnClickListener colorClick = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onColorSelect == null) return;
				onColorSelect.onSelect((Integer)v.getTag());
				for (ViewRow view : views) {
					view.colorName.setTextColor(getResources().getColor(R.color.grey));
				}
				((TextView)v.findViewById(R.id.name)).setTextColor(getResources().getColor(R.color.black));
			}
		};
		
		views = new ViewRow[3];
		
		for (int i = 0; i < colors.length; i++) {
			Paint paint = new Paint();
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(getResources().getColor(colors[i]));
			
			view = inflater.inflate(R.layout.color_pallet, null);
			
			views[i] = new ViewRow();
			views[i].color = view.findViewById(R.id.color);
			views[i].colorName = ((TextView) view.findViewById(R.id.name));
			
			views[i].color.setBackgroundColor(getResources().getColor(colors[i]));
			views[i].colorName.setText(colorNames[i]);
			
			view.setTag(colors[i]);
			view.setOnClickListener(colorClick);
			
			this.addView(view);
		}
		
		this.addView(buttonToggleDrawMode);
	}
	
	private class ViewRow {
		View color;
		TextView colorName;
	}
	
	public abstract class OnColorSelect {
		public abstract void onSelect(int newColor);
	}
	
	public abstract class OnDrawModeChange {
		public abstract void onModeChange(boolean isInDrawMode);
	}
}