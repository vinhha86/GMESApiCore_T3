package vn.gpay.gsmart.core.api.users;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import vn.gpay.gsmart.core.approle.AppFunction;
import vn.gpay.gsmart.core.approle.AppRole_User;
import vn.gpay.gsmart.core.approle.AppRole_User_Service;
import vn.gpay.gsmart.core.approle.AppUserFunction;
import vn.gpay.gsmart.core.approle.AppUserFunction_Service;
import vn.gpay.gsmart.core.approle.AppUserMenu;
import vn.gpay.gsmart.core.approle.AppUserMenu_Service;
import vn.gpay.gsmart.core.approle.IAppFunctionService;
import vn.gpay.gsmart.core.approle.IAppRole_User_Service;
import vn.gpay.gsmart.core.base.ResponseBase;
import vn.gpay.gsmart.core.menu.IUserMenuService;
import vn.gpay.gsmart.core.menu.Menu;
import vn.gpay.gsmart.core.menu.MenuServiceImpl;
import vn.gpay.gsmart.core.menu.UserMenu;
import vn.gpay.gsmart.core.org.IOrgService;
import vn.gpay.gsmart.core.org.Org;
import vn.gpay.gsmart.core.pcontract.IPContractService;
import vn.gpay.gsmart.core.personel.IPersonnel_Service;
import vn.gpay.gsmart.core.personel.Personel;
import vn.gpay.gsmart.core.security.GpayAuthentication;
import vn.gpay.gsmart.core.security.GpayRole;
import vn.gpay.gsmart.core.security.GpayUser;
import vn.gpay.gsmart.core.security.GpayUserOrg;
import vn.gpay.gsmart.core.security.IGpayUserOrgService;
import vn.gpay.gsmart.core.security.IGpayUserService;
import vn.gpay.gsmart.core.utils.AtributeFixValues;
import vn.gpay.gsmart.core.utils.ResponseMessage;


@RestController
@RequestMapping("/api/v1/users")
public class UserAPI {

	private PasswordEncoder passwordEncoder;
	@Autowired IGpayUserService  userDetailsService ;
	@Autowired IOrgService orgService;
	@Autowired IUserMenuService  userMenuService ;
	@Autowired IAppRole_User_Service appuserService;
	@Autowired MenuServiceImpl menuService;
	@Autowired AppUserMenu_Service usermenuService;
	@Autowired AppUserFunction_Service appuserfService;
	@Autowired AppRole_User_Service roleuserService;
	@Autowired IAppFunctionService appfuncService;
	@Autowired IPContractService pcontractService;
	@Autowired IGpayUserOrgService userOrgService;
	@Autowired IPersonnel_Service personnelService;
	
