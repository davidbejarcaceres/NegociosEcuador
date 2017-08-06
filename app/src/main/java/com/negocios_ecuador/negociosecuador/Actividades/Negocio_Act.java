package com.negocios_ecuador.negociosecuador.Actividades;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.negocios_ecuador.negociosecuador.R;
import com.squareup.picasso.Picasso;

public class Negocio_Act extends AppCompatActivity {
    String ID,NOMBRE,DESCRIPCION,IMAGEN,DIRECCION;
    TextView descripccion;
    ImageView imagen_v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negocio_2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //VARIABLES RECIBIDAS DEL FRAGMENT
        ID=getIntent().getExtras().getString("ID");
        NOMBRE=getIntent().getExtras().getString("NOMBRE");
        DESCRIPCION=getIntent().getExtras().getString("DESCRIPCION");
        DIRECCION=getIntent().getExtras().getString("DIRECCION");
        IMAGEN=getIntent().getExtras().getString("IMAGEN");

        // DECLARAMOS LOS ELEMENTOS

        descripccion = (TextView) findViewById(R.id.descripcion);
        imagen_v = (ImageView) findViewById(R.id.imagen);


        //COLOCAMOS VALORES RECIBIDOS
        descripccion.setText(DESCRIPCION);
        setTitle(NOMBRE);
        Picasso.with(this)
                .load(IMAGEN)
                //.load(url)
                //.resize(50, 50)
                //.centerCrop()
                .into(imagen_v);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
