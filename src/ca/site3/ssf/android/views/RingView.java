package ca.site3.ssf.android.views;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import ca.site3.ssf.android.R;
import ca.site3.ssf.gamemodel.FireEmitter.Location;
import ca.site3.ssf.gamemodel.FireEmitter;
import ca.site3.ssf.gamemodel.FireEmitterChangedEvent;
import ca.site3.ssf.gamemodel.IGameModel.Entity;
import ca.site3.ssf.guiprotocol.Event.GameEvent.FireEmitterType;

/**
 * Displays the Ring and player health bars
 * 
 * @author kate
 *
 */
public class RingView extends SurfaceView {
	static int num_effects_in_ring = 16;
	static int num_row_effect_length = 8;
	
	List<Emitter> emitters;

	int ringX;
	int ringY;
	int ringD = 150;
	
	Paint arenaBackground;
	Paint paintHealthBackground;
	Paint paintHealthText;
	Paint paintOtherBackground;
	Paint paintHealth;
	Paint paintFocus;
	Paint paintMeditation;
	
	DecimalFormat percent = new DecimalFormat("#%");
	
	public float playerHealth[] = {(float) 0, (float) 0};
	public float playerFocus[] = {(float) 0, (float) 0};
	public float playerMeditation[] = {(float) 0, (float) 0};
	
	int selectedColor = R.color.ringmaster;
	
	/*
	 * the radius of a fire effect on screen
	 */
	int emitterRadius = 10;
	
	// Does touching an emitter cause it to fire?
	public boolean isInDrawMode = false;
	
	public OnEmitterTouch onEmitterTouch;
	
	public RingView(Context context) {
		super(context);
		setupPaints();
	}
	
