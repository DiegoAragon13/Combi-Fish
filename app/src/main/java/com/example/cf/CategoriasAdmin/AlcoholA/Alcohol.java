package com.example.cf.CategoriasAdmin.AlcoholA;



public class Alcohol {

    private String imagen;
    private String nombre;
    private int precio;

    private String Uid;


    public Alcohol() { //SE NECESITA UN CONSTRUCTOR VACIO EN CADA CATEGORIA PARA LISTAR LAS IMAGES

    }

    public Alcohol(String imagen, String nombre, int precio, String uid) {
        this.imagen = imagen;
        this.nombre = nombre;
        this.precio = precio;
        Uid = uid;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}
