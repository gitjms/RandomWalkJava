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

    // FIGURE
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
    final String path = "C:\\DATA";
    final String fexec = "walk.exe";
    final String pyexec = "python plot3d.py";
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
        Button nappiHelp = new Button("HELP");
        Button nappiNoHelp = new Button("HELP");
        Button nappiMenuHelp = new Button("HELP");
        nappiScene1.setMinWidth(buttonWidth);
        nappiScene1.setMaxWidth(buttonWidth);
        nappiScene2.setMinWidth(buttonWidth);
        nappiScene2.setMaxWidth(buttonWidth);
        nappiHelp.setMinWidth(buttonWidth);
        nappiHelp.setMaxWidth(buttonWidth);
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

        GridPane.setHalignment(nappiHelp, HPos.LEFT);
        asettelu.add(nappiMenuHelp, 0, 4, 2, 1);
        nappiMenuHelp.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        nappiMenuHelp.setVisible(true);

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
        nappiHelp.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                nappiHelp.setEffect(shadow);
        });
        nappiHelp.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                nappiHelp.setEffect(null);
        });
        nappiHelp.setOnAction(event -> {
            textAreaCalc.setText(helpTextCalc());
        });
        nappiHelp.setVisible(true);

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
            nappiHelp);
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
        nappiScene1.setOnAction(event -> {
            stage.setTitle("R_rms calculation");
            if (!textAreaMenu.getText().equals(helpTextMenu()))
                textAreaCalc.setText(textAreaMenu.getText());
            else
                textAreaCalc.setText("");
            stage.setScene(calcScene);
            
        });
        menuNappiCalc.setOnAction(event -> {
            stage.setTitle("Random Walk");
            if (!textAreaCalc.getText().equals(helpTextCalc()))
                textAreaMenu.setText(textAreaCalc.getText());
            else
                textAreaMenu.setText("");
            stage.setScene(firstScene);
        });

        Scene noCalcScene = new Scene(asetteluNoCalc,stageWidth,stageHeight);
        noCalcScene.getStylesheets().add("/styles/Styles.css");
        nappiScene2.setOnAction(event -> {
            stage.setTitle("Random Walk simulation");
            if (!textAreaMenu.getText().equals(helpTextMenu()))
                textAreaNoCalc.setText(textAreaMenu.getText());
            else
                textAreaNoCalc.setText("");
            stage.setScene(noCalcScene);
        });
        menuNappiNoCalc.setOnAction(event -> {
            stage.setTitle("Random Walk");
            if (!textAreaNoCalc.getText().equals(helpTextNoCalc()))
                textAreaMenu.setText(textAreaNoCalc.getText());
            else
                textAreaMenu.setText("");
            stage.setScene(firstScene);
        });

        XYChart calcChart = new XYChartBuilder()
            .width(chartWidth).height(chartHeight)
            .theme(ChartTheme.Matlab).build();

        XChartPanel chartPanel = new XChartPanel(calcChart);
        JFrame frame = new JFrame();
        ////////////////////////////////////////////////////
        // EXECUTE CALC
        executeNappiCalc.setOnMouseClicked((MouseEvent event) -> {
            // BUTTON PRESSED ON
            this.vars = getCalcScene.getVars();
            Data data = new Data(this.vars);
            //this.vars[0] = "0" amount
            //this.vars[1] = size, from user
            //this.vars[2] = steps, from user
            //this.vars[3] = skip, from user
            //this.vars[4] = dimension, from user
            //this.vars[5] = "-" avoid (n/a: only one particle at a time)
            //this.vars[6] = "s" save (n/a: save is default)
            //this.vars[7] = "-" xgraph (n/a: normal save is default)

            /////////////////////////
            // CREATEDATA CALC     //
            /////////////////////////
            textAreaCalc.setText(data.createData(folder, this.fexec, true));

            String calcData = "";
            if (this.vars[4].trim().equals("1") ){
                calcData = "rms_1D.xy";
            } else if (this.vars[4].trim().equals("2") ){
                calcData = "rms_2D.xy";
            } else if (this.vars[4].trim().equals("3") ){
                calcData = "rms_3D.xy";
            }
            File calcDataFile = new File(path + "\\" + calcData);

            // GET DATA FROM READDATA()
            Pair<String,List<Pair<Double,Double>>> dataPair
                = Data.readDataCalc(calcDataFile);
            String header = dataPair.getKey();
            List<Pair<Double,Double>> datapari = dataPair.getValue();
            int runs = datapari.size();

            // FORMAT DATA TO BE COMPATIBLE WITH CHART
            double[] xDataToChart = new double[datapari.size()];
            for (int i=0;i<runs;i++)
                xDataToChart[i] = datapari.get(i).getKey();
            double[] yDataToChart = new double[datapari.size()];
            for (int i=0;i<runs;i++)
                yDataToChart[i] = datapari.get(i).getValue();

            calcChart.removeSeries(header);
            chartPanel.removeAll();
            frame.getContentPane().removeAll();
            frame.setTitle("Random Walk - R_rms Calculation");
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setBounds(screenWidth/2-chartWidth,
                    (screenHeight-stageHeight)/2,
                    chartWidth, chartHeight);
            calcChart.setTitle("R_rms vs sqrt(N), "+this.vars[4] + "D, " + runs + " runs/steps");
            calcChart.setXAxisTitle("sqrt(N)");
            calcChart.setYAxisTitle("R_rms");
            calcChart.addSeries(header,xDataToChart,yDataToChart);
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
        });

        ////////////////////////////////////////////////////
        // EXECUTE NO CALC
        executeNappiNoCalc.setOnMouseClicked((MouseEvent event) -> {
            // BUTTON PRESSED ON
            this.vars = getNoCalcScene.getVars();
            Data data = new Data(this.vars);
            //this.vars[0] = amount, from user
            //this.vars[1] = size, from user
            //this.vars[2] = steps, from user
            //this.vars[3] = skip, from user
            //this.vars[4] = dimension, from user
            //this.vars[5] = avoid, from user
            //this.vars[6] = save or real time, from user
            //this.vars[7] = xgraph or normal save, from user

            String xDataPath = "";
            String yDataPath = "";
            String zDataPath = "";
            BufferedImage image = null;

            /////////////////////////
            // CREATEDATA NO CALC  //
            /////////////////////////
            if (this.vars[6].equals("s")) {
                textAreaNoCalc.setText(data.createData(folder, fexec, true));

                // GET DATA FROM READDATANOCALC...()
                String header = "";
                Pair<String,List<Double>> dataPairX;
                File xDataFile = new File(
                    path + "\\" + "x_path"
                    + this.vars[4] + "D_"
                    + this.vars[0] + ".xy");
                xDataPath = path + "\\" + "x_path"
                    + this.vars[4] + "D_"
                    + this.vars[0] + ".xy";
                dataPairX = Data.readDataNoCalcX(xDataFile);
                if (this.vars[4].equals("1") ){
                    header = dataPairX.getKey();
                } else if ( this.vars[4].equals("2") || this.vars[4].equals("3") ){
                    header = dataPairX.getKey().substring(2, 15);
                }
                List<Double> xdata = dataPairX.getValue();
                int runs = xdata.size();

                // FORMAT DATA TO BE COMPATIBLE WITH CHART
                double[] xDataToChart = new double[runs];
                double[] yDataToChart = new double[runs];
                double[] zDataToChart = new double[runs];
                for (int i=0;i<runs;i++)
                    xDataToChart[i] = xdata.get(i);
                    
                if (this.vars[4].equals("2") || this.vars[4].equals("3") ) {
                    Pair<String,List<Double>> dataPairY;
                    File yDataFile = new File(
                    path + "\\" + "y_path"
                    + this.vars[4] + "D_"
                    + this.vars[0] + ".xy");
                    yDataPath = path + "\\" + "y_path"
                    + this.vars[4] + "D_"
                    + this.vars[0] + ".xy";
                    dataPairY = Data.readDataNoCalcY(yDataFile);
                    if ( this.vars[4].equals("2") ){
                        header += dataPairY.getKey().substring(7, dataPairY.getKey().length());
                    } else if ( this.vars[4].equals("3") ){
                        header += dataPairY.getKey().substring(7, 15);
                    }
                    List<Double> ydata = dataPairY.getValue();
                    yDataToChart = new double[runs];
                    for (int i=0;i<runs;i++)
                        yDataToChart[i] = ydata.get(i);
                }

                if ( this.vars[4].equals("3") ) {
                    Pair<String,List<Double>> dataPairZ;
                    File zDataFile = new File(
                    path + "\\" + "z_path"
                    + this.vars[4] + "D_"
                    + this.vars[0] + ".xy");
                    zDataPath = path + "\\" + "z_path"
                    + this.vars[4] + "D_"
                    + this.vars[0] + ".xy";
                    dataPairZ = Data.readDataNoCalcZ(zDataFile);
                    header += dataPairZ.getKey().substring(15, dataPairZ.getKey().length());
                    List<Double> zdata = dataPairZ.getValue();
                    zDataToChart = new double[runs];
                    for (int i=0;i<runs;i++)
                        zDataToChart[i] = zdata.get(i);
                    
                    Py3dplot pyplot = new Py3dplot();
                    String[] files = new String[]{xDataPath, yDataPath, zDataPath};
                    textAreaNoCalc.setText(pyplot.createPlot(folder, files, pyexec));
                    String imgFile = "jpyplot_N" + this.vars[0] + ".png";
                    image = pyplot.readPyPlot(new File(path + "\\" + imgFile));
                }

                if ( this.vars[4].trim().equals("2")) {
                    calcChart.removeSeries(header);
                    chartPanel.removeAll();
                    frame.getContentPane().removeAll();
                    frame.setTitle("Random Walk - Path Tracing");
                    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    frame.setBounds(screenWidth/2-chartWidth,
                            (screenHeight-stageHeight)/2,
                            chartWidth, chartHeight);
                    calcChart.setTitle("Random Walk, N="+this.vars[0]+", "+runs+" runs");
                    calcChart.addSeries(header,xDataToChart,yDataToChart);
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
                } else if ( this.vars[4].trim().equals("3")) {
                    frame.getContentPane().removeAll();
                    frame.setTitle("Random Walk Path Tracing");
                    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    JLabel titleLabel = new JLabel("Random Walk, N="+this.vars[0]+", "+runs+" runs");
                    java.awt.Font labelFont = titleLabel.getFont();
                    int newFontSize = (int)(labelFont.getSize() * 2);
                    titleLabel.setFont(new java.awt.Font(labelFont.getName(), java.awt.Font.PLAIN, newFontSize));
                    titleLabel.setBounds(chartWidth/2-150,10,chartWidth/2+150,newFontSize);
                    ImageIcon figIcn = new ImageIcon(image);
                    JLabel figLabel = new JLabel(figIcn);
                    frame.add(titleLabel);
                    frame.add(figLabel);
                    frame.repaint();
                    frame.setBounds(20, (screenHeight-chartHeight)/2-60, chartWidth, chartHeight);
                    frame.pack();
                    frame.setVisible(true);
                }
            } else if (this.vars[6].trim().equals("-")){
                //textAreaNoCalc.setText(data.createData(folder, false));

                // GET REAL TIME DATA FROM SOMEWHERE AND PLOT IT
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
