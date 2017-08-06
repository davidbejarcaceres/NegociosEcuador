package com.negocios_ecuador.negociosecuador.Actividades;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.negocios_ecuador.negociosecuador.Adapters.Adapter_listados;
import com.negocios_ecuador.negociosecuador.Objects.Negocios;
import com.negocios_ecuador.negociosecuador.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.negocios_ecuador.negociosecuador.R.id.nombre;

public class Negocio_A extends AppCompatActivity {
    String ID, NOMBRE, DESCRIPCION, IMAGEN, DIRECCION, EMAIL_,telefono_1,telefono_2,share,latitud,longitud;
    TextView descripccion, negocio, direccion, email, telefono1, telefono2,textviewshare;
    ImageView imagen_v;
    String URL;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negocio_);

        //VARIABLES RECIBIDAS DEL FRAGMENT
        ID = getIntent().getExtras().getString("ID");
        NOMBRE = getIntent().getExtras().getString("NOMBRE");
        DESCRIPCION = getIntent().getExtras().getString("DESCRIPCION");
        DIRECCION = getIntent().getExtras().getString("DIRECCION");
        IMAGEN = getIntent().getExtras().getString("IMAGEN");
        DIRECCION = getIntent().getExtras().getString("DIRECCION");
        latitud = getIntent().getExtras().getString("LATITUD");
        longitud = getIntent().getExtras().getString("LONGITUD");


        //URL PARA LA PETICION
        URL = getString(R.string.url_rest) + "negocios/" + ID;

        // DECLARAMOS LOS ELEMENTOS

        descripccion = (TextView) findViewById(R.id.descripcion);
        negocio = (TextView) findViewById(R.id.nombre);
        direccion = (TextView) findViewById(R.id.direccion);
        email = (TextView) findViewById(R.id.email);
        telefono1 = (TextView) findViewById(R.id.telefono1);
        telefono2 = (TextView) findViewById(R.id.telefono2);
        textviewshare = (TextView) findViewById(R.id.share);
        imagen_v = (ImageView) findViewById(R.id.imagen);
        Button btn_navegar = (Button) findViewById(R.id.btn_Navegar);


        btn_navegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Codigo para navegar a la actividad del mapa
                iniciarMapa();
            }
        });

        //COLOCAMOS VALORES RECIBIDOS
        descripccion.setText(DESCRIPCION);
        direccion.setText(DIRECCION);
        negocio.setText(NOMBRE);

        setTitle(NOMBRE);
        Picasso.with(this)
                .load(IMAGEN)
                //.load(url)
                //.resize(50, 50)
                //.centerCrop()
                .into(imagen_v);

        getDATA(this);

    }

    private void iniciarMapa() {



        //Inicia la siguiente actividad por medio de un intent
        Intent myIntent = new Intent(this, MapsActivity.class);
        myIntent.putExtra("LATITUD", latitud);
        myIntent.putExtra("LONGITUD", longitud);
        myIntent.putExtra("NOMBRE", NOMBRE);
        startActivity(myIntent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

    }

    private void getDATA(final Context contexto) {


        JsonArrayRequest req = new JsonArrayRequest(
                URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {


                ParseData(response, contexto);


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {

                //Log.e("error 2",error.toString());
                //getDATA(contexto);
                String message = null;
                if (volleyError instanceof NetworkError) {
                    message = "No se pudo conectar, revise su conexión a internet";
                } else if (volleyError instanceof ServerError) {
                    message = "El Servidor no responde, Intente mas tarde";
                } else if (volleyError instanceof AuthFailureError) {
                    message = "No se pudo conectar, revise su conexión a internet";
                } else if (volleyError instanceof ParseError) {
                    message = "No se pudo conectar, intente mas tarde";
                } else if (volleyError instanceof NoConnectionError) {
                    message = "No se pudo conectar, revise su conexión a internet";
                } else if (volleyError instanceof TimeoutError) {
                    message = "No se pudo conectar, revise su conexión a internet";
                }

                Toast.makeText(contexto, message, Toast.LENGTH_LONG).show();


            }
        });

        RequestQueue queue = Volley.newRequestQueue(contexto);
        int socketTimeout = 6000;//3000 --- 30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
        queue.add(req);
    }

    private void ParseData(JSONArray arreglo, Context contexto) {


        JSONObject json1 = null;
        JSONObject json2 = null;
        JSONArray arr = null;
        try {
            json1 = (JSONObject) arreglo.get(0);

            //Obtener el EMAIL
            arr = json1.getJSONArray("field_email");
            json2 = (JSONObject) arr.get(0);
            String email_string = json2.getString("value");

            email.setText(email_string);
            email.setText(email_string);

            //Obtener el Telefono
            arr = json1.getJSONArray("field_telefono");
            json2 = (JSONObject) arr.get(0);
            telefono_1 = json2.getString("value");
            telefono1.setText(telefono_1);


            json2 = (JSONObject) arr.get(1);
            telefono_2 = json2.getString("value");
            telefono2.setText(telefono_2);

            //Obtener el link
            arr = json1.getJSONArray("field_enlace_compartir");
            json2 = (JSONObject) arr.get(0);
            share = json2.getString("uri");


            //Obtener Coordenadas GPS
            arr = json1.getJSONArray("field_mapa");
            json2 = (JSONObject) arr.get(0);
            ///estas variables las puedes usar entodo este archivo
            latitud = json2.getString("lat");
            longitud = json2.getString("lng");



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void Telefono1(View v) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", telefono_1, null)));
        Log.e("si vale ",":O");

    }

    public void Telefono2(View v) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", telefono_2, null)));
        Log.e("si vale ",":O");

    }
    public void Share(View v) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, share);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }


}
