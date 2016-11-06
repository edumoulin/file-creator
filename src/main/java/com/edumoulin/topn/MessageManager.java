package com.edumoulin.topn;

import java.text.MessageFormat;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Message Interface of jar lang property file.
 * 
 * @author etienne
 *
 */
public class MessageManager {
	
	private static Logger logger = Logger.getLogger(MessageManager.class);
	public static Properties props = null;
	
	private static void init(){
		if(props == null){
			try{
				props = new Properties();
				props.load(MessageManager.class.getResourceAsStream("/topn_lang.properties"));
			}catch(Exception e){
				logger.warn("Unable to access properties",e);
			}
		}
	}
	
	/**
	 * Get a static message.
	 * @param key The message id
	 * @return A human readable message
	 */
	public static String getProperty(String key){
		return getProperty(key,null);
	}
	
	/**
	 * Get a dynamic message.
	 * @param key The message id
	 * @param vars The dynamic part of the message.
	 * @return A human readable message
	 */
	public static String getProperty(String key, Object[] vars){
		init();
		if(vars == null){
			return props.getProperty(key,"?"+key+"?");
		}else{
			return MessageFormat.format(props.getProperty(key,"?"+key+"?"),vars);
		}
	}

}
