import javafx.scene.canvas.GraphicsContext;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import java.io.File;

public class Menu {

    public static final int WIDTH = HighSeaTower.WIDTH, HEIGHT = HighSeaTower.HEIGHT;
    private final Rectangle fondNoir = new Rectangle(0,0,WIDTH, HEIGHT);

    private final FichierImg logo = new FichierImg(
            65, 180,
            "/images/logo.png",
            220, 120);

    private final MenuBulle[] bulles = new MenuBulle[]{
            new MenuBulle(Math.random()*(WIDTH-30), HEIGHT+40,
                    "/images/bubble.png", 30, 30, Math.random()*40 + 20),
            new MenuBulle(Math.random()*(WIDTH-30), HEIGHT+40,
                    "/images/bubble.png", 30, 30, Math.random()*40 + 20),
            new MenuBulle(Math.random()*(WIDTH-30), HEIGHT+40,
                    "/images/bubble.png", 30, 30, Math.random()*40 + 20)
    };

    private final FichierImg boutonNormal = new FichierImg(
            90, HEIGHT + 195,
            "/images/boutonnm.png",
            180, 98);

    private final FichierImg boutonNormalHover = new FichierImg(
            boutonNormal.getPosX(), boutonNormal.getPosY(),
            "/images/boutonnmpresse.png",
            180, 98);

    private final FichierImg boutonDiff = new FichierImg(
            boutonNormal.getPosX(), boutonNormal.getPosY()+boutonNormal.getHauteur(),
            "/images/boutondf.png",
            180, 98);

    private final FichierImg boutonDiffHover = new FichierImg(
            boutonNormal.getPosX(), boutonNormal.getPosY()+boutonNormal.getHauteur(),
            "/images/boutondfpresse.png",
            180, 98);

    private final FichierImg boutonOmg = new FichierImg(
            boutonNormal.getPosX(), boutonDiff.getPosY()+ boutonDiff.getHauteur(),
            "/images/boutonomg.png",
            180, 98);

    private final FichierImg boutonOmgHover = new FichierImg(
            boutonNormal.getPosX(), boutonDiff.getPosY()+ boutonDiff.getHauteur(),
            "/images/boutonomgpresse.png",
            180, 98);

    private final FichierImg hst = new FichierImg(
            0, HEIGHT,
            "/images/hst.png",
            WIDTH, 200);

    private final AudioClip[] sons = new AudioClip[]{
            new AudioClip(new File("src/main/resources/sons/coins.wav").toURI().toString()),
            new AudioClip(new File("src/main/resources/sons/menu.wav").toURI().toString()),
            new AudioClip(new File("src/main/resources/sons/menu.wav").toURI().toString()),
            new AudioClip(new File("src/main/resources/sons/menu.wav").toURI().toString()),
    };

    private final boolean[] bulleReady = {true, true, true};

    private final boolean[] mediaPlayed = {false, false, false};
    private double tempDelais;
    private boolean sonLogojoue = false;
    private boolean musicStart;
    private boolean finMenu;
    private boolean restart;

    public Menu(boolean restart){
        if(restart){
            this.restart = true;
            hst.setPosY(0);
            boutonNormal.setPosY(195);
            boutonNormalHover.setPosY(boutonNormal.getPosY());
            boutonDiff.setPosY(boutonNormal.getPosY()+boutonNormal.getHauteur());
            boutonDiffHover.setPosY(boutonNormal.getPosY()+boutonNormal.getHauteur());
            boutonOmg.setPosY(boutonDiff.getPosY()+ boutonDiff.getHauteur());
            boutonOmgHover.setPosY(boutonDiff.getPosY()+ boutonDiff.getHauteur());
        }
    }

    public int choixDifficulte(){
        if(HighSeaTower.mouseX > boutonNormal.getPosX() && HighSeaTower.mouseX < boutonNormal.getPosX()+boutonNormal.getLargeur()) {
            if (HighSeaTower.mouseY > boutonNormal.getPosY() && HighSeaTower.mouseY < boutonNormal.getPosY() + boutonNormal.getHauteur()) {
                finMenu = true;
                return 0;
            } else if (HighSeaTower.mouseY > boutonDiff.getPosY() && HighSeaTower.mouseY < boutonDiff.getPosY() + boutonDiff.getHauteur()) {
                finMenu = true;
                return 1;
            } else if (HighSeaTower.mouseY > boutonOmg.getPosY() && HighSeaTower.mouseY < boutonOmg.getPosY() + boutonOmg.getHauteur()) {
                finMenu = true;
                return 2;
            }
        }
        return -1;
    }

