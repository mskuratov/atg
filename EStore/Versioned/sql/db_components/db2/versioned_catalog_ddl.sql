


--  @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/sql/ddlgen/catalog_ddl.xml#2 $$Change: 742374 $

create table crs_sku (
	asset_version	numeric(19)	not null,
	sku_id	varchar(40)	not null,
	ship_exempt	numeric(1)	default null,
	gift_wrap_el	numeric(1)	default null,
	admin_display	varchar(254)	default null,
	max_quantity	integer	default null,
	margin	double precision	default null
,constraint crs_sku_p primary key (sku_id,asset_version)
,constraint crs_sku_c check (ship_exempt in (0,1))
,constraint crs_sku_c2 check (gift_wrap_el in (0,1)));


create table crs_clothing_sku (
	asset_version	numeric(19)	not null,
	sku_id	varchar(40)	not null,
	sku_size	varchar(254)	default null,
	color	varchar(254)	default null,
	color_swatch	varchar(40)	default null
,constraint crs_clothing_p primary key (sku_id,asset_version));


create table crs_furniture_sku (
	asset_version	numeric(19)	not null,
	sku_id	varchar(40)	not null,
	color_swatch	varchar(40)	default null,
	wood_finish	varchar(254)	default null
,constraint crs_furniture_p primary key (sku_id,asset_version));


create table crs_promo_content (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	numeric(1)	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	default null,
	checkin_date	timestamp	default null,
	promo_id	varchar(40)	not null,
	version	integer	not null,
	creation_date	timestamp	default null,
	start_date	timestamp	default null,
	end_date	timestamp	default null,
	marketing_priority	integer	default null,
	display_name	varchar(254)	default null,
	store_display_name	varchar(254)	default null,
	description	varchar(1000)	default null,
	long_description	varchar(4000)	default null,
	image_url	varchar(254)	default null,
	template_id	varchar(40)	default null,
	path	varchar(254)	default null,
	parent_folder_id	varchar(40)	default null,
	category_id	varchar(40)	default null,
	product_id	varchar(40)	default null,
	site_id	varchar(40)	default null,
	link_text	varchar(256)	default null,
	link_url	varchar(256)	default null,
	promotion_id	varchar(40)	default null
,constraint crs_promo_ctn_p primary key (promo_id,asset_version));

create index crs_promo_cont_wsx on crs_promo_content (workspace_id);
create index crs_promo_cont_cix on crs_promo_content (checkin_date);

create table crs_category (
	asset_version	numeric(19)	not null,
	category_id	varchar(40)	not null,
	feature_promo_id	varchar(40)	default null,
	hero_image_id	varchar(40)	default null
,constraint crs_category_p primary key (category_id,asset_version));


create table crs_cat_site_features (
	asset_version	numeric(19)	not null,
	category_id	varchar(40)	not null,
	site_feature_promo_id	varchar(40)	not null,
	site_id	varchar(40)	not null
,constraint crs_cat_site_features_p primary key (site_feature_promo_id,site_id,asset_version));


create table crs_cat_rel_prod (
	asset_version	numeric(19)	not null,
	category_id	varchar(40)	not null,
	sequence_num	integer	not null,
	product_id	varchar(40)	not null
,constraint crs_cat_relprod_p primary key (category_id,sequence_num,asset_version));


create table crs_prod_seen_in (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	numeric(1)	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	default null,
	checkin_date	timestamp	default null,
	seen_in_id	varchar(40)	not null,
	version	integer	not null,
	source_date	timestamp	default null,
	display_name	varchar(254)	default null,
	description	varchar(4000)	default null,
	source_name	varchar(254)	default null
,constraint crs_prod_seen_in_p primary key (seen_in_id,asset_version));

create index crs_prod_seen__wsx on crs_prod_seen_in (workspace_id);
create index crs_prod_seen__cix on crs_prod_seen_in (checkin_date);

create table crs_product (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	is_new	numeric(1)	default null,
	promo_tagline	varchar(254)	default null,
	brief_description	varchar(254)	default null,
	full_image_id	varchar(40)	default null,
	medium_image_id	varchar(40)	default null,
	average_customer_rating	integer	default null,
	usage_instructions	varchar(4000)	default null,
	as_seen_in	varchar(40)	default null,
	preorderable	numeric(1)	default null,
	preord_end_date	timestamp	default null,
	use_inv_for_preord	numeric(1)	default null,
	email_frnd_enabled	numeric(1)	default null
,constraint crs_product_p primary key (product_id,asset_version)
,constraint crs_product_c check (is_new in (0,1))
,constraint crs_product_c3 check (preorderable in (0,1))
,constraint crs_product_c4 check (use_inv_for_preord in (0,1)));


create table crs_discount_promo (
	asset_version	numeric(19)	not null,
	promotion_id	varchar(40)	not null,
	qualifier	integer	not null
,constraint crs_discount_pro_p primary key (promotion_id,asset_version));


create table crs_feature (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	numeric(1)	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	default null,
	checkin_date	timestamp	default null,
	feature_id	varchar(40)	not null,
	version	integer	not null,
	feature_name	varchar(254)	default null,
	start_date	timestamp	default null,
	end_date	timestamp	default null,
	display_name	varchar(254)	default null,
	description	varchar(1000)	default null,
	long_description	varchar(4000)	default null,
	hero_image_id	varchar(40)	default null,
	small_image_id	varchar(40)	default null,
	large_image_id	varchar(40)	default null,
	hidden	numeric(1)	default null
,constraint crs_feature_p primary key (feature_id,asset_version)
,constraint crs_feature_c check (hidden in (0,1)));

create index crs_feature_wsx on crs_feature (workspace_id);
create index crs_feature_cix on crs_feature (checkin_date);

create table crs_prd_features (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	feature_id	varchar(40)	not null
,constraint crs_prd_features_p primary key (product_id,sequence_num,asset_version));


create table crs_prd_tips (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	tip_text	varchar(2000)	not null
,constraint crs_prd_tips_p primary key (product_id,sequence_num,asset_version));


create table crs_prd_ship_cntry (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	country	varchar(40)	not null
,constraint crs_prd_shp_cnt_p primary key (product_id,country,asset_version));


create table crs_prd_nshp_cntry (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	country	varchar(40)	not null
,constraint crs_prd_nshp_cnt_p primary key (product_id,country,asset_version));


create table crs_catalog (
	asset_version	numeric(19)	not null,
	catalog_id	varchar(40)	not null,
	root_nav_cat	varchar(40)	not null
,constraint crs_catalog_p primary key (catalog_id,asset_version));

commit;


