/*
Francis Lalonde (801363)		    TP 2 - High Sea Tower
Jean-Daniel Toupin (20046724)		14 avril 2020
*/
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

import static javafx.scene.media.MediaPlayer.INDEFINITE;

public class HighSeaTower extends Application {

    public static double mouseX, mouseY;

    public static final int WIDTH = 350, HEIGHT = 480;

    public static void main(String[] args) {
        launch(args);
    }

    public boolean restart = false;
    public boolean pasDeDoubleSaut = false;
    private String konami = "xxxxxxxxxx";

    private final Media sound = new Media(new File("src/main/resources/sons/gymnopedie.mp3").toURI().toString());
    private final MediaPlayer mediaPlayer = new MediaPlayer(sound);
    private final Media spaceSound = new Media(new File("src/main/resources/sons/gymnopespace.mp3").toURI().toString());
    private final MediaPlayer spacePlayer = new MediaPlayer(spaceSound);

    @Override
    public void start(Stage primaryStage) {
        mediaPlayer.setCycleCount(INDEFINITE);
        spacePlayer.setCycleCount(INDEFINITE);
        Pane root = new Pane();

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.getIcons().add(new Image("/images/icon.png"));
        primaryStage.setTitle("High Sea Tower");
        primaryStage.setResizable(false);

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext context = canvas.getGraphicsContext2D();
        Controleur controleur = new Controleur();

        canvas.setOnMouseMoved((event) -> {
            mouseX = event.getX();
            mouseY = event.getY();
        });

        canvas.setOnMouseClicked((event)-> controleur.choixDifficulte());

        Text godMode = creerText(Color.FIREBRICK, "Helvetica", 26, WIDTH/2 -60, 80);
        Text debugInfo = creerText(Color.WHITESMOKE, "Segoe UI", 14, 5, 15);
        Text score = creerText(Color.WHITE, "Helvetica", 20,WIDTH/2 - 40,40);
        Text astronaute = creerText(Color.STEELBLUE, "Helvetica",26, WIDTH/2 -100, -50 );
        Text lune = creerText(Color.WHITE, "Helvetica", 22, 50, HEIGHT+100);

        root.getChildren().addAll(canvas, godMode, astronaute, lune, debugInfo, score);

        scene.setOnKeyPressed((value) -> keyBinding(value, controleur));

        scene.setOnKeyReleased((value) -> {
            if(value.getCode() == KeyCode.RIGHT || value.getCode() == KeyCode.LEFT ||
                value.getCode() == KeyCode.D || value.getCode() == KeyCode.A){
                controleur.mouvement("xStop");

            } else if(value.getCode() == KeyCode.SPACE || value.getCode() == KeyCode.UP || value.getCode() == KeyCode.W){
                // empêche de tricher en maintenant la touche de saut enfoncée.
                pasDeDoubleSaut = false;
            } else if(controleur.isAstronaut()){
                if(value.getCode() == KeyCode.SPACE || value.getCode() == KeyCode.UP || value.getCode() == KeyCode.W
                || value.getCode() == KeyCode.DOWN || value.getCode() == KeyCode.S){
                    controleur.mouvement("yStop");
                }
            }
        });

        AnimationTimer timer = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }

                double deltaTime = (now - lastTime) * 1e-9;
                controleur.update(deltaTime);
                controleur.draw(context);

                if(restart){
                    context.clearRect(0, 0, WIDTH, HEIGHT);
                    score.setText("");
                    godMode.setText("");
                    godMode.setY(80);
                    debugInfo.setText("");
                    konami = "xxxxxxxxxx";
                    lune.setText("");
                    lune.setY(HEIGHT+100);
                    astronaute.setText("");
                    astronaute.setY(-50);
                    spacePlayer.setMute(true);
                    mediaPlayer.setMute(false);
                    restart = controleur.restart();

                }

                if(controleur.getMusicStart() || controleur.getJeuCommence() & !controleur.isAstronaut()){
                    mediaPlayer.play();
                    spacePlayer.play();
                    spacePlayer.setMute(true);
                }

