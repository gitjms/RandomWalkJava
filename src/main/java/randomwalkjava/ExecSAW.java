package randomwalkjava;

import javafx.animation.AnimationTimer;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private List <Double> saw_expd;
    private List <Double> saw_rms;
    private List <Double> expd_runs;
    private List<Double> rms_runs;
    private List <Double> saw_lengths;
    private List<Double> cbmc_mu;
    private List<Double> cbmc_mu2;
    private List <Integer> xAxis;
    private List <Integer> xhistAxis;
    private List <Double> yhistAxis;
    private int prevAlign;

    /**
     * Initiating class
     */
    ExecSAW(String language) {
        super();
        this.setLanguage(language);
    }

    void setSawClick(File folder, String executable, @NotNull Button execSAW, @NotNull Button execCBMC,
                     SceneRealTimeSaw sawScene, HBox isovalikkoSaw, Pane sawPane, TextArea sawText,
                     Button plotSAW, Button closeNappiSAW, Button menuNappiSAW, Button helpNappiSAW,
                     Slider ceeSlider) {

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

                sawScene.refresh(folder, executable, getFirst(), getSawLengths(), getCbmcMu(), getCbmcMu2(), getSawExpd(), getSawRms(),
                    getExpdRuns(), getRmsRuns(), getXAxis(), getXhistAxis(), isSaw(), ceeSlider);

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
            } else {
                sawScene.setSawCbmc("E");
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
                sawScene.getFxplot().setS2Data(this.getXAxis(), this.getRmsRuns(), false);
                sawScene.getFxplot().setS3Data(labelMap, this.getXhistAxis(), this.getYhistAxis());

                sawScene.start();

                execSAW.setText(this.getLanguage().equals("fin") ? "SEIS" : "STOP");
                menuNappiSAW.setDisable(true);
                helpNappiSAW.setDisable(true);
                closeNappiSAW.setDisable(true);
                plotSAW.setDisable(true);
                execCBMC.setDisable(true);
                sawScene.getDimension().setDisable(true);
            }
        });

        execCBMC.setOnMouseClicked((MouseEvent event) -> {
            if (sawScene.isRunning()) {
                sawScene.stop();
                sawScene.getDimension().setDisable(false);
                menuNappiSAW.setDisable(false);
                helpNappiSAW.setDisable(false);
                closeNappiSAW.setDisable(false);
                plotSAW.setDisable(false);
                execCBMC.setText(this.getLanguage().equals("fin") ? "AJA CBMC" : "RUN CBMC");
            } else {
                sawScene.setSawCbmc("F");
                sawScene.setSawPlot("-");
                sawScene.setSave("-");
                this.setVars(sawScene.getVars());
                boolean fail = false;

                int steps = Integer.parseInt(this.getVars()[3]);
                if (this.getVars()[3].isEmpty() || steps == 0) fail = true;

                int dim = Integer.parseInt(this.getVars()[5]);

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

                this.setSawExpd(new ArrayList<>());
                this.setSawRms(new ArrayList<>());

                this.setExpdRuns(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getExpdRuns().add(0.0);

                this.setRmsRuns(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getRmsRuns().add(0.0);

                this.setSawLengths(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getSawLengths().add(0.0);

                this.setCbmcMu(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getCbmcMu().add(0.0);

                this.setCbmcMu2(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getCbmcMu2().add(0.0);

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

                sawScene.getFxplot().setS1Data(this.getXAxis(), this.getExpdRuns(), dim);
                sawScene.getFxplot().setS2Data(this.getXAxis(), this.getRmsRuns(), true);
                sawScene.getFxplot().setS3Data(labelMap, this.getXhistAxis(), this.getYhistAxis());

                sawScene.start();

                execCBMC.setText(this.getLanguage().equals("fin") ? "SEIS" : "STOP");
                menuNappiSAW.setDisable(true);
                helpNappiSAW.setDisable(true);
                closeNappiSAW.setDisable(true);
                plotSAW.setDisable(true);
                execSAW.setDisable(true);
                sawScene.getDimension().setDisable(true);
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
                       Execution ex, TextField setMax, TextFlow result){

        final Text noresult = new Text(this.getLanguage().equals("fin") ? "Ei tulosta" : "No result");
        noresult.setFill(Color.RED);
        noresult.setStyle("-fx-font-size: 20px;");
        String txt1 = this.getLanguage().equals("fin") ? " ajo" : " run";
        String txt2 = this.getLanguage().equals("fin") ? " ajoa" : " runs";

        plotNappi.setOnMouseClicked((MouseEvent event) -> {
            result.getChildren().clear();
            if (execSAW.isDisabled()) {
                sawScene.setSawCbmc("F");
                this.setIsCbmc(true);
            } else {
                sawScene.setSawCbmc("E");
                this.setIsCbmc(false);
            }
            sawScene.setSawPlot("p");
            sawScene.setSave("s");
            String[] vars = sawScene.getVars();
            this.setVars(vars);
            Data data = new Data(vars);
            int dim = Integer.parseInt(getVars()[4]);
            boolean fail = false;

            if ( dim < 2 || dim > 3 ) fail = true;
            if ( fail ) return;

            valikkoSAW.setDisable(true);

            int count = 0;
            int max = 100;
            if (isNumInteger(setMax.getText().trim())){
                max = Integer.parseInt(setMax.getText());
            }

            boolean ok = false;
            while (!ok && count < max) {
                ok = ex.executeSAW(datafolder, datapath, fexec, pyexecsaw2d,
                    pyexecsaw3d, valikkoSAW, data, this.getVars(), this.isCbmc());
                count += 1;
            }

            if(!ok) {
                if (this.getPrevAlign() == 1) {
                    result.setTextAlignment(TextAlignment.CENTER);
                    this.setPrevAlign(2);
                } else if (this.getPrevAlign() == 2) {
                    result.setTextAlignment(TextAlignment.RIGHT);
                    this.setPrevAlign(3);
                } else {
                    result.setTextAlignment(TextAlignment.LEFT);
                    this.setPrevAlign(1);
                }
                result.getChildren().add(noresult);
            } else {
                Text resulttext = new Text(count + (count == 1 ? txt1 : txt2));
                resulttext.setFill(Color.GREEN);
                resulttext.setStyle("-fx-font-size: 20px;");
                result.setTextAlignment(TextAlignment.LEFT);
                this.setPrevAlign(3);
                result.getChildren().add(resulttext);
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
     * @return the cbmc_mu
     */
    @Contract(pure = true)
    private List<Double> getCbmcMu() { return this.cbmc_mu; }

    /**
     * @param cbmc_mu the cbmc_mu to set
     */
    private void setCbmcMu(List<Double> cbmc_mu) { this.cbmc_mu = cbmc_mu; }

    /**
     * @return the cbmc_mu2
     */
    @Contract(pure = true)
    private List<Double> getCbmcMu2() { return this.cbmc_mu2; }

    /**
     * @param cbmc_mu2 the cbmc_mu2 to set
     */
    private void setCbmcMu2(List<Double> cbmc_mu2) { this.cbmc_mu2 = cbmc_mu2; }

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

    /**
     * @return the prevAlign
     */
    @Contract(pure = true)
    private int getPrevAlign() { return prevAlign; }

    /**
     *  the prevAlign to set
     */
    private void setPrevAlign(int prevAlign) { this.prevAlign = prevAlign; }
}
