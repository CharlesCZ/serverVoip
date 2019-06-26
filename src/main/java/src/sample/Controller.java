package src.sample;




import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import src.model.DatabaseVoip;
import src.model.HistoryConnection;
import src.model.User;
import src.udpP2P.UdpP2P;

import javax.sip.*;
import javax.sip.Dialog;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.header.*;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
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
    String toAdressBye;

    Dialog dialog; //
    UdpP2P client2;
    Logger Log = Logger.getLogger("Controller.class");

    UdpP2P client1;
    String sipNick;
    private User user;
private HistoryConnection historyConnection;
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private ObservableList<String> activeUsers;

    @FXML
    private TextArea textAreaId;

    @FXML
    private TextField textFieldId;

    @FXML
    private ListView<String> listViewId;


    public void setInfoInTextAreaId(String info){

        textAreaId.appendText(info);
    }


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
            this.contactAddress = this.addressFactory.createAddress("sip:"+sipNick+"@" + this.ip + ":" + this.port);
            // Create the contact header used for all SIP messages.
            this.contactHeader = this.headerFactory.createContactHeader(contactAddress);

            // Display the local IP address and port in the text area.
            textAreaId.appendText("Local address: sip:"+sipNick+"@" + this.ip + ":" + this.port + "\n");

            activeUsers= FXCollections.observableArrayList();


            listViewId.setItems(activeUsers);

        } catch (Exception e) {
            // If an error occurs, display an error message box and exit.
            // JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }




    public void create() throws UnknownHostException {

        sipNick=user.getLogin();
        historyConnection=new HistoryConnection();
        historyConnection.setIdUser(user.getId());
        port = 5080;
        ip = getLocalHost().getHostAddress();
        System.out.println("Moj adres"+sipNick+" "+ip+"  "+port);
        onOpen();

    }

    @FXML
    public void signOutAction(ActionEvent event)  {
        try {

sipStack.stop();
sipFactory.resetFactory();
            if(client1!=null){
                client1.endSession();
            }

            if(client2!=null) {
                client2.endSession();
            }
            ((Node)event.getSource()).getScene().getWindow().hide();
            Stage primaryStage= new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            primaryStage.setTitle("voip");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
primaryStage.setOnCloseRequest(event1 -> {
    Platform.exit();
    System.exit(0);});

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void initSecond(Request request, Response response, ServerTransaction transaction, Integer tag, ContactHeader contactHeader, MessageFactory messageFactory){
        try{
            Stage userStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Pane root = (Pane) loader.load(getClass().getResource("/call.fxml").openStream());

            CallController callController = (CallController) loader.getController();
            callController.initData(request,response,transaction,tag,contactHeader,messageFactory,dialog,sipProvider,historyConnection);

            Scene scene= new Scene(root);
            userStage.setScene(scene);
            userStage.setTitle("Connection");
            //
            // userStage.setResizable(false);
            userStage.showAndWait();
        }catch (IOException | UnsupportedAudioFileException ex)
        {
            ex.printStackTrace();
        }
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

            historyConnection.setUriSender("sip:"+sipNick+"@"+ip+":"+port);
            historyConnection.setUriInvited(addressTo.toString());
            historyConnection.setBeginDate(new Timestamp(System.currentTimeMillis()));
        } catch (Exception e) {
            // If an error occurred, display the error.
            this.textAreaId.appendText("Request sent failed: " + e.getMessage() + "\n");
        }

    }

    public void onByeClicked() {
        try {
            // A method called when you click on the "Bye" button.
            if(dialog!=null) {
                Request request = this.dialog.createRequest("BYE");
                ClientTransaction transaction = this.sipProvider.getNewClientTransaction(request);
                this.dialog.sendRequest(transaction);
                historyConnection.setEndDate(new Timestamp(System.currentTimeMillis()));
                DatabaseVoip databaseVoip=new DatabaseVoip();
                databaseVoip.insertHistoryConnection(historyConnection);

                // Display the message in the text area.
                this.textAreaId.appendText(
                        "Request sent:\n" + request.toString() + "\n\n");
            }

        } catch (SipException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            // If an error occurred, display the error.
            this.textAreaId.appendText("Request sent failed: " + ex.getMessage() + "\n");
        }

    }

    public void onSearchClicked() throws InterruptedException {

          activeUsers.removeAll();
//listViewId.refresh();
        listViewId.getItems().clear();

        // Display the message in the text area.
        this.textAreaId.appendText(
                "Searching for network"+ "\n\n");

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
                    String[] czescAdresu=  adressDocelowy.split("\\.");

                    for (int jPort = 5080; jPort < 5081; ++jPort)
                        for (int i = 1; i <254; ++i) {

                            String ipToSend=czescAdresu[0]+"."+czescAdresu[1]+"."+czescAdresu[2]+"." + i;

                            if(   !( (port==jPort) && (ip.equals(ipToSend)))  )
                            {

                                String  substr = "sip" + ":" + ipToSend + ":" + jPort;


                                // The "To" header.
                                ToHeader      toHeader = headerFactory.createToHeader(addressFactory.createAddress(substr), null);
                                //  System.out.println(toHeader);


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
                                //   System.out.println("via" + headerFactory.createViaHeader(ip, port, "udp", null).toString());
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
        System.out.println("requestyy");
        System.out.println("processRequest");
        System.out.println(requestEvent.getRequest().getMethod());
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

                // String to be scanned to find the pattern.
                String pattern = "(sip:[A-Za-z0-9]*@\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d{1,5})";
                // Create a Pattern object
                Pattern r = Pattern.compile(pattern);
                // Now create matcher object.
                Matcher m = r.matcher(request.getHeader("From").toString());
                if (m.find( )) {
                    historyConnection.setUriSender(m.group(0));
                }
                historyConnection.setUriInvited("sip:"+sipNick+"@"+ip+":"+port);
                historyConnection.setBeginDate(new Timestamp(System.currentTimeMillis()));


                //trying or not
                response = this.messageFactory.createResponse(180, request);
                ((ToHeader)response.getHeader("To")).setTag(String.valueOf(this.tag));
                response.addHeader(this.contactHeader);
                transaction.sendResponse(response);
                textAreaId.appendText(" / SENT " + response.getStatusCode() + " " + response.getReasonPhrase());

                //czy odebrać czy nie
                class  responseTask implements Runnable{
                    private Request request;
                    private Response response;
                    private ServerTransaction transaction;
                    private MessageFactory messageFactory;
                    private Integer tag;
                    private ContactHeader contactHeader;

                    public responseTask(Request request, Response response, ServerTransaction transaction, Integer tag, ContactHeader contactHeader, MessageFactory messageFactory){
                        this.request=request;
                        this.response=response;
                        this.transaction=transaction;
                        this.messageFactory=messageFactory;
                        this.tag=tag;
                        this.contactHeader=contactHeader;

                    }


                    @Override
                    public void run() {

                        initSecond(request,response,transaction,tag, contactHeader,messageFactory);

                    }
                }


                Platform.runLater(new  responseTask(request,response,transaction,tag, contactHeader,messageFactory));


                // If the request is an INVITE.
             /*   response = this.messageFactory.createResponse(200, request);
                ((ToHeader)response.getHeader("To")).setTag(String.valueOf(this.tag));
                response.addHeader(this.contactHeader);
                transaction.sendResponse(response);
                textAreaId.appendText(" / SENT " + response.getStatusCode() + " " + response.getReasonPhrase());*/
            }
            else if(request.getMethod().equals("ACK")) {
                // If the request is an ACK.
                dialog=requestEvent.getDialog();
                client1=new UdpP2P();
                client1.setHOST(ip);
                client1.setPORT(port+1);

                String line=request.getHeader("From").toString();
                toAdressBye=line;
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
                System.out.println("weszlo do bye");
                response = this.messageFactory.createResponse(200, request);
                ((ToHeader)response.getHeader("To")).setTag(String.valueOf(this.tag));
                response.addHeader(this.contactHeader);
                transaction.sendResponse(response);


                if(client1!=null){
                    client1.endSession();
                }

                if(client2!=null) {
                    client2.endSession();
                }
                transaction.terminate();
                dialog.delete();
                textAreaId.appendText(" / SENT " + response.getStatusCode() + " " + response.getReasonPhrase());
                historyConnection.setEndDate(new Timestamp(System.currentTimeMillis()));
                DatabaseVoip databaseVoip=new DatabaseVoip();
                databaseVoip.insertHistoryConnection(historyConnection);

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
        System.out.println("process Response");

        System.out.println(responseEvent.getResponse().toString());



        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Get the response.
        Response response = responseEvent.getResponse();
        CSeqHeader cseq = (CSeqHeader) response.getHeader(CSeqHeader.NAME);




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

                            this.textAreaId.appendText(
                                    "Request sent:\n" + request.toString() + "\n\n");
                            responseEvent.getClientTransaction().terminate();;


                            client2 = new UdpP2P();
                            client2.setHOST(ip);
                            client2.setPORT(port + 1);
                            System.out.println(request.getRequestURI());
                            String[] URIclient2 = request.getRequestURI().toString().split(":");
                            int port2 = Integer.valueOf(URIclient2[2]);
                            String ip2 = URIclient2[1];

                            // String to be scanned to find the pattern.
                            String pattern = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})";

                            Pattern r=Pattern.compile(pattern);

                            Matcher m=r.matcher(ip2);

                            if(m.find()) {
                                // System.out.println("znalazlo"+m.group(0));

                                client2.setServerHOST(m.group(0));
                                client2.setServerPORT(port2 + 1);
                                client2.init();
                            }
                            else{

                                System.out.println("not matched");
                            }
                        }
                    }

                } catch (InvalidArgumentException | SipException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else if(cseq.getMethod().equals(Request.BYE)){
               // System.out.println("wejscie do bye");

                if(client2!=null) {
                    Log.info(client2.getSocket().toString());
                    client2.endSession();
                }

                if(client1!=null){
                    client1.endSession();
                }

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
                            listViewId.setItems(activeUsers);
                            listViewId.refresh();
                        }
                    });


                }else {
                    System.out.println("NO MATCH");
                }

            }
        } if (response.getStatusCode() == Response.DECLINE){
            historyConnection.setEndDate(historyConnection.getBeginDate());
            DatabaseVoip databaseVoip=new DatabaseVoip();
            databaseVoip.insertHistoryConnection(historyConnection);
        }

    }

@FXML
    public void showHistoryAction(ActionEvent event) throws IOException {
    Stage primaryStage= new Stage();
    FXMLLoader loader=new FXMLLoader();
    Parent root = loader.load(getClass().getResource("/history.fxml").openStream());
    HistoryController afterMainController=(HistoryController) loader.getController();
   // afterMainController.setUser(user);
    afterMainController.create(user);
    primaryStage.setTitle("connection history");
    primaryStage.setScene(new Scene(root));
    primaryStage.show();
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