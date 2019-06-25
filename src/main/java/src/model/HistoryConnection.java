package src.model;

import java.sql.Date;
import java.sql.Timestamp;

public class HistoryConnection {
   private int idCon;
   private int idUser;
   private String uriSender;
   private String uriInvited;
   private Timestamp beginDate;
   private Timestamp endDate;

    public HistoryConnection() {
    }

    public HistoryConnection(int idCon, int idUser, String uriSender, String uriInvited, Timestamp beginDate, Timestamp endDate) {
        this.idCon = idCon;
        this.idUser = idUser;
        this.uriSender = uriSender;
        this.uriInvited = uriInvited;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public int getIdCon() {
        return idCon;
    }

    public void setIdCon(int idCon) {
        this.idCon = idCon;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUriSender() {
        return uriSender;
    }

    public void setUriSender(String uriSender) {
        this.uriSender = uriSender;
    }

    public String getUriInvited() {
        return uriInvited;
    }

    public void setUriInvited(String uriInvited) {
        this.uriInvited = uriInvited;
    }

    public Timestamp getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Timestamp beginDate) {
        this.beginDate = beginDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "HistoryConnection{" +
                "idCon=" + idCon +
                ", idUser=" + idUser +
                ", uriSender='" + uriSender + '\'' +
                ", uriInvited='" + uriInvited + '\'' +
                ", beginDate=" + beginDate +
                ", endDate=" + endDate +
                '}';
    }
}
