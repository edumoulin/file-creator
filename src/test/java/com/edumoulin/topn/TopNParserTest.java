package com.edumoulin.topn;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test for parsing.
 * @author etienne
 *
 */
public class TopNParserTest {
	
	private static Logger logger = Logger.getLogger(TopNParserTest.class);
	
	protected CommandLineParser parser = new DefaultParser();
	protected Options options = TopNParser.createOptions();


	@BeforeClass
	public static void init(){
		LoggerInit.init();
	}
	
	
	/**
	 * Parse File argument and test validity
	 * @param args File arguments such as "-f" "test.txt"
	 * @param notNull True if a not null answer is expected
	 * @throws ParseException
	 */
	public void testFileArgument(String[] args,boolean notNull) throws ParseException{
		CommandLine cmd = null;
		try{
			cmd = parser.parse(options, args);
		}catch(Exception e){
			assertFalse(notNull);
		}
		if(cmd != null){
			if(notNull){
				assertTrue("Test correct file arguments: "+Arrays.toString(args),
						TopNParser.getFileArgument(cmd) != null);
			}else{
				assertTrue("Test incorrect file arguments: "+Arrays.toString(args),
						TopNParser.getFileArgument(cmd) == null);
			}
		}
	}

	/**
	 * Test the file argument parsing.
	 */
	@Test
	public void testFileArgument(){

		try{
			testFileArgument(new String[]{"-f","test.txt"},true);
			testFileArgument(new String[]{"-f","/tmp/test.txt"},true);
			testFileArgument(new String[]{"-f","target"+TopNFileWriter.lineSep+"test.txt"},true);
			testFileArgument(new String[]{"-f"},false);
			testFileArgument(new String[]{},false);
			//This check is not working on linux.
			//testFileArgument(new String[]{"-f","c://not:good.txt"},false);
		}catch(Exception e){
			logger.error(e,e);
			assertTrue("Unexpected error: "+e,false);
		}
	}

	/**
	 * Test validity of size arguments
	 * @param args Size arguments such as "-s" "10K"
	 * @param notNull True if a not null answer is expected
	 * @throws ParseException
	 */
	public void testSizeArgument(String[] args,boolean notNull) throws ParseException{
		CommandLine cmd = null;
		try{
			cmd = parser.parse(options, args);
		}catch(Exception e){
			assertFalse(notNull);
		}
		if(notNull){
			assertTrue("Test correct file arguments: "+Arrays.toString(args),
					TopNParser.getSizeArgument(cmd) != null);
		}else{
			assertTrue("Test incorrect file arguments: "+Arrays.toString(args),
					TopNParser.getSizeArgument(cmd) == null);
		}
	}

	/**
	 * Test the size argument parsing.
	 */
	@Test
	public void testSizeArgument(){

		try{
			testSizeArgument(new String[]{"-s","0"},true);
			testSizeArgument(new String[]{"-s","0K"},true);
			testSizeArgument(new String[]{"-s","10M"},true);
			testSizeArgument(new String[]{"-s","1G"},true);
			testSizeArgument(new String[]{"-s","231m"},true);
			testSizeArgument(new String[]{"-s","019"},true);
			testSizeArgument(new String[]{},false);
			testSizeArgument(new String[]{"-s","1.3"},false);
			testSizeArgument(new String[]{"-s","231A"},false);
			testSizeArgument(new String[]{"-s","abc1"},false);
			testSizeArgument(new String[]{"-s","1abc1"},false);
		}catch(Exception e){
			logger.error(e,e);
			assertTrue("Unexpected error: "+e,false);
		}
	}

	/**
	 * Test check size.
	 */
	@Test
	public void testCheckSize(){
		File f = new File("/tmp/test.txt");
		try{
			assertTrue("small file check disk", TopNParser.checkSize(f.getParentFile(),10));
			assertFalse("big file check disk", TopNParser.checkSize(f.getParentFile(),(long)10e15));
			assertFalse("big file check disk", TopNParser.checkSize(new File("test.txt").getAbsoluteFile().getParentFile(),(long)10e15));
		}catch(Exception e){
			logger.error(e,e);
			assertTrue("Unexpected error: "+e,false);
		}
	}
	

	/**
	 * Test a full run.
	 * @param args Command line arguments
	 * @param ok True, if it is expected to run.
	 * @throws Exception
	 */
	public void testFullRun(String[] args,boolean ok) throws Exception{
		if(ok){
			CommandLine cmd = parser.parse(options, args);
			File f = new File(cmd.getOptionValue("f"));
			f.delete();
			assertTrue("Full run correct, arguments: "+Arrays.toString(args),
					TopNParser.parseAndRun(args));
			assertTrue("Test file exist after run: "+Arrays.toString(args),
					f.exists());
			f.delete();
		}else{
			assertFalse("Full run incorrect, arguments: "+Arrays.toString(args),
					TopNParser.parseAndRun(args));
		}
	}
	
	/**
	 * Test parser and run.
	 */
	@Test
	public void testParserFunction(){
		try{
			testFullRun(new String[]{"-f","test.txt","-s","0"},true);
			testFullRun(new String[]{"-f","test.txt","-s","10K"},true);
			testFullRun(new String[]{"-f","test.txt","-s","1M"},true);
			testFullRun(new String[]{"-f","test.txt","-s","10M"},true);
			
			testFullRun(new String[]{"-f","test.txt"},false);
		}catch(Exception e){
			logger.error(e,e);
			assertTrue("Unexpected error: "+e,false);
		}
	}
}
