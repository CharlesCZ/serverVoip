package src.udpServer;



import src.udpClient.udpClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpServer {

    private static final String HOST = System.getProperty("host", "192.168.0.11");
    /**
     * The port to connect to the host with.
     */
    private static final int PORT = Integer.parseInt(System.getProperty("port", "5080"));
    /**
     * The DatagramSocket instance for server/client interaction.
     */
    private DatagramSocket socket;

void init() throws IOException {
    socket= new DatagramSocket(PORT, InetAddress.getByName(HOST));
    System.out.println(socket.getLocalPort()+ " "+socket.getLocalAddress());

   // socket.connect(InetAddress.getByName("192.168.0.11"),5081);
    while (true){
        byte[] buffer = new byte[1600]; //powinno byc 1600
        DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(receivedPacket);


        receivedPacket.setLength(receivedPacket.getLength());
        receivedPacket.setAddress(InetAddress.getByName(HOST));

        if(receivedPacket.getPort()!=5081) {
            receivedPacket.setPort(5081);
            DatagramPacket response = receivedPacket;
            socket.send(response);
        }
        else {
            receivedPacket.setPort(5082);
            DatagramPacket response = receivedPacket;
            socket.send(response);
        }
    }

}
    public static void main(String[] args) throws IOException {

    UdpServer server=new UdpServer();
    new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                server.init();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }).start();

        System.out.println("fafas");
        udpClient client=new udpClient();
        client.init();

    }

}
