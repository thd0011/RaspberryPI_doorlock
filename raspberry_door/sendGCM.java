import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;


public class sendGCM extends Thread{
	String mac, msg;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String mac;
		String msg;
		if(args.length<2) {
			Scanner in = new Scanner(System.in);
			mac = in.nextLine();
			msg = in.nextLine();
		} else {
			mac = args[0];
			msg = args[1];
			System.out.println(args[0]);
			System.out.println(args[1]);
		}

		if(msg.equals("On")) msg = "DoorLock is opened.";
		else if(msg.equals("Off")) msg = "DoorLock is closed.";

		sendGCM s = new sendGCM(mac, msg);
		
		s.start();
	}
	
	public sendGCM(String mac, String msg) {
		this.mac = mac;
		this.msg = msg;
	}

	public void run() {
		Sender sender = new Sender("AIzaSyClOE-NjlTy3RfQpQOn55Fb22Az2doMuuw");  
		
		try {
			msg = URLEncoder.encode(msg, "EUC-KR");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Message message = new Message.Builder().addData("msg", msg)
				.build();
		List<String> list = getTokens();
		try {
			if(list.size()!=0) sender.send(message, list, 5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private List<String> getTokens() {
		List<String> ret = new ArrayList<String>();
		String str = "http://winlab.incheon.ac.kr/door/db_search_regid.php?MAC="+mac;
		try {
			URL url = new URL(str);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			
			String inputLine;
			System.out.println("search "+mac);	
			while((inputLine = in.readLine())!= null) {
				ret.add(inputLine);
				System.out.println(inputLine);
			}
			
			return ret;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
