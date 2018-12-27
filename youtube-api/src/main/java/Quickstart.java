package src.main.java;

import src.jdbc.OracleSql;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.ArrayMap;

import com.google.api.services.youtube.model.*;//Comment-Video-Channel-CommentThread

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static src.main.java.YouItems.*;

public class Quickstart {

    static OracleSql sqlConnection = OracleSql.getOracleSql();

    /** Main program, searches for a (query + date interval) return (videoid, channelid) as a tuple
     *  loops throught each videoId and gets and stores the found information into the List<Video> videoItems
     *  searches for the channelIds and stores the found infomation in the List<Channel> channelItems
     *  searches for the CommentThreads for the given videoId and stores the data in the List<CommentThread> CommentThreadItems
     *  searches every CommentThreads for comments-replies and stores all found comments into List<Comment> CommentItems 
     *  searches every commentItem for a channelId and searches for that channelId s data to store it into List<Channel> channelItems  
     */
    public static void main(String[] args) {
        try {

            Scanner in = new Scanner(System.in);
            System.out.print("Enter the Query : ");
            String query = in.nextLine();
            // Search for <query>
            Date dFrom = new Date(/*year*/ 2018-1900,/*month*/ 11 - 1,/*Day*/ 12);
            Date dTo   = new Date(/*year*/ 2018-1900,/*month*/ 12 - 1 ,/*Day*/ 12);
            List<SearchResult> items = getVideosByQuery(query
                                                    , dFrom
                                                    , dTo
                                                    );

            sqlConnection.insert(query, dFrom, dTo);

System.out.println(" QUERY ITEM : " + query + ", DATEfrom : "+ dFrom +", DATEto" + dTo );
System.out.println("###############################################################\n");
            if(items != null){

                Video videoItem = null;
                // A list of channels informations that own the videoItem and their comments s owners
                List<Channel> channelItems = new ArrayList<Channel>(); 
                // A list of CommentThreads informations for everyvideo in the videoItems
                List<CommentThread> commentThreadItems = new ArrayList<CommentThread>();
                // A list of Comments informations for everyvideo in the CommentThreads
                List<Comment> commentItems = new ArrayList<Comment>();
                
                // Collectusefull information in the searched items (videoId and channelIds)
                // For Every item return by our search
                for (SearchResult item : items) {                    
                    // seach and store Videoitems s data
                    videoItem = getVideoItem( item.getId().getVideoId() ); 

                    // Search for ChannelIds and add returned items into the <channelItems> : List
                    channelItems.add(  getChannelItem( item.getSnippet().getChannelId() )  );
                    
                    // Try-Catch for 403 Forbidden recovery of the CommentThread items (comments for a given video might be disabled)
                    try { 
                        // Collect CommentThreads for each and every video(id), found by the given seach (if there is any) 
                        List<CommentThread> cTs = getCommentThreadsByVId(item.getId().getVideoId()); 

                        // If the CommentThread list is non empty
                        if (cTs != null) {
                            
                            // Then Append every Item 
                            for(CommentThread cT : cTs){
                                // Add any found commentThreads into the commentThreadItems list 
                                commentThreadItems.add(cT);

                                // Add the topLevelComments from the each commentThread item to the Commentitems List
                                Comment commentOfCT =  cT.getSnippet().getTopLevelComment();

                                // Add its Author s channel s data into the Channels list
                                channelItems.add(  getChannelItem( ((ArrayMap<String,String>)commentOfCT.getSnippet().getAuthorChannelId()).get("value") )  );

                                // Get a list of the Comments replying on the <commentOfCT>  
                                List<Comment> commentsChildren = getCommentsByParentId( commentOfCT.getId(), cT.getSnippet().getTotalReplyCount() );

                                // Add every Comment and its author s channel into the recovered list into The <CommentItems> list
                                for(Comment comment : commentsChildren){
                                    commentItems.add(comment);
                                    channelItems.add(getChannelItem( ((ArrayMap<String,String>)comment.getSnippet().getAuthorChannelId()).get("value")));  
                                }
                            }
                        }
                    } catch (GoogleJsonResponseException e){}
sqlConnection.insert(videoItem, channelItems, commentThreadItems, commentItems);                    
/*System.out.println("\n\n\n*********************************************************************************************\n");
if (videoItem != null ){
    System.out.println("VIDEO : \nId : "  + videoItem.getId());
    System.out.println("publishedAt : " + videoItem.getSnippet().getPublishedAt() );
    System.out.println("title : " + videoItem.getSnippet().getTitle());
    System.out.println("description : " + videoItem.getSnippet().getDescription());
    System.out.println("defaultLanguage : " + videoItem.getSnippet().getDefaultLanguage());
    System.out.println("defaultAudioLanguage : " + videoItem.getSnippet().getDefaultAudioLanguage());
    System.out.println("viewCount : " + videoItem.getStatistics().getViewCount());
    System.out.println("likeCount : " + videoItem.getStatistics().getLikeCount());
    System.out.println("dislikeCount : " + videoItem.getStatistics().getDislikeCount());
    System.out.println("commentCount : " + videoItem.getStatistics().getCommentCount());
    System.out.println("channels_id : " + videoItem.getSnippet().getChannelId());
videoItem = null;
}System.out.println("\n#############################RECOVERED CHANNELS#################################\n");

if (channelItems != null ) for(Channel ch : channelItems) {
    System.out.println("CHANNEL : \nchannels_id :" + ch.getId());
    System.out.println("title : " + ch.getSnippet().getTitle());
    System.out.println("description : " + ch.getSnippet().getDescription());
    System.out.println("customUrl : " + ch.getSnippet().getCustomUrl());
    System.out.println("publishedAt : " + ch.getSnippet().getPublishedAt());
    System.out.println("viewCount : " + ch.getStatistics().getViewCount());
    System.out.println("commentCount : " + ch.getStatistics().getCommentCount());
    System.out.println("subscriberCount : " + ch.getStatistics().getSubscriberCount());
    System.out.println("videoCount : " + ch.getStatistics().getVideoCount());
    System.out.println("_________________________________________________________________________________");
}
channelItems.clear();
System.out.println("\n###############################RECOVERED COMMENT THREADS##############################\n");


if (commentThreadItems != null ) for (CommentThread ct : commentThreadItems) {

    System.out.println("\n___________________________store into Comment_____________________________________\n");   

    System.out.println("Comments : \ncomment_ Id : " + ct.getSnippet().getTopLevelComment().getId());
    System.out.println("textOriginal : " + ct.getSnippet().getTopLevelComment().getSnippet().getTextOriginal());
    System.out.println("likeCount : " + ct.getSnippet().getTopLevelComment().getSnippet().getLikeCount());
    System.out.println("publishedAt : " + ct.getSnippet().getTopLevelComment().getSnippet().getPublishedAt());
    System.out.println("updatedAt : " + ct.getSnippet().getTopLevelComment().getSnippet().getUpdatedAt());
    System.out.println("#channels_id : " + ( (ArrayMap<String, String>) ct.getSnippet().getTopLevelComment().getSnippet().getAuthorChannelId()).get("value"));
    
    System.out.println("\n___________________________Comment Thread_____________________________________\n");   

    System.out.println("CommentThreads : \ncomment_ Id : " + ct.getSnippet().getTopLevelComment().getId());
    System.out.println("VideoId  : "  + ct.getSnippet().getVideoId());
    System.out.println("totalReplyCount : " + ct.getSnippet().getTotalReplyCount());

    System.out.println("\n");

}commentThreadItems.clear();
System.out.println("\n##############################RECOVERED COMMENT REPLIES #############################\n");
if (commentItems != null ) for (Comment comment : commentItems){
    System.out.println("\n___________________________store into Comment_____________________________________\n");   
    
    System.out.println("Comments : \ncomment_ Id : " + comment.getId());
    System.out.println("textOriginal : " + comment.getSnippet().getTextOriginal());
    System.out.println("likeCount : " + comment.getSnippet().getLikeCount());
    System.out.println("publishedAt : " + comment.getSnippet().getPublishedAt());
    System.out.println("updatedAt : " + comment.getSnippet().getUpdatedAt());
    System.out.println("#channels_id : " + ( (ArrayMap<String, String>) comment.getSnippet().getAuthorChannelId()).get("value"));
    
    System.out.println("\n___________________________Comment replies_____________________________________\n");   
    
    System.out.println("Comments : \ncomment_ Id : " + comment.getId());
    System.out.println("parent id  : "  + comment.getSnippet().getParentId());

    System.out.println("\n");
}commentItems.clear();*/
                }//for
                in.nextLine();
            }
        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            System.err.println("There was a service error: " +
                e.getDetails().getCode() + " : " + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}