import javafx.scene.canvas.GraphicsContext;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;

import java.io.File;

public class Jeu {
    // Variables purement du jeu
    public static final int WIDTH = HighSeaTower.WIDTH, HEIGHT = HighSeaTower.HEIGHT;
    private boolean modeDebug;
    private boolean pause;

    private double score;

    private double jeuAcceleration;
    private double vitesseNormale;
    private double vitesseJeu;
    private final double[] stockerPendantPause = new double[2];

    private boolean partieCommence = false;
    protected boolean finJeu;

    private final AudioClip[] sonBulles = new AudioClip[]{
            new AudioClip(new File("src/main/resources/sons/bulles1.wav").toURI().toString()),
            new AudioClip(new File("src/main/resources/sons/bulles2.wav").toURI().toString()),
            new AudioClip(new File("src/main/resources/sons/bulles3.wav").toURI().toString()),
            new AudioClip(new File("src/main/resources/sons/bulles4.wav").toURI().toString()),
    };

    private final AudioClip whoosh = new AudioClip(new File("src/main/resources/sons/whoosh.wav").toURI().toString());

    // Objets créés
    private final Meduse meduse;
    private final Plateforme[] plateformes = new Plateforme[6];
    private final Bulles[][] bulles = {new Bulles[5], new Bulles[5], new Bulles[5]};
    private final Rectangle cheese = new Rectangle(0, -200, WIDTH, 200);
    private final FichierImg spaaace = new FichierImg(0,0,"/images/spaaaace.png", WIDTH, HEIGHT);
    private final FichierImg lune = new FichierImg(0,(double)-HEIGHT/2,"/images/lune.png", WIDTH, 193);
    private final FichierImg drapeau = new FichierImg(0,0, "/images/drapeau.png",50,62);

    // Variables pour la génération d'objets
    private final String[] typesPlateformes = {"simple", "rebondissante", "accelerante", "solide"};

    private int platActuelle = 1; // on a déjà placé la première plateforme au début du jeu
    private int platPrecedente = 0;
    private boolean empecherSolide;

    private double tempsBulles;

    // Variables de gestion de la mise à jour des objets en lien avec le jeu
    private final boolean[] testAuSol = new boolean[plateformes.length];
    private boolean retablirVit = false;
    private boolean lockView = false;
    private double tempAccY = 0;
    private int delais;
    double opacity = 1;
    private boolean astonaut;
    private boolean gg = false;
    private boolean preGG = false;

    /**
     * Constructeur de jeu. Demande la génération de la méduse et des premières plateformes et démarre en pseudo-pause.
     */
    public Jeu(int difficulte){
        double[][] difficultes = {{2, 50}, {4, 50}, {4, 100}};
        this.jeuAcceleration = difficultes[difficulte][0];
        this.vitesseNormale = difficultes[difficulte][1];

        this.vitesseJeu = 0;
        finJeu = false;

        // la première plateforme est sous la méduse
        plateformes[0] = new Plateforme(100, 400, 150, "simple", vitesseJeu);

        meduse = new Meduse(150, 350);

        for (int i = 1; i < plateformes.length; i++) {
            plateformes[i] = genererPlateforme();
        }
        genererBulles(0);
        genererBulles(1);
        genererBulles(2);
    }

