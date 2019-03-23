package frc.team6027.robot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;

public class RobotShutdownHook extends Thread {
    private final Logger logger = LogManager.getLogger(getClass());
    private final static File frcLogFile = new File("/var/local/natinst/log/FRC_UserProgram.log");
    private final static File logArchiveDir = new File("/home/lvuser/log_archive_frc");
    private final static SimpleDateFormat ts = new SimpleDateFormat("yyyyMMdd_hhmmss");
    public RobotShutdownHook() {
        createLogArchiveDir();
    }

    protected void createLogArchiveDir() {
        if (! logArchiveDir.exists()) {
            logArchiveDir.mkdirs();
            // TODO: prune old logs
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