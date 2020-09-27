import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Plateforme extends Rectangle {
    private final String type;
    private Color couleur;
    private final Color couleurOriginale;
    private boolean collision = false;
    private boolean couleurChange = false;
    /**
     * Constructeur pour les plateformes.
     * @param posX  Position horizontale où est générée la plateforme
     * @param posY  Position verticale où est générée la plateforme
     * @param largeur Largeur de la plateforme
     * @param type  Type de la plateforme : "simple", "rebondissante", "accelerante" ou "solide".
     */
    public Plateforme(double posX, double posY, double largeur, String type, double vitesse){
        super(posX, posY, largeur,10);
        this.setVitY(vitesse);
        this.type = type;

        switch (type) {
            case "rebondissante":
                this.couleur = Color.LIGHTGREEN;
                break;

            case "accelerante":
                this.couleur = Color.rgb(230,221,58);
                break;

            case "solide":
                this.couleur = Color.rgb(184,15,36);
                break;

            case "simple":
            default:
                this.couleur = Color.rgb(230,134,58);
                break;
        }
        this.couleurOriginale = this.couleur;
    }

    @Override
    public void draw(GraphicsContext context) {
        context.setFill(couleur);
        context.fillRect(getPosX(), getPosY(), getLargeur(), getHauteur());
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        // Gère le changement de couleur en mode débug
        if(this.isModeDebug() && this.collision && !this.couleurChange){
            this.couleur = Color.YELLOW;
            this.couleurChange = true;

        } else if(!this.isModeDebug() && this.couleurChange || !this.collision && this.couleurChange){
            this.couleur = couleurOriginale;
            couleurChange = false;
        }
    }

    public String getType() {
        return type;
    }

    public boolean isCollision() {
        return collision;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }
}