    public void update(double deltaTime){
        if(!restart) {
            fondNoir.update(deltaTime);
            logo.update(deltaTime);
            boutonNormal.update(deltaTime);
            boutonNormalHover.update(deltaTime);
            boutonDiff.update(deltaTime);
            boutonDiffHover.update(deltaTime);
            boutonOmg.update(deltaTime);
            boutonOmgHover.update(deltaTime);
            hst.update(deltaTime);
            tempDelais += deltaTime;

            if (tempDelais > 1 && !sonLogojoue) {
                sons[0].play();
                sonLogojoue = true;
            }

            if (sonLogojoue && tempDelais > 3) {

                tempDelais = 0;
                fondNoir.setVitY(-120);
                logo.setVitY(-120);
                boutonNormal.setVitY(-120);
                boutonNormalHover.setVitY(-120);
                boutonDiff.setVitY(-120);
                boutonDiffHover.setVitY(-120);
                boutonOmg.setVitY(-120);
                boutonOmgHover.setVitY(-120);
                hst.setVitY(-120);
            }

            if (boutonNormal.getPosY() < 195) {
                fondNoir.setVitY(0);
                logo.setVitY(0);
                boutonNormal.setVitY(0);
                boutonNormalHover.setVitY(0);
                boutonDiff.setVitY(0);
                boutonDiffHover.setVitY(0);
                boutonOmg.setVitY(0);
                boutonOmgHover.setVitY(0);
                hst.setVitY(0);
                musicStart = true;
            }

        } else {
            boutonNormal.update(deltaTime);
            boutonNormalHover.update(deltaTime);
            boutonDiff.update(deltaTime);
            boutonDiffHover.update(deltaTime);
            boutonOmg.update(deltaTime);
            boutonOmgHover.update(deltaTime);
        }

        if (boutonNormal.getPosY() <= 195) {
            double bulleFlag = Math.random()*100;

            for(int i = 0; i<bulles.length; ++i){

                if(bulleFlag > 3*i && bulleFlag <= 3*i+3 && bulleReady[i]){
                    bulles[i] = new MenuBulle(Math.random()*(WIDTH-30), HEIGHT+40,
                            "/images/bubble.png", 30, 30, Math.random()*1.5+.2);
                    bulles[i].setVitY(-100);
                    bulleReady[i] = false;
                }
                if(bulles[i].getPosY() < -35){
                    bulles[i].setVitY(0);
                    bulleReady[i] = true;
                }
                if(!bulleReady[i]){
                    bulles[i].update(deltaTime);
                }
            }

        }

    }

    public void draw(GraphicsContext context){
        context.setFill(Color.DARKBLUE);
        context.fillRect(0, 0, WIDTH, HEIGHT);
        if(!restart) {
            fondNoir.draw(context);
            logo.draw(context);
        }
        bulles[1].draw(context);
        hst.draw(context);
        bulles[0].draw(context);
        boutonNormal.draw(context);
        boutonDiff.draw(context);
        boutonOmg.draw(context);

        if(HighSeaTower.mouseX > boutonNormal.getPosX() && HighSeaTower.mouseX < boutonNormal.getPosX()+boutonNormal.getLargeur()){
            if(HighSeaTower.mouseY > boutonNormal.getPosY() && HighSeaTower.mouseY < boutonNormal.getPosY()+boutonNormal.getHauteur()){
                boutonNormalHover.draw(context);

                if(!mediaPlayed[0]){
                    sons[1].play();
                    mediaPlayed[0] = true;
                }
                mediaPlayed[1] = false;
                mediaPlayed[2] = false;

            } else if (HighSeaTower.mouseY > boutonDiff.getPosY() && HighSeaTower.mouseY < boutonDiff.getPosY()+boutonDiff.getHauteur()){
                boutonDiffHover.draw(context);

                if(!mediaPlayed[1]){
                    sons[2].play();
                    mediaPlayed[1] = true;
                }

                mediaPlayed[0] = false;
                mediaPlayed[2] = false;

            } else if (HighSeaTower.mouseY > boutonOmg.getPosY() && HighSeaTower.mouseY < boutonOmg.getPosY()+boutonOmg.getHauteur()) {
                boutonOmgHover.draw(context);

                if(!mediaPlayed[2]){
                    sons[3].play();
                    mediaPlayed[2] = true;
                }

                mediaPlayed[0] = false;
                mediaPlayed[1] = false;

            } else {
                mediaPlayed[0] = false;
                mediaPlayed[1] = false;
                mediaPlayed[2] = false;
            }
        } else {
            mediaPlayed[0] = false;
            mediaPlayed[1] = false;
            mediaPlayed[2] = false;
        }
        bulles[2].draw(context);
    }

    public boolean isMusicStart() {
        return musicStart;
    }

    public boolean isFinMenu() {
        return finMenu;
    }

}
