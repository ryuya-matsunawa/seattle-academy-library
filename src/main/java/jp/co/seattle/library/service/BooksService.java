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

		List<BookInfo> getedBookList = jdbcTemplate.query(
				"SELECT id, title, author, publisher, publish_date, thumbnail_url FROM books ORDER BY title ASC",
				new BookInfoRowMapper());

		return getedBookList;
	}

	/**
	 * 書籍IDに紐づく書籍詳細情報を取得する
	 * 
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
	 * 書籍IDに紐づく書籍情報の貸出情報を取得(rentbook_idが対象書籍情報にあるかないか)
	 *
	 * @param bookId 書籍ID
	 * @return 書籍情報
	 */
	public String getRentBookInfo(int bookId) {

		// JSPに渡すデータを設定する

		String sql = "SELECT case WHEN rent.rent_date is null THEN '貸出可' ELSE '貸出中' END from books LEFT OUTER JOIN rent on books.id = rent.id where books.id ="+ bookId;

		String rentBookDetailsInfo = jdbcTemplate.queryForObject(sql, String.class);

		return rentBookDetailsInfo;
	}

	/**
	 * 書籍IDに紐づく書籍詳細情報を引数なしで取得する
	 *
	 * @return 書籍情報
	 */
	public BookDetailsInfo getBookInfo() {

		// JSPに渡すデータを設定する
		String sql = "SELECT * FROM books where id = (SELECT MAX(id) FROM books)";

		BookDetailsInfo bookDetailsInfo = jdbcTemplate.queryForObject(sql, new BookDetailsInfoRowMapper());

		return bookDetailsInfo;
	}

	/**
	 * 書籍を登録する
	 *
	 * @param bookInfo 書籍情報
	 */
	public void registBook(BookDetailsInfo bookInfo) {

		String sql = "INSERT INTO books (title,author,publisher,publish_date,isbn,explain,thumbnail_name,thumbnail_url,reg_date,upd_date) VALUES ('"
				+ bookInfo.getTitle() + "','" + bookInfo.getAuthor() + "','" + bookInfo.getPublisher() + "','"
				+ bookInfo.getPublishDate() + "','" + bookInfo.getIsbn() + "','" + bookInfo.getExplain() + "','"
				+ bookInfo.getThumbnailName() + "','" + bookInfo.getThumbnailUrl() + "'," + "now()," + "now())";
		jdbcTemplate.update(sql);
	}

	/**
	 * 書籍を削除する
	 *
	 * @param bookId 書籍Id
	 */
	public void deleteBook(int bookId) {

		String sql = "DELETE FROM books WHERE id =" + bookId;

		jdbcTemplate.update(sql);

	}

	/**
	 * 書籍を更新する
	 *
	 * @param bookId 書籍Id
	 */
	public void updateBook(BookDetailsInfo bookInfo) {

		String sql = "UPDATE books SET (title,author,publisher,publish_date,isbn,explain,thumbnail_name,thumbnail_url,upd_date) = ('"
				+ bookInfo.getTitle() + "','" + bookInfo.getAuthor() + "','" + bookInfo.getPublisher() + "','"
				+ bookInfo.getPublishDate() + "','" + bookInfo.getIsbn() + "','" + bookInfo.getExplain() + "','"
				+ bookInfo.getThumbnailName() + "','" + bookInfo.getThumbnailUrl() + "'," + "now()) WHERE id="
				+ bookInfo.getBookId();

		jdbcTemplate.update(sql);

	}

	/**
	 * 書籍を一括登録する
	 *
	 * @param bookInfo 書籍情報
	 */
	public void bulkregistBook(BookDetailsInfo bookInfo) {

		String sql = "INSERT INTO books (title,author,publisher,publish_date,isbn,explain,thumbnail_name,thumbnail_url,reg_date,upd_date) VALUES ('"
				+ bookInfo.getTitle() + "','" + bookInfo.getAuthor() + "','" + bookInfo.getPublisher() + "','"
				+ bookInfo.getPublishDate() + "','" + bookInfo.getIsbn() + "','" + bookInfo.getExplain() + "','"
				+ bookInfo.getThumbnailName() + "','" + bookInfo.getThumbnailUrl() + "'," + "now()," + "now())";

		jdbcTemplate.update(sql);
	}

	/**
	 * 入力された文字列が、タイトルに含まれている書籍情報を取得
	 *
	 * @return 検索書籍リスト
	 */
	public List<BookInfo> getSearchBooksList(String title) {
		System.out.println(title);
		System.out.println(title.length());

		List<BookInfo> getedSearchBooksList = jdbcTemplate
				.query("SELECT id, title, author, publisher, publish_date, thumbnail_url FROM books where title LIKE '%"
						+ title + "%';", new BookInfoRowMapper());

		return getedSearchBooksList;

	}

	/**
	 * booksテーブルに書籍の返却日を登録する
	 *
	 * @param bookInfo 書籍情報
	 */
	public void returnRegist(Integer bookId) {

		String sql = "INSERT INTO books (return_date) VALUES(now()) WHERE id = " + bookId;

		jdbcTemplate.update(sql);
	}

}