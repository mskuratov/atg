


--  @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/International/sql/ddlgen/promotion_i18n_ddl.xml#1 $$Change: 735822 $
alter session set NLS_LENGTH_SEMANTICS='CHAR';

create table crs_prm_xlate (
	translation_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null
,constraint crs_prm_xlate_p primary key (translation_id));


create table crs_prm_prm_xlate (
	promotion_id	varchar2(40)	not null,
	locale	varchar2(40)	not null,
	translation_id	varchar2(40)	not null
,constraint crs_prm_prm_xlt_p primary key (promotion_id,locale)
,constraint crs_prm_xlate_f foreign key (translation_id) references crs_prm_xlate (translation_id));

create index crs_prm_xlt_tr_id on crs_prm_prm_xlate (translation_id);

create table crs_cq_xlate (
	translation_id	varchar2(40)	not null,
	name	varchar2(254)	null
,constraint crs_cq_xlate_p primary key (translation_id));


create table crs_cq_cq_xlate (
	close_qualif_id	varchar2(40)	not null,
	locale	varchar2(40)	not null,
	translation_id	varchar2(40)	not null
,constraint crs_cq_cq_xlt_p primary key (close_qualif_id,locale)
,constraint crs_cq_cq_xlate_f foreign key (translation_id) references crs_cq_xlate (translation_id));

create index crs_cq_xlt_tr_id on crs_cq_cq_xlate (translation_id);
alter session set NLS_LENGTH_SEMANTICS='BYTE';



