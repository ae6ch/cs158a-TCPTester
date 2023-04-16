public class TCPTester {
    public static void main(String[] args) {
        int port;
        String host;

        switch (args.length) {
            case 2:
                host = args[0];
                port = Integer.parseInt(args[1]);
                //Server client = new Client(host, port);
                break;
            case 1:
                port = Integer.parseInt(args[0]);
                Server tcpServer = new Server(port, Protocols.TCP);
                //Server udpServer = new Server(port, Protocols.UDP);

                new Thread(tcpServer).start();
                //new Thread(udpServer).start();

                break;
            default:
                System.out.println("Usage: java TCPTester <host> <port>");
                System.exit(1);
        }
    }  
}
