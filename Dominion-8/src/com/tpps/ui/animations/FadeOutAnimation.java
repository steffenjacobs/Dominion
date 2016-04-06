package com.tpps.ui.animations;

import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.GameObject;

public class FadeOutAnimation extends Animation {
	private Thread playAnimationThread;
	private Callable<?> callOnDone;

	private static final int DELAY_MILLIS = 20;
	private static final float FADE_LOWER_BOUND = .01f;

	private int frameCounter;
	private final int maxFrames;

	public FadeOutAnimation(GameObject gameObject, int durationMillis, Callable<?> callWhenDone) {
		super(gameObject, durationMillis);
		this.maxFrames = durationMillis / DELAY_MILLIS;
		this.frameCounter = 0;
		this.callOnDone = callWhenDone;
		this.playAnimationThread = setupLogic();
	}

	private Thread setupLogic() {
		return new Thread(new Runnable() {
			float transparency = 1f;
			private BufferedImage baseImage;
			private float fadePerStep = 1 / (float) maxFrames;

			@Override
			public void run() {
				// backup of the basis-image
				this.baseImage = new BufferedImage(gameObject.getRenderdImage().getWidth(null),
						gameObject.getRenderdImage().getHeight(null), BufferedImage.TYPE_INT_ARGB);
				baseImage.getGraphics().drawImage(gameObject.getRenderdImage(), 0, 0, null);

				while (!isPaused && isRunning && !playAnimationThread.isInterrupted()) {
					frameCounter++;

					// check if done or skipped
					if (frameCounter >= maxFrames || transparency < FADE_LOWER_BOUND || skip) {
						System.out.println(frameCounter + "/" + maxFrames);
						gameObject.setVisible(false);
						gameObject.updatedBufferedImage(this.baseImage);
						System.out.println("fade-animation finished.");

						try {
							callOnDone.call();
						} catch (Exception e) {
							e.printStackTrace();
						}
						return;
					}
					// check reset-flag
					if (reset) {
						gameObject.updatedBufferedImage(this.baseImage);
						return;
					}

					// update image
					transparency -= fadePerStep;
					gameObject.updatedBufferedImage(GraphicsUtil.setAlpha(baseImage, transparency));
					try {
						Thread.sleep(DELAY_MILLIS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		});
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