import java.io.*;
import java.net.*;
import java.util.Scanner;

public class getPhoneNumber
{
	public static void main(String[] args)
	{

		//Here is the DB Regist Part;
			try {	
			URL url = new URL("http://winlab.incheon.ac.kr/door/user_phone.php?MAC="+args[0]);
			
			URLConnection conn =url.openConnection();
			conn.setConnectTimeout(3000);
			conn.setUseCaches(false);
			
			InputStream is = conn.getInputStream();
			Scanner sc = new Scanner(is);
			String str="";
			while(sc.hasNext())
			{
				str = sc.nextLine();
				System.out.println(str);
			}
				sc.close();
			}catch (Exception e) {
			
			e.printStackTrace();
			}


	}
}
