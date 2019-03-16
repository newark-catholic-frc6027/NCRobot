package frc.team6027.robot.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProcessClientRequestTask implements Runnable {
    private final Logger logger = LogManager.getLogger(getClass());
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static final String PYTHON_MAP_RESPONSE_TEMPLATE = "{'result' : '%s', 'timestamp': '%s'}";

    private final Socket clientSocket;
    private final RobotStatusServer server;

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
            if ("vision-ping".equals(msg)) {
                logger.debug("Got vision-ping, sending 'robot-pong'");
                String currentTime = TIME_FORMAT.format(new Date());
                out.println(String.format(PYTHON_MAP_RESPONSE_TEMPLATE, "robot-pong", currentTime));
            } else if ("stop".equals(msg)) {
                server.stop();
            } else {
                logger.warn("Received [{}] from client, ignoring", msg);
            }
        } catch (Exception ex) {
            logger.error("Failure processing RobotServer client request!", ex);
        }
    }
}
