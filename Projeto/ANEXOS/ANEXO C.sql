create database dbgvc;

use dbgvc;
create table tbusers(
	iduser int primary key,
    user varchar(50) NOT NULL,
    telefone varchar(15),
    login varchar(15) NOT NULL unique,
    senha varchar(15) NOT NULL
);

insert into tbusers(iduser, user, telefone, login, senha) 
values(1, 'Administrador', '99999-9999', 'admin', '123');

insert into tbusers(iduser, user, telefone, login, senha) 
values(2, 'Pedro Jambers Trevisani', '99163-5102', 'pedro', '123');

insert into tbusers(iduser, user, telefone, login, senha) 
values(3, 'Claudemir Rodrigues Trevisani', '98413-9747', 'claudemir', '123');

select * from tbusers;

DELETE FROM tbusers where iduser = 1;

create table tbclientes(
	idcliente int primary key auto_increment,
	nomecliente varchar(50) NOT NULL,
    endcliente varchar(100),
    fonecliente varchar(15),
    emailcli varchar(50)
    );
    
ALTER TABLE tbusers CHANGE COLUMN user nome varchar(50);

DROP TABLE tbusers;

create table tb_usuarios(
	id_usuario int primary key auto_increment,
    nome_usuario varchar(50) NOT NULL,
    fone_usuario varchar(15),
    login_usuario varchar(15) NOT NULL unique,
    senha_usuario varchar(15) NOT NULL
);

drop table tbclientes;

create table tb_clientes(
	id_cliente int primary key auto_increment,
    nome_cliente varchar(50) not null,
    end_cliente varchar(50),
	fone_cliente varchar(15),
    email_cliente varchar(50)
);

alter table tb_clientes
	add column tipo_cliente varchar(2) not null,
    add column cpf_cliente varchar(11),
    add column cnpj_cliente varchar(14),
    add column razao_cliente varchar(100) not null,
    add column cep_cliente varchar(8),
    add column cidade_cliente varchar(50),
    add column cel_cliente varchar(10),
    add column uf_cliente varchar(2),
    add column num_cliente varchar(15)    
;

alter table tb_clientes
	drop column email_cliente;


insert into tb_clientes(nome_cliente, end_cliente, fone_cliente, email_cliente) values ('Diogo', 'Avenida Brasil 1004', '99662-57741', 'diogo@hotmail.com');

insert into tb_usuarios(nome_usuario, fone_usuario, login_usuario, senha_usuario) 
values('Administrador', '99999-9999', 'admin', '123');

insert into tb_usuarios(nome_usuario, fone_usuario, login_usuario, senha_usuario) 
values('Pedro Jambers Trevisani', '99163-5102', 'pedro', '123');

insert into tb_usuarios(nome_usuario, fone_usuario, login_usuario, senha_usuario) 
values('Claudemir Rodrigues Trevisani', '98413-9747', 'claudemir', '123');

select * from tb_usuarios;

insert into tb_clientes(nome_cliente, end_cliente, fone_cliente, email_cliente) 
values ('Edson', 'Rua Guaraná', '98425-6345', 'edson@hotmail.com');

insert into tb_clientes(nome_cliente, end_cliente, fone_cliente, email_cliente) 
values ('João', 'Rua do Professor', '9995-5588', 'joao@hotmail.com');

insert into tb_clientes(nome_cliente, end_cliente, fone_cliente, cpf_cliente, cnpj_cliente, razao_cliente, cep_cliente, cidade_cliente, cel_cliente, uf_cliente, num_cliente)
values ('Pedro', 'Rua Florianópolis Zona 02', '4430191305', '12970405970', '12345678910110', 'GVC Impressão Digital', '87200310', 'Cianorte', '4491635102', 'PR', '1970');

select * from tb_clientes;

delete from tb_clientes where id_cliente = 3;

alter table tb_clientes
	modify column end_cliente varchar(200);
    
describe tb_clientes;

describe tb_usuarios;

alter table tb_clientes
	modify column nome_cliente varchar(200),
    modify column razao_cliente varchar(200);

alter table tb_clientes
	modify column razao_cliente varchar(100);
    
alter table tb_usuarios
	add column email_usuario varchar(200);
    
select * from tb_usuarios;

insert into tb_usuarios(nome_cliente, end_cliente, fone_cliente, email_cliente) 
values ('João', 'Rua do Professor', '9995-5588', 'joao@hotmail.com') ;




create table tb_clientes(
	id_cliente int primary key auto_increment,
    nome_cliente varchar(50) not null,
    end_cliente varchar(50),
	fone_cliente varchar(15),
    email_cliente varchar(50)
);

create table tb_venda(
	id_venda int primary key auto_increment,
    descricao varchar(200) not null,
    tamanho decimal(10,3),
    preco_unit decimal(10,2),
    quantidade int,
    preco_total decimal(10,2) not null,
    pago boolean not null,
    dt_emissao timestamp default current_timestamp,
    id_cliente int not null, 
    foreign key(id_cliente) references tb_clientes(id_cliente)
);

insert tb_venda(descricao, tamanho, preco_unit, quantidade, preco_total, pago, id_cliente) 
values('Adesivo', 1.000, 120.00, 2, 240.00, true, 6);

select * from tb_venda;

alter table tb_venda
	modify column pago varchar(3);
    
ALTER TABLE tb_venda 
	CHANGE COLUMN preco_unit preco_m2 decimal(10,2);
    
ALTER TABLE tb_venda
	add column preco_unit decimal(10,2);
    
select * from tb_venda where date(dt_emissao) = curdate();

create table tb_fornecedores(
	id_forn int primary key auto_increment,
    nome_forn varchar(100) not null,
    razao_forn varchar(100),
    cnpj_forn varchar(14),
    fone_forn varchar(15),
    email_forn varchar(100),
    cep_forn varchar(8),
	cidade_forn varchar(50),
    uf_forn varchar(2),
    end_forn varchar(50),
    num_forn varchar(15)    
);

select * from tb_fornecedores;

create table tb_produto(
	id_produto int primary key auto_increment,
    nome_produto varchar(100) not null,
    acabamento_produto varchar(50),
    rolo_produto varchar(100),
    gramatura_produto varchar(50),
    peso_produto varchar(100),
    preco_produto varchar(10000)
);

create table tb_materia(
	id_materia int primary key auto_increment,
    nome_materia varchar(255) not null,
    tipo_materia varchar(255) not null,
    preco_materia varchar(10000) not null,
    disponibilidade boolean not null,
    descricao varchar (255),
	id_forn int not null, 
    foreign key(id_forn) references tb_fornecedores(id_forn)
);

describe tb_clientes;
describe tb_fornecedores;
describe tb_produtos;
describe tb_usuarios;
describe tb_venda;
describe tb_materia;


alter table tb_materia
	modify column disponibilidade varchar(25) NOT NULL;
    
alter table tb_materia
	add column nome_forn varchar(100);
    
create table tb_contas(
	id_conta int primary key auto_increment,
    descricao varchar(255) not null,
    valor varchar(10000) not null,
    dt_validade timestamp,
    situacao varchar (255),
	id_forn int not null, 
    foreign key(id_forn) references tb_fornecedores(id_forn)
);

alter table tb_contas
	add column cnpj_forn varchar(14);
    
alter table tb_contas
	add column razao_forn varchar(100);    
    
alter table tb_contas
	modify column dt_validade date;
    
describe tb_contas;