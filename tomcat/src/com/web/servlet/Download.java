package com.web.servlet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class Download extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String downloadPath = "/mnt/sdcard/upload/test.png"; // 上传文件的目录
	/**
	 * Constructor of the object.
	 */
	public Download() {
		super();
	}

	/**
	 * 
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doGet");
		doPost(request,response);
	}

    /**
     * 
     */
	public void doPost(HttpServletRequest request, HttpServletResponse response){
		System.out.println("start");
        String path="";
        try {
			path=new String(downloadPath.getBytes("ISO-8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        download(path,request,response);
	}
	
    /**
     * 下载数据
     * @param path
     * @param request
     * @param response
     */
	public void download(String path,HttpServletRequest request,HttpServletResponse response) {
		byte[] buffer = null;
		try {
            // path是指欲下载的文件的路径。
            File file = new File(path);
            // 取得文件名。
            String filename = file.getName();
            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(file));
            buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
		} catch (IOException e) {
			System.out.println("*******io error*********");
			e.printStackTrace();
			responseMessage(response,"fail");
		}
		responseMessage(response,"ok"); //返回成功标志，
		responseFile(response,buffer);
	}

	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		super.service(arg0, arg1);
		System.out.println("hello servlet");
	}
	
	/**
	 * 响应客户端的请求
	 * @param response
	 * @param returnStatus
	 */
	public void responseFile(HttpServletResponse response,	byte[] buffer) {
        try {
			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
		} catch (IOException e) {
			e.printStackTrace();
			responseMessage(response,"download fail");
		}
	}
	
	/**
	 * 响应客户端的请求
	 * @param response
	 * @param returnStatus
	 */
	public void responseMessage(HttpServletResponse response,	String returnStatus) {
		response.setHeader("Content-type","text/html;charset=UTF-8");
		try {
			PrintWriter out = response.getWriter();
			out.println(returnStatus);
			out.flush();
			out.close();
			System.out.println("******number*********"+returnStatus);
		} catch (IOException e) {
			System.out.println("*******io error*********");
			e.printStackTrace();
		}
	}
	
}
