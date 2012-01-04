package com.bendude56.bencmd.chat;

import java.util.ArrayList;
import java.util.List;

import com.bendude56.bencmd.BenCmd;

public class ChatChecker {
	public static boolean checkBlocked(String message) {
		List<String> splitChars = new ArrayList<String>();
		splitChars.add(" ");
		splitChars.add(".");
		splitChars.add("!");
		splitChars.add("\\?");
		splitChars.add(",");
		splitChars.add("\\(");
		splitChars.add("\\)");
		List<String> splitString = splitBy(splitChars, message);
		for (String messagePart : splitString) {
			for (String blockedWord : BenCmd.getMainProperties().getString("blockedWords", "").split(",")) {
				if (messagePart.equalsIgnoreCase(blockedWord) && !messagePart.equalsIgnoreCase("")) {
					return true;
				}
			}
		}
		return false;
	}

	public static List<String> splitBy(List<String> remainingChars, String originalMessage) {
		List<String> splitMessage = new ArrayList<String>();
		for (String split : originalMessage.split(remainingChars.get(0))) {
			splitMessage.add(split);
		}
		List<String> newSplitMessage = new ArrayList<String>();
		newSplitMessage.addAll(splitMessage);
		if (remainingChars.size() != 1) {
			remainingChars.remove(0);
			for (String toSplit : splitMessage) {
				newSplitMessage.addAll(splitBy(remainingChars, toSplit));
			}
		}
		return newSplitMessage;
	}
	
	public static boolean isAllCaps(String msg) {
		return (msg.toUpperCase().equals(msg) && !msg.toLowerCase().equals(msg) && msg.length() > 3);
	}
}
