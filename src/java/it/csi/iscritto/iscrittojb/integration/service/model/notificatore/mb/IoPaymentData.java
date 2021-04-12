package it.csi.iscritto.iscrittojb.integration.service.model.notificatore.mb;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IoPaymentData implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonProperty("amount")
	private Integer amount;

	@JsonProperty("notice_number")
	private String noticeNumber;

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getNoticeNumber() {
		return noticeNumber;
	}

	public void setNoticeNumber(String noticeNumber) {
		this.noticeNumber = noticeNumber;
	}

}
