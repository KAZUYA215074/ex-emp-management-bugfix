package jp.co.sample.emp_management.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.sample.emp_management.domain.Employee;
import jp.co.sample.emp_management.repository.EmployeeRepository;

/**
 * 従業員情報を操作するサービス.
 * 
 * @author igamasayuki
 *
 */
@Service
@Transactional
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	/**
	 * 従業員情報を全件取得します.
	 * 
	 * @return 従業員情報一覧
	 */
	public List<Employee> showList() {
		List<Employee> employeeList = employeeRepository.findAll();
		return employeeList;
	}

	/**
	 * 従業員情報を取得します.
	 * 
	 * @param id ID
	 * @return 従業員情報
	 * @throws 検索されない場合は例外が発生します
	 */
	public Employee showDetail(Integer id) {
		Employee employee = employeeRepository.load(id);
		return employee;
	}

	/**
	 * 従業員情報を更新します.
	 * 
	 * @param employee 更新した従業員情報
	 */
	public void update(Employee employee) {
		employeeRepository.update(employee);
	}

	public List<Employee> serchEmployeeList(String name) {
		List<Employee> employeeList = employeeRepository.findEmployeeList(name);
		return employeeList;
	}

	/**
	 * 従業員情報を登録します.
	 * 
	 * @param employee
	 */
	public void insert(Employee employee) {
		employeeRepository.insert(employee);
	}

	/**
	 * 従業員リストを分割します
	 * 
	 * @param mainList 全ての従業員リスト
	 * @return 分割した従業員リストのリスト
	 */
	public List<List<Employee>> splitList(List<Employee> mainList) {
		List<Employee> employeeSubList;
		List<List<Employee>> employeeList = new ArrayList<List<Employee>>();

		for (int i = 0; i <= (int) (mainList.size() / 10); i++) {
			employeeSubList = new ArrayList<Employee>();
			if (i == (int) (mainList.size() / 10)) {
				employeeSubList = mainList.subList(0 + i * 10, (mainList.size()));
				System.out.println(0 + i * 10 + "," + (mainList.size()));
			} else {
				employeeSubList = mainList.subList(0 + i * 10, (i + 1) * 10);
				System.out.println(0 + i * 10 + "," + (i + 1) * 10);
			}
			employeeList.add(employeeSubList);
		}

		return employeeList;

	}
}
