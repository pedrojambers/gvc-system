/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.gvc.telas;

import br.com.gvc.dal.ModuloConexao;
import java.sql.*;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Pedro
 */
public class TelaInternaUsuarios extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    private static TelaInternaUsuarios telaInternaUsuarios;

    public static TelaInternaUsuarios getInstancia() {
        if (telaInternaUsuarios == null) {
            telaInternaUsuarios = new TelaInternaUsuarios();
        }
        return telaInternaUsuarios;

    }

    public TelaInternaUsuarios() {
        initComponents();
        conexao = ModuloConexao.conector();

    }

    private void buscar() {
        String sql = "select * from tb_usuarios where id_usuario=?";
        try {
            ps = conexao.prepareStatement(sql);
            ps.setString(1, txt_id.getText());
            rs = ps.executeQuery();
            if (rs.next()) {
                txt_nome.setText(rs.getString(2));
                txt_cel.setText(rs.getString(3));
                txt_login.setText(rs.getString(4));
                txt_senha.setText(rs.getString(5));
                txt_email.setText(rs.getString(6));

            } else {
                JOptionPane.showMessageDialog(null, "Usuário não cadastrado!");
                limparCampos();

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    private void adicionar() {
        String sql = "insert into tb_usuarios(nome_usuario, "
                + "fone_usuario, login_usuario, senha_usuario, email_usuario) "
                + " values(?, ?, ?, ?, ?)";

        try {
            ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, txt_nome.getText());
            ps.setString(2, txt_cel.getText().replaceAll("[()-]", ""));
            ps.setString(3, txt_login.getText());
            ps.setString(4, txt_senha.getText());
            ps.setString(5, txt_email.getText());

            if (txt_nome.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Defina o nome do Usuário!");
            } else if (txt_login.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Defina o login do Usuário!");
            } else if (txt_senha.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Defina a senha do Usuário!");
            } else {
                int adicionado = ps.executeUpdate();
                if (adicionado > 0) {
                    rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        txt_id.setText("" + id);

                    }
                    JOptionPane.showMessageDialog(null, "Usuário cadastrado!");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    private void editar() {
        String sql = "update tb_usuarios set nome_usuario = ?, fone_usuario = ?,"
                + "login_usuario = ?, senha_usuario = ?, email_usuario = ? where id_usuario = ?";
        try {
            ps = conexao.prepareStatement(sql);
            ps.setString(1, txt_nome.getText());
            ps.setString(2, txt_cel.getText().replaceAll("[()-]", ""));
            ps.setString(3, txt_login.getText());
            ps.setString(4, txt_senha.getText());
            ps.setString(5, txt_email.getText());
            ps.setString(6, txt_id.getText());

            if (txt_nome.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Defina o nome do Usuário!");
            } else if (txt_login.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Defina o login do Usuário!");
            } else if (txt_senha.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Defina a senha do Usuário!");
            } else {
                int adicionado = ps.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Usuário editado!");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    private void remover() {
        int confirma = JOptionPane.showConfirmDialog(null, "Deseja realmente remover?", "Atenção!", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from tb_usuarios where id_usuario = ?";
            try {
                ps = conexao.prepareStatement(sql);
                ps.setString(1, txt_id.getText());
                int apagado = ps.executeUpdate();
                if (apagado > 0) {
                    pesquisar();
                    JOptionPane.showMessageDialog(null, "Usuário removido!");
                    limparCampos();

                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    private void pesquisar() {
        String sql = "select id_usuario as ID, nome_usuario as Nome, fone_usuario as Telefone,"
                + "login_usuario as Login, senha_usuario as Senha, email_usuario as Email"
                + " from tb_usuarios where nome_usuario like ?";

        try {
            ps = conexao.prepareStatement(sql);
            ps.setString(1, txt_pesquisaUsuario.getText() + "%");
            rs = ps.executeQuery();

            tbl_usuarios.setModel(DbUtils.resultSetToTableModel(rs));
            //tbl_usuarios.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
    }

    public void setar_campos() {
        int setar = tbl_usuarios.getSelectedRow();
        txt_id.setText(tbl_usuarios.getModel().getValueAt(setar, 0).toString());
        txt_nome.setText(tbl_usuarios.getModel().getValueAt(setar, 1).toString());
        txt_cel.setText(tbl_usuarios.getModel().getValueAt(setar, 2).toString());
        txt_login.setText(tbl_usuarios.getModel().getValueAt(setar, 3).toString());
        txt_senha.setText(tbl_usuarios.getModel().getValueAt(setar, 4).toString());
        txt_email.setText(tbl_usuarios.getModel().getValueAt(setar, 5).toString());

        btn_salvar.setEnabled(false);
    }

    private void limparCampos() {
        txt_id.setText(null);
        txt_nome.setText(null);
        txt_cel.setText(null);
        txt_login.setText(null);
        txt_senha.setText(null);
        txt_email.setText(null);

        btn_salvar.setEnabled(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg_tipo = new javax.swing.ButtonGroup();
        id = new javax.swing.JLabel();
        nome = new javax.swing.JLabel();
        txt_nome = new javax.swing.JTextField();
        txt_login = new javax.swing.JTextField();
        login = new javax.swing.JLabel();
        txt_id = new javax.swing.JFormattedTextField();
        celular = new javax.swing.JLabel();
        txt_cel = new javax.swing.JFormattedTextField();
        btn_salvar = new javax.swing.JButton();
        btn_editar = new javax.swing.JButton();
        btn_remover = new javax.swing.JButton();
        pesquisar = new javax.swing.JLabel();
        txt_pesquisaUsuario = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_usuarios = new javax.swing.JTable();
        btn_buscaCep1 = new javax.swing.JButton();
        btn_remover1 = new javax.swing.JButton();
        icon = new javax.swing.JLabel();
        senha = new javax.swing.JLabel();
        txt_senha = new javax.swing.JPasswordField();
        email = new javax.swing.JLabel();
        txt_email = new javax.swing.JTextField();

        setBorder(null);
        setClosable(true);
        setForeground(new java.awt.Color(240, 240, 240));
        setTitle("Usuários");
        setFrameIcon(null);
        setOpaque(true);
        setPreferredSize(new java.awt.Dimension(800, 690));
        setVisible(false);

        id.setText("ID");

        nome.setText("Nome");

        txt_nome.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_nomeFocusGained(evt);
            }
        });
        txt_nome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_nomeActionPerformed(evt);
            }
        });

        txt_login.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_loginFocusGained(evt);
            }
        });

        login.setText("Login");

        txt_id.setFocusTraversalKeysEnabled(false);
        txt_id.setEnabled(false);
        txt_id.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_idFocusGained(evt);
            }
        });
        txt_id.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_idKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_idKeyTyped(evt);
            }
        });

        celular.setText("Celular");

        try {
            txt_cel.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##)#####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txt_cel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_celFocusGained(evt);
            }
        });

        btn_salvar.setBackground(new java.awt.Color(230, 230, 230));
        btn_salvar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_salvar.setForeground(new java.awt.Color(0, 153, 51));
        btn_salvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gvcsystem/img/salvar.png"))); // NOI18N
        btn_salvar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btn_salvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_salvarActionPerformed(evt);
            }
        });

        btn_editar.setBackground(new java.awt.Color(230, 230, 230));
        btn_editar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_editar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gvcsystem/img/editar.png"))); // NOI18N
        btn_editar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btn_editar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_editarActionPerformed(evt);
            }
        });

        btn_remover.setBackground(new java.awt.Color(230, 230, 230));
        btn_remover.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_remover.setForeground(new java.awt.Color(255, 0, 0));
        btn_remover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gvcsystem/img/excluir.png"))); // NOI18N
        btn_remover.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btn_remover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_removerActionPerformed(evt);
            }
        });

        pesquisar.setText("Pesquisar");

        txt_pesquisaUsuario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_pesquisaUsuarioKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_pesquisaUsuarioKeyReleased(evt);
            }
        });

        tbl_usuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome", "Telefone", "Login", "Senha", "Email"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_usuarios.setColumnSelectionAllowed(true);
        tbl_usuarios.getTableHeader().setReorderingAllowed(false);
        tbl_usuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_usuariosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_usuarios);
        tbl_usuarios.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        btn_buscaCep1.setText("🔎");
        btn_buscaCep1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                btn_buscaCep1FocusGained(evt);
            }
        });
        btn_buscaCep1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscaCep1ActionPerformed(evt);
            }
        });
        btn_buscaCep1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btn_buscaCep1KeyPressed(evt);
            }
        });

        btn_remover1.setBackground(new java.awt.Color(230, 230, 230));
        btn_remover1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_remover1.setForeground(new java.awt.Color(51, 51, 255));
        btn_remover1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gvcsystem/img/novo.png"))); // NOI18N
        btn_remover1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btn_remover1.setMaximumSize(new java.awt.Dimension(89, 25));
        btn_remover1.setMinimumSize(new java.awt.Dimension(89, 25));
        btn_remover1.setPreferredSize(new java.awt.Dimension(89, 25));
        btn_remover1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_remover1ActionPerformed(evt);
            }
        });

        icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gvcsystem/img/gvc_system_logo_login.png"))); // NOI18N

        senha.setText("Senha");

        email.setText("Email");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(id)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txt_id, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(nome)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_nome, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(celular)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_cel, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(email)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_email))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(login)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_login, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(senha)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_senha))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(pesquisar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txt_pesquisaUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btn_buscaCep1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(btn_remover1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(btn_salvar, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(btn_editar, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(btn_remover, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(89, 89, 89)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(82, 82, 82)
                        .addComponent(icon)
                        .addGap(250, 250, 250))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 771, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(id)
                            .addComponent(nome)
                            .addComponent(txt_nome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(login)
                            .addComponent(txt_login, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(senha)
                            .addComponent(txt_senha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(celular)
                            .addComponent(txt_cel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(email)
                            .addComponent(txt_email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_remover, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_editar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_salvar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_remover1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(icon)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pesquisar)
                    .addComponent(txt_pesquisaUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_buscaCep1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(244, Short.MAX_VALUE))
        );

        setBounds(0, 0, 800, 690);
    }// </editor-fold>//GEN-END:initComponents


    private void txt_nomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_nomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_nomeActionPerformed

    private void txt_idKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_idKeyTyped
        char c = evt.getKeyChar();
        if (c < '0' || c > '9') {
            evt.consume();
        }

    }//GEN-LAST:event_txt_idKeyTyped

    private void txt_idFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_idFocusGained
        // TODO add your handling code here:

    }//GEN-LAST:event_txt_idFocusGained

    private void txt_nomeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_nomeFocusGained
        // TODO add your handling code here:
        txt_nome.selectAll();
    }//GEN-LAST:event_txt_nomeFocusGained

    private void txt_loginFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_loginFocusGained
        // TODO add your handling code here:
        txt_login.selectAll();
    }//GEN-LAST:event_txt_loginFocusGained

    private void txt_celFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_celFocusGained
        // TODO add your handling code here:

    }//GEN-LAST:event_txt_celFocusGained

    private void btn_editarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_editarActionPerformed
        // TODO add your handling code here:
        editar();
        pesquisar();
    }//GEN-LAST:event_btn_editarActionPerformed

    private void txt_idKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_idKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            buscar();

        }

    }//GEN-LAST:event_txt_idKeyPressed

    private void btn_salvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salvarActionPerformed
        adicionar();

    }//GEN-LAST:event_btn_salvarActionPerformed

    private void btn_removerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_removerActionPerformed
        // TODO add your handling code here:
        remover();
    }//GEN-LAST:event_btn_removerActionPerformed

    private void btn_buscaCep1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btn_buscaCep1FocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_buscaCep1FocusGained

    private void btn_buscaCep1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscaCep1ActionPerformed
        // TODO add your handling code here:
        pesquisar();
    }//GEN-LAST:event_btn_buscaCep1ActionPerformed

    private void btn_buscaCep1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btn_buscaCep1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_buscaCep1KeyPressed

    private void btn_remover1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_remover1ActionPerformed
        // TODO add your handling code here:
        limparCampos();
    }//GEN-LAST:event_btn_remover1ActionPerformed

    private void txt_pesquisaUsuarioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pesquisaUsuarioKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_pesquisaUsuarioKeyPressed

    private void txt_pesquisaUsuarioKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pesquisaUsuarioKeyReleased
        // TODO add your handling code here:
        pesquisar();
    }//GEN-LAST:event_txt_pesquisaUsuarioKeyReleased

    private void tbl_usuariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_usuariosMouseClicked
        // TODO add your handling code here:
        limparCampos();
        setar_campos();
    }//GEN-LAST:event_tbl_usuariosMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bg_tipo;
    private javax.swing.JButton btn_buscaCep1;
    private javax.swing.JButton btn_editar;
    private javax.swing.JButton btn_remover;
    private javax.swing.JButton btn_remover1;
    private javax.swing.JButton btn_salvar;
    private javax.swing.JLabel celular;
    private javax.swing.JLabel email;
    private javax.swing.JLabel icon;
    private javax.swing.JLabel id;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel login;
    private javax.swing.JLabel nome;
    private javax.swing.JLabel pesquisar;
    private javax.swing.JLabel senha;
    private javax.swing.JTable tbl_usuarios;
    private javax.swing.JFormattedTextField txt_cel;
    private javax.swing.JTextField txt_email;
    private javax.swing.JFormattedTextField txt_id;
    private javax.swing.JTextField txt_login;
    private javax.swing.JTextField txt_nome;
    private javax.swing.JTextField txt_pesquisaUsuario;
    private javax.swing.JPasswordField txt_senha;
    // End of variables declaration//GEN-END:variables
}
