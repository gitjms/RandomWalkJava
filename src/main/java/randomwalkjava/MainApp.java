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
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
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

    final int stageWidth = 800;
    final int stageHeight = 510;
    final int chartWidth = 810;
    final int chartHeight = 515;
    final int buttonWidth = 150;
    final int screenWidth = Screen.getMainScreen().getWidth();
    final int screenHeight = Screen.getMainScreen().getHeight();
    final String path = "C:\\DATA";
    public String[] vars;

    @Override
    public void start(Stage stage) throws Exception {
        File findFile = new File(path);
        if (Files.notExists(findFile.toPath()))
            createFolder(path);

        stage.setTitle("Random Walk");
        stage.setMinWidth(stageWidth);
        stage.setMaxWidth(stageWidth);
        stage.setMinHeight(stageHeight);
        stage.setMaxHeight(stageHeight);
        stage.setResizable(false);
        stage.setX(screenWidth/2);
        stage.setY((screenHeight-stageHeight)/2);

        NappiInput napitjamuut = new NappiInput();

        BorderPane asettelu = new BorderPane();

        HBox isovalikko = new HBox();
        isovalikko.setPadding(new Insets(0, 0, 0, 0));
        isovalikko.setSpacing(0);

        VBox valikko = new VBox();
        valikko.setPadding(new Insets(10, 10, 10, 10));
        valikko.setSpacing(20);

        // BUTTON: EXECUTE
        Button executeNappi = new Button("EXECUTE");
        executeNappi.setMinWidth(this.buttonWidth);
        executeNappi.setMaxWidth(this.buttonWidth);
        
        valikko.getChildren().addAll(napitjamuut.getNappiInput(),executeNappi);
        isovalikko.getChildren().add(valikko);
        asettelu.setCenter(valikko);

        executeNappi.setOnMouseClicked((MouseEvent event) -> {
            if(executeNappi.getText().equals("EXECUTE")){
                // BUTTON PRESSED ON
                executeNappi.setText("EXECUTING...");
                executeNappi.setBackground(new Background(new BackgroundFill(Color.RED,CornerRadii.EMPTY,Insets.EMPTY)));
                executeNappi.setTextFill(Color.ANTIQUEWHITE);

                this.vars = napitjamuut.getVars();
                Data data = new Data(this.vars);

                //TextArea newTextArea = new TextArea();
                /////////////////////////
                // CREATEDATA          //
                /////////////////////////        
                isovalikko.getChildren().clear();
                isovalikko.getChildren().addAll(valikko,data.createData(path));
                asettelu.setCenter(isovalikko);
                
                // GET DATA FROM READDATA()
                Pair<String,List<Pair<Double,Double>>> dataPair = Data.readData(path);

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
                XYChart chart = new XYChartBuilder().width(chartWidth).height(chartHeight)
                    .title("R_rms vs sqrt(N), "+runs+" runs")
                    .xAxisTitle("sqrt(N)").yAxisTitle("R_rms")
                    .theme(ChartTheme.Matlab).build();;

                chart.addSeries(header,xDataToChart,yDataToChart);
                chart.getStyler().setLegendVisible(false);
                chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
                chart.getStyler().setXAxisDecimalPattern("0.0");
                chart.getStyler().setYAxisDecimalPattern("0.0");
                chart.getStyler().setMarkerSize(0);

                // SHOW CHART
                new SwingWrapper(chart).displayChart()
                    .setBounds(screenWidth/2-chartWidth, (screenHeight-stageHeight)/2, chartWidth, chartHeight);
                
                executeNappi.setText("EXECUTE");
                executeNappi.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
                executeNappi.setTextFill(Color.BLACK);
            }
        });

        Scene scene = new Scene(asettelu,stageWidth,stageHeight);
        scene.getStylesheets().add("/styles/Styles.css");
        stage.setScene(scene);
        
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

    public void createFolder(String path){
        File dataFile = new File(path);
        boolean result = false;
        try {
            System.out.println("creating directory: " + path);
            dataFile.mkdir();
            result = true;
        } catch (SecurityException se) {
            System.out.println("Could not create a new directory\n"+se.getMessage());
        }

        File sourceFile = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\walk.exe");
        
        if (sourceFile == null) {
            throw new IllegalArgumentException("Resource file is not found");
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
            System.out.println("Resource file is not copied to new folder\n"+e.getMessage());
        } finally {
            try {
                fin.close();
                fout.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {

        launch(args);

    }
}
