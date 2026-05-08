module ed.masanz.ut9.pruebachat {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens ed.masanz.ut9.pruebachat to javafx.fxml;
    exports ed.masanz.ut9.pruebachat;
    exports ed.masanz.ut9.pruebachat.p1;
    exports ed.masanz.ut9.pruebachat.p5;
    opens ed.masanz.ut9.pruebachat.p1 to javafx.fxml;
    opens ed.masanz.ut9.pruebachat.p5 to javafx.fxml;
}