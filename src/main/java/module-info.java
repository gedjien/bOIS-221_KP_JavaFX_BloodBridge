module com.bloodbridge {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.desktop;
    requires transitive com.google.gson;
    requires java.sql;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens com.bloodbridge to javafx.fxml;
    opens com.bloodbridge.controller to javafx.fxml;
    opens com.bloodbridge.model to javafx.base, com.google.gson, com.fasterxml.jackson.databind;
    opens com.bloodbridge.service to javafx.fxml;
    opens com.bloodbridge.util to com.google.gson, com.fasterxml.jackson.databind;

    exports com.bloodbridge;
    exports com.bloodbridge.controller;
    exports com.bloodbridge.model;
    exports com.bloodbridge.service;
    exports com.bloodbridge.util;
}