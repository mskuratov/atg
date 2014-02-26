



create table crs_rnow_url (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	crs_rnow_url	varchar(254)	null
,constraint crs_rnow_pk primary key (id,asset_version))



go
