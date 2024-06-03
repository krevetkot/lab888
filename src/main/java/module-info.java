module com.labs.secondsemester.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires java.sql;
    requires jakarta.xml.bind;
    requires org.apache.logging.log4j;



    opens com.labs.secondsemester.client to javafx.fxml;
    exports com.labs.secondsemester.client;
}