public class MenuBulle extends FichierImg {

    private final double amplitude;

    public MenuBulle(double posX, double posY, String image, double largeur, double hauteur, double amplitude) {
        super(posX, posY, image, largeur, hauteur);
        this.amplitude = amplitude;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        setPosX(getPosX() + amplitude*Math.sin(Math.toRadians(getPosY())));
        setPosX(Math.min(getPosX(), HighSeaTower.WIDTH));
        setPosX(Math.max(getPosX(), -30));
    }
}
