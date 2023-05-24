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
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.JTable;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Pedro
 */
public class TelaInternaFornecedores extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    private static TelaInternaFornecedores telaInternaFornecedores;

    public static TelaInternaFornecedores getInstancia() {
        if (telaInternaFornecedores == null) {
            telaInternaFornecedores = new TelaInternaFornecedores();
        }
        return telaInternaFornecedores;

    }

    public TelaInternaFornecedores() {
        initComponents();
        conexao = ModuloConexao.conector();
        cb_uf.setSelectedItem("PR");
        
        txt_nome.requestFocusInWindow();

    }

    private void buscar() {
        String sql = "select * from tb_fornecedores where id_fornecedor=?";
        try {
            ps = conexao.prepareStatement(sql);
            ps.setString(1, txt_id.getText());
            rs = ps.executeQuery();
            if (rs.next()) {
                txt_nome.setText(rs.getString(2));
                txt_razao.setText(rs.getString(3));
                txt_cnpj.setText(rs.getString(4));
                txt_telefone.setText(rs.getString(5));
                txt_email.setText(rs.getString(6));
                txt_cep.setText(rs.getString(7));
                txt_cidade.setText(rs.getString(8));
                cb_uf.setSelectedItem(rs.getString(9));
                txt_endereco.setText(rs.getString(10));
                txt_num.setText(rs.getString(11));

            } else {
                JOptionPane.showMessageDialog(null, "Fornecedor não cadastrado!");
                limparCampos();

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    private void adicionar() {
        String sql = "insert into tb_fornecedores(nome_forn, razao_forn,"
                + "cnpj_forn, fone_forn, email_forn, cep_forn, cidade_forn,"
                + "uf_forn, end_forn, num_forn) values(?, ?, ?, ?, ?, ?, ?, ?, "
                + "?, ?)";

        try {
            ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, txt_nome.getText());
            ps.setString(2, txt_razao.getText());
            ps.setString(3, txt_cnpj.getText().replaceAll("[.|-]", "").replace("/", ""));
            ps.setString(4, txt_telefone.getText().replaceAll("[()-]", ""));
            ps.setString(5, txt_email.getText());
            ps.setString(6, txt_cep.getText().replaceAll("[-]", ""));
            ps.setString(7, txt_cidade.getText());
            ps.setString(8, cb_uf.getSelectedItem().toString());
            ps.setString(9, txt_endereco.getText());
            ps.setString(10, txt_num.getText());

            if (txt_nome.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha o nome do Fornecedor!");
            } else if(txt_razao.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha a razão social do Fornecedor!");
            } else {
                int adicionado = ps.executeUpdate();
                if (adicionado > 0) {

                    rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        txt_id.setText("" + id);

                    }
                    JOptionPane.showMessageDialog(null, "Fornecedor cadastrado!");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    private void editar() {
        String sql = "update tb_fornecedores set nome_forn = ?, "
                + "razao_forn = ?, cnpj_forn = ?, fone_forn = ?, email_forn = ?, "
                + "cep_forn = ?, cidade_forn = ?, uf_forn = ?, end_forn = ?, "
                + "num_forn = ? where id_forn = ?";
        try {
            ps = conexao.prepareStatement(sql);
            ps.setString(1, txt_nome.getText());
            ps.setString(2, txt_razao.getText());
            ps.setString(3, txt_cnpj.getText().replaceAll("[.|-]", "").replace("/", ""));
            ps.setString(4, txt_telefone.getText().replaceAll("[()-]", ""));
            ps.setString(5, txt_email.getText());
            ps.setString(6, txt_cep.getText().replaceAll("[-]", ""));
            ps.setString(7, txt_cidade.getText());
            ps.setString(8, cb_uf.getSelectedItem().toString());
            ps.setString(9, txt_endereco.getText());
            ps.setString(10, txt_num.getText());
            ps.setString(11, txt_id.getText());

            if (txt_id.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha o ID do Fornecedor!");
            } else if (txt_nome.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha o nome do Fornecedor!");
            } else {
                int adicionado = ps.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Fornecedor editado!");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    private void remover() {
        int confirma = JOptionPane.showConfirmDialog(null, "Deseja realmente remover?", "Atenção!", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from tb_fornecedores where id_forn = ?";
            try {
                ps = conexao.prepareStatement(sql);
                ps.setString(1, txt_id.getText());
                int apagado = ps.executeUpdate();
                if (apagado > 0) {
                    pesquisar();
                    JOptionPane.showMessageDialog(null, "Fornecedor removido!");
                    limparCampos();

                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    private void pesquisar() {
        String sql = "select id_forn as ID, nome_forn as Nome, razao_forn as Razão_Social, "
                + "cnpj_forn as CNPJ, fone_forn as Telefone, email_forn as Email, "
                + "cep_forn as CEP, cidade_forn as Cidade, "
                + "uf_forn as UF, end_forn as Endereço, num_forn as Número "
                + "from tb_fornecedores where nome_forn like ?";

        try {
            ps = conexao.prepareStatement(sql);
            ps.setString(1, txt_pesquisaFornecedores.getText() + "%");
            rs = ps.executeQuery();

            tbl_fornecedores.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
    }

    public void setar_campos() {
        int setar = tbl_fornecedores.getSelectedRow();
        txt_id.setText(tbl_fornecedores.getModel().getValueAt(setar, 0).toString());
        txt_nome.setText(tbl_fornecedores.getModel().getValueAt(setar, 1).toString());
        txt_razao.setText(tbl_fornecedores.getModel().getValueAt(setar, 2).toString());
        txt_cnpj.setText(tbl_fornecedores.getModel().getValueAt(setar, 3).toString());
        txt_telefone.setText(tbl_fornecedores.getModel().getValueAt(setar, 4).toString());
        txt_email.setText(tbl_fornecedores.getModel().getValueAt(setar, 5).toString());
        txt_cep.setText(tbl_fornecedores.getModel().getValueAt(setar, 6).toString());
        txt_cidade.setText(tbl_fornecedores.getModel().getValueAt(setar, 7).toString());
        cb_uf.setSelectedItem(tbl_fornecedores.getModel().getValueAt(setar, 8).toString());
        txt_endereco.setText(tbl_fornecedores.getModel().getValueAt(setar, 9).toString());
        txt_num.setText(tbl_fornecedores.getModel().getValueAt(setar, 10).toString());

        btn_salvar.setEnabled(false);
    }

    private void limparCampos() {
        txt_id.setText(null);
        txt_nome.setText(null);
        txt_razao.setText(null);
        txt_cnpj.setText(null);
        txt_telefone.setText(null);
        txt_email.setText(null);
        txt_cep.setText(null);
        txt_cidade.setText(null);
        cb_uf.setSelectedItem("PR");
        txt_endereco.setText(null);
        txt_num.setText(null);
        btn_salvar.setEnabled(true);
    }

    private void buscarCep() {
        String logradouro = "";
        String tipoLogradouro = "";
        String bairro = "";
        String resultado = null;
        String cep = txt_cep.getText();

        try {
            URL url = new URL("http://cep.republicavirtual.com.br/web_cep.php?cep=" + cep + "&formato=xml");
            SAXReader xml = new SAXReader();
            Document documento = xml.read(url);
            Element root = documento.getRootElement();

            for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
                Element element = it.next();
                if (element.getQualifiedName().equals("cidade")) {
                    txt_cidade.setText(element.getText());
                }
                if (element.getQualifiedName().equals("uf")) {
                    cb_uf.setSelectedItem(element.getText());
                }
                if (element.getQualifiedName().equals("tipo_logradouro")) {
                    tipoLogradouro = element.getText();
                }
                if (element.getQualifiedName().equals("logradouro")) {
                    logradouro = element.getText();
                }
                if (element.getQualifiedName().equals("bairro")) {
                    bairro = element.getText();
                }
                if (element.getQualifiedName().equals("resultado")) {
                    resultado = element.getText();
                    if (resultado.equals("1")) {

                    } else {
                        txt_cep.requestFocus();
                        JOptionPane.showMessageDialog(null, "CEP não encontrado!");

                    }
                }

            }

            txt_endereco.setText(tipoLogradouro + " " + logradouro + ", " + bairro);

        } catch (Exception e) {
            System.out.println(e);

        }
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
        endereco = new javax.swing.JLabel();
        cnpj = new javax.swing.JLabel();
        txt_nome = new javax.swing.JTextField();
        txt_razao = new javax.swing.JTextField();
        razao = new javax.swing.JLabel();
        cep = new javax.swing.JLabel();
        btn_buscaCep = new javax.swing.JButton();
        uf = new javax.swing.JLabel();
        cb_uf = new javax.swing.JComboBox<>();
        txt_cep = new javax.swing.JFormattedTextField();
        txt_id = new javax.swing.JFormattedTextField();
        txt_cnpj = new javax.swing.JFormattedTextField();
        txt_telefone = new javax.swing.JFormattedTextField();
        txt_endereco = new javax.swing.JTextField();
        cel = new javax.swing.JLabel();
        cidade = new javax.swing.JLabel();
        txt_cidade = new javax.swing.JTextField();
        numero = new javax.swing.JLabel();
        txt_num = new javax.swing.JTextField();
        btn_salvar = new javax.swing.JButton();
        btn_editar = new javax.swing.JButton();
        btn_remover = new javax.swing.JButton();
        pesquisar = new javax.swing.JLabel();
        txt_pesquisaFornecedores = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_fornecedores = new javax.swing.JTable();
        btn_buscaCep1 = new javax.swing.JButton();
        btn_remover1 = new javax.swing.JButton();
        icon = new javax.swing.JLabel();
        txt_email = new javax.swing.JTextField();

        setBorder(null);
        setClosable(true);
        setForeground(new java.awt.Color(240, 240, 240));
        setTitle("Fornecedores");
        setFrameIcon(null);
        setOpaque(true);
        setPreferredSize(new java.awt.Dimension(800, 690));
        setVisible(false);
        getContentPane().setLayout(null);

        id.setText("ID");
        getContentPane().add(id);
        id.setBounds(12, 16, 11, 16);

        nome.setText("Nome");
        getContentPane().add(nome);
        nome.setBounds(91, 16, 33, 16);

        telefone.setText("Telefone");
        getContentPane().add(telefone);
        telefone.setBounds(12, 84, 45, 16);

        endereco.setText("Endereço");
        getContentPane().add(endereco);
        endereco.setBounds(12, 108, 49, 16);

        cnpj.setText("CNPJ");
        getContentPane().add(cnpj);
        cnpj.setBounds(12, 60, 28, 16);

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
        getContentPane().add(txt_nome);
        txt_nome.setBounds(129, 13, 367, 22);

        txt_razao.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_razaoFocusGained(evt);
            }
        });
        txt_razao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_razaoActionPerformed(evt);
            }
        });
        getContentPane().add(txt_razao);
        txt_razao.setBounds(85, 35, 411, 22);

        razao.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        razao.setText("Razão Social");
        getContentPane().add(razao);
        razao.setBounds(12, 38, 68, 16);

        cep.setText("CEP");
        getContentPane().add(cep);
        cep.setBounds(180, 84, 21, 16);

        btn_buscaCep.setText("🔎");
        btn_buscaCep.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                btn_buscaCepFocusGained(evt);
            }
        });
        btn_buscaCep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscaCepActionPerformed(evt);
            }
        });
        btn_buscaCep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btn_buscaCepKeyPressed(evt);
            }
        });
        getContentPane().add(btn_buscaCep);
        btn_buscaCep.setBounds(319, 80, 45, 25);

        uf.setText("UF");
        getContentPane().add(uf);
        uf.setBounds(314, 130, 14, 16);

        cb_uf.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "  ", "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO" }));
        cb_uf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_ufActionPerformed(evt);
            }
        });
        getContentPane().add(cb_uf);
        cb_uf.setBounds(333, 127, 67, 22);

        try {
            txt_cep.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("#####-###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txt_cep.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txt_cep.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_cepFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_cepFocusLost(evt);
            }
        });
        txt_cep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_cepKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_cepKeyTyped(evt);
            }
        });
        getContentPane().add(txt_cep);
        txt_cep.setBounds(206, 81, 113, 22);

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
        getContentPane().add(txt_id);
        txt_id.setBounds(28, 13, 52, 22);

        try {
            txt_cnpj.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##.###.###/####-##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txt_cnpj.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_cnpjFocusGained(evt);
            }
        });
        txt_cnpj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_cnpjActionPerformed(evt);
            }
        });
        getContentPane().add(txt_cnpj);
        txt_cnpj.setBounds(52, 57, 124, 22);

        try {
            txt_telefone.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##)####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txt_telefone.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_telefoneFocusGained(evt);
            }
        });
        getContentPane().add(txt_telefone);
        txt_telefone.setBounds(62, 81, 113, 22);

        txt_endereco.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_enderecoFocusGained(evt);
            }
        });
        getContentPane().add(txt_endereco);
        txt_endereco.setBounds(66, 105, 430, 22);

        cel.setText("Email");
        getContentPane().add(cel);
        cel.setBounds(181, 60, 29, 16);

        cidade.setText("Cidade");
        getContentPane().add(cidade);
        cidade.setBounds(12, 130, 37, 16);

        txt_cidade.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_cidadeFocusGained(evt);
            }
        });
        txt_cidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_cidadeActionPerformed(evt);
            }
        });
        getContentPane().add(txt_cidade);
        txt_cidade.setBounds(61, 127, 248, 22);

        numero.setText("Nº");
        getContentPane().add(numero);
        numero.setBounds(371, 84, 14, 16);

        txt_num.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_numActionPerformed(evt);
            }
        });
        txt_num.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_numKeyTyped(evt);
            }
        });
        getContentPane().add(txt_num);
        txt_num.setBounds(390, 83, 106, 22);

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
        getContentPane().add(btn_salvar);
        btn_salvar.setBounds(181, 160, 70, 30);

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
        getContentPane().add(btn_editar);
        btn_editar.setBounds(263, 160, 70, 30);

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
        getContentPane().add(btn_remover);
        btn_remover.setBounds(345, 160, 70, 30);

        pesquisar.setText("Pesquisar");
        getContentPane().add(pesquisar);
        pesquisar.setBounds(12, 264, 50, 16);

        txt_pesquisaFornecedores.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_pesquisaFornecedoresKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_pesquisaFornecedoresKeyReleased(evt);
            }
        });
        getContentPane().add(txt_pesquisaFornecedores);
        txt_pesquisaFornecedores.setBounds(67, 261, 376, 22);

        tbl_fornecedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome", "Razão_Social", "CNPJ", "Telefone", "Email", "CEP", "Cidade", "UF", "Endereço", "Número"
            }
        ));
        tbl_fornecedores.setColumnSelectionAllowed(true);
        tbl_fornecedores.getTableHeader().setReorderingAllowed(false);
        tbl_fornecedores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_fornecedoresMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_fornecedores);
        tbl_fornecedores.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(12, 285, 771, 238);

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
        getContentPane().add(btn_buscaCep1);
        btn_buscaCep1.setBounds(443, 260, 45, 25);

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
        getContentPane().add(btn_remover1);
        btn_remover1.setBounds(99, 160, 70, 30);

        icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gvcsystem/img/gvc_system_logo_login.png"))); // NOI18N
        getContentPane().add(icon);
        icon.setBounds(580, 40, 147, 201);
        getContentPane().add(txt_email);
        txt_email.setBounds(215, 57, 281, 22);

        setBounds(0, 0, 800, 690);
    }// </editor-fold>//GEN-END:initComponents


    private void cb_ufActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_ufActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cb_ufActionPerformed

    private void txt_nomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_nomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_nomeActionPerformed

    private void txt_idKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_idKeyTyped
        char c = evt.getKeyChar();
        if (c < '0' || c > '9') {
            evt.consume();
        }

    }//GEN-LAST:event_txt_idKeyTyped

    private void btn_buscaCepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscaCepActionPerformed
        buscarCep();

    }//GEN-LAST:event_btn_buscaCepActionPerformed

    private void txt_cidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_cidadeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_cidadeActionPerformed

    private void txt_idFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_idFocusGained
        // TODO add your handling code here:

    }//GEN-LAST:event_txt_idFocusGained

    private void txt_cnpjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_cnpjActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_cnpjActionPerformed

    private void txt_cnpjFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_cnpjFocusGained
        // TODO add your handling code here:

    }//GEN-LAST:event_txt_cnpjFocusGained

    private void txt_nomeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_nomeFocusGained
        // TODO add your handling code here:
        txt_nome.selectAll();
    }//GEN-LAST:event_txt_nomeFocusGained

    private void txt_razaoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_razaoFocusGained
        // TODO add your handling code here:
        txt_razao.selectAll();
    }//GEN-LAST:event_txt_razaoFocusGained

    private void txt_telefoneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_telefoneFocusGained
        // TODO add your handling code here:

    }//GEN-LAST:event_txt_telefoneFocusGained

    private void txt_cepFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_cepFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_cepFocusGained

    private void txt_enderecoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_enderecoFocusGained
        // TODO add your handling code here:
        txt_endereco.selectAll();
    }//GEN-LAST:event_txt_enderecoFocusGained

    private void txt_cidadeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_cidadeFocusGained
        // TODO add your handling code here:
        txt_cidade.selectAll();
    }//GEN-LAST:event_txt_cidadeFocusGained

    private void txt_numActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_numActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_numActionPerformed

    private void txt_cepKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_cepKeyPressed
        // TODO add your handling code here:

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            buscarCep();

        }
    }//GEN-LAST:event_txt_cepKeyPressed

    private void txt_numKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_numKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();
        if (c < '0' || c > '9')
            evt.consume();
    }//GEN-LAST:event_txt_numKeyTyped

    private void txt_cepKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_cepKeyTyped

    }//GEN-LAST:event_txt_cepKeyTyped

    private void btn_buscaCepFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btn_buscaCepFocusGained
        // TODO add your handling code here:


    }//GEN-LAST:event_btn_buscaCepFocusGained

    private void txt_cepFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_cepFocusLost
        // TODO add your handling code here:

    }//GEN-LAST:event_txt_cepFocusLost

    private void btn_buscaCepKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btn_buscaCepKeyPressed
        // TODO add your handling code here:

    }//GEN-LAST:event_btn_buscaCepKeyPressed

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

    private void txt_pesquisaFornecedoresKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pesquisaFornecedoresKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_pesquisaFornecedoresKeyPressed

    private void txt_pesquisaFornecedoresKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pesquisaFornecedoresKeyReleased
        // TODO add your handling code here:
        pesquisar();
    }//GEN-LAST:event_txt_pesquisaFornecedoresKeyReleased

    private void tbl_fornecedoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_fornecedoresMouseClicked
        // TODO add your handling code here:
        limparCampos();
        setar_campos();
    }//GEN-LAST:event_tbl_fornecedoresMouseClicked

    private void txt_razaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_razaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_razaoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bg_tipo;
    private javax.swing.JButton btn_buscaCep;
    private javax.swing.JButton btn_buscaCep1;
    private javax.swing.JButton btn_editar;
    private javax.swing.JButton btn_remover;
    private javax.swing.JButton btn_remover1;
    private javax.swing.JButton btn_salvar;
    private javax.swing.JComboBox<String> cb_uf;
    private javax.swing.JLabel cel;
    private javax.swing.JLabel cep;
    private javax.swing.JLabel cidade;
    private javax.swing.JLabel cnpj;
    private javax.swing.JLabel endereco;
    private javax.swing.JLabel icon;
    private javax.swing.JLabel id;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel nome;
    private javax.swing.JLabel numero;
    private javax.swing.JLabel pesquisar;
    private javax.swing.JLabel razao;
    private javax.swing.JTable tbl_fornecedores;
    private javax.swing.JLabel telefone;
    private javax.swing.JFormattedTextField txt_cep;
    private javax.swing.JTextField txt_cidade;
    private javax.swing.JFormattedTextField txt_cnpj;
    private javax.swing.JTextField txt_email;
    private javax.swing.JTextField txt_endereco;
    private javax.swing.JFormattedTextField txt_id;
    private javax.swing.JTextField txt_nome;
    private javax.swing.JTextField txt_num;
    private javax.swing.JTextField txt_pesquisaFornecedores;
    private javax.swing.JTextField txt_razao;
    private javax.swing.JFormattedTextField txt_telefone;
    private javax.swing.JLabel uf;
    // End of variables declaration//GEN-END:variables
}
