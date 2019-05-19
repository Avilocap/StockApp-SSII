package com.example.pai5inventory;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import org.apache.derby.jdbc.EmbeddedDriver;

public class Database {



    public static void main(String[] args){

        Database e = new Database();
//        e.testDerby();
//        e.prepareDatabase();
//        e.createTestClient();
//        e.createTestOrderRegistry();
//        e.viewClients();
//        e.viewOrders();
//        e.clientExists();
//        Ejecutar este comando solo para borrar la tabla cliente
//        e.cleanDatabase();
//        e.simpleCommandInput("drop database pai5db");



    }

    /**
     * Open connection and keep it open
     * @return Connection to play with it
     */
    public Connection openConnectionGet(){
        Connection conn = null;
        try {
            Driver derbyEmbeddedDriver = new EmbeddedDriver();
            DriverManager.registerDriver(derbyEmbeddedDriver);
            conn = DriverManager.getConnection
                    ("jdbc:derby:pai5db;create=true");
            conn.setAutoCommit(false);
        } catch (SQLException ex) {
            System.out.println("in connection" + ex);
        }
        return conn;
    }

    /**
     * Open connection and keep it open
     */
    public void openConnection(){
        Connection conn = null;
        try {
            Driver derbyEmbeddedDriver = new EmbeddedDriver();
            DriverManager.registerDriver(derbyEmbeddedDriver);
            conn = DriverManager.getConnection
                    ("jdbc:derby:pai5db;create=true");
            conn.setAutoCommit(false);
        } catch (SQLException ex) {
            System.out.println("in connection" + ex);
        }
    }

    /**
     * Close active connection, if any.
     */
    public void closeConnection(){
        try {
            DriverManager.getConnection
                    ("jdbc:derby:;shutdown=true");
        } catch (SQLException ex) {
            if (((ex.getErrorCode() == 50000) &&
                    ("XJ015".equals(ex.getSQLState())))) {
                System.out.println("Derby shut down normally");
            } else {
                System.err.println("Derby did not shut down normally");
                System.err.println(ex.getMessage());
            }
        }
    }




    /**
     * Create table client & database if it is not created. This method open and close the connection itself.
     */
    public void prepareDatabase(){
        Connection conn = null;
        PreparedStatement pstmt;
        Statement stmt;
        Statement stm2;
        String createCLientSQL = "create table client ("
                + "id integer not null generated always as"
                + " identity (start with 1, increment by 1),   "
                + "clientNumber varchar(30) not null, publicKey blob(16M),"
                + "constraint primary_key primary key (id))";

        String createOrdersSQL = "create table orders ("
                + "id integer not null generated always as"
                + " identity (start with 1, increment by 1),   "
                + " date Date not null, accepted boolean,"
                + "constraint orders_primary_key primary key (id))";

        try {
            Driver derbyEmbeddedDriver = new EmbeddedDriver();
            DriverManager.registerDriver(derbyEmbeddedDriver);
            conn = DriverManager.getConnection
                    ("jdbc:derby:pai5db;create=true");
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            stmt.execute(createCLientSQL);
            System.out.println("Table client created");

            stm2 = conn.createStatement();
            stm2.execute(createOrdersSQL);
            System.out.println("Table orders created");

            conn.commit();
        } catch (SQLException e) {
            System.out.println("in connection" + e);
        }

        try {
            DriverManager.getConnection
                    ("jdbc:derby:;shutdown=true");
        } catch (SQLException ex) {
            if (((ex.getErrorCode() == 50000) &&
                    ("XJ015".equals(ex.getSQLState())))) {
                System.out.println("Derby shut down normally");
                System.out.println("Database prepared");
            } else {
                System.err.println("Derby did not shut down normally");
                System.err.println(ex.getMessage());
            }
        }

    }

    /**
     * Clean client table. This method open and close the connection itself.
     */
    public void cleanDatabase(){
        Connection conn = null;
        Statement stmt;
        Statement stmt2;
        String createSQL = "drop table client";
        String createSQLorders = "drop table orders";

        try {
            Driver derbyEmbeddedDriver = new EmbeddedDriver();
            DriverManager.registerDriver(derbyEmbeddedDriver);
            conn = DriverManager.getConnection
                    ("jdbc:derby:pai5db");
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            stmt.execute(createSQL);
            stmt2 = conn.createStatement();
            stmt2.execute(createSQLorders);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("in connection" + e);
        }

        try {
            DriverManager.getConnection
                    ("jdbc:derby:;shutdown=true");
        } catch (SQLException ex) {
            if (((ex.getErrorCode() == 50000) &&
                    ("XJ015".equals(ex.getSQLState())))) {
                System.out.println("Derby shut down normally");
                System.out.println("All tables deleted");
            } else {
                System.err.println("Derby did not shut down normally");
                System.err.println(ex.getMessage());
            }
        }

    }