	public RingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupPaints();
	}

	public RingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setupPaints();
	}
	
	public void setColor(int color) {
		selectedColor = color;
	}
	
	public void createEmitters() {
		float angle = (float) ((2 * Math.PI) / num_effects_in_ring);
		emitters = new ArrayList<RingView.Emitter>();
		
		float ring_offset = (float)-0.5;
		
		for (int i = 0; i < num_effects_in_ring; i++) {
			Emitter newEmitter = new Emitter((int) (ringD * Math.cos((-i + ring_offset) * angle)), (int) (ringD * Math.sin((-i + ring_offset) * angle)));
			newEmitter.location = Location.OUTER_RING;
			newEmitter.locationIndex = i;
			emitters.add(newEmitter);
		}
		
		int rows_y_offset = ringD / 3;
		int rows_x_offset = ringD / 6;
		int index = 0;
		for (int i = -1 * (num_row_effect_length / 2); i < num_row_effect_length / 2; i++) {
			Emitter new_ring_flame = new Emitter(-i * rows_x_offset - (rows_x_offset / 2), -1 * rows_y_offset);
			new_ring_flame.location = Location.RIGHT_RAIL;
			new_ring_flame.locationIndex = index;
			emitters.add(new_ring_flame);
			new_ring_flame = new Emitter(-i * rows_x_offset - (rows_x_offset / 2), rows_y_offset);
			new_ring_flame.location = Location.LEFT_RAIL;
			new_ring_flame.locationIndex = index;
			emitters.add(new_ring_flame);
			index++;
		}
	}
	
	public static FireEmitter.Location getEmitterLocation(int index) {
		if (index < num_effects_in_ring) {
			return FireEmitter.Location.OUTER_RING;
		}
		if (index % 2 == 0) return FireEmitter.Location.LEFT_RAIL;
		return FireEmitter.Location.RIGHT_RAIL;
	}
	
	public static int getEmitterIndex(int index) {
		if (index < num_effects_in_ring) {
			return index;
		}
		return (index - num_effects_in_ring) / 2;
	}
	
	public void setupPaints() {
		arenaBackground = new Paint();
		arenaBackground.setColor(getResources().getColor(R.color.arena_bg));
		
		paintHealthBackground = new Paint();
		paintHealthBackground.setColor(getResources().getColor(R.color.healthbar_bg));
		paintHealthBackground.setAntiAlias(true);
		
		paintHealthText = new Paint();
		paintHealthText.setColor(getResources().getColor(R.color.healthbar_text));
		paintHealthText.setTextSize(35);
		
		paintHealth = new Paint();
		paintHealth.setColor(getResources().getColor(R.color.healthbar));
		paintHealth.setAntiAlias(true);
		
		paintFocus = new Paint();
		paintFocus.setColor(getResources().getColor(R.color.focusbar));
		paintFocus.setAntiAlias(true);
		
		paintMeditation = new Paint();
		paintMeditation.setColor(getResources().getColor(R.color.meditationbar));
		paintMeditation.setAntiAlias(true);
		
		paintOtherBackground = new Paint();
		paintOtherBackground.setColor(getResources().getColor(R.color.other_bg));
		paintOtherBackground.setAntiAlias(true);
	}
	
	@Override
	public void draw(Canvas canvas) {
		// center the ring
		ringX = getWidth() / 2;
		ringY = (int) Math.floor((getHeight() / 2 + (Math.min(getHeight(), getWidth()) * .055)));
		
		// The ring is 85% of the min length
		ringD = (int) (Math.min(getHeight(), getWidth()) * .85) / 2;
		
		// this generally looks pretty sane
		emitterRadius = ringD / num_effects_in_ring;
		
		if (emitters == null) {
			createEmitters();
		}
		
		canvas.drawRect(0, 0, getWidth(), getHeight(), arenaBackground);

		drawHealthbars(canvas);
		
		Paint paintEmitterOutline = new Paint();
		paintEmitterOutline.setColor(getResources().getColor(R.color.emitter_outline));
		paintEmitterOutline.setStyle(Paint.Style.STROKE);
		paintEmitterOutline.setAntiAlias(true);
		
		Paint paintEmitterFill = new Paint();
		paintEmitterFill.setStyle(Paint.Style.FILL);
		paintEmitterFill.setColor(getResources().getColor(selectedColor));
		
		// draw the emitters
		for (Emitter emitter : emitters) {
			paintEmitterFill.setAlpha((int) (255 * emitter.intensity));
			canvas.drawCircle(ringX + emitter.x, ringY + emitter.y, emitterRadius, paintEmitterFill);
			if (emitter.touching) {
				paintEmitterOutline.setStrokeWidth(2);
			} else {
				paintEmitterOutline.setStrokeWidth(1);
			}
			canvas.drawCircle(ringX + emitter.x, ringY + emitter.y, emitterRadius, paintEmitterOutline);
		}
	}
	
	private void drawHealthbars(Canvas canvas) {
		float healthbarWidth = (float) (getWidth() * .35);
		float healthbarHeigth = (float) ((float) healthbarWidth * .1);
		float otherbarHeight = (float) ((float) healthbarHeigth * .4);
		
		Path path = new Path();
		
		// draw the left healthbar background
		path.moveTo(0, 0);
		path.lineTo(healthbarWidth, 0);
		path.lineTo(healthbarWidth, healthbarHeigth);
		path.lineTo(0, healthbarHeigth);
		canvas.drawPath(path, paintHealthBackground);
		
		// draw the left focus
		path = new Path();
		path.moveTo(0, healthbarHeigth);
		path.lineTo(healthbarWidth * playerFocus[1], healthbarHeigth);
		path.lineTo(healthbarWidth * playerFocus[1], healthbarHeigth + otherbarHeight);
		path.lineTo(0, healthbarHeigth + otherbarHeight);
		canvas.drawPath(path, paintFocus);
		
		// draw the left meditation
		path = new Path();
		path.moveTo(0, healthbarHeigth + otherbarHeight);
		path.lineTo(healthbarWidth * playerMeditation[1], healthbarHeigth + otherbarHeight);
		path.lineTo(healthbarWidth * playerMeditation[1], healthbarHeigth + otherbarHeight * 2);
		path.lineTo(0, healthbarHeigth + otherbarHeight * 2);
		canvas.drawPath(path, paintMeditation);
		
		// draw the left healthbar health
		path = new Path();
		path.moveTo(0, 0);
		path.lineTo((healthbarWidth * playerHealth[1] / 100), 0);
		path.lineTo((healthbarWidth * playerHealth[1] / 100), healthbarHeigth);
		path.lineTo(0, healthbarHeigth);
		canvas.drawPath(path, paintHealth);
		canvas.drawText(percent.format(playerHealth[1] / 100), (healthbarWidth / 2) - 20 , healthbarHeigth - (healthbarHeigth / 4), paintHealthText);
		
		// draw the right healthbar background
		path = new Path();
		path.moveTo(getWidth(), 0);
		path.lineTo(getWidth() - healthbarWidth, 0);
		path.lineTo(getWidth() - healthbarWidth, healthbarHeigth);
		path.lineTo(getWidth(), healthbarHeigth);
		canvas.drawPath(path, paintHealthBackground);
		
		// draw the right other bars background
		path = new Path();
		path.moveTo(getWidth(), healthbarHeigth);
		path.lineTo(getWidth() - healthbarWidth * playerFocus[0], healthbarHeigth);
		path.lineTo(getWidth() - healthbarWidth * playerFocus[0], healthbarHeigth + otherbarHeight);
		path.lineTo(getWidth(), healthbarHeigth + otherbarHeight);
		canvas.drawPath(path, paintFocus);
		
		// draw the right other bars background
		path = new Path();
		path.moveTo(getWidth(), healthbarHeigth + otherbarHeight);
		path.lineTo(getWidth() - healthbarWidth * playerMeditation[0], healthbarHeigth + otherbarHeight);
		path.lineTo(getWidth() - healthbarWidth * playerMeditation[0], healthbarHeigth + otherbarHeight * 2);
		path.lineTo(getWidth(), healthbarHeigth + otherbarHeight * 2);
		canvas.drawPath(path, paintMeditation);
		
		// draw the right healthbar health
		path = new Path();
		path.moveTo(getWidth(), 0);
		path.lineTo(getWidth() - healthbarWidth * playerHealth[0] / 100, 0);
		path.lineTo(getWidth() - healthbarWidth * playerHealth[0] / 100, healthbarHeigth);
		path.lineTo(getWidth(), healthbarHeigth);
		canvas.drawPath(path, paintHealth);
		canvas.drawText(percent.format(playerHealth[0] / 100), getWidth() - (healthbarWidth / 2) - 20 , healthbarHeigth - (healthbarHeigth / 4), paintHealthText);
	}
	
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isInDrawMode) return true; // touching an emitter does nothing

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
            	for (int p = 0; p < event.getPointerCount(); p++) {
                    touch_move(event.getX(p), event.getY(p));
                }
                break;
            case MotionEvent.ACTION_UP:
            	touch_up();
            	break;
        }
        
        this.postInvalidate();
        
        return true;
    }
    
    private void touch_move(float x, float y) {
    	//
    	int emitterTouchRadius = (int) (emitterRadius * 2.5);
		for (Emitter emitter : emitters) {
			if (Math.sqrt(square(x - emitter.x - ringX) + square(y - emitter.y - ringY)) < emitterTouchRadius) {
				// FIXME the touch should be sending an event to the server, not changing how the ring is drawn directly
				emitter.touching = true;
				if (onEmitterTouch != null)
					onEmitterTouch.onEmitterTouch(emitter, emitter.location, emitter.locationIndex);
			}
		}
    }
    
    private void touch_up() {
		for (Emitter emitter : emitters) {
			emitter.touching = false;
		}
    }
    
    private float square(float i) {
    	return i * i;
    }
    
    // FIXME use emitter from GUIProtocol
	public class Emitter {
		int x;
		int y;
		float intensity = 0;
		int locationIndex;
		boolean touching = false;
		FireEmitter.Location location;
		
		public Emitter(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	public void handleFireEmitterEvent(FireEmitterChangedEvent event) {
		if (emitters == null) return;
		Emitter targetEmitter = null;
		switch (event.getLocation()) {
		case OUTER_RING:
			targetEmitter = emitters.get(event.getIndex());
			break;
		case LEFT_RAIL:
			targetEmitter = emitters.get(num_effects_in_ring + event.getIndex() * 2 + 1);
			break;
		case RIGHT_RAIL:
			targetEmitter = emitters.get(num_effects_in_ring + event.getIndex() * 2);
			break;
		}
		if (targetEmitter != null) {
			targetEmitter.intensity = 0;
			for (Entity entity : event.getContributingEntities()) {
				targetEmitter.intensity = Math.max(targetEmitter.intensity, event.getIntensity(entity));
			}
		}
		this.postInvalidate();
	}
	
	public abstract class OnEmitterTouch {
		public abstract void onEmitterTouch(Emitter emitter, FireEmitter.Location location, int index);
	}
}