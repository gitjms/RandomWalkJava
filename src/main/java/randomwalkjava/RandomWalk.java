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
import java.io.PrintWriter;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
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

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * TODO: javadocs
 */
public class RandomWalk extends Application {

    private final int stageWidth = 940;
    private final int stageHeight = 660;
    private final int buttonWidth = 150;
    private final int textwidth = 740;
    private final int textheight = 600;
    private final int animwidth = 900;
    private final int animheight = 900;
    private final int pathheight = 660;
    private final int paneWidth = 200;
    private final int screenWidth = Screen.getMainScreen().getWidth();
    private final int screenHeight = Screen.getMainScreen().getHeight();
    private FXPlot fxplot;
    private JFrame frame;
    private String[] vars;
    private double scalefactor = 1.0;
    private double linewidth = 1.0;
    private boolean isscaled = false;
    private boolean newdata = false;
    private boolean standnorm = true;
    private double mincount;
    private double maxcount;
    private double[] rms_runs;
    private double[] rms_norm;
    private List <Double> energy_x;
    private List <Double> energy_y;

    @Override
    public void start(Stage stage) throws Exception {
        /**
        * FILE AND FOLDER CHECK
        * creates a folder C:\RWDATA if not exist
        * copies Fortran and Python executables from lib folder
        * to RWDATA folder if not in RWDATA folder already
        */
        String datapath = "C:/RWDATA";
        String sourcepath = "src/main/resources/lib/";
        String fexec = "walk.exe";
        String pyexecrms = "plotrms.py";
        String pyexec1d = "plot1d.py";
        String pyexec2d = "plot2d.py";
        String pyexec3d = "plot3d.py";
        String pyexecmmc2d = "plotmmc2d.py";
        String pyexecmmc3d = "plotmmc3d.py";
        File datafolder = new File(datapath);
        File sourceFile = new File(datapath + "/" + fexec);

        if (Files.notExists(datafolder.toPath())){
            if ( createFolder(sourcepath, datapath, fexec, true) == false )
                this.stop();
            if ( createFolder(sourcepath, datapath, pyexecrms, false) == false )
                this.stop();
            if ( createFolder(sourcepath, datapath, pyexec1d, false) == false )
                this.stop();
            if ( createFolder(sourcepath, datapath, pyexec2d, false) == false )
                this.stop();
            if ( createFolder(sourcepath, datapath, pyexec3d, false) == false )
                this.stop();
            if ( createFolder(sourcepath, datapath, pyexecmmc2d, false) == false )
                this.stop();
            if ( createFolder(sourcepath, datapath, pyexecmmc3d, false) == false )
                this.stop();
        } else if (Files.notExists(sourceFile.toPath())) {
            if ( createFolder(sourcepath, datapath, fexec, false) == false )
                this.stop();
            sourceFile = new File(datapath + "/" + pyexecrms);
            if (Files.notExists(sourceFile.toPath())) {
                if ( createFolder(sourcepath, datapath, pyexecrms, false) == false )
                    this.stop();
            }
            sourceFile = new File(datapath + "/" + pyexec1d);
            if (Files.notExists(sourceFile.toPath())) {
                if ( createFolder(sourcepath, datapath, pyexec1d, false) == false )
                    this.stop();
            }
            sourceFile = new File(datapath + "/" + pyexec2d);
            if (Files.notExists(sourceFile.toPath())) {
                if ( createFolder(sourcepath, datapath, pyexec2d, false) == false )
                    this.stop();
            }
            sourceFile = new File(datapath + "/" + pyexec3d);
            if (Files.notExists(sourceFile.toPath())) {
                if ( createFolder(sourcepath, datapath, pyexec3d, false) == false )
                    this.stop();
            }
            sourceFile = new File(datapath + "/" + pyexecmmc2d);
            if (Files.notExists(sourceFile.toPath())) {
                if ( createFolder(sourcepath, datapath, pyexecmmc2d, false) == false )
                    this.stop();
            }
            sourceFile = new File(datapath + "/" + pyexecmmc3d);
            if (Files.notExists(sourceFile.toPath())) {
                if ( createFolder(sourcepath, datapath, pyexecmmc3d, false) == false )
                    this.stop();
            }
        }

        /**
        * CREATE STAGE
        */
        stage.setTitle("Random Walk");
        stage.setWidth(this.stageWidth);
        stage.setHeight(this.stageHeight);
        stage.setResizable(false);
        stage.setX( (double) (this.screenWidth-this.stageWidth) );
        stage.setY( (double) (this.screenHeight-this.stageHeight) / 2.0 );

        DropShadow shadow = new DropShadow();

        /**
        * SET FIRST VIEW BORDERPANE
        */
        GridPane asettelu = new GridPane();
        asettelu.setMaxWidth(this.paneWidth);
        asettelu.setVgap(5);
        asettelu.setHgap(10);
        asettelu.setPadding(new Insets(0, 0, 0, 0));

        /**
        * FIRST VIEW BUTTONS
        */
        Button nappiScene1 = new Button("RMS vs SQRT(N)"); // SceneCalculation
        Button nappiScene2 = new Button("PATH TRACING"); // ScenePathTracing
        Button nappiScene3 = new Button("REAL TIME RMS"); // SceneRealTimeRms
        Button nappiScene4 = new Button("MMC DIFFUSION"); // SceneMMC
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

        /**
        * OTHER COMPONENTS
        */
        BorderPane asetteluMenu = new BorderPane();
        HBox isovalikkoMenu = new HBox();
        isovalikkoMenu.setPadding(new Insets(0, 0, 0, 0));
        isovalikkoMenu.setSpacing(10);
        VBox valikkoMenu = new VBox();
        valikkoMenu.setPadding(new Insets(10, 10, 10, 10));
        valikkoMenu.setSpacing(10);

        /**
        * OTHER VIEWS
        */
        SceneCalculation getCalcScene = new SceneCalculation();
        ScenePathTracing getPathScene = new ScenePathTracing();
        SceneRealTimeRms getRealScene = new SceneRealTimeRms();
        SceneMMC getMMCScene = new SceneMMC();

        BorderPane asetteluCalc = new BorderPane();
        BorderPane asetteluPath = new BorderPane();
        BorderPane asetteluReal = new BorderPane();
        BorderPane asetteluMMC = new BorderPane();

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

        /**
        * TEXT AREAS
        */
        HelpText helpText = new HelpText();
        /**
        * CALCULATION TEXT AREA
        */
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
        /**
        * PATH TRACING TEXT AREA
        */
        TextArea textAreaPath = new TextArea();
        textAreaPath.setMinWidth(this.textwidth);
        textAreaPath.setMaxWidth(this.textwidth);
        textAreaPath.setMinHeight(this.pathheight);
        textAreaPath.setMaxHeight(this.pathheight);
        textAreaPath.setFont(Font.font("Consolas",FontWeight.NORMAL, 18));
        textAreaPath.setBorder(null);
        textAreaPath.setEditable(false);
        textAreaPath.setBackground(
            new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        textAreaPath.setBlendMode(BlendMode.DIFFERENCE);
        /**
        * REAL TIME RMS TEXT AREA
        */
        TextArea textAreaReal = new TextArea();
        textAreaReal.setMinWidth(this.animwidth);
        textAreaReal.setMaxWidth(this.animwidth);
        textAreaReal.setMinHeight(this.animheight);
        textAreaReal.setMaxHeight(this.animheight);
        textAreaReal.setFont(Font.font("Consolas",FontWeight.NORMAL, 18));
        textAreaReal.setBorder(null);
        textAreaReal.setEditable(false);
        textAreaReal.setBackground(
            new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        textAreaReal.setBlendMode(BlendMode.DIFFERENCE);
        /**
        * MMC TEXT AREA
        */
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

        /**
        * TEXT AREA MENU
        */
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
        
        /**
        * REAL TIME RMS COMPONENTS
        */
        Canvas rtrmsAlusta = new Canvas(this.animwidth, this.animheight);
        rtrmsAlusta.setVisible(true);

        GraphicsContext piirturi = rtrmsAlusta.getGraphicsContext2D();
        piirturi.setFill(Color.BLACK);
        piirturi.fillRect(0, 0, this.animwidth, this.animheight);

        Pane pane = new Pane();
        pane.setPrefSize(this.animwidth, this.animheight);
        pane.getChildren().add(rtrmsAlusta);
        pane.setVisible(true);
        
        /**
        * MMC COMPONENTS
        */
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

        /**
        * FIRST VIEW BUTTON: HELP
        */
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

        /**
        * FIRST VIEW BUTTON: CLOSE
        */
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
                //System.gc();
                stage.close();
            }
        });
        closeNappiMenu.setVisible(true);

