package jp.co.seattle.library.dto;

import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 書籍基本情報格納DTO
 */
@Configuration
@Data
public class RentInfo {

    private int bookId;
    
    private int rentBookId;
    
    private String title;

    private String rentDate;

    private String returnDate;

    public void rentInfo() {

    }

    // コンストラクタ
    public void rentInfo(int bookId, int rentBookId, String title, String rentDate, String returnDate) {
        this.bookId = bookId;
        this.rentBookId = rentBookId;
        this.title = title;
        this.rentDate = rentDate;
        this.returnDate = returnDate;
    }

}