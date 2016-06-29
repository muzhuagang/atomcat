/**
 * 
 */
package com.cat.core;

import java.util.HashMap;
import android.content.Context;

/**
 * 创建servlet和类之间的映射
 * @author Administrator
 *
 */
public class ServletMappingInfo {

	private static HashMap<String, String> servletmap = new HashMap<String, String>();  //定义servlet集合,类名-包路径
	
	private static Context mContext = null;
	/**
	 * 获取资源中的servlet类别并导入进哈希表中
	 * @param servletName
	 * @param servletPahtName
	 */
	public static void create(String[] servletName,String[] servletPahtName){
		for (int i = 0; i < servletName.length; i++) {
			servletmap.put(servletName[i], servletPahtName[i]);
		}
	}
	
	/**
	 * 获取所有servlet类信息
	 * @return
	 */
	public static HashMap<String, String> getServletMap(){
		return servletmap;
	}
	
	/**
	 * 设置上下文
	 * @param pContext
	 */
	public static void setAContext(Context pContext){
		mContext = pContext;
	}
	
	/**
	 * 获取上下文
	 * @return
	 */
	public static Context getAContext(){
		return mContext;
	}
}
