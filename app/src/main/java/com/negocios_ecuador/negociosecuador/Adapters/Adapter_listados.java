package com.negocios_ecuador.negociosecuador.Adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.negocios_ecuador.negociosecuador.Actividades.Negocio_A;
import com.negocios_ecuador.negociosecuador.Actividades.Negocio_Act;
import com.negocios_ecuador.negociosecuador.Objects.Negocios;
import com.negocios_ecuador.negociosecuador.Otros.ItemClickListener;
import com.negocios_ecuador.negociosecuador.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.negocios_ecuador.negociosecuador.R.id.imagen;
import static com.negocios_ecuador.negociosecuador.R.id.item_touch_helper_previous_elevation;

public class Adapter_listados extends RecyclerView.Adapter<Adapter_listados.MovieViewHolder> {
    private List<Negocios> items;
    private Context context;
    public String URL,USUARIO,CLAVE;


    public Adapter_listados(List<Negocios> item, Context cont) {

        context = cont;
        items = item;
        URL = URL;
        USUARIO=USUARIO;
        CLAVE=CLAVE;

    }



    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card, viewGroup, false);


        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder viewHolder, final int i) {




        viewHolder.nombre.setText(items.get(i).getNOMBRE());
        viewHolder.descripcion.setText(items.get(i).getDESCRIPCION());
        viewHolder.direccion.setText(items.get(i).getDIRECION());





        Picasso.with(context)
                .load(items.get(i).getIMAGEN())
                //.load(url)
                //.resize(50, 50)
                //.centerCrop()
                .into(viewHolder.imagen_);



        //onclick
        viewHolder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
                    //click Largo


                    //ENVIO URL A LA ACTIVIDAD PARA CADA MEGOVIO
                    Intent Nego = new Intent(context, Negocio_A.class);
                    Nego.putExtra("ID", items.get(i).getID());
                    Nego.putExtra("NOMBRE", items.get(i).getNOMBRE());
                    Nego.putExtra("DESCRIPCION", items.get(i).getDESCRIPCION());
                    Nego.putExtra("DIRECCION", items.get(i).getDIRECION());
                    Nego.putExtra("IMAGEN", items.get(i).getIMAGEN());
                    Nego.putExtra("LATITUD", items.get(i).getLATITUD());
                    Nego.putExtra("LONGITUD", items.get(i).getLONGITUD());

                    Activity activity = (Activity) context ;
                    activity.startActivity(Nego);
                    //Añade la animacion de entrada y la de salida

                    activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                    Log.e(":O",""+items.get(i).getID());

                    //context.startActivity(Nego);


                } else {
                    //click Corto

                    //ENVIO URL A LA ACTIVIDAD PARA CADA MEGOVIO
                    Intent Nego = new Intent(context, Negocio_A.class);
                    Nego.putExtra("ID", items.get(i).getID());
                    Nego.putExtra("NOMBRE", items.get(i).getNOMBRE());
                    Nego.putExtra("DESCRIPCION", items.get(i).getDESCRIPCION());
                    Nego.putExtra("DIRECCION", items.get(i).getDIRECION());
                    Nego.putExtra("IMAGEN", items.get(i).getIMAGEN());
                    Nego.putExtra("LATITUD", items.get(i).getLATITUD());
                    Nego.putExtra("LONGITUD", items.get(i).getLONGITUD());
                    Log.e(":O",""+items.get(i).getID());

                    Activity activity = (Activity) context ;
                    activity.startActivity(Nego);
                    //Añade la animacion de entrada y la de salida
                    activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                    //context.startActivity(Nego);


                }

            }
        });





    }



    public static class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{



        private ItemClickListener clickListener;
        public TextView nombre;
        public TextView descripcion;
        public TextView direccion;
        public ImageView imagen_;



        public MovieViewHolder(View v) {
            super(v);


            nombre = (TextView) v.findViewById(R.id.nombre);
            descripcion = (TextView) v.findViewById(R.id.descripcion);
            direccion = (TextView) v.findViewById(R.id.direccion);
            imagen_ = (ImageView) v.findViewById(imagen);


            v.setTag(v);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        ////\\\\\ para el click ////
        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getPosition(), true);
            return true;
        }

    }


}
