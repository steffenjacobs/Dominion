package com.tpps.ui.animations;

import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.GameObject;

/**
 * this represents a simple fading animation
 * 
 * @author Steffen Jacobs
 */
public class FadeAnimation extends Animation {
	private Thread playAnimationThread;
	private Callable<?> callOnDone;

	private static final int DELAY_MILLIS = 20;

	private int frameCounter;
	private final int maxFrames;
	private final int alphaStart, alphaEnd;
	/** true: fade in, false: fade out */
	private final boolean direction, resetImageAfterwards;

	/**
	 * @param gameObject
	 *            the game-object to play the animation on
	 * @param durationMillis
	 *            the duration of the animation in milliseconds
	 * @param callWhenDone
	 *            a callable to call after the animation is finished
	 * @param alphaStart
	 *            alpha-start-value [0-255]
	 * @param alphaEnd
	 *            alpha-end-value [0-255]
	 * @param resetImageAfterwards
	 *            whether to reset the image after the animation
	 */
	public FadeAnimation(GameObject gameObject, int durationMillis, Callable<?> callWhenDone, int alphaStart,
			int alphaEnd, boolean resetImageAfterwards) {

		super(gameObject, durationMillis);

		this.alphaStart = alphaStart;
		this.alphaEnd = alphaEnd;
		this.maxFrames = durationMillis / DELAY_MILLIS;
		this.frameCounter = 0;
		this.callOnDone = callWhenDone;
		this.resetImageAfterwards = resetImageAfterwards;

		this.direction = alphaStart < alphaEnd;
		this.playAnimationThread = setupLogic();

	}

	/**
	 * contains the entire animation-logic
	 * 
	 * @return Thread the thread that is running the animation
	 */
	private Thread setupLogic() {
		return new Thread(new Runnable() {
			float transparency = (float) alphaStart / 255;
			private BufferedImage baseImage;
			private float fadePerStep = Math.abs(alphaStart - alphaEnd) / 255f / maxFrames;

			@Override
			public void run() {
				// backup of the basis-image
				this.baseImage = new BufferedImage(gameObject.getRenderdImage().getWidth(null),
						gameObject.getRenderdImage().getHeight(null), BufferedImage.TYPE_INT_ARGB);
				baseImage.getGraphics().drawImage(gameObject.getRenderdImage(), 0, 0, null);

				while (!isPaused && isRunning && !playAnimationThread.isInterrupted()) {
					frameCounter++;

					// check if done or skipped
					if (frameCounter >= maxFrames
							|| (direction ? (transparency > alphaEnd / 255f) : (transparency < alphaEnd / 255f))
							|| skip) {
						System.out.println(frameCounter + "/" + maxFrames);
						// gameObject.setVisible(false);
						if (resetImageAfterwards) {
							gameObject.updatedBufferedImage(this.baseImage);
						}

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
					if (direction) {
						transparency += fadePerStep;
					} else {
						transparency -= fadePerStep;
					}
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

	/** @return a readable representation of the object */
	@Override
	public String toString() {
		return "@" + System.identityHashCode(this) + " - " + super.gameObject.toString();
	}

	/** is called on start */
	@Override
	protected void onStart() {
		this.playAnimationThread.start();
	}

	/** is called on resume */
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		this.playAnimationThread.resume();
	}

	/** is called on pause */
	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		this.playAnimationThread.suspend();
	}
}