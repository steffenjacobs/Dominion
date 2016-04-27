package com.tpps.ui.animations;

import com.tpps.ui.GameObject;

/**
 * basic class for playing visual Animations on the GraphicsFramework
 * 
 * @author Steffen Jacobs
 */
public abstract class Animation {

	protected GameObject gameObject;
	protected final int durationMillis;
	protected boolean isRunning = false;
	protected boolean isPaused = false, skip = false, reset = false;

	/**
	 * constructor
	 * 
	 * @param _gameObject
	 *            the gameObject to play the animation with
	 * @param _durationMillis
	 *            the duration of the animation in milliseconds
	 */
	public Animation(GameObject _gameObject, int _durationMillis) {
		this.gameObject = _gameObject;
		this.durationMillis = _durationMillis;
	}

	/**
	 * starts the animation
	 */
	public void play() {
		if (!this.isRunning) {
			this.isRunning = true;
			onStart();
		} else if (this.isPaused) {
			this.isPaused = false;
			this.onResume();
		} else {
			System.err.println("Error: " + this.toString() + " is already running or paused.");
		}
	}

	/**
	 * pauses the animation
	 */
	public void pause() {
		if (this.isRunning && !this.isPaused) {
			this.isPaused = true;
			this.onPause();
		} else {
			System.err.println("Error: " + this.toString() + " is not running or already paused.");
		}
	}

	/**
	 * skips the animation
	 */
	public void skip() {
		if (this.isRunning && !this.skip) {
			skip = true;
		} else {
			System.err.println("Error: " + this.toString() + " is not running or already skipped.");
		}
	}

	/**
	 * aborts the animation
	 */
	public void abort() {
		if (this.isRunning && !this.reset) {
			this.reset = true;
		} else {
			System.err.println("Error: " + this.toString() + " is not running or already resetting.");
		}
	}

	/**
	 * is called when the animation was started
	 */
	protected abstract void onStart();

	/**
	 * is called when the animation was resumed after it was paused
	 */
	protected abstract void onResume();

	/**
	 * is called when the animation was paused
	 */
	protected abstract void onPause();

	/** @return a readable representation of the object */
	@Override
	public abstract String toString();

}
