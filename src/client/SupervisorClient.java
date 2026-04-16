package client;

import java.rmi.Naming;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import service.LecturaTemp;
import service.TempService;

public class SupervisorClient {
    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println("💻 --- DASHBOARD DE GESTIÓN DE CADENA DE FRÍO ---");

        while (true) {
            try {
                // Se conecta al mismo servidor que los camiones
                TempService servicio = (TempService) Naming.lookup("rmi://10.146.251.76/TempService");
                List<LecturaTemp> estadoGeneral = servicio.obtenerEstadoActual();

                System.out.println("\n--- ESTADO DE LA RED (" + sdf.format(new Date()) + ") ---");
                System.out.println("Registros en DB: " + estadoGeneral.size());

                if (estadoGeneral.isEmpty()) {
                    System.out.println("Esperando datos de los sensores...");
                } else {
                    // Solo mostramos los últimos 5 para no saturar la consola
                    int inicio = Math.max(0, estadoGeneral.size() - 5);
                    for (int i = inicio; i < estadoGeneral.size(); i++) {
                        LecturaTemp dato = estadoGeneral.get(i);
                        String hora = sdf.format(new Date(dato.timestamp));
                        
                        System.out.printf("[%s] %s | %.2f °C\n", hora, dato.idSensor, dato.temperatura);
                        
                        // Validar cadena de frío
                        if (dato.temperatura > -18.0) {
                            System.out.println("   ⚠️ ¡ALERTA! Temperatura fuera de rango.");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("🔄 Buscando conexión con el CEDI...");
            }

            // Refrescar la pantalla del jefe cada 8 segundos
            try { Thread.sleep(8000); } catch (InterruptedException ie) {}
        }
    }
}