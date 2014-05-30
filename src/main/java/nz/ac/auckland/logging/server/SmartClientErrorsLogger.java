package nz.ac.auckland.logging.server;

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.*;

/**
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
public class SmartClientErrorsLogger implements ClientErrorsLogger{
	private static final Logger LOG = LoggerFactory.getLogger(ClientErrorsLogger.class);

	static int DEFAULT_LOG_CACHE_SIZE = 30;
	static int DEFAULT_EXPIRATION_TIME_SECONDS = 15;

	Cache<ClientErrorData, ValueWrapper> cache;

	public SmartClientErrorsLogger(){
		this(DEFAULT_EXPIRATION_TIME_SECONDS, DEFAULT_LOG_CACHE_SIZE, null);
	}

	public SmartClientErrorsLogger(int expiration, int logCacheSize, Ticker ticker){
		super();
		System.out.println("Creating cache with " + expiration + ":" + logCacheSize);

		if (ticker == null)
			ticker = Ticker.systemTicker();

		cache = CacheBuilder.newBuilder()
				.concurrencyLevel(1)
				.ticker(ticker)
				.expireAfterWrite(expiration, TimeUnit.SECONDS)
				.maximumSize(logCacheSize)
				.removalListener(new RemovalListener<ClientErrorData, ValueWrapper>() {
					@Override
					public void onRemoval(RemovalNotification<ClientErrorData, ValueWrapper> removalNotification) {
						int count = removalNotification.getValue().count != null ? removalNotification.getValue().count : 1;
						writeClientError(removalNotification.getKey(), count);
					}
				})
				.build();


		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

		service.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				cache.cleanUp();
				System.out.println("Cleaning up");
			}
		}, 0, 2, TimeUnit.SECONDS);
	}

	public void logClientError(ClientErrorData errorData){
		try {
			ValueWrapper current = cache.get(errorData, new Callable<ValueWrapper>() {
				@Override
				public ValueWrapper call() throws Exception {
					return new ValueWrapper();
				}
			});
			current.count++;
		} catch (ExecutionException e) {
			e.printStackTrace();
			writeClientError(errorData, 1);
		}
	}

	public static class ValueWrapper {
		Integer count = 0;
	}

	/**
	 * TODO make logging level configurable?
	 *
	 * @param errorData
	 */
	public void writeClientError(ClientErrorData errorData, int count){
		LOG.debug("Javascript error [{}]: {} \nat: {}:{}:{} \nstacktrace: {}",
				count, errorData.message, errorData.file, errorData.line, errorData.column, errorData.errorObj);
	}
}
