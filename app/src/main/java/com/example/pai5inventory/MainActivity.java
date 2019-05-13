package com.example.pai5inventory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


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

        // Comprobamos los datos introducidos en el formulario
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
                this.sendData(mesasNumberTexto, sillasNumberTexto, sofasNumberTexto, sillasNumberTexto, clientNumberTexto);
            }


        }


    }


    private void sendData(final String mesas_number, final String sillas_number, final String sillones_number, final String sofas_number, final String client_num) throws Resources.NotFoundException {

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

                                    // 2. Firmar los datos

                                    // 3. Enviar los datos

                                    Toast.makeText(MainActivity.this, "Petición enviada correctamente", Toast.LENGTH_SHORT).show();
                                }
                            }

                    )
                    .

                            setNegativeButton(android.R.string.no, null)

                    .

                            show();
    }
}
