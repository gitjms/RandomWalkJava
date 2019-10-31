package randomwalkjava;

import com.sun.glass.ui.Screen;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 */
@SuppressWarnings("SameReturnValue")
public class RandomWalk extends Application {

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
        Dialog langDialog = getDialogs.getLangChoice(buttonFI, buttonEX, buttonEN);
        langDialog.showAndWait();
        if ( langDialog.getResult() == buttonFI ) { this.setLanguage("fin"); langDialog.close(); }
        else if ( langDialog.getResult() == buttonEN ) { this.setLanguage("eng"); langDialog.close(); }
        else if ( langDialog.getResult() == buttonEX ) { langDialog.close(); return; }

        /*
         * INITIATE PARAMETERS
         */
        this.setScreenWidth(Toolkit.getDefaultToolkit().getScreenSize().width / Screen.getMainScreen().getRenderScale());
        this.setScreenHeight(Toolkit.getDefaultToolkit().getScreenSize().height / Screen.getMainScreen().getRenderScale());
        this.setDiffScalefactor();
        this.setNewdata();

        /*
        * FILE AND FOLDER CHECK
        * creates a folder "C:/RWDATA" if not exist
        * copies Fortran and Python executables from resources/ folder
        * to "RWDATA" folder if not in there already
        * Uses instance of FilesAndFolders.java class
         */
        String datapath = "C:/RWDATA";
        String fexec = "walk.exe";
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

        if (Files.notExists(datafolder.toPath())){
            if (filesAndFolders.checkCreateFolders(datapath, fexec, pyexecrms, pyexec1d, pyexec2d, pyexec3d,
                pyexecdiff2d, pyexecdiff3d, pyexec1Ddist, pyexecsaw2d, pyexecsaw3d))
                Platform.exit();
        } else if (Files.notExists(sourceFile.toPath())) {
            if (filesAndFolders.createFolder(datapath, fexec, false)) {
                Platform.exit();
            }
            if (filesAndFolders.checkSourceFiles(datapath, pyexecrms, pyexec1d, pyexec2d, pyexec3d,
                pyexecdiff2d, pyexecdiff3d, pyexec1Ddist, pyexecsaw2d, pyexecsaw3d)) {
                Platform.exit();
            }
        }

        /*
        * CREATE STAGE
        */
        stage.setTitle(this.getLanguage().equals("fin") ? "Satunnaiskulku" : "Random Walk");
        stage.setWidth(this.getStageWidth());
        stage.setHeight(this.getStageHeight());
        stage.setResizable(false);
        stage.setX(this.getScreenWidth()-this.getStageWidth()-10.0);
        stage.setY((this.getScreenHeight()-this.getStageHeight()) / 2.0);

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
        this.setAsettelu(GetComponents.GetAsettelu(nappiScene1, nappiScene2, nappiScene3, nappiScene4, nappiScene5, nappiScene6));

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
        this.setTextAreaMenu(getComponents.GetTextArea(this.getTextWidth(), this.getTextHeight()));
        this.setTextAreaPath(getComponents.GetTextArea(this.getTextWidth(), this.getPathHeight()));
        this.setTextArea1Ddist(getComponents.GetTextArea(this.getTextWidth(), this.getTextHeight()));
        this.setTextAreaCalc(getComponents.GetTextArea(this.getTextWidth(), this.getTextHeight()));
        this.setTextAreaReal(getComponents.GetTextArea(this.getAnimWidth(), this.getAnimHeight()));
        this.setTextAreaDiff(getComponents.GetTextArea(this.getAnimWidth(), this.getAnimHeight()));
        this.setTextAreaSAW(getComponents.GetTextArea(this.getSawTextWidth(), this.getSawTextHeight()));

        /*
         * CREATE A FRAME FOR CALCULATION AND PATH TRACING PLOTS
         */
        this.setFrame(new JFrame());

        /*
         * CREATE AN INSTANCE FOR CODE EXECUTIONS
         */
        Execution ex = new Execution(this.getLanguage());

