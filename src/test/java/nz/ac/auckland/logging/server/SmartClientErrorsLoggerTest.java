package nz.ac.auckland.logging.server;


import com.google.common.base.Ticker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SmartClientErrorsLoggerTest {


	/**
	 * Make sure message object can be used as a key in cache
	 */
	@Test
	public void testCacheKey(){
		assert generateErrorA().equals(generateErrorA());
		assert generateErrorA().hashCode() == generateErrorA().hashCode();

		HashMap<ClientErrorData, Integer> testKeys = new HashMap<>();
		testKeys.put(generateErrorA(), 2);
		testKeys.put(generateErrorA(), 3);

		assert testKeys.size() == 1;
	}

	/**
	 * Make sure same errors are 'merged' into one and counter is updated
	 */
	@Test
	public void testGrouping(){

		SmartClient logger = new SmartClient(2, 15, null);

		logger.logClientError(generateErrorA());
		logger.logClientError(generateErrorA());
		logger.logClientError(generateErrorA());
		logger.logClientError(generateErrorA());

		// TODO use ticker
		try {
			// wait for clean up to kick in
			Thread.sleep(2100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(logger.calls.size());
		assert logger.calls.size()==1;
		assert logger.calls.iterator().next()[1].equals(4);
	}

	/**
	 * Make sure it starts logging errors as soon as it reaches maximum size)
	 */
	@Test
	public void testCacheMaximumSize(){
		SmartClient logger = new SmartClient(20, 5, null);

		logger.logClientError(generateError("a"));
		logger.logClientError(generateError("b"));
		logger.logClientError(generateError("c"));
		logger.logClientError(generateError("d"));
		logger.logClientError(generateError("e"));
		logger.logClientError(generateError("f"));


		System.out.println(logger.calls.size());
		assert logger.calls.size()>=1;

	}


	/**
	 * Make sure it logs all errors as soon theit time expires
	 */
	@Test
	public void testCacheExpiration(){
		final long delay = 16;
		final Wrapper flagHolder = new Wrapper();
		SmartClient logger = new SmartClient((int)delay, 15, new Ticker() {
			@Override
			public long read() {
			if (flagHolder.start){
				return System.nanoTime() + (delay+1L)*1000L*1000000L;
			}else{
				return System.nanoTime();
			}
			}
		});

		logger.logClientError(generateErrorA());
		logger.logClientError(generateErrorA());
		logger.logClientError(generateErrorA());
		logger.logClientError(generateErrorA());

		flagHolder.start = true;
		logger.cache.cleanUp();

		try {
			// wait for clean up to finish
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assert logger.calls.size()>=1;
	}

	/**
	 * To signal ticker to start showing 'later' time
	 */
	public static class Wrapper {
		boolean start = false;
	}

	/**
	 * Capture 'writes' to underlying logger
	 */
	public static class SmartClient extends SmartClientErrorsLogger{
		List<Object[]> calls = new ArrayList<>();

		public SmartClient(int expiration, int logCacheSize, Ticker ticker){
			super(expiration, logCacheSize, ticker);
		}

		@Override
		public void writeClientError(ClientErrorData errorData, int count){
			calls.add(new Object[]{errorData, count});
		}
	}

	private ClientErrorData generateErrorA(){
		return generateError("a");
	}

	private ClientErrorData generateErrorB(){
		return generateError("b");
	}

	private ClientErrorData generateError(String modifier){
		ClientErrorData result = new ClientErrorData();
		result.message = modifier.toUpperCase()+" message";
		result.file = modifier+".js";
		result.line = 150l;
		result.errorObj = "something something something\ndark side";
		return result;
	}

}
