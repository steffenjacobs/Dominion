package com.tpps.technicalServices.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConcurrentMultiMap<K, V> {
	private ConcurrentHashMap<K, CopyOnWriteArrayList<V>> map = new ConcurrentHashMap<>();

	public synchronized void put(K key, V value) {

		CopyOnWriteArrayList<V> list;
		if (map.containsKey(key)) {
			list = map.get(key);
		} else {
			list = new CopyOnWriteArrayList<V>();
		}

		list.add(value);
		map.put(key, list);
	}

	public V get(Object k) {
		if (map.containsKey(k))
			return map.get(k).get(0);
		else
			return null;
	}

	public synchronized V remove(K key) {
		if (!map.containsKey(key))
			return null;
		if (map.get(key).size() == 1)
			return map.remove(key).get(0);
		CopyOnWriteArrayList<V> list = map.get(key);
		V res = list.remove(list.size() - 1);
		map.put(key, list);
		return res;
	}
}