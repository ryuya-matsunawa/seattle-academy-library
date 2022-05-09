package jp.co.seattle.library.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jp.co.seattle.library.dto.RentBookDetailsInfo;
import jp.co.seattle.library.rowMapper.RentRowMapper;

/**
 * 書籍サービス
 * 
 * booksテーブルに関する処理を実装する
 */
@Service
public class RentService {
	final static Logger logger = LoggerFactory.getLogger(RentService.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 書籍を貸出登録する
	 *
	 * @param bookId 書籍Id
	 */
	public void rentBook(int bookId) {

		String sql = "INSERT INTO rent (id,rent_date) VALUES ('" + bookId + "'," + "now())";

		jdbcTemplate.update(sql);

	}

	/**
	 * 貸出書籍情報取得
	 *
	 * @param bookId 書籍ID
	 * @return 書籍情報
	 */

	public RentBookDetailsInfo selectRentBookInfo(int bookId) {

		String sql = "SELECT * FROM rent where id =" + bookId;

		try {
			RentBookDetailsInfo selectedRentBookInfo = jdbcTemplate.queryForObject(sql, new RentRowMapper());

			return selectedRentBookInfo;

		} catch (Exception e) {
			return null;
		}

	}
}
