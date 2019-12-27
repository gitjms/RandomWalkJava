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
 * Class for executing Path Tracing
 */
class ExecPath extends Data {

    private String language;

    /**
     * Initiating class
     */
    ExecPath(String language) {
        super();
        this.setLanguage(language);
    }

    /**
     * method for setting execute path mouseclicked
     * @param execNappi button
     * @param pathScene scene
     * @param ex INSTANCE FOR CODE EXECUTIONS
     * @param datafolder data folder
     * @param datapath data path
     * @param fexec fexec
     * @param pyexec1d pyexec1d
     * @param pyexec2d pyexec2d
     * @param pyexec3d pyexec3d
     * @param getDialogs getDialogs
     */
    void setExecClick(@NotNull Button execNappi, ScenePathTracing pathScene, Execution ex, File datafolder, String datapath,
                      String fexec, String pyexec1d, String pyexec2d, String pyexec3d, GetDialogs getDialogs) {
        execNappi.setOnMouseClicked((MouseEvent event) -> {
            pathScene.setSave("s");
            String[] vars = pathScene.getVars();
            this.setVars(vars);
            Data data = new Data(vars);
            boolean fail = false;

            int particles = Integer.parseInt(this.getVars()[1]);
            int steps = Integer.parseInt(this.getVars()[3]);
            int dim = Integer.parseInt(this.getVars()[4]);
            String fixed = this.getVars()[6];
            String lattice = this.getVars()[7];

            if ( particles < 0 ) fail = true;
            if ( steps < 1 ) fail = true;
            if ( dim < 1 || dim > 3 ) fail = true;
            if ( !fixed.equals("f") && !fixed.equals("-") ) fail = true;
            if ( !lattice.equals("l") && !lattice.equals("-") ) fail = true;

            if ( fail ) return;

            String warnText = "";
            int cost = (int) Math.log10(particles*steps);

            if (cost > 10) {
                warnText = this.getLanguage().equals("fin") ? "Ajo voi kestää kauan." : "Run may take a long time.";
            }

            if ( cost < 6 ) {
                ex.executePath(datafolder, datapath, fexec, pyexec1d, pyexec2d, pyexec3d, data, this.getVars());
            } else {
                /*
                 * ALERT DIALOG
                 */
                String alertText = warnText + (this.getLanguage().equals("fin") ? " Jatketaanko?" : " Do you want to continue?");
                Alert alertPath = getDialogs.getAlert(this.getLanguage(), this.getButtonYES(), this.getButtonNO(), alertText);
                alertPath.showAndWait();

                if ( alertPath.getResult().getButtonData().equals(this.getButtonYES().getButtonData()) ) {
                    ex.executePath(datafolder, datapath, fexec, pyexec1d, pyexec2d, pyexec3d, data, this.getVars());
                    alertPath.close();
                } else alertPath.close();
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
