package randomwalkjava;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

public class MainApp extends Application {

    final int width = 500;
    final int height = 540;
    public String amount;
    public String size;
    public String steps;
    public String dimensions;
    public String avoid ;
    public String xgraph;
    public String[] vars;

    @Override
    public void start(Stage stage) throws Exception {
        this.amount = "";
        this.size = "";
        this.steps = "";
        this.dimensions = "";
        this.avoid = "";
        this.xgraph = "";
        this.vars = null;

        stage.setTitle("Random Walk");
        stage.setMinWidth(250);
        stage.setMinHeight(height);
        stage.setMaxHeight(height);

        NappiInput napitjamuut = new NappiInput();

        BorderPane asettelu = new BorderPane();

        VBox valikko = new VBox();
        valikko.setPadding(new Insets(10, 10, 10, 10));
        valikko.setSpacing(10);

        // BUTTON: EXECUTE
        Button executeNappi = new Button("          Execute        ");
        executeNappi.setMinWidth(200);
        executeNappi.setMaxWidth(200);
        valikko.getChildren().addAll(napitjamuut.getNappiInput(),executeNappi);
        
        asettelu.setBottom(valikko);
        
        /*executeNappi.setOnMouseClicked((MouseEvent event) -> {
            if(executeNappi.getText().equals("         EXECUTE         ")){
                // BUTTON PRESED ON
                executeNappi.setText("       EXECUTING         ");
                executeNappi.setBackground(new Background(new BackgroundFill(Color.RED,CornerRadii.EMPTY,Insets.EMPTY)));
                executeNappi.setTextFill(Color.ANTIQUEWHITE);
                this.vars[0] = asettelu.getChildren().get(1).toString();
                this.vars[1] = asettelu.getChildren().get(3).toString();
                this.vars[2] = asettelu.getChildren().get(5).toString();
                this.vars[3] = asettelu.getChildren().get(7).toString();

                Data.createData(this.vars);
                // GET DATA FROM READDATA()
                Pair<String,List<Pair<Double,Double>>> dataPair = Data.readData();

                String header = dataPair.getKey();
                List<Pair<Double,Double>> data = dataPair.getValue();
                int runs = data.size();
                // FORMAT DATA TO BE COMPATIBLE WITH XCHART
                double[] xDataToChart = new double[data.size()];
                for (int i=0;i<runs;i++)
                    xDataToChart[i] = data.get(i).getValue();
                double[] yDataToChart = new double[data.size()];
                for (int i=0;i<runs;i++)
                    yDataToChart[i] = data.get(i).getKey();
                // CREATE CHART
                XYChart chart = QuickChart
                    .getChart("R_rms_vs_sqrt(N)","sqrt(N)",
                        "R_rms",
                        String.valueOf(runs),
                        xDataToChart, yDataToChart);
                chart.getStyler().setLegendVisible(false);
                chart.setTitle("R_rms vs sqrt(N), "+runs+" runs");
                SwingWrapper chartWrap = new SwingWrapper(chart);
                // SHOW CHART
                chartWrap.displayChart();
            } else if(executeNappi.getText().equals("       EXECUTING         ")){
                // BUTTON PRESED OFF
                executeNappi.setText("         EXECUTE         ");
                executeNappi.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
                executeNappi.setTextFill(Color.BLACK);
                this.vars = new String[]{"0","0.0","0","0","-","-"};
            }
        });*/
        
        Scene scene = new Scene(asettelu,width,height);

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
    
    public static void main(String[] args) {//throws InterruptedException {

        //createData(String[] vars = {"0", "0.1", "1000", "2", "-", "-"});
        launch(args);

    }

 

}
