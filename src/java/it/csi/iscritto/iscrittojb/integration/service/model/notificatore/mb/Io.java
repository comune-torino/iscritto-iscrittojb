package it.csi.iscritto.iscrittojb.integration.service.model.notificatore.mb;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Io implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonProperty("time_to_live")
	private Integer timeToLive;

	@JsonProperty("content")
	private IoContent content;

	public Integer getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(Integer timeToLive) {
		this.timeToLive = timeToLive;
	}

	public IoContent getContent() {
		return content;
	}

	public void setContent(IoContent content) {
		this.content = content;
	}

}
