package com.cat.core;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.cat.core.http.HttpRequest;
import com.cat.core.http.HttpRequestFacade;
import com.cat.core.http.HttpResponse;
import com.cat.core.http.HttpResponseFacade;

/**
 * 处理动态请求
 * @author Administrator
 *
 */
public class ServletProcessor {

	/**
	 * 1.解析请求连接、
	 * 2.构建自定义类加载器，加载解析后的servlet
	 * 3.生成servlet实例并且执行该servlet的service方法
	 * @param request
	 * @param response
	 */
	public void process(HttpRequest request,HttpResponse response){
		System.out.println("process=");
		String uri = request.getRequestURI();                     //获取请求的servlet连接例如/servlet/servletName
		int lastSlash = uri.lastIndexOf("/");
		String servletName = uri.substring(lastSlash + 1); //截取servlet的名字及类名
        System.out.println("servletname="+servletName);
		
        //在此处可以以后添加拦截、过滤
        String servletPath=ServletMappingInfo.getServletMap().get(servletName);
		
		try {
        if(null == servletPath){
        	System.out.println("");
			PrintWriter out = response.getWriter();
			out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
			out.println("<HTML>");
			out.println("  <HEAD><TITLE>illegal</TITLE></HEAD>");
			out.println("  <BODY>");
			out.print("  不支持 404错误");
			out.println("  </BODY>");
			out.println("</HTML>");
			out.flush();
			out.close();
			return;
        }
		Servlet servlet = null;

			servlet = (Servlet)Class.forName(servletPath).newInstance();        //实例化servlet类
			HttpRequestFacade requestFacade = new HttpRequestFacade(request);
			HttpResponseFacade responseFacade = new HttpResponseFacade(response);
		    ((HttpResponse) response).finishResponse();
		    servlet.service(requestFacade, responseFacade);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}