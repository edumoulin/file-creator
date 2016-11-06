package com.edumoulin.file;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Init logger for tests.
 * @author etienne
 *
 */
public class LoggerInit {
	
	private static boolean init = false;
	
	public static void init(){
		if(!init){
			BasicConfigurator.configure();
			Logger.getRootLogger().setLevel(Level.DEBUG);
			init = true;
		}
	}

}
