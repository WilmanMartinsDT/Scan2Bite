package wilman.martins.scan2bite.models;

import java.io.Serializable;

public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private String categoria;
    private double azucares;
    private int puntuacion;
    private String alternativa;

    public Producto(String nombre, String categoria, double azucares, int puntuacion, String alternativa) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.azucares = azucares;
        this.puntuacion = puntuacion;
        this.alternativa = alternativa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getAzucares() {
        return azucares;
    }

    public void setAzucares(double azucares) {
        this.azucares = azucares;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getAlternativa() {
        return alternativa;
    }

    public void setAlternativa(String alternativa) {
        this.alternativa = alternativa;
    }
}
