package com.example.cf.Modelo;

public class Administrador {

    String UID,NOMBRES,APELLIDOS,CORREO,Imagen;
    int EDAD;

    public Administrador() {

    }

    public Administrador(String UID, String NOMBRES, String APELLIDOS, String CORREO, String imagen, int EDAD) {
        this.UID = UID;
        this.NOMBRES = NOMBRES;
        this.APELLIDOS = APELLIDOS;
        this.CORREO = CORREO;
        this.Imagen = imagen;
        this.EDAD = EDAD;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getNOMBRES() {
        return NOMBRES;
    }

    public void setNOMBRES(String NOMBRES) {
        this.NOMBRES = NOMBRES;
    }

    public String getAPELLIDOS() {
        return APELLIDOS;
    }

    public void setAPELLIDOS(String APELLIDOS) {
        this.APELLIDOS = APELLIDOS;
    }

    public String getCORREO() {
        return CORREO;
    }

    public void setCORREO(String CORREO) {
        this.CORREO = CORREO;
    }

    public String getImagen() {
        return Imagen;
    }

    public void setImagen(String imagen) {
        Imagen = imagen;
    }

    public int getEDAD() {
        return EDAD;
    }

    public void setEDAD(int EDAD) {
        this.EDAD = EDAD;
    }
}
