


--  @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/sql/ddlgen/multisite_ddl.xml#3 $$Change: 788809 $

create table crs_site_attribute (
	asset_version	number(19)	not null,
	id	varchar2(40)	not null,
	resource_bundle	varchar2(254)	null,
	prod_threshold	number(10)	null,
	page_size	number(10)	null,
	css_file	varchar2(254)	null,
	large_site_icon	varchar2(254)	null,
	default_country_code	varchar2(2)	null,
	emailafriend	number(1)	null,
	backinstock_addr	varchar2(254)	null,
	newpass_addr	varchar2(254)	null,
	orderconfirm_addr	varchar2(254)	null,
	ordershipped_addr	varchar2(254)	null,
	changepass_addr	varchar2(254)	null,
	registereduser_addr	varchar2(254)	null,
	promo_addr	varchar2(254)	null,
	price_slider_min	number(10)	null,
	price_slider_max	number(10)	null,
	channel	varchar2(254)	null
,constraint crs_site_attr_p primary key (id,asset_version));


create table crs_bill_codes (
	asset_version	number(19)	not null,
	id	varchar2(40)	not null,
	country_codes	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint crs_bill_codes_p primary key (id,sequence_num,asset_version));


create table crs_non_bill_codes (
	asset_version	number(19)	not null,
	id	varchar2(40)	not null,
	country_codes	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint crs_non_bill_c_p primary key (id,sequence_num,asset_version));


create table crs_ship_codes (
	asset_version	number(19)	not null,
	id	varchar2(40)	not null,
	country_codes	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint crs_ship_codes_p primary key (id,sequence_num,asset_version));


create table crs_non_ship_codes (
	asset_version	number(19)	not null,
	id	varchar2(40)	not null,
	country_codes	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint crs_non_ship_c_p primary key (id,sequence_num,asset_version));


create table crs_site_languages (
	asset_version	number(19)	not null,
	id	varchar2(40)	not null,
	languages	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint crs_site_lang_p primary key (id,sequence_num,asset_version));




