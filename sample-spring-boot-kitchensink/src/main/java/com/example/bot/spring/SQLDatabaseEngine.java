package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	@Override
	String search(String text) throws Exception {
		//Write your code here
		String result = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = this.getConnection();
			//2
			stmt = connection.prepareStatement("SELECT * FROM testtable");
			//3
			rs = stmt.executeQuery();
			while (result == null && rs.next()) {
				//String[] parts = sCurrentLine.split(":");
				if (text.toLowerCase().equals(rs.getString(1).toLowerCase())) {
					result = rs.getString(2);
				}
			}
		} catch (Exception e) {
			log.info("URISyntaxException while reading file: {}", e.toString());
		} finally {
			try {
				if (rs.next())
					rs.close();
				if (stmt!=null)
					stmt.close();
				if (connection!=null)
					connection.close();
			} catch (Exception ex) {
				log.info("URISyntaxException while closing file: {}", ex.toString());
			}
		}
		if (result != null)
			return result;
		throw new Exception("NOT FOUND");
	}
	
	
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}