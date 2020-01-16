package randomwalkjava;

import com.sun.glass.ui.Screen;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 *
 * Class for executing and plotting Diffusion
 */
class ExecDiff extends Data {

    private String language;
    private List <Double> diffusion_x;
    private List <Double> diffusion_y;
    private List <Double> energy_x;
    private List <Double> energy_y;
    private List <Double> visc_x;
    private List <Double> visc_y;
    private HBox isovalikko;
    private Pane pane;
    private Canvas alusta;
    private double scalefactor;
    private double linewidth;
    private boolean newdata;

    /**
     * Initiating class
     */
    ExecDiff(String language) {
        super();
        this.setLanguage(language);
    }

    void setExecClick(@NotNull Button execNappi, SceneDiff diffScene, GraphicsContext diffpiirturi,
                      double scalefactor, double animwidth, double animheight, boolean newdata, HBox isovalikkoDiff,
                      VBox valikkoDiff, Pane diffpane, String datapath, File datafolder, String fexec,
                      Button remBarNappiDiff, Button cancelNappiDiff, Button plotDiff, Button closeNappiDiff,
                      Button menuNappiDiff, Button helpNappiDiff, Canvas diffAlusta) {

        this.setIsoValikko(isovalikkoDiff);
        this.setPane(diffpane);
        this.setScalefactor(scalefactor);
        this.setAlusta(diffAlusta);
        this.setNewdata(newdata);

        execNappi.setOnMouseClicked((MouseEvent event) -> {
            if ( diffScene.timerIsRunning()) return;
            diffScene.setSave("-");
            String[] vars = diffScene.getVars();
            this.setVars(vars);
            int particles = Integer.parseInt(this.getVars()[1]);
            double diam = Double.parseDouble(this.getVars()[2]);
            int dim = Integer.parseInt(this.getVars()[4]);
            String lattice = this.getVars()[7];
            boolean fail = false;

            if ( particles < 0 ) fail = true;
            if ( diam <= 0.0 || diam >= 1.0 ) fail = true;
            if ( dim < 2 || dim > 3 ) fail = true;
            if ( !lattice.equals("l") && !lattice.equals("-") ) fail = true;

            if ( fail ) return;

            if (diffScene.getFxplot() != null) {
                if (diffScene.getFxplot().getFrame().isShowing()
                    || diffScene.getFxplot().getFrame().isActive()
                    || diffScene.getFxplot().getFrame().isDisplayable())
                    diffScene.getFxplot().getFrame().dispose();
            }

            GetComponents getComponents = new GetComponents();

            if (this.getIsoValikko().getChildren().size() > 1) {
                this.getIsoValikko().getChildren().remove(1);
                this.setPane(getComponents.getPane(this.getAlusta(), this.getAnimWidth(), this.getAnimHeight()));
                this.getIsoValikko().getChildren().add(this.getPane());
                helpNappiDiff.setText(this.getLanguage().equals("fin") ? "OHJE" : "HELP");
            }

            diffScene.setFxplot( new FXPlot());
            diffScene.getFxplot().setFXPlot(this.getLanguage(),"energy&diffusion");

            this.setEnergy_x(new ArrayList<>());
            this.setEnergy_y(new ArrayList<>());
            this.setDiffusion_x(new ArrayList<>());
            this.setDiffusion_y(new ArrayList<>());
            this.setVisc_x(new ArrayList<>());
            this.setVisc_y(new ArrayList<>());

            diffpiirturi.scale(1.0/this.getScalefactor(), 1.0/this.getScalefactor());

            double measure;
            double diff;
            if ( particles < 25 ) {
                diff = 0.3;
                measure = 21.0;
            } else {
                measure = Math.round(3.0 * Math.sqrt( 2.0 * (double) particles ));
                double diff1 = Math.sqrt(measure)/10.0 + measure/20.0 - 0.7;
                double diff2 = measure/20.0 - 0.7;

                if ( (measure+1.0)%4.0 == 0.0 ) {
                    diff = diff1;
                    measure -= 1.0;
                } else if ( measure%4.0 == 0.0 ) {
                    diff = diff1;
                    measure -= 2.0;
                } else if ( measure%2.0 == 0.0 ) {
                    diff = diff1;
                    measure -= 0.0;
                } else {
                    diff = diff2;
                }
            }

            this.setScalefactor((animwidth - 83.3) / measure);
            if ( dim == 2 ) this.setLinewidth(1.0 / this.getScalefactor());
            else this.setLinewidth(diam / this.getScalefactor());

            diffpiirturi.scale(this.getScalefactor(), this.getScalefactor());

            this.setNewdata(true);

            diffpiirturi.setGlobalAlpha(1.0);
            diffpiirturi.setFill(Color.BLACK);
            diffpiirturi.fillRect(0, 0, 1.0/this.getScalefactor()*animwidth, 1.0/this.getScalefactor()*animheight);
            diffpiirturi.fill();

            /*
             * GET INITIAL DATA
             */
            File initialDataFile = new File( datapath + "/startDiff_" + dim + "D_" + particles + "N.xy");
            boolean deleted;
            if (Files.exists(initialDataFile.toPath())) {
                deleted = initialDataFile.delete();
                String warntext = this.getLanguage().equals("fin")
                    ? "Vanhaa datatiedostoa 'startDiff_" + dim + "D_" + particles + "N.xy' ei voitu"
                    + " poistaa, mikä voi pilata ajon.\n"
                    + "Yritä sulkea sovellus, poistaa tiedosto käsin, ja sitten ajaa uudestaan."
                    : "Old data file 'startDiff_" + dim + "D_" + particles + "N.xy' couldn't be"
                    + " deleted, which may spoil your run.\n"
                    + "Try closing the application, delete the file manually, and then run again.";
                if (!deleted) System.out.println(warntext);
            }

            valikkoDiff.getChildren().set(3, remBarNappiDiff);
            valikkoDiff.getChildren().set(4, cancelNappiDiff);

            /*
             * DRAW DIFFUSION ANIMATION
             */
            diffScene.refresh(datafolder, initialDataFile, fexec, diffpiirturi, this.getScalefactor(),
                animwidth, this.getLinewidth(), remBarNappiDiff, cancelNappiDiff, execNappi, valikkoDiff,
                plotDiff, closeNappiDiff, menuNappiDiff, helpNappiDiff, this.getEnergy_x(), this.getEnergy_y(),
                this.getDiffusion_x(), this.getDiffusion_y(), this.getVisc_x(), this.getVisc_y(),
                this.isNewdata(), measure, diff
            );

            this.setNewdata(false);
        });
    }

