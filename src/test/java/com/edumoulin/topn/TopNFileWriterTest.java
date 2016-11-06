package com.edumoulin.topn;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit Test for writting a file.
 * @author etienne
 *
 */
public class TopNFileWriterTest {

	private static Logger logger = Logger.getLogger(TopNFileWriterTest.class);
	

	@BeforeClass
	public static void init(){
		LoggerInit.init();
	}
	
	/**
	 * Check if a file can be written.
	 */
	@Test
	public void testWriteFile(){
		File f = new File("test.txt");
		f.delete();
		try{
			new TopNFileWriter().writeFile(f,0,new TopNDichotomicSearch());
			assertTrue("empty file should exist",f.exists());
			
			f.delete();
			new TopNFileWriter().writeFile(f,10,new TopNLPEquation());
			assertTrue("small file should exist",f.exists());
			f.delete();
		}catch(Exception e){
			logger.error(e,e);
			assertTrue("Unexpected error: "+e,false);
		}

	}


}
