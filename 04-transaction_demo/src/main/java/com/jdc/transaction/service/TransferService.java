package com.jdc.transaction.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.jdc.transaction.conn.ConnectionManager;
import com.jdc.transaction.domain.Account;
import com.jdc.transaction.domain.TransferLog;
import com.jdc.transaction.util.exception.MessageDbException;

public class TransferService {

	public TransferLog transfer(int from,int to,int amount) {
		
		try(var conn = ConnectionManager.getConnection()){
			
			try {

				conn.setAutoCommit(false);
				// get amount from account 
				var fromAmount = getAmount(conn, from);
				
				// get amount to account
				var toAmount = getAmount(conn, to);
				
				// update amount from account
				if(fromAmount < amount) {
					throw new MessageDbException("Not Enough Money");
				}
				updateAmount(conn, from, fromAmount-amount);
				
				// update amount to account
				updateAmount(conn, to, toAmount+amount);
				
				// insert transfer log
				int id = createTransferLog(conn, from, to, amount, fromAmount, toAmount);
				
				// get transfer log data
				var result = getTransferLog(conn, id);
				
				conn.commit();
				return result;

			} catch(SQLException e) {
				conn.rollback(); 
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private TransferLog getTransferLog(Connection conn, int id) throws SQLException {

		var stmt = conn.prepareStatement("""
				select fa.name fname,ta.name tname,tl.transfer_time transferTime,tl.transfer_amount amount,
				tl.from_amount famount,tl.to_amount tamount 
				from transfer_log tl 
				join account fa on fa.id=tl.from_account 
				join account ta on ta.id=tl.to_account 
				where tl.id=?
				""");
		stmt.setInt(1, id);
		var resultSet = stmt.executeQuery();
		
		while(resultSet.next()) {
			return new TransferLog(
					resultSet.getString("fname"), 
					resultSet.getString("tname"), 
					resultSet.getTimestamp("transferTime").toLocalDateTime(), 
					resultSet.getInt("amount"), 
					resultSet.getInt("famount"), 
					resultSet.getInt("tamount"));
		}
		return null;
	}

	private int createTransferLog(Connection conn, int from, int to, int amount, int fromAmount,int toAmount) throws SQLException {
	
		var stmt = conn.prepareStatement("""
				insert into transfer_log(from_account,to_account,transfer_amount,from_amount,to_amount) values (?,?,?,?,?)
				""",Statement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, from);
		stmt.setInt(2, to);
		stmt.setInt(3, amount);
		stmt.setInt(4, fromAmount);
		stmt.setInt(5, toAmount);
		stmt.executeUpdate();
		
		var keys = stmt.getGeneratedKeys();
		
		while(keys.next()) {
			return keys.getInt(1);
		}
		
		return 0;
	}

	private void updateAmount(Connection conn,int id, int amount) throws SQLException {

		var stmt = conn.prepareStatement("update account set amount=? where id=?");
		stmt.setInt(1, amount);
		stmt.setInt(2, id);
		stmt.executeUpdate();
	}

	private int getAmount(Connection conn, int account) throws SQLException {

		var stmt = conn.prepareStatement("select amount from account where id=?");
		stmt.setInt(1, account);
		var result = stmt.executeQuery();
		while(result.next()) {
			return result.getInt("amount");
		}
		
		throw new MessageDbException("Account Not Found");
	}
	
	
	
	
	
}











