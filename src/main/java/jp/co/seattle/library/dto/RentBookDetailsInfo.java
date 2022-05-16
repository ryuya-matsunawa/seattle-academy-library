package jp.co.seattle.library.dto;

import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 貸出書籍情報格納DTO
 *
 */
@Configuration
@Data
public class RentBookDetailsInfo {

	private int rentBookId;

	private int bookId;

	public RentBookDetailsInfo() {

	}

	public RentBookDetailsInfo(int rentBookId, int bookId) {
		this.rentBookId = rentBookId;
		this.bookId = bookId;
	}

}