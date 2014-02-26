


--  @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/sql/ddlgen/catalog_ddl.xml#2 $$Change: 742374 $

create table crs_sku (
	sku_id	varchar(40)	not null,
	ship_exempt	tinyint	null,
	gift_wrap_el	tinyint	null,
	admin_display	varchar(254)	null,
	max_quantity	integer	null,
	margin	numeric(19,7)	null
,constraint crs_sku_p primary key (sku_id)
,constraint crs_sku_f foreign key (sku_id) references dcs_sku (sku_id)
,constraint crs_sku_c check (ship_exempt in (0,1))
,constraint crs_sku_c2 check (gift_wrap_el in (0,1)))


create table crs_clothing_sku (
	sku_id	varchar(40)	not null,
	sku_size	varchar(254)	null,
	color	varchar(254)	null,
	color_swatch	varchar(40)	null
,constraint crs_clothing_p primary key (sku_id)
,constraint crs_clothing_f foreign key (sku_id) references dcs_sku (sku_id)
,constraint crs_sku_fswch foreign key (color_swatch) references dcs_media (media_id))

create index crs_sku1_x on crs_clothing_sku (color_swatch)

create table crs_furniture_sku (
	sku_id	varchar(40)	not null,
	color_swatch	varchar(40)	null,
	wood_finish	varchar(254)	null
,constraint crs_furniture_p primary key (sku_id)
,constraint crs_furniture_f foreign key (sku_id) references dcs_sku (sku_id)
,constraint crs_sku_fswch2 foreign key (color_swatch) references dcs_media (media_id))

create index crs_sku2_x on crs_furniture_sku (color_swatch)

create table crs_promo_content (
	promo_id	varchar(40)	not null,
	version	integer	not null,
	creation_date	datetime	null,
	start_date	datetime	null,
	end_date	datetime	null,
	marketing_priority	integer	null,
	display_name	varchar(254)	null,
	store_display_name	varchar(254)	null,
	description	varchar(1000)	null,
	long_description	text	null,
	image_url	varchar(254)	null,
	template_id	varchar(40)	null,
	path	varchar(254)	null,
	parent_folder_id	varchar(40)	null,
	category_id	varchar(40)	null,
	product_id	varchar(40)	null,
	site_id	varchar(40)	null,
	link_text	varchar(256)	null,
	link_url	varchar(256)	null,
	promotion_id	varchar(40)	null
,constraint crs_promo_ctn_p primary key (promo_id)
,constraint crs_promo_ctn_fmed foreign key (template_id) references dcs_media (media_id)
,constraint crs_promo_ctn_ffol foreign key (parent_folder_id) references dcs_folder (folder_id)
,constraint crs_promo_ctn_fctg foreign key (category_id) references dcs_category (category_id)
,constraint crs_promo_ctn_fprd foreign key (product_id) references dcs_product (product_id)
,constraint crs_promo_ctn_fpro foreign key (promotion_id) references dcs_promotion (promotion_id))

create index crs_promcontent1_x on crs_promo_content (template_id)
create index crs_promcontent2_x on crs_promo_content (parent_folder_id)
create index crs_promcontent3_x on crs_promo_content (category_id)
create index crs_promcontent4_x on crs_promo_content (product_id)
create index crs_promcontent5_x on crs_promo_content (promotion_id)

create table crs_category (
	category_id	varchar(40)	not null,
	feature_promo_id	varchar(40)	null,
	hero_image_id	varchar(40)	null
,constraint crs_category_p primary key (category_id)
,constraint crs_category_fctg foreign key (category_id) references dcs_category (category_id)
,constraint crs_category_ffpr foreign key (feature_promo_id) references crs_promo_content (promo_id)
,constraint crs_category_fmed foreign key (hero_image_id) references dcs_media (media_id))

create index crs_category1_x on crs_category (feature_promo_id)
create index crs_category3_x on crs_category (hero_image_id)

create table crs_cat_site_features (
	category_id	varchar(40)	not null,
	site_feature_promo_id	varchar(40)	not null,
	site_id	varchar(40)	not null
,constraint crs_cat_site_features_p primary key (site_feature_promo_id,site_id)
,constraint crs_cat_site_features_fc foreign key (category_id) references dcs_category (category_id))


create table crs_cat_rel_prod (
	category_id	varchar(40)	not null,
	sequence_num	integer	not null,
	product_id	varchar(40)	not null
,constraint crs_cat_relprod_p primary key (category_id,sequence_num)
,constraint crs_cat_relprod_fc foreign key (category_id) references dcs_category (category_id)
,constraint crs_cat_relprod_fp foreign key (product_id) references dcs_product (product_id))

