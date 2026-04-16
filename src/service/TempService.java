package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface TempService extends Remote {
    void registrarTemperatura(LecturaTemp lectura) throws RemoteException;
    void sincronizarDatosOffline(List<LecturaTemp> lecturas) throws RemoteException;
    List<LecturaTemp> obtenerEstadoActual() throws RemoteException;
}