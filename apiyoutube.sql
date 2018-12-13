create table query (
Id_query int primary key,
mot varchar(10)
);

create table plage_date (
id_palge		Int primary key,		
date_from		Date,				
date_to		Date
);

create table  video (
	video_id Varchar(30)		primary key,
	snippet_publishedAt		Date,
	snippet_title		Varchar	(100),
	snippet_description		Varchar(5000),
	snippet_categoryId	Varchar(20)	,
	video_snippet_categoryId	Varchar(20)	,
	snippet_defaultAudioLanguage	Varchar(20),
	status_privacyStatus		Varchar(30)	,
	status_publishAt	Date	,
	status_license	Varchar	(30),
	statistics_viewCount	number (12),
	statistics_likeCount	number	(12),
	statistics_dislikeCount	number	(12),
	statistics_commentCount	number	(12),
	fileDetails_durationMs	number	(12),
	fileDetails_bitrateBps	number	(12),
	fileDetails_creationTime	Varchar	(25)

);

create table commentthread (

	commentthread_id Varchar(25)	,
	snippet_topLevelComment	Int,
	snippet_totalReplyCount	number(12),
	snippet_isPublic	varchar(4) , 
	replies_comments	Varchar(20),
    check( snippet_isPublic ='true'  or orsnippet_isPublic ='false' )
);

create table Comments(
	Comments_id		Varchar	(30) primary key ,
	snippet_authorDisplayName	Varchar(30),
	snippet_authorProfileImageUrl	Varchar(60),
	snippet_authorChannelUrl	Varchar	(60),
	snippet_authorChannelId_value	Varchar(30),
	snippet_channelId	Varchar	(30),
	snippet_textDisplay		Varchar	(30),
	snippet_textOriginal	Varchar(30),
	snippet_parentId	Varchar(30),
	snippet_likeCount	number(12),
	snippet_publishedAt		Date,
	snippet_updatedAt	Date
);

create table channels (
	channels_id	Varchar	(30) primary key,
	snippet_title		Varchar(30),
	snippet_description	Varchar(1000),
	snippet_customUrl	Varchar(40),
	snippet_publishedAt	Date,
	snippet_defaultLanguage	Varchar(10),
	snippet_country	Varchar(29),
	contentDetails_relatedPlaylists_likes	Varchar(12),
	contentDetails_relatedPlaylists_uploads	Varchar(12),
	statistics_viewCount	number(12),
	statistics_commentCount	number(12),
	statistics_subscriberCount	number	(12),
	statistics_videoCount	number	(12),
	contentOwnerDetails_contentOwner Varchar(30),
	contentOwnerDetails_timeLinked	Date
);





