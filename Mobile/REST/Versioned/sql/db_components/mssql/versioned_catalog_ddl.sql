


--  @version $Id: //hosting-blueprint/MobileCommerce/version/10.2/server/MobileCommerce/sql/ddlgen/catalog_ddl.xml#1 $$Change: 735822 $

create table crs_mobile_img (
	asset_version	numeric(19)	not null,
	promo_content_id	varchar(40)	not null,
	device_name	varchar(254)	null,
	url	varchar(254)	null)


create table crs_mobile_desc (
	asset_version	numeric(19)	not null,
	promo_content_id	varchar(40)	not null,
	device_name	varchar(254)	null,
	url	varchar(254)	null)



go
