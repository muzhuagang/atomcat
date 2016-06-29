package com.cat.core;

import java.net.Socket;
import java.io.OutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.cat.core.http.HttpHeader;
import com.cat.core.http.HttpRequest;
import com.cat.core.http.HttpRequestLine;
import com.cat.core.http.HttpResponse;
import com.cat.core.http.SocketInputStream;
import com.cat.core.util.FastHttpDateFormat;
import com.cat.core.util.RequestUtil;

/**
 * 
 * 创建请求和响应对象即生成HttpRequest对象和HttpResponse对象
 * 
 * @author Administrator
 *
 */
public class HttpProcessor {

  public HttpProcessor(HttpConnector connector) {
    this.connector = connector;
  }
  /**
   * The HttpConnector with which this processor is associated.
   */
  private HttpConnector connector = null;                        //连接器
  private HttpRequest request;                                   //请求工作
  private HttpRequestLine requestLine = new HttpRequestLine();   //获取http头信息
  private HttpResponse response;                                 //响应信息

  protected String method = null;
  protected String queryString = null;

  /**
   * The string manager for this package.
   */
//  protected StringManager sm =
//    StringManager.getManager("com.cat.core.http.http");

  public boolean process(Socket socket) {
    SocketInputStream input = null;            //http请求头的流信息，获取http的相关信息
    OutputStream output = null;                //响应流
    try {
      input = new SocketInputStream(socket.getInputStream(), 2048);    //定义它的大小
      output = socket.getOutputStream();

      // create HttpRequest object and parse
      request = new HttpRequest(input);       //实例化请求

      // create HttpResponse object
      response = new HttpResponse(output);    //实例化响应
      response.setRequest(request);

      response.setHeader("Server", "zkt Servlet Container");           //设置响应的头部的服务器信息
      response.setHeader("Date", FastHttpDateFormat.getCurrentDate()); //设置响应的头部的日期信息
      response.setHeader("Connection", "keep-alive");                  //设置支持HTTP1.1协议的长连接
      
      parseRequest(input, output);         //解析请求信息获取请求的类型、资源链接、http协议版本，session信息        将其值放入request对象值中
      parseHeaders(input);                 //解析请求信息设置请求的头信息，包括常用的一些类型和cooke和文本类型   将其值放入request对象值中

      //check if this is a request for a servlet or a static resource
      //a request for a servlet begins with "/servlet/"
      if (request.getRequestURI().startsWith("/servlet/")) { //以这个请求的提交到动态请求区域
        ServletProcessor processor = new ServletProcessor();
        processor.process(request, response);
      }else if(request.getRequestURI().startsWith("/SHUTDOWN")){
    	  ((HttpResponse) response).finishResponse();
    	  responseMessage(response,"shutdown");
      } else { //以这个请求的提交到静态请求区域
        StaticResourceProcessor processor = new StaticResourceProcessor();
        processor.process(request, response);
      }

      // Close the socket
      socket.close();
      // no shutdown for this application
    }
    catch (Exception e) {
      e.printStackTrace();
      return true;    //异常情况则退出
    }
    System.out.println("uri="+request.getRequestURI()+" url="+request.getRequestURL());
    if(request.getRequestURI().equalsIgnoreCase("/SHUTDOWN")){
    	return true;	
    }else{
    	return false;
    }
  }

