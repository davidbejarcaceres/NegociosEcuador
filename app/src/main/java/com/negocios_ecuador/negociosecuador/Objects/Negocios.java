package com.negocios_ecuador.negociosecuador.Objects;


// Este es el Objeto donde se llenaran los datos que extraemos desde el API-REST
public class Negocios{

    private String id;
    private String nombre;
    private String descripcion;
    private String direccion;
    private String imagen;
    private String latitud;
    private String longitud;





    public Negocios(String id, String nombre, String descripcion, String direccion
            ,String imagen, String latitud, String longitud) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.imagen = imagen;
        this.latitud = latitud;
        this.longitud = longitud;

    }


    public String getID() {
        return id;
    }

    public String getNOMBRE() {
        return nombre;
    }

    public String getDESCRIPCION() {
        return descripcion;
    }

    public String getDIRECION() {
        return direccion;
    }

    public String getIMAGEN() {
        return imagen;
    }

    public String getLATITUD() {
        return latitud;
    }

    public String getLONGITUD() {
        return longitud;
    }



}