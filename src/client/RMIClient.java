package client;

import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;
import service.LecturaTemp;
import service.TempService;

public class RMIClient {
    public static void main(String[] args) {
        // Esta lista simula la memoria interna del sensor cuando no hay internet
        List<LecturaTemp> datosOffline = new ArrayList<>();
        String camionId = args.length > 0 ? args[0] : "Tractomula-001";
        String servidorUrl = args.length > 1 ? args[1] : "rmi://192.168.1.53/TempService";

        System.out.println("🚚 Sensor del camión " + camionId + " iniciado.");
        System.out.println("🌐 Enviando datos a: " + servidorUrl);

        while (true) {
            try {
                // 1. Leer el termómetro (Simulamos temperatura entre -20 y -18 grados)
                double tempActual = -20 + (Math.random() * 3);
                LecturaTemp nuevaLectura = new LecturaTemp(camionId, tempActual, System.currentTimeMillis());

                // 2. Conectar al servidor
                TempService servicio = (TempService) Naming.lookup(servidorUrl);

                // 3. ¿Teníamos datos acumulados por falta de internet? Si es así, los enviamos de golpe.
                if (!datosOffline.isEmpty()) {
                    servicio.sincronizarDatosOffline(datosOffline);
                    System.out.println("✅ Se sincronizaron " + datosOffline.size() + " registros atrasados.");
                    datosOffline.clear(); // Vaciamos la memoria interna
                }

                // 4. Enviar el dato normal en tiempo real
                servicio.registrarTemperatura(nuevaLectura);
                System.out.println("📡 Dato enviado OK -> Temp: " + String.format("%.2f", tempActual) + " °C");

            } catch (Exception e) {
                // LA MAGIA DE TU PROYECTO: Si falla el Naming.lookup o el envío, caemos aquí.
                System.out.println("❌ Sin señal de internet. Guardando temperatura en memoria local...");
                
                // Generamos un dato local para no perder la lectura
                double tempGuardada = -20 + (Math.random() * 3);
                datosOffline.add(new LecturaTemp(camionId, tempGuardada, System.currentTimeMillis()));
            }

            // Esperar 4 segundos antes de tomar la siguiente medida
            try { Thread.sleep(4000); } catch (InterruptedException ie) {}
        }
    }
}