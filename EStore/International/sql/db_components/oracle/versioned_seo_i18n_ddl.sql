


--  @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/International/sql/ddlgen/seo_i18n_ddl.xml#1 $$Change: 735822 $

create table crs_seo_xlate (
	asset_version	number(19)	not null,
	workspace_id	varchar2(40)	not null,
	branch_id	varchar2(40)	not null,
	is_head	number(1)	not null,
	version_deleted	number(1)	not null,
	version_editable	number(1)	not null,
	pred_version	number(19)	null,
	checkin_date	date	null,
	translation_id	varchar2(40)	not null,
	title	varchar2(254)	null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null,
	keywords	varchar2(254)	null
,constraint crs_seo_xlate_pk primary key (translation_id,asset_version));

create index crs_seo_xlate_wsx on crs_seo_xlate (workspace_id);
create index crs_seo_xlate_cix on crs_seo_xlate (checkin_date);

create table crs_seo_seo_xlate (
	asset_version	number(19)	not null,
	seo_tag_id	varchar2(40)	not null,
	locale	varchar2(40)	not null,
	translation_id	varchar2(40)	not null
,constraint crs_seo_tag_xlt_pk primary key (seo_tag_id,locale,asset_version));




