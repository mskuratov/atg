


--  @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/International/sql/ddlgen/seo_i18n_ddl.xml#1 $$Change: 735822 $

create table crs_seo_xlate (
	translation_id	varchar2(40)	not null,
	title	varchar2(254)	null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null,
	keywords	varchar2(254)	null
,constraint crs_seo_xlate_pk primary key (translation_id));


create table crs_seo_seo_xlate (
	seo_tag_id	varchar2(40)	not null,
	locale	varchar2(40)	not null,
	translation_id	varchar2(40)	not null
,constraint crs_seo_tag_xlt_pk primary key (seo_tag_id,locale)
,constraint crs_seo_tag_xlt_fk foreign key (translation_id) references crs_seo_xlate (translation_id));

create index crs_seo_tag_xlt_id on crs_seo_seo_xlate (translation_id);



