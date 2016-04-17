package com.tpps.technicalServices.network.core;

/**
 * Supercallable is like a Callable, but much more super.
 * 
 * @author Steffen Jacobs
 * @param <T>
 *            the type of the callable
 */
public interface SuperCallable<T> {

	/**
	 * is called like as a callable
	 * 
	 * @param object
	 *            the object to give to the callable
	 * @return a result
	 */
	public T callMeMaybe(T object);

}
