package it.csi.iscritto.iscrittojb.integration.service.model.notificatore.mb;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IoContent implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonProperty("subject")
	private String subject;

	@JsonProperty("markdown")
	private String markdown;

	@JsonProperty("payment_data")
	private IoPaymentData paymentData;

	@JsonProperty("due_date")
	private String dueDate;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMarkdown() {
		return markdown;
	}

	public void setMarkdown(String markdown) {
		this.markdown = markdown;
	}

	public IoPaymentData getPaymentData() {
		return paymentData;
	}

	public void setPaymentData(IoPaymentData paymentData) {
		this.paymentData = paymentData;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

}
