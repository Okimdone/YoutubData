package src.jdbc;

import com.google.api.services.youtube.model.*;//Comment-Video-Channel-CommentThread
import com.google.api.client.util.ArrayMap;
import java.sql.*; 
import java.util.*; 

public class OracleSql{
	static private int queryMotIndex=1;
	static private int queryPlageIndex=1;
	
	static final String URL = "jdbc:oracle:thin:@localhost:1521:xe"; 
	static final String USER = "system"; 
	static final String PASS = "0002"; 
	static private OracleSql oracleSql = null; 
	static private Connection connection=null;
	static {
		try{
			DriverManager.registerDriver(new oracle.jdbc.OracleDriver()); 
			connection = DriverManager.getConnection(URL,USER,PASS); 
		} catch (SQLException e){
			System.err.println("Can't Create a Connection!!");
			System.err.println(e.getMessage()); 
		}
	}

	private OracleSql()
	  throws SQLException
	{
		dropTables();//drop tables if they exist
		createTables();
	}

	public static OracleSql getOracleSql(){
		if(oracleSql == null) {
			try{
				return new OracleSql();
			} catch (SQLException e) {
				System.err.println(e.getMessage()); 
			}
		}//if
		return oracleSql; 
	}

	public void closeconnection(){
		try{
			dropTables();
			oracleSql.connection.close();
			oracleSql=null;	
		} catch (SQLException e) {
			System.err.println(e.getMessage()); 
		}	
	}

	/** FOR TESTS : TO_DEL */
	public static void main(String[] s){
		System.out.println("Main : ");
		OracleSql ora = getOracleSql();
		try{
			ora.insert("text to search", new java.util.Date(/*year*/ 2018-1900,/*month*/ 11 - 1,/*Day*/ 12), 
			  new java.util.Date(/*year*/ 2018-1900,/*month*/ 12 - 1 ,/*Day*/ 12));
		} catch (SQLException e){
			System.err.println(e.getMessage());
		}
		
	}

	/** You can only increment the query_plage */
	public void incrQueryPlageIndex(){
		queryPlageIndex++;
	}
	
	/** Insert Query information into database */
	public void insert(String query, java.util.Date dateFrom, java.util.Date dateTo)
	  throws SQLException
	{
		//Check if query exists
		String sql = "SELECT query_id FROM QUERY WHERE mots = '"+ query.toLowerCase() +"' ";  
		
		Statement st = connection.createStatement(); 
		
		// Setting the Query_id s value and inserting the Query if it doesn t already exist
		int query_index=1;
		ResultSet rs = st.executeQuery(sql); 
		if (rs.next()) {			//if there are rows.  
			query_index = rs.getInt("QUERY_ID");
		}
		else {
			ResultSet maxIndex = st.executeQuery("SELECT MAX(query_id) as query_id FROM QUERY");
			if(maxIndex.next()){	
				query_index = maxIndex.getInt("QUERY_ID") + 1;
			}
			else {
				query_index = 1;
			}
		}
		sql = "INSERT INTO query VALUES("
		 + query_index  + ", '"
		 + query.toLowerCase()  + "' )";
		
		insert(sql);
	
		sql = "INSERT INTO query_plage VALUES ( "
		 + queryPlageIndex++ + ", "
		 + query_index + ", "
		 + "TO_DATE('" + dateFrom.getDay()+"/"+dateFrom.getMonth()+"/"+dateFrom.getYear() +"', 'DD/MM/YYYY'), "
		 + "TO_DATE('" + dateTo.getDay()+"/"+dateTo.getMonth()+"/"+dateTo.getYear() +"', 'DD/MM/YYYY')"
		 + " )";

		 insert(sql);
	}//insert

	/** Insert every Other youtube item into the datebase */
	public void insert(Video video, List<Channel> channels, List<CommentThread> commentThreads, List<Comment> comments){
		try{
			insertChannels(channels);
			insert(video);
			insertCommentThreads(commentThreads);
			insertComments(comments);
		} catch(SQLException sqle){
			System.err.println(sqle.getMessage()); 
			rollback();
			sqle.printStackTrace();
		}
		commit();
	}
	
