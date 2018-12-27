ALTER TABLE Query_plage DISABLE CONSTRAINT qry_plge_qry_id_FK;
ALTER TABLE Video DISABLE CONSTRAINT v_ch_id_FK;
ALTER TABLE Trouver DISABLE CONSTRAINT Tr_v_id_FK;
ALTER TABLE Trouver DISABLE CONSTRAINT Tr_qry_plage_id_FK;
ALTER TABLE Comments DISABLE CONSTRAINT co_ch_id_FK;
ALTER TABLE Comment_video DISABLE CONSTRAINT cv_co_id_FK;
ALTER TABLE Comment_video DISABLE CONSTRAINT cv_v_id_FK;
ALTER TABLE Comment_reponse DISABLE CONSTRAINT cr_co_id_FK;
ALTER TABLE Comment_reponse DISABLE CONSTRAINT cr_co_p_id_FK;


drop table Comment_reponse;
drop table Comment_video;
drop table comments;
drop table trouver;
drop table video;
drop table channel;
drop table Query_plage;
drop table query;