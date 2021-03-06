package restaurant.controller;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import common.database.MyBatisSessionFactory;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import restaurant.vo.AdminVo;
import restaurant.vo.EventVo;
import restaurant.vo.LoginSessionVo;
import restaurant.vo.MemberVo;
import restaurant.vo.MenuVo;
import restaurant.vo.RestaurantVo;
import restaurant.vo.ReviewVo;
import restaurant.vo.StoreVo;

@Controller
public class RestaurantController {

	String SERVER_ADDR = "http://her5793.iptime.org:8080/light_web/";

	
	@RequestMapping(value = "/selectLogin", produces = "application/json; charset=UTF-8")
	public @ResponseBody String getLogin(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		request.setCharacterEncoding("UTF-8");

		List<LoginSessionVo> list = new ArrayList<LoginSessionVo>();
		LoginSessionVo loginSessionVo = new LoginSessionVo();
		MemberVo memberVo = new MemberVo();
		AdminVo adminVo = new AdminVo();
		
		
		SqlSession session = MyBatisSessionFactory.getSqlSession();

		String login_id = request.getParameter("login_id");
		String password = request.getParameter("password");
	
		
		HashMap map = new HashMap<String, Object>();
		map.put("member_id", login_id);
		map.put("password", password);

		
		try {
			memberVo = session.selectOne("SqlRestaurant.selectMember", map);
		} finally {
			session.commit();
	
		}

		if (memberVo != null) {
			loginSessionVo.setLoginType(0);
			loginSessionVo.setMember_id(memberVo.getMember_id());
			loginSessionVo.setMember_name(memberVo.getMember_name());
			loginSessionVo.setMember_picture(memberVo.getMember_picture());
			loginSessionVo.setAdmin_id("");
		}else
		{
			
			map.put("admin_id", login_id);
			map.put("password", password);	
			try {
				adminVo = session.selectOne("SqlRestaurant.selectAdmin", map);
			} finally {
				session.commit();

			}

			
			
			if (adminVo != null) {
				loginSessionVo.setLoginType(1);
				loginSessionVo.setAdmin_id(adminVo.getAdmin_id());
				loginSessionVo.setMember_id("");
				loginSessionVo.setAdmin_name(adminVo.getAdmin_name());
				loginSessionVo.setAdmin_picture(adminVo.getAdmin_picture());
				loginSessionVo.setBeacon_id(adminVo.getBeacon_id());
			}
		
		}

		
		
		list.add(loginSessionVo);
		
		
		session.close();

		JSONArray jsonArray = JSONArray.fromObject(list);

		map = new HashMap<String, Object>();
		map.put("loginSession", jsonArray);

		JSONObject jsonObject = JSONObject.fromObject(map);
		System.out.println("json - " + jsonObject.toString());
		return jsonObject.toString();
	}
	
/*송명근*/ ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@RequestMapping(value = "/selectStore", produces = "application/json; charset=UTF-8")//송명근 이래 호출 해라
	public @ResponseBody String getSelectStore(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		request.setCharacterEncoding("UTF-8");

		SqlSession session = MyBatisSessionFactory.getSqlSession();
		// request �ޱ�

		String beacon_id = request.getParameter("beacon_id");
		String store_name = request.getParameter("store_name");
		String store_type = request.getParameter("store_type");
		
		System.out.println("beacon_id::" + beacon_id);
		System.out.println("store_name::" + store_name);
		System.out.println("store_type::" + store_type);

		HashMap map = new HashMap<String, Object>();
		
		
		
		if(beacon_id != null)
		{
			String[] trans_beacon_id = beacon_id.split(",");
			List beacon_id_list = new ArrayList();

			for(int i=0; i<trans_beacon_id.length; i++)
				{
					beacon_id_list.add(trans_beacon_id[i].toString());
					System.out.println(trans_beacon_id[i].toString());
				}
			System.out.println(beacon_id_list.size());
			map.put("beacon_id", beacon_id_list);
		}
		else if(store_name != null){
			map.put("store_name", "%"+store_name+"%");
		}else if(store_type != null){
			map.put("store_type", store_type);
		}
		
		
		List<RestaurantVo> list = session.selectList("SqlRestaurant.selectStore", map);
		session.close();

		JSONArray jsonArray = JSONArray.fromObject(list);

		map = new HashMap<String, Object>();
		map.put("store", jsonArray);

		JSONObject jsonObject = JSONObject.fromObject(map);
		System.out.println("json - " + jsonObject.toString());
		return jsonObject.toString();
	}
	
