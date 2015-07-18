/*
 * put your module comment here
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */


package  com.crionics.turbomandel;

/*
 (c) 1996 Olivier REFALO
 */
import  java.awt.Color;


//
// Cette classe est utilisee pour gerer la palette de couleur
//
final class Palette {
    private Color[] tblcouleurs;

    // Debug!
    // Constructeur : nbr de couleurs de la palette et couleur initiale.
    //
    Palette (int nbr_cols) {
        tblcouleurs = new Color[nbr_cols];
    }

    //
    // Retourne la couleur d'index i
    //
    public Color getcolor (int i) {
        return  tblcouleurs[i];
    }

    //
    // Permet de creer dans degrades dans la palette
    // src: couleur source
    // dst: couleur destination
    // nbr_deg: nbr d'iteration de src à dst
    // idx: index de la 1ere couleur dans le tableau tblcouleurs
    //
    public void degrade (Color src, Color dst, int nbr_deg, int idx) {
        int r = src.getRed();
        int v = src.getGreen();
        int b = src.getBlue();
        int increment_r = (dst.getRed() - r)/nbr_deg;
        int increment_v = (dst.getGreen() - v)/nbr_deg;
        int increment_b = (dst.getBlue() - b)/nbr_deg;
        for (int i = 0; i < nbr_deg; i++) {
            tblcouleurs[idx + i] = new Color(r, v, b);
            r += increment_r;
            v += increment_v;
            b += increment_b;
        }
    }
}



