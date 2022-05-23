package jp.co.seattle.library.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jp.co.seattle.library.dto.RentInfo;
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
	 * rentテーブルに書籍を貸出登録する()
	 *
	 *初回の貸出のため、rentテーブルにレコードがない時、rentテーブルにidとrent_dateを追加（insert）
	 *2回目以降の貸出の場合、rentテーブルのrent_dateが空、return_dateに返却日が入っているため、rent_dateに貸出日、return_dateを空に更新（update）
	 *
	 * @param bookId 書籍Id
	 */
	public void rentBook(int bookId) {
		
		String sql = "insert into rent (id, rent_date, return_date) values (" + bookId + "," + "now() "+ "," + "now()) on conflict (id) do update set rent_date = " + "now()" + ", return_date = null";
		
		jdbcTemplate.update(sql);

	}

	
	/**
	 * 貸出書籍情報取得(エラーメッセージ表示情報) 
	 *
	 * @param bookId 書籍ID
	 * @return 書籍情報
	 */

	public String selectRentBookInfo(int bookId) {

		String sql = "SELECT rent_date FROM rent where id =" + bookId;

		try {
			String selectedRentBookInfo = jdbcTemplate.queryForObject(sql, String.class);

			return selectedRentBookInfo;

		} catch (Exception e) {
			
			return null;
		}

	}
	
	/**
	 * rentテーブルの書籍の返却日情報を登録し、貸出日情報を削除する
	 *
	 * @param bookInfo 書籍情報
	 */
	public void returnBook(Integer bookId) {

		String sql = "UPDATE rent SET return_date = now(), rent_date = null where id =" + bookId;
				
		jdbcTemplate.update(sql);
	}
	
	
	/*
	 * 貸出書籍情報からタイトル、貸出日、返却日を取得する
	 *
	 * @param bookId 書籍Id
	 */
	public List<RentInfo> getRentLogList() {

		List<RentInfo> getedRentLogList = jdbcTemplate.query(
				"SELECT rent.id, title, rent_date, return_date FROM books INNER JOIN rent on books.id = rent.id",
				new RentRowMapper());

		return getedRentLogList;

	}
}
