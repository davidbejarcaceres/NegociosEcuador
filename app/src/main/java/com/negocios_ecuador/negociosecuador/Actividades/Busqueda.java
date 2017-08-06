package com.negocios_ecuador.negociosecuador.Actividades;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Busqueda extends AppCompatActivity {

    String query;
    private List<Negocios> items = new ArrayList<Negocios>();
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager linearLayoutManager;
    public ProgressDialog loading;
    String URL_FULL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);
        setTitle("Resultados de Busqueda");
        query=getIntent().getExtras().getString("termino");

        URL_FULL = getString(R.string.url_rest)+"full";
        Log.e("Llegada","cvbcvb"+URL_FULL);



        recycler = (RecyclerView) findViewById(R.id.reci);
        linearLayoutManager = new LinearLayoutManager(this);

        loading = ProgressDialog.show(this,"Cargando Datos", "Espere Por Favor...",false,false);




        Configuration config = getResources().getConfiguration();


        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(getContext(), "landscape", Toast.LENGTH_SHORT).show();
            recycler.setLayoutManager(new GridLayoutManager(this, 2));

        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT){
            //Toast.makeText(getContext(), "portrait", Toast.LENGTH_SHORT).show();
            recycler.setLayoutManager(new GridLayoutManager(this, 1));
        }


        getDATA();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top);
        }
        return super.onOptionsItemSelected(item);
    }

    private void getDATA(){



        JsonArrayRequest req = new JsonArrayRequest(
                URL_FULL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {



                ParseData(response);



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

                Toast.makeText(Busqueda.this,message, Toast.LENGTH_LONG).show();


            }
        });

        RequestQueue queue = Volley.newRequestQueue(Busqueda.this);
        int socketTimeout = 6000;//3000 --- 30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
        queue.add(req);
    }

    private void ParseData(JSONArray arreglo) {


        for (int i = 0; i < arreglo.length(); i++) {

            JSONArray arr = null;
            JSONObject json1;
            try {
                JSONObject json = (JSONObject) arreglo.get(i);

                //Obtener el ID
                arr = json.getJSONArray("nid");
                json1 = (JSONObject) arr.get(0);
                String ID = json1.getString("value");


                //Obtener el Nombre del Negocio
                arr = json.getJSONArray("title");
                json1 = (JSONObject) arr.get(0);
                String Nombre = json1.getString("value");

                //Obtener la Descripcion del Negocio
                arr = json.getJSONArray("field_descripcion");
                json1 = (JSONObject) arr.get(0);
                String Descripcion = json1.getString("value");

                //Obtener la Direccion del Negocio
                arr = json.getJSONArray("field_calles");
                json1 = (JSONObject) arr.get(0);
                String Direccion = json1.getString("value");

                //Obtener la Imagen del Negocio
                arr = json.getJSONArray("field_image");
                json1 = (JSONObject) arr.get(0);
                String Imagen = json1.getString("url");

                //Obtiene las coordenaas
                arr = json.getJSONArray("field_mapa");
                json1 = (JSONObject) arr.get(0);
                String latitud = json1.getString("lat");
                String longitud = json1.getString("lng");


                //Busqueda Negocio Nombre
                String Nombre1 = Nombre.toLowerCase();
                boolean condicion = Nombre1.contains(query.toLowerCase());

                //Busqueda Negocio Direccion
                String Direccion1 = Direccion.toLowerCase();
                boolean condicion1 = Direccion1.contains(query.toLowerCase());

                //Busqueda Negocio descripción
                String Descripcion1 = Descripcion.toLowerCase();
                boolean condicion2 = Descripcion1.contains(query.toLowerCase());


                if(condicion || condicion1 || condicion2){
                    //agregamos los items al Array
                    items.add(new Negocios(ID,Nombre,Descripcion,Direccion,Imagen, latitud, longitud));
                }






            } catch (JSONException e) {
                e.printStackTrace();
            }



        }

        //inflamos el Recycler con el Array
        adapter = new Adapter_listados(items, Busqueda.this);
        recycler.setAdapter(adapter);
        loading.dismiss();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            recycler.setLayoutManager(new GridLayoutManager(this, 2));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            recycler.setLayoutManager(new GridLayoutManager(this, 1));
            //Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

}


