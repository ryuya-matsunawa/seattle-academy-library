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

import jp.co.seattle.library.dto.UserInfo;
import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.UsersService;

/**
 * アカウント作成コントローラー
 */
@Controller // APIの入り口
public class AccountController {
	final static Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private BooksService booksService;
	@Autowired
	private UsersService usersService;

	@RequestMapping(value = "/newAccount", method = RequestMethod.GET) // value＝actionで指定したパラメータ
	public String createAccount(Model model) {
		return "createAccount";
	}

	/**
	 * 新規アカウント作成
	 *
	 * @param email            メールアドレス
	 * @param password         パスワード
	 * @param passwordForCheck 確認用パスワード
	 * @param model
	 * @return ホーム画面に遷移
	 */
	@Transactional
	@RequestMapping(value = "/createAccount", method = RequestMethod.POST)
	public String createAccount(Locale locale, @RequestParam("email") String email,
			@RequestParam("password") String password, @RequestParam("passwordForCheck") String passwordForCheck,
			Model model) {
		// デバッグ用ログ
		logger.info("Welcome createAccount! The client locale is {}.", locale);

		// パラメータで受け取った書籍情報をDtoに格納する。
		UserInfo userInfo = new UserInfo();
		userInfo.setEmail(email);

		// TODO バリデーションチェック、パスワード一致チェック実装
		boolean validPass = password.matches("^[a-zA-Z0-9]{8,}$");

		if (!validPass) {
			model.addAttribute("validError", "パスワードは半角英数8文字以上");
			return "createAccount";
		}

		if (!(password.equals(passwordForCheck))) {
			model.addAttribute("passError", "パスワードが一致しません");
			return "createAccount";
		}

		userInfo.setPassword(password);
		usersService.registUser(userInfo);

		return "login";
	}

}
