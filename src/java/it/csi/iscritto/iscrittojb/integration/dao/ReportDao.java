package it.csi.iscritto.iscrittojb.integration.dao;

import java.sql.Connection;
import java.util.List;

import it.csi.iscritto.iscrittojb.exception.DaoException;
import it.csi.iscritto.iscrittojb.integration.dao.dto.DatiScuola;
import it.csi.iscritto.iscrittojb.integration.dao.dto.DatiStepRow;

public interface ReportDao {

	Connection openAndGetNewConnection() throws DaoException;

	void closeNewConnection(Connection connection);

	DatiStepRow findDatiStep(String codOrdineScuola, String codStatoGra) throws DaoException;

	List<DatiScuola> findDatiScuole(Long idAnnoScolastico, String codOrdineScuola) throws DaoException;

}
