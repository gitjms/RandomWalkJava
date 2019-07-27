package randomwalkjava;

import com.sun.glass.ui.Screen;
import javafx.scene.image.Image;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import static javafx.application.Application.launch;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.JFrame;

public class MainApp extends Application {

    // STAGE
    private final int stageWidth = 940;
    private final int stageHeight = 660;
    // COMPONENTS
    private final int buttonWidth = 150;
    private final int textwidth = 740;
    private final int textheight = 600;
    private final int animwidth = 900;
    private final int animheight = 900;
    private final int simheight = 660;
    private final int paneWidth = 200;
    private final int screenWidth = Screen.getMainScreen().getWidth();
    private final int screenHeight = Screen.getMainScreen().getHeight();
    private FXPlot fxplot;
    // DATA
    public String[] vars;
    private double scalefactor = 1.0;
    private double linewidth = 1.0;
    private boolean isscaled = false;

    private boolean newdata = false;
    private double[] rms_runs;
    private double[] rms_norm;
    private List <Double> energy_x;
    private List <Double> energy_y;
    private boolean barrier;

    @Override
    public void start(Stage stage) throws Exception {
        this.fxplot = null;
        ////////////////////////////////////////////////////
        // FILE AND FOLDER CHECK
        String datapath = "C:\\DATA";
        String sourcepath = "lib\\";
        String fexec = "walk.exe";
        String pyexecrms = "plotrms.py";
        String pyexec1d = "plot1d.py";
        String pyexec2d = "plot2d.py";
        String pyexec3d = "plot3d.py";
        File datafolder = new File(datapath);
        File sourceFile = new File(datapath + "\\" + fexec);
        boolean sourceFound = false;
        if (Files.notExists(datafolder.toPath())){
            sourceFound = createFolder(sourcepath, datapath, fexec, true);
            if (sourceFound == false)
                this.stop();
            sourceFound = createFolder(sourcepath, datapath, pyexecrms, false);
            if (sourceFound == false)
                this.stop();
            sourceFound = createFolder(sourcepath, datapath, pyexec1d, false);
            if (sourceFound == false)
                this.stop();
            sourceFound = createFolder(sourcepath, datapath, pyexec2d, false);
            if (sourceFound == false)
                this.stop();
            sourceFound = createFolder(sourcepath, datapath, pyexec3d, false);
            if (sourceFound == false)
                this.stop();
        } else if (Files.notExists(sourceFile.toPath())) {
            sourceFound = createFolder(sourcepath, datapath, fexec, false);
            if (sourceFound == false)
                this.stop();
            sourceFile = new File(datapath + "\\" + pyexecrms);
            if (Files.notExists(sourceFile.toPath())) {
                sourceFound = createFolder(sourcepath, datapath, pyexecrms, false);
                if (sourceFound == false)
                    this.stop();
            }
            sourceFile = new File(datapath + "\\" + pyexec1d);
            if (Files.notExists(sourceFile.toPath())) {
                sourceFound = createFolder(sourcepath, datapath, pyexec1d, false);
                if (sourceFound == false)
                    this.stop();
            }
            sourceFile = new File(datapath + "\\" + pyexec2d);
            if (Files.notExists(sourceFile.toPath())) {
                sourceFound = createFolder(sourcepath, datapath, pyexec2d, false);
                if (sourceFound == false)
                    this.stop();
            }
            sourceFile = new File(datapath + "\\" + pyexec3d);
            if (Files.notExists(sourceFile.toPath())) {
                sourceFound = createFolder(sourcepath, datapath, pyexec3d, false);
                if (sourceFound == false)
                    this.stop();
            }
        }

        ////////////////////////////////////////////////////
        // CREATE STAGE
        stage.setTitle("Random Walk");
        stage.setWidth(this.stageWidth);
        stage.setHeight(this.stageHeight);
        stage.setResizable(false);
        stage.setX(this.screenWidth/2);
        stage.setY((this.screenHeight-this.stageHeight)/2);

        DropShadow shadow = new DropShadow();

        ////////////////////////////////////////////////////
        // SET FIRST VIEW BORDERPANE
        GridPane asettelu = new GridPane();
        asettelu.setMaxWidth(this.paneWidth);
        asettelu.setVgap(5);
        asettelu.setHgap(10);
        asettelu.setPadding(new Insets(0, 0, 0, 0));

        ////////////////////////////////////////////////////
        // FIRST VIEW LABELS AND BUTTONS
        Button nappiScene1 = new Button("R_RMS vs SQRT(N)");
        Button nappiScene2 = new Button("RANDOM WALK");
        Button nappiScene3 = new Button("REAL TIME WALK");
        Button nappiScene4 = new Button("MMC");
        nappiScene1.setMinWidth(this.buttonWidth);
        nappiScene1.setMaxWidth(this.buttonWidth);
        nappiScene2.setMinWidth(this.buttonWidth);
        nappiScene2.setMaxWidth(this.buttonWidth);
        nappiScene3.setMinWidth(this.buttonWidth);
        nappiScene3.setMaxWidth(this.buttonWidth);
        nappiScene4.setMinWidth(this.buttonWidth);
        nappiScene4.setMaxWidth(this.buttonWidth);

        Button nappiMenuHelp = new Button("HELP");
        nappiMenuHelp.setMinWidth(this.buttonWidth);
        nappiMenuHelp.setMaxWidth(this.buttonWidth);

        GridPane.setHalignment(nappiScene1, HPos.LEFT);
        asettelu.add(nappiScene1, 0, 0, 2, 1);
        nappiScene1.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        nappiScene1.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                nappiScene1.setEffect(shadow);
        });
        nappiScene1.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                nappiScene1.setEffect(null);
        });
        nappiScene1.setVisible(true);

        final Pane empty1 = new Pane();
        GridPane.setHalignment(empty1, HPos.LEFT);
        asettelu.add(empty1, 0, 1, 2, 1);

        GridPane.setHalignment(nappiScene2, HPos.LEFT);
        asettelu.add(nappiScene2, 0, 2, 2, 1);
        nappiScene2.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        nappiScene2.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                nappiScene2.setEffect(shadow);
        });
        nappiScene2.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                nappiScene2.setEffect(null);
        });
        nappiScene2.setVisible(true);

        final Pane empty2 = new Pane();
        GridPane.setHalignment(empty2, HPos.LEFT);
        asettelu.add(empty2, 0, 3, 2, 1);

        GridPane.setHalignment(nappiScene3, HPos.LEFT);
        asettelu.add(nappiScene3, 0, 4, 2, 1);
        nappiScene3.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        nappiScene3.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                nappiScene3.setEffect(shadow);
        });
        nappiScene3.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                nappiScene3.setEffect(null);
        });
        nappiScene3.setVisible(true);

        final Pane empty3 = new Pane();
        GridPane.setHalignment(empty3, HPos.LEFT);
        asettelu.add(empty3, 0, 5, 2, 1);

        GridPane.setHalignment(nappiScene4, HPos.LEFT);
        asettelu.add(nappiScene4, 0, 6, 2, 1);
        nappiScene4.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        nappiScene4.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                nappiScene4.setEffect(shadow);
        });
        nappiScene4.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                nappiScene4.setEffect(null);
        });
        nappiScene4.setVisible(true);

        final Pane empty4 = new Pane();
        GridPane.setHalignment(empty4, HPos.LEFT);
        asettelu.add(empty4, 0, 7, 2, 1);

        asettelu.add(nappiMenuHelp, 0, 8, 2, 1);
        nappiMenuHelp.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        nappiMenuHelp.setVisible(true);

        // OTHER STUFF
        BorderPane asetteluMenu = new BorderPane();
        HBox isovalikkoMenu = new HBox();
        isovalikkoMenu.setPadding(new Insets(0, 0, 0, 0));
        isovalikkoMenu.setSpacing(10);
        VBox valikkoMenu = new VBox();
        valikkoMenu.setPadding(new Insets(10, 10, 10, 10));
        valikkoMenu.setSpacing(10);

        ////////////////////////////////////////////////////
        // OTHER VIEWS
        SceneCalculation getCalcScene = new SceneCalculation();
        SceneSimulation getSimScene = new SceneSimulation();
        SceneAnimation getAnimScene = new SceneAnimation();
        SceneMMC getMMCScene = new SceneMMC();

        BorderPane asetteluCalc = new BorderPane();
        BorderPane asetteluSim = new BorderPane();
        BorderPane asetteluAnim = new BorderPane();
        BorderPane asetteluMMC = new BorderPane();

        HBox isovalikkoCalc = new HBox();
        isovalikkoCalc.setPadding(new Insets(0, 0, 0, 0));
        isovalikkoCalc.setSpacing(0);
        
        HBox isovalikkoSim = new HBox();
        isovalikkoSim.setPadding(new Insets(0, 0, 0, 0));
        isovalikkoSim.setSpacing(0);

        HBox isovalikkoAnim = new HBox();
        isovalikkoAnim.setPadding(new Insets(0, 0, 0, 0));
        isovalikkoAnim.setSpacing(0);

        HBox isovalikkoMMC = new HBox();
        isovalikkoMMC.setPadding(new Insets(0, 0, 0, 0));
        isovalikkoMMC.setSpacing(0);

        VBox valikkoCalc = new VBox();
        valikkoCalc.setPadding(new Insets(10, 10, 10, 10));
        valikkoCalc.setSpacing(20);

        VBox valikkoSim = new VBox();
        valikkoSim.setPadding(new Insets(10, 10, 10, 10));
        valikkoSim.setSpacing(20);

        VBox valikkoAnim = new VBox();
        valikkoAnim.setPadding(new Insets(10, 10, 10, 10));
        valikkoAnim.setSpacing(20);

        VBox valikkoMMC = new VBox();
        valikkoMMC.setPadding(new Insets(10, 10, 10, 10));
        valikkoMMC.setSpacing(20);

        ////////////////////////////////////////////////////
        // TEXT AREAS
        HelpText helpText = new HelpText();
        // CALCULATION
        TextArea textAreaCalc = new TextArea();
        textAreaCalc.setMinWidth(this.textwidth);
        textAreaCalc.setMaxWidth(this.textwidth);
        textAreaCalc.setMinHeight(this.textheight);
        textAreaCalc.setMaxHeight(this.textheight);
        textAreaCalc.setFont(Font.font("Consolas",FontWeight.NORMAL, 18));
        textAreaCalc.setBorder(null);
        textAreaCalc.setEditable(false);
        textAreaCalc.setBackground(
            new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        textAreaCalc.setBlendMode(BlendMode.DIFFERENCE);
        // SIMULATION
        TextArea textAreaSim = new TextArea();
        textAreaSim.setMinWidth(this.textwidth);
        textAreaSim.setMaxWidth(this.textwidth);
        textAreaSim.setMinHeight(this.simheight);
        textAreaSim.setMaxHeight(this.simheight);
        textAreaSim.setFont(Font.font("Consolas",FontWeight.NORMAL, 18));
        textAreaSim.setBorder(null);
        textAreaSim.setEditable(false);
        textAreaSim.setBackground(
            new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        textAreaSim.setBlendMode(BlendMode.DIFFERENCE);
        // ANIMATION
        TextArea textAreaAnim = new TextArea();
        textAreaAnim.setMinWidth(this.animwidth);
        textAreaAnim.setMaxWidth(this.animwidth);
        textAreaAnim.setMinHeight(this.animheight);
        textAreaAnim.setMaxHeight(this.animheight);
        textAreaAnim.setFont(Font.font("Consolas",FontWeight.NORMAL, 18));
        textAreaAnim.setBorder(null);
        textAreaAnim.setEditable(false);
        textAreaAnim.setBackground(
            new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        textAreaAnim.setBlendMode(BlendMode.DIFFERENCE);
        // MMC
        TextArea textAreaMMC = new TextArea();
        textAreaMMC.setMinWidth(this.animwidth);
        textAreaMMC.setMaxWidth(this.animwidth);
        textAreaMMC.setMinHeight(this.animheight);
        textAreaMMC.setMaxHeight(this.animheight);
        textAreaMMC.setFont(Font.font("Consolas",FontWeight.NORMAL, 18));
        textAreaMMC.setBorder(null);
        textAreaMMC.setEditable(false);
        textAreaMMC.setBackground(
            new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        textAreaMMC.setBlendMode(BlendMode.DIFFERENCE);

        ////////////////////////////////////////////////////
        // TEXT AREA MENU
        TextArea textAreaMenu = new TextArea(helpText.welcome());
        textAreaMenu.setMinWidth(this.textwidth);
        textAreaMenu.setMaxWidth(this.textwidth);
        textAreaMenu.setMinHeight(this.textheight);
        textAreaMenu.setMaxHeight(this.textheight);
        textAreaMenu.setFont(Font.font("Consolas",FontWeight.NORMAL, 18));
        textAreaMenu.setBorder(null);
        textAreaMenu.setEditable(false);
        textAreaMenu.setBackground(
            new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        textAreaMenu.setBlendMode(BlendMode.DIFFERENCE);
        
        ////////////////////////////////////////////////////
        // ANIMATION COMPONENTS
        Canvas animAlusta = new Canvas(this.animwidth, this.animheight);
        animAlusta.setVisible(true);

        GraphicsContext piirturi = animAlusta.getGraphicsContext2D();
        piirturi.setFill(Color.BLACK);
        piirturi.fillRect(0, 0, this.animwidth, this.animheight);
        //piirturi.setStroke(Color.YELLOW);
        //piirturi.setGlobalAlpha(0.2);

        Pane pane = new Pane();
        pane.setPrefSize(this.animwidth, this.animheight);
        pane.getChildren().add(animAlusta);
        pane.setVisible(true);
        
        ////////////////////////////////////////////////////
        // MMC COMPONENTS
        Canvas mmcAlusta = new Canvas(this.animwidth, this.animheight);
        mmcAlusta.setVisible(true);

        GraphicsContext mmcpiirturi = mmcAlusta.getGraphicsContext2D();
        mmcpiirturi.setFill(Color.BLACK);
        mmcpiirturi.fillRect(0, 0, this.animwidth, this.animheight);
        mmcpiirturi.setStroke(Color.YELLOW);

        Pane mmcpane = new Pane();
        mmcpane.setPrefSize(this.animwidth, this.animheight);
        mmcpane.getChildren().add(mmcAlusta);
        mmcpane.setVisible(true);

        ////////////////////////////////////////////////////
        // FIRST VIEW BUTTON: HELP
        nappiMenuHelp.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                nappiMenuHelp.setEffect(shadow);
        });
        nappiMenuHelp.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                nappiMenuHelp.setEffect(null);
        });
        nappiMenuHelp.setOnAction(event -> {
            textAreaMenu.setText(helpText.menu());
        });

        // FIRST VIEW BUTTON: CLOSE
        Button closeNappiMenu = new Button("CLOSE");
        closeNappiMenu.setMinWidth(this.buttonWidth);
        closeNappiMenu.setMaxWidth(this.buttonWidth);
        closeNappiMenu.setTextFill(Color.RED);
        closeNappiMenu.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        GridPane.setHalignment(closeNappiMenu, HPos.LEFT);
        closeNappiMenu.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                closeNappiMenu.setEffect(shadow);
        });
        closeNappiMenu.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                closeNappiMenu.setEffect(null);
        });
        closeNappiMenu.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Close application?",
                ButtonType.OK, ButtonType.CANCEL);
            alert.showAndWait();
            if ( alert.getResult() == ButtonType.OK ) {
                System.gc();
                stage.close();
            }
        });
        closeNappiMenu.setVisible(true);

        ////////////////////////////////////////////////////
        // OTHER VIEWS BUTTON: EXECUTE CALCULATION
        Button executeNappiCalc = new Button("EXECUTE");
        executeNappiCalc.setDefaultButton(true);
        executeNappiCalc.setMinWidth(this.buttonWidth);
        executeNappiCalc.setMaxWidth(this.buttonWidth);
        executeNappiCalc.setStyle("-fx-background-color: Red");
        executeNappiCalc.setTextFill(Color.WHITE);
        executeNappiCalc.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                executeNappiCalc.setEffect(shadow);
        });
        executeNappiCalc.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                executeNappiCalc.setEffect(null);
        });
        executeNappiCalc.setVisible(true);

        // OTHER VIEWS BUTTON: CALCULATION MENU
        Button menuNappiCalc = new Button("BACK TO MENU");
        menuNappiCalc.setMinWidth(this.buttonWidth);
        menuNappiCalc.setMaxWidth(this.buttonWidth);
        menuNappiCalc.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                menuNappiCalc.setEffect(shadow);
        });
        menuNappiCalc.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                menuNappiCalc.setEffect(null);
        });
        menuNappiCalc.setVisible(true);

        // OTHER VIEWS BUTTON: CALCULATION HELP
        Button helpNappiCalc = new Button("HELP");
        helpNappiCalc.setMinWidth(this.buttonWidth);
        helpNappiCalc.setMaxWidth(this.buttonWidth);
        GridPane.setHalignment(helpNappiCalc, HPos.LEFT);
        helpNappiCalc.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                helpNappiCalc.setEffect(shadow);
        });
        helpNappiCalc.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                helpNappiCalc.setEffect(null);
        });
        helpNappiCalc.setOnAction(event -> {
            textAreaCalc.setText(helpText.calculation());
        });
        helpNappiCalc.setVisible(true);

        // OTHER VIEWS BUTTON: CALCULATION CLOSE
        Button closeNappiCalc = new Button("CLOSE");
        closeNappiCalc.setMinWidth(this.buttonWidth);
        closeNappiCalc.setMaxWidth(this.buttonWidth);
        closeNappiCalc.setTextFill(Color.RED);
        closeNappiCalc.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        GridPane.setHalignment(closeNappiCalc, HPos.LEFT);
        closeNappiCalc.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                closeNappiCalc.setEffect(shadow);
        });
        closeNappiCalc.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                closeNappiCalc.setEffect(null);
        });
        closeNappiCalc.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Close application?",
                ButtonType.OK, ButtonType.CANCEL);
            alert.showAndWait();
            if ( alert.getResult() == ButtonType.OK ) {
                System.gc();
                stage.close();
            }
        });
        closeNappiCalc.setVisible(true);

        ////////////////////////////////////////////////////
        // OTHER VIEWS BUTTON: EXECUTE SIMULATION
        Button executeNappiSim = new Button("EXECUTE");
        executeNappiSim.setDefaultButton(true);
        executeNappiSim.setMinWidth(this.buttonWidth);
        executeNappiSim.setMaxWidth(this.buttonWidth);
        executeNappiSim.setStyle("-fx-background-color: Red");
        executeNappiSim.setTextFill(Color.WHITE);
        executeNappiSim.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                executeNappiSim.setEffect(shadow);
        });
        executeNappiSim.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                executeNappiSim.setEffect(null);
        });
        executeNappiSim.setVisible(true);

        // OTHER VIEWS BUTTON: SIMULATION MENU
        Button menuNappiSim = new Button("BACK TO MENU");
        menuNappiSim.setMinWidth(this.buttonWidth);
        menuNappiSim.setMaxWidth(this.buttonWidth);
        menuNappiSim.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                menuNappiSim.setEffect(shadow);
        });
        menuNappiSim.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                menuNappiSim.setEffect(null);
        });
        menuNappiSim.setVisible(true);

        // OTHER VIEWS BUTTON: SIMULATION HELP
        Button helpNappiSim = new Button("HELP");
        helpNappiSim.setMinWidth(this.buttonWidth);
        helpNappiSim.setMaxWidth(this.buttonWidth);
        helpNappiSim.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                helpNappiSim.setEffect(shadow);
        });
        helpNappiSim.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                helpNappiSim.setEffect(null);
        });
        helpNappiSim.setOnAction(event -> {
            textAreaSim.setText(helpText.simulation());
        });
        helpNappiSim.setVisible(true);

        // OTHER VIEWS BUTTON: SIMULATION CLOSE
        Button closeNappiSim = new Button("CLOSE");
        closeNappiSim.setMinWidth(this.buttonWidth);
        closeNappiSim.setMaxWidth(this.buttonWidth);
        closeNappiSim.setTextFill(Color.RED);
        closeNappiSim.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        GridPane.setHalignment(closeNappiSim, HPos.LEFT);
        closeNappiSim.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                closeNappiSim.setEffect(shadow);
        });
        closeNappiSim.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                closeNappiSim.setEffect(null);
        });
        closeNappiSim.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Close application?",
                ButtonType.OK, ButtonType.CANCEL);
            alert.showAndWait();
            if ( alert.getResult() == ButtonType.OK ) {
                System.gc();
                stage.close();
            }
        });
        closeNappiSim.setVisible(true);

        ////////////////////////////////////////////////////
        // OTHER VIEWS BUTTON: RUN ANIMATION
        Button runAnim = new Button("RUN");
        runAnim.setDefaultButton(true);
        runAnim.setMinWidth(this.buttonWidth);
        runAnim.setMaxWidth(this.buttonWidth);
        runAnim.setStyle("-fx-background-color: Red");
        runAnim.setTextFill(Color.WHITE);
        runAnim.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                runAnim.setEffect(shadow);
        });
        runAnim.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                runAnim.setEffect(null);
        });
        runAnim.setVisible(true);

        // OTHER VIEWS BUTTON: ANIMATION MENU
        Button menuNappiAnim = new Button("BACK TO MENU");
        menuNappiAnim.setMinWidth(this.buttonWidth);
        menuNappiAnim.setMaxWidth(this.buttonWidth);
        menuNappiAnim.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                menuNappiAnim.setEffect(shadow);
        });
        menuNappiAnim.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                menuNappiAnim.setEffect(null);
        });
        menuNappiAnim.setVisible(true);

        // OTHER VIEWS BUTTON: ANIMATION HELP
        Button helpNappiAnim = new Button("HELP");
        helpNappiAnim.setMinWidth(this.buttonWidth);
        helpNappiAnim.setMaxWidth(this.buttonWidth);
        GridPane.setHalignment(helpNappiAnim, HPos.LEFT);
        helpNappiAnim.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                helpNappiAnim.setEffect(shadow);
        });
        helpNappiAnim.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                helpNappiAnim.setEffect(null);
        });
        helpNappiAnim.setOnAction(event -> {
            if (isovalikkoAnim.getChildren().contains(pane)){
                isovalikkoAnim.getChildren().remove(pane);
                isovalikkoAnim.getChildren().add(textAreaAnim);
            }
            textAreaAnim.setText(helpText.animation());
        });
        helpNappiAnim.setVisible(true);

        // OTHER VIEWS BUTTON: ANIMATION CLOSE
        Button closeNappiAnim = new Button("CLOSE");
        closeNappiAnim.setMinWidth(this.buttonWidth);
        closeNappiAnim.setMaxWidth(this.buttonWidth);
        closeNappiAnim.setTextFill(Color.RED);
        closeNappiAnim.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        GridPane.setHalignment(closeNappiAnim, HPos.LEFT);
        closeNappiAnim.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                closeNappiAnim.setEffect(shadow);
        });
        closeNappiAnim.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                closeNappiAnim.setEffect(null);
        });
        closeNappiAnim.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Close application?",
                ButtonType.OK, ButtonType.CANCEL);
            alert.showAndWait();
            if ( alert.getResult() == ButtonType.OK ) {
                System.gc();
                stage.close();
            }
        });
        closeNappiAnim.setVisible(true);

        ////////////////////////////////////////////////////
        // OTHER VIEWS BUTTON: EXECUTE MMC
        Button runMMC = new Button("EXECUTE");
        runMMC.setDefaultButton(true);
        runMMC.setMinWidth(this.buttonWidth);
        runMMC.setMaxWidth(this.buttonWidth);
        runMMC.setStyle("-fx-background-color: Red");
        runMMC.setTextFill(Color.WHITE);
        runMMC.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                runMMC.setEffect(shadow);
        });
        runMMC.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                runMMC.setEffect(null);
        });
        runMMC.setVisible(true);

        // OTHER VIEWS BUTTON: MMC MENU
        Button menuNappiMMC = new Button("BACK TO MENU");
        menuNappiMMC.setMinWidth(this.buttonWidth);
        menuNappiMMC.setMaxWidth(this.buttonWidth);
        menuNappiMMC.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                menuNappiMMC.setEffect(shadow);
        });
        menuNappiMMC.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                menuNappiMMC.setEffect(null);
        });
        menuNappiMMC.setVisible(true);

        // OTHER VIEWS BUTTON: MMC HELP
        Button helpNappiMMC = new Button("HELP");
        helpNappiMMC.setMinWidth(this.buttonWidth);
        helpNappiMMC.setMaxWidth(this.buttonWidth);
        GridPane.setHalignment(helpNappiMMC, HPos.LEFT);
        helpNappiMMC.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                helpNappiMMC.setEffect(shadow);
        });
        helpNappiMMC.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                helpNappiMMC.setEffect(null);
        });
        helpNappiMMC.setOnAction(event -> {
            if (isovalikkoMMC.getChildren().contains(pane)){
                isovalikkoMMC.getChildren().remove(pane);
                isovalikkoMMC.getChildren().add(textAreaMMC);
            }
            textAreaMMC.setText(helpText.mmc());
        });
        helpNappiMMC.setVisible(true);

        // OTHER VIEWS BUTTON: MMC REMOVE BARRIER
        Button remBarNappiMMC = new Button("REMOVE BARRIER");
        remBarNappiMMC.setMinWidth(this.buttonWidth);
        remBarNappiMMC.setMaxWidth(this.buttonWidth);
        remBarNappiMMC.setTextFill(Color.RED);
        remBarNappiMMC.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        GridPane.setHalignment(remBarNappiMMC, HPos.LEFT);
        remBarNappiMMC.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                remBarNappiMMC.setEffect(shadow);
        });
        remBarNappiMMC.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                remBarNappiMMC.setEffect(null);
        });
        remBarNappiMMC.setVisible(false);

        // OTHER VIEWS BUTTON: MMC CLOSE
        Button closeNappiMMC = new Button("CLOSE");
        closeNappiMMC.setMinWidth(this.buttonWidth);
        closeNappiMMC.setMaxWidth(this.buttonWidth);
        closeNappiMMC.setTextFill(Color.RED);
        closeNappiMMC.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        GridPane.setHalignment(closeNappiMMC, HPos.LEFT);
        closeNappiMMC.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                closeNappiMMC.setEffect(shadow);
        });
        closeNappiMMC.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                closeNappiMMC.setEffect(null);
        });
        closeNappiMMC.setOnAction(event -> {
            if ( getMMCScene.timerIsRunning()) return;
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Close application?",
                ButtonType.OK, ButtonType.CANCEL);
            alert.showAndWait();
            if ( alert.getResult() == ButtonType.OK ) {
                System.gc();
                stage.close();
            }
        });
        closeNappiMMC.setVisible(true);

        ////////////////////////////////////////////////////
        // SET FIRST VIEW BORDERPANE
        valikkoMenu.getChildren().addAll(
            asettelu,
            nappiMenuHelp,
            closeNappiMenu);
        isovalikkoMenu.getChildren().addAll(
            valikkoMenu,
            textAreaMenu);
        asetteluMenu.setCenter(isovalikkoMenu);

        ////////////////////////////////////////////////////
        // SET CALCULATION BORDERPANE
        valikkoCalc.getChildren().addAll(
            menuNappiCalc,
            helpNappiCalc,
            getCalcScene.getSceneCalc(),
            executeNappiCalc,
            closeNappiCalc);
        isovalikkoCalc.getChildren().addAll(
            valikkoCalc,
            textAreaCalc);
        asetteluCalc.setCenter(isovalikkoCalc);

        ////////////////////////////////////////////////////
        // SET SIMULATION BORDERPANE
        valikkoSim.getChildren().addAll(
            menuNappiSim,
            helpNappiSim,
            getSimScene.getSceneSim(),
            executeNappiSim,
            closeNappiSim);
        isovalikkoSim.getChildren().addAll(
            valikkoSim,
            textAreaSim);
        asetteluSim.setCenter(isovalikkoSim);

        ////////////////////////////////////////////////////
        // SET ANIMATION BORDERPANE
        valikkoAnim.getChildren().addAll(
            menuNappiAnim,
            helpNappiAnim,
            getAnimScene.getSceneAnim(),
            runAnim,
            closeNappiAnim);
        isovalikkoAnim.getChildren().addAll(
            valikkoAnim,
            textAreaAnim);
        asetteluAnim.setCenter(isovalikkoAnim);

        ////////////////////////////////////////////////////
        // SET MMC BORDERPANE
        valikkoMMC.getChildren().addAll(
            menuNappiMMC,
            helpNappiMMC,
            getMMCScene.getSceneMMC(),
            runMMC,
            remBarNappiMMC,
            closeNappiMMC);
        isovalikkoMMC.getChildren().addAll(
            valikkoMMC,
            textAreaMMC);
        asetteluMMC.setCenter(isovalikkoMMC);

        ////////////////////////////////////////////////////
        // SET SCENES
        Scene firstScene = new Scene(asetteluMenu,this.stageWidth,this.stageHeight);
        firstScene.getStylesheets().add("/styles/Styles.css");

        Scene calcScene = new Scene(asetteluCalc,this.stageWidth,this.stageHeight);
        calcScene.getStylesheets().add("/styles/Styles.css");

        Scene simScene = new Scene(asetteluSim,this.stageWidth,this.stageHeight
            + (this.simheight-this.textheight));
        simScene.getStylesheets().add("/styles/Styles.css");

        Scene animScene = new Scene(asetteluAnim,
            this.stageWidth + (this.animwidth-this.textwidth),
            this.stageHeight + (this.animheight-this.textheight));
        animScene.getStylesheets().add("/styles/Styles.css");

        Scene mmcScene = new Scene(asetteluMMC,
            this.stageWidth + (this.animwidth-this.textwidth),
            this.stageHeight + (this.animheight-this.textheight));
        mmcScene.getStylesheets().add("/styles/Styles.css");

        ////////////////////////////////////////////////////
        // SET SCENE CHOICE BUTTONS' EFFECTS
        // CALCULATION
        nappiScene1.setOnMouseClicked(event -> {
            stage.setTitle("R_rms calculation");
            stage.setScene(calcScene);
            
        });
        menuNappiCalc.setOnMouseClicked(event -> {
            stage.setTitle("Random Walk");
            if (textAreaCalc.getText().equals(helpText.calculation()))
                textAreaMenu.setText(helpText.welcome());
            else
                textAreaMenu.setText(textAreaCalc.getText());
            stage.setScene(firstScene);
        });
        // SIMULATION
        nappiScene2.setOnMouseClicked(event -> {
            stage.setTitle("Random Walk simulation");
            if ( stage.getHeight() == this.stageHeight ){
                stage.setHeight(this.stageHeight+(this.simheight-this.textheight));
                stage.setY((this.screenHeight-this.stageHeight)/2-(this.simheight-this.textheight)/2);
            }
            stage.setScene(simScene);
        });
        menuNappiSim.setOnAction(event -> {
            stage.setTitle("Random Walk");
            if (textAreaSim.getText().equals(helpText.simulation()))
                textAreaMenu.setText(helpText.welcome());
            else
                textAreaMenu.setText(textAreaSim.getText());
            stage.setY((this.screenHeight-this.stageHeight)/2);
            stage.setHeight(this.stageHeight);
            stage.setScene(firstScene);
        });
        // ANIMATION
        nappiScene3.setOnMouseClicked(event -> {
            stage.setTitle("Random Walk Animation");
            if ( stage.getWidth() == this.stageWidth ){
                stage.setWidth(this.stageWidth+(this.animwidth-this.textwidth));
                stage.setHeight(this.stageHeight+(this.animheight-this.textheight));
                stage.setX(this.screenWidth/2-(this.animwidth-this.textwidth)+15);
                stage.setY((this.screenHeight-this.stageHeight)/2
                    -(this.animheight-this.textheight)/2-30);
            }
            stage.setScene(animScene);
        });
        menuNappiAnim.setOnAction(event -> {
            stage.setTitle("Random Walk");
            if (textAreaAnim.getText().equals(helpText.animation()))
                textAreaMenu.setText(helpText.welcome());
            else
                textAreaMenu.clear();
            stage.setX(this.screenWidth/2);
            stage.setY((this.screenHeight-this.stageHeight)/2);
            stage.setWidth(this.stageWidth);
            stage.setHeight(this.stageHeight);
            stage.setScene(firstScene);
        });
        // MMC
        nappiScene4.setOnMouseClicked(event -> {
            stage.setTitle("MMC Random Walk");
            if ( stage.getWidth() == this.stageWidth ){
                stage.setWidth(this.stageWidth+(this.animwidth-this.textwidth));
                stage.setHeight(this.stageHeight+(this.animheight-this.textheight));
                stage.setX(this.screenWidth/2-(this.animwidth-this.textwidth)+15);
                stage.setY((this.screenHeight-this.stageHeight)/2
                    -(this.animheight-this.textheight)/2-30);
            }
            stage.setScene(mmcScene);
        });
        menuNappiMMC.setOnAction(event -> {
            stage.setTitle("Random Walk");
            if (textAreaMMC.getText().equals(helpText.mmc()))
                textAreaMenu.setText(helpText.welcome());
            else
                textAreaMenu.setText(textAreaMMC.getText());
            stage.setX(this.screenWidth/2);
            stage.setY((this.screenHeight-this.stageHeight)/2);
            stage.setWidth(this.stageWidth);
            stage.setHeight(this.stageHeight);
            stage.setScene(firstScene);
        });

        ////////////////////////////////////////////////////
        // CREATE A FRAME FOR CALCULATION AND SIMULATION PLOTS
        JFrame frame = new JFrame();

        ////////////////////////////////////////////////////
        // CREATE AN INSTANCE FOR CODE EXECUTIONS
        Execution ex = new Execution();

        ////////////////////////////////////////////////////
        // EXECUTE BUTTON CALCULATION
        executeNappiCalc.setOnMouseClicked((MouseEvent event) -> {
            // BUTTON PRESSED ON
            this.vars = getCalcScene.getVars();
            Data data = new Data(this.vars);
            boolean fail = false;

            int steps = Integer.valueOf(vars[3]);
            int dim = Integer.valueOf(this.vars[4]);
            String lattice = this.vars[7];

            if ( steps < 1 ) fail = true;
            if ( dim < 1 || dim > 3 ) fail = true;
            if ( !lattice.equals("l") && !lattice.equals("-") ) fail = true;

            if ( fail == true ) return;

            ex.executeRms(datafolder, textAreaSim, frame, data, this.vars);
        });

        ////////////////////////////////////////////////////
        // EXECUTE BUTTON SIMULATION
        executeNappiSim.setOnMouseClicked((MouseEvent event) -> {
            // BUTTON PRESSED ON
            this.vars = getSimScene.getVars();
            Data data = new Data(this.vars);
            boolean fail = false;

            int particles = Integer.valueOf(vars[0]);
            double diam = Double.valueOf(vars[1]);
            int charge = Integer.valueOf(vars[2]);
            int steps = Integer.valueOf(vars[3]);
            int dim = Integer.valueOf(this.vars[4]);
            String fixed = this.vars[6];
            String lattice = this.vars[7];
            String avoid = this.vars[8];

            if ( particles < 0 ) fail = true;
            if ( diam <= 0.0 || diam >= 1.0 ) fail = true;
            if ( charge < 0 || charge > 2 ) fail = true;
            if ( steps < 1 ) fail = true;
            if ( dim < 1 || dim > 3 ) fail = true;
            if ( !fixed.equals("f") && !fixed.equals("-") ) fail = true;
            if ( !lattice.equals("l") && !lattice.equals("-") ) fail = true;
            if ( !avoid.equals("a") && !avoid.equals("-") ) fail = true;

            if ( fail == true ) return;

            ex.executeSim(datafolder, textAreaSim, frame, data, this.vars);
        });

        ////////////////////////////////////////////////////
        // ANIMATION TIMER FOR REAL TIME RANDOM WALK ANIMATION
        new AnimationTimer() {
            // päivitetään animaatiota noin 100 millisekunnin välein
            private final long sleepNanoseconds = 100 * 1000000;
            private long prevTime = 0;
            private int dim;
            private String[] vars;

            @Override
            public void handle(long currentNanoTime) {

                // päivitetään animaatiota noin 100 millisekunnin välein
                if ((currentNanoTime - this.prevTime) < this.sleepNanoseconds) {
                    return;
                }

                if ( !getAnimScene.isRunning())
                    return;

                if ( isovalikkoAnim.getChildren().contains(textAreaAnim) ) {
                    textAreaAnim.clear();
                    isovalikkoAnim.getChildren().remove(textAreaAnim);
                    isovalikkoAnim.getChildren().add(pane);
                }

                this.vars = getAnimScene.getVars();
                // FROM SCENEANIMATION
                // vars from user:
                // vars[0] = particles,     USER
                // vars[1] = diameter,      n/a
                // vars[2] = charge,        n/a
                // vars[3] = steps,         USER
                // vars[4] = dimension,     USER
                // vars[5] = mmc,           n/a
                // vars[6] = fixed,         n/a
                // vars[7] = lattice,       n/a
                // vars[8] = avoid,         n/a
                // vars[9] = save           n/a

                this.dim = Integer.valueOf(this.vars[4]);

                piirturi.setGlobalAlpha(1.0);
                piirturi.setFill(Color.BLACK);
                if ( this.dim == 1 )
                    piirturi.fillRect(0, 0, 1.0/scalefactor*animwidth, animheight);
                else if ( this.dim == 2 )
                    piirturi.fillRect(0, 0, 1.0/scalefactor*animwidth, 1.0/scalefactor*animheight);
                else if ( this.dim == 3 )
                    piirturi.fillRect(0, 0, 1.0/scalefactor*animwidth, 1.0/scalefactor*animheight);
                piirturi.fill();

                // DRAW ANIMATION
                getAnimScene.refresh(
                    datafolder, fexec, piirturi, scalefactor, animwidth,
                    linewidth, fxplot, rms_runs, rms_norm, newdata
                );
                newdata = false;

                // älä muuta tätä
                this.prevTime = currentNanoTime;
            }
        }.start();

        ////////////////////////////////////////////////////
        // RUN BUTTON ANIMATION
        runAnim.setOnMouseClicked((MouseEvent event) -> {
            if (getAnimScene.isRunning()) {
                getAnimScene.stop();
                if ( this.isscaled == true ) {
                    piirturi.scale(1.0/this.scalefactor, 1.0/this.scalefactor);
                }
                runAnim.setText("RUN");
            } else {
                this.vars = getAnimScene.getVars();
                boolean fail = false;

                int particles = Integer.valueOf(vars[0]);
                int dim = Integer.valueOf(this.vars[4]);
                int steps = Integer.valueOf(vars[3]);
 
                if ( particles < 0 ) fail = true;
                if ( steps < 1 ) fail = true;
                if ( dim < 1 || dim > 3 ) fail = true;

                if ( fail == true ) return;

                if (this.fxplot != null) {
                if (this.fxplot.isRunning()) this.fxplot.stop();
                if (this.fxplot.getFrame().isShowing()
                    || this.fxplot.getFrame().isActive()
                    || this.fxplot.getFrame().isDisplayable())
                    this.fxplot.getFrame().dispose();
                }
                this.fxplot = new FXPlot("W&H", this.screenHeight);

                this.scalefactor = 
                Math.sqrt( this.animwidth + Math.pow(dim,2.0) * 100 * Math.pow(Math.log10((double) steps),2.0) )
                / Math.pow(Math.log10((double) steps),2.0);
 
                if ( dim == 1 ) {
                    this.linewidth = 1.0 / Math.log10((double) steps);
                    piirturi.scale(this.scalefactor, 1.0);
                } else if ( dim == 2 ) {
                    this.linewidth = 1.0 / ( this.scalefactor * Math.sqrt(Math.log10((double) steps)) );
                    piirturi.scale(this.scalefactor, this.scalefactor);
                } else if ( dim == 3 ) {
                    //this.linewidth = 1.0 / ( this.scalefactor * Math.sqrt(Math.log10((double) steps)) );
                    piirturi.scale(this.scalefactor, this.scalefactor);
                }
                this.isscaled = true;
                piirturi.setGlobalAlpha(1.0 / this.scalefactor * Math.pow(Math.log10((double) steps),2.0));

                this.newdata = true;
                this.rms_runs = new double[10];
                this.rms_norm = new double[10];
                Arrays.fill(this.rms_runs, 0.0);
                Arrays.fill(this.rms_norm, 0.0);
                double expected = Math.sqrt((double) steps);
                int mincount;
                if ( (int) expected < 5 )
                    mincount = 0;
                else
                    mincount = (int) expected - 5;
                int maxcount = (int) expected + 5;
                fxplot.setWData("R_rms", "sqrt(N)", this.rms_runs, this.rms_runs, expected);
                fxplot.setHData("norm", this.rms_norm, this.rms_norm, mincount, maxcount);
        
                getAnimScene.start();
                runAnim.setText("STOP");
            }
        });

        ////////////////////////////////////////////////////
        // EXECUTE BUTTON MMC
        runMMC.setOnMouseClicked((MouseEvent event) -> {
            // BUTTON PRESSED ON
            if ( getMMCScene.timerIsRunning()) return;
            this.vars = getMMCScene.getVars();
            int particles = Integer.valueOf(vars[0]);
            double diam = Double.valueOf(vars[1]);
            int charge = Integer.valueOf(vars[2]);
            int dim = Integer.valueOf(vars[4]);
            String lattice = this.vars[7];
            boolean fail = false;

            if ( particles < 0 ) fail = true;
            if ( diam <= 0.0 || diam >= 1.0 ) fail = true;
            if ( charge < 0 || charge > 2 ) fail = true;
            if ( dim < 2 || dim > 3 ) fail = true;
            if ( !lattice.equals("l") && !lattice.equals("-") ) fail = true;

            if ( fail == true ) return;

            if (this.fxplot != null) {
                if (this.fxplot.isRunning()) this.fxplot.stop();
                if (this.fxplot.getFrame().isShowing()
                    || this.fxplot.getFrame().isActive()
                    || this.fxplot.getFrame().isDisplayable())
                    this.fxplot.getFrame().dispose();
            }
            this.fxplot = new FXPlot("E", this.screenHeight);

            if ( this.isscaled == true ) {
                mmcpiirturi.scale(1.0/this.scalefactor, 1.0/this.scalefactor);
            }

            if ( particles < 25 )
                this.scalefactor = (this.animwidth - 100.0)
                    / 10.0;
            else
                if ( dim < 3 )
                    this.scalefactor = (this.animwidth - 100.0)
                        / ( 2.0 * Math.sqrt( 2.0 * (double) particles ) );
                else
                    this.scalefactor = (this.animwidth - 100.0)
                        / ( Math.sqrt( 2.0 * (double) particles ) );

            if ( dim == 2 )
                this.linewidth = 1.0 / this.scalefactor;
            else
                this.linewidth = diam / this.scalefactor;

            mmcpiirturi.scale(this.scalefactor, this.scalefactor);

            this.isscaled = true;
            mmcpiirturi.setGlobalAlpha(1.0 / this.scalefactor );

            this.newdata = true;
            this.energy_x = new ArrayList();
            this.energy_y = new ArrayList();

			if ( isovalikkoMMC.getChildren().contains(textAreaMMC)) {
                textAreaMMC.clear();
                isovalikkoMMC.getChildren().remove(textAreaMMC);
                isovalikkoMMC.getChildren().add(mmcpane);
            }

            mmcpiirturi.setGlobalAlpha(1.0);
            mmcpiirturi.setFill(Color.BLACK);
            mmcpiirturi.fillRect(0, 0, 1.0/this.scalefactor*this.animwidth,
                1.0/this.scalefactor*this.animheight);
            mmcpiirturi.fill();

            // GET INITIAL DATA
            File initialDataFile = new File(
                datapath + "\\startMMC_" + dim + "D_" + particles + "N.xy");

            // DRAW ANIMATION
            getMMCScene.refresh(
                datafolder, initialDataFile, fexec, mmcpiirturi, this.scalefactor,
                this.animwidth, this.linewidth, this.fxplot, remBarNappiMMC,
                this.energy_x, this.energy_y, this.newdata
            );

            this.newdata = false;

        });

        ////////////////////////////////////////////////////
        // RUN BUTTON ONE ROUND DEBUGGING
        /*runAnim.setOnMouseClicked((MouseEvent event) -> {
            this.vars = getAnimScene.getVars();
            int dim = Integer.valueOf(this.vars[4]);
            double steps = Double.valueOf(vars[3]);

            boolean fail = false;
            if ( Integer.valueOf(this.vars[0]) < 0 ) fail = true; // particles
                if ( steps < 1 ) fail = true; // steps
                if ( dim < 1 || dim > 3 ) fail = true; // dimension

                if ( fail == true ) return;

            if (this.fxplot != null) {
                if (this.fxplot.isRunning()) this.fxplot.stop();
                if (this.fxplot.getFrame().isShowing()
                    || this.fxplot.getFrame().isActive()
                    || this.fxplot.getFrame().isDisplayable())
                    this.fxplot.getFrame().dispose();
            }
            this.fxplot = new FXPlot("W&H", this.screenHeight);

            if ( this.isscaled == true ) {
                piirturi.scale(1.0/this.scalefactor, 1.0/this.scalefactor);
            }

            this.scalefactor = 
                Math.sqrt( this.animwidth + 100 * Math.pow(Math.log10(steps),2.0) )
                / Math.pow(Math.log10(steps),2.0);
 
            if ( dim == 2 ) {
                this.linewidth = 1.0 / ( this.scalefactor * Math.sqrt(Math.log10(steps)) );
                piirturi.scale(this.scalefactor, this.scalefactor);
            } else if ( dim == 3 ) {
                this.linewidth = 1.0 / ( 3.0 * this.scalefactor * Math.sqrt(Math.log10(steps)) );
                piirturi.scale(this.scalefactor, this.scalefactor);
            }
            this.isscaled = true;
            piirturi.setGlobalAlpha(1.0 / this.scalefactor * Math.pow(Math.log10(steps),2.0));

            this.newdata = true;
            this.rms_runs = new double[10];
            this.rms_norm = new double[10];
            Arrays.fill(this.rms_runs, 0.0);
            Arrays.fill(this.rms_norm, 0.0);
            double expected = Math.sqrt(steps);
            int mincount;
            if ( (int) expected < 5 )
                mincount = 0;
            else
                mincount = (int) expected - 5;
            int maxcount = (int) expected + 5;
            fxplot.setWData("R_rms", "sqrt(N)", this.rms_runs, this.rms_runs, expected);
            fxplot.setHData("norm", this.rms_norm, this.rms_norm, mincount, maxcount);

			if ( isovalikkoAnim.getChildren().contains(textAreaAnim)) {
                textAreaAnim.clear();
                isovalikkoAnim.getChildren().remove(textAreaAnim);
                isovalikkoAnim.getChildren().add(pane);
            }

            piirturi.setGlobalAlpha(1.0);
            piirturi.setFill(Color.BLACK);
            if ( dim == 2 )
                piirturi.fillRect(0, 0, 1.0/scalefactor*animwidth, 1.0/scalefactor*animheight);
            else if ( dim == 3 )
                piirturi.fillRect(0, 0, 1.0/scalefactor*animwidth, 1.0/scalefactor*animheight);
            piirturi.fill();

            // DRAW ANIMATION
            getAnimScene.refresh(
                datafolder, fexec, piirturi, scalefactor,
                linewidth, fxplot, rms_runs, rms_norm, newdata
            );
            newdata = false;
        });*/

        stage.setScene(firstScene);
        Image img = new Image("images/icon.png");
        stage.getIcons().add(img);
        stage.addEventHandler(EventType.ROOT, e -> {
            stage.setOnHiding(f-> {
                if (this.fxplot != null) {
                    if (this.fxplot.isRunning()) this.fxplot.stop();
                    if (this.fxplot.getFrame().isShowing()
                        || this.fxplot.getFrame().isActive()
                        || this.fxplot.getFrame().isDisplayable())
                        this.fxplot.getFrame().dispose();
                }
                if (getMMCScene.runtimeIsRunning())
                    getMMCScene.stopRuntime();
            });
        });
        stage.initStyle(StageStyle.UTILITY);
        stage.toFront();
        stage.show();
    }

    public boolean createFolder(String source, String destination, String executable, boolean createDir){
        if (createDir == true) {
            File dataFile = new File(destination);
            try {
                System.out.println("creating directory: " + destination);
                dataFile.mkdir();
            } catch (SecurityException se) {
                System.out.println("Could not create a new directory\n"+se.getMessage());
            }
        }

        File sourceDir = new File(source);
        if (Files.notExists(sourceDir.toPath())) {
            System.out.println("Source file " + executable + " not found from " + source);
            return false;
        }

        File sourceFile = new File(source + "\\" + executable);
        File destinationFile = new File(destination + "\\" + executable);
        InputStream fin = null;
        OutputStream fout = null;
        //sourceFile.deleteOnExit();
        
        try {
            fin = new BufferedInputStream(new FileInputStream(sourceFile));
            fout = new BufferedOutputStream(new FileOutputStream(destinationFile));
            byte[] readBytes = new byte[1024];
            int readed = 0;
            System.out.println("Copying resource file, please wait...");
            while((readed = fin.read(readBytes)) != -1){
                fout.write(readBytes, 0, readed);
            }
            System.out.println("Copying finished.");
        } catch (IOException e) {
            System.out.println("Resource file " + sourceFile + " not copied into new folder\n"+e.getMessage());
        } finally {
            try {
                fin.close();
                fout.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        
        return true;
    }

    public static void main(String[] args) {

        launch(args);

    }
}