	@RequestMapping(value = "/selectRestaurant", produces = "application/json; charset=UTF-8")
	public @ResponseBody String getSelectRestaurant(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		request.setCharacterEncoding("UTF-8");

		SqlSession session = MyBatisSessionFactory.getSqlSession();
		// request �ޱ�

		int store_id = Integer.parseInt(request.getParameter("store_id"));

		System.out.println("store_id::" + store_id);
		HashMap map = new HashMap<String, Object>();
		map.put("store_id", store_id);
		List<RestaurantVo> list = session.selectList("SqlRestaurant.selectRestaurant", map);
		session.close();

		JSONArray jsonArray = JSONArray.fromObject(list);

		map = new HashMap<String, Object>();
		map.put("store", jsonArray);

		JSONObject jsonObject = JSONObject.fromObject(map);
		System.out.println("json - " + jsonObject.toString());
		return jsonObject.toString();
	}

	@RequestMapping(value = "/selectMenu", produces = "application/json; charset=UTF-8")
	public @ResponseBody String getSelectMenu(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		request.setCharacterEncoding("UTF-8");

		SqlSession session = MyBatisSessionFactory.getSqlSession();
		// request �ޱ�

		int store_id = Integer.parseInt(request.getParameter("store_id"));

		System.out.println("store_id::" + store_id);
		HashMap map = new HashMap<String, Object>();
		map.put("store_id", store_id);
		List<MenuVo> list = session.selectList("SqlRestaurant.selectMenu", map);
		session.close();

		JSONArray jsonArray = JSONArray.fromObject(list);

		map = new HashMap<String, Object>();
		map.put("menu", jsonArray);

		JSONObject jsonObject = JSONObject.fromObject(map);
		System.out.println("json - " + jsonObject.toString());
		return jsonObject.toString();
	}

	@RequestMapping(value = "/selectReview", produces = "application/json; charset=UTF-8")
	public @ResponseBody String getSelectReview(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		request.setCharacterEncoding("UTF-8");

		SqlSession session = MyBatisSessionFactory.getSqlSession();
		// request �ޱ�

		int store_id = Integer.parseInt(request.getParameter("store_id"));

		System.out.println("store_id::" + store_id);
		HashMap map = new HashMap<String, Object>();
		map.put("store_id", store_id);
		List<MenuVo> list = session.selectList("SqlRestaurant.selectReview", map);
		session.close();

		JSONArray jsonArray = JSONArray.fromObject(list);

		map = new HashMap<String, Object>();
		map.put("review", jsonArray);

		JSONObject jsonObject = JSONObject.fromObject(map);
		System.out.println("json - " + jsonObject.toString());
		return jsonObject.toString();
	}

	@RequestMapping(value = "/selectEvent", produces = "application/json; charset=UTF-8")
	public @ResponseBody String getSelectEvent(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		request.setCharacterEncoding("UTF-8");

		SqlSession session = MyBatisSessionFactory.getSqlSession();
		// request �ޱ�

		int store_id = Integer.parseInt(request.getParameter("store_id"));

		System.out.println("store_id::" + store_id);
		HashMap map = new HashMap<String, Object>();
		map.put("store_id", store_id);
		List<MenuVo> list = session.selectList("SqlRestaurant.selectEvent", map);
		session.close();

		JSONArray jsonArray = JSONArray.fromObject(list);

		map = new HashMap<String, Object>();
		map.put("event", jsonArray);

		JSONObject jsonObject = JSONObject.fromObject(map);
		System.out.println("json - " + jsonObject.toString());
		return jsonObject.toString();
	}

	@RequestMapping(value = "/insertReview", produces = "application/json; charset=utf-8")

