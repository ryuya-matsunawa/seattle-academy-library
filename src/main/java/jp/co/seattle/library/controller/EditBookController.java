package jp.co.seattle.library.controller;

import java.util.ArrayList;
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

	/**
	 * 
	 * 書籍情報を更新する
	 * 
	 * @param locale ローケル情報
	 * @palam model モデル
	 * @param bookId 書籍ID
	 * @return 遷移先画面
	 */
	@RequestMapping(value = "/editBook", method = RequestMethod.POST) // value＝actionで指定したパラメータ
	// RequestParamでname属性を取得
	public String editBook(Locale locale, @RequestParam("bookId") Integer bookId, Model model) {
		model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
		return "editBook";
	}

	/**
	 * 書籍情報を登録する
	 * 
	 * @param locale      ロケール情報
	 * @param bookId      書籍ID
	 * @param title       書籍名
	 * @param author      著者名
	 * @param publisher   出版社
	 * @param publishDate 出版日
	 * @param thumbnail   サムネイル
	 * @param isbn        コード
	 * @param model       モデル
	 * @return 遷移先画面
	 */
	@Transactional
	@RequestMapping(value = "/updateBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
	public String insertBook(Locale locale, @RequestParam("bookId") int bookId, @RequestParam("title") String title,
			@RequestParam("author") String author, @RequestParam("publisher") String publisher,
			@RequestParam("thumbnail") MultipartFile file, @RequestParam("publishDate") String publishDate,
			@RequestParam("isbn") String isbn, @RequestParam("description") String description, Model model) {
		logger.info("Welcome insertBooks.java! The client locale is {}.", locale);

		// パラメータで受け取った書籍情報をDtoに格納する。
		BookDetailsInfo bookInfo = new BookDetailsInfo();
		bookInfo.setBookId(bookId);
		bookInfo.setTitle(title);
		bookInfo.setAuthor(author);
		bookInfo.setPublisher(publisher);
		bookInfo.setPublishDate(publishDate);
		bookInfo.setIsbn(isbn);
		bookInfo.setDescription(description);

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
		// タスク5バリデーションチェック
		ArrayList<String> errorMessages = new ArrayList<String>();

		if (title.isEmpty() || author.isEmpty() || publisher.isEmpty() || publishDate.isEmpty()) {
			errorMessages.add("入力必須項目があります");

		}
		if (publishDate.isEmpty() || publishDate.length() != 8 || !(publishDate.matches("^[0-9]*$"))) {
			errorMessages.add("出版日は半角数字のYYYYMMDD形式で入力してください");

		}

		if (isbn.length() != 10 && isbn.length() != 13 && isbn.length() != 0 || !(isbn.matches("^[0-9]*$"))) {
			errorMessages.add("ISBNの桁数または半角数字が正しくありません");

		}

		if (errorMessages.isEmpty()) {

			booksService.updateBook(bookInfo);

			model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));

		} else {

			model.addAttribute("errorMessages", errorMessages);
			model.addAttribute("bookDetailsInfo", bookInfo);
			return "editBook";
		}

		return "details";
	}

}