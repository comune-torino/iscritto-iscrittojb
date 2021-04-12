package it.csi.iscritto.iscrittojb.integration;

public enum TipoGraduatoria {
	PROVVISORIA("P", "Provvisoria"),
	DEFINITIVA("D", "Definitiva"),
	PROVVISORIA_MAT("MP", "Provvisoria materne"),
	DEFINITIVA_MAT("MD", "Definitiva materne"),
	;

	private String code;
	private String desc;

	private TipoGraduatoria(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public static TipoGraduatoria findByCod(String cod) {
		for (TipoGraduatoria e : values()) {
			if (e.getCode().equals(cod)) {
				return e;
			}
		}

		return null;
	}

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

}
