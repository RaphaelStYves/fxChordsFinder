module fxbeatpartern {

    requires java.compiler;
    requires javafx.fxml;
    requires javafx.controls;
    requires java.desktop;
    requires java.logging;
    requires javafx.graphics;
    requires javafx.base;


    exports menuBar;
    opens menuBar;

    opens sample;

}

