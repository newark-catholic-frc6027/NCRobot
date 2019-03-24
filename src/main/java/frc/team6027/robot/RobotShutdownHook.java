package frc.team6027.robot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;

public class RobotShutdownHook extends Thread {
    private final Logger logger = LogManager.getLogger(getClass());
    public static final int MAX_LOG_COUNT = 5;
    private final static File frcLogFile = new File("/var/local/natinst/log/FRC_UserProgram.log");
    private final static File logArchiveDir = new File("/home/lvuser/frclog-backups");
    private final static SimpleDateFormat ts = new SimpleDateFormat("yyyyMMdd_hhmmss");
    public RobotShutdownHook() {
        createLogArchiveDir();
        pruneLogs();
    }

    public void pruneLogs() {
        try {
            if (logArchiveDir.exists()) {
                File[] logFiles = logArchiveDir.listFiles();
                if (logFiles != null && logFiles.length > MAX_LOG_COUNT) {
                    Arrays.sort(logFiles, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
                    for (int i = 0; i < logFiles.length - MAX_LOG_COUNT; i++) {
                        logFiles[i].delete();
                        logger.info("Pruned old log file '{}", logFiles[i].getPath());
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Failure while pruning logs. Error: {}", ex.getMessage());
        }
    }
    protected void createLogArchiveDir() {
        if (! logArchiveDir.exists()) {
            if (! logArchiveDir.mkdirs()) {
                logger.warn("Failed to create log archive dir '{}''", this.logArchiveDir.getPath());
            }
        }
    }

    @Override
    public void run() {
        logger.info("Robot is shutting down.");
        this.saveCurrentLog();
    }

    public void saveCurrentLog() {
        if (frcLogFile.exists()) {
            if (logArchiveDir.exists()) {
                String filename = String.format("FRC_UserProgram_%s.log", ts.format(new Date()));
                File logBackupfile = new File(logArchiveDir, filename);

                try {
                    FileUtils.copyFile(frcLogFile, logBackupfile);
                    logger.info("FRC log file successfully copied to '{}'", logBackupfile.getPath());
                } catch (Exception ex) {
                    logger.error("Failed to back up FRC log file to '{}'. Reason: {}", 
                        logBackupfile.getPath(), ex.getMessage());
                }
            }
        } else {
            logger.info("{} not found, skipping copy", frcLogFile.getPath());
        }
    }
}