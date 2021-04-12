package it.csi.iscritto.iscrittojb.integration.service;

import static it.csi.iscritto.iscrittojb.util.LoggingUtils.buildMessage;

import javax.xml.ws.BindingProvider;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.log4j.Logger;

import it.csi.iscritto.iscrittojb.business.VerificaAnagrafica;
import it.csi.iscritto.iscrittojb.exception.ServiceException;
import it.csi.naosrv.serviceCSI.cxfclient.visuraFamiglia.JavaServiceDesc;
import it.csi.naosrv.serviceCSI.cxfclient.visuraFamiglia.JavaServiceDescService;

public final class NaoServiceLocator {
	private static final Logger log = Logger.getLogger(VerificaAnagrafica.class);

	private static JavaServiceDesc serviceVisuraFamiglia;
	private static final Long CONNECTION_TIMEOUT = 300000L;

	public static synchronized JavaServiceDesc getVisuraFamigliaService(String baseUrl, String user, String password) throws ServiceException {
		final String methodName = "getVisuraFamigliaService";

		if (serviceVisuraFamiglia == null) {
			log.info(buildMessage(NaoServiceLocator.class, methodName, "creazione client serviceVisuraFamiglia"));

			try {
				// http://stackoverflow.com/questions/20114945/cxf-web-service-client-cannot-create-a-secure-xmlinputfactory
				System.setProperty("org.apache.cxf.stax.allowInsecureParser", "1");

				serviceVisuraFamiglia = new JavaServiceDescService().getSrvVisuraFamiglia();
				BindingProvider bindingProvider = (BindingProvider) serviceVisuraFamiglia;
				bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, baseUrl + "/SrvVisuraFamiglia");
				bindingProvider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, user);
				bindingProvider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);

				Client client = ClientProxy.getClient(serviceVisuraFamiglia);
				HTTPConduit conduit = (HTTPConduit) client.getConduit();
				HTTPClientPolicy policy = conduit.getClient();
				policy.setConnectionTimeout(CONNECTION_TIMEOUT);
				policy.setReceiveTimeout(CONNECTION_TIMEOUT);
				policy.setConnection(ConnectionType.CLOSE);
			}
			catch (Exception e) {
				log.error(buildMessage(NaoServiceLocator.class, methodName, e.getMessage()));

				throw new ServiceException(e);
			}
		}

		return serviceVisuraFamiglia;
	}

}
