package src.sample;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.log4j.BasicConfigurator;
import src.SipClient;
import src.udpP2P.UdpP2P;

import javax.sip.*;
import javax.sip.Dialog;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.header.*;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.net.InetAddress.getLocalHost;

public class Controller implements SipListener {

    static int zmienna = 0;
    // Objects used to communicate to the JAIN SIP API.
  private   SipFactory sipFactory;          // Used to access the SIP API.
    private SipStack sipStack;              // The SIP stack.
    private SipProvider sipProvider;        // Used to send SIP messages.
    private MessageFactory messageFactory;  // Used to create SIP message factory.
    private HeaderFactory headerFactory;    // Used to create SIP headers.
    private  AddressFactory addressFactory;  // Used to create SIP URIs.
    private  ListeningPoint listeningPoint;  // SIP listening IP address/port.
    private   Properties properties;          // Other properties.

    // Objects keeping local configuration.
    static String ip;                      // The local IP address.
    static int port;                // The local port.
    String protocol = "udp";        // The local protocol (UDP).
    int tag = (new Random()).nextInt(); // The local tag.
    private String remoteTag;    //The remote tag.
    Address contactAddress;         // The contact address.
    ContactHeader contactHeader;    // The contact header.


    Dialog dialog; //
    UdpP2P client2;
    Logger Log = Logger.getLogger("Controller.class");

    UdpP2P client1;
    private ObservableList<String> activeUsers;

    @FXML
    private TextArea textAreaId;

    @FXML
    private TextField textFieldId;

    @FXML
private ListView<String> listViewId;