    void setPlotClick (@NotNull Button plotNappi, SceneDiff diffScene, VBox valikkoDiff, String datapath,
                       File datafolder, String fexec, String pyexecdiff2d, String pyexecdiff3d, Execution ex) {

        plotNappi.setOnMouseClicked((MouseEvent event) -> {
            diffScene.setSave("+");
            String[] vars = diffScene.getVars();
            this.setVars(vars);
            Data data = new Data(vars);
            int particles = Integer.parseInt(getVars()[1]);
            double diam = Double.parseDouble(getVars()[2]);
            int dim = Integer.parseInt(getVars()[4]);
            String lattice = this.getVars()[7];
            boolean fail = false;

            if ( particles < 0 ) fail = true;
            if ( diam <= 0.0 || diam >= 1.0 ) fail = true;
            if ( dim < 2 || dim > 3 ) fail = true;
            if ( !lattice.equals("l") && !lattice.equals("-") ) fail = true;

            if ( fail ) return;

            valikkoDiff.setDisable(true);

            ex.executeDiff(datafolder, datapath, fexec, pyexecdiff2d,
                pyexecdiff3d, valikkoDiff, data, this.getVars());

            valikkoDiff.setDisable(false);
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
     * @return the isovalikko
     */
    @Contract(pure = true)
    private HBox getIsoValikko() { return this.isovalikko; }

    /**
     * @param isovalikko the isovalikko to set
     */
    private void setIsoValikko(HBox isovalikko) { this.isovalikko = isovalikko; }

    /**
     * @return the pane
     */
    @Contract(pure = true)
    private Pane getPane() { return this.pane; }

    /**
     * @param pane the pane to set
     */
    private void setPane(Pane pane) { this.pane = pane; }

    /**
     * @return the alusta
     */
    @Contract(pure = true)
    private Canvas getAlusta() { return this.alusta; }

    /**
     * @param alusta the alusta to set
     */
    private void setAlusta(Canvas alusta) { this.alusta = alusta; }

    /**
     * @return the diffusion_x
     */
    @Contract(pure = true)
    private List<Double> getDiffusion_x() { return this.diffusion_x; }

    /**
     * @param diffusion_x the diffusion_x to set
     */
    private void setDiffusion_x(List<Double> diffusion_x) { this.diffusion_x = diffusion_x; }

    /**
     * @return the diffusion_y
     */
    @Contract(pure = true)
    private List <Double> getDiffusion_y() { return this.diffusion_y; }

    /**
     * @param diffusion_y the diffusion_y to set
     */
    private void setDiffusion_y(List<Double> diffusion_y) { this.diffusion_y = diffusion_y; }

    /**
     * @return the energy_x
     */
    @Contract(pure = true)
    private List <Double> getEnergy_x() { return this.energy_x; }

    /**
     * @param energy_x the energy_x to set
     */
    private void setEnergy_x(List<Double> energy_x) { this.energy_x = energy_x; }

    /**
     * @return the energy_y
     */
    @Contract(pure = true)
    private List <Double> getEnergy_y() { return this.energy_y; }

    /**
     * @param energy_y the energy_y to set
     */
    private void setEnergy_y(List<Double> energy_y) { this.energy_y = energy_y; }

    /**
     * @return the visc_x
     */
    @Contract(pure = true)
    private List <Double> getVisc_x() { return this.visc_x; }

    /**
     * @param visc_x the visc_x to set
     */
    private void setVisc_x(List<Double> visc_x) { this.visc_x = visc_x; }

    /**
     * @return the visc_y
     */
    @Contract(pure = true)
    private List <Double> getVisc_y() { return this.visc_y; }

    /**
     * @param visc_y the visc_y to set
     */
    private void setVisc_y(List<Double> visc_y) { this.visc_y = visc_y; }

    /**
     * @return the scalefactor
     */
    @Contract(pure = true)
    private double getScalefactor() { return scalefactor; }

    /**
     * @param scalefactor the scalefactor to set
     */
    private void setScalefactor(double scalefactor) { this.scalefactor = scalefactor; }

    /**
     * @return the linewidth
     */
    @Contract(pure = true)
    private double getLinewidth() { return linewidth; }

    /**
     * @param linewidth the linewidth to set
     */
    private void setLinewidth(double linewidth) { this.linewidth = linewidth; }

    /**
     * @return the newdata
     */
    @Contract(pure = true)
    private boolean isNewdata() { return newdata; }

    /**
     * @param newdata the newdata to set
     */
    private void setNewdata(boolean newdata) { this.newdata = newdata; }

    /**
     * @return the animwidth
     */
    @Contract(pure = true)
    private double getAnimWidth() { return 750.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the animheight
     */
    @Contract(pure = true)
    private double getAnimHeight() { return 750.0 / Screen.getMainScreen().getRenderScale(); }

}
