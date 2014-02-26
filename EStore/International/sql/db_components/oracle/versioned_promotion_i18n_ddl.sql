


--  @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/International/sql/ddlgen/promotion_i18n_ddl.xml#1 $$Change: 735822 $

      alter session set NLS_LENGTH_SEMANTICS='CHAR';
    

create table crs_prm_xlate (
	asset_version	number(19)	not null,
	workspace_id	varchar2(40)	not null,
	branch_id	varchar2(40)	not null,
	is_head	number(1)	not null,
	version_deleted	number(1)	not null,
	version_editable	number(1)	not null,
	pred_version	number(19)	null,
	checkin_date	date	null,
	translation_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null
,constraint crs_prm_xlate_p primary key (translation_id,asset_version));

create index crs_prm_xlate_wsx on crs_prm_xlate (workspace_id);
create index crs_prm_xlate_cix on crs_prm_xlate (checkin_date);

create table crs_prm_prm_xlate (
	asset_version	number(19)	not null,
	promotion_id	varchar2(40)	not null,
	locale	varchar2(40)	not null,
	translation_id	varchar2(40)	not null
,constraint crs_prm_prm_xlt_p primary key (promotion_id,locale,asset_version));

create index crs_prm_xlt_tr_id on crs_prm_prm_xlate (translation_id);

create table crs_cq_xlate (
	asset_version	number(19)	not null,
	workspace_id	varchar2(40)	not null,
	branch_id	varchar2(40)	not null,
	is_head	number(1)	not null,
	version_deleted	number(1)	not null,
	version_editable	number(1)	not null,
	pred_version	number(19)	null,
	checkin_date	date	null,
	translation_id	varchar2(40)	not null,
	name	varchar2(254)	null
,constraint crs_cq_xlate_p primary key (translation_id,asset_version));

create index crs_cq_xlate_wsx on crs_cq_xlate (workspace_id);
create index crs_cq_xlate_cix on crs_cq_xlate (checkin_date);

create table crs_cq_cq_xlate (
	asset_version	number(19)	not null,
	close_qualif_id	varchar2(40)	not null,
	locale	varchar2(40)	not null,
	translation_id	varchar2(40)	not null
,constraint crs_cq_cq_xlt_p primary key (close_qualif_id,locale,asset_version));

create index crs_cq_xlt_tr_id on crs_cq_cq_xlate (translation_id);

      alter session set NLS_LENGTH_SEMANTICS='BYTE';
    



