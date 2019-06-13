package jp.co.sample.emp_management.controller;

import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.sample.emp_management.domain.Administrator;
import jp.co.sample.emp_management.form.InsertAdministratorForm;
import jp.co.sample.emp_management.form.LoginForm;
import jp.co.sample.emp_management.service.AdministratorService;

/**
 * 管理者情報を操作するコントローラー.
 * 
 * @author igamasayuki
 *
 */
@SpringBootApplication
@Controller
@RequestMapping("/")
public class AdministratorController {

	@Autowired
	private AdministratorService administratorService;

	@Autowired
	private HttpSession session;
	
	@Autowired
    PasswordEncoder passwordEncoder;
	
	@Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	/**
	 * 使用するフォームオブジェクトをリクエストスコープに格納する.
	 * 
	 * @return フォーム
	 */
	@ModelAttribute
	public InsertAdministratorForm setUpInsertAdministratorForm() {
		return new InsertAdministratorForm();
	}

	// (SpringSecurityに任せるためコメントアウトしました)
	@ModelAttribute
	public LoginForm setUpLoginForm() {
		return new LoginForm();
	}

	/////////////////////////////////////////////////////
	// ユースケース：管理者を登録する
	/////////////////////////////////////////////////////
	/**
	 * 管理者登録画面を出力します.
	 * 
	 * @return 管理者登録画面
	 */
	@RequestMapping("/toInsert")
	public String toInsert() {
		// トークン
		String token = UUID.randomUUID().toString();
		session.setAttribute("insertToken", token);
		
		return "administrator/insert";
	}

	/**
	 * 管理者情報を登録します.
	 * 
	 * @param form 管理者情報用フォーム
	 * @return ログイン画面へリダイレクト
	 */
	@RequestMapping("/insert")
	public String insert(@Validated InsertAdministratorForm form, BindingResult result, Model model, String insertToken) {

		// トークン
		String tokenInSession = (String) session.getAttribute("insertToken");
		if (insertToken == null || !(insertToken.equals(tokenInSession))) {
			return "redirect:/toInsert";
		}
		session.removeAttribute("insertToken");
		
		// 入力値チェック
		if (result.hasErrors()) {
			return this.toInsert();
		}

		// パスワード誤入力チェック
		if (!form.getPassword().equals(form.getPasswordAgain())) {
			model.addAttribute("passwordError", "入力されたパスワードが異なります");
			return this.toInsert();
		}

		Administrator administrator = new Administrator();
		// フォームからドメインにプロパティ値をコピー
		BeanUtils.copyProperties(form, administrator);

		// メールアドレスチェック
		if (administratorService.findByMailAddress(administrator.getMailAddress()) != null) {
			model.addAttribute("mailAddressError", "既に同じメールアドレスが登録されています");
			return this.toInsert();
		}
		// パスワードのハッシュ化
		String hashedPassword = administratorService.encde(administrator.getPassword());
		administrator.setPassword(hashedPassword);
		
		administratorService.insert(administrator);

		return "redirect:/";
	}

	/////////////////////////////////////////////////////
	// ユースケース：ログインをする
	/////////////////////////////////////////////////////
	/**
	 * ログイン画面を出力します.
	 * 
	 * @return ログイン画面
	 */
	@RequestMapping("/")
	public String toLogin() {
		String token = UUID.randomUUID().toString();
		session.setAttribute("loginToken", token);
		return "administrator/login";
	}

	/**
	 * ログインします.
	 * 
	 * @param form   管理者情報用フォーム
	 * @param result エラー情報格納用オブッジェクト
	 * @return ログイン後の従業員一覧画面
	 */
	@RequestMapping("/login")
	public String login(@Validated LoginForm form, BindingResult result, Model model, String loginToken) {
		
		// トークン
		String tokenInSession = (String) session.getAttribute("loginToken");
		if (loginToken == null || !(loginToken.equals(tokenInSession))) {
			return "redirect:/";
		}
		session.removeAttribute("loginToken");
		
		// 入力値チェック
		if (result.hasErrors()) {
			return this.toLogin();
		}

		// 管理者情報の照合
		if (administratorService.findByMailAddress(form.getMailAddress()) == null) {
			model.addAttribute("errorMessage", "そのメールアドレスは登録されていません");
			return this.toInsert();
		}
		Administrator administrator = administratorService.findByMailAddress(form.getMailAddress());
		if(!passwordEncoder.matches(form.getPassword(), administrator.getPassword())) {
			model.addAttribute("errorMessage", "メールアドレスまたはパスワードが間違っています");
			return toLogin();
		}
		
//		Administrator administrator = administratorService.login(form.getMailAddress(), form.getPassword());
//		if (administrator == null) {
//			model.addAttribute("errorMessage", "メールアドレスまたはパスワードが間違っています");
//			return toLogin();
//		}

		session.setAttribute("administratorName", administrator.getName());

		return "forward:/employee/showList";
	}

	/////////////////////////////////////////////////////
	// ユースケース：ログアウトをする
	/////////////////////////////////////////////////////
	/**
	 * ログアウトをします. (SpringSecurityに任せるためコメントアウトしました)
	 * 
	 * @return ログイン画面
	 */
	@RequestMapping(value = "/logout")
	public String logout() {
		session.invalidate();
		return "redirect:/";
	}

}
