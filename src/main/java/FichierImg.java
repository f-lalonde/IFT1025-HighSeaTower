import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class FichierImg extends Corps {
    private final Image image;

    public FichierImg(double posX, double posY, String image, double largeur, double hauteur){
        super(posX, posY);
        this.setLargeur(largeur);
        this.setHauteur(hauteur);
        this.image = new Image(image);
    }

    @Override
    public void draw(GraphicsContext context){
        context.drawImage(image, getPosX(), getPosY(), getLargeur(), getHauteur());
    }
}
