package it.csi.iscritto.iscrittojb.util;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

public final class Utils {
	private Utils() {
		/* NOP */
	}

	public static boolean areEquals(String a, String b) {
		return a != null && b != null && a.trim().equalsIgnoreCase(b.trim());
	}

	public static JsonNode getNode(JsonNode node, String fieldName) {
		if (node == null || StringUtils.isBlank(fieldName)) {
			return null;
		}

		return node.findValue(fieldName);
	}

	public static String getText(JsonNode node, String fieldName) {
		JsonNode value = getNode(node, fieldName);
		if (value == null) {
			return null;
		}

		return value.asText();
	}

	public static boolean statusOk(JsonNode rootNode) {
		JsonNode statusNode = Utils.getNode(rootNode, "status");
		String statusCode = Utils.getText(statusNode, "status");

		return "200".equals(statusCode);
	}

	public static void delay(int seconds) {
		if (seconds > 0) {
			try {
				Thread.sleep(seconds * 1000);
			}
			catch (InterruptedException e) {
				/* NOP */
			}
		}
	}

}
