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
            logger.info("Msg received: [{}]", msg);
            if (msg != null) {
                if ("vision-ping".equals(msg)) {
                    logger.debug("Got vision-ping, sending 'robot-pong'");
                    String currentTime = TIME_FORMAT.format(new Date());
                    out.println(String.format(PYTHON_TIME_RESPONSE_TEMPLATE, "robot-pong", currentTime));
                } else if (msg.startsWith("vision-data")) {
                    out.println(String.format(PYTHON_SIMPLE_RESPONSE_TEMPLATE, "OK"));
                    this.visionDatahub = DatahubRegistry.instance().get(VisionDataConstants.VISION_DATA_KEY);
                    msg = msg.replace("vision-data;", "");
                    this.processVisionData(msg);
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
        if (visionDatahub != null) {
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
                logger.trace("Dump of vision data...");
                this.visionDatahub.getAll().forEach((key,value) -> logger.trace("key={}, value={}", key, this.visionDatahub.getDouble(key)));
            }
        } else {
            logger.warn("Vision datahub doesn't exist yet, not retaining received vision data: [{}]", msg);
        }
    }
}
