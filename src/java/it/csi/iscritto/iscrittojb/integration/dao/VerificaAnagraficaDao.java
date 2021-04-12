package it.csi.iscritto.iscrittojb.integration.dao;

import java.util.List;

import it.csi.iscritto.iscrittojb.exception.DaoException;
import it.csi.iscritto.iscrittojb.integration.dao.dto.AnagraficaRow;

public interface VerificaAnagraficaDao {

	List<AnagraficaRow> findAnagraficaSoggetti() throws DaoException;

	int updateFlagResidenzaNao(Long idSoggetto, Boolean flag) throws DaoException;
	
	int updateIdCivicoNao(Long idSoggetto, Long idCivico) throws DaoException;

	int updateUltimaVerifica() throws DaoException;

}
