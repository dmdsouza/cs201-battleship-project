

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/echo")
public class random_matchmaking {
	private volatile static Vector<Session> existingSessions = new Vector<Session>();
	private Map<String, Session> GameSessions = new HashMap<String, Session>();
	private Map<Session, Boolean> paired = new HashMap<Session,Boolean>();
	
	@OnOpen
	public void open(Session session) {
		System.out.println("hi");
		session.getUserProperties().put("paired", false);
		synchronized (existingSessions) {
			for(Session activeSession : existingSessions)
			{
				if(activeSession.isOpen() && activeSession.getUserProperties().get("paired").equals(false))
				{
					synchronized (GameSessions) {
						GameSessions.put(activeSession.getId(), session);
						GameSessions.put(session.getId(), activeSession);
						session.getUserProperties().put("paired",true);
						activeSession.getUserProperties().put("paired",true);						
					}
				}
				break;				
			}
			existingSessions.add(session);
		}
		if(session.getUserProperties().get("paired").equals(true))
		{
			//Sending the start message to both players
			
			String message1 = "{\"game\":\"start\",\"player\":1}";
			String message2 = "{\"game\":\"start\",\"player\":2}";
			Session otherSession = GameSessions.get(session);
		
			session.getAsyncRemote().sendText(message1);
		    otherSession.getAsyncRemote().sendText(message2);
		}
		synchronized (GameSessions) {
			for(Map.Entry<String,Session> entry : GameSessions.entrySet()) {
				System.out.println(entry.getKey());
				System.out.println(entry.getValue().getId());
			}
		}
		
		
	}
	
	
	@OnClose
	public void close(Session session) {
		synchronized (GameSessions) {
			Session opponent = GameSessions.get(session);
			GameSessions.remove(session);
			GameSessions.remove(opponent);
			synchronized (existingSessions) {
				existingSessions.remove(opponent);
				existingSessions.remove(session);	
			}						
		}
			
		System.out.println("Disconnecting!");
	}
	
	@OnMessage
	public void onMessage(String message, Session session) {
		//try {
		System.out.println(message);
		synchronized (GameSessions) {
			for(Map.Entry<String,Session> entry : GameSessions.entrySet()) {
				System.out.println(entry.getKey());
				System.out.println(entry.getValue().getId());
			}
		}
			synchronized (GameSessions) {
				System.out.println(session.getId());
				if(GameSessions.get(session).isOpen())
				{
					GameSessions.get(session).getAsyncRemote().sendText(message);
				}				
			}
	//	} 
		//catch (IOException ioe) {
		//	System.out.println("ioe: " + ioe.getMessage());
//			synchronized (GameSessions) {
//				// Sending game over String
//				String quote = "\"";
//				String msg = "{" + quote + "game" + quote + ":" + quote + "over" + quote + "," +
//						quote + "win" + quote + ":" + quote + "yes" + quote +"}";
//
//				try {
//					Session opponent = GameSessions.get(session);
//					opponent.getBasicRemote().sendText(msg);
//				} 
//				catch (IOException ioe2) {
//					Session opponent = GameSessions.get(session);
//					System.out.println("ioe: " + ioe2.getMessage());
//					close(GameSessions.get(opponent));
//				}
//			}
	//		close(session);
	//	}
	}	

	@OnError
	public void error(Session s, Throwable error) {
		//Do nothing. Log the Error
		System.out.println(error.getMessage());
	}
}
