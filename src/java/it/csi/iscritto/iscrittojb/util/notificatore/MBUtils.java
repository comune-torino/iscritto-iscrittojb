package it.csi.iscritto.iscrittojb.util.notificatore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import it.csi.iscritto.iscrittojb.dto.NotificaDto;
import it.csi.iscritto.iscrittojb.integration.service.model.notificatore.mb.Io;
import it.csi.iscritto.iscrittojb.integration.service.model.notificatore.mb.IoContent;
import it.csi.iscritto.iscrittojb.integration.service.model.notificatore.mb.Message;
import it.csi.iscritto.iscrittojb.integration.service.model.notificatore.mb.Mex;
import it.csi.iscritto.iscrittojb.integration.service.model.notificatore.mb.Notification;
import it.csi.iscritto.iscrittojb.integration.service.model.notificatore.mb.Sms;
import it.csi.iscritto.iscrittojb.util.Constants;
import it.csi.iscritto.iscrittojb.util.DateUtils;

public final class MBUtils {
	public static final int DAYS_TO_EXPIRATION = 3;

	private MBUtils() {
		/* NOP */
	}

	public static List<Message> buildModel(List<NotificaDto> notifiche) {
		List<Message> result = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(notifiche)) {
			final String bulkId = getUuid();
			final Date expirationDate = DateUtils.addDays(new Date(), DAYS_TO_EXPIRATION);

			for (NotificaDto notifica : notifiche) {
				final String uuid = getUuid();

				Message nt = new Message();
				nt.setUuid(uuid);
				nt.setPayload(buildPayload(notifica, uuid, bulkId));
				nt.setExpireAt(DateUtils.toIso8601Format(expirationDate, DateUtils.UTC));

				result.add(nt);
			}
		}

		return result;
	}

	private static Notification buildPayload(NotificaDto notifica, String id, String bulkId) {
		Notification payload = new Notification();

		payload.setId(id);
		payload.setUserId(notifica.getCodiceFiscaleRichiedente());
		payload.setTag(Constants.MEX_TAG);
		payload.setSms(buildSms(notifica));
		payload.setMex(buildMex(notifica));
		payload.setIo(buildIo(notifica));
		payload.setBulkId(bulkId);

		return payload;
	}

	private static Sms buildSms(NotificaDto notifica) {
		Sms sms = new Sms();

		if (isContoTerzi(notifica)) {
			sms.setPhone(notifica.getTelefono());
		}

		sms.setContent(notifica.getTesto());

		return sms;
	}

	private static Mex buildMex(NotificaDto notifica) {
		Mex mex = new Mex();

		mex.setTitle(Constants.MEX_TITLE);
		mex.setBody(notifica.getTesto());
		mex.setCallToAction(Constants.MEX_CALL_TO_ACTION);

		return mex;
	}

	private static Io buildIo(NotificaDto notifica) {
		IoContent content = new IoContent();
		content.setSubject(Constants.IO_CONTENT_SUBJECT);
		content.setMarkdown(notifica.getTesto());

		Io io = new Io();
		io.setTimeToLive(Constants.IO_TIME_TO_LIVE);
		io.setContent(content);

		return io;
	}

	private static String getUuid() {
		return UUID.randomUUID().toString();
	}

	private static boolean isContoTerzi(NotificaDto notifica) {
		return StringUtils.isNotBlank(notifica.getTelefono());
	}

}
