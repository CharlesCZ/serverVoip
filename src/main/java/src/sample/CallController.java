package src.sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;


import javax.sip.*;
import javax.sip.header.ContactHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CallController {



    @FXML
    private Button cancelBtn;

    @FXML
    private Button connectBtn;

    @FXML
    private Label label;
   private Request request;


   private Response response;

   private ServerTransaction transaction;

   private MessageFactory messageFactory;

   private Integer tag;

   private ContactHeader contactHeader;

   private Dialog dialog;
    private SipProvider sipProvider;        // Used to send SIP messages.
    public void initData(Request request, Response response, ServerTransaction transaction, Integer tag, ContactHeader contactHeader, MessageFactory messageFactory, Dialog dialog,SipProvider sipProvider){
this.request=request;
this.response=response;
this.transaction=transaction;
this.messageFactory=messageFactory;
this.tag=tag;
this.contactHeader=contactHeader;
this.dialog=dialog;
this.sipProvider=sipProvider;
        // String to be scanned to find the pattern.
        String pattern = "(sip:[A-Za-z0-9]*@\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d{1,5})";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(response.getHeader("From").toString());
        if (m.find( )) {
            label.setText(m.group(0));
        }


    }
    @FXML
    public void onConnectAction() throws ParseException, SipException, InvalidArgumentException {
        // If the request is an INVITE.
        response = this.messageFactory.createResponse(200, request);
        ((ToHeader)response.getHeader("To")).setTag(String.valueOf(this.tag));




        response.addHeader(this.contactHeader);
        transaction.sendResponse(response);
        System.out.println(" / SENT " + response.getStatusCode() + " " + response.getReasonPhrase());


        // get a handle to the stage
        Stage stage = (Stage) connectBtn.getScene().getWindow();
        // do what you have to do
        stage.close();
    }


    @FXML
    public void onCancelAction(){

        try {
            // A method called when you click on the "Bye" button.
            if(dialog!=null) {
                Request request = this.dialog.createRequest("BYE");
                ClientTransaction transaction = this.sipProvider.getNewClientTransaction(request);
                this.dialog.sendRequest(transaction);
            }

        } catch (SipException ex) {
            Logger.getLogger(CallController.class.getName()).log(Level.SEVERE, null, ex);
        }


        // get a handle to the stage
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        // do what you have to do
        stage.close();

    }
}
