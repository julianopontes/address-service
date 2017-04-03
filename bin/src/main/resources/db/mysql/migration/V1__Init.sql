create table address (
	ID bigint not null auto_increment,
	CEP integer not null,
	CITY varchar(255) not null,
	COMPLEMENT varchar(255),
	NEIGHBORHOOD varchar(255),
	NUMBER varchar(30) not null,
	STATE varchar(2) not null,
	STREET varchar(255) not null,
	primary key (ID)
);

alter table address
	add constraint UK_ADDRESS  unique (CEP, STREET);