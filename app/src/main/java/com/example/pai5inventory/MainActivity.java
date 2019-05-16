package com.example.pai5inventory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Capturamos el boton de Enviar
        View button = findViewById(R.id.sendButton);

        // Llama al listener del boton Enviar
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkData();

            }
        });

    }

    private void checkData() {

        //Se transforman los datos del formulario
        EditText mesasData = findViewById(R.id.mesasInput);
        EditText sillasData = findViewById(R.id.sillasInput);
        EditText sofasData = findViewById(R.id.sofasInput);
        EditText sillonesData = findViewById(R.id.sillonesInput);
        EditText numero_cliente_Data = findViewById(R.id.clientNumberInput);

        String mesasNumberTexto = mesasData.getText().toString().trim();
        String sillasNumberTexto = sillasData.getText().toString().trim();
        String sofasNumberTexto = sofasData.getText().toString().trim();
        String sillonesNumberTexto = sillonesData.getText().toString().trim();
        String clientNumberTexto = numero_cliente_Data.getText().toString().trim();

        // Comprobamos los datos introducidos en el formulario
        if (mesasNumberTexto.isEmpty() || sillasNumberTexto.isEmpty() || sofasNumberTexto.isEmpty() || sillonesNumberTexto.isEmpty() || clientNumberTexto.isEmpty()){
            Toast.makeText(MainActivity.this, "Por favor, introduce 0 si no quieres ninguno", Toast.LENGTH_SHORT).show();

            mesasData.setText("");
            sillasData.setText("");
            sofasData.setText("");
            sillonesData.setText("");

        }else{

            Integer mesasNumberInt = Integer.valueOf(mesasNumberTexto);
            Integer sillasNumberInt = Integer.valueOf(sillasNumberTexto);
            Integer sofasNumberInt = Integer.valueOf(sofasNumberTexto);
            Integer sillonesNumberInt =Integer.valueOf(sillonesNumberTexto);

            if(mesasNumberInt<0 || mesasNumberInt > 300 || sillasNumberInt<0 || sillasNumberInt > 300 || sofasNumberInt<0 || sofasNumberInt > 300 || sillonesNumberInt<0 || sillonesNumberInt > 300){
                Toast.makeText(MainActivity.this, "Solo puedes pedir de 0 a 300 elementos de cada tipo", Toast.LENGTH_SHORT).show();
            }else {
                //Si está correcto, se procede a enviar el mensaje
                this.sendData(mesasNumberTexto, sillasNumberTexto, sofasNumberTexto, sillasNumberTexto, clientNumberTexto);
            }


        }


    }


    private void sendData(final String mesas_number, final String sillas_number, final String sillones_number, final String sofas_number, final String client_num) throws Resources.NotFoundException {

        //Hay que poner esta instrucción para permitir a la aplicación ciertas funciones "prohibidas en producción"
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

            new AlertDialog.Builder(this)
                    .setTitle("Enviar")
                    .setMessage("Se va a proceder al envio")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                // Catch ok button and send information
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    // 1. Extraer los datos de la vista y comprobar su validez

                                    String numero_mesas = mesas_number;
                                    String numero_sillas = sillas_number;
                                    String numero_sillones = sillones_number;
                                    String numero_sofas = sofas_number;
                                    String numero_cliente = client_num;

                                    String dataToSign = numero_mesas+";"+numero_sillas+";"+numero_sillones+";"+numero_sofas;
                                    // 2. Firmar los datos
                                    byte[] signb = new byte[12];

                                    //Generamos el par de claves y la firma
                                    KeyPair par_de_claves = generateKeyPar();
                                    byte[] keybytes = par_de_claves.getPublic().getEncoded();
                                    Signature firma = generateSignature();

                                    //Inicializamos la firma
                                    SecureRandom secureRandom = new SecureRandom();
                                    try {
                                        firma.initSign(par_de_claves.getPrivate(), secureRandom);
                                    } catch (InvalidKeyException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        byte[] datass = dataToSign.getBytes("UTF-8");
                                        firma.update(datass);
                                        signb = firma.sign();

                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    } catch (SignatureException e) {
                                        e.printStackTrace();
                                    }



                                    // 3. Enviamos el número de cliente para que el servidor verifique

                                    try {

                                        Socket socket = null;
                                        ObjectOutputStream oos = null;
                                        ObjectInputStream ois = null;
                                            //Hay que utilizar la dirección ip 10.0.2.2 para hacer referencia al localhot de nuestra máquina. No se puede usar 127.0.0.1 porque la utiliza android para el emulador
                                        socket = new Socket("10.0.2.2", 9876);
                                        oos = new ObjectOutputStream(socket.getOutputStream());
                                        System.out.println("Sending request to Socket Server");
                                            //Enviamos los parámetros
                                        oos.writeObject(""+numero_cliente);
                                        ois = new ObjectInputStream(socket.getInputStream());
                                        String message = (String) ois.readObject();
                                        System.out.println("Parameter recived: " + message);
                                        //Si el número de cliente está verificado, se envian los datos, si no, se informa
                                        if(!message.equals("false")){

                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                            oos.flush();
                                            oos.writeObject(dataToSign);

                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                            oos.flush();
                                            oos.writeObject(signb);



                                            String receptionOk = (String) ois.readObject();
                                            if(receptionOk.equals("ok")){
                                                Toast.makeText(MainActivity.this, "Datos enviados correctamente", Toast.LENGTH_SHORT).show();
                                                ois.close();
                                                oos.close();
                                                socket.close();
                                            }else {
                                                Toast.makeText(MainActivity.this, "Ha habido un error al enviar los datos", Toast.LENGTH_SHORT).show();
                                                ois.close();
                                                oos.close();
                                                socket.close();
                                            }
                                        }else {
                                            Toast.makeText(MainActivity.this, "La autenticación de cliente ha fallado", Toast.LENGTH_SHORT).show();
                                            ois.close();
                                            oos.close();
                                            socket.close();


                                        }








                                    } catch (UnknownHostException e) {
                                        e.printStackTrace();
                                    } catch (IOException e){
                                        e.printStackTrace();
                                    } catch (ClassNotFoundException e){
                                        e.printStackTrace();
                                    }







//                                    Toast.makeText(MainActivity.this, "Petición enviada correctamente", Toast.LENGTH_SHORT).show();
                                }
                            }

                    )
                    .

                            setNegativeButton(android.R.string.no, null)

                    .

                            show();
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

    private Signature generateSignature(){

        try {
            Signature signature = Signature.getInstance("SHA1WithDSA");
            return signature;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
