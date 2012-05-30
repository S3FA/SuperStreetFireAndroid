package ca.site3.ssf.android;

import java.io.IOException;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ca.site3.ssf.android.views.ColorPallet;
import ca.site3.ssf.android.views.GameControlsView;
import ca.site3.ssf.android.views.PlayerAttackControlsView;
import ca.site3.ssf.android.views.RingView;
import ca.site3.ssf.gamemodel.ActionFactory.PlayerActionType;

public class ArenaFragment extends Fragment {
	
	RingView ringView;
	ColorPallet colorPallet;
	PlayerAttackControlsView playerOneAttackControls;
	PlayerAttackControlsView playerTwoAttackControls;
	GameControlsView gameControlsView;
	
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
		
		playerOneAttackControls = (PlayerAttackControlsView) view.findViewById(R.id.player_1_attack_controls);
		playerOneAttackControls.onPlayerActionSelect = playerOneAttackControls.new OnPlayerActionSelect() {
			@Override
			public void onAction(PlayerActionType action) {
				try {
					// TODO why do I need to know the handedness of a gesture?
					((SSFActivity)getActivity()).ssfApi.getClient().executePlayerAction(1, action, false, false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		playerTwoAttackControls = (PlayerAttackControlsView) view.findViewById(R.id.player_2_attack_controls);
		playerTwoAttackControls.onPlayerActionSelect = playerTwoAttackControls.new OnPlayerActionSelect() {
			@Override
			public void onAction(PlayerActionType action) {
				try {
					// TODO why do I need to know the handedness of a gesture?
					((SSFActivity)getActivity()).ssfApi.getClient().executePlayerAction(2, action, false, false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		gameControlsView = (GameControlsView) view.findViewById(R.id.game_controls);
		gameControlsView.onPlayerActionSelect = gameControlsView.new OnPlayerActionSelect() {
			@Override
			public void onAction() {
				try {
					((SSFActivity)getActivity()).ssfApi.getClient().initiateNextState();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		return view;
	}

}