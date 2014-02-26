


--  @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/International/sql/ddlgen/promotion_i18n_ddl.xml#1 $$Change: 735822 $

create table crs_prm_xlate (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	datetime	null,
	translation_id	varchar(40)	not null,
	display_name	varchar(254)	null,
	description	varchar(254)	null
,constraint crs_prm_xlate_p primary key (translation_id,asset_version));

create index crs_prm_xlate_wsx on crs_prm_xlate (workspace_id);
create index crs_prm_xlate_cix on crs_prm_xlate (checkin_date);

create table crs_prm_prm_xlate (
	asset_version	numeric(19)	not null,
	promotion_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
,constraint crs_prm_prm_xlt_p primary key (promotion_id,locale,asset_version));

create index crs_prm_xlt_tr_id on crs_prm_prm_xlate (translation_id);

create table crs_cq_xlate (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	datetime	null,
	translation_id	varchar(40)	not null,
	name	varchar(254)	null
,constraint crs_cq_xlate_p primary key (translation_id,asset_version));

create index crs_cq_xlate_wsx on crs_cq_xlate (workspace_id);
create index crs_cq_xlate_cix on crs_cq_xlate (checkin_date);

create table crs_cq_cq_xlate (
	asset_version	numeric(19)	not null,
	close_qualif_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
,constraint crs_cq_cq_xlt_p primary key (close_qualif_id,locale,asset_version));

create index crs_cq_xlt_tr_id on crs_cq_cq_xlate (translation_id);
commit;


