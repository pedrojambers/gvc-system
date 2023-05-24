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
import java.text.SimpleDateFormat;
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
public class TelaInternaContasAPagar extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    private static TelaInternaContasAPagar telaInternaContasAPagar;

    public static TelaInternaContasAPagar getInstancia() {
        if (telaInternaContasAPagar == null) {
            telaInternaContasAPagar = new TelaInternaContasAPagar();
        }
        return telaInternaContasAPagar;

    }

    public TelaInternaContasAPagar() {
        initComponents();
        conexao = ModuloConexao.conector();

        txt_nomeFornecedor.requestFocusInWindow();
        txt_valor.setText("0,00");
        cb_situacao.setSelectedIndex(0);
        contas();
    }

    private String pago = "não";
    private String cnpj;
    private String id_forn;
    boolean editando = false;

    private void pesquisar_fornecedores() {
        String sql = "select id_forn as ID, razao_forn as Fornecedor, cnpj_forn as CNPJ "
                + "from tb_fornecedores where razao_forn like ?";

        try {
            ps = conexao.prepareStatement(sql);
            ps.setString(1, txt_pesquisaFornecedor.getText() + "%");
            rs = ps.executeQuery();
            tbl_fornecedores.setModel(DbUtils.resultSetToTableModel(rs));
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
        int setar = tbl_fornecedores.getSelectedRow();
        id_forn = tbl_fornecedores.getModel().getValueAt(setar, 0).toString();
        txt_nomeFornecedor.setText(tbl_fornecedores.getModel().getValueAt(setar, 1).toString());
        cnpj = tbl_fornecedores.getModel().getValueAt(setar, 2).toString();
    }
    
private static final DecimalFormat df = new DecimalFormat("0.000");

 //   private void calc_tamanho() {
        
 //       double altura = Double.parseDouble(txt_altura.getText().replace(",", "."));
 //       double comp = Double.parseDouble(txt_comp.getText().replace(",", "."));
 //       double tamanho = altura * comp;
 //       txt_tamanho.setText(String.valueOf(df.format(tamanho)).replace(".", ","));
 //  }


    private void emitir_conta() {
        String sql = "insert into tb_contas(descricao, valor, dt_validade, situacao, id_forn, cnpj_forn, razao_forn) "
                + "values(?, ?, ?, ?, ?, ?, ?)";

        try {
            ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, txt_descricao.getText());
            ps.setString(2, txt_valor.getText().replace(",", "."));
            
            java.util.Date data1 = d1.getDate();
            SimpleDateFormat formatador = new SimpleDateFormat("yyyy-MM-dd");
            String data1Formatada = formatador.format(data1);
            ps.setString(3, data1Formatada);
            
            ps.setString(4, pago);
            ps.setString(5, id_forn);
            ps.setString(6, cnpj);
            ps.setString(7, txt_nomeFornecedor.getText());

            if (id_forn.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione o Fornecedor!");
            } else if (txt_descricao.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Defina a Descrição da conta!");
            } else if (txt_valor.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Insira o valor da conta!");
            } else {
                int adicionado = ps.executeUpdate();
                if (adicionado > 0) {
                    rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        fechar_conta();

                        int id = rs.getInt(1);
                        txt_id_conta.setText("" + id);

                        String sql2 = ("select * from tb_contas where id_conta = " + id);

                        try {
                            ps = conexao.prepareStatement(sql2);
                            rs = ps.executeQuery();
                            if (rs.next()) {
                                data1Formatada = rs.getString(4);
                                d1.setDateFormatString(data1Formatada);
                            }

                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, e);
                        }
                        contas();
                    }

                    JOptionPane.showMessageDialog(null, "Conta registrada!");
                    int confirma = JOptionPane.showConfirmDialog(null, "Deseja gerar o Relatório da Conta?", "Atenção!", JOptionPane.YES_NO_OPTION);
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
        txt_id_conta.setText(null);
        txt_nomeFornecedor.setText(null);
        txt_descricao.setText(null);
        d1.setDate(null);
        cb_situacao.setSelectedIndex(0);
        txt_valor.setText("0,00");
    }

    private void buscar_conta() {
        String num_conta = JOptionPane.showInputDialog("ID da conta:");
            
        String sql = "select * from tb_contas where id_conta = " + num_conta;
        if (num_conta.isBlank() == false) {
            try {
                ps = conexao.prepareStatement(sql);
                rs = ps.executeQuery();
                if (rs.next()) {
                    txt_id_conta.setText(rs.getString(1));
                    txt_descricao.setText(rs.getString(2));
                    txt_valor.setText(rs.getString(3).replace(".", ","));
                    d1.setDate(rs.getDate(4));
                    
                    switch (rs.getString(5)) {
                        case "não":
                            cb_situacao.setSelectedIndex(0);
                            break;
                        case "sim":
                            cb_situacao.setSelectedIndex(1);
                            break;
                    }
                    
                    id_forn = rs.getString(6);
                    cnpj = rs.getString(7);
                    txt_nomeFornecedor.setText(rs.getString(8));
                    fechar_conta();

                } else {
                    JOptionPane.showMessageDialog(null, "Conta não encontrada!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Conta não encontrada!");
            }
        }

    }
    
    private void editar_conta() {       
        String sql = "update tb_contas set descricao=?, valor=?, dt_validade=?, situacao=?, "
                + "id_forn=?, cnpj_forn=?, razao_forn=? where id_conta=?";
        
        try {
            ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, txt_descricao.getText());
            ps.setString(2, txt_valor.getText().replace(",", "."));
            
            java.util.Date data1 = d1.getDate();
            SimpleDateFormat formatador = new SimpleDateFormat("yyyy-MM-dd");
            String data1Formatada = formatador.format(data1);
            ps.setString(3, data1Formatada);
            
            ps.setString(4, pago);
            ps.setString(5, id_forn);
            ps.setString(6, cnpj);
            ps.setString(7, txt_nomeFornecedor.getText());
            ps.setString(8, txt_id_conta.getText());

            if (id_forn.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione o Fornecedor!");
            } else if (txt_descricao.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Defina a Descrição da conta!");
            } else if (txt_valor.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Insira o valor da conta!");
            } else {
                int adicionado = ps.executeUpdate();
                if (adicionado > 0) {
                    fechar_conta();
                    contas();
                    editando = false;
                    JOptionPane.showMessageDialog(null, "Conta editada!");

                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void excluir_conta() {
        int confirma = JOptionPane.showConfirmDialog(null, "Deseja realmente remover?", "Atenção!", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from tb_contas where id_conta=?";
            try {
                ps = conexao.prepareStatement(sql);
                ps.setString(1, txt_id_conta.getText());
                int apagado = ps.executeUpdate();
                if (apagado > 0) {
                    JOptionPane.showMessageDialog(null, "Conta removida!");
                    limparCampos();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    private void contas() {
        String sql = "select id_conta as ID, descricao as Descrição, valor as Valor, "
                + "dt_validade as Dt_Vencimento, situacao as Situação, cnpj_forn as CNPJ, razao_forn as Fornecedor "
                + "from tb_contas where situacao = 'não'";

        try {
            ps = conexao.prepareStatement(sql);
            rs = ps.executeQuery();

            tbl_contas.setModel(DbUtils.resultSetToTableModel(rs));
            soma_contas();
            
            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
            for(int i = 2; i <=5; i++){
                tbl_contas.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void soma_contas(){
        double soma = 0;
        for(int i = 0; i < tbl_contas.getRowCount(); i++){
            soma = soma + Double.parseDouble(tbl_contas.getValueAt(i, 2).toString());
        }
        
        lblSoma.setText(Double.toString(soma));
    }

    public void imprimir() {
        Map p = new HashMap();
        p.put("cod_conta", txt_id_conta.getText());
        JasperReport relatorio;
        JasperPrint impressao;
        try {
            if (!txt_id_conta.getText().equals("")) {
                relatorio = JasperCompileManager.compileReport(new File("").getAbsolutePath() + "/rel/rlConta.jrxml");
                impressao = JasperFillManager.fillReport(relatorio, p, conexao);
                JasperViewer view = new JasperViewer(impressao, false);
                view.setTitle("RLV" + txt_id_conta.getText());
                view.setVisible(true);

            } else {

                JOptionPane.showMessageDialog(null, "Selecione a conta!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void fechar_conta() {
        txt_descricao.setEditable(false);
        d1.setEnabled(false);
        cb_situacao.setEnabled(false);
        txt_valor.setEditable(false);
        btn_salvar.setEnabled(false);
        jPanel1.setEnabled(false);
        tbl_fornecedores.setEnabled(false);
    }

    private void abrir_conta() {
        txt_descricao.setEditable(true);
        d1.setEnabled(true);
        cb_situacao.setEnabled(true);
        txt_valor.setEditable(true);
        btn_salvar.setEnabled(true);
        jPanel1.setEnabled(true);
        tbl_fornecedores.setEnabled(true);
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
        txt_id_conta = new javax.swing.JTextField();
        fornecedor = new javax.swing.JLabel();
        txt_nomeFornecedor = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        txt_pesquisaFornecedor = new javax.swing.JTextField();
        btn_buscaCliente = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_fornecedores = new javax.swing.JTable();
        descricao = new javax.swing.JLabel();
        txt_descricao = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txt_valor = new javax.swing.JTextField();
        situacao = new javax.swing.JLabel();
        cb_situacao = new javax.swing.JComboBox<>();
        btn_salvar = new javax.swing.JButton();
        btn_editar = new javax.swing.JButton();
        btn_remover = new javax.swing.JButton();
        btn_remover1 = new javax.swing.JButton();
        btn_imprimir = new javax.swing.JButton();
        btn_buscar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_contas = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        descricao1 = new javax.swing.JLabel();
        d1 = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        lblSoma = new javax.swing.JLabel();

        setBorder(null);
        setClosable(true);
        setForeground(new java.awt.Color(240, 240, 240));
        setTitle("Contas a Pagar");
        setFrameIcon(null);
        setMinimumSize(new java.awt.Dimension(64, 27));
        setNormalBounds(new java.awt.Rectangle(0, 0, 64, 0));
        setOpaque(true);
        setPreferredSize(new java.awt.Dimension(800, 690));

        id.setText("ID Conta");

        txt_id_conta.setEditable(false);

        fornecedor.setText("Fornecedor");

        txt_nomeFornecedor.setEditable(false);
        txt_nomeFornecedor.setFocusable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Fornecedores"));

        txt_pesquisaFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_pesquisaFornecedorActionPerformed(evt);
            }
        });
        txt_pesquisaFornecedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_pesquisaFornecedorKeyReleased(evt);
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

        tbl_fornecedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Fornecedor", "CNPJ"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_fornecedores.setFocusable(false);
        tbl_fornecedores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_fornecedoresMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_fornecedores);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(txt_pesquisaFornecedor)
                .addGap(0, 0, 0)
                .addComponent(btn_buscaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_pesquisaFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_buscaCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE))
        );

        descricao.setText("Desc.");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Contas a Pagar");

        txt_valor.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        txt_valor.setForeground(new java.awt.Color(51, 153, 0));
        txt_valor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_valor.setText("0,00");

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

        btn_buscar.setBackground(new java.awt.Color(230, 230, 230));
        btn_buscar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gvcsystem/img/buscar.png"))); // NOI18N
        btn_buscar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btn_buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscarActionPerformed(evt);
            }
        });

        tbl_contas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Descrição", "Valor", "Dt_validade", "Situação", "CNPJ", "Fornecedor"
            }
        ));
        jScrollPane2.setViewportView(tbl_contas);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        jLabel4.setText("Valor Total(R$)");

        descricao1.setText("Dt. de Vencimento");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("TOTAL:");

        lblSoma.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSoma.setForeground(new java.awt.Color(255, 0, 0));
        lblSoma.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSoma.setText("0,00");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(descricao)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_descricao))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(descricao1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(d1, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(situacao)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cb_situacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(id)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_id_conta, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(fornecedor)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_nomeFornecedor)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
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
                                        .addComponent(btn_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(7, 7, 7)
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_valor)))
                                .addGap(18, 18, 18)))
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(lblSoma, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 771, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txt_id_conta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(fornecedor)
                                        .addComponent(txt_nomeFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(3, 3, 3)
                                        .addComponent(id)))
                                .addGap(2, 2, 2)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(descricao)
                                    .addComponent(txt_descricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(2, 2, 2)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(descricao1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(d1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cb_situacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(situacao)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_valor, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_remover1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_salvar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_editar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_remover, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_imprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblSoma)
                        .addComponent(jLabel1)))
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

    private void btn_salvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salvarActionPerformed

        if (!editando) {
            emitir_conta();
        } else {
            editar_conta();
        }
    }//GEN-LAST:event_btn_salvarActionPerformed

    private void btn_editarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_editarActionPerformed
        abrir_conta();
        editando = true;

    }//GEN-LAST:event_btn_editarActionPerformed

    private void btn_removerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_removerActionPerformed
        excluir_conta();

    }//GEN-LAST:event_btn_removerActionPerformed

    private void btn_remover1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_remover1ActionPerformed
        limparCampos();
        abrir_conta();

    }//GEN-LAST:event_btn_remover1ActionPerformed

    private void btn_imprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_imprimirActionPerformed
        int confirma = JOptionPane.showConfirmDialog(null, "Deseja gerar o Relatório da Venda?", "Atenção!", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            imprimir();
        }

    }//GEN-LAST:event_btn_imprimirActionPerformed

    private void btn_buscaClienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_buscaClienteMouseClicked
        pesquisar_fornecedores();
    }//GEN-LAST:event_btn_buscaClienteMouseClicked

    private void txt_pesquisaFornecedorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pesquisaFornecedorKeyReleased
        pesquisar_fornecedores();

    }//GEN-LAST:event_txt_pesquisaFornecedorKeyReleased

    private void tbl_fornecedoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_fornecedoresMouseClicked
        setar_campos();
    }//GEN-LAST:event_tbl_fornecedoresMouseClicked

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

    private void btn_buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscarActionPerformed
        buscar_conta();
    }//GEN-LAST:event_btn_buscarActionPerformed

    private void txt_pesquisaFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_pesquisaFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_pesquisaFornecedorActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_buscaCliente;
    private javax.swing.JButton btn_buscar;
    private javax.swing.JButton btn_editar;
    private javax.swing.JButton btn_imprimir;
    private javax.swing.JButton btn_remover;
    private javax.swing.JButton btn_remover1;
    private javax.swing.JButton btn_salvar;
    private javax.swing.JComboBox<String> cb_situacao;
    private com.toedter.calendar.JDateChooser d1;
    private javax.swing.JLabel descricao;
    private javax.swing.JLabel descricao1;
    private javax.swing.JLabel fornecedor;
    private javax.swing.JLabel id;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblSoma;
    private javax.swing.JLabel situacao;
    private javax.swing.JTable tbl_contas;
    private javax.swing.JTable tbl_fornecedores;
    private javax.swing.JFormattedTextField txt_cnpj;
    private javax.swing.JFormattedTextField txt_cnpj1;
    private javax.swing.JTextField txt_descricao;
    private javax.swing.JTextField txt_id_conta;
    private javax.swing.JTextField txt_nomeFornecedor;
    private javax.swing.JTextField txt_pesquisaFornecedor;
    private javax.swing.JTextField txt_valor;
    // End of variables declaration//GEN-END:variables
}
