package nz.ac.auckland.logging.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
public interface ClientErrorsLogger{
	public void logClientError(ClientErrorData errorData);
}
