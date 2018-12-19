
#1NF tables as representations of relations -Wikipedia-
    - There's no top-to-bottom ordering to the rows.
    - There's no left-to-right ordering to the 4columns.
    - There are no duplicate rows.
    - Every row-and-column intersection contains exactly one value from the applicable domain (and nothing else).
    - All columns are regular [i.e. rows have no hidden components such as row IDs, object IDs, or hidden timestamps].

-------------------------------------------------------------


#------------------------------------------------------------
# Table: Channel
#------------------------------------------------------------

 Channel( __channels_id__, title , description, customUrl , publishedAt, defaultLanguage, country, viewCount, commentCount, subscriberCount, videoCount)

F_ch = (
	__channels_id__ ==> description, publishedAt, defaultLanguage, country, viewCount, commentCount, subscriberCount , videoCount , title, customUrl  )

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

 Query(__query_id__, mots)
 Query_plage(__Query_plage_id__, #query_id, date_from , date_to)

#------------------------------------------------------------
# Table: Trouver
#------------------------------------------------------------

 Trouver(**#video_id ,#__Query_plage_id__** )

 F_tr = (**#video_id ,#__Query_plage_id__** ==> #video_id ,#query_id)

#------------------------------------------------------------
# Table: Comments
#------------------------------------------------------------

 Comments( __comments_id__, textOriginal, likeCount, publishedAt, updatedAt, #channels_id)

 F_Co = (__comments_id__ ==> textOriginal, likeCount, publishedAt, updatedAt, #channels_id)

##video_id can be null if the comment in a response to an other comment
 Comments_video(**#comments_id, #video_id**, totalReplyCount )

 F_Co_v = (**#comments_id, #video_id** ==> totalReplyCount )

##comments_id_Parent can be null if the comment is a commentthread comment and has no parent comment id 
 Comments_reponse(**#comments_id, #comments_id_Parent**)

 F_Cp_r = (**#comments_id, #comments_id_Parent** ==> #comments_id, #comments_id_Parent)
 