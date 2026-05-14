package ed.masanz.ut9.pruebachat.p5;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private static final int PORT = 12345;
//    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static Map<String, PrintWriter> mapClientWriters;

    public ChatServer() { mapClientWriters = new HashMap<>();}

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        System.out.println("Servidor iniciado en el puerto " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                // El servidor se queda esperando una nueva conexión
                Socket socket = serverSocket.accept();
                new ChatClientHandler(socket, mapClientWriters).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