	private void insert(Video video)
	  throws SQLException
	{
		String sql = "SELECT video_id FROM VIDEO WHERE video_id = '"+ video.getId() +"' ";  
		Statement st = connection.createStatement(); 
		
		// Setting the Query_id s value and inserting the Query if it doesn t already exist
		ResultSet rs = st.executeQuery(sql); 
		if (rs.next()) {			//if there are rows.  
			sql = "UPDATE VIDEO SET "
			+ "title ='" 		+ video.getSnippet().getTitle()+"', "
			+ "description = q'#" + video.getSnippet().getDescription() 	  +"#', "
			+ "viewCount = "	+ video.getStatistics().getViewCount()	  +", " 
			+ "likeCount = "	+ video.getStatistics().getLikeCount() 	  +", "
			+ "dislikeCount = " + video.getStatistics().getDislikeCount() +", " 
			+ "commentCount = " + video.getStatistics().getCommentCount() + " "
			+ "Where video_id = '"	+ video.getId()+ "'";; 

			update(sql);
		}//if
		else {		
			sql = "insert into VIDEO values('"
			+ video.getId() +"', " 
			+ "to_timestamp_tz('" + video.getSnippet().getPublishedAt()+"', 'YYYY-MM-DD\"t\"HH24:MI:SS.FF7TZR'), q'#"
			+ video.getSnippet().getTitle()+"#', q'#"
			+ video.getSnippet().getDescription() +"#',  '"
			+ video.getSnippet().getDefaultLanguage() +"','"
			+ video.getSnippet().getDefaultAudioLanguage() +"', "
			+ video.getStatistics().getViewCount() +", " 
			+ video.getStatistics().getLikeCount() +", "
			+ video.getStatistics().getDislikeCount() +", " 
			+ video.getStatistics().getCommentCount() + " , '"
			+ video.getSnippet().getChannelId() +"'  )"; 

			insert(sql);
			try{
				sql = "insert into TROUVER values( '"
				+ video.getId() + "', "
				+ (queryPlageIndex - 1) + " )";
				
				insert(sql);
			} catch (SQLException e){
				//if it exists do nothing 
			} 
		}//else
	}

	private void insertChannels(List<Channel> channels)
	  throws SQLException
	{
		String sql;
		for(Channel channel : channels) {
			sql = "SELECT channel_id FROM CHANNEL WHERE channel_id = '"+ channel.getId() +"' ";  
			Statement st = connection.createStatement(); 
			
			// Setting the Query_id s value and inserting the Query if it doesn t already exist
			ResultSet rs = st.executeQuery(sql); 
			if (rs.next()) {			//if there are rows.  
				sql = "UPDATE CHANNEL SET "
				+ "title= q'#" 			+ channel.getSnippet().getTitle()+"#', "
				+ "description= q'#"		+ channel.getSnippet().getDescription() +"#',"
				+ "customUrl= '"		+ channel.getSnippet().getCustomUrl() + "',"
				+ "viewCount="			+ channel.getStatistics().getViewCount() +" , " 
				+ "commentCount="		+ channel.getStatistics().getCommentCount() +", " 
				+ "subscriberCount=" 	+ channel.getStatistics().getSubscriberCount() + " , "
				+ "videoCount=" 		+ channel.getStatistics().getVideoCount() 
				+ " Where channel_id = '"	+ channel.getId()+ "'";; 

				update(sql);
			}//if
			else {
				sql = "insert into CHANNEL values( '" 
				+ channel.getId() +"', q'#" 
				+ channel.getSnippet().getTitle()+"#', q'#"
				+ channel.getSnippet().getDescription() +"#', '"
				+ channel.getSnippet().getCustomUrl() + "', "
				+ "to_timestamp_tz('" + channel.getSnippet().getPublishedAt()+"', 'YYYY-MM-DD\"t\"HH24:MI:SS.FF7TZR'), "
				+ channel.getStatistics().getViewCount() +" , " 
				+ channel.getStatistics().getCommentCount() +", " 
				+ channel.getStatistics().getSubscriberCount() + " , "
				+ channel.getStatistics().getVideoCount() +"  )"; 

				insert(sql);
			}//else
		}
	}
	
