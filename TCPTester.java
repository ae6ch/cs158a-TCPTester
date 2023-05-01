//Zayd Kudaimi  Shinhyung Lee  Steve Rubin 

import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;

public class TCPTester {
    public static void main(String[] args) {
        int port;
        String host;
        int[] pingSizes = { 1, 2, 5,10,32,64, 100, 200, 400, 600, 800, 1000 };
        final int MESSAGE_START = 1024;
        final int MESSAGE_STEP = 1024;
        final int MESSAGE_END = 32768; 
        final int PING_COUNT = 1;
        final long BIG_MESSAGE_SIZE = 1024L * 1024L;
        int[] bigMessages = { 256, 512, 1024, 2048, 4096, 8192 };

        switch (args.length) {
            case 2:
                host = args[0];
                port = Integer.parseInt(args[1]);
                try {

                    Client tcpClient = new Client(host, port, Protocols.TCP);
                    HashMap<Integer, ArrayList<Long>> pingTimes = tcpClient.pingSweep(pingSizes,PING_COUNT);
                    System.out.println("TCP RTT Times:");
                    Client.printPingTimes(pingTimes);
                    System.out.println("TCP Throughput Sweep:");
                    HashMap<Integer, ArrayList<Long>> tcpMessageTimes = tcpClient.messageDataSizeSweep(MESSAGE_START,MESSAGE_END,MESSAGE_STEP);
                    Client.printMessageTimes(tcpMessageTimes);
                    System.out.println("TCP 1MB Transfered - Throughput at at various message sizes:");
                    HashMap<Long, Long> tcpBigMessageTimes = tcpClient.messageCountSweep(BIG_MESSAGE_SIZE, bigMessages);
                    Client.printMessageCountSweep(tcpBigMessageTimes);

                    Client udpClient = new Client(host, port, Protocols.UDP);
                    HashMap<Integer, ArrayList<Long>> udpPingTimes = udpClient.pingSweep(pingSizes,PING_COUNT);
                    System.out.println("UDP RTT Times:");
                    Client.printPingTimes(udpPingTimes);
                    System.out.println("UDP Throughput Sweep:");
                    HashMap<Integer, ArrayList<Long>> udpMessageTimes = udpClient.messageDataSizeSweep(MESSAGE_START,MESSAGE_END,MESSAGE_STEP);
                    Client.printMessageTimes(udpMessageTimes);

                



                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                port = Integer.parseInt(args[0]);
                Server tcpServer = new Server(port, Protocols.TCP);
                Server udpServer = new Server(port, Protocols.UDP);

                new Thread(tcpServer).start();
                new Thread(udpServer).start();

                break;
            default:
                System.out.println("Usage: java TCPTester [host] <port>");
                System.exit(1);
        }
    }
}
