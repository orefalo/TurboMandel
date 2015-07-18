/*
 * put your module comment here
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */


package  com.crionics.turbomandel;

/*
 TurboMandel
 Written in 1992 by Olivier REFALO
 Java version 1996 by Olivier REFALO
 */
import  java.awt.*;
import  java.awt.event.*;
import  java.applet.*;
import  java.lang.Integer;
import  javax.swing.*;


/**
 * put your documentation comment here
 */
public final class TurboMandel extends JApplet {
    DimensionFloat plane_area=new DimensionFloat();
    private AboutForm theAboutForm=new AboutForm();
    private JPanel controls = new JPanel();
    TurboMandelCanvas Mandelbrot=new TurboMandelCanvas(32);
    JComboBox iterations=new JComboBox();
    private JCheckBox optimize = new JCheckBox("Optimize display");
    private JButton reset=new JButton("Reset");
    private JButton about_button=new JButton("About");

    /**
     * put your documentation comment here
     */
    public void init () {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * put your documentation comment here
     */
    public void start () {
        plane_area = new DimensionFloat();
        plane_area.xr0 = -2.0;
        plane_area.yr0 = 2.0;
        plane_area.xr1 = 2.;
        plane_area.yr1 = -2.;
        Mandelbrot.start_computation(plane_area);
    }

    /**
     * put your documentation comment here
     */
    public void stop () {
        Mandelbrot.kill_computation();
    }

    private class MyListener
            implements MouseMotionListener, MouseListener {

        /**
         * put your documentation comment here
         * @param ev
         */
        public void mousePressed (MouseEvent ev) {
            if (Mandelbrot.IsMandeling())
                return;
            Mandelbrot.AppuiSouris(ev.getX(), ev.getY());
        }

        /**
         * put your documentation comment here
         * @param ev
         */
        public void mouseClicked (MouseEvent ev) {}

        /**
         * put your documentation comment here
         * @param ev
         */
        public void mouseEntered (MouseEvent ev) {}

        /**
         * put your documentation comment here
         * @param ev
         */
        public void mouseExited (MouseEvent ev) {}

        /**
         * put your documentation comment here
         * @param ev
         */
        public void mouseMoved (MouseEvent ev) {}

        /**
         * put your documentation comment here
         * @param ev
         */
        public void mouseDragged (MouseEvent ev) {
            if (Mandelbrot.IsMandeling())
                return;
            Mandelbrot.DeplaceSouris(ev.getX(), ev.getY());
        }

        /**
         * put your documentation comment here
         * @param ev
         */
        public void mouseReleased (MouseEvent ev) {
            if (Mandelbrot.IsMandeling())
                return;
            DimensionFloat res;
            res = Mandelbrot.RelacheSouris(ev.getX(), ev.getY());
            if (res != plane_area) {
                plane_area = res;
                Mandelbrot.start_computation(res);
            }
        }
    }

    /**
     * put your documentation comment here
     */
    public void clickedReset () {
        Mandelbrot.kill_computation();
        plane_area = new DimensionFloat();
        plane_area.xr0 = -2.0;
        plane_area.yr0 = 2.0;
        plane_area.xr1 = 2.0;
        plane_area.yr1 = -2.0;
        Mandelbrot.start_computation(plane_area);
    }

    /**
     * put your documentation comment here
     */
    public void clickedAbout () {
        if (theAboutForm.isVisible() == false) {
            String Message = "Written by Olivier REFALO in 1996\n" + "refalo@mygale.org\n\n"
                    + "This little applet actually does quite a\n" + "few nifty things. In addition to drawing\n"
                    + "the Mandelbrot set with a quite fast\n" + "home made algorithm, the applet is\n"
                    + "double-buffered (no flicker) and multithreaded.\n" +
                    "One thread is solely responsible for drawing\n" + "the Mandelbrot in an offscreen buffer while\n"
                    + "the main thread handles the UI.\n\n" + "The code was originally written in C/C++ for Linux/X11 and then\n"
                    + "ported to Java.\n\n" + "Current Status: ";
            if (Mandelbrot.temps_calcule == 0)
                Message += "Drawing Mandelbrot\n";
            else
                Message += "Mandelbrot drawing phaze took " + Mandelbrot.temps_calcule/1000.
                        + " seconds\n";
            theAboutForm.setTexte(Message);
            theAboutForm.show();
        }
    }

    /**
     * put your documentation comment here
     * @param arg[]
     */
    static public void main (String arg[]) {
        JFrame f = new JFrame();
        f.addWindowListener(new java.awt.event.WindowAdapter() {

            /**
             * put your documentation comment here
             * @param evt
             */
            public void windowClosing (java.awt.event.WindowEvent evt) {
                System.exit(0);
            }
        });
        TurboMandel tm = new TurboMandel();
        tm.init();
        f.getContentPane().add(tm);
        f.pack();
        f.show();
        tm.start();
    }

    /**
     * put your documentation comment here
     */
    public TurboMandel () {
    }

    /**
     * put your documentation comment here
     * @exception Exception
     */
    private void jbInit () throws Exception {
        getContentPane().setLayout(new BorderLayout());
    optimize.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        optimize_itemStateChanged(e);
      }
    });
    getContentPane().add("South", controls);

        getContentPane().add("Center", Mandelbrot);

        iterations.setEditable(true);
        iterations.addItem("100");
        iterations.addItem("200");
        iterations.addItem("400");
        iterations.addItem("800");
        iterations.addItem("1600");
        iterations.addItem("3200");
        iterations.addItem("7000");
        iterations.addItem("10000");
        controls.add(iterations);

        iterations.addItemListener(new ItemListener() {
            public void itemStateChanged (ItemEvent e) {
                Mandelbrot.setIterations(Integer.parseInt((String)iterations.getSelectedItem()));
            }
        });

        controls.add(optimize);

        reset.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                clickedReset();
            }
        });
        controls.add(reset);

        about_button.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                clickedAbout();
            }
        });
        controls.add(about_button);

        MyListener myListener = new MyListener();
        Mandelbrot.addMouseListener(myListener);
        Mandelbrot.addMouseMotionListener(myListener);
    }

  void optimize_itemStateChanged(ItemEvent e) {
   Mandelbrot.setOptimize(e.getStateChange() == ItemEvent.SELECTED);
  }
}