	private void insertCommentThreads(List<CommentThread> commentThreads)
	  throws SQLException
	{
		
		String sql;
		for(CommentThread ct : commentThreads){
			sql = "SELECT comment_id FROM Comments WHERE comment_id = '"+ ct.getSnippet().getTopLevelComment().getId() +"' ";  
			Statement st = connection.createStatement(); 
			
			// Setting the Query_id s value and inserting the Query if it doesn t already exist
			ResultSet rs = st.executeQuery(sql); 
			if (rs.next()) {			//if there are rows.  
				sql = "update COMMENTS SET " 
				+ "textOriginal= q'#" 		+ ct.getSnippet().getTopLevelComment().getSnippet().getTextOriginal() +"#' , "
				+ "likeCount="				+ ct.getSnippet().getTopLevelComment().getSnippet().getLikeCount() +", "
				+ "updatedAt=to_timestamp_tz('"	+ ct.getSnippet().getTopLevelComment().getSnippet().getUpdatedAt() +"', 'YYYY-MM-DD\"t\"HH24:MI:SS.FF7TZR') "
				+ "Where comment_id = '"	+ ct.getSnippet().getTopLevelComment().getId() + "'"; 

				update(sql);

				sql = "update Comment_video SET " 
				+ "totalReplyCount = " + ct.getSnippet().getTotalReplyCount() + " "
				+ "Where comments_id = '"	+ ct.getSnippet().getTopLevelComment().getId() + "'";
				
				update(sql);
			}//if
			else {
				sql = "insert into COMMENTS values('"
				+ ct.getSnippet().getTopLevelComment().getId() +"', q'#" 
				+ ct.getSnippet().getTopLevelComment().getSnippet().getTextOriginal() +"#' , "
				+ ct.getSnippet().getTopLevelComment().getSnippet().getLikeCount() +", "
				+ "to_timestamp_tz('" + ct.getSnippet().getTopLevelComment().getSnippet().getPublishedAt() +"', 'YYYY-MM-DD\"t\"HH24:MI:SS.FF7TZR'), "
				+ "to_timestamp_tz('" + ct.getSnippet().getTopLevelComment().getSnippet().getUpdatedAt() +"', 'YYYY-MM-DD\"t\"HH24:MI:SS.FF7TZR'), '"
				+ ( (ArrayMap<String, String>) ct.getSnippet().getTopLevelComment().getSnippet().getAuthorChannelId()).get("value") +"' "
				+ " )"; 

				insert(sql);

				sql = "insert into COMMENT_VIDEO values('"
				+ ct.getSnippet().getTopLevelComment().getId() +"', '" 
				+ ct.getSnippet().getVideoId() +"' , "
				+ ct.getSnippet().getTotalReplyCount() + " )";
				
				insert(sql);
			}
		}
	}
	
	private void insertComments(List<Comment> comments)
	  throws SQLException
	{
		String sql;
		for(Comment comment : comments){
			sql = "SELECT comment_id FROM Comments WHERE comment_id = '"+ comment.getId() +"' ";  
			Statement st = connection.createStatement(); 
			
			// Setting the Query_id s value and inserting the Query if it doesn t already exist
			ResultSet rs = st.executeQuery(sql); 
			if (rs.next()) {			//if there are rows.  
				sql = "update COMMENTS SET " 
				+ "textOriginal= q'#" 		+ comment.getSnippet().getTextOriginal() +"#' , "
				+ "likeCount="				+ comment.getSnippet().getLikeCount() +","
				+ "updatedAt=to_timestamp_tz('"+ comment.getSnippet().getUpdatedAt() +"', 'YYYY-MM-DD\"t\"HH24:MI:SS.FF7TZR') "
				+ "Where comment_id = '"	+ comment.getId() + "'"; 

				update(sql);
			}//if
			else {		
				sql = "insert into COMMENTS values('"
				+ comment.getId() +"', q'#" 
				+ comment.getSnippet().getTextOriginal() +"#' , "
				+ comment.getSnippet().getLikeCount() +", "
				+ "to_timestamp_tz('" + comment.getSnippet().getPublishedAt() +"', 'YYYY-MM-DD\"t\"HH24:MI:SS.FF7TZR'), "
				+ "to_timestamp_tz('" + comment.getSnippet().getUpdatedAt() +"', 'YYYY-MM-DD\"t\"HH24:MI:SS.FF7TZR') , q'#"
				+ ( (ArrayMap<String, String>) comment.getSnippet().getAuthorChannelId()).get("value") +"#' "
				+ " )"; 

				insert(sql);

				sql = "insert into COMMENT_REPONSE values('"
				+ comment.getId() +"', '" 
				+ comment.getSnippet().getParentId() + "' )";
				
				insert(sql);
			}//else
		}
	}
	
	private void insert(String insertSqlQuery)
	  throws SQLException
	{
		System.out.println(insertSqlQuery);
		Statement st = connection.createStatement(); 
		int m = st.executeUpdate(insertSqlQuery); 
		if (m == 1) 
			System.out.println("inserted successfully!!!! \n"); 
	}//insert

