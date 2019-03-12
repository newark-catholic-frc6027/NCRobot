package frc.team6027.robot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RobotStatusServer {
    public static final int DEFAULT_ROBOT_SERVER_PORT = 6060;
    private final Logger logger = LogManager.getLogger(getClass());

    protected boolean stopped = false;
    protected int port = DEFAULT_ROBOT_SERVER_PORT;

    public RobotStatusServer() {
    }

    public RobotStatusServer(int port) {
        this.port = port;
    }

    public void start() {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(5);

        Runnable serverTask = () -> {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(RobotStatusServer.this.port);
                logger.info("RobotServer is waiting for a client connection...");
            } catch (Exception ex) {
                RobotStatusServer.this.stopped = true;
                logger.error("Failed to start server on port " + RobotStatusServer.this.port, ex);
            }

            while(! RobotStatusServer.this.stopped) {
                Socket clientSocket;
                try {
                    clientSocket = serverSocket.accept();
                } catch (Exception ex) {
                    logger.error("Failure on serverSocket accept!", ex);
                    continue;
                }
                clientProcessingPool.submit(new ClientTask(clientSocket));
                logger.info("RobotServer is waiting for a client connection...");
            }
        };

        Thread serverThread = new Thread(serverTask);
        serverThread.start();

    }

    protected class ClientTask implements Runnable {
        private final Socket clientSocket;

        private ClientTask(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            logger.info("Client connected");
            try (
                this.clientSocket;
                BufferedReader br = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(this.clientSocket.getOutputStream(), true);
            ) {
                String msg = br.readLine();
                if ("vision-ping".equals(msg)) {
                    logger.info("Received vision-ping from client, replying with 'robot-pong'");
                    out.println("robot-pong");
                } else if ("stop".equals(msg)) {
                    RobotStatusServer.this.stopped = true;
                } else {
                    logger.info("Received [{}] from client, ignoring", msg);
                }
            } catch (Exception ex) {
                logger.error("Failure processing RobotServer client request!", ex);
            }
        }
    }

    public static void main(String[] args) {
        RobotStatusServer server = new RobotStatusServer(DEFAULT_ROBOT_SERVER_PORT);
        server.start();
    }
}