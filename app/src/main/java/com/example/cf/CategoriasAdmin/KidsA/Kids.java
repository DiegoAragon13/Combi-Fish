package com.example.cf.CategoriasAdmin.KidsA;

public class Kids {

    private String imagen;
    private String nombre;
    private int precio;

    private String Uid;

    public Kids() {

    }

    public Kids(String imagen, String nombre, int precio, String uid) {
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
