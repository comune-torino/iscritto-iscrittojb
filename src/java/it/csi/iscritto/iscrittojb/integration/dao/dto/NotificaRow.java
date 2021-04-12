package it.csi.iscritto.iscrittojb.integration.dao.dto;

import java.io.Serializable;
import java.util.Date;

public class NotificaRow implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long idInvioSms;
	private Long idInvioMassivo;
	private String testo;
	private Long idDomandaIscrizione;
	private Date dtInserimento;
	private Date dtInvio;
	private String esito;
	private String telefono;
	private String codiceFiscaleRichiedente;

	public Long getIdInvioSms() {
		return idInvioSms;
	}

	public void setIdInvioSms(Long idInvioSms) {
		this.idInvioSms = idInvioSms;
	}

	public Long getIdInvioMassivo() {
		return idInvioMassivo;
	}

	public void setIdInvioMassivo(Long idInvioMassivo) {
		this.idInvioMassivo = idInvioMassivo;
	}

	public String getTesto() {
		return testo;
	}

	public void setTesto(String testo) {
		this.testo = testo;
	}

	public Long getIdDomandaIscrizione() {
		return idDomandaIscrizione;
	}

	public void setIdDomandaIscrizione(Long idDomandaIscrizione) {
		this.idDomandaIscrizione = idDomandaIscrizione;
	}

	public Date getDtInserimento() {
		return dtInserimento;
	}

	public void setDtInserimento(Date dtInserimento) {
		this.dtInserimento = dtInserimento;
	}

	public Date getDtInvio() {
		return dtInvio;
	}

	public void setDtInvio(Date dtInvio) {
		this.dtInvio = dtInvio;
	}

	public String getEsito() {
		return esito;
	}

	public void setEsito(String esito) {
		this.esito = esito;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getCodiceFiscaleRichiedente() {
		return codiceFiscaleRichiedente;
	}

	public void setCodiceFiscaleRichiedente(String codiceFiscaleRichiedente) {
		this.codiceFiscaleRichiedente = codiceFiscaleRichiedente;
	}

}
