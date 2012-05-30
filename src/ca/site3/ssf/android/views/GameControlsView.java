package ca.site3.ssf.android.views;

import ca.site3.ssf.android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class GameControlsView extends LinearLayout {
	public OnPlayerActionSelect onPlayerActionSelect;

	public GameControlsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.setOrientation(HORIZONTAL);
		
		OnClickListener onAttackButtonClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onPlayerActionSelect == null) return;
				onPlayerActionSelect.onAction();
			}
		};
		
		Button button = new Button(context);
		button.setText(R.string.next_state);
		button.setOnClickListener(onAttackButtonClick);
		this.addView(button);

	}
	
	public abstract class OnPlayerActionSelect {
		public abstract void onAction();
	}

}