package it.csi.iscritto.iscrittojb.integration.dao.dto;

import java.io.Serializable;

public class DatiStepRow implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long idStep;
	private Long idAnnoScolastico;

	public Long getIdStep() {
		return idStep;
	}

	public void setIdStep(Long idStep) {
		this.idStep = idStep;
	}

	public Long getIdAnnoScolastico() {
		return idAnnoScolastico;
	}

	public void setIdAnnoScolastico(Long idAnnoScolastico) {
		this.idAnnoScolastico = idAnnoScolastico;
	}

}
