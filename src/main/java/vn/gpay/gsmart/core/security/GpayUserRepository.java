package vn.gpay.gsmart.core.security;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface GpayUserRepository extends JpaRepository<GpayUser, Long>,JpaSpecificationExecutor<GpayUser> {

	GpayUser findByUsername(String username);

	GpayUser findByEmail(String email);
	
	GpayUser findById(long id);
	
	//lấy danh sách user theo mã nhân viên, không chứa id truyền vào
	@Query(value="select c from GpayUser c where c.personnel_code = :personnel_code "
			+ "and c.id <> :id")
	public List<GpayUser> getUserBycode_Personel(
			@Param ("personnel_code") final String personnel_code,
			@Param ("id") final Long id);
	
	@Query(value="select distinct c from GpayUser c " 
			+ " left join AppRole_User d on c.id = d.user_id "
			+ " where lower(c.firstname) like lower(concat('%',:firstname,'%')) "
			+ " and lower(c.middlename) like lower(concat('%',:middlename,'%')) "
			+ " and lower(c.lastname) like lower(concat('%',:lastname,'%')) "
			+ " and lower(c.username) like lower(concat('%',:username,'%')) "
			+ " and (d.role_id = :groupuserid_link or :groupuserid_link is null or :groupuserid_link = 0) "
			+ " order by c.id desc,c.orgid_link, c.lastname, c.middlename, c.firstname "
			)
	public List<GpayUser> getUserList_search(
			@Param ("firstname") final String firstname,
			@Param ("middlename") final String middlename,
			@Param ("lastname") final String lastname,
			@Param ("username") final String username,
			@Param ("groupuserid_link") final Long groupuserid_link
			);
	// firstname, middlename, lastname, username, groupuserid_link
}