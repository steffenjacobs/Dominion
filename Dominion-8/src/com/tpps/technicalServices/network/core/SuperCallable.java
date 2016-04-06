package com.tpps.technicalServices.network.core;

/**@author Steffen Jacobs
 * Supercallable is like a Callable, but much more super.*/
public interface SuperCallable<T> {
	
	/**is called like as a callable*/
	public T callMeMaybe(T object);

}
