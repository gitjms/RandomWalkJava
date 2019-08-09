
package randomwalkjava;

import com.sun.glass.ui.Screen;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.jetbrains.annotations.Contract;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for Path Tracing
 */
@SuppressWarnings("SameReturnValue")
class ScenePathTracing extends Data {

    private final Button nappiFixed;
    private final Button nappiLattice;

    /**
     * main class gets vars via this
     * @return clone of vars array
     */
    public String[] getVars() {
        return this.vars.clone();
    }
 
    /**
     * initiating scene buttons and user variable array
     */
    ScenePathTracing() {
        super();
        this.nappiFixed = new Button("FIXED");
        this.nappiLattice = new Button("FREE");
        this.vars = new String[]{
            "0",    // vars[0] particles        USER
            "0.0",  // vars[1] diameter         USER
            "0",    // vars[2] charge           USER
            "0",    // vars[3] steps            USER
            "0",    // vars[4] dimension        USER
            "-",    // vars[5] mmc              n/a
            "f",    // vars[6] fixed(/spread)   USER
            "-",    // vars[7] (lattice/)free   USER
            "s"};   // vars[8] save (on)        n/a
    }

    /**
     * method for checking if user input in GUI is a double
     * @param str GUI input string
     * @return true if input is a double, false otherwise
     */
    private static boolean isNumDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    /**
     * method for checking if user input in GUI is an integer
     * @param str GUI input string
     * @return true if input is an integer, false otherwise
     */
    private static boolean isNumInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    /**
     * Create GUI for Path tracing
     * @return PATH TRACING SCENE
     */
    Parent getScenePath(){
        GridPane asettelu = new GridPane();
        asettelu.setMaxWidth(getPaneWidth());
        asettelu.setVgap(5);
        asettelu.setHgap(10);
        asettelu.setPadding(new Insets(0, 0, 0, 0));
        VBox valikko = new VBox();
        valikko.setPadding(new Insets(20, 10, 0, 0));
        valikko.setSpacing(10);
        
        DropShadow shadow = new DropShadow();

        /*
        * COMPONENTS...
        */
        Label labNumParticles = new Label("number of particles:");
        TextField setNumParticles = new TextField("");
        setNumParticles.setOnKeyReleased(e -> {
            if (isNumInteger(setNumParticles.getText().trim())){
                if (setNumParticles.getText().trim().equals("0")){
                    setNumParticles.setText("1");
                    this.vars[0] = "1";
                } else {
                    this.vars[0] = setNumParticles.getText().trim();
                }
            } else
                this.vars[0] = "0";
        });

        Label labSizeParticles = new Label("diameter of particle:");
        TextField setSizeParticles = new TextField("");
        setSizeParticles.setOnKeyReleased(e -> {
            if (isNumDouble(setSizeParticles.getText().trim()))
                this.vars[1] = setSizeParticles.getText().trim();
            else
                this.vars[1] = "0.0";
        });

        Label labCharge = new Label("charge of particles:");
        ToggleButton setCharge0 = new ToggleButton("0");
        setCharge0.setMinWidth(35);
        setCharge0.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        setCharge0.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        setCharge0.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setCharge0.setEffect(shadow));
        setCharge0.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setCharge0.setEffect(null));
        ToggleButton setCharge1 = new ToggleButton("1");
        setCharge1.setMinWidth(35);
        setCharge1.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        setCharge1.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        setCharge1.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setCharge1.setEffect(shadow));
        setCharge1.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setCharge1.setEffect(null));
        ToggleButton setCharge2 = new ToggleButton("2");
        setCharge2.setMinWidth(35);
        setCharge2.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        setCharge2.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        setCharge2.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setCharge2.setEffect(shadow));
        setCharge2.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setCharge2.setEffect(null));
        HBox setCharge = new HBox(setCharge0,setCharge1,setCharge2);
        setCharge.setSpacing(20);
        setCharge0.setOnMouseClicked(f -> {
            setCharge0.setBackground(new Background(new BackgroundFill(
                Color.CYAN,CornerRadii.EMPTY,Insets.EMPTY)));
            setCharge1.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            setCharge2.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[2] = "0";
        });
        setCharge1.setOnMouseClicked(f -> {
            setCharge0.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            setCharge1.setBackground(new Background(new BackgroundFill(
                Color.CYAN,CornerRadii.EMPTY,Insets.EMPTY)));
            setCharge2.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[2] = "1";
        });
        setCharge2.setOnMouseClicked(f -> {
            setCharge0.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            setCharge1.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            setCharge2.setBackground(new Background(new BackgroundFill(
                Color.CYAN,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[2] = "2";
        });

        Label labNumSteps = new Label("number of steps:");
        TextField setNumSteps = new TextField("");
        setNumSteps.setOnKeyReleased(e -> {
            if (isNumInteger(setNumSteps.getText().trim())){
                this.vars[3] = setNumSteps.getText().trim();
            } else
                this.vars[3] = "0";
        });

        Label labNumDimensions = new Label("dimension:");
        ToggleButton setDim1 = new ToggleButton("1");
        setDim1.setMinWidth(35);
        setDim1.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        setDim1.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        setDim1.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDim1.setEffect(shadow));
        setDim1.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDim1.setEffect(null));
        ToggleButton setDim2 = new ToggleButton("2");
        setDim2.setMinWidth(35);
        setDim2.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        setDim2.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        setDim2.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDim2.setEffect(shadow));
        setDim2.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDim2.setEffect(null));
        ToggleButton setDim3 = new ToggleButton("3");
        setDim3.setMinWidth(35);
        setDim3.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        setDim3.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        setDim3.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDim3.setEffect(shadow));
        setDim3.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDim3.setEffect(null));
        HBox setDimension = new HBox(setDim1,setDim2,setDim3);
        setDimension.setSpacing(20);
        setDim1.setOnMouseClicked(f -> {
            setDim1.setBackground(new Background(new BackgroundFill(
                Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            setDim2.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            setDim3.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "1";
        });
        setDim2.setOnMouseClicked(f -> {
            setDim1.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            setDim2.setBackground(new Background(new BackgroundFill(
                Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            setDim3.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "2";
        });
        setDim3.setOnMouseClicked(f -> {
            setDim1.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            setDim2.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            setDim3.setBackground(new Background(new BackgroundFill(
                Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "3";
        });

        this.vars[5] = "-"; // mmc n/a

        /*
        * ...THEIR PLACEMENTS
        */
        GridPane.setHalignment(labNumParticles, HPos.LEFT);
        asettelu.add(labNumParticles, 0, 0);
        GridPane.setHalignment(setNumParticles, HPos.CENTER);
        setNumParticles.setMinWidth(getCompwidth());
        setNumParticles.setMaxWidth(getCompwidth());
        asettelu.add(setNumParticles, 0, 1);
        
        GridPane.setHalignment(labSizeParticles, HPos.LEFT);
        asettelu.add(labSizeParticles, 0, 2);
        GridPane.setHalignment(setSizeParticles, HPos.CENTER);
        setSizeParticles.setMinWidth(getCompwidth());
        setSizeParticles.setMaxWidth(getCompwidth());
        asettelu.add(setSizeParticles, 0, 3);

        GridPane.setHalignment(labCharge, HPos.LEFT);
        asettelu.add(labCharge, 0, 4);
        GridPane.setHalignment(setCharge, HPos.CENTER);
        setCharge.setMinWidth(getCompwidth());
        setCharge.setMaxWidth(getCompwidth());
        asettelu.add(setCharge, 0, 5);

        GridPane.setHalignment(labNumSteps, HPos.LEFT);
        asettelu.add(labNumSteps, 0, 6);
        GridPane.setHalignment(setNumSteps, HPos.CENTER);
        setNumSteps.setMinWidth(getCompwidth());
        setNumSteps.setMaxWidth(getCompwidth());
        asettelu.add(setNumSteps, 0, 7);
        
        GridPane.setHalignment(labNumDimensions, HPos.LEFT);
        asettelu.add(labNumDimensions, 0, 8);
        GridPane.setHalignment(setDimension, HPos.CENTER);
        setDimension.setMinWidth(getCompwidth());
        setDimension.setMaxWidth(getCompwidth());
        asettelu.add(setDimension, 0, 9);

        /*
        * BUTTON: FIXED
        */
        this.getNappiFixed().setMinWidth(getCompwidth());
        this.getNappiFixed().setMaxWidth(getCompwidth());
        this.getNappiFixed().setBackground(new Background(
            new BackgroundFill(
                Color.GOLD,CornerRadii.EMPTY,Insets.EMPTY)));
        this.getNappiFixed().setId("fixed");
        this.getNappiFixed().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.getNappiFixed().setEffect(shadow));
        this.getNappiFixed().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.getNappiFixed().setEffect(null));
        this.getNappiFixed().setOnMouseClicked((MouseEvent event) -> {
            if (this.getNappiFixed().getText().equals("SPREAD")){
                this.getNappiFixed().setText("FIXED");
                this.getNappiFixed().setBackground(
                    new Background(
                        new BackgroundFill(
                            Color.GOLD,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[6] = "f";
            } else if (this.getNappiFixed().getText().equals("FIXED")){
                this.getNappiFixed().setText("SPREAD");
                this.getNappiFixed().setBackground(
                    new Background(new BackgroundFill(
                        Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[6] = "-";
            }
        });
        valikko.getChildren().add(this.getNappiFixed());

        /*
        * BUTTON: LATTICE
        */
        this.getNappiLattice().setMinWidth(getCompwidth());
        this.getNappiLattice().setMaxWidth(getCompwidth());
        this.getNappiLattice().setBackground(new Background(
            new BackgroundFill(
                Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
        this.getNappiLattice().setId("lattice");
        this.getNappiLattice().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.getNappiLattice().setEffect(shadow));
        this.getNappiLattice().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.getNappiLattice().setEffect(null));
        this.getNappiLattice().setOnMouseClicked((MouseEvent event) -> {
            if (this.getNappiLattice().getText().equals("LATTICE")){
                this.getNappiLattice().setText("FREE");
                this.getNappiLattice().setBackground(
                    new Background(
                        new BackgroundFill(
                            Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[7] = "-";
            } else if (this.getNappiLattice().getText().equals("FREE")){
                this.getNappiLattice().setText("LATTICE");
                this.getNappiLattice().setBackground(
                    new Background(new BackgroundFill(
                        Color.GOLD,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[7] = "l";
            }
        });
        valikko.getChildren().add(this.getNappiLattice());

        this.vars[8] = "s"; // save on

        GridPane.setHalignment(valikko, HPos.LEFT);
        asettelu.add(valikko, 0, 10, 2, 1);

        final Pane empty = new Pane();
        GridPane.setHalignment(empty, HPos.CENTER);
        asettelu.add(empty, 0, 11, 2, 1);

       return asettelu;
    }

    /**
     * @return the compwidth
     */
    @Contract(pure = true)
    private int getCompwidth() { return 150; }

    /**
     * @return the paneWidth
     */
    @Contract(pure = true)
    private int getPaneWidth() { return 200; }

    /**
     * @return the nappiFixed
     */
    @Contract(pure = true)
    private Button getNappiFixed() {
        return nappiFixed;
    }

    /**
     * @return the nappiLattice
     */
    @Contract(pure = true)
    private Button getNappiLattice() {
        return nappiLattice;
    }
}