    private void onOpen() {
        // A method called when you open your application.


        try {
            // Get the local IP address.
            // this.ip = InetAddress.getLocalHost().getHostAddress();
            // Create the SIP factory and set the path name.
            this.sipFactory = SipFactory.getInstance();
            this.sipFactory.setPathName("gov.nist");
            // Create and set the SIP stack properties.
            this.properties = new Properties();
            this.properties.setProperty("javax.sip.STACK_NAME", "stack");
            // Create the SIP stack.
            this.sipStack = this.sipFactory.createSipStack(this.properties);
            // Create the SIP message factory.
            this.messageFactory = this.sipFactory.createMessageFactory();
            // Create the SIP header factory.
            this.headerFactory = this.sipFactory.createHeaderFactory();
            // Create the SIP address factory.
            this.addressFactory = this.sipFactory.createAddressFactory();
            // Create the SIP listening point and bind it to the local IP address, port and protocol.
            this.listeningPoint = this.sipStack.createListeningPoint(this.ip, this.port, this.protocol);
            // Create the SIP provider.
            this.sipProvider = this.sipStack.createSipProvider(this.listeningPoint);
            // Add our application as a SIP listener.
            this.sipProvider.addSipListener(this);
            // Create the contact address used for all SIP messages.
            this.contactAddress = this.addressFactory.createAddress("sip:cezar@" + this.ip + ":" + this.port);
            // Create the contact header used for all SIP messages.
            this.contactHeader = this.headerFactory.createContactHeader(contactAddress);

            // Display the local IP address and port in the text area.
            textAreaId.appendText("Local address: " + this.ip + ":" + this.port + "\n");

            activeUsers= FXCollections.observableArrayList();

            /*final String[] strings = {
                    "Row 1", "Row 2", "Long Row 3", "Row 4", "Row 5", "Row 6", "Row 7",
                    "Row 8", "Row 9", "Row 10", "Row 11", "Row 12", "Row 13", "Row 14",
                    "Row 15", "Row 16", "Row 17", "Row 18", "Row 19", "Row 20"
            };
            activeUsers.addAll(strings);*/
            listViewId.setItems(activeUsers);

        } catch (Exception e) {
            // If an error occurs, display an error message box and exit.
            // JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void initialize() throws UnknownHostException {
        port = 5080;
        ip = getLocalHost().getHostAddress();
        onOpen();

    }


    public void onInviteClicked() {
        // A method called when you click on the "Invite" button.


        try {
            // Get the destination address from the text field.
            Address addressTo = this.addressFactory.createAddress(textFieldId.getText());
            // Create the request URI for the SIP message.
            javax.sip.address.URI requestURI = addressTo.getURI();


            // Create the SIP message headers.

            // The "Via" headers.
            ArrayList viaHeaders = new ArrayList();
            ViaHeader viaHeader = this.headerFactory.createViaHeader(this.ip, this.port, "udp", null);
            viaHeaders.add(viaHeader);
            // The "Max-Forwards" header.
            MaxForwardsHeader maxForwardsHeader = this.headerFactory.createMaxForwardsHeader(70);
            // The "Call-Id" header.
            CallIdHeader callIdHeader = this.sipProvider.getNewCallId();
            // The "CSeq" header.
            CSeqHeader cSeqHeader = this.headerFactory.createCSeqHeader(1L, Request.INVITE);
            // The "From" header.
            FromHeader fromHeader = this.headerFactory.createFromHeader(this.contactAddress, String.valueOf(this.tag));
            // The "To" header.
            ToHeader toHeader = this.headerFactory.createToHeader(addressTo, remoteTag);

            // Create the REGISTER request.
            Request request = this.messageFactory.createRequest(
                    requestURI,
                    "INVITE",
                    callIdHeader,
                    cSeqHeader,
                    fromHeader,
                    toHeader,
                    viaHeaders,
                    maxForwardsHeader);
            // Add the "Contact" header to the request.
            request.addHeader(contactHeader);

            // Send the request statelessly through the SIP provider.
// this.sipProvider.sendRequest(request);
            //   System.out.println(request);
// Create a new SIP client transaction.
            ClientTransaction transaction = this.sipProvider.getNewClientTransaction(request);
// Send the request statefully, through the client transaction.
            transaction.sendRequest();

            // Display the message in the text area.
            this.textAreaId.appendText(
                    "Request sent:\n" + request.toString() + "\n\n");
        } catch (Exception e) {
            // If an error occurred, display the error.
            this.textAreaId.appendText("Request sent failed: " + e.getMessage() + "\n");
        }

    }

    public void onByeClicked() {
        try {
            // A method called when you click on the "Bye" button.

            Request request = this.dialog.createRequest("BYE");
            ClientTransaction transaction = this.sipProvider.getNewClientTransaction(request);
            this.dialog.sendRequest(transaction);

        } catch (SipException ex) {
            Logger.getLogger(SipClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void onSearchClicked() throws InterruptedException {

     //   activeUsers.removeAll();
//listViewId.refresh();
listViewId.getItems().clear();


        new Thread(new Runnable() {

            @Override
            public void run() {

                try {



                    // Create the SIP message headers.

                    // The "Via" headers.
                    ArrayList viaHeaders = new ArrayList();
                    ViaHeader viaHeader = headerFactory.createViaHeader(ip, port, "udp", null);
                    viaHeaders.add(viaHeader);
                    // The "Max-Forwards" header.
                    MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
                    // The "Call-Id" header.
                    CallIdHeader callIdHeader = sipProvider.getNewCallId();
                    // The "CSeq" header.
                    CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L, "MESSAGE");

                    // The "From" header.
                    FromHeader fromHeader = headerFactory.createFromHeader(contactAddress, String.valueOf(tag));




                    //we are broadcasting message to whole WLAN

                    String adressDocelowy = InetAddress.getLocalHost().getHostAddress();
                    adressDocelowy.split(".");

                    String substr = new String();
                    for (int jPort = 5080; jPort < 5083; ++jPort)
                        for (int i = 1; i <254; ++i) {

                            String ipToSend=InetAddress.getLocalHost().getHostAddress().substring(0, 10) + i;

                            if(   !( (port==jPort) && (ip.equals(ipToSend)))  )
                            {

                                substr = "sip" + ":" + InetAddress.getLocalHost().getHostAddress().substring(0, 10) + i + ":" + jPort;


                                // The "To" header.
                                ToHeader      toHeader = headerFactory.createToHeader(addressFactory.createAddress(substr), null);
                                System.out.println(toHeader);


                                // Create the REGISTER request.
                                Request request = messageFactory.createRequest(
                                        addressFactory.createAddress(substr).getURI(),
                                        Request.MESSAGE,
                                        callIdHeader,
                                        cSeqHeader,
                                        fromHeader,
                                        toHeader,
                                        viaHeaders,
                                        maxForwardsHeader);
                                // Add the "Contact" header to the request.
                                request.addHeader(contactHeader);
                                System.out.println("via" + headerFactory.createViaHeader(ip, port, "udp", null).toString());
                                ContentTypeHeader contentTypeHeader = headerFactory
                                        .createContentTypeHeader("text", "plain");
                                request.setContent("send me your URI", contentTypeHeader);


                                // Send the request statelessly through the SIP provider.
                                sipProvider.sendRequest(request);
                            }
                        }


                } catch (Exception e) {
                    // If an error occurred, display the error.

                   Platform.runLater(new Runnable() {
                       @Override
                       public void run() {
                           textAreaId.appendText("Request sent failed: " + e.getMessage() + "\n");
                       }
                   });
                }

            }
        }).start();



    }

    @Override
    public void processRequest(RequestEvent requestEvent) {
        // Get the request.
        Request request = requestEvent.getRequest();

        textAreaId.appendText("\nRECV " + request.getMethod() + " " + request.getRequestURI().toString());


        try {
            // Get or create the server transaction.
            ServerTransaction transaction = requestEvent.getServerTransaction();
            if(null == transaction) {
                transaction = this.sipProvider.getNewServerTransaction(request);
            }

            // Update the SIP message table.

            textAreaId.appendText(request.toString());
     //       this.updateTable(requestEvent, request, transaction);

            // Process the request and send a response.
            Response response;
            if(request.getMethod().equals("REGISTER")) {
                // If the request is a REGISTER.
                response = this.messageFactory.createResponse(200, request);
                ((ToHeader)response.getHeader("To")).setTag(String.valueOf(this.tag));
                response.addHeader(this.contactHeader);
                transaction.sendResponse(response);
                textAreaId.appendText(" / SENT " + response.getStatusCode() + " " + response.getReasonPhrase());
            }
            else if(request.getMethod().equals("INVITE")) {


                //trying or not
                response = this.messageFactory.createResponse(180, request);
                ((ToHeader)response.getHeader("To")).setTag(String.valueOf(this.tag));
                response.addHeader(this.contactHeader);
                transaction.sendResponse(response);
                textAreaId.appendText(" / SENT " + response.getStatusCode() + " " + response.getReasonPhrase());

                //czy odebrać czy nie




                // If the request is an INVITE.
                response = this.messageFactory.createResponse(200, request);
                ((ToHeader)response.getHeader("To")).setTag(String.valueOf(this.tag));
                response.addHeader(this.contactHeader);
                transaction.sendResponse(response);
                textAreaId.appendText(" / SENT " + response.getStatusCode() + " " + response.getReasonPhrase());
            }
            else if(request.getMethod().equals("ACK")) {
                // If the request is an ACK.
                client1=new UdpP2P();
                client1.setHOST(ip);
                client1.setPORT(port+1);

                String line=request.getHeader("From").toString();

                // String to be scanned to find the pattern.
                String pattern = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d{1,5})";

                // Create a Pattern object
                Pattern r = Pattern.compile(pattern);

                // Now create matcher object.
                Matcher m = r.matcher(line);
                if (m.find( )) {
                    System.out.println("Found value: " + m.group(0) );
                    System.out.println("Found value: " + m.group(1) );
                    System.out.println("Found value: " + m.group(2) );
                }else {
                    System.out.println("NO MATCH");
                }

                client1.setServerHOST(m.group(1));
                client1.setServerPORT(Integer.valueOf(m.group(2))+1);
                System.out.println("CLIENT1  "+client1.getHOST()+client1.getPORT()+"  server"+client1.getServerHOST()+" "+client1.getServerPORT());
                client1.init();
            }
            else if(request.getMethod().equals("BYE")) {
                // If the request is a BYE.
                response = this.messageFactory.createResponse(200, request);
                ((ToHeader)response.getHeader("To")).setTag(String.valueOf(this.tag));
                response.addHeader(this.contactHeader);
                transaction.sendResponse(response);
                client1.endSession();
                transaction.terminate();

                textAreaId.appendText(" / SENT " + response.getStatusCode() + " " + response.getReasonPhrase());

            }
            else if(request.getMethod().equals("MESSAGE")){
                response = this.messageFactory.createResponse(200, request);
                // The "To" header.
                ToHeader toHeader = headerFactory.createToHeader(contactHeader.getAddress(), remoteTag);
                response.setHeader(toHeader);

                response.addHeader(this.contactHeader);


                ContentTypeHeader   contentTypeHeader = headerFactory
                        .createContentTypeHeader("text", "plain");
                response.setContent(response.getHeader("To").toString(),contentTypeHeader);

                transaction.sendResponse(response);
                textAreaId.appendText(" / SENT " + response.getStatusCode() + " " + response.getReasonPhrase());


            }
        }
        catch(SipException e) {
            textAreaId.appendText("\nERROR (SIP): " + e.getMessage());
        }
        catch(Exception e) {
            textAreaId.appendText("\nERROR: " + e.getMessage());
        }
    }

    @Override
    public void processResponse(ResponseEvent responseEvent) {
        //Dla register tez bys musial response dac

        // System.out.println(responseEvent.getResponse().toString());

        // A method called when you receive a SIP request.
        //  System.out.println("proces response w "+responseEvent.getClientTransaction().toString());

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Get the response.
        Response response = responseEvent.getResponse();
        CSeqHeader cseq = (CSeqHeader) response.getHeader(CSeqHeader.NAME);


        // System.out.println("client trans"+ responseEvent.getClientTransaction());


// Display the response message in the text area.
        textAreaId.appendText("\nReceived response: " + response.toString());

        ToHeader toHeader = (ToHeader)response.getHeader("To");

        remoteTag=toHeader.getTag();




        if (response.getStatusCode() == Response.OK) {
            //System.out.println(cseq.getMethod());
            if (cseq.getMethod().equals(Request.INVITE)) {
                try {

                    if (responseEvent.getClientTransaction() != null) {
                        dialog = responseEvent.getDialog();
                        if (dialog != null) {

                            Request request = dialog.createAck(((CSeqHeader) response.getHeader("CSeq")).getSeqNumber());
                            request.addHeader(contactHeader);
                            dialog.sendAck(request);
                            responseEvent.getClientTransaction().terminate();;


                            client2 = new UdpP2P();
                            client2.setHOST(ip);
                            client2.setPORT(port + 1);
                            System.out.println(request.getRequestURI());
                            String[] URIclient2 = request.getRequestURI().toString().split(":");
                            int port2 = Integer.valueOf(URIclient2[2]);
                            String ip2 = URIclient2[1];
                            client2.setServerHOST(ip2);
                            client2.setServerPORT(port2 + 1);
                            client2.init();
                        }
                    }

                } catch (InvalidArgumentException ex) {
                    Logger.getLogger(SipClient.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SipException ex) {
                    Logger.getLogger(SipClient.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else if(cseq.getMethod().equals(Request.BYE)){
                //  System.out.println("wejscie do bye");
                Log.info(client2.getSocket().toString());
                client2.endSession();
                try {
                    responseEvent.getClientTransaction().terminate();
                } catch (ObjectInUseException e) {
                    e.printStackTrace();
                }
                dialog.delete();


            }else if(cseq.getMethod().equals(Request.MESSAGE) ){

                System.out.println(++zmienna);
                System.out.println(responseEvent.getResponse().getHeader("To").toString());

                // String to be scanned to find the pattern.
                String pattern = "(sip:[A-Za-z0-9]*@\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d{1,5})";

                // Create a Pattern object
                Pattern r = Pattern.compile(pattern);

                // Now create matcher object.
                Matcher m = r.matcher(responseEvent.getResponse().getHeader("To").toString());
                if (m.find( )) {
                    System.out.println("Found value: " + m.group(0) );

Platform.runLater(new Runnable() {
    @Override
    public void run() {
        activeUsers.add(m.group(0));
    }
});
//listViewId.setItems(activeUsers);
                }else {
                    System.out.println("NO MATCH");
                }

            }
        }

    }

    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {

    }

    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {

    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {

    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {

    }
}