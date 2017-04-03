    drop table ADDRESS if exists;

    create table ADDRESS (
        ID bigint generated by default as identity (start with 1),
        CEP integer not null,
        CITY varchar(255) not null,
        COMPLEMENT varchar(255),
        NEIGHBORHOOD varchar(255),
        NUMBER varchar(30) not null,
        STATE varchar(2) not null,
        STREET varchar(255) not null,
        primary key (ID)
    );

    alter table ADDRESS 
        add constraint UK_ADDRESS  unique (CEP, STREET);
