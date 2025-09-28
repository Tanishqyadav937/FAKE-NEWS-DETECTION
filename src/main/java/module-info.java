module ai.javacodereviewer {
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires gson;
    requires java.desktop;
    
    exports com.javacodereviewer;
    exports com.javacodereviewer.controller;
    exports com.javacodereviewer.model;
    exports com.javacodereviewer.service;
    exports com.javacodereviewer.util;
}
