package com.edumoulin.file;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.edumoulin.file.FileCDichotomicSearch;

public class FileCDichotomicSearchTest {

	private static Logger logger = Logger.getLogger(FileCDichotomicSearchTest.class);
	
	
	@BeforeClass
	public static void init(){
		LoggerInit.init();
	}
	
	@Test
	public void evalFct(){
		FileCDichotomicSearch dich = new FileCDichotomicSearch();
		long eval = dich.evalFileSize(1);
		assertTrue("Eval 1: "+eval,eval == 2);
		eval = dich.evalFileSize(2);
		assertTrue("Eval 2: "+eval,eval == 4);
	}
	
}
