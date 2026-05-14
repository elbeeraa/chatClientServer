package ed.masanz.ut9.pruebachat.p5;

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

            registerClient(in);

            while (in.hasNextLine()) {
                String message = in.nextLine();
                System.out.println("Mensaje recibido de " + clientName + ": " + message);
                if(message.equalsIgnoreCase("bye")){
                    System.out.println(clientName + " se ha desconectado.");
                    broadcastMessage(clientName + " se ha desconectado.");
                    break;
                } else if (message.equalsIgnoreCase("who")) {
                    StringBuilder clientsList = new StringBuilder("Clientes conectados: ");
                    synchronized (mapClientWriters) {
                        for (String name : mapClientWriters.keySet()) {
                            clientsList.append(name).append(" ");
                        }
                    }
                    out.println(clientsList);
                    continue;
                }
                // Reenviar el mensaje a todos los demás
                broadcastMessage(clientName + ": " + message);
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
                    writer.println(message);
                }
            }
    }

    private void registerClient(Scanner in) {
        while(true){
            if(in.hasNextLine()){
                String proposedName = in.nextLine().trim();
                synchronized (mapClientWriters) {
                    if (!proposedName.isEmpty() && !mapClientWriters.containsKey(proposedName)) {
                        clientName = proposedName;
                        mapClientWriters.put(clientName, out);
                        System.out.println("Cliente registrado con nombre: " + clientName);
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
