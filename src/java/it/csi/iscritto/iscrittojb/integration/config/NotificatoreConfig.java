package it.csi.iscritto.iscrittojb.integration.config;

import java.io.Serializable;

public class NotificatoreConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	private String messagebrokerUrl;
	private String messagebrokerToken;

	public String getMessagebrokerUrl() {
		return messagebrokerUrl;
	}

	public void setMessagebrokerUrl(String messagebrokerUrl) {
		this.messagebrokerUrl = messagebrokerUrl;
	}

	public String getMessagebrokerToken() {
		return messagebrokerToken;
	}

	public void setMessagebrokerToken(String messagebrokerToken) {
		this.messagebrokerToken = messagebrokerToken;
	}

}
