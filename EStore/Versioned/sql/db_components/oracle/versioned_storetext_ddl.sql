


--  @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/sql/ddlgen/storetext_ddl.xml#1 $$Change: 735822 $

create table crs_store_text (
	asset_version	number(19)	not null,
	workspace_id	varchar2(40)	not null,
	branch_id	varchar2(40)	not null,
	is_head	number(1)	not null,
	version_deleted	number(1)	not null,
	version_editable	number(1)	not null,
	pred_version	number(19)	null,
	checkin_date	date	null,
	text_id	varchar2(40)	not null,
	key_id	varchar2(254)	not null,
	tag	varchar2(40)	null,
	text_type	number(10)	null
,constraint crs_txt_key_p primary key (text_id,key_id,asset_version));

create index crs_txt_key_id on crs_store_text (key_id);
create index crs_store_text_wsx on crs_store_text (workspace_id);
create index crs_store_text_cix on crs_store_text (checkin_date);

create table crs_store_short_txt (
	asset_version	number(19)	not null,
	text_id	varchar2(40)	not null,
	text_template	clob	null
,constraint crs_shrt_txt_key_p primary key (text_id,asset_version));


create table crs_store_long_txt (
	asset_version	number(19)	not null,
	text_id	varchar2(40)	not null,
	text_template	clob	null
,constraint crs_lng_txt_key_p primary key (text_id,asset_version));


create table crs_store_list_txt (
	asset_version	number(19)	not null,
	list_id	varchar2(40)	not null,
	sequence_num	integer	not null,
	text_id	varchar2(40)	not null
,constraint crs_lst_txt_key_p primary key (list_id,sequence_num,asset_version));