    /**
     * Génère une plateforme avec une largeur, un type, et une position horizontale aléatoire.
     * Génère la plateforme avec une distance verticale de 100 pixels par rapport à la plateforme précédente
     * @return l'objet Plateforme générée
     */
    public Plateforme genererPlateforme(){
        // on extrait la position de la plateforme précédente
        double posYprec = plateformes[platPrecedente].getPosY();

        // Constantes calculées depuis la contrainte sur la largeur (min 80, max 175)
        double constante = 0.8421;
        double constL = 95;

        double largeurPlateforme = Math.round(constL * (Math.random() + constante));
        double plateformePosX = Math.round(WIDTH * Math.random());

        // on s'assure qu'elle ne dépasse pas la largeur de la scène
        if((plateformePosX + largeurPlateforme) > WIDTH){
            plateformePosX = plateformePosX - largeurPlateforme;
        }

        // on choisi le type de plateforme générée
        int choixType = (int) Math.round(Math.random() * 100);
        String typePlateforme;
        if(choixType <= 5){                             // 5% Solide
            typePlateforme = typesPlateformes[3];
        } else if(choixType <=15) {                     // 10% accélérante
            typePlateforme = typesPlateformes[2];
        } else if(choixType <= 35) {                    // 20% rebondissante
            typePlateforme = typesPlateformes[1];
        } else {                                        // 65% simple
            typePlateforme = typesPlateformes[0];
        }

        // Si on a deux plateformes solides de suite, on rechoisi, avec plus de chance d'avoir une accélérante
        if(typePlateforme.equals("solide") && empecherSolide) {
            choixType = (int) Math.round(Math.random() * 100);
            if (choixType <= 50) {                              // 50% accélérante
                typePlateforme = typesPlateformes[2];
            } else if (choixType <= 80) {                       // 30% rebondissante
                typePlateforme = typesPlateformes[1];
            } else {                                            // 20% simple
                typePlateforme = typesPlateformes[0];
            }
        }

        // convivialité pour plateformes de type solide
        if(typePlateforme.equals("solide")){
            // assure qu'il y a toujours un espace des deux côtés de la plateforme pour passer
            if(WIDTH - (plateformePosX + largeurPlateforme) < 60){
                plateformePosX -= 60;
            }  else if (plateformes[platPrecedente].getPosX() < 100) {
                plateformePosX = Math.random() * (WIDTH - 100) + plateformes[platPrecedente].getPosX();
            }

            boolean bloqueForSure = plateformePosX <= plateformes[platPrecedente].getPosX() &&
                    plateformePosX+largeurPlateforme >= plateformes[platPrecedente].getPosX() + plateformes[platPrecedente].getLargeur();

            // empêche une plateforme solide de bloquer complètement le joueur
            if(bloqueForSure) {
                if (plateformes[platPrecedente].getLargeur() >= 130) {
                    largeurPlateforme = plateformes[platPrecedente].getLargeur() - 55;
                } else {
                    plateformePosX = plateformes[platPrecedente].getPosX() - 50;
                }

            }
        }

        // On appelle le constructeur de la plateforme avec les variables générées
        Plateforme tempPlate = new Plateforme(plateformePosX, posYprec-100, largeurPlateforme, typePlateforme, vitesseJeu);

        // Si la plateforme générée était solide, on  s'assure que la prochaine ne le sera pas.
        empecherSolide = typePlateforme.equals("solide");

        // on garde un suivi pour la prochaine plateforme qui sera générée
        platPrecedente = platActuelle;
        platActuelle = (platActuelle + 1) % (plateformes.length);

        return tempPlate;
    }

    /**
     * Génère un groupe de bulles selon la grandeur du tableau de bulles envoyé.
     * Choisi au hasard un diamètre pour chaque bulle, et une position horizontale pour le groupe de bulles.
     * @param groupe tableau d'objets Bulles
     */
    public void genererBulles(int groupe){
        double bullesPosX = Math.random()*WIDTH;
        double bullesPosY = Math.random()*20 + HEIGHT + 40;

        for(int i=0; i<bulles[groupe].length; ++i){
            double diametre = Math.random()*30 + 10;
            bulles[groupe][i] = new Bulles(bullesPosX, bullesPosY, diametre);
        }
    }

    /**
     * Débute la partie. Initialise la vitesse de la méduse et des plateformes,
     * ainsi que l'accélération verticale de la méduse.
     */
    public void commencerPartie(){
        partieCommence = true;
        this.vitesseJeu = vitesseNormale;
        meduse.setAccY(1200);
        for (Plateforme plateforme : plateformes) {
            plateforme.setVitY(vitesseJeu);
        }
    }

