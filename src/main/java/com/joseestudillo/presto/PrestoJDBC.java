package com.joseestudillo.presto;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * Example of a Presto JDBC client
 * 
 * @author Jose Estudillo
 *
 */
public class PrestoJDBC {
	/*
	 * Connection Strings examples:
	 * 
	 * jdbc:presto://host:port
	 * 
	 * jdbc:presto://host:port/catalog
	 * 
	 * jdbc:presto://host:port/catalog/schema
	 */

	public static Logger log = Logger.getLogger(PrestoJDBC.class);
	private static final String PRESTO_DRIVER = "com.facebook.presto.jdbc.PrestoDriver";
	private static final String PRESTO_LOCAL = "jdbc:presto://localhost:8080";

	private static void logResultSet(ResultSet rs, int limit) throws SQLException {

		ResultSetMetaData rsmd = rs.getMetaData();
		int nCols = rsmd.getColumnCount();
		StringBuffer tmp;
		int index = 0;
		while (rs.next() && index < limit) {
			tmp = new StringBuffer();
			tmp.append("{");
			int i = 1;
			tmp.append(String.format("%s:%s", rsmd.getColumnName(i), rs.getObject(i)));
			for (i = i + 1; i <= nCols; i++) {
				tmp.append(String.format(", %s:%s", rsmd.getColumnName(i), rs.getObject(i)));
			}
			tmp.append("}");
			log.info(tmp.toString());
			index++;
		}
		rs.close();
	}

	public static void main(String[] args) throws Exception {
		Class.forName(PRESTO_DRIVER);

		String connectionString = (args.length == 0) ? PRESTO_LOCAL : args[0];

		log.info(String.format("Using: %s", connectionString));

		Connection connection = DriverManager.getConnection(connectionString, "user", "password");

		DatabaseMetaData metadata = connection.getMetaData();
		log.info(String.format("Connected to: %s %s", metadata.getDatabaseProductName(),
				metadata.getDatabaseProductVersion()));

		Statement stmt = connection.createStatement();

		String[] queries = new String[] {
				"SHOW SCHEMAS FROM system",
				"SHOW TABLES FROM system.runtime",
				"SELECT * FROM system.runtime.nodes",
				"SELECT * FROM system.metadata.catalogs",
				"SELECT * FROM system.runtime.queries",
				"SELECT * FROM system.runtime.tasks"
		};

		for (String query : queries) {
			log.info(String.format("-- %s", query));
			ResultSet rSet = stmt.executeQuery(query);
			logResultSet(rSet, 15); //limit for tables that contain queries and tasks
		}

		connection.close();
		System.exit(0);
	}
}
