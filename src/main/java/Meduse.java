import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;

import java.io.File;

public class Meduse extends Corps {
    private Image[] animActuelle;
    private Image img;

    private double tempsTotal = 0;

    private boolean auSol;
    private boolean surAccelere = false;
    private boolean godMode = false;

    private boolean astronaut = false;
    private boolean cantStopWontStop;

    private final AudioClip[] sonSaut = new AudioClip[]{
            new AudioClip(new File("src/main/resources/sons/saut1.wav").toURI().toString()),
            new AudioClip(new File("src/main/resources/sons/saut2.wav").toURI().toString()),
            new AudioClip(new File("src/main/resources/sons/saut3.wav").toURI().toString()),
            new AudioClip(new File("src/main/resources/sons/saut4.wav").toURI().toString()),
    };

/* Ne fonctionne pas :(
    private final FichierImg vapBas = new FichierImg(0,-100,"/images/vapbas.png", 60,60);
    private final FichierImg vapHaut = new FichierImg(0,-100,"/images/vaphaut.png", 60,60);
    private final FichierImg vapGauche = new FichierImg(0,-100,"/images/vapgauche.png", 60,60);
    private final FichierImg vapDroit = new FichierImg(0,-100,"/images/vapdroit.png", 60,60);
    private final boolean[] vapActif = {false,false,false,false};*/

    /**
     * Constructeur pour la méduse.
     * @param posX Position horizontale où est générée la méduse
     * @param posY Position verticale où est générée la méduse
     */
    public Meduse(double posX, double posY){
        super(posX, posY);
        this.animActuelle = framesDroite;
        this.img = animActuelle[0];
        this.setLargeur(50);
        this.setHauteur(50);
        this.setAccY(0);
        this.auSol = true;
    }

    // Tableaux des frames pour la méduse
    private final Image[] framesDroite = new Image[]{
            new Image("/images/jellyfish1.png"),
            new Image("/images/jellyfish2.png"),
            new Image("/images/jellyfish3.png"),
            new Image("/images/jellyfish4.png"),
            new Image("/images/jellyfish5.png"),
            new Image("/images/jellyfish6.png")
    };

    private final Image[] framesGauche = new Image[]{
            new Image("/images/jellyfish1g.png"),
            new Image("/images/jellyfish2g.png"),
            new Image("/images/jellyfish3g.png"),
            new Image("/images/jellyfish4g.png"),
            new Image("/images/jellyfish5g.png"),
            new Image("/images/jellyfish6g.png")
    };

    private final Image[] framesGMDroite = new Image[]{
            new Image("/images/gmjellyfish1.png"),
            new Image("/images/gmjellyfish2.png"),
            new Image("/images/gmjellyfish3.png"),
            new Image("/images/gmjellyfish4.png"),
            new Image("/images/gmjellyfish5.png"),
            new Image("/images/gmjellyfish6.png")
    };

    private final Image[] framesGMGauche = new Image[]{
            new Image("/images/gmjellyfish1g.png"),
            new Image("/images/gmjellyfish2g.png"),
            new Image("/images/gmjellyfish3g.png"),
            new Image("/images/gmjellyfish4g.png"),
            new Image("/images/gmjellyfish5g.png"),
            new Image("/images/gmjellyfish6g.png")
    };

    private final Image[] framesAstroDroit = new Image[]{
            new Image("/images/astro1.png"),
            new Image("/images/astro2.png"),
            new Image("/images/astro3.png"),
            new Image("/images/astro4.png"),
            new Image("/images/astro5.png"),
            new Image("/images/astro6.png")
    };

    private final Image[] framesAstroGauche = new Image[]{
            new Image("/images/astro1g.png"),
            new Image("/images/astro2g.png"),
            new Image("/images/astro3g.png"),
            new Image("/images/astro4g.png"),
            new Image("/images/astro5g.png"),
            new Image("/images/astro6g.png")
    };