	@RequestMapping(value = "/user_create",method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> GetSkuByCode( @RequestBody UserCreateRequest entity,HttpServletRequest request ) {
		ResponseBase response = new ResponseBase();
		try {
			GpayUser appuser =entity.data;
			if(appuser.getId()!=null && appuser.getId()>0) {
				GpayUser appuserold = userDetailsService.findById(appuser.getId());
				Org org = orgService.findOne(entity.data.getOrgid_link());
				appuserold.setOrg_type(org.getOrgtypeid_link());
				appuserold.setRootorgid_link(org.getOrgrootid_link());
				appuserold.setLastname(appuser.getLastname());
				appuserold.setMiddlename(appuser.getMiddlename());
				appuserold.setFirstname(appuser.getFirstname());
				appuserold.setStatus(appuser.getStatus());
				
				userDetailsService.save(appuserold);
				List<UserMenu> listmenu = userMenuService.findByUserid(appuser.getId());
				if(listmenu!=null) {
					for (UserMenu userMenu : listmenu) {
						userMenuService.delete(userMenu);
					}
				}
				
				for(MenuId entry : entity.usermenu) {
					UserMenu userMenu = new UserMenu();
					userMenu.setMenuid(entry.id);
					userMenu.setUserid(appuser.getId());
					userMenuService.save(userMenu);
				}
				
			}
			else {
				Org org = orgService.findOne(entity.data.getOrgid_link());
				appuser.setPassword(passwordEncoder.encode(appuser.getPassword()));
				appuser.setOrg_type(org.getOrgtypeid_link());
				appuser.setRootorgid_link(org.getOrgrootid_link());
				List<GpayRole> roles = new ArrayList<GpayRole>();
				appuser.setRoles(roles);
				GpayUser appusernew = userDetailsService.save(appuser);
				
				for(MenuId entry : entity.usermenu) {
					UserMenu userMenu = new UserMenu();
					userMenu.setMenuid(entry.id);
					userMenu.setUserid(appusernew.getId());
					userMenuService.save(userMenu);
				}
			}
			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_EXCEPTION));
		    return new ResponseEntity<ResponseBase>(response,HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/user_list",method = RequestMethod.POST)
	public ResponseEntity<UserResponse> GetUserList( @RequestBody UserRequest entity,HttpServletRequest request ) {
		UserResponse response = new UserResponse();
		try {
			GpayAuthentication user = (GpayAuthentication)SecurityContextHolder.getContext().getAuthentication();
			//GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication()
			//		.getPrincipal();
			long orgid_link = user.getOrgId();
			response.data=userDetailsService.getUserList(orgid_link,entity.textsearch, entity.status);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<UserResponse>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<UserResponse>(response,HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/updatepass",method = RequestMethod.POST)
	public ResponseEntity<update_pass_response> UpdatePass( @RequestBody update_pass_request entity,HttpServletRequest request ) {
		update_pass_response response = new update_pass_response();
		try {
			String result = "";
			String line;
			GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String urlPush = AtributeFixValues.url_authen+"/o2admin/changepass";
			String token = request.getHeader("Authorization");
						
			URL url = new URL(urlPush);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("authorization", token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");
            
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode appParNode = objectMapper.createObjectNode();
            appParNode.put("username", user.getUsername());
            appParNode.put("oldpwd", entity.old_pass);
            appParNode.put("newpwd", entity.new_pass);
            String jsonReq = objectMapper.writeValueAsString(appParNode);
            
            OutputStream os = conn.getOutputStream();
            os.write(jsonReq.getBytes());
            os.flush();
                     
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
            
            conn.disconnect();
            
            JsonParser springParser = JsonParserFactory.getJsonParser();
            Map<String, Object> map = springParser.parseMap(result);
            
            String status = map.get("status").toString();
            if(!status.equals("0")) {
            	response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
    			response.setMessage(map.get("msg").toString());
            }
            else {
            	response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
    			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
            }
			
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    
		}
		return new ResponseEntity<update_pass_response>(response,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getbyorg",method = RequestMethod.POST)
	public ResponseEntity<UserResponse> GetByOrg( @RequestBody user_getbyorg_request entity,HttpServletRequest request ) {
		UserResponse response = new UserResponse();
		try {
			long orgid_link = entity.orgid_link;
			
			if(orgid_link == -1) {
				GpayAuthentication user = (GpayAuthentication)SecurityContextHolder.getContext().getAuthentication();
				orgid_link = user.getOrgId();
			}
			
			response.data=userDetailsService.getUserList(orgid_link,"", 1);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<UserResponse>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<UserResponse>(response,HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/getby_org_buyer",method = RequestMethod.POST)
	public ResponseEntity<getby_org_pcontract_response> GetByOrg( @RequestBody getby_org_pcontract_request entity,HttpServletRequest request ) {
		getby_org_pcontract_response response = new getby_org_pcontract_response();
		try {
			GpayAuthentication user = (GpayAuthentication)SecurityContextHolder.getContext().getAuthentication();
			long orgrootid_link = user.getRootorgid_link();
			long orgid_link = entity.orgid_link;
			long orgbuyerid_link = entity.orgbuyerid_link;
			
			response.data=userDetailsService.getUserList(orgid_link,"", 1);
			
			for(GpayUser _user : response.data) {
				long merchandiserid_link = _user.getId();
				_user.index = pcontractService.getby_buyer_merchandiser(orgrootid_link, orgbuyerid_link, merchandiserid_link);
			}
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<getby_org_pcontract_response>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<getby_org_pcontract_response>(response,HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/getby_org_buyer_multi",method = RequestMethod.POST)
	public ResponseEntity<getby_org_pcontract_response> getby_org_buyer_multi( @RequestBody getby_org_pcontract_request entity,HttpServletRequest request ) {
		getby_org_pcontract_response response = new getby_org_pcontract_response();
		try {
//			GpayAuthentication user = (GpayAuthentication)SecurityContextHolder.getContext().getAuthentication();
//			long orgrootid_link = user.getRootorgid_link();
//			long orgid_link = entity.orgid_link;
//			long orgbuyerid_link = entity.orgbuyerid_link;
			List<Long> orgid_link_arr = entity.orgid_link_arr;
			response.data = new ArrayList<GpayUser>();
			for(Long orgid_link : orgid_link_arr) {
				List<GpayUser> user_list = userDetailsService.getUserList(orgid_link,"", 1);
				response.data.addAll(0, user_list);
			}
			
//			response.data=userDetailsService.getUserList(orgid_link,"", 1);
			
//			for(GpayUser _user : response.data) {
//				long merchandiserid_link = _user.getId();
//				_user.index = pcontractService.getby_buyer_merchandiser(orgrootid_link, orgbuyerid_link, merchandiserid_link);
//			}
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<getby_org_pcontract_response>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<getby_org_pcontract_response>(response,HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/user_delete",method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> Delete( @RequestBody UserByIdRequest entity,HttpServletRequest request ) {
		ResponseBase response = new ResponseBase();
		try {
			userDetailsService.deleteById(entity.userid);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_EXCEPTION));
		    return new ResponseEntity<ResponseBase>(response,HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/user_getbyid",method = RequestMethod.POST)
	public ResponseEntity<UserByIdResponse> GetByID( @RequestBody UserByIdRequest entity,HttpServletRequest request ) {
		UserByIdResponse response = new UserByIdResponse();
		try {
			response.data=userDetailsService.findById(entity.userid);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<UserByIdResponse>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_EXCEPTION));
		    return new ResponseEntity<UserByIdResponse>(response,HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/user_list_bypage",method = RequestMethod.POST)
	public ResponseEntity<UserResponse> GetUserList_Page( @RequestBody User_getList_byPage_request entity,HttpServletRequest request ) {
		UserResponse response = new UserResponse();
		try {
//			GpayAuthentication user = (GpayAuthentication)SecurityContextHolder.getContext().getAuthentication();
			//GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication()
			//		.getPrincipal();
			String firstname = entity.firstname == null ? "" : entity.firstname.trim();
			String middlename = entity.middlename == null ? "" : entity.middlename.trim();
			String lastname = entity.lastname == null ? "" : entity.lastname.trim();
			String username = entity.username == null ? "" : entity.username.trim();
			Long groupuserid_link = entity.groupuserid_link == (long)0 ? null : entity.groupuserid_link;
			
			
//			response.data=userDetailsService.getUserList_page( firstname, middlename, lastname, username, groupuserid_link);
			response.data=userDetailsService.getUserList_search(firstname, middlename, lastname, username, groupuserid_link);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<UserResponse>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<UserResponse>(response,HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/user_getinfo",method = RequestMethod.POST)
	public ResponseEntity<UserByIdResponse> GetByID(HttpServletRequest request, @RequestBody User_getinfo_request entity ) {
		UserByIdResponse response = new UserByIdResponse();
		try {
			GpayAuthentication user = (GpayAuthentication)SecurityContextHolder.getContext().getAuthentication();
			
			if(entity.id == null || entity.id == 0) {
				response.data=userDetailsService.findById(user.getUserId());
			}
			else
				response.data=userDetailsService.findById(entity.id);
			
			// TODO: kiem tra quyen truoc khi tra len
			
			response.data.setPassword("");
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<UserByIdResponse>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_EXCEPTION));
		    return new ResponseEntity<UserByIdResponse>(response,HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/user_update",method = RequestMethod.POST)
	public ResponseEntity<UserResponse> UserUpdatet( @RequestBody User_update_request entity,HttpServletRequest request ) {
		UserResponse response = new UserResponse();
		try {
			
			GpayUser appuser = userDetailsService.findOne(entity.user.getId());
			
		
			//TH mã trường nhân viên ngoài view có dữ liệu
			String personel_code = entity.user.getPersonnel_code();
			if(personel_code != null ) {
				//kiểm tra mã nhân viên đã bị trùng trong bảng app_user không
				//danh sách user theo mã nhân viên và không chứa id truyền vào
				List<GpayUser> lstuser = userDetailsService.getUserBycode_Personel(personel_code, entity.user.getId());
				//nếu có phần tử trong danh sách thì không được thêm return luôn
				if(lstuser.size() != 0) {
					response.setMessage("Mã nhân viên đã trùng!");
					return new ResponseEntity<UserResponse>(response,HttpStatus.OK);
				}
				//kiểm tra mã nhân viên có tồn tại trong bảng personnel không
				// nhân viên theo mã nhân viên truyền vào
				Personel personel = personnelService.getPersonelBycode(personel_code);
				//nếu trống thì -> mã nhân viên không tồn tại return
				if (personel == null) {
					response.setMessage("Mã nhân viên không tồn tại!");
					return new ResponseEntity<UserResponse>(response,HttpStatus.OK);
				}
			}
			
			
			appuser.setEmail(entity.user.getEmail());
			appuser.setFirstname(entity.user.getFirstname());
			appuser.setMiddlename(entity.user.getMiddlename());
			appuser.setLastname(entity.user.getLastname());
			appuser.setOrgid_link(entity.user.getOrgid_link());
			appuser.setTel_mobile(entity.user.getTel_mobile());
			appuser.setTel_office(entity.user.getTel_office());
			appuser.setStatus(entity.user.getStatus());
			appuser.setOrg_grant_id_link(entity.user.getOrg_grant_id_link());
			appuser.setPersonnel_code(personel_code);
			//update lai Orgtype cua User
			Org userorg = orgService.findOne(null!=entity.user.getOrg_grant_id_link()?entity.user.getOrg_grant_id_link():entity.user.getOrgid_link());
			if (null != userorg){
				appuser.setOrg_type(userorg.getOrgtypeid_link());
			}
			
			userDetailsService.save(appuser);
			
			//Cap nhat sang authen ve nhom quyen cua user co phai admin nua khong
			String result = "";
			String line;
			String urlPush = AtributeFixValues.url_authen+"/o2admin/enabled";
			String token = request.getHeader("Authorization");
						
			URL url = new URL(urlPush);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("authorization", token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");
            
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode appParNode = objectMapper.createObjectNode();
            boolean enabled = false;
            if(entity.user.getStatus() == 1) {
            	enabled = true;
            }
            appParNode.put("userid", entity.user.getId());
            appParNode.put("enable", enabled);
            String jsonReq = objectMapper.writeValueAsString(appParNode);
            
            OutputStream os = conn.getOutputStream();
            os.write(jsonReq.getBytes());
            os.flush();
                     
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
            
            conn.disconnect();
            
            JsonParser springParser = JsonParserFactory.getJsonParser();
            Map<String, Object> map = springParser.parseMap(result);
            
            String status = map.get("status").toString();
            if(!status.equals("0")) {
            	response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
    			response.setMessage(map.get("msg").toString());
            }
            else {
            	response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
    			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
            }
		}
		catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}
	    return new ResponseEntity<UserResponse>(response,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/user_updaterole",method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> UpdateRole(HttpServletRequest request, @RequestBody User_updateRole_request entity ) {
		ResponseBase response = new ResponseBase();
		try {
			long roleid_link = entity.roleid_link;
			long userid = entity.userid;
			//checked = true : thêm mới , false: xóa
			if(entity.checked) {
				//Lưu nhóm quyền
				AppRole_User appuser = new AppRole_User();
				appuser.setId(null);
				appuser.setRole_id(roleid_link);
				appuser.setUser_id(userid);
				appuserService.save(appuser);
				
				//Lưu menu
				List<Menu> listmenu = menuService.getMenu_byRole(roleid_link);
				for(Menu menu : listmenu) {
					String menuid_link = menu.getId();
					AppUserMenu usermenu = new AppUserMenu();
					usermenu.setId(null);
					usermenu.setMenuid(menuid_link);
					usermenu.setUserid(userid);
					usermenuService.save(usermenu);
					
					//Lưu function
					List<AppFunction> list_app_role_func = appfuncService.getAppFunction_inmenu(menuid_link, roleid_link);
					for(AppFunction appf : list_app_role_func) {
						long functionid_link = appf.getId();
						AppUserFunction appuserf = new AppUserFunction();
						appuserf.setFunctionid_link(functionid_link);
						appuserf.setId(null);
						appuserf.setIshidden(false);
						appuserf.setIsreadonly(false);
						appuserf.setUserid_link(userid);
						
						appuserfService.save(appuserf);
					}
				}
				
				
			}
			else {
				//Xóa nhóm quyền
				AppRole_User appuser = appuserService.getby_user_and_role(userid, roleid_link).get(0);
				appuserService.delete(appuser);
				
				//Xóa menu
				List<Menu> listmenu = menuService.getMenu_byRole(roleid_link);
				for(Menu menu: listmenu) {
					String menuid_link = menu.getId();
					AppUserMenu usermenu = usermenuService.getuser_menu_by_menuid_and_userid(menuid_link, userid).get(0);
					usermenuService.delete(usermenu);
					
					List<AppFunction> list_app_role_func = appfuncService.getAppFunction_inmenu(menuid_link, roleid_link);
					for(AppFunction appf : list_app_role_func) {
						long functionid_link = appf.getId();
						AppUserFunction appuserf = appuserfService.getby_function_and_user(functionid_link, userid).get(0);
						appuserfService.delete(appuserf);
					}
				}
				
			}
			
			//Cap nhat sang authen ve nhom quyen cua user co phai admin nua khong
			String result = "";
			String line;
			String urlPush = AtributeFixValues.url_authen+"/o2admin/update_useradmin";
			String token = request.getHeader("Authorization");
						
			URL url = new URL(urlPush);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("authorization", token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");
            
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode appParNode = objectMapper.createObjectNode();
            boolean isadmin = false;
            if(AtributeFixValues.role_id_admin == roleid_link && entity.checked) {
            	isadmin = true;
            }
            appParNode.put("isadmin", isadmin);
            appParNode.put("userid", userid);
            String jsonReq = objectMapper.writeValueAsString(appParNode);
            
            OutputStream os = conn.getOutputStream();
            os.write(jsonReq.getBytes());
            os.flush();
                     
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
            
            conn.disconnect();
            
            JsonParser springParser = JsonParserFactory.getJsonParser();
            Map<String, Object> map = springParser.parseMap(result);
            
            String status = map.get("status").toString();
            if(!status.equals("0")) {
            	response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
    			response.setMessage(map.get("msg").toString());
            }
            else {
            	response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
    			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
            }
			return new ResponseEntity<ResponseBase>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<ResponseBase>(response,HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/user_create_fromauthen",method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> Create_FromAuthen( @RequestBody user_create_user_fromauthen_request entity,HttpServletRequest request ) {
		ResponseBase response = new ResponseBase();
		try {
			GpayUser user = new GpayUser();			
			
			if(!entity.enable) {
				user.setOrgid_link((long)0);
				user.setId((long)entity.id);
				user.setFirstname(entity.firstname);
				user.setMiddlename(entity.middlename);
				user.setLastname(entity.lastname);
				user.setFullname(entity.fullname);
				user.setEmail(entity.email);
				user.setUsername(entity.username);
				user.setRootorgid_link((long)entity.orgrootid);
				user.setStatus(1);		
				user.setEnabled(true);
				user.setUserrole("ROLE_USER");
				user.setOrg_type(1);
				user.setRootorgid_link(user.getRootorgid_link());

				user = userDetailsService.save(user);
			}
			
			
			if(entity.isrootadmin) {
				AppRole_User role = new AppRole_User();
				role.setId(null);
				role.setRole_id(AtributeFixValues.role_id_admin);
				role.setUser_id((long)entity.id);
				roleuserService.save(role);
			}
			else {
				//Xoa quyen cua user
				List<AppRole_User> listrole = roleuserService.getby_user_and_role((long)entity.id, (long)0);
				for(AppRole_User role : listrole) {
					roleuserService.delete(role);
				}
			}
			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_EXCEPTION));
		    return new ResponseEntity<ResponseBase>(response,HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/user_orgview_getall",method = RequestMethod.POST)
	public ResponseEntity<User_OrgView_Response> user_orgview_getall(HttpServletRequest request, @RequestBody User_getinfo_request entity) {
		User_OrgView_Response response = new User_OrgView_Response();
		try {
//			response.data=userOrgService.getall_byuser_andtype(entity.id, entity.orgtypeid_link);
			response.data=userOrgService.getall_byuser_andtypelist(entity.id, entity.orgtypeid_link_list);
			
			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<User_OrgView_Response>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_EXCEPTION));
		    return new ResponseEntity<User_OrgView_Response>(response,HttpStatus.BAD_REQUEST);
		}
	}
	@RequestMapping(value = "/user_orgview_add",method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> user_orgview_add(HttpServletRequest request, @RequestBody User_OrgView_Add_Request entity) {
		User_OrgView_Response response = new User_OrgView_Response();
		try {
			for (Long orgid_link:entity.listId){
				//Neu chua ton tai --> Inserrt
				if (userOrgService.getby_user_org(entity.userid_link, orgid_link).size() == 0){
					GpayUserOrg theGpayUserOrg =  new GpayUserOrg();
					theGpayUserOrg.setOrgid_link(orgid_link);
					theGpayUserOrg.setUserid_link(entity.userid_link);
					userOrgService.save(theGpayUserOrg);
				}
			}

			
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_EXCEPTION));
		    return new ResponseEntity<ResponseBase>(response,HttpStatus.BAD_REQUEST);
		}
	}
	@RequestMapping(value = "/user_orgview_delete",method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> user_orgview_delete(HttpServletRequest request, @RequestBody User_getinfo_request entity) {
		User_OrgView_Response response = new User_OrgView_Response();
		try {
			GpayUserOrg theGpayUserOrg =  userOrgService.findOne(entity.id);
			if (null != theGpayUserOrg){
				userOrgService.delete(theGpayUserOrg);
			
				response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
				response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
				return new ResponseEntity<ResponseBase>(response,HttpStatus.OK);
			} else {
				response.setRespcode(ResponseMessage.KEY_RC_KEY_NOTEXIST);
				response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_KEY_NOTEXIST));
			    return new ResponseEntity<ResponseBase>(response,HttpStatus.BAD_REQUEST);
			}
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_EXCEPTION));
		    return new ResponseEntity<ResponseBase>(response,HttpStatus.BAD_REQUEST);
		}
	}
	@RequestMapping(value = "/user_getuserinorg",method = RequestMethod.POST)
	public ResponseEntity<UserResponse> GetUserinOrgId(HttpServletRequest request ) {
		UserResponse response = new UserResponse();
		try {
			GpayAuthentication user = (GpayAuthentication)SecurityContextHolder.getContext().getAuthentication();
			Long orgid_link = user.getOrgId();
			
			List<Org> listorg = orgService.findOrgAllByRoot(user.getRootorgid_link(), orgid_link, new ArrayList<String>(), true);
						
			response.data=userDetailsService.getUserinOrgid(listorg);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<UserResponse>(response,HttpStatus.OK);
		}catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		    return new ResponseEntity<UserResponse>(response,HttpStatus.OK);
		}
	}
}
