


--  @version $Id: //hosting-blueprint/MobileCommerce/version/10.2/server/MobileCommerce/sql/ddlgen/store_ddl.xml#3 $$Change: 796712 $

create table crsm_store_location (
	store_id	varchar2(40)	not null,
	latitude	number(9,6)	not null,
	longitude	number(9,6)	not null
,constraint crsm_store_p primary key (store_id)
,constraint crsm_store_f foreign key (store_id) references crs_store_location (store_id));




