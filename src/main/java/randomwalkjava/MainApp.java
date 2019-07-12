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
import java.util.Arrays;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
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
import javax.swing.JFrame;

public class MainApp extends Application {

    // IMAGE
    final int chartWidth = 860;
    final int chartHeight = 605;
    // STAGE
    final int stageWidth = 940;
    final int stageHeight = 660;
    // COMPONENTS
    final int buttonWidth = 150;
    final int textwidth = 740;
    final int textheight = 600;
    final int animwidth = 900;
    final int animheight = 900;
    final int simheight = 610;
    final int mmcheight = 630;
    final int paneWidth = 200;
    final int screenWidth = Screen.getMainScreen().getWidth();
    final int screenHeight = Screen.getMainScreen().getHeight();
    // DATA
    public String[] vars;
    public double scalefactor = 1.0;
    public double globalpha = 1.0;
    public double linewidth = 1.0;
    public boolean isscaled = false;
    public boolean onoff = false;

    private boolean newdata = false;
    public double[] rms_runs;
    public double[] rms_std;

    @Override
    public void start(Stage stage) throws Exception {

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
        stage.setWidth(stageWidth);
        stage.setHeight(stageHeight);
        stage.setResizable(false);
        stage.setX(screenWidth/2);
        stage.setY((screenHeight-stageHeight)/2);

        DropShadow shadow = new DropShadow();

        ////////////////////////////////////////////////////
        // SET FIRST VIEW BORDERPANE
        GridPane asettelu = new GridPane();
        asettelu.setMaxWidth(paneWidth);
        asettelu.setVgap(5);
        asettelu.setHgap(10);
        asettelu.setPadding(new Insets(0, 0, 0, 0));

        ////////////////////////////////////////////////////
        // FIRST VIEW LABELS AND BUTTONS
        Button nappiScene1 = new Button("R_RMS vs SQRT(N)");
        Button nappiScene2 = new Button("RANDOM WALK");
        Button nappiScene3 = new Button("REAL TIME WALK");
        Button nappiScene4 = new Button("MMC");
        nappiScene1.setMinWidth(buttonWidth);
        nappiScene1.setMaxWidth(buttonWidth);
        nappiScene2.setMinWidth(buttonWidth);
        nappiScene2.setMaxWidth(buttonWidth);
        nappiScene3.setMinWidth(buttonWidth);
        nappiScene3.setMaxWidth(buttonWidth);
        nappiScene4.setMinWidth(buttonWidth);
        nappiScene4.setMaxWidth(buttonWidth);

        Button nappiMenuHelp = new Button("HELP");
        nappiMenuHelp.setMinWidth(buttonWidth);
        nappiMenuHelp.setMaxWidth(buttonWidth);

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
        textAreaCalc.setMinWidth(textwidth);
        textAreaCalc.setMaxWidth(textwidth);
        textAreaCalc.setMinHeight(textheight);
        textAreaCalc.setMaxHeight(textheight);
        textAreaCalc.setFont(Font.font("Consolas",FontWeight.NORMAL, 18));
        textAreaCalc.setBorder(null);
        textAreaCalc.setEditable(false);
        textAreaCalc.setBackground(
            new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        textAreaCalc.setBlendMode(BlendMode.DIFFERENCE);
        // SIMULATION
        TextArea textAreaSim = new TextArea();
        textAreaSim.setMinWidth(textwidth);
        textAreaSim.setMaxWidth(textwidth);
        textAreaSim.setMinHeight(simheight);
        textAreaSim.setMaxHeight(simheight);
        textAreaSim.setFont(Font.font("Consolas",FontWeight.NORMAL, 18));
        textAreaSim.setBorder(null);
        textAreaSim.setEditable(false);
        textAreaSim.setBackground(
            new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        textAreaSim.setBlendMode(BlendMode.DIFFERENCE);
        // ANIMATION
        TextArea textAreaAnim = new TextArea();
        textAreaAnim.setMinWidth(animwidth);
        textAreaAnim.setMaxWidth(animwidth);
        textAreaAnim.setMinHeight(animheight);
        textAreaAnim.setMaxHeight(animheight);
        textAreaAnim.setFont(Font.font("Consolas",FontWeight.NORMAL, 18));
        textAreaAnim.setBorder(null);
        textAreaAnim.setEditable(false);
        textAreaAnim.setBackground(
            new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        textAreaAnim.setBlendMode(BlendMode.DIFFERENCE);
        // MMC
        TextArea textAreaMMC = new TextArea();
        textAreaMMC.setMinWidth(textwidth);
        textAreaMMC.setMaxWidth(textwidth);
        textAreaMMC.setMinHeight(mmcheight);
        textAreaMMC.setMaxHeight(mmcheight);
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
        textAreaMenu.setMinWidth(textwidth);
        textAreaMenu.setMaxWidth(textwidth);
        textAreaMenu.setMinHeight(textheight);
        textAreaMenu.setMaxHeight(textheight);
        textAreaMenu.setFont(Font.font("Consolas",FontWeight.NORMAL, 18));
        textAreaMenu.setBorder(null);
        textAreaMenu.setEditable(false);
        textAreaMenu.setBackground(
            new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        textAreaMenu.setBlendMode(BlendMode.DIFFERENCE);
        
        ////////////////////////////////////////////////////
        // ANIMATION COMPONENTS
        Canvas animAlusta = new Canvas(animwidth, animheight);
        animAlusta.setVisible(true);

        GraphicsContext piirturi = animAlusta.getGraphicsContext2D();
        piirturi.setFill(Color.BLACK);
        piirturi.fillRect(0, 0, animwidth, animheight);
        piirturi.setStroke(Color.YELLOW);
        piirturi.setGlobalAlpha(0.2);

        Pane pane = new Pane();
        pane.setPrefSize(animwidth, animheight);
        pane.getChildren().add(animAlusta);
        pane.setVisible(true);

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

        ////////////////////////////////////////////////////
        // OTHER VIEWS BUTTON: EXECUTE CALCULATION
        Button executeNappiCalc = new Button("EXECUTE");
        executeNappiCalc.setDefaultButton(true);
        executeNappiCalc.setMinWidth(buttonWidth);
        executeNappiCalc.setMaxWidth(buttonWidth);
        executeNappiCalc.setTextFill(Color.RED);
                    executeNappiCalc.setBackground(
                        new Background(
                            new BackgroundFill(
                                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
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
        menuNappiCalc.setMinWidth(buttonWidth);
        menuNappiCalc.setMaxWidth(buttonWidth);
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
        helpNappiCalc.setMinWidth(buttonWidth);
        helpNappiCalc.setMaxWidth(buttonWidth);
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

        ////////////////////////////////////////////////////
        // OTHER VIEWS BUTTON: EXECUTE SIMULATION
        Button executeNappiSim = new Button("EXECUTE");
        executeNappiSim.setDefaultButton(true);
        executeNappiSim.setMinWidth(buttonWidth);
        executeNappiSim.setMaxWidth(buttonWidth);
        executeNappiSim.setTextFill(Color.RED);
        executeNappiSim.setBackground(
                    new Background(
                        new BackgroundFill(
                            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
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
        menuNappiSim.setMinWidth(buttonWidth);
        menuNappiSim.setMaxWidth(buttonWidth);
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
        helpNappiSim.setMinWidth(buttonWidth);
        helpNappiSim.setMaxWidth(buttonWidth);
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

        ////////////////////////////////////////////////////
        // OTHER VIEWS BUTTON: RUN ANIMATION
        Button runAnim = new Button("RUN");
        runAnim.setDefaultButton(true);
        runAnim.setMinWidth(buttonWidth);
        runAnim.setMaxWidth(buttonWidth);
        runAnim.setTextFill(Color.RED);
                    runAnim.setBackground(
                        new Background(
                            new BackgroundFill(
                                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
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
        menuNappiAnim.setMinWidth(buttonWidth);
        menuNappiAnim.setMaxWidth(buttonWidth);
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
        helpNappiAnim.setMinWidth(buttonWidth);
        helpNappiAnim.setMaxWidth(buttonWidth);
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

        ////////////////////////////////////////////////////
        // OTHER VIEWS BUTTON: EXECUTE MMC
        Button runMMC = new Button("RUN");
        runMMC.setDefaultButton(true);
        runMMC.setMinWidth(buttonWidth);
        runMMC.setMaxWidth(buttonWidth);
        runMMC.setTextFill(Color.RED);
                    runMMC.setBackground(
                        new Background(
                            new BackgroundFill(
                                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
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
        menuNappiMMC.setMinWidth(buttonWidth);
        menuNappiMMC.setMaxWidth(buttonWidth);
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
        helpNappiMMC.setMinWidth(buttonWidth);
        helpNappiMMC.setMaxWidth(buttonWidth);
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

        ////////////////////////////////////////////////////
        // SET FIRST VIEW BORDERPANE
        valikkoMenu.getChildren().addAll(
            asettelu,
            nappiMenuHelp);
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
            executeNappiCalc);
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
            executeNappiSim);
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
            runAnim);
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
            runMMC);
        isovalikkoMMC.getChildren().addAll(
            valikkoMMC,
            textAreaMMC);
        asetteluMMC.setCenter(isovalikkoMMC);

        ////////////////////////////////////////////////////
        // SET SCENES
        Scene firstScene = new Scene(asetteluMenu,stageWidth,stageHeight);
        firstScene.getStylesheets().add("/styles/Styles.css");

        Scene calcScene = new Scene(asetteluCalc,stageWidth,stageHeight);
        calcScene.getStylesheets().add("/styles/Styles.css");

        Scene simScene = new Scene(asetteluSim,stageWidth,stageHeight + (simheight-textheight));
        simScene.getStylesheets().add("/styles/Styles.css");

        Scene animScene = new Scene(asetteluAnim,
            stageWidth + (animwidth-textwidth),
            stageHeight + (animheight-textheight));
        animScene.getStylesheets().add("/styles/Styles.css");

        Scene mmcScene = new Scene(asetteluMMC,
            stageWidth + (animwidth-textwidth),
            stageHeight + (animheight-textheight));
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
            if ( stage.getHeight() == stageHeight ){
                stage.setHeight(stageHeight+(simheight-textheight));
                stage.setY((screenHeight-stageHeight)/2-(simheight-textheight)/2);
            }
            stage.setScene(simScene);
        });
        menuNappiSim.setOnAction(event -> {
            stage.setTitle("Random Walk");
            if (textAreaSim.getText().equals(helpText.simulation()))
                textAreaMenu.setText(helpText.welcome());
            else
                textAreaMenu.setText(textAreaSim.getText());
            stage.setY((screenHeight-stageHeight)/2);
            stage.setHeight(stageHeight);
            stage.setScene(firstScene);
        });
        // ANIMATION
        nappiScene3.setOnMouseClicked(event -> {
            stage.setTitle("Random Walk Animation");
            if ( stage.getWidth() == stageWidth ){
                stage.setWidth(stageWidth+(animwidth-textwidth));
                stage.setHeight(stageHeight+(animheight-textheight));
                stage.setX(screenWidth/2-(animwidth-textwidth)+15);
                stage.setY((screenHeight-stageHeight)/2-(animheight-textheight)/2-30);
            }
            stage.setScene(animScene);
        });
        menuNappiAnim.setOnAction(event -> {
            stage.setTitle("Random Walk");
            if (textAreaAnim.getText().equals(helpText.animation()))
                textAreaMenu.setText(helpText.welcome());
            else
                textAreaMenu.clear();
            stage.setX(screenWidth/2);
            stage.setY((screenHeight-stageHeight)/2);
            stage.setWidth(stageWidth);
            stage.setHeight(stageHeight);
            stage.setScene(firstScene);
        });
        // MMC
        nappiScene4.setOnMouseClicked(event -> {
            stage.setTitle("MMC Random Walk");
            if ( stage.getWidth() == stageWidth ){
                stage.setWidth(stageWidth+(animwidth-textwidth));
                stage.setHeight(stageHeight+(animheight-textheight));
                stage.setX(screenWidth/2-(animwidth-textwidth)+15);
                stage.setY((screenHeight-stageHeight)/2-(animheight-textheight)/2-30);
            }
            stage.setScene(mmcScene);
        });
        menuNappiMMC.setOnAction(event -> {
            stage.setTitle("Random Walk");
            if (textAreaMMC.getText().equals(helpText.mmc()))
                textAreaMenu.setText(helpText.welcome());
            else
                textAreaMenu.setText(textAreaMMC.getText());
            stage.setX(screenWidth/2);
            stage.setY((screenHeight-stageHeight)/2);
            stage.setWidth(stageWidth);
            stage.setHeight(stageHeight);
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
            ex.executeRms(datafolder, textAreaSim, frame, data, vars);
        });

        ////////////////////////////////////////////////////
        // EXECUTE BUTTON SIMULATION
        executeNappiSim.setOnMouseClicked((MouseEvent event) -> {
            // BUTTON PRESSED ON
            this.vars = getSimScene.getVars();
            Data data = new Data(this.vars);
            ex.executeSim(datafolder, textAreaSim, frame, data, vars);
        });

        ////////////////////////////////////////////////////
        // EXECUTE BUTTON MMC
        runMMC.setOnMouseClicked((MouseEvent event) -> {
            // BUTTON PRESSED ON
            this.vars = getMMCScene.getVars();
            Data data = new Data(this.vars);
            //ex.executeMMC(datafolder, textAreaMMC, frame, data, vars);
        });

        ////////////////////////////////////////////////////
        // CREATE AN INSTANCE FOR REAL TIME PLOTTING
        FXPlot fxplot = new FXPlot();

        ////////////////////////////////////////////////////
        // ANIMATION TIMER FOR REAL TIME RANDOM WALK ANIMATION
        new AnimationTimer() {
            // päivitetään animaatiota noin 100 millisekunnin välein
            private final long sleepNanoseconds = 100 * 1000000;
            private long prevTime = 0;
            private int dim;
            private double steps;

            @Override
            public void handle(long currentNanoTime) {

                // päivitetään animaatiota noin 100 millisekunnin välein
                if ((currentNanoTime - prevTime) < sleepNanoseconds) {
                    return;
                }

                if (!getAnimScene.isRunning()) {
                    return;
                }

                if ( isovalikkoAnim.getChildren().contains(textAreaAnim)) {
                    textAreaAnim.clear();
                    isovalikkoAnim.getChildren().remove(textAreaAnim);
                    isovalikkoAnim.getChildren().add(pane);
                }

                piirturi.setGlobalAlpha(1.0);
                piirturi.setFill(Color.BLACK);
                if ( dim == 1 )
                    piirturi.fillRect(0, 0, 1.0/scalefactor*animwidth, animheight);
                else
                    piirturi.fillRect(0, 0, 1.0/scalefactor*animwidth, 1.0/scalefactor*animheight);
                piirturi.fill();
                piirturi.setStroke(Color.YELLOW);
                if (isscaled) {
                    if ( dim == 1 )
                        piirturi.scale(1.0/scalefactor, 1.0);
                    else
                        piirturi.scale(1.0/scalefactor, 1.0/scalefactor);
                    isscaled = false;
                }

                String[] vars = getAnimScene.getVars();
                // FROM SCENEANIMATION
                // vars from user:
                // vars[0] = particles,
                // vars[1] = diameter,
                // vars[2] = charge,        n/a
                // vars[3] = steps,
                // vars[4] = dimension,
                // vars[5] = temperature,   n/a
                // vars[6] = fixed,         n/a
                // vars[7] = lattice,
                // vars[8] = avoid,         n/a
                // vars[9] = save           n/a
                steps = Double.valueOf(vars[3]);
                dim = Integer.valueOf(vars[4]);

                if ( dim == 1 ) {
                    scalefactor = Math.sqrt((animwidth+200)
                                / Math.sqrt(steps))
                                - Math.sqrt(Math.log10(steps));
                    linewidth = (Math.log10(steps) + 1.0)
                                / Math.sqrt(steps);
                    piirturi.scale(scalefactor, 1.0);
                    isscaled = true;
                } else if ( dim > 1 ) {
                    scalefactor = Math.sqrt((animwidth+200)
                            / Math.sqrt(steps))
                            - Math.sqrt(Math.log10(steps));
                    linewidth = Math.pow(Math.log10(steps),2.0)
                            / (Math.sqrt(2.0 * scalefactor * steps));
                    piirturi.scale(scalefactor, scalefactor);
                    isscaled = true;
                }

                // DRAW ANIMATION
                getAnimScene.refresh(
                    datafolder, fexec, piirturi, scalefactor,
                    linewidth, fxplot, rms_runs, rms_std, newdata
                );
                newdata = false;

                // älä muuta tätä
                prevTime = currentNanoTime;
            }
        }.start();

        ////////////////////////////////////////////////////
        // RUN BUTTON ANIMATION
        runAnim.setOnMouseClicked((MouseEvent event) -> {
            if (getAnimScene.isRunning()) {
                // FOR ONE ROUND OPERATION
                // COMMENT OUT NEXT LINE
                getAnimScene.stop();
                runAnim.setText("RUN");
            } else {
                // FOR ONE ROUND OPERATION
                // COMMENT OUT NEXT LINE
                this.newdata = true;
                this.rms_runs = new double[10];
                this.rms_std = new double[10];
                Arrays.fill(this.rms_runs, 0.0);
                String[] vars = getAnimScene.getVars();
                double mean = Math.sqrt(Double.valueOf(vars[3]));
                int mincount;
                if ( (int) mean < 5 )
                    mincount = 0;
                else
                    mincount = (int) mean - 5;
                int maxcount = (int) mean + 5;
                fxplot.setWData("sqrt(N)", "R_rms", rms_runs, rms_runs);
                fxplot.setHData("mean","norm", rms_std, rms_std, mincount, maxcount, mean);
        
                getAnimScene.start();
                runAnim.setText("STOP");
            }
        });

        stage.setScene(firstScene);
        Image img = new Image("images/icon.png");
        stage.getIcons().add(img);

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
