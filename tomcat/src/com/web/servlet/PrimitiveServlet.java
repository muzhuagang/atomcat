package com.web.servlet;


import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class PrimitiveServlet implements Servlet {

  public void init(ServletConfig config) throws ServletException {
    System.out.println("init");
  }

  public void service(ServletRequest request, ServletResponse response)
    throws ServletException, IOException {
    System.out.println("from service");
  }

  public void destroy() {
    System.out.println("destroy");
  }

  public String getServletInfo() {
    return null;
  }
  public ServletConfig getServletConfig() {
    return null;
  }
  
	/**
	 * 响应客户端的请求
	 * @param response
	 * @param returnStatus
	 */
	public void responseMessage(HttpServletResponse response,	String returnStatus) {
		response.setHeader("Content-type","text/html;charset=UTF-8");
		try {
			OutputStream stream = response.getOutputStream();
			stream.write(returnStatus.getBytes("UTF-8"));
			stream.flush();
			System.out.println("******number*********"+returnStatus);
		} catch (IOException e) {
			System.out.println("*******io error*********");
			e.printStackTrace();
		}
	}

}
