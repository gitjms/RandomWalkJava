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
    final int stageHeight = 600;
    // COMPONENTS
    final int buttonWidth = 150;
    final int textwidth = 740;
    final int textheight = 540;
    final int animwidth = 900;
    final int animheight = 900;
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
        nappiScene1.setMinWidth(buttonWidth);
        nappiScene1.setMaxWidth(buttonWidth);
        nappiScene2.setMinWidth(buttonWidth);
        nappiScene2.setMaxWidth(buttonWidth);
        nappiScene3.setMinWidth(buttonWidth);
        nappiScene3.setMaxWidth(buttonWidth);

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
                nappiScene2.setEffect(null);
        });
        nappiScene3.setVisible(true);

        final Pane empty3 = new Pane();
        GridPane.setHalignment(empty3, HPos.LEFT);
        asettelu.add(empty3, 0, 5, 2, 1);

        asettelu.add(nappiMenuHelp, 0, 6, 2, 1);
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
        SceneNoCalculation getNoCalcScene = new SceneNoCalculation();
        SceneAnim getAnimScene = new SceneAnim();

        BorderPane asetteluCalc = new BorderPane();
        BorderPane asetteluNoCalc = new BorderPane();
        BorderPane asetteluAnim = new BorderPane();

        HBox isovalikkoCalc = new HBox();
        isovalikkoCalc.setPadding(new Insets(0, 0, 0, 0));
        isovalikkoCalc.setSpacing(0);
        
        HBox isovalikkoNoCalc = new HBox();
        isovalikkoNoCalc.setPadding(new Insets(0, 0, 0, 0));
        isovalikkoNoCalc.setSpacing(0);

        HBox isovalikkoAnim = new HBox();
        isovalikkoAnim.setPadding(new Insets(0, 0, 0, 0));
        isovalikkoAnim.setSpacing(0);

        VBox valikkoCalc = new VBox();
        valikkoCalc.setPadding(new Insets(10, 10, 10, 10));
        valikkoCalc.setSpacing(20);

        VBox valikkoNoCalc = new VBox();
        valikkoNoCalc.setPadding(new Insets(10, 10, 10, 10));
        valikkoNoCalc.setSpacing(20);

        VBox valikkoAnim = new VBox();
        valikkoAnim.setPadding(new Insets(10, 10, 10, 10));
        valikkoAnim.setSpacing(20);

        ////////////////////////////////////////////////////
        // TEXT AREA CALC
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

        ////////////////////////////////////////////////////
        // TEXT AREA NO CALC
        TextArea textAreaNoCalc = new TextArea();
        textAreaNoCalc.setMinWidth(textwidth);
        textAreaNoCalc.setMaxWidth(textwidth);
        textAreaNoCalc.setMinHeight(textheight);
        textAreaNoCalc.setMaxHeight(textheight);
        textAreaNoCalc.setFont(Font.font("Consolas",FontWeight.NORMAL, 18));
        textAreaNoCalc.setBorder(null);
        textAreaNoCalc.setEditable(false);
        textAreaNoCalc.setBackground(
            new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        textAreaNoCalc.setBlendMode(BlendMode.DIFFERENCE);

        ////////////////////////////////////////////////////
        // TEXT AREA ANIM
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

        ////////////////////////////////////////////////////
        // TEXT AREA MENU
        TextArea textAreaMenu = new TextArea(welcomeText());
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
        // ANIM COMPONENTS
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
            textAreaMenu.setText(helpTextMenu());
        });

        ////////////////////////////////////////////////////
        // OTHER VIEWS BUTTON: EXECUTE CALC
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

        // OTHER VIEWS BUTTON: CALC MENU
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

        // OTHER VIEWS BUTTON: CALC HELP
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
            textAreaCalc.setText(helpTextCalc());
        });
        helpNappiCalc.setVisible(true);

        ////////////////////////////////////////////////////
        // OTHER VIEWS BUTTON: EXECUTE NO CALC
        Button executeNappiNoCalc = new Button("EXECUTE");
        executeNappiNoCalc.setDefaultButton(true);
        executeNappiNoCalc.setMinWidth(buttonWidth);
        executeNappiNoCalc.setMaxWidth(buttonWidth);
        executeNappiNoCalc.setTextFill(Color.RED);
        executeNappiNoCalc.setBackground(
                    new Background(
                        new BackgroundFill(
                            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        executeNappiNoCalc.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                executeNappiNoCalc.setEffect(shadow);
        });
        executeNappiNoCalc.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                executeNappiNoCalc.setEffect(null);
        });
        executeNappiNoCalc.setVisible(true);

        // OTHER VIEWS BUTTON: NO CALC MENU
        Button menuNappiNoCalc = new Button("BACK TO MENU");
        menuNappiNoCalc.setMinWidth(buttonWidth);
        menuNappiNoCalc.setMaxWidth(buttonWidth);
        menuNappiNoCalc.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                menuNappiNoCalc.setEffect(shadow);
        });
        menuNappiNoCalc.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                menuNappiNoCalc.setEffect(null);
        });
        menuNappiNoCalc.setVisible(true);

        // OTHER VIEWS BUTTON: NO CALC HELP
        Button helpNappiNoCalc = new Button("HELP");
        helpNappiNoCalc.setMinWidth(buttonWidth);
        helpNappiNoCalc.setMaxWidth(buttonWidth);
        helpNappiNoCalc.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                helpNappiNoCalc.setEffect(shadow);
        });
        helpNappiNoCalc.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                helpNappiNoCalc.setEffect(null);
        });
        helpNappiNoCalc.setOnAction(event -> {
            textAreaNoCalc.setText(helpTextNoCalc());
        });
        helpNappiNoCalc.setVisible(true);

        ////////////////////////////////////////////////////
        // OTHER VIEWS BUTTON: RUN ANIM
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

        // OTHER VIEWS BUTTON: ANIM MENU
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

        // OTHER VIEWS BUTTON: ANIM HELP
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
            textAreaAnim.setText(helpTextAnim());
        });
        helpNappiAnim.setVisible(true);

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
        // SET CALC BORDERPANE
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
        // SET NO CALC BORDERPANE
        valikkoNoCalc.getChildren().addAll(
            menuNappiNoCalc,
            helpNappiNoCalc,
            getNoCalcScene.getSceneNoCalc(),
            executeNappiNoCalc);
        isovalikkoNoCalc.getChildren().addAll(
            valikkoNoCalc,
            textAreaNoCalc);
        asetteluNoCalc.setCenter(isovalikkoNoCalc);

        ////////////////////////////////////////////////////
        // SET ANIM BORDERPANE
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
        // SET SCENES
        Scene firstScene = new Scene(asetteluMenu,stageWidth,stageHeight);
        firstScene.getStylesheets().add("/styles/Styles.css");

        Scene calcScene = new Scene(asetteluCalc,stageWidth,stageHeight);
        calcScene.getStylesheets().add("/styles/Styles.css");

        Scene noCalcScene = new Scene(asetteluNoCalc,stageWidth,stageHeight);
        noCalcScene.getStylesheets().add("/styles/Styles.css");

        Scene animScene = new Scene(asetteluAnim,
            stageWidth + (animwidth-textwidth),
            stageHeight + (animheight-textheight));
        animScene.getStylesheets().add("/styles/Styles.css");

        ////////////////////////////////////////////////////
        // SET SCENE CHOICE BUTTONS' EFFECTS
        nappiScene1.setOnMouseClicked(event -> {
            stage.setTitle("R_rms calculation");
            stage.setScene(calcScene);
            
        });
        menuNappiCalc.setOnMouseClicked(event -> {
            stage.setTitle("Random Walk");
            if (textAreaCalc.getText().equals(helpTextCalc()))
                textAreaMenu.setText(welcomeText());
            else
                textAreaMenu.setText(textAreaCalc.getText());
            stage.setScene(firstScene);
        });

        nappiScene2.setOnMouseClicked(event -> {
            stage.setTitle("Random Walk simulation");
            stage.setScene(noCalcScene);
        });
        menuNappiNoCalc.setOnAction(event -> {
            stage.setTitle("Random Walk");
            if (textAreaNoCalc.getText().equals(helpTextNoCalc()))
                textAreaMenu.setText(welcomeText());
            else
                textAreaMenu.setText(textAreaNoCalc.getText());
            stage.setScene(firstScene);
        });

        nappiScene3.setOnMouseClicked(event -> {
            stage.setTitle("Random Walk Animation");
            if ( stage.getWidth() == stageWidth ){
                stage.setWidth(stageWidth+(animwidth-textwidth));
                stage.setHeight(stageHeight+(animheight-textheight));
                stage.setX(screenWidth/2-(animwidth-textwidth));
                stage.setY((screenHeight-stageHeight)/2-(animheight-textheight)/2-30);
                stage.setResizable(true);
                
                stage.setResizable(false);
            }
            stage.setScene(animScene);
        });
        menuNappiAnim.setOnAction(event -> {
            stage.setTitle("Random Walk");
            if (textAreaAnim.getText().equals(helpTextAnim()))
                textAreaMenu.setText(welcomeText());
            else
                textAreaMenu.clear();
            stage.setX(screenWidth/2);
            stage.setY((screenHeight-stageHeight)/2);
            stage.setResizable(true);
            stage.setWidth(stageWidth);
            stage.setHeight(stageHeight);
            stage.setResizable(false);
            stage.setScene(firstScene);
        });

        ////////////////////////////////////////////////////
        // CREATE A FRAME FOR CALC AND NO CALC PLOTS
        JFrame frame = new JFrame();

        ////////////////////////////////////////////////////
        // CREATE AN INSTANCE FOR CODE EXECUTIONS
        Execution ex = new Execution();

        ////////////////////////////////////////////////////
        // EXECUTE BUTTON CALC
        executeNappiCalc.setOnMouseClicked((MouseEvent event) -> {
            // BUTTON PRESSED ON
            this.vars = getCalcScene.getVars();
            Data data = new Data(this.vars);
            ex.executeRms(datafolder, textAreaNoCalc, frame, data, vars);
        });

        ////////////////////////////////////////////////////
        // EXECUTE BUTTON NO CALC
        executeNappiNoCalc.setOnMouseClicked((MouseEvent event) -> {
            // BUTTON PRESSED ON
            this.vars = getNoCalcScene.getVars();
            Data data = new Data(this.vars);
            ex.executeTrace(datafolder, textAreaNoCalc, frame, data, vars);
        });

        ////////////////////////////////////////////////////
        // CREATE AN INSTANCE FOR REAL TIME PLOTTING
        FXPlot fxplot = new FXPlot(screenHeight);

        ////////////////////////////////////////////////////
        // ANIMATION TIMER FOR REAL TIME RANDOM WALK
        new AnimationTimer() {
            // päivitetään animaatiota noin 100 millisekunnin välein
            private final long sleepNanoseconds = 100 * 1000000;
            private long prevTime = 0;
            private int dim;

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
                dim = Integer.valueOf(vars[3]);

                if ( dim == 1 ) {
                    scalefactor = Math.sqrt((animwidth+200)
                                / Math.sqrt(Double.valueOf(vars[2])))
                                - Math.sqrt(Math.log10(Double.valueOf(vars[2])));
                    linewidth = (Math.log10(Double.valueOf(vars[2])) + 1.0)
                                / Math.sqrt(Double.valueOf(vars[2]));
                    piirturi.scale(scalefactor, 1.0);
                    isscaled = true;
                } else if ( dim > 1 ) {
                    scalefactor = Math.sqrt((animwidth+200)
                            / Math.sqrt(Double.valueOf(vars[2])))
                            - Math.sqrt(Math.log10(Double.valueOf(vars[2])));
                    linewidth = Math.pow(Math.log10(Double.valueOf(vars[2])),2.0)
                            / (Math.sqrt(2.0 * scalefactor * Double.valueOf(vars[2])));
                    piirturi.scale(scalefactor, scalefactor);
                    isscaled = true;
                }

                getAnimScene.refresh(datafolder, fexec, piirturi, scalefactor, linewidth, fxplot, rms_runs, newdata);
                newdata = false;

                // älä muuta tätä
                prevTime = currentNanoTime;
            }
        }.start();

        ////////////////////////////////////////////////////
        // RUN BUTTON ANIM
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
                Arrays.fill(this.rms_runs, 0.0);
                fxplot.setData("sqrt(N)", "R_rms", rms_runs, rms_runs);
                getAnimScene.start();
                runAnim.setText("STOP");
            }

            // FOR ONE ROUND OPERATION
            /*if ( isovalikkoAnim.getChildren().contains(textAreaAnim)) {
                    textAreaAnim.clear();
                    isovalikkoAnim.getChildren().remove(textAreaAnim);
                    isovalikkoAnim.getChildren().add(pane);
                }

                piirturi.setGlobalAlpha(1.0);
                piirturi.setFill(Color.BLACK);
                piirturi.fillRect(0, 0, 1.0/scalefactor*animwidth, 1.0/scalefactor*animheight);
                piirturi.fill();
                piirturi.setStroke(Color.YELLOW);
                if (isscaled) {
                    piirturi.scale(1.0/scalefactor, 1.0/scalefactor);
                    isscaled = false;
                }

                String[] vars = getAnimScene.getVars();

                if ( Double.valueOf(vars[2]) < 100.0 ) {
                    scalefactor = 14.0 - 0.0 * Math.log10(Double.valueOf(vars[2]));
                    piirturi.setLineWidth((10.0 - Math.log10(Double.valueOf(vars[2]))) / 55.0);
                    piirturi.setGlobalAlpha((10.0 - Math.log10(Double.valueOf(vars[2]))) / 15.0);
                } else if ( Double.valueOf(vars[2]) < 1000.0 ) {
                    scalefactor = 13.0 - 1.0 * Math.log10(Double.valueOf(vars[2]));
                    piirturi.setLineWidth((10.0 - Math.log10(Double.valueOf(vars[2]))) / 40.0);
                    piirturi.setGlobalAlpha((10.0 - Math.log10(Double.valueOf(vars[2]))) / 20.0);
                } else if ( Double.valueOf(vars[2]) < 10000.0 ) {
                    scalefactor = 12.0 - 2.0 * Math.log10(Double.valueOf(vars[2]));
                    piirturi.setLineWidth((10.0 - Math.log10(Double.valueOf(vars[2]))) / 25.0);
                    piirturi.setGlobalAlpha((10.0 - Math.log10(Double.valueOf(vars[2]))) / 30.0);
                } else if ( Double.valueOf(vars[2]) < 100000.0 ) {
                    scalefactor = 10.5 - 2.0 * Math.log10(Double.valueOf(vars[2]));
                    piirturi.setLineWidth((10.0 - Math.log10(Double.valueOf(vars[2])))/10.0);
                    piirturi.setGlobalAlpha((10.0 - Math.log10(Double.valueOf(vars[2])))/40.0);
                } else {
                    scalefactor = 1.0 - 1.0 / (15.0 - 2.0 * Math.log10(Double.valueOf(vars[2])));
                    piirturi.setLineWidth((10.0 - Math.log10(Double.valueOf(vars[2])))/20.0);
                    piirturi.setGlobalAlpha((10.0 - Math.log10(Double.valueOf(vars[2])))/10.0);
                }
                piirturi.scale(scalefactor, scalefactor);
                isscaled = true;

                getAnimScene.refresh(datafolder, fexec, piirturi, scalefactor);
                runAnim.setText("RUN");*/
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

    public String helpTextMenu() {
        String text = " Button 'RRMS vs SQRT(N)' shows a control panel with which you can\n"
                    + " calculate root mean square distances (R_rms) of random walk particles.\n\n"
                    + " Program plots 'R_rms' versus 'sqrt(steps)'.\n\n"
                    + " Every run will save the data in a file replacing the previous one.\n\n"
                    + " ----------------------------------------------------------------------\n\n"
                    + " Button 'RANDOM WALK' shows a control panel with which you can plot\n"
                    + " different random walk simulations.\n\n"
                    + " You can choose to save the data or to only plot without saving.";
    
        return text;
    }

    public String helpTextCalc() {
        String text = " Diameter is a positive real number on the interval ]0.0, 1.0[.\n\n"
                    + " Steps is a positive integer. It means the cumulative random steps\n"
                    + " the particles take while moving.\n\n"
                    + " Skip is a positive integer meaning jumping in the iteration steps.\n"
                    + " Iteration starts from skip, not from 1. No skip is 0 or 1.\n\n"
                    + " Dimension is either 1, 2, or 3. One means moving along x-axis, two\n"
                    + " means moving on a plane of x and y axes, three means moving in a\n"
                    + " cube of x, y, and z axes.\n\n"
                    + " --------------------------------------------------------------------\n\n"
                    + " Program plots 'R_rms' versus 'sqrt(steps)'.\n\n"
                    + " Every run will save the data in a file replacing the previous one.\n\n"
                    + " You can save the image with 'Right-click + Save As...' or 'ctrl+S'.\n"
                    + " Saving formats are: PNG, JPEG, BMP, GIF, SVG, EPS, and PDF.";
    
        return text;
    }

    public String helpTextNoCalc() {
        String text = " Number of particles is a positive integer, at least 1.\n\n"
                    + " Diameter is a positive real number on the interval ]0.0, 1.0[.\n\n"
                    + " Steps is a positive integer. It means the cumulative random steps\n"
                    + " the particles take while moving.\n\n"
                    + " Dimension is either 1, 2, or 3. One means moving along x-axis, two\n"
                    + " means moving on a plane of x and y axes, three means moving in a\n"
                    + " cube of x, y, and z axes.\n\n"
                    + " --------------------------------------------------------------------\n\n"
                    + " - Avoid sets the particles to self avoiding mode.\n"
                    + " - Save toggle changes mode between realtime (no save) and save mode.\n"
                    + "   Real time doesn't save the data, but shows the path trace in real\n"
                    + "   time. Save mode saves the data, and you can plot the trace paths\n"
                    + "   by yourself.";
    
        return text;
    }

    public String helpTextAnim() {
        String text = " Number of particles is a positive integer, at least 1.\n\n"
                    + " Diameter is a positive real number on the interval ]0.0, 1.0[.\n\n"
                    + " Steps is a positive integer. It means the cumulative random steps\n"
                    + " the particles take while moving.\n\n"
                    + " Dimension is either 1, 2, or 3. One means moving along x-axis, two\n"
                    + " means moving on a plane of x and y axes, three means moving in a\n"
                    + " cube of x, y, and z axes.\n\n"
                    + " --------------------------------------------------------------------\n\n"
                    + " - Avoid sets the particles to self avoiding mode.\n"
                    + " - Save toggle changes mode between realtime (no save) and save mode.\n"
                    + "   Real time doesn't save the data, but shows the path trace in real\n"
                    + "   time. Save mode saves the data, and you can plot the trace paths\n"
                    + "   by yourself.";
    
        return text;
    }

    public String welcomeText() {
        String text = "\n"
                    + "        /////       ///       //    // ///        /////       //   //\n" 
                    + "       ///  //     ////      ///   // //////    ///    //    ///  ///\n"
                    + "      ///    //   /////     ////  // ///   // ///      //   //// ////\n"
                    + "     ///   //    /// //    ///// // ///    /////       //  //////////\n"
                    + "    //////      ///  //   /// //// ///     ////        // /// //// //\n"
                    + "   ///   //    // ----------------------------------  // ///  ///  //\n"
                    + "  ///     //  // |                                  | / ///   //   //\n"
                    + " ///     /// /// |           Jari Sunnari           |  ///         //\n"
                    + "                 |       Kandidaatintutkielma       |                \n"
                    + "                 |                                  |                \n"
                    + "       ////      |               2019               |    ////  ////  \n"
                    + "       ////      |                                  |   ////  ////   \n"
                    + "       ////       ----------------------------------   //// ////     \n"
                    + "       ////          ////  ////  ////      ////       ///////        \n"
                    + "       ////  ////   ////  ////   ////     ////       ////////        \n"
                    + "       //// ////// ////  ////////////    ////       ////   ////      \n"
                    + "       /////// ///////  ////     ////   ////       ////    ////      \n"
                    + "       //////  //////  ////      ////  /////////  ////     /////       ";
    
        return text;
    }

    public static void main(String[] args) {

        launch(args);

    }
}
