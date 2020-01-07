package randomwalkjava;

import javafx.animation.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 *
 * Class for executing and plotting Real Time SAW
 */
class ExecSAW extends Data {

    private String language;
    private boolean issaw;
    private boolean iseff;
    private boolean ismcsaw;
    private boolean first;
    private List <Double> saw_expd;
    private List <Double> saw_rms;
    private List <Double> expd_runs;
    private List<Double> rms_runs;
    private List <Double> eff_runs;
    private List <Double> succ_runs;
    private List <Double> saw_lengths;
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

    void setSawClick(File folder, String executable, @NotNull Button execSAW, @NotNull Button execMCSAW,
                     @NotNull Button runEFF, SceneRealTimeSaw sawScene, HBox isovalikkoSaw, Pane sawPane,
                     TextArea sawText, Button plotSAW, Button closeNappiSAW, Button menuNappiSAW,
                     Button helpNappiSAW, Slider aaSlider) {

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
                    getExpdRuns(), getRmsRuns(), null, null, getXAxis(), getXhistAxis(), isSaw(), isEff(), aaSlider);

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
                execSAW.setText(this.getLanguage().equals("fin") ? "AJA SAW" : "RUN SAW");
                runEFF.setDisable(false);
            } else {
                sawScene.setSawMc("E");
                sawScene.setSawPlot("-");
                sawScene.setSave("-");
                this.setVars(sawScene.getVars());
                boolean fail = false;

                int steps = Integer.parseInt(this.getVars()[3]);
                if (steps > 0) fail = true;

                int dim = Integer.parseInt(this.getVars()[4]);

                if (dim < 2 || dim > 3) fail = true;

                if (fail) return;

                if (sawScene.getFxplot() != null) {
                    if (sawScene.getFxplot().getFrame().isShowing()
                        || sawScene.getFxplot().getFrame().isActive()
                        || sawScene.getFxplot().getFrame().isDisplayable())
                        sawScene.getFxplot().getFrame().dispose();
                }

                this.setIsSaw(true);
                this.setIsEff(false);

                sawScene.setFxplot(new FXPlot());
                sawScene.getFxplot().setFXPlot(this.getLanguage(), "saw");

                this.setSawExpd(new ArrayList<>());
                this.setSawRms(new ArrayList<>());

                this.setExpdRuns(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getExpdRuns().add(0.0);

                this.setRmsRuns(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getRmsRuns().add(0.0);

                this.setSawLengths(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getSawLengths().add(0.0);

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

                sawScene.getFxplot().setS1Data(this.getXAxis(), this.getExpdRuns(), dim);
                sawScene.getFxplot().setS2Data(this.getXAxis(), this.getRmsRuns());
                sawScene.getFxplot().setS3Data(labelMap, this.getXhistAxis(), this.getYhistAxis());

                sawScene.start();

                execSAW.setText(this.getLanguage().equals("fin") ? "SEIS" : "STOP");
                menuNappiSAW.setDisable(true);
                helpNappiSAW.setDisable(true);
                closeNappiSAW.setDisable(true);
                plotSAW.setDisable(true);
                execMCSAW.setDisable(true);
                sawScene.getDimension().setDisable(true);
                runEFF.setDisable(true);
            }
        });

        execMCSAW.setOnMouseClicked((MouseEvent event) -> {
            if (sawScene.isRunning()) {
                sawScene.stop();
                sawScene.getDimension().setDisable(false);
                menuNappiSAW.setDisable(false);
                helpNappiSAW.setDisable(false);
                closeNappiSAW.setDisable(false);
                plotSAW.setDisable(false);
                execMCSAW.setText(this.getLanguage().equals("fin") ? "AJA MC" : "RUN MC");
                runEFF.setDisable(false);
            } else {
                sawScene.setSawMc("F");
                sawScene.setSawPlot("-");
                sawScene.setSave("-");
                this.setVars(sawScene.getVars());
                boolean fail = false;

                int steps = Integer.parseInt(this.getVars()[3]);
                if (this.getVars()[3].isEmpty() || steps == 0) fail = true;

                int dim = Integer.parseInt(this.getVars()[4]);

                if (dim < 2 || dim > 3) fail = true;

                if (fail) return;

                if (sawScene.getFxplot() != null) {
                    if (sawScene.getFxplot().getFrame().isShowing()
                        || sawScene.getFxplot().getFrame().isActive()
                        || sawScene.getFxplot().getFrame().isDisplayable())
                        sawScene.getFxplot().getFrame().dispose();
                }

                this.setIsSaw(false);
                this.setIsEff(false);

                sawScene.setFxplot(new FXPlot());
                sawScene.getFxplot().setFXPlot(this.getLanguage(), "saw");

                this.setSawExpd(new ArrayList<>());
                this.setSawRms(new ArrayList<>());

                this.setExpdRuns(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getExpdRuns().add(0.0);

                this.setRmsRuns(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getRmsRuns().add(0.0);

                this.setSawLengths(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getSawLengths().add(0.0);

                this.setXAxis(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getXAxis().add(x);

                this.setXhistAxis(new ArrayList<>());
                for (int x = 0; x < 20; x++) this.getXhistAxis().add(x);
                this.setYhistAxis(new ArrayList<>());
                for (int x = 0; x < 20; x++) this.getYhistAxis().add(0.0);

                this.setFirst(true);

                Map<Object, Object> labelMap = new HashMap<>();
                if (steps < 30) for (int i = 0; i < 20; i++) labelMap.put(i, String.valueOf((i+1)));
                else if (steps < 500) for (int i = 0; i < 20; i++) labelMap.put(i, String.valueOf((i+1)*3));
                else for (int i = 0; i < 20; i++) labelMap.put(i, String.valueOf((i+1)*10));
                if (steps < 30) {
                    for (int i = 0; i < 20; i++) labelMap.put(i, String.valueOf((i+1)));
                } else if (steps < 200) {
                    for (int i = 0; i < 20; i++) labelMap.put(i, String.valueOf((i+1)*3));
                } else if (steps < 400) {
                    for (int i = 0; i < 20; i++) labelMap.put(i, String.valueOf((i+1)*10));
                } else {
                    for (int i = 0; i < 20; i++) labelMap.put(i, String.valueOf((i+1)*20));
                }

                sawScene.getFxplot().setS1Data(this.getXAxis(), this.getExpdRuns(), dim);
                sawScene.getFxplot().setS2Data(this.getXAxis(), this.getRmsRuns());
                sawScene.getFxplot().setS3Data(labelMap, this.getXhistAxis(), this.getYhistAxis());

                sawScene.start();

                execMCSAW.setText(this.getLanguage().equals("fin") ? "SEIS" : "STOP");
                menuNappiSAW.setDisable(true);
                helpNappiSAW.setDisable(true);
                closeNappiSAW.setDisable(true);
                plotSAW.setDisable(true);
                execSAW.setDisable(true);
                sawScene.getDimension().setDisable(true);
                runEFF.setDisable(true);
            }
        });
    }

    /**
     * method for checking if user input in GUI is an integer
     * @param str GUI input string
     * @return true if input is an integer, false otherwise
     */
    private static boolean isNumInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    void setPlotClick (@NotNull Button plotNappi, @NotNull Button execSAW, SceneRealTimeSaw sawScene, VBox valikkoSAW,
                       String datapath, File datafolder, String fexec, String pyexecsaw2d, String pyexecsaw3d,
                       Execution ex, TextField setMax, @NotNull TextFlow result){

        Label redlabel = new Label(this.getLanguage().equals("fin") ? "Ei tulosta" : "No result");
        redlabel.setTextFill(Color.RED);
        redlabel.setStyle("-fx-font-size: 20px;");

        Label greenlabel = new Label();
        greenlabel.setTextFill(Color.GREEN);
        greenlabel.setStyle("-fx-font-size: 20px;");

        FadeTransition fadegreen = new FadeTransition(Duration.seconds(0.2), greenlabel);
        FadeTransition fadered = new FadeTransition(Duration.seconds(0.2), redlabel);
        fadegreen.setFromValue(0.0);
        fadegreen.setToValue(1.0);
        fadegreen.setCycleCount(2);
        fadered.setFromValue(0.0);
        fadered.setToValue(1.0);
        fadered.setCycleCount(2);
        String txt1 = this.getLanguage().equals("fin") ? " ajo" : " run";
        String txt2 = this.getLanguage().equals("fin") ? " ajoa" : " runs";
        result.setTextAlignment(TextAlignment.CENTER);

        plotNappi.setOnMouseClicked((MouseEvent event) -> {
            result.getChildren().clear();
            if (execSAW.isDisabled()) {
                sawScene.setSawMc("F");
                this.setIsMcsaw(true);
            } else {
                sawScene.setSawMc("E");
                this.setIsMcsaw(false);
            }
            sawScene.setSawPlot("p");
            sawScene.setSave("s");
            sawScene.setSawMcEff("-");
            String[] vars = sawScene.getVars();
            this.setVars(vars);
            Data data = new Data(vars);
            int dim = Integer.parseInt(getVars()[4]);
            boolean fail = false;

            if ( dim < 2 || dim > 3 ) fail = true;
            if ( fail ) return;

            valikkoSAW.setDisable(true);
            this.setIsEff(false);

            int count = 0;
            int max = 100;
            if (isNumInteger(setMax.getText().trim())){
                max = Integer.parseInt(setMax.getText());
            }

            boolean ok = false;
            while (!ok && count < max) {
                ok = ex.executeSAW(datafolder, datapath, fexec, pyexecsaw2d,
                    pyexecsaw3d, valikkoSAW, data, this.getVars(), this.isMcsaw());
                count += 1;
            }

            if(!ok) {
                result.setVisible(true);
                result.getChildren().add(redlabel);
                fadered.play();
            } else {
                result.setVisible(true);
                greenlabel.setText(count + (count == 1 ? txt1 : txt2));
                result.getChildren().add(greenlabel);
                fadegreen.play();
            }
            valikkoSAW.setDisable(false);
        });
    }

    void setEffClick (File folder, String executable, @NotNull Button runEFF, @NotNull Button execSAW,
                      @NotNull Button execMCSAW, @NotNull Button plotNappi, SceneRealTimeSaw sawScene,
                      VBox valikkoSAW, @NotNull TextField setEff, @NotNull TextFlow resultEff, Slider aaSlider){

        setEff.setOnKeyReleased(e -> {
            if (isNumInteger(setEff.getText().trim())){
                execSAW.setDisable(true);
                execMCSAW.setDisable(true);
                plotNappi.setDisable(true);
                this.setIsEff(true);
            } else {
                execSAW.setDisable(false);
                execMCSAW.setDisable(false);
                plotNappi.setDisable(false);
                this.setIsEff(false);
            }
        });

        runEFF.setOnMouseClicked((MouseEvent event) -> {
            resultEff.getChildren().clear();
            sawScene.setSawMc("F");
            sawScene.setSawPlot("-");
            sawScene.setSave("-");
            String[] vars = sawScene.getVars();
            this.setVars(vars);
            int dim = Integer.parseInt(getVars()[4]);
            boolean fail = false;

            if ( dim < 2 || dim > 3 ) fail = true;
            if (fail) return;

            if (sawScene.getFxplot() != null) {
                if (sawScene.getFxplot().getFrame().isShowing()
                    || sawScene.getFxplot().getFrame().isActive()
                    || sawScene.getFxplot().getFrame().isDisplayable())
                    sawScene.getFxplot().getFrame().dispose();
            }
            sawScene.setFxplot(new FXPlot());
            sawScene.getFxplot().setFXPlot(this.getLanguage(), "eff");

            this.setIsEff(true);
            this.setIsSaw(false);
            valikkoSAW.setDisable(true);
            this.setFirst(true);

            int max_runs;
            if (isNumInteger(setEff.getText().trim())) {
                max_runs = Integer.parseInt(setEff.getText());

                this.setEffRuns(new ArrayList<>());
                for (int x = 0; x < max_runs; x++) this.getEffRuns().add(null);
                this.setSuccRuns(new ArrayList<>());
                for (int x = 0; x < max_runs; x++) this.getSuccRuns().add(null);
                this.setXAxis(new ArrayList<>());
                for (int x = 0; x < max_runs; x++) this.getXAxis().add(x);

                sawScene.getFxplot().setFData(this.getXAxis(), this.getEffRuns(), max_runs);

                for (int i = 1; i <= max_runs; i ++) {
                    sawScene.setSawMcEff(String.valueOf(i));

                    sawScene.refresh(folder, executable, this.getFirst(), null, null, null,
                        null, null, this.getEffRuns(), this.getSuccRuns(), this.getXAxis(),
                        null, this.isSaw(), this.isEff(), aaSlider);

                    if (this.getFirst()) this.setFirst(false);
                }
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
     * @return the ismcsaw
     */
    @Contract(pure = true)
    private boolean isMcsaw() { return this.ismcsaw; }

    /**
     * @param ismcsaw the ismcsaw to set
     */
    private void setIsMcsaw(boolean ismcsaw) { this.ismcsaw = ismcsaw; }

    /**
     * @return the iseff
     */
    @Contract(pure = true)
    private boolean isEff() { return this.iseff; }

    /**
     * @param iseff the iseff to set
     */
    private void setIsEff(boolean iseff) { this.iseff = iseff; }

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
    private List <Double> getSawExpd() { return this.saw_expd; }

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
     * @return the expd_runs
     */
    @Contract(pure = true)
    private List <Double> getExpdRuns() { return this.expd_runs; }

    /**
     * @param expd_runs the expd_runs to set
     */
    private void setExpdRuns(List<Double> expd_runs) { this.expd_runs = expd_runs; }

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
     * @return the eff_runs
     */
    @Contract(pure = true)
    private List <Double> getEffRuns() { return this.eff_runs; }

    /**
     * @param eff_runs the eff_runs to set
     */
    private void setEffRuns(List<Double> eff_runs) { this.eff_runs = eff_runs; }

    /**
     * @return the succ_runs
     */
    @Contract(pure = true)
    private List <Double> getSuccRuns() { return this.succ_runs; }

    /**
     * @param succ_runs the succ_runs to set
     */
    private void setSuccRuns(List<Double> succ_runs) { this.succ_runs = succ_runs; }

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
