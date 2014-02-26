



create table crs_rnow_xlate (
	asset_version	number(19)	not null,
	workspace_id	varchar2(40)	not null,
	branch_id	varchar2(40)	not null,
	is_head	number(1)	not null,
	version_deleted	number(1)	not null,
	version_editable	number(1)	not null,
	pred_version	number(19)	null,
	checkin_date	date	null,
	translation_id	varchar2(40)	not null,
	url	varchar2(254)	null
,constraint crs_rnow_xlate_p primary key (translation_id,asset_version));

create index crs_rnow_xlate_wsx on crs_rnow_xlate (workspace_id);
create index crs_rnow_xlate_cix on crs_rnow_xlate (checkin_date);

create table crs_rnow_url_xlate (
	asset_version	number(19)	not null,
	id	varchar2(40)	not null,
	locale	varchar2(40)	not null,
	translation_id	varchar2(40)	not null
,constraint crs_rnow_xlt_p primary key (id,locale,asset_version));

create index crs_rnow_xlt_tr_id on crs_rnow_url_xlate (translation_id);



