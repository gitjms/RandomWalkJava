package randomwalkjava;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
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

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 *
 * Class for executing and plotting MMC Diffusion
 */
class ExecMMC extends Data {

    private String language;
    private List <Double> diffusion_x;
    private List <Double> diffusion_y;
    private List <Double> energy_x;
    private List <Double> energy_y;
    private List <Double> visc_x;
    private List <Double> visc_y;
    private double mmcscalefactor;
    private double linewidth;
    private boolean newdata;

    /**
     * Initiating class
     */
    ExecMMC(String language) {
        super();
        this.setLanguage(language);
    }

    void setExecClick(@NotNull Button execNappi, SceneMMC mmcScene,
                      GraphicsContext mmcpiirturi, double scalefactor, double animwidth, double animheight,
                      boolean newdata, HBox isovalikkoMMC, VBox valikkoMMC, TextArea textAreaMMC, Pane mmcpane,
                      String datapath, File datafolder, String fexec, Button remBarNappiMMC, Button plotMMC,
                      Button closeNappiMMC, Button menuNappiMMC, Button helpNappiMMC) {

        this.setMmcScalefactor(scalefactor);
        this.setNewdata(newdata);

        execNappi.setOnMouseClicked((MouseEvent event) -> {
            if ( mmcScene.timerIsRunning()) return;
            mmcScene.setSave("-");
            String[] vars = mmcScene.getVars();
            this.setVars(vars);
            int particles = parseInt(getVars()[0]);
            double diam = parseDouble(getVars()[1]);
            int charge = parseInt(getVars()[2]);
            int dim = parseInt(getVars()[4]);
            String lattice = this.getVars()[7];
            boolean fail = false;

            if ( particles < 0 ) fail = true;
            if ( diam <= 0.0 || diam >= 1.0 ) fail = true;
            if ( charge < 1 || charge > 2 ) fail = true;
            if ( dim < 2 || dim > 3 ) fail = true;
            if ( !lattice.equals("l") && !lattice.equals("-") ) fail = true;

            if ( fail ) return;

            if (mmcScene.getFxplot() != null) {
                if (mmcScene.getFxplot().getFrame().isShowing()
                    || mmcScene.getFxplot().getFrame().isActive()
                    || mmcScene.getFxplot().getFrame().isDisplayable())
                    mmcScene.getFxplot().getFrame().dispose();
            }

            mmcScene.setFxplot( new FXPlot());
            mmcScene.getFxplot().setFXPlot(this.getLanguage(),"energy&diffusion");

            this.setEnergy_x(new ArrayList<>());
            this.setEnergy_y(new ArrayList<>());
            this.setDiffusion_x(new ArrayList<>());
            this.setDiffusion_y(new ArrayList<>());
            this.setVisc_x(new ArrayList<>());
            this.setVisc_y(new ArrayList<>());

            mmcpiirturi.scale(1.0/this.getMmcScalefactor(), 1.0/this.getMmcScalefactor());

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
                } else {
                    diff = diff2;
                }
            }

            this.setMmcScalefactor((animwidth - 83.3) / measure);
            if ( dim == 2 ) this.setLinewidth(1.0 / this.getMmcScalefactor());
            else this.setLinewidth(diam / this.getMmcScalefactor());

            mmcpiirturi.scale(this.getMmcScalefactor(), this.getMmcScalefactor());

            this.setNewdata(true);

            if ( isovalikkoMMC.getChildren().contains(textAreaMMC)) {
                textAreaMMC.clear();
                isovalikkoMMC.getChildren().remove(textAreaMMC);
                isovalikkoMMC.getChildren().add(mmcpane);
            }

            mmcpiirturi.setGlobalAlpha(1.0);
            mmcpiirturi.setFill(Color.BLACK);
            mmcpiirturi.fillRect(0, 0, 1.0/this.getMmcScalefactor()*animwidth, 1.0/this.getMmcScalefactor()*animheight);
            mmcpiirturi.fill();

            /*
             * GET INITIAL DATA
             */
            File initialDataFile = new File( datapath + "/startMMC_" + dim + "D_" + particles + "N.xy");
            boolean deleted;
            if (Files.exists(initialDataFile.toPath())) {
                deleted = initialDataFile.delete();
                String warntext = this.getLanguage().equals("fin")
                    ? "Vanhaa datatiedostoa 'startMMC_" + dim + "D_" + particles + "N.xy' ei voitu"
                    + " poistaa, mikä voi pilata ajon.\n"
                    + "Yritä sulkea sovellus, poistaa tiedosto käsin, ja sitten ajaa uudestaan."
                    : "Old data file 'startMMC_" + dim + "D_" + particles + "N.xy' couldn't be"
                    + " deleted, which may spoil your run.\n"
                    + "Try closing the application, delete the file manually, and then run again.";
                if (!deleted) System.out.println(warntext);
            }

            valikkoMMC.getChildren().set(3, remBarNappiMMC);

            /*
             * DRAW MMC ANIMATION
             */
            mmcScene.refresh(datafolder, initialDataFile, fexec, mmcpiirturi, this.getMmcScalefactor(),
                animwidth, this.getLinewidth(), remBarNappiMMC, execNappi, valikkoMMC,
                plotMMC, closeNappiMMC, menuNappiMMC, helpNappiMMC, this.getEnergy_x(), this.getEnergy_y(),
                this.getDiffusion_x(), this.getDiffusion_y(), this.getVisc_x(), this.getVisc_y(),
                this.isNewdata(), measure, diff
            );

            this.setNewdata(false);
        });
    }

    void setPlotClick (@NotNull Button plotNappi, SceneMMC mmcScene, VBox valikkoMMC, String datapath,
                       File datafolder, String fexec, String pyexecmmc2d, String pyexecmmc3d, Execution ex) {

        plotNappi.setOnMouseClicked((MouseEvent event) -> {
            valikkoMMC.setDisable(true);
            mmcScene.setSave("+");
            String[] vars = mmcScene.getVars();
            this.setVars(vars);
            Data data = new Data(vars);
            int particles = parseInt(getVars()[0]);
            double diam = parseDouble(getVars()[1]);
            int charge = parseInt(getVars()[2]);
            int dim = parseInt(getVars()[4]);
            String lattice = this.getVars()[7];
            boolean fail = false;

            if ( particles < 0 ) fail = true;
            if ( diam <= 0.0 || diam >= 1.0 ) fail = true;
            if ( charge < 1 || charge > 2 ) fail = true;
            if ( dim < 2 || dim > 3 ) fail = true;
            if ( !lattice.equals("l") && !lattice.equals("-") ) fail = true;

            if ( fail ) return;

            ex.executeMMC(datafolder, datapath, fexec, pyexecmmc2d,
                pyexecmmc3d, valikkoMMC, data, this.getVars());
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
     * @return the mmcscalefactor
     */
    @Contract(pure = true)
    private double getMmcScalefactor() { return mmcscalefactor; }

    /**
     * @param mmcscalefactor the mmcscalefactor to set
     */
    private void setMmcScalefactor(double mmcscalefactor) { this.mmcscalefactor = mmcscalefactor; }

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

}
