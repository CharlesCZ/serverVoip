package src.model;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DatabaseVoip {

    public static final String DRIVER = "org.sqlite.JDBC";
    public static final String DB_URL = "jdbc:sqlite:databaseVoip.db";

    private Connection conn;
    private Statement stat;

    public DatabaseVoip() {
        try {
            Class.forName(DatabaseVoip.DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Brak sterownika JDBC");
            e.printStackTrace();
        }

        try {
            conn = DriverManager.getConnection(DB_URL);
            stat = conn.createStatement();
        } catch (SQLException e) {
            System.err.println("Problem z otwarciem polaczenia");
            e.printStackTrace();
        }


        createTables();

    }

    private boolean createTables() {

        String createUsers = "CREATE TABLE IF NOT EXISTS users (id_user INTEGER PRIMARY KEY AUTOINCREMENT, login varchar(255), password varchar(255))";
        String createHistoryConnections = "CREATE TABLE IF NOT EXISTS history_connections (id_con INTEGER PRIMARY KEY AUTOINCREMENT, id_user int, uri_sender varchar(255), uri_invited varchar(255), begin_date date, end_date date )";

        try {
            stat.execute(createUsers);
            stat.execute(createHistoryConnections);

        } catch (SQLException e) {
            System.err.println("Blad przy tworzeniu tabeli");
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public boolean insertUser(String login, String password) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement("insert into users values(NULL,?,?)");
            prepStmt.setString(1, login);
            prepStmt.setString(2, password);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Blad przy wstawianiu uzytkownika");
            e.printStackTrace();
            return false;
        }finally {
            closeConnection();
        }
        return true;
    }


    public boolean insertHistoryConnection(HistoryConnection historyConnection) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement("insert into history_connections values (NULL,?,?,?,?,?);");
            prepStmt.setInt(1, historyConnection.getIdUser());
            prepStmt.setString(2, historyConnection.getUriSender());
            prepStmt.setString(3, historyConnection.getUriInvited());
            prepStmt.setTimestamp(4, historyConnection.getBeginDate());
            prepStmt.setTimestamp(5, historyConnection.getEndDate());
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Blad przy dodawaniu do historii");
            return false;
        }finally {
            closeConnection();
        }
        return true;
    }


    public List<User> selectUsers() {
        List<User> usersList = new LinkedList<>();
        try {
            ResultSet resultSet = stat.executeQuery("SELECT * FROM users");
            int id;
            String login, password;
            while (resultSet.next()) {
                id = resultSet.getInt("id_user");
                login = resultSet.getString("login");
                password = resultSet.getString("password");
                usersList.add(new User(id, login, password));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }finally {
            closeConnection();
        }
        return usersList;
    }


    public User selectUser(String login, String password){
       User user=new User();

        try {
            String query="SELECT * FROM users WHERE login=? AND password=?";
            PreparedStatement prepStmt=conn.prepareStatement(query);
            prepStmt.setString(1,login);
            prepStmt.setString(2,password);
            ResultSet resultSet =  prepStmt.executeQuery();
            if (resultSet.next()) {
                user.setId( resultSet.getInt("id_user"));
              user.setLogin( resultSet.getString("login"));
              user.setPassword(resultSet.getString("password"));
                return  user;
            }


        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }finally {
            closeConnection();
        }
        return null;
    }


    public List<HistoryConnection> selectHistoryConnectionByUserId(int userId) {

        List<HistoryConnection> historyConnections = new LinkedList<>();
        try {
            String query="SELECT * FROM history_connections WHERE id_user=?";
            PreparedStatement prepStmt=conn.prepareStatement(query);
            prepStmt.setInt(1,userId);
            ResultSet resultSet = prepStmt.executeQuery();
            int idCon;
            int idUser;
            String uriSender;
            String uriInvited;
            Timestamp beginDate;
            Timestamp endDate;
            while (resultSet.next()) {
                idCon = resultSet.getInt("id_con");
                idUser = resultSet.getInt("id_user");
                uriSender = resultSet.getString("uri_sender");
                uriInvited = resultSet.getString("uri_invited");
                beginDate = resultSet.getTimestamp("begin_date");
                endDate = resultSet.getTimestamp("end_date");
                historyConnections.add(new HistoryConnection(idCon,idUser,uriSender,uriInvited,beginDate,endDate));

            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }finally {
            closeConnection();
        }

        return historyConnections;

    }

    public List<HistoryConnection> selectHistoryConnection() {
        List<HistoryConnection> historyConnections = new LinkedList<>();
        try {
            ResultSet resultSet = stat.executeQuery("SELECT * FROM history_connections");
            int idCon;
            int idUser;
            String uriSender;
            String uriInvited;
            Timestamp beginDate;
            Timestamp endDate;
            while (resultSet.next()) {
                idCon = resultSet.getInt("id_con");
                idUser = resultSet.getInt("id_user");
                uriSender = resultSet.getString("uri_sender");
                uriInvited = resultSet.getString("uri_invited");
                beginDate = resultSet.getTimestamp("begin_date");
                endDate = resultSet.getTimestamp("end_date");
                historyConnections.add(new HistoryConnection(idCon,idUser,uriSender,uriInvited,beginDate,endDate));

            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }finally {
            closeConnection();
        }

        return historyConnections;

    }
    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            System.err.println("Problem z zamknieciem polaczenia");
            e.printStackTrace();
        }
    }

}



