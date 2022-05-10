package jp.co.seattle.library.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.seattle.library.service.BooksService;

@Controller // APIの入り口
public class ReturnBookController {
	final static Logger logger = LoggerFactory.getLogger(ReturnBookController.class);

	@Autowired
	private BooksService booksService;

	/**
	 * 書籍貸出
	 * 
	 * @param locale ロケール情報
	 * @param bookId  書籍ID
	 * @param model      モデル
	 * @return 遷移先画面
	 **/

	@RequestMapping(value = "/returnBook", method = RequestMethod.POST) // value＝actionで指定したパラメータ
	public String returnBook(Locale locale, @RequestParam("bookId") Integer bookId, Model model) {

		int count2 = booksService.size(bookId);

		if (count2 == 1) {
			booksService.returnBook(bookId);

		
		} else if(count2 == 0) {
			model.addAttribute("ErrorMessage", "貸出しされていません。");

		}

		model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));

		return "details";
	}
}
