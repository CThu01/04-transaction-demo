package com.jdc.transaction.test.initializer;

import java.sql.SQLException;

import com.jdc.transaction.conn.ConnectionManager;

public class DatabaseInitializer {

	public static void truncateTables(String ...tables) {
		
		try(var conn = ConnectionManager.getConnection();
				var stmt = conn.createStatement()){
			
			stmt.execute("set foreign_key_checks=0");
			
			for(var table : tables) {
				stmt.executeUpdate("truncate table %s".formatted(table));
			}
			
			stmt.executeUpdate("insert into account(name,amount) values ('AungAung',200000)");
			stmt.executeUpdate("insert into account(name,amount) values ('Thidar',200000)");
			
			stmt.execute("set foreign_key_checks=1");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
