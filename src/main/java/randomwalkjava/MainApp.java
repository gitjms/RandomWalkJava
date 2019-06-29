package randomwalkjava;

import com.sun.glass.ui.Screen;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.util.Pair;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler.ChartTheme;

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
    final int textheight = 510;
    final int paneWidth = 200;
    final int screenWidth = Screen.getMainScreen().getWidth();
    final int screenHeight = Screen.getMainScreen().getHeight();
    // FILES AND FOLDERS
    final String path = "C:\\DATA";
    final String fexec = "walk.exe";
    final String pyexec1d = "python plot1d.py";
    final String pyexec2d = "python plot2d.py";
    final String pyexec3d = "python plot3d.py";
    // DATA
    public String[] vars;

    @Override
    public void start(Stage stage) throws Exception {
        ////////////////////////////////////////////////////
        // FILE AND FOLDER CHECK
        File folder = new File(path);
        File sourceFile = new File(path + "\\" + fexec);
        boolean sourceFound = false;
        if (Files.notExists(folder.toPath())){
            sourceFound = createFolder(path, true);
            if (sourceFound == false)
                this.stop();
        } else if (Files.notExists(sourceFile.toPath()))
            sourceFound = createFolder(path, false);
            if (sourceFound == false)
                this.stop();

        ////////////////////////////////////////////////////
        // CREATE STAGE
        stage.setTitle("Random Walk");
        stage.setMinWidth(stageWidth);
        stage.setMaxWidth(stageWidth);
        stage.setMinHeight(stageHeight);
        stage.setMaxHeight(stageHeight);
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
        
        Button nappiNoHelp = new Button("HELP");
        Button nappiMenuHelp = new Button("HELP");
        nappiScene1.setMinWidth(buttonWidth);
        nappiScene1.setMaxWidth(buttonWidth);
        nappiScene2.setMinWidth(buttonWidth);
        nappiScene2.setMaxWidth(buttonWidth);
        
        nappiNoHelp.setMinWidth(buttonWidth);
        nappiNoHelp.setMaxWidth(buttonWidth);
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

        
        asettelu.add(nappiMenuHelp, 0, 4, 2, 1);
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

        BorderPane asetteluCalc = new BorderPane();
        BorderPane asetteluNoCalc = new BorderPane();

        HBox isovalikkoCalc = new HBox();
        isovalikkoCalc.setPadding(new Insets(0, 0, 0, 0));
        isovalikkoCalc.setSpacing(0);
        
        HBox isovalikkoNoCalc = new HBox();
        isovalikkoNoCalc.setPadding(new Insets(0, 0, 0, 0));
        isovalikkoNoCalc.setSpacing(0);

        VBox valikkoCalc = new VBox();
        valikkoCalc.setPadding(new Insets(10, 10, 10, 10));
        valikkoCalc.setSpacing(20);

        VBox valikkoNoCalc = new VBox();
        valikkoNoCalc.setPadding(new Insets(10, 10, 10, 10));
        valikkoNoCalc.setSpacing(20);

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
        Button helpNappi = new Button("HELP");
        helpNappi.setMinWidth(buttonWidth);
        helpNappi.setMaxWidth(buttonWidth);
        GridPane.setHalignment(helpNappi, HPos.LEFT);
        helpNappi.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                helpNappi.setEffect(shadow);
        });
        helpNappi.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                helpNappi.setEffect(null);
        });
        helpNappi.setOnAction(event -> {
            textAreaCalc.setText(helpTextCalc());
        });
        helpNappi.setVisible(true);

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
        nappiNoHelp.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                nappiNoHelp.setEffect(shadow);
        });
        nappiNoHelp.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                nappiNoHelp.setEffect(null);
        });
        nappiNoHelp.setOnAction(event -> {
            textAreaNoCalc.setText(helpTextNoCalc());
        });
        nappiNoHelp.setVisible(true);

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
        // SET OTHER VIEWS BORDERPANES
        valikkoCalc.getChildren().addAll(
            menuNappiCalc,
            getCalcScene.getSceneCalc(),
            executeNappiCalc,
            helpNappi);
        isovalikkoCalc.getChildren().addAll(
            valikkoCalc,
            textAreaCalc);
        asetteluCalc.setCenter(isovalikkoCalc);

        valikkoNoCalc.getChildren().addAll(
            menuNappiNoCalc,
            getNoCalcScene.getSceneNoCalc(),
            executeNappiNoCalc,
            nappiNoHelp);
        isovalikkoNoCalc.getChildren().addAll(
            valikkoNoCalc,
            textAreaNoCalc);
        asetteluNoCalc.setCenter(isovalikkoNoCalc);

        ////////////////////////////////////////////////////
        // SET SCENES
        Scene firstScene = new Scene(asetteluMenu,stageWidth,stageHeight);
        firstScene.getStylesheets().add("/styles/Styles.css");

        Scene calcScene = new Scene(asetteluCalc,stageWidth,stageHeight);
        calcScene.getStylesheets().add("/styles/Styles.css");

        nappiScene1.setOnMouseClicked(event -> {
            stage.setTitle("R_rms calculation");
            /*if (textAreaMenu.getText().equals(helpTextMenu()))
                textAreaCalc.setText("");*/
            //textAreaCalc.setText(textAreaMenu.getText());
            stage.setScene(calcScene);
            
        });
        menuNappiCalc.setOnMouseClicked(event -> {
            stage.setTitle("Random Walk");
            if (textAreaCalc.getText().equals(helpTextCalc()))
                textAreaMenu.setText("");
            else
                textAreaMenu.setText(textAreaCalc.getText());
            stage.setScene(firstScene);
        });

        Scene noCalcScene = new Scene(asetteluNoCalc,stageWidth,stageHeight);
        noCalcScene.getStylesheets().add("/styles/Styles.css");

        nappiScene2.setOnMouseClicked(event -> {
            stage.setTitle("Random Walk simulation");
            /*if (textAreaMenu.getText().equals(helpTextMenu()))
                textAreaNoCalc.setText("");*/
            //textAreaNoCalc.setText(textAreaMenu.getText());
            stage.setScene(noCalcScene);
        });
        menuNappiNoCalc.setOnAction(event -> {
            stage.setTitle("Random Walk");
            if (textAreaNoCalc.getText().equals(helpTextNoCalc()))
                textAreaMenu.setText("");
            else
                textAreaMenu.setText(textAreaNoCalc.getText());
            stage.setScene(firstScene);
        });

        XYChart calcChart = new XYChartBuilder()
            .width(chartWidth).height(chartHeight)
            .theme(ChartTheme.Matlab).build();

        XChartPanel chartPanel = new XChartPanel(calcChart);
        JFrame frame = new JFrame();
        
        Execution ex = new Execution();
        ////////////////////////////////////////////////////
        // EXECUTE CALC
        executeNappiCalc.setOnMouseClicked((MouseEvent event) -> {
            // BUTTON PRESSED ON
            this.vars = getCalcScene.getVars();
            Data data = new Data(this.vars);
            ex.executeRms(folder, textAreaNoCalc, frame, data, vars);
        });

        ////////////////////////////////////////////////////
        // EXECUTE NO CALC
        executeNappiNoCalc.setOnMouseClicked((MouseEvent event) -> {
            // BUTTON PRESSED ON
            this.vars = getNoCalcScene.getVars();
            Data data = new Data(this.vars);

            /////////////////////////
            // CREATEDATA NO CALC  //
            /////////////////////////
            // DATA SAVED -> READ DATA FIRST
            if (this.vars[6].equals("s")) {
                ex.executeTrace(folder, textAreaNoCalc, frame, data, vars);
            // GET REAL TIME DATA FROM SOMEWHERE AND PLOT IT*/
            } else if (this.vars[6].trim().equals("-")){
                //textAreaNoCalc.setText(data.createData(folder, false));
                    /*textAreaNoCalc.setText(data.createData(folder, fexec, true));
                int particles = Integer.valueOf(this.vars[0]);
                int dimension = Integer.valueOf(this.vars[4]);

                // GET DATA FROM DATA.READDATANOCALC...()
                String header = "";
                Pair<String,List<Double[]>> dataPairX;
                File xDataFile = new File(
                    path + "\\" + "x_path"
                    + this.vars[4] + "D_"
                    + this.vars[0] + ".xy");
                xDataPath = path + "\\" + "x_path"
                    + this.vars[4] + "D_"
                    + this.vars[0] + ".xy";
                dataPairX = Data.readDataNoCalc(xDataFile, particles);
                if (this.vars[4].equals("1") ){
                    header = dataPairX.getKey();
                } else if ( this.vars[4].equals("2") || this.vars[4].equals("3") ){
                    header = dataPairX.getKey().substring(2, 15);
                }
                List<Double[]> xdata = dataPairX.getValue();
                int runs = xdata.size();
                double[][] xDataToChart = new double[runs][particles];
                double[][] yDataToChart = new double[runs][particles];
                double[][] zDataToChart = new double[runs][particles];

                // FORMAT DATA TO BE COMPATIBLE WITH CHART
                for (int i = 0; i < particles; i++) {
                    for (int j = 0; j < runs; j++) {
                        xDataToChart[j][i] = xdata.get(j)[i];
                    }
                }
                    calcChart.getSeriesMap().clear();//.removeSeries(header);
                    chartPanel.removeAll();
                    frame.getContentPane().removeAll();
                    frame.setTitle("Random Walk - Path Tracing");
                    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    frame.setBounds(10,
                            (screenHeight-chartHeight)/2,
                            chartWidth, chartHeight);
                    calcChart.setTitle("Random Walk, N="+this.vars[0]+", "+runs+" runs");
                    for (int i = 0; i < particles; i++) {
                        calcChart.addSeries(
                            String.valueOf(
                                header+", amount="+particles+", "+dimension+"D"+i),
                            xDataToChart[i],yDataToChart[i]);
                    }
                    calcChart.getStyler().setLegendVisible(false);
                    calcChart.getStyler()
                        .setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
                    calcChart.getStyler().setXAxisDecimalPattern("0.0");
                    calcChart.getStyler().setYAxisDecimalPattern("0.0");
                    calcChart.getStyler().setMarkerSize(0);
                    chartPanel.getChart();
                    frame.add(chartPanel);
                    frame.repaint();
                    frame.pack();
                    frame.setVisible(true);
            }
        });

        stage.setScene(firstScene);
        
        //Canvas piirtoalusta = new Canvas(leveys, korkeus);
        //root.getChildren().add(piirtoalusta);
        
        //GraphicsContext piirturi = piirtoalusta.getGraphicsContext2D();
        
        //RandomWalk rw = new RandomWalk(800, 800);
        //rw.alustaSatunnaisesti();

        
        
        /*nappi.setOnMouseClicked((MouseEvent event) -> {
            //leike.play();
        });*/

        /*new AnimationTimer() {
            // päivitetään animaatiota noin 100 millisekunnin välein
            private long sleepNanoseconds = 100 * 1000000;
            private long prevTime = 0;
            
            @Override
            public void handle(long currentNanoTime) {
                // päivitetään animaatiota noin 200 millisekunnin välein
                if ((currentNanoTime - prevTime) < sleepNanoseconds) {
                    return;
                }

                // piirretään alusta
                piirturi.setFill(Color.YELLOW);
                piirturi.clearRect(0, 0, leveys, korkeus);

                // piirretään peli
                piirturi.setFill(Color.BLACK);

                //int[][] taulukko = gol.getTaulukko();
                /*for (int x = 0; x < taulukko.length; x++) {
                    for (int y = 0; y < taulukko[x].length; y++) {
                        if (taulukko[x][y] == 1) {
                            piirturi.fillRect(x * 4, y * 4, 4, 4);
                        }
                    }
                }
                // kutsutaan game of lifelle kehity-metodia
                //rw.etene();

                // älä muuta tätä
                prevTime = currentNanoTime;
            }
        }.start();*/

        stage.show();
    }

    public boolean createFolder(String path, boolean createAll){
        if (createAll == true) {
            File dataFile = new File(path);
            try {
                System.out.println("creating directory: " + path);
                dataFile.mkdir();
            } catch (SecurityException se) {
                System.out.println("Could not create a new directory\n"+se.getMessage());
            }
        }

        File sourceFile = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\lib\\walk.exe");
        if (Files.notExists(sourceFile.toPath())) {
            System.out.println("Fortran source file not found");
            return false;
        }

        File destinationFile = new File(path+"\\walk.exe");
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
            System.out.println("Resource file not copied into new folder\n"+e.getMessage());
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
                    + "   by yourself.\n"
                    + " - Xgraph saves the data in XGraph format. Default is normal save.";
    
        return text;
    }

    public String welcomeText() {
        String text = "\n"
                    + "        /////       ///       //    // ///        /////       //   //\n" 
                    + "       ///  //     ////      ///   // //////    ///    //    ///  ///\n"
                    + "      ///    //   /////     ////  // ///   // ///      //   //// ////\n"
                    + "     ///   //    /// //    ///// // ///    /////       //  //////////\n"
                    + "    //////      ///  //   /// //// ///     ////        // /// //// //\n"
                    + "   ///   //    ////////  ///  /// ///     /////       // ///  ///  //\n"
                    + "  ///     //  ///    // ///   // ///    //  ///     //  ///   //   //\n"
                    + " ///     /// ///     /////    / ///////       //////   ///         //\n"
                    + "\n\n"
                    + "               ///           // ///       ///       ///   //\n"
                    + "               ///          // ////      ///       ///   //\n"
                    + "               ///         // /////     ///       ///  //\n"
                    + "               ///   //   // /// //    ///       /////\n"
                    + "               ///  ///  // ///  //   ///       /////\n"
                    + "               /// //// // ////////  ///       ///  //\n"
                    + "               ///// //// ///    // ///       ///    //\n"
                    + "               ////  /// ///     /////////// ///     ///";
    
        return text;
    }

    public static void main(String[] args) {

        launch(args);

    }
}
