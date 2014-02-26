



create table crs_rnow_xlate (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	datetime	null,
	translation_id	varchar(40)	not null,
	url	varchar(254)	null
,constraint crs_rnow_xlate_p primary key (translation_id,asset_version))

create index crs_rnow_xlate_wsx on crs_rnow_xlate (workspace_id)
create index crs_rnow_xlate_cix on crs_rnow_xlate (checkin_date)

create table crs_rnow_url_xlate (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
,constraint crs_rnow_xlt_p primary key (id,locale,asset_version))

create index crs_rnow_xlt_tr_id on crs_rnow_url_xlate (translation_id)


go
