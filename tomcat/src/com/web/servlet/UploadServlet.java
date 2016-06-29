package com.web.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;

import com.cat.core.ServletMappingInfo;

import android.content.Context;
import android.telephony.TelephonyManager;

public class UploadServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UploadServlet() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doGet");
		String str=request.getParameter("name");
		System.out.println("参数是="+str);
		String imei="";
		try {
			Context mContext= ServletMappingInfo.getAContext();
			TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
			imei = tm.getDeviceId();
//			imei=request.getContextPath();
		} catch (Exception e) {
			e.printStackTrace();
			responseMessage(response,"get phone exception");
		}
		responseMessage(response,imei);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response){
		System.out.println("doPost");
		Context mContext= ServletMappingInfo.getAContext();
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		responseMessage(response,imei);
	}
	
	/**
	 * 响应客户端的请求
	 * @param response
	 * @param returnStatus
	 */
	public void responseMessage(HttpServletResponse response,String returnStatus) {
//		response.setHeader("Content-type","text/html;charset=UTF-8");
//		response.setHeader("Content-type","text/html;charset=gb2312");
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
