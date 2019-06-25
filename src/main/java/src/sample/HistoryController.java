package src.sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import src.model.DatabaseVoip;
import src.model.HistoryConnection;
import src.model.User;

public class HistoryController {

    @FXML
    private TableColumn columnId;
    @FXML
    private TableColumn columnUser_Id;
    @FXML
    private TableColumn columnUri_sender;
    @FXML
    private TableColumn columnUri_inviter;
    @FXML
    private TableColumn columnBegin_date;
    @FXML
    private TableColumn columnEnd_date;
    @FXML
    private TableView tableViewId;
    private User user;


    public void create(User user) {
        this.user=user;
        DatabaseVoip d=new DatabaseVoip();
    final ObservableList<HistoryConnection> data=  FXCollections.observableArrayList(d.selectHistoryConnectionByUserId(user.getId()));
tableViewId.setItems(data);
//tableViewId.getItems().set
        System.out.println(tableViewId.getItems().get(0));

tableViewId.refresh();

    }





}
