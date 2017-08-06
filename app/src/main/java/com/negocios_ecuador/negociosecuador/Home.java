package com.negocios_ecuador.negociosecuador;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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
import com.negocios_ecuador.negociosecuador.Actividades.Busqueda;
import com.negocios_ecuador.negociosecuador.Actividades.Negocio_A;
import com.negocios_ecuador.negociosecuador.Fragments.Listados;
import com.negocios_ecuador.negociosecuador.Objects.Negocios;
import com.negocios_ecuador.negociosecuador.Objects.tipo_categoria;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String URL_CATEGORIAS;
    JSONArray array = new JSONArray();

    private List<tipo_categoria> items = new ArrayList<tipo_categoria>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //inflamos el menu
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //aqui llamamos a las categorias
        Menu m = navigationView.getMenu();
        final SubMenu topChannelMenu = m.addSubMenu("Categorias");

        navigationView.setNavigationItemSelectedListener(this);


        Bundle bundle = new Bundle();
        bundle.putString("id_categoria", "POPULARES" );

        //Llamada a la libreria
        android.support.v4.app.FragmentManager fragmentManager;
        android.support.v4.app.FragmentTransaction fragmentTransaction;

        //ABRIR EL FRAGMENT
        //android.support.v4.app.FragmentManager fragmentManager;
        //android.support.v4.app.FragmentTransaction fragmentTransaction;

        //ABRIR EL FRAGMENT
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        Listados inboxFragment = new Listados();
        //inboxFragment.newInstance(variable);
        inboxFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.list, inboxFragment);
        fragmentTransaction.commit();


        //url de las categorias
        URL_CATEGORIAS = getString(R.string.url_rest)+"tipo_negocio";




        //carga el menu hamburguesa
        JsonArrayRequest req = new JsonArrayRequest(
                URL_CATEGORIAS, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {


                for (int i = 0; i < response.length(); i++) {

                    JSONArray arr = null;
                    JSONObject json1;
                    try {
                        JSONObject json = (JSONObject) response.get(i);

                        //Nombre Categoria
                        arr = json.getJSONArray("name");
                        json1 = (JSONObject) arr.get(0);
                        String Categoria = json1.getString("value");

                        //ID Categoria
                        arr = json.getJSONArray("tid");
                        json1 = (JSONObject) arr.get(0);
                        String IDCategoria = json1.getString("value");


                        //Creo Objeto Json para llenar el Array que
                        // definira abajo que url llamar en el fragment

                        JSONObject items_ = new JSONObject();
                        items_.put("id", IDCategoria);
                        items_.put("categoria", Categoria);
                        array.put(items_);

                        //agrego los tipos de categorias al Drawer
                        topChannelMenu.add(Categoria);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }



            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {


                //Aqui he colocado los mesajes de Error
                //la libreria Volley
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

                //Toast.makeText(contexto,message, Toast.LENGTH_LONG).show();


            }
        });

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        int socketTimeout = 6000;//3000 --- 30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
        queue.add(req);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //permite modificar el hint que el EditText muestra por defecto
        searchView.setQueryHint(getText(R.string.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(Home.this, query, Toast.LENGTH_SHORT).show();
                //se oculta el EditText
                searchView.setQuery("", false);
                searchView.setIconified(true);

                Intent Nego = new Intent(Home.this, Busqueda.class);
                Nego.putExtra("termino", query);

                startActivity(Nego);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //textView.setText(newText);
                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //Log.e("ITEM",""+item.getItemId());
        int id = item.getItemId();


            // aqui el menu de POPULARES
        if (id == R.id.populares) {

            Bundle bundle = new Bundle();
            bundle.putString("id_categoria", "POPULARES" );

            //Llamada a la libreria
            android.support.v4.app.FragmentManager fragmentManager;
            android.support.v4.app.FragmentTransaction fragmentTransaction;

            //ABRIR EL FRAGMENT
            //android.support.v4.app.FragmentManager fragmentManager;
            //android.support.v4.app.FragmentTransaction fragmentTransaction;

            //ABRIR EL FRAGMENT
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            Listados inboxFragment = new Listados();
            //inboxFragment.newInstance(variable);
            inboxFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.list, inboxFragment);
            fragmentTransaction.commit();


        } else{

            //Log.e("hola","");
            //Aqui van todos las Categorias
            ///////////////////////////////////////////////
            //en este for comparo lo que el usuario clickea
            // con el array para enviarlo al menu correcto
            for (int i = 0; i < array.length(); i++) {
                try {


                    JSONObject ob = (JSONObject) array.get(i);
                    String cat = item.toString();
                    if(ob.getString("categoria") == cat){
                       //Cambio de titulo al de La categoria
                        setTitle(cat);

                        //Preparar DATOS PARA el ENVIO
                        //al fragment
                        Bundle bundle = new Bundle();
                        bundle.putString("id_categoria", ob.getString("id") );

                        //Llamada a la libreria
                        android.support.v4.app.FragmentManager fragmentManager;
                        android.support.v4.app.FragmentTransaction fragmentTransaction;

                        //ABRIR EL FRAGMENT
                        //android.support.v4.app.FragmentManager fragmentManager;
                        //android.support.v4.app.FragmentTransaction fragmentTransaction;

                        //ABRIR EL FRAGMENT
                        fragmentManager = getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        Listados inboxFragment = new Listados();
                        //inboxFragment.newInstance(variable);
                        inboxFragment.setArguments(bundle);
                        fragmentTransaction.replace(R.id.list, inboxFragment);
                        fragmentTransaction.commit();


                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
