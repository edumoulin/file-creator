package com.edumoulin.file;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.edumoulin.file.FileCDichotomicSearch;
import com.edumoulin.file.FileCFileWriter;
import com.edumoulin.file.FileCLPEquation;

/**
 * Unit Test for writting a file.
 * @author etienne
 *
 */
public class FileCFileWriterTest {

	private static Logger logger = Logger.getLogger(FileCFileWriterTest.class);
	

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
			new FileCFileWriter().writeFile(f,0,new FileCDichotomicSearch());
			assertTrue("empty file should exist",f.exists());
			
			f.delete();
			new FileCFileWriter().writeFile(f,10,new FileCLPEquation());
			assertTrue("small file should exist",f.exists());
			f.delete();
		}catch(Exception e){
			logger.error(e,e);
			assertTrue("Unexpected error: "+e,false);
		}

	}


}
