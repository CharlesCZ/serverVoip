package src.sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import src.model.DatabaseVoip;
import src.model.User;

import java.io.IOException;
import java.util.List;


public class LoginController {

    @FXML
    private TextField loginUserFieldId;

    @FXML
    private PasswordField loginPasswordFieldId;

    @FXML
    private TextField registerUserFieldId;

    @FXML
    private TextField registerPasswordFieldId;

    @FXML
    private TextField registerConfPasswordFieldId;

    @FXML
    private Text textUsername;

    @FXML
    private  Text passwordMisMatch;

    @FXML
    private Text passwordMisMatch2;

    @FXML
    private Text statusRegisterId;

    @FXML
    private Text loginText;

    @FXML
    private void onLoginAction(ActionEvent event) throws IOException {
        loginText.setText("");
        DatabaseVoip d=new DatabaseVoip();

        User user=d.selectUser(loginUserFieldId.getText(),GFG.encryptThisString(loginPasswordFieldId.getText()));
if(user==null){
    loginText.setText("Wrong login or password");
    return;
}
       // d.insertHistoryConnection(user.getId(),"sip sendera","sip invitera",new Date(System.currentTimeMillis()),new Date(System.currentTimeMillis()));

        ((Node)event.getSource()).getScene().getWindow().hide();
        Stage primaryStage= new Stage();
FXMLLoader loader=new FXMLLoader();
        Parent root = loader.load(getClass().getResource("/sample.fxml").openStream());
        Controller afterLoginController=(Controller)loader.getController();
        afterLoginController.setUser(user);
        afterLoginController.create();
        ControllerManager.controller=afterLoginController;
        primaryStage.setTitle("voip");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();

    }


    @FXML
    private void onRegisterAction(){
        textUsername.setText("");
        passwordMisMatch.setText("");
        passwordMisMatch2.setText("");
        statusRegisterId.setText("");

        DatabaseVoip d=new DatabaseVoip();
        List<User> users=d.selectUsers();
       if( users.stream().anyMatch(user -> user.getLogin().equals(registerUserFieldId.getText()))) {
           textUsername.setText("Login already exists");
            return;
       }

        if( !registerPasswordFieldId.getText().equals(registerConfPasswordFieldId.getText())) {
            passwordMisMatch.setText("Password mismatch");
            passwordMisMatch2.setText("Password mismatch");
            return;
        }
         d=new DatabaseVoip();
        d.insertUser(registerUserFieldId.getText(),GFG.encryptThisString(registerPasswordFieldId.getText()));
        statusRegisterId.setText("Account created!");

    }
}
