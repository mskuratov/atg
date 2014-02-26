


--  @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/sql/ddlgen/multisite_ddl.xml#3 $$Change: 788809 $

create table crs_site_attribute (
	id	varchar(40)	not null,
	resource_bundle	varchar(254)	null,
	prod_threshold	integer	null,
	page_size	integer	null,
	css_file	varchar(254)	null,
	large_site_icon	varchar(254)	null,
	default_country_code	varchar(2)	null,
	emailafriend	tinyint	null,
	backinstock_addr	varchar(254)	null,
	newpass_addr	varchar(254)	null,
	orderconfirm_addr	varchar(254)	null,
	ordershipped_addr	varchar(254)	null,
	changepass_addr	varchar(254)	null,
	registereduser_addr	varchar(254)	null,
	promo_addr	varchar(254)	null,
	price_slider_min	integer	null,
	price_slider_max	integer	null,
	channel	varchar(254)	null
,constraint crs_site_attr_p primary key (id))


create table crs_bill_codes (
	id	varchar(40)	not null,
	country_codes	varchar(40)	not null,
	sequence_num	integer	not null
,constraint crs_bill_codes_p primary key (id,sequence_num)
,constraint crs_bill_codes_f foreign key (id) references site_configuration (id))


create table crs_non_bill_codes (
	id	varchar(40)	not null,
	country_codes	varchar(40)	not null,
	sequence_num	integer	not null
,constraint crs_non_bill_c_p primary key (id,sequence_num)
,constraint crs_non_bill_c_f foreign key (id) references site_configuration (id))


create table crs_ship_codes (
	id	varchar(40)	not null,
	country_codes	varchar(40)	not null,
	sequence_num	integer	not null
,constraint crs_ship_codes_p primary key (id,sequence_num)
,constraint crs_ship_codes_f foreign key (id) references site_configuration (id))


create table crs_non_ship_codes (
	id	varchar(40)	not null,
	country_codes	varchar(40)	not null,
	sequence_num	integer	not null
,constraint crs_non_ship_c_p primary key (id,sequence_num)
,constraint crs_non_ship_c_f foreign key (id) references site_configuration (id))


create table crs_site_languages (
	id	varchar(40)	not null,
	languages	varchar(40)	not null,
	sequence_num	integer	not null
,constraint crs_site_lang_p primary key (id,sequence_num)
,constraint crs_site_lang_f foreign key (id) references site_configuration (id))



go
