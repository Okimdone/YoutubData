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
import java.util.HashMap;   //search video.list
import java.util.List;


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

    public static void main(String[] args) {
        try {
            youtube = getYouTubeService();
            List<SearchResult> items = getVideosByQuery("hello"
                                                    , new Date(/*year*/ 2018-1900,/*month*/ 10 + 1,/*Day*/ 12)
                                                    , new Date(/*year*/ 2018-1900,/*month*/ 11 + 1 ,/*Day*/ 12)
                                                    );
            if(items != null){
                String videoIds = null;
                // A list of videos informations
                List<Video> videoItems = new ArrayList<>();
                // A list of channels informations that own the videoItems
                List<Channel> channelItems = new ArrayList<>(); 
                // A list of CommentThreads informations for everyvideo in the videoItems
                List<CommentThread> commentThreadItems = new ArrayList<>();
                
                // Collectusefull information in the searched items (videoIds and channelIds)
                for(SearchResult item : items){
                    if(videoIds == null){
                        videoIds = item.getId().getVideoId();
                    } else {
                        videoIds += "," + item.getId().getVideoId();
                    }
                    channelItems.add(getChannelItem(item.getSnippet().getChannelId()));
                    try {
                        List<CommentThread> cTs = getCommentThreadsByVId("6r5y4bsVUt8"/*item.getId().getVideoId()*/); 
                        if(cTs != null){
                            for(CommentThread cT : cTs){
                                System.out.println(cT.getSnippet().getTopLevelComment().getSnippet().getTextDisplay());
                                System.out.println();
                                System.out.println(cT.getSnippet().getTopLevelComment().getSnippet().getTextOriginal());
                                System.out.println();
                                System.out.println();
                                // the Making of the commentTread List of items
                                commentThreadItems.add(cT);
                            }
                        }
                    } catch (GoogleJsonResponseException e){

                    }
                }videoItems = getVideoItems(videoIds);
                for(Video v : videoItems){
                    System.out.println( "Title : " + v.getSnippet().getLocalized().getTitle() + "\nChannel :  " +
                                        v.getSnippet().getChannelTitle() + "\n\n"
                                        );
                    //System.out.println(channelItems);
                } 
                /*System.out.println();
                System.out.println(getChannelItem(commentThreadItems.toString()));
                */
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
            searchListByKeywordRequest.setMaxResults(Long.parseLong("1"));
            searchListByKeywordRequest.setType("video");
            if (query != "")    searchListByKeywordRequest.setQ(query);
            searchListByKeywordRequest.setPublishedAfter(new DateTime(dateFrom));
            searchListByKeywordRequest.setPublishedBefore(new DateTime(dateTo ));
            // Possible Values (date, rating , relevance, title, videoCount, viewCount)
            searchListByKeywordRequest.setOrder("date");//"relevance" by default.

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
        return commentThreadsListByVideoIdRequest.execute().getItems();
    }
}