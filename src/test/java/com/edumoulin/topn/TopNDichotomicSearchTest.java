package com.edumoulin.topn;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

public class TopNDichotomicSearchTest {

	private static Logger logger = Logger.getLogger(TopNDichotomicSearchTest.class);
	
	
	@BeforeClass
	public static void init(){
		LoggerInit.init();
	}
	
	@Test
	public void evalFct(){
		TopNDichotomicSearch dich = new TopNDichotomicSearch();
		long eval = dich.eval(1);
		assertTrue("Eval 1: "+eval,eval == 2);
		eval = dich.eval(2);
		assertTrue("Eval 2: "+eval,eval == 4);
	}
	
}
