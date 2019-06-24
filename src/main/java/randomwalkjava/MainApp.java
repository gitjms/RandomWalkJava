package randomwalkjava;

import com.sun.glass.ui.Screen;
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
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.ChartTheme;

public class MainApp extends Application {

    final int stageWidth = 940;
    final int stageHeight = 600;
    final int chartWidth = 860;
    final int chartHeight = 605;
    final int buttonWidth = 150;
    final int textwidth = 740;
    final int textheight = 510;
    final int paneWidth = 200;
    final int screenWidth = Screen.getMainScreen().getWidth();
    final int screenHeight = Screen.getMainScreen().getHeight();
    final String path = "C:\\DATA";
    public String[] vars;

    @Override
    public void start(Stage stage) throws Exception {
        ////////////////////////////////////////////////////
        // FILE AND FOLDER CHECK
        File folder = new File(path);
        File dataFile = new File(path + "\\rms_2D.xy");
        File sourceFile = new File(path + "\\walk.exe");
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
        asettelu.setHgap(0);
        asettelu.setPadding(new Insets(5, 5, 0, 0));

        ////////////////////////////////////////////////////
        // FIRST VIEW LABELS AND BUTTONS
        Label rmsCalc = new Label("Calculate R_rms");
        Label randomWalk = new Label("Random Walk");
        Button nappiScene1 = new Button("R_RMS vs SQRT(N)");
        Button nappiScene2 = new Button("RANDOM WALK");
        nappiScene1.setMinWidth(paneWidth);
        nappiScene1.setMaxWidth(paneWidth);
        nappiScene2.setMinWidth(paneWidth);
        nappiScene2.setMaxWidth(paneWidth);

        GridPane.setHalignment(rmsCalc, HPos.LEFT);
        asettelu.add(rmsCalc, 0, 0);

        GridPane.setHalignment(nappiScene1, HPos.LEFT);
        asettelu.add(nappiScene1, 0, 1, 2, 1);
        nappiScene1.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        nappiScene1.setId("scene1");
        nappiScene1.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                nappiScene1.setEffect(shadow);
        });
        nappiScene1.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                nappiScene1.setEffect(null);
        });


        GridPane.setHalignment(randomWalk, HPos.LEFT);
        asettelu.add(randomWalk, 0, 3);

        GridPane.setHalignment(nappiScene2, HPos.LEFT);
        asettelu.add(nappiScene2, 0, 4, 2, 1);
        nappiScene2.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        nappiScene2.setId("scene2");
        nappiScene2.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                nappiScene2.setEffect(shadow);
        });
        nappiScene2.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                nappiScene2.setEffect(null);
        });

        ////////////////////////////////////////////////////
        // OTHER VIEWS
        NappiInput napitjamuut = new NappiInput();

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
        valikkoCalc.setSpacing(10);

        VBox valikkoNoCalc = new VBox();
        valikkoNoCalc.setPadding(new Insets(10, 10, 10, 10));
        valikkoNoCalc.setSpacing(10);

        ////////////////////////////////////////////////////
        // TEXT AREA CALC
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

        ////////////////////////////////////////////////////
        // TEXT AREA NO CALC
        TextArea textAreaNoCalc = new TextArea();
        textAreaNoCalc.setMinWidth(this.textwidth);
        textAreaNoCalc.setMaxWidth(this.textwidth);
        textAreaNoCalc.setMinHeight(this.textheight);
        textAreaNoCalc.setMaxHeight(this.textheight);
        textAreaNoCalc.setFont(Font.font("Consolas",FontWeight.NORMAL, 18));
        textAreaNoCalc.setBorder(null);
        textAreaNoCalc.setEditable(false);
        textAreaNoCalc.setBackground(
            new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        textAreaNoCalc.setBlendMode(BlendMode.DIFFERENCE);

        ////////////////////////////////////////////////////
        // OTHER VIEWS BUTTON: EXECUTE CALC
        Button executeNappiCalc = new Button("EXECUTE");
        executeNappiCalc.setMinWidth(this.buttonWidth);
        executeNappiCalc.setMaxWidth(this.buttonWidth);
        executeNappiCalc.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                executeNappiCalc.setEffect(shadow);
        });
        executeNappiCalc.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                executeNappiCalc.setEffect(null);
        });

        Button goBackNappiCalc = new Button("GO BACK");
        goBackNappiCalc.setMinWidth(this.buttonWidth);
        goBackNappiCalc.setMaxWidth(this.buttonWidth);
        goBackNappiCalc.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                goBackNappiCalc.setEffect(shadow);
        });
        goBackNappiCalc.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                goBackNappiCalc.setEffect(null);
        });

        ////////////////////////////////////////////////////
        // OTHER VIEWS BUTTON: EXECUTE NO CALC
        Button executeNappiNoCalc = new Button("EXECUTE");
        executeNappiNoCalc.setMinWidth(this.buttonWidth);
        executeNappiNoCalc.setMaxWidth(this.buttonWidth);
        executeNappiNoCalc.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                executeNappiNoCalc.setEffect(shadow);
        });
        executeNappiNoCalc.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                executeNappiNoCalc.setEffect(null);
        });

        Button goBackNappiNoCalc = new Button("GO BACK");
        goBackNappiNoCalc.setMinWidth(this.buttonWidth);
        goBackNappiNoCalc.setMaxWidth(this.buttonWidth);
        goBackNappiNoCalc.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                goBackNappiNoCalc.setEffect(shadow);
        });
        goBackNappiNoCalc.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                goBackNappiNoCalc.setEffect(null);
        });

        ////////////////////////////////////////////////////
        // SET OTHER VIEWS BORDERPANES
        valikkoCalc.getChildren().addAll(napitjamuut.getSceneCalc(),executeNappiCalc,goBackNappiCalc);
        isovalikkoCalc.getChildren().addAll(valikkoCalc,textAreaCalc);
        asetteluCalc.setCenter(isovalikkoCalc);

        valikkoNoCalc.getChildren().addAll(napitjamuut.getSceneNoCalc(),executeNappiNoCalc,goBackNappiNoCalc);
        isovalikkoNoCalc.getChildren().addAll(valikkoNoCalc,textAreaNoCalc);
        asetteluNoCalc.setCenter(isovalikkoNoCalc);

        ////////////////////////////////////////////////////
        // SET SCENES
        Scene firstScene = new Scene(asettelu,stageWidth,stageHeight);
        firstScene.getStylesheets().add("/styles/Styles.css");

        Scene calcScene = new Scene(asetteluCalc,stageWidth,stageHeight);
        calcScene.getStylesheets().add("/styles/Styles.css");
        nappiScene1.setOnAction(event -> {
            stage.setTitle("Random Walk - R_rms calculation");
            stage.setScene(calcScene);
        });
        goBackNappiCalc.setOnAction(event -> {
            stage.setTitle("Random Walk");
            stage.setScene(firstScene);
        });

        Scene noCalcScene = new Scene(asetteluNoCalc,stageWidth,stageHeight);
        noCalcScene.getStylesheets().add("/styles/Styles.css");
        nappiScene2.setOnAction(event -> {
            stage.setTitle("Random Walk - something else");
            stage.setScene(noCalcScene);
        });
        goBackNappiNoCalc.setOnAction(event -> {
            stage.setTitle("Random Walk");
            stage.setScene(firstScene);
        });

        ////////////////////////////////////////////////////
        // EXECUTE CALC
        executeNappiCalc.setOnMouseClicked((MouseEvent event) -> {
            if(executeNappiCalc.getText().equals("EXECUTE")){
                // BUTTON PRESSED ON
                executeNappiCalc.setText("EXECUTING...");
                executeNappiCalc.setBackground(
                    new Background(
                        new BackgroundFill(
                            Color.RED,CornerRadii.EMPTY,Insets.EMPTY)));
                executeNappiCalc.setTextFill(Color.ANTIQUEWHITE);

                this.vars = napitjamuut.getVars();
                Data data = new Data(this.vars);

                /////////////////////////
                // CREATEDATA          //
                /////////////////////////
                textAreaCalc.setText(data.createData(folder));

                if (this.vars[0].equals("0")) {
                    // GET DATA FROM READDATA()
                    Pair<String,List<Pair<Double,Double>>> dataPair = Data.readData(dataFile);

                    String header = dataPair.getKey();
                    List<Pair<Double,Double>> datapari = dataPair.getValue();
                    int runs = datapari.size();

                    // FORMAT DATA TO BE COMPATIBLE WITH XCHART
                    double[] xDataToChart = new double[datapari.size()];
                    for (int i=0;i<runs;i++)
                        xDataToChart[i] = datapari.get(i).getValue();
                    double[] yDataToChart = new double[datapari.size()];
                    for (int i=0;i<runs;i++)
                        yDataToChart[i] = datapari.get(i).getKey();

                    // CREATE CHART
                    XYChart chart = new XYChartBuilder()
                        .width(chartWidth).height(chartHeight)
                        .title("R_rms vs sqrt(N), "+runs+" runs")
                        .xAxisTitle("sqrt(N)").yAxisTitle("R_rms")
                        .theme(ChartTheme.Matlab).build();;
                    chart.addSeries(header,xDataToChart,yDataToChart);
                    chart.getStyler().setLegendVisible(false);
                    chart.getStyler()
                        .setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
                    chart.getStyler().setXAxisDecimalPattern("0.0");
                    chart.getStyler().setYAxisDecimalPattern("0.0");
                    chart.getStyler().setMarkerSize(0);

                    // SHOW CHART
                    new SwingWrapper(chart).displayChart()
                        .setBounds(screenWidth/2-chartWidth,
                            (screenHeight-stageHeight)/2,
                            chartWidth, chartHeight);
                 }

                executeNappiCalc.setText("EXECUTE");
                executeNappiCalc.setBackground(
                    new Background(
                        new BackgroundFill(
                    Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
                executeNappiCalc.setTextFill(Color.BLACK);
            }
        });

        ////////////////////////////////////////////////////
        // EXECUTE NO CALC
        executeNappiNoCalc.setOnMouseClicked((MouseEvent event) -> {
            if(executeNappiNoCalc.getText().equals("EXECUTE")){
                // BUTTON PRESSED ON
                executeNappiNoCalc.setText("EXECUTING...");
                executeNappiNoCalc.setBackground(
                    new Background(
                        new BackgroundFill(
                            Color.RED,CornerRadii.EMPTY,Insets.EMPTY)));
                executeNappiNoCalc.setTextFill(Color.ANTIQUEWHITE);

                this.vars = napitjamuut.getVars();
                Data data = new Data(this.vars);

                /////////////////////////
                // CREATEDATA          //
                /////////////////////////
                textAreaNoCalc.setText(data.createData(folder));
// TODO
/*                if (this.vars[0].equals("0")) {
                    // GET DATA FROM READDATA()
                    Pair<String,List<Pair<Double,Double>>> dataPair = Data.readData(dataFile);

                    String header = dataPair.getKey();
                    List<Pair<Double,Double>> datapari = dataPair.getValue();
                    int runs = datapari.size();

                    // FORMAT DATA TO BE COMPATIBLE WITH XCHART
                    double[] xDataToChart = new double[datapari.size()];
                    for (int i=0;i<runs;i++)
                        xDataToChart[i] = datapari.get(i).getValue();
                    double[] yDataToChart = new double[datapari.size()];
                    for (int i=0;i<runs;i++)
                        yDataToChart[i] = datapari.get(i).getKey();

                    // CREATE CHART
                    XYChart chart = new XYChartBuilder()
                        .width(chartWidth).height(chartHeight)
                        .title("R_rms vs sqrt(N), "+runs+" runs")
                        .xAxisTitle("sqrt(N)").yAxisTitle("R_rms")
                        .theme(ChartTheme.Matlab).build();;
                    chart.addSeries(header,xDataToChart,yDataToChart);
                    chart.getStyler().setLegendVisible(false);
                    chart.getStyler()
                        .setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
                    chart.getStyler().setXAxisDecimalPattern("0.0");
                    chart.getStyler().setYAxisDecimalPattern("0.0");
                    chart.getStyler().setMarkerSize(0);

                    // SHOW CHART
                    new SwingWrapper(chart).displayChart()
                        .setBounds(screenWidth/2-chartWidth,
                            (screenHeight-stageHeight)/2,
                            chartWidth, chartHeight);
                 }*/
//
                executeNappiNoCalc.setText("EXECUTE");
                executeNappiNoCalc.setBackground(
                    new Background(
                        new BackgroundFill(
                    Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
                executeNappiNoCalc.setTextFill(Color.BLACK);
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

        File sourceFile = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\walk.exe");
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

    public static void main(String[] args) {

        launch(args);

    }
}
