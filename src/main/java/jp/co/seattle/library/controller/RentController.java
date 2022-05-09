package jp.co.seattle.library.controller;

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

import jp.co.seattle.library.dto.RentBookDetailsInfo;
import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.RentService;
import jp.co.seattle.library.service.ThumbnailService;

/**
 * Handles requests for the application home page.
 */
@Controller // APIの入り口
public class RentController {
	final static Logger logger = LoggerFactory.getLogger(RentController.class);

	@Autowired
	private BooksService booksService;

	@Autowired
	private RentService rentService;

	@Autowired
	private ThumbnailService thumbnailService;

	/**
	 * 対象書籍を貸出リストに登録する
	 *
	 * @param locale ロケール情報
	 * @param bookId 書籍ID
	 * @param model  モデル情報
	 * @return 遷移先画面名
	 */
	@Transactional
	@RequestMapping(value = "/rentBook", method = RequestMethod.POST)
	public String rentBook(Locale locale, @RequestParam("bookId") Integer bookId, Model model) {
		logger.info("Welcome delete! The client locale is {}.", locale);

		
		RentBookDetailsInfo selectedRentBookInfo = rentService.selectRentBookInfo(bookId);
		
		if (!(selectedRentBookInfo == null)){
			model.addAttribute("errorMessage", "貸し出し済みです");
			model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
			return "details";
						
		} else {		
			rentService.rentBook(bookId);
			model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
			return "details";

	}
	}
}
