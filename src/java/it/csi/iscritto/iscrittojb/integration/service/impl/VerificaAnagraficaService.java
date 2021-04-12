package it.csi.iscritto.iscrittojb.integration.service.impl;

import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_BEGIN;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_END;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_ERROR;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.buildMessage;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import it.csi.iscritto.iscrittojb.dto.AnagraficaDto;
import it.csi.iscritto.iscrittojb.exception.ServiceException;
import it.csi.iscritto.iscrittojb.integration.config.NaoConfig;
import it.csi.iscritto.iscrittojb.integration.converter.AnagraficaConverter;
import it.csi.iscritto.iscrittojb.integration.dao.VerificaAnagraficaDao;
import it.csi.iscritto.iscrittojb.integration.service.AbstractService;
import it.csi.iscritto.iscrittojb.integration.service.NaoServiceLocator;
import it.csi.iscritto.iscrittojb.util.NaoUtils;
import it.csi.iscritto.iscrittojb.util.Utils;
import it.csi.naosrv.serviceCSI.cxfclient.visuraFamiglia.DettaglioFamiglia;
import it.csi.naosrv.serviceCSI.cxfclient.visuraFamiglia.JavaServiceDesc;

public class VerificaAnagraficaService extends AbstractService {
	private static final Logger log = Logger.getLogger(ReportService.class);

	private VerificaAnagraficaDao verificaAnagraficaDao;
	private NaoConfig naoConfig;

	public void setVerificaAnagraficaDao(VerificaAnagraficaDao verificaAnagraficaDao) {
		this.verificaAnagraficaDao = verificaAnagraficaDao;
	}

	public void setNaoConfig(NaoConfig naoConfig) {
		this.naoConfig = naoConfig;
	}

	//////////////////////////////////////////////////////////////////////

	public List<AnagraficaDto> getAnagraficaSoggetti() throws ServiceException {
		final String methodName = "getAnagraficaSoggetti";
		log.info(buildMessage(getClass(), methodName, LOG_BEGIN));

		List<AnagraficaDto> result;
		try {
			result = new AnagraficaConverter().convertAll(
					this.verificaAnagraficaDao.findAnagraficaSoggetti());
		}
		catch (Exception e) {
			log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			throw new ServiceException(e);
		}

		log.info(buildMessage(getClass(), methodName, LOG_END));
		return result;
	}

	public void updateFlagResidenza(AnagraficaDto soggetto, Boolean flag) throws ServiceException {
		List<AnagraficaDto> soggetti = new ArrayList<>();
		soggetti.add(soggetto);

		this.updateFlagResidenza(soggetti, flag);
	}
	
	public void updateIdCivico(AnagraficaDto soggetto, Long idCivico) throws ServiceException {
		List<AnagraficaDto> soggetti = new ArrayList<>();
		soggetti.add(soggetto);

		this.updateIdCivico(soggetti, idCivico);
	}
	

	public void updateFlagResidenza(List<AnagraficaDto> soggetti, Boolean flag) throws ServiceException {
		final String methodName = "updateFlagResidenza";
		log.info(buildMessage(getClass(), methodName, LOG_BEGIN));

		try {
			if (CollectionUtils.isNotEmpty(soggetti)) {
				for (AnagraficaDto soggetto : soggetti) {
					this.verificaAnagraficaDao.updateFlagResidenzaNao(soggetto.getIdAnagraficaSoggetto(), flag);

					String message = String.format(
							"domanda: %d; soggetto: %d; tipo soggetto: %s flag: %s",
							soggetto.getIdDomandaIscrizione(),
							soggetto.getIdAnagraficaSoggetto(),
							soggetto.getCodTipoSoggetto(),
							flag);

					log.info(buildMessage(getClass(), methodName, message));
				}
			}
		}
		catch (Exception e) {
			log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			throw new ServiceException(e);
		}

		log.info(buildMessage(getClass(), methodName, LOG_END));
	}
	
	public void updateIdCivico(List<AnagraficaDto> soggetti, Long idCivico) throws ServiceException {
		final String methodName = "updateIdCivico";
		log.info(buildMessage(getClass(), methodName, LOG_BEGIN));

		try {
			if (CollectionUtils.isNotEmpty(soggetti)) {
				for (AnagraficaDto soggetto : soggetti) {
					this.verificaAnagraficaDao.updateIdCivicoNao(soggetto.getIdAnagraficaSoggetto(), idCivico);

					String message = String.format(
							"domanda: %d; soggetto: %d; tipo soggetto: %s id_civico: %s",
							soggetto.getIdDomandaIscrizione(),
							soggetto.getIdAnagraficaSoggetto(),
							soggetto.getCodTipoSoggetto(),
							idCivico);

					log.info(buildMessage(getClass(), methodName, message));
				}
			}
		}
		catch (Exception e) {
			log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			throw new ServiceException(e);
		}

		log.info(buildMessage(getClass(), methodName, LOG_END));
	}

	public void updateUltimaVerifica() throws ServiceException {
		final String methodName = "updateUltimaVerifica";
		log.info(buildMessage(getClass(), methodName, LOG_BEGIN));

		try {
			this.verificaAnagraficaDao.updateUltimaVerifica();
		}
		catch (Exception e) {
			log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			throw new ServiceException(e);
		}

		log.info(buildMessage(getClass(), methodName, LOG_END));
	}

	public DettaglioFamiglia getVisuraFamiglia(String codiceFiscale) throws ServiceException {
		final String methodName = "getVisuraFamiglia";
		log.info(buildMessage(getClass(), methodName, LOG_BEGIN));

		DettaglioFamiglia result;
		try {
			JavaServiceDesc visuraFamigliaService = NaoServiceLocator.getVisuraFamigliaService(
					this.naoConfig.getBasePath(), this.naoConfig.getUser(), this.naoConfig.getPassword());

			result = visuraFamigliaService.dettaglioFamigliaPerCodiceFiscale(
					codiceFiscale, NaoUtils.buildDatiProfilati(), NaoUtils.buildUtenteServizio());
			
			Integer delay = this.naoConfig.getDelay();

			log.info(buildMessage(getClass(), methodName, "secondi di attesa: " + delay));
			Utils.delay(delay);
		}
		catch (Exception e) {
			log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			throw new ServiceException(e);
		}

		log.info(buildMessage(getClass(), methodName, LOG_END));
		return result;
	}

}
