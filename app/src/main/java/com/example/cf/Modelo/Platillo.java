package com.example.cf.Modelo;


public class Platillo {
    private String nombre;
    private int precio;
    private String imagen;

    public Platillo() {
        // Constructor vacío necesario para Firebase
    }

    public Platillo(String nombre, int precio, String imagen) {
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
    }

    // Getters y setters
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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
