package com.example.pai5inventory;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
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


    public void testDerby() {
        Connection conn = null;
        PreparedStatement pstmt;
        Statement stmt;
        ResultSet rs = null;
        String createSQL = "create table person ("
                + "id integer not null generated always as"
                + " identity (start with 1, increment by 1),   "
                + "name varchar(30) not null, email varchar(30), phone varchar(10),"
                + "constraint primary_key primary key (id))";

        try {
            Driver derbyEmbeddedDriver = new EmbeddedDriver();
            DriverManager.registerDriver(derbyEmbeddedDriver);
            conn = DriverManager.getConnection
                    ("jdbc:derby:pai5db;create=true");
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            stmt.execute(createSQL);

            pstmt = conn.prepareStatement("insert into person (name,email,phone) values(?,?,?)");
            pstmt.setString(1, "Hagar the Horrible");
            pstmt.setString(2, "hagar@somewhere.com");
            pstmt.setString(3, "1234567890");
            pstmt.executeUpdate();

            rs = stmt.executeQuery("select * from person");
            while (rs.next()) {
                System.out.printf("%d %s %s %s\n",
                        rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getString(4));
            }

            stmt.execute("drop table person");

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
            } else {
                System.err.println("Derby did not shut down normally");
                        System.err.println(ex.getMessage());
            }
        }
    }


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


    private KeyPair generateKeyPar() {

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            return keyPair;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

    }
}
