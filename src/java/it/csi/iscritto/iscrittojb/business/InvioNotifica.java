package it.csi.iscritto.iscrittojb.business;

import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_BEGIN;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_END;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_ERROR;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.buildMessage;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import it.csi.iscritto.iscrittojb.dto.NotificaDto;
import it.csi.iscritto.iscrittojb.exception.ServiceException;
import it.csi.iscritto.iscrittojb.integration.config.Context;
import it.csi.iscritto.iscrittojb.integration.service.impl.InvioNotificaService;
import it.csi.iscritto.iscrittojb.util.rest.CallResult;
import it.csi.iscritto.iscrittojb.util.rest.RestUtils;

public class InvioNotifica {
	private static final Logger log = Logger.getLogger(InvioNotifica.class);

	public static void main(String[] args) {
		final String methodName = "main";

		if (args.length != 0) {
			System.out.println("Utilizzare: java -jar invio-notifica.jar");
			System.exit(1);
		}

		try {
			execute();
		}
		catch (Throwable t) {
			log.error(buildMessage(InvioNotifica.class, methodName, LOG_ERROR), t);
			System.exit(1);
		}

		System.exit(0);
	}

	public static void execute() throws Exception {
		final String methodName = "execute";
		log.info(buildMessage(InvioNotifica.class, methodName, LOG_BEGIN));

		InvioNotificaService invioNotificaService = Context.getInstance().getInvioNotificaService();

		List<NotificaDto> notifiche = invioNotificaService.getNotificheDaInviare();
		if (CollectionUtils.isEmpty(notifiche)) {
			log.info(buildMessage(InvioNotifica.class, methodName, "nessuna notifica da inviare"));
			return;
		}

		String json = invioNotificaService.buildJsonInvio(notifiche);

		CallResult callResult = invioNotificaService.invioNotifica(json);
		Long idInvioMassivo = invioNotificaService.insertLogInvioMassivo(json, RestUtils.getStatusCode(callResult));

		if (!RestUtils.isCreated(callResult)) {
			ServiceException e = new ServiceException("errore nell'accodamento delle notifiche");
			log.error(buildMessage(InvioNotifica.class, methodName, LOG_ERROR), e);
			throw e;
		}

		invioNotificaService.aggiornaNotifiche(notifiche, idInvioMassivo);

		log.info(buildMessage(InvioNotifica.class, methodName, LOG_END));
	}

}
