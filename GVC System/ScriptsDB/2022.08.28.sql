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
	drop cel_cliente;

alter table tb_clientes
	add column cel_cliente varchar(11);
    
alter table tb_clientes
	drop fone_cliente;
    
alter table tb_clientes
	add column fone_cliente varchar(10);
    
select * from tb_clientes;