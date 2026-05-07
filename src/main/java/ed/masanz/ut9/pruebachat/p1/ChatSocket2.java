package ed.masanz.ut9.pruebachat.p1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ChatSocket2 {
    private static final int PORT = 12345;
    // Lista para llevar registro de todos los clientes conectados
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("¿Deseas iniciar como (S)ervidor o (C)liente?");
        String choice = sc.nextLine().trim().toUpperCase();

        if (choice.equals("S")) {
            startServer();
        } else if (choice.equals("C")) {
            startClient();
        } else {
            System.out.println("Opción no válida.");
        }
    }

    // --- LÓGICA DEL SERVIDOR ---
    private static void startServer() {
        System.out.println("Servidor iniciado en el puerto " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                // El servidor se queda esperando una nueva conexión
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            System.out.println("Nuevo cliente conectado: " + socket.getInetAddress());
            try (Scanner in = new Scanner(socket.getInputStream())) {
                out = new PrintWriter(socket.getOutputStream(), true);

                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                while (in.hasNextLine()) {
                    String message = in.nextLine();
                    System.out.println("Mensaje recibido: " + message);
                    // Reenviar el mensaje a todos los demás
                    synchronized (clientWriters) {
                        for (PrintWriter writer : clientWriters) {
                            writer.println(message);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error en la conexión con un cliente.");
            } finally {
                if (out != null) {
                    synchronized (clientWriters) { clientWriters.remove(out); }
                }
                try { socket.close(); } catch (IOException e) { }
            }
        }
    }

    // --- LÓGICA DEL CLIENTE ---
    private static void startClient() {
        try (Socket socket = new Socket("172.20.6.100", PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner in = new Scanner(socket.getInputStream());
             Scanner userInput = new Scanner(System.in)) {

            System.out.println("Conectado al servidor. Escribe tu nombre:");
            String name = userInput.nextLine();

            // Hilo para escuchar mensajes del servidor sin bloquear la escritura
            new Thread(() -> {
                while (in.hasNextLine()) {
                    System.out.println("\n" + in.nextLine());
                }
            }).start();

            System.out.println("Ya puedes escribir mensajes:");
            while (userInput.hasNextLine()) {
                out.println(name + ": " + userInput.nextLine());
            }

        } catch (IOException e) {
            System.out.println("No se pudo conectar al servidor. ¿Está encendido?");
        }
    }
}