        /*
         * FIRST VIEW BUTTONS: HELP & CLOSE
         */
        Button nappiMenuHelp = getButtons.getHelpButton(
            this.getLanguage(), this.getTextAreaMenu(), null, null, "menu", 0);
        this.getAsettelu().add(nappiMenuHelp, 0, 14, 2, 1);
        Button closeNappiMenu = getButtons.getCloseButton(getRealScene, getDiffScene, getSAWScene,
            0, ex, this.getLanguage(), this.getFrame(), this.getButtonYES(), this.getButtonNO());

        /*
         * OTHER VIEWS BUTTONS: PATH TRACING
         */
        Button executeNappiPath = getButtons.getExecuteButton(this.getLanguage(), 0, "EXEC");
        Button menuNappiPath = getButtons.getMenuButton(this.getLanguage(), 0);
        Button helpNappiPath = getButtons.getHelpButton(
            this.getLanguage(), this.getTextAreaPath(), this.getIsovalikkoPath(), null, "path", 0);
        Button closeNappiPath = getButtons.getCloseButton(getRealScene, getDiffScene, getSAWScene,
            0, ex, this.getLanguage(), this.getFrame(), this.getButtonYES(), this.getButtonNO());

        /*
         * OTHER VIEWS BUTTONS: 1D DISTANCE
         */
        Button executeNappi1Ddist = getButtons.getExecuteButton(this.getLanguage(), 0, "EXEC");
        Button menuNappi1Ddist = getButtons.getMenuButton(this.getLanguage(), 0);
        Button helpNappi1Ddist = getButtons.getHelpButton(
            this.getLanguage(), this.getTextArea1Ddist(), this.getIsovalikko1Ddist(), null, "1Ddist", 0);
        Button closeNappi1Ddist = getButtons.getCloseButton(getRealScene, getDiffScene, getSAWScene,
            0, ex, this.getLanguage(), this.getFrame(), this.getButtonYES(), this.getButtonNO());

        /*
        * OTHER VIEWS BUTTONS: RMS CALCULATION
        */
        Button executeNappiCalc = getButtons.getExecuteButton(this.getLanguage(), 0, "EXEC");
        Button menuNappiCalc = getButtons.getMenuButton(this.getLanguage(), 0);
        Button closeNappiCalc = getButtons.getCloseButton(getRealScene, getDiffScene, getSAWScene,
            0, ex, this.getLanguage(), this.getFrame(), this.getButtonYES(), this.getButtonNO());
        // CALCULATION COMPONENTS
        // MATH CARD
        Image imgCalcFI = new Image("file:src/main/resources/mathcardCalcFI.png");
        Image imgCalcEN = new Image("file:src/main/resources/mathcardCalcEN.png");
        ImageView ivCalcFI = new ImageView(imgCalcFI);
        ImageView ivCalcEN = new ImageView(imgCalcEN);
        this.setCalcPane(this.getLanguage().equals("fin") ? getComponents.getPane2(ivCalcFI) : getComponents.getPane2(ivCalcEN));
        Button helpNappiCalc = getButtons.getHelpButton(
            this.getLanguage(), this.getTextAreaCalc(), this.getIsovalikkoCalc(), this.getCalcPane(), "calc", 0);

        /*
        * OTHER VIEWS BUTTONS: REAL TIME RMS
        */
        Button runReal = getButtons.getExecuteButton(this.getLanguage(), 0, "RUN");
        Button menuNappiReal = getButtons.getMenuButton(this.getLanguage(), 0);
        Button closeNappiReal = getButtons.getCloseButton(getRealScene, getDiffScene, getSAWScene,
            0, ex, this.getLanguage(), this.getFrame(), this.getButtonYES(), this.getButtonNO());