        /**
        * OTHER VIEWS BUTTON: EXECUTE CALCULATION
        */
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

        /**
        * OTHER VIEWS BUTTON: CALCULATION MENU
        */
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

        /**
        * OTHER VIEWS BUTTON: CALCULATION HELP
        */
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

        /**
        * OTHER VIEWS BUTTON: CALCULATION CLOSE
        */
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
                if (this.frame != null) {
                    if (this.frame.isShowing()
                        || this.frame.isActive()
                        || this.frame.isDisplayable())
                        this.frame.dispose();
                }
                //System.gc();
                stage.close();
            }
        });
        closeNappiCalc.setVisible(true);

        /**
        * OTHER VIEWS BUTTON: EXECUTE PATH TRACING
        */
        Button executeNappiPath = new Button("EXECUTE");
        executeNappiPath.setDefaultButton(true);
        executeNappiPath.setMinWidth(this.buttonWidth);
        executeNappiPath.setMaxWidth(this.buttonWidth);
        executeNappiPath.setStyle("-fx-background-color: Red");
        executeNappiPath.setTextFill(Color.WHITE);
        executeNappiPath.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                executeNappiPath.setEffect(shadow);
        });
        executeNappiPath.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                executeNappiPath.setEffect(null);
        });
        executeNappiPath.setVisible(true);

        /**
        * OTHER VIEWS BUTTON: PATH TRACING MENU
        */
        Button menuNappiPath = new Button("BACK TO MENU");
        menuNappiPath.setMinWidth(this.buttonWidth);
        menuNappiPath.setMaxWidth(this.buttonWidth);
        menuNappiPath.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                menuNappiPath.setEffect(shadow);
        });
        menuNappiPath.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                menuNappiPath.setEffect(null);
        });
        menuNappiPath.setVisible(true);

        /**
        * OTHER VIEWS BUTTON: PATH TRACING HELP
        */
        Button helpNappiPath = new Button("HELP");
        helpNappiPath.setMinWidth(this.buttonWidth);
        helpNappiPath.setMaxWidth(this.buttonWidth);
        helpNappiPath.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                helpNappiPath.setEffect(shadow);
        });
        helpNappiPath.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                helpNappiPath.setEffect(null);
        });
        helpNappiPath.setOnAction(event -> {
            textAreaPath.setText(helpText.pathtracing());
        });
        helpNappiPath.setVisible(true);

        /**
        * OTHER VIEWS BUTTON: PATH TRACING CLOSE
        */
        Button closeNappiPath = new Button("CLOSE");
        closeNappiPath.setMinWidth(this.buttonWidth);
        closeNappiPath.setMaxWidth(this.buttonWidth);
        closeNappiPath.setTextFill(Color.RED);
        closeNappiPath.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        GridPane.setHalignment(closeNappiPath, HPos.LEFT);
        closeNappiPath.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                closeNappiPath.setEffect(shadow);
        });
        closeNappiPath.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                closeNappiPath.setEffect(null);
        });
        closeNappiPath.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Close application?",
                ButtonType.OK, ButtonType.CANCEL);
            alert.showAndWait();
            if ( alert.getResult() == ButtonType.OK ) {
                if (this.frame != null) {
                    if (this.frame.isShowing()
                        || this.frame.isActive()
                        || this.frame.isDisplayable())
                        this.frame.dispose();
                }
                //System.gc();
                stage.close();
            }
        });
        closeNappiPath.setVisible(true);

        /**
        * OTHER VIEWS BUTTON: PLOT CHOICE REAL TIME RMS
        */
        Button standNorm = new Button("STD NORM");
        standNorm.setMinWidth(this.buttonWidth);
        standNorm.setMaxWidth(this.buttonWidth);
        standNorm.setBackground(new Background(
            new BackgroundFill(
                Color.GOLD,CornerRadii.EMPTY,Insets.EMPTY)));
        standNorm.setId("standnorm");
        standNorm.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                standNorm.setEffect(shadow);
        });
        standNorm.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                standNorm.setEffect(null);
        });
        standNorm.setOnMouseClicked((MouseEvent event) -> {
            if (standNorm.getText().equals("NORM")){
                // BUTTON PRESSED ON
                standNorm.setText("STD NORM");
                standNorm.setBackground(
                    new Background(
                        new BackgroundFill(
                            Color.GOLD,CornerRadii.EMPTY,Insets.EMPTY)));
                this.standnorm = true;
            } else if (standNorm.getText().equals("STD NORM")){
                // BUTTON PRESSED OFF
                standNorm.setText("NORM");
                standNorm.setBackground(
                    new Background(new BackgroundFill(
                        Color.LIGHTSKYBLUE,CornerRadii.EMPTY,Insets.EMPTY)));
                this.standnorm = false;
            }
        });
        standNorm.setVisible(true);

        /**
        * OTHER VIEWS BUTTON: RUN REAL TIME RMS
        */
        Button runReal = new Button("RUN");
        runReal.setDefaultButton(true);
        runReal.setMinWidth(this.buttonWidth);
        runReal.setMaxWidth(this.buttonWidth);
        runReal.setStyle("-fx-background-color: Red");
        runReal.setTextFill(Color.WHITE);
        runReal.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                runReal.setEffect(shadow);
        });
        runReal.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                runReal.setEffect(null);
        });
        runReal.setVisible(true);

        /**
        * OTHER VIEWS BUTTON: REAL TIME RMS MENU
        */
        Button menuNappiReal = new Button("BACK TO MENU");
        menuNappiReal.setMinWidth(this.buttonWidth);
        menuNappiReal.setMaxWidth(this.buttonWidth);
        menuNappiReal.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                menuNappiReal.setEffect(shadow);
        });
        menuNappiReal.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                menuNappiReal.setEffect(null);
        });
        menuNappiReal.setVisible(true);

        /**
        * OTHER VIEWS BUTTON: REAL TIME RMS HELP
        */
        Button helpNappiReal = new Button("HELP");
        helpNappiReal.setMinWidth(this.buttonWidth);
        helpNappiReal.setMaxWidth(this.buttonWidth);
        GridPane.setHalignment(helpNappiReal, HPos.LEFT);
        helpNappiReal.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                helpNappiReal.setEffect(shadow);
        });
        helpNappiReal.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                helpNappiReal.setEffect(null);
        });
        helpNappiReal.setOnAction(event -> {
            if (isovalikkoReal.getChildren().contains(pane)){
                isovalikkoReal.getChildren().remove(pane);
                isovalikkoReal.getChildren().add(textAreaReal);
            }
            textAreaReal.setText(helpText.realtimerms());
        });
        helpNappiReal.setVisible(true);

        /**
        * OTHER VIEWS BUTTON: REAL TIME RMS CLOSE
        */
        Button closeNappiReal = new Button("CLOSE");
        closeNappiReal.setMinWidth(this.buttonWidth);
        closeNappiReal.setMaxWidth(this.buttonWidth);
        closeNappiReal.setTextFill(Color.RED);
        closeNappiReal.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        GridPane.setHalignment(closeNappiReal, HPos.LEFT);
        closeNappiReal.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                closeNappiReal.setEffect(shadow);
        });
        closeNappiReal.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                closeNappiReal.setEffect(null);
        });
        closeNappiReal.setOnAction(event -> {
            if ( !getRealScene.isRunning() ) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Close application?",
                    ButtonType.OK, ButtonType.CANCEL);
                alert.showAndWait();
                if ( alert.getResult() == ButtonType.OK ) {
                    //System.gc();
                    stage.close();
                }
            }
        });
        closeNappiReal.setVisible(true);

        /**
        * OTHER VIEWS BUTTON: EXECUTE MMC
        */
        Button runMMC = new Button("ANIMATION");
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

        /**
        * OTHER VIEWS BUTTON: PLOT MMC
        */
        Button plotMMC = new Button("PLOT");
        plotMMC.setDefaultButton(true);
        plotMMC.setMinWidth(this.buttonWidth);
        plotMMC.setMaxWidth(this.buttonWidth);
        plotMMC.setStyle("-fx-background-color: Blue");
        plotMMC.setTextFill(Color.WHITE);
        plotMMC.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                plotMMC.setEffect(shadow);
        });
        plotMMC.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                plotMMC.setEffect(null);
        });
        plotMMC.setVisible(true);

        /**
        * OTHER VIEWS BUTTON: MMC MENU
        */
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

        /**
        * OTHER VIEWS BUTTON: MMC HELP
        */
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

        /**
        * OTHER VIEWS BUTTON: MMC REMOVE BARRIER
        */
        Button remBarNappiMMC = new Button("CONTINUE");
        remBarNappiMMC.setMinWidth(this.buttonWidth);
        remBarNappiMMC.setMaxWidth(this.buttonWidth);
        remBarNappiMMC.setTextFill(Color.BLACK);
        remBarNappiMMC.setBackground(new Background(
            new BackgroundFill(
                Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
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

        /**
        * OTHER VIEWS BUTTON: MMC CLOSE
        */
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
            if ( getMMCScene.timerIsRunning() && getMMCScene.barrierIsOn() ) {
                    if ( getMMCScene.walkState() == true ) {
                        PrintWriter pw = null;
                        if (getMMCScene.getProcOut() != null)
                            pw = new PrintWriter(getMMCScene.getProcOut());
                        if (pw != null) {
                            pw.println("-");
                            pw.flush();
                            pw.close();
                        }
                    }
                    Alert alert = new Alert(
                        Alert.AlertType.CONFIRMATION,
                        "Close application?",
                        ButtonType.OK, ButtonType.CANCEL);
                    alert.showAndWait();
                    if ( alert.getResult() == ButtonType.OK ) {
                        if (this.frame != null) {
                            if (this.frame.isShowing()
                                || this.frame.isActive()
                                || this.frame.isDisplayable())
                                this.frame.dispose();
                        }
                        //System.gc();
                        stage.close();
                    }
            } else if ( !getMMCScene.timerIsRunning() && !getMMCScene.barrierIsOn() ) {
                Alert alert = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Close application?",
                    ButtonType.OK, ButtonType.CANCEL);
                alert.showAndWait();
                if ( alert.getResult() == ButtonType.OK ) {
                    //System.gc();
                    stage.close();
                }
            }
        });
        closeNappiMMC.setVisible(true);

        /**
        * SET FIRST VIEW BORDERPANE
        */
        valikkoMenu.getChildren().addAll(
            asettelu,
            nappiMenuHelp,
            closeNappiMenu);
        isovalikkoMenu.getChildren().addAll(
            valikkoMenu,
            textAreaMenu);
        asetteluMenu.setCenter(isovalikkoMenu);

        /**
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
            textAreaCalc);
        asetteluCalc.setCenter(isovalikkoCalc);

        /**
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
            textAreaPath);
        asetteluPath.setCenter(isovalikkoPath);

        /**
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
            textAreaReal);
        asetteluReal.setCenter(isovalikkoReal);

        /**
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
            textAreaMMC);
        asetteluMMC.setCenter(isovalikkoMMC);

        /**
        * SET SCENES
        */
        Scene firstScene = new Scene(asetteluMenu,this.stageWidth,this.stageHeight);
        firstScene.getStylesheets().add("/styles/Styles.css");

        Scene calcScene = new Scene(asetteluCalc,this.stageWidth,this.stageHeight);
        calcScene.getStylesheets().add("/styles/Styles.css");

        Scene pathScene = new Scene(asetteluPath,this.stageWidth,this.stageHeight
            + (this.pathheight-this.textheight));
        pathScene.getStylesheets().add("/styles/Styles.css");

        Scene animScene = new Scene(asetteluReal,
            this.stageWidth + (this.animwidth-this.textwidth),
            this.stageHeight + (this.animheight-this.textheight));
        animScene.getStylesheets().add("/styles/Styles.css");

        Scene mmcScene = new Scene(asetteluMMC,
            this.stageWidth + (this.animwidth-this.textwidth),
            this.stageHeight + (this.animheight-this.textheight));
        mmcScene.getStylesheets().add("/styles/Styles.css");

        /**
        * SET SCENE CHOICE BUTTONS' EFFECTS
        * CALCULATION
        */
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
        /**
        * PATH TRACING
        */
        nappiScene2.setOnMouseClicked(event -> {
            stage.setTitle("Path Tracing");
            if ( stage.getHeight() == this.stageHeight ){
                stage.setHeight(this.stageHeight+(this.pathheight-this.textheight));
                stage.setY((this.screenHeight-this.stageHeight)/2-(this.pathheight-this.textheight)/2);
            }
            stage.setScene(pathScene);
        });
        menuNappiPath.setOnAction(event -> {
            stage.setTitle("Random Walk");
            if (textAreaPath.getText().equals(helpText.pathtracing()))
                textAreaMenu.setText(helpText.welcome());
            else
                textAreaMenu.setText(textAreaPath.getText());
            stage.setY((this.screenHeight-this.stageHeight)/2);
            stage.setHeight(this.stageHeight);
            stage.setScene(firstScene);
        });
        /**
        * REAL TIME RMS
        */
        nappiScene3.setOnMouseClicked(event -> {
            stage.setTitle("Real Time rms");
            if ( stage.getWidth() == this.stageWidth ){
                stage.setWidth(this.stageWidth+(this.animwidth-this.textwidth));
                stage.setHeight(this.stageHeight+(this.animheight-this.textheight));
                stage.setX(this.screenWidth/2-(this.animwidth-this.textwidth)+15);
                stage.setY((this.screenHeight-this.stageHeight)/2
                    -(this.animheight-this.textheight)/2-30);
            }
            stage.setScene(animScene);
        });
        menuNappiReal.setOnAction(event -> {
            stage.setTitle("Random Walk");
            if (textAreaReal.getText().equals(helpText.realtimerms()))
                textAreaMenu.setText(helpText.welcome());
            else
                textAreaMenu.clear();
            stage.setX(this.screenWidth/2);
            stage.setY((this.screenHeight-this.stageHeight)/2);
            stage.setWidth(this.stageWidth);
            stage.setHeight(this.stageHeight);
            stage.setScene(firstScene);
        });
        /**
        * MMC
        */
        nappiScene4.setOnMouseClicked(event -> {
            stage.setTitle("MMC Diffusion");
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

        /**
        * CREATE A FRAME FOR CALCULATION AND PATH TRACING PLOTS
        */
        this.frame = new JFrame();

        /**
        * CREATE AN INSTANCE FOR CODE EXECUTIONS
        */
        Execution ex = new Execution();

        /**
        * EXECUTE BUTTON CALCULATION
        */
        executeNappiCalc.setOnMouseClicked((MouseEvent event) -> {
            this.vars = getCalcScene.getVars();
            getCalcScene.setVar(8, "s");
            Data data = new Data(this.vars);
            boolean fail = false;

            int steps = parseInt(vars[3]);
            int dim = parseInt(this.vars[4]);
            String lattice = this.vars[7];

            if ( steps < 1 ) fail = true;
            if ( dim < 1 || dim > 3 ) fail = true;
            if ( !lattice.equals("l") && !lattice.equals("-") ) fail = true;

            if ( fail == true ) return;

            ex.executeRms(datafolder, datapath, fexec, pyexecrms,
                this.frame, data, this.vars);
        });

        /**
        * EXECUTE BUTTON PATH TRACING
        */
        executeNappiPath.setOnMouseClicked((MouseEvent event) -> {
            this.vars = getPathScene.getVars();
            getMMCScene.setVar(8, "s");
            Data data = new Data(this.vars);
            boolean fail = false;

            int particles = parseInt(vars[0]);
            double diam = parseDouble(vars[1]);
            int charge = parseInt(vars[2]);
            int steps = parseInt(vars[3]);
            int dim = parseInt(this.vars[4]);
            String fixed = this.vars[6];
            String lattice = this.vars[7];

            if ( particles < 0 ) fail = true;
            if ( diam <= 0.0 || diam >= 1.0 ) fail = true;
            if ( charge < 0 || charge > 2 ) fail = true;
            if ( steps < 1 ) fail = true;
            if ( dim < 1 || dim > 3 ) fail = true;
            if ( !fixed.equals("f") && !fixed.equals("-") ) fail = true;
            if ( !lattice.equals("l") && !lattice.equals("-") ) fail = true;

            if ( fail == true ) return;

            ex.executePath(datafolder, datapath, fexec, pyexec1d, pyexec2d,
                pyexec3d, this.frame, data, this.vars);
        });

        /**
        * ANIMATION TIMER FOR REAL TIME RMS ANIMATION
        */
        new AnimationTimer() {
            /**
            * REFESH ANIMATION IN ABOUT 100 MILLISECOND STEPS
            */
            private final long sleepNanoseconds = 100 * 1000000;
            private long prevTime = 0;
            private int dim;
            private String[] vars;

            @Override
            public void handle(long currentNanoTime) {

                if ((currentNanoTime - this.prevTime) < this.sleepNanoseconds) {
                    return;
                }

                if ( !getRealScene.isRunning())
                    return;

                if ( isovalikkoReal.getChildren().contains(textAreaReal) ) {
                    textAreaReal.clear();
                    isovalikkoReal.getChildren().remove(textAreaReal);
                    isovalikkoReal.getChildren().add(pane);
                }

                this.vars = getRealScene.getVars();
                /**
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

                this.dim = parseInt(this.vars[4]);

                piirturi.setGlobalAlpha(1.0);
                piirturi.setFill(Color.BLACK);
                if ( this.dim == 1 ) {
                    piirturi.fillRect(0, 0, 1.0/scalefactor*animwidth, animheight);
                } else {
                    piirturi.fillRect(0, 0, 1.0/scalefactor*animwidth, 1.0/scalefactor*animheight);
                }
                piirturi.fill();

                /**
                * DRAW ANIMATION
                */
                getRealScene.refresh(
                    datafolder, fexec, piirturi, scalefactor, animwidth, linewidth,
                    fxplot, rms_runs, rms_norm, newdata, mincount, maxcount,
                    standnorm
                );
                newdata = false;

                this.prevTime = currentNanoTime;
            }
        }.start();

        /**
        * RUN BUTTON REAL TIME RMS
        */
        runReal.setOnMouseClicked((MouseEvent event) -> {
            if (getRealScene.isRunning()) {
                getRealScene.stop();
                if ( this.isscaled == true ) {
                    if ( this.vars[4].equals("1") )
                        piirturi.scale(1.0/this.scalefactor, 1.0);
                    else
                        piirturi.scale(1.0/this.scalefactor, 1.0/this.scalefactor);
                }
                runReal.setText("RUN");
            } else {
                this.vars = getRealScene.getVars();
                getRealScene.setVar(8, "-");
                boolean fail = false;

                int particles = parseInt(vars[0]);
                int dim = parseInt(this.vars[4]);
                int steps = parseInt(vars[3]);
 
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
                    Math.sqrt( this.animwidth + Math.pow(dim,2.0) * 100
                    * Math.pow(Math.log10((double) steps),2.0) )
                    / Math.pow(Math.log10((double) steps),2.0);
 
                switch (dim) {
                    case 1:
                        this.linewidth = 1.0 / Math.log10((double) steps);
                        piirturi.scale(this.scalefactor, 1.0);
                        break;
                    case 2:
                        this.linewidth = 1.0 / ( this.scalefactor * Math.sqrt(Math.log10((double) steps)) );
                        piirturi.scale(this.scalefactor, this.scalefactor);
                        break;
                    case 3:
                        piirturi.scale(this.scalefactor, this.scalefactor);
                        break;
                    default:
                        break;
                }
                this.isscaled = true;
                piirturi.setGlobalAlpha(1.0 / this.scalefactor * Math.pow(Math.log10((double) steps),2.0));

                this.newdata = true;
                this.rms_runs = new double[10];
                this.rms_norm = new double[10];
                Arrays.fill(this.rms_runs, 0.0);
                Arrays.fill(this.rms_norm, 0.0);
                double expected = Math.sqrt((double) steps);

                if ( this.standnorm == false ) {
                    if ( (int) expected < 5 ) {
                        this.mincount = 0.0;
                    } else {
                        this.mincount = expected - 5.0;
                    }
                    this.maxcount = expected + 5.0;
                } else {
                    this.mincount = -4.0;
                    this.maxcount = 4.0;
                }

                this.fxplot.setWData("R_rms", "sqrt(N)", this.rms_runs, this.rms_runs, expected);
                this.fxplot.setHData("norm", this.rms_norm, this.rms_norm, this.mincount,
                    this.maxcount, this.standnorm);
        
                getRealScene.start();
                runReal.setText("STOP");
            }
        });

        /**
        * PLOT BUTTON MMC
        */
        plotMMC.setOnMouseClicked((MouseEvent event) -> {
            valikkoMMC.setDisable(true);
            this.vars = getMMCScene.getVars();
            getMMCScene.setVar(8, "s");
            Data data = new Data(this.vars);
            int particles = parseInt(vars[0]);
            double diam = parseDouble(vars[1]);
            int charge = parseInt(vars[2]);
            int dim = parseInt(vars[4]);
            String lattice = this.vars[7];
            boolean fail = false;

            if ( particles < 0 ) fail = true;
            if ( diam <= 0.0 || diam >= 1.0 ) fail = true;
            if ( charge < 0 || charge > 2 ) fail = true;
            if ( dim < 2 || dim > 3 ) fail = true;
            if ( !lattice.equals("l") && !lattice.equals("-") ) fail = true;

            if ( fail == true ) return;

            ex.executeMMC(datafolder, datapath, fexec, pyexecmmc2d,
                pyexecmmc3d, valikkoMMC, this.frame, data, this.vars);
        });

        /**
        * EXECUTE BUTTON MMC
        */
        runMMC.setOnMouseClicked((MouseEvent event) -> {
            if ( getMMCScene.timerIsRunning()) return;
            this.vars = getMMCScene.getVars();
            getMMCScene.setVar(8, "-");
            int particles = parseInt(vars[0]);
            double diam = parseDouble(vars[1]);
            int charge = parseInt(vars[2]);
            int dim = parseInt(vars[4]);
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
  
            int measure;
            double diff = 0.3;
            if ( particles < 25 ) {
                measure = 21;
            } else {
                measure = (int)( 3.0 * Math.sqrt( 2.0 * (double) particles ) );

                if ( (measure+1)%4 == 0 || measure%2 == 0 ) {
                    diff = diff + Math.sqrt(measure)/10.0 + measure/20.0 - 1.0;
                    measure -= 1;
                }else if ( measure%4 == 0 ) {
                    diff = diff + Math.sqrt(measure)/10.0 + measure/20.0 - 1.0;
                    measure -= 2;
                } else if ( measure%2 != 0 ) {
                    diff = diff + measure/20.0 - 1.0;
                }
            }

            this.scalefactor = (this.animwidth - 100.0) / (double) measure;
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

            /**
            * GET INITIAL DATA
            */
            File initialDataFile = new File(
                datapath + "/startMMC_" + dim + "D_" + particles + "N.xy");

            /**
            * DRAW MMC ANIMATION
            */
            getMMCScene.refresh(
                datafolder, initialDataFile, fexec, mmcpiirturi, this.scalefactor,
                this.animwidth, this.linewidth, this.fxplot, remBarNappiMMC,
                runMMC, plotMMC, closeNappiMMC, menuNappiMMC, helpNappiMMC,
                this.energy_x, this.energy_y, this.newdata, measure, diff
            );
            this.newdata = false;

        });

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
                if (getRealScene.runtimeIsRunning())
                    getRealScene.stopRuntime();
                if (ex.runtimeIsRunning())
                    ex.stopRuntime();
                System.gc();
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

        File sourceFile = new File(source + "/" + executable);
        File destinationFile = new File(destination + "/" + executable);
        InputStream fin = null;
        OutputStream fout = null;
        
        try {
            fin = new BufferedInputStream(new FileInputStream(sourceFile));
            fout = new BufferedOutputStream(new FileOutputStream(destinationFile));
            byte[] readBytes = new byte[1024];
            int readed;
            System.out.println("Copying resource file, please wait...");
            while((readed = fin.read(readBytes)) != -1){
                fout.write(readBytes, 0, readed);
            }
            System.out.println("Copying finished.");
        } catch (IOException e) {
            System.out.println("Resource file " + sourceFile + " not copied into new folder\n"+e.getMessage());
        } finally {
            try {
                if ( fin != null ) fin.close();
                if ( fout != null ) fout.close();
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
