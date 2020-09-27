import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bulles extends Corps{
    private final double diametre;

    /**
     * Constructeur pour les bulles.
     * @param posX Position horizontale où est générée la bulle.
     * @param posY Position verticale où est générée la bulle.
     * @param diametre Diamètre de la bulle, en pixel.
     */
    public Bulles(double posX, double posY, double diametre){
        super(posX, posY);
        this.diametre = diametre;
        this.setHauteur(diametre);
        this.setLargeur(diametre);
        this.setVitY(-(200 + Math.random()*100));
    }

    @Override
    public void draw(GraphicsContext context) {
        context.setFill(Color.rgb(0,0,255,0.4));
        context.fillOval(this.getPosX(), this.getPosY(), this.diametre, this.diametre);
    }
}
