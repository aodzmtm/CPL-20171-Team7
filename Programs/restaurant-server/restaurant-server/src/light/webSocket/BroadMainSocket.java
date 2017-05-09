package light.webSocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import common.database.MyBatisSessionFactory;
import light.vo.RequestVO;
import light.vo.ReplyVO;
import light.vo.EventVO;
import light.vo.HistoryVo;
import light.vo.LampVo;
import light.parsing.EventParsing;
import light.parsing.RequestParsing;


@ServerEndpoint("/echo.do")
public class BroadMainSocket {
	public static  BroadMainSocket instance;
	
	@Autowired
	private SqlSession sqlSession = MyBatisSessionFactory.getSqlSession(); 		
	
	public  BroadMainSocket(){
		instance=this;
	}
	
	public BroadMainSocket getInstance () {
		
		return instance;
	}
	
	private static Set<Session> webClients = Collections 
			 			.synchronizedSet(new HashSet<Session>()); 
	
	private static Set<Session> clients = Collections 
 			.synchronizedSet(new HashSet<Session>()); 

	@OnMessage
	public void onMessage(String message, Session session) throws IOException { 
//				System.out.println(message);
		
		 		if(message.equals("web"))
		 		{
		 			webClients.add(session);
		 			clients.remove(session);
		 			return;
		 		}
		 		
		 		synchronized (webClients) { 
		 			if(session == null)
			 		{	
				 		for (Session client : webClients) 
				 		{ 
				 			client.getBasicRemote().sendText(message); 	
				 		}
			 		}
		 			else
		 			{
		 				//��پ� �ڵ�
		 				//����� 1��1 ��� ��
		 				//���⼭ ������ ������
		 				try{
		 					System.out.println(message);
		 					if(message.length() >= 14 && message.charAt(13) == 'R'){
		 						RequestParsing requestParsing = new RequestParsing();
		 						RequestVO requestVO;
		 						// instruction == 'R' parsing
		 						requestVO = requestParsing.parsing(message);
		 					
		 						// ���� �ּҷ� ���̵� �о����.
		 						String beacon_id= (String)sqlSession.selectOne("ParsingMapper.selectBeaconID", requestVO);
		 						
		 						
		 						/*System.out.println("beacon_id:  " + beacon_id);
		 						System.out.println("VO beacon_id:  " + requestVO.getmACAddr());*/
		 						
		 						if(beacon_id != null){
		 							ReplyVO replyVO = new ReplyVO();
		 							replyVO.setsTX('2');
		 							replyVO.setDate_time(requestVO.getDate_time());
		 							replyVO.setInstruction('E');
		 							replyVO.setBeacon_id(beacon_id);
		 							replyVO.seteTX('3');
		 							//System.out.println(replyVO.replyMessage());
		 							session.getBasicRemote().sendText(replyVO.getsTX()+replyVO.getDate_time()+replyVO.getInstruction()+replyVO.getBeacon_id()+replyVO.geteTX());
		 						}
		 						else{
		 							System.out.println("Beacon address \"" + requestVO.getmACAddr() + "\" is not exist\n");
		 						}
		 						
		 					}
		 					else if(message.length() >= 14 && message.charAt(13) == 'E')
		 					{
		 						EventParsing eventParsing = new EventParsing();
		 						EventVO eventVO;
		 						// instruction == 'E' parsing
		 						eventVO = eventParsing.parsing(message);
		 						
		 						// ����� ���� ������Ʈ
		 						if(eventVO != null){
		 							// event�� ����� ��� ���¿� ��ġ�ϴ� ����� ������ select. 
		 							LampVo lampVO = sqlSession.selectOne("ParsingMapper.selectLampState", eventVO);
		 							
		 							int result = sqlSession.update("ParsingMapper.updateLampState", eventVO);
		 							System.out.printf("update result: %d", result);
		 							if(result > 0 ){
		 								System.out.println("commit ����");
		 								sqlSession.commit();
		 							}
			 					
		 							// ACK
		 							ReplyVO replyVO = new ReplyVO();
		 							replyVO.setsTX('2');
		 							replyVO.setDate_time(eventVO.getDate_time());
		 							replyVO.setInstruction('E');
		 							replyVO.setBeacon_id(eventVO.getBeacon_id());
		 							replyVO.seteTX('3');
		 							session.getBasicRemote().sendText(replyVO.getsTX()+replyVO.getDate_time()+replyVO.getInstruction()+replyVO.getBeacon_id()+replyVO.geteTX());
		 							
		 							// ����� event ���� DB���� �����ϴٸ� WebClinet�� �޽����� �������ش�.
		 							if(lampVO != null && occurModification(lampVO, eventVO)){
		 								for (Session client : webClients) 
		 						 		{ 
//		 						 			client.getBasicRemote().sendText("���ο� ���� ����"
//		 						 					+ "\nbeacon_id: " + eventVO.getBeacon_id()
//		 						 					+ "\npower_off: " + eventVO.getPower_off()
//		 						 					+ "\nabnormal_blink: " + eventVO.getAbnormal_blink()
//		 						 					+ "\nshort_circuit: " + eventVO.getShort_circuit()
//		 						 					+ "\nlamp_failure: " + eventVO.getLamp_failure()
//		 						 					+ "\nlamp_state: " + eventVO.getLamp_state()
//		 						 					+ "\nillumination: " + eventVO.getIllumination()
//		 									
//		 						 					);
		 									client.getBasicRemote().sendText(lampVO.getLocation()+" ���ȵ��� �����Ǿ����ϴ�.");
		 						 		}
		 								
		 								insertHistory(lampVO, eventVO);
		 							}
		 						}
		 					}
		 				}
		 				catch(Exception e){
		 					e.printStackTrace();
		 				}
		 			}
		 		}
		 	} 
	@OnOpen 
	 	public void onOpen(Session session) { 
	 		// Add session to the connected sessions set
//			System.out.println(session.getRequestURI().toString()+"\n");
	 		System.out.println("wServer:"+session); 
	 		clients.add(session);
	 	} 
	 
