package jms.randomwalk.execs;

import enums.DblSizes;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import jms.randomwalk.datahandling.Data;
import jms.randomwalk.ui.GetComponents;
import jms.randomwalk.plots.Execution;
import jms.randomwalk.plots.FXPlot;
import jms.randomwalk.scenes.SceneDiff;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 *
 * Class for executing and plotting Diffusion.
 */
public class ExecDiff extends Data {

    private String language;
    private List<Double> diffusionX;
    private List<Double> diffusionY;
    private List<Double> energyX;
    private List<Double> energyY;
    private List<Double> viscX;
    private List<Double> viscY;
    private HBox isovalikko;
    private Pane pane;
    private Canvas alusta;
    private double scalefactor;
    private double linewidth;
    private boolean newdata;

    /**
     * Initiating class.
     * @param language which ui language: finnish or english
     */
    public ExecDiff(String language) {
        super();
        this.setLanguage(language);
    }

    /**
     * Method for setting execute diffusion mouseclicked.
     * @param execNappi execute diffusion button
     * @param diffScene scene for diffusion
     * @param diffpiirturi GraphicsContext object
     * @param scalefactor scale factor
     * @param animwidth graphics width
     * @param animheight graphics height
     * @param newdata boolean if new run
     * @param isovalikkoDiff HBox object
     * @param valikkoDiff VBox object
     * @param diffpane Pane object
     * @param datapath data path
     * @param datafolder data folder
     * @param fexec fortran executable file
     * @param remBarNappiDiff barrier removal button
     * @param cancelNappiDiff cancel button
     * @param plotDiff plot button
     * @param closeNappiDiff close button
     * @param menuNappiDiff menu button
     * @param helpNappiDiff help button
     * @param diffAlusta Canvas object
     */
    public void setExecClick(Button execNappi, SceneDiff diffScene, GraphicsContext diffpiirturi,
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
            if (diffScene.timerIsRunning()) {
                return;
            }
            diffScene.setSave("-");
            String[] variables = diffScene.getVars();
            this.setVars(variables);
            int particles = Integer.parseInt(this.getVars()[1]);
            double diam = Double.parseDouble(this.getVars()[2]);
            int dim = Integer.parseInt(this.getVars()[4]);
            String lattice = this.getVars()[7];
            boolean fail = false;

            if (particles < 0) {
                fail = true;
            }
            if (diam <= 0.0 || diam >= 1.0) {
                fail = true;
            }
            if (dim < 2 || dim > 3) {
                fail = true;
            }
            if (!lattice.equals("l") && !lattice.equals("-")) {
                fail = true;
            }

            if (fail) {
                return;
            }

            if (diffScene.getFxplot() != null) {
                if (diffScene.getFxplot().getFrame().isShowing()
                    || diffScene.getFxplot().getFrame().isActive()
                    || diffScene.getFxplot().getFrame().isDisplayable()) {
                    diffScene.getFxplot().getFrame().dispose();
                }
            }

            GetComponents getComponents = new GetComponents();

            if (this.getIsoValikko().getChildren().size() > 1) {
                this.getIsoValikko().getChildren().remove(1);
                this.setPane(getComponents.getPane(this.getAlusta(), DblSizes.ANIMSIZE.getDblSize(), DblSizes.ANIMSIZE.getDblSize()));
                this.getIsoValikko().getChildren().add(this.getPane());
                helpNappiDiff.setText(this.getLanguage().equals("fin") ? "OHJE" : "HELP");
            }

            diffScene.setFxplot(new FXPlot());
            diffScene.getFxplot().setFXPlot(this.getLanguage(), "energy&diffusion");

            this.setEnergyX(new ArrayList<>());
            this.setEnergyY(new ArrayList<>());
            this.setDiffusionX(new ArrayList<>());
            this.setDiffusionY(new ArrayList<>());
            this.setViscX(new ArrayList<>());
            this.setViscY(new ArrayList<>());

            diffpiirturi.scale(1.0 / this.getScalefactor(), 1.0 / this.getScalefactor());

            double measure;
            double diff;
            if (particles < 25) {
                diff = 0.3;
                measure = 21.0;
            } else {
                measure = Math.round(3.0 * Math.sqrt(2.0 * (double) particles));
                double diff1 = Math.sqrt(measure) / 10.0 + measure / 20.0 - 0.7;
                double diff2 = measure / 20.0 - 0.7;

                if ((measure + 1.0) % 4.0 == 0.0) {
                    diff = diff1;
                    measure -= 1.0;
                } else if (measure % 4.0 == 0.0) {
                    diff = diff1;
                    measure -= 2.0;
                } else if (measure % 2.0 == 0.0) {
                    diff = diff1;
                    measure -= 0.0;
                } else {
                    diff = diff2;
                }
            }

            this.setScalefactor((animwidth - 83.3) / measure);
            if (dim == 2) {
                this.setLinewidth(1.0 / this.getScalefactor());
            } else {
                this.setLinewidth(diam / this.getScalefactor());
            }

            diffpiirturi.scale(this.getScalefactor(), this.getScalefactor());

            this.setNewdata(true);

            diffpiirturi.setGlobalAlpha(1.0);
            diffpiirturi.setFill(Color.BLACK);
            diffpiirturi.fillRect(0, 0, 1.0 / this.getScalefactor() * animwidth, 1.0 / this.getScalefactor() * animheight);
            diffpiirturi.fill();

            /*
             * GET INITIAL DATA
             */
            File initialDataFile = new File(datapath + "/startDiff_" + dim + "D_" + particles + "N.xy");
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
                if (!deleted) {
                    System.out.println(warntext);
                }
            }

            valikkoDiff.getChildren().set(3, remBarNappiDiff);
            valikkoDiff.getChildren().set(4, cancelNappiDiff);

