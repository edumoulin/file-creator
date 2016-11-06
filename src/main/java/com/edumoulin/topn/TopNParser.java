package com.edumoulin.topn;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Methop to parse command line arguments.
 * 
 * @author etienne
 *
 * The method contains the main class.
 */
public class TopNParser {

	private static Logger logger = Logger.getLogger(TopNParser.class);

	/**
	 * Create the command line options.
	 * @return Command line options supported.
	 */
	public static Options createOptions(){
		Options options = new Options();
		options.addOption("h","help",false, MessageManager.getProperty("help.msg"));
		options.addOption("ll","log4j-level",true, MessageManager.getProperty("help.msg"));
		options.addOption("f","file",true, MessageManager.getProperty("file.msg"));
		options.addOption("s","size",true, MessageManager.getProperty("size.msg"));
		options.addOption("lp","linear-progaming",false, MessageManager.getProperty("lp.msg"));
		return options;
	}

	/**
	 * Print the help.
	 * @param options Command line options.
	 */
	public static void printHelp(Options options){
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("topn", MessageManager.getProperty("help.desc"), options,"" );
	}

	/**
	 * Return a File from the command line argument.
	 * @param cmd The parsed command line arguments.
	 * @return null if there is an error.
	 */
	public static File getFileArgument(CommandLine cmd){
		File file = null;
		String filePath = null;
		try{
			filePath = cmd.getOptionValue("f");
		}catch(Exception e){}
		if(filePath == null){
			logger.warn(MessageManager.getProperty("filepath.missing.msg"));
		}else{
			file = new File(filePath).getAbsoluteFile();
			try{
				Paths.get(filePath);
				file.getCanonicalPath();
			}catch(Exception e){
				file = null;
				logger.error(MessageManager.getProperty("filepath.invalid.msg",new Object[]{filePath}));
			}
			if(file != null && file.exists()){
				file = null;
				logger.error(MessageManager.getProperty("file.alreadyexists.msg",new Object[]{filePath}));
			}
		}
		return file;
	}

	/**
	 * Return a size in byte from the command line argument.
	 * @param cmd The parsed command line arguments.
	 * @return null if there is an error.
	 */
	public static Long getSizeArgument(CommandLine cmd){
		String sizeFile = null;
		try{
			sizeFile = cmd.getOptionValue("s");
		}catch(Exception e){}
		
		if(sizeFile == null){
			logger.warn(MessageManager.getProperty("sizefile.missing.msg"));
			return null;
		}

		String sizeUnity = null;
		String integerPart = sizeFile;
		Long size = null;
		if(sizeFile.toUpperCase().endsWith("T")){
			integerPart = sizeFile.substring(0, sizeFile.length()-1);
			sizeUnity = "T";
		}else if(sizeFile.toUpperCase().endsWith("G")){
			integerPart = sizeFile.substring(0, sizeFile.length()-1);
			sizeUnity = "G";
		}else if(sizeFile.toUpperCase().endsWith("M")){
			integerPart = sizeFile.substring(0, sizeFile.length()-1);
			sizeUnity = "M";
		}else if(sizeFile.toUpperCase().endsWith("K")){
			integerPart = sizeFile.substring(0, sizeFile.length()-1);
			sizeUnity = "K";
		}  
		try{
			logger.debug("To convert: "+integerPart);
			size = Long.valueOf(integerPart);
			if(sizeUnity == "K"){
				size *= 1000;
			}else if(sizeUnity == "M"){
				size *= 1000*1000;
			}else if(sizeUnity == "G"){
				size *= 1000*1000*1000;
			}else if(sizeUnity == "T"){
				size *= 1000*1000*1000*1000;
			}
		}catch(Exception e){
			logger.error(MessageManager.getProperty("sizefile.invalid.msg",new Object[]{sizeFile}));
			size = null;
		}

		return size;

	}

	/**
	 * Check if the file system has enough space.
	 * @param f The file to write.
	 * @param size The size of the file requested.
	 * @return True if there is enough space.
	 * @throws IOException 
	 */
	public static boolean checkSize(File f, long size) throws IOException{
		if(f != null){
			long freeSpace = f.getUsableSpace();
			logger.debug("Free disk space "+f.getPath()+": "+freeSpace);
			//This method can fail silently
			if(freeSpace != 0){
				return freeSpace > size;
			}
		}
		logger.info("Disk space not checked");
		return true;
	}


	/**
	 * Parse command line arguments and run
	 * @param args Command line arguments
	 * @return True if the execution has been successful.
	 * @throws Exception
	 */
	public static boolean parseAndRun(String[] args) throws Exception {
		boolean ok = true;
		CommandLineParser parser = new DefaultParser();
		Options options = createOptions();
		CommandLine cmd = null;
		try{
			cmd = parser.parse(options, args);
		}catch(Exception e){
			logger.error(e,e);
			printHelp(options);
			return false;
		}
		String loggerLevel = cmd.getOptionValue("ll");
		if(loggerLevel != null){
			try{
				Logger.getRootLogger().setLevel(Level.toLevel(loggerLevel));
			}catch(Exception e){
				logger.error(MessageManager.getProperty("log4j.invalid.msg",new Object[]{loggerLevel}));
			}
		}
		if(cmd.hasOption("h")){
			printHelp(options);
		}else{

			File file = getFileArgument(cmd);
			Long size = getSizeArgument(cmd);
			if(file == null || size == null){
				printHelp(options);
				ok = false;
			}else{
				try{
					file.getParentFile().mkdirs();
				}catch(Exception e){
				}
				if(checkSize(file.getParentFile(),size)){
					if(cmd.hasOption("lp")){
						new TopNFileWriter().writeFile(file,size, new TopNLPEquation());
					}else{
						new TopNFileWriter().writeFile(file,size, new TopNDichotomicSearch());
					}
					if(file.exists()){
						logger.info("SUCCESS");
					}
				}else{
					logger.error(MessageManager.getProperty("diskcapacityexceeded"));
					ok = false;
				}
			}
		}
		return ok;

	}

	public static void main(String[] args) {
		// Set up a simple configuration that logs on the console.
		BasicConfigurator.configure();
		try{
			if(!parseAndRun(args)){
				System.exit(1);
			}
		}catch(Exception e){
			logger.error(MessageManager.getProperty("unexpeced.error.msg",new Object[]{e}),e);
			System.exit(1);
		}
	}

}
