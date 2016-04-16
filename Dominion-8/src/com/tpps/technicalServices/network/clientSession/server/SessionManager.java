package com.tpps.technicalServices.network.clientSession.server;

import java.io.PrintStream;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

/** @author Steffen Jacobs */
public class SessionManager {

	private static final int MAX_CACHE_SIZE = 10000;
	private static final int MAX_CONCURRENT_USERS = 5;
	private static final int EXPIRATION_TIME_SECONDS = 120;

	// represents a cache which entries will be auto-removed after
	// EXPIRATION_TIME_SECONDS
	// using Guava for the cache. Project: https://github.com/google/guava
	private static Cache<String, UUID> validSessions;

	/**
	 * static constructor, is called when class is created
	 */
	static {
		validSessions = CacheBuilder.newBuilder().concurrencyLevel(MAX_CONCURRENT_USERS).maximumSize(MAX_CACHE_SIZE)
				.expireAfterAccess(EXPIRATION_TIME_SECONDS, TimeUnit.SECONDS).build(new CacheLoader<String, UUID>() {

					@Override
					public UUID load(String arg0) throws Exception {
						return validSessions.getIfPresent(arg0);
					}
				});
	}

	/**
	 * updates the timestamp from the entry from the username
	 * 
	 * @param username
	 *            name of the user whos timestamp should be updated
	 */
	public static void revalidate(String username) {
		validSessions.getIfPresent(username);
	}

	/**
	 * checks if the username is already associated with a session
	 * 
	 * @param username
	 *            name of the user who's session should be checked
	 * @return whether there is a session for the user existing
	 */
	public static boolean hasSession(String username) {
		return validSessions.getIfPresent(username) != null;
	}

	/**
	 * invalidates a user -> user will be removed from the cache
	 * 
	 * @param username
	 *            name of user who will be removed
	 */
	private static void invalidate(String username) {
		validSessions.invalidate(username);
	}

	/**
	 * @param username
	 *            name of the user
	 * @param uuid
	 *            unique-ID linked with username
	 * @return wheter the uuid with the given username is valid
	 */
	public static boolean isValid(String username, UUID uuid) {
		if (uuid == null)
			return false;
		if (uuid.equals(validSessions.getIfPresent(username)))
			return true;

		invalidate(username);
		return false;
	}

	/**
	 * @return the SessionID from the username
	 * @param username
	 *            Name of the user whos sessionID is needed
	 */
	public static UUID getValidSession(String username) {
		UUID sessionID = validSessions.getIfPresent(username);
		if (sessionID == null) {
			System.out.println("Generating new UUID for player " + username + "...");
			sessionID = generateUUID();
			validSessions.put(username, sessionID);
		}
		return sessionID;
	}

	/**
	 * generates a sessionID
	 * 
	 * @return a new randomly generated sessionID
	 */
	private static UUID generateUUID() {
		return UUID.randomUUID();
	}

	/**
	 * writes all valid user-sessions to a stream
	 * 
	 * @param stream
	 *            Stream to write all the users with their sessions in.
	 */
	static void outputAll(PrintStream stream) {

		Set<Entry<String, UUID>> snapshot = validSessions.asMap().entrySet();
		stream.println("\nCached users (" + snapshot.size() + "): ");
		for (Entry<String, UUID> entry : snapshot) {
			stream.println(entry.getKey() + ": " + entry.getValue().toString());
		}
		stream.println("--------------------------\n");
		stream.println();
	}

	/**
	 * getter for EXPIRATION_TIME_SECONDS
	 * 
	 * @return expiration time in seconds
	 */
	public static int getExpiration() {
		return EXPIRATION_TIME_SECONDS;
	}
}