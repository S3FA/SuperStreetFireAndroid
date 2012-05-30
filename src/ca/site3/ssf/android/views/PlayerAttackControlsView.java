package ca.site3.ssf.android.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import ca.site3.ssf.gamemodel.ActionFactory.PlayerActionType;

public class PlayerAttackControlsView extends LinearLayout {
	public OnPlayerActionSelect onPlayerActionSelect;

	public PlayerAttackControlsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.setOrientation(VERTICAL);
		
		OnClickListener onAttackButtonClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onPlayerActionSelect == null) return;
				
				PlayerActionType action = (PlayerActionType) v.getTag();
				onPlayerActionSelect.onAction(action);
			}
		};
		
		for (PlayerActionType action : PlayerActionType.values()) {
			Button attackbutton = new Button(context);
			attackbutton.setText(action.toString());
			attackbutton.setTag(action);
			attackbutton.setOnClickListener(onAttackButtonClick);
			this.addView(attackbutton);
		}
	}
	
	public abstract class OnPlayerActionSelect {
		public abstract void onAction(PlayerActionType action);
	}

}