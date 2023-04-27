//Zayd Kudaimi 015637245 Shinhyung Lee 014175837 Steve Rubin 017439448

import java.net.*;
import java.io.*;
import java.util.Arrays;


public class Server implements Runnable {
    private ServerSocket tcpServerSocket;
    private DatagramSocket udpServerSocket;
    private Socket socket;
    private BufferedReader tcpIn;
    private OutputStream tcpOut;
    private UdpRW udpRw;
    private String line;
    private boolean done = false;
    final char EOL = '\n';
    private Protocols protocol;

    public Server(int port, Protocols protocol) {
        this.protocol = protocol;
        switch (protocol) {
            case UDP:
                try {
                    udpServerSocket = new DatagramSocket(port);
                }

                catch (IOException e) {
                    System.out.println("Error: " + e);
                }
                break;
            case TCP:
                try {
                    tcpServerSocket = new ServerSocket(port);   
                } catch (IOException e) {
                    System.out.println("Error: " + e);
                }
                break;
            default:
        }

    }

    @Override
    public void run() {
        try {
            if (protocol == Protocols.TCP) {
                socket = tcpServerSocket.accept();
                socket.setTcpNoDelay(true);

                tcpIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                tcpOut = socket.getOutputStream();
            }

            if (protocol == Protocols.UDP) {
                udpRw = new UdpRW(udpServerSocket);
            }

            while (!done){
                line = readLine();
                if (line == null) {
                    done = true;
                    socket.close();
                    System.out.println("Socket closed");
                    System.exit(0);
                }

                String[] split = line.split(" ");
                switch (split[0].charAt(0)) {
                    case 'P':
                        Ping(line.getBytes());
                        break;
                    case 'S':
                        for (long i = 1; i <= Long.parseLong(split[1]); i++) {
                            
                            SendMeMessage(Integer.parseInt(split[2]));
                        }
                        break;
                    case 'Q':
                        done = true;
                        break;
                    default:
                        System.out.println("Error: " + line);
                        writeWithEOL("ERROR".getBytes());
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    private void Ping(byte[] line) throws IOException {
        writeWithEOL(line);
;


    }

    private String readLine() throws IOException {
        switch (protocol) {
            case UDP:
                return udpRw.readLine();
            default:
                return tcpIn.readLine();

        }
    }

    private void writeWithEOL(byte[] msg) throws IOException {
        byte[] sendBuffer = new byte[msg.length + 1]; // +1 to append EOL
        System.arraycopy(msg, 0, sendBuffer, 0, msg.length);
        sendBuffer[msg.length] = (byte) EOL;
        switch (protocol) {
            case UDP:
                udpRw.write(sendBuffer);
                break;
            default:
                write(sendBuffer);
        }
    }

    private void write(byte [] msg) throws IOException {
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

    private void SendMeMessage(int numBytes) throws IOException {
        byte[] buf = new byte[numBytes];
        Arrays.fill(buf, (byte) 'S');
        //System.out.println("Sending " + numBytes + " bytes");
        write(buf);
    }
}
