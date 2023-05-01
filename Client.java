//Zayd Kudaimi  Shinhyung Lee  Steve Rubin 

import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {
   final char EOL = '\n';
   Socket tcpSocket;
   DatagramSocket udpSocket;
   InetAddress udpServerAddress;
   int port;
   Protocols protocol;
   private UdpRW udpRw;
   private OutputStream tcpOut;
   private BufferedReader tcpIn;

   public Client(String host, int port, Protocols protocol) throws IOException {
      this.port = port;
      this.protocol = protocol;

      if (protocol == Protocols.UDP) {
         udpSocket = new DatagramSocket();
         udpServerAddress = InetAddress.getByName(host);
         udpSocket.connect(udpServerAddress, port);
         udpRw = new UdpRW(udpSocket);

      } else {
         tcpSocket = new Socket(host, port);
         tcpSocket.setTcpNoDelay(true);
         tcpOut = tcpSocket.getOutputStream();
         tcpIn = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));

      }
   }

   public HashMap<Integer, ArrayList<Long>> messageDataSizeSweep(int messageStart, int messageEnd, int messageStep)
         throws IOException {
      HashMap<Integer, ArrayList<Long>> messageTimes = new HashMap<>();
      for (int size = messageStart; size <= messageEnd; size += messageStep) {
         messageTimes.put((int) size, messageTest(size, 1));
      }
      return (messageTimes);
   }

   public HashMap<Long, Long> messageCountSweep(long dataSize, int[] sizes) throws IOException {
      HashMap<Long, Long> messageTimes = new HashMap<>();
      for (int numMessages : sizes) {
         messageTimes.put(dataSize / numMessages, message((int) (dataSize / numMessages), numMessages));
      }
      return (messageTimes);
   }
   public static void printMessageCountSweep(HashMap<Long, Long> messageTimes) {
      System.out.println("NumMessages,Throughput");
      for (long packetSize : messageTimes.keySet()) {
         long time = messageTimes.get(packetSize);
         System.out.printf("%d,%d\n", packetSize, time);
      }
   }

   public ArrayList<Long> messageTest(int size, int numMessages) throws IOException {
      ArrayList<Long> messageTimes = new ArrayList<>();
      messageTimes.add(message(size, numMessages));

      return messageTimes;
   }

   public long message(int dataSize, int numMessages) throws IOException {
      String message = "S " + numMessages + " " + dataSize + EOL;
      write(message.getBytes());
      byte[] buffer = new byte[dataSize];
      int bytesRead = 0;
      int totalBytesRead = 0;
      long startTime = System.nanoTime();
      while ((bytesRead = read(buffer)) != -1) {
         totalBytesRead += bytesRead;
         if (totalBytesRead == dataSize * numMessages) {
            break;
         }
      }
      long endTime = System.nanoTime();
      long transferTime = endTime - startTime;
      long throughput = (long) ((totalBytesRead * 8) / (transferTime / 1000000000.0));
      return (throughput);
   }

   public HashMap<Integer, ArrayList<Long>> pingSweep(int[] pingSizes, int pingCount) throws IOException {
      HashMap<Integer, ArrayList<Long>> pingTimes = new HashMap<>();
      for (int size : pingSizes) {
         pingTimes.put(size, pingTest(size, pingCount));
      }
      return (pingTimes);
   }

   public static void printMessageTimes(HashMap<Integer, ArrayList<Long>> messageTimes) {
      System.out.println("Size,Thoughput");

      for (int size : messageTimes.keySet()) {
         ArrayList<Long> times = messageTimes.get(size);
         long total = 0;
         long min = 0;
         long max = 0;
         for (long time : times) {
            total += time;
            if (time < min || min == 0) {
               min = time;
            }
            if (time > max) {
               max = time;
            }
         }
         if (times.size() == 1)
            System.out.printf("%d, %d\n", size, min);
         else
            System.out.printf("Samples %d, size %d, min %d, max %d, avg %d\n", times.size(), size, min, max,
                  total / times.size());
      }
   }

   public static void printPingTimes(HashMap<Integer, ArrayList<Long>> pingTimes) {
      System.out.println("Size,Time(ns)");
      for (int size : pingTimes.keySet()) {
         ArrayList<Long> times = pingTimes.get(size);
         long total = 0;
         long min = 0;
         long max = 0;
         for (long time : times) {
            total += time;
            if (time < min || min == 0) {
               min = time;
            }
            if (time > max) {
               max = time;
            }
         }
         if (times.size() == 1)
            System.out.printf("%d, %d\n", size, min);
         else
            System.out.printf("Samples %d, size %d, min %d, max %d, avg %d\n", times.size(), size, min, max,
                  total / times.size());
      }
   }

   public ArrayList<Long> pingTest(int size, long count) throws IOException {
      ArrayList<Long> pingTimes = new ArrayList<>();

      while (count > 0) {
         pingTimes.add(ping(size));
         count--;
      }

      return pingTimes;
   }

   public long ping(int size) throws IOException {
      long startTime = System.nanoTime();
      byte[] sendBuffer = new byte[size + 1];
      Arrays.fill(sendBuffer, (byte) 'P');
      byte[] trimmedBuffer = Arrays.copyOf(sendBuffer, size);
      sendBuffer[size] = (byte) EOL;
      write(sendBuffer);
      String line = readLine();

      if (line.equals(new String(trimmedBuffer))) { // Check for data corruption or loss
         long endTime = System.nanoTime();
         return endTime - startTime;
      } else {
         return -1;
      }
   }


   private void write(byte[] msg) throws IOException {
      switch (protocol) {
         case UDP:
            udpRw.write(msg);
            break;
         default:
            tcpOut.write(msg, 0, msg.length);
            tcpOut.flush();
            break;
      }
   }

   private String readLine() throws IOException {
      switch (protocol) {
         case UDP:
            return udpRw.readLine();
         default:
            return tcpIn.readLine();

      }
   }

   private int read(byte[] b) throws IOException {
      switch (protocol) {
         case UDP:
            String line = udpRw.read();
            b = line.getBytes();
            return line.length();
         default:
            char[] c = new char[b.length];
            return tcpIn.read(c);

      }
   }

}
