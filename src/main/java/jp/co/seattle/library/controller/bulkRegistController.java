
package jp.co.seattle.library.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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
public class bulkRegistController {
	final static Logger logger = LoggerFactory.getLogger(bulkRegistController.class);

	@Autowired
	private BooksService booksService;

	@Autowired
	private ThumbnailService thumbnailService;

	/**
	 * 書籍情報を登録する
	 * 
	 * @param locale ロケール情報
	 * @param bookId bookId
	 * @param model  モデル
	 * @return 遷移先画面
	 */

	@RequestMapping(value = "/bulkRegist", method = RequestMethod.GET) // value＝actionで指定したパラメータ
	// RequestParamでname属性を取得
	public String bulkRegiString(Model model) {
		return "bulkRegist";
	}

	@RequestMapping(value = "/regist", method = RequestMethod.POST)
	public String Regist(Locale locale, Model model, @RequestParam("upload_file") MultipartFile uploadFile) {

		ArrayList<BookDetailsInfo> bookList = new ArrayList<BookDetailsInfo>();
		ArrayList<String> setErrors = new ArrayList<String>();

		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(uploadFile.getInputStream(), StandardCharsets.UTF_8))) {
			String line;
			line = br.readLine();
			int count = 1;

			while (!StringUtils.isEmpty(line)) {

				final String[] split = line.split(",", -1);

				BookDetailsInfo bookInfo = new BookDetailsInfo();
				bookInfo.setTitle(split[0]);
				bookInfo.setAuthor(split[1]);
				bookInfo.setPublisher(split[2]);
				bookInfo.setPublishDate(split[3]);
				bookInfo.setIsbn(split[4]);

				boolean isbnCheck = !(split[4].length() == 10 || split[4].length() == 13 || split[4].length() == 0);
				boolean publishDateCheck = !(split[3].length() == 8 && split[3].matches("^[0-9]+$"));
				boolean requiredcheck = split[0].isEmpty() || split[1].isEmpty() || split[2].isEmpty() || split[3].isEmpty();

				if (isbnCheck || publishDateCheck || requiredcheck) {

					setErrors.add(count + "行目の書籍登録でエラーが起きました。");

				} else {
					bookList.add(bookInfo);
				}

				count++;
				line = br.readLine();

			}
			if (bookList.isEmpty()) {
				model.addAttribute("emptyFail", "CSVに書籍情報がありません。");
				return "bulkRegist";
			}

		} catch (IOException e) {
			throw new RuntimeException("ファイルが読み込めません", e);
		}

		if (setErrors.size() > 0) {
			model.addAttribute("setError", setErrors);

			return "bulkRegist";

		} else {

			for (BookDetailsInfo bookInfo : bookList) {
				booksService.registBook(bookInfo);
			}

			model.addAttribute("bookList", booksService.getBookList());
			return "home";
		}

	}
}