    @Override
    public void update(double deltaTime) {
        // Physique du personnage
        super.update(deltaTime);

        // Mise à jour de l'image affichée
        tempsTotal += deltaTime;
        double frameRate = 8;
        int frame = (int) (tempsTotal * frameRate);
        img = animActuelle[frame % animActuelle.length];

        if(!cantStopWontStop && getVitX() != 0){
            setAccX(-2*getVitX());
        } else if (!cantStopWontStop){
            setAccX(0);
        }
        /* ne fonctionne pas :(
        if(astronaut){
            if(vapActif[0]){
                vapBas.setPosX(getPosX());
                vapBas.setPosY(getPosY());
            }else if(vapActif[1]){
                vapHaut.setPosX(getPosX());
                vapHaut.setPosY(getPosY());
            }else if(vapActif[2]){
                vapGauche.setPosX(getPosX());
                vapGauche.setPosY(getPosY());
            }else if(vapActif[3]){
                vapDroit.setPosX(getPosX());
                vapDroit.setPosY(getPosY());
            }else {
                vapDroit.setPosY(-100);
                vapGauche.setPosY(-100);
                vapBas.setPosY(-100);
                vapHaut.setPosY(-100);
            }
        }*/
    }

    /**
     * Vérifie s'il y a collision entre la méduse et une plateforme. Retourne vrai seulement dans le cas où
     * la méduse se trouve sur une plateforme.
     * La collision a lieu entre la plateforme et le bas de la méduse, sauf dans le cas des plateformes solides,
     * avec lesquelles le haut de la méduse provoquera aussi une collision. 
     * @param other Plateforme contre laquelle on teste la collision
     * @return valeur bool indiquant si la méduse se trouve sur une plateforme
     */
    public boolean testCollision(Plateforme other) {
        // indicateur qui indique s'il y a eu une collision avec la tête de la méduse et une plateforme solide
        boolean testSolide = false; 
        
        // test de collision avec une plateforme solide et la tête de la méduse
        if (other.getType().equals("solide") && intersects(other) && Math.abs(other.getPosY() + other.getHauteur() - this.getPosY()) < 10){
            other.setCollision(true);
            testSolide = true;
            this.setVitY(-getVitY() / 3);
            double deltaY = other.getPosY() - this.getPosY();
            this.setPosY(getPosY() - deltaY);
            other.setModeDebug(this.isModeDebug());
        }

        // test de collision entre le bas de la méduse et tout type de plateforme
        if (intersects(other) && Math.abs(this.getPosY() + this.getHauteur() - other.getPosY()) < 10) {
            other.setCollision(true);
            this.auSol = true;
            other.setModeDebug(this.isModeDebug());
            if(this.getVitY() > 0) {
                pushOut(other);
                switch (other.getType()) {

                    // Boing boing! Les plateformes rebondissantes changent la vitesse de la méduse
                    case "rebondissante":
                        int son = (int)Math.floor(Math.random()*3.99);
                        if (this.getVitY() > 150) { // Si la vitesse est assez grande, on augmente dans le sens inverse
                            this.setVitY((-1.6) * this.getVitY());
                        } else {
                            this.setVitY(-250); // Si la vitesse est petite, on donne quand même une swing!
                        }
                        sonSaut[son].play();
                        break;

                    // Met un flag. L'accélération sera gérée par le jeu
                    case "accelerante":
                        this.surAccelere = true;
                        this.setVitY(0);
                        break;

                    // Dans les autres cas, la collision fait que la méduse se dépose, au repos, sur la plateforme.
                    case "solide":
                    case "simple":
                    default:
                        this.setVitY(0);
                        break;
                }
            }
            return true;
            
        } else {
            if(!testSolide){
                other.setCollision(false);
            }
            return false;
        }
    }

    /**
     * Repousse la méduse vers le haut (sans déplacer la plateforme)
     */
    public void pushOut(Plateforme other) {
        double deltaY = this.getPosY() + this.getHauteur() - other.getPosY();
        this.setPosY(getPosY() - deltaY);
    }

