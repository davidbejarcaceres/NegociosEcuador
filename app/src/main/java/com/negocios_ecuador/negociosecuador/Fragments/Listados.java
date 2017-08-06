package com.negocios_ecuador.negociosecuador.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.negocios_ecuador.negociosecuador.Adapters.Adapter_listados;
import com.negocios_ecuador.negociosecuador.Objects.Negocios;
import com.negocios_ecuador.negociosecuador.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class Listados extends Fragment {

    private List<Negocios> items = new ArrayList<Negocios>();
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager linearLayoutManager;
    public ProgressDialog loading;
    String URL_POPULARES;
    public Listados() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_listados,
                container, false);


        recycler = (RecyclerView) view.findViewById(R.id.reci);
        linearLayoutManager = new LinearLayoutManager(getContext());



        Configuration config = getResources().getConfiguration();


        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(getContext(), "landscape", Toast.LENGTH_SHORT).show();
            recycler.setLayoutManager(new GridLayoutManager(getContext(), 2));

        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT){
            //Toast.makeText(getContext(), "portrait", Toast.LENGTH_SHORT).show();
            recycler.setLayoutManager(new GridLayoutManager(getContext(), 1));
        }

        String regreso = this.getArguments().getString("id_categoria");
        if(regreso == "POPULARES"){
            URL_POPULARES = getString(R.string.url_rest) + "POPULARES";
        }else {
            URL_POPULARES = getString(R.string.url_rest) + "negocios/cat/" + this.getArguments().getString("id_categoria");
        }
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loading = ProgressDialog.show(getContext(),"Cargando Datos", "Espere Por Favor...",false,false);


        getDATA(getContext());



    }

    private void getDATA(final Context contexto){



        JsonArrayRequest req = new JsonArrayRequest(
                 URL_POPULARES, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {



                ParseData(response, contexto);
                //Log.e(":O",""+response);


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

                Toast.makeText(contexto,message, Toast.LENGTH_LONG).show();


            }
        });

        RequestQueue queue = Volley.newRequestQueue(contexto);
        int socketTimeout = 6000;//3000 --- 30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
        queue.add(req);
    }

    private void ParseData(JSONArray arreglo, Context contexto) {
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

                //agregamos los items al Array
                items.add(new Negocios(ID,Nombre,Descripcion,Direccion,Imagen, latitud, longitud));


            } catch (JSONException e) {
                e.printStackTrace();
            }



        }

        //inflamos el Recycler con el Array
        adapter = new Adapter_listados(items, contexto);
        recycler.setAdapter(adapter);
        loading.dismiss();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            recycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            recycler.setLayoutManager(new GridLayoutManager(getContext(), 1));
            //Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

}
