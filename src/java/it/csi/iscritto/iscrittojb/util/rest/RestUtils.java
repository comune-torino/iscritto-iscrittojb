package it.csi.iscritto.iscrittojb.util.rest;

import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_BEGIN;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_END;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_ERROR;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.buildMessage;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import it.csi.iscritto.iscrittojb.exception.ServiceException;

public final class RestUtils {
	private static final Logger log = Logger.getLogger(RestUtils.class);

	private RestUtils() {
		/* NOP */
	}

	public static String callService(String uri, Map<String, String> headers, String user, String password) throws ServiceException {
		CredentialsProvider provider = new BasicCredentialsProvider();
		provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));

		return callService(uri, headers, provider);
	}

	public static String callService(String uri, Map<String, String> headers) throws ServiceException {
		return callService(uri, headers, null);
	}

	public static String callService(String uri, Map<String, String> headers, CredentialsProvider provider) throws ServiceException {
		String result = null;

		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		if (provider != null) {
			clientBuilder.setDefaultCredentialsProvider(provider);
		}

		try (CloseableHttpClient httpClient = clientBuilder.build();) {
			RequestBuilder builder = RequestBuilder.get();
			builder.setUri(uri);

			if (headers != null) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					builder.setHeader(entry.getKey(), entry.getValue());
				}
			}

			HttpUriRequest request = builder.build();
			try (CloseableHttpResponse response = httpClient.execute(request)) {
				StatusLine statusLine = response.getStatusLine();

				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					result = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
				}
			}
		}
		catch (Exception e) {
			throw new ServiceException(e);
		}

		return result;
	}

	//////////////////////////////////////////////////////////////////////

	public static CallResult postJson(String uri, Map<String, String> headers, String json) throws ServiceException {
		final String methodName = "postJson";
		log.debug(buildMessage(RestUtils.class, methodName, LOG_BEGIN));

		CallResult result = null;
		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();) {
			HttpPost httpPost = new HttpPost(uri);
			httpPost.setEntity(new StringEntity(json));

			if (headers != null) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					httpPost.setHeader(entry.getKey(), entry.getValue());
				}
			}

			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
				StatusLine statusLine = response.getStatusLine();

				result = new CallResult();
				result.setStatusCode(statusLine.getStatusCode());
				result.setReasonPhrase(statusLine.getReasonPhrase());

				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					result.setJson(IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8));
				}
			}
		}
		catch (Exception e) {
			log.error(buildMessage(RestUtils.class, methodName, LOG_ERROR), e);
			throw new ServiceException(e);
		}

		log.debug(buildMessage(RestUtils.class, methodName, LOG_END));
		return result;
	}

	public static boolean isCreated(CallResult callResult) {
		return callResult != null && callResult.getStatusCode() == HttpStatus.SC_CREATED;
	}

	public static Integer getStatusCode(CallResult callResult) {
		return callResult == null ? null : callResult.getStatusCode();
	}

}
