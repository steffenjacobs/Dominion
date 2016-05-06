package com.tpps.technicalServices.network.chat.client;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

import com.tpps.technicalServices.util.CollectionsUtil;

/**
 * The BadWordFilter is a class which handles abusive language in chat messages
 * It will filter any abusive words like "BadWord", "B.adWord" and
 * "BadWordhuman" (the last example means that the whole word is filtered if it
 * contains an abusive word.)
 * 
 * @author nicolaswipfler
 *
 */
public class BadWordFilter {

	private static final ArrayList<String> ABUSIVE_WORDS = CollectionsUtil.getArrayList(
			"d2l4ZXI=", "YXJzY2g=", "bnV0dGU=", "aHVyZQ==", "dHVudGU=", "cGVuaXM=",
			"Y29jaw==", "ZGljaw==", "bm9vYg==", "Y3VudA==", "Yml0Y2g=", "emllZ2U=",
			"aGl0bGVy", "c3Blcm1h", "Zm90emU=", "aG9tbw==", "YXNz", "YmFzdGFyZA==",
			"ZnVjaw==", "c2hpdA==", "ZmFn", "ZGFtbg==", "aHVyZQ==", "c3Bhc3Q="
	);

	/**
	 * This method parses any chat/log message and removes abusive language.
	 * Abusive words will be replaced in every position of their length randomly
	 * by "#?$%&@"
	 * 
	 * If there is
	 * 
	 * @param message
	 *            the message to be parsed
	 * @return the parsed message without any abusive words.
	 * 
	 * @author Nicolas Wipfler
	 */
	public static String parseForbiddenWords(String message) {
		StringBuffer result = new StringBuffer();
		String replaceCharacters = "#?$%&@";
		for (String originalWord : message.split("\\s+")) {
			String removedSpecialCharacters = originalWord.replaceAll("[^a-zA-Z\u00c4\u00e4\u00d6\u00f6\u00dc\u00fc\u00df0-9\\s]", "");
			String filter = "";
			if (abusiveWordListContains(removedSpecialCharacters.toLowerCase())) {
				for (int i = 0; i < originalWord.length(); i++) {
					filter += Character.isLetterOrDigit(originalWord.charAt(i)) ? replaceCharacters.charAt(new Random().nextInt(replaceCharacters.length())) : originalWord.charAt(i);
				}
				/**
				 * die folgende Zeile replaced einfach ein "@" in der ersten
				 * Position durch einen random Character aus replaceCharacters
				 * (der natürlich != "@" ist)
				 * */
				StringBuffer filterNew = new StringBuffer().append(
						filter.charAt(0) == '@' ? replaceCharacters.replaceAll("[@]", "").charAt(new Random().nextInt(replaceCharacters.replaceAll("[@]", "").length())) : filter.charAt(0)).append(
						filter.substring(1, filter.length()));
				filter = filterNew.toString();
			} else
				filter = originalWord;
			result.append(filter + " ");
		}
		return result.toString();
	}

	/**
	 * 
	 * @param word
	 *            the word to look up
	 * @return if the parameter contains any abusive words
	 * 
	 * @author Nicolas Wipfler
	 */
	private static boolean abusiveWordListContains(String word) {
		for (String abusiveWord : BadWordFilter.ABUSIVE_WORDS) {
			if (/*!MatchmakingController.getPlayerNameList().contains(word) && */word.contains(base64decode(abusiveWord))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param msg
	 *            the msg to encode
	 * @return base64encoded String
	 * 
	 * @author Nicolas Wipfler
	 */
	private static String base64encode(String msg) {
		try {
			return Base64.getEncoder().encodeToString(msg.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return msg;
	}

	/**
	 * 
	 * @param msg
	 *            the msg to decode
	 * @return base64decoded String
	 * 
	 * @author Nicolas Wipfler
	 */
	private static String base64decode(String msg) {
		try {
			return new String(Base64.getDecoder().decode(msg), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return msg;
	}

	/**
	 * a class used by me to create base64encoded bad words for the attribute
	 * ABUSIVE_WORDS
	 * 
	 * @author Nicolas Wipfler
	 * */
	private static void abusiveWordCreator(String... abWords) {
		ArrayList<String> words = CollectionsUtil.getArrayList(abWords);
		StringBuffer sBuf = new StringBuffer();
		for (String word : words) {
			String encoding = base64encode(word.toLowerCase());
			sBuf.append(", \"" + encoding + "\"");
//			System.out.println("Encoding: " + encoding);
		}
		System.out.println(sBuf.toString());
	}
	
	/**
	 * 
	 * @param args unused
	 */
	public static void main(String[] args) {
		abusiveWordCreator("...");
	}
}
