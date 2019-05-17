package com.example.pai5inventory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**

 *
 * https://www.journaldev.com/741/java-socket-programming-server-client
 */
public class serverSide {

    //static ServerSocket variable
    private static ServerSocket server;
    //socket server port on which it will listen
    private static int port = 9876;

    public static void main(String[] args) throws IOException, ClassNotFoundException{
        //create the socket server object
        server = new ServerSocket(port);
        //keep listens indefinitely until receives 'exit' call or program terminates
        while(true){
            System.out.println("Waiting for the client request");
            //creating socket and waiting for client connection
            Socket socket = server.accept();
            //read from socket to ObjectInputStream object
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            //convert ObjectInputStream object to String
            String message = (String) ois.readObject();
            //create ObjectOutputStream object
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //Verificamos el número de cliente
            if(verifyClientNumber(message)){
                System.out.println("Message Received: " + message);
                oos.writeObject(""+ message);
                oos.flush();



                byte[] bytes = (byte[]) ois.readObject();
//                byte[] bytes = get_client_pubkey(message);
                System.out.println("Public Key received");
                oos.flush();

                PublicKey pubKey = null;
                try {
                    X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(bytes);
                    KeyFactory keyFactory = KeyFactory.getInstance("DSA");
                    pubKey = keyFactory.generatePublic(pubKeySpec);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }



                String data_to_sign = (String) ois.readObject();
                System.out.println("Data received" + data_to_sign);



                byte[] b = (byte[]) ois.readObject();
                System.out.println("bytes received");


                if(verificaFirmaDigital(pubKey,data_to_sign,b)){
                    oos.writeObject("ok");
                }else{
                    oos.writeObject("nook");
                    oos.flush();

                }

                ois.close();
                oos.close();
                socket.close();
                break;
            }else{
                System.out.println("You are not allowed to send data: " + message);
                message = "false";
                //write object to Socket
                oos.writeObject(""+ message);
                //close resources
                ois.close();
                oos.close();
                socket.close();
                break;

            }
            //terminate the server if client sends exit request
        }
        System.out.println("Shutting down Socket server!!");
        //close the ServerSocket object
        server.close();
    }


    private static Boolean verificaFirmaDigital(PublicKey publicKey, String cadenaCaracteres, byte[] b){

        Signature firma= null;
        try {
            firma = Signature.getInstance("SHA1withDSA");
            firma.initVerify(publicKey);
            firma.update(cadenaCaracteres.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        try {
            if(firma.verify(b)){
                    return true;
                }else {
                    return false;
                }
        } catch (SignatureException e) {
            e.printStackTrace();
            return false;
        }


    }


    private static Boolean verifyClientNumber(String clientNumber){
        Database e = new Database();
        return e.clientExists(clientNumber);


    }

    private static byte[] get_client_pubkey(String clientNumber){
        Database e = new Database();
        return e.client_public_key(clientNumber);


    }
}