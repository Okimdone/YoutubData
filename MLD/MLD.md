# Table: Query
 Query(__query_id__, mots)
 
 Query_plage(__Query_plage_id__, #query_id, date_from , date_to)

# Table: Channel
 Channel( __channels_id__, title , description, customUrl , publishedAt, defaultLanguage, country, viewCount, commentCount, subscriberCount, videoCount)

# Table: Video
 Video( __video_id__, publishedAt, title, description, defaultLanguage, defaultAudioLanguage, viewCount, likeCount, dislikeCount, commentCount, durationMs, creationTime, #channels_id)

# Table: Trouver
 Trouver(**#video_id ,#Query_plage_id** )

# Table: Comments
 Comments( __comments_id__, textOriginal, likeCount, publishedAt, updatedAt, #channels_id)

# Commentthreads 
 Comments_video(**#comments_id, #video_id**, totalReplyCount )

# Comment replies
 Comments_reponse(**#comments_id, #comments_id_Parent**)
