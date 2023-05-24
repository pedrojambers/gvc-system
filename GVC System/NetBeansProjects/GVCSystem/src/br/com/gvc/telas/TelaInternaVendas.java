/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.gvc.telas;

import java.awt.Color;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.sql.*;
import br.com.gvc.dal.ModuloConexao;
import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
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
public class TelaInternaVendas extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    private static TelaInternaVendas telaInternaVendas;

    public static TelaInternaVendas getInstancia() {
        if (telaInternaVendas == null) {
            telaInternaVendas = new TelaInternaVendas();
        }
        return telaInternaVendas;

    }

    public TelaInternaVendas() {
        initComponents();
        conexao = ModuloConexao.conector();

        txt_nomeCliente.requestFocusInWindow();

        txt_tamanho.setText("0,00");
        txt_valorm2.setText("0,00");
        txt_valorUnit.setText("0,00");
        txt_quantidade.setText("1");
        txt_valorTotal.setText("0,00");
        cb_situacao.setSelectedIndex(0);
        vendas();
    }

    private String pago = "não";

    boolean editando = false;

    private void pesquisar_cliente() {
        String sql = "select id_cliente as ID, nome_cliente as Nome, "
                + "razao_cliente as Razão_Social from tb_clientes where nome_cliente like ?";

        try {
            ps = conexao.prepareStatement(sql);
            ps.setString(1, txt_pesquisaCliente.getText() + "%");
            rs = ps.executeQuery();
            tbl_clientes.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
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

    private void setar_campos() {
        int setar = tbl_clientes.getSelectedRow();
        txt_idCliente.setText(tbl_clientes.getModel().getValueAt(setar, 0).toString());
        txt_nomeCliente.setText(tbl_clientes.getModel().getValueAt(setar, 1).toString());
        txt_razao.setText(tbl_clientes.getModel().getValueAt(setar, 2).toString());
    }
    
private static final DecimalFormat df = new DecimalFormat("0.000");

    private void calc_tamanho() {
        
        double altura = Double.parseDouble(txt_altura.getText().replace(",", "."));
        double comp = Double.parseDouble(txt_comp.getText().replace(",", "."));
        double tamanho = altura * comp;
        txt_tamanho.setText(String.valueOf(df.format(tamanho)).replace(".", ","));
    }
    
    //

    private void calc_valorUnit() {
        double tamanho = Double.parseDouble(txt_tamanho.getText().replace(",", "."));
        double valorm2 = Double.parseDouble(txt_valorm2.getText().replace(",", "."));
        double precoUnit = (tamanho * valorm2);
        txt_valorUnit.setText(String.valueOf(df.format(precoUnit)).replace(".", ","));
        txt_valorTotal.setText(String.valueOf(df.format(precoUnit)).replace(".", ","));
    }

    private void calc_valorTotal() {
        double precoUnit = Double.parseDouble(txt_valorUnit.getText().replace(",", "."));
        double quantidade = Double.parseDouble(txt_quantidade.getText());
        double precoTotal = (precoUnit * quantidade);
        txt_valorTotal.setText(String.valueOf(df.format(precoTotal)).replace(".", ","));
    }

    private void emitir_venda() {
        String sql = "insert into tb_venda(descricao, tamanho, preco_m2, preco_unit, quantidade, preco_total, pago, id_cliente) "
                + "values(?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, txt_descricao.getText());
            ps.setString(2, txt_tamanho.getText().replace(",", "."));
            ps.setString(3, txt_valorm2.getText().replace(",", "."));
            ps.setString(4, txt_valorUnit.getText().replace(",", "."));
            ps.setString(5, txt_quantidade.getText().replace(",", "."));
            ps.setString(6, txt_valorTotal.getText().replace(",", "."));
            ps.setString(7, pago);
            ps.setString(8, txt_idCliente.getText());

            if (txt_idCliente.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione o Cliente!");
            } else if (txt_descricao.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Defina a Descrição da venda!");
            } else if (txt_quantidade.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Insira a Quantidade da venda!");
            } else if (txt_valorTotal.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Insira o valor total da venda!");
            } else {
                int adicionado = ps.executeUpdate();
                if (adicionado > 0) {
                    rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        fechar_venda();

                        int id = rs.getInt(1);
                        txt_id.setText("" + id);

                        String sql2 = ("select * from tb_venda where id_venda = " + id);

                        try {
                            ps = conexao.prepareStatement(sql2);
                            rs = ps.executeQuery();
                            if (rs.next()) {
                                txt_data.setText(rs.getString(9));

                            }

                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, e);
                        }
                        vendas();
                    }

                    JOptionPane.showMessageDialog(null, "Venda cadastrada!");
                    int confirma = JOptionPane.showConfirmDialog(null, "Deseja gerar o Relatório da Venda?", "Atenção!", JOptionPane.YES_NO_OPTION);
                    if (confirma == JOptionPane.YES_OPTION) {
                        imprimir();
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void limparCampos() {
        txt_id.setText(null);
        txt_data.setText(null);
        txt_idCliente.setText(null);
        txt_nomeCliente.setText(null);
        txt_razao.setText(null);
        txt_descricao.setText(null);
        txt_altura.setText(null);
        txt_comp.setText(null);
        txt_tamanho.setText("0,00");
        txt_valorm2.setText("0,00");
        txt_valorUnit.setText("0,00");
        cb_situacao.setSelectedIndex(0);
        txt_quantidade.setText("1");
        txt_valorTotal.setText("0,00");
    }

    private void buscar_venda() {
        String num_venda = JOptionPane.showInputDialog("ID da venda:");

        String sql = "select * from tb_venda where id_venda = " + num_venda;
        if (num_venda.isBlank() == false) {
            try {
                ps = conexao.prepareStatement(sql);
                rs = ps.executeQuery();
                if (rs.next()) {
                    txt_id.setText(rs.getString(1));
                    txt_descricao.setText(rs.getString(2));
                    txt_tamanho.setText(rs.getString(3).replace(".", ","));
                    txt_valorm2.setText(rs.getString(4).replace(".", ","));
                    txt_valorUnit.setText(rs.getString(5).replace(".", ","));
                    txt_quantidade.setText(rs.getString(6));
                    txt_valorTotal.setText(rs.getString(7).replace(".", ","));

                    switch (rs.getString(8)) {
                        case "não":
                            cb_situacao.setSelectedIndex(0);
                            break;
                        case "sim":
                            cb_situacao.setSelectedIndex(1);
                            break;
                    }

                    txt_data.setText(rs.getString(9));
                    txt_idCliente.setText(rs.getString(10));
                    String num_cliente = rs.getString(10);
                    String sql2 = "select * from tb_clientes where id_cliente = " + num_cliente;
                    try {
                        ps = conexao.prepareStatement(sql2);
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            txt_nomeCliente.setText(rs.getString(2));
                            txt_razao.setText(rs.getString(7));
                            fechar_venda();
                        } else {
                            JOptionPane.showMessageDialog(null, "Venda não encontrada!");
                        }

                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e);
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Venda não encontrada!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Venda não encontrada!");
            }
        }

    }

    private void buscar_produto(){
    String num_produto = JOptionPane.showInputDialog("ID do produto:");

        String sql = "select * from tb_produtos where id_produto = " + num_produto;
        if (num_produto.isBlank() == false) {
            try {
                ps = conexao.prepareStatement(sql);
                rs = ps.executeQuery();
                if (rs.next()) {
                    txt_descricao.setText(rs.getString(2));
                    txt_valorm2.setText(rs.getString(7).replace(".", ","));
                    calc_valorUnit();
                    calc_valorTotal();
                } else {
                    JOptionPane.showMessageDialog(null, "Produto não encontrado!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Produto não encontrado!");
            }
        }

}
    
    private void editar_venda() {
        String sql = "update tb_venda set descricao=?, tamanho=?, preco_m2=?, preco_unit=?, "
                + "quantidade=?, preco_total=?, pago=? where id_venda=?";

        try {
            ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, txt_descricao.getText());
            ps.setString(2, txt_tamanho.getText().replace(",", "."));
            ps.setString(3, txt_valorm2.getText().replace(",", "."));
            ps.setString(4, txt_valorUnit.getText().replace(",", "."));
            ps.setString(5, txt_quantidade.getText().replace(",", "."));
            ps.setString(6, txt_valorTotal.getText().replace(",", "."));
            ps.setString(7, pago);
            ps.setString(8, txt_id.getText());

            if (txt_idCliente.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione o Cliente!");
            } else if (txt_descricao.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Defina a Descrição da venda!");
            } else if (txt_quantidade.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Insira a Quantidade da venda!");
            } else if (txt_valorTotal.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Insira o valor total da venda!");
            } else {
                int adicionado = ps.executeUpdate();
                if (adicionado > 0) {
                    fechar_venda();
                    vendas();
                    editando = false;
                    JOptionPane.showMessageDialog(null, "Venda editada!");

                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void excluir_venda() {
        int confirma = JOptionPane.showConfirmDialog(null, "Deseja realmente remover?", "Atenção!", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from tb_venda where id_venda=?";
            try {
                ps = conexao.prepareStatement(sql);
                ps.setString(1, txt_id.getText());
                int apagado = ps.executeUpdate();
                if (apagado > 0) {
                    JOptionPane.showMessageDialog(null, "Venda removida!");
                    limparCampos();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    private void vendas() {
        String sql = "select id_venda as ID, descricao as Descrição, preco_m2 as ValorMetro, "
                + "preco_unit as ValorUnit, quantidade as Qtd, preco_total as ValorTotal, pago as Pago,"
                + "dt_emissao as DtEmissão, id_cliente as Cliente from tb_venda where date(dt_emissao) = CURDATE()";

        try {
            ps = conexao.prepareStatement(sql);
            rs = ps.executeQuery();

            tbl_vendasDia.setModel(DbUtils.resultSetToTableModel(rs));
            
            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
            for(int i = 2; i <=7; i++){
                tbl_vendasDia.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void imprimir() {
        Map p = new HashMap();
        p.put("cod_venda", txt_id.getText());
        JasperReport relatorio;
        JasperPrint impressao;
        try {
            if (!txt_id.getText().equals("")) {
                relatorio = JasperCompileManager.compileReport(new File("").getAbsolutePath() + "/rel/rlVenda.jrxml");
                impressao = JasperFillManager.fillReport(relatorio, p, conexao);
                JasperViewer view = new JasperViewer(impressao, false);
                view.setTitle("RLV" + txt_id.getText());
                view.setVisible(true);

            } else {

                JOptionPane.showMessageDialog(null, "Selecione a venda!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void fechar_venda() {
        txt_descricao.setEditable(false);
        txt_altura.setEditable(false);
        txt_comp.setEditable(false);
        txt_tamanho.setEditable(false);
        txt_valorm2.setEditable(false);
        txt_valorUnit.setEditable(false);
        txt_quantidade.setEditable(false);
        cb_situacao.setEnabled(false);
        txt_valorTotal.setEditable(false);
        btn_salvar.setEnabled(false);
        jPanel1.setEnabled(false);
        tbl_clientes.setEnabled(false);
    }

    private void abrir_venda() {
        txt_descricao.setEditable(true);
        txt_altura.setEditable(true);
        txt_comp.setEditable(true);
        txt_tamanho.setEditable(true);
        txt_valorm2.setEditable(true);
        txt_valorUnit.setEditable(true);
        txt_quantidade.setEditable(true);
        cb_situacao.setEnabled(true);
        txt_valorTotal.setEditable(true);
        btn_salvar.setEnabled(true);
        jPanel1.setEnabled(true);
        tbl_clientes.setEnabled(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        id = new javax.swing.JLabel();
        txt_id = new javax.swing.JTextField();
        cliente = new javax.swing.JLabel();
        txt_idCliente = new javax.swing.JTextField();
        nome = new javax.swing.JLabel();
        txt_nomeCliente = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        txt_pesquisaCliente = new javax.swing.JTextField();
        btn_buscaCliente = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_clientes = new javax.swing.JTable();
        data = new javax.swing.JLabel();
        txt_data = new javax.swing.JTextField();
        descricao = new javax.swing.JLabel();
        txt_descricao = new javax.swing.JTextField();
        txt_altura = new javax.swing.JTextField();
        txt_comp = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        tamanho = new javax.swing.JLabel();
        txt_tamanho = new javax.swing.JTextField();
        valorm2 = new javax.swing.JLabel();
        txt_valorm2 = new javax.swing.JTextField();
        quantidade = new javax.swing.JLabel();
        txt_quantidade = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txt_valorTotal = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        situacao = new javax.swing.JLabel();
        cb_situacao = new javax.swing.JComboBox<>();
        btn_salvar = new javax.swing.JButton();
        btn_editar = new javax.swing.JButton();
        btn_remover = new javax.swing.JButton();
        btn_remover1 = new javax.swing.JButton();
        btn_imprimir = new javax.swing.JButton();
        razao_social = new javax.swing.JLabel();
        txt_razao = new javax.swing.JTextField();
        valorUnit = new javax.swing.JLabel();
        txt_valorUnit = new javax.swing.JTextField();
        btn_buscar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_vendasDia = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        btn_buscar_produto = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setBorder(null);
        setClosable(true);
        setForeground(new java.awt.Color(240, 240, 240));
        setTitle("Venda");
        setFrameIcon(null);
        setMinimumSize(new java.awt.Dimension(64, 27));
        setNormalBounds(new java.awt.Rectangle(0, 0, 64, 0));
        setOpaque(true);
        setPreferredSize(new java.awt.Dimension(800, 690));

        id.setText("ID");

        txt_id.setEditable(false);

        cliente.setText("Cliente");

        txt_idCliente.setEditable(false);
        txt_idCliente.setFocusable(false);

        nome.setText("Nome");

        txt_nomeCliente.setEditable(false);
        txt_nomeCliente.setFocusable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Clientes"));

        txt_pesquisaCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_pesquisaClienteActionPerformed(evt);
            }
        });
        txt_pesquisaCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_pesquisaClienteKeyReleased(evt);
            }
        });

        btn_buscaCliente.setText("🔎");
        btn_buscaCliente.setFocusable(false);
        btn_buscaCliente.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                btn_buscaClienteFocusGained(evt);
            }
        });
        btn_buscaCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_buscaClienteMouseClicked(evt);
            }
        });
        btn_buscaCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscaClienteActionPerformed(evt);
            }
        });
        btn_buscaCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btn_buscaClienteKeyPressed(evt);
            }
        });

        tbl_clientes.setModel(new javax.swing.table.DefaultTableModel(
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
        tbl_clientes.setFocusable(false);
        tbl_clientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_clientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_clientes);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(txt_pesquisaCliente)
                .addGap(0, 0, 0)
                .addComponent(btn_buscaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_pesquisaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_buscaCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );

        data.setText("Data");

        txt_data.setEditable(false);

        descricao.setText("Desc.");

        txt_altura.setForeground(java.awt.Color.gray);
        txt_altura.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_altura.setText("Altura");
        txt_altura.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_alturaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_alturaFocusLost(evt);
            }
        });
        txt_altura.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_alturaKeyReleased(evt);
            }
        });

        txt_comp.setForeground(java.awt.Color.gray);
        txt_comp.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_comp.setText("Comp.");
        txt_comp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_compFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_compFocusLost(evt);
            }
        });
        txt_comp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_compActionPerformed(evt);
            }
        });
        txt_comp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_compKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_compKeyReleased(evt);
            }
        });

        jLabel1.setText("X");

        tamanho.setText("Tamanho (m²)");

        txt_tamanho.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_tamanho.setText("0,00");
        txt_tamanho.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txt_tamanhoPropertyChange(evt);
            }
        });
        txt_tamanho.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_tamanhoKeyReleased(evt);
            }
        });

        valorm2.setText("Valor (m²)");

        txt_valorm2.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_valorm2.setText("0,00");
        txt_valorm2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_valorm2KeyReleased(evt);
            }
        });

        quantidade.setText("Quantidade");

        txt_quantidade.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_quantidade.setText("1");
        txt_quantidade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_quantidadeKeyReleased(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Vendas do dia");

        txt_valorTotal.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        txt_valorTotal.setForeground(new java.awt.Color(51, 153, 0));
        txt_valorTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_valorTotal.setText("0,00");

        jLabel3.setText("=");

        situacao.setText("Situação:");

        cb_situacao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pagamento pendente", "Pagamento efetuado" }));
        cb_situacao.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cb_situacaoItemStateChanged(evt);
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

        btn_imprimir.setBackground(new java.awt.Color(230, 230, 230));
        btn_imprimir.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_imprimir.setForeground(new java.awt.Color(0, 102, 102));
        btn_imprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gvcsystem/img/imprimir.png"))); // NOI18N
        btn_imprimir.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btn_imprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_imprimirActionPerformed(evt);
            }
        });

        razao_social.setText("Razão Social");

        txt_razao.setEditable(false);
        txt_razao.setFocusable(false);
        txt_razao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_razaoActionPerformed(evt);
            }
        });

        valorUnit.setText("Valor Unitário (R$)");

        txt_valorUnit.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_valorUnit.setText("0,00");
        txt_valorUnit.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                txt_valorUnitComponentAdded(evt);
            }
        });
        txt_valorUnit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_valorUnitFocusLost(evt);
            }
        });
        txt_valorUnit.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txt_valorUnitPropertyChange(evt);
            }
        });
        txt_valorUnit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_valorUnitKeyReleased(evt);
            }
        });

        btn_buscar.setBackground(new java.awt.Color(230, 230, 230));
        btn_buscar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gvcsystem/img/buscar.png"))); // NOI18N
        btn_buscar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btn_buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscarActionPerformed(evt);
            }
        });

        tbl_vendasDia.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Descrição", "ValorMetro", "ValorUnit", "Qtd", "ValorTotal", "DtEmissao"
            }
        ));
        jScrollPane2.setViewportView(tbl_vendasDia);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        jLabel4.setText("Valor Total(R$)");

        btn_buscar_produto.setBackground(new java.awt.Color(230, 230, 230));
        btn_buscar_produto.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_buscar_produto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gvcsystem/img/buscar_produto.png"))); // NOI18N
        btn_buscar_produto.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btn_buscar_produto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscar_produtoActionPerformed(evt);
            }
        });

        jLabel5.setText("m");

        jLabel6.setText("m");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(descricao)
                                .addGap(13, 13, 13)
                                .addComponent(txt_descricao))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(valorm2)
                                .addGap(5, 5, 5)
                                .addComponent(txt_valorm2, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(valorUnit))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(quantidade)
                                .addGap(9, 9, 9)
                                .addComponent(txt_quantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(52, 52, 52)
                                .addComponent(situacao))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txt_altura, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel1)
                                .addGap(12, 12, 12)
                                .addComponent(txt_comp, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addGap(32, 32, 32)
                                .addComponent(tamanho)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_tamanho, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(cb_situacao, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(7, 7, 7)
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_valorTotal))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(btn_remover1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(2, 2, 2)
                                        .addComponent(btn_salvar, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(2, 2, 2)
                                        .addComponent(btn_editar, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(2, 2, 2)
                                        .addComponent(btn_remover, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(2, 2, 2)
                                        .addComponent(btn_imprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(2, 2, 2)
                                        .addComponent(btn_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addComponent(txt_valorUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btn_buscar_produto, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(id)
                                .addGap(10, 10, 10)
                                .addComponent(txt_id, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(data)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_data))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cliente)
                                .addGap(5, 5, 5)
                                .addComponent(txt_idCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(nome)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_nomeCliente))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(razao_social)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_razao, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(24, 24, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 771, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_data, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(id)
                                    .addComponent(data))))
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_idCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_nomeCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cliente)
                                    .addComponent(nome))))
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(razao_social))
                            .addComponent(txt_razao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_buscar_produto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(descricao))
                            .addComponent(txt_descricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txt_altura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txt_comp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5)
                                .addComponent(jLabel1))
                            .addComponent(jLabel3)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tamanho)
                                .addComponent(txt_tamanho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_valorm2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_valorUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(valorm2)
                                    .addComponent(valorUnit))))
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_quantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cb_situacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(quantidade)
                                    .addComponent(situacao))))
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_valorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_remover1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_salvar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_editar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_remover, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_imprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );

        setBounds(0, 0, 800, 690);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_buscaClienteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btn_buscaClienteFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_buscaClienteFocusGained

    private void btn_buscaClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscaClienteActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_btn_buscaClienteActionPerformed

    private void btn_buscaClienteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btn_buscaClienteKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_buscaClienteKeyPressed

    private void txt_alturaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_alturaFocusGained
        txt_altura.selectAll();

        if (txt_altura.getText().equals("Altura")) {
            txt_altura.setText("");
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    txt_altura.selectAll();
                }
            });

            removePlaceholderStyle(txt_altura);
        }
    }//GEN-LAST:event_txt_alturaFocusGained

    private void txt_alturaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_alturaFocusLost
        if (txt_altura.getText().equals("")) {
            txt_altura.setText("Altura");

            addPlaceholderStyle(txt_altura);
        }
    }//GEN-LAST:event_txt_alturaFocusLost

    private void txt_compFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_compFocusGained
        txt_comp.selectAll();

        if (txt_comp.getText().equals("Comp.")) {
            txt_comp.setText("");
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    txt_comp.selectAll();
                }
            });

            removePlaceholderStyle(txt_comp);
        }
    }//GEN-LAST:event_txt_compFocusGained

    private void txt_compFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_compFocusLost
        if (txt_comp.getText().equals("")) {
            txt_comp.setText("Comp.");

            addPlaceholderStyle(txt_comp);
        }
    }//GEN-LAST:event_txt_compFocusLost

    private void btn_salvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salvarActionPerformed

        if (!editando) {
            emitir_venda();
        } else {
            editar_venda();
        }
    }//GEN-LAST:event_btn_salvarActionPerformed

    private void btn_editarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_editarActionPerformed
        abrir_venda();
        editando = true;

    }//GEN-LAST:event_btn_editarActionPerformed

    private void btn_removerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_removerActionPerformed
        excluir_venda();

    }//GEN-LAST:event_btn_removerActionPerformed

    private void btn_remover1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_remover1ActionPerformed
        limparCampos();
        abrir_venda();

    }//GEN-LAST:event_btn_remover1ActionPerformed

    private void btn_imprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_imprimirActionPerformed
        int confirma = JOptionPane.showConfirmDialog(null, "Deseja gerar o Relatório da Venda?", "Atenção!", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            imprimir();
        }

    }//GEN-LAST:event_btn_imprimirActionPerformed

    private void btn_buscaClienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_buscaClienteMouseClicked
        pesquisar_cliente();
    }//GEN-LAST:event_btn_buscaClienteMouseClicked

    private void txt_pesquisaClienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pesquisaClienteKeyReleased
        pesquisar_cliente();

    }//GEN-LAST:event_txt_pesquisaClienteKeyReleased

    private void tbl_clientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_clientesMouseClicked
        setar_campos();
    }//GEN-LAST:event_tbl_clientesMouseClicked

    private void cb_situacaoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cb_situacaoItemStateChanged
        switch (cb_situacao.getModel().getSelectedItem().toString()) {
            case "Pagamento pendente":
                System.out.println("não");
                pago = "não";
                break;

            case "Pagamento efetuado":
                pago = "sim";
                break;
        }
    }//GEN-LAST:event_cb_situacaoItemStateChanged

    private void txt_compKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_compKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_compKeyPressed

    private void txt_compKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_compKeyReleased
        calc_tamanho();
        calc_valorUnit();
        calc_valorTotal();
    }//GEN-LAST:event_txt_compKeyReleased

    private void txt_quantidadeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_quantidadeKeyReleased
        calc_valorTotal();
    }//GEN-LAST:event_txt_quantidadeKeyReleased

    private void txt_valorm2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_valorm2KeyReleased
        calc_valorUnit();
        calc_valorTotal();
    }//GEN-LAST:event_txt_valorm2KeyReleased

    private void txt_valorUnitKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_valorUnitKeyReleased
        calc_valorTotal();
    }//GEN-LAST:event_txt_valorUnitKeyReleased

    private void btn_buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscarActionPerformed
        buscar_venda();
    }//GEN-LAST:event_btn_buscarActionPerformed

    private void txt_razaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_razaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_razaoActionPerformed

    private void txt_pesquisaClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_pesquisaClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_pesquisaClienteActionPerformed

    private void btn_buscar_produtoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscar_produtoActionPerformed
        buscar_produto();
        
    }//GEN-LAST:event_btn_buscar_produtoActionPerformed

    private void txt_tamanhoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_tamanhoKeyReleased
        calc_valorUnit();
        calc_valorTotal();
    }//GEN-LAST:event_txt_tamanhoKeyReleased

    private void txt_tamanhoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txt_tamanhoPropertyChange

    }//GEN-LAST:event_txt_tamanhoPropertyChange

    private void txt_compActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_compActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_compActionPerformed

    private void txt_valorUnitFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_valorUnitFocusLost
        calc_valorTotal();
    }//GEN-LAST:event_txt_valorUnitFocusLost

    private void txt_valorUnitPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txt_valorUnitPropertyChange
        
    }//GEN-LAST:event_txt_valorUnitPropertyChange

    private void txt_valorUnitComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_txt_valorUnitComponentAdded
        
    }//GEN-LAST:event_txt_valorUnitComponentAdded

    private void txt_alturaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_alturaKeyReleased
        calc_valorTotal();
    }//GEN-LAST:event_txt_alturaKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_buscaCliente;
    private javax.swing.JButton btn_buscar;
    private javax.swing.JButton btn_buscar_produto;
    private javax.swing.JButton btn_editar;
    private javax.swing.JButton btn_imprimir;
    private javax.swing.JButton btn_remover;
    private javax.swing.JButton btn_remover1;
    private javax.swing.JButton btn_salvar;
    private javax.swing.JComboBox<String> cb_situacao;
    private javax.swing.JLabel cliente;
    private javax.swing.JLabel data;
    private javax.swing.JLabel descricao;
    private javax.swing.JLabel id;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel nome;
    private javax.swing.JLabel quantidade;
    private javax.swing.JLabel razao_social;
    private javax.swing.JLabel situacao;
    private javax.swing.JLabel tamanho;
    private javax.swing.JTable tbl_clientes;
    private javax.swing.JTable tbl_vendasDia;
    private javax.swing.JTextField txt_altura;
    private javax.swing.JTextField txt_comp;
    private javax.swing.JTextField txt_data;
    private javax.swing.JTextField txt_descricao;
    private javax.swing.JTextField txt_id;
    private javax.swing.JTextField txt_idCliente;
    private javax.swing.JTextField txt_nomeCliente;
    private javax.swing.JTextField txt_pesquisaCliente;
    private javax.swing.JTextField txt_quantidade;
    private javax.swing.JTextField txt_razao;
    private javax.swing.JTextField txt_tamanho;
    private javax.swing.JTextField txt_valorTotal;
    private javax.swing.JTextField txt_valorUnit;
    private javax.swing.JTextField txt_valorm2;
    private javax.swing.JLabel valorUnit;
    private javax.swing.JLabel valorm2;
    // End of variables declaration//GEN-END:variables
}
