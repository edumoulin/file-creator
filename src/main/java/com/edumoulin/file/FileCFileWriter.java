package com.edumoulin.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.apache.log4j.Logger;

/**
 * Write the topN file. 
 * @author etienne
 *
 */
public class FileCFileWriter {

	private static Logger logger = Logger.getLogger(FileCFileWriter.class);
	/**
	 * OS specific line separator.
	 */
	public static final String lineSep = System.getProperty("line.separator");

	/**
	 * Write the top N file
	 * @param file The file to write
	 * @param sizeFile The size of the file
	 * @throws Exception
	 */
	public void writeFile(File file,long sizeFile,FileCSolver solver) throws Exception{
		//Get the number to start with
		long maxNumber = solver.getMaxNumber(sizeFile);
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = null;
		try{
			bw = new BufferedWriter(fw);
			long i = maxNumber;
			//Add a number line by line and decrement
			while(i > 0){
				if(logger.isDebugEnabled() && i % 10e8 == 0){
					logger.debug("write line :"+i);
				}
				bw.write(i+lineSep);
				--i;
			}
		}catch(Exception e){
			logger.debug(e,e);
			//In case there is an error in the middle of writting, close the file
			bw.close();
			//We don't want incomplete file so we remove it
			file.delete();
			//Throw the original error.
			throw e;
		}
		bw.close();
	}

}
