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
public class TelaInternaClientes extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    private static TelaInternaClientes telaInternaClientes;

    public static TelaInternaClientes getInstancia() {
        if (telaInternaClientes == null) {
            telaInternaClientes = new TelaInternaClientes();
        }
        return telaInternaClientes;

    }

    public TelaInternaClientes() {
        initComponents();
        conexao = ModuloConexao.conector();
        cb_tipo.setSelectedItem("PF");
        txt_cpf.setEnabled(true);
        txt_cnpj.setEnabled(false);
        txt_razao.setEnabled(false);

        cb_uf.setSelectedItem("PR");

        txt_nome.requestFocusInWindow();

    }

    private void buscar() {
        String sql = "select * from tb_clientes where id_cliente=?";
        try {
            ps = conexao.prepareStatement(sql);
            ps.setString(1, txt_id.getText());
            rs = ps.executeQuery();
            if (rs.next()) {
                txt_nome.setText(rs.getString(2));
                txt_endereco.setText(rs.getString(3));
                System.out.println(rs.getString(4));
                if (rs.getString(4) == "PF") {
                    cb_tipo.setSelectedItem("PF");
                } else {
                    cb_tipo.setSelectedItem("PJ");
                }

                txt_cpf.setText(rs.getString(5));
                txt_cnpj.setText(rs.getString(6));
                txt_razao.setText(rs.getString(7));
                txt_cep.setText(rs.getString(8));
                txt_cidade.setText(rs.getString(9));
                txt_cel.setText(rs.getString(12));
                cb_uf.setSelectedItem(rs.getString(10));
                txt_num.setText(rs.getString(11));
                txt_telefone.setText(rs.getString(13));

            } else {
                JOptionPane.showMessageDialog(null, "Cliente não cadastrado!");
                limparCampos();

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    private void adicionar() {
        String sql = "insert into tb_clientes(nome_cliente, "
                + "end_cliente, tipo_cliente, cpf_cliente, cnpj_cliente, "
                + "razao_cliente, cep_cliente, cidade_cliente, uf_cliente, "
                + "num_cliente, cel_cliente, fone_cliente) values(?, ?, ?, "
                + "?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, txt_nome.getText());
            ps.setString(2, txt_endereco.getText());
            ps.setString(3, cb_tipo.getSelectedItem().toString());

            /*
            if (cb_tipo.toString() == "PF") {
                ps.setString(3, "PF");
            } else {
                ps.setString(3, "PJ");
            }*/
            ps.setString(4, txt_cpf.getText().replaceAll("[.-]", ""));
            ps.setString(5, txt_cnpj.getText().replaceAll("[.|-]", "").replace("/", ""));
            ps.setString(6, txt_razao.getText());
            ps.setString(7, txt_cep.getText().replaceAll("[-]", ""));
            ps.setString(8, txt_cidade.getText());
            ps.setString(9, cb_uf.getSelectedItem().toString());
            ps.setString(10, txt_num.getText());
            ps.setString(11, txt_cel.getText().replaceAll("[()-]", ""));
            ps.setString(12, txt_telefone.getText().replaceAll("[()-]", ""));

            if (txt_nome.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha o nome do Cliente!");
            } else {
                int adicionado = ps.executeUpdate();
                if (adicionado > 0) {

                    rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        txt_id.setText("" + id);

                    }
                    JOptionPane.showMessageDialog(null, "Cliente cadastrado!");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    private void editar() {
        String sql = "update tb_clientes set nome_cliente = ?, "
                + "end_cliente = ?, tipo_cliente = ?, cpf_cliente = ?, cnpj_cliente = ?, "
                + "razao_cliente = ?, cep_cliente = ?, cidade_cliente = ?, uf_cliente = ?, "
                + "num_cliente = ?, cel_cliente = ?, fone_cliente = ? where id_cliente = ?";
        try {
            ps = conexao.prepareStatement(sql);
            ps.setString(1, txt_nome.getText());
            ps.setString(2, txt_endereco.getText());
            ps.setString(3, cb_tipo.getSelectedItem().toString());
            ps.setString(4, txt_cpf.getText().replaceAll("[.-]", ""));
            ps.setString(5, txt_cnpj.getText().replaceAll("[.|-]", "").replace("/", ""));
            ps.setString(6, txt_razao.getText());
            ps.setString(7, txt_cep.getText().replaceAll("[-]", ""));
            ps.setString(8, txt_cidade.getText());
            ps.setString(9, cb_uf.getSelectedItem().toString());
            ps.setString(10, txt_num.getText());
            ps.setString(11, txt_cel.getText().replaceAll("[()-]", ""));
            ps.setString(12, txt_telefone.getText().replaceAll("[()-]", ""));
            ps.setString(13, txt_id.getText());

            if (txt_id.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha o ID do Cliente!");
            } else if (txt_nome.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha o nome do Cliente!");
            } else {
                int adicionado = ps.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Cliente editado!");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    private void remover() {
        int confirma = JOptionPane.showConfirmDialog(null, "Deseja realmente remover?", "Atenção!", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from tb_clientes where id_cliente = ?";
            try {
                ps = conexao.prepareStatement(sql);
                ps.setString(1, txt_id.getText());
                int apagado = ps.executeUpdate();
                if (apagado > 0) {
                    pesquisar();
                    JOptionPane.showMessageDialog(null, "Cliente removido!");
                    limparCampos();

                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    private void pesquisar() {
        String sql = "select id_cliente as ID, nome_cliente as Nome, end_cliente as Endereço, "
                + "tipo_cliente as Tipo, cpf_cliente as CPF, cnpj_cliente as CNPJ,"
                + "razao_cliente as Razão_Social, cep_cliente as CEP, cidade_cliente as Cidade,"
                + "uf_cliente as UF, num_cliente as Número, cel_cliente as Celular, "
                + "fone_cliente as Telefone from tb_clientes where nome_cliente like ?";

        try {
            ps = conexao.prepareStatement(sql);
            ps.setString(1, txt_pesquisaCliente.getText() + "%");
            rs = ps.executeQuery();

            tbl_clientes.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
    }
    
    private void pendencias(){
        String sql = "select id_venda as ID, descricao as Descrição, preco_m2 as ValorMetro, "
                + "preco_unit as ValorUnit, quantidade as Qtd, preco_total as ValorTotal, "
                + "dt_emissao as DtEmissão from tb_venda where id_cliente like ? and pago = 'não'"
                + "order by dt_emissao asc";
        
        try {
            ps = conexao.prepareStatement(sql);
            ps.setString(1, txt_id.getText());
            rs = ps.executeQuery();
            
            tbl_pendencias.setModel(DbUtils.resultSetToTableModel(rs));
            soma_pendencias();
            
            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
            for(int i = 2; i <=5; i++){
                tbl_pendencias.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void soma_pendencias(){
        double soma = 0;
        for(int i = 0; i < tbl_pendencias.getRowCount(); i++){
            soma = soma + Double.parseDouble(tbl_pendencias.getValueAt(i, 5).toString());
        }
        
        lblSoma.setText(Double.toString(soma));
    }

    public void setar_campos() {
        int setar = tbl_clientes.getSelectedRow();
        txt_id.setText(tbl_clientes.getModel().getValueAt(setar, 0).toString());
        txt_nome.setText(tbl_clientes.getModel().getValueAt(setar, 1).toString());
        txt_endereco.setText(tbl_clientes.getModel().getValueAt(setar, 2).toString());

        switch (tbl_clientes.getModel().getValueAt(setar, 3).toString()) {
            case "PF":
                cb_tipo.setSelectedIndex(0);
                break;
            case "PJ":
                cb_tipo.setSelectedIndex(1);
                break;
        }
        /*if (tbl_clientes.getModel().getValueAt(setar, 3).toString() == "PF") {
            cb_tipo.setSelectedItem("PF");
        } else {          
            cb_tipo.setSelectedItem("PJ");
        }*/

        txt_cpf.setText(tbl_clientes.getModel().getValueAt(setar, 4).toString());
        txt_cnpj.setText(tbl_clientes.getModel().getValueAt(setar, 5).toString());
        txt_razao.setText(tbl_clientes.getModel().getValueAt(setar, 6).toString());
        txt_cep.setText(tbl_clientes.getModel().getValueAt(setar, 7).toString());
        txt_cidade.setText(tbl_clientes.getModel().getValueAt(setar, 8).toString());
        cb_uf.setSelectedItem(tbl_clientes.getModel().getValueAt(setar, 9).toString());
        txt_num.setText(tbl_clientes.getModel().getValueAt(setar, 10).toString());
        txt_cel.setText(tbl_clientes.getModel().getValueAt(setar, 11).toString());
        txt_telefone.setText(tbl_clientes.getModel().getValueAt(setar, 12).toString());

        pendencias();
        btn_salvar.setEnabled(false);
    }

    private void limparCampos() {
        txt_id.setText(null);
        txt_nome.setText(null);
        txt_endereco.setText(null);
        txt_telefone.setText(null);
        cb_tipo.setSelectedItem("PF");
        txt_cpf.setText(null);
        txt_cnpj.setText(null);
        txt_razao.setText(null);
        txt_cep.setText(null);
        txt_cidade.setText(null);
        txt_cel.setText(null);
        cb_uf.setSelectedItem("PR");
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
        tipo = new javax.swing.JLabel();
        telefone = new javax.swing.JLabel();
        endereco = new javax.swing.JLabel();
        cpf = new javax.swing.JLabel();
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
        txt_cpf = new javax.swing.JFormattedTextField();
        txt_cnpj = new javax.swing.JFormattedTextField();
        txt_telefone = new javax.swing.JFormattedTextField();
        txt_endereco = new javax.swing.JTextField();
        cel = new javax.swing.JLabel();
        txt_cel = new javax.swing.JFormattedTextField();
        cidade = new javax.swing.JLabel();
        txt_cidade = new javax.swing.JTextField();
        numero = new javax.swing.JLabel();
        txt_num = new javax.swing.JTextField();
        btn_salvar = new javax.swing.JButton();
        btn_editar = new javax.swing.JButton();
        btn_remover = new javax.swing.JButton();
        pesquisar = new javax.swing.JLabel();
        txt_pesquisaCliente = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_clientes = new javax.swing.JTable();
        btn_buscaCep1 = new javax.swing.JButton();
        contas = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_pendencias = new javax.swing.JTable();
        btn_remover1 = new javax.swing.JButton();
        cb_tipo = new javax.swing.JComboBox<>();
        icon = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblSoma = new javax.swing.JLabel();

        setBorder(null);
        setClosable(true);
        setForeground(new java.awt.Color(240, 240, 240));
        setTitle("Clientes");
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
        nome.setBounds(12, 39, 33, 16);

        tipo.setText("Tipo:");
        getContentPane().add(tipo);
        tipo.setBounds(85, 16, 26, 16);

        telefone.setText("Telefone");
        getContentPane().add(telefone);
        telefone.setBounds(12, 86, 45, 16);

        endereco.setText("Endereço");
        getContentPane().add(endereco);
        endereco.setBounds(12, 111, 49, 16);

        cpf.setText("CPF");
        getContentPane().add(cpf);
        cpf.setBounds(199, 16, 21, 16);

        cnpj.setText("CNPJ");
        getContentPane().add(cnpj);
        cnpj.setBounds(339, 16, 28, 16);

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
        txt_nome.setBounds(50, 36, 446, 22);

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
        txt_razao.setBounds(85, 59, 411, 22);

        razao.setText("Razão Social");
        getContentPane().add(razao);
        razao.setBounds(12, 62, 68, 16);

        cep.setText("CEP");
        getContentPane().add(cep);
        cep.setBounds(305, 86, 21, 16);

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
        btn_buscaCep.setBounds(451, 82, 45, 25);

        uf.setText("UF");
        getContentPane().add(uf);
        uf.setBounds(307, 134, 14, 16);

        cb_uf.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "  ", "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO" }));
        cb_uf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_ufActionPerformed(evt);
            }
        });
        getContentPane().add(cb_uf);
        cb_uf.setBounds(326, 131, 67, 22);

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
        txt_cep.setBounds(331, 83, 113, 22);

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
            txt_cpf.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###.###.###-##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txt_cpf.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_cpfFocusGained(evt);
            }
        });
        txt_cpf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_cpfActionPerformed(evt);
            }
        });
        getContentPane().add(txt_cpf);
        txt_cpf.setBounds(225, 13, 100, 22);

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
        txt_cnpj.setBounds(372, 13, 124, 22);

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
        txt_telefone.setBounds(61, 83, 106, 22);

        txt_endereco.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_enderecoFocusGained(evt);
            }
        });
        getContentPane().add(txt_endereco);
        txt_endereco.setBounds(66, 108, 430, 22);

        cel.setText("Cel");
        getContentPane().add(cel);
        cel.setBounds(172, 86, 17, 16);

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
        getContentPane().add(txt_cel);
        txt_cel.setBounds(194, 83, 106, 22);

        cidade.setText("Cidade");
        getContentPane().add(cidade);
        cidade.setBounds(12, 134, 37, 16);

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
        txt_cidade.setBounds(54, 131, 248, 22);

        numero.setText("Nº");
        getContentPane().add(numero);
        numero.setBounds(405, 134, 14, 16);

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
        txt_num.setBounds(426, 131, 70, 22);

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
        btn_salvar.setBounds(177, 160, 70, 30);

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
        btn_editar.setBounds(255, 160, 70, 30);

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
        btn_remover.setBounds(333, 160, 70, 30);

        pesquisar.setText("Pesquisar");
        getContentPane().add(pesquisar);
        pesquisar.setBounds(12, 238, 50, 16);

        txt_pesquisaCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_pesquisaClienteKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_pesquisaClienteKeyReleased(evt);
            }
        });
        getContentPane().add(txt_pesquisaCliente);
        txt_pesquisaCliente.setBounds(67, 235, 376, 22);

        tbl_clientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome", "Endereço", "Tipo", "CPF", "CNPJ", "Razão_Social", "CEP", "Cidade", "UF", "Número", "Celular", "Telefone"
            }
        ));
        tbl_clientes.setColumnSelectionAllowed(true);
        tbl_clientes.getTableHeader().setReorderingAllowed(false);
        tbl_clientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_clientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_clientes);
        tbl_clientes.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(12, 266, 771, 164);

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
        btn_buscaCep1.setBounds(445, 234, 45, 25);

        contas.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        contas.setText("Pendências:");
        getContentPane().add(contas);
        contas.setBounds(12, 438, 65, 16);

        tbl_pendencias.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Descrição", "ValorMetro", "ValorUnit", "Qtd", "ValorTotal", "DtEmissao"
            }
        ));
        jScrollPane2.setViewportView(tbl_pendencias);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(12, 464, 771, 186);

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

        cb_tipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PF", "PJ" }));
        cb_tipo.setFocusable(false);
        cb_tipo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cb_tipoItemStateChanged(evt);
            }
        });
        cb_tipo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cb_tipoMouseClicked(evt);
            }
        });
        cb_tipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_tipoActionPerformed(evt);
            }
        });
        getContentPane().add(cb_tipo);
        cb_tipo.setBounds(116, 13, 65, 22);

        icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gvcsystem/img/gvc_system_logo_login.png"))); // NOI18N
        getContentPane().add(icon);
        icon.setBounds(575, 13, 147, 201);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("TOTAL:");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(679, 440, 40, 16);

        lblSoma.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSoma.setForeground(new java.awt.Color(255, 0, 0));
        lblSoma.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSoma.setText("0,00");
        getContentPane().add(lblSoma);
        lblSoma.setBounds(719, 440, 60, 16);

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

    private void txt_cpfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_cpfActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_cpfActionPerformed

    private void txt_cpfFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_cpfFocusGained
        // TODO add your handling code here:

    }//GEN-LAST:event_txt_cpfFocusGained

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

    private void txt_celFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_celFocusGained
        // TODO add your handling code here:

    }//GEN-LAST:event_txt_celFocusGained

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

    private void txt_pesquisaClienteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pesquisaClienteKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_pesquisaClienteKeyPressed

    private void txt_pesquisaClienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pesquisaClienteKeyReleased
        // TODO add your handling code here:
        pesquisar();
    }//GEN-LAST:event_txt_pesquisaClienteKeyReleased

    private void tbl_clientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_clientesMouseClicked
        // TODO add your handling code here:
        limparCampos();
        setar_campos();
    }//GEN-LAST:event_tbl_clientesMouseClicked

    private void cb_tipoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cb_tipoItemStateChanged

        switch (cb_tipo.getSelectedItem().toString()) {
            case "PF":
                txt_cpf.setEnabled(true);
                txt_cnpj.setEnabled(false);
                txt_razao.setEnabled(false);
                break;

            case "PJ":
                txt_cnpj.setEnabled(true);
                txt_razao.setEnabled(true);
                txt_cpf.setEnabled(false);

                break;
        }
    }//GEN-LAST:event_cb_tipoItemStateChanged

    private void cb_tipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_tipoActionPerformed
        // TODO add your handling code here:


    }//GEN-LAST:event_cb_tipoActionPerformed

    private void cb_tipoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cb_tipoMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_cb_tipoMouseClicked

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
    private javax.swing.JComboBox<String> cb_tipo;
    private javax.swing.JComboBox<String> cb_uf;
    private javax.swing.JLabel cel;
    private javax.swing.JLabel cep;
    private javax.swing.JLabel cidade;
    private javax.swing.JLabel cnpj;
    private javax.swing.JLabel contas;
    private javax.swing.JLabel cpf;
    private javax.swing.JLabel endereco;
    private javax.swing.JLabel icon;
    private javax.swing.JLabel id;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblSoma;
    private javax.swing.JLabel nome;
    private javax.swing.JLabel numero;
    private javax.swing.JLabel pesquisar;
    private javax.swing.JLabel razao;
    private javax.swing.JTable tbl_clientes;
    private javax.swing.JTable tbl_pendencias;
    private javax.swing.JLabel telefone;
    private javax.swing.JLabel tipo;
    private javax.swing.JFormattedTextField txt_cel;
    private javax.swing.JFormattedTextField txt_cep;
    private javax.swing.JTextField txt_cidade;
    private javax.swing.JFormattedTextField txt_cnpj;
    private javax.swing.JFormattedTextField txt_cpf;
    private javax.swing.JTextField txt_endereco;
    private javax.swing.JFormattedTextField txt_id;
    private javax.swing.JTextField txt_nome;
    private javax.swing.JTextField txt_num;
    private javax.swing.JTextField txt_pesquisaCliente;
    private javax.swing.JTextField txt_razao;
    private javax.swing.JFormattedTextField txt_telefone;
    private javax.swing.JLabel uf;
    // End of variables declaration//GEN-END:variables
}
