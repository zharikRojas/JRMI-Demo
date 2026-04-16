package service;

import java.io.Serializable;

public class LecturaTemp implements Serializable {
    // Requisito de RMI para poder enviar objetos por la red
    private static final long serialVersionUID = 1L; 
    
    public String idSensor;
    public double temperatura;
    public long timestamp;

    public LecturaTemp(String idSensor, double temperatura, long timestamp) {
        this.idSensor = idSensor;
        this.temperatura = temperatura;
        this.timestamp = timestamp;
    }
}