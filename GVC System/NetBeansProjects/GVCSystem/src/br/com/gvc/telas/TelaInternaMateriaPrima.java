/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.gvc.telas;

import br.com.gvc.dal.ModuloConexao;
import java.sql.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Pedro
 */
public class TelaInternaMateriaPrima extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    private static TelaInternaMateriaPrima telaInternaMateriaPrima;

    public static TelaInternaMateriaPrima getInstancia() {
        if (telaInternaMateriaPrima == null) {
            telaInternaMateriaPrima = new TelaInternaMateriaPrima();
        }
        return telaInternaMateriaPrima;

    }

    public TelaInternaMateriaPrima() {
        initComponents();
        conexao = ModuloConexao.conector();
        
        txt_nome.requestFocusInWindow();

    }
    
    private void adicionar() {
        String sql = "insert into tb_materia(nome_materia, tipo_materia,"
                + "preco_materia, disponibilidade, descricao, nome_forn, id_forn) "
                + "values(?, ?, ?, ?, ?, ?, ?)";


        try {
            ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, txt_nome.getText());
            ps.setString(2, cb_tipo.getSelectedItem().toString());
            ps.setString(3, txt_preco.getText().replace(",", "."));
            ps.setString(4, cb_disp.getSelectedItem().toString());
            ps.setString(5, txt_descricao.getText());
            ps.setString(6, txt_fornecedor.getText());
            ps.setString(7, txt_id_forn.getText());

            if (txt_nome.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha o Nome!");
            } else {
                int adicionado = ps.executeUpdate();
                if (adicionado > 0) {

                    rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        txt_id.setText("" + id);

                    }
                    JOptionPane.showMessageDialog(null, "Cadastro concluído!");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    private void editar() {
        String sql = "update tb_materia set nome_materia = ?, "
                + "tipo_materia = ?, preco_materia = ?, disponibilidade = ?, "
                + "descricao = ?, nome_forn = ?, id_forn = ? where id_materia = ?";
        try {
            ps = conexao.prepareStatement(sql);
            ps.setString(1, txt_nome.getText());
            ps.setString(2, cb_tipo.getSelectedItem().toString());
            ps.setString(3, txt_preco.getText().replace(",", "."));
            ps.setString(4, cb_disp.getSelectedItem().toString());
            ps.setString(5, txt_descricao.getText());
            ps.setString(6, txt_fornecedor.getText());
            ps.setString(7, txt_id_forn.getText());
            ps.setString(8, txt_id.getText());
            

            if (txt_id.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha o ID!");
            } else if (txt_nome.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha o nome!");
            } else {
                int adicionado = ps.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Edição concluida!");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    private void remover() {
        int confirma = JOptionPane.showConfirmDialog(null, "Deseja realmente remover?", "Atenção!", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from tb_materia where id_materia = ?";
            try {
                ps = conexao.prepareStatement(sql);
                ps.setString(1, txt_id.getText());
                int apagado = ps.executeUpdate();
                if (apagado > 0) {
                    pesquisar();
                    JOptionPane.showMessageDialog(null, "Removido!");
                    limparCampos();

                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    private void pesquisar() {
        String sql = "select id_materia as ID, nome_Materia as Nome, tipo_materia as Tipo, "
                + "preco_materia as Preco, disponibilidade as Disponibilidade, "
                + "descricao as Descricao, nome_forn as Fornecedor, id_forn as Id_fornecedor "
                + "from tb_materia where nome_materia like ?";

        try {
            ps = conexao.prepareStatement(sql);
            ps.setString(1, txt_pesquisaMateria.getText() + "%");
            rs = ps.executeQuery();

            tbl_materia.setModel(DbUtils.resultSetToTableModel(rs));

            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
            tbl_materia.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
    }

    public void setar_campos() {
        int setar = tbl_materia.getSelectedRow();
        txt_id.setText(tbl_materia.getModel().getValueAt(setar, 0).toString());
        txt_nome.setText(tbl_materia.getModel().getValueAt(setar, 1).toString());
        cb_tipo.setSelectedItem(tbl_materia.getModel().getValueAt(setar, 2).toString());       
        txt_preco.setText(tbl_materia.getModel().getValueAt(setar, 3).toString());
        cb_disp.setSelectedItem(tbl_materia.getModel().getValueAt(setar, 4).toString());
        txt_descricao.setText(tbl_materia.getModel().getValueAt(setar, 5).toString());
        txt_fornecedor.setText(tbl_materia.getModel().getValueAt(setar, 6).toString());
        txt_id_forn.setText(tbl_materia.getModel().getValueAt(setar, 7).toString());
        
        btn_salvar.setEnabled(false);
    }

    private void limparCampos() {
        txt_id.setText(null);
        txt_nome.setText(null);
        cb_tipo.setSelectedIndex(0);
        txt_preco.setText(null);
        txt_fornecedor.setText(null);
        txt_descricao.setText(null);
        cb_disp.setSelectedIndex(0);
        btn_salvar.setEnabled(true);
    }
    
        private void pesquisar_forn() {
        String sql = "select id_forn as ID, nome_forn as Nome, "
                + "razao_forn as Razão_Social from tb_fornecedores where nome_forn like ?";

        try {
            ps = conexao.prepareStatement(sql);
            ps.setString(1, txt_pesquisa_fornecedor.getText() + "%");
            rs = ps.executeQuery();
            tbl_forn.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
        
    public void setar_campos_forn() {
        int setar = tbl_forn.getSelectedRow();
        txt_id_forn.setText(tbl_forn.getModel().getValueAt(setar, 0).toString());
        txt_fornecedor.setText(tbl_forn.getModel().getValueAt(setar, 2).toString());
        
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
        razao = new javax.swing.JLabel();
        txt_id = new javax.swing.JFormattedTextField();
        txt_preco = new javax.swing.JTextField();
        cel = new javax.swing.JLabel();
        numero = new javax.swing.JLabel();
        btn_salvar = new javax.swing.JButton();
        btn_editar = new javax.swing.JButton();
        btn_remover = new javax.swing.JButton();
        pesquisar = new javax.swing.JLabel();
        txt_pesquisaMateria = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_materia = new javax.swing.JTable();
        btn_remover1 = new javax.swing.JButton();
        txt_fornecedor = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cb_tipo = new javax.swing.JComboBox<>();
        txt_descricao = new javax.swing.JTextField();
        cb_disp = new javax.swing.JComboBox<>();
        jInternalFrame1 = new javax.swing.JInternalFrame();
        id1 = new javax.swing.JLabel();
        nome1 = new javax.swing.JLabel();
        txt_nome1 = new javax.swing.JTextField();
        razao1 = new javax.swing.JLabel();
        txt_id1 = new javax.swing.JFormattedTextField();
        txt_preco1 = new javax.swing.JTextField();
        cel1 = new javax.swing.JLabel();
        numero1 = new javax.swing.JLabel();
        btn_salvar1 = new javax.swing.JButton();
        btn_editar1 = new javax.swing.JButton();
        btn_remover2 = new javax.swing.JButton();
        pesquisar1 = new javax.swing.JLabel();
        txt_pesquisaProdutos1 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_produtos1 = new javax.swing.JTable();
        btn_buscaCep2 = new javax.swing.JButton();
        btn_remover3 = new javax.swing.JButton();
        icon1 = new javax.swing.JLabel();
        txt_fornecedor1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        txt_descricao1 = new javax.swing.JTextField();
        jComboBox4 = new javax.swing.JComboBox<>();
        btn_buscaCep3 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        txt_pesquisa_fornecedor = new javax.swing.JTextField();
        btn_busca_forn = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        tbl_forn = new javax.swing.JTable();
        txt_id_forn = new javax.swing.JFormattedTextField();
        id2 = new javax.swing.JLabel();

        setBorder(null);
        setClosable(true);
        setForeground(new java.awt.Color(240, 240, 240));
        setTitle("Matéria-prima");
        setFrameIcon(null);
        setOpaque(true);
        setPreferredSize(new java.awt.Dimension(800, 690));
        setVisible(false);

        id.setText("ID Forn.");

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

        razao.setText("Tipo:");

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

        cel.setText("Fornecedor");

        numero.setText("Preço");

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

        txt_pesquisaMateria.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_pesquisaMateriaKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_pesquisaMateriaKeyReleased(evt);
            }
        });

        tbl_materia.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome", "Tipo", "Preco", "Disponibilidade", "Descricao", "Fornecedor", "Id_fornecedor"
            }
        ));
        tbl_materia.setColumnSelectionAllowed(true);
        tbl_materia.getTableHeader().setReorderingAllowed(false);
        tbl_materia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_materiaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_materia);
        tbl_materia.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

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

        txt_fornecedor.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txt_fornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_fornecedorActionPerformed(evt);
            }
        });

        jLabel2.setText("Descrição");

        cb_tipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Adesivo", "Banner", "Tinta", "Solvente" }));

        txt_descricao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_descricaoActionPerformed(evt);
            }
        });

        cb_disp.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Disponível", "Indisponível" }));

        jInternalFrame1.setBorder(null);
        jInternalFrame1.setClosable(true);
        jInternalFrame1.setForeground(new java.awt.Color(240, 240, 240));
        jInternalFrame1.setTitle("Matéria-prima");
        jInternalFrame1.setFrameIcon(null);
        jInternalFrame1.setOpaque(true);
        jInternalFrame1.setPreferredSize(new java.awt.Dimension(800, 690));
        jInternalFrame1.setVisible(false);

        id1.setText("ID");

        nome1.setText("Nome");

        txt_nome1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_nome1FocusGained(evt);
            }
        });
        txt_nome1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_nome1ActionPerformed(evt);
            }
        });

        razao1.setText("Tipo");

        txt_id.setFocusTraversalKeysEnabled(false);
        txt_id1.setEnabled(false);
        txt_id1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_id1FocusGained(evt);
            }
        });
        txt_id1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_id1KeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_id1KeyTyped(evt);
            }
        });

        txt_preco1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_preco1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_preco1FocusGained(evt);
            }
        });

        cel1.setText("Fornecedor");

        numero1.setText("Preço");

        btn_salvar1.setBackground(new java.awt.Color(230, 230, 230));
        btn_salvar1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_salvar1.setForeground(new java.awt.Color(0, 153, 51));
        btn_salvar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gvcsystem/img/salvar.png"))); // NOI18N
        btn_salvar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btn_salvar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_salvar1ActionPerformed(evt);
            }
        });

        btn_editar1.setBackground(new java.awt.Color(230, 230, 230));
        btn_editar1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_editar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gvcsystem/img/editar.png"))); // NOI18N
        btn_editar1.setToolTipText("");
        btn_editar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btn_editar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_editar1ActionPerformed(evt);
            }
        });

        btn_remover2.setBackground(new java.awt.Color(230, 230, 230));
        btn_remover2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_remover2.setForeground(new java.awt.Color(255, 0, 0));
        btn_remover2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gvcsystem/img/excluir.png"))); // NOI18N
        btn_remover2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btn_remover2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_remover2ActionPerformed(evt);
            }
        });

        pesquisar1.setText("Pesquisar");

        txt_pesquisaProdutos1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_pesquisaProdutos1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_pesquisaProdutos1KeyReleased(evt);
            }
        });

        tbl_produtos1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Descrição", "Acabamento", "Tam. Rolo (m)", "Gramatura (g/m2)", "Peso (g)", "Preço (R$)"
            }
        ));
        tbl_produtos1.getTableHeader().setReorderingAllowed(false);
        tbl_produtos1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_produtos1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tbl_produtos1);
        tbl_produtos1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        btn_buscaCep2.setText("🔎");
        btn_buscaCep2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                btn_buscaCep2FocusGained(evt);
            }
        });
        btn_buscaCep2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscaCep2ActionPerformed(evt);
            }
        });
        btn_buscaCep2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btn_buscaCep2KeyPressed(evt);
            }
        });

        btn_remover3.setBackground(new java.awt.Color(230, 230, 230));
        btn_remover3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_remover3.setForeground(new java.awt.Color(51, 51, 255));
        btn_remover3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gvcsystem/img/novo.png"))); // NOI18N
        btn_remover3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btn_remover3.setMaximumSize(new java.awt.Dimension(89, 25));
        btn_remover3.setMinimumSize(new java.awt.Dimension(89, 25));
        btn_remover3.setPreferredSize(new java.awt.Dimension(89, 25));
        btn_remover3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_remover3ActionPerformed(evt);
            }
        });

        icon1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gvcsystem/img/gvc_system_logo_login.png"))); // NOI18N

        txt_fornecedor1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel3.setText("Descrição");

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Adesivo", "Banner", "Tinta", "Solvente" }));

        txt_descricao1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_descricao1ActionPerformed(evt);
            }
        });

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Disponível", "Indisponível" }));

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrame1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jInternalFrame1Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(id1)
                        .addGap(5, 5, 5)
                        .addComponent(txt_id1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(nome1)
                        .addGap(5, 5, 5)
                        .addComponent(txt_nome1, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jInternalFrame1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_descricao1))
                    .addGroup(jInternalFrame1Layout.createSequentialGroup()
                        .addGap(110, 110, 110)
                        .addComponent(btn_remover3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btn_salvar1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btn_editar1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btn_remover2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jInternalFrame1Layout.createSequentialGroup()
                        .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jInternalFrame1Layout.createSequentialGroup()
                                .addComponent(razao1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jInternalFrame1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cel1)
                                .addGap(7, 7, 7)
                                .addComponent(txt_fornecedor1))
                            .addGroup(jInternalFrame1Layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(numero1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_preco1)))))
                .addGap(70, 70, 70)
                .addComponent(icon1))
            .addGroup(jInternalFrame1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jInternalFrame1Layout.createSequentialGroup()
                        .addComponent(pesquisar1)
                        .addGap(5, 5, 5)
                        .addComponent(txt_pesquisaProdutos1, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_buscaCep2))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 771, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrame1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(icon1)
                    .addGroup(jInternalFrame1Layout.createSequentialGroup()
                        .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_id1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_nome1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jInternalFrame1Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(id1)
                                    .addComponent(nome1))))
                        .addGap(2, 2, 2)
                        .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_descricao1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(2, 2, 2)
                        .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(razao1))
                            .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txt_fornecedor1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cel1)))
                        .addGap(2, 2, 2)
                        .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_preco1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(numero1)
                            .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_remover3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_salvar1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_editar1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_remover2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(53, 53, 53)
                .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jInternalFrame1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(pesquisar1))
                    .addGroup(jInternalFrame1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(txt_pesquisaProdutos1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btn_buscaCep2))
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        btn_buscaCep3.setText("🔎");
        btn_buscaCep3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                btn_buscaCep3FocusGained(evt);
            }
        });
        btn_buscaCep3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscaCep3ActionPerformed(evt);
            }
        });
        btn_buscaCep3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btn_buscaCep3KeyPressed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Fornecedores"));

        txt_pesquisa_fornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_pesquisa_fornecedorActionPerformed(evt);
            }
        });
        txt_pesquisa_fornecedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_pesquisa_fornecedorKeyReleased(evt);
            }
        });

        btn_busca_forn.setText("🔎");
        btn_busca_forn.setFocusable(false);
        btn_busca_forn.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                btn_busca_fornFocusGained(evt);
            }
        });
        btn_busca_forn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_busca_fornMouseClicked(evt);
            }
        });
        btn_busca_forn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_busca_fornActionPerformed(evt);
            }
        });
        btn_busca_forn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btn_busca_fornKeyPressed(evt);
            }
        });

        tbl_forn.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome", "Razão_Social"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_forn.setFocusable(false);
        tbl_forn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_fornMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tbl_forn);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(txt_pesquisa_fornecedor, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(btn_busca_forn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_pesquisa_fornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_busca_forn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE))
        );

        txt_id.setFocusTraversalKeysEnabled(false);
        txt_id_forn.setEnabled(false);
        txt_id_forn.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_id_fornFocusGained(evt);
            }
        });
        txt_id_forn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_id_fornKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_id_fornKeyTyped(evt);
            }
        });

        id2.setText("ID");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(4, 4, 4)
                                                .addComponent(id2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txt_id, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(nome)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txt_nome))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(razao)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cb_tipo, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txt_descricao))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(cb_disp, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(56, 56, 56)
                                                        .addComponent(numero)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(txt_preco, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(cel)
                                                        .addGap(8, 8, 8)
                                                        .addComponent(txt_fornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(id)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(txt_id_forn)))
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                        .addGap(12, 12, 12))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(73, 73, 73)
                                        .addComponent(btn_remover1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(btn_salvar, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(btn_editar, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(btn_remover, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(36, 36, 36))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jInternalFrame1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(352, 352, 352))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pesquisar)
                        .addGap(5, 5, 5)
                        .addComponent(txt_pesquisaMateria, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_buscaCep3, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(id2)
                            .addComponent(nome)
                            .addComponent(txt_nome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txt_descricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cb_tipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(razao))
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txt_fornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cel))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txt_id_forn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(id)))
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txt_preco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(numero))
                            .addComponent(cb_disp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_remover1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_salvar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_editar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_remover, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(pesquisar))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(txt_pesquisaMateria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btn_buscaCep3))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jInternalFrame1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE))))
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

    private void txt_precoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_precoFocusGained
        // TODO add your handling code here:
        txt_preco.selectAll();
    }//GEN-LAST:event_txt_precoFocusGained

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

    private void btn_remover1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_remover1ActionPerformed
        // TODO add your handling code here:
        limparCampos();
    }//GEN-LAST:event_btn_remover1ActionPerformed

    private void txt_pesquisaMateriaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pesquisaMateriaKeyPressed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_txt_pesquisaMateriaKeyPressed

    private void txt_pesquisaMateriaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pesquisaMateriaKeyReleased
        // TODO add your handling code here:
        pesquisar();
    }//GEN-LAST:event_txt_pesquisaMateriaKeyReleased

    private void tbl_materiaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_materiaMouseClicked
        // TODO add your handling code here:
        limparCampos();
        setar_campos();
    }//GEN-LAST:event_tbl_materiaMouseClicked

    private void txt_descricaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_descricaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_descricaoActionPerformed

    private void txt_nome1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_nome1FocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_nome1FocusGained

    private void txt_nome1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_nome1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_nome1ActionPerformed

    private void txt_id1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_id1FocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_id1FocusGained

    private void txt_id1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_id1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_id1KeyPressed

    private void txt_id1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_id1KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_id1KeyTyped

    private void txt_preco1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_preco1FocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_preco1FocusGained

    private void btn_salvar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salvar1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_salvar1ActionPerformed

    private void btn_editar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_editar1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_editar1ActionPerformed

    private void btn_remover2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_remover2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_remover2ActionPerformed

    private void txt_pesquisaProdutos1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pesquisaProdutos1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_pesquisaProdutos1KeyPressed

    private void txt_pesquisaProdutos1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pesquisaProdutos1KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_pesquisaProdutos1KeyReleased

    private void tbl_produtos1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_produtos1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tbl_produtos1MouseClicked

    private void btn_buscaCep2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btn_buscaCep2FocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_buscaCep2FocusGained

    private void btn_buscaCep2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscaCep2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_buscaCep2ActionPerformed

    private void btn_buscaCep2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btn_buscaCep2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_buscaCep2KeyPressed

    private void btn_remover3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_remover3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_remover3ActionPerformed

    private void txt_descricao1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_descricao1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_descricao1ActionPerformed

    private void btn_buscaCep3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btn_buscaCep3FocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_buscaCep3FocusGained

    private void btn_buscaCep3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscaCep3ActionPerformed
        pesquisar();
    }//GEN-LAST:event_btn_buscaCep3ActionPerformed

    private void btn_buscaCep3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btn_buscaCep3KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_buscaCep3KeyPressed

    private void txt_pesquisa_fornecedorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pesquisa_fornecedorKeyReleased
        pesquisar_forn();
    }//GEN-LAST:event_txt_pesquisa_fornecedorKeyReleased

    private void btn_busca_fornFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btn_busca_fornFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_busca_fornFocusGained

    private void btn_busca_fornMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_busca_fornMouseClicked
        pesquisar_forn();
    }//GEN-LAST:event_btn_busca_fornMouseClicked

    private void btn_busca_fornActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_busca_fornActionPerformed
        // TODO add your handling code here:
        pesquisar_forn();
    }//GEN-LAST:event_btn_busca_fornActionPerformed

    private void btn_busca_fornKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btn_busca_fornKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_busca_fornKeyPressed

    private void tbl_fornMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_fornMouseClicked
        setar_campos_forn();
    }//GEN-LAST:event_tbl_fornMouseClicked

    private void txt_pesquisa_fornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_pesquisa_fornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_pesquisa_fornecedorActionPerformed

    private void txt_id_fornFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_id_fornFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_id_fornFocusGained

    private void txt_id_fornKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_id_fornKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_id_fornKeyPressed

    private void txt_id_fornKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_id_fornKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_id_fornKeyTyped

    private void txt_fornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_fornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_fornecedorActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bg_tipo;
    private javax.swing.JButton btn_buscaCep2;
    private javax.swing.JButton btn_buscaCep3;
    private javax.swing.JButton btn_busca_forn;
    private javax.swing.JButton btn_editar;
    private javax.swing.JButton btn_editar1;
    private javax.swing.JButton btn_remover;
    private javax.swing.JButton btn_remover1;
    private javax.swing.JButton btn_remover2;
    private javax.swing.JButton btn_remover3;
    private javax.swing.JButton btn_salvar;
    private javax.swing.JButton btn_salvar1;
    private javax.swing.JComboBox<String> cb_disp;
    private javax.swing.JComboBox<String> cb_tipo;
    private javax.swing.JLabel cel;
    private javax.swing.JLabel cel1;
    private javax.swing.JLabel icon1;
    private javax.swing.JLabel id;
    private javax.swing.JLabel id1;
    private javax.swing.JLabel id2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel nome;
    private javax.swing.JLabel nome1;
    private javax.swing.JLabel numero;
    private javax.swing.JLabel numero1;
    private javax.swing.JLabel pesquisar;
    private javax.swing.JLabel pesquisar1;
    private javax.swing.JLabel razao;
    private javax.swing.JLabel razao1;
    private javax.swing.JTable tbl_forn;
    private javax.swing.JTable tbl_materia;
    private javax.swing.JTable tbl_produtos1;
    private javax.swing.JTextField txt_descricao;
    private javax.swing.JTextField txt_descricao1;
    private javax.swing.JTextField txt_fornecedor;
    private javax.swing.JTextField txt_fornecedor1;
    private javax.swing.JFormattedTextField txt_id;
    private javax.swing.JFormattedTextField txt_id1;
    private javax.swing.JFormattedTextField txt_id_forn;
    private javax.swing.JTextField txt_nome;
    private javax.swing.JTextField txt_nome1;
    private javax.swing.JTextField txt_pesquisaMateria;
    private javax.swing.JTextField txt_pesquisaProdutos1;
    private javax.swing.JTextField txt_pesquisa_fornecedor;
    private javax.swing.JTextField txt_preco;
    private javax.swing.JTextField txt_preco1;
    // End of variables declaration//GEN-END:variables
}
