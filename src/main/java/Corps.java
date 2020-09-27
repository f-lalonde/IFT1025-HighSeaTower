import javafx.scene.canvas.GraphicsContext;

/**
 * Classe abstraite à étendre pour tous les objets qui ont une présence physique dans le jeu.
 * Il faut définir la méthode draw() selon les caractéristiques que l'on veut que l'objet prenne.
 */
public abstract class Corps {

    private double largeur, hauteur;
    private double posX, posY;

    private double vitX, vitY;
    private double accX, accY;
    private double vitAppoint;

    private boolean modeDebug;

    /**
     *
     * @param posX  Position horizontale où est générée l'objet
     * @param posY  Position verticale où est générée l'objet
     */
    public Corps(double posX, double posY){
        this.posX = posX;
        this.posY = posY;
    }
    /**
     * Met à jour la position et la vitesse de l'objet
     * @param deltaTime Temps écoulé depuis le dernier update() en secondes
     */
    public void update(double deltaTime){
        vitX += deltaTime * accX;
        vitY += deltaTime * accY;
        posX += deltaTime * vitX;
        posY += deltaTime * (vitY + vitAppoint);
        // ↑ vitAppoint est utilisé pour aider à synchroniser des objets entre eux.

        // Force l'objet à rester dans les bornes de l'écran avec un léger rebondissement
        if(posX + largeur > HighSeaTower.WIDTH || posX < 0) {
            vitX = -vitX/3;
        }

        // Si l'objet est généré ou se retrouve temporairement hors de la zone de jeu, il y est poussé.
        posX = Math.min(posX, HighSeaTower.WIDTH - largeur);
        posX = Math.max(posX, 0);

        // on permet une certaine zone où les objets peuvent exister en haut et en bas de la zone de jeu.
        //posY = Math.min(posY, 2*HighSeaTower.HEIGHT);
        posY = Math.max(posY, -HighSeaTower.HEIGHT);
    }

    public double getLargeur(){
        return this.largeur;
    }

    public void setLargeur(double largeur) {
        this.largeur = largeur;
    }

    public double getHauteur(){
        return this.hauteur;
    }

    public void setHauteur(double hauteur) {
        this.hauteur = hauteur;
    }

    public double getPosX(){
        return this.posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY(){
        return this.posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getVitX(){
        return this.vitX;
    }

    public void setVitX(double vitX) {
        this.vitX = vitX;
    }

    public double getVitY(){
        return this.vitY;
    }

    public void setVitY(double vitY) {
        this.vitY = vitY;
    }

    public double getAccX(){
        return this.accX;
    }

    public void setAccX(double accX) {
        this.accX = accX;
    }

    public double getAccY(){
        return this.accY;
    }

    public void setAccY(double accY) {
        this.accY = accY;
    }

    public double getVitAppoint(){
        return this.vitAppoint;
    }

    public void setVitAppoint(double vitAppoint) {
        this.vitAppoint = vitAppoint;
    }

    public boolean isModeDebug() {
        return modeDebug;
    }

    public void setModeDebug(boolean modeDebug) {
        this.modeDebug = modeDebug;
    }

    public abstract void draw(GraphicsContext context);
}
