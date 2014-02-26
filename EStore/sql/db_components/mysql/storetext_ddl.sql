


--  @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/sql/ddlgen/storetext_ddl.xml#1 $$Change: 735822 $

create table crs_store_text (
	text_id	varchar(40)	not null,
	key_id	varchar(254)	not null,
	tag	varchar(40)	null,
	text_type	integer	null
,constraint crs_txt_key_p primary key (text_id,key_id));

create index crs_txt_key_id on crs_store_text (key_id);

create table crs_store_short_txt (
	text_id	varchar(40)	not null,
	text_template	longtext	null
,constraint crs_shrt_txt_key_p primary key (text_id));


create table crs_store_long_txt (
	text_id	varchar(40)	not null,
	text_template	longtext	null
,constraint crs_lng_txt_key_p primary key (text_id));


create table crs_store_list_txt (
	list_id	varchar(40)	not null,
	sequence_num	integer	not null,
	text_id	varchar(40)	not null
,constraint crs_lst_txt_key_p primary key (list_id,sequence_num));

commit;


