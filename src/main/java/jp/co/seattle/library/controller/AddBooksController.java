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
@Controller //APIの入り口
public class AddBooksController {
    final static Logger logger = LoggerFactory.getLogger(AddBooksController.class);

	

    @Autowired
    private BooksService booksService;

    @Autowired
    private ThumbnailService thumbnailService;

    @RequestMapping(value = "/addBook", method = RequestMethod.GET) //value＝actionで指定したパラメータ
    //RequestParamでname属性を取得
    public String login(Model model) {
        return "addBook";
    }

    /**
     * 書籍情報を登録する
     * @param locale ロケール情報
     * @param title 書籍名
     * @param author 著者名
     * @param publisher 出版社
     * @param file サムネイルファイル
     * @param model モデル
     * @return 遷移先画面
     */
    @Transactional
    @RequestMapping(value = "/insertBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String insertBook(Locale locale,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("publisher") String publisher,
            @RequestParam("publish_date") String publishDate,
            @RequestParam("isbn") String isbn,
            @RequestParam("explain") String explain,
            
            @RequestParam("thumbnail") MultipartFile file,
            Model model) {
        logger.info("Welcome insertBooks.java! The client locale is {}.", locale);

        // パラメータで受け取った書籍情報をDtoに格納する。
        BookDetailsInfo bookInfo = new BookDetailsInfo();
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
                return "addBook";
            }
        }
        
        
       

       
        List<String> list = new ArrayList<String>();
        
        if(bookInfo.getTitle().isEmpty() || bookInfo.getAuthor().isEmpty() || bookInfo.getPublisher().isEmpty()) {

        	list.add("必須項目を入力して下さい。");    	
        }
      
       
        if(!(bookInfo.getPublishDate().length() == 8 && bookInfo.getPublishDate().matches("^[0-9]+$"))) {
        

        	list.add("出版日は半角数字のYYYYMMDD形式で入力してください");       		

        }

        //ISBN
        if (!(bookInfo.getIsbn().length()== 10 || bookInfo.getIsbn().length()== 13 || bookInfo.getIsbn().length()== 0 )) {

    		list.add("ISBNの桁数または半角数字が正しくありません");       		

    	}    
        
        if (list.size() == 0) {
            booksService.registBook(bookInfo);

            model.addAttribute("resultMessage", "登録完了");
          
    		model.addAttribute("bookDetailsInfo", booksService.getLatestBookInfo());
            return "details";
        }

        else  {
        	model.addAttribute("errorlists",list);
        	return "addBook";
        }


        
 
    }

}

