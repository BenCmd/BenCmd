package com.bendude56.bencmd.chat;

import java.util.ArrayList;
import java.util.List;

import com.bendude56.bencmd.BenCmd;


public class ChatChecker {
	public static boolean checkBlocked(String message, BenCmd plugin) {
		List<String> splitChars = new ArrayList<String>();
		splitChars.add(" ");
		splitChars.add(".");
		splitChars.add("!");
		splitChars.add("\\?");
		splitChars.add(",");
		splitChars.add("\\(");
		splitChars.add("\\)");
		List<String> splitString = splitBy(splitChars, message, plugin);
		for (String messagePart : splitString) {
			for (String blockedWord : plugin.mainProperties.getString(
					"blockedWords", "").split(",")) {
				if (messagePart.equalsIgnoreCase(blockedWord)) {
					return true;
				}
			}
		}
		return false;
	}

	public static List<String> splitBy(List<String> remainingChars,
			String originalMessage, BenCmd plugin) {
		List<String> splitMessage = new ArrayList<String>();
		for (String split : originalMessage.split(remainingChars.get(0))) {
			splitMessage.add(split);
		}
		List<String> newSplitMessage = new ArrayList<String>();
		newSplitMessage.addAll(splitMessage);
		if (remainingChars.size() != 1) {
			remainingChars.remove(0);
			for (String toSplit : splitMessage) {
				newSplitMessage
						.addAll(splitBy(remainingChars, toSplit, plugin));
			}
		}
		return newSplitMessage;
	}
}