    /**
     * Generate test client using the '123456' as client number and running machine generated keypair. This method open and close the connection itself.
     */
    public void createTestClient(){
        Connection conn = null;
        PreparedStatement pstmt;
        Statement stmt;
        String client_number = "123456";


        KeyPair par_de_claves = generateKeyPar();
        byte[] keybytes = par_de_claves.getPublic().getEncoded();


        try {
            Driver derbyEmbeddedDriver = new EmbeddedDriver();
            DriverManager.registerDriver(derbyEmbeddedDriver);
            conn = DriverManager.getConnection
                    ("jdbc:derby:pai5db");
            conn.setAutoCommit(false);


            pstmt = conn.prepareStatement("insert into client (clientNumber,publicKey) values(?,?)");
            pstmt.setString(1, client_number);
            pstmt.setBytes(2, keybytes);
            pstmt.executeUpdate();


            conn.commit();

        } catch (SQLException ex) {
            System.out.println("in connection" + ex);
        }


        try {
            DriverManager.getConnection
                    ("jdbc:derby:;shutdown=true");
        } catch (SQLException ex) {
            if (((ex.getErrorCode() == 50000) &&
                    ("XJ015".equals(ex.getSQLState())))) {
                System.out.println("Derby shut down normally");
                System.out.println("Test client imported sucessfully");
            } else {
                System.err.println("Derby did not shut down normally");
                System.err.println(ex.getMessage());
            }
        }


    }


    /**
     * Generate test order generating random order from the last 4 months. This method open and close the connection itself.
     */
    public void createTestOrderRegistry(){
        Connection conn = null;
        PreparedStatement pstmt;
        Random random = new Random();

        KeyPair par_de_claves = generateKeyPar();


        try {
            Driver derbyEmbeddedDriver = new EmbeddedDriver();
            DriverManager.registerDriver(derbyEmbeddedDriver);
            conn = DriverManager.getConnection
                    ("jdbc:derby:pai5db");
            conn.setAutoCommit(false);


            for(int i=0; i<=80;i++){
                pstmt = conn.prepareStatement("insert into orders (Date,Accepted) values(?,?)");
                pstmt.setDate(1, createRandomDate());
                pstmt.setBoolean(2, random.nextBoolean());
                pstmt.executeUpdate();
                System.out.println(createRandomDate().toString());
            }




            conn.commit();

        } catch (SQLException ex) {
            System.out.println("in connection" + ex);
        }


        try {
            DriverManager.getConnection
                    ("jdbc:derby:;shutdown=true");
        } catch (SQLException ex) {
            if (((ex.getErrorCode() == 50000) &&
                    ("XJ015".equals(ex.getSQLState())))) {
                System.out.println("Derby shut down normally");
                System.out.println("Test client imported sucessfully");
            } else {
                System.err.println("Derby did not shut down normally");
                System.err.println(ex.getMessage());
            }
        }

    }

    /**
     * Generate new order. This method open and close the connection itself.
     */
    public void createOrderRegistry(Date fecha, Boolean res){
        Connection conn = null;
        PreparedStatement pstmt;



        try {
            Driver derbyEmbeddedDriver = new EmbeddedDriver();
            DriverManager.registerDriver(derbyEmbeddedDriver);
            conn = DriverManager.getConnection
                    ("jdbc:derby:pai5db");
            conn.setAutoCommit(false);

            java.sql.Date sqldate = new java.sql.Date(fecha.getTime());
            pstmt = conn.prepareStatement("insert into orders (Date,Accepted) values(?,?)");
            pstmt.setDate(1, sqldate);
            pstmt.setBoolean(2, res);
            pstmt.executeUpdate();
            System.out.println(createRandomDate().toString());

            conn.commit();

        } catch (SQLException ex) {
            System.out.println("in connection" + ex);
        }


        try {
            DriverManager.getConnection
                    ("jdbc:derby:;shutdown=true");
        } catch (SQLException ex) {
            if (((ex.getErrorCode() == 50000) &&
                    ("XJ015".equals(ex.getSQLState())))) {
                System.out.println("Derby shut down normally");
                System.out.println("Test client imported sucessfully");
            } else {
                System.err.println("Derby did not shut down normally");
                System.err.println(ex.getMessage());
            }
        }

    }

