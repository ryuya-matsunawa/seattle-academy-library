package jp.co.seattle.library.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.ThumbnailService;


/**
 * Handles requests for the application home page.
 */
@Controller // APIの入り口
public class EditBookController {
	final static Logger logger = LoggerFactory.getLogger(EditBookController.class);

	@Autowired
	private BooksService booksService;

	@Autowired
	private ThumbnailService thumbnailService;

	// 書籍情報画面から編集画面に遷移
	@RequestMapping(value = "/editBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
	// value＝actionで指定したパラメータ(actionはjspのもの)
	
	// RequestParamでname属性を取得
	public String editBook(Locale locale,
		@RequestParam("bookId") Integer bookId, Model model) {
		logger.info("Welcome insertBooks.java! The client locale is {}.", locale);
		
		model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId)); 
		
		return "editbook";
	}

	/**
	 * 書籍情報を更新する
	 * 
	 * @param locale    ロケール情報
	 * @param title     書籍名
	 * @param author    著者名
	 * @param publisher 出版社
	 * @param file      サムネイルファイル
	 * @param model     モデル
	 * @return 遷移先画面
	 */
	@Transactional
	@RequestMapping(value = "/updateBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")

	public String updateBook(Locale locale, 
			@RequestParam("bookId") Integer bookId,
			@RequestParam("title") String title, 
			@RequestParam("author") String author,
			@RequestParam("publisher") String publisher, 
			@RequestParam("publishDate") String publishDate,
			@RequestParam("isbn") String isbn, 
			@RequestParam("explain") String explain,
			@RequestParam("thumbnail") MultipartFile file, Model model) {
		logger.info("Welcome updateBooks.java! The client locale is {}.", locale);

		// パラメータで受け取った書籍情報をDtoに格納する。
		BookDetailsInfo bookInfo = new BookDetailsInfo();
		bookInfo.setBookId(bookId);
		bookInfo.setTitle(title);
		bookInfo.setAuthor(author);
		bookInfo.setPublisher(publisher);
		bookInfo.setPublishDate(publishDate);
		bookInfo.setIsbn(isbn);
		bookInfo.setExplain(explain);

		// クライアントのファイルシステムにある元のファイル名を設定する
		String thumbnail = file.getOriginalFilename();

		if (!file.isEmpty()) {
			try {
				// サムネイル画像をアップロード
				String fileName = thumbnailService.uploadThumbnail(thumbnail, file);
				// URLを取得
				String thumbnailUrl = thumbnailService.getURL(fileName);

				bookInfo.setThumbnailName(fileName);
				bookInfo.setThumbnailUrl(thumbnailUrl);

			} catch (Exception e) {

				// 異常終了時の処理
				logger.error("サムネイルアップロードでエラー発生", e);
				model.addAttribute("bookDetailsInfo", bookInfo);
				return "editBook";
			}
		}

		List<String> errorMessages = new ArrayList<String>();

		// 必須項目チェック
		if (title.isEmpty() || author.isEmpty() || publisher.isEmpty() || publishDate.isEmpty()) {
			errorMessages.add("必須項目を入力してください<br><br>");
		}

		// 出版日桁数&チェック
		if (!(publishDate.length() == 8 && publishDate.matches("^[0-9]+$"))) {
			errorMessages.add("出版日は半角数字のYYYYMMDD形式で入力してください<br><br>");
		}

		// isbn桁数チェック
		Boolean digitNumberCheck = !isbn.matches("^\\d{10}$") && !isbn.matches("^\\d{13}$");
		if (!isbn.isEmpty() && digitNumberCheck) {
			errorMessages.add("ISBNの桁数または半角数字が正しくありません");
		}

		if ((errorMessages == null) || (errorMessages.size() == 0)) {

			// 書籍情報を更新する
			booksService.updateBook(bookInfo);
			model.addAttribute("resultMessage", "更新完了");

			// 更新した書籍の詳細情報を表示するように実装
			model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
			return "details";

		} else {
			model.addAttribute("bookDetailInfo", bookInfo);
			model.addAttribute("errorMessage", errorMessages);
			return "editbook";

		}
		
	

	}
}