    /**
     * Transmet à la méduse les commandes envoyés depuis la classe principale.
     * @param move String représentant la touche d'action appuyée ou relâchée.
     */
    public void mouvement(String move){
        if(partieCommence) {
            if(meduse.isAstronaut()){
                meduse.spaceMove(move);

            }else if("saut".equals(move)) {
                meduse.saut();

            } else {
                meduse.xMouvement(move);
            }
        } else {
            commencerPartie();
        }
    }

    /**
     * Stocke les informations de vitesse afin de mettre le jeu en pause.
     */
    private void preparePause() {
        stockerPendantPause[0] = jeuAcceleration;
        stockerPendantPause[1] = vitesseJeu;
        jeuAcceleration = 0;
        vitesseJeu = 0;
    }

    /**
     * Lorsque la touche T est appuyée, met le jeu en pause, et active le mode débogage.
     */
    public void toggleDebug(){
        if(partieCommence){
            modeDebug ^= true; // XOR true --> "flip" la valeur booléenne
        }

        if(modeDebug){
            meduse.setModeDebug(true);
            preparePause();
            pause = true;

        } else {
            pause = false;
            meduse.setModeDebug(false);
            jeuAcceleration = stockerPendantPause[0];
            vitesseJeu = stockerPendantPause[1];
        }
    }

    /**
     * Récupère les informations de débogage et les formatte dans un String.
     * @return  un String formatté qui contient les informations de manière lisible.
     */
    public String getDebugInfo(){
        // calcule de la différence avec la position initiale de la méduse.
        int posY = (int)score - (int)meduse.getPosY() + HEIGHT - 130;
        int posX = (int)meduse.getPosX();

        // La vitesse affichée est relative au "monde" du jeu, et non pas relative à l'écran.
        int vitX = (int)meduse.getVitX();
        int vitY = (int)meduse.getVitY();
        int accX = (int)meduse.getAccX();
        int accY = (int)meduse.getAccY();
        int vitJeu = (int)vitesseJeu;
        boolean sol = meduse.estAuSol();

        String ouiNon;
        if(sol){
            ouiNon = "oui";
        } else {
            ouiNon = "non";
        }

        return  "Position = (" + posX + ", " + posY + ")\n" +
                "v = (" + vitX + ", " + vitY + ")\n" +
                "a = (" + accX + ", " + accY + ")\n" +
                "Touche au sol :" + ouiNon +"\n" +
                "Vitesse y absolue : "+ Math.abs(vitY + vitJeu)+"\n";
    }

