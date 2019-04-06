package src.udpP2P;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class udpP2P {

    /**
     * Adress we want to be binded.
     */
    private  String HOST = System.getProperty("host", "192.168.0.11");
    /**
     * The port  we want to be binded.
     */
    private  int PORT = Integer.parseInt(System.getProperty("port", "5081"));

    private  String serverHOST;

    private  int serverPORT;
    /**
     * The DatagramSocket instance for server/client interaction.
     */
    private DatagramSocket socket;
    /**
     * The {@link Microphone} instance.
     */
    private Microphone microphone;
    /**
     * The {@link Speaker} instance.
     */
    private Speaker speaker;


    public String getHOST() {
        return HOST;
    }

    public void setHOST(String HOST) {
        this.HOST = HOST;
    }

    public int getPORT() {
        return PORT;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }


    public String getServerHOST() {
        return serverHOST;
    }

    public void setServerHOST(String serverHOST) {
        this.serverHOST = serverHOST;
    }

    public int getServerPORT() {
        return serverPORT;
    }

    public void setServerPORT(int serverPORT) {
        this.serverPORT = serverPORT;
    }

    public udpP2P() {
        this.microphone = new Microphone();
        this.speaker = new Speaker();
    }


   public void init() throws IOException {
        InetAddress clientAddress = InetAddress.getByName(HOST);


        socket = new DatagramSocket(PORT,clientAddress); //Otwarcie gniazda

socket.connect(InetAddress.getByName(serverHOST),serverPORT);
        System.out.println(socket.getPort()+" "+socket.getInetAddress());
        //Reads data received from server
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (speaker.open()) {
                    speaker.start();
                  //  System.out.println("port gniazda"+socket.getPort());
                    while (socket.isConnected()) {
                        try {
                            byte[] buffer = new byte[speaker.getBufferSize() / 5];
                            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
                             socket.receive(receivedPacket);
                            int read =receivedPacket.getLength();
                            speaker.write(buffer, 0, read);

                        } catch (IOException e) {
                            System.err.println("Could not read data from server:" + e.getMessage());
                        }
                    }
                }
            }
        }).start();

        System.out.println("miedzy");
        //Sends data to server
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (microphone.open()) {
                    microphone.start();

                    while (socket.isConnected()) {
                        try {
                            byte[] buffer = new byte[microphone.getBufferSize() / 5];
                            int read = microphone.read(buffer, 0, buffer.length);
                           // System.out.println("wewnatrz microph");
                           // System.out.println(read);
                            DatagramPacket sentPacket = new DatagramPacket(buffer, read, InetAddress.getByName(serverHOST),serverPORT);
                            socket.send(sentPacket);
                        } catch (Exception e) {
                            System.err.println("Could not send data to server:"+ e.getMessage());
                        }
                    }
                }
            }
        }).start();


        System.out.println("po");
        while (true);
    }


    public static void main(String[] args) throws IOException, UnknownHostException {




udpP2P client=new udpP2P();
        client.setHOST(System.getProperty("host", "192.168.0.11"));
        client.setPORT(Integer.parseInt(System.getProperty("port", "5081")));
        client.setServerHOST(System.getProperty("serverhost", "192.168.0.11"));
        client.setServerPORT(Integer.parseInt(System.getProperty("port", "5080")));
        client.init();

client.init();

    }


}
