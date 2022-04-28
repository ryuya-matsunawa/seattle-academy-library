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


@Controller

public class EditBookController {
	
	final static Logger logger = LoggerFactory.getLogger(EditBookController.class);

	
	
	 @Autowired
	    private BooksService booksService;
	 @Autowired
	    private ThumbnailService thumbnailService;
	
	 /**
	 	 * 書籍情報を登録する
	 	 * @param locale ロケール情報
	 	 * @param bookId bookId
	    * @param model モデル
	    * @return 遷移先画面
	    * 
	 	 */
	
	
	 @RequestMapping(value = "/editBook", method = RequestMethod.POST)
    public String editBook(Locale locale,
            @RequestParam("bookId") Integer bookId,
            Model model) {
		 model.addAttribute("bookeditInfo", booksService.getBookInfo(bookId));
		 return "editBook";
		
	}
	
	 /**
	     * 書籍情報を登録する
	     * @param locale ロケール情報
	     * @param title 書籍名
	     * @param author 著者名
	     * @param publisher 出版社
	     * @param publish_date 出版日
	     * @param isbn 
	     * @param explain 説明文
	     * @param file サムネイルファイル
	     * @param model モデル
	     * @return 遷移先画面
	     */
	 
	 
	 @Transactional
	    @RequestMapping(value = "/updateBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
	    public String insertBook(Locale locale,
	    		@RequestParam("bookId") Integer bookId,
	            @RequestParam("title") String title,
	            @RequestParam("author") String author,
	            @RequestParam("publisher") String publisher,
	            @RequestParam("publish_date") String publishDate,
	            @RequestParam("isbn") String isbn,
	            @RequestParam("explain") String explain,
	            @RequestParam("thumbnail") MultipartFile file,
	            Model model) {
	        logger.info("Welcome editBook.java! The client locale is {}.", locale);

	        // パラメータで受け取った書籍情報をDtoに格納する。
	        BookDetailsInfo bookInfo = new BookDetailsInfo();
	        bookInfo.setBookId(bookId);
	        bookInfo.setTitle(title);
	        bookInfo.setAuthor(author);
	        bookInfo.setPublisher(publisher);
	        bookInfo.setPublishDate(publishDate);
	        bookInfo.setIsbn(isbn);
	        bookInfo.setExplain(explain);

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
	                model.addAttribute("bookeditInfo", bookInfo);
	                return "editBook";
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
	            booksService.updateBook(bookInfo, bookId);

	            model.addAttribute("resultMessage", "登録完了");
	          
	    		model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookInfo.getBookId()));
	            return "details";
	        }

	        else  {
	        	model.addAttribute("errorlists",list);
	        	model.addAttribute("bookeditInfo", bookInfo);
	        	return "editBook";
	        }
	 
	       
	 
	 
	
	 }
        
}

