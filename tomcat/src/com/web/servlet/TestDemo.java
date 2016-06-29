package com.web.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestDemo extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public TestDemo() {
		super();
	}

	/**
	 * 
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doGet");
		doPost(request, response);
	}

	/**
     * 
     */
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("start");
		PrintWriter out;
		try {
			out = response.getWriter();
			out.println("hello world");
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

//		responseMessage(response, "ll ok");
	}

	/**
	 * 响应客户端的请求
	 * 
	 * @param response
	 * @param returnStatus
	 */
	public void responseMessage(HttpServletResponse response,String returnStatus) {
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		Cookie cookie = new Cookie("cxxoo", "yousuidf!");
		cookie.setMaxAge(24*3600);
		cookie.setPath("/ddaaademocookie");
		response.addCookie(cookie);
		
		try {
			OutputStream stream = response.getOutputStream();
			stream.write(returnStatus.getBytes("UTF-8"));
			stream.flush();
			System.out.println("******number*********" + returnStatus);
		} catch (IOException e) {
			System.out.println("*******io error*********");
			e.printStackTrace();
		}
	}

}
