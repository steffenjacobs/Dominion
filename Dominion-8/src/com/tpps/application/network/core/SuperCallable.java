package com.tpps.application.network.core;

/**@author sjacobs - Gamingfish
 * Supercallable is like a Callable, but much more super.*/
public interface SuperCallable<T> {
	
	/**is called like as a callable*/
	public T callMeMaybe(T object);

}
