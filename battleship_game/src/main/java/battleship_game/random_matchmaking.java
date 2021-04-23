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
	private Map<Session, Session> GameSessions = new HashMap<Session, Session>();
	
	
	@OnOpen
	public void open(Session session) {
		session.getUserProperties().put("paired", false);
		synchronized (existingSessions) {
			for(Session activeSession : existingSessions)
			{
				if(activeSession.isOpen() && activeSession.getUserProperties().get("paired").equals(false))
				{
					synchronized (GameSessions) {
						GameSessions.put(activeSession, session);
						GameSessions.put(session, activeSession);
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
			
			String message1 = "{game:start,player:1}";
			String message2 = "{game:start,player:2}";
			Session otherSession = GameSessions.get(session);
			try {
				session.getBasicRemote().sendText(message1);
			} 
			catch (IOException ioe) {
				System.out.println("ioe: " + ioe.getMessage());
				close(session);
			}
			try {
				otherSession.getBasicRemote().sendText(message2);
			} 
			catch (IOException ioe) {
				System.out.println("ioe: " + ioe.getMessage());
				close(GameSessions.get(otherSession));
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
		try {
			synchronized (GameSessions) {
				Session opponent = GameSessions.get(session);
				if(opponent.isOpen())
				{
					opponent.getBasicRemote().sendText(message);
				}				
			}
		} 
		catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
			synchronized (GameSessions) {
				// Sending game over String
				String msg = "{game:over,win:yes}";
				try {
					Session opponent = GameSessions.get(session);
					opponent.getBasicRemote().sendText(msg);
				} 
				catch (IOException ioe2) {
					Session opponent = GameSessions.get(session);
					System.out.println("ioe: " + ioe2.getMessage());
					close(GameSessions.get(opponent));
				}
			}
			close(session);
		}
	}	

	@OnError
	public void error(Session s, Throwable error) {
		//Do nothing. Log the Error
		System.out.println("Error!");
	}
}