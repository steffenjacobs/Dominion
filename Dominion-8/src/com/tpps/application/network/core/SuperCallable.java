package com.tpps.application.network.core;

/**@author sjacobs - Gamingfish
 * Supercallable is like a Callable, but much more better.*/
public interface SuperCallable<T> {
	
	/**is called like as a callable*/
	public T call(T object);

}
