package vn.gpay.gsmart.core.api.menu;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import vn.gpay.gsmart.core.approle.IAppRoleMenuService;
import vn.gpay.gsmart.core.base.ResponseError;
import vn.gpay.gsmart.core.menu.IMenuService;
import vn.gpay.gsmart.core.menu.Menu;
import vn.gpay.gsmart.core.menu.MenuTree;
import vn.gpay.gsmart.core.security.GpayAuthentication;
import vn.gpay.gsmart.core.security.GpayUser;
import vn.gpay.gsmart.core.security.GpayUserOrg;
import vn.gpay.gsmart.core.security.IGpayUserOrgService;
import vn.gpay.gsmart.core.security.IGpayUserService;
import vn.gpay.gsmart.core.utils.ResponseMessage;

@RestController
@RequestMapping("/api/v1/menu")
public class MenuAPI {

	@Autowired IMenuService menuService;
	@Autowired IAppRoleMenuService rolemenuService;
	@Autowired IGpayUserService  userDetailsService ;
	@Autowired IGpayUserOrgService userOrgService;
	
	@RequestMapping(value = "/menu_data",method = RequestMethod.POST)
	//@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String MenuLeft(HttpServletRequest request) {
		try {
			//Resource resource = new ClassPathResource("menudata.json");
			File file = ResourceUtils.getFile("classpath:menudata.json");
			String content = new String(Files.readAllBytes(file.toPath()));
			return content;
		} catch (Exception e) {
			//ResponseError errorBase = new ResponseError();
			//errorBase.setErrorcode(ResponseError.ERRCODE_RUNTIME_EXCEPTION);
			//errorBase.setMessage(e.getMessage());
		    return "{\"errorcode\":9000}";
		
		}
	}
	@RequestMapping(value = "/menu_list",method = RequestMethod.POST)
	public ResponseEntity<?> MenuList(HttpServletRequest request ) {
		try {
			GpayAuthentication user = (GpayAuthentication)SecurityContextHolder.getContext().getAuthentication();
			List<Menu> menu = menuService.findByUserid(user.getUserId());
			return new ResponseEntity<List<Menu>>(menu,HttpStatus.OK);
		}catch (RuntimeException e) {
			ResponseError errorBase = new ResponseError();
			errorBase.setErrorcode(ResponseError.ERRCODE_RUNTIME_EXCEPTION);
			errorBase.setMessage(e.getMessage());
		    return new ResponseEntity<>(errorBase, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/menu_tree",method = RequestMethod.POST)
	public ResponseEntity<?> MenuTree(HttpServletRequest request ) {
		try {
			MenuTreeResponse response = new MenuTreeResponse();
			GpayUser user = (GpayUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			List<Menu> menu = menuService.findByUserid(user.getId());
			List<MenuTree> children = menuService.createTree(menu);
			response.children=children;
			
			response.data=userDetailsService.findById(user.getId());
			response.data.setPassword("");
			
			//Lay danh sach org quan ly cua user
			List<String> orgs = new ArrayList<String>();
			Long orgid_link = user.getOrgid_link();
			String orgcode = user.getOrgcode();
			
			if(orgid_link != 0 && orgid_link != 1 && orgid_link != null) {
				for(GpayUserOrg userorg:userOrgService.getall_byuser(user.getId())){
					orgs.add(userorg.getOrgcode());
				}
				//Them chinh don vi cua user
				orgs.add(orgcode);
			}
			
			for(String code : orgs ) {
				response.listorg += code+";";
			}
			
			return new ResponseEntity<MenuTreeResponse>(response,HttpStatus.OK);
		}catch (RuntimeException e) {
			ResponseError errorBase = new ResponseError();
			errorBase.setErrorcode(ResponseError.ERRCODE_RUNTIME_EXCEPTION);
			errorBase.setMessage(e.getMessage());
		    return new ResponseEntity<>(errorBase, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/usertree_getbyid",method = RequestMethod.POST)
	public ResponseEntity<?> UserTreeGetbyid(@RequestBody UserTreeGetByIDRequest entity,HttpServletRequest request ) {
		try {
			MenuTreeResponse response = new MenuTreeResponse();
			GpayAuthentication user = (GpayAuthentication)SecurityContextHolder.getContext().getAuthentication();
			long userid = entity.userid;
			if(0==userid) {
				userid=user.getUserId();
				List<Menu> menu = menuService.findByUserid(userid);
				List<MenuTree> children = menuService.createTree(menu);
				response.children=children;
				return new ResponseEntity<MenuTreeResponse>(response,HttpStatus.OK);
			}else {
				List<Menu> menu = menuService.findByUserid(userid);
				List<Menu> menulogin = menuService.findByUserid(user.getUserId());
				for (Menu entry :menulogin) {
					for (Menu entry1 : menu) {
						if(entry.getId().equals(entry1.getId())) {
							entry.setChecked(true);
						}
					}
				}
				List<MenuTree> children = menuService.createTree(menulogin);
				response.children=children;
				return new ResponseEntity<MenuTreeResponse>(response,HttpStatus.OK);
			}
			
		}catch (RuntimeException e) {
			ResponseError errorBase = new ResponseError();
			errorBase.setErrorcode(ResponseError.ERRCODE_RUNTIME_EXCEPTION);
			errorBase.setMessage(e.getMessage());
		    return new ResponseEntity<>(errorBase, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/menu_tree_getbyrole",method = RequestMethod.POST)
	public ResponseEntity<?> MenuTree_getAll(HttpServletRequest request, @RequestBody Menu_getbyrole_request entity ) {
		try {
			MenuTreeResponse response = new MenuTreeResponse();
			long roleid_link = entity.roleid_link;
			
			List<Menu> menu = menuService.findAll();
			List<String> listmenuid = rolemenuService.getmenuid_byrole(roleid_link);
			
			for(Menu m : menu) {
				String id = m.getId();
				if(listmenuid.contains(id))
					m.setChecked(true);
			}
			
			List<MenuTree> children = menuService.createTree(menu);
			response.children=children;
			return new ResponseEntity<MenuTreeResponse>(response,HttpStatus.OK);
		}catch (RuntimeException e) {
			ResponseError errorBase = new ResponseError();
			errorBase.setErrorcode(ResponseError.ERRCODE_RUNTIME_EXCEPTION);
			errorBase.setMessage(e.getMessage());
		    return new ResponseEntity<>(errorBase, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/menu_tree_inrole",method = RequestMethod.POST)
	public ResponseEntity<?> MenuTree_getinrole(HttpServletRequest request, @RequestBody Menu_getbyrole_request entity ) {
		try {
			MenuTreeResponse response = new MenuTreeResponse();
			long roleid_link = entity.roleid_link;
			
			List<Menu> listmenu = menuService.getMenu_byRole(roleid_link);		
			for(Menu m : listmenu) {
					m.setChecked(true);
			}
			
			List<MenuTree> children = menuService.createTree(listmenu);
			response.children=children;
			return new ResponseEntity<MenuTreeResponse>(response,HttpStatus.OK);
		}catch (RuntimeException e) {
			ResponseError errorBase = new ResponseError();
			errorBase.setErrorcode(ResponseError.ERRCODE_RUNTIME_EXCEPTION);
			errorBase.setMessage(e.getMessage());
		    return new ResponseEntity<>(errorBase, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/menu_mobile",method = RequestMethod.POST)
	public ResponseEntity<?> MenuMobile(HttpServletRequest request ) {
		try {
			MenuResponse response = new MenuResponse();
			GpayUser user = (GpayUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			List<Menu> menu = menuService.findByUserid(user.getId());
			
			List<Menu> result = new ArrayList<Menu>();
			
			for(Menu item : menu) {
				// bỏ trang chủ
				if(item.getId().equals("dashboard")) {
					continue;
				}
				// thêm nếu ko có con
				if(item.getParent_id() == null || item.getParent_id().equals("")) {
					List<Menu> list = menuService.getby_parentid(item.getId());
					if(list.size() == 0) {
						result.add(item);
					}
				}
				// thêm nếu có cha
				else {
					result.add(item);
				}
			}
			
			response.data = result;
			
			return new ResponseEntity<MenuResponse>(response,HttpStatus.OK);
		}catch (RuntimeException e) {
			ResponseError errorBase = new ResponseError();
			errorBase.setErrorcode(ResponseError.ERRCODE_RUNTIME_EXCEPTION);
			errorBase.setMessage(e.getMessage());
		    return new ResponseEntity<>(errorBase, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	/**
	 * add
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseEntity<Menu_create_response> Menu_add(@RequestBody Menu_create_request entity,
			HttpServletRequest request) {
		Menu_create_response response = new Menu_create_response();
		try {
			
			menuService.save(entity.data);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));

		} catch (RuntimeException e) {
			response.setRespcode(ResponseError.ERRCODE_RUNTIME_EXCEPTION);
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<Menu_create_response>(response, HttpStatus.OK);
	}
	
}