    /**
     * Effectue la mise à jour de tous les éléments du jeu
     * @param deltaTime temps écoulé depuis le dernier update(), en secondes.
     */
    public void update(double deltaTime){
        // Éléments mis à jour peu importe l'état du jeu
        meduse.update(deltaTime);
        cheese.update(deltaTime);

        // On génère des bulles aux 3 secondes
        tempsBulles += deltaTime;
        if(score < 500000) {
            if (tempsBulles >= 6) {
                int son = (int) Math.floor(Math.random() * 3.99);
                genererBulles(0);
                genererBulles(1);
                genererBulles(2);
                tempsBulles = 0;
                sonBulles[son].play();
            }
        }
        if(score < 600000){
            // mise à jour de la position des bulles
            for (Bulles[] bulle : bulles) {
                for (Bulles value : bulle) {
                    value.update(deltaTime);
                }
            }
        }

        // Éléments mis à jour lorsque la partie est en cours
        if(partieCommence && !astonaut) {
            if(!pause){
                // Lorsque le jeu est actif
                this.vitesseNormale += jeuAcceleration * deltaTime;

                score += vitesseJeu*deltaTime;

                // Si la méduse tombe sous l'écran, on termine la partie actuelle et on en commence une nouvelle

                if (meduse.getPosY() > HEIGHT + meduse.getHauteur()) {
                    finJeu = true;
                    partieCommence = false;
                }

                if (!lockView) {
                    meduse.setVitAppoint(vitesseJeu);
                }

                double tempVitY; // stocke la vitesse de la méduse durant le mode "lockView"

                // On désactive le mode "lockView" lorsque la méduse "redescend" sous le seuil, dans l'univers du jeu.
                if (lockView && vitesseJeu <= vitesseNormale) {
                    meduse.setAccY(tempAccY);
                    meduse.setVitAppoint(vitesseJeu);
                    tempAccY = 0;
                    lockView = false;
                    delais = 7; // attente de quelques cycles pour éviter un mode "lockView" instable
                }
                if (delais != 0) {
                    delais--;
                }

                // force à retourner à la vitesse normale en cas de décalage
                if (!lockView && !meduse.estSurAccelere() && vitesseJeu != vitesseNormale) {
                    vitesseJeu = vitesseNormale;
                }

                /*  Si la méduse atteint 75% de la hauteur de l'écran, on active le mode "lockView", i.e. on la
                    maintient en place et on additionne la magnitude de la vitesse de la méduse à ce moment à la
                    vitesse du jeu. La méduse conserve donc sa vitesse relative dans l'univers du jeu, mais la
                    "caméra" qui la suit accélère.
                 */
                if (delais == 0 && !lockView && meduse.getPosY() <= (double) HEIGHT / 4) {
                    tempAccY = meduse.getAccY();
                    tempVitY = meduse.getVitY() + vitesseJeu;
                    meduse.setVitAppoint(0);
                    meduse.setVitY(0);
                    meduse.setAccY(0);
                    lockView = true;
                    vitesseJeu -= tempVitY;

                } else if(delais == 0 && meduse.getPosY() <= (double) HEIGHT / 4){
                    // Si durant le lockview le joueur arrive à sauter au dela du seuil, il sera replacé au seuil.
                    vitesseJeu -= meduse.getVitY();
                    // 30000? Mais qu'est-ce? o_O
                    if(vitesseJeu > 30000){
                        vitesseJeu = 30000;
                    }
                    meduse.setVitY(0);
                    meduse.setPosY((double) HEIGHT / 4);
                }

                // Mise à jour de la vitesse du jeu plutôt que la vitesse de la méduse
                if (lockView) {
                    vitesseJeu -= deltaTime * tempAccY;
                }

                // Gestion de la plateforme accélérante

                // Si la méduse n'est plus sur une plateforme accélérante, on retourne à la vitesse normale de jeu.
                if (!meduse.estSurAccelere() && retablirVit) {
                    vitesseJeu = vitesseNormale;
                    retablirVit = false;
                }

                // Si la méduse est sur une plateforme accélérante, on triple la vitesse du jeu.
                if (meduse.estSurAccelere() && !retablirVit) {
                    vitesseJeu = 3 * vitesseNormale;
                    retablirVit = true;

                    // changement immédiat de la vitesse de la méduse pour éviter un problème d' "oscillation"
                    meduse.setVitAppoint(vitesseJeu);
                }

                // Si la méduse atteint une certaine vitesse, elle a soit sauté,
                // ou bien elle s'est jetée en bas de la plateforme accélérante.
                if (Math.abs(meduse.getVitY()) > 55) {
                    meduse.setSurAccelere(false);
                }

            } else {
                // en mode debug + pause

                meduse.setVitAppoint(0);
                // On sort de la pause, en mode débug, lorsque la méduse atteint 75% de la hauteur de l'écran
                if(meduse.getPosY() <= (double) HEIGHT / 4){
                    pause = false;
                    jeuAcceleration = stockerPendantPause[0];
                    vitesseJeu = stockerPendantPause[1];
                }

                // Ouhhhh ♫ secret! secret! I got a secret! ♫
                if (meduse.getPosY() > HEIGHT) {
                    meduse.setGodMode(true);
                }
                if(meduse.getPosY() > 2*HEIGHT){
                    finJeu = true;
                    partieCommence = false;
                }
            }

            /*if(modeDebug){

            // Espace réservé aux membres selects du club VIP "trucs à faire en débogage".

            }*/

            // Mise à jour des variables des plateformes, et génération de nouvelles plateformes
            if(score < 505000) {
                for (int i = 0; i < plateformes.length; ++i) {
                    plateformes[i].setVitY(vitesseJeu);
                    plateformes[i].setModeDebug(modeDebug);
                    plateformes[i].update(deltaTime);
                    testAuSol[i] = meduse.testCollision(plateformes[i]);
                    if (plateformes[i].getPosY() > HEIGHT + 40 && score < 500000) {
                        plateformes[platActuelle] = genererPlateforme();
                    }
                }
            }

            // gestion des collisions entre la méduse et les plateformes
            boolean tousPasAuSol = true;
            for (boolean test : testAuSol) {
                if (test) {
                    meduse.setAuSol(true);
                    tousPasAuSol = false;
                }
            }
            if(tousPasAuSol){
                meduse.setAuSol(false);
            }

            // gestion du mode astronaute
            if(score > 500000 && !meduse.isAstronaut()){
                cheese.setVitY(200);
                if(!whoosh.isPlaying()){
                    whoosh.play();
                }
                if(cheese.getPosY()  > 50){
                    meduse.setAstronaut(true);
                    astonaut = true;
                    meduse.setVitAppoint(0);
                    vitesseJeu = 0;
                }
            }

        } else if (astonaut) {
            if(score < 1000000){
                score -= meduse.getVitY()*deltaTime;

                if(meduse.getPosY() < (double)HEIGHT/3){
                    meduse.setPosY((double)HEIGHT/3);

                } else if (score > 500000 && meduse.getPosY() > (double)2*HEIGHT/3){
                    meduse.setPosY((double)2*HEIGHT/3);

                } else if (meduse.getPosY() > HEIGHT+meduse.getHauteur()){
                    finJeu = true;
                    partieCommence = false;
                }

            } else {
                preGG = true;
                score = 1000000;
                meduse.setVitY(0);
                meduse.setAccY(0);

                if(lune.getPosY()>=0){
                    meduse.setVitY(-20);
                    lune.setPosY(0);
                    lune.setVitY(0);
                } else {
                    meduse.setPosY((double)HEIGHT/3);
                    lune.setVitY(50);
                }
                if(meduse.getPosY() <= 100){
                    meduse.setVitY(0);
                    drapeau.setPosY(meduse.getPosY());
                    if(meduse.getPosX() < HEIGHT -100){
                        drapeau.setPosX(meduse.getPosX() + 60);
                    } else {
                        drapeau.setPosX(meduse.getPosX() - 60);

                    }
                    gg = true;
                }
                lune.update(deltaTime);
            }


        }

    }

