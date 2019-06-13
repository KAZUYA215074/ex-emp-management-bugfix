package jp.co.sample.emp_management.service;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.sample.emp_management.domain.Administrator;
import jp.co.sample.emp_management.repository.AdministratorRepository;

/**
 * 管理者情報を操作するサービス.
 * 
 * @author igamasayuki
 *
 */
@SpringBootApplication
@Service
@Transactional
public class AdministratorService {
	
	@Autowired
	private AdministratorRepository administratorRepository;

	@Autowired
    PasswordEncoder passwordEncoder;
	

	/**
	 * 管理者情報を登録します.
	 * 
	 * @param administrator 管理者情報
	 * @throws SQLException
	 */
	public void insert(Administrator administrator) {
		administratorRepository.insert(administrator);
	}

	/**
	 * メールアドレスの重複チェックをします.
	 * 
	 * @param address メールアドレス
	 * @return 判定結果
	 */
	public Administrator findByMailAddress(String address) {
		return administratorRepository.findByMailAddress(address);
	}

	/**
	 * ログインをします.
	 * 
	 * @param mailAddress メールアドレス
	 * @param password    パスワード
	 * @return 管理者情報 存在しない場合はnullが返ります
	 */
	public Administrator login(String mailAddress, String passward) {
		Administrator administrator = administratorRepository.findByMailAddressAndPassward(mailAddress, passward);
		return administrator;
	}
	
	/**
	 * パスワードをハッシュ化します.
	 * 
	 * @param password パスワード
	 * @return ハッシュ化されたパスワード
	 */
	public String Encde(String password) {
		return passwordEncoder.encode(password);
	}
}
