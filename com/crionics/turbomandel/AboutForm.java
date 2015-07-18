/*
 * put your module comment here
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */


package  com.crionics.turbomandel;

import  java.awt.*;
import  javax.swing.*;


/**
 * put your documentation comment here
 */
public class AboutForm extends JFrame {
    private BorderLayout borderLayout1 = new BorderLayout();
    JScrollPane sp = new JScrollPane();
    JTextArea text = new JTextArea();

    /**
     * put your documentation comment here
     */
    public AboutForm () {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * put your documentation comment here
     * @param txt
     */
    public void setTexte (String txt) {
        text.setText(txt);
    }

    /**
     * put your documentation comment here
     * @exception Exception
     */
    private void jbInit () throws Exception {
        this.setTitle("About");
        getContentPane().setLayout(borderLayout1);
        //        Texte.setEditable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {

            /**
             * put your documentation comment here
             * @param evt
             */
            public void windowClosing (java.awt.event.WindowEvent evt) {
                setVisible(false);
            }
        });
        //  setSize(200, 150);
        text.setText("jTextArea1");
        sp.setPreferredSize(new Dimension(350, 300));
        this.getContentPane().add(sp, BorderLayout.CENTER);
        sp.getViewport().add(text, null);
        pack();
    }
}



