



create table crs_rnow_xlate (
	translation_id	varchar2(40)	not null,
	url	varchar2(254)	null
,constraint crs_rnow_xlate_p primary key (translation_id));


create table crs_rnow_url_xlate (
	id	varchar2(40)	not null,
	locale	varchar2(40)	not null,
	translation_id	varchar2(40)	not null
,constraint crs_rnow_xlt_p primary key (id,locale)
,constraint crs_rnow_xlate_f foreign key (translation_id) references crs_rnow_xlate (translation_id));

create index crs_rnow_xlt_tr_id on crs_rnow_url_xlate (translation_id);



