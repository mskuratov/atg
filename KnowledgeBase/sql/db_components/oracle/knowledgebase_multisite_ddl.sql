



create table crs_rnow_url (
	id	varchar2(40)	not null,
	crs_rnow_url	varchar2(254)	null
,constraint crs_rnow_pk primary key (id)
,constraint crs_rnow_fk foreign key (id) references site_configuration (id));




