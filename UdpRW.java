import java.net.*;
import java.io.IOException;

public class UdpRW extends Object {
    DatagramSocket socket;
    InetAddress remoteAddress;
    int remotePort;

    public UdpRW(DatagramSocket socket) {
        this.socket = socket;
    }

    public String readLine() throws IOException {
        //System.out.println("Reading line");

        StringBuffer line = new StringBuffer("");
        byte[] buf = new byte[65535];
        do {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            remoteAddress = packet.getAddress();
            remotePort = packet.getPort();
            String messageString = new String(packet.getData(), 0, packet.getLength());
            line.append(messageString);

        } while (line.indexOf("\n") == -1);
        line.deleteCharAt(line.indexOf("\n"));
        //System.out.println("Read: " + line.toString().length());
        return line.toString();
    }

    public String read() throws IOException {
        //System.out.println("Reading line");

        StringBuffer line = new StringBuffer("");
        byte[] buf = new byte[65535];

        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        remoteAddress = packet.getAddress();
        remotePort = packet.getPort();
        String messageString = new String(packet.getData(), 0, packet.getLength());
        line.append(messageString);

      //  System.out.println("Read: " + line.toString().length());
        return line.toString();
    }

    public void write(byte[] b) throws IOException {
        DatagramPacket packet = new DatagramPacket(b, b.length, remoteAddress, remotePort);
        packet.setData(b);
        packet.setLength(b.length);
        socket.send(packet);
    }

    public void writeLine(byte[] b) throws IOException {
        byte[] buf = new byte[b.length + 1];
        System.arraycopy(b, 0, buf, 0, b.length);
        buf[b.length] = (byte) '\n';
        write(buf);
    }

}
