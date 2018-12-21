CREATE TABLE Query
(   query_id NUMERIC(10) CONSTRAINT qry_id_pk PRIMARY KEY
,   mots VARCHAR2(200) CONSTRAINT qry_mts_NN NOT NULL
);

CREATE TABLE Query_plage
(   Query_plage_id NUMERIC(10) CONSTRAINT qry_plge_id_PK PRIMARY KEY
,   query_id NUMERIC(10)       CONSTRAINT qry_plge_qry_id_FK REFERENCES Query(query_id) ON DELETE SET NULL
,   DATE_from DATE             CONSTRAINT qry_plge_dt_fm_NN NOT NULL
,   DATE_to DATE               CONSTRAINT qry_plge_dt_TO_NN NOT NULL
);

CREATE TABLE Channel
(   channel_id VARCHAR2(11) CONSTRAINT ch_id_PK PRIMARY KEY
,   title VARCHAR2(100)      CONSTRAINT ch_id_NN NOT NULL
,   description VARCHAR2(4000) CONSTRAINT ch_dscr_NN NOT NULL
,   customUrl  VARCHAR2(40)  CONSTRAINT ch_curl_NN NOT NULL
,   publishedAt DATE         CONSTRAINT ch_pat_NN NOT NULL
,   defaultLanguage VARCHAR2(10) CONSTRAINT ch_dl_NN NOT NULL
,   country VARCHAR2(29)     CONSTRAINT ch_ctr_NN NOT NULL
,   viewCount NUMERIC(12)    CONSTRAINT ch_vc_NN NOT NULL
,   commentCount NUMERIC(12)    DEFAULT NULL
,   subscriberCount NUMERIC(12) DEFAULT NULL
,   videoCount NUMERIC(12)      DEFAULT NULL
);

CREATE TABLE Video
(   video_id VARCHAR2(11) CONSTRAINT v_id_PK PRIMARY KEY
,   publishedAt DATE CONSTRAINT v_pat_NN NOT NULL
,   title VARCHAR2(100) CONSTRAINT v_title_NN NOT NULL
,   description VARCHAR2(4000) CONSTRAINT v_descr_NN NOT NULL
,   defaultLanguage VARCHAR2(20) CONSTRAINT v_dl_NN NOT NULL
,   defaultAudioLanguage VARCHAR2(20) CONSTRAINT v_dal_NN NOT NULL
,   viewCount NUMERIC(12) DEFAULT NULL
,   likeCount  NUMERIC(12) DEFAULT NULL
,   dislikeCount  NUMERIC(12) DEFAULT NULL
,   commentCount  NUMERIC(12) DEFAULT NULL
,   durationMs  NUMERIC(12) CONSTRAINT v_dms_NN NOT NULL
,   creationTime  VARCHAR2(25) CONSTRAINT v_ct_NN NOT NULL
,   channel_id VARCHAR2(11)
,       CONSTRAINT v_ch_id_FK FOREIGN KEY (channel_id) REFERENCES channel(channel_id) ON DELETE CASCADE
,       CONSTRAINT v_vld_CK CHECK ( likeCount + dislikeCount < viewCount)
);

CREATE TABLE Trouver
(   video_id VARCHAR2(10) CONSTRAINT Tr_v_id_FK REFERENCES video(video_id) ON DELETE CASCADE
,   Query_plage_id NUMERIC(10) CONSTRAINT Tr_qry_plage_id_FK REFERENCES Query_plage(Query_plage_id) ON DELETE CASCADE
,       CONSTRAINT tr_PK PRIMARY KEY (video_id, Query_plage_id)
);

CREATE TABLE Comments
(   comments_id VARCHAR2(12)    CONSTRAINT co_id_PK PRIMARY KEY
,   textOriginal VARCHAR2(4000) CONSTRAINT co_to_NN NOT NULL
,   likeCount NUMERIC(12)       DEFAULT NULL
,   publishedAt DATE            CONSTRAINT co_pat_NN NOT NULL
,   updatedAt DATE              CONSTRAINT co_uat_NN NOT NULL
,   channel_id VARCHAR2(11)      CONSTRAINT co_ch_id_FK REFERENCES channel(channel_id) ON DELETE CASCADE
);

CREATE TABLE Comments_video
(   comments_id VARCHAR2(12)    CONSTRAINT cv_co_id_FK REFERENCES comments(comments_id) ON DELETE CASCADE
,   video_id VARCHAR2(12)       CONSTRAINT cv_v_id_FK REFERENCES video(video_id) ON DELETE CASCADE
,   totalReplyCount NUMERIC(12) CONSTRAINT cv_trc_NN NOT NULL
,   CONSTRAINT cv_PK PRIMARY KEY (comments_id, video_id)
);

CREATE TABLE Comments_reponse
(   comments_id  VARCHAR2(12)       CONSTRAINT cr_co_id_FK REFERENCES comments(comments_id) ON DELETE CASCADE
,   comments_id_Parent VARCHAR2(12) CONSTRAINT cr_co_p_id_FK REFERENCES comments(comments_id) ON DELETE CASCADE 
,   CONSTRAINT cr_PK PRIMARY KEY(comments_id)
);