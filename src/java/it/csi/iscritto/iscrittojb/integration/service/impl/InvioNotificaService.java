package it.csi.iscritto.iscrittojb.integration.service.impl;

import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_BEGIN;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_END;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_ERROR;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.buildMessage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.csi.iscritto.iscrittojb.dto.NotificaDto;
import it.csi.iscritto.iscrittojb.exception.ServiceException;
import it.csi.iscritto.iscrittojb.integration.config.NotificatoreConfig;
import it.csi.iscritto.iscrittojb.integration.converter.NotificaConverter;
import it.csi.iscritto.iscrittojb.integration.dao.InvioNotificaDao;
import it.csi.iscritto.iscrittojb.integration.service.AbstractService;
import it.csi.iscritto.iscrittojb.integration.service.model.notificatore.mb.Message;
import it.csi.iscritto.iscrittojb.util.Joiner;
import it.csi.iscritto.iscrittojb.util.notificatore.MBUtils;
import it.csi.iscritto.iscrittojb.util.rest.CallResult;
import it.csi.iscritto.iscrittojb.util.rest.RestUtils;

public class InvioNotificaService extends AbstractService {
	private static final Logger log = Logger.getLogger(InvioNotificaService.class);

	private InvioNotificaDao invioNotificaDao;
	private NotificatoreConfig notificatoreConfig;

	public void setInvioNotificaDao(InvioNotificaDao invioNotificaDao) {
		this.invioNotificaDao = invioNotificaDao;
	}

	public void setNotificatoreConfig(NotificatoreConfig notificatoreConfig) {
		this.notificatoreConfig = notificatoreConfig;

		if (StringUtils.isBlank(this.notificatoreConfig.getMessagebrokerUrl())) {
			throw new IllegalArgumentException("messagebrokerUrl non presente");
		}

		if (StringUtils.isBlank(this.notificatoreConfig.getMessagebrokerToken())) {
			throw new IllegalArgumentException("messagebrokerToken non presente");
		}
	}

	//////////////////////////////////////////////////////////////////////

	public List<NotificaDto> getNotificheDaInviare() throws ServiceException {
		final String methodName = "getNotificheDaInviare";
		log.debug(buildMessage(getClass(), methodName, LOG_BEGIN));

		List<NotificaDto> result;
		try {
			result = new NotificaConverter().convertAll(
					this.invioNotificaDao.findNotificheDaInviare());

			log.info(buildMessage(getClass(), methodName, String.format(
					"trovate %d notifiche da inviare", CollectionUtils.size(result))));
		}
		catch (Exception e) {
			log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			throw new ServiceException(e);
		}

		log.debug(buildMessage(getClass(), methodName, LOG_END));
		return result;
	}

	public String buildJsonInvio(List<NotificaDto> notifiche) throws ServiceException {
		final String methodName = "getNotificheDaInviare";
		log.debug(buildMessage(getClass(), methodName, LOG_BEGIN));

		String result;
		try {
			List<Message> model = MBUtils.buildModel(notifiche);

			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);

			result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(model);
		}
		catch (Exception e) {
			log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			throw new ServiceException(e);
		}

		log.debug(buildMessage(getClass(), methodName, LOG_END));
		return result;
	}

	public CallResult invioNotifica(String json) throws ServiceException {
		final String methodName = "invioNotifica";
		log.debug(buildMessage(getClass(), methodName, LOG_BEGIN));

		Validate.notBlank(json);

		CallResult result;
		try {
			Map<String, String> headers = new LinkedHashMap<>();
			headers.put("Content-Type", "application/json");
			headers.put("x-authentication", this.notificatoreConfig.getMessagebrokerToken());

			String uri = new URIBuilder().setPath(this.notificatoreConfig.getMessagebrokerUrl() + "/topics/messages")
					.build()
					.toString();

			log.info(buildMessage(getClass(), methodName, "URI: " + uri));
			log.info(buildMessage(getClass(), methodName, "payload: " + json));

			result = RestUtils.postJson(uri, headers, json);

			log.info(buildMessage(getClass(), methodName, "statusCode: " + result.getStatusCode()));
			log.info(buildMessage(getClass(), methodName, "reasonPhrase: " + result.getReasonPhrase()));
			log.info(buildMessage(getClass(), methodName, "response: " + result.getJson()));
		}
		catch (Exception e) {
			log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			throw new ServiceException(e);
		}

		log.debug(buildMessage(getClass(), methodName, LOG_END));
		return result;
	}

	public Long insertLogInvioMassivo(String json, Integer statusCode) throws ServiceException {
		final String methodName = "insertLogInvioMassivo";
		log.debug(buildMessage(getClass(), methodName, LOG_BEGIN));

		Long result;
		try {
			result = this.invioNotificaDao.logInvioMassivo(json, statusCode);
			log.info(buildMessage(getClass(), methodName, String.format("id invio massivo: %d", result)));
		}
		catch (Exception e) {
			log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			throw new ServiceException(e);
		}

		log.debug(buildMessage(getClass(), methodName, LOG_END));
		return result;
	}

	public void aggiornaNotifiche(List<NotificaDto> notifiche, Long idInvioMassivo) throws ServiceException {
		final String methodName = "aggiornaNotifiche";
		log.debug(buildMessage(getClass(), methodName, LOG_BEGIN));

		if (CollectionUtils.isEmpty(notifiche)) {
			log.info(buildMessage(getClass(), methodName, "nessuna notifica da aggiornare"));
			return;
		}

		try {
			List<Long> ids = new ArrayList<>();
			for (NotificaDto notifica : notifiche) {
				ids.add(notifica.getIdInvioSms());
			}

			this.invioNotificaDao.aggiornaNotifiche(ids, idInvioMassivo);

			log.info(buildMessage(getClass(), methodName, String.format(
					"aggiornate %d notifiche", CollectionUtils.size(ids))));

			log.info(buildMessage(getClass(), methodName, String.format(
					"elenco id notifiche aggiornate: %s", Joiner.on(", ").join(ids))));
		}
		catch (Exception e) {
			log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			throw new ServiceException(e);
		}

		log.debug(buildMessage(getClass(), methodName, LOG_END));
	}

}
