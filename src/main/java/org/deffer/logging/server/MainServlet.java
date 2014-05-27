package org.deffer.logging.server;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class MainServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;


	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		//config.getServletContext();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		List<String> lines = IOUtils.readLines(req.getInputStream());
		String body = StringUtils.join(lines, " ");
		System.out.println(body);
		try {
			ClientErrorData data = JacksonHelper.deserialize(body, ClientErrorData.class);
			System.out.println(JacksonHelper.serialize(data));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void serveCardPage(HttpServletRequest req, HttpServletResponse resp, String barcode)throws ServletException, IOException{
		req.getSession().removeAttribute("error");
		RequestDispatcher rd =  req.getRequestDispatcher("checked.jsp");
		System.out.println("Forwarding...");
		rd.forward(req, resp);
	}	
		
	
	public void serveMainPage(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		System.out.println("Redirecting to main...");
		resp.sendRedirect("index.html");
	}
	
	public void serveCardNotFound(HttpServletRequest req, HttpServletResponse resp, String barcode)throws ServletException, IOException{
		req.getSession().setAttribute("error", "Card not found!");
		req.getSession().setAttribute("errorDescr", "Barcode: "+barcode);
		req.getSession().removeAttribute("card");
		RequestDispatcher rd =  req.getRequestDispatcher("checked.jsp");
		System.out.println("Forwarding to CardNotFound...");
		rd.forward(req, resp);		
	}
	
	public void serveException(HttpServletRequest req, HttpServletResponse resp, Exception e)throws ServletException, IOException{
		req.getSession().setAttribute("error", "Internal error");
		req.getSession().setAttribute("errorDescr", e.getMessage());
		req.getSession().removeAttribute("card");
		RequestDispatcher rd =  req.getRequestDispatcher("checked.jsp");
		System.out.println("Forwarding to Error...");
		rd.forward(req, resp);
	}
}
