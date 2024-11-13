package com.example.appvehiculos.ui.vehiculocrear;
import com.google.firebase.firestore.PropertyName;

public class Vehiculo {
    private int año;
    private String placa;
    private String marca;
    private String modelo;
    private String imageUrl;
    private String userId; // Nuevo campo para almacenar el userId del propietario
    private int kilometraje;

    public Vehiculo() {
        // Constructor vacío requerido para Firestore
    }

    public Vehiculo(int año, String placa, String marca, String modelo, String imageUrl, String userId, int kilometraje) {
        this.año = año;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.imageUrl = imageUrl;
        this.userId = userId; // Asignación en el constructor
        this.kilometraje = kilometraje;
    }

    @PropertyName("año")
    public int getAño() {
        return año;
    }

    @PropertyName("año")
    public void setAño(int año) {
        this.año = año;
    }

    @PropertyName("placa")
    public String getPlaca() {
        return placa;
    }

    @PropertyName("placa")
    public void setPlaca(String placa) {
        this.placa = placa;
    }

    @PropertyName("marca")
    public String getMarca() {
        return marca;
    }

    @PropertyName("marca")
    public void setMarca(String marca) {
        this.marca = marca;
    }

    @PropertyName("modelo")
    public String getModelo() {
        return modelo;
    }

    @PropertyName("modelo")
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    @PropertyName("imageUrl")
    public String getImageUrl() {
        return imageUrl;
    }

    @PropertyName("imageUrl")
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @PropertyName("userId")
    public String getUserId() {
        return userId;
    }

    @PropertyName("userId")
    public void setUserId(String userId) {
        this.userId = userId;
    }
    @PropertyName("kilometraje")
    public int getKilometraje() {
        return kilometraje;
    }
    @PropertyName("kilometraje")
    public void setKilometraje(int kilometraje) {
        this.kilometraje = kilometraje;
    }
}
