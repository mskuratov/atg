


--  @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/sql/ddlgen/catalog_ddl.xml#2 $$Change: 742374 $

      alter session set NLS_LENGTH_SEMANTICS='CHAR';
    

create table crs_sku (
	asset_version	number(19)	not null,
	sku_id	varchar2(40)	not null,
	ship_exempt	number(1)	null,
	gift_wrap_el	number(1)	null,
	admin_display	varchar2(254)	null,
	max_quantity	integer	null,
	margin	number(19,7)	null
,constraint crs_sku_p primary key (sku_id,asset_version)
,constraint crs_sku_c check (ship_exempt in (0,1))
,constraint crs_sku_c2 check (gift_wrap_el in (0,1)));


create table crs_clothing_sku (
	asset_version	number(19)	not null,
	sku_id	varchar2(40)	not null,
	sku_size	varchar2(254)	null,
	color	varchar2(254)	null,
	color_swatch	varchar2(40)	null
,constraint crs_clothing_p primary key (sku_id,asset_version));


create table crs_furniture_sku (
	asset_version	number(19)	not null,
	sku_id	varchar2(40)	not null,
	color_swatch	varchar2(40)	null,
	wood_finish	varchar2(254)	null
,constraint crs_furniture_p primary key (sku_id,asset_version));


create table crs_promo_content (
	asset_version	number(19)	not null,
	workspace_id	varchar2(40)	not null,
	branch_id	varchar2(40)	not null,
	is_head	number(1)	not null,
	version_deleted	number(1)	not null,
	version_editable	number(1)	not null,
	pred_version	number(19)	null,
	checkin_date	date	null,
	promo_id	varchar2(40)	not null,
	version	integer	not null,
	creation_date	timestamp	null,
	start_date	timestamp	null,
	end_date	timestamp	null,
	marketing_priority	integer	null,
	display_name	varchar2(254)	null,
	store_display_name	varchar2(254)	null,
	description	varchar2(1000)	null,
	long_description	clob	null,
	image_url	varchar2(254)	null,
	template_id	varchar2(40)	null,
	path	varchar2(254)	null,
	parent_folder_id	varchar2(40)	null,
	category_id	varchar2(40)	null,
	product_id	varchar2(40)	null,
	site_id	varchar2(40)	null,
	link_text	varchar2(256)	null,
	link_url	varchar2(256)	null,
	promotion_id	varchar2(40)	null
,constraint crs_promo_ctn_p primary key (promo_id,asset_version));

create index crs_promo_cont_wsx on crs_promo_content (workspace_id);
create index crs_promo_cont_cix on crs_promo_content (checkin_date);

create table crs_category (
	asset_version	number(19)	not null,
	category_id	varchar2(40)	not null,
	feature_promo_id	varchar2(40)	null,
	hero_image_id	varchar2(40)	null
,constraint crs_category_p primary key (category_id,asset_version));


create table crs_cat_site_features (
	asset_version	number(19)	not null,
	category_id	varchar2(40)	not null,
	site_feature_promo_id	varchar2(40)	not null,
	site_id	varchar2(40)	not null
,constraint crs_cat_site_features_p primary key (site_feature_promo_id,site_id,asset_version));


create table crs_cat_rel_prod (
	asset_version	number(19)	not null,
	category_id	varchar2(40)	not null,
	sequence_num	integer	not null,
	product_id	varchar2(40)	not null
,constraint crs_cat_relprod_p primary key (category_id,sequence_num,asset_version));


create table crs_prod_seen_in (
	asset_version	number(19)	not null,
	workspace_id	varchar2(40)	not null,
	branch_id	varchar2(40)	not null,
	is_head	number(1)	not null,
	version_deleted	number(1)	not null,
	version_editable	number(1)	not null,
	pred_version	number(19)	null,
	checkin_date	date	null,
	seen_in_id	varchar2(40)	not null,
	version	integer	not null,
	source_date	timestamp	null,
	display_name	varchar2(254)	null,
	description	varchar2(4000)	null,
	source_name	varchar2(254)	null
,constraint crs_prod_seen_in_p primary key (seen_in_id,asset_version));

