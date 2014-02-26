



create table crs_rnow_url (
	id	varchar(40)	not null,
	crs_rnow_url	varchar(254)	default null
,constraint crs_rnow_pk primary key (id)
,constraint crs_rnow_fk foreign key (id) references site_configuration (id));

commit;


