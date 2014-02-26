


--  @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/sql/ddlgen/crs_order_ddl.xml#2 $$Change: 788278 $

      alter table dcspp_credit_card modify credit_card_number varchar(80);
    

create table crs_order (
	order_id	varchar(40)	not null,
	oms_order_id	varchar(40)	null,
	status_change_date	datetime	null,
	processing_date	datetime	null
,constraint crs_order_p primary key (order_id)
,constraint crs_order_f foreign key (order_id) references dcspp_order (order_id));


create table crs_hrd_ship_group (
	shipping_group_id	varchar(40)	not null,
	carrier_code	varchar(40)	null,
	weight	double precision	null
,constraint crs_hrd_shp_grp_p primary key (shipping_group_id)
,constraint crs_hrdshp_grp_f1 foreign key (shipping_group_id) references dcspp_ship_group (shipping_group_id));


create table crs_order_sub_req (
	order_sub_req_id	varchar(40)	not null,
	order_ack_time_out	integer	null,
	max_order_submits	integer	null,
	client_id	varchar(40)	null
,constraint crs_ord_sub_req_p primary key (order_sub_req_id));


create table crs_oms_segments (
	order_id	varchar(40)	not null,
	sequence_num	integer	not null,
	oms_segment	nvarchar(4000)	null
,constraint crs_oms_segments_p primary key (order_id,sequence_num)
,constraint crs_oms_segments_f foreign key (order_id) references dcspp_order (order_id));


create table crs_item_price (
	item_price_info_id	varchar(40)	not null,
	tax_price_info_id	varchar(40)	null
,constraint crs_item_price_p primary key (item_price_info_id)
,constraint crs_item_price_f foreign key (item_price_info_id) references dcspp_amount_info (amount_info_id));


create table crs_tax_price (
	amount_info_id	varchar(40)	not null,
	district_tax	double precision	null
,constraint crs_tax_price_p primary key (amount_info_id)
,constraint crs_tax_price_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id));


create table crs_ship_price (
	ship_price_info_id	varchar(40)	not null,
	tax_price_info_id	varchar(40)	null
,constraint crs_ship_price_p primary key (ship_price_info_id)
,constraint crs_ship_price_f foreign key (ship_price_info_id) references dcspp_amount_info (amount_info_id));

commit;


