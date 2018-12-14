import java.sql.*; 
import java.util.*; 
public class insert {
	static	String url = "jdbc:oracle:thin:@localhost:1521:xe"; 
	static String user = "system"; 
	static	String pass = "abdo1234"; 

	public void query(int  Id_query,String mot,String date_from,String date_to ){
		String sql = "insert into  query values("+Id_query+",'"+mot+"','"+date_from+"','"+date_to+"')"; 
		Connection con=null; 
		try
		{ 
			DriverManager.registerDriver(new oracle.jdbc.OracleDriver()); 

			//Reference to connection interface 
			con = DriverManager.getConnection(url,user,pass); 

			Statement st = con.createStatement(); 
			int m = st.executeUpdate(sql); 
			if (m == 1) 
				System.out.println("inserted successfully : "+sql); 
			else
				System.out.println("insertion failed"); 
			con.close(); 
		} 
		catch(Exception ex) 
		{ 
			System.err.println(ex); 
		}

	}


public void channels(String channels_id,String snippet_title,String snippet_description ,String  snippet_publishedAt , String snippet_defaultLanguage ,String snippet_country , int statistics_viewCount , int statistics_commentCount , int statistics_subscriberCount , int statistics_videoCount  , String contentOwnerDetailsr    ){
		String sql = "insert into  channels values('"+channels_id+"', '"+snippet_title+"','"+snippet_description+"' , '"+snippet_publishedAt +"',  '"+snippet_defaultLanguage +"','"+snippet_country+"' , "+statistics_viewCount+"  , " +statistics_commentCount+" , "+statistics_subscriberCount+" ,"+ statistics_videoCount + " ,  '"+contentOwnerDetailsr+"'  )"; 

		Connection con=null; 
		try
		{ 
			DriverManager.registerDriver(new oracle.jdbc.OracleDriver()); 

			//Reference to connection interface 
			con = DriverManager.getConnection(url,user,pass); 

			Statement st = con.createStatement(); 
			int m = st.executeUpdate(sql); 
			if (m == 1) 
				System.out.println("inserted successfully : "+sql); 
			else
				System.out.println("insertion failed"); 
			con.close(); 
		} 
		catch(Exception ex) 
		{ 
			System.err.println(ex); 
		}

	}





public void video(String video_id ,String snippet_publishedAt ,String snippet_title ,String snippet_description , String snippet_categoryId  ,String snippet_defaultAudioLanguage ,String status_privacyStatus  , String  status_publishAt , String status_license   , int statistics_viewCount   ,  int statistics_likeCount ,int  statistics_dislikeCount ,  int statistics_commentCount , int fileDetails_durationMs , String channels_id  ){
		String sql = "insert into  video values('"+video_id+"' ,'"+snippet_publishedAt+"' , '"+snippet_title+"' ,'"+snippet_description+"' ,  '"+snippet_categoryId+"'  ,'"+snippet_defaultAudioLanguage +"', '"+status_privacyStatus +"' , '"+status_publishAt +"','"+status_license +"'  ,  "+statistics_viewCount+"   , "+statistics_likeCount+" , "+statistics_dislikeCount+" ,  "+statistics_commentCount+" , "+fileDetails_durationMs+" , '"+channels_id+"')"; 
 
		Connection con=null; 
		try
		{ 
			DriverManager.registerDriver(new oracle.jdbc.OracleDriver()); 

			//Reference to connection interface 
			con = DriverManager.getConnection(url,user,pass); 

			Statement st = con.createStatement(); 
			int m = st.executeUpdate(sql); 
			if (m == 1) 
				System.out.println("inserted successfully : "+sql); 
			else
				System.out.println("insertion failed"); 
			con.close(); 
		} 
		catch(Exception ex) 
		{ 
			System.err.println(ex); 
		}

	}
	public void commentthread( String commentthread_id   ,int snippet_topLevelComment  ,int snippet_totalReplyCount  ,String snippet_isPublic ,String video_id ){
		String sql = "insert into commentthread values( '"+commentthread_id+ "'  , "+snippet_topLevelComment+"  ,"+snippet_totalReplyCount +" ,'"+snippet_isPublic+"' , '"+video_id +"' )"; 
 
		Connection con=null; 
		try
		{ 
			DriverManager.registerDriver(new oracle.jdbc.OracleDriver()); 

			//Reference to connection interface 
			con = DriverManager.getConnection(url,user,pass); 

			Statement st = con.createStatement(); 
			int m = st.executeUpdate(sql); 
			if (m == 1) 
				System.out.println("inserted successfully : "+sql); 
			else
				System.out.println("insertion failed"); 
			con.close(); 
		} 
		catch(Exception ex) 
		{ 
			System.err.println(ex); 
		}

	}


         	public void recherche(String  video_id  ,int Id_query ){
		String sql = "insert into recherche values(  '"+video_id+"'  ,"+Id_query+" )"; 
 
		Connection con=null; 
		try
		{ 
			DriverManager.registerDriver(new oracle.jdbc.OracleDriver()); 

			//Reference to connection interface 
			con = DriverManager.getConnection(url,user,pass); 

			Statement st = con.createStatement(); 
			int m = st.executeUpdate(sql); 
			if (m == 1) 
				System.out.println("inserted successfully : "+sql); 
			else
				System.out.println("insertion failed"); 
			con.close(); 
		} 
		catch(Exception ex) 
		{ 
			System.err.println(ex); 
		}

	}           


  
public void Comments(String Comments_id  ,String snippet_authorDisplayName  ,String snippet_textOriginal ,int  snippet_likeCount,String snippet_publishedAt,String snippet_updatedAt,String channels_id ,String Comments_id_repond  ){
		String sql = "insert into Comments values('"+Comments_id+"'  ,'" +snippet_authorDisplayName+"'  ,'" +snippet_textOriginal+"' ,  "+snippet_likeCount+", '"+snippet_publishedAt+"','"+ snippet_updatedAt+"','"+channels_id+"' , '"+Comments_id_repond+"')"; 
 
		Connection con=null; 
		try
		{ 
			DriverManager.registerDriver(new oracle.jdbc.OracleDriver()); 

			//Reference to connection interface 
			con = DriverManager.getConnection(url,user,pass); 

			Statement st = con.createStatement(); 
			int m = st.executeUpdate(sql); 
			if (m == 1) 
				System.out.println("inserted successfully : "+sql); 
			else
				System.out.println("insertion failed"); 
			con.close(); 
		} 
		catch(Exception ex) 
		{ 
			System.err.println(ex); 
		}

	} 
		
public void commentthread(String commentthread_id  ,String Comments_id){
		String sql = "insert into commentthread values('"+commentthread_id+"','"+Comments_id+"')"; 
 
		Connection con=null; 
		try
		{ 
			DriverManager.registerDriver(new oracle.jdbc.OracleDriver()); 

			//Reference to connection interface 
			con = DriverManager.getConnection(url,user,pass); 

			Statement st = con.createStatement(); 
			int m = st.executeUpdate(sql); 
			if (m == 1) 
				System.out.println("inserted successfully : "+sql); 
			else
				System.out.println("insertion failed"); 
			con.close(); 
		} 
		catch(Exception ex) 
		{ 
			System.err.println(ex); 
		}

	}






        


}