package src.udpP2P;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class UdpP2P {

    /**
     * Adress we want to be binded.
     */
    private  String HOST;
    /**
     * The port  we want to be binded.
     */
    private  int PORT;

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

Thread speakerThread;
Thread microphoneThread;
    Logger Log= Logger.getLogger("UdpP2P.class");
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

    public DatagramSocket getSocket() {
        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    public UdpP2P() {
        this.microphone = new Microphone();
        this.speaker = new Speaker();
    }


   public void init() throws IOException {
        InetAddress clientAddress = InetAddress.getByName(HOST);

     //  System.out.println("tu zawsze wywala");
       System.out.println(PORT+"    "+clientAddress);
        socket = new DatagramSocket(PORT,clientAddress); //Otwarcie gniazda

socket.connect(InetAddress.getByName(serverHOST),serverPORT);
    //    System.out.println(socket.getPort()+" "+socket.getInetAddress());
        //Reads data received from server
       speakerThread= new Thread(new Runnable() {
            @Override
            public void run() {
                if (speaker.open()) {
                    speaker.start();
                System.out.println("socket status speak "+socket.isConnected());
                    while (socket.isConnected()) {
                        try {
                            byte[] buffer = new byte[speaker.getBufferSize() / 5];
                            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
                             socket.receive(receivedPacket);
                          //  System.out.println(receivedPacket.getPort());

                                 int read = receivedPacket.getLength();
                                 speaker.write(buffer, 0, read);

                        } catch (IOException e) {
                            System.err.println("Could not read data from server:" + e.getMessage());
                        }
                    }
                }
            }
        });
       speakerThread.start();

     //   System.out.println("miedzy");
        //Sends data to server
        microphoneThread=new Thread(new Runnable() {
            @Override
            public void run() {
                if (microphone.open()) {
                    microphone.start();
               //   System.out.println("socket status  mic"+socket.isConnected());
                  //  System.out.println(socket.getPort()+" "+socket.getInetAddress());
                    while (socket.isConnected()) {
                        try {
                            byte[] buffer = new byte[microphone.getBufferSize() / 5];
                            int read = microphone.read(buffer, 0, buffer.length);
                            //System.out.println("wewnatrz microph");
                           // System.out.println(read);
                            DatagramPacket sentPacket = new DatagramPacket(buffer, read, InetAddress.getByName(serverHOST),serverPORT);
                            socket.send(sentPacket);
                        } catch (Exception e) {
                            System.err.println("Could not send data to server:"+ e.getMessage());
                        }
                    }
                }
            }
        });
microphoneThread.start();

        System.out.println("po");

    }

public void endSession(){
    try {
        microphone.stop();
        microphoneThread.stop();
        microphoneThread=null;
        speaker.stop();
        speakerThread.stop();
        speakerThread=null;

        Log.info(socket.toString()+"   is connected? "+socket.isConnected());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }finally {
        socket.close();
        System.out.println("po wszystkim EndSession");
    }

}





}
