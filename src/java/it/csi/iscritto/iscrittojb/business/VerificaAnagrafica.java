package it.csi.iscritto.iscrittojb.business;

import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_BEGIN;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_END;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_ERROR;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.buildMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import it.csi.iscritto.iscrittojb.dto.AnagraficaDto;
import it.csi.iscritto.iscrittojb.exception.ServiceException;
import it.csi.iscritto.iscrittojb.integration.config.Context;
import it.csi.iscritto.iscrittojb.integration.service.impl.VerificaAnagraficaService;
import it.csi.iscritto.iscrittojb.util.NaoUtils;
import it.csi.naosrv.serviceCSI.cxfclient.visuraFamiglia.DettaglioFamiglia;
import it.csi.naosrv.serviceCSI.cxfclient.visuraFamiglia.VisuraSoggetto;

public class VerificaAnagrafica {
	private static final Logger log = Logger.getLogger(VerificaAnagrafica.class);

	public static void main(String[] args) {
		final String methodName = "main";

		if (args.length != 0) {
			System.out.println("Utilizzare: java -jar verifica-anagrafica.jar");
			System.exit(1);
		}

		try {
			execute();
		}
		catch (Throwable t) {
			log.error(buildMessage(VerificaAnagrafica.class, methodName, LOG_ERROR), t);
			System.exit(1);
		}

		System.exit(0);
	}

	public static void execute() throws Exception {
		final String methodName = "execute";
		log.info(buildMessage(VerificaAnagrafica.class, methodName, LOG_BEGIN));

		VerificaAnagraficaService anagraficaService = Context.getInstance().getVerificaAnagraficaService();

		List<AnagraficaDto> soggetti = anagraficaService.getAnagraficaSoggetti();
		log.info(buildMessage(VerificaAnagrafica.class, methodName, "soggetti: " + buildInfoSoggetti(soggetti)));

		// gestione minori e altri componenti
		List<AnagraficaDto> minori = getSoggettiByTipo(soggetti, "MIN");
		log.info(buildMessage(VerificaAnagrafica.class, methodName, "MIN: " + buildInfoSoggetti(minori)));

		for (AnagraficaDto minore : minori) {
			log.info(buildMessage(VerificaAnagrafica.class, methodName, getInfoSoggetto(minore)));

			List<AnagraficaDto> altriComponenti = getAltriComponenti(soggetti, minore);
			log.info(buildMessage(VerificaAnagrafica.class, methodName, "ALT_CMP: " + buildInfoSoggetti(altriComponenti)));

			DettaglioFamiglia dettaglioFamiglia = anagraficaService.getVisuraFamiglia(minore.getCodiceFiscale());

			final boolean presenteInNao = isPresenteInNao(dettaglioFamiglia, minore.getCodiceFiscale());
			log.info(buildMessage(VerificaAnagrafica.class, methodName, "minore presente in nao: " + presenteInNao));

			if(presenteInNao && dettaglioFamiglia != null && dettaglioFamiglia.getIndirizzoResidenzaFamiglia() != null) {
				log.info(buildMessage(VerificaAnagrafica.class, methodName, "@@id_civico Int : " + dettaglioFamiglia.getIndirizzoResidenzaFamiglia().getIdCivico()));
				Long idCivico = new Long(dettaglioFamiglia.getIndirizzoResidenzaFamiglia().getIdCivico());
				log.info(buildMessage(VerificaAnagrafica.class, methodName, "@@id_civico Long: " + idCivico));
				anagraficaService.updateIdCivico(minore, idCivico);
			}


			anagraficaService.updateFlagResidenza(minore, presenteInNao);
			for (AnagraficaDto componente : altriComponenti) {
				VisuraSoggetto visuraSoggetto = NaoUtils.findSoggettoByCF(dettaglioFamiglia, componente.getCodiceFiscale());
				if (NaoUtils.compare(visuraSoggetto, componente)) {
					anagraficaService.updateFlagResidenza(componente, presenteInNao);
				}
			}
		}

		// gestione soggetti 2 e 3
		updateResidenzaSoggetti(soggetti, "SOG2");
		updateResidenzaSoggetti(soggetti, "SOG3");

		anagraficaService.updateUltimaVerifica();

		log.info(buildMessage(VerificaAnagrafica.class, methodName, LOG_END));
	}

