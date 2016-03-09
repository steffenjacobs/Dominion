package com.tpps.application.network.core;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class PacketQueue<E> extends ConcurrentLinkedQueue<E> {
	private static final long serialVersionUID = 9133220497162299860L;

	private Semaphore lock = new Semaphore(1);

	@Override
	public boolean offer(E e) {
		super.offer(e);
		lock.release();
		return true;
	}

	@Override
	public E poll() {
		try {
			lock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return super.poll();
	}

	@Override
	public E peek() {
		try {
			lock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return super.peek();
	}

	@Override
	public boolean add(E e) {
		super.add(e);
		lock.release();
		return false;
	}

}
