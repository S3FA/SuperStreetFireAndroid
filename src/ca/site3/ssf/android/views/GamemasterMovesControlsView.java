package ca.site3.ssf.android.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import ca.site3.ssf.android.R;
import ca.site3.ssf.gamemodel.ActionFactory.ActionType;;

public class GamemasterMovesControlsView extends LinearLayout {
	public OnPlayerActionSelect onPlayerActionSelect;

	public GamemasterMovesControlsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.setOrientation(VERTICAL);
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View view = inflater.inflate(R.layout.gamemaster_moves, null);
		addView(view);
		
		int[] ids = {R.id.half_left,
				R.id.half_right,
				R.id.jab_left,
				R.id.jab_right,
				R.id.eruption,
				R.id.hadouken,
				R.id.random};
		
		MoveDescription[] moves = {
				new MoveDescription(ActionType.RINGMASTER_HALF_RING_ACTION, true, false),
				new MoveDescription(ActionType.RINGMASTER_HALF_RING_ACTION, false, true),
				new MoveDescription(ActionType.RINGMASTER_JAB_ACTION, true, false),
				new MoveDescription(ActionType.RINGMASTER_JAB_ACTION, false, true),
				new MoveDescription(ActionType.RINGMASTER_ERUPTION_ACTION, true, true),
				new MoveDescription(ActionType.RINGMASTER_HADOUKEN_ACTION, true, true),
				new MoveDescription(ActionType.RINGMASTER_DRUM_ACTION, true, true)
		};
		
		OnClickListener buttonClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				MoveDescription move = (MoveDescription)v.getTag();
				if (onPlayerActionSelect != null)
					onPlayerActionSelect.onAction(move.action, move.leftHand, move.rightHand);
			}
		};
		
		for (int i = 0; i < moves.length; i++) {
			View button = findViewById(ids[i]);
			button.setTag(moves[i]);
			button.setOnClickListener(buttonClick);
		}
	}
	
	class MoveDescription {
		public ActionType action;
		public boolean leftHand;
		public boolean rightHand;
		public MoveDescription(ActionType action, boolean leftHand, boolean rightHand) {
			this.action = action;
			this.leftHand = leftHand;
			this.rightHand = rightHand;
		}
	}
	
	public abstract class OnPlayerActionSelect {
		public abstract void onAction(ActionType action, boolean leftHand, boolean rightHand);
	}
}