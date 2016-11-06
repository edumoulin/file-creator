package com.edumoulin.topn;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Solve the problem by dichotomic search
 * @author etienne
 *
 */
public class TopNDichotomicSearch implements TopNSolver {
	private static Logger logger = Logger.getLogger(TopNDichotomicSearch.class);
	
	protected static List<Long> sizeBase10 = new LinkedList<Long>();
	
	/**
	 * count numbers with n digit 
	 * @param index
	 * @return
	 */
	protected long getNumberBase10(int index){
		if(sizeBase10.size() <= index){
			long max_ans_cur = ((long)Math.pow(10, index+1)) - (long)Math.pow(10, index);
			sizeBase10.add(max_ans_cur);
		}
		if(logger.isTraceEnabled()){
			logger.trace("Number of base 10 number for "+index+": "+sizeBase10.get(index));
		}
		return sizeBase10.get(index);
	}
	
	/**
	 * Evaluate the file size with the number
	 * @param number
	 * @return The file size
	 */
	protected long evalFileSize(long number){
		boolean end = false;
		int lineSepLength = TopNFileWriter.lineSep.length();
		long ans = 0;
		long ans_size = 0;
		int i = 1;
		while(!end){
			long max_ans_cur = getNumberBase10(i-1);
			long max_ans_size_cur = 
					max_ans_cur
							*(i+lineSepLength);
			if(ans +max_ans_cur >= number){
				long ans_cur = number - ans;
				ans+= ans_cur;
				ans_size += ans_cur*(i+lineSepLength);
				if(logger.isTraceEnabled()){
					logger.trace("Found result (number,size-1,cur_size, ans): "
							+number+","+ans_size+", "+max_ans_size_cur+","+ans);
				}
				end = true;
			}else{
				ans += max_ans_cur;
				ans_size += max_ans_size_cur;
			}
			++i;
		}
		return ans_size;
	}
	
	/**
	 * Get the number needed for creating a file of the given size
	 * @param min The minimum number it could be
	 * @param max The maximum number it could be
	 * @param numberByte Size Requested
	 * @return 
	 */
	public long search(long min, long max,long numberByte){
		logger.debug("Dichotomic search (min,max,numbeByte): "+min+","+max+","+numberByte);
		long mid = min + (max - min)/2;
		long eval = evalFileSize(mid);
		logger.debug("Eval mid "+mid+": "+eval);
		if(eval < numberByte){
			min = mid+1;
		}else{
			max = mid;
		}
		if(min == max){
			logger.debug("Final result: "+min);
			return min;
		}else{
			return search(min,max,numberByte);
		}
	}
	
	/**
	 * Get the answer of the problem.
	 * 
	 * @param numberByte The size of the file.
	 * @return The number that our file has to start with.
	 * @throws Exception
	 */
	public long getMaxNumber(long numberByte) throws Exception{
		return search(0, numberByte,numberByte);
	}

}
