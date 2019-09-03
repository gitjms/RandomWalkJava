package randomwalkjava;

import com.sun.glass.ui.Screen;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventType;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 */
@SuppressWarnings("SameReturnValue")
public class RandomWalk extends Application {

    private double screenWidth;
    private double screenHeight;
    private randomwalkjava.FXPlot fxplot;
    private JFrame frame;
    private String[] vars;
    private TextArea textAreaCalc;
    private TextArea textAreaPath;
    private TextArea textAreaReal;
    private TextArea textAreaMMC;
    private TextArea textArea1Ddist;
    private TextArea textAreaMenu;
    private double realscalefactor;
    private double mmcscalefactor;
    private double linewidth;
    private boolean isrealscaled;
    private boolean ismmcscaled;
    private boolean newdata;
    private boolean standnorm;
    private double mincount;
    private double maxcount;
    private double[] rms_runs;
    private double[] rms_norm;
    private List <Double> energy_x;
    private List <Double> energy_y;

    @Override
    public void start(Stage stage) {

        /*
         * initiate parameters
         */
        setScreenWidth(Toolkit.getDefaultToolkit().getScreenSize().width / Screen.getMainScreen().getRenderScale());
        setScreenHeight(Toolkit.getDefaultToolkit().getScreenSize().height / Screen.getMainScreen().getRenderScale());
        setRealScalefactor(1.0);
        setMmcScalefactor(1.0);
        setLinewidth(1.0);
        setRealScaled(false);
        setMmcScaled(false);
        setNewdata(false);
        setStandnorm(true);

        /*
        * FILE AND FOLDER CHECK
        * creates a folder C:/RWDATA if not exist
        * copies Fortran and Python executables from resources/ folder
        * to RWDATA folder if not in there already
        */
        String datapath = "C:/RWDATA";
        String fexec = "walk.exe";
        String pyexecrms = "plotrms.py";
        String pyexec1d = "plot1d.py";
        String pyexec2d = "plot2d.py";
        String pyexec3d = "plot3d.py";
        String pyexecmmc2d = "plotmmc2d.py";
        String pyexecmmc3d = "plotmmc3d.py";
        String pyexec1Ddist = "plot1Ddist.py";
        File datafolder = new File(datapath);
        File sourceFile = new File(datapath + "/" + fexec);

        if (Files.notExists(datafolder.toPath())){
            if (createFolder(datapath, fexec, true)) {
                Platform.exit(); return;
            }
            if (createFolder(datapath, pyexecrms, false)) {
                Platform.exit(); return;
            }
            if (createFolder(datapath, pyexec1d, false)) {
                Platform.exit(); return;
            }
            if (createFolder(datapath, pyexec2d, false)) {
                Platform.exit(); return;
            }
            if (createFolder(datapath, pyexec3d, false)) {
                Platform.exit(); return;
            }
            if (createFolder(datapath, pyexecmmc2d, false)) {
                Platform.exit(); return;
            }
            if (createFolder(datapath, pyexecmmc3d, false)) {
                Platform.exit(); return;
            }
            if (createFolder(datapath, pyexec1Ddist, false)) {
                Platform.exit(); return;
            }
        } else if (Files.notExists(sourceFile.toPath())) {
            if (createFolder(datapath, fexec, false)) {
                Platform.exit(); return;
            }
            sourceFile = new File(datapath + "/" + pyexecrms);
            if (Files.notExists(sourceFile.toPath())) {
                if (createFolder(datapath, pyexecrms, false)) {
                    Platform.exit(); return;
                }
            }
            sourceFile = new File(datapath + "/" + pyexec1d);
            if (Files.notExists(sourceFile.toPath())) {
                if (createFolder(datapath, pyexec1d, false)) {
                    Platform.exit(); return;
                }
            }
            sourceFile = new File(datapath + "/" + pyexec2d);
            if (Files.notExists(sourceFile.toPath())) {
                if (createFolder(datapath, pyexec2d, false)) {
                    Platform.exit(); return;
                }
            }
            sourceFile = new File(datapath + "/" + pyexec3d);
            if (Files.notExists(sourceFile.toPath())) {
                if (createFolder(datapath, pyexec3d, false)) {
                    Platform.exit(); return;
                }
            }
            sourceFile = new File(datapath + "/" + pyexecmmc2d);
            if (Files.notExists(sourceFile.toPath())) {
                if (createFolder(datapath, pyexecmmc2d, false)) {
                    Platform.exit(); return;
                }
            }
            sourceFile = new File(datapath + "/" + pyexecmmc3d);
            if (Files.notExists(sourceFile.toPath())) {
                if (createFolder(datapath, pyexecmmc3d, false)) {
                    Platform.exit(); return;
                }
            }
            sourceFile = new File(datapath + "/" + pyexec1Ddist);
            if (Files.notExists(sourceFile.toPath())) {
                if (createFolder(datapath, pyexec1Ddist, false)) {
                    Platform.exit(); return;
                }
            }
        }

        /*
        * CREATE STAGE
        */
        stage.setTitle("Random Walk");
        stage.setWidth(this.getStageWidth());
        stage.setHeight(this.getStageHeight());
        stage.setResizable(false);
        stage.setX(this.getScreenWidth()-this.getStageWidth()-10.0);
        stage.setY((this.getScreenHeight()-this.getStageHeight()) / 2.0);

        DropShadow shadow = new DropShadow();

        /*
        * SET FIRST VIEW BORDERPANE
        */
        GridPane asettelu = new GridPane();
        asettelu.setMaxWidth(this.getPaneWidth());
        asettelu.setVgap(5);
        asettelu.setHgap(10);
        asettelu.setPadding(new Insets(0, 0, 0, 0));

        /*
        * FIRST VIEW BUTTONS
        */
        Button nappiScene1 = new Button("PATH TRACING"); // ScenePathTracing
        Button nappiScene2 = new Button("1D DISTANCE"); // Scene1Ddist
        Button nappiScene3 = new Button("RMS vs SQRT(S)"); // SceneCalculation
        Button nappiScene4 = new Button("REAL TIME RMS"); // SceneRealTimeRms
        Button nappiScene5 = new Button("MMC DIFFUSION"); // SceneMMC
        nappiScene1.setMinWidth(this.getButtonWidth());
        nappiScene1.setMaxWidth(this.getButtonWidth());
        nappiScene2.setMinWidth(this.getButtonWidth());
        nappiScene2.setMaxWidth(this.getButtonWidth());
        nappiScene3.setMinWidth(this.getButtonWidth());
        nappiScene3.setMaxWidth(this.getButtonWidth());
        nappiScene4.setMinWidth(this.getButtonWidth());
        nappiScene4.setMaxWidth(this.getButtonWidth());
        nappiScene5.setMinWidth(this.getButtonWidth());
        nappiScene5.setMaxWidth(this.getButtonWidth());

        Button nappiMenuHelp = new Button("HELP");
        nappiMenuHelp.setMinWidth(this.getButtonWidth());
        nappiMenuHelp.setMaxWidth(this.getButtonWidth());

        /*
         * GRIDPANES: ScenePathTracing
         */
        GridPane.setHalignment(nappiScene1, HPos.LEFT);
        asettelu.add(nappiScene1, 0, 0, 2, 1);
        nappiScene1.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        nappiScene1.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> nappiScene1.setEffect(shadow));
        nappiScene1.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> nappiScene1.setEffect(null));
        nappiScene1.setVisible(true);

        final Pane empty1 = new Pane();
        GridPane.setHalignment(empty1, HPos.LEFT);
        asettelu.add(empty1, 0, 1, 2, 1);

        /*
         * GRIDPANES: Scene1Ddist
         */
        GridPane.setHalignment(nappiScene2, HPos.LEFT);
        asettelu.add(nappiScene2, 0, 2, 2, 1);
        nappiScene2.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        nappiScene2.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> nappiScene2.setEffect(shadow));
        nappiScene2.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> nappiScene2.setEffect(null));
        nappiScene2.setVisible(true);

        final Pane empty2 = new Pane();
        GridPane.setHalignment(empty2, HPos.LEFT);
        asettelu.add(empty2, 0, 3, 2, 1);

        /*
        * GRIDPANES: SceneCalculation
         */
        GridPane.setHalignment(nappiScene3, HPos.LEFT);
        asettelu.add(nappiScene3, 0, 4, 2, 1);
        nappiScene3.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        nappiScene3.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> nappiScene3.setEffect(shadow));
        nappiScene3.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> nappiScene3.setEffect(null));
        nappiScene3.setVisible(true);

        final Pane empty3 = new Pane();
        GridPane.setHalignment(empty3, HPos.LEFT);
        asettelu.add(empty3, 0, 5, 2, 1);

        /*
         * GRIDPANES: SceneRealTimeRms
         */
        GridPane.setHalignment(nappiScene4, HPos.LEFT);
        asettelu.add(nappiScene4, 0, 6, 2, 1);
        nappiScene4.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        nappiScene4.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> nappiScene4.setEffect(shadow));
        nappiScene4.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> nappiScene4.setEffect(null));
        nappiScene4.setVisible(true);

        final Pane empty4 = new Pane();
        GridPane.setHalignment(empty4, HPos.LEFT);
        asettelu.add(empty4, 0, 7, 2, 1);

        /*
         * GRIDPANES: SceneMMC
         */
        GridPane.setHalignment(nappiScene5, HPos.LEFT);
        asettelu.add(nappiScene5, 0, 8, 2, 1);
        nappiScene5.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        nappiScene5.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> nappiScene5.setEffect(shadow));
        nappiScene5.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> nappiScene5.setEffect(null));
        nappiScene5.setVisible(true);

        final Pane empty5 = new Pane();
        GridPane.setHalignment(empty5, HPos.LEFT);
        asettelu.add(empty5, 0, 9, 2, 1);

        asettelu.add(nappiMenuHelp, 0, 10, 2, 1);
        nappiMenuHelp.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        nappiMenuHelp.setVisible(true);

        /*
        * OTHER COMPONENTS
        */
        BorderPane asetteluMenu = new BorderPane();
        HBox isovalikkoMenu = new HBox();
        isovalikkoMenu.setPadding(new Insets(0, 0, 0, 0));
        isovalikkoMenu.setSpacing(10);
        VBox valikkoMenu = new VBox();
        valikkoMenu.setPadding(new Insets(10, 10, 10, 10));
        valikkoMenu.setSpacing(10);

        /*
        * OTHER VIEWS
        */
        SceneCalculation getCalcScene = new SceneCalculation();
        ScenePathTracing getPathScene = new ScenePathTracing();
        SceneRealTimeRms getRealScene = new SceneRealTimeRms();
        SceneMMC getMMCScene = new SceneMMC();
        Scene1Ddist get1DdistScene = new Scene1Ddist();

        BorderPane asetteluCalc = new BorderPane();
        BorderPane asetteluPath = new BorderPane();
        BorderPane asetteluReal = new BorderPane();
        BorderPane asetteluMMC = new BorderPane();
        BorderPane asettelu1Ddist = new BorderPane();

        HBox isovalikkoCalc = new HBox();
        isovalikkoCalc.setPadding(new Insets(0, 0, 0, 0));
        isovalikkoCalc.setSpacing(0);
        
        HBox isovalikkoPath = new HBox();
        isovalikkoPath.setPadding(new Insets(0, 0, 0, 0));
        isovalikkoPath.setSpacing(0);

        HBox isovalikkoReal = new HBox();
        isovalikkoReal.setPadding(new Insets(0, 0, 0, 0));
        isovalikkoReal.setSpacing(0);

        HBox isovalikkoMMC = new HBox();
        isovalikkoMMC.setPadding(new Insets(0, 0, 0, 0));
        isovalikkoMMC.setSpacing(0);

        HBox isovalikko1Ddist = new HBox();
        isovalikko1Ddist.setPadding(new Insets(0, 0, 0, 0));
        isovalikko1Ddist.setSpacing(0);

        VBox valikkoCalc = new VBox();
        valikkoCalc.setPadding(new Insets(10, 10, 10, 10));
        valikkoCalc.setSpacing(20);

        VBox valikkoPath = new VBox();
        valikkoPath.setPadding(new Insets(10, 10, 10, 10));
        valikkoPath.setSpacing(20);

        VBox valikkoReal = new VBox();
        valikkoReal.setPadding(new Insets(10, 10, 10, 10));
        valikkoReal.setSpacing(20);

        VBox valikkoMMC = new VBox();
        valikkoMMC.setPadding(new Insets(10, 10, 10, 10));
        valikkoMMC.setSpacing(20);

        VBox valikko1Ddist = new VBox();
        valikko1Ddist.setPadding(new Insets(10, 10, 10, 10));
        valikko1Ddist.setSpacing(20);

        /*
        * TEXT AREAS
        */
        HelpText helpText = new HelpText();
        /*
        * CALCULATION TEXT AREA
        */
        this.textAreaCalc = new TextArea();
        this.textAreaCalc.setMinWidth(this.getTextWidth());
        this.textAreaCalc.setMaxWidth(this.getTextWidth());
        this.textAreaCalc.setMinHeight(this.getTextHeight());
        this.textAreaCalc.setMaxHeight(this.getTextHeight());
        this.textAreaCalc.setFont(Font.font("Consolas",FontWeight.NORMAL, 18));
        this.textAreaCalc.setBorder(null);
        this.textAreaCalc.setEditable(false);
        this.textAreaCalc.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.textAreaCalc.setBlendMode(BlendMode.DIFFERENCE);

        /*
        * PATH TRACING TEXT AREA
        */
        this.textAreaPath = new TextArea();
        this.textAreaPath.setMinWidth(this.getTextWidth());
        this.textAreaPath.setMaxWidth(this.getTextWidth());
        this.textAreaPath.setMinHeight(this.getPathHeight());
        this.textAreaPath.setMaxHeight(this.getPathHeight());
        this.textAreaPath.setFont(Font.font("Consolas",FontWeight.NORMAL, 18));
        this.textAreaPath.setBorder(null);
        this.textAreaPath.setEditable(false);
        this.textAreaPath.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.textAreaPath.setBlendMode(BlendMode.DIFFERENCE);

        /*
        * REAL TIME RMS TEXT AREA
        */
        this.textAreaReal = new TextArea();
        this.textAreaReal.setMinWidth(this.getAnimWidth());
        this.textAreaReal.setMaxWidth(this.getAnimWidth());
        this.textAreaReal.setMinHeight(this.getAnimHeight());
        this.textAreaReal.setMaxHeight(this.getAnimHeight());
        this.textAreaReal.setFont(Font.font("Consolas",FontWeight.NORMAL, 18));
        this.textAreaReal.setBorder(null);
        this.textAreaReal.setEditable(false);
        this.textAreaReal.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.textAreaReal.setBlendMode(BlendMode.DIFFERENCE);
        /*
        * MMC TEXT AREA
        */
        this.textAreaMMC = new TextArea();
        this.textAreaMMC.setMinWidth(this.getAnimWidth());
        this.textAreaMMC.setMaxWidth(this.getAnimWidth());
        this.textAreaMMC.setMinHeight(this.getAnimHeight());
        this.textAreaMMC.setMaxHeight(this.getAnimHeight());
        this.textAreaMMC.setFont(Font.font("Consolas",FontWeight.NORMAL, 18));
        this.textAreaMMC.setBorder(null);
        this.textAreaMMC.setEditable(false);
        this.textAreaMMC.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.textAreaMMC.setBlendMode(BlendMode.DIFFERENCE);
        /*
         * 1D DISTANCE TEXT AREA
         */
        this.textArea1Ddist = new TextArea();
        this.textArea1Ddist.setMinWidth(this.getTextWidth());
        this.textArea1Ddist.setMaxWidth(this.getTextWidth());
        this.textArea1Ddist.setMinHeight(this.getTextHeight());
        this.textArea1Ddist.setMaxHeight(this.getTextHeight());
        this.textArea1Ddist.setFont(Font.font("Consolas",FontWeight.NORMAL, 18));
        this.textArea1Ddist.setBorder(null);
        this.textArea1Ddist.setEditable(false);
        this.textArea1Ddist.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.textArea1Ddist.setBlendMode(BlendMode.DIFFERENCE);

        /*
        * TEXT AREA MENU
        */
        this.textAreaMenu = new TextArea(helpText.welcome());
        this.textAreaMenu.setMinWidth(this.getTextWidth());
        this.textAreaMenu.setMaxWidth(this.getTextWidth());
        this.textAreaMenu.setMinHeight(this.getTextHeight());
        this.textAreaMenu.setMaxHeight(this.getTextHeight());
        this.textAreaMenu.setFont(Font.font("Consolas",FontWeight.NORMAL, 18));
        this.textAreaMenu.setBorder(null);
        this.textAreaMenu.setEditable(false);
        this.textAreaMenu.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.textAreaMenu.setBlendMode(BlendMode.DIFFERENCE);
        
        /*
        * REAL TIME RMS COMPONENTS
        */
        Canvas rtrmsAlusta = new Canvas(this.getAnimWidth(), this.getAnimHeight());
        rtrmsAlusta.setVisible(true);

        GraphicsContext piirturi = rtrmsAlusta.getGraphicsContext2D();
        piirturi.setFill(Color.BLACK);
        piirturi.fillRect(0, 0, this.getAnimWidth(), this.getAnimHeight());

        Pane pane = new Pane();
        pane.setPrefSize(this.getAnimWidth(), this.getAnimHeight());
        pane.getChildren().add(rtrmsAlusta);
        pane.setVisible(true);
        
        /*
        * MMC COMPONENTS
        */
        Canvas mmcAlusta = new Canvas(this.getAnimWidth(), this.getAnimHeight());
        mmcAlusta.setVisible(true);

        GraphicsContext mmcpiirturi = mmcAlusta.getGraphicsContext2D();
        mmcpiirturi.setFill(Color.BLACK);
        mmcpiirturi.fillRect(0, 0, this.getAnimWidth(), this.getAnimHeight());
        mmcpiirturi.setStroke(Color.YELLOW);

        Pane mmcpane = new Pane();
        mmcpane.setPrefSize(this.getAnimWidth(), this.getAnimHeight());
        mmcpane.getChildren().add(mmcAlusta);
        mmcpane.setVisible(true);

        /*
        * FIRST VIEW BUTTON: HELP
        */
        nappiMenuHelp.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> nappiMenuHelp.setEffect(shadow));
        nappiMenuHelp.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> nappiMenuHelp.setEffect(null));
        nappiMenuHelp.setOnAction(event -> this.textAreaMenu.setText(helpText.menu()));

        /*
        * FIRST VIEW BUTTON: CLOSE
        */
        Button closeNappiMenu = new Button("CLOSE");
        closeNappiMenu.setMinWidth(this.getButtonWidth());
        closeNappiMenu.setMaxWidth(this.getButtonWidth());
        closeNappiMenu.setTextFill(Color.RED);
        closeNappiMenu.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        GridPane.setHalignment(closeNappiMenu, HPos.LEFT);
        closeNappiMenu.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> closeNappiMenu.setEffect(shadow));
        closeNappiMenu.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> closeNappiMenu.setEffect(null));
        closeNappiMenu.setOnAction(event -> {
            Alert alert;
            alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Close application?",
                ButtonType.OK, ButtonType.CANCEL);
            alert.showAndWait();
            if ( alert.getResult() == ButtonType.OK ) stage.close();
        });
        closeNappiMenu.setVisible(true);

        /*
        * OTHER VIEWS BUTTON: EXECUTE CALCULATION
        */
        Button executeNappiCalc = new Button("EXECUTE");
        executeNappiCalc.setDefaultButton(true);
        executeNappiCalc.setMinWidth(this.getButtonWidth());
        executeNappiCalc.setMaxWidth(this.getButtonWidth());
        executeNappiCalc.setStyle("-fx-background-color: Red");
        executeNappiCalc.setTextFill(Color.WHITE);
        executeNappiCalc.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> executeNappiCalc.setEffect(shadow));
        executeNappiCalc.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> executeNappiCalc.setEffect(null));
        executeNappiCalc.setVisible(true);

        /*
        * OTHER VIEWS BUTTON: CALCULATION MENU
        */
        Button menuNappiCalc = new Button("BACK TO MENU");
        menuNappiCalc.setMinWidth(this.getButtonWidth());
        menuNappiCalc.setMaxWidth(this.getButtonWidth());
        menuNappiCalc.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> menuNappiCalc.setEffect(shadow));
        menuNappiCalc.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> menuNappiCalc.setEffect(null));
        menuNappiCalc.setVisible(true);

        /*
        * OTHER VIEWS BUTTON: CALCULATION HELP
        */
        Button helpNappiCalc = new Button("HELP");
        helpNappiCalc.setMinWidth(this.getButtonWidth());
        helpNappiCalc.setMaxWidth(this.getButtonWidth());
        GridPane.setHalignment(helpNappiCalc, HPos.LEFT);
        helpNappiCalc.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> helpNappiCalc.setEffect(shadow));
        helpNappiCalc.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> helpNappiCalc.setEffect(null));
        helpNappiCalc.setOnAction(event -> this.textAreaCalc.setText(helpText.calculation()));
        helpNappiCalc.setVisible(true);

        /*
        * OTHER VIEWS BUTTON: CALCULATION CLOSE
        */
        Button closeNappiCalc = new Button("CLOSE");
        closeNappiCalc.setMinWidth(this.getButtonWidth());
        closeNappiCalc.setMaxWidth(this.getButtonWidth());
        closeNappiCalc.setTextFill(Color.RED);
        closeNappiCalc.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        GridPane.setHalignment(closeNappiCalc, HPos.LEFT);
        closeNappiCalc.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> closeNappiCalc.setEffect(shadow));
        closeNappiCalc.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> closeNappiCalc.setEffect(null));
        closeNappiCalc.setOnAction(event -> {
            Alert alert;
            alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Close application?",
                ButtonType.OK, ButtonType.CANCEL);
            alert.showAndWait();
            if ( alert.getResult() == ButtonType.OK ) {
                if (this.getFrame() != null) if (this.getFrame().isShowing()
                    || this.getFrame().isActive()
                    || this.getFrame().isDisplayable())
                    this.getFrame().dispose();
                stage.close();
            }
        });
        closeNappiCalc.setVisible(true);

        /*
        * OTHER VIEWS BUTTON: EXECUTE PATH TRACING
        */
        Button executeNappiPath = new Button("EXECUTE");
        executeNappiPath.setDefaultButton(true);
        executeNappiPath.setMinWidth(this.getButtonWidth());
        executeNappiPath.setMaxWidth(this.getButtonWidth());
        executeNappiPath.setStyle("-fx-background-color: Red");
        executeNappiPath.setTextFill(Color.WHITE);
        executeNappiPath.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> executeNappiPath.setEffect(shadow));
        executeNappiPath.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> executeNappiPath.setEffect(null));
        executeNappiPath.setVisible(true);

        /*
        * OTHER VIEWS BUTTON: PATH TRACING MENU
        */
        Button menuNappiPath = new Button("BACK TO MENU");
        menuNappiPath.setMinWidth(this.getButtonWidth());
        menuNappiPath.setMaxWidth(this.getButtonWidth());
        menuNappiPath.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> menuNappiPath.setEffect(shadow));
        menuNappiPath.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> menuNappiPath.setEffect(null));
        menuNappiPath.setVisible(true);

        /*
        * OTHER VIEWS BUTTON: PATH TRACING HELP
        */
        Button helpNappiPath = new Button("HELP");
        helpNappiPath.setMinWidth(this.getButtonWidth());
        helpNappiPath.setMaxWidth(this.getButtonWidth());
        helpNappiPath.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> helpNappiPath.setEffect(shadow));
        helpNappiPath.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> helpNappiPath.setEffect(null));
        helpNappiPath.setOnAction(event -> this.textAreaPath.setText(helpText.pathtracing()));
        helpNappiPath.setVisible(true);

        /*
        * OTHER VIEWS BUTTON: PATH TRACING CLOSE
        */
        Button closeNappiPath = new Button("CLOSE");
        closeNappiPath.setMinWidth(this.getButtonWidth());
        closeNappiPath.setMaxWidth(this.getButtonWidth());
        closeNappiPath.setTextFill(Color.RED);
        closeNappiPath.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        GridPane.setHalignment(closeNappiPath, HPos.LEFT);
        closeNappiPath.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> closeNappiPath.setEffect(shadow));
        closeNappiPath.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> closeNappiPath.setEffect(null));
        closeNappiPath.setOnAction(event -> {
            Alert alert;
            alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Close application?",
                ButtonType.OK, ButtonType.CANCEL);
            alert.showAndWait();
            if ( alert.getResult() == ButtonType.OK ) {
                if (this.getFrame() != null) if (this.getFrame().isShowing()
                    || this.getFrame().isActive()
                    || this.getFrame().isDisplayable())
                    this.getFrame().dispose();
                stage.close();
            }
        });
        closeNappiPath.setVisible(true);

        /*
        * OTHER VIEWS BUTTON: PLOT CHOICE REAL TIME RMS
        */
        Button standNorm = new Button("STD NORM");
        standNorm.setMinWidth(this.getButtonWidth());
        standNorm.setMaxWidth(this.getButtonWidth());
        standNorm.setBackground(new Background(
            new BackgroundFill(
                Color.GOLD,CornerRadii.EMPTY,Insets.EMPTY)));
        standNorm.setId("standnorm");
        standNorm.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> standNorm.setEffect(shadow));
        standNorm.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> standNorm.setEffect(null));
        standNorm.setOnMouseClicked((MouseEvent event) -> {
            if (standNorm.getText().equals("NORM")){
                // BUTTON PRESSED ON
                standNorm.setText("STD NORM");
                standNorm.setBackground(
                    new Background(
                        new BackgroundFill(
                            Color.GOLD,CornerRadii.EMPTY,Insets.EMPTY)));
                this.setStandnorm(true);
            } else if (standNorm.getText().equals("STD NORM")) {
                // BUTTON PRESSED OFF
                standNorm.setText("NORM");
                standNorm.setBackground(
                    new Background(new BackgroundFill(
                        Color.LIGHTSKYBLUE,CornerRadii.EMPTY,Insets.EMPTY)));
                this.setStandnorm(false);
            }
        });
        standNorm.setVisible(true);

        /*
        * OTHER VIEWS BUTTON: RUN REAL TIME RMS
        */
        Button runReal = new Button("RUN");
        runReal.setDefaultButton(true);
        runReal.setMinWidth(this.getButtonWidth());
        runReal.setMaxWidth(this.getButtonWidth());
        runReal.setStyle("-fx-background-color: Red");
        runReal.setTextFill(Color.WHITE);
        runReal.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> runReal.setEffect(shadow));
        runReal.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> runReal.setEffect(null));
        runReal.setVisible(true);

        /*
        * OTHER VIEWS BUTTON: REAL TIME RMS MENU
        */
        Button menuNappiReal = new Button("BACK TO MENU");
        menuNappiReal.setMinWidth(this.getButtonWidth());
        menuNappiReal.setMaxWidth(this.getButtonWidth());
        menuNappiReal.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> menuNappiReal.setEffect(shadow));
        menuNappiReal.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> menuNappiReal.setEffect(null));
        menuNappiReal.setVisible(true);

        /*
        * OTHER VIEWS BUTTON: REAL TIME RMS HELP
        */
        Button helpNappiReal = new Button("HELP");
        helpNappiReal.setMinWidth(this.getButtonWidth());
        helpNappiReal.setMaxWidth(this.getButtonWidth());
        GridPane.setHalignment(helpNappiReal, HPos.LEFT);
        helpNappiReal.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> helpNappiReal.setEffect(shadow));
        helpNappiReal.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> helpNappiReal.setEffect(null));
        helpNappiReal.setOnAction(event -> {
            if (isovalikkoReal.getChildren().contains(pane)){
                isovalikkoReal.getChildren().remove(pane);
                isovalikkoReal.getChildren().add(this.textAreaReal);
            }
            this.textAreaReal.setText(helpText.realtimerms());
        });
        helpNappiReal.setVisible(true);

        /*
        * OTHER VIEWS BUTTON: REAL TIME RMS CLOSE
        */
        Button closeNappiReal = new Button("CLOSE");
        closeNappiReal.setMinWidth(this.getButtonWidth());
        closeNappiReal.setMaxWidth(this.getButtonWidth());
        closeNappiReal.setTextFill(Color.RED);
        closeNappiReal.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        GridPane.setHalignment(closeNappiReal, HPos.LEFT);
        closeNappiReal.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> closeNappiReal.setEffect(shadow));
        closeNappiReal.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> closeNappiReal.setEffect(null));
        closeNappiReal.setOnAction(event -> {
            if ( !getRealScene.isRunning() ) {
                Alert alert;
                alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Close application?",
                    ButtonType.OK, ButtonType.CANCEL);
                alert.showAndWait();
                if ( alert.getResult() == ButtonType.OK ) stage.close();
            }
        });
        closeNappiReal.setVisible(true);

        /*
        * OTHER VIEWS BUTTON: EXECUTE MMC
        */
        Button runMMC = new Button("ANIMATION");
        runMMC.setDefaultButton(true);
        runMMC.setMinWidth(this.getButtonWidth());
        runMMC.setMaxWidth(this.getButtonWidth());
        runMMC.setStyle("-fx-background-color: Red");
        runMMC.setTextFill(Color.WHITE);
        runMMC.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> runMMC.setEffect(shadow));
        runMMC.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> runMMC.setEffect(null));
        runMMC.setVisible(true);

        /*
        * OTHER VIEWS BUTTON: PLOT MMC
        */
        Button plotMMC = new Button("PLOT");
        plotMMC.setDefaultButton(true);
        plotMMC.setMinWidth(this.getButtonWidth());
        plotMMC.setMaxWidth(this.getButtonWidth());
        plotMMC.setStyle("-fx-background-color: Blue");
        plotMMC.setTextFill(Color.WHITE);
        plotMMC.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> plotMMC.setEffect(shadow));
        plotMMC.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> plotMMC.setEffect(null));
        plotMMC.setVisible(true);

        /*
        * OTHER VIEWS BUTTON: MMC MENU
        */
        Button menuNappiMMC = new Button("BACK TO MENU");
        menuNappiMMC.setMinWidth(this.getButtonWidth());
        menuNappiMMC.setMaxWidth(this.getButtonWidth());
        menuNappiMMC.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> menuNappiMMC.setEffect(shadow));
        menuNappiMMC.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> menuNappiMMC.setEffect(null));
        menuNappiMMC.setVisible(true);

        /*
        * OTHER VIEWS BUTTON: MMC HELP
        */
        Button helpNappiMMC = new Button("HELP");
        helpNappiMMC.setMinWidth(this.getButtonWidth());
        helpNappiMMC.setMaxWidth(this.getButtonWidth());
        GridPane.setHalignment(helpNappiMMC, HPos.LEFT);
        helpNappiMMC.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> helpNappiMMC.setEffect(shadow));
        helpNappiMMC.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> helpNappiMMC.setEffect(null));
        helpNappiMMC.setOnAction(event -> {
            if (isovalikkoMMC.getChildren().contains(pane)){
                isovalikkoMMC.getChildren().remove(pane);
                isovalikkoMMC.getChildren().add(this.textAreaMMC);
            }
            this.textAreaMMC.setText(helpText.mmc());
        });
        helpNappiMMC.setVisible(true);

        /*
        * OTHER VIEWS BUTTON: MMC REMOVE BARRIER
        */
        Button remBarNappiMMC = new Button("CONTINUE");
        remBarNappiMMC.setMinWidth(this.getButtonWidth());
        remBarNappiMMC.setMaxWidth(this.getButtonWidth());
        remBarNappiMMC.setTextFill(Color.BLACK);
        remBarNappiMMC.setBackground(new Background(
            new BackgroundFill(
                Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
        GridPane.setHalignment(remBarNappiMMC, HPos.LEFT);
        remBarNappiMMC.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> remBarNappiMMC.setEffect(shadow));
        remBarNappiMMC.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> remBarNappiMMC.setEffect(null));
        remBarNappiMMC.setVisible(false);

        /*
        * OTHER VIEWS BUTTON: MMC CLOSE
        */
        Button closeNappiMMC = new Button("CLOSE");
        closeNappiMMC.setMinWidth(this.getButtonWidth());
        closeNappiMMC.setMaxWidth(this.getButtonWidth());
        closeNappiMMC.setTextFill(Color.RED);
        closeNappiMMC.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        GridPane.setHalignment(closeNappiMMC, HPos.LEFT);
        closeNappiMMC.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> closeNappiMMC.setEffect(shadow));
        closeNappiMMC.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> closeNappiMMC.setEffect(null));
        closeNappiMMC.setOnAction(event -> {
            if ( getMMCScene.timerIsRunning() && getMMCScene.barrierIsOn() ) {
                if ( getMMCScene.walkState() ) {
                    PrintWriter pw = null;
                    if (getMMCScene.getProcOut() != null)
                        pw = new PrintWriter(getMMCScene.getProcOut());
                    if (pw != null) {
                        pw.println("-");
                        pw.flush();
                        pw.close();
                    }
                }
                Alert alert;
                alert = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Close application?",
                    ButtonType.OK, ButtonType.CANCEL);
                alert.showAndWait();
                if ( alert.getResult() == ButtonType.OK ) {
                    if (this.getFrame() != null) if (this.getFrame().isShowing()
                        || this.getFrame().isActive()
                        || this.getFrame().isDisplayable())
                        this.getFrame().dispose();
                    stage.close();
                }
            } else if ( !getMMCScene.timerIsRunning() && !getMMCScene.barrierIsOn() ) {
                Alert alert;
                alert = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Close application?",
                    ButtonType.OK, ButtonType.CANCEL);
                alert.showAndWait();
                if ( alert.getResult() == ButtonType.OK ) {
                    stage.close();
                }
            }
        });
        closeNappiMMC.setVisible(true);

        /*
         * OTHER VIEWS BUTTON: EXECUTE 1D DISTANCE
         */
        Button executeNappi1Ddist = new Button("EXECUTE");
        executeNappi1Ddist.setDefaultButton(true);
        executeNappi1Ddist.setMinWidth(this.getButtonWidth());
        executeNappi1Ddist.setMaxWidth(this.getButtonWidth());
        executeNappi1Ddist.setStyle("-fx-background-color: Red");
        executeNappi1Ddist.setTextFill(Color.WHITE);
        executeNappi1Ddist.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> executeNappi1Ddist.setEffect(shadow));
        executeNappi1Ddist.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> executeNappi1Ddist.setEffect(null));
        executeNappi1Ddist.setVisible(true);

        /*
         * OTHER VIEWS BUTTON: 1D DISTANCE MENU
         */
        Button menuNappi1Ddist = new Button("BACK TO MENU");
        menuNappi1Ddist.setMinWidth(this.getButtonWidth());
        menuNappi1Ddist.setMaxWidth(this.getButtonWidth());
        menuNappi1Ddist.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> menuNappi1Ddist.setEffect(shadow));
        menuNappi1Ddist.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> menuNappi1Ddist.setEffect(null));
        menuNappi1Ddist.setVisible(true);

        /*
         * OTHER VIEWS BUTTON: 1D DISTANCE HELP
         */
        Button helpNappi1Ddist = new Button("HELP");
        helpNappi1Ddist.setMinWidth(this.getButtonWidth());
        helpNappi1Ddist.setMaxWidth(this.getButtonWidth());
        helpNappi1Ddist.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> helpNappi1Ddist.setEffect(shadow));
        helpNappi1Ddist.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> helpNappi1Ddist.setEffect(null));
        helpNappi1Ddist.setOnAction(event -> this.textArea1Ddist.setText(helpText.distance1D()));
        helpNappi1Ddist.setVisible(true);

        /*
         * OTHER VIEWS BUTTON: 1D DISTANCE CLOSE
         */
        Button closeNappi1Ddist = new Button("CLOSE");
        closeNappi1Ddist.setMinWidth(this.getButtonWidth());
        closeNappi1Ddist.setMaxWidth(this.getButtonWidth());
        closeNappi1Ddist.setTextFill(Color.RED);
        closeNappi1Ddist.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        GridPane.setHalignment(closeNappi1Ddist, HPos.LEFT);
        closeNappi1Ddist.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> closeNappi1Ddist.setEffect(shadow));
        closeNappi1Ddist.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> closeNappi1Ddist.setEffect(null));
        closeNappi1Ddist.setOnAction(event -> {
            Alert alert;
            alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Close application?",
                ButtonType.OK, ButtonType.CANCEL);
            alert.showAndWait();
            if ( alert.getResult() == ButtonType.OK ) {
                if (this.getFrame() != null) if (this.getFrame().isShowing()
                    || this.getFrame().isActive()
                    || this.getFrame().isDisplayable())
                    this.getFrame().dispose();
                stage.close();
            }
        });
        closeNappi1Ddist.setVisible(true);

        /*
        * SET FIRST VIEW BORDERPANE
        */
        valikkoMenu.getChildren().addAll(
            asettelu,
            nappiMenuHelp,
            closeNappiMenu);
        isovalikkoMenu.getChildren().addAll(
            valikkoMenu,
            this.textAreaMenu);
        asetteluMenu.setCenter(isovalikkoMenu);

        /*
        * SET CALCULATION BORDERPANE
        */
        valikkoCalc.getChildren().addAll(
            menuNappiCalc,
            helpNappiCalc,
            getCalcScene.getSceneCalc(),
            executeNappiCalc,
            closeNappiCalc);
        isovalikkoCalc.getChildren().addAll(
            valikkoCalc,
            this.textAreaCalc);
        asetteluCalc.setCenter(isovalikkoCalc);

        /*
        * SET PATH TRACING BORDERPANE
        */
        valikkoPath.getChildren().addAll(
            menuNappiPath,
            helpNappiPath,
            getPathScene.getScenePath(),
            executeNappiPath,
            closeNappiPath);
        isovalikkoPath.getChildren().addAll(
            valikkoPath,
            this.textAreaPath);
        asetteluPath.setCenter(isovalikkoPath);

        /*
        * SET REAL TIME RMS BORDERPANE
        */
        valikkoReal.getChildren().addAll(
            menuNappiReal,
            helpNappiReal,
            getRealScene.getSceneReal(),
            standNorm,
            runReal,
            closeNappiReal);
        isovalikkoReal.getChildren().addAll(
            valikkoReal,
            this.textAreaReal);
        asetteluReal.setCenter(isovalikkoReal);

        /*
        * SET MMC BORDERPANE
        */
        valikkoMMC.getChildren().addAll(
            menuNappiMMC,
            helpNappiMMC,
            getMMCScene.getSceneMMC(),
            runMMC,
            plotMMC,
            remBarNappiMMC,
            closeNappiMMC);
        isovalikkoMMC.getChildren().addAll(
            valikkoMMC,
            this.textAreaMMC);
        asetteluMMC.setCenter(isovalikkoMMC);

        /*
         * SET 1D DISTANCE BORDERPANE
         */
        valikko1Ddist.getChildren().addAll(
            menuNappi1Ddist,
            helpNappi1Ddist,
            get1DdistScene.getScene1Ddist(),
            executeNappi1Ddist,
            closeNappi1Ddist);
        isovalikko1Ddist.getChildren().addAll(
            valikko1Ddist,
            this.textArea1Ddist);
        asettelu1Ddist.setCenter(isovalikko1Ddist);

        /*
        * SET SCENES
        */
        Scene firstScene = new Scene(asetteluMenu, this.getStageWidth(), this.getStageHeight());
        firstScene.getStylesheets().add("/Styles.css");

        Scene calcScene = new Scene(asetteluCalc, this.getStageWidth(), this.getStageHeight());
        calcScene.getStylesheets().add("/Styles.css");

        Scene pathScene = new Scene(asetteluPath, this.getStageWidth(),this.getStageHeight()
            + (this.getPathHeight()-this.getTextHeight()));
        pathScene.getStylesheets().add("/Styles.css");

        Scene realScene = new Scene(asetteluReal,
            this.getStageWidth() + (this.getAnimWidth()-this.getTextWidth()),
            this.getStageHeight() + (this.getAnimHeight()-this.getTextHeight()));
        realScene.getStylesheets().add("/Styles.css");

        Scene mmcScene = new Scene(asetteluMMC,
            this.getStageWidth() + (this.getAnimWidth()-this.getTextWidth()),
            this.getStageHeight() + (this.getAnimHeight()-this.getTextHeight()));
        mmcScene.getStylesheets().add("/Styles.css");

        Scene dist1DScene = new Scene(asettelu1Ddist, this.getStageWidth(), this.getStageHeight());
        dist1DScene.getStylesheets().add("/Styles.css");

        /*
         * SET SCENE CHOICE BUTTONS' EFFECTS
         * PATH TRACING
         */
        nappiScene1.setOnMouseClicked(event -> {
            stage.setTitle("Path Tracing");
            if ( stage.getHeight() == this.getStageHeight() ){
                stage.setHeight(
                    this.getStageHeight()+(this.getPathHeight()-this.getTextHeight()));
                stage.setY(
                    (this.getScreenHeight()-this.getStageHeight())/2.0
                        - (this.getPathHeight()-this.getTextHeight())/2.0);
            }
            stage.setScene(pathScene);
        });
        menuNappiPath.setOnAction(event -> {
            stage.setTitle("Random Walk");
            if (this.textAreaPath.getText().equals(helpText.pathtracing()))
                this.textAreaMenu.setText(helpText.welcome());
            else
                this.textAreaMenu.setText(this.textAreaPath.getText());
            stage.setY((this.getScreenHeight()-this.getStageHeight())/2.0);
            stage.setHeight(this.getStageHeight());
            stage.setScene(firstScene);
        });
        /*
         * 1D DISTANCE
         */
        nappiScene2.setOnMouseClicked(event -> {
            stage.setTitle("1D Distance");
            stage.setScene(dist1DScene);
        });
        menuNappi1Ddist.setOnAction(event -> {
            stage.setTitle("Random Walk");
            if (this.textArea1Ddist.getText().equals(helpText.distance1D()))
                this.textAreaMenu.setText(helpText.welcome());
            else
                this.textAreaMenu.setText(this.textArea1Ddist.getText());
            stage.setScene(firstScene);
        });
        /*
        * CALCULATION
        */
        nappiScene3.setOnMouseClicked(event -> {
            stage.setTitle("R_rms calculation");
            stage.setScene(calcScene);
        });
        menuNappiCalc.setOnMouseClicked(event -> {
            stage.setTitle("Random Walk");
            if (this.textAreaCalc.getText().equals(helpText.calculation()))
                this.textAreaMenu.setText(helpText.welcome());
            else
                this.textAreaMenu.setText(this.textAreaCalc.getText());
            stage.setScene(firstScene);
        });
        /*
        * REAL TIME RMS
        */
        nappiScene4.setOnMouseClicked(event -> {
            stage.setTitle("Real Time rms");
            if ( stage.getWidth() == this.getStageWidth() ){
                stage.setWidth(this.getStageWidth()+(this.getAnimWidth()-this.getTextWidth()));
                stage.setHeight(this.getStageHeight()+(this.getAnimHeight()-this.getTextHeight()));
                stage.setX(this.getScreenWidth()-(this.getAnimWidth()+this.getPaneWidth()));
                stage.setY((this.getScreenHeight() -this.getStageHeight())/2.0
                    -(this.getAnimHeight() -this.getTextHeight())/2.0-10.0);
            }
            stage.setScene(realScene);
        });
        menuNappiReal.setOnAction(event -> {
            stage.setTitle("Random Walk");
            if (this.textAreaReal.getText().equals(helpText.realtimerms()))
                this.textAreaMenu.setText(helpText.welcome());
            else
                this.textAreaMenu.clear();
            stage.setX(this.getScreenWidth()-this.getStageWidth());
            stage.setY((this.getScreenHeight()-this.getStageHeight())/2.0);
            stage.setWidth(this.getStageWidth());
            stage.setHeight(this.getStageHeight());
            stage.setScene(firstScene);
        });
        /*
        * MMC
        */
        nappiScene5.setOnMouseClicked(event -> {
            stage.setTitle("MMC Diffusion");
            if ( stage.getWidth() == this.getStageWidth() ){
                stage.setWidth(this.getStageWidth()+(this.getAnimWidth()-this.getTextWidth()));
                stage.setHeight(this.getStageHeight()+(this.getAnimHeight()-this.getTextHeight()));
                stage.setX(this.getScreenWidth()-(this.getAnimWidth()+this.getPaneWidth()));
                stage.setY((this.getScreenHeight() -this.getStageHeight())/2.0
                    -(this.getAnimHeight()-this.getTextHeight())/2.0-10.0);
            }
            stage.setScene(mmcScene);
        });
        menuNappiMMC.setOnAction(event -> {
            stage.setTitle("Random Walk");
            if (this.textAreaMMC.getText().equals(helpText.mmc()))
                this.textAreaMenu.setText(helpText.welcome());
            else
                this.textAreaMenu.setText(this.textAreaMMC.getText());
            stage.setX(this.getScreenWidth()-this.getStageWidth());
            stage.setY((this.getScreenHeight()-this.getStageHeight())/2.0);
            stage.setWidth(this.getStageWidth());
            stage.setHeight(this.getStageHeight());
            stage.setScene(firstScene);
        });

        /*
        * CREATE A FRAME FOR CALCULATION AND PATH TRACING PLOTS
        */
        this.setFrame(new JFrame());

        /*
        * CREATE AN INSTANCE FOR CODE EXECUTIONS
        */
        Execution ex = new Execution();

        /*
        * EXECUTE BUTTON CALCULATION
        */
        executeNappiCalc.setOnMouseClicked((MouseEvent event) -> {
            getCalcScene.setVar("s");
            String[] vars = getCalcScene.getVars();
            this.setVars(vars);
            Data data = new Data(vars);
            boolean fail = false;

            int steps = parseInt(getVars()[3]);
            int dim = parseInt(this.getVars()[4]);
            String lattice = this.getVars()[7];

            if ( steps < 1 ) fail = true;
            if ( dim < 1 || dim > 3 ) fail = true;
            if ( !lattice.equals("l") && !lattice.equals("-") ) fail = true;

            if ( fail ) return;

            int hours = (int) (steps * dim * 0.00005)/60;
            int mins = (int) (steps * dim * 0.00005)%60;
            String warnText = "";
            if (Math.log10(steps) > 4) {
                if (hours > 1)
                    warnText = "Data processing may take " + hours + "h.";
                else if (hours > 0)
                    warnText = "Data processing may take " + mins + "min.";
            }

            if (Math.log10(steps) < 5) {
                ex.executeRms(datafolder, datapath, fexec, pyexecrms, data, this.getVars());
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                    warnText + " Do you want to continue?",  ButtonType.NO, ButtonType.YES);
                alert.showAndWait();
                if ( alert.getResult() == ButtonType.YES) {
                    ex.executeRms(datafolder, datapath, fexec, pyexecrms, data, this.getVars());
                }
            }
        });

        /*
        * EXECUTE BUTTON PATH TRACING
        */
        executeNappiPath.setOnMouseClicked((MouseEvent event) -> {
            getMMCScene.setVar("s");
            String[] vars = getPathScene.getVars();
            this.setVars(vars);
            Data data = new Data(vars);
            boolean fail = false;

            int particles = parseInt(getVars()[0]);
            double diam = parseDouble(getVars()[1]);
            int charge = parseInt(getVars()[2]);
            int steps = parseInt(getVars()[3]);
            int dim = parseInt(this.getVars()[4]);
            String fixed = this.getVars()[6];
            String lattice = this.getVars()[7];

            if ( particles < 0 ) fail = true;
            if ( diam <= 0.0 || diam >= 1.0 ) fail = true;
            if ( charge < 0 || charge > 2 ) fail = true;
            if ( steps < 1 ) fail = true;
            if ( dim < 1 || dim > 3 ) fail = true;
            if ( !fixed.equals("f") && !fixed.equals("-") ) fail = true;
            if ( !lattice.equals("l") && !lattice.equals("-") ) fail = true;

            if ( fail ) return;

            String warnText;
            int cost = (int) Math.log10(particles*steps);

            if (cost < 8) {
                warnText = "Time estimate is over half an hour.";
            } else {
                warnText = "Time estimate is hours.";
            }

            if ( cost < 6 ) {
                    ex.executePath(datafolder, datapath, fexec, pyexec1d, pyexec2d, pyexec3d, data, this.getVars());
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                    warnText + " Do you want to continue?",  ButtonType.NO, ButtonType.YES);
                alert.showAndWait();
                if ( alert.getResult() == ButtonType.YES) {
                    ex.executePath(datafolder, datapath, fexec, pyexec1d, pyexec2d, pyexec3d, data, this.getVars());
                }
            }
        });

        /*
        * ANIMATION TIMER FOR REAL TIME RMS ANIMATION
        */
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

                if ( !getRealScene.isRunning())
                    return;

                if ( isovalikkoReal.getChildren().contains(textAreaReal) ) {
                    textAreaReal.clear();
                    isovalikkoReal.getChildren().remove(textAreaReal);
                    isovalikkoReal.getChildren().add(pane);
                }

                String[] vars = getRealScene.getVars();
                /*
                * FROM SCENEREALTIMERMS
                * vars from user:
                * vars[0] = particles,     USER
                * vars[1] = diameter,      n/a
                * vars[2] = charge,        n/a
                * vars[3] = steps,         USER
                * vars[4] = dimension,     USER
                * vars[5] = mmc,           n/a
                * vars[6] = fixed,         n/a
                * vars[7] = lattice,       n/a
                * vars[8] = save           n/a
                */

                int dim = parseInt(vars[4]);

                piirturi.setGlobalAlpha(1.0);
                piirturi.setFill(Color.BLACK);
                if ( dim == 1 ) {
                    piirturi.fillRect(0, 0, 1.0/getRealScalefactor()*getAnimWidth(), getAnimHeight());
                } else {
                    piirturi.fillRect(0, 0, 1.0/getRealScalefactor()*getAnimWidth(), 1.0/getRealScalefactor()*getAnimHeight());
                }
                piirturi.fill();

                /*
                * DRAW ANIMATION
                */
                getRealScene.refresh(datafolder, fexec, piirturi, getRealScalefactor(), getLinewidth(), getFxplot(),
                    rms_runs, isNewdata(), getMincount(), getMaxcount(), isStandnorm(), getAnimWidth());
                setNewdata(false);

                this.prevTime = currentNanoTime;
            }
        }.start();

        /*
        * RUN BUTTON REAL TIME RMS
        */
        runReal.setOnMouseClicked((MouseEvent event) -> {
            if (getRealScene.isRunning()) {
                getRealScene.stop();
                if ( this.isRealScaled() ) {
                    if ( this.getVars()[4].equals("1") )
                        piirturi.scale(1.0/this.getRealScalefactor(), 1.0);
                    else
                        piirturi.scale(1.0/this.getRealScalefactor(), 1.0/this.getRealScalefactor());
                }
                menuNappiReal.setDisable(false);
                helpNappiReal.setDisable(false);
                closeNappiReal.setDisable(false);
                standNorm.setDisable(false);
                runReal.setText("RUN");
            } else {
                this.setVars(getRealScene.getVars());
                getRealScene.setVar("-");
                boolean fail = false;

                int particles = parseInt(getVars()[0]);
                int dim = parseInt(this.getVars()[4]);
                int steps = parseInt(getVars()[3]);
 
                if ( particles < 0 ) fail = true;
                if ( steps < 1 ) fail = true;
                if ( dim < 1 || dim > 3 ) fail = true;

                if ( fail ) return;

                if (this.getFxplot() != null) {
                if (this.getFxplot().isRunning()) this.getFxplot().stop();
                if (this.getFxplot().getFrame().isShowing()
                    || this.getFxplot().getFrame().isActive()
                    || this.getFxplot().getFrame().isDisplayable())
                    this.getFxplot().getFrame().dispose();
                }
                this.setFxplot(new FXPlot("Walks&norm"));

                this.setRealScalefactor(Math.sqrt(this.getAnimWidth() + Math.pow(dim,2.0) * 100
                    * Math.pow(Math.log10(steps),2.0) ) / Math.pow(Math.log10(steps),2.0));
 
                switch (dim) {
                    case 1:
                        this.setLinewidth(1.0 / Math.log10(steps));
                        piirturi.scale(this.getRealScalefactor(), 1.0);
                        break;
                    case 2:
                        this.setLinewidth(1.0 / (this.getRealScalefactor() * Math.sqrt(Math.log10(steps))));
                        piirturi.scale(this.getRealScalefactor(), this.getRealScalefactor());
                        break;
                    case 3:
                        piirturi.scale(this.getRealScalefactor(), this.getRealScalefactor());
                        break;
                    default:
                        break;
                }
                this.setRealScaled(true);
                piirturi.setGlobalAlpha(1.0 / this.getRealScalefactor() * Math.pow(Math.log10(steps),2.0));

                this.setNewdata(true);
                this.setRms_runs();
                this.setRms_norm();
                double expected = Math.sqrt(steps);

                if ( !this.isStandnorm() ) {
                    if ( (int) expected < 5 ) {
                        this.setMincount(0.0);
                    } else {
                        this.setMincount(expected - 5.0);
                    }
                    this.setMaxcount(expected + 5.0);
                } else {
                    this.setMincount(-4.0);
                    this.setMaxcount(4.0);
                }

                this.getFxplot().setWData(this.getRms_runs(), this.getRms_runs(), expected);
                this.getFxplot().setHData(this.getRms_norm(), this.getRms_norm(), this.getMincount(), this.getMaxcount(), this.isStandnorm());
        
                getRealScene.start();
                runReal.setText("STOP");
                menuNappiReal.setDisable(true);
                helpNappiReal.setDisable(true);
                closeNappiReal.setDisable(true);
                standNorm.setDisable(true);
            }
        });

        /*
        * PLOT BUTTON MMC
        */
        plotMMC.setOnMouseClicked((MouseEvent event) -> {
            valikkoMMC.setDisable(true);
            getMMCScene.setVar("s");
            String[] vars = getMMCScene.getVars();
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
            if ( charge < 0 || charge > 2 ) fail = true;
            if ( dim < 2 || dim > 3 ) fail = true;
            if ( !lattice.equals("l") && !lattice.equals("-") ) fail = true;

            if ( fail ) return;

            ex.executeMMC(datafolder, datapath, fexec, pyexecmmc2d,
                pyexecmmc3d, valikkoMMC, data, this.getVars());
        });

        /*
        * EXECUTE BUTTON MMC
        */
        runMMC.setOnMouseClicked((MouseEvent event) -> {
            if ( getMMCScene.timerIsRunning()) return;
            getMMCScene.setVar("-");
            String[] vars = getMMCScene.getVars();
            this.setVars(vars);
            int particles = parseInt(getVars()[0]);
            double diam = parseDouble(getVars()[1]);
            int charge = parseInt(getVars()[2]);
            int dim = parseInt(getVars()[4]);
            String lattice = this.getVars()[7];
            boolean fail = false;

            if ( particles < 0 ) fail = true;
            if ( diam <= 0.0 || diam >= 1.0 ) fail = true;
            if ( charge < 0 || charge > 2 ) fail = true;
            if ( dim < 2 || dim > 3 ) fail = true;
            if ( !lattice.equals("l") && !lattice.equals("-") ) fail = true;

            if ( fail ) return;

            if (this.getFxplot() != null) {
                if (this.getFxplot().isRunning()) this.getFxplot().stop();
                if (this.getFxplot().getFrame().isShowing()
                    || this.getFxplot().getFrame().isActive()
                    || this.getFxplot().getFrame().isDisplayable())
                    this.getFxplot().getFrame().dispose();
            }
            this.setFxplot(new FXPlot("E"));

            if ( this.isMmcScaled() ) {
                mmcpiirturi.scale(1.0/this.getMmcScalefactor(), 1.0/this.getMmcScalefactor());
            }

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

            this.setMmcScalefactor((this.getAnimWidth() - 83.3) / measure);
            if ( dim == 2 )
                this.setLinewidth(1.0 / this.getMmcScalefactor());
            else
                this.setLinewidth(diam / this.getMmcScalefactor());

            mmcpiirturi.scale(this.getMmcScalefactor(), this.getMmcScalefactor());

            this.setMmcScaled(true);
            mmcpiirturi.setGlobalAlpha(1.0 / this.getMmcScalefactor() );

            this.setNewdata(true);
            this.setEnergy_x(new ArrayList<>());
            this.setEnergy_y(new ArrayList<>());

			if ( isovalikkoMMC.getChildren().contains(this.textAreaMMC)) {
                this.textAreaMMC.clear();
                isovalikkoMMC.getChildren().remove(this.textAreaMMC);
                isovalikkoMMC.getChildren().add(mmcpane);
            }

            mmcpiirturi.setGlobalAlpha(1.0);
            mmcpiirturi.setFill(Color.BLACK);
            mmcpiirturi.fillRect(0, 0, 1.0/this.getMmcScalefactor()*this.getAnimWidth(),
                1.0/this.getMmcScalefactor()*this.getAnimHeight());
            mmcpiirturi.fill();

            /*
            * GET INITIAL DATA
            */
            File initialDataFile = new File(
                datapath + "/startMMC_" + dim + "D_" + particles + "N.xy");

            /*
            * DRAW MMC ANIMATION
            */
            getMMCScene.refresh(datafolder, initialDataFile, fexec, mmcpiirturi, this.getMmcScalefactor(),
                    this.getAnimWidth(), this.getLinewidth(), this.getFxplot(), remBarNappiMMC, runMMC,
                    plotMMC, closeNappiMMC, menuNappiMMC, helpNappiMMC, this.getEnergy_x(), this.getEnergy_y(),
                    this.isNewdata(), measure, diff
            );
            this.setNewdata(false);

        });

        /*
         * EXECUTE BUTTON 1D DISTANCE
         */
        executeNappi1Ddist.setOnMouseClicked((MouseEvent event) -> {
            String[] vars = get1DdistScene.getVars();
            this.setVars(vars);
            Data data = new Data(vars);
            boolean fail = false;

            int particles = parseInt(getVars()[0]);
            int steps = parseInt(getVars()[3]);
            String lattice = this.getVars()[7];

            if ( particles < 0 ) fail = true;
            if ( steps < 1 ) fail = true;
            if ( !lattice.equals("l") && !lattice.equals("-") ) fail = true;

            if ( fail ) return;

            int hours = (int) (particles * steps * 0.0000005)/60;
            int mins = (int) (particles * steps * 0.0000005)%60;
            String warnText = "";
            if (Math.log10(particles * steps) > 6) {
                if (hours > 1)
                    warnText = "Data processing may take " + hours + "h.";
                else
                    warnText = "Data processing may take " + mins + "min.";
            }

            if (Math.log10(particles * steps) <= 6) {
                ex.execute1Ddist(datafolder, datapath, fexec, pyexec1Ddist, data, this.getVars());
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                    warnText + " Do you want to continue?",  ButtonType.NO, ButtonType.YES);
                alert.showAndWait();
                if ( alert.getResult() == ButtonType.YES) {
                    ex.execute1Ddist(datafolder, datapath, fexec, pyexec1Ddist, data, this.getVars());
                }
            }
        });

        stage.addEventHandler(EventType.ROOT, e -> stage.setOnHiding(f-> {
            if (this.getFxplot() != null) {
                if (this.getFxplot().isRunning()) this.getFxplot().stop();
                if (this.getFxplot().getFrame().isShowing()
                    || this.getFxplot().getFrame().isActive()
                    || this.getFxplot().getFrame().isDisplayable())
                    this.getFxplot().getFrame().dispose();
            }
            if (getMMCScene.runtimeIsRunning())
                getMMCScene.stopRuntime();
            if (getRealScene.runtimeIsRunning())
                getRealScene.stopRuntime();
            if (ex.runtimeIsRunning())
                ex.stopRuntime();
        }));
        stage.setOnCloseRequest((WindowEvent e) -> {
            Platform.exit();
            System.exit(0);
        });
        stage.setScene(firstScene);
        stage.initStyle(StageStyle.DECORATED);
        Image icn64 = new Image("icon64.png");
        Image icn48 = new Image("icon48.png");
        Image icn32 = new Image("icon32.png");
        Image icn24 = new Image("icon24.png");
        Image icn16 = new Image("icon16.png");
        stage.getIcons().addAll(icn64,icn48,icn32,icn24,icn16);
        stage.toFront();
        stage.show();
    }

    /**
     * method for creating a working directory C:/RWDATA if needed, and
     * copies executables there from resources/.
     * <p>
     *     returns false if all goes well, true otherwise
     * </p>
     * @param destination path for working directory C:/RWDATA
     * @param executable file to copy from resources/ to C:/RWDATA
     * @param createDir true if has to create working directory
     * @return false if all goes well, true otherwise
     */
    private boolean createFolder(String destination, String executable, boolean createDir){

        File dataFolder = new File(destination);
        Path dataPath = dataFolder.toPath();

        if ( createDir ) {
            if(!Files.exists(dataPath)) {
                try {
                    Files.createDirectory(dataPath);
                } catch (IOException e) {
                    e.printStackTrace();
                    return true;
                }
                System.out.println("creating directory: " + destination);
            }
            else System.out.println("Could not create a new directory\n");
        }

        File destinationFile = new File(destination + "/" + executable);
        boolean createdNewFile;
        try {
            createdNewFile = destinationFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }

        InputStream fin = null;
        OutputStream fout = null;

        if (createdNewFile) {
            try {
                System.out.println("Copying resource file '" + executable + "' into folder '" + destination + "', please wait...");
                fin = new BufferedInputStream(RandomWalk.class.getResourceAsStream("/"+executable));
                fout = new BufferedOutputStream(new FileOutputStream(destinationFile, false));
                byte[] buffer = new byte[1024];
                int read;
                while ((read = fin.read(buffer)) != -1) {
                    fout.write(buffer, 0, read);
                }
                System.out.println("Copying finished.");
            } catch (IOException e) {
                System.out.println("Resource file '" + executable + "' not copied into folder '" + destination + "'.\n" + e.getMessage());
                return true;
            } finally {
                try {
                    if (fin != null) fin.close();
                    if (fout != null) fout.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }

        }
        return false;
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
     * @return the buttonWidth
     */
    @Contract(pure = true)
    private double getButtonWidth() { return 150.0 /Screen.getMainScreen().getRenderScale(); }

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
     * @return the paneWidth
     */
    @Contract(pure = true)
    private double getPaneWidth() { return 200.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the pathheight
     */
    @Contract(pure = true)
    private double getPathHeight() { return 660.0 / Screen.getMainScreen().getRenderScale(); }

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
     * @return the fxplot
     */
    @Contract(pure = true)
    private FXPlot getFxplot() { return fxplot; }

    /**
     * @param fxplot the fxplot to set
     */
    private void setFxplot(FXPlot fxplot) {
        this.fxplot = fxplot;
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

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
     * @return the vars
     */
    private String[] getVars() { return vars.clone(); }

    /**
     * @param vars the vars to set
     */
    private void setVars(@NotNull String[] vars) { this.vars = vars.clone(); }

    /**
     * @return the realscalefactor
     */
    @Contract(pure = true)
    private double getRealScalefactor() { return realscalefactor; }

    /**
     * @param realscalefactor the realscalefactor to set
     */
    private void setRealScalefactor(double realscalefactor) { this.realscalefactor = realscalefactor; }

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
     * @return the isrealscaled
     */
    @Contract(pure = true)
    private boolean isRealScaled() { return isrealscaled; }

    /**
     * @param isrealscaled the isrealscaled to set
     */
    private void setRealScaled(boolean isrealscaled) { this.isrealscaled = isrealscaled; }

    /**
     * @return the ismmcscaled
     */
    @Contract(pure = true)
    private boolean isMmcScaled() { return ismmcscaled; }

    /**
     * @param ismmcscaled the ismmcscaled to set
     */
    private void setMmcScaled(boolean ismmcscaled) { this.ismmcscaled = ismmcscaled; }

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
     * @return the standnorm
     */

    @Contract(pure = true)
    private boolean isStandnorm() { return standnorm; }

    /**
     * @param standnorm the standnorm to set
     */
    private void setStandnorm(boolean standnorm) { this.standnorm = standnorm; }

    /**
     * @return the mincount
     */
    @Contract(pure = true)
    private double getMincount() { return mincount; }

    /**
     * @param mincount the mincount to set
     */
    private void setMincount(double mincount) { this.mincount = mincount; }

    /**
     * @return the maxcount
     */
    @Contract(pure = true)
    private double getMaxcount() { return maxcount; }

    /**
     * @param maxcount the maxcount to set
     */
    private void setMaxcount(double maxcount) { this.maxcount = maxcount; }

    /**
     * @return the rms_runs
     */
    private double[] getRms_runs() { return rms_runs.clone(); }

    /**
     * rms_runs to fill with zeros
     */
    private void setRms_runs() {
        this.rms_runs = new double[10];
        Arrays.fill(this.rms_runs, 0.0);
    }

    /**
     * @return the rms_norm
     */
    private double[] getRms_norm() { return rms_norm.clone(); }

    /**
     * rms_norm to fill with zeros
     */
    private void setRms_norm() {
        this.rms_norm = new double[10];
        Arrays.fill(this.rms_norm, 0.0);
    }

    /**
     * @return the energy_x
     */
    @Contract(pure = true)
    private List <Double> getEnergy_x() { return energy_x; }

    /**
     * @param energy_x the energy_x to set
     */
    private void setEnergy_x(List<Double> energy_x) { this.energy_x = energy_x; }

    /**
     * @return the energy_y
     */
    @Contract(pure = true)
    private List <Double> getEnergy_y() { return energy_y; }

    /**
     * @param energy_y the energy_y to set
     */
    private void setEnergy_y(List<Double> energy_y) { this.energy_y = energy_y; }
}
