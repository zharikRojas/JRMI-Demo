package service;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TempServiceImpl extends UnicastRemoteObject implements TempService {

    // Lista sincronizada para evitar errores si 2 camiones envían datos al mismo tiempo
    private List<LecturaTemp> baseDeDatos = Collections.synchronizedList(new ArrayList<>());

    public TempServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void registrarTemperatura(LecturaTemp lectura) throws RemoteException {
        System.out.println("📥 Dato recibido -> Camión: " + lectura.idSensor + 
                           " | Temp: " + String.format("%.2f", lectura.temperatura) + " °C");
        baseDeDatos.add(lectura);
    }

    @Override
    public void sincronizarDatosOffline(List<LecturaTemp> lecturas) throws RemoteException {
        System.out.println("🔄 Sincronizando " + lecturas.size() + " datos atrasados de un camión...");
        baseDeDatos.addAll(lecturas);
    }

    @Override
    public List<LecturaTemp> obtenerEstadoActual() throws RemoteException {
        // Retornamos una copia para que la tablet la lea
        return new ArrayList<>(baseDeDatos);
    }
}