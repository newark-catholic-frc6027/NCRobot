package frc.team6027.robot.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import frc.team6027.robot.data.Datahub;
import frc.team6027.robot.data.DatahubRegistry;
import frc.team6027.robot.data.VisionDataConstants;

public class ProcessClientRequestTask implements Runnable {
    private final Logger logger = LogManager.getLogger(getClass());
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static final String PYTHON_SIMPLE_RESPONSE_TEMPLATE = "{'result' : '%s'}";
    private static final String PYTHON_TIME_RESPONSE_TEMPLATE = "{'result' : '%s', 'timestamp': '%s'}";
    private static final String VISION_DATA_MESSAGE_TOKEN = "vision-data;";
    private static final int VISION_DATA_MESSAGE_TOKEN_LEN = VISION_DATA_MESSAGE_TOKEN.length();

    private final Socket clientSocket;
    private final RobotStatusServer server;
    private Datahub visionDatahub;

    public ProcessClientRequestTask(RobotStatusServer server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        logger.debug("Client connected");
        try (
            this.clientSocket;
            BufferedReader br = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(this.clientSocket.getOutputStream(), true);
        ) {
            String msg = br.readLine();
            logger.trace("Msg received: [{}]", msg);
            if (msg != null) {
                if ("vision-ping".equals(msg)) {
                    logger.debug("Got vision-ping, sending 'robot-pong'");
                    Date curTime = new Date();
                    String currentTimeStr = TIME_FORMAT.format(curTime);
                    out.println(String.format(PYTHON_TIME_RESPONSE_TEMPLATE, "robot-pong", currentTimeStr));
                    this.visionDatahub = DatahubRegistry.instance().get(VisionDataConstants.VISION_DATA_KEY);
                    this.visionDatahub.put(VisionDataConstants.LAST_PING_TIME_MS, Long.valueOf(curTime.getTime()));
                    // this.server.setStatus(RobotStatusServer.set)
                } else if (msg.startsWith(VISION_DATA_MESSAGE_TOKEN)) {
                    // reply immediately with OK
                    out.println(String.format(PYTHON_SIMPLE_RESPONSE_TEMPLATE, "OK"));
                    this.visionDatahub = DatahubRegistry.instance().get(VisionDataConstants.VISION_DATA_KEY);
                    if (msg.length() > VISION_DATA_MESSAGE_TOKEN_LEN) {
                        msg = msg.substring(VISION_DATA_MESSAGE_TOKEN_LEN);
                        this.processVisionData(msg);
                    }
                } else if ("stop".equals(msg)) {
                    server.stop();
                } else {
                    logger.warn("Received [{}] from client, ignoring", msg);
                }
            } else {
                logger.warn("Received <null> response from client, ignoring");
            }
        } catch (Exception ex) {
            logger.error("Failure processing RobotServer client request!", ex);
        }
    }

    protected void processVisionData(String msg) {
        if (this.visionDatahub != null) {
            String[] msgParts = msg.split(";");
            Map<String, Object> visionData = new HashMap<>();
            for (String msgPart : msgParts) {
                String[] keyValue = msgPart.split("=");
                if (keyValue != null) {
                    if (keyValue.length == 1) {
                        visionData.put(keyValue[0], null);
                    } else if (keyValue.length == 2) {
                        visionData.put(keyValue[0], keyValue[1]);
                        logger.debug("Put to vision datahub: key={}, value={}", keyValue[0], keyValue[1]);
                    }
                }
            }
            if (visionData.size() > 0) {
                this.visionDatahub.put(visionData, true);
                if (logger.isTraceEnabled()) {
                    logger.trace("Dump of vision data...");
                    this.visionDatahub.getAll().forEach((key,value) -> logger.trace("key={}, value={}", key, this.visionDatahub.getDouble(key)));
                }
            }
        } else {
            logger.warn("Vision datahub doesn't exist yet, not retaining received vision data: [{}]", msg);
        }
    }
}
