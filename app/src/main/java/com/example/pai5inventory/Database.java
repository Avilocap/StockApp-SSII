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

import org.apache.derby.jdbc.EmbeddedDriver;

public class Database {



    public static void main(String[] args){

        Database e = new Database();
//        e.testDerby();
//        e.prepareDatabase();
//        e.createTestClient();
//        e.viewClients();
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
        String createSQL = "create table client ("
                + "id integer not null generated always as"
                + " identity (start with 1, increment by 1),   "
                + "clientNumber varchar(30) not null, publicKey blob(16M),"
                + "constraint primary_key primary key (id))";

        try {
            Driver derbyEmbeddedDriver = new EmbeddedDriver();
            DriverManager.registerDriver(derbyEmbeddedDriver);
            conn = DriverManager.getConnection
                    ("jdbc:derby:pai5db;create=true");
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
        String createSQL = "drop table client";

        try {
            Driver derbyEmbeddedDriver = new EmbeddedDriver();
            DriverManager.registerDriver(derbyEmbeddedDriver);
            conn = DriverManager.getConnection
                    ("jdbc:derby:pai5db");
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
                System.out.println("Table Client deleted");
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
     * List the client registed in database. This method open and close the connection itself.
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
