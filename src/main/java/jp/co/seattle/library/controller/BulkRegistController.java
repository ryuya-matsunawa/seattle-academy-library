package jp.co.seattle.library.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.ThumbnailService;

@Controller // APIの入り口
public class BulkRegistController {
	final static Logger logger = LoggerFactory.getLogger(BulkRegistController.class);

	@Autowired
	private BooksService booksService;

	@Autowired
	private ThumbnailService thumbnailService;

	@RequestMapping(value = "/bulkRegist", method = RequestMethod.GET) // value＝actionで指定したパラメータ
	public String bulkRegist(Model model) {
		return "bulkRegist";
	}

	/**
	 * 書籍情報を登録する(CSV一括登録)
	 * 
	 * @param uploadFile CSVファイル
	 * @param model      モデル
	 * @return 遷移先画面
	 **/

	@RequestMapping(value = "/bulkRegist", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
	public String uploadFile(@RequestParam("uploadFile") MultipartFile uploadFile, Model model) {
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(uploadFile.getInputStream(), StandardCharsets.UTF_8))) {

			String inputValue;
			int lineCount = 0;
			List<String> errorMessages = new ArrayList<String>();
			List<BookDetailsInfo> bookLists = new ArrayList<BookDetailsInfo>();
			if (uploadFile.isEmpty()) {
				errorMessages.add("CSVファイルが読み取れません");
			}
			while ((inputValue = br.readLine()) != null) {
				final String[] inputValues = inputValue.split(",", -1);

				BookDetailsInfo bookInfo = new BookDetailsInfo();
				bookInfo.setTitle(inputValues[0]);
				bookInfo.setAuthor(inputValues[1]);
				bookInfo.setPublisher(inputValues[2]);
				bookInfo.setPublishDate(inputValues[3]);
				bookInfo.setIsbn(inputValues[4]);

				// 行数カウントインクリメント
				lineCount++;

				// 一括登録バリデーション

				if ((inputValues[0].isEmpty() || inputValues[1].isEmpty() || inputValues[2].isEmpty() || inputValues[3].isEmpty())
						|| (inputValues[3].length() != 8 || !(inputValues[3].matches("^[0-9]*$")))
						|| (inputValues[4].length() != 10 && inputValues[4].length() != 13
						&& inputValues[4].length() != 0 || !(inputValues[4].matches("^[0-9]*$")))) {
					errorMessages.add(lineCount + "行目でバリデーションエラーが発生しました");
				} else {
					bookLists.add(bookInfo);
				}
			}
			// エラーメッセージあればrender
			if (CollectionUtils.isEmpty(errorMessages)) {
				bookLists.forEach(bookList -> booksService.bulkRegist(bookList));
				return "redirect:home";
			} else {
				model.addAttribute("errorMessages", errorMessages);
				return "bulkRegist";
			}
		} catch (Exception e) {
			List<String> errorMessages = new ArrayList<String>();
			errorMessages.add("ファイルが読み込めません");
			model.addAttribute("errorMessages", errorMessages);
			return "bulkRegist";
		}
	}
}
