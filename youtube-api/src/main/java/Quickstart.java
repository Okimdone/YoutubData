import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.util.DateTime;//For setPublishedBefore/From(DateTime x)
import com.google.api.client.util.ArrayMap;

import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.*;
import com.sun.source.doctree.CommentTree;
import com.google.api.services.youtube.YouTube;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;//search video.list
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


public class Quickstart {

    /** Application name. */
    private static final String APPLICATION_NAME = "YOUTUBE API";

    /** Directory to store user credentials for get channel  application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/youtube-api");

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the scopes required by this quickstart.
    */
    private static final List<String> SCOPES = Arrays.asList(YouTubeScopes.YOUTUBE_READONLY
                                                            ,"https://www.googleapis.com/auth/youtube"
                                                            ,"https://www.googleapis.com/auth/youtube.force-ssl"
                                                            );
    
    //Youtube Object Where all the magic comes from
    private static YouTube youtube;

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;        
    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;
    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY   = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Create an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
            Quickstart.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
            new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(DATA_STORE_FACTORY)
            .setAccessType("offline")
            .build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        //System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized API client service, such as a YouTube
     * Data API client service.
     * @return an authorized API client service
     * @throws IOException
     */
    public static YouTube getYouTubeService() throws IOException {
        Credential credential = authorize();
        return new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /** Main program, searched for a (query + date interval) return (videoid, channelid) as a tuple
     *  searches for the videoIds and stores the found information in the List<Video> videoItems
     *  searches for the channelIds and stores the found infomation in the List<Channel> channelItems
     *  searches for the CommentThreads for the given videoIds and stores the data in the List<CommentThread> CommentThreadItems
     *  searches every Comment in the CommentThreads for comments-replies and stores all found comments into List<Comment> CommentItems 
     *  searches every commentItem for a channelId and searches for that channelId s data and adds it to List<Channel> channelItems  
     */
    public static void main(String[] args) {
        try {
            // Youtube s magic
            youtube = getYouTubeService();
            
            Scanner in = new Scanner(System.in);
            System.out.print("Enter the Query : ");
            String query = in.nextLine();
            // Search for <query>
            List<SearchResult> items = getVideosByQuery(query
                                                    , new Date(/*year*/ 2018-1900,/*month*/ 11 - 1,/*Day*/ 12)
                                                    , new Date(/*year*/ 2018-1900,/*month*/ 12 - 1 ,/*Day*/ 12)
                                                    );
            if(items != null){
                String videoIds = null;
                // A list of videos informations
                List<Video> videoItems = new ArrayList<>();
                // A list of channels informations that own the videoItems
                List<Channel> channelItems = new ArrayList<>(); 
                // A list of CommentThreads informations for everyvideo in the videoItems
                List<CommentThread> commentThreadItems = new ArrayList<>();
                // A list of Comments informations for everyvideo in the CommentThreads
                List<Comment> commentItems = new ArrayList<>();
                
                // Collectusefull information in the searched items (videoIds and channelIds)
                // For Every item return by our search
                for (SearchResult item : items) {
                    
                    // Append the [videoIds,...] string for a multiple searchByVideoIds search
                    if(videoIds == null)    videoIds = item.getId().getVideoId();
                    else                    videoIds += "," + item.getId().getVideoId();
                    
                    // Search for ChannelIds and add returned items into the <channelItems> : List
                    channelItems.add(  getChannelItem( item.getSnippet().getChannelId() )  );
                    
                    // Try-Catch for 403 Forbidden recovery of the CommentThread items (comments for a given video might be disabled)
                    try { 
                        // Collect CommentThreads for each and every video(id), found by the given seach (if there is any) 
                        List<CommentThread> cTs = getCommentThreadsByVId(item.getId().getVideoId()); 

                        // If an the CommentThread list is non empty
                        if (cTs != null) {
                            
                            // Then Append every Item 
                            for(CommentThread cT : cTs){
                                // Add any found commentThreads into the commentThreadItems list 
                                commentThreadItems.add(cT);

                                // Add the topLevelComments from the each commentThread item to the Commentitems List
                                Comment commentOfCT =  cT.getSnippet().getTopLevelComment();
                                
                                // Add the top level comment of the current thread into the Comments list
                                commentItems.add(commentOfCT);
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

                }

                // seach and store Videoitems s data
                videoItems = getVideoItems(videoIds); 

                // Print the results out

                System.out.println("Listing Videos s data :");                
                if(commentItems != null){
                    for(Video video : videoItems){    
                        System.out.println("Video :   " + video.getSnippet().getTitle());
                        System.out.println("Channel : " + video.getSnippet().getChannelTitle() + "\n");
                    }
                }else System.out.println("No videos");
                
                if(commentItems != null){
                    System.out.println("Listing Channels :");
                    for(Channel channel : channelItems){    
                        System.out.println("Channel : " + channel.getSnippet().getTitle() + "\n");
                    }
                }else System.out.println("No Channels");
                
                System.out.println("Listing Comments : ");
                if(commentItems != null){
                    for(Comment comment : commentItems){    
                        System.out.println("Author : " + comment.getSnippet().getAuthorDisplayName());
                        System.out.println("text : " +   comment.getSnippet().getTextOriginal());
                    } 
                }else System.out.println("No comments");
                
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


    /** Return a list of videos, searched for with the given query, and between the from - to dates  
     *  The list is in "published date" order 
    */
    private static List<SearchResult> getVideosByQuery(String query, Date dateFrom, Date dateTo)
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
    private static List<Video> getVideoItems(String videoIds) 
    throws IOException {
        YouTube.Videos.List videosListMultipleIdsRequest = youtube.videos().list("snippet,contentDetails,statistics");
        if (videoIds != "") videosListMultipleIdsRequest.setId(videoIds);
        return videosListMultipleIdsRequest.execute().getItems();
    }
    
    /** Takes one channel id, and returns its equivalent informations */
    private static Channel getChannelItem(String channelId) 
    throws IOException {
        YouTube.Channels.List channelsListByIdRequest = youtube.channels().list("snippet,contentDetails,statistics");
        if ( channelId != "") channelsListByIdRequest.setId(channelId);
        return channelsListByIdRequest.execute().getItems().get(0);
    }

    /** Get the commentthreads by the given video Id */
    private static List<CommentThread> getCommentThreadsByVId(String videoId) 
    throws IOException {
        YouTube.CommentThreads.List commentThreadsListByVideoIdRequest = youtube.commentThreads().list("snippet,replies");
        if (videoId != "")  commentThreadsListByVideoIdRequest.setVideoId(videoId);
        commentThreadsListByVideoIdRequest.setOrder ("relevance");
        commentThreadsListByVideoIdRequest.setMaxResults(Long.parseLong("1"));
        return commentThreadsListByVideoIdRequest.execute().getItems();
    }

    /** get the comments items by the given comment parent_id */
    private static List<Comment> getCommentsByParentId(String parentId, Long replyNumber)
    throws IOException {
        YouTube.Comments.List commentsListRequest = youtube.comments().list("snippet");
        if (parentId != "") commentsListRequest.setParentId( parentId );
        commentsListRequest.setMaxResults( replyNumber );
        return commentsListRequest.execute().getItems();
    }
}