	//////////////////////////////////////////////////////////////////////

	private static boolean isPresenteInNao(DettaglioFamiglia dettaglioFamiglia, String codiceFiscale) {
		return NaoUtils.findSoggettoByCF(dettaglioFamiglia, codiceFiscale) != null;
	}

	private static void updateResidenzaSoggetti(List<AnagraficaDto> soggetti, String tipoSoggetto) throws Exception {
		final String methodName = "updateResidenzaSoggetti";

		VerificaAnagraficaService anagraficaService = Context.getInstance().getVerificaAnagraficaService();

		List<AnagraficaDto> soggettiByTipo = getSoggettiByTipo(soggetti, tipoSoggetto);
		log.info(buildMessage(VerificaAnagrafica.class, methodName, tipoSoggetto + ": " + buildInfoSoggetti(soggettiByTipo)));

		for (AnagraficaDto soggetto : soggettiByTipo) {
			log.info(buildMessage(VerificaAnagrafica.class, methodName, getInfoSoggetto(soggetto)));

			DettaglioFamiglia dettaglioFamiglia = anagraficaService.getVisuraFamiglia(soggetto.getCodiceFiscale());

			final boolean presenteInNao = isPresenteInNao(dettaglioFamiglia, soggetto.getCodiceFiscale());
			log.info(buildMessage(VerificaAnagrafica.class, methodName, "soggetto presente in nao: " + presenteInNao));

			anagraficaService.updateFlagResidenza(soggetto, presenteInNao);
		}
	}

	private static String getInfoSoggetto(AnagraficaDto soggetto) {
		StringBuilder sb = new StringBuilder();
		if (soggetto != null) {
			sb.append("id domanda: ").append(soggetto.getIdDomandaIscrizione()).append("; ");
			sb.append("id soggetto: ").append(soggetto.getIdAnagraficaSoggetto()).append("; ");
			sb.append("tipo soggetto: ").append(soggetto.getCodTipoSoggetto()).append("; ");
			sb.append("data nascita: ").append(soggetto.getDataNascita()).append("; ");
		}

		return sb.toString();
	}

	private static String buildInfoSoggetti(List<AnagraficaDto> soggetti) {
		return Optional.ofNullable(soggetti).orElse(new ArrayList<>())
				.stream()
				.filter(Objects::nonNull)
				.map(x -> x.getIdAnagraficaSoggetto())
				.filter(Objects::nonNull)
				.map(x -> String.valueOf(x))
				.collect(Collectors.joining(", "));
	}

	private static List<AnagraficaDto> getSoggettiByTipo(List<AnagraficaDto> soggetti, String codTipoSoggetto) throws ServiceException {
		return Optional.of(soggetti).orElse(new ArrayList<>())
				.stream()
				.filter(Objects::nonNull)
				.filter(x -> codTipoSoggetto.equalsIgnoreCase(x.getCodTipoSoggetto()))
				.collect(Collectors.toList());
	}

	private static List<AnagraficaDto> getAltriComponenti(List<AnagraficaDto> soggetti, AnagraficaDto minore) {
		return Optional.ofNullable(soggetti).orElse(new ArrayList<>())
				.stream()
				.filter(Objects::nonNull)
				.filter(x -> x.getIdDomandaIscrizione().equals(minore.getIdDomandaIscrizione()))
				.filter(x -> "ALT_CMP".equalsIgnoreCase(x.getCodTipoSoggetto()))
				.collect(Collectors.toList());
	}

}
