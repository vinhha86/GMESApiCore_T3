package vn.gpay.gsmart.core.api.Upload;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.gpay.gsmart.core.base.ResponseBase;
import vn.gpay.gsmart.core.org.IOrgService;
import vn.gpay.gsmart.core.org.IOrgTypeService;
import vn.gpay.gsmart.core.org.Org;
import vn.gpay.gsmart.core.org.OrgType;
import vn.gpay.gsmart.core.personel.IPersonnel_Service;
import vn.gpay.gsmart.core.personel.Personel;
import vn.gpay.gsmart.core.personnel_history.IPersonnel_His_Service;
import vn.gpay.gsmart.core.personnel_history.Personnel_His;
import vn.gpay.gsmart.core.personnel_position.IPersonnel_Position_Service;
import vn.gpay.gsmart.core.personnel_position.Personnel_Position;
import vn.gpay.gsmart.core.personnel_type.IPersonnelType_Service;
import vn.gpay.gsmart.core.personnel_type.PersonnelType;
import vn.gpay.gsmart.core.security.GpayUser;
import vn.gpay.gsmart.core.utils.*;


@RestController
@RequestMapping("/api/v1/upload_personnel")
public class UploadPersonnelAPI {
	@Autowired
	Common commonService;
	@Autowired
	IPersonnel_Service personnel_service;
	@Autowired
	IOrgService org_service;
	@Autowired
	IOrgTypeService org_type_service;
	@Autowired
	IPersonnel_His_Service personnel_his_service;
	@Autowired
	IPersonnel_Position_Service personnel_position_service;
	@Autowired
	IPersonnelType_Service personneltypeService;
	@Autowired
	IOrgTypeService orgtype_service;
	@RequestMapping(value = "/personnel", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> UploadPersonnel(HttpServletRequest request,
														@RequestParam("file") MultipartFile file, @RequestParam("orgmanageid_link") long orgmanageid_link) {
		ResponseBase response = new ResponseBase();
		Date current_time = new Date();
		String name = "";
		String mes_err = "";
		Personel person = new Personel();
		GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long orgrootid_link = user.getRootorgid_link();
		try {
			String FolderPath = "upload/personnel";
			String uploadRootPath = request.getServletContext().getRealPath(FolderPath);
			File uploadRootDir = new File(uploadRootPath);
			if (!uploadRootDir.exists()) {
				uploadRootDir.mkdirs();
			}
			name = file.getOriginalFilename();

			if (name != null && name.length() > 0) {
				String[] str = name.split("\\.");
				String extend = str[str.length - 1];
				name = current_time.getTime() + "." + extend;
				File serverFile = new File(uploadRootDir.getAbsolutePath() + File.separator + name);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();

				// doc file upload
				XSSFWorkbook workbook = new XSSFWorkbook(serverFile);
				XSSFSheet sheet = workbook.getSheetAt(0);

				int rowNum = 1;
				// int colNum = 1;

				Row row = sheet.getRow(rowNum);

				try {
					String MaSoMoi = "";
					MaSoMoi = commonService.getStringValue(row.getCell(ColumnPersonnel.MaSoMoi)).trim();
					MaSoMoi = MaSoMoi.equals("0") ? "" : MaSoMoi;
					while (!MaSoMoi.equals("")) {


						// tim nhan vien theo ma so moi v?? ????n v??? ??ang ???????c ch???n
						person = personnel_service.getPersonelBycode_orgmanageid_link(MaSoMoi, orgmanageid_link);
						// neu khong co nhan vien. thi tao nhan vien moi theo ma so moi
						if (person == null) {
							person = new Personel();
							person.setCode(MaSoMoi);
						}


						String ThoiVu = commonService.getStringValue(row.getCell(ColumnPersonnel.ThoiVu)).trim();
						Long LoaiNV = null;
						String ssThoiVu = ThoiVu.trim().toLowerCase();
						if(ssThoiVu.equals("c")  || ssThoiVu.equals("c??")){
							//l???y id c???a lo???i nh??n vi??n
							List<PersonnelType> lst_type=personneltypeService.getByName("Th???i v???");
							LoaiNV=lst_type.get(0).getId();

						}else {
							List<PersonnelType> lst_type=personneltypeService.getByName("H???p ?????ng");
							LoaiNV=lst_type.get(0).getId();
						}

						String HoVaTen = commonService.getStringValue(row.getCell(ColumnPersonnel.HoVaTen)).trim();
						String gioiTinh = commonService.getStringValue(row.getCell(ColumnPersonnel.GT)).trim();
						int GioiTinh;
						if (gioiTinh.equals("Nam")) {
							GioiTinh = 1;
						} else {
							GioiTinh = 0;
						}
						// ngay sinh
						Date NgaySinh = null;
						try {
							String ngaySinh = commonService.getStringValue(row.getCell(ColumnPersonnel.NgaySinh)).trim();
							if (ngaySinh.contains("/")) {
								String[] s_date = ngaySinh.split("/");
								if (Integer.parseInt(s_date[1].toString()) < 13
										&& Integer.parseInt(s_date[0].toString()) < 32
										&& Integer.parseInt(s_date[1].toString()) > 0
										&& Integer.parseInt(s_date[0].toString()) > 0) {
									NgaySinh = new SimpleDateFormat("dd/MM/yyyy").parse(ngaySinh);
								} else {
									mes_err = " ?????nh d???ng ng??y sinh kh??ng ????ng dd/MM/yyyy! ??? d??ng  : " + (rowNum+1);
									break;
								}
							} else if (ngaySinh != "") {
								if (DateUtil.isCellDateFormatted(row.getCell(ColumnPersonnel.NgaySinh))) {
									NgaySinh = row.getCell(ColumnPersonnel.NgaySinh).getDateCellValue();
								}
							}
						} catch (Exception e) {
							if (DateUtil.isCellDateFormatted(row.getCell(ColumnPersonnel.NgaySinh))) {
								NgaySinh = row.getCell(ColumnPersonnel.NgaySinh).getDateCellValue();
							}
						}

						String BoPhan = commonService.getStringValue(row.getCell(ColumnPersonnel.BoPhan)).trim();

						String ChucVu = commonService.getStringValue(row.getCell(ColumnPersonnel.ChucVu)).trim();
						String ChucVuBH = commonService.getStringValue(row.getCell(ColumnPersonnel.ChucVutrongBH)).trim();
						// kiem tra chu vu trong DB, neu chua co thi them chuc vu vao DB
						List<Personnel_Position> lst_personnel_Position = personnel_position_service.getByName_Code(ChucVuBH,ChucVu);
						Long positionid_link;
						if (lst_personnel_Position.size() != 0) {
							positionid_link = lst_personnel_Position.get(0).getId();
						} else {
							Personnel_Position personnel_Position = new Personnel_Position();
							personnel_Position.setCode(ChucVu);
							personnel_Position.setName(ChucVuBH);
							Personnel_Position per_positionid = personnel_position_service.save(personnel_Position);
							positionid_link = per_positionid.getId();
						}
						// ngay ki hop dong co thoi han
						Date NgayKiHDCTH = null;
						try {
							String ngayKiHDCTH = commonService.getStringValue(row.getCell(ColumnPersonnel.NgayKiHDCTH)).trim();
							if (ngayKiHDCTH.contains("/")) {
								String[] s_date = ngayKiHDCTH.split("/");
								if (Integer.parseInt(s_date[1].toString()) < 13
										&& Integer.parseInt(s_date[0].toString()) < 32) {
									NgayKiHDCTH = new SimpleDateFormat("dd/MM/yyyy").parse(ngayKiHDCTH);
								} else {
									mes_err = " ?????nh d???ng ng??y k?? HDCTH kh??ng ????ng dd/MM/yyyy! " + " ??? d??ng " + (rowNum+1);
									break;
								}

							} else if (ngayKiHDCTH != "") {
								if (DateUtil.isCellDateFormatted(row.getCell(ColumnPersonnel.NgayKiHDCTH))) {
									NgayKiHDCTH = row.getCell(ColumnPersonnel.NgayKiHDCTH).getDateCellValue();
								}
							}

						} catch (Exception e) {
							if (DateUtil.isCellDateFormatted(row.getCell(ColumnPersonnel.NgayKiHDCTH))) {
								NgayKiHDCTH = row.getCell(ColumnPersonnel.NgayKiHDCTH).getDateCellValue();
							}
						}

//						String Bac = commonService.getStringValue(row.getCell(ColumnPersonnel.Bac));
//						String MaBacLuongBH = commonService.getStringValue(row.getCell(ColumnPersonnel.MaBacluongBH));
//						String HeSoLuong = commonService.getStringValue(row.getCell(ColumnPersonnel.HeSoLuongBH));
//						String MucLuongBH = commonService.getStringValue(row.getCell(ColumnPersonnel.MucLuongDongBH));
//						String HeSoCV = commonService.getStringValue(row.getCell(ColumnPersonnel.HeSoCV));
//						String ThangGiamBH = commonService.getStringValue(row.getCell(ColumnPersonnel.ThangGiamBH));

						// kiem tra bo phan co trong DB khong
						// lay bo phan theo ma, don vi quan ly - code,parentid_link
						Long parentid_link = (long) orgmanageid_link;
						Long orgid_link = null;
						List<Org> lst_bp = org_service.getByCodeAndParentid_link(BoPhan, parentid_link);

						if (person.getId() != null) {
							// ki???m tra xem ng??y quy???t ?????nh m???i c?? l??n h??n ng??y quy???t ?????nh c?? kh??ng, n???u nh???
							// h??n th?? kh??ng ???????c th??m m???i quy???t ?????nh
							List<Personnel_His> perhis = personnel_his_service.getHis_personByType_Id(person.getId(),
									3);
							// n???u ng??y quy???t ?????nh nh??? h??n ng??y ???? t???n t???i
							if (perhis.size() != 0 && perhis.get(0).getDecision_date()!=null) {
								if (NgayKiHDCTH.compareTo(perhis.get(0).getDecision_date()) < 0) {
									mes_err = " K?? h???p ?????ng c?? th???i h???n m???i kh??ng ???????c nh??? h??n ng??y k?? h???p ?????ng th???i c?? th???i h???n ???? c?? "
											+ " ??? d??ng " + (rowNum+1);
									break;
								}
							}
						}
						//l???y m?? b??? ph???n
						if (lst_bp.size() != 0) {
							// th??m
							//	orgid_link = lst_bp.get(0).getId();
							for(int i =0;i< lst_bp.size();i++) {

								//orgid_link = lst_bp.get(i).getId();
								//ki???m tra xem m?? lo???i ????n v??? c???a ph??ng ban c?? trong DB orgtype ko?
								//1.n???u c?? th?? l???y
								OrgType orgtype = orgtype_service.findOne(lst_bp.get(i).getOrgtypeid_link());
								if(orgtype!=null) {
									orgid_link = lst_bp.get(i).getId();
									break;
								}else {
									continue;
								}
							}

						} else {

							// n???u ch??a c?? th?? th??m b??? ph???n v??o DB
							mes_err = " B??? ph???n kh??ng t???n t???i! " + " ??? d??ng " + (rowNum+1) + " c???t B??? Ph???n ";
							break;
						}

						// ngay vao cong ty
						Date NgayVaoCT = null;
						try {
							String ngayVaoCT = commonService.getStringValue(row.getCell(ColumnPersonnel.NgayVaoCT)).trim();
							if (ngayVaoCT.contains("/")) {
								String[] s_date = ngayVaoCT.split("/");
								if (Integer.parseInt(s_date[1].toString()) < 13
										&& Integer.parseInt(s_date[0].toString()) < 32) {
									NgayVaoCT = new SimpleDateFormat("dd/MM/yyyy").parse(ngayVaoCT);
								} else {
									mes_err = " ?????nh d???ng ng??y v??o CT kh??ng ????ng dd/MM/yyyy! " + " ??? d??ng : "
											+ (rowNum+1);
									break;
								}

							} else if (ngayVaoCT != "") {
								if (DateUtil.isCellDateFormatted(row.getCell(ColumnPersonnel.NgayVaoCT))) {
									NgayVaoCT = row.getCell(ColumnPersonnel.NgayVaoCT).getDateCellValue();
								}
							}

						} catch (Exception e) {
							if (DateUtil.isCellDateFormatted(row.getCell(ColumnPersonnel.NgayVaoCT))) {
								NgayVaoCT = row.getCell(ColumnPersonnel.NgayVaoCT).getDateCellValue();
							}
						}

						// ngay thoi viec
						Date NgayThoiViec = null;
						try {
							String ngayThoiViec = commonService
									.getStringValue(row.getCell(ColumnPersonnel.NgayThoiViec)).trim();
							if (ngayThoiViec.contains("/")) {
								String[] s_date = ngayThoiViec.split("/");
								if (Integer.parseInt(s_date[1].toString()) < 13
										&& Integer.parseInt(s_date[0].toString()) < 32) {
									NgayThoiViec = new SimpleDateFormat("dd/MM/yyyy").parse(ngayThoiViec);
								} else {
									mes_err = " ?????nh d???ng ng??y th??i vi???c kh??ng ????ng dd/MM/yyyy! " + " ??? d??ng " + (rowNum+1);
									break;
								}

							} else if (ngayThoiViec != "") {
								if (DateUtil.isCellDateFormatted(row.getCell(ColumnPersonnel.NgayThoiViec))) {
									NgayThoiViec = row.getCell(ColumnPersonnel.NgayThoiViec).getDateCellValue();
								}
							}

						} catch (Exception e) {
							if (DateUtil.isCellDateFormatted(row.getCell(ColumnPersonnel.NgayThoiViec))) {
								NgayThoiViec = row.getCell(ColumnPersonnel.NgayThoiViec).getDateCellValue();
							}
						}

						//lo???i nh??n vi??n
						String tinhTrang = commonService.getStringValue(row.getCell(ColumnPersonnel.TinhTrang)).trim();
						int TinhTrang =0;
						if(tinhTrang==null) {
							//ngh??? vi???c
							if(NgayThoiViec!=null) {
								TinhTrang = 1;
							}
						}else {
							//ngh??? vi???c
							if (tinhTrang.equals("N")) {
								TinhTrang = 1;
//								rowNum++;
//								row = sheet.getRow(rowNum);
//								MaSoMoi = commonService.getStringValue(row.getCell(ColumnPersonnel.MaSoMoi));
//								MaSoMoi = MaSoMoi.equals("0") ? "" : MaSoMoi;
//								continue;
							}
						}


						// ngay ki hop dong thu viec
						Date NgayKiHDTV = null;
						try {
							String ngayKiHDTV = commonService.getStringValue(row.getCell(ColumnPersonnel.NgayKiHDTV)).trim();
							if (ngayKiHDTV.contains("/")) {
								String[] s_date = ngayKiHDTV.split("/");
								if (Integer.parseInt(s_date[1].toString()) < 13
										&& Integer.parseInt(s_date[0].toString()) < 32) {
									NgayKiHDTV = new SimpleDateFormat("dd/MM/yyyy").parse(ngayKiHDTV);
								} else {
									mes_err = " ?????nh d???ng ng??y k?? HDTV kh??ng ????ng dd/MM/yyyy! " + " ??? d??ng " + (rowNum+1);
									break;
								}

							} else if (ngayKiHDTV != "") {
								if (DateUtil.isCellDateFormatted(row.getCell(ColumnPersonnel.NgayKiHDTV))) {
									NgayKiHDTV = row.getCell(ColumnPersonnel.NgayKiHDTV).getDateCellValue();
								}
							}

						} catch (Exception e) {
							if (DateUtil.isCellDateFormatted(row.getCell(ColumnPersonnel.NgayKiHDTV))) {
								NgayKiHDTV = row.getCell(ColumnPersonnel.NgayKiHDTV).getDateCellValue();
							}
						}

						// ngay ki hop dong vo thoi han
						Date NgayKiHDVTH = null;
						try {
							String ngayKiHDVTH = commonService.getStringValue(row.getCell(ColumnPersonnel.NgayKiHDVTH)).trim();
							if (ngayKiHDVTH.contains("/")) {
								String[] s_date = ngayKiHDVTH.split("/");
								if (Integer.parseInt(s_date[1].toString()) < 13
										&& Integer.parseInt(s_date[0].toString()) < 32) {
									NgayKiHDVTH = new SimpleDateFormat("dd/MM/yyyy").parse(ngayKiHDVTH);
								} else {
									mes_err = " ?????nh d???ng ng??y k?? HDVTH kh??ng ????ng dd/MM/yyyy! " + " ??? d??ng "+ (rowNum+1);
									break;
								}

							} else if (ngayKiHDVTH != "") {
								if (DateUtil.isCellDateFormatted(row.getCell(ColumnPersonnel.NgayKiHDVTH))) {
									NgayKiHDVTH = row.getCell(ColumnPersonnel.NgayKiHDVTH).getDateCellValue();
								}
							}

						} catch (Exception e) {
							if (DateUtil.isCellDateFormatted(row.getCell(ColumnPersonnel.NgayKiHDVTH))) {
								NgayKiHDVTH = row.getCell(ColumnPersonnel.NgayKiHDVTH).getDateCellValue();
							}
						}

						// ngay dong bao hiem
						Date NgayDongBH = null;
						try {
							String ngayDongBH = commonService.getStringValue(row.getCell(ColumnPersonnel.NgayDongBH)).trim();
							if (ngayDongBH.contains("/")) {
								String[] s_date = ngayDongBH.split("/");
								if (Integer.parseInt(s_date[1].toString()) < 13
										&& Integer.parseInt(s_date[0].toString()) < 32) {
									NgayDongBH = new SimpleDateFormat("dd/MM/yyyy").parse(ngayDongBH);
								} else {
									mes_err = " ?????nh d???ng ng??y ????ng BH kh??ng ????ng dd/MM/yyyy! " + " ??? d??ng " + (rowNum+1);
									break;
								}

							} else if (ngayDongBH != "") {
								if (DateUtil.isCellDateFormatted(row.getCell(ColumnPersonnel.NgayDongBH))) {
									NgayDongBH = row.getCell(ColumnPersonnel.NgayDongBH).getDateCellValue();
								}
							}

						} catch (Exception e) {
							if (DateUtil.isCellDateFormatted(row.getCell(ColumnPersonnel.NgayDongBH))) {
								NgayDongBH = row.getCell(ColumnPersonnel.NgayDongBH).getDateCellValue();
							}
						}

						// ly do nghi viec
						String LyDo = commonService.getStringValue(row.getCell(ColumnPersonnel.LyDo)).trim();
						String Thon = commonService.getStringValue(row.getCell(ColumnPersonnel.Thon)).trim();
						String Xa = commonService.getStringValue(row.getCell(ColumnPersonnel.Xa)).trim();
						String Huyen = commonService.getStringValue(row.getCell(ColumnPersonnel.Huyen)).trim();
						String Tinh = commonService.getStringValue(row.getCell(ColumnPersonnel.Tinh)).trim();
						String DiaChi = commonService.getStringValue(row.getCell(ColumnPersonnel.DiaChi)).trim();
						String DT = commonService.getStringValue(row.getCell(ColumnPersonnel.DienThoai)).trim();

						String SoSoHoKhau = commonService.getStringValue(row.getCell(ColumnPersonnel.SoSoHoKhau)).trim();
						String SoTaiKhoan = commonService.getStringValue(row.getCell(ColumnPersonnel.SoTaiKhoan)).trim();
						if (DT.equals("#N/A")) {
							DT = "";
						}
						String CMND = commonService.getStringValue(row.getCell(ColumnPersonnel.CMND)).trim();

						// kiem tra tinh trong danh sach orgtypeid_link = 25;
						Long tinh = null;

						//Long id_huyen = null;
						List<Org> lst_tinh = org_service.getByNameAndType(Tinh, 25);
						if (lst_tinh.size() != 0) {
							tinh = lst_tinh.get(0).getId();
						} else {
							Org org = new Org();
							org.setName(Tinh);
							org.setCode(Tinh.replaceAll(" ", ""));
							org.setStatus(1);
							org.setIs_manufacturer(0);
							org.setOrgtypeid_link(25);
							org.setOrgrootid_link(user.getRootorgid_link());
							Org id_org = org_service.save(org);

							// l???y id t???nh th??nh v???a th??m
							tinh = id_org.getId();

						}

						// kiem tra huyen trong danh sach tinh orgtypeid_link = 26;
						Long huyen = null;
						Org lst_huyen = org_service.getByNameAndTypeAndParentid_link(Huyen, 26, tinh);

						if (lst_huyen != null) {
							huyen = lst_huyen.getId();
						} else {
							Org org = new Org();
							org.setName(Huyen);
							org.setCode(Huyen.replaceAll(" ", ""));
							org.setStatus(1);
							org.setIs_manufacturer(0);
							org.setParentid_link(tinh);
							org.setOrgrootid_link(user.getRootorgid_link());
							org.setOrgtypeid_link(26);

							// l???y id huy???n v???a th??m
							Org id_org = org_service.save(org);
							huyen = id_org.getId();
//
						}
						// kiem tra xa trong danh sach huyen orgtypeid_link = 27;
						Long xa = null;
						Org lst_xa = org_service.getByNameAndTypeAndParentid_link(Xa, 27, huyen);
						if (lst_xa != null) {
							xa = lst_xa.getId();
						} else {
							Org org = new Org();
							org.setName(Xa);
							org.setCode(Xa.replaceAll(" ", ""));
							org.setStatus(1);
							org.setIs_manufacturer(0);
							org.setParentid_link(huyen);
							org.setOrgtypeid_link(27);
							org.setOrgrootid_link(user.getRootorgid_link());
							// l???y id huy???n v???a th??m
							Org id_org = org_service.save(org);
							xa = id_org.getId();

						}

						// String NgayCap =
						// commonService.getStringValue(row.getCell(ColumnPersonnel.NgayCap));
						// ngay caop cmnd
						Date NgayCap = null;
						try {
							String ngayCap = commonService.getStringValue(row.getCell(ColumnPersonnel.NgayCap));
							if (ngayCap.contains("/")) {
								String[] s_date = ngayCap.split("/");
								if(s_date.length > 3) {
									mes_err = " ?????nh d???ng ng??y c???p kh??ng ????ng dd/MM/yyyy! ??? d??ng " + (rowNum+1) +" c???t Ng??y c???p";
									break;
								}

								if (Integer.parseInt(s_date[1].toString()) < 13
										&& Integer.parseInt(s_date[0].toString()) < 32) {
									NgayCap = new SimpleDateFormat("dd/MM/yyyy").parse(ngayCap);
								} else {
									mes_err = " ?????nh d???ng ng??y c???p kh??ng ????ng dd/MM/yyyy! ??? d??ng " + (rowNum+1);
									break;
								}
							} else if (ngayCap != "") {
								if (DateUtil.isCellDateFormatted(row.getCell(ColumnPersonnel.NgayCap))) {
									NgayCap = row.getCell(ColumnPersonnel.NgayCap).getDateCellValue();
								}
							}
						} catch (Exception e) {
							if (DateUtil.isCellDateFormatted(row.getCell(ColumnPersonnel.NgayCap))) {
								NgayCap = row.getCell(ColumnPersonnel.NgayCap).getDateCellValue();
							}
						}

						String NoiCap = commonService.getStringValue(row.getCell(ColumnPersonnel.NoiCap)).trim();
						String CMTM = commonService.getStringValue(row.getCell(ColumnPersonnel.CMTM)).trim();
						// String NgayCapMoi =
						// commonService.getStringValue(row.getCell(ColumnPersonnel.NgayCapMoi));

						// ngay caop cmnd
						Date NgayCapMoi = null;
						try {
							String ngayCapMoi = commonService.getStringValue(row.getCell(ColumnPersonnel.NgayCapMoi));
							if (ngayCapMoi.contains("/")) {
								String[] s_date = ngayCapMoi.split("/");
								if (Integer.parseInt(s_date[1].toString()) < 13
										&& Integer.parseInt(s_date[0].toString()) < 32) {
									NgayCapMoi = new SimpleDateFormat("dd/MM/yyyy").parse(ngayCapMoi);
								} else {
									mes_err = " ?????nh d???ng ng??y c???p m???i kh??ng ????ng dd/MM/yyyy! " + " ??? d??ng  "
											+ (rowNum+1);
									break;
								}
							} else if (ngayCapMoi != "") {
								if (DateUtil.isCellDateFormatted(row.getCell(ColumnPersonnel.NgayCapMoi))) {
									NgayCapMoi = row.getCell(ColumnPersonnel.NgayCapMoi).getDateCellValue();
								}
							}
						} catch (Exception e) {
							if (DateUtil.isCellDateFormatted(row.getCell(ColumnPersonnel.NgayCapMoi))) {
								NgayCapMoi = row.getCell(ColumnPersonnel.NgayCapMoi).getDateCellValue();
							}
						}

						///
						// Ngay cap so ho khau
						Date NgayCapSoHoKhau = null;
						try {
							String ngayCapSoHoKhau = commonService.getStringValue(row.getCell(ColumnPersonnel.NgayCapHoKhau));
							if (ngayCapSoHoKhau.contains("/")) {
								String[] s_date = ngayCapSoHoKhau.split("/");
								if (Integer.parseInt(s_date[1].toString()) < 13
										&& Integer.parseInt(s_date[0].toString()) < 32
										&& Integer.parseInt(s_date[0]) > 0
										&& Integer.parseInt(s_date[1]) > 0) {
									NgayCapSoHoKhau = new SimpleDateFormat("dd/MM/yyyy").parse(ngayCapSoHoKhau);



								} else {
									mes_err = " ?????nh d???ng ng??y c???p m???i kh??ng ????ng dd/MM/yyyy! " + " ??? d??ng  "
											+ (rowNum+1);
									break;
								}
							} else if (ngayCapSoHoKhau != "") {
								if (DateUtil.isCellDateFormatted(row.getCell(ColumnPersonnel.NgayCapHoKhau))) {
									NgayCapSoHoKhau = row.getCell(ColumnPersonnel.NgayCapHoKhau).getDateCellValue();
								}
							}
						} catch (Exception e) {
							if (DateUtil.isCellDateFormatted(row.getCell(ColumnPersonnel.NgayCapHoKhau))) {
								NgayCapSoHoKhau = row.getCell(ColumnPersonnel.NgayCapHoKhau).getDateCellValue();
							}
						}

						String TenNganHang = commonService.getStringValue(row.getCell(ColumnPersonnel.TenNganHang)).trim();
						String NoiCapMoi = commonService.getStringValue(row.getCell(ColumnPersonnel.NoiCapMoi)).trim();
						String SK = commonService.getStringValue(row.getCell(ColumnPersonnel.SucKhoe)).trim();
						String SoSBH = commonService.getStringValue(row.getCell(ColumnPersonnel.SoSBH)).trim();
						if (SoSBH.equals("#N/A")) {
							SoSBH = "";
						}

//						String BienSoXe = commonService.getStringValue(row.getCell(ColumnPersonnel.BienSoXe)).trim();
//						String XeDap = commonService.getStringValue(row.getCell(ColumnPersonnel.XeDap)).trim();
//						Boolean isXeDap = false;
//						if(XeDap.toLowerCase().equals("x")) {
//							isXeDap = true;
//						}


						// person.setCode(MaSoMoi);
						person.setOrgrootid_link(orgrootid_link);
						person.setStatus(TinhTrang);

						//lo???i nh??n vi??n
						person.setPersonnel_typeid_link(LoaiNV);
						person.setFullname(HoVaTen);
						person.setGender(GioiTinh);
						person.setOrgid_link(orgid_link);
						person.setPositionid_link(positionid_link);
						person.setOrgmanagerid_link(orgmanageid_link);

						person.setBirthdate(NgaySinh);
						person.setDate_startworking(NgayVaoCT);
						person.setDate_endworking(NgayThoiViec);
						person.setReason(LyDo);
						person.setDate_probation_contract(NgayKiHDTV);
						person.setDate_limit_contract(NgayKiHDCTH);
						person.setDate_unlimit_contract(NgayKiHDVTH);
						person.setDate_insurance(NgayDongBH);

						person.setProvinceid_link(tinh);
						person.setDistrictid_link(huyen);
						person.setCommuneid_link(xa);
						person.setVillage(Thon);
						person.setAddress(DiaChi);
						person.setTel(DT);

						if (CMTM != "") {
							person.setIdnumber(CMTM);
						} else {
							person.setIdnumber(CMND);
						}
						if (NgayCapMoi != null) {
							person.setDateof_idnumber(NgayCapMoi);
						} else {
							person.setDateof_idnumber(NgayCap);
						}
						if (NoiCapMoi != "") {
							person.setPlace_idnumber(NoiCapMoi);
						} else {
							person.setPlace_idnumber(NoiCap);
						}

						person.setHealthinfo(SK);
						person.setInsurance_number(SoSBH);
						person.setAccount_number(SoTaiKhoan);
						person.setHousehold_number(SoSoHoKhau);
						person.setBankname(TenNganHang);
						person.setDate_household_grant(NgayCapSoHoKhau);
//                        person.setBike_number(BienSoXe);
//                        person.setIsbike(isXeDap);

						// luu nhan vien
						Personel personnel = personnel_service.save(person);
						Long personnelid_link = personnel.getId();

						// luu chuc vu
						int type = 1;// chu vu type =1
						// Date decision_date = new SimpleDateFormat("dd/MM/yyyy").parse("01/08/2021");
						Personnel_His personnel_His = new Personnel_His();
						List<Personnel_His> lst_personnel_His = personnel_his_service
								.getHis_personByType_Id(personnelid_link, type);
						// neu da co trong danh sach roi-> update
						if (lst_personnel_His.size() != 0) {
							personnel_His.setId(lst_personnel_His.get(0).getId());
							personnel_His.setPositionid_link(positionid_link);
							personnel_His.setType(type);
							personnel_His.setDecision_date(NgayKiHDCTH);
							personnel_His.setPersonnelid_link(personnelid_link);
						} // neu khong co trong danh sach thi tao moi
						else {

							personnel_His.setPositionid_link(positionid_link);
							personnel_His.setType(type);
							personnel_His.setDecision_date(NgayKiHDCTH);
							personnel_His.setPersonnelid_link(personnelid_link);
						}
						personnel_his_service.save(personnel_His);

						// luu ph??ng ban orgid_link
						// chu vu type =1, phong ban type =3
						List<Personnel_His> lst_personnel_His_org = personnel_his_service
								.getHis_personByType_Id(personnelid_link, 3);
						personnel_His = new Personnel_His();
						// neu da co trong danh sach roi-> update
						if (lst_personnel_His_org.size() != 0) {
							personnel_His.setId(lst_personnel_His_org.get(0).getId());
							personnel_His.setOrgid_link(orgid_link);
							personnel_His.setType(3);
							personnel_His.setDecision_date(NgayKiHDCTH);
							personnel_His.setPersonnelid_link(personnelid_link);
						} // neu khong co trong danh sach thi tao moi
						else {

							personnel_His.setOrgid_link(orgid_link);
							personnel_His.setType(3);
							personnel_His.setDecision_date(NgayKiHDCTH);
							personnel_His.setPersonnelid_link(personnelid_link);
						}
						personnel_his_service.save(personnel_His);

						response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
						response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));

						// Chuyen sang row tiep theo neu con du lieu thi xu ly tiep khong thi dung lai
						rowNum++;
						row = sheet.getRow(rowNum);
						if (row == null)
							break;

						MaSoMoi = commonService.getStringValue(row.getCell(ColumnPersonnel.MaSoMoi));
						MaSoMoi = MaSoMoi.equals("0") ? "" : MaSoMoi;
					}

				} catch (Exception e) {
					System.out.println(e.getMessage());
					mes_err = "C?? l???i ??? d??ng " + (rowNum + 1) + " " + mes_err;
				} finally {
					workbook.close();
					serverFile.delete();
				}
				// neu co loi
				if (mes_err == "") {
					response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
					response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
				} else {
					response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
					response.setMessage(mes_err);
				}
			}

		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}