create index crs_cat_relprod1_x on crs_cat_rel_prod (product_id)

create table crs_prod_seen_in (
	seen_in_id	varchar(40)	not null,
	version	integer	not null,
	source_date	datetime	null,
	display_name	varchar(254)	null,
	description	varchar(4000)	null,
	source_name	varchar(254)	null
,constraint crs_prod_seen_in_p primary key (seen_in_id))


create table crs_product (
	product_id	varchar(40)	not null,
	is_new	tinyint	null,
	promo_tagline	varchar(254)	null,
	brief_description	varchar(254)	null,
	full_image_id	varchar(40)	null,
	medium_image_id	varchar(40)	null,
	average_customer_rating	integer	null,
	usage_instructions	varchar(4000)	null,
	as_seen_in	varchar(40)	null,
	preorderable	tinyint	null,
	preord_end_date	datetime	null,
	use_inv_for_preord	tinyint	null,
	email_frnd_enabled	tinyint	null
,constraint crs_product_p primary key (product_id)
,constraint crs_product_fpro foreign key (product_id) references dcs_product (product_id)
,constraint crs_product_ffmd foreign key (full_image_id) references dcs_media (media_id)
,constraint crs_product_fmmd foreign key (medium_image_id) references dcs_media (media_id)
,constraint crs_product_fsee foreign key (as_seen_in) references crs_prod_seen_in (seen_in_id)
,constraint crs_product_c check (is_new in (0,1))
,constraint crs_product_c3 check (preorderable in (0,1))
,constraint crs_product_c4 check (use_inv_for_preord in (0,1)))

create index crs_product1_x on crs_product (full_image_id)
create index crs_product2_x on crs_product (medium_image_id)
create index crs_product3_x on crs_product (as_seen_in)

create table crs_discount_promo (
	promotion_id	varchar(40)	not null,
	qualifier	integer	not null
,constraint crs_discount_pro_p primary key (promotion_id)
,constraint crs_discount_pro_f foreign key (promotion_id) references dcs_promotion (promotion_id))


create table crs_feature (
	feature_id	varchar(40)	not null,
	version	integer	not null,
	feature_name	varchar(254)	null,
	start_date	datetime	null,
	end_date	datetime	null,
	display_name	varchar(254)	null,
	description	varchar(1000)	null,
	long_description	varchar(4000)	null,
	hero_image_id	varchar(40)	null,
	small_image_id	varchar(40)	null,
	large_image_id	varchar(40)	null,
	hidden	tinyint	null
,constraint crs_feature_p primary key (feature_id)
,constraint crs_feature_fl foreign key (large_image_id) references dcs_media (media_id)
,constraint crs_feature_fs foreign key (small_image_id) references dcs_media (media_id)
,constraint crs_feature_ft foreign key (hero_image_id) references dcs_media (media_id)
,constraint crs_feature_c check (hidden in (0,1)))

create index crs_feature1_x on crs_feature (large_image_id)
create index crs_feature2_x on crs_feature (small_image_id)
create index crs_feature3_x on crs_feature (hero_image_id)

create table crs_prd_features (
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	feature_id	varchar(40)	not null
,constraint crs_prd_features_p primary key (product_id,sequence_num)
,constraint crs_prd_featres_ff foreign key (feature_id) references crs_feature (feature_id)
,constraint crs_prd_featres_fp foreign key (product_id) references dcs_product (product_id))

create index crs_prdfeatures1_x on crs_prd_features (feature_id)

create table crs_prd_tips (
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	tip_text	varchar(2000)	not null
,constraint crs_prd_tips_p primary key (product_id,sequence_num)
,constraint crs_prd_tips_f foreign key (product_id) references dcs_product (product_id))


create table crs_prd_ship_cntry (
	product_id	varchar(40)	not null,
	country	varchar(40)	not null
,constraint crs_prd_shp_cnt_p primary key (product_id,country)
,constraint crs_prd_shp_cnt_f foreign key (product_id) references dcs_product (product_id))


create table crs_prd_nshp_cntry (
	product_id	varchar(40)	not null,
	country	varchar(40)	not null
,constraint crs_prd_nshp_cnt_p primary key (product_id,country)
,constraint crs_prd_nshp_cnt_f foreign key (product_id) references dcs_product (product_id))


create table crs_catalog (
	catalog_id	varchar(40)	not null,
	root_nav_cat	varchar(40)	not null
,constraint crs_catalog_p primary key (catalog_id)
,constraint crs_catalog_f foreign key (root_nav_cat) references dcs_category (category_id))

create index crs_ctlrtnavcat1_x on crs_catalog (root_nav_cat)


go
