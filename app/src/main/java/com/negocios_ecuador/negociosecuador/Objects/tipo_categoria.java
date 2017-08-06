package com.negocios_ecuador.negociosecuador.Objects;

// Este es el Objeto donde se llenaran los datos que extraemos desde el API-REST
public class tipo_categoria{

    private String id;
    private String nombre;




    public tipo_categoria(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;

    }


    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

}