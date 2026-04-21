package server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import service.TempService;
import service.TempServiceImpl;

public class RMIServer {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            TempService servicio = new TempServiceImpl();
            Naming.rebind("rmi://192.168.1.53/TempService", servicio);

            System.out.println("✅ Servidor Central listo y esperando datos de los camiones...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
