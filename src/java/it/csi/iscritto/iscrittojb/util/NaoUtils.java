package it.csi.iscritto.iscrittojb.util;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import it.csi.iscritto.iscrittojb.dto.AnagraficaDto;
import it.csi.naosrv.serviceCSI.cxfclient.visuraFamiglia.ArrayOfVisuraSoggetto;
import it.csi.naosrv.serviceCSI.cxfclient.visuraFamiglia.DatiProfilati;
import it.csi.naosrv.serviceCSI.cxfclient.visuraFamiglia.Generalita;
import it.csi.naosrv.serviceCSI.cxfclient.visuraFamiglia.Identita;
import it.csi.naosrv.serviceCSI.cxfclient.visuraFamiglia.UtenteServizio;
import it.csi.naosrv.serviceCSI.cxfclient.visuraFamiglia.VisuraSoggetto;

public final class NaoUtils {
	private NaoUtils() {
		/* NOP */
	}

	public static VisuraSoggetto findSoggettoByCF(it.csi.naosrv.serviceCSI.cxfclient.visuraFamiglia.DettaglioFamiglia dettaglioFamiglia,
			String codiceFiscale) {
		if (dettaglioFamiglia == null || StringUtils.isBlank(codiceFiscale)) {
			return null;
		}

		ArrayOfVisuraSoggetto componentiFamiglia = dettaglioFamiglia.getComponentiFamiglia();
		if (componentiFamiglia == null) {
			return null;
		}

		List<VisuraSoggetto> soggetti = componentiFamiglia.getItem();
		if (CollectionUtils.isEmpty(soggetti)) {
			return null;
		}

		for (VisuraSoggetto soggetto : soggetti) {
			Generalita generalita = soggetto.getGeneralita();
			if (generalita == null) {
				continue;
			}

			if (Utils.areEquals(codiceFiscale, generalita.getCodiceFiscale())) {
				return soggetto;
			}
		}

		return null;
	}

	public static boolean compare(VisuraSoggetto visuraSoggetto, AnagraficaDto soggetto) throws Exception {
		if (visuraSoggetto == null || soggetto == null) {
			return false;
		}

		Generalita generalita = visuraSoggetto.getGeneralita();
		if (generalita == null) {
			return false;
		}

		String dtA = DateUtils.changeFormat(generalita.getDataNascita(), DateUtils.NAO_DATE_FORMAT, DateUtils.DEFAULT_DATE_FORMAT);
		String dtB = soggetto.getDataNascita();

		return dtA != null && dtB != null && dtA.equals(dtB);
	}

	public static DatiProfilati buildDatiProfilati() {
		DatiProfilati result = new DatiProfilati();

		result.setFlgPaternitaMaternita(false);
		result.setFlgProtocolliRiservatiE(false);
		result.setFlgProtocolliRiservatiQ(false);
		result.setFlgRettificheGeneralita(false);
		result.setFlgSoggettiCancellati(false);

		return result;
	}

	public static Identita buildIdentita() {
		Identita result = new Identita();

		result.setCodFiscale(null);
		result.setCognome(null);
		result.setIdProvider(null);
		result.setLivelloAutenticazione(0);
		result.setNome(null);

		return result;
	}

	public static UtenteServizio buildUtenteServizio() {
		UtenteServizio result = new UtenteServizio();

		result.setCodiceUtente("ISCRITTO");
		result.setIdentita(buildIdentita());

		return result;
	}

}
