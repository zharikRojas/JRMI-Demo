package client;

import java.rmi.Naming;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import service.LecturaTemp;
import service.TempService;

public class SupervisorClient {
    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String servidorUrl = args.length > 0 ? args[0] : "rmi://192.168.1.53/TempService";
        Scanner scanner = new Scanner(System.in);

        System.out.println("💻 --- DASHBOARD DE GESTIÓN DE CADENA DE FRÍO ---");
        System.out.println("🌐 Consultando servidor: " + servidorUrl);
        System.out.println("Escribe el ID de un camión para filtrar, ENTER para ver todos, o 'salir'.\n");

        while (true) {
            try {
                // Se conecta al mismo servidor que los camiones
                TempService servicio = (TempService) Naming.lookup(servidorUrl);

                System.out.print("Filtro (camión): ");
                String filtroCamion = scanner.nextLine().trim();

                if (filtroCamion.equalsIgnoreCase("salir") || filtroCamion.equalsIgnoreCase("exit")) {
                    System.out.println("Cerrando dashboard supervisor...");
                    break;
                }

                List<LecturaTemp> estadoGeneral;
                if (filtroCamion.isEmpty()) {
                    estadoGeneral = servicio.obtenerEstadoActual();
                } else {
                    estadoGeneral = servicio.obtenerEstadoPorCamion(filtroCamion);
                }

                System.out.println("\n--- ESTADO DE LA RED (" + sdf.format(new Date()) + ") ---");
                System.out.println("Modo: " + (filtroCamion.isEmpty() ? "TODOS" : "CAMION " + filtroCamion));
                System.out.println("Registros en DB: " + estadoGeneral.size());

                if (filtroCamion.isEmpty()) {
                    List<String> camiones = servicio.obtenerCamionesRegistrados();
                    System.out.println("Camiones detectados: " + (camiones.isEmpty() ? "ninguno" : String.join(", ", camiones)));
                }

                if (estadoGeneral.isEmpty()) {
                    System.out.println("Esperando datos de los sensores...");
                } else {
                    // Solo mostramos los últimos 10 para no saturar la consola
                    int inicio = Math.max(0, estadoGeneral.size() - 10);
                    for (int i = inicio; i < estadoGeneral.size(); i++) {
                        LecturaTemp dato = estadoGeneral.get(i);
                        String hora = sdf.format(new Date(dato.timestamp));
                        
                        System.out.printf("[%s] %s | %.2f °C\n", hora, dato.idSensor, dato.temperatura);
                        
                        // Validar cadena de frío
                        if (dato.temperatura > -18.0) {
                            System.out.println(" ¡ALERTA! Temperatura fuera de rango.");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("🔄 Buscando conexión con el servidor...");
            }

            try { Thread.sleep(8000); } catch (InterruptedException ie) {}
        }

        scanner.close();
    }
}