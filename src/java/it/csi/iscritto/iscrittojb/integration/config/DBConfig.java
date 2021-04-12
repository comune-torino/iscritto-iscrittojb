package it.csi.iscritto.iscrittojb.integration.config;

import java.io.Serializable;

public class DBConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	private String dbUrl;
	private String dbUser;
	private String dbPassword;

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

}
