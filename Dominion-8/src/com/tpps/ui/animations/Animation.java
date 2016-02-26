package com.tpps.ui.animations;

import com.tpps.ui.GameObject;

public abstract class Animation {

	protected GameObject gameObject;
	protected final int durationMillis;
	protected boolean isRunning = false;
	protected boolean isPaused = false, skip = false, reset = false;

	public Animation(GameObject _gameObject, int _durationMillis) {
		this.gameObject = _gameObject;
		this.durationMillis = _durationMillis;
	}

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

	public void pause() {
		if (this.isRunning && !this.isPaused) {
			this.isPaused = true;
			this.onPause();
		} else {
			System.err.println("Error: " + this.toString() + " is not running or already paused.");
		}
	}

	public void skip() {
		if (this.isRunning && !this.skip) {
			skip = true;
		} else {
			System.err.println("Error: " + this.toString() + " is not running or already skipped.");
		}
	}

	public void abort() {
		if (this.isRunning && !this.reset) {
			this.reset = true;
		} else {
			System.err.println("Error: " + this.toString() + " is not running or already resetting.");
		}
	}

	protected abstract void onStart();

	protected abstract void onResume();

	protected abstract void onPause();

	@Override
	public abstract String toString();

}
