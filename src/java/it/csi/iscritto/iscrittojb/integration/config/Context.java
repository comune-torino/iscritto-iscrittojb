package it.csi.iscritto.iscrittojb.integration.config;

import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_ERROR;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.buildMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import it.csi.iscritto.iscrittojb.integration.dao.InvioNotificaDao;
import it.csi.iscritto.iscrittojb.integration.dao.ReportDao;
import it.csi.iscritto.iscrittojb.integration.dao.VerificaAnagraficaDao;
import it.csi.iscritto.iscrittojb.integration.dao.impl.InvioNotificaDaoImpl;
import it.csi.iscritto.iscrittojb.integration.dao.impl.ReportDaoImpl;
import it.csi.iscritto.iscrittojb.integration.dao.impl.VerificaAnagraficaDaoImpl;
import it.csi.iscritto.iscrittojb.integration.service.impl.InvioNotificaService;
import it.csi.iscritto.iscrittojb.integration.service.impl.ReportService;
import it.csi.iscritto.iscrittojb.integration.service.impl.VerificaAnagraficaService;

public final class Context {
	private static final Logger log = Logger.getLogger(Context.class);
	private static Context instance;

	private DBConfig dbConfig;
	private NotificatoreConfig notificatoreConfig;
	private ReportConfig reportConfig;
	private NaoConfig naoConfig;

	private InvioNotificaDao invioNotificaDao;
	private ReportDao reportDao;
	private VerificaAnagraficaDao verificaAnagraficaDao;

	private InvioNotificaService invioNotificaService;
	private ReportService reportService;
	private VerificaAnagraficaService verificaAnagraficaService;

	private Context() throws Exception {
		loadLog4jConfig();

		this.dbConfig = loadDBConfig();
		this.notificatoreConfig = loadNotificatoreConfig();
		this.reportConfig = loadReportConfig();
		this.naoConfig = loadNaoConfig();

		this.initDao();
		this.initService();
	}

	public static Context getInstance() throws Exception {
		if (instance == null) {
			synchronized (Context.class) {
				if (instance == null) {
					instance = new Context();
				}
			}
		}

		return instance;
	}

	public static final InputStream getResourceFromClassPath(String fileName) {
		return Context.class.getClassLoader().getResourceAsStream(fileName);
	}

	public InvioNotificaService getInvioNotificaService() {
		return this.invioNotificaService;
	}

	public ReportService getReportService() {
		return this.reportService;
	}

	public VerificaAnagraficaService getVerificaAnagraficaService() {
		return this.verificaAnagraficaService;
	}

	//////////////////////////////////////////////////////////////////////

	private void initDao() {
		final String methodName = "initDao";

		try {
			Class.forName("org.postgresql.Driver");
		}
		catch (ClassNotFoundException e) {
			log.error(buildMessage(Context.class, methodName, LOG_ERROR), e);
		}

		this.invioNotificaDao = new InvioNotificaDaoImpl(this.dbConfig);
		this.reportDao = new ReportDaoImpl(this.dbConfig);
		this.verificaAnagraficaDao = new VerificaAnagraficaDaoImpl(this.dbConfig);
	}

	private void initService() {
		this.invioNotificaService = new InvioNotificaService();
		this.invioNotificaService.setInvioNotificaDao(this.invioNotificaDao);
		this.invioNotificaService.setNotificatoreConfig(this.notificatoreConfig);

		this.reportService = new ReportService();
		this.reportService.setReportDao(this.reportDao);
		this.reportService.setReportConfig(this.reportConfig);

		this.verificaAnagraficaService = new VerificaAnagraficaService();
		this.verificaAnagraficaService.setVerificaAnagraficaDao(this.verificaAnagraficaDao);
		this.verificaAnagraficaService.setNaoConfig(this.naoConfig);
	}

	private static void loadLog4jConfig() {
		PropertyConfigurator.configure(Context.class.getClassLoader().getResource("log4j.properties"));
	}

	private static DBConfig loadDBConfig() throws IOException {
		final Properties properties = loadConfig("config.properties");
		DBConfig config = new DBConfig();

		config.setDbUrl(properties.getProperty("stringaConnessioneDatabase"));
		config.setDbUser(properties.getProperty("username"));
		config.setDbPassword(properties.getProperty("password"));

		return config;
	}

	private static NotificatoreConfig loadNotificatoreConfig() throws IOException {
		final Properties properties = loadConfig("config.properties");
		NotificatoreConfig config = new NotificatoreConfig();

		config.setMessagebrokerUrl(properties.getProperty("notificatore.ws.messagebroker.url"));
		config.setMessagebrokerToken(properties.getProperty("notificatore.ws.messagebroker.tokenjwt"));

		return config;
	}

	private static ReportConfig loadReportConfig() throws IOException {
		final Properties properties = loadConfig("config.properties");
		ReportConfig config = new ReportConfig();

		config.setProvOrdineScuola(properties.getProperty("nid.grad.prov.cod.ordine.scuola"));
		config.setProvCodStato(properties.getProperty("nid.grad.prov.cod.stato"));
		config.setProvNomeReport(properties.getProperty("nid.grad.prov.nome.report"));
		config.setProvOutputDir(properties.getProperty("nid.grad.prov.output.dir"));

		config.setDefOrdineScuola(properties.getProperty("nid.grad.def.cod.ordine.scuola"));
		config.setDefCodStato(properties.getProperty("nid.grad.def.cod.stato"));
		config.setDefNomeReport(properties.getProperty("nid.grad.def.nome.report"));
		config.setDefOutputDir(properties.getProperty("nid.grad.def.output.dir"));

		config.setProvOrdineScuolaMat(properties.getProperty("mat.grad.prov.cod.ordine.scuola"));
		config.setProvCodStatoMat(properties.getProperty("mat.grad.prov.cod.stato"));
		config.setProvNomeReportMat(properties.getProperty("mat.grad.prov.nome.report"));
		config.setProvOutputDirMat(properties.getProperty("mat.grad.prov.output.dir"));

		config.setDefOrdineScuolaMat(properties.getProperty("mat.grad.def.cod.ordine.scuola"));
		config.setDefCodStatoMat(properties.getProperty("mat.grad.def.cod.stato"));
		config.setDefNomeReportMat(properties.getProperty("mat.grad.def.nome.report"));
		config.setDefOutputDirMat(properties.getProperty("mat.grad.def.output.dir"));

		return config;
	}

	private static NaoConfig loadNaoConfig() throws IOException {
		final Properties properties = loadConfig("config.properties");
		NaoConfig config = new NaoConfig();

		config.setBasePath(properties.getProperty("nao.service.url"));
		config.setUser(properties.getProperty("nao.service.user"));
		config.setPassword(properties.getProperty("nao.service.password"));
		config.setDelay(Integer.valueOf(properties.getProperty("nao.service.delay")));

		return config;
	}

	private static Properties loadConfig(String fileName) throws IOException {
		final Properties properties = new Properties();
		try (final InputStream stream = Context.class.getClassLoader().getResourceAsStream(fileName)) {
			if (stream == null) {
				throw new IllegalArgumentException();
			}

			properties.load(stream);

			return properties;
		}
	}

}
