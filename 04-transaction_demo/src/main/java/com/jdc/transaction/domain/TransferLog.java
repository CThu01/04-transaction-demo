package com.jdc.transaction.domain;

import java.time.LocalDateTime;

public record TransferLog(
		String from,
		String to,
		LocalDateTime transferTime,
		int transferAmount,
		int fromAmount,
		int toAmount
		) {

}
