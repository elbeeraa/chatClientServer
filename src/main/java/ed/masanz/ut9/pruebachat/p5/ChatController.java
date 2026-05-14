package ed.masanz.ut9.pruebachat.p5;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;

public class ChatController {

    @FXML
    private TextArea areaMensajes;

    @FXML
    private Button bttnConectar;

    @FXML
    private Button bttnEnviar;

    @FXML
    private TextField textIP;

    @FXML
    private TextField textMensajes;

    @FXML
    private TextField textPort;

    @FXML
    private TextField textNombre;

    private boolean isConnected;
    private  int port;
    private String ip;
    private String userName;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private Thread listenerThread;


    @FXML
    void controladorBotonConectar(ActionEvent event) {
        if(isConnected){
            disconnect();
        } else {
            connect();
        }
    }

    private void disconnect() {
        System.out.println("Desconectando...");
        if(out != null){
            out.println("bye");
        }
        closeResources();
        setConnected(false);
    }

    private void connect() {
//        System.out.println("Conectando a " + textIP.getText() + ":" + textPort.getText());
        ip = textIP.getText();
       if(ip.trim().isEmpty()){
           textIP.requestFocus();
           return;
       }
       userName = textNombre.getText();
       if(userName.trim().isEmpty()){
           textNombre.requestFocus();
           return;
       }
       try{
           port = Integer.parseInt(textPort.getText());
       }catch (NumberFormatException e) {
           textPort.requestFocus();
           return;
       }
//        setConnected(true);
       new Thread(() -> connectInBackground()).start();
    }

    private void connectInBackground() {
        System.out.println("Conectando a " + ip + ":" + port);
        try {
            socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(userName);
            String response = in.readLine();
            if(response == null || !response.equals("OK")){
                System.out.println("Error al registrar el nombre");
                closeResources();
                Platform.runLater(() -> {
                    setConnected(false);
                    appendMessage("Error al registrar el nombre. Elige otro y vuelve a conectar.");
                });
                return;
            }
            Platform.runLater(() -> {
                setConnected(true);
                appendMessage("Conectado al servidor. Ya puedes escribir mensajes:");
            });

            startListening();

        } catch (IOException e) {
            System.out.println("No se pudo conectar al servidor. ¿Está encendido?");
        }
    }

    private void startListening() {
        isConnected = true;
        listenerThread = new Thread(() -> {
            try {
              String line;
                while ((line = in.readLine()) != null) {
                        String finalLine = line;
                        Platform.runLater(() -> appendMessage(finalLine));
                    }
            } catch (IOException e) {
                if(isConnected){
                    Platform.runLater(() -> appendMessage("Se ha perdido la conexión con el servidor."));
                }
            } finally {
                Platform.runLater(() -> {
                    setConnected(false);
                });
                closeResources();
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private void appendMessage(String s) {
        areaMensajes.appendText(s + "\n");
    }

    private void closeResources() {
        //cerrar in out socket
        try {
            if (in != null) {in.close();}
            if (out != null) {out.close();}
            if (socket != null){socket.close();}
        } catch (IOException e) {
            System.out.println("Error al cerrar recursos: " + e.getMessage());
        }
    }

    @FXML
    private void initialize() {
        areaMensajes.setEditable(false);
        setConnected(isConnected);
    }

    private void setConnected(boolean b) {

        bttnConectar.setText(b ? "Desconectar" : "Conectar");
        textIP.setDisable(b);
        textPort.setDisable(b);
        textNombre.setDisable(b);

        areaMensajes.setDisable(!b);
        textMensajes.setDisable(!b);
        bttnEnviar.setDisable(!b);
    }

    @FXML
    void controladorBotonEnviar(ActionEvent event) {
        if(!isConnected || out == null){
            return;
        }
        String message = textMensajes.getText().trim();
        if(message.isEmpty()) {
            return;
        }
        if(message.equalsIgnoreCase("bye")){
            disconnect();
            setConnected(false);
            return;
        }
        out.println(message);
        textMensajes.clear();

    }

}
