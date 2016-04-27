package com.tpps.ui.animations;

import java.util.concurrent.Callable;

import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;

public class MoveAnimation extends Animation {
	private Thread playAnimationThread;
	private Callable<?> callOnDone;

	private static final int DELAY_MILLIS = 16;

	private int frameCounter;
	private final int maxFrames;
	private final GraphicFramework framework;
	private final double startX, startY, destX, destY;

	public MoveAnimation(GraphicFramework gf, GameObject gameObject, int durationMillis,
			Callable<?> callWhenDone, double x, double y) {
		super(gameObject, durationMillis);
		this.maxFrames = durationMillis / DELAY_MILLIS;
		this.frameCounter = 0;
		this.callOnDone = callWhenDone;
		this.framework = gf;
		this.destX = x;
		this.destY = y;
		this.startX = (double) gameObject.getLocation().getX() / gf.getWidth();
		this.startY = (double) gameObject.getLocation().getY() / gf.getHeight();
		this.playAnimationThread = setupLogic();
	}

	private Thread setupLogic() {
		return new Thread(new Runnable() {

			@Override
			public void run() {

				while (!isPaused && isRunning && !playAnimationThread.isInterrupted()) {
					frameCounter++;
					
					//skip
					if(skip){
						framework.moveGameObjectTo(gameObject, destX, destY);
						return;
					}

					// check if done 
					if (frameCounter >= maxFrames) {
						System.out.println(frameCounter + "/" + maxFrames);
						framework.moveGameObjectTo(gameObject, destX, destY);

						try {
							callOnDone.call();
						} catch (Exception e) {
							e.printStackTrace();
						}
						return;
					}
					// check reset-flag
					if (reset) {
						framework.moveGameObjectTo(gameObject, startX, startY);
						return;
					}
					
					//move
					framework.moveGameObjectTo(gameObject, getXAtFrame(frameCounter), getYAtFrame(frameCounter));

					// update image
//					transparency -= fadePerStep;
//					gameObject.updateImage(GraphicsUtil.setAlpha(baseImage, transparency));
					try {
						Thread.sleep(DELAY_MILLIS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		});
	}

	private double getXAtFrame(int frame) {
		return this.startX + (this.destX - this.startX) / maxFrames * frame;
	}

	private double getYAtFrame(int frame) {
		return this.startY + (this.destY - this.startY) / maxFrames * frame;
	}

	@Override
	public String toString() {
		return "@" + System.identityHashCode(this) + " - " + super.gameObject.toString();
	}

	@Override
	protected void onStart() {
		this.playAnimationThread.start();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		this.playAnimationThread.resume();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		this.playAnimationThread.suspend();
	}

}
