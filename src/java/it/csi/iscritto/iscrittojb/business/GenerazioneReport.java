package it.csi.iscritto.iscrittojb.business;

import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_BEGIN;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_END;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_ERROR;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.buildMessage;

import org.apache.log4j.Logger;

import it.csi.iscritto.iscrittojb.integration.TipoGraduatoria;
import it.csi.iscritto.iscrittojb.integration.config.Context;
import it.csi.iscritto.iscrittojb.integration.service.impl.ReportService;

public class GenerazioneReport {
	private static final Logger log = Logger.getLogger(GenerazioneReport.class);

	public static void main(String[] args) {
		final String methodName = "main";

		if (args.length != 1) {
			System.out.println("Utilizzare: java -jar generazione-report.jar [P/D/MP/MD]");
			System.out.println("P: graduatoria provvisoria nidi; D: graduatoria definitiva nidi; PM: graduatoria provvisoria materne; DM: graduatoria definitiva materne");
			System.exit(1);
		}

		try {
			TipoGraduatoria tipoGraduatoria = TipoGraduatoria.findByCod(args[0]);
			if (tipoGraduatoria == null) {
				System.out.println("input non valido");
				System.exit(1);
			}

			execute(tipoGraduatoria);


		}
		catch (Throwable t) {
			log.error(buildMessage(GenerazioneReport.class, methodName, LOG_ERROR), t);
			System.exit(1);
		}

		System.exit(0);
	}

	public static void execute(TipoGraduatoria tipoGraduatoria) throws Exception {
		final String methodName = "execute";
		log.info(buildMessage(GenerazioneReport.class, methodName, LOG_BEGIN));

		ReportService reportService = Context.getInstance().getReportService();

		reportService.generaReport(tipoGraduatoria);

		log.info(buildMessage(GenerazioneReport.class, methodName, LOG_END));
	}



}