    /**
     * Vérifie s'il y a intersection entre la méduse et une plateforme
     * @param other La plateforme contre laquelle on fait les test
     * @return un bool indiquant s'il y a intersection
     */
    public boolean intersects(Plateforme other) {
        return !( // Un des objets est à gauche de l’autre
                this.getPosX() + this.getLargeur() < other.getPosX()
                || other.getPosX() + other.getLargeur() < this.getPosX()
                // Un des objets est en haut de l’autre
                || this.getPosY() + this.getHauteur() < other.getPosY()
                || other.getPosY() + other.getHauteur() < this.getPosY());
    }

    /**
     * La méduse peut seulement sauter si elle se trouve sur une plateforme
     * (sauf s'il y a activation du GodMode)
     */
    public void saut(){
        int son = (int)Math.floor(Math.random()*3.99);
        if(godMode){
            setVitY(-1200);
            sonSaut[son].play();
        } else {
            if (auSol) {
                setVitY(-600);
                sonSaut[son].play();
            }
        }
    }

    /**
     * Effectue le déplacement horizontal de la méduse selon les commandes entrées.
     * @param direction String indiquant quelle touche est activée / désactivée
     */
    public void xMouvement(String direction) {
        switch (direction) {
            case "droite":
                cantStopWontStop = true;
                if(godMode){
                    animActuelle = framesGMDroite;
                } else {
                    animActuelle = framesDroite;
                }
                setAccX(1200);
                break;

            case "gauche":
                cantStopWontStop = true;
                if(godMode){
                    animActuelle = framesGMGauche;
                } else {
                    animActuelle = framesGauche;
                }
                setAccX(-1200);
                break;

            case "xStop":
                cantStopWontStop = false;
                setAccX(0);
                break;
        }
    }

    public void spaceMove(String direction){
        switch (direction){
            case "droite":
                setAccX(600);
                animActuelle = framesAstroDroit;
                //vapActif[3] = true;
                break;

            case "gauche":
                setAccX(-600);
                animActuelle = framesAstroGauche;
                //vapActif[2] = true;
                break;

            case "saut":
                //vapActif[1] = true;
                setAccY(-1200);
                break;

            case "bas":
                //vapActif[0] = true;
                setAccY(1200);
                break;

            case "xStop":
                //vapActif[2] = false;
                //vapActif[3] = false;
                setAccX(0);
                break;

            case "yStop":
                //vapActif[0] = false;
                //vapActif[1] = false;
                setAccY(0);
                break;
        }
    }

    public boolean estSurAccelere() {
        return surAccelere;
    }
    
    public void setSurAccelere(boolean surAccelere) {
        this.surAccelere = surAccelere;
    }

    public boolean estAuSol(){
        return auSol;
    }

    public void setAuSol(boolean auSol){
        this.auSol = auSol;
    }
    
    public boolean isGodMode(){
        return this.godMode;
    }
    
    public void setGodMode(boolean godMode){
        if(!astronaut){
            if(godMode){
                this.animActuelle = framesGMGauche;
            } else {
                this.animActuelle = framesGauche;
            }
        }
        this.godMode = godMode;
    }

    public boolean isAstronaut() {
        return astronaut;
    }

    public void setAstronaut(boolean astronaut) {
        this.astronaut = astronaut;
        this.setAccY(0);
        animActuelle = framesAstroDroit;
        cantStopWontStop = false;
    }

    @Override
    public void setModeDebug(boolean modeDebug) {
        super.setModeDebug(modeDebug);
    }

    @Override
    public void draw(GraphicsContext context){
        context.drawImage(img, getPosX(), getPosY(), getLargeur(), getHauteur());

        /* ne fonctionne pas :(
        if(vapActif[0]){
            vapBas.draw(context);
        }
        if(vapActif[1]){
            vapHaut.draw(context);
        }
        if(vapActif[2]){
            vapGauche.draw(context);
        }
        if(vapActif[3]){
            vapDroit.draw(context);
        }*/

        if(isModeDebug()){
            context.setFill(Color.rgb(255, 0,0,0.4));
            context.fillRect(getPosX(),getPosY(), getLargeur(),getHauteur());
        }
    }
}
