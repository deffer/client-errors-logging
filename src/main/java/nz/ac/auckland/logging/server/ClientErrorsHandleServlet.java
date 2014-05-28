package nz.ac.auckland.logging.server;

import nz.ac.auckland.util.JacksonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ClientErrorsHandleServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	static ClientErrorsLogger errorsLogger;



	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		errorsLogger = new ClientErrorsLogger();
		//config.getServletContext();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		List<String> lines = IOUtils.readLines(req.getInputStream());
		String body = StringUtils.join(lines, " ");
		System.out.println(body);
		try {
			ClientErrorData data = JacksonHelper.deserialize(body, ClientErrorData.class);
			errorsLogger.logClientError(data);
		} catch (Exception e) {
			e.printStackTrace();
			resp.sendError(500, "Unexpected payload");
		}

	}

	/**
	 * In case we want to replace logger with own implementation
	 * @param logger other implementation of a logger
	 */
	public static void setClientErrorsLogger(ClientErrorsLogger logger){
		errorsLogger = logger;
	}
}
