package battleship_game;
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
	private static Map<String, String> GameSessions = new HashMap<String, String>();
	private static Map<Session, Boolean> paired = new HashMap<Session,Boolean>();
	
	@OnOpen
	
	public void open(Session session) {
		session.getUserProperties().put("paired", false);
		synchronized (existingSessions) {
			for(Session activeSession:existingSessions) {
				if(activeSession.isOpen() && activeSession.getUserProperties().get("paired").equals(false)){
					synchronized(GameSessions) {
						GameSessions.put(activeSession.getId(),session.getId());
						GameSessions.put(session.getId(),activeSession.getId());
						session.getUserProperties().put("paired",true);
						activeSession.getUserProperties().put("paired",true);
					}
					break;
				}
			}
			existingSessions.add(session);
		}
		if(session.getUserProperties().get("paired").equals(true))
		{
			//Sending the start message to both players
			String message1 = "{\"game\":\"start\",\"player\":1}";
			String message2 = "{\"game\":\"start\",\"player\":2}";
			String otherSession = GameSessions.get(session.getId());
			
			session.getAsyncRemote().sendText(message1);
			synchronized(existingSessions) {
				for(Session s:existingSessions) {
					if(s.getId().equals(otherSession)) {
						s.getAsyncRemote().sendText(message2);
					}
				}
			}
		}   

	}	
	
	@OnClose
	public void close(Session session) {
		synchronized (GameSessions) {
			
			String opponent = GameSessions.get(session.getId());
			GameSessions.remove(session.getId());
			GameSessions.remove(opponent);
			synchronized (existingSessions) {
				Session y=null;
				for(Session s:existingSessions) {
					if(s.getId().equals(opponent)) {
						y=s;
					}
				}	
				existingSessions.remove(y);	
				existingSessions.remove(session);	
			}						
		}
		String message1 = "{\"game\":\"over\"}";
		session.getAsyncRemote().sendText(message1);
			

	}
	
	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println(message);
		synchronized (existingSessions) {
			synchronized (GameSessions) {
				String opponent = GameSessions.get(session.getId());
				for(Session s:existingSessions) {
					if(s.getId().equals(opponent)) {

							s.getAsyncRemote().sendText(message);

					}
				}		
			}
		}
			
	}	

	@OnError
	public void error(Session s, Throwable error) {
		//Do nothing. Log the Error
		System.out.println(error.getMessage());
	}
}