                if(controleur.getJeuCommence()) {
                    score.setText(controleur.getScore() + " m");
                    if (konami.equals("UUDDLRLRBA")) {
                        controleur.toggleGodMode();
                        konami = "xxxxxxxxxx";
                    }
                    if (controleur.getGodMode()) {
                        godMode.setText("GODMODE");
                        if(controleur.isAstronaut() && godMode.getY() > -100){
                            godMode.setY(godMode.getY() - 100*deltaTime);
                        }
                    } else {
                        godMode.setText("");
                    }
                    if (controleur.getDebug()) {
                        debugInfo.setText(controleur.getDebugInfo());
                    } else {
                        debugInfo.setText("");
                    }
                }
                if(controleur.isAstronaut() && godMode.getY() < -50){
                    spacePlayer.setMute(false);
                    mediaPlayer.setMute(true);
                    if(astronaute.getY() < 80){
                        astronaute.setText("Mode Astronaute");
                        astronaute.setY(astronaute.getY() + 100*deltaTime);
                    } else if(lune.getY() > HEIGHT - 80){
                        lune.setText("         Mission: Lune\nDistance restante : " +(1000000 - controleur.getScore()));
                        lune.setY(lune.getY() - 100*deltaTime);
                    } else {
                        lune.setText("         Mission: Lune\nDistance restante : " +(1000000 - controleur.getScore()));
                    }
                }
                if(controleur.preGg()){
                    astronaute.setText("");
                    score.setText("");
                }
                if(controleur.isGg()){
                    lune.setFont(Font.font("Helvetica", FontWeight.BOLD, 16));
                    lune.setY((double)HEIGHT/2);
                    lune.setX(15);
                    lune.setText("Le programme Lunaire médusien\nfut un franc succès!\n" +
                            "Merci pour votre bon travail!\n" +
                            "Pesez sur ESC pour retourner au menu.");
                }

                lastTime = now;
            }
        };
        timer.start();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Génère un objet Text (JavaFX)
     * @param couleur Objet Color (JavaFX)
     * @param font  Nom de la police d'écriture désirée
     * @param taille Taille de la police d'écriture
     * @param posX Position horizontale du texte dans le parent
     * @param posY Position verticale du texte dans le parent
     * @return l'objet Text avec les propriétés spécifiées
     */
    Text creerText(Color couleur, String font, int taille, int posX, int posY){
        Text text = new Text();
        text.setFont(Font.font(font, FontWeight.BOLD, taille));
        text.setFill(couleur);
        text.setY(posY);
        text.setX(posX);

        return text;
    }

    /**
     * Regroupe toutes les assiagnations de touche.
     * @param value valeur du KeyEvent
     * @param controleur Controleur qui va recevoir les entrées
     */
    void keyBinding(KeyEvent value, Controleur controleur){
        switch (value.getCode()) {

            case ESCAPE:
                if(controleur.getJeuCommence()){
                    restart = true;
                } else{
                    Platform.exit();
                }
                break;

            case UP:
                konami = konami.substring(1, 10).concat("U");
            case SPACE:
            case W:
                if(!pasDeDoubleSaut){
                    controleur.mouvement("saut");
                    if(!controleur.isAstronaut()){
                        pasDeDoubleSaut = true;
                    }
                }
                break;

            case RIGHT:
                konami = konami.substring(1, 10).concat("R");
            case D:
                controleur.mouvement("droite");
                break;

            case A:
                konami = konami.substring(1, 10).concat("A");
                controleur.mouvement("gauche");
                break;

            case LEFT:
                konami = konami.substring(1, 10).concat("L");
                controleur.mouvement("gauche");
                break;

            case T:
                controleur.toggleDebug();
                controleur.mouvement("noBind");
                break;

            case DOWN:
                konami = konami.substring(1, 10).concat("D");
            case S:
                controleur.mouvement("bas");
                break;

            case B:
                konami = konami.substring(1, 10).concat("B");
                controleur.mouvement("noBind");
                break;

            default:
                konami = konami.substring(1, 10).concat("X");
                controleur.mouvement("noBind");
        }
    }

}
