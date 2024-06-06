module labs.secondSemester.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires java.sql;
    requires jakarta.xml.bind;
    requires org.apache.logging.log4j;
//    requires org.apache.logging.log4j;
//    requires labs.secondSemester.commons;
//    requires org.apache.logging.log4j;
//    requires labs.secondSemester.commons;

    opens labs.secondSemester.client to javafx.fxml;

    exports labs.secondSemester.client;
    exports labs.secondSemester.client.controllers;
    opens labs.secondSemester.client.controllers to javafx.fxml;
}