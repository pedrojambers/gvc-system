/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.gvc.telas;

import br.com.gvc.dal.ModuloConexao;
import java.sql.*;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.sql.Connection;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Pedro
 */
public class TelaInternaProdutos extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    private static TelaInternaProdutos telaInternaProdutos;

    public static TelaInternaProdutos getInstancia() {
        if (telaInternaProdutos == null) {
            telaInternaProdutos = new TelaInternaProdutos();
        }
        return telaInternaProdutos;

    }

    public TelaInternaProdutos() {
        initComponents();
        conexao = ModuloConexao.conector();
        
        txt_nome.requestFocusInWindow();

    }


    private void adicionar() {
        String sql = "insert into tb_produtos(nome_produto, acabamento_produto,"
                + "rolo_produto, gramatura_produto, peso_produto, preco_produto) "
                + "values(?, ?, ?, ?, ?, ?)";

        try {
            ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, txt_nome.getText());
            ps.setString(2, txt_acabamento.getText());
            ps.setString(3, txt_rolo.getText().replace(",", "."));
            ps.setString(4, txt_gramatura.getText().replace(",", "."));
            ps.setString(5, txt_peso.getText().replace(",", "."));
            ps.setString(6, txt_preco.getText().replace(",", "."));

            if (txt_nome.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha a Descrição do Produto!");
            } else {
                int adicionado = ps.executeUpdate();
                if (adicionado > 0) {

                    rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        txt_id.setText("" + id);
                        pesquisar();
                        limparCampos();

                    }
                    JOptionPane.showMessageDialog(null, "Produto cadastrado!");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    private void editar() {
        String sql = "update tb_produtos set nome_produto = ?, "
                + "acabamento_produto = ?, rolo_produto = ?, gramatura_produto = ?, "
                + "peso_produto = ?, preco_produto = ? where id_produto = ?";
        try {
            ps = conexao.prepareStatement(sql);
            ps.setString(1, txt_nome.getText());
            ps.setString(2, txt_acabamento.getText());
            ps.setString(3, txt_rolo.getText().replace(",", "."));
            ps.setString(4, txt_gramatura.getText().replace(",", "."));
            ps.setString(5, txt_peso.getText().replace(",", "."));
            ps.setString(6, txt_preco.getText().replace(",", "."));
            ps.setString(7, txt_id.getText());
            

            if (txt_id.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha o ID do Produto!");
            } else if (txt_nome.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha o nome do Produto!");
            } else {
                int adicionado = ps.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Produto editado!");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    private void remover() {
        int confirma = JOptionPane.showConfirmDialog(null, "Deseja realmente remover?", "Atenção!", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from tb_produtos where id_produto = ?";
            try {
                ps = conexao.prepareStatement(sql);
                ps.setString(1, txt_id.getText());
                int apagado = ps.executeUpdate();
                if (apagado > 0) {
                    pesquisar();
                    JOptionPane.showMessageDialog(null, "Produto removido!");
                    limparCampos();

                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    private void pesquisar() {
        String sql = "select id_produto as ID, nome_produto as Nome, acabamento_produto as Acabamento, "
                + "rolo_produto as TamRolo, gramatura_produto as Gramatura, "
                + "peso_produto as Peso, preco_produto as Preço "
                + "from tb_produtos where nome_produto like ?";

        try {
            ps = conexao.prepareStatement(sql);
            ps.setString(1, txt_pesquisaProdutos.getText() + "%");
            rs = ps.executeQuery();

            tbl_produtos.setModel(DbUtils.resultSetToTableModel(rs));

            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
            for(int i = 3; i <=6; i++){
                tbl_produtos.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
            }
            
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
    }

    public void setar_campos() {
        int setar = tbl_produtos.getSelectedRow();
        txt_id.setText(tbl_produtos.getModel().getValueAt(setar, 0).toString());
        txt_nome.setText(tbl_produtos.getModel().getValueAt(setar, 1).toString());
        txt_acabamento.setText(tbl_produtos.getModel().getValueAt(setar, 2).toString());       
        txt_rolo.setText(tbl_produtos.getModel().getValueAt(setar, 3).toString().replace(".", ","));
        txt_gramatura.setText(tbl_produtos.getModel().getValueAt(setar, 4).toString().replace(".", ","));
        txt_peso.setText(tbl_produtos.getModel().getValueAt(setar, 5).toString().replace(".", ","));
        txt_preco.setText(tbl_produtos.getModel().getValueAt(setar, 6).toString().replace(".", ","));
        
        btn_salvar.setEnabled(false);
    }

    private void limparCampos() {
        txt_id.setText(null);
        txt_nome.setText(null);
        txt_acabamento.setText(null);
        txt_rolo.setText(null);
        txt_gramatura.setText(null);
        txt_peso.setText(null);
        txt_preco.setText(null);
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
        telefone = new javax.swing.JLabel();
        cnpj = new javax.swing.JLabel();
        txt_nome = new javax.swing.JTextField();
        txt_acabamento = new javax.swing.JTextField();
        razao = new javax.swing.JLabel();
        txt_id = new javax.swing.JFormattedTextField();
        txt_preco = new javax.swing.JTextField();
        cel = new javax.swing.JLabel();
        numero = new javax.swing.JLabel();
        txt_peso = new javax.swing.JTextField();
        btn_salvar = new javax.swing.JButton();
        btn_editar = new javax.swing.JButton();
        btn_remover = new javax.swing.JButton();
        pesquisar = new javax.swing.JLabel();
        txt_pesquisaProdutos = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_produtos = new javax.swing.JTable();
        btn_buscaCep1 = new javax.swing.JButton();
        btn_remover1 = new javax.swing.JButton();
        icon = new javax.swing.JLabel();
        txt_gramatura = new javax.swing.JTextField();
        txt_rolo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setBorder(null);
        setClosable(true);
        setForeground(new java.awt.Color(240, 240, 240));
        setTitle("Produtos");
        setFrameIcon(null);
        setOpaque(true);
        setPreferredSize(new java.awt.Dimension(800, 690));
        setVisible(false);

        id.setText("ID");

        nome.setText("Descrição");

        telefone.setText("Peso");

        cnpj.setText("Tam. Rolo");

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

        txt_acabamento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_acabamentoFocusGained(evt);
            }
        });
        txt_acabamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_acabamentoActionPerformed(evt);
            }
        });

        razao.setText("Acabamento");

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

        txt_preco.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_preco.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_precoFocusGained(evt);
            }
        });

        cel.setText("Gramatura");

        numero.setText("Preço (R$)");

        txt_peso.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_peso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_pesoActionPerformed(evt);
            }
        });
        txt_peso.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_pesoKeyTyped(evt);
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
        btn_editar.setToolTipText("");
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

        txt_pesquisaProdutos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_pesquisaProdutosKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_pesquisaProdutosKeyReleased(evt);
            }
        });

        tbl_produtos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Descrição", "Acabamento", "Tam. Rolo (m)", "Gramatura (g/m2)", "Peso (g)", "Preço (R$)"
            }
        ));
        tbl_produtos.setColumnSelectionAllowed(true);
        tbl_produtos.getTableHeader().setReorderingAllowed(false);
        tbl_produtos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_produtosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_produtos);
        tbl_produtos.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

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

        txt_gramatura.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txt_rolo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_rolo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_roloActionPerformed(evt);
            }
        });

        jLabel2.setText("g/m2");

        jLabel1.setText("m");

        jLabel3.setText("g");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(razao, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txt_acabamento))
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(cnpj)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txt_rolo))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(telefone)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txt_peso, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel3)
                                            .addGap(0, 29, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addGap(0, 0, 0)
                                            .addComponent(jLabel1)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(cel)
                                        .addComponent(numero))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(txt_gramatura, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jLabel2))
                                        .addComponent(txt_preco))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(id)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(16, 16, 16)
                                        .addComponent(txt_id, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(nome)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_nome, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(116, 116, 116)
                        .addComponent(btn_remover1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_salvar, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_editar, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_remover, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(icon)
                .addGap(91, 91, 91))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 771, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pesquisar)
                        .addGap(5, 5, 5)
                        .addComponent(txt_pesquisaProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_buscaCep1)))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(id))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txt_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_nome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(nome)))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(razao)
                            .addComponent(txt_acabamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cnpj)
                            .addComponent(cel)
                            .addComponent(txt_gramatura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_rolo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(numero)
                            .addComponent(telefone)
                            .addComponent(txt_peso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_preco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btn_remover1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_salvar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_editar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_remover, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(icon))
                .addGap(53, 53, 53)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(pesquisar))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(txt_pesquisaProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btn_buscaCep1))
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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

    private void txt_acabamentoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_acabamentoFocusGained
        // TODO add your handling code here:
        txt_acabamento.selectAll();
    }//GEN-LAST:event_txt_acabamentoFocusGained

    private void txt_precoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_precoFocusGained
        // TODO add your handling code here:
        txt_preco.selectAll();
    }//GEN-LAST:event_txt_precoFocusGained

    private void txt_pesoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_pesoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_pesoActionPerformed

    private void txt_pesoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pesoKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();
        if (c < '0' || c > '9')
            evt.consume();
    }//GEN-LAST:event_txt_pesoKeyTyped

    private void btn_editarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_editarActionPerformed
        // TODO add your handling code here:
        editar();
        pesquisar();
    }//GEN-LAST:event_btn_editarActionPerformed

    private void txt_idKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_idKeyPressed


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

    private void txt_pesquisaProdutosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pesquisaProdutosKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_pesquisaProdutosKeyPressed

    private void txt_pesquisaProdutosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pesquisaProdutosKeyReleased
        // TODO add your handling code here:
        pesquisar();
    }//GEN-LAST:event_txt_pesquisaProdutosKeyReleased

    private void tbl_produtosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_produtosMouseClicked
        // TODO add your handling code here:
        limparCampos();
        setar_campos();
    }//GEN-LAST:event_tbl_produtosMouseClicked

    private void txt_acabamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_acabamentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_acabamentoActionPerformed

    private void txt_roloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_roloActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_roloActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bg_tipo;
    private javax.swing.JButton btn_buscaCep1;
    private javax.swing.JButton btn_editar;
    private javax.swing.JButton btn_remover;
    private javax.swing.JButton btn_remover1;
    private javax.swing.JButton btn_salvar;
    private javax.swing.JLabel cel;
    private javax.swing.JLabel cnpj;
    private javax.swing.JLabel icon;
    private javax.swing.JLabel id;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel nome;
    private javax.swing.JLabel numero;
    private javax.swing.JLabel pesquisar;
    private javax.swing.JLabel razao;
    private javax.swing.JTable tbl_produtos;
    private javax.swing.JLabel telefone;
    private javax.swing.JTextField txt_acabamento;
    private javax.swing.JTextField txt_gramatura;
    private javax.swing.JFormattedTextField txt_id;
    private javax.swing.JTextField txt_nome;
    private javax.swing.JTextField txt_peso;
    private javax.swing.JTextField txt_pesquisaProdutos;
    private javax.swing.JTextField txt_preco;
    private javax.swing.JTextField txt_rolo;
    // End of variables declaration//GEN-END:variables
}
