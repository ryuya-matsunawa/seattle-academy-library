package jp.co.seattle.library.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.seattle.library.service.RentService;

/**
 * Handles requests for the application home page.
 */
@Controller // APIの入り口
public class RentLogController {
	final static Logger logger = LoggerFactory.getLogger(RentLogController.class);

	@Autowired
	private RentService rentService;

//	@Autowired
//	private ThumbnailService thumbnailService;

	// ホームから、貸出履歴一覧画面に遷移
	@RequestMapping(value = "/rentLog", method = RequestMethod.GET)
	// value＝actionで指定したパラメータ(actionはjspのもの)
	// RequestParamでname属性を取得

	/**
	 *
	 * 書籍IDに紐づく書籍詳細情報を取得する
	 * 
	 * @param bookId 書籍ID
	 * @return 書籍情報
	 */
	public String RentLog(Locale locale, Model model) {
		model.addAttribute("bookList", rentService.getRentLogList());
		System.out.println(rentService.getRentLogList());
		return "rentLog";
	}
}