    public static java.sql.Date createRandomDate() {
        int day = createRandomIntBetween(1, 28);
        int month = createRandomIntBetween(1, 4);
        int year = createRandomIntBetween(2019, 2019);
        LocalDate res = LocalDate.of(year, month, day);
        Date date = Date.from(res.atStartOfDay(ZoneId.systemDefault()).toInstant());
        java.sql.Date sqldate = new java.sql.Date(date.getTime());
        return sqldate;
    }

    public static int createRandomIntBetween(int start, int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }

    /**
     * Execute the input command in database. This method open and close the connection itself.
     * @param command command to execute.
     */
    public void simpleCommandInput(String command){
        Connection conn = null;
        Statement stmt;
        String createSQL = "drop table client";

        try {
            Driver derbyEmbeddedDriver = new EmbeddedDriver();
            DriverManager.registerDriver(derbyEmbeddedDriver);
            conn = DriverManager.getConnection
                    ("jdbc:derby:pai5db:create=true");
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            stmt.execute(createSQL);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("in connection" + e);
        }

        try {
            DriverManager.getConnection
                    ("jdbc:derby:;shutdown=true");
        } catch (SQLException ex) {
            if (((ex.getErrorCode() == 50000) &&
                    ("XJ015".equals(ex.getSQLState())))) {
                System.out.println("Derby shut down normally");
            } else {
                System.err.println("Derby did not shut down normally");
                System.err.println(ex.getMessage());
            }
        }

    }


    /**
     * List the client registered in database. This method open and close the connection itself.
     */
    public void viewClients(){
        Connection conn;
        Statement stmt;
        ResultSet rs;


        try {
            Driver derbyEmbeddedDriver = new EmbeddedDriver();
            DriverManager.registerDriver(derbyEmbeddedDriver);
            conn = DriverManager.getConnection
                    ("jdbc:derby:pai5db");
            conn.setAutoCommit(false);
            stmt = conn.createStatement();

            rs = stmt.executeQuery("select * from client");
            while (rs.next()) {
                System.out.printf("%d %s\n",
                        rs.getInt(1), rs.getString(2));
            }



        } catch (SQLException e) {
            System.out.println("in connection" + e);
        }

        try {
            DriverManager.getConnection
                    ("jdbc:derby:;shutdown=true");
        } catch (SQLException ex) {
            if (((ex.getErrorCode() == 50000) &&
                    ("XJ015".equals(ex.getSQLState())))) {
                System.out.println("Derby shut down normally");
            } else {
                System.err.println("Derby did not shut down normally");
                System.err.println(ex.getMessage());
            }
        }

    }

    /**
     * List the orders registered in database. This method open and close the connection itself.
     */
    public void viewOrders(){
        Connection conn;
        Statement stmt;
        ResultSet rs;


        try {
            Driver derbyEmbeddedDriver = new EmbeddedDriver();
            DriverManager.registerDriver(derbyEmbeddedDriver);
            conn = DriverManager.getConnection
                    ("jdbc:derby:pai5db");
            conn.setAutoCommit(false);
            stmt = conn.createStatement();

            rs = stmt.executeQuery("select * from orders");
            while (rs.next()) {
                System.out.print(rs.getDate(2).toString()+" "+rs.getBoolean(3)+"\n");
            }



        } catch (SQLException e) {
            System.out.println("in connection" + e);
        }

        try {
            DriverManager.getConnection
                    ("jdbc:derby:;shutdown=true");
        } catch (SQLException ex) {
            if (((ex.getErrorCode() == 50000) &&
                    ("XJ015".equals(ex.getSQLState())))) {
                System.out.println("Derby shut down normally");
            } else {
                System.err.println("Derby did not shut down normally");
                System.err.println(ex.getMessage());
            }
        }

    }

    /**
     * Get the orders registered in database. This method open and close the connection itself.
     */
    public ResultSet getAllOrders(){
        Connection conn;
        Statement stmt;
        ResultSet rs = null;

        try {
            Driver derbyEmbeddedDriver = new EmbeddedDriver();
            DriverManager.registerDriver(derbyEmbeddedDriver);
            conn = DriverManager.getConnection
                    ("jdbc:derby:pai5db");
            conn.setAutoCommit(false);
            stmt = conn.createStatement();

            rs = stmt.executeQuery("select * from orders");

        } catch (SQLException e) {
            System.out.println("in connection" + e);
        }

        try {
            DriverManager.getConnection
                    ("jdbc:derby:;shutdown=true");
        } catch (SQLException ex) {
            if (((ex.getErrorCode() == 50000) &&
                    ("XJ015".equals(ex.getSQLState())))) {
                System.out.println("Derby shut down normally");
            } else {
                System.err.println("Derby did not shut down normally");
                System.err.println(ex.getMessage());
            }
        }

        return rs;
    }

