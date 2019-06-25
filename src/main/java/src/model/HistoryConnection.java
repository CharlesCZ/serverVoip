package src.model;

import java.sql.Date;

public class HistoryConnection {
   private int idCon;
   private int idUser;
   private String uriSender;
   private String uriInvited;
   private Date beginDate;
   private Date endDate;


    public HistoryConnection(int idCon, int idUser, String uriSender, String uriInvited, Date beginDate, Date endDate) {
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

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
