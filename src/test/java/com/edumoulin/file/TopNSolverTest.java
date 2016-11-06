package com.edumoulin.file;

import static org.junit.Assert.assertTrue;

import java.text.NumberFormat;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.edumoulin.file.TopNDichotomicSearch;
import com.edumoulin.file.TopNFileWriter;
import com.edumoulin.file.TopNLPEquation;
import com.edumoulin.file.TopNSolver;

/**
 * Unit test for solving the topN equation
 * @author etienne
 *
 */
public class TopNSolverTest {

	private static Logger logger = Logger.getLogger(TopNSolverTest.class);

	private TopNSolver equation = null;

	@BeforeClass
	public static void init(){
		LoggerInit.init();
	}
	
	/**
	 * Given a size, Check if the problem is solved
	 * @param numberByte
	 * @throws Exception
	 */
	private void checkEquationResult(long numberByte) throws Exception{
		long numberByteCnt = 0;
		long numberByteCntMinus1 = 0;
		long result = equation.getMaxNumber(numberByte);
		int lineSepValue = TopNFileWriter.lineSep.length();
		for(long i = 1; i <= result;++i){
			if(i%10e7== 0){
				logger.debug(NumberFormat.getInstance().format(i)
						+"/"+NumberFormat.getInstance().format(result)
						+": Calculate size file");
			}
			numberByteCntMinus1 = numberByteCnt;
			numberByteCnt += (long)(Math.log10(i) + 1)+ lineSepValue;
		}
		logger.info(numberByteCntMinus1+" <= "+numberByte+" <= "+numberByteCnt);
		assertTrue("min "+numberByte, numberByte <= numberByteCnt);
		assertTrue("max "+numberByte, numberByte >= numberByteCntMinus1);
	}
	
	
	/**
	 * Runs different problem size and check the result
	 */
	public void checkResult(TopNSolver solver){
		equation = solver;
		try{
			assertTrue("0 test",0 == equation.getMaxNumber(0));
			for(long i=1; i < 100;++i){
				checkEquationResult(i);
			}
			for(long i=100; i < 1000;i+=50){
				checkEquationResult(i);
			}
			for(long i=1000; i < 100000;i+=500){
				checkEquationResult(i);
			}
			checkEquationResult((long)10e7);
			//Too long to test
			//checkEquationResult((long)10e11);
		}catch(Exception e){
			logger.error(e,e);
			assertTrue("Unexpected error: "+e,false);
		}
	}

	@Test
	public void checkLP(){
		checkResult(new TopNLPEquation());
	}
	
	@Test
	public void checkDichotomy(){
		checkResult(new TopNDichotomicSearch());
	}
}
