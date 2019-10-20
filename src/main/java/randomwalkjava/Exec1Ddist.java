package randomwalkjava;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static java.lang.Integer.parseInt;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 *
 * Class for creating execute button for 1D distance
 */
public class Exec1DdistClick extends Data {

    private String language;

    /**
     * Initiating class
     */
    Exec1DdistClick(String language) {
        super();
        this.setLanguage(language);
    }

    /**
     * method for setting execute path mouseclicked
     * @param execNappi button
     * @param distScene scene
     * @param ex INSTANCE FOR CODE EXECUTIONS
     * @param datafolder data folder
     * @param datapath data path
     * @param fexec fexec
     * @param pyexec1Ddist pyexec1Ddist
     * @param getDialogs getDialogs
     */
    void setMouseClick(@NotNull Button execNappi, Scene1Ddist distScene, Execution ex, File datafolder, String datapath,
                       String fexec, String pyexec1Ddist, GetDialogs getDialogs) {
        execNappi.setOnMouseClicked((MouseEvent event) -> {
            String[] vars = distScene.getVars();
            this.setVars(vars);
            Data data = new Data(vars);
            boolean fail = false;

            int particles = parseInt(getVars()[0]);
            int steps = parseInt(getVars()[3]);
            String lattice = this.getVars()[7];

            if ( particles < 0 ) fail = true;
            if ( steps < 1 ) fail = true;
            if ( !lattice.equals("l") && !lattice.equals("-") ) fail = true;

            if ( fail ) return;

            int hours = (int) (particles * steps * 0.0000005)/60;
            int mins = (int) (particles * steps * 0.0000005)%60;
            String warnText = "";
            if (Math.log10(particles * steps) > 6) {
                if (hours > 1)
                    warnText = this.getLanguage().equals("fin") ? "Datankäsittely voi kestää " + hours + "h." : "Data processing may take " + hours + "h.";
                else
                    warnText = this.getLanguage().equals("fin") ? "Datankäsittely voi kestää " + mins + "min." : "Data processing may take " + mins + "min.";
            }

            if (Math.log10(particles * steps) <= 6) {
                ex.execute1Ddist(datafolder, datapath, fexec, pyexec1Ddist, data, this.getVars());
            } else {
                /*
                 * ALERT DIALOG
                 */
                String alertText = warnText + (this.getLanguage().equals("fin") ? " Jatketaanko?" : " Do you want to continue?");
                Alert alert1Ddist = getDialogs.getAlert(this.getLanguage(), this.getButtonYES(), this.getButtonNO(), alertText);
                alert1Ddist.showAndWait();

                if ( alert1Ddist.getResult() == ButtonType.YES) {
                    ex.execute1Ddist(datafolder, datapath, fexec, pyexec1Ddist, data, this.getVars());
                }
            }
        });
    }

    /**
     * @return the language
     */
    @Contract(pure = true)
    private String getLanguage() { return this.language; }

    /**
     * @param language the language to set
     */
    private void setLanguage(String language) { this.language = language; }

    /**
     * @return the vars
     */
    private String[] getVars() { return vars.clone(); }

    /**
     * @param vars the vars to set
     */
    private void setVars(@NotNull String[] vars) { this.vars = vars.clone(); }

    /**
     * @return the buttonYES
     */
    @NotNull
    @Contract(pure = true)
    private ButtonType getButtonYES() { return new ButtonType(this.getLanguage().equals("fin") ? "KYLLÄ" : "YES", ButtonBar.ButtonData.YES); }

    /**
     * @return the buttonNO
     */
    @NotNull
    @Contract(pure = true)
    private ButtonType getButtonNO() { return new ButtonType( this.getLanguage().equals("fin") ? "EI" : "NO", ButtonBar.ButtonData.NO); }

}
