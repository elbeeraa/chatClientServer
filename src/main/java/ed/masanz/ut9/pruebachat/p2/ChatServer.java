package ed.masanz.ut9.pruebachat.p2;

import ed.masanz.ut9.pruebachat.p1.ChatSocket2;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {
    private static final int PORT = 12345;
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args) {
        start();
    }
    public static void start() {
        System.out.println("Servidor iniciado en el puerto " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                // El servidor se queda esperando una nueva conexión
                Socket socket = serverSocket.accept();
                new ChatClientHandler(socket, clientWriters).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
