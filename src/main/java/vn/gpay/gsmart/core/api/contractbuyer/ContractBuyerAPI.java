package vn.gpay.gsmart.core.api.contractbuyer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.gpay.gsmart.core.base.ResponseBase;
import vn.gpay.gsmart.core.contractbuyer.ContractBuyer;
import vn.gpay.gsmart.core.contractbuyer.ContractBuyerD;
import vn.gpay.gsmart.core.contractbuyer.IContractBuyerDService;
import vn.gpay.gsmart.core.contractbuyer.IContractBuyerService;
import vn.gpay.gsmart.core.utils.Common;
import vn.gpay.gsmart.core.utils.ResponseMessage;

@RestController
@RequestMapping("/api/v1/contractbuyer")
public class ContractBuyerAPI {
	@Autowired
	IContractBuyerService contractBuyerService;
	@Autowired
	IContractBuyerDService contractBuyerDService;
	@Autowired
	Common commonService;

	@RequestMapping(value = "/getbypaging", method = RequestMethod.POST)
	public ResponseEntity<ContractBuyer_getbypaging_response> ContractBuyerGetpage(
			@RequestBody ContractBuyer_getbypaging_request entity, HttpServletRequest request) {
		ContractBuyer_getbypaging_response response = new ContractBuyer_getbypaging_response();
		try {
			List<ContractBuyer> cblist = contractBuyerService.getContractBuyerBySearch(entity);

			List<ContractBuyer> temp = new ArrayList<ContractBuyer>();

			for (ContractBuyer cb : cblist) {
				String contract_code = cb.getContract_code().toLowerCase();
				if (!contract_code.contains(entity.contract_code.toLowerCase()))
					continue;
				if (cb.getIs_delete() == true)
					continue;

				temp.add(cb);
			}

			response.totalCount = temp.size();

			PageRequest page = PageRequest.of(entity.page - 1, entity.limit);
			int start = (int) page.getOffset();
			int end = (start + page.getPageSize()) > temp.size() ? temp.size() : (start + page.getPageSize());
			Page<ContractBuyer> pageToReturn = new PageImpl<ContractBuyer>(temp.subList(start, end), page, temp.size());

			response.data = pageToReturn.getContent();
//			response.data = temp;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ContractBuyer_getbypaging_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ContractBuyer_getbypaging_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getall", method = RequestMethod.POST)
	public ResponseEntity<ContractBuyer_getbypaging_response> ContractBuyerGetAll(HttpServletRequest request) {
		ContractBuyer_getbypaging_response response = new ContractBuyer_getbypaging_response();
		try {
			List<ContractBuyer> contractBuyers = contractBuyerService.findAll();

			response.data = contractBuyers;

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ContractBuyer_getbypaging_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ContractBuyer_getbypaging_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getone", method = RequestMethod.POST)
	public ResponseEntity<ContractBuyer_getone_response> ContractBuyerGetOne(
			@RequestBody ContractBuyer_getone_request entity, HttpServletRequest request) {
		ContractBuyer_getone_response response = new ContractBuyer_getone_response();
		try {

			response.data = contractBuyerService.findOne(entity.id);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ContractBuyer_getone_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ContractBuyer_getone_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<ContractBuyer_create_response> ContractBuyerCreate(
			@RequestBody ContractBuyer_create_request entity, HttpServletRequest request) {
		ContractBuyer_create_response response = new ContractBuyer_create_response();
		try {
			ContractBuyer cb = entity.data;
			List<ContractBuyerD> listContractBuyerD = cb.getContractBuyerDs();

//			for(ContractBuyerD c : listContractBuyerD) {
//				System.out.println
//			}

			// check nếu mã đã tồn tại
			List<ContractBuyer> cblist = new ArrayList<ContractBuyer>();
			if (cb.getId() == 0) {
				// create
				cblist = contractBuyerService.getByContractCode(cb.getContract_code());

			} else {
				// update
				cblist = contractBuyerService.getOtherContractBuyerByContractCode(cb.getContract_code(), cb.getId());

			}
			if (cblist.size() == 0) {
				// chưa tồn tại
				List<Long> buyerIds = new ArrayList<Long>();
				ContractBuyer temp = contractBuyerService.save(cb);
				Long contractBuyerId = temp.getId();

				for (ContractBuyerD contractBuyerD : listContractBuyerD) {
					Long buyerid_link = contractBuyerD.getBuyerid_link();
					contractBuyerD.setContractbuyerid_link(contractBuyerId);
					buyerIds.add(buyerid_link);

					List<ContractBuyerD> contractBuyerDByContractBuyerIdAndBuyerId = contractBuyerDService
							.getByContractBuyerIdAndBuyerId(contractBuyerId, buyerid_link);
					if (contractBuyerDByContractBuyerIdAndBuyerId.size() == 0) {

						contractBuyerDService.save(contractBuyerD);
					}
				}

				List<ContractBuyerD> contractBuyerDByContractBuyerId = contractBuyerDService
						.getByContractBuyerId(contractBuyerId);
				for (ContractBuyerD contractBuyerD : contractBuyerDByContractBuyerId) {
					if (!buyerIds.contains(contractBuyerD.getBuyerid_link())) {
						contractBuyerDService.deleteById(contractBuyerD.getId());
					}
				}

				response.id = contractBuyerId;
				response.setMessage("Lưu thành công");
			} else {
				// đã tồn tại
				response.setMessage("Mã hợp đồng đã tồn tại");
			}

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			return new ResponseEntity<ContractBuyer_create_response>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ContractBuyer_create_response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> ContractBuyerDelete(@RequestBody ContractBuyer_delete_request entity,
			HttpServletRequest request) {
		ResponseBase response = new ResponseBase();
		try {
//			contractBuyerService.deleteById(entity.id);
			ContractBuyer contractBuyer = contractBuyerService.findOne(entity.id);
			contractBuyer.setIs_delete(true);
			contractBuyerService.save(contractBuyer);

			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ResponseBase>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getyears", method = RequestMethod.POST)
	public ResponseEntity<ContractBuyer_getbypaging_response> ContractBuyerGetYears(HttpServletRequest request) {
		ContractBuyer_getbypaging_response response = new ContractBuyer_getbypaging_response();
		try {
			List<Integer> years = contractBuyerService.getAllYears();
//			years.add(0, null);

			List<ContractBuyer> data = new ArrayList<>();
			for (Integer year : years) {
				ContractBuyer temp = new ContractBuyer();
				temp.setContract_year(year);
				data.add(temp);
			}

			response.data = data;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ContractBuyer_getbypaging_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ContractBuyer_getbypaging_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getByBuyer", method = RequestMethod.POST)
	public ResponseEntity<ContractBuyer_getbypaging_response> ContractBuyerGetByBuyer(
			@RequestBody ContractBuyer_getone_request entity, HttpServletRequest request) {
		ContractBuyer_getbypaging_response response = new ContractBuyer_getbypaging_response();
		try {
			response.data = contractBuyerService.getByBuyer(entity.id); // buyerid_link
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<ContractBuyer_getbypaging_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ContractBuyer_getbypaging_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/upload_file", method = RequestMethod.POST)
	public ResponseEntity<Upload_file_response> Product_updteImg(HttpServletRequest request,
			@RequestParam("file") MultipartFile file, @RequestParam("id") int id) {
		Upload_file_response response = new Upload_file_response();

		try {
			ContractBuyer contract = contractBuyerService.findOne(id);
			String FolderPath = "contract";

			String uploadRootPath = request.getServletContext().getRealPath("");
			File uploadRootDir = new File(uploadRootPath);
			File folder_upload = new File(uploadRootDir.getParent() + "/" + FolderPath);
			// Tạo thư mục gốc upload nếu nó không tồn tại.
			if (!folder_upload.exists()) {
				folder_upload.mkdirs();
			}

			String name = file.getOriginalFilename();
			if (name != null && name.length() > 0) {
				File serverFile = new File(folder_upload.getAbsolutePath() + File.separator + name);

				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();
				contract.setFile_contract_name(name);
				response.filename = name;
			}

			contractBuyerService.save(contract);
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<Upload_file_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<Upload_file_response>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/download_file", method = RequestMethod.POST)
	public ResponseEntity<download_file_response> DownloadChaoGia(HttpServletRequest request,
			@RequestBody download_file_request entity) {

		download_file_response response = new download_file_response();
		try {
			ContractBuyer contract = contractBuyerService.findOne(entity.id);
			String filename = contract.getFile_contract_name();

			String FolderPath = "contract";

			// Thư mục gốc upload file.
			String uploadRootPath = request.getServletContext().getRealPath("");

			File uploadRootDir = new File(uploadRootPath);
			String parent = uploadRootDir.getParent();
			String filePath = parent + "/" + FolderPath + "/" + filename;
			Path path = Paths.get(filePath);
			byte[] data = Files.readAllBytes(path);
			response.data = data;
			response.setRespcode(ResponseMessage.KEY_RC_SUCCESS);
			response.setMessage(ResponseMessage.getMessage(ResponseMessage.KEY_RC_SUCCESS));
			return new ResponseEntity<download_file_response>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setRespcode(ResponseMessage.KEY_RC_EXCEPTION);
			response.setMessage(e.getMessage());
			return new ResponseEntity<download_file_response>(response, HttpStatus.OK);
		}
	}
}
