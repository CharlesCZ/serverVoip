package src.sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import src.model.DatabaseVoip;
import src.model.HistoryConnection;


import javax.sip.*;
import javax.sip.header.ContactHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private HistoryConnection historyConnection;

    private SimpleAudioPlayer  audioPlayer;

    private  Thread thread;

    AtomicBoolean flagOfRingTone;
    public void initData(Request request, Response response, ServerTransaction transaction, Integer tag, ContactHeader contactHeader, MessageFactory messageFactory, Dialog dialog, SipProvider sipProvider, HistoryConnection historyConnection) throws UnsupportedAudioFileException, IOException {
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

this.historyConnection=historyConnection;

flagOfRingTone=new AtomicBoolean(true);

        String projectPath=System.getProperty("user.dir");
        System.setProperty("ring.tone", projectPath + "/telephone-ring-04.wav");
        SimpleAudioPlayer.filePath = System.getProperty("ring.tone");
        try {
            audioPlayer =
                    new SimpleAudioPlayer();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                while (flagOfRingTone.get()) {
                    audioPlayer.play();
                }
                try {
                    audioPlayer.stop();
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                    e.printStackTrace();
                }


            }
        });
       thread.start();


    }
    @FXML
    public void onConnectAction() throws ParseException, SipException, InvalidArgumentException {
      flagOfRingTone.set(false);
        // If the request is an INVITE.
        response = this.messageFactory.createResponse(200, request);
        ((ToHeader)response.getHeader("To")).setTag(String.valueOf(this.tag));




        response.addHeader(this.contactHeader);
        transaction.sendResponse(response);
        System.out.println(" / SENT " + response.getStatusCode() + " " + response.getReasonPhrase());
historyConnection.setBeginDate(new Timestamp(System.currentTimeMillis()));


        ControllerManager.controller.setInfoInTextAreaId(" / SENT " + response.getStatusCode() + " " + response.getReasonPhrase());
        // get a handle to the stage
        Stage stage = (Stage) connectBtn.getScene().getWindow();
        // do what you have to do
        stage.close();

    }


    @FXML
    public void onCancelAction(){
        flagOfRingTone.set(false);
        try {
            // A method called when you click on the "Bye" button.



                response = this.messageFactory.createResponse(603, request);
                ((ToHeader)response.getHeader("To")).setTag(String.valueOf(this.tag));
                response.addHeader(this.contactHeader);
                transaction.sendResponse(response);
                System.out.println(" / SENT " + response.getStatusCode() + " " + response.getReasonPhrase());


         ControllerManager.controller.setInfoInTextAreaId(" / SENT " + response.getStatusCode() + " " + response.getReasonPhrase());
        } catch (SipException ex) {
            Logger.getLogger(CallController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidArgumentException | ParseException e) {
            e.printStackTrace();
        }

        historyConnection.setEndDate(historyConnection.getBeginDate());
        DatabaseVoip databaseVoip=new DatabaseVoip();
        databaseVoip.insertHistoryConnection(historyConnection);
        // get a handle to the stage
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        // do what you have to do
        stage.close();


    }


    public  void stoppedByCaller(){
        flagOfRingTone.set(false);

        // get a handle to the stage
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        // do what you have to do
        stage.close();
    }
}
