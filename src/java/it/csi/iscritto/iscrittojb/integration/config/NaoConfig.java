package it.csi.iscritto.iscrittojb.integration.config;

import java.io.Serializable;

public class NaoConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	private String basePath;
	private String user;
	private String password;
	private Integer delay;

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getDelay() {
		return delay;
	}

	public void setDelay(Integer delay) {
		this.delay = delay;
	}

}