    public void toggleGodMode(){
        meduse.setGodMode(!meduse.isGodMode());
    }

    public boolean getGodMode() {
        return meduse.isGodMode();
    }

    public boolean getDebug(){
        return modeDebug;
    }

    public double getScore(){
        return this.score;
    }

    public void draw(GraphicsContext context){
        if(score<300000){
            context.setFill(Color.DARKBLUE);
        } else {
            if(score > 500000){

                spaaace.draw(context);
                opacity = 1-(score-500000)/750000;
            }
            context.setFill(Color.rgb(0,0,Math.max(0,139-(int)(score-300000)/5000), Math.max(0,opacity)));
        }
        context.fillRect(0,0, WIDTH, HEIGHT);

        if(score > 1000000 - HEIGHT){
            lune.draw(context);
        }

        for (Bulles[] bulle : bulles) {
            for (Bulles value : bulle) {
                value.draw(context);
            }
        }
        meduse.draw(context);
        cheese.draw(context);
        for (Plateforme p : plateformes) {
            p.draw(context);
        }
        if(gg){
            drapeau.draw(context);
        }
    }

    public boolean isAstonaut() {
        return astonaut;
    }

    public boolean isGg() {
        return gg;
    }

    public boolean isPreGG() {
        return preGG;
    }
}
