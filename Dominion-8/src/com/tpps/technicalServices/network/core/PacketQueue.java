package com.tpps.technicalServices.network.core;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

/**
 * This class represents a packet-queue
 * 
 * @author Steffen Jacobs
 */
public class PacketQueue<E> extends ConcurrentLinkedQueue<E> {
	private static final long serialVersionUID = 9133220497162299860L;

	private Semaphore lock = new Semaphore(1);

	/**
	 * adds the element to the tail of the queue and notifies the consumer (if
	 * not already consuming)
	 * 
	 * @param elem
	 *            element to add to the queue
	 * 
	 * @return true as specified by Queue.offer(E))
	 */
	@Override
	public boolean offer(E elem) {
		super.offer(elem);
		lock.release();
		return true;
	}

	/**
	 * removes the head of the queue, blocks if queue is empty
	 * 
	 * @return the head of the queue
	 */
	@Override
	public E poll() {
		try {
			lock.acquire();
		} catch (InterruptedException e) {
			//normal when thread is killed
		}
		return super.poll();
	}

	/**
	 * returns the packet at the head of the queue, blocks if queue is empty
	 * 
	 * @return the head of the queue
	 */
	@Override
	public E peek() {
		try {
			lock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return super.peek();
	}

	/**
	 * adds a packet to the queue and notifies the consumer (if not already
	 * consuming)
	 * 
	 * @return false as specified by Queue.offer(E))
	 */
	@Override
	public boolean add(E e) {
		super.add(e);
		lock.release();
		return false;
	}

}
