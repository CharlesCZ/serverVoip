package src.sample;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import src.model.DatabaseVoip;
import src.model.HistoryConnection;
import src.model.User;

public class HistoryController {

    @FXML
    private TableColumn columnHashId;
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
        columnHashId.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HistoryConnection, HistoryConnection>, ObservableValue<HistoryConnection>>() {
            @Override public ObservableValue<HistoryConnection> call(TableColumn.CellDataFeatures<HistoryConnection, HistoryConnection> p) {
                return new ReadOnlyObjectWrapper(tableViewId.getItems().indexOf(p.getValue()) + "");
            }
        });
        columnHashId.setSortable(false);
        this.user=user;
        DatabaseVoip d=new DatabaseVoip();
    final ObservableList<HistoryConnection> data=  FXCollections.observableArrayList(d.selectHistoryConnectionByUserId(user.getId()));
tableViewId.setItems(data);
tableViewId.refresh();

    }





}
