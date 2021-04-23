

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
	private Map<String, String> GameSessions = new HashMap<String, String>();
	private Map<Session, Boolean> paired = new HashMap<Session,Boolean>();
	private Session player1 = null;
	private Session player2 = null;
	@OnOpen
	
	public void open(Session session) {
		synchronized (existingSessions) {
			existingSessions.add(session);
			System.out.println("hi2");
			if(existingSessions.size()==2)
			{
				//Sending the start message to both players
				System.out.println("hi3");
				String message1 = "{\"game\":\"start\",\"player\":1}";
				String message2 = "{\"game\":\"start\",\"player\":2}";
				existingSessions.get(0).getAsyncRemote().sendText(message1);
				existingSessions.get(1).getAsyncRemote().sendText(message2);
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
			
		System.out.println("Disconnecting!");
	}
	
	@OnMessage
	public void onMessage(String message, Session session) {
		if(session.getId().equals(existingSessions.get(0).getId())) {
			existingSessions.get(1).getAsyncRemote().sendText(message);
		}else {
			existingSessions.get(0).getAsyncRemote().sendText(message);
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
