package com.edumoulin.file;

/**
 * Solve mathematically the topN problem.
 * @author etienne
 * 
 */
public interface TopNSolver {
	
	/**
	 * Get the answer of the problem.
	 * 
	 * @param numberByte The size of the file.
	 * @return The number that our file has to start with.
	 * @throws Exception
	 */
	public long getMaxNumber(long numberByte) throws Exception;
}
