import javafx.scene.canvas.GraphicsContext;

/**
 * Fait le lien entre la mécanique du jeu et la classe principale
 */
public class Controleur {
    int difficulte;
    boolean jeuCommence;
    Jeu jeu;
    Menu menu;

    public Controleur(){
        jeuCommence = false;
        menu = new Menu(false);
    }

    void jeuCommence(){
        if(difficulte == -1){
            restart();
        } else {
            jeu = new Jeu(difficulte);
            jeuCommence = true;
        }

    }

    void draw(GraphicsContext context){
        if(!jeuCommence){
            menu.draw(context);
        } else {
            jeu.draw(context);
        }
    }

    void update(double deltaTime){
        if(!jeuCommence){
            menu.update(deltaTime);
            jeuCommence = menu.isFinMenu();
            if(jeuCommence){
                jeuCommence();
            }
        } else {
            jeu.update(deltaTime);
            if (jeu.finJeu) {
                if(difficulte == -1){
                    restart();
                } else {
                    jeu = new Jeu(difficulte);
                }
            }
        }
    }

    int getScore(){
        return (int)jeu.getScore();
    }

    void toggleGodMode(){
        jeu.toggleGodMode();
    }

    void toggleDebug(){
        jeu.toggleDebug();
    }

    String getDebugInfo(){
        return jeu.getDebugInfo();
    }

    boolean getDebug(){
        return jeu.getDebug();
    }

    boolean getGodMode() {
       return jeu.getGodMode();
    }

    void mouvement(String mouvement){
        if(jeuCommence){
            jeu.mouvement(mouvement);
        }
    }

    boolean getMusicStart(){
        return menu.isMusicStart();
    }

    void choixDifficulte(){
        if(!jeuCommence){
            difficulte = menu.choixDifficulte();
        }
    }

    boolean getJeuCommence(){
        return jeuCommence;
    }

    boolean restart(){
        jeuCommence = false;
        menu = new Menu(true);
        return false;
    }

    boolean isAstronaut(){
        if(jeuCommence){
            return jeu.isAstonaut();
        } else {
            return false;
        }
    }

    boolean isGg(){
        if(isAstronaut()){
            return jeu.isGg();
        } else {
            return false;
        }
    }

    boolean preGg(){
        if(isAstronaut()){
            return jeu.isPreGG();
        } else {
            return false;
        }
    }
}
