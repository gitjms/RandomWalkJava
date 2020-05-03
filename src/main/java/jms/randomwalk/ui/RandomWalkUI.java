package jms.randomwalk.ui;

import enums.DblSizes;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import jms.randomwalk.execs.*;
import jms.randomwalk.plots.Execution;
import jms.randomwalk.scenes.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import org.apache.maven.surefire.shade.booter.org.apache.commons.lang3.SystemUtils;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 */
public class RandomWalkUI extends Application {

    private double screenWidth;
    private double screenHeight;
    private JFrame frame;
    private TextArea textAreaCalc;
    private TextArea textAreaPath;
    private TextArea textAreaReal;
    private TextArea textAreaDiff;
    private TextArea textArea1Ddist;
    private TextArea textAreaSAW;
    private TextArea textAreaMenu;
    private double diffscalefactor;
    private boolean newdata;
    private String language;
    private HBox isovalikkoMenu;
    private HBox isovalikkoPath;
    private HBox isovalikkoCalc;
    private HBox isovalikko1Ddist;
    private HBox isovalikkoReal;
    private HBox isovalikkoDiff;
    private HBox isovalikkoSAW;
    private VBox valikkoMenu;
    private VBox valikkoPath;
    private VBox valikkoCalc;
    private VBox valikko1Ddist;
    private VBox valikkoReal;
    private VBox valikkoDiff;
    private VBox valikkoSAW;
    private Pane calcpane;
    private Pane realpane;
    private Pane diffpane;
    private Pane sawpane;
    private GridPane asettelu;

