package randomwalkjava;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 *
 * Class for executing Rms calculation
 */
class ExecCalc extends Data {

    private String language;

    /**
     * Initiating class
     */
    ExecCalc(String language) {
        super();
        this.setLanguage(language);
    }

    /**
     * method for setting execute path mouseclicked
     * @param execNappi button
     * @param calcScene scene
     * @param ex instance for code executions
     * @param datafolder data folder
     * @param datapath data path
     * @param fexec fexec
     * @param pyexecrms pyexec1Ddist
     * @param getDialogs getDialogs
     */
    void setExecClick(@NotNull Button execNappi, SceneCalculation calcScene, Execution ex, File datafolder, String datapath,
                      String fexec, String pyexecrms, GetDialogs getDialogs) {
        execNappi.setOnMouseClicked((MouseEvent event) -> {
            calcScene.setSave("s");
            String[] vars = calcScene.getVars();
            this.setVars(vars);
            Data data = new Data(vars);
            boolean fail = false;

            int steps = Integer.parseInt(this.getVars()[4]);
            int dim = Integer.parseInt(this.getVars()[5]);
            String lattice = this.getVars()[8];

            if ( steps < 1 ) fail = true;
            if ( dim < 1 || dim > 3 ) fail = true;
            if ( !lattice.equals("l") && !lattice.equals("-") ) fail = true;

            if ( fail ) return;

            String warnText = "";
            if (Math.log10(steps) > 4) {
                warnText = this.getLanguage().equals("fin") ? "Datankäsittely voi kestää kauan." : "Data processing may take a long time.";
            }

            if (Math.log10(steps) < 5) {
                ex.executeRms(datafolder, datapath, fexec, pyexecrms, data, this.getVars());
            } else {
                /*
                 * ALERT DIALOG
                 */
                String alertText = warnText + (this.getLanguage().equals("fin") ? " Jatketaanko?" : " Do you want to continue?");
                Alert alertRms = getDialogs.getAlert(this.getLanguage(), this.getButtonYES(), this.getButtonNO(), alertText);
                alertRms.showAndWait();

                if ( alertRms.getResult().getButtonData().equals(this.getButtonYES().getButtonData()) ) {
                    ex.executeRms(datafolder, datapath, fexec, pyexecrms, data, this.getVars());
                    alertRms.close();
                } else alertRms.close();
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