	public @ResponseBody String insertReview(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		SqlSession session = MyBatisSessionFactory.getSqlSession();
		ReviewVo reviewVo = new ReviewVo();

		if (!isMultipart) {
		} else {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding("utf-8");
			List items = null;
			try {
				items = upload.parseRequest(request);
			} catch (FileUploadException e) {
				System.out.println(" error: " + e);
			}
			Iterator itr = items.iterator();
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				if (item.isFormField()) {
					// 수정 되어야 할 부분
					if (item != null && item.getFieldName().equals("member_id"))
						reviewVo.setMember_id(item.getString("UTF-8"));
					if (item != null && item.getFieldName().equals("menu_id"))
						reviewVo.setMenu_id(Integer.parseInt(item.getString("UTF-8")));
					if (item != null && item.getFieldName().equals("review_eval"))
						reviewVo.setReview_eval(Double.parseDouble(item.getString("UTF-8")));
					if (item != null && item.getFieldName().equals("review_info"))
						reviewVo.setReview_info(item.getString("UTF-8"));
					
					if (item != null && item.getFieldName().equals("write_date"))
						reviewVo.setWrite_date(item.getString("UTF-8"));
					if (item != null && item.getFieldName().equals("store_id"))
						reviewVo.setStore_id(Integer.parseInt(item.getString("UTF-8")));
					if (item != null && item.getFieldName().equals("admin_id"))
						reviewVo.setAdmin_id(item.getString("UTF-8"));
				} else {
					try {
						String itemName = item.getName();
						if (itemName == null || itemName.equals(""))
							continue;
						String fileName = FilenameUtils.getName(itemName);
						// String rootPath =
						// getServletContext().getRealPath("/");
						File savedFile = new File(
								"C:/Users/house/Desktop/dev/restaurant-server/restaurant-server/WebContent/review/"
										+ fileName);
						item.write(savedFile);
						
						 while(true)
						    {
							    URL url = new URL(SERVER_ADDR + "review/" + fileName);
							    URLConnection con = url.openConnection();
							    HttpURLConnection exitCode = (HttpURLConnection)con;
		
		
							   if( exitCode.getResponseCode() == 200)
								   break;
							   else if(exitCode.getResponseCode() == 404)
								   System.out.println("NO존재");
						    }
						
						System.out.println("이미지 파일 저장:" + savedFile);

						reviewVo.setReview_picture(SERVER_ADDR + "review/" + fileName);

					} catch (Exception e) {
						System.out.println("error: " + e);
					}
				}
			}
			/* 데이터베이스에 저장 */
			try {
				session.insert("SqlRestaurant.insertReview", reviewVo);
			} finally {
				session.commit();
				session.close();
			}

		}
		return "uploaded successfully";
	}

	@RequestMapping(value = "/insertEvent", produces = "application/json; charset=utf-8")

	public @ResponseBody String insertEvent(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		SqlSession session = MyBatisSessionFactory.getSqlSession();
		EventVo eventVo = new EventVo();

		if (!isMultipart) {
		} else {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding("utf-8");
			List items = null;
			try {
				items = upload.parseRequest(request);
			} catch (FileUploadException e) {
				System.out.println(" error: " + e);
			}
			Iterator itr = items.iterator();
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				if (item.isFormField()) {
					// 수정 되어야 할 부분

					if (item != null && item.getFieldName().equals("store_id"))
						eventVo.setStore_id(Integer.parseInt(item.getString("UTF-8")));

					if (item != null && item.getFieldName().equals("event_name"))
						eventVo.setEvent_name(item.getString("UTF-8"));

					if (item != null && item.getFieldName().equals("event_price"))
						eventVo.setEvent_price(item.getString("UTF-8"));

					if (item != null && item.getFieldName().equals("start_date"))
						eventVo.setStart_date(item.getString("UTF-8"));

					if (item != null && item.getFieldName().equals("end_date"))
						eventVo.setEnd_date(item.getString("UTF-8"));

					if (item != null && item.getFieldName().equals("event_info"))
						eventVo.setEvent_info(item.getString("UTF-8"));

					if (item != null && item.getFieldName().equals("event_type"))
						eventVo.setEvent_type(item.getString("UTF-8"));

				} else {
					try {
						String itemName = item.getName();
						if (itemName == null || itemName.equals(""))
							continue;
						String fileName = FilenameUtils.getName(itemName);
						// String rootPath =
						// getServletContext().getRealPath("/");
						File savedFile = new File(
								"C:/Users/house/Desktop/dev/restaurant-server/restaurant-server/WebContent/event/"
										+ fileName);
						item.write(savedFile);
						
						 while(true)
						    {
							    URL url = new URL(SERVER_ADDR + "event/" + fileName);
							    URLConnection con = url.openConnection();
							    HttpURLConnection exitCode = (HttpURLConnection)con;
		
		
							   if( exitCode.getResponseCode() == 200)
								   break;
							   else if(exitCode.getResponseCode() == 404)
								   System.out.println("NO존재");
						    }
						
						System.out.println("이미지 파일 저장:" + savedFile);
						eventVo.setEvent_picture(SERVER_ADDR + "event/" + fileName);

					} catch (Exception e) {
						System.out.println("error: " + e);
					}
				}
			}

			/* 데이터베이스에 저장 */
			try {
				session.insert("SqlRestaurant.insertEvent", eventVo);
			} finally {
				session.commit();
				session.close();
			}
		}
		return "uploaded successfully";
	}

	@RequestMapping(value = "/insertMenu", produces = "application/json; charset=utf-8")

	public @ResponseBody String insertMenu(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		SqlSession session = MyBatisSessionFactory.getSqlSession();
		MenuVo menuVo = new MenuVo();

		if (!isMultipart) {
		} else {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding("utf-8");
			List items = null;
			try {
				items = upload.parseRequest(request);
			} catch (FileUploadException e) {
				System.out.println(" error: " + e);
			}
			Iterator itr = items.iterator();
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				if (item.isFormField()) {
					// 수정 되어야 할 부분

					if (item != null && item.getFieldName().equals("store_id"))
						menuVo.setStore_id(Integer.parseInt(item.getString("UTF-8")));

					if (item != null && item.getFieldName().equals("menu_name"))
						menuVo.setMenu_name(item.getString("UTF-8"));

					if (item != null && item.getFieldName().equals("menu_price"))
						menuVo.setMenu_price(item.getString("UTF-8"));

					if (item != null && item.getFieldName().equals("menu_info"))
						menuVo.setMenu_info(item.getString("UTF-8"));

				} else {
					try {
						String itemName = item.getName();
						if (itemName == null || itemName.equals(""))
							continue;
						String fileName = FilenameUtils.getName(itemName);
						// String rootPath =
						// getServletContext().getRealPath("/");
						File savedFile = new File(
								"C:/Users/house/Desktop/dev/restaurant-server/restaurant-server/WebContent/menu/"
										+ fileName);
						item.write(savedFile);
					    while(true)
					    {
						    URL url = new URL(SERVER_ADDR + "menu/" + fileName);
						    URLConnection con = url.openConnection();
						    HttpURLConnection exitCode = (HttpURLConnection)con;
	
	
						   if( exitCode.getResponseCode() == 200)
							   break;
						   else if(exitCode.getResponseCode() == 404)
							   System.out.println("NO존재");
					    }
						System.out.println("이미지 파일 저장:" + savedFile);
						menuVo.setMenu_picture(SERVER_ADDR + "menu/" + fileName);
					} catch (Exception e) {
						System.out.println("error: " + e);
					}
				}
			}
			/* 데이터베이스에 저장 */
			try {
				session.insert("SqlRestaurant.insertMenu", menuVo);
			} finally {
				session.commit();
				session.close();
			}

		}
		
		return "uploaded successfully";
	}

	@RequestMapping(value = "/insertMember", produces = "application/json; charset=utf-8")

	public @ResponseBody String insertMember(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		SqlSession session = MyBatisSessionFactory.getSqlSession();
		MemberVo memberVo = new MemberVo();

		if (!isMultipart) {
		} else {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding("utf-8");
			List items = null;
			try {
				items = upload.parseRequest(request);
			} catch (FileUploadException e) {
				System.out.println(" error: " + e);
			}
			Iterator itr = items.iterator();
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				if (item.isFormField()) {
					// 수정 되어야 할 부분

					if (item != null && item.getFieldName().equals("member_id"))
						memberVo.setMember_id(item.getString("UTF-8"));

					if (item != null && item.getFieldName().equals("password"))
						memberVo.setPassword(item.getString("UTF-8"));

					if (item != null && item.getFieldName().equals("member_phone"))
						memberVo.setMember_phone(item.getString("UTF-8"));

					if (item != null && item.getFieldName().equals("member_name"))
						memberVo.setMember_name(item.getString("UTF-8"));

				} else {
					try {
						String itemName = item.getName();
						if (itemName == null || itemName.equals(""))
							continue;
						String fileName = FilenameUtils.getName(itemName);
						// String rootPath =
						// getServletContext().getRealPath("/");
						File savedFile = new File(
								"C:/Users/house/Desktop/dev/restaurant-server/restaurant-server/WebContent/member/"
										+ fileName);
						item.write(savedFile);
						
						 while(true)
						    {
							    URL url = new URL(SERVER_ADDR + "member/" + fileName);
							    URLConnection con = url.openConnection();
							    HttpURLConnection exitCode = (HttpURLConnection)con;
		
		
							   if( exitCode.getResponseCode() == 200)
								   break;
							   else if(exitCode.getResponseCode() == 404)
								   System.out.println("NO존재");
						    }
						
						System.out.println("이미지 파일 저장:" + savedFile);

						memberVo.setMember_picture(SERVER_ADDR + "member/" + fileName);
					} catch (Exception e) {
						System.out.println("error: " + e);
					}
				}
			}
			/* 데이터베이스에 저장 */
			try {
				session.insert("SqlRestaurant.insertMember", memberVo);
			} finally {
				session.commit();
				session.close();
			}
		}
		return "uploaded successfully";
	}

	@RequestMapping(value = "/insertAdmin", produces = "application/json; charset=utf-8")

	public @ResponseBody String insertAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		SqlSession session = MyBatisSessionFactory.getSqlSession();
		AdminVo adminVo = new AdminVo();

		if (!isMultipart) {
		} else {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding("utf-8");
			List items = null;
			try {
				items = upload.parseRequest(request);
			} catch (FileUploadException e) {
				System.out.println(" error: " + e);
			}
			Iterator itr = items.iterator();
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				if (item.isFormField()) {
					// 수정 되어야 할 부분


					if (item != null && item.getFieldName().equals("admin_id"))
						adminVo.setAdmin_id(item.getString("UTF-8"));

					if (item != null && item.getFieldName().equals("beacon_id"))
						adminVo.setBeacon_id(item.getString("UTF-8"));

					if (item != null && item.getFieldName().equals("admin_phone"))
						adminVo.setAdmin_phone(item.getString("UTF-8"));

					if (item != null && item.getFieldName().equals("admin_name"))
						adminVo.setAdmin_name(item.getString("UTF-8"));

					if (item != null && item.getFieldName().equals("password"))
						adminVo.setPassword(item.getString("UTF-8"));

				} else {
					try {
						String itemName = item.getName();
						if (itemName == null || itemName.equals(""))
							continue;
						String fileName = FilenameUtils.getName(itemName);
						// String rootPath =
						// getServletContext().getRealPath("/");
						File savedFile = new File(
								"C:/Users/house/Desktop/dev/restaurant-server/restaurant-server/WebContent/admin/"
										+ fileName);
						item.write(savedFile);
						 while(true)
						    {
							    URL url = new URL(SERVER_ADDR + "admin/" + fileName);
							    URLConnection con = url.openConnection();
							    HttpURLConnection exitCode = (HttpURLConnection)con;
		
		
							   if( exitCode.getResponseCode() == 200)
								   break;
							   else if(exitCode.getResponseCode() == 404)
								   System.out.println("NO존재");
						    }
						
						System.out.println("이미지 파일 저장:" + savedFile);
						adminVo.setAdmin_picture(SERVER_ADDR + "admin/" + fileName);
						
					} catch (Exception e) {
						System.out.println("error: " + e);
					}
				}
			}
			/* 데이터베이스에 저장 */
			try {
				session.insert("SqlRestaurant.insertAdmin", adminVo);
			} finally {
				session.commit();
				session.close();
			}
		}
		return "uploaded successfully";
	}
	
	
	@RequestMapping(value = "/insertStore", produces = "application/json; charset=utf-8")

	public @ResponseBody String insertStore(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		SqlSession session = MyBatisSessionFactory.getSqlSession();
		StoreVo storeVo = new StoreVo();

		if (!isMultipart) {
		} else {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding("utf-8");
			List items = null;
			try {
				items = upload.parseRequest(request);
			} catch (FileUploadException e) {
				System.out.println(" error: " + e);
			}
			Iterator itr = items.iterator();
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				if (item.isFormField()) {
					// 수정 되어야 할 부분

					if (item != null && item.getFieldName().equals("store_type"))
						storeVo.setStore_type(item.getString("UTF-8"));
			
		
					if (item != null && item.getFieldName().equals("store_address"))
						storeVo.setStore_address(item.getString("UTF-8"));

					if (item != null && item.getFieldName().equals("store_phone"))
						storeVo.setStore_phone(item.getString("UTF-8"));

					if (item != null && item.getFieldName().equals("store_info"))
						storeVo.setStore_info(item.getString("UTF-8"));

					if (item != null && item.getFieldName().equals("store_name"))
						storeVo.setStore_name(item.getString("UTF-8"));
					
					if (item != null && item.getFieldName().equals("beacon_id"))
						storeVo.setBeacon_id(item.getString("UTF-8"));

				} else {
					try {
						String itemName = item.getName();
						if (itemName == null || itemName.equals(""))
							continue;
						String fileName = FilenameUtils.getName(itemName);
						// String rootPath =
						// getServletContext().getRealPath("/");
						File savedFile = new File(
								"C:/Users/house/Desktop/dev/restaurant-server/restaurant-server/WebContent/restaurant/"
										+ fileName);
						item.write(savedFile);
						 while(true)
						    {
							    URL url = new URL(SERVER_ADDR + "restaurant/" + fileName);
							    URLConnection con = url.openConnection();
							    HttpURLConnection exitCode = (HttpURLConnection)con;
		
		
							   if( exitCode.getResponseCode() == 200)
								   break;
							   else if(exitCode.getResponseCode() == 404)
								   System.out.println("NO존재");
						    }
						
						System.out.println("이미지 파일 저장:" + savedFile);
						storeVo.setStore_picture(SERVER_ADDR + "restaurant/" + fileName);
						
					} catch (Exception e) {
						System.out.println("error: " + e);
					}
				}
			}
			/* 데이터베이스에 저장 */
			try {
				session.insert("SqlRestaurant.insertStore", storeVo);
			} finally {
				session.commit();
				session.close();
			}
		}
		return "uploaded successfully";
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RequestMapping(value = "/deleteMenu", produces = "application/json; charset=UTF-8")//송명근 이래 호출 해라
	public void getDeleteMenu(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		request.setCharacterEncoding("UTF-8");
		SqlSession session = MyBatisSessionFactory.getSqlSession();
	
		System.out.println("==========================================================================================================\n\n");
		int menu_id = Integer.parseInt(request.getParameter("menu_id"));
		
		System.out.println("delete::"+menu_id);
		HashMap map = new HashMap<String, Object>();
		map.put("menu_id", menu_id);
		
		try {
			session.delete("SqlRestaurant.deleteMenu", map);
		
		} finally {
			session.commit();
			session.close();
		}

	}
	
	@RequestMapping(value = "/deleteEvent", produces = "application/json; charset=UTF-8")//송명근 이래 호출 해라
	public void getDeleteEvent(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		request.setCharacterEncoding("UTF-8");
		SqlSession session = MyBatisSessionFactory.getSqlSession();
	
		System.out.println("==========================================================================================================\n\n");
		int event_id = Integer.parseInt(request.getParameter("event_id"));
		
		System.out.println("delete::"+event_id);
		HashMap map = new HashMap<String, Object>();
		map.put("event_id", event_id);
		
		try {
			session.delete("SqlRestaurant.deleteEvent", map);
		
		} finally {
			session.commit();
			session.close();
		}

	}

	@RequestMapping(value = "/selectMenuInfo", produces = "application/json; charset=UTF-8")
	public @ResponseBody String getSelectMenuInfo(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		request.setCharacterEncoding("UTF-8");

		SqlSession session = MyBatisSessionFactory.getSqlSession();
		// request �ޱ�

		int menu_id = Integer.parseInt(request.getParameter("menu_id"));

		System.out.println("menu_id::" + menu_id);
		HashMap map = new HashMap<String, Object>();
		map.put("menu_id", menu_id);
		List<MenuVo> list = session.selectList("SqlRestaurant.selectMenuInfo", map);
		session.close();

		JSONArray jsonArray = JSONArray.fromObject(list);

		map = new HashMap<String, Object>();
		map.put("menu", jsonArray);

		JSONObject jsonObject = JSONObject.fromObject(map);
		System.out.println("json - " + jsonObject.toString());
		return jsonObject.toString();
	}


}