        // REAL TIME RMS COMPONENTS
        Canvas rtrmsAlusta = new Canvas(this.getAnimWidth(), this.getAnimHeight());
        rtrmsAlusta.setVisible(true);
        GraphicsContext piirturi = rtrmsAlusta.getGraphicsContext2D();
        piirturi.setFill(Color.BLACK);
        piirturi.fillRect(0, 0, this.getAnimWidth(), this.getAnimHeight());
        this.setRealPane(getComponents.getPane(rtrmsAlusta));
        Button helpNappiReal = getButtons.getHelpButton(
            this.getLanguage(), this.getTextAreaReal(), this.getIsovalikkoReal(), this.getRealPane(), "real", 0);

        /*
        * OTHER VIEWS BUTTONS: DIFFUSION
        */
        Button runDiff = getButtons.getExecuteButton(this.getLanguage(), 0, "ANIM");
        Button plotDiff = getButtons.getExecuteButton(this.getLanguage(), 0, "PLOT");
        Button menuNappiDiff = getButtons.getMenuButton(this.getLanguage(), 0);
        Button helpNappiDiff = getButtons.getHelpButton(
            this.getLanguage(), this.getTextAreaDiff(), this.getIsovalikkoDiff(), this.getDiffPane(), "diff", 0);
        Button remBarNappiDiff = getButtons.getDiffBarrierButton(this.getLanguage());
        Button closeNappiDiff = getButtons.getCloseDiffButton(this.getLanguage(), getDiffScene, this.getFrame());

        // DIFFUSION COMPONENTS
        Canvas diffAlusta = new Canvas(this.getAnimWidth(), this.getAnimHeight());
        diffAlusta.setVisible(true);
        GraphicsContext diffpiirturi = diffAlusta.getGraphicsContext2D();
        diffpiirturi.setFill(Color.BLACK);
        diffpiirturi.fillRect(0, 0, this.getAnimWidth(), this.getAnimHeight());
        this.setDiffPane(getComponents.getPane(diffAlusta));

        /*
         * OTHER VIEWS BUTTONS: REAL TIME SAW
         */
        Button runSAW = getButtons.getExecuteButton(this.getLanguage(), 0, "SAW");
        Button runCBMC = getButtons.getExecuteButton(this.getLanguage(), 0, "CBMC");
        HBox sawButtonBox = new HBox(5);
        sawButtonBox.getChildren().addAll(runSAW, runCBMC);
        Button plotSAW = getButtons.getExecuteButton(this.getLanguage(), 1, "SAWPLOT");
        Button menuNappiSAW = getButtons.getMenuButton(this.getLanguage(), 1);
        Button closeNappiSAW = getButtons.getCloseButton(getRealScene, getDiffScene, getSAWScene,
            1, ex, this.getLanguage(), this.getFrame(), this.getButtonYES(), this.getButtonNO());

        // REAL TIME SAW COMPONENTS
        // SLIDERS
        final Label labgam = new Label("\u03B3 :");
        final Label labelgam = new Label();
        Slider sliderGam = new Slider(0.0, 2.0, 0.0);
        sliderGam.setOrientation(Orientation.HORIZONTAL);
        sliderGam.setMaxSize(this.getSawCompWidth(), this.getSawCompHeight());
        sliderGam.setShowTickLabels(true);
        sliderGam.setMinorTickCount(4);
        sliderGam.setMajorTickUnit(0.5);
        sliderGam.setShowTickMarks(true);
        sliderGam.setSnapToTicks(false);
        labelgam.textProperty().bind(
            Bindings.format( "%.2f", sliderGam.valueProperty() )
        );
        final Label labaa = new Label("A :");
        final Label labelaa = new Label();
        Slider sliderAa = new Slider(0.0, 2.0, 0.0);
        sliderAa.setOrientation(Orientation.HORIZONTAL);
        sliderAa.setMaxSize(this.getSawCompWidth(), this.getSawCompHeight());
        sliderAa.setShowTickLabels(true);
        sliderAa.setMinorTickCount(4);
        sliderAa.setMajorTickUnit(0.5);
        sliderAa.setShowTickMarks(true);
        sliderAa.setSnapToTicks(false);
        labelaa.textProperty().bind(
            Bindings.format( "%.2f", sliderAa.valueProperty() )
        );
        VBox sliderBox = new VBox(5);
        sliderBox.getChildren().addAll(labgam, labelgam, sliderGam, labaa, labelaa, sliderAa);

