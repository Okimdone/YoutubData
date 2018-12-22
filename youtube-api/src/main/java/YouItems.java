package src.main.java;

import com.google.api.client.util.DateTime;//For setPublishedBefore/From(DateTime x)
import com.google.api.services.youtube.model.*;//Comment-Video-Channel-CommentThread
import com.google.api.services.youtube.YouTube;//Youtube.xxxx.List()
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class YouItems{
    //Youtube Object Where all the magic comes from
    private static YouTube youtube;
    static {
        try {
            // Youtube s magic
            youtube = Oauth.getYouTubeService();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(2);
        }
    }

    private YouItems(){}

    /** Return a list of videos, searched for with the given query, and between the from - to dates  
     *  The list is in "published date" order 
    */
    public static List<SearchResult> getVideosByQuery(String query, Date dateFrom, Date dateTo)
    throws IOException
    {
        YouTube.Search.List searchListByKeywordRequest = youtube.search().list("id,snippet");
        //to change (it s set to 5 by default)
        searchListByKeywordRequest.setMaxResults(Long.parseLong("2"));
        searchListByKeywordRequest.setType("video");
        if (query != "")    searchListByKeywordRequest.setQ(query);
        searchListByKeywordRequest.setPublishedAfter(new DateTime(dateFrom));
        searchListByKeywordRequest.setPublishedBefore(new DateTime(dateTo ));
        // Possible Values (date, rating , relevance, title, videoCount, viewCount)
        //searchListByKeywordRequest.setOrder("date");//"relevance" by default.

        return (searchListByKeywordRequest.execute().getItems());  
    }      

    /** Takes a list of ids as a comma separated ids values and return an array of video*/
    public static Video getVideoItem(String videoId) 
    throws IOException {
        YouTube.Videos.List videoRequest = youtube.videos().list("snippet,contentDetails,statistics");
        if (videoId != "") videoRequest.setId(videoId);
        return videoRequest.execute().getItems().get(0);
    }
    
    /** Takes one channel id, and returns its equivalent informations */
    public static Channel getChannelItem(String channelId) 
    throws IOException {
        YouTube.Channels.List channelsListByIdRequest = youtube.channels().list("snippet,contentDetails,statistics");
        if ( channelId != "") channelsListByIdRequest.setId(channelId);
        return channelsListByIdRequest.execute().getItems().get(0);
    }

    /** Get the commentthreads by the given video Id */
    public static List<CommentThread> getCommentThreadsByVId(String videoId) 
    throws IOException {
        YouTube.CommentThreads.List commentThreadsListByVideoIdRequest = youtube.commentThreads().list("snippet,replies");
        if (videoId != "")  commentThreadsListByVideoIdRequest.setVideoId(videoId);
        commentThreadsListByVideoIdRequest.setOrder ("relevance");
        commentThreadsListByVideoIdRequest.setMaxResults(Long.parseLong("1"));
        return commentThreadsListByVideoIdRequest.execute().getItems();
    }

    /** get the comments items by the given comment parent_id */
    public static List<Comment> getCommentsByParentId(String parentId, Long replyNumber)
    throws IOException {
        YouTube.Comments.List commentsListRequest = youtube.comments().list("snippet");
        if (parentId != "") commentsListRequest.setParentId( parentId );
        commentsListRequest.setMaxResults( replyNumber );
        return commentsListRequest.execute().getItems();
    }
}