package smpp4j.util;

import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smpp4j.pdu.Command;

public class SocketUtil {
	
	private static final Logger log = LoggerFactory.getLogger(SocketUtil.class);

	public static Socket openSocket(String host, int port, int socketTimeoutMillis) {
		try {
			Socket socket = new Socket();	
			socket.connect(new InetSocketAddress(host, port), socketTimeoutMillis);
			socket.setSoTimeout(socketTimeoutMillis); // in milliseconds
			if (socket.isBound() && socket.isConnected()) {
				log.info("Socket open host: {} port: {}", host, port);
				return socket;
			}
		} catch (Exception e) {
			log.error("While oppening socket at "+host+":"+port+": "+e.getMessage(), e);
		}
		
		return null;
	}
	
	public static void tearDownSocket(Socket socket) {
		if (socket == null) {
			log.info("Socket teared down");
			return;
		}
		
		try { socket.shutdownOutput(); } catch (Exception ignore) { }
		try { socket.shutdownInput(); } catch (Exception ignore) { }
		try { socket.close(); } catch (Exception ignore) { }
		
		log.info("Socket teared down");
	}
	
}
