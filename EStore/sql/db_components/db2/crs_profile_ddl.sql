


--  @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/sql/ddlgen/crs_profile_ddl.xml#2 $$Change: 788278 $

      alter table dps_user alter column login set data type varchar(255);
    

create table crs_user (
	user_id	varchar(40)	not null,
	external_id	varchar(40)	default null,
	number_of_orders	numeric(19,0)	default null,
	last_purchase_date	date	default null,
	referral_source	integer	default null,
	receive_promo_email	integer	default null
,constraint crs_user_p primary key (user_id)
,constraint crs_user_fk foreign key (user_id) references dps_user (id));


create table crs_items_bought (
	user_id	varchar(40)	not null,
	sequence_id	integer	not null,
	item	varchar(40)	not null
,constraint crs_item_bought_p primary key (user_id,sequence_id));


create table crs_email_recpient (
	email_recipient_id	varchar(40)	not null,
	email	varchar(255)	not null,
	user_id	varchar(40)	default null,
	source_code	varchar(40)	default null
,constraint crs_email_recpnt_p primary key (email_recipient_id));


create table crs_user_shipping (
	user_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	addr_id	varchar(40)	not null
,constraint crs_user_shpping_p primary key (user_id,tag)
,constraint crs_user_shppng_fk foreign key (user_id) references dps_user (id));

create index crs_u_shipaddr_idx on crs_user_shipping (addr_id);

create table crs_back_in_stock (
	id	varchar(40)	not null,
	email	varchar(255)	default null,
	catalog_ref_id	varchar(40)	default null,
	product_id	varchar(40)	default null,
	site_id	varchar(40)	default null,
	locale	varchar(40)	default null
,constraint crs_back_in_ntfy_p primary key (id));


create table crs_rec_viewed_prod (
	id	varchar(40)	not null,
	product_id	varchar(40)	default null,
	site_id	varchar(40)	default null,
	time_stamp	timestamp	not null
,constraint rec_viewed_prod_p primary key (id));


create table crs_rec_viewed (
	user_id	varchar(40)	not null,
	sequence_id	integer	not null,
	rec_viewed_prod	varchar(40)	not null
,constraint crs_rec_viewed_p primary key (user_id,sequence_id));

commit;


