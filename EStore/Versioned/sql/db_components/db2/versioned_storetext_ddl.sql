


--  @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/sql/ddlgen/storetext_ddl.xml#1 $$Change: 735822 $

create table crs_store_text (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	numeric(1)	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	default null,
	checkin_date	timestamp	default null,
	text_id	varchar(40)	not null,
	key_id	varchar(254)	not null,
	tag	varchar(40)	default null,
	text_type	integer	default null
,constraint crs_txt_key_p primary key (text_id,key_id,asset_version));

create index crs_txt_key_id on crs_store_text (key_id);
create index crs_store_text_wsx on crs_store_text (workspace_id);
create index crs_store_text_cix on crs_store_text (checkin_date);

create table crs_store_short_txt (
	asset_version	numeric(19)	not null,
	text_id	varchar(40)	not null,
	text_template	varchar(4000)	default null
,constraint crs_shrt_txt_key_p primary key (text_id,asset_version));


create table crs_store_long_txt (
	asset_version	numeric(19)	not null,
	text_id	varchar(40)	not null,
	text_template	varchar(4000)	default null
,constraint crs_lng_txt_key_p primary key (text_id,asset_version));


create table crs_store_list_txt (
	asset_version	numeric(19)	not null,
	list_id	varchar(40)	not null,
	sequence_num	integer	not null,
	text_id	varchar(40)	not null
,constraint crs_lst_txt_key_p primary key (list_id,sequence_num,asset_version));

commit;


