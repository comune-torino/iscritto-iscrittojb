package it.csi.iscritto.iscrittojb.integration.converter;

import it.csi.iscritto.iscrittojb.dto.AnagraficaDto;
import it.csi.iscritto.iscrittojb.integration.dao.dto.AnagraficaRow;
import it.csi.iscritto.iscrittojb.util.DateUtils;
import it.csi.iscritto.iscrittojb.util.converter.AbstractConverter;

public class AnagraficaConverter extends AbstractConverter<AnagraficaRow, AnagraficaDto> {

	@Override
	public AnagraficaDto convert(AnagraficaRow source) {
		AnagraficaDto target = null;
		if (source != null) {
			target = new AnagraficaDto();

			target.setCodiceFiscale(source.getCodiceFiscale());
			target.setCodTipoSoggetto(source.getCodTipoSoggetto());
			target.setDataNascita(DateUtils.toStringDate(source.getDataNascita(), DateUtils.DEFAULT_DATE_FORMAT));
			target.setIdAnagraficaSoggetto(source.getIdAnagraficaSoggetto());
			target.setIdDomandaIscrizione(source.getIdDomandaIscrizione());
		}

		return target;
	}

}