	private void update(String insertSqlQuery)
	  throws SQLException
	{
		System.out.println(insertSqlQuery);
		Statement st = connection.createStatement(); 
		int m = st.executeUpdate(insertSqlQuery); 
		if (m == 1) 
			System.out.println("Updated successfully : \n"); 
	}

	private void commit()
  	{
		try{
			Statement st = connection.createStatement();
			st.execute("COMMIT");
		} catch (SQLException e) {
			System.err.println("Nothing Commited");
		}
	}//commit()

	private void rollback(){
		try{
			Statement st = connection.createStatement();
			st.execute("ROLLBACK");
		} catch (SQLException e) {
			System.err.println("Error Rolling back!");
		}
	}//rollback()

	private void createTables()
	  throws SQLException
	{
		Statement st = connection.createStatement();
		String sql = "CREATE TABLE Query " 
					+ "(   query_id NUMERIC(10) CONSTRAINT qry_id_pk PRIMARY KEY"
					+ ",   mots VARCHAR2(200) CONSTRAINT qry_mts_NN NOT NULL"
					+ ")";

		st.execute(sql);

		sql = "CREATE TABLE Query_plage"
			+ "(   Query_plage_id NUMERIC(10) CONSTRAINT qry_plge_id_PK PRIMARY KEY"
			+ ",   query_id NUMERIC(10)       CONSTRAINT qry_plge_qry_id_FK REFERENCES Query(query_id) ON DELETE SET NULL"
			+ ",   DATE_from DATE             CONSTRAINT qry_plge_dt_fm_NN NOT NULL"
			+ ",   DATE_to DATE               CONSTRAINT qry_plge_dt_TO_NN NOT NULL"
			+ ")";

		st.execute(sql);

		sql = "CREATE TABLE Channel"
			+ "(   channel_id VARCHAR2(24) CONSTRAINT ch_id_PK PRIMARY KEY"
			+ ",   title VARCHAR2(100)      CONSTRAINT ch_id_NN NOT NULL"
			+ ",   description VARCHAR2(4000)"
			+ ",   customUrl  VARCHAR2(40)  CONSTRAINT ch_curl_NN NOT NULL"
			+ ",   publishedAt DATE         CONSTRAINT ch_pat_NN NOT NULL"
			+ ",   viewCount NUMERIC(12)    CONSTRAINT ch_vc_NN NOT NULL"
			+ ",   commentCount NUMERIC(12)    DEFAULT NULL"
			+ ",   subscriberCount NUMERIC(12) DEFAULT NULL"
			+ ",   videoCount NUMERIC(12)      DEFAULT NULL"
			+ ")";

		st.execute(sql);

		sql = "CREATE TABLE Video"
			+ "(   video_id VARCHAR2(11) CONSTRAINT v_id_PK PRIMARY KEY"
			+ ",   publishedAt DATE CONSTRAINT v_pat_NN NOT NULL"
			+ ",   title VARCHAR2(100) CONSTRAINT v_title_NN NOT NULL"
			+ ",   description VARCHAR2(4000) "
			+ ",   defaultLanguage VARCHAR2(20) CONSTRAINT v_dl_NN NOT NULL"
			+ ",   defaultAudioLanguage VARCHAR2(20) CONSTRAINT v_dal_NN NOT NULL"
			+ ",   viewCount NUMERIC(12) DEFAULT NULL"
			+ ",   likeCount  NUMERIC(12) DEFAULT NULL"
			+ ",   dislikeCount  NUMERIC(12) DEFAULT NULL"
			+ ",   commentCount  NUMERIC(12) DEFAULT NULL"
			+ ",   channel_id VARCHAR2(24)"
			+ ",       CONSTRAINT v_ch_id_FK FOREIGN KEY (channel_id) REFERENCES channel(channel_id) ON DELETE CASCADE"
			+ ",       CONSTRAINT v_vld_CK CHECK ( likeCount + dislikeCount < viewCount)"
			+ ")";

		st.execute(sql);
		
		sql = "CREATE TABLE Trouver"
			+ "(   video_id VARCHAR2(11) CONSTRAINT Tr_v_id_FK REFERENCES video(video_id) ON DELETE CASCADE"
			+ ",   Query_plage_id NUMERIC(10) CONSTRAINT Tr_qry_plage_id_FK REFERENCES Query_plage(Query_plage_id) ON DELETE CASCADE"
			+ ",       CONSTRAINT tr_PK PRIMARY KEY (video_id, Query_plage_id)"
			+ ")";

		st.execute(sql);
		
		sql = "CREATE TABLE Comments"
			+ "(   comment_id VARCHAR2(49)     CONSTRAINT co_id_PK PRIMARY KEY"
			+ ",   textOriginal VARCHAR2(4000) CONSTRAINT co_to_NN NOT NULL"
			+ ",   likeCount NUMERIC(12)       DEFAULT NULL"
			+ ",   publishedAt DATE            CONSTRAINT co_pat_NN NOT NULL"
			+ ",   updatedAt DATE              CONSTRAINT co_uat_NN NOT NULL"
			+ ",   channel_id VARCHAR2(24)     CONSTRAINT co_ch_id_FK REFERENCES channel(channel_id) ON DELETE CASCADE"
			+ ")";

		st.execute(sql);
		
		sql = "CREATE TABLE Comment_video"
			+ "(   comments_id VARCHAR2(49)    CONSTRAINT cv_co_id_FK REFERENCES comments(comment_id) ON DELETE CASCADE"
			+ ",   video_id VARCHAR2(11)       CONSTRAINT cv_v_id_FK REFERENCES video(video_id) ON DELETE CASCADE"
			+ ",   totalReplyCount NUMERIC(12) CONSTRAINT cv_trc_NN NOT NULL"
			+ ",   CONSTRAINT cv_PK PRIMARY KEY (comments_id, video_id)"
			+ ")";

		st.execute(sql);

		sql = "CREATE TABLE Comment_reponse"
			+ "(   comments_id  VARCHAR2(49)       CONSTRAINT cr_co_id_FK REFERENCES comments(comment_id) ON DELETE CASCADE"
			+ ",   comments_id_Parent VARCHAR2(49) CONSTRAINT cr_co_p_id_FK REFERENCES comments(comment_id) ON DELETE CASCADE" 
			+ ",   CONSTRAINT cr_PK PRIMARY KEY(comments_id)"
			+ ")";

		st.execute(sql);
	}//createTables

