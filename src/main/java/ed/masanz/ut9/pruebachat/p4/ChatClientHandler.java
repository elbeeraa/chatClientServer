package ed.masanz.ut9.pruebachat.p4;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

public class ChatClientHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private String clientName;
    private Map<String, PrintWriter> mapClientWriters;
    public ChatClientHandler(Socket socket, Map<String, PrintWriter> clientWriters) {
        this.socket = socket;
        this.mapClientWriters = clientWriters;
    }

    public void run() {
        System.out.println("Nuevo cliente conectado: " + socket.getInetAddress());
        try (Scanner in = new Scanner(socket.getInputStream())) {
            out = new PrintWriter(socket.getOutputStream(), true);

            registrerClient(in);

            while (in.hasNextLine()) {
                String message = in.nextLine();
                System.out.println("Mensaje recibido de " + clientName + ": " + message);
                if(message.equalsIgnoreCase("bye")){
                    System.out.println(clientName + " se ha desconectado.");
                    broadcastMessage(clientName + " se ha desconectado.");
                    break;
                }
                // Reenviar el mensaje a todos los demás
                broadcastMessage(message);
            }
        } catch (IOException e) {
            System.out.println("Error en la conexión con un cliente.");
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        if (clientName != null) {
            synchronized (mapClientWriters) {
                mapClientWriters.remove(clientName);
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Error al cerrar la conexión con el cliente.");
        }
    }

    private void broadcastMessage(String message) {
            synchronized (mapClientWriters) {
                for (PrintWriter writer : mapClientWriters.values()) {
                    writer.println(clientName + ": " + message);
                }
            }
    }

    private void registrerClient(Scanner in) {
        while(true){
            if(in.hasNextLine()){
                String proposedName = in.nextLine().trim();
                synchronized (mapClientWriters) {
                    if (!proposedName.isEmpty() && !mapClientWriters.containsKey(proposedName)) {
                        clientName = proposedName;
                        mapClientWriters.put(clientName, out);
                        out.println("OK");
                        break;
                    } else {
                        out.println("NOK");
                    }
                }
            }
        }
    }
}
