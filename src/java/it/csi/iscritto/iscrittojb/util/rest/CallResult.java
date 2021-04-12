package it.csi.iscritto.iscrittojb.util.rest;

import java.io.Serializable;

public class CallResult implements Serializable {
	private static final long serialVersionUID = 1L;

	private String json;
	private int statusCode;
	private String reasonPhrase;

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getReasonPhrase() {
		return reasonPhrase;
	}

	public void setReasonPhrase(String reasonPhrase) {
		this.reasonPhrase = reasonPhrase;
	}

}
