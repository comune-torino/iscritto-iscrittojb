package it.csi.iscritto.iscrittojb.integration.dao;

import java.util.List;

import it.csi.iscritto.iscrittojb.exception.DaoException;
import it.csi.iscritto.iscrittojb.integration.dao.dto.NotificaRow;

public interface InvioNotificaDao {

	List<NotificaRow> findNotificheDaInviare() throws DaoException;

	void aggiornaNotifiche(List<Long> ids, Long idInvioMassivo) throws DaoException;

	Long logInvioMassivo(String json, Integer statusCode) throws DaoException;

}
