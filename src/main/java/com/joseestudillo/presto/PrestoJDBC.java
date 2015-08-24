package com.joseestudillo.presto;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class PrestoJDBC {
	/*
	 * jdbc:presto://host:port
	 * 
	 * jdbc:presto://host:port/catalog
	 * 
	 * jdbc:presto://host:port/catalog/schema
	 * 
	 * For example, use the following URL to connect to Presto running on example.net port 8080 with the catalog hive and the schema sales:
	 * 
	 * jdbc:presto://example.net:8080/hive/sales
	 */

	public static Logger log = Logger.getLogger(PrestoJDBC.class);
	private static final String PRESTO_DRIVER = "com.facebook.presto.jdbc.PrestoDriver";//"org.postgresql.Driver";
	private static final String PRESTO_LOCAL = "jdbc:presto://localhost:8080";

	private static void logResultSet(ResultSet rs) throws SQLException {

		ResultSetMetaData rsmd = rs.getMetaData();
		int nCols = rsmd.getColumnCount();
		StringBuffer tmp;
		while (rs.next()) {
			tmp = new StringBuffer();
			tmp.append("{");
			int i = 1;
			tmp.append(String.format("%s:%s", rsmd.getColumnName(i), rs.getObject(i)));
			for (i = i + 1; i <= nCols; i++) {
				tmp.append(String.format(", %s:%s", rsmd.getColumnName(i), rs.getObject(i)));
			}
			tmp.append("}");
			//log.info(tmp.toString());
		}
		rs.close();
	}

	public static void main(String[] args) throws Exception {
		//DOMConfigurator.configure("log4j.xml");
		//Logger log = Logger.getLogger(PrestoJDBC.class);

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
			logResultSet(rSet);
		}

		//showTables(metadata);

		//		Statement stmt = connection.createStatement();
		//
		//		query = "CREATE TABLE IF NOT EXISTS test_table (s STRING, i INT)";
		//		log.info(String.format("-- %s", query));
		//		stmt.execute(query);
		//
		//		query = "SELECT * FROM test_table";
		//		log.info(String.format("-- %s", query));
		//		ResultSet rSet = stmt.executeQuery(query);
		//		logResultSet(rSet);
		//
		//		query = "SHOW DATABASES"; //"SHOW SCHEMAS" also works
		//		log.info(String.format("-- %s", query));
		//		rSet = stmt.executeQuery(query);
		//		logResultSet(rSet);
		//
		//		query = "USE default";
		//		log.info(String.format("-- %s", query));
		//		stmt.execute(query);
		//		query = "SHOW TABLES";
		//		log.info(String.format("-- %s", query));
		//		rSet = stmt.executeQuery(query);
		//		logResultSet(rSet);

		connection.close();
		System.exit(0);
	}
}
