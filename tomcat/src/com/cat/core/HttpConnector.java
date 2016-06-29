/**
 * 
 */
package com.cat.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

/**
 * 连接器，接收到请求时启动处理器，这个是tomcat4默认的连接器
 * 功能：负责等待http的请求
 * @author Administrator
 *
 */
public class HttpConnector extends Service{

	ServerSocket serverSocket = null;
	private static int port = 8089;
	boolean stopped;
	Messenger mesenger;
	
	@Override
	public IBinder onBind(Intent intent) {
		System.out.println("---------------------------->onBind");
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("---------------------------->onCreate");
        
        
//        
//		startService();
	}
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mesenger = (Messenger)intent.getExtras().get("handler");
		System.out.println("---------------------------->onStartCommand");
		startService();
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 启动一个线程运行服务
	 */
	private void startService(){
		String serverurl="http://"+getIpAddress()+":"+port;
		Message msg=Message.obtain();
		if(null==mesenger){
			Toast.makeText(this, "初始化地址失败", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			msg.what=1;
			msg.obj=serverurl;
			mesenger.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		Toast.makeText(this, "访问服务的地址是:" + serverurl, Toast.LENGTH_SHORT).show();
		
		new Thread() {   //在新线程中新建套接字连接
			public void run() {
				runSingle();
			};
		}.start();
	}
	
	/**
	 * 启动一个服务端监听的接口，如果这个端口有请求则创建一个处理实例来处理，
	 */
	public void runSingle() {
		System.out.println("启动服务");
	    try {
	      serverSocket = new ServerSocket(port);
	    }
	    catch (IOException e) {
	      e.printStackTrace();
	      System.exit(1);
	    }
		System.out.println("HTTP服务器正在运行,端口:" + port);
		System.out.println("访问服务的地址是:" + "http://"+getIpAddress()+":"+port);
	    while (!stopped) {
	      System.out.println("请求服务");
	      Socket socket = null;
	      try {
	        socket = serverSocket.accept();
	      }
	      catch (Exception e) {
	        System.out.println("监听服务异常");
	        continue;
	      }
	      System.out.println("处理服务");
	      HttpProcessor processor = new HttpProcessor(this);
	      stopped =processor.process(socket);
	    }
	}
	
	/**
	 * 获取本机IP地址
	 * @return
	 */
	private String getIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();	enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						String ipaddress = inetAddress.getHostAddress().toString();
					       if(!ipaddress.contains("::")){//ipV6的地址
					           return ipaddress;
					          }
					}
				}
			}
		} catch (SocketException ex) {
			System.out.println("获取ip地址异常");
			ex.printStackTrace();
		}
		return "127.0.0.1";
	}
	
	public void onDestroy() {
		super.onDestroy();
		try {
			serverSocket.close();      //去掉端口绑定
		} catch (IOException e) {
			System.out.println("关闭套接字异常");
			e.printStackTrace();
		}
		System.out.println("停止service");
	}
}