	private void dropTables()
	  throws SQLException
	{
		Statement st = connection.createStatement();
		String sql;
		try{		
			sql = "drop table Comment_reponse";
			st.execute(sql);
		}catch(SQLException e){}

		try{		
			sql = "drop table Comment_video";
			st.execute(sql);
		}catch(SQLException e){}
				
		try{		
			sql = "drop table comments";
			st.execute(sql);
		}catch(SQLException e){}
		
		try{		
			sql = "drop table trouver";
			st.execute(sql);
		}catch(SQLException e){}
		
		try{		
			sql = "drop table video";
			st.execute(sql);
		}catch(SQLException e){}
		
		try{		
			sql = "drop table channel";
			st.execute(sql);
		}catch(SQLException e){}
		
		try{		
			sql = "drop table Query_plage";
			st.execute(sql);
		}catch(SQLException e){}
		
		try{		
			sql = "drop table query";
			st.execute(sql);
		}catch(SQLException e){}

	}//dropTables

	private void dropFK()
	  throws SQLException
	{
		Statement st = connection.createStatement();
		String sql;
		sql = "ALTER TABLE Query_plage DISABLE CONSTRAINT qry_plge_qry_id_FK";
		st.execute(sql);
		
		sql = "ALTER TABLE Video DISABLE CONSTRAINT v_ch_id_FK";
		st.execute(sql);

		sql = "ALTER TABLE Trouver DISABLE CONSTRAINT Tr_v_id_FK";
		st.execute(sql);

		sql = "ALTER TABLE Trouver DISABLE CONSTRAINT Tr_qry_plage_id_FK";
		st.execute(sql);

		sql = "ALTER TABLE Comments DISABLE CONSTRAINT co_ch_id_FK";
		st.execute(sql);

		sql = "ALTER TABLE Comment_video DISABLE CONSTRAINT cv_co_id_FK";
		st.execute(sql);

		sql = "ALTER TABLE Comment_video DISABLE CONSTRAINT cv_v_id_FK";
		st.execute(sql);

		sql = "ALTER TABLE Comment_reponse DISABLE CONSTRAINT cr_co_id_FK";
		st.execute(sql);

		sql = "ALTER TABLE Comment_reponse DISABLE CONSTRAINT cr_co_p_id_FK";
		st.execute(sql);
	}//dropFK
}