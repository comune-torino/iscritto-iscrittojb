package it.csi.iscritto.iscrittojb.integration.converter;

import it.csi.iscritto.iscrittojb.dto.NotificaDto;
import it.csi.iscritto.iscrittojb.integration.dao.dto.NotificaRow;
import it.csi.iscritto.iscrittojb.util.converter.AbstractConverter;

public class NotificaConverter extends AbstractConverter<NotificaRow, NotificaDto> {

	@Override
	public NotificaDto convert(NotificaRow source) {
		NotificaDto target = null;
		if (source != null) {
			target = new NotificaDto();

			target.setCodiceFiscaleRichiedente(source.getCodiceFiscaleRichiedente());
			target.setDtInserimento(source.getDtInserimento());
			target.setDtInvio(source.getDtInvio());
			target.setEsito(source.getEsito());
			target.setIdDomandaIscrizione(source.getIdDomandaIscrizione());
			target.setIdInvioSms(source.getIdInvioSms());
			target.setTelefono(source.getTelefono());
			target.setTesto(source.getTesto());
		}

		return target;
	}

}
