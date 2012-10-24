package org.daisy.dotify.system;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.logging.Logger;

import org.daisy.util.file.FileJuggler;
import org.daisy.util.file.FileUtils;

/**
 * Utility class to run a list of tasks.
 * @author Joel Håkansson
 *
 */
public class TaskRunner {
	public final static String TEMP_DIR;// = System.getProperty("java.io.tmpdir");
	static {
		String path = System.getProperty("java.io.tmpdir");
		if (path!=null && !"".equals(path) && new File(path).isDirectory()) {
			TEMP_DIR = path;
		} else {
			// user.home is guaranteed to be defined
			TEMP_DIR = System.getProperty("user.home");
		}
	}
	private final Logger logger;
	private File tempFilesFolder;
	private boolean writeTempFiles;
	private String identifier;
	
	public TaskRunner() {
		this.logger = Logger.getLogger(this.getClass().getCanonicalName());
		this.tempFilesFolder = new File(TEMP_DIR);
		this.writeTempFiles = false;
		this.identifier = "" + System.currentTimeMillis();
	}

	public boolean isWriteTempFiles() {
		return writeTempFiles;
	}

	public void setWriteTempFiles(boolean writeTempFiles) {
		this.writeTempFiles = writeTempFiles;
	}


	public File getTempFilesFolder() {
		return tempFilesFolder;
	}

	public void setTempFilesFolder(File tempFilesFolder) {
		if (tempFilesFolder.isDirectory()) {
			this.tempFilesFolder = tempFilesFolder;
		}
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void runTasks(File input, File output, TaskSystem taskSystem, RunParameters rp) throws IOException, TaskSystemException {
		Progress progress = new Progress();
		logger.info("\"" + taskSystem.getName() + "\" started on " + progress.getStart() + " with parameters " + rp);
		List<InternalTask> tasks = taskSystem.compile(rp);
		double i = 0;
		NumberFormat nf = NumberFormat.getPercentInstance();
		FileJuggler fj = new FileJuggler(input, output);
		for (InternalTask task : tasks) {
			if (task instanceof ReadWriteTask) {
				logger.info("Running (r/w) " + task.getName());
				((ReadWriteTask)task).execute(fj.getInput(), fj.getOutput());
				if (writeTempFiles) {
					String it = ""+((int)i+1);
					while (it.length()<3) {
						it = "0" + it; 
					}
					String fileName = (identifier + "-"
									+ truncate(taskSystem.getName(), 20) + "-" 
									+ it + "-" 
									+ truncate(task.getName(), 20)
								).toLowerCase().replaceAll("[^a-zA-Z0-9@\\-]+", "_");
					fileName += ".tmp";
					File f = new File(tempFilesFolder, fileName);
					logger.fine("Writing debug file: " + f);
					FileUtils.copy(fj.getOutput(), f);
				}
				fj.swap();
			} else if (task instanceof ReadOnlyTask) {
				logger.info("Running (r) " + task.getName());
				((ReadOnlyTask)task).execute(fj.getInput());
			} else {
				logger.warning("Unknown task type, skipping.");
			}
			i++;
			progress.updateProgress(i/tasks.size());
			logger.info(nf.format(progress.getProgress()) + " done. ETA " + progress.getETA());
			//progress(i/tasks.size());
		}
		fj.close();
		logger.info("\"" + taskSystem.getName() + "\" finished in " + Math.round(progress.timeSinceStart()/100d)/10d + " s");
	}
	
	private String truncate(String str, int pos) {
		if (str.length()>pos) {
			return str.substring(0, pos);
		} else {
			return str;
		}
	}
}
