package jp.co.seattle.library.dto;

import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 書籍詳細情報格納DTO
 *
 */
@Configuration
@Data
public class BookDetailsInfo {

    private int bookId;

    private String title;

    private String author;

    private String publisher;
    
    private String publishDate;

    private String thumbnailUrl;

    private String thumbnailName;
    
    private String isbn;
    
    private String description;

    public BookDetailsInfo() {

    }

    public BookDetailsInfo(int bookId, String title, String author, String publisher, String publishDate, 
            String thumbnailUrl, String thumbnailName, String isbn, String description) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.thumbnailUrl = thumbnailUrl;
        this.thumbnailName = thumbnailName;
        this.isbn = isbn;
        this.description = description;
        
       
    }

}