		return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/personnelUploadBike", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> UploadPersonnelBike(HttpServletRequest request,
															@RequestParam("file") MultipartFile file) {
		ResponseBase response = new ResponseBase();
		Date current_time = new Date();
		String name = "";
		String mes_err = "";
		List<Personel> personList = new ArrayList<Personel>();
		GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long orgrootid_link = user.getRootorgid_link();
		try {
			String FolderPath = "upload/personnel";
			String uploadRootPath = request.getServletContext().getRealPath(FolderPath);
			File uploadRootDir = new File(uploadRootPath);
			if (!uploadRootDir.exists()) {
				uploadRootDir.mkdirs();
			}
			name = file.getOriginalFilename();

			if (name != null && name.length() > 0) {
				String[] str = name.split("\\.");
				String extend = str[str.length - 1];
				name = current_time.getTime() + "." + extend;
				File serverFile = new File(uploadRootDir.getAbsolutePath() + File.separator + name);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();

				// doc file upload
				XSSFWorkbook workbook = new XSSFWorkbook(serverFile);
				XSSFSheet sheet = workbook.getSheetAt(0);

				int rowNum = 1;
				// int colNum = 1;

				Row row = sheet.getRow(rowNum);

				try {
					String MaSoNV = "";
					MaSoNV = commonService.getStringValue(row.getCell(ColumnPersonnelBike.MaSoNV)).trim();
					MaSoNV = MaSoNV.equals("0") ? "" : MaSoNV;
					while (!MaSoNV.equals("")) {
						// tim nhan vien theo ma so v?? ????n v??? ??ang ???????c ch???n
						personList = personnel_service.getPersonelByCodeAndOrgManager(MaSoNV, null);
						if(personList.size() == 0) {
							// kh??ng t??m th???y nh??n vi??n v???i m?? s??? -> b??o l???i
							mes_err = " Nh??n vi??n v???i m?? s??? " + MaSoNV + " ??? d??ng " + (rowNum+1) + " kh??ng t???n t???i. ";
							break;
						}else {
							//
							for(Personel personel : personList) {
								String LoaiXe = commonService.getStringValue(row.getCell(ColumnPersonnelBike.LoaiXe)).trim();
								String NhanHieu = commonService.getStringValue(row.getCell(ColumnPersonnelBike.NhanHieu)).trim();
								String MauXe = commonService.getStringValue(row.getCell(ColumnPersonnelBike.MauXe)).trim();
								String BienSo = commonService.getStringValue(row.getCell(ColumnPersonnelBike.BienSo)).trim();

								Integer bikeType = 0;
								// xe m??y, xe ?????p ??i???n, xe ?????p
								if(LoaiXe.toLowerCase().equals("xe m??y")) {
									bikeType = PersonnelBikeType.BIKETYPE_XEMAY;
								}else if(LoaiXe.toLowerCase().equals("xe ?????p ??i???n")) {
									bikeType = PersonnelBikeType.BIKETYPE_XEDAPDIEN;
								}else if(LoaiXe.toLowerCase().equals("xe ?????p")) {
									bikeType = PersonnelBikeType.BIKETYPE_XEDAP;
								}else if(LoaiXe.toLowerCase().equals("")){
									bikeType = null;
								}else{
									mes_err = " Lo???i xe " + LoaiXe + " ??? d??ng " + (rowNum+1) + " kh??ng h???p l???. ";
									break;
								}
								personel.setBiketype(bikeType);
								personel.setBike_number(BienSo);
								personel.setBike_color(MauXe);
								personel.setBike_brand(NhanHieu);
								personel = personnel_service.save(personel);
							}
						}

						response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
						response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));

						// Chuyen sang row tiep theo neu con du lieu thi xu ly tiep khong thi dung lai
						rowNum++;
						row = sheet.getRow(rowNum);
						if (row == null)
							break;

						MaSoNV = commonService.getStringValue(row.getCell(ColumnPersonnelBike.MaSoNV)).trim();
						MaSoNV = MaSoNV.equals("0") ? "" : MaSoNV;
					}

				} catch (Exception e) {
					e.printStackTrace();
					mes_err = "C?? l???i ??? d??ng " + (rowNum + 1) + " " + mes_err;
				} finally {
					workbook.close();
					serverFile.delete();
				}
				// neu co loi
				if (mes_err == "") {
					response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
					response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
				} else {
					response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
					response.setMessage(mes_err);
				}
			}

		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}

		return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
	}
	@RequestMapping(value = "/personnelUploadSalary", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> UploadPersonnelSalary(HttpServletRequest request,
															  @RequestParam("file") MultipartFile file) {
		ResponseBase response = new ResponseBase();
		Date current_time = new Date();
		String name = "";
		String mes_err = "";
		List<Personel> personList = new ArrayList<Personel>();
		GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long orgrootid_link = user.getRootorgid_link();
		try {
			String FolderPath = "upload/personnel";
			String uploadRootPath = request.getServletContext().getRealPath(FolderPath);
			File uploadRootDir = new File(uploadRootPath);
			if (!uploadRootDir.exists()) {
				uploadRootDir.mkdirs();
			}
			name = file.getOriginalFilename();

			if (name != null && name.length() > 0) {
				String[] str = name.split("\\.");
				String extend = str[str.length - 1];
				name = current_time.getTime() + "." + extend;
				File serverFile = new File(uploadRootDir.getAbsolutePath() + File.separator + name);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();

				// doc file upload
				XSSFWorkbook workbook = new XSSFWorkbook(serverFile);
				XSSFSheet sheet = workbook.getSheetAt(0);

				int rowNum = 1;
				// int colNum = 1;

				Row row = sheet.getRow(rowNum);

				try {
					String MaSoNV = "";
					MaSoNV = commonService.getStringValue(row.getCell(ColumnPersonnelSalary.MaSoNV)).trim();
					MaSoNV = MaSoNV.equals("0") ? "" : MaSoNV;
					while (!MaSoNV.equals("")) {
						// tim nhan vien theo ma so v?? ????n v??? ??ang ???????c ch???n
						personList = personnel_service.getPersonelByCodeAndOrgManager(MaSoNV, null);
						if(personList.size() == 0) {
							// kh??ng t??m th???y nh??n vi??n v???i m?? s??? -> b??o l???i
							mes_err = " Nh??n vi??n v???i m?? s??? " + MaSoNV + " ??? d??ng " + (rowNum+1) + " kh??ng t???n t???i. ";
							break;
						}else {
							//
							for(Personel personel : personList) {
								try {
									Long bac = 0L;
									Long maBacLuongBH = 0L;
									Float heSoLuongBH = 0f;
									Long mucLuongDongBH = 0L;
									Long mucLuongHienTai = 0L;
									Float heSoCV = 0f;

									String Bac = commonService.getStringValue(row.getCell(ColumnPersonnelSalary.Bac)).trim();
									if(Bac != "") {
										bac = Long.parseLong(Bac);
									}
									if (bac < 0) {
										mes_err = " Nh??n vi??n v???i m?? s??? " + MaSoNV + " ??? d??ng " + (rowNum + 1) + " c?? c???t " + ColumnPersonnelSalary.Bac + " nh??? h??n kh??ng!";
										break;
									}

									String MaBacLuongBH = commonService.getStringValue(row.getCell(ColumnPersonnelSalary.MaBacLuongBH)).trim();
									if(MaBacLuongBH != "") {
										maBacLuongBH = Long.parseLong(MaBacLuongBH);
									}
									if (maBacLuongBH < 0) {
										mes_err = " Nh??n vi??n v???i m?? s??? " + MaSoNV + " ??? d??ng " + (rowNum + 1) + " c?? c???t " + ColumnPersonnelSalary.MaBacLuongBH + " nh??? h??n kh??ng!";
										break;
									}

									String HeSoLuongBH = commonService.getStringValue(row.getCell(ColumnPersonnelSalary.HeSoLuongBH)).trim();
									if(HeSoLuongBH != "") {
										heSoLuongBH = Float.parseFloat(HeSoLuongBH);
									}
									if (heSoLuongBH < 0) {
										mes_err = " Nh??n vi??n v???i m?? s??? " + MaSoNV + " ??? d??ng " + (rowNum + 1) + " c?? c???t " + ColumnPersonnelSalary.HeSoLuongBH + " nh??? h??n kh??ng!";
										break;
									}

									String MucLuongDongBH = commonService.getStringValue(row.getCell(ColumnPersonnelSalary.MucLuongDongBH)).trim();
									if(MucLuongDongBH != "") {
										mucLuongDongBH = Long.parseLong(MucLuongDongBH);
									}
									if (mucLuongDongBH < 0) {
										mes_err = " Nh??n vi??n v???i m?? s??? " + MaSoNV + " ??? d??ng " + (rowNum + 1) + " c?? c???t " + ColumnPersonnelSalary.MucLuongDongBH + " nh??? h??n kh??ng!";
										break;
									}

									String MucLuongHienTai = commonService.getStringValue(row.getCell(ColumnPersonnelSalary.MucLuongHienTai)).trim();
									if(MucLuongHienTai != "") {
										mucLuongHienTai = Long.parseLong(MucLuongHienTai);
									}
									if (mucLuongHienTai < 0) {
										mes_err = " Nh??n vi??n v???i m?? s??? " + MaSoNV + " ??? d??ng " + (rowNum + 1) + " c?? c???t " + ColumnPersonnelSalary.MucLuongHienTai + " nh??? h??n kh??ng!";
										break;
									}

									System.out.println("test");
									System.out.println(row.getCell(ColumnPersonnelSalary.HeSoCV));
									String HeSoCV = commonService.getStringValue(row.getCell(ColumnPersonnelSalary.HeSoCV)).trim();
									System.out.println(HeSoCV);
									System.out.println("test");
									if(HeSoCV != "") {
										heSoCV = Float.parseFloat(HeSoCV);
									}
									if (heSoCV < 0) {
										mes_err = " Nh??n vi??n v???i m?? s??? " + MaSoNV + " ??? d??ng " + (rowNum + 1) + " c?? c???t " + ColumnPersonnelSalary.HeSoCV + " nh??? h??n kh??ng!";
										break;
									}
									personel.setSalaryLevel(bac);
									personel.setSalaryCode(maBacLuongBH);
									personel.setFactorSalaryBH(heSoLuongBH);
									personel.setSalaryBH(mucLuongDongBH);
									personel.setSalaryCurrent(mucLuongHienTai);
									personel.setFactorCV(heSoCV);
									personel = personnel_service.save(personel);
								}
								catch (Exception e) {
									mes_err = " Nh??n vi??n v???i m?? s??? " + MaSoNV + " ??? d??ng " + (rowNum+1) + " c?? c???t kh??ng ????ng ?????nh d???ng s??? ";
									break;
								}
							}
						}
						if (mes_err != "") {
							break;
						}
//						response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
//						response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));

						// Chuyen sang row tiep theo neu con du lieu thi xu ly tiep khong thi dung lai
						rowNum++;
						row = sheet.getRow(rowNum);
						if (row == null)
							break;

						MaSoNV = commonService.getStringValue(row.getCell(ColumnPersonnelSalary.MaSoNV)).trim();
						MaSoNV = MaSoNV.equals("0") ? "" : MaSoNV;
					}
				} catch (Exception e) {
					e.printStackTrace();
					mes_err = "C?? l???i ??? d??ng " + (rowNum + 1) + " " + mes_err;
				} finally {
					workbook.close();
					serverFile.delete();
				}
				// neu co loi
				if (mes_err == "") {
					response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
					response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
				} else {
					response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
					response.setMessage(mes_err);
				}
			}

		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}

		return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/personnelUploadBank", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> UploadPersonnelBank(HttpServletRequest request,
															@RequestParam("file") MultipartFile file) {
		ResponseBase response = new ResponseBase();
		Date current_time = new Date();
		String name = "";
		String mes_err = "";
		List<Personel> personList = new ArrayList<Personel>();
		GpayUser user = (GpayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long orgrootid_link = user.getRootorgid_link();
		try {
			String FolderPath = "upload/personnel";
			String uploadRootPath = request.getServletContext().getRealPath(FolderPath);
			File uploadRootDir = new File(uploadRootPath);
			if (!uploadRootDir.exists()) {
				uploadRootDir.mkdirs();
			}
			name = file.getOriginalFilename();

			if (name != null && name.length() > 0) {
				String[] str = name.split("\\.");
				String extend = str[str.length - 1];
				name = current_time.getTime() + "." + extend;
				File serverFile = new File(uploadRootDir.getAbsolutePath() + File.separator + name);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();

				// doc file upload
				XSSFWorkbook workbook = new XSSFWorkbook(serverFile);
				XSSFSheet sheet = workbook.getSheetAt(0);

				int rowNum = 1;
				// int colNum = 1;

				Row row = sheet.getRow(rowNum);

				try {
					String MaSoNV = "";
					MaSoNV = commonService.getStringValue(row.getCell(ColumnPersonnelBank.MaSoNV)).trim();
					MaSoNV = MaSoNV.equals("0") ? "" : MaSoNV;
					while (!MaSoNV.equals("")) {
						// tim nhan vien theo ma so v?? ????n v??? ??ang ???????c ch???n
						personList = personnel_service.getPersonelByCodeAndOrgManager(MaSoNV, null);
						if(personList.size() == 0) {
							// kh??ng t??m th???y nh??n vi??n v???i m?? s??? -> b??o l???i
							mes_err = " Nh??n vi??n v???i m?? s??? " + MaSoNV + " ??? d??ng " + (rowNum+1) + " kh??ng t???n t???i. ";
							break;
						}else {
							//
							for(Personel personel : personList) {
								String NganHang = commonService.getStringValue(row.getCell(ColumnPersonnelBank.NganHang)).trim();
								String SoTaiKhoan = commonService.getStringValue(row.getCell(ColumnPersonnelBank.SoTaiKhoan)).trim();

								personel.setBankname(NganHang);
								personel.setAccount_number(SoTaiKhoan);
								personel = personnel_service.save(personel);
							}
						}

						response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
						response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));

						// Chuyen sang row tiep theo neu con du lieu thi xu ly tiep khong thi dung lai
						rowNum++;
						row = sheet.getRow(rowNum);
						if (row == null)
							break;

						MaSoNV = commonService.getStringValue(row.getCell(ColumnPersonnelBank.MaSoNV)).trim();
						MaSoNV = MaSoNV.equals("0") ? "" : MaSoNV;
					}

				} catch (Exception e) {
					e.printStackTrace();
					mes_err = "C?? l???i ??? d??ng " + (rowNum + 1) + " " + mes_err;
				} finally {
					workbook.close();
					serverFile.delete();
				}
				// neu co loi
				if (mes_err == "") {
					response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
					response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
				} else {
					response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
					response.setMessage(mes_err);
				}
			}

		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
		}

		return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
	}
}
