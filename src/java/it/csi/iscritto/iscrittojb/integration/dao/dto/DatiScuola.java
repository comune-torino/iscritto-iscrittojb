package it.csi.iscritto.iscrittojb.integration.dao.dto;

import java.io.Serializable;

public class DatiScuola implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long idScuola;
	private String codScuola;
	private Long idTipoFrequenza;
	private String tipoFrequenza;

	public Long getIdScuola() {
		return idScuola;
	}

	public void setIdScuola(Long idScuola) {
		this.idScuola = idScuola;
	}

	public String getCodScuola() {
		return codScuola;
	}

	public void setCodScuola(String codScuola) {
		this.codScuola = codScuola;
	}

	public Long getIdTipoFrequenza() {
		return idTipoFrequenza;
	}

	public void setIdTipoFrequenza(Long idTipoFrequenza) {
		this.idTipoFrequenza = idTipoFrequenza;
	}

	public String getTipoFrequenza() {
		return tipoFrequenza;
	}

	public void setTipoFrequenza(String tipoFrequenza) {
		this.tipoFrequenza = tipoFrequenza;
	}

}
