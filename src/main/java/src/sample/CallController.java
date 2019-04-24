package src.sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javax.sip.InvalidArgumentException;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.header.ContactHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;

public class CallController {



    @FXML
    private Button cancelBtn;

    @FXML
    private Button connectBtn;

   private Request request;


   private Response response;

   private ServerTransaction transaction;

   private MessageFactory messageFactory;

   private Integer tag;

   private ContactHeader contactHeader;
    public void initData(Request request, Response response, ServerTransaction transaction, Integer tag, ContactHeader contactHeader, MessageFactory messageFactory){
this.request=request;
this.response=response;
this.transaction=transaction;
this.messageFactory=messageFactory;
this.tag=tag;
this.contactHeader=contactHeader;

    }
    @FXML
    public void onConnectAction() throws ParseException, SipException, InvalidArgumentException {
        // If the request is an INVITE.
        response = this.messageFactory.createResponse(200, request);
        ((ToHeader)response.getHeader("To")).setTag(String.valueOf(this.tag));
        response.addHeader(this.contactHeader);
        transaction.sendResponse(response);
        System.out.println(" / SENT " + response.getStatusCode() + " " + response.getReasonPhrase());
    }


    @FXML
    public void onCancelAction(){
        // get a handle to the stage
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        // do what you have to do
        stage.close();

    }
}