    @Override
    public void start(Stage stage) {

        /*
        * DIALOG FOR LANGUAGE CHOICE
        */
        GetDialogs getDialogs = new GetDialogs();

        ButtonType buttonFI = new ButtonType("FI", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonEN = new ButtonType("EN", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonEX = new ButtonType("EXIT", ButtonBar.ButtonData.OK_DONE);
        Dialog<?> langDialog = getDialogs.getLangChoice(buttonFI, buttonEX, buttonEN);

        langDialog.showAndWait();
        if (langDialog.getResult() == buttonFI) { 
            this.setLanguage("fin");
            langDialog.close();
        } else if (langDialog.getResult() == buttonEN) { 
            this.setLanguage("eng");
            langDialog.close();
        } else if (langDialog.getResult() == buttonEX) {
            langDialog.close();
            return; 
        }

        /*
         * INITIATE PARAMETERS
         */
        this.setScreenWidth(Toolkit.getDefaultToolkit().getScreenSize().width);
        this.setScreenHeight(Toolkit.getDefaultToolkit().getScreenSize().height);
        this.setDiffScalefactor();
        this.setNewdata();

        /*
        * FILE AND FOLDER CHECK
        * creates a folder "C:/RWDATA" or "home/user/RWDATA" if not exist
        * copies Fortran and Python executables from resources/ folder
        * to "RWDATA" folder if not in there already
        * Uses instance of FilesAndFolders.java class
         */
        boolean isWin = SystemUtils.IS_OS_WINDOWS;
        String datapath = isWin ? "C:/RWDATA" : System.getProperty("user.home") + "/RWDATA";
        String fexec = isWin ? "walk.exe" : "walkLx";
        String pyexecrms = "plotrms.py";
        String pyexec1d = "plot1d.py";
        String pyexec2d = "plot2d.py";
        String pyexec3d = "plot3d.py";
        String pyexecdiff2d = "plotdiff2d.py";
        String pyexecdiff3d = "plotdiff3d.py";
        String pyexec1Ddist = "plot1Ddist.py";
        String pyexecsaw2d = "plotSaw2d.py";
        String pyexecsaw3d = "plotSaw3d.py";
        File datafolder = new File(datapath);
        File sourceFile = new File(datapath + "/" + fexec);

        FilesAndFolders filesAndFolders = new FilesAndFolders(this.getLanguage());

        if (Files.notExists(datafolder.toPath())) {
            if (filesAndFolders.checkCreateFolders(datapath, fexec, pyexecrms, pyexec1d, pyexec2d, pyexec3d,
                pyexecdiff2d, pyexecdiff3d, pyexec1Ddist, pyexecsaw2d, pyexecsaw3d)) {
                Platform.exit();
            }
        } else if (Files.notExists(sourceFile.toPath())) {
            if (filesAndFolders.createFolder(datapath, fexec, false)) {
                Platform.exit();
            }
            if (filesAndFolders.checkSourceFiles(datapath, pyexecrms, pyexec1d, pyexec2d, pyexec3d,
                pyexecdiff2d, pyexecdiff3d, pyexec1Ddist, pyexecsaw2d, pyexecsaw3d)) {
                Platform.exit();
            }
        }
        if (filesAndFolders.checkSourceFiles(datapath, pyexecrms, pyexec1d, pyexec2d, pyexec3d,
            pyexecdiff2d, pyexecdiff3d, pyexec1Ddist, pyexecsaw2d, pyexecsaw3d)) {
            Platform.exit();
        }

        /*
        * CREATE STAGE
        */
        stage.setTitle(this.getLanguage().equals("fin") ? "Satunnaiskulku" : "Random Walk");
        stage.setWidth(DblSizes.STGW.getDblSize());
        stage.setHeight(DblSizes.STGH.getDblSize());
        stage.setResizable(false);
        stage.setX(this.getScreenWidth() - DblSizes.STGW.getDblSize() - 10.0);
        stage.setY((this.getScreenHeight() - DblSizes.STGH.getDblSize()) / 2.0);

        /*
        * GET BUTTONS AND OTHER COMPONENTS
        */
        GetComponents getComponents = new GetComponents();
        GetButtons getButtons = new GetButtons(stage);

        /*
        * FIRST VIEW BUTTONS
        */
        Button nappiScene1 = getButtons.getSceneButton(this.getLanguage().equals("fin") ? "LIIKERADAT" : "PATH TRACING");
        Button nappiScene2 = getButtons.getSceneButton(this.getLanguage().equals("fin") ? "1D-ETÄISYYS" : "1D DISTANCE");
        Button nappiScene3 = getButtons.getSceneButton(this.getLanguage().equals("fin") ? "RMS vs SQRT(S)" : "RMS vs SQRT(S)");
        Button nappiScene4 = getButtons.getSceneButton(this.getLanguage().equals("fin") ? "REAALIAIKA-RMS" : "REAL TIME RMS");
        Button nappiScene5 = getButtons.getSceneButton(this.getLanguage().equals("fin") ? "DIFFUUSIO" : "DIFFUSION");
        Button nappiScene6 = getButtons.getSceneButton(this.getLanguage().equals("fin") ? "REAALIAIKA-SAW" : "REAL TIME SAW");

        /*
        * PUT ALL BUTTONS TO GRIDPANE
        */
        this.setAsettelu(GetComponents.getAsettelu(nappiScene1, nappiScene2, nappiScene3, nappiScene4, nappiScene5, nappiScene6));

        /*
        * GET SCENES
        */
        ScenePathTracing getPathScene = new ScenePathTracing(this.getLanguage());
        Scene1Ddist get1DdistScene = new Scene1Ddist(this.getLanguage());
        SceneCalculation getCalcScene = new SceneCalculation(this.getLanguage());
        SceneRealTimeRms getRealScene = new SceneRealTimeRms(this.getLanguage());
        SceneDiff getDiffScene = new SceneDiff(this.getLanguage());
        SceneRealTimeSaw getSAWScene = new SceneRealTimeSaw(this.getLanguage());

        /*
        * HBOXES (ISOVALIKKO)
        */
        this.setIsovalikkoMenu(getComponents.getHBox(0, 10));
        this.setIsovalikkoPath(getComponents.getHBox(0, 0));
        this.setIsovalikko1Ddist(getComponents.getHBox(0, 0));
        this.setIsovalikkoCalc(getComponents.getHBox(0, 0));
        this.setIsovalikkoReal(getComponents.getHBox(0, 0));
        this.setIsovalikkoDiff(getComponents.getHBox(0, 0));
        this.setIsovalikkoSAW(getComponents.getHBox(0, 0));

        /*
         * VBOXES (VALIKKO)
         */
        this.setValikkoMenu(getComponents.getVBox(10));
        this.setValikkoPath(getComponents.getVBox(20));
        this.setValikko1Ddist(getComponents.getVBox(20));
        this.setValikkoCalc(getComponents.getVBox(20));
        this.setValikkoReal(getComponents.getVBox(20));
        this.setValikkoDiff(getComponents.getVBox(20));
        this.setValikkoSAW(getComponents.getVBox(20));

        /*
        * TEXTAREAS
        */
        this.setTextAreaMenu(getComponents.getTextArea(DblSizes.TXTW.getDblSize(), DblSizes.TXTH.getDblSize()));
        this.setTextAreaPath(getComponents.getTextArea(DblSizes.TXTW.getDblSize(), DblSizes.TXTH.getDblSize()));
        this.setTextArea1Ddist(getComponents.getTextArea(DblSizes.TXTW.getDblSize(), DblSizes.TXTH.getDblSize()));
        this.setTextAreaCalc(getComponents.getTextArea(DblSizes.TXTW.getDblSize(), DblSizes.TXTH.getDblSize()));
        this.setTextAreaReal(getComponents.getTextArea(DblSizes.ANIMSIZE.getDblSize(), DblSizes.ANIMSIZE.getDblSize()));
        this.setTextAreaDiff(getComponents.getTextArea(DblSizes.ANIMSIZE.getDblSize(), DblSizes.ANIMSIZE.getDblSize()));
        this.setTextAreaSAW(getComponents.getTextArea(DblSizes.SAWTXTW.getDblSize(), DblSizes.TXTH.getDblSize()));

        /*
         * CREATE A FRAME FOR CALCULATION AND PATH TRACING PLOTS
         */
        this.setFrame(new JFrame());

        /*
         * CREATE AN INSTANCE FOR CODE EXECUTIONS
         */
        Execution ex = new Execution(this.getLanguage());

        /*
         * CREATE AN INSTANCE FOR HELP TEXTS
         */
        HelpText helpText = new HelpText();

        /*
         * FIRST VIEW BUTTONS: HELP & CLOSE
         */
        Button nappiMenuHelp = getButtons.getHelpButton(
            this.getLanguage(), this.getTextAreaMenu(), null, null, null, null, null, "menu", 0);
        this.getAsettelu().add(nappiMenuHelp, 0, 14, 2, 1);
        Button closeNappiMenu = getButtons.getCloseButton(getRealScene, getDiffScene, getSAWScene,
            0, ex, this.getLanguage(), this.getFrame(), this.getButtonYES(), this.getButtonNO());

        /*
         * OTHER VIEWS BUTTONS: PATH TRACING
         */
        Button executeNappiPath = getButtons.getExecuteButton(this.getLanguage(), 0, "EXEC");
        Button menuNappiPath = getButtons.getMenuButton(this.getLanguage(), 0);
        Button closeNappiPath = getButtons.getCloseButton(getRealScene, getDiffScene, getSAWScene,
            0, ex, this.getLanguage(), this.getFrame(), this.getButtonYES(), this.getButtonNO());
        this.getTextAreaPath().setText(this.getLanguage().equals("fin") ? helpText.pathtracingFI() : helpText.pathtracingEN());

        /*
         * OTHER VIEWS BUTTONS: 1D DISTANCE
         */
        Button executeNappi1Ddist = getButtons.getExecuteButton(this.getLanguage(), 0, "EXEC");
        Button menuNappi1Ddist = getButtons.getMenuButton(this.getLanguage(), 0);
        Button closeNappi1Ddist = getButtons.getCloseButton(getRealScene, getDiffScene, getSAWScene,
            0, ex, this.getLanguage(), this.getFrame(), this.getButtonYES(), this.getButtonNO());
        this.getTextArea1Ddist().setText(this.getLanguage().equals("fin") ? helpText.distance1DFI() : helpText.distance1DEN());

        /*
        * OTHER VIEWS BUTTONS: RMS CALCULATION
        */
        Button executeNappiCalc = getButtons.getExecuteButton(this.getLanguage(), 0, "EXEC");
        Button menuNappiCalc = getButtons.getMenuButton(this.getLanguage(), 0);
        Button closeNappiCalc = getButtons.getCloseButton(getRealScene, getDiffScene, getSAWScene,
            0, ex, this.getLanguage(), this.getFrame(), this.getButtonYES(), this.getButtonNO());
        // CALCULATION COMPONENTS
        // MATH CARD
        Image imgCalcFI = new Image("/calcFI.png");
        Image imgCalcEN = new Image("/calcEN.png");
        ImageView ivCalcFI = new ImageView(imgCalcFI);
        ImageView ivCalcEN = new ImageView(imgCalcEN);
        ivCalcFI.setSmooth(true);
        ivCalcEN.setSmooth(true);
        this.setCalcPane(this.getLanguage().equals("fin")
            ? getComponents.getPane2(ivCalcFI, DblSizes.TXTW.getDblSize(), DblSizes.TXTH.getDblSize())
            : getComponents.getPane2(ivCalcEN, DblSizes.TXTW.getDblSize(), DblSizes.TXTH.getDblSize()));
        Button helpNappiCalc = getButtons.getHelpButton(
            this.getLanguage(), this.getTextAreaCalc(), this.getIsovalikkoCalc(),
                this.getCalcPane(), null, null, null, "calc", 0);

        /*
        * OTHER VIEWS BUTTONS: REAL TIME RMS
        */
        Button runReal = getButtons.getExecuteButton(this.getLanguage(), 0, "RUN");
        Button menuNappiReal = getButtons.getMenuButton(this.getLanguage(), 0);
        Button closeNappiReal = getButtons.getCloseButton(getRealScene, getDiffScene, getSAWScene,
            0, ex, this.getLanguage(), this.getFrame(), this.getButtonYES(), this.getButtonNO());

        // REAL TIME RMS COMPONENTS
        Canvas rtrmsAlusta = new Canvas(DblSizes.ANIMSIZE.getDblSize(), DblSizes.ANIMSIZE.getDblSize());
        rtrmsAlusta.setVisible(true);
        GraphicsContext piirturi = rtrmsAlusta.getGraphicsContext2D();
        piirturi.setFill(Color.BLACK);
        piirturi.fillRect(0, 0, DblSizes.ANIMSIZE.getDblSize(), DblSizes.ANIMSIZE.getDblSize());
        this.setRealPane(getComponents.getPane(rtrmsAlusta, DblSizes.ANIMSIZE.getDblSize(), DblSizes.ANIMSIZE.getDblSize()));
        Image imgRms1FI = new Image("/rms1FI.png");
        Image imgRms1EN = new Image("/rms1EN.png");
        Image imgRms2FI = new Image("/rms2FI.png");
        Image imgRms2EN = new Image("/rms2EN.png");
        ImageView ivRms1FI = new ImageView(imgRms1FI);
        ImageView ivRms1EN = new ImageView(imgRms1EN);
        ImageView ivRms2FI = new ImageView(imgRms2FI);
        ImageView ivRms2EN = new ImageView(imgRms2EN);
        ivRms1FI.setSmooth(true);
        ivRms1EN.setSmooth(true);
        ivRms2FI.setSmooth(true);
        ivRms2EN.setSmooth(true);
        Button helpNappiReal = getButtons.getHelpButton(
            this.getLanguage(), this.getTextAreaReal(), this.getIsovalikkoReal(),
                null, this.getRealPane(), null, null, "real", 0);

        /*
        * OTHER VIEWS BUTTONS: DIFFUSION
        */
        Button runDiff = getButtons.getExecuteButton(this.getLanguage(), 0, "ANIM");
        Button plotDiff = getButtons.getExecuteButton(this.getLanguage(), 0, "PLOT");
        Button menuNappiDiff = getButtons.getMenuButton(this.getLanguage(), 0);
        Button remBarNappiDiff = getButtons.getDiffBarCanButtons(this.getLanguage(), 1);
        Button cancelNappiDiff = getButtons.getDiffBarCanButtons(this.getLanguage(), 2);
        Button closeNappiDiff = getButtons.getCloseDiffButton(this.getLanguage(), getDiffScene, this.getFrame());

        // DIFFUSION COMPONENTS
        Canvas diffAlusta = new Canvas(DblSizes.ANIMSIZE.getDblSize(), DblSizes.ANIMSIZE.getDblSize());
        diffAlusta.setVisible(true);
        GraphicsContext diffpiirturi = diffAlusta.getGraphicsContext2D();
        diffpiirturi.setFill(Color.BLACK);
        diffpiirturi.fillRect(0, 0, DblSizes.ANIMSIZE.getDblSize(), DblSizes.ANIMSIZE.getDblSize());
        // MATH CARD
        Image imgDiffFI = new Image("/diffFI.png");
        Image imgDiffEN = new Image("/diffEN.png");
        ImageView ivDiffFI = new ImageView(imgDiffFI);
        ImageView ivDiffEN = new ImageView(imgDiffEN);
        ivDiffFI.setSmooth(true);
        ivDiffEN.setSmooth(true);
        this.setDiffPane(this.getLanguage().equals("fin")
            ? getComponents.getPane2(ivDiffFI, DblSizes.ANIMSIZE.getDblSize(), DblSizes.ANIMSIZE.getDblSize())
            : getComponents.getPane2(ivDiffEN, DblSizes.ANIMSIZE.getDblSize(), DblSizes.ANIMSIZE.getDblSize()));
        this.getDiffPane().setId("image");
        Button helpNappiDiff = getButtons.getHelpButton(
            this.getLanguage(), this.getTextAreaDiff(), this.getIsovalikkoDiff(),
            null, null, this.getDiffPane(), null, "diff", 0);

        /*
         * OTHER VIEWS BUTTONS: REAL TIME SAW
         */
        Button runSAW = getButtons.getExecuteButton(this.getLanguage(), 0, "SAW");
        Button runMCSAW = getButtons.getExecuteButton(this.getLanguage(), 0, "MCSAW");
        HBox sawButtonBox = new HBox(5);
        sawButtonBox.getChildren().addAll(runSAW, runMCSAW);
        Button runEFF = getButtons.getExecuteButton(this.getLanguage(), 1, "EFF");
        Button plotSAW = getButtons.getExecuteButton(this.getLanguage(), 1, "PLOT");
        Button menuNappiSAW = getButtons.getMenuButton(this.getLanguage(), 1);
        Button closeNappiSAW = getButtons.getCloseButton(getRealScene, getDiffScene, getSAWScene,
            1, ex, this.getLanguage(), this.getFrame(), this.getButtonYES(), this.getButtonNO());

        // REAL TIME SAW COMPONENTS
        // SLIDER A
        String labaStr = this.getLanguage().equals("fin") ? "Amplitudi A :  " : "Amplitude A :  ";
        final Label labelaa = new Label();
        Slider sliderAa = new Slider(0.00, 1.20, 0.00);
        sliderAa.setOrientation(Orientation.HORIZONTAL);
        sliderAa.setMaxSize(DblSizes.SAWCOMPW.getDblSize(), DblSizes.SAWCOMPH.getDblSize());
        sliderAa.setShowTickLabels(true);
        sliderAa.setMinorTickCount(18);
        sliderAa.setMajorTickUnit(0.2);
        sliderAa.setShowTickMarks(false);
        sliderAa.setSnapToTicks(true);
        labelaa.textProperty().bind(Bindings.format(labaStr + "%.2f", sliderAa.valueProperty()));
        VBox sliderBox = new VBox(5);
        sliderBox.setPadding(new Insets(0, 0, -15, 0));
        sliderBox.getChildren().addAll(labelaa, sliderAa);

        // MATH CARD
        Image imgSawFI = new Image("/sawFI.png");
        Image imgSawEN = new Image("/sawEN.png");
        ImageView ivSawFI = new ImageView(imgSawFI);
        ImageView ivSawEN = new ImageView(imgSawEN);
        ivSawFI.setSmooth(true);
        ivSawEN.setSmooth(true);
        this.setSawPane(this.getLanguage().equals("fin")
            ? getComponents.getPane2(ivSawFI, DblSizes.SAWTXTW.getDblSize(), DblSizes.TXTH.getDblSize())
            : getComponents.getPane2(ivSawEN, DblSizes.SAWTXTW.getDblSize(), DblSizes.TXTH.getDblSize()));
        Button helpNappiSAW = getButtons.getHelpButton(
            this.getLanguage(), this.getTextAreaSAW(), this.getIsovalikkoSAW(),
                null, null, null, this.getSawPane(), "saw", 1);

        VBox effBox = new VBox(0);
        effBox.setPadding(new Insets(-15, 0, -15, 0));
        Label labEff = new Label(this.getLanguage().equals("fin") ? "tehokkuusaskeleita max:" : "efficiency steps max:");
        TextField setEff = new TextField("");
        TextFlow resultEff = new TextFlow();
        resultEff.setMinSize(DblSizes.PANEW.getDblSize(), 10);
        resultEff.setMaxSize(DblSizes.PANEW.getDblSize(), 10);
        effBox.getChildren().addAll(labEff, setEff, resultEff);

        VBox maxBox = new VBox(0);
        maxBox.setPadding(new Insets(-15, 0, 10, 0));
        Label labMax = new Label(this.getLanguage().equals("fin") ? "kuvaaja-ajoja max: (oletus 100)" : "plot runs max: (default 100)");
        TextField setMax = new TextField("");
        TextFlow result = new TextFlow();
        result.setMinSize(DblSizes.PANEW.getDblSize(), 10);
        result.setMaxSize(DblSizes.PANEW.getDblSize(), 10);
        maxBox.getChildren().addAll(labMax, setMax, result);

        /*
        * SET FIRST VIEW BORDERPANE
        */
        this.getTextAreaMenu().setText(this.getLanguage().equals("fin") ? helpText.welcomeFI() : helpText.welcomeEN());
        this.getValikkoMenu().getChildren().addAll(this.getAsettelu(), nappiMenuHelp, closeNappiMenu);
        this.getIsovalikkoMenu().getChildren().addAll(this.getValikkoMenu(), this.getTextAreaMenu());
        BorderPane asetteluMenu = new BorderPane();
        asetteluMenu.setCenter(this.getIsovalikkoMenu());

        /*
         * SET PATH TRACING BORDERPANE
         */
        this.getValikkoPath().getChildren().addAll(
            menuNappiPath, getPathScene.getScenePath(), executeNappiPath, closeNappiPath);
        this.getIsovalikkoPath().getChildren().addAll(this.getValikkoPath(), this.getTextAreaPath());
        BorderPane asetteluPath = new BorderPane();
        asetteluPath.setCenter(this.getIsovalikkoPath());

        /*
         * SET 1D DISTANCE BORDERPANE
         */
        this.getValikko1Ddist().getChildren().addAll(
            menuNappi1Ddist, get1DdistScene.getScene1Ddist(), executeNappi1Ddist, closeNappi1Ddist);
        this.getIsovalikko1Ddist().getChildren().addAll(this.getValikko1Ddist(), this.getTextArea1Ddist());
        BorderPane asettelu1Ddist = new BorderPane();
        asettelu1Ddist.setCenter(this.getIsovalikko1Ddist());

        /*
        * SET CALCULATION BORDERPANE
        */
        this.getValikkoCalc().getChildren().addAll(
            menuNappiCalc, helpNappiCalc, getCalcScene.getSceneCalc(), executeNappiCalc, closeNappiCalc);
        this.getIsovalikkoCalc().getChildren().addAll(this.getValikkoCalc(), this.getCalcPane());
        BorderPane asetteluCalc = new BorderPane();
        asetteluCalc.setCenter(this.getIsovalikkoCalc());

        /*
        * SET REAL TIME RMS BORDERPANE
        */
        this.getValikkoReal().getChildren().addAll(
            menuNappiReal, helpNappiReal, getRealScene.getSceneReal(
                this.getIsovalikkoReal(), this.getRealPane(), ivRms1FI, ivRms1EN, ivRms2FI, ivRms2EN),
            runReal, closeNappiReal);
        this.getIsovalikkoReal().getChildren().addAll(this.getValikkoReal(),
            this.getTextAreaReal());
        BorderPane asetteluReal = new BorderPane();
        asetteluReal.setCenter(this.getIsovalikkoReal());

        /*
        * SET DIFFUSION BORDERPANE
        */
        this.getValikkoDiff().getChildren().addAll(
            menuNappiDiff, helpNappiDiff, getDiffScene.getSceneDiff(), runDiff, plotDiff, closeNappiDiff);
        this.getIsovalikkoDiff().getChildren().addAll(this.getValikkoDiff(), this.getDiffPane());
        BorderPane asetteluDiff = new BorderPane();
        asetteluDiff.setCenter(this.getIsovalikkoDiff());

        /*
         * SET REAL TIME SAW BORDERPANE
         */
        this.getValikkoSAW().getChildren().addAll(
            menuNappiSAW, helpNappiSAW, getSAWScene.getSceneRealTimeSaw(
                sliderBox, sliderAa, this.getSawPane(), runSAW, runMCSAW),
            sawButtonBox, runEFF, effBox, plotSAW, maxBox, closeNappiSAW);
        this.getIsovalikkoSAW().getChildren().addAll(this.getValikkoSAW(), this.getSawPane());
        BorderPane asetteluSAW = new BorderPane();
        asetteluSAW.setCenter(this.getIsovalikkoSAW());

        /*
        * SET SCENES
        */
        Scene firstScene = new Scene(asetteluMenu, DblSizes.STGW.getDblSize(), DblSizes.STGH.getDblSize());

        Scene pathScene = new Scene(asetteluPath, DblSizes.STGW.getDblSize(), DblSizes.STGH.getDblSize());

        Scene dist1DScene = new Scene(asettelu1Ddist, DblSizes.STGW.getDblSize(), DblSizes.STGH.getDblSize());

        Scene calcScene = new Scene(asetteluCalc, DblSizes.STGW.getDblSize(), DblSizes.STGH.getDblSize());

        Scene realScene = new Scene(asetteluReal,
            DblSizes.STGW.getDblSize() + (DblSizes.ANIMSIZE.getDblSize() - DblSizes.TXTW.getDblSize()),
            DblSizes.STGH.getDblSize() + (DblSizes.ANIMSIZE.getDblSize() - DblSizes.TXTH.getDblSize()));

        Scene diffScene = new Scene(asetteluDiff,
            DblSizes.STGW.getDblSize() + (DblSizes.ANIMSIZE.getDblSize() - DblSizes.TXTW.getDblSize()),
            DblSizes.STGH.getDblSize() + (DblSizes.ANIMSIZE.getDblSize() - DblSizes.TXTH.getDblSize()));

        Scene sawScene = new Scene(asetteluSAW, DblSizes.STGW.getDblSize(), DblSizes.STGH.getDblSize());

        /*
         * SET SCENE CHOICE BUTTONS EFFECTS (WINDOW RESIZE ETC.)
         */
        SetSceneChoices setChoices = new SetSceneChoices(stage);

        setChoices.setSceneEffects(this.getLanguage(), nappiScene1, pathScene, "Liikeradat", "Path Tracing");
        setChoices.setMenuEffects(this.getLanguage(), menuNappiPath, "path",
            this.getTextAreaPath(), this.getTextAreaMenu(), firstScene, 1);

        setChoices.setSceneEffects(this.getLanguage(), nappiScene2, dist1DScene, "1D-etäisyys", "1D Distance");
        setChoices.setMenuEffects(this.getLanguage(), menuNappi1Ddist, "1Ddist",
            this.getTextArea1Ddist(), this.getTextAreaMenu(), firstScene, 0);

        setChoices.setSceneEffects(this.getLanguage(), nappiScene3, calcScene, "Rms-laskenta", "Rms calculation");
        setChoices.setMenuEffects(this.getLanguage(), menuNappiCalc, "calc",
            this.getTextAreaCalc(), this.getTextAreaMenu(), firstScene, 0);

        setChoices.setBigSceneEffects(this.getLanguage(), nappiScene4, realScene, "Reaaliaika-rms", "Real Time Rms");
        setChoices.setMenuEffects(this.getLanguage(), menuNappiReal, "real",
            this.getTextAreaReal(), this.getTextAreaMenu(), firstScene, 2);

        setChoices.setBigSceneEffects(this.getLanguage(), nappiScene5, diffScene, "Diffuusio", "Diffusion");
        setChoices.setMenuEffects(this.getLanguage(), menuNappiDiff, "diff",
            this.getTextAreaDiff(), this.getTextAreaMenu(), firstScene, 2);

        setChoices.setSceneEffects(this.getLanguage(), nappiScene6, sawScene, "Reaaliaika-saw", "Real Time saw");
        setChoices.setMenuEffects(this.getLanguage(), menuNappiSAW, "saw",
            this.getTextAreaSAW(), this.getTextAreaMenu(), firstScene, 1);

        /*
        * EXECUTE BUTTON PATH TRACING MOUSECLICKED
        */
        ExecPath execPath = new ExecPath(this.getLanguage());
        execPath.setExecClick(executeNappiPath, getPathScene, ex, datafolder, datapath, fexec, pyexec1d,
            pyexec2d, pyexec3d, getDialogs);

        /*
        * EXECUTE BUTTON 1D DISTANCE
        */
        Exec1Ddist exec1Ddist = new Exec1Ddist(this.getLanguage());
        exec1Ddist.setExecClick(executeNappi1Ddist, get1DdistScene, ex, datafolder, datapath, fexec, pyexec1Ddist,
            getDialogs);

        /*
        * EXECUTE BUTTON RMS CALCULATION
        */
        ExecCalc execCalc = new ExecCalc(this.getLanguage());
        execCalc.setExecClick(executeNappiCalc, getCalcScene, ex, datafolder, datapath, fexec, pyexecrms, getDialogs);

        /*
         * PLOT & RUN BUTTONS REAL TIME RMS
         */
        ExecReal execReal = new ExecReal(this.getLanguage());
        execReal.setRmsClick(datafolder, fexec, runReal, getRealScene, closeNappiReal, menuNappiReal, helpNappiReal,
            this.getIsovalikkoReal(), this.getTextAreaReal(), this.getRealPane(), rtrmsAlusta, piirturi);

        /*
        * PLOT & EXECUTE BUTTONS DIFFUSION
        */
        ExecDiff execDIFF = new ExecDiff(this.getLanguage());
        execDIFF.setPlotClick(plotDiff, getDiffScene, this.valikkoDiff, datapath, datafolder,
            fexec, pyexecdiff2d, pyexecdiff3d, ex);
        execDIFF.setExecClick(runDiff, getDiffScene, diffpiirturi,
            this.getDiffScalefactor(), DblSizes.ANIMSIZE.getDblSize(), DblSizes.ANIMSIZE.getDblSize(),
            this.newdata, this.isovalikkoDiff, this.valikkoDiff, this.diffpane, datapath, datafolder,
            fexec, remBarNappiDiff, cancelNappiDiff, plotDiff, closeNappiDiff, menuNappiDiff, helpNappiDiff, diffAlusta);

        /*
         * PLOT & RUN BUTTONS REAL TIME SAW
         */
        ExecSAW execSAW = new ExecSAW(this.getLanguage());
        execSAW.setPlotClick(plotSAW, runSAW, getSAWScene, this.getValikkoSAW(), datapath, datafolder,
                fexec, pyexecsaw2d, pyexecsaw3d, ex, setMax, result);
        execSAW.setEffClick(datafolder, fexec, runEFF, runSAW, runMCSAW, plotSAW, getSAWScene, this.getValikkoSAW(), setEff, resultEff, sliderAa);
        execSAW.setSawClick(datafolder, fexec, runSAW, runMCSAW, runEFF, getSAWScene, this.getIsovalikkoSAW(),
                this.getSawPane(), this.getTextAreaSAW(), plotSAW, closeNappiSAW, menuNappiSAW, helpNappiSAW, sliderAa);

        /*
        * CLOSE STAGE
        */
        stage.setOnCloseRequest((WindowEvent e) -> {
            if (this.getFrame() != null) {
                if (this.getFrame().isShowing() || this.getFrame().isActive() || this.getFrame().isDisplayable()) {
                    this.getFrame().dispose();
                }
            }
            if (getDiffScene.runtimeIsRunning()) {
                Runtime.getRuntime().gc();
                getDiffScene.stopRuntime();
            }
            if (getRealScene.runtimeIsRunning()) {
                Runtime.getRuntime().gc();
                getRealScene.stopRuntime();
            }
            if (getSAWScene.runtimeIsRunning()) {
                Runtime.getRuntime().gc();
                getSAWScene.stopRuntime();
            }
            if (ex.runtimeIsRunning()) {
                Runtime.getRuntime().gc();
                ex.stopRuntime();
            }

            stage.close();
            Platform.exit();
            System.exit(0);
        });
        stage.setScene(firstScene);
        stage.initStyle(StageStyle.DECORATED);
        Image icn16 = new Image("icon16.png");
        Image icn64 = new Image("icon64.png");
        Image icn48 = new Image("icon48.png");
        Image icn32 = new Image("icon32.png");
        Image icn24 = new Image("icon24.png");
        stage.getIcons().addAll(icn64, icn48, icn32, icn24, icn16);
        stage.toFront();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * @return the screenWidth
     */
    private double getScreenWidth() {
        return screenWidth;
    }

    /**
     * @param screenWidth the screenWidth to set
     */
    private void setScreenWidth(double screenWidth) {
        this.screenWidth = screenWidth;
    }

    /**
     * @return the screenHeight
     */
    private double getScreenHeight() {
        return screenHeight;
    }

    /**
     * @param screenHeight the screenHeight to set
     */
    private void setScreenHeight(double screenHeight) {
        this.screenHeight = screenHeight;
    }

    /**
     * @return the frame
     */
    private JFrame getFrame() {
        return frame;
    }

    /**
     * @param frame the frame to set
     */
    private void setFrame(JFrame frame) {
        this.frame = frame;
    }

    /**
     * @return the diffscalefactor
     */
    private double getDiffScalefactor() {
        return diffscalefactor;
    }

    /**
     */
    private void setDiffScalefactor() {
        this.diffscalefactor = 1.0;
    }

    /**
     */
    private void setNewdata() {
        this.newdata = false;
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
     * @return the textAreaPath
     */
    private TextArea getTextAreaPath() {
        return this.textAreaPath;
    }

    /**
     * @param textarea the textAreaPath to set
     */
    private void setTextAreaPath(TextArea textarea) {
        this.textAreaPath = textarea;
    }

    /**
     * @return the textArea1Ddist
     */
    private TextArea getTextArea1Ddist() {
        return this.textArea1Ddist;
    }

    /**
     * @param textarea the textArea1Ddist to set
     */
    private void setTextArea1Ddist(TextArea textarea) {
        this.textArea1Ddist = textarea;
    }

    /**
     * @return the textAreaCalc
     */
    private TextArea getTextAreaCalc() {
        return this.textAreaCalc;
    }

    /**
     * @param textarea the textAreaCalc to set
     */
    private void setTextAreaCalc(TextArea textarea) {
        this.textAreaCalc = textarea;
    }

    /**
     * @return the textAreaReal
     */
    private TextArea getTextAreaReal() {
        return this.textAreaReal;
    }

    /**
     * @param textarea the textAreaReal to set
     */
    private void setTextAreaReal(TextArea textarea) {
        this.textAreaReal = textarea;
    }

    /**
     * @return the textAreaDiff
     */
    private TextArea getTextAreaDiff() {
        return this.textAreaDiff;
    }

    /**
     * @param textarea the textAreaDiff to set
     */
    private void setTextAreaDiff(TextArea textarea) {
        this.textAreaDiff = textarea;
    }

    /**
     * @return the textAreaSAW
     */
    private TextArea getTextAreaSAW() {
        return this.textAreaSAW;
    }

    /**
     * @param textarea the textAreaSAW to set
     */
    private void setTextAreaSAW(TextArea textarea) {
        this.textAreaSAW = textarea;
    }

    /**
     * @return the textAreaMenu
     */
    private TextArea getTextAreaMenu() {
        return this.textAreaMenu;
    }

    /**
     * @param textarea the textAreaMenu to set
     */
    private void setTextAreaMenu(TextArea textarea) {
        this.textAreaMenu = textarea;
    }

    /**
     * @return the isovalikkoMenu
     */
    private HBox getIsovalikkoMenu() {
        return this.isovalikkoMenu;
    }

    /**
     * @param hbox the isovalikkoMenu to set
     */
    private void setIsovalikkoMenu(HBox hbox) {
        this.isovalikkoMenu = hbox;
    }

    /**
     * @return the isovalikkoPath
     */
    private HBox getIsovalikkoPath() {
        return this.isovalikkoPath;
    }

    /**
     * @param hbox the isovalikkoPath to set
     */
    private void setIsovalikkoPath(HBox hbox) {
        this.isovalikkoPath = hbox;
    }

    /**
     * @return the isovalikkoCalc
     */
    private HBox getIsovalikkoCalc() {
        return this.isovalikkoCalc;
    }

    /**
     * @param hbox the isovalikkoCalc to set
     */
    private void setIsovalikkoCalc(HBox hbox) {
        this.isovalikkoCalc = hbox;
    }

    /**
     * @return the isovalikko1Ddist
     */
    private HBox getIsovalikko1Ddist() {
        return this.isovalikko1Ddist;
    }

    /**
     * @param hbox the isovalikko1Ddist to set
     */
    private void setIsovalikko1Ddist(HBox hbox) {
        this.isovalikko1Ddist = hbox;
    }

    /**
     * @return the isovalikkoReal
     */
    private HBox getIsovalikkoReal() {
        return this.isovalikkoReal;
    }

    /**
     * @param hbox the isovalikkoReal to set
     */
    private void setIsovalikkoReal(HBox hbox) {
        this.isovalikkoReal = hbox;
    }

    /**
     * @return the isovalikkoDiff
     */
    private HBox getIsovalikkoDiff() {
        return this.isovalikkoDiff;
    }

    /**
     * @param hbox the isovalikkoDiff to set
     */
    private void setIsovalikkoDiff(HBox hbox) {
        this.isovalikkoDiff = hbox;
    }

    /**
     * @return the isovalikkoSAW
     */
    private HBox getIsovalikkoSAW() {
        return this.isovalikkoSAW;
    }

    /**
     * @param hbox the isovalikkoSAW to set
     */
    private void setIsovalikkoSAW(HBox hbox) {
        this.isovalikkoSAW = hbox;
    }

    /**
     * @return the valikkoMenu
     */
    private VBox getValikkoMenu() {
        return this.valikkoMenu;
    }

    /**
     * @param vbox the valikkoMenu to set
     */
    private void setValikkoMenu(VBox vbox) {
        this.valikkoMenu = vbox;
    }

    /**
     * @return the valikkoPath
     */
    private VBox getValikkoPath() {
        return this.valikkoPath;
    }

    /**
     * @param vbox the valikkoPath to set
     */
    private void setValikkoPath(VBox vbox) {
        this.valikkoPath = vbox;
    }

    /**
     * @return the valikkoCalc
     */
    private VBox getValikkoCalc() {
        return this.valikkoCalc;
    }

    /**
     * @param vbox the valikkoCalc to set
     */
    private void setValikkoCalc(VBox vbox) {
        this.valikkoCalc = vbox;
    }

    /**
     * @return the valikko1Ddist
     */
    private VBox getValikko1Ddist() {
        return this.valikko1Ddist;
    }

    /**
     * @param vbox the valikko1Ddist to set
     */
    private void setValikko1Ddist(VBox vbox) {
        this.valikko1Ddist = vbox;
    }

    /**
     * @return the valikkoReal
     */
    private VBox getValikkoReal() {
        return this.valikkoReal;
    }

    /**
     * @param vbox the valikkoReal to set
     */
    private void setValikkoReal(VBox vbox) {
        this.valikkoReal = vbox;
    }

    /**
     * @return the valikkoDiff
     */
    private VBox getValikkoDiff() {
        return this.valikkoDiff;
    }

    /**
     * @param vbox the valikkoDiff to set
     */
    private void setValikkoDiff(VBox vbox) {
        this.valikkoDiff = vbox;
    }

    /**
     * @return the valikkoSAW
     */
    private VBox getValikkoSAW() {
        return this.valikkoSAW;
    }

    /**
     * @param vbox the valikkoSAW to set
     */
    private void setValikkoSAW(VBox vbox) {
        this.valikkoSAW = vbox;
    }

    /**
     * @return the calcpane
     */
    private Pane getCalcPane() {
        return this.calcpane;
    }

    /**
     * @param pane the calcpane to set
     */
    private void setCalcPane(Pane pane) {
        this.calcpane = pane;
    }

    /**
     * @return the realpane
     */
    private Pane getRealPane() {
        return this.realpane;
    }

    /**
     * @param pane the realpane to set
     */
    private void setRealPane(Pane pane) {
        this.realpane = pane;
    }

    /**
     * @return the diffpane
     */
    private Pane getDiffPane() {
        return this.diffpane;
    }

    /**
     * @param pane the diffpane to set
     */
    private void setDiffPane(Pane pane) {
        this.diffpane = pane;
    }

    /**
     * @return the sawpane
     */
    private Pane getSawPane() {
        return this.sawpane;
    }

    /**
     * @param pane the sawpane to set
     */
    private void setSawPane(Pane pane) {
        this.sawpane = pane;
    }

    /**
     * @return the asettelu
     */
    private GridPane getAsettelu() {
        return this.asettelu;
    }

    /**
     * @param asettelu the asettelu to set
     */
    private void setAsettelu(GridPane asettelu) {
        this.asettelu = asettelu;
    }

    /**
     * @return the buttonYES
     */
    private ButtonType getButtonYES() {
        return new ButtonType(this.getLanguage().equals("fin") ? "KYLLÄ" : "YES", ButtonBar.ButtonData.YES);
    }

    /**
     * @return the buttonNO
     */
    private ButtonType getButtonNO() {
        return new ButtonType(this.getLanguage().equals("fin") ? "EI" : "NO", ButtonBar.ButtonData.NO);
    }

}
