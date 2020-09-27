import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Rectangle extends Corps{

    public Rectangle(double posX, double posY, double largeur, double hauteur){
        super(posX, posY);
        this.setLargeur(largeur);
        this.setHauteur(hauteur);
    }

    @Override
    public void draw(GraphicsContext context) {
        context.setFill(Color.BLACK);
        context.fillRect(getPosX(), getPosY(), getLargeur(), getHauteur());
    }
}
