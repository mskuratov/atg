


--  @version $Id: //hosting-blueprint/MobileCommerce/version/10.2/server/MobileCommerce/sql/ddlgen/catalog_ddl.xml#1 $$Change: 735822 $

create table crs_mobile_img (
	promo_content_id	varchar(40)	not null,
	device_name	varchar(254)	default null,
	url	varchar(254)	default null);


create table crs_mobile_desc (
	promo_content_id	varchar(40)	not null,
	device_name	varchar(254)	default null,
	url	varchar(254)	default null);

commit;


