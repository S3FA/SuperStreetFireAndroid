package ca.site3.ssf.android.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import ca.site3.ssf.android.R;

/**
 * Displays the Ring and player health bars
 * 
 * @author kate
 *
 */
public class RingView extends SurfaceView {
	int num_effects_in_ring = 16;
	int num_row_effect_length = 8;

	List<Emitter> emitters;

	int ringX;
	int ringY;
	int ringD = 150;
	
	Paint arenaBackground;
	Paint paintHealthBackground;
	Paint paintHealth;
	
	int selectedColor = R.color.ringmaster;
	
	/*
	 * the radius of a fire effect on screen
	 */
	int emitterRadius = 10;
	
	// Does touching an emitter cause it to fire?
	public boolean isInDrawMode = false;
	
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
	
	// FIXME move get the emitters from the server
	public void createEmitters() {
		float angle = (float) ((2 * Math.PI) / num_effects_in_ring);
		emitters = new ArrayList<RingView.Emitter>();
		
		for (int i = 0; i < num_effects_in_ring; i++) {
			Emitter newEmitter = new Emitter((int) (ringD * Math.cos((i + .5) * angle)), (int) (ringD * Math.sin((i + .5) * angle)));
			emitters.add(newEmitter);
		}
		
		int rows_y_offset = ringD / 6;
		int rows_x_offset = ringD / 6;
		for (int i = -1 * (num_row_effect_length / 2); i < num_row_effect_length / 2; i++) {
			Emitter new_ring_flame = new Emitter(i * rows_x_offset + (rows_x_offset / 2), -1 * rows_y_offset);
			emitters.add(new_ring_flame);
			new_ring_flame = new Emitter(i * rows_x_offset + (rows_x_offset / 2), rows_y_offset);
			emitters.add(new_ring_flame);
		}
	}
	
	public void setupPaints() {
		arenaBackground = new Paint();
		arenaBackground.setColor(getResources().getColor(R.color.arena_bg));
		
		paintHealthBackground = new Paint();
		paintHealthBackground.setColor(getResources().getColor(R.color.healthbar_bg));
		paintHealthBackground.setAntiAlias(true);
		
		paintHealth = new Paint();
		paintHealth.setColor(getResources().getColor(R.color.healthbar));
		paintHealth.setAntiAlias(true);
	}
	
	@Override
	public void draw(Canvas canvas) {
		// center the ring
		ringX = getWidth() / 2;
		ringY = getHeight() / 2;
		
		// The ring is 80% of the min length
		ringD = (int) (Math.min(getHeight(), getWidth()) * .9) / 2;
		
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
		
		// draw the emitters
		for (Emitter emitter : emitters) {
			if (emitter.on) {
				Paint paintEmitterFill = new Paint();
				paintEmitterFill.setStyle(Paint.Style.FILL);
				paintEmitterFill.setColor(getResources().getColor(selectedColor));
				canvas.drawCircle(ringX + emitter.x, ringY + emitter.y, emitterRadius, paintEmitterFill);
			}
			canvas.drawCircle(ringX + emitter.x, ringY + emitter.y, emitterRadius, paintEmitterOutline);
		}
	}
	
	private void drawHealthbars(Canvas canvas) {
		// FIXME healthbars using dummy values
		float playerOneDummyHealth = (float) 0.5;
		float playerTwoDummyHealth = (float) 0.8;
		
		float healthbarWidth = (float) (getWidth() * .35);
		float healthbarHeigth = (float) ((float) healthbarWidth * .1);
		
		// draw the left healthbar background
		Path pathHealthbar = new Path();
		pathHealthbar.moveTo(0, 0);
		pathHealthbar.lineTo(healthbarWidth, 0);
		pathHealthbar.lineTo(healthbarWidth, healthbarHeigth);
		pathHealthbar.lineTo(0, healthbarHeigth);
		canvas.drawPath(pathHealthbar, paintHealthBackground);
		
		// draw the left healthbar health
		pathHealthbar = new Path();
		pathHealthbar.moveTo(0, 0);
		pathHealthbar.lineTo((healthbarWidth * playerOneDummyHealth), 0);
		pathHealthbar.lineTo((healthbarWidth * playerOneDummyHealth), healthbarHeigth);
		pathHealthbar.lineTo(0, healthbarHeigth);
		canvas.drawPath(pathHealthbar, paintHealth);
		
		// draw the right healthbar background
		pathHealthbar = new Path();
		pathHealthbar.moveTo(getWidth(), 0);
		pathHealthbar.lineTo(getWidth() - healthbarWidth, 0);
		pathHealthbar.lineTo(getWidth() - healthbarWidth, healthbarHeigth);
		pathHealthbar.lineTo(getWidth(), healthbarHeigth);
		canvas.drawPath(pathHealthbar, paintHealthBackground);

		// draw the right healthbar health
		pathHealthbar = new Path();
		pathHealthbar.moveTo(getWidth(), 0);
		pathHealthbar.lineTo(getWidth() - healthbarWidth * playerTwoDummyHealth, 0);
		pathHealthbar.lineTo(getWidth() - healthbarWidth * playerTwoDummyHealth, healthbarHeigth);
		pathHealthbar.lineTo(getWidth(), healthbarHeigth);
		canvas.drawPath(pathHealthbar, paintHealth);
	}
	
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isInDrawMode) return true; // touching an emitter does nothing

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
            	for (Emitter emitter : emitters) {
        			emitter.on = false;
        		}
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
    
    // FIXME below is dummy code, the touch should be sending an event to the server, not changing how the ring is drawn directly
    private void touch_move(float x, float y) {
    	int emitterTouchRadius = emitterRadius * 2;
		for (Emitter emitter : emitters) {
			if (Math.abs(x - emitter.x - ringX) < emitterTouchRadius && Math.abs(y - emitter.y - ringY) < emitterTouchRadius) {
				emitter.on = true;
			}
		}
    }
    
    private void touch_up() {
		for (Emitter emitter : emitters) {
			emitter.on = false;
		}
    }
    
    // FIXME use emitter from GUIProtocol
	class Emitter {
		int x;
		int y;
		boolean on = false;
		
		public Emitter(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}