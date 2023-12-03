package com.jdc.transaction.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jdc.transaction.service.TransferService;
import com.jdc.transaction.test.initializer.DatabaseInitializer;

public class TransferTest {

	TransferService transferService;
	
	@BeforeEach
	void beforeEachSetUp() {
		DatabaseInitializer.truncateTables("account","transfer_log");
		transferService = new TransferService();
	}
	
	@Test
	void testCreateTransfer() {
		
		var result = transferService.transfer(1, 2, 50000);
		
		assertEquals("AungAung", result.from());
		assertEquals("Thidar", result.to());
		assertEquals(50000, result.transferAmount());
		assertEquals(200000, result.fromAmount());
		assertEquals(200000, result.toAmount());
	}
}
