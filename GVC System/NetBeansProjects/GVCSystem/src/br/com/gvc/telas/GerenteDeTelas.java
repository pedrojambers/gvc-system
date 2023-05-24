/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.gvc.telas;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseMotionListener;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.plaf.basic.BasicInternalFrameUI;

/**
 *
 * @author Pedro
 */
public class GerenteDeTelas {
    
    private static JDesktopPane jDesktopPane;
    
    public GerenteDeTelas(JDesktopPane jDesktopPane) {
        GerenteDeTelas.jDesktopPane = jDesktopPane;
    }
    
    public void abrirJanelas(JInternalFrame jInternalFrame) {

        if(jInternalFrame.isVisible()){
            jInternalFrame.toFront();
            jInternalFrame.requestFocus();
            
            
        } else {
            jDesktopPane.add(jInternalFrame);
            jInternalFrame.setVisible(true);
            jInternalFrame.setLocation(0, 0);
            
            //Deixar a janela interna fixa
            BasicInternalFrameUI ui = (BasicInternalFrameUI) jInternalFrame.getUI();
            Component northPane = ui.getNorthPane();
            MouseMotionListener[] motionListeners = (MouseMotionListener[]) northPane.getListeners(MouseMotionListener.class);
            for (MouseMotionListener listener: motionListeners)
               northPane.removeMouseMotionListener(listener);
            jInternalFrame.getContentPane().setBackground(new java.awt.Color(230, 230, 230));

        }
        
    }
}
