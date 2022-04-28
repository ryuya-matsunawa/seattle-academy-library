package jp.co.seattle.library.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.dto.BookInfo;
import jp.co.seattle.library.rowMapper.BookDetailsInfoRowMapper;
import jp.co.seattle.library.rowMapper.BookInfoRowMapper;

/**
 * 書籍サービス
 * 
 * booksテーブルに関する処理を実装する
 */
@Service
public class BooksService {
	final static Logger logger = LoggerFactory.getLogger(BooksService.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 書籍リストを取得する
	 *
	 * @return 書籍リスト
	 */
	public List<BookInfo> getBookList() {

		// TODO 取得したい情報を取得するようにSQLを修正
		List<BookInfo> getedBookList = jdbcTemplate.query(
				"SELECT id, title, author, publisher, publish_date, thumbnail_name, thumbnail_url FROM books ORDER BY title asc",
				new BookInfoRowMapper());

		return getedBookList;
	}

	/**
	 * 書籍IDに紐づく書籍詳細情報を取得する
	 *
	 * @param bookId 書籍ID
	 * @return 書籍情報
	 */
	public BookDetailsInfo getBookInfo(int bookId) {

		// JSPに渡すデータを設定する
		String sql = "SELECT * FROM books where id =" + bookId;

		BookDetailsInfo bookDetailsInfo = jdbcTemplate.queryForObject(sql, new BookDetailsInfoRowMapper());

		return bookDetailsInfo;
	}

	/**
	 * 書籍を登録する
	 *
	 * @param bookInfo 書籍情報
	 */
	public void registBook(BookDetailsInfo bookInfo) {

		String sql = "INSERT INTO books (title, author, publisher, publish_date, thumbnail_name, thumbnail_url, isbn, description, reg_date, upd_date) VALUES ('"
				+ bookInfo.getTitle() + "','" + bookInfo.getAuthor() + "','" + bookInfo.getPublisher() + "','"
				+ bookInfo.getPublishDate() + "','" + bookInfo.getThumbnailName() + "','" + bookInfo.getThumbnailUrl()
				+ "','" + bookInfo.getIsbn() + "','" + bookInfo.getDescription() + "'," + "now()," + "now())";

		jdbcTemplate.update(sql);

	}

	// 本の編集内容を更新
	// updateBook
	public void updateBook(BookDetailsInfo bookInfo) {
		String sql;
		if (bookInfo.getThumbnailUrl() == null) {
			sql = "update books set title ='" + bookInfo.getTitle() + "', author ='" + bookInfo.getAuthor()
					+ "' , publisher ='" + bookInfo.getPublisher() + "', publish_date ='" + bookInfo.getPublishDate()
					+ "' , upd_date = 'now()'" + ",isbn = '" + bookInfo.getIsbn() + "', description= '"
					+ bookInfo.getDescription() + "' where id =" + bookInfo.getBookId() + ";";
		} else {
			sql = "update books set title ='" + bookInfo.getTitle() + "', author ='" + bookInfo.getAuthor()
					+ "' , publisher ='" + bookInfo.getPublisher() + "', publish_date ='" + bookInfo.getPublishDate()
					+ "' , thumbnail_url ='" + bookInfo.getThumbnailUrl() + "', thumbnail_name ='"
					+ bookInfo.getThumbnailName() + "' , upd_date = 'now()'" + ",isbn = '" + bookInfo.getIsbn()
					+ "', description = '" + bookInfo.getDescription() + "' where id =" + bookInfo.getBookId() + ";";
		}

		jdbcTemplate.update(sql);
	}

	// 書籍を削除
	public void deleteBook(int bookId) {

		String sql = "DELETE FROM books WHERE id =" + bookId;

		jdbcTemplate.update(sql);
	}

	public int getMaxbookId() {

		String sql = "SELECT max(id) FROM books";
		int bookId = jdbcTemplate.queryForObject(sql, Integer.class);
		return bookId;
	}
}
