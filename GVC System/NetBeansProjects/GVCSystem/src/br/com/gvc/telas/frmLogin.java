/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package br.com.gvc.telas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import br.com.gvc.dal.ModuloConexao;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.event.KeyEvent;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author Pedro
 */
public class frmLogin extends javax.swing.JFrame {

    Connection conexao = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    public void logar() {
        String sql = "select * from tb_usuarios where login_usuario = ? and senha_usuario = ?";
        try {
            ps = conexao.prepareStatement(sql);
            ps.setString(1, txt_usuario.getText());
            ps.setString(2, txt_senha.getText());

            rs = ps.executeQuery();

            if (rs.next()) {
                TelaPrincipal principal = new TelaPrincipal();
                principal.setVisible(true);
                this.dispose();
                conexao.close();

            } else {
                JOptionPane.showMessageDialog(null, "Usuário ou senha incorreto!", "Atenção!", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Creates new form frmLogin
     */
    public frmLogin() {
        initComponents();

        //Icone da janela
        //ImageIcon img = new ImageIcon("src/gvcsystem/img/icone_janela.png");
        //this.setIconImage(img.getImage());

        
        addPlaceholderStyle(txt_usuario);
        addPlaceholderStyle(txt_senha);

        conexao = ModuloConexao.conector();

        txt_usuario.requestFocusInWindow();
    }



    public void addPlaceholderStyle(JTextField textField) {
        Font font = textField.getFont();
        font = font.deriveFont(Font.PLAIN);
        textField.setFont(font);
        textField.setForeground(Color.gray);
    }

    public void removePlaceholderStyle(JTextField textField) {
        Font font = textField.getFont();
        font = font.deriveFont(Font.PLAIN);
        textField.setFont(font);
        textField.setForeground(Color.black);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txt_usuario = new javax.swing.JTextField();
        txt_senha = new javax.swing.JPasswordField();
        btn_login = new javax.swing.JButton();
        loginLabel = new javax.swing.JLabel();
        version = new javax.swing.JLabel();
        icon = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("GVC System - Login");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(555, 243));
        setResizable(false);
        setSize(new java.awt.Dimension(555, 243));
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });
        getContentPane().setLayout(null);

        txt_usuario.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        txt_usuario.setForeground(new java.awt.Color(153, 153, 153));
        txt_usuario.setText("Usuário...");
        txt_usuario.setCaretColor(new java.awt.Color(102, 102, 102));
        txt_usuario.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txt_usuario.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_usuarioFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_usuarioFocusLost(evt);
            }
        });
        txt_usuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_usuarioActionPerformed(evt);
            }
        });
        getContentPane().add(txt_usuario);
        txt_usuario.setBounds(300, 110, 180, 27);

        txt_senha.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        txt_senha.setForeground(new java.awt.Color(153, 153, 153));
        txt_senha.setText("Senha...");
        txt_senha.setToolTipText("");
        txt_senha.setCaretColor(new java.awt.Color(102, 102, 102));
        txt_senha.setEchoChar('\u0000');
        txt_senha.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_senhaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_senhaFocusLost(evt);
            }
        });
        txt_senha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_senhaActionPerformed(evt);
            }
        });
        txt_senha.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_senhaKeyPressed(evt);
            }
        });
        getContentPane().add(txt_senha);
        txt_senha.setBounds(300, 140, 180, 27);

        btn_login.setBackground(new java.awt.Color(71, 138, 201));
        btn_login.setFont(new java.awt.Font("Tahoma", 1, 17)); // NOI18N
        btn_login.setForeground(new java.awt.Color(255, 255, 255));
        btn_login.setText("Login");
        btn_login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_loginActionPerformed(evt);
            }
        });
        getContentPane().add(btn_login);
        btn_login.setBounds(300, 180, 180, 30);

        loginLabel.setFont(new java.awt.Font("NanamiRoundedW01-Bold", 1, 24)); // NOI18N
        loginLabel.setForeground(new java.awt.Color(102, 102, 102));
        loginLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        loginLabel.setText("GVC SYSTEM");
        getContentPane().add(loginLabel);
        loginLabel.setBounds(300, 20, 180, 60);

        version.setText("v1.23");
        getContentPane().add(version);
        version.setBounds(450, 60, 30, 16);

        icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gvcsystem/img/gvc_system_logo_login.png"))); // NOI18N
        getContentPane().add(icon);
        icon.setBounds(70, 20, 147, 201);

        setSize(new java.awt.Dimension(571, 282));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txt_usuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_usuarioActionPerformed


    }//GEN-LAST:event_txt_usuarioActionPerformed

    private void txt_usuarioFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_usuarioFocusGained

        txt_usuario.selectAll();

        if (txt_usuario.getText().equals("Usuário...")) {
            txt_usuario.setText("");
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    txt_usuario.selectAll();
                }
            });

            removePlaceholderStyle(txt_usuario);
        }
    }//GEN-LAST:event_txt_usuarioFocusGained

    private void txt_usuarioFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_usuarioFocusLost
        // TODO add your handling code here:

        if (txt_usuario.getText().equals("")) {
            txt_usuario.setText("Usuário...");

            addPlaceholderStyle(txt_usuario);
        }

    }//GEN-LAST:event_txt_usuarioFocusLost

    private void txt_senhaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_senhaFocusGained
        txt_senha.selectAll();

        if (txt_senha.getText().equals("Senha...")) {
            txt_senha.setText("");
            txt_senha.setEchoChar('●');
            txt_senha.requestFocus();
            removePlaceholderStyle(txt_senha);
        }
    }//GEN-LAST:event_txt_senhaFocusGained

    private void txt_senhaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_senhaFocusLost
        // TODO add your handling code here:
        if (txt_senha.getText().equals("")) {
            txt_senha.setText("Senha...");
            txt_senha.setEchoChar('\u0000');

            addPlaceholderStyle(txt_senha);
        }
    }//GEN-LAST:event_txt_senhaFocusLost

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        // TODO add your handling code here:
        this.requestFocusInWindow();
    }//GEN-LAST:event_formWindowGainedFocus

    private void btn_loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_loginActionPerformed
        // TODO add your handling code here:
        logar();
    }//GEN-LAST:event_btn_loginActionPerformed

    private void txt_senhaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_senhaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            logar();
        }

    }//GEN-LAST:event_txt_senhaKeyPressed

    private void txt_senhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_senhaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_senhaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            FlatLightLaf.install();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmLogin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_login;
    private javax.swing.JLabel icon;
    private javax.swing.JLabel loginLabel;
    private javax.swing.JPasswordField txt_senha;
    private javax.swing.JTextField txt_usuario;
    private javax.swing.JLabel version;
    // End of variables declaration//GEN-END:variables
}