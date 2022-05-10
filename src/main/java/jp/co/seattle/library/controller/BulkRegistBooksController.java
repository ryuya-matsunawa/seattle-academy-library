package jp.co.seattle.library.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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

/**
 * Handles requests for the application home page.
 */
@Controller // APIの入り口
public class BulkRegistBooksController {
	final static Logger logger = LoggerFactory.getLogger(BulkRegistBooksController.class);

	@Autowired
	private BooksService booksService;

	/**
	 * ホームから、一括登録画面に遷移
	 * 
	 * @return 遷移先画面
	 */
	@RequestMapping(value = "/bulkRegistBooks", method = RequestMethod.GET)
	// value＝actionで指定したパラメータ(actionはjspのもの)
	// RequestParamでname属性を取得
	public String bulkRegistBooks(Model model) {
		return "bulkRegistBooks";
	}
	
	/**
	 * CSVファイルを受け取ってDBに登録
	 * 
	 * @param locale    ロケール情報
	 * @param file      サムネイルファイル
	 * @param model     モデル
	 * @return 遷移先画面
	 */
	@Transactional
	@RequestMapping(value = "/bulkRegistBook", method = RequestMethod.POST)

	public String bulkRegistBook(Locale locale, @RequestParam("file") MultipartFile uploadFile, Model model) {

		List<String> bulkerrorMessages = new ArrayList<>();
		List<BookDetailsInfo> bulkRegistBookList = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(uploadFile.getInputStream(), StandardCharsets.UTF_8))) {

			String inputValue;
			
			int lineCount = 0;

			while ((inputValue = br.readLine()) != null) {
				String[] inputValues = inputValue.split(",", -1);
				BookDetailsInfo bookInfo = new BookDetailsInfo();
				
				lineCount++;

				// TODO バリデーションチェック
				// 必須項目チェック
				Boolean mustCheck = bookInfo.getTitle().isEmpty() || bookInfo.getAuthor().isEmpty()
						|| bookInfo.getPublisher().isEmpty() || bookInfo.getPublishDate().isEmpty();

				// 出版日桁数チェック
				Boolean dateCheck = !(bookInfo.getPublishDate().matches("^\\d{8}$"));

				// isbn桁数チェック
				Boolean isbnCheck = !bookInfo.getIsbn().isEmpty() && !bookInfo.getIsbn().matches("^\\d{10}$")
						&& !bookInfo.getIsbn().matches("^\\d{13}$");

				if (mustCheck || dateCheck || isbnCheck) {
					bulkerrorMessages.add(lineCount + "行目の書籍登録でエラーが起きました。");

				// エラーがない
				} else {
					
					bookInfo.setTitle(inputValues[0]);
					bookInfo.setAuthor(inputValues[1]);
					bookInfo.setPublisher(inputValues[2]);
					bookInfo.setPublishDate(inputValues[3]);
					bookInfo.setIsbn(inputValues[4]);
					
					bulkRegistBookList.add(bookInfo);
				}
			}

		// なんらかのエラーでファイルが読み込めなかった場合、エラー文を表示し、書籍一括画面に戻る
		} catch (IOException e) {
			throw new RuntimeException("ファイルが読み込めません", e);
		}
		
		// CSVファイルにデータがあるかどうか
		if (bulkRegistBookList.size() == 0) {
			bulkerrorMessages.add("CSVに書籍情報がありません。");
		}

		// エラーメッセージが一つでも表示されていれば、書籍一括登録画面に戻る
		if (bulkerrorMessages.size() > 0) {
			model.addAttribute("errorMessage", bulkerrorMessages);
			return "bulkRegistBooks";

		// 一括登録し、ホームに戻る
		} else {
			
			// bulkregistBookメソッドを繰り返して登録してる
			for (BookDetailsInfo bookInfo : bulkRegistBookList) {
				booksService.bulkregistBook(bookInfo);
			}
			model.addAttribute("bookList", booksService.getBookList());
			return "home";
		}

	}
}