create index crs_prod_seen__wsx on crs_prod_seen_in (workspace_id);
create index crs_prod_seen__cix on crs_prod_seen_in (checkin_date);

create table crs_product (
	asset_version	number(19)	not null,
	product_id	varchar2(40)	not null,
	is_new	number(1)	null,
	promo_tagline	varchar2(254)	null,
	brief_description	varchar2(254)	null,
	full_image_id	varchar2(40)	null,
	medium_image_id	varchar2(40)	null,
	average_customer_rating	number(10)	null,
	usage_instructions	varchar2(4000)	null,
	as_seen_in	varchar2(40)	null,
	preorderable	number(1)	null,
	preord_end_date	timestamp	null,
	use_inv_for_preord	number(1)	null,
	email_frnd_enabled	number(1)	null
,constraint crs_product_p primary key (product_id,asset_version)
,constraint crs_product_c check (is_new in (0,1))
,constraint crs_product_c3 check (preorderable in (0,1))
,constraint crs_product_c4 check (use_inv_for_preord in (0,1)));


create table crs_discount_promo (
	asset_version	number(19)	not null,
	promotion_id	varchar2(40)	not null,
	qualifier	integer	not null
,constraint crs_discount_pro_p primary key (promotion_id,asset_version));


create table crs_feature (
	asset_version	number(19)	not null,
	workspace_id	varchar2(40)	not null,
	branch_id	varchar2(40)	not null,
	is_head	number(1)	not null,
	version_deleted	number(1)	not null,
	version_editable	number(1)	not null,
	pred_version	number(19)	null,
	checkin_date	date	null,
	feature_id	varchar2(40)	not null,
	version	integer	not null,
	feature_name	varchar2(254)	null,
	start_date	timestamp	null,
	end_date	timestamp	null,
	display_name	varchar2(254)	null,
	description	varchar2(1000)	null,
	long_description	varchar2(4000)	null,
	hero_image_id	varchar2(40)	null,
	small_image_id	varchar2(40)	null,
	large_image_id	varchar2(40)	null,
	hidden	number(1)	null
,constraint crs_feature_p primary key (feature_id,asset_version)
,constraint crs_feature_c check (hidden in (0,1)));

create index crs_feature_wsx on crs_feature (workspace_id);
create index crs_feature_cix on crs_feature (checkin_date);

create table crs_prd_features (
	asset_version	number(19)	not null,
	product_id	varchar2(40)	not null,
	sequence_num	integer	not null,
	feature_id	varchar2(40)	not null
,constraint crs_prd_features_p primary key (product_id,sequence_num,asset_version));


create table crs_prd_tips (
	asset_version	number(19)	not null,
	product_id	varchar2(40)	not null,
	sequence_num	integer	not null,
	tip_text	varchar2(2000)	not null
,constraint crs_prd_tips_p primary key (product_id,sequence_num,asset_version));


create table crs_prd_ship_cntry (
	asset_version	number(19)	not null,
	product_id	varchar2(40)	not null,
	country	varchar2(40)	not null
,constraint crs_prd_shp_cnt_p primary key (product_id,country,asset_version));


create table crs_prd_nshp_cntry (
	asset_version	number(19)	not null,
	product_id	varchar2(40)	not null,
	country	varchar2(40)	not null
,constraint crs_prd_nshp_cnt_p primary key (product_id,country,asset_version));


create table crs_catalog (
	asset_version	number(19)	not null,
	catalog_id	varchar2(40)	not null,
	root_nav_cat	varchar2(40)	not null
,constraint crs_catalog_p primary key (catalog_id,asset_version));


      alter session set NLS_LENGTH_SEMANTICS='BYTE';
    



