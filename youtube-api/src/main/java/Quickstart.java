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
import com.google.api.services.youtube.YouTube;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private static final List<String> SCOPES = Arrays.asList(YouTubeScopes.YOUTUBE_READONLY);

    
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
//        System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
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

    public static void main(String[] args) throws IOException {
        try {
            youtube = getYouTubeService();
            SearchListResponse videos = getVideosByQuery("hello"
                                                    , new Date(/*year*/ 2018-1900,/*month*/ 10 + 1,/*Day*/ 12)
                                                    , new Date(/*year*/ 2018-1900,/*month*/ 11 + 1 ,/*Day*/ 12)
                                                    );
            System.out.println("Number of returned videos : " + videos.getItems().size());
            System.out.println(videos);
        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            System.err.println("There was a service error: " +
                e.getDetails().getCode() + " : " + e.getDetails().getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    /**Return a list of videos, searched for with the given query, and between the from - to dates  
     * The list is in "published date" order 
    */
    private static SearchListResponse getVideosByQuery(String query, Date dateFrom, Date dateTo)
    throws Throwable
    {
            YouTube.Search.List searchListByKeywordRequest = youtube.search().list("snippet");
            //to change (it s set to 5 by default)
            searchListByKeywordRequest.setMaxResults(Long.parseLong("1"));
            searchListByKeywordRequest.setType("video");
            if (query != "")    searchListByKeywordRequest.setQ(query);
            searchListByKeywordRequest.setPublishedAfter(new DateTime(dateFrom));
            searchListByKeywordRequest.setPublishedBefore(new DateTime(dateTo ));
            // Possible Values (date, reting , relevance, title, videoCount, viewCount)
            searchListByKeywordRequest.setOrder("date");//"relevance" by default.

            return searchListByKeywordRequest.execute();  
    }      
}

