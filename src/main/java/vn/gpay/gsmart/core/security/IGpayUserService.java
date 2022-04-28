package vn.gpay.gsmart.core.security;

import java.util.List;

import vn.gpay.gsmart.core.base.Operations;
import vn.gpay.gsmart.core.org.Org;

public interface IGpayUserService extends Operations<GpayUser>{

	public GpayUser findByUsername(String username);

	public GpayUser findByEmail(String email);
	
	public GpayUser findById(long userid);
    public List<GpayUser> getUserList(long orgid_link,String textsearch,int status);
    public List<GpayUser> getUserList_page(String firstname, String middlename, String lastname,
    		String username, Long groupuserid_link);

	List<GpayUser> getUserinOrgid(List<Org> listorg);
	
	//lấy danh sách user theo mã nhân viên, không chứa id truyền vào
	public  List<GpayUser> getUserBycode_Personel(String personnel_code, Long id);
	List<GpayUser> getUserList_search(String firstname, String middlename, String lastname, String username, Long groupuserid_link);
}
