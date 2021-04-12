package it.csi.iscritto.iscrittojb.integration.service.model.notificatore.mb;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Email implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonProperty("to")
	private String to;

	@JsonProperty("subject")
	private String subject;

	@JsonProperty("body")
	private String body;

	@JsonProperty("template_id")
	private String templateId;

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

}
