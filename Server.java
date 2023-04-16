import java.net.*;
import java.io.*;

public class Server implements Runnable {
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader in;
    private OutputStream out;
    private String line;
    private boolean done = false;
    final char EOL = '\n';

    public Server(int port, Protocols protocol)  {
        switch (protocol) {
            case TCP:
            try {
                serverSocket = new ServerSocket(port);
            }
            catch (IOException e) {
                System.out.println("Error: " + e);
            }
            break;
            case UDP:
                break;
            default:
        }
       
    }

    private void close() throws IOException {
        socket.close();
    }

    @Override
    public void run() {
        try {
            socket = serverSocket.accept();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = socket.getOutputStream();
            while (!done) {
                line = in.readLine();
                String[] split = line.split(" ");
                switch (split[0]) {
                    case "PING":
                        Ping(split);
                        break;
                    case "SENDME":
                        SendMe(Long.parseLong(split[1]));
                        break;
                    case "QUIT":
                        done = true;
                        break;
                    default:
                        out.write("ERROR".getBytes());
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    // PING responds with PONG and any additional arguments
    private void Ping(String[] split) throws IOException{
        out.write("PONG".getBytes());
        if (split.length > 1) {
            for (int i = 1; i < split.length; i++) {
                out.write(' ');
                out.write(split[i].getBytes());
            }
        }
        out.write(EOL);
    }

    // SENDME sends N bytes followed by EOL
    private void SendMe(long numBytes) throws IOException{
        for (long i = 0; i < numBytes; i++) {
            out.write('*');
            out.flush();
        }
        out.write(EOL);
    }
}