            /*
             * DRAW DIFFUSION ANIMATION
             */
            diffScene.refresh(datafolder, initialDataFile, fexec, diffpiirturi, this.getScalefactor(),
                animwidth, this.getLinewidth(), remBarNappiDiff, cancelNappiDiff, execNappi, valikkoDiff,
                plotDiff, closeNappiDiff, menuNappiDiff, helpNappiDiff, this.getEnergyX(), this.getEnergyY(),
                this.getDiffusionX(), this.getDiffusionY(), this.getViscX(), this.getViscY(),
                this.isNewdata(), measure, diff
            );

            this.setNewdata(false);
        });
    }

    /**
     * Method for setting plot diffusion mouseclicked.
     * @param plotNappi plot button
     * @param diffScene scene for diffusion
     * @param valikkoDiff VBox object
     * @param datapath data path
     * @param datafolder data folder
     * @param fexec fortran executable file
     * @param pyexecdiff2d python executable file
     * @param pyexecdiff3d python executable file
     * @param ex instance of Execution class (package plots)
     */
    public void setPlotClick(Button plotNappi, SceneDiff diffScene, VBox valikkoDiff, String datapath,
        File datafolder, String fexec, String pyexecdiff2d, String pyexecdiff3d, Execution ex) {

        plotNappi.setOnMouseClicked((MouseEvent event) -> {
            diffScene.setSave("+");
            String[] variables = diffScene.getVars();
            this.setVars(variables);
            Data data = new Data(variables);
            int particles = Integer.parseInt(getVars()[1]);
            double diam = Double.parseDouble(getVars()[2]);
            int dim = Integer.parseInt(getVars()[4]);
            String lattice = this.getVars()[7];
            boolean fail = false;

            if (particles < 0) {
                fail = true;
            }
            if (diam <= 0.0 || diam >= 1.0) {
                fail = true;
            }
            if (dim < 2 || dim > 3) {
                fail = true;
            }
            if (!lattice.equals("l") && !lattice.equals("-")) {
                fail = true;
            }

            if (fail) {
                return;
            }

            valikkoDiff.setDisable(true);

            ex.executeDiff(datafolder, datapath, fexec, pyexecdiff2d,
                pyexecdiff3d, valikkoDiff, data, this.getVars());

            valikkoDiff.setDisable(false);
        });
    }

    /**
     * @return the language
     */
    private String getLanguage() {
        return this.language;
    }

    /**
     * @param language the language to set
     */
    private void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the vars
     */
    private String[] getVars() {
        return vars.clone();
    }

    /**
     * @param vars the vars to set
     */
    private void setVars(String[] vars) {
        this.vars = vars.clone();
    }

    /**
     * @return the isovalikko
     */
    private HBox getIsoValikko() {
        return this.isovalikko;
    }

    /**
     * @param isovalikko the isovalikko to set
     */
    private void setIsoValikko(HBox isovalikko) {
        this.isovalikko = isovalikko;
    }

    /**
     * @return the pane
     */
    private Pane getPane() {
        return this.pane;
    }

    /**
     * @param pane the pane to set
     */
    private void setPane(Pane pane) {
        this.pane = pane;
    }

    /**
     * @return the alusta
     */
    private Canvas getAlusta() {
        return this.alusta;
    }

    /**
     * @param alusta the alusta to set
     */
    private void setAlusta(Canvas alusta) {
        this.alusta = alusta;
    }

    /**
     * @return the diffusionX
     */
    private List<Double> getDiffusionX() {
        return this.diffusionX;
    }

    /**
     * @param diffusionX the diffusionX to set
     */
    private void setDiffusionX(List<Double> diffusionX) {
        this.diffusionX = diffusionX;
    }

    /**
     * @return the diffusionY
     */
    private List<Double> getDiffusionY() {
        return this.diffusionY;
    }

    /**
     * @param diffusionY the diffusionY to set
     */
    private void setDiffusionY(List<Double> diffusionY) {
        this.diffusionY = diffusionY;
    }

    /**
     * @return the energyX
     */
    private List<Double> getEnergyX() {
        return this.energyX;
    }

    /**
     * @param energyX the energyX to set
     */
    private void setEnergyX(List<Double> energyX) {
        this.energyX = energyX;
    }

    /**
     * @return the energyY
     */
    private List<Double> getEnergyY() {
        return this.energyY;
    }

    /**
     * @param energyY the energyY to set
     */
    private void setEnergyY(List<Double> energyY) {
        this.energyY = energyY;
    }

    /**
     * @return the viscX
     */
    private List<Double> getViscX() {
        return this.viscX;
    }

    /**
     * @param viscX the viscX to set
     */
    private void setViscX(List<Double> viscX) {
        this.viscX = viscX;
    }

    /**
     * @return the viscY
     */
    private List<Double> getViscY() {
        return this.viscY;
    }

    /**
     * @param viscY the viscY to set
     */
    private void setViscY(List<Double> viscY) {
        this.viscY = viscY;
    }

    /**
     * @return the scalefactor
     */
    private double getScalefactor() {
        return scalefactor;
    }

    /**
     * @param scalefactor the scalefactor to set
     */
    private void setScalefactor(double scalefactor) {
        this.scalefactor = scalefactor;
    }

    /**
     * @return the linewidth
     */
    private double getLinewidth() {
        return linewidth;
    }

    /**
     * @param linewidth the linewidth to set
     */
    private void setLinewidth(double linewidth) {
        this.linewidth = linewidth;
    }

    /**
     * @return the newdata
     */
    private boolean isNewdata() {
        return newdata;
    }

    /**
     * @param newdata the newdata to set
     */
    private void setNewdata(boolean newdata) {
        this.newdata = newdata;
    }

}
