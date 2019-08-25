
package randomwalkjava;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for thread failure message
 */
class Message extends Thread {
    @Override
    public void run()
    {
        Alert alert;
        alert = new Alert(
                Alert.AlertType.ERROR,
                "Program is closing due to errors.",
                ButtonType.OK);
        alert.showAndWait();
    }
}