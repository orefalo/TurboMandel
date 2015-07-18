/*
 * put your module comment here
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */


package  com.crionics.turbomandel;

/*
 (c)1996 Olivier REFALO
 */
import  java.awt.*;
import  java.lang.*;
import  java.util.Date;
import  javax.swing.*;
import  java.awt.event.*;


/**
 * put your documentation comment here
 */
public final class TurboMandelCanvas extends JPanel
        implements Runnable {
    // true - the thread is running (introduced because of JDK12 deprecations)
    private boolean thread_running = false;
    // Variable utilisee pour le calcule du mandelbrot
    private int maxitr = 100;                   // Nbr maximum d'iterations
    private int nbrcols = 32;                   // Nbr de couleurs de la representation
    private boolean optimize = false;           // Optimiser l'affichage ?
    // Coordonnée d'affichage
    private DimensionFloat plane_area=null;
    // Variables necessaires pour le double-buffer
    private Image bufimage = null;
    private Dimension bufsz = null;
    private Graphics bufgfx = null;
    // Gestion de la palette de couleur
    private Palette palette;
    // Pour le calcule fractionné du mandelbrot
    private int lx, ly, testcolor, step;
    private double ratio_x, ratio_y;
    private boolean samecolor;
    private int repaint_step;
    // Pour gerer l'élastique
    private int x_deb, y_deb;
    private int x_fin, y_fin;
    // Pour le multi Thread
    private Thread creation_mandel = null;
    // 0 si pas termine sinon, nbr de secondes!
    public long temps_calcule;

    /**
     * put your documentation comment here
     * @return
     */
    public boolean IsMandeling () {
        return  (creation_mandel != null);
    }

    /**
     * put your documentation comment here
     * @param     int nc
     */
    public TurboMandelCanvas (int nc) {
        super();
        nbrcols = nc;
        palette = new Palette(nbrcols);
        palette.degrade(new Color(0x80, 0x00, 0xF0), new Color(0xF0, 0x00,
                0xF0), 4, 0);
        palette.degrade(new Color(0xF0, 0x00, 0xF0), new Color(0xF0, 0x00,
                0x00), 7, 3);
        palette.degrade(new Color(0xF0, 0x10, 0x00), new Color(0xF0, 0xF0,
                0x00), 6, 9);
        palette.degrade(new Color(0xF0, 0xF0, 0x10), new Color(0x10, 0xF0,
                0x10), 6, 14);
        palette.degrade(new Color(0x10, 0xF0, 0x10), new Color(0xF0, 0xC0,
                0xB0), 6, 19);
        palette.degrade(new Color(0x00, 0xC0, 0xB0), new Color(0x00, 0x00,
                0x00), 7, 25);
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Positionnement de l'optimisation
    public void setOptimize (boolean value) {
        optimize = value;
    }

    /**
     * put your documentation comment here
     * @param value
     */
    public void setIterations (int value) {
        maxitr = value;
    }

    //
    // Lancement du calcule
    //
    public void start_computation (DimensionFloat df) {
        if (IsMandeling() && creation_mandel.isAlive())
            kill_computation();
        else {
            bufsz = getSize();
            bufimage = createImage(bufsz.width, bufsz.height);
            bufgfx = bufimage.getGraphics();
        }
        plane_area = df;
        creation_mandel = new Thread(this);
        // le nouveau thread appel la fonction run()
        creation_mandel.start();
    }

    //
    // Destruction du calcule
    //
    public void kill_computation () {
        if (IsMandeling() && creation_mandel.isAlive()) {
            thread_running = false;
            while (creation_mandel.isAlive());
        }
    }

    //
    // Thread qui calcule le mandelbrot
    //
    public void run () {
        thread_running = true;
        temps_calcule = 0;
        long debut = new Date().getTime();
        ratio_x = (plane_area.xr1 - plane_area.xr0)/bufsz.width;
        ratio_y = (plane_area.yr1 - plane_area.yr0)/bufsz.height;
        bufgfx.clearRect(0, 0, bufsz.width, bufsz.height);
        fractionne(0, 0, bufsz.width, bufsz.height);
        repaint();
        temps_calcule = new Date().getTime() - debut;
        creation_mandel = null;
    }

    /**
     * put your documentation comment here
     * @param g
     */
    public synchronized void update (Graphics g) {
        g.drawImage(bufimage, 0, 0, null);
    }

    /**
     * put your documentation comment here
     * @param g
     */
    public void paint (Graphics g) {
        g.drawImage(bufimage, 0, 0, null);
    }

    //
    // Les fonctions suivantes mettent en place l'elastique
    //
    // Cette methode dessine l'elastique
    private void draw_elastique (int x1, int y1, int x2, int y2) {
        int swap;
        if (x2 < x1) {
            swap = x1;
            x1 = x2;
            x2 = swap;
        }
        if (y2 < y1) {
            swap = y1;
            y1 = y2;
            y2 = swap;
        }
        bufgfx.drawRect(x1, y1, x2 - x1, y2 - y1);
    }

    /**
     * put your documentation comment here
     * @param x
     * @param y
     */
    public void DeplaceSouris (int x, int y) {
        // Effacer l'ancienne position et repositionner l'elastique
        draw_elastique(x_deb, y_deb, x_fin, y_fin);
        x_fin = x;
        y_fin = y;
        draw_elastique(x_deb, y_deb, x_fin, y_fin);
        repaint();
    }

    /**
     * put your documentation comment here
     * @param x
     * @param y
     */
    public void AppuiSouris (int x, int y) {
        // Si un thread dessinait deja un mandel -> le stopper
        x_deb = x;
        y_deb = y;
        x_fin = x;
        y_fin = y;
        // Passer en mode inversion video et dessiner l'elastique
        bufgfx.setXORMode(Color.white);
        bufgfx.drawRect(x, y, 0, 0);
    }

    /**
     * put your documentation comment here
     * @param x
     * @param y
     * @return
     */
    public DimensionFloat RelacheSouris (int x, int y) {
        // Effacer l'elastique
        draw_elastique(x_deb, y_deb, x_fin, y_fin);
        bufgfx.setPaintMode();
        x_fin = x;
        y_fin = y;
        // Mettre les coordonnee dans le bon sens!
        if (x_fin != x_deb || y_fin != y_deb) {
            int swap;
            DimensionFloat triture = new DimensionFloat();
            if (x_fin < x_deb) {
                swap = x_deb;
                x_deb = x_fin;
                x_fin = swap;
            }
            if (y_fin < y_deb) {
                swap = y_deb;
                y_deb = y_fin;
                y_fin = swap;
            }
            // Calcule d'un zoom aproximativement carre!
            double ml = (Math.abs((x_fin - x_deb)*ratio_x) + Math.abs((y_fin
                    - y_deb)*ratio_y))/2.;
            triture.xr0 = plane_area.xr0 + (x_deb*ratio_x);
            triture.yr0 = plane_area.yr0 + (y_deb*ratio_y);
            triture.xr1 = triture.xr0 + ml;
            triture.yr1 = triture.yr0 - ml;
            return  (triture);
        }
        else
            // Si pas de zoom, reprendre l'execution du calcule
            return  (plane_area);
    }

    /**
     * put your documentation comment here
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    private void fractionne (int x1, int y1, int x2, int y2) {
        if (thread_running == false)
            return;
        if ((ly = y2 - y1) == 1) {
            for (; x1 < x2; x1++) {
                bufgfx.setColor(palette.getcolor(mand_calc(x1, y1)));
                bufgfx.drawLine(x1, y1, x1, y1);
            }
            return;
        }
        if ((lx = x2 - x1) == 1) {
            for (; y1 < y2; y1++) {
                bufgfx.setColor(palette.getcolor(mand_calc(x1, y1)));
                bufgfx.drawLine(x1, y1, x1, y1);
            }
            return;
        }
        int mx = (x1 + x2) >> 1;
        int my = (y1 + y2) >> 1;
        testcolor = mand_calc(mx, my);
        step = lx >> 1;
        if (step == 0)
            step = 1;
        samecolor = true;
        int i = 0;
        while (i <= lx && (samecolor &= (testcolor == mand_calc(x1 + i, y1)))
                && (samecolor &= (testcolor == mand_calc(x2 - i, y2))))
            i += step;
        if (samecolor) {
            step = ly >> 1;
            if (step == 0)
                step = 1;
            i = 0;
            while (i <= ly && (samecolor &= (testcolor == mand_calc(x1, y1 +
                    i))) && (samecolor &= (testcolor == mand_calc(x2, y2 -
                    i))))
                i += step;
        }
        if (samecolor) {
            bufgfx.setColor(palette.getcolor(testcolor));
            bufgfx.fillRect(x1, y1, x2 - x1, y2 - y1);
            if (optimize == false)
                if ((repaint_step++)%100 == 0)
                    repaint();
        }
        else {
            fractionne(x1, y1, mx, my);
            fractionne(x1, my, mx, y2);
            fractionne(mx, y1, x2, my);
            fractionne(mx, my, x2, y2);
        }
    }

    //
    //	Cette Methode calcule l'equation de MandelBrot au point (xe,ye)
    //	La valeur retournee est un index sur la table des couleurs
    //  par hypothese, la couleur noir est en fin de palette.
    private int mand_calc (int xe, int ye) {
        double za = 0.0, zb = 0.0, module = 0.0;
        double x_reel, y_reel, old_za;
        int itr = 0;
        x_reel = plane_area.xr0 + (xe*ratio_x);
        y_reel = plane_area.yr0 + (ye*ratio_y);
        while (module < 4 && itr <= maxitr) {
            old_za = za;
            za = za*za - zb*zb + x_reel;
            zb = 2.0*old_za*zb + y_reel;
            module = za*za + zb*zb;
            itr++;
        }
        if (itr < maxitr)
            return  (itr%nbrcols);
        return  (nbrcols - 1);
    }

    /**
     * put your documentation comment here
     * @exception Exception
     */
    private void jbInit () throws Exception {
        this.setPreferredSize(new Dimension(400, 400));
        this.addComponentListener(new java.awt.event.ComponentAdapter() {

            /**
             * put your documentation comment here
             * @param e
             */
            public void componentResized (ComponentEvent e) {
                this_componentResized(e);
            }
        });
    }

    /**
     * put your documentation comment here
     * @param e
     */
    void this_componentResized (ComponentEvent e) {
        if (plane_area!=null && getSize().getWidth() > 0 && getSize().getHeight() > 0) {
            start_computation(plane_area);
        }
    }
}



