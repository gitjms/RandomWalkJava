package randomwalkjava;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 *
 * Class for executing and plotting Real Time SAW
 */
class ExecSAW extends Data {

    private String language;
    private boolean issaw;
    private boolean iscbmc;
    private boolean first;
    private List <Double> saw_rms;
    private List<Double> saw_rmsruns;
    private List <Double> saw_lengths;
    private List<Double> saw_expd;
    private List <Double> rms_runs;
    private List <Integer> xAxis;
    private List <Integer> xhistAxis;
    private List <Double> yhistAxis;

    /**
     * Initiating class
     */
    ExecSAW(String language) {
        super();
        this.setLanguage(language);
    }

    void setSawClick(File folder, String executable, @NotNull Button execSAW, @NotNull Button execBMC, SceneRealTimeSaw sawScene,
                     HBox isovalikkoSaw, Pane sawPane, TextArea sawText, Button plotSAW, Button closeNappiSAW,
                     Button menuNappiSAW, Button helpNappiSAW, Slider gamSlider, Slider aaSlider) {

        new AnimationTimer() {
            private long prevTime = 0;

            @Override
            public void handle(long currentNanoTime) {

                /*
                 * REFESH ANIMATION IN ABOUT 100 MILLISECOND STEPS
                 */
                long sleepNanoseconds = 100 * 1000000;
                if ((currentNanoTime - this.prevTime) < sleepNanoseconds) {
                    return;
                }

                if ( isovalikkoSaw.getChildren().contains(sawText)) {
                    sawText.clear();
                    isovalikkoSaw.getChildren().remove(sawText);
                    isovalikkoSaw.getChildren().add(sawPane);
                }

                if ( !sawScene.isRunning())
                    return;

                sawScene.refresh(folder, executable, getFirst(), getSawLengths(), getSawExpd(), getSawRms(),
                    getSawRmsRuns(), getRmsRuns(), getXAxis(), getXhistAxis(), isSaw(), gamSlider, aaSlider);

                if (getFirst()) setFirst(false);

                this.prevTime = currentNanoTime;
            }
        }.start();

        execSAW.setOnMouseClicked((MouseEvent event) -> {
            if (sawScene.isRunning()) {
                sawScene.stop();
                sawScene.getDimension().setDisable(false);
                menuNappiSAW.setDisable(false);
                helpNappiSAW.setDisable(false);
                closeNappiSAW.setDisable(false);
                plotSAW.setDisable(false);
                execSAW.setText(this.getLanguage().equals("fin") ? "UUSI AJO" : "NEW RUN");
            } else {
                this.setVars(sawScene.getVars());
                sawScene.setSawCbmc("-");
                sawScene.setSave("-");
                boolean fail = false;

                int steps = parseInt(this.getVars()[3]);
                if (steps > 0) fail = true;

                int dim = parseInt(this.getVars()[4]);

                if (dim < 2 || dim > 3) fail = true;

                if (fail) return;

                if (sawScene.getFxplot() != null) {
                    if (sawScene.getFxplot().getFrame().isShowing()
                        || sawScene.getFxplot().getFrame().isActive()
                        || sawScene.getFxplot().getFrame().isDisplayable())
                        sawScene.getFxplot().getFrame().dispose();
                }

                this.setIsSaw(true);

                sawScene.setFxplot(new FXPlot());
                sawScene.getFxplot().setFXPlot(this.getLanguage(), "saw");

                this.setSawRms(new ArrayList<>());

                this.setRmsRuns(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getRmsRuns().add(0.0);

                this.setSawRmsRuns(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getSawRmsRuns().add(0.0);

                this.setSawLengths(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getSawLengths().add(0.0);

                this.setSawExpd(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getSawExpd().add(0.0);

                this.setXAxis(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getXAxis().add(x);

                this.setXhistAxis(new ArrayList<>());
                for (int x = 0; x < 20; x++) this.getXhistAxis().add(x);
                this.setYhistAxis(new ArrayList<>());
                for (int x = 0; x < 20; x++) this.getYhistAxis().add(0.0);

                this.setFirst(true);

                Map<Object, Object> labelMap = new HashMap<>();
                if (dim == 2) for (int i = 0; i < 20; i++) labelMap.put(i, String.valueOf((i+1)*3));
                else for (int i = 0; i < 20; i++) labelMap.put(i, String.valueOf((i+1)*10));

                sawScene.getFxplot().setS1Data(this.getXAxis(), this.getRmsRuns(), dim);
                sawScene.getFxplot().setS2Data(this.getXAxis(), this.getRmsRuns());
                sawScene.getFxplot().setS3Data(labelMap, this.getXhistAxis(), this.getYhistAxis());

                sawScene.start();

                execSAW.setText(this.getLanguage().equals("fin") ? "SEIS" : "STOP");
                menuNappiSAW.setDisable(true);
                helpNappiSAW.setDisable(true);
                closeNappiSAW.setDisable(true);
                plotSAW.setDisable(true);
                execBMC.setDisable(true);
                sawScene.getDimension().setDisable(true);
            }
        });

        execBMC.setOnMouseClicked((MouseEvent event) -> {
            if (sawScene.isRunning()) {
                sawScene.stop();
                sawScene.getDimension().setDisable(false);
                menuNappiSAW.setDisable(false);
                helpNappiSAW.setDisable(false);
                closeNappiSAW.setDisable(false);
                plotSAW.setDisable(false);
                execBMC.setText(this.getLanguage().equals("fin") ? "UUSI AJO" : "NEW RUN");
            } else {
                this.setVars(sawScene.getVars());
                sawScene.setSawCbmc("c");
                sawScene.setSave("-");
                boolean fail = false;

                int steps = parseInt(this.getVars()[3]);
                if (this.getVars()[3].isEmpty() || steps == 0) fail = true;

                int dim = parseInt(this.getVars()[4]);

                if (dim < 2 || dim > 3) fail = true;

                if (fail) return;

                if (sawScene.getFxplot() != null) {
                    if (sawScene.getFxplot().getFrame().isShowing()
                        || sawScene.getFxplot().getFrame().isActive()
                        || sawScene.getFxplot().getFrame().isDisplayable())
                        sawScene.getFxplot().getFrame().dispose();
                }

                this.setIsSaw(false);

                sawScene.setFxplot(new FXPlot());
                sawScene.getFxplot().setFXPlot(this.getLanguage(), "saw");

                this.setSawRms(new ArrayList<>());

                this.setRmsRuns(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getRmsRuns().add(0.0);

                this.setSawRmsRuns(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getSawRmsRuns().add(0.0);

                this.setSawLengths(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getSawLengths().add(0.0);

                this.setSawExpd(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getSawExpd().add(0.0);

                this.setXAxis(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getXAxis().add(x);

                this.setXhistAxis(new ArrayList<>());
                for (int x = 0; x < 20; x++) this.getXhistAxis().add(x);
                this.setYhistAxis(new ArrayList<>());
                for (int x = 0; x < 20; x++) this.getYhistAxis().add(0.0);

                this.setFirst(true);

                Map<Object, Object> labelMap = new HashMap<>();
                if (dim == 2) for (int i = 0; i < 20; i++) labelMap.put(i, String.valueOf((i+1)));
                else for (int i = 0; i < 20; i++) labelMap.put(i, String.valueOf((i+1)*3));

                sawScene.getFxplot().setS1Data(this.getXAxis(), this.getRmsRuns(), dim);
                sawScene.getFxplot().setS2Data(this.getXAxis(), this.getRmsRuns());
                sawScene.getFxplot().setS3Data(labelMap, this.getXhistAxis(), this.getYhistAxis());

                sawScene.start();

                execBMC.setText(this.getLanguage().equals("fin") ? "SEIS" : "STOP");
                menuNappiSAW.setDisable(true);
                helpNappiSAW.setDisable(true);
                closeNappiSAW.setDisable(true);
                plotSAW.setDisable(true);
                execSAW.setDisable(true);
                sawScene.getDimension().setDisable(true);
            }
        });
    }

        void setPlotClick (@NotNull Button plotNappi, @NotNull Button runSAW, SceneRealTimeSaw sawScene, VBox valikkoSAW, String datapath,
                           File datafolder, String fexec, String pyexecsaw2d, String pyexecsaw3d, Execution ex){

            plotNappi.setOnMouseClicked((MouseEvent event) -> {
                if (runSAW.isDisabled()) {
                    sawScene.setSawCbmc("c");
                    this.setIsCbmc(true);
                } else {
                    sawScene.setSawCbmc("-");
                    this.setIsCbmc(false);
                }
                valikkoSAW.setDisable(true);
                sawScene.setSave("s");
                String[] vars = sawScene.getVars();
                this.setVars(vars);
                Data data = new Data(vars);
                int dim = parseInt(getVars()[4]);
                boolean fail = false;

                if ( dim < 2 || dim > 3 ) fail = true;
                if ( fail ) return;

                boolean ok = false;
                while (!ok) {
                    ok = ex.executeSAW(datafolder, datapath, fexec, pyexecsaw2d,
                        pyexecsaw3d, valikkoSAW, data, this.getVars(), this.isCbmc());
                }
                valikkoSAW.setDisable(false);
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
     * @return the issaw
     */
    @Contract(pure = true)
    private boolean isSaw() { return this.issaw; }

    /**
     * @param issaw the issaw to set
     */
    private void setIsSaw(boolean issaw) { this.issaw = issaw; }

    /**
     * @return the iscbmc
     */
    @Contract(pure = true)
    private boolean isCbmc() { return this.iscbmc; }

    /**
     * @param iscbmc the iscbmc to set
     */
    private void setIsCbmc(boolean iscbmc) { this.iscbmc = iscbmc; }

    /**
     * @return the vars
     */
    private String[] getVars() { return vars.clone(); }

    /**
     * @param vars the vars to set
     */
    private void setVars(@NotNull String[] vars) { this.vars = vars.clone(); }

    /**
     * @return the saw_lengths
     */
    @Contract(pure = true)
    private List<Double> getSawLengths() { return this.saw_lengths; }

    /**
     * @param saw_lengths the saw_lengths to set
     */
    private void setSawLengths(List<Double> saw_lengths) { this.saw_lengths = saw_lengths; }

    /**
     * @return the saw_expd
     */
    @Contract(pure = true)
    private List<Double> getSawExpd() { return this.saw_expd; }

    /**
     * @param saw_expd the saw_expd to set
     */
    private void setSawExpd(List<Double> saw_expd) { this.saw_expd = saw_expd; }

    /**
     * @return the saw_rms
     */
    @Contract(pure = true)
    private List <Double> getSawRms() { return this.saw_rms; }

    /**
     * @param saw_rms the saw_rms to set
     */
    private void setSawRms(List<Double> saw_rms) { this.saw_rms = saw_rms; }

    /**
     * @return the saw_rmsruns
     */
    @Contract(pure = true)
    private List <Double> getSawRmsRuns() { return this.saw_rmsruns; }

    /**
     * @param saw_rmsruns the saw_rmsruns to set
     */
    private void setSawRmsRuns(List<Double> saw_rmsruns) { this.saw_rmsruns = saw_rmsruns; }

    /**
     * @return the rms_runs
     */
    @Contract(pure = true)
    private List <Double> getRmsRuns() { return this.rms_runs; }

    /**
     * @param rms_runs the rms_runs to set
     */
    private void setRmsRuns(List<Double> rms_runs) { this.rms_runs = rms_runs; }

    /**
     * @return the xAxis
     */
    @Contract(pure = true)
    private List <Integer> getXAxis() { return this.xAxis; }

    /**
     * @param xAxis the xAxis to set
     */
    private void setXAxis(List<Integer> xAxis) { this.xAxis = xAxis; }

    /**
     * @return the yhistAxis
     */
    @Contract(pure = true)
    private List <Double> getYhistAxis() { return this.yhistAxis; }

    /**
     * @param yhistAxis the yhistAxis to set
     */
    private void setYhistAxis(List<Double> yhistAxis) { this.yhistAxis = yhistAxis; }

    /**
     * @param xhistAxis x-axis data array for walk plot to set
     */
    private void setXhistAxis(List<Integer> xhistAxis) { this.xhistAxis = xhistAxis; }

    /**
     * @return x-axis data array for walk plot
     */
    @Contract(pure = true)
    private List<Integer> getXhistAxis() { return this.xhistAxis; }

    /**
     * @return the first
     */
    @Contract(pure = true)
    private boolean getFirst() { return first; }

    /**
     *  the first to set
     */
    private void setFirst(boolean first) { this.first = first; }
}