  /**
   * 解析请求的头信息，cookie、和文本类型并将其值放入request的属性对象中
   * @param input
   * @throws IOException
   * @throws ServletException
   */
  private void parseHeaders(SocketInputStream input)   //设置请求的头信息，包括常用的一些类型和cookie和文本类型
    throws IOException, ServletException {
    while (true) {
      HttpHeader header = new HttpHeader();;

      // Read the next header
      input.readHeader(header);
			if (header.nameEnd == 0) {
				if (header.valueEnd == 0) {
					return;
				} else {
					throw new ServletException("读http头异常");
				}
			}

      String name = new String(header.name, 0, header.nameEnd);
      String value = new String(header.value, 0, header.valueEnd);
      request.addHeader(name, value);
      // do something for some headers, ignore others.
      if (name.equals("cookie")) {
        Cookie cookies[] = RequestUtil.parseCookieHeader(value);
        for (int i = 0; i < cookies.length; i++) {
          if (cookies[i].getName().equals("jsessionid")) {
            // Override anything requested in the URL
            if (!request.isRequestedSessionIdFromCookie()) {
              // Accept only the first session id cookie
              request.setRequestedSessionId(cookies[i].getValue());
              request.setRequestedSessionCookie(true);
              request.setRequestedSessionURL(false);
            }
          }
          request.addCookie(cookies[i]);
        }
      }
      else if (name.equals("content-length")) {
        int n = -1;
        try {
          n = Integer.parseInt(value);
        }
        catch (Exception e) {
          throw new ServletException("解析http文本类型异常");
        }
        request.setContentLength(n);
      }
      else if (name.equals("content-type")) {
        request.setContentType(value);
      }
    } //end while
  }

  
  /**
   * 解析请求的对象 ，主要是获取http请求的类型、请求的资源链接、请求协议版本 ，请求的session，并检测其中是否有异常并将所获取的值放入request对象的属性值中
   * @param input
   * @param output
   * @throws IOException
   * @throws ServletException
   */
  private void parseRequest(SocketInputStream input, OutputStream output)
    throws IOException, ServletException {

    // Parse the incoming request line
    input.readRequestLine(requestLine);
    String method =
      new String(requestLine.method, 0, requestLine.methodEnd); //获取请求类型
    String uri = null;
    String protocol = new String(requestLine.protocol, 0, requestLine.protocolEnd);

    // Validate the incoming request line
    if (method.length() < 1) {
      throw new ServletException("Missing HTTP request method");
    }
    else if (requestLine.uriEnd < 1) {
      throw new ServletException("Missing HTTP request URI");
    }
    // Parse any query parameters out of the request URI
    int question = requestLine.indexOf("?");
    if (question >= 0) {
      request.setQueryString(new String(requestLine.uri, question + 1,
        requestLine.uriEnd - question - 1));
      uri = new String(requestLine.uri, 0, question);
    }
    else {
      request.setQueryString(null);
      uri = new String(requestLine.uri, 0, requestLine.uriEnd);
    }


    // Checking for an absolute URI (with the HTTP protocol)
    if (!uri.startsWith("/")) {
      int pos = uri.indexOf("://");
      // Parsing out protocol and host name
      if (pos != -1) {
        pos = uri.indexOf('/', pos + 3);
        if (pos == -1) {
          uri = "";
        }
        else {
          uri = uri.substring(pos);
        }
      }
    }

    // Parse any requested session ID out of the request URI
    String match = ";jsessionid=";
    int semicolon = uri.indexOf(match);
    if (semicolon >= 0) {
      String rest = uri.substring(semicolon + match.length());
      int semicolon2 = rest.indexOf(';');
      if (semicolon2 >= 0) {
        request.setRequestedSessionId(rest.substring(0, semicolon2));
        rest = rest.substring(semicolon2);
      }
      else {
        request.setRequestedSessionId(rest);
        rest = "";
      }
      request.setRequestedSessionURL(true);
      uri = uri.substring(0, semicolon) + rest;
    }
    else {
      request.setRequestedSessionId(null);
      request.setRequestedSessionURL(false);
    }

    // Normalize URI (using String operations at the moment)
    String normalizedUri = normalize(uri);

    // Set the corresponding request properties
    ((HttpRequest) request).setMethod(method);
    request.setProtocol(protocol);
    if (normalizedUri != null) {
      ((HttpRequest) request).setRequestURI(normalizedUri);
    }
    else {
      ((HttpRequest) request).setRequestURI(uri);
    }

    if (normalizedUri == null) {
      throw new ServletException("Invalid URI: " + uri + "'");
    }
  }

  /**
   * Return a context-relative path, beginning with a "/", that represents
   * the canonical version of the specified path after ".." and "." elements
   * are resolved out.  If the specified path attempts to go outside the
   * boundaries of the current context (i.e. too many ".." path elements
   * are present), return <code>null</code> instead.
   *
   * @param path Path to be normalized
   */
  protected String normalize(String path) {
    if (path == null)
      return null;
    // Create a place for the normalized path
    String normalized = path;

    // Normalize "/%7E" and "/%7e" at the beginning to "/~"
    if (normalized.startsWith("/%7E") || normalized.startsWith("/%7e"))
      normalized = "/~" + normalized.substring(4);

    // Prevent encoding '%', '/', '.' and '\', which are special reserved
    // characters
    if ((normalized.indexOf("%25") >= 0)
      || (normalized.indexOf("%2F") >= 0)
      || (normalized.indexOf("%2E") >= 0)
      || (normalized.indexOf("%5C") >= 0)
      || (normalized.indexOf("%2f") >= 0)
      || (normalized.indexOf("%2e") >= 0)
      || (normalized.indexOf("%5c") >= 0)) {
      return null;
    }

    if (normalized.equals("/."))
      return "/";

    // Normalize the slashes and add leading slash if necessary
    if (normalized.indexOf('\\') >= 0)
      normalized = normalized.replace('\\', '/');
    if (!normalized.startsWith("/"))
      normalized = "/" + normalized;

    // Resolve occurrences of "//" in the normalized path
    while (true) {
      int index = normalized.indexOf("//");
      if (index < 0)
        break;
      normalized = normalized.substring(0, index) +
        normalized.substring(index + 1);
    }

    // Resolve occurrences of "/./" in the normalized path
    while (true) {
      int index = normalized.indexOf("/./");
      if (index < 0)
        break;
      normalized = normalized.substring(0, index) +
        normalized.substring(index + 2);
    }

    // Resolve occurrences of "/../" in the normalized path
    while (true) {
      int index = normalized.indexOf("/../");
      if (index < 0)
        break;
      if (index == 0)
        return (null);  // Trying to go outside our context
      int index2 = normalized.lastIndexOf('/', index - 1);
      normalized = normalized.substring(0, index2) +
      normalized.substring(index + 3);
    }

    // Declare occurrences of "/..." (three or more dots) to be invalid
    // (on some Windows platforms this walks the directory tree!!!)
    if (normalized.indexOf("/...") >= 0)
      return (null);

    // Return the normalized path that we have completed
    return (normalized);

  }

	/**
	 * 响应客户端的请求
	 * @param response
	 * @param returnStatus
	 */
	public void responseMessage(HttpServletResponse response,String returnStatus) {
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
