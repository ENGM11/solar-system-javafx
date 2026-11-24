import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;
import javafx.util.Duration;
public class SolarSysteam extends Application {
    private final String[] planetNames = {
         "Mercury", "Venus", "Earth", "Mars", "Jupiter",
         "Saturn", "Uranus", "Neptune", "Pluto"
    };
    private final String[] planetExt = {
         "png", "jpg", "jpg", "jpg", "jpg", "jpg", "jpg", "jpg", "jpg"
    };
    private final Color[] planetColors = {
         Color.DARKGRAY, Color.GOLDENROD, Color.DODGERBLUE, Color.ORANGERED,
    Color.PERU,               Color.KHAKI,           Color.LIGHTBLUE,    Color.ROYALBLUE,
Color.ROSYBROWN
    };
    private final double[] planetSizes = {6, 9, 10, 8, 20, 17, 13, 13, 5};
    private final Circle[] planets = new Circle[planetNames.length];
    private final PathTransition[] transitions = new PathTransition[planetNames.length];
    private final boolean[] added = new boolean[planetNames.length];
    private Circle moon;
    private PathTransition moonTransition;
    private MediaPlayer soundPlayer;
    private final Pane spacePane = new Pane();
    private final double centerX = 400;
    private final double centerY = 220;
    @Override
    public void start(Stage myStage) {
      BorderPane root = new BorderPane();
      spacePane.setStyle("-fx-background-color: #1a1a2e;");
      spacePane.setPrefSize(800, 440);
      Circle sun = new Circle(centerX, centerY, 45, Color.KHAKI);
      spacePane.getChildren().add(sun);
    for (int i = 0; i < planetNames.length; i++) {
        Ellipse orbit = new Ellipse(centerX, centerY, 70 + i * 33, 40 + i * 18);
        orbit.setFill(Color.TRANSPARENT);
        orbit.setStroke(Color.LIGHTGRAY);
        orbit.setStrokeWidth(0.8);
        spacePane.getChildren().add(orbit);
    }
    root.setCenter(spacePane);
    ListView<String> list = new ListView<>();
    list.getItems().addAll(planetNames);
    list.setPrefWidth(140);
    root.setLeft(list);
    GridPane grid = new GridPane();
    grid.setHgap(8);
    grid.setVgap(8);
    grid.setAlignment(Pos.CENTER);
    grid.setStyle("-fx-background-color: #16213e; -fx-padding: 8;");
    for (int i = 0; i < planetNames.length; i++) {
        final int index = i;
        Button planetBtn = new Button(planetNames[i]);
        ImageView icon = new ImageView(new Image(
             getClass().getResource(planetNames[i].toLowerCase()               +          "."           +
planetExt[i]).toExternalForm()));
        icon.setFitWidth(45);
        icon.setFitHeight(45);
        planetBtn.setGraphic(icon);
        planetBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        planetBtn.setOnAction(e -> addPlanetGrey(index));
        grid.add(planetBtn, i % 5, i / 5);
    }
    root.setBottom(grid);
    list.setOnMouseClicked(e -> {
        String name = list.getSelectionModel().getSelectedItem();
        if (name == null) return;
        int index = indexOf(name);
        if (!added[index]) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Planet Error");
          alert.setHeaderText("The planet is not yet added!");
          alert.setContentText(name + " is still missing on its orbit. Add it from the grid first.");
          ImageView alertIcon = new ImageView(new Image(
              getClass().getResource(name.toLowerCase()                   +           "."               +
planetExt[index]).toExternalForm()));
           alertIcon.setFitWidth(60);
           alertIcon.setFitHeight(60);
           alert.setGraphic(alertIcon);
           alert.showAndWait();
       } else {
           planets[index].setFill(planetColors[index]);
           playSound(name);
       }
     });
     MenuItem stopAll = new MenuItem("Stop all animations");
     stopAll.setOnAction(e -> {
       for (PathTransition t : transitions)
           if (t != null) t.pause();
       if (moonTransition != null) moonTransition.pause();
     });
     MenuItem startAll = new MenuItem("Start all animations");
     startAll.setOnAction(e -> {
       for (PathTransition t : transitions)
           if (t != null) t.play();
       if (moonTransition != null) moonTransition.play();
     });
     Menu controlMenu = new Menu("Control Animation");
     controlMenu.getItems().addAll(stopAll, startAll);
         ContextMenu contextMenu = new ContextMenu();
         contextMenu.getItems().add(controlMenu);
         spacePane.setOnContextMenuRequested(e ->
              contextMenu.show(spacePane, e.getScreenX(), e.getScreenY()));
         Button detailsBtn = new Button("Show Planets Details");
         detailsBtn.setOnAction(e -> showDetailsDialog());
         HBox topBar = new HBox(detailsBtn);
         topBar.setAlignment(Pos.CENTER_RIGHT);
         topBar.setStyle("-fx-padding: 5; -fx-background-color: #0f3460;");
         root.setTop(topBar);
         Scene scene = new Scene(root, 980, 640);
         myStage.setScene(scene);
         myStage.setTitle("Motaz's Solar System");
         myStage.show();
     }
     private void addPlanetGrey(int index) {
         if (added[index]) return;
         Circle planet = new Circle(planetSizes[index], Color.GREY);
         planets[index] = planet;
         spacePane.getChildren().add(planet);
         added[index] = true;
         Ellipse path = new Ellipse(centerX, centerY, 70 + index * 33, 40 + index * 18);
         PathTransition pt = new PathTransition();
         pt.setNode(planet);
         pt.setPath(path);
         pt.setDuration(Duration.seconds(6 + index * 3));
         pt.setCycleCount(Animation.INDEFINITE);
         pt.setRate(-1);
         pt.play();
         transitions[index] = pt;
         if (planetNames[index].equals("Earth")) {
             moon = new Circle(3, Color.LIGHTGRAY);
             spacePane.getChildren().add(moon);
             Ellipse moonPath = new Ellipse(0, 0, 18, 10);
             moonTransition = new PathTransition();
             moonTransition.setNode(moon);
             moonTransition.setPath(moonPath);
             moonTransition.setDuration(Duration.seconds(2));
             moonTransition.setCycleCount(Animation.INDEFINITE);
             moonTransition.setRate(-1);
             moon.translateXProperty().bind(planet.translateXProperty());
             moon.translateYProperty().bind(planet.translateYProperty());
             moonTransition.play();
         }
     }
     private void playSound(String name) {
         if (soundPlayer != null) {
             soundPlayer.stop();
             soundPlayer.dispose();
         }
    Media      sound    =             new   Media(getClass().getResource(name.toLowerCase()   +
".mp3").toExternalForm());
         soundPlayer = new MediaPlayer(sound);
         soundPlayer.play();
     }
     private void showDetailsDialog() {
         TableView<Planet> table = new TableView<>();
         TableColumn<Planet, String> nameCol = new TableColumn<>("Name");
         nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
         TableColumn<Planet, String> massCol = new TableColumn<>("Mass");
         massCol.setCellValueFactory(new PropertyValueFactory<>("mass"));
         TableColumn<Planet, String> distCol = new TableColumn<>("Distance from Sun");
         distCol.setCellValueFactory(new PropertyValueFactory<>("distance"));
         TableColumn<Planet, String> speedCol = new TableColumn<>("Orbital Speed");
         speedCol.setCellValueFactory(new PropertyValueFactory<>("speed"));
         table.getColumns().addAll(nameCol, massCol, distCol, speedCol);
         table.getItems().addAll(
              new Planet("Mercury", "3.30 x 10^23 kg", "57.9 M km", "47.4 km/s"),
              new Planet("Venus", "4.87 x 10^24 kg", "108.2 M km", "35.0 km/s"),
              new Planet("Earth", "5.97 x 10^24 kg", "149.6 M km", "29.8 km/s"),
              new Planet("Mars",    "6.42 x 10^23 kg", "227.9 M km", "24.1 km/s"),
              new Planet("Jupiter", "1.90 x 10^27 kg", "778.6 M km", "13.1 km/s"),
              new Planet("Saturn", "5.68 x 10^26 kg", "1433.5 M km", "9.7 km/s"),
              new Planet("Uranus", "8.68 x 10^25 kg", "2872.5 M km", "6.8 km/s"),
              new Planet("Neptune", "1.02 x 10^26 kg", "4495.1 M km", "5.4 km/s"),
              new Planet("Pluto", "1.31 x 10^22 kg", "5906.4 M km", "4.7 km/s")
         );
         Alert dialog = new Alert(Alert.AlertType.INFORMATION);
         dialog.setTitle("Planets Details");
         dialog.setHeaderText("Solar System Data");
         dialog.getDialogPane().setContent(table);
         dialog.getDialogPane().setPrefSize(560, 420);
         dialog.showAndWait();
     }
     private int indexOf(String name) {
         for (int i = 0; i < planetNames.length; i++)
              if (planetNames[i].equals(name)) return i;
         return -1;
     }
     public static void main(String[] args) {
       launch(args);
   }
}
