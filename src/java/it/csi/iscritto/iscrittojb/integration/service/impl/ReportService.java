package it.csi.iscritto.iscrittojb.integration.service.impl;

import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_BEGIN;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_END;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_ERROR;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.buildMessage;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import it.csi.iscritto.iscrittojb.exception.ServiceException;
import it.csi.iscritto.iscrittojb.integration.TipoGraduatoria;
import it.csi.iscritto.iscrittojb.integration.config.Context;
import it.csi.iscritto.iscrittojb.integration.config.ReportConfig;
import it.csi.iscritto.iscrittojb.integration.dao.ReportDao;
import it.csi.iscritto.iscrittojb.integration.dao.dto.DatiScuola;
import it.csi.iscritto.iscrittojb.integration.dao.dto.DatiStepRow;
import it.csi.iscritto.iscrittojb.integration.service.AbstractService;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public class ReportService extends AbstractService {
	private static final Logger log = Logger.getLogger(ReportService.class);

	private ReportDao reportDao;
	private ReportConfig reportConfig;

	public void setReportDao(ReportDao reportDao) {
		this.reportDao = reportDao;
	}

	public void setReportConfig(ReportConfig reportConfig) {
		this.reportConfig = reportConfig;
	}

	//////////////////////////////////////////////////////////////////////

	public void generaReport(TipoGraduatoria tipoGraduatoria) throws ServiceException {
		final String methodName = "generaReport";
		log.debug(buildMessage(getClass(), methodName, LOG_BEGIN));

		log.info(buildMessage(getClass(), methodName, "tipoGraduatoria: " + tipoGraduatoria));
		Validate.notNull(tipoGraduatoria);

		try {
			String codOrdineScuola;
			String codStatoGra;

			switch (tipoGraduatoria) {
			case PROVVISORIA:
				codOrdineScuola = this.reportConfig.getProvOrdineScuola();
				codStatoGra = this.reportConfig.getProvCodStato();
				break;
			case DEFINITIVA:
				codOrdineScuola = this.reportConfig.getDefOrdineScuola();
				codStatoGra = this.reportConfig.getDefCodStato();
				break;
			case PROVVISORIA_MAT:
				codOrdineScuola = this.reportConfig.getProvOrdineScuolaMat();
				codStatoGra = this.reportConfig.getProvCodStatoMat();
				break;
			case DEFINITIVA_MAT:
				codOrdineScuola = this.reportConfig.getDefOrdineScuolaMat();
				codStatoGra = this.reportConfig.getDefCodStatoMat();
				break;
			default:
				throw new ServiceException("tipo graduatoria non gestita");
			}

			log.info(buildMessage(getClass(), methodName, "codOrdineScuola: " + codOrdineScuola));
			log.info(buildMessage(getClass(), methodName, "codOrdicodStatoGraneScuola: " + codStatoGra));

			DatiStepRow datiStep = this.reportDao.findDatiStep(codOrdineScuola, codStatoGra);
			if (datiStep == null) {
				throw new ServiceException("non e' stato possibile reperire lo step e l'anno scolastico.");
			}

			Long idStep = datiStep.getIdStep();
			Long idAnnoScolastico = datiStep.getIdAnnoScolastico();

			log.info(buildMessage(getClass(), methodName, "idStep: " + idStep));
			log.info(buildMessage(getClass(), methodName, "idAnnoScolastico: " + idAnnoScolastico));

			if (idStep == null || idAnnoScolastico == null) {
				throw new ServiceException("errore nella configurazione della graduatoria");
			}


			List<DatiScuola> datiScuole = this.reportDao.findDatiScuole(idAnnoScolastico, codOrdineScuola);
			log.info(buildMessage(getClass(), methodName, "datiScuole: " + CollectionUtils.size(datiScuole)));

			if (CollectionUtils.isEmpty(datiScuole)) {
				throw new ServiceException("codici scuola non trovati");
			}

			this.generaReportPdf(datiScuole, idStep, tipoGraduatoria);

		} catch (Exception e) {
			log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			throw new ServiceException(e);
		}

		log.debug(buildMessage(getClass(), methodName, LOG_END));
	}

	public void generaReportPdf(List<DatiScuola> datiScuole, Long idStep, TipoGraduatoria tipoGraduatoria)
			throws ServiceException {
		final String methodName = "generaReportPdf";
		log.debug(buildMessage(getClass(), methodName, LOG_BEGIN));

		log.info(buildMessage(getClass(), methodName, "idStep: " + idStep));
		log.info(buildMessage(getClass(), methodName, "tipoGraduatoria: " + tipoGraduatoria));
		Validate.notNull(idStep);
		Validate.notNull(tipoGraduatoria);

		Connection connection = null;
		try {
			String nomeReport;
			String outputDir;

			switch (tipoGraduatoria) {
			case PROVVISORIA:
				nomeReport = this.reportConfig.getProvNomeReport();
				outputDir = this.reportConfig.getProvOutputDir();
				break;
			case DEFINITIVA:
				nomeReport = this.reportConfig.getDefNomeReport();
				outputDir = this.reportConfig.getDefOutputDir();
				break;
			case PROVVISORIA_MAT:
				nomeReport = this.reportConfig.getProvNomeReportMat();
				outputDir = this.reportConfig.getProvOutputDirMat();
				break;
			case DEFINITIVA_MAT:
				nomeReport = this.reportConfig.getDefNomeReportMat();
				outputDir = this.reportConfig.getDefOutputDirMat();
				break;
			default:
				throw new ServiceException("tipo graduatoria non gestita");
			}

			log.info(buildMessage(getClass(), methodName, "nomeReport: " + nomeReport));
			log.info(buildMessage(getClass(), methodName, "outputDir: " + outputDir));

			checkOutputDir(outputDir);

			JasperReport jasperReport = JasperCompileManager
					.compileReport(Context.getResourceFromClassPath(nomeReport));
			log.info(buildMessage(getClass(), methodName, String.format("report %s compilato", nomeReport)));

			connection = this.reportDao.openAndGetNewConnection();

			for (DatiScuola ds : datiScuole) {
				Long idScuola = ds.getIdScuola();
				Long idTipoFreq = ds.getIdTipoFrequenza();
				String codScuola = ds.getCodScuola();
				String codTipoFreq = ds.getTipoFrequenza();

				String reportFileName = codScuola + "_" + codTipoFreq + ".pdf";
				String reportPath = Paths.get(outputDir, reportFileName).toString();

				log.info(buildMessage(getClass(), methodName, "reportPath: " + reportPath));

				JasperPrint print = JasperFillManager.fillReport(jasperReport,
						mapParameters(connection, idScuola, idTipoFreq, idStep));
				JasperExportManager.exportReportToPdfFile(print, reportPath);

				log.info(buildMessage(getClass(), methodName, "generato report: " + reportFileName));
			}
		} catch (Exception e) {
			log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			throw new ServiceException(e);
		} finally {
			this.reportDao.closeNewConnection(connection);
		}
	}

	//////////////////////////////////////////////////////////////////////

	private static void checkOutputDir(String outputDir) {
		if (StringUtils.isBlank(outputDir)) {
			throw new IllegalArgumentException("outputDir non configurata");
		}

		File directory = new File(outputDir);

		if (!directory.exists() || !directory.isDirectory()) {
			throw new IllegalArgumentException("outputDir non presente");
		}

		if (directory.list().length > 0) {
			throw new IllegalArgumentException("outputDir non vuota");
		}
	}

	private static HashMap<String, Object> mapParameters(Connection conn, Long idScuola, Long idTipoFrequenza,
			Long idStepGracon) {
		HashMap<String, Object> map = new HashMap<>();

		map.put(JRParameter.REPORT_LOCALE, Locale.ITALY);

		map.put("REPORT_CONNECTION", conn);
		map.put("IDSCUOLA", new BigDecimal(idScuola.longValue()));
		map.put("IDTIPOFREQUENZA", new BigDecimal(idTipoFrequenza.longValue()));
		map.put("IDSTEPGRACON", new BigDecimal(idStepGracon.longValue()));

		return map;
	}

}
