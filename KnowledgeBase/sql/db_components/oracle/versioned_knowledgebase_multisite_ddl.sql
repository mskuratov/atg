



create table crs_rnow_url (
	asset_version	number(19)	not null,
	id	varchar2(40)	not null,
	crs_rnow_url	varchar2(254)	null
,constraint crs_rnow_pk primary key (id,asset_version));