        // MATH CARD
        Image imgSawFI_one = new Image("file:src/main/resources/mathcardSawFI_one.png");
        Image imgSawEN_one = new Image("file:src/main/resources/mathcardSawEN_one.png");
        ImageView ivSawFI_one = new ImageView(imgSawFI_one);
        ImageView ivSawEN_one = new ImageView(imgSawEN_one);
        this.setSawPane(this.getLanguage().equals("fin") ? getComponents.getPane2(ivSawFI_one) : getComponents.getPane2(ivSawEN_one));
        Button helpNappiSAW = getButtons.getHelpButton(
            this.getLanguage(), this.getTextAreaSAW(), this.getIsovalikkoSAW(), this.getSawPane(), "saw", 1);

        /*
        * SET FIRST VIEW BORDERPANE
        */
        HelpText helpText = new HelpText();
        this.getTextAreaMenu().setText(this.getLanguage().equals("fin") ? helpText.welcomeFI() : helpText.welcomeEN());
        this.getValikkoMenu().getChildren().addAll(this.getAsettelu(), nappiMenuHelp, closeNappiMenu);
        this.getIsovalikkoMenu().getChildren().addAll(this.getValikkoMenu(), this.getTextAreaMenu());
        BorderPane asetteluMenu = new BorderPane();
        asetteluMenu.setCenter(this.getIsovalikkoMenu());

        /*
         * SET PATH TRACING BORDERPANE
         */
        this.getValikkoPath().getChildren().addAll(
            menuNappiPath, helpNappiPath, getPathScene.getScenePath(), executeNappiPath, closeNappiPath);
        this.getIsovalikkoPath().getChildren().addAll(this.getValikkoPath(), this.getTextAreaPath());
        BorderPane asetteluPath = new BorderPane();
        asetteluPath.setCenter(this.getIsovalikkoPath());

