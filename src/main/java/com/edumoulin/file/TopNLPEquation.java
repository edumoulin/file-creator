/**
 * 
 */
package com.edumoulin.file;

import org.apache.log4j.Logger;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

/**
 * Solve mathematically the topN problem.
 * @author etienne
 * 
 * In the topN problem, we need to calculate the number we have to start from.
 * We use linear programming for solving the following problem.
 *
 */
public class TopNLPEquation implements TopNSolver{
	private static Logger logger = Logger.getLogger(TopNLPEquation.class);

	/**
	 * Create a string with zeros except one index
	 * @param index Index on which to write 1
	 * @param sizeArray Size of the array.
	 * @return A string array space delimited.
	 */
	protected String getZeroArrayExceptOne(int index, int sizeArray){
		StringBuilder ans = new StringBuilder();
		for(int i = 0; i < sizeArray;++i){
			if(i != 0){
				ans.append(" ");
			}
			if(i == index){
				ans.append("1");
			}else{
				ans.append("0");
			}
		}
		if(logger.isDebugEnabled()){
			logger.debug("("+index+","+sizeArray+"): "+ans.toString());
		}
		return ans.toString();
	}

	/**
	 * Setup the LpSolver
	 * @param sizeInB File size requested in Byte 
	 * @param lineSepSize The size of the line separator for this OS
	 * @return The solver, setup and ready to solve.
	 * @throws LpSolveException
	 */
	protected LpSolve getSolver(long sizeInB, int lineSepSize) throws LpSolveException{
		//The size of the problem depends on the number of bytes requested
		//The problem needs at least one variable
		//Every variable you add, can handle a power of 10 more byte.
		int nbVariable = Math.max(1,(int) (Math.log10(sizeInB)+1));
		if(logger.isDebugEnabled()){
			logger.debug("Number of variables: "+nbVariable);
		}

		// Create a problem with n variables
		LpSolve solver = LpSolve.makeLp(0, nbVariable);
		// Set the verbose level to Important
		solver.setVerbose(3);
		//Add constraints for every variables
		StringBuilder sizeEquation = new StringBuilder();
		StringBuilder objectiveEq = new StringBuilder();
		for(int i = 0; i < nbVariable;++i){
			if(i != 0){
				sizeEquation.append(" ");
				objectiveEq.append(" ");
			}
			//No values can be smaller than 0
			solver.strAddConstraint(getZeroArrayExceptOne(i, nbVariable), 
					LpSolve.GE, 0);
			// The maximum value depends on the number of digit
			solver.strAddConstraint(getZeroArrayExceptOne(i, nbVariable), 
					LpSolve.LE, 
					Math.pow(10, i+1) - Math.pow(10, i));
			
			sizeEquation.append(i+1+lineSepSize);
			objectiveEq.append(i+1);
		}

		if(logger.isDebugEnabled()){
			logger.debug("Size Equation: "+sizeEquation.toString());
			logger.debug("Minimization: "+objectiveEq.toString());
		}

		//Add the size constraint
		solver.strAddConstraint(sizeEquation.toString(), LpSolve.GE, sizeInB);

		//Set cost function
		solver.strSetObjFn(objectiveEq.toString());

		return solver;
	}

	/**
	 * Get the answer of the problem.
	 * 
	 * @param numberByte The size of the file.
	 * @return The number that our file has to start with.
	 * @throws LpSolveException
	 */
	public long getMaxNumber(long numberByte) throws LpSolveException{
		logger.info("Size requested: "+numberByte);
		LpSolve solver = new TopNLPEquation().getSolver(numberByte,TopNFileWriter.lineSep.length());

		// solve the problem
		solver.solve();

		double[] var = solver.getPtrVariables();
		if(logger.isDebugEnabled()){
			// print solution
			logger.debug("Value of objective function: " + solver.getObjective());
			for (int i = 0; i < var.length; i++) {
				logger.debug("Value of var[" + i + "] = " + var[i]);
			}
		}
		//Find the last index
		int cur = var.length -1;
		while(var[cur] == 0 && cur > 0){
			--cur;
		}
		//Calculate the max value from the last index
		long ans = 0;
		if(var[cur] > 0){
			ans = (long)(Math.ceil(var[cur])-1+ Math.pow(10, cur));
		}
		logger.info("Number to start with: "+ans);

		// delete the problem and free memory
		solver.deleteLp();
		
		return ans;
	}
}
