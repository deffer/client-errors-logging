package org.deffer.logging.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
public class ClientErrorsLogger {
	private static final Logger LOG = LoggerFactory.getLogger(ClientErrorsLogger.class);

	/**
	 * TODO make logging level configurable?
	 *
	 * @param errorData
	 */
	public void logClientError(ClientErrorData errorData){
		LOG.debug("Javascript error: {} \nat: {}:{}:{} \nstacktrace: {}", errorData.message, errorData.file, errorData.line, errorData.column, errorData.errorObj);
	}
}
