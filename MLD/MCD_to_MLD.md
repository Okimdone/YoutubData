#------------------------------------------------------------
# Table: Channel
#------------------------------------------------------------

 Channel( __channels_id__, title, description, customUrl, publishedAt, defaultLanguage, country, viewCount, commentCount, subscriberCount, videoCount)

F_ch = (
	__channels_id__ ==> title, description, customUrl, publishedAt, defaultLanguage, country, viewCount, commentCount, subscriberCount , videoCount
;	title			==> __channels_id__, description, customUrl, publishedAt, defaultLanguage, country, viewCount, commentCount, subscriberCount , videoCount 
;	customUrl 		==> __channels_id__ , title, description, publishedAt, defaultLanguage, country, viewCount, commentCount,
	subscriberCount, videoCount
)

#------------------------------------------------------------
# Table: Video
#------------------------------------------------------------

 Video( __video_id__, publishedAt, title, description, defaultLanguage, defaultAudioLanguage, viewCount, likeCount, dislikeCount, commentCount, durationMs, creationTime, #channels_id)

F_v = (
	__video_id__ ==> publishedAt, title, description, defaultLanguage, defaultAudioLanguage, viewCount, likeCount, dislikeCount, commentCount, durationMs, creationTime, #channels_id
)

#------------------------------------------------------------
# Table: Query
#------------------------------------------------------------

 Query( __query_id__, mot, date_from, date_to)

F_q = (
	__query_id__ ==> mot, date_from, date_to
)

#------------------------------------------------------------
# Table: Trouver
#------------------------------------------------------------

 Trouver(**#video_id ,#query_id** )

F_Tr = (
	#video_id ,#query_id ==> __video_id__, __query_id__;
)

#------------------------------------------------------------
# Table: Comments
#------------------------------------------------------------

 Comments( __comments_id__, textOriginal, likeCount, publishedAt, updatedAt, totalReplyCount, #video_id, #comments_id_parent, #channels_id)

F_co = (
	__comments_id__ 	 ==> textOriginal, likeCount, publishedAt, updatedAt, totalReplyCount, #video_id, #channels_id, #comments_id_parent
;	__comments_id__ , videoId ==>  totalReplyCount
)