	@OnClose 
	 	public void onClose(Session session) { 
	 		// Remove session from the connected sessions set 
	 		webClients.remove(session); 
	 		clients.remove(session);
	 	}
	
	@OnError
		public void onError(Session session, Throwable throwable){
	
	}
	
	public boolean occurModification(LampVo lampVo, EventVO eventVo){
		if(lampVo.getPower_off().charAt(0) != eventVo.getPower_off())
			return true;
		if(lampVo.getAbnormal_blink().charAt(0) != eventVo.getAbnormal_blink())
			return true;
		if(lampVo.getShort_circuit().charAt(0) != eventVo.getShort_circuit())
			return true;
		if(lampVo.getLamp_failure().charAt(0) != eventVo.getLamp_failure())
			return true;
		if(lampVo.getLamp_state().charAt(0) != eventVo.getLamp_state())
			return true;
		if(lampVo.getIllumination().charAt(0) != eventVo.getIllumination())
			return true;
		
		return false;
	}
	
	public void insertHistory(LampVo lampVo, EventVO eventVo){
		HistoryVo historyVo = new HistoryVo();
		
		historyVo.setBeacon_addr(lampVo.getBeacon_addr());
		historyVo.setBeacon_id(eventVo.getBeacon_id());
		historyVo.setLocation(lampVo.getLocation());
		historyVo.setDate_time(eventVo.getDate_time());
//		System.out.println(historyVo.getLocation());
//		System.out.println(historyVo.getBeacon_id());
//		System.out.println(historyVo.getBeacon_addr());
//		System.out.println(historyVo.getDate_time());
		
		try{
			if (eventVo.getPower_off() == '1') {
				historyVo.setFailure_reason_id(1);
				historyVo.setFailure_reason_text("����");
				historyVo.setFailure_type("0");
				historyVo.setRepair("1");
				historyVo.setRecent("1");
				if (sqlSession.selectList("SqlMobileMapper.selectHistory", historyVo).isEmpty())
					sqlSession.insert("SqlMobileMapper.insertHistory", historyVo);
			}
			if (eventVo.getAbnormal_blink() == '1') {
				historyVo.setFailure_reason_id(2);
				historyVo.setFailure_reason_text("�̻�����");
				historyVo.setFailure_type("1");
				historyVo.setRepair("1");
				historyVo.setRecent("1");
				if (sqlSession.selectList("SqlMobileMapper.selectHistory", historyVo).isEmpty())
					sqlSession.insert("SqlMobileMapper.insertHistory", historyVo);
			}
			if (eventVo.getAbnormal_blink() == '2') {
				historyVo.setFailure_reason_id(3);
				historyVo.setFailure_reason_text("�̻�ҵ�");
				historyVo.setFailure_type("1");
				historyVo.setRepair("1");
				historyVo.setRecent("1");
				if (sqlSession.selectList("SqlMobileMapper.selectHistory", historyVo).isEmpty())
					sqlSession.insert("SqlMobileMapper.insertHistory", historyVo);
			}
			if (eventVo.getShort_circuit() == '1') {
				historyVo.setFailure_reason_id(4);
				historyVo.setFailure_reason_text("����");
				historyVo.setFailure_type("2");
				historyVo.setRepair("1");
				historyVo.setRecent("1");
				if (sqlSession.selectList("SqlMobileMapper.selectHistory", historyVo).isEmpty())
					sqlSession.insert("SqlMobileMapper.insertHistory", historyVo);
			}
			if (eventVo.getLamp_failure() == '1') {
				historyVo.setFailure_reason_id(5);
				historyVo.setFailure_reason_text("��������");
				historyVo.setFailure_type("3");
				historyVo.setRepair("1");
				historyVo.setRecent("1");
				if (sqlSession.selectList("SqlMobileMapper.selectHistory", historyVo).isEmpty())
					sqlSession.insert("SqlMobileMapper.insertHistory", historyVo);
			}
			if (eventVo.getLamp_failure() == '2') {
				historyVo.setFailure_reason_id(6);
				historyVo.setFailure_reason_text("������ ����");
				historyVo.setFailure_type("3");
				historyVo.setRepair("1");
				historyVo.setRecent("1");
				if (sqlSession.selectList("SqlMobileMapper.selectHistory", historyVo).isEmpty())
					sqlSession.insert("SqlMobileMapper.insertHistory", historyVo);
			}
			if (eventVo.getLamp_failure() == '3') {
				historyVo.setFailure_reason_id(7);
				historyVo.setFailure_reason_text("���� ������ ����");
				historyVo.setFailure_type("3");
				historyVo.setRepair("1");
				historyVo.setRecent("1");
				if (sqlSession.selectList("SqlMobileMapper.selectHistory", historyVo).isEmpty())
					sqlSession.insert("SqlMobileMapper.insertHistory", historyVo);
			}

			if (eventVo.getLamp_state() == '2') {
				historyVo.setFailure_reason_id(8);
				historyVo.setFailure_reason_text("�����ҵ�");
				historyVo.setFailure_type("4");
				historyVo.setRepair("1");
				historyVo.setRecent("1");
				if (sqlSession.selectList("SqlMobileMapper.selectHistory", historyVo).isEmpty())
					sqlSession.insert("SqlMobileMapper.insertHistory", historyVo);
			}
			if (eventVo.getLamp_state() == '3') {
				historyVo.setFailure_reason_id(9);
				historyVo.setFailure_reason_text("��������");
				historyVo.setFailure_type("4");
				historyVo.setRepair("1");
				historyVo.setRecent("1");
				if (sqlSession.selectList("SqlMobileMapper.selectHistory", historyVo).isEmpty())
					sqlSession.insert("SqlMobileMapper.insertHistory", historyVo);
			}
		}
		finally{
			sqlSession.commit();
		}
	}
}