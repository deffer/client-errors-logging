package nz.ac.auckland.logging.server;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
public class SmartClientErrorsLogger implements ClientErrorsLogger{
	private static final Logger LOG = LoggerFactory.getLogger(ClientErrorsLogger.class);

	int LOG_CACHE_SIZE = 30;
	int EXPIRATION_TIME_SECONDS = 15;

	Cache<ClientErrorData, Integer> cache = CacheBuilder.newBuilder()
			.expireAfterWrite(EXPIRATION_TIME_SECONDS, TimeUnit.SECONDS)
			.maximumSize(LOG_CACHE_SIZE)
			.removalListener(new RemovalListener<Object, Object>() {
				@Override
				public void onRemoval(RemovalNotification<Object, Object> objectObjectRemovalNotification) {

				}
			})
			.build();


	/**
	 * TODO make logging level configurable?
	 *
	 * @param errorData
	 */
	public void logClientError(ClientErrorData errorData){
		LOG.debug("Javascript error: {} \nat: {}:{}:{} \nstacktrace: {}", errorData.message, errorData.file, errorData.line, errorData.column, errorData.errorObj);
	}
}