    public List<String> getAllOrdersInRange(Date startDate, Date endDate){
        Connection conn;
        PreparedStatement pstmt;
        ResultSet rs = null;
        List<String> resultado = new ArrayList<>();

        try {
            Driver derbyEmbeddedDriver = new EmbeddedDriver();
            DriverManager.registerDriver(derbyEmbeddedDriver);
            conn = DriverManager.getConnection
                    ("jdbc:derby:pai5db");
            conn.setAutoCommit(false);
//            stmt = conn.createStatement();

//            rs = stmt.executeQuery("select * from orders where date BETWEEN ? AND ? ");
            String sqlquery = "select * from orders where date BETWEEN ? AND ?";
            pstmt = conn.prepareStatement(sqlquery);
            pstmt.setDate(1, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(2, new java.sql.Date(endDate.getTime()));

            rs = pstmt.executeQuery();

            while (rs.next()) {
//                System.out.print(rs.getDate(2).toString()+" "+rs.getBoolean(3)+"\n");
                resultado.add(""+rs.getBoolean(3));
            }


        } catch (SQLException e) {
            System.out.println("in connection" + e);
        }

        try {
            DriverManager.getConnection
                    ("jdbc:derby:;shutdown=true");
        } catch (SQLException ex) {
            if (((ex.getErrorCode() == 50000) &&
                    ("XJ015".equals(ex.getSQLState())))) {
                System.out.println("Derby shut down normally");
            } else {
                System.err.println("Derby did not shut down normally");
                System.err.println(ex.getMessage());
            }
        }

        return resultado;
    }


    /**
     * This method check if the client exists in the server database. This method open and close the connection itself.
     * @param client_number Client number to check.
     * @return if client exists or not.
     */
    public Boolean clientExists(String client_number){

        if(client_number.isEmpty()){
            client_number="0";
        }

        Boolean res = false;
        Connection conn;
        Statement stmt;
        ResultSet rs;


        try {
            Driver derbyEmbeddedDriver = new EmbeddedDriver();
            DriverManager.registerDriver(derbyEmbeddedDriver);
            conn = DriverManager.getConnection
                    ("jdbc:derby:pai5db");
            conn.setAutoCommit(false);
            stmt = conn.createStatement();

            rs = stmt.executeQuery("select * from client where clientNumber = '"+client_number+"'");

            if(rs.next() != false){
                res = true;
            }



        } catch (SQLException e) {
            System.out.println("in connection" + e);
        }

        try {
            DriverManager.getConnection
                    ("jdbc:derby:;shutdown=true");
        } catch (SQLException ex) {
            if (((ex.getErrorCode() == 50000) &&
                    ("XJ015".equals(ex.getSQLState())))) {
                System.out.println("Derby shut down normally");
            } else {
                System.err.println("Derby did not shut down normally");
                System.err.println(ex.getMessage());
            }
        }

        return res;
    }

    /**
     * Return associated public key of client
     * @param client_number client number
     * @return bytes of client public key
     */
    public byte[] client_public_key(String client_number){

        if(client_number.isEmpty()){
            client_number="0";
        }

        byte[] res = null;
        Connection conn;
        Statement stmt;
        ResultSet rs;


        try {
            Driver derbyEmbeddedDriver = new EmbeddedDriver();
            DriverManager.registerDriver(derbyEmbeddedDriver);
            conn = DriverManager.getConnection
                    ("jdbc:derby:pai5db");
            conn.setAutoCommit(false);
            stmt = conn.createStatement();

            rs = stmt.executeQuery("select publicKey from client where clientNumber = '"+client_number+"'");

            while (rs.next()) {
                res = rs.getBytes(1);
            }




        } catch (SQLException e) {
            System.out.println("in connection" + e);
        }

        try {
            DriverManager.getConnection
                    ("jdbc:derby:;shutdown=true");
        } catch (SQLException ex) {
            if (((ex.getErrorCode() == 50000) &&
                    ("XJ015".equals(ex.getSQLState())))) {
                System.out.println("Derby shut down normally");
            } else {
                System.err.println("Derby did not shut down normally");
                System.err.println(ex.getMessage());
            }
        }

        return res;
    }


    private KeyPair generateKeyPar() {

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            return keyPair;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

    }
}
