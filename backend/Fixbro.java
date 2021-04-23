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
		System.out.println("hi1");
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
		System.out.println("hi2");
		if(session.getUserProperties().get("paired").equals(true))
		{
			//Sending the start message to both players
			System.out.println("hi3");
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
			System.out.println("hi4");
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
			
		System.out.println("Disconnecting!");
	}
	
	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println(message);
		synchronized (existingSessions) {
			synchronized (GameSessions) {
				String opponent = GameSessions.get(session.getId());
				for(Session s:existingSessions) {
					System.out.println("Current session: " +s.getId());
					System.out.println("opponent id: "+ opponent);
					if(s.getId().equals(opponent)) {
						try {
							System.out.println("sending");
							s.getBasicRemote().sendText(message);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("error2");
							e.printStackTrace();
						}
					}
				}		
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
