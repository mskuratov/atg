



create table crs_rnow_xlate (
	translation_id	varchar(40)	not null,
	url	varchar(254)	default null
,constraint crs_rnow_xlate_p primary key (translation_id));


create table crs_rnow_url_xlate (
	id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
,constraint crs_rnow_xlt_p primary key (id,locale)
,constraint crs_rnow_xlate_f foreign key (translation_id) references crs_rnow_xlate (translation_id));

create index crs_rnow_xlt_tr_id on crs_rnow_url_xlate (translation_id);
commit;


