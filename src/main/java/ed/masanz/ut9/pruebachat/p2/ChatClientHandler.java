package ed.masanz.ut9.pruebachat.p2;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;

public class ChatClientHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private Set<PrintWriter> clientWriters;
    public ChatClientHandler(Socket socket, Set<PrintWriter> clientWriters) {
        this.socket = socket;
        this.clientWriters = clientWriters;
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