        /*
         * SET 1D DISTANCE BORDERPANE
         */
        this.getValikko1Ddist().getChildren().addAll(
            menuNappi1Ddist, helpNappi1Ddist, get1DdistScene.getScene1Ddist(), executeNappi1Ddist, closeNappi1Ddist);
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
            menuNappiReal, helpNappiReal, getRealScene.getSceneReal(), runReal, closeNappiReal);
        this.getIsovalikkoReal().getChildren().addAll(this.getValikkoReal(),
            this.getTextAreaReal());
        BorderPane asetteluReal = new BorderPane();
        asetteluReal.setCenter(this.getIsovalikkoReal());

        /*
        * SET DIFFUSION BORDERPANE
        */
        this.getValikkoDiff().getChildren().addAll(
            menuNappiDiff, helpNappiDiff, getDiffScene.getSceneDiff(), runDiff, plotDiff, closeNappiDiff);
        this.getIsovalikkoDiff().getChildren().addAll(this.getValikkoDiff(), this.getTextAreaDiff());
        BorderPane asetteluDiff = new BorderPane();
        asetteluDiff.setCenter(this.getIsovalikkoDiff());

        /*
         * SET REAL TIME SAW BORDERPANE
         */
        this.getValikkoSAW().getChildren().addAll(
            menuNappiSAW, helpNappiSAW, getSAWScene.getSceneRealTimeSaw(
                sliderGam, sliderAa, this.getSawPane(), runSAW, runCBMC), sliderBox, sawButtonBox, plotSAW, closeNappiSAW);
        this.getIsovalikkoSAW().getChildren().addAll(this.getValikkoSAW(), this.getSawPane());
        BorderPane asetteluSAW = new BorderPane();
        asetteluSAW.setCenter(this.getIsovalikkoSAW());

        /*
        * SET SCENES
        */
        Scene firstScene = new Scene(asetteluMenu, this.getStageWidth(), this.getStageHeight());
        firstScene.getStylesheets().add("/Styles.css");

        Scene pathScene = new Scene(asetteluPath, this.getStageWidth(),this.getStageHeight()
            + (this.getPathHeight()-this.getTextHeight()));
        pathScene.getStylesheets().add("/Styles.css");

        Scene dist1DScene = new Scene(asettelu1Ddist, this.getStageWidth(), this.getStageHeight());
        dist1DScene.getStylesheets().add("/Styles.css");

        Scene calcScene = new Scene(asetteluCalc, this.getStageWidth(), this.getStageHeight());
        calcScene.getStylesheets().add("/Styles.css");

        Scene realScene = new Scene(asetteluReal,
            this.getStageWidth() + (this.getAnimWidth()-this.getTextWidth()),
            this.getStageHeight() + (this.getAnimHeight()-this.getTextHeight()));
        realScene.getStylesheets().add("/Styles.css");

        Scene diffScene = new Scene(asetteluDiff,
            this.getStageWidth() + (this.getAnimWidth()-this.getTextWidth()),
            this.getStageHeight() + (this.getAnimHeight()-this.getTextHeight()));
        diffScene.getStylesheets().add("/Styles.css");

        Scene sawScene = new Scene(asetteluSAW,
            this.getStageWidth() + (this.getAnimWidth()-this.getTextWidth()),
            this.getStageHeight() + (this.getSawHeight()-this.getTextHeight()));
        sawScene.getStylesheets().add("/Styles.css");

        /*
         * SET SCENE CHOICE BUTTONS EFFECTS (WINDOW RESIZE ETC.)
         */
        SetSceneChoices setChoices = new SetSceneChoices(stage);

        setChoices.setSemiSceneEffects(this.getLanguage(), nappiScene1, pathScene, "path");
        setChoices.setMenuEffects(this.getLanguage(), menuNappiPath, "path",
            this.getTextAreaPath(), this.getTextAreaMenu(), firstScene, 1);

        setChoices.setSceneEffects(this.getLanguage(), nappiScene2, dist1DScene,"1D-etäisyys", "1D Distance");
        setChoices.setMenuEffects(this.getLanguage(), menuNappi1Ddist, "1Ddist",
            this.getTextArea1Ddist(), this.getTextAreaMenu(), firstScene, 0);

        setChoices.setSceneEffects(this.getLanguage(), nappiScene3, calcScene,"Rms-laskenta","Rms calculation");
        setChoices.setMenuEffects(this.getLanguage(), menuNappiCalc, "calc",
            this.getTextAreaCalc(), this.getTextAreaMenu(), firstScene, 0);

        setChoices.setBigSceneEffects(this.getLanguage(), nappiScene4, realScene, "Reaaliaika-rms", "Real Time Rms");
        setChoices.setMenuEffects(this.getLanguage(), menuNappiReal, "real",
            this.getTextAreaReal(), this.getTextAreaMenu(), firstScene, 2);

        setChoices.setBigSceneEffects(this.getLanguage(), nappiScene5, diffScene, "Diffuusio", "Diffusion");
        setChoices.setMenuEffects(this.getLanguage(), menuNappiDiff, "diff",
            this.getTextAreaDiff(), this.getTextAreaMenu(), firstScene, 2);

        setChoices.setSemiSceneEffects(this.getLanguage(), nappiScene6, sawScene, "saw");
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
        execReal.setRmsClick(datafolder, fexec, runReal, getRealScene, closeNappiReal, menuNappiReal,
            helpNappiReal, this.getIsovalikkoReal(), this.getTextAreaReal(), this.getRealPane(), piirturi);

        /*
        * PLOT & EXECUTE BUTTONS DIFFUSION
        */
        ExecDiff execDIFF = new ExecDiff(this.getLanguage());
        execDIFF.setPlotClick(plotDiff, getDiffScene, this.valikkoDiff, datapath, datafolder,
            fexec, pyexecdiff2d, pyexecdiff3d, ex);
        execDIFF.setExecClick(runDiff, getDiffScene, diffpiirturi,
            this.getDiffScalefactor(), this.getAnimWidth(), this.getAnimHeight(),
            this.newdata, this.isovalikkoDiff, this.valikkoDiff, this.textAreaDiff, this.diffpane, datapath, datafolder,
            fexec, remBarNappiDiff, plotDiff, closeNappiDiff, menuNappiDiff, helpNappiDiff);

        /*
         * PLOT & RUN BUTTONS REAL TIME SAW
         */
        ExecSAW execSAW = new ExecSAW(this.getLanguage());
        execSAW.setPlotClick(plotSAW, runSAW, getSAWScene, this.valikkoSAW, datapath, datafolder,
            fexec, pyexecsaw2d, pyexecsaw3d, ex);
        execSAW.setSawClick(datafolder, fexec, runSAW, runCBMC, getSAWScene, this.getIsovalikkoSAW(), this.getSawPane(),
            this.getTextAreaSAW(), plotSAW, closeNappiSAW, menuNappiSAW, helpNappiSAW, sliderGam, sliderAa);

        /*
        * CLOSE STAGE
        */
        stage.setOnCloseRequest((WindowEvent e) -> {
            if (this.getFrame() != null)
                if (this.getFrame().isShowing() || this.getFrame().isActive() || this.getFrame().isDisplayable())
                    this.getFrame().dispose();

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
        stage.getIcons().addAll(icn64,icn48,icn32,icn24,icn16);
        stage.toFront();
        stage.show();
    }

    public static void main(String[] args) { launch(args); }

    /**
     * @return the stageWidth
     */
    @Contract(pure = true)
    private double getStageWidth() { return 940.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the stageHeight
     */
    @Contract(pure = true)
    private double getStageHeight() { return 660.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the textwidth
     */
    @Contract(pure = true)
    private double getTextWidth() { return 740.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the textheight
     */
    @Contract(pure = true)
    private double getTextHeight() { return 600.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the textwidth
     */
    @Contract(pure = true)
    private double getSawTextWidth() { return 675.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the textheight
     */
    @Contract(pure = true)
    private double getSawTextHeight() { return 615.0 / Screen.getMainScreen().getRenderScale(); }

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

    /**
     * @return the pathheight
     */
    @Contract(pure = true)
    private double getPathHeight() { return 660.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the pathheight
     */
    @Contract(pure = true)
    private double getSawHeight() { return 625.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the buttonWidth
     */
    @Contract(pure = true)
    private double getSawCompWidth() { return 205.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the buttonWidth
     */
    @Contract(pure = true)
    private double getSawCompHeight() { return 50.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the screenWidth
     */
    @Contract(pure = true)
    private double getScreenWidth() { return screenWidth; }

    /**
     * @param screenWidth the screenWidth to set
     */
    private void setScreenWidth(double screenWidth) { this.screenWidth = screenWidth; }

    /**
     * @return the screenHeight
     */
    @Contract(pure = true)
    private double getScreenHeight() { return screenHeight; }

    /**
     * @param screenHeight the screenHeight to set
     */
    private void setScreenHeight(double screenHeight) { this.screenHeight = screenHeight; }

    /**
     * @return the frame
     */
    @Contract(pure = true)
    private JFrame getFrame() { return frame; }

    /**
     * @param frame the frame to set
     */
    private void setFrame(JFrame frame) { this.frame = frame; }

    /**
     * @return the diffscalefactor
     */
    @Contract(pure = true)
    private double getDiffScalefactor() { return diffscalefactor; }

    /**
     */
    private void setDiffScalefactor() { this.diffscalefactor = 1.0; }

    /**
     */
    private void setNewdata() { this.newdata = false; }

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
     * @return the textAreaPath
     */
    @Contract(pure = true)
    private TextArea getTextAreaPath() { return this.textAreaPath; }

    /**
     * @param textarea the textAreaPath to set
     */
    private void setTextAreaPath(TextArea textarea) { this.textAreaPath = textarea; }

    /**
     * @return the textArea1Ddist
     */
    @Contract(pure = true)
    private TextArea getTextArea1Ddist() { return this.textArea1Ddist; }

    /**
     * @param textarea the textArea1Ddist to set
     */
    private void setTextArea1Ddist(TextArea textarea) { this.textArea1Ddist = textarea; }

    /**
     * @return the textAreaCalc
     */
    @Contract(pure = true)
    private TextArea getTextAreaCalc() { return this.textAreaCalc; }

    /**
     * @param textarea the textAreaCalc to set
     */
    private void setTextAreaCalc(TextArea textarea) { this.textAreaCalc = textarea; }

    /**
     * @return the textAreaReal
     */
    @Contract(pure = true)
    private TextArea getTextAreaReal() { return this.textAreaReal; }

    /**
     * @param textarea the textAreaReal to set
     */
    private void setTextAreaReal(TextArea textarea) { this.textAreaReal = textarea; }

    /**
     * @return the textAreaDiff
     */
    @Contract(pure = true)
    private TextArea getTextAreaDiff() { return this.textAreaDiff; }

    /**
     * @param textarea the textAreaDiff to set
     */
    private void setTextAreaDiff(TextArea textarea) { this.textAreaDiff = textarea; }

    /**
     * @return the textAreaSAW
     */
    @Contract(pure = true)
    private TextArea getTextAreaSAW() { return this.textAreaSAW; }

    /**
     * @param textarea the textAreaSAW to set
     */
    private void setTextAreaSAW(TextArea textarea) { this.textAreaSAW = textarea; }

    /**
     * @return the textAreaMenu
     */
    @Contract(pure = true)
    private TextArea getTextAreaMenu() { return this.textAreaMenu; }

    /**
     * @param textarea the textAreaMenu to set
     */
    private void setTextAreaMenu(TextArea textarea) { this.textAreaMenu = textarea; }

    /**
     * @return the isovalikkoMenu
     */
    @Contract(pure = true)
    private HBox getIsovalikkoMenu() { return this.isovalikkoMenu; }

    /**
     * @param hbox the isovalikkoMenu to set
     */
    private void setIsovalikkoMenu(HBox hbox) { this.isovalikkoMenu = hbox; }

    /**
     * @return the isovalikkoPath
     */
    @Contract(pure = true)
    private HBox getIsovalikkoPath() { return this.isovalikkoPath; }

    /**
     * @param hbox the isovalikkoPath to set
     */
    private void setIsovalikkoPath(HBox hbox) { this.isovalikkoPath = hbox; }

    /**
     * @return the isovalikkoCalc
     */
    @Contract(pure = true)
    private HBox getIsovalikkoCalc() { return this.isovalikkoCalc; }

    /**
     * @param hbox the isovalikkoCalc to set
     */
    private void setIsovalikkoCalc(HBox hbox) { this.isovalikkoCalc = hbox; }

    /**
     * @return the isovalikko1Ddist
     */
    @Contract(pure = true)
    private HBox getIsovalikko1Ddist() { return this.isovalikko1Ddist; }

    /**
     * @param hbox the isovalikko1Ddist to set
     */
    private void setIsovalikko1Ddist(HBox hbox) { this.isovalikko1Ddist = hbox; }

    /**
     * @return the isovalikkoReal
     */
    @Contract(pure = true)
    private HBox getIsovalikkoReal() { return this.isovalikkoReal; }

    /**
     * @param hbox the isovalikkoReal to set
     */
    private void setIsovalikkoReal(HBox hbox) { this.isovalikkoReal = hbox; }

    /**
     * @return the isovalikkoDiff
     */
    @Contract(pure = true)
    private HBox getIsovalikkoDiff() { return this.isovalikkoDiff; }

    /**
     * @param hbox the isovalikkoDiff to set
     */
    private void setIsovalikkoDiff(HBox hbox) { this.isovalikkoDiff = hbox; }

    /**
     * @return the isovalikkoSAW
     */
    @Contract(pure = true)
    private HBox getIsovalikkoSAW() { return this.isovalikkoSAW; }

    /**
     * @param hbox the isovalikkoSAW to set
     */
    private void setIsovalikkoSAW(HBox hbox) { this.isovalikkoSAW = hbox; }

    /**
     * @return the valikkoMenu
     */
    @Contract(pure = true)
    private VBox getValikkoMenu() { return this.valikkoMenu; }

    /**
     * @param vbox the valikkoMenu to set
     */
    private void setValikkoMenu(VBox vbox) { this.valikkoMenu = vbox; }

    /**
     * @return the valikkoPath
     */
    @Contract(pure = true)
    private VBox getValikkoPath() { return this.valikkoPath; }

    /**
     * @param vbox the valikkoPath to set
     */
    private void setValikkoPath(VBox vbox) { this.valikkoPath = vbox; }

    /**
     * @return the valikkoCalc
     */
    @Contract(pure = true)
    private VBox getValikkoCalc() { return this.valikkoCalc; }

    /**
     * @param vbox the valikkoCalc to set
     */
    private void setValikkoCalc(VBox vbox) { this.valikkoCalc = vbox; }

    /**
     * @return the valikko1Ddist
     */
    @Contract(pure = true)
    private VBox getValikko1Ddist() { return this.valikko1Ddist; }

    /**
     * @param vbox the valikko1Ddist to set
     */
    private void setValikko1Ddist(VBox vbox) { this.valikko1Ddist = vbox; }

    /**
     * @return the valikkoReal
     */
    @Contract(pure = true)
    private VBox getValikkoReal() { return this.valikkoReal; }

    /**
     * @param vbox the valikkoReal to set
     */
    private void setValikkoReal(VBox vbox) { this.valikkoReal = vbox; }

    /**
     * @return the valikkoDiff
     */
    @Contract(pure = true)
    private VBox getValikkoDiff() { return this.valikkoDiff; }

    /**
     * @param vbox the valikkoDiff to set
     */
    private void setValikkoDiff(VBox vbox) { this.valikkoDiff = vbox; }

    /**
     * @return the valikkoSAW
     */
    @Contract(pure = true)
    private VBox getValikkoSAW() { return this.valikkoSAW; }

    /**
     * @param vbox the valikkoSAW to set
     */
    private void setValikkoSAW(VBox vbox) { this.valikkoSAW = vbox; }

    /**
     * @return the calcpane
     */
    @Contract(pure = true)
    private Pane getCalcPane() { return this.calcpane; }

    /**
     * @param pane the calcpane to set
     */
    private void setCalcPane(Pane pane) { this.calcpane = pane; }

    /**
     * @return the realpane
     */
    @Contract(pure = true)
    private Pane getRealPane() { return this.realpane; }

    /**
     * @param pane the realpane to set
     */
    private void setRealPane(Pane pane) { this.realpane = pane; }

    /**
     * @return the diffpane
     */
    @Contract(pure = true)
    private Pane getDiffPane() { return this.diffpane; }

    /**
     * @param pane the diffpane to set
     */
    private void setDiffPane(Pane pane) { this.diffpane = pane; }

    /**
     * @return the sawpane
     */
    @Contract(pure = true)
    private Pane getSawPane() { return this.sawpane; }

    /**
     * @param pane the sawpane to set
     */
    private void setSawPane(Pane pane) { this.sawpane = pane; }

    /**
     * @return the asettelu
     */
    @Contract(pure = true)
    private GridPane getAsettelu() { return this.asettelu; }

    /**
     * @param asettelu the asettelu to set
     */
    private void setAsettelu(GridPane asettelu) { this.asettelu = asettelu; }

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
