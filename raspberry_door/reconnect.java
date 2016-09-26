

import java.net.*;
import java.util.Scanner;
import java.io.*;

public class reconnect
{

	public static void main(String args[])
	{
	File aFile = new File("info.txt");
	try
	{
		
		FileReader fileReader = new FileReader(aFile);
		BufferedReader reader = new BufferedReader(fileReader);

		String lineRead = reader.readLine();
		String IpAddress = reader.readLine();
		reader.close();
		

		 String cmd = "sudo python door3.py " + lineRead;

		try
		{

		Thread.sleep(3000);


		Process pb = Runtime.getRuntime().exec(cmd);
		

		/*pb.waitFor();
		if(pb.exitValue()!=0)
		{	
			if(errorNum.equals("0"))
			  {
				errorNum="2";
				  pWriter.write(errorNum);
        		  pWriter.flush();
				  System.exit(0);
			  }
			
		}*/
		
		OutputStream out = pb.getOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
		//bw.write("connect\n");
		//bw.flush();
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}


		Socket sock;
		OutputStream out;
		InputStream in;
		PrintWriter pw;
		BufferedReader br;




		String publicip="";
		String privateip="";
	
		///getIpPart
		try
		{
		Process pb = Runtime.getRuntime().exec("curl http://ipecho.net/plain");
		pb.waitFor();
		
		OutputStream outs = pb.getOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outs));

		String line;
		BufferedReader input = new BufferedReader(new InputStreamReader(pb.getInputStream()));
		
		publicip = input.readLine();
		System.out.println(publicip);
		}
		catch(Exception e)
		{
		}

		try
		{
		String[] cmds = {"/bin/sh","-c","ifconfig -a | grep inet | grep Bcast: | awk \'{print $2}\' | awk -F: \'{print $2}\'"};
		Process pb = Runtime.getRuntime().exec(cmds);
		pb.waitFor();
		
		OutputStream outs = pb.getOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outs));

		BufferedReader input = new BufferedReader(new InputStreamReader(pb.getInputStream()));
		
		privateip = input.readLine();
		System.out.println(privateip);
		}
		catch(Exception e)
		{
		}

		//get ip part
		//
		//


		try
		{
		Process pb2 = Runtime.getRuntime().exec("upnpc -d 1111 TCP");
		Process pb = Runtime.getRuntime().exec("upnpc -a "+privateip+" 8080 1111 TCP");
		
		OutputStream outs = pb.getOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outs));

		}
		catch(Exception e)
		{
		}


		try
		{
			SocketAddress sa = new InetSocketAddress("117.16.244.105",10001);
			sock = new Socket();
			sock.connect(sa, 0);
			sock.setKeepAlive(true);
			BufferedReader keyboard= new BufferedReader(new InputStreamReader(System.in));
		
			out = sock.getOutputStream();
			in = sock.getInputStream();


			pw = new PrintWriter(new OutputStreamWriter(out));

			br = new BufferedReader(new InputStreamReader(in));

			pw.write("1\n");
			pw.flush();

			String port = br.readLine();
			


			//Here is the DB Regist Part;
			try {	
			URL url = new URL("http://winlab.incheon.ac.kr/door/db_insert_device.php?IP="+publicip+"&MAC="+lineRead+"&PORT="+port);
			
			URLConnection conn =url.openConnection();
			conn.setConnectTimeout(3000);
			conn.setUseCaches(false);
			
			int line =1;
			InputStream is = conn.getInputStream();
			Scanner sc = new Scanner(is);
			String str;
			while(sc.hasNext())
			{
				str = sc.nextLine();
				System.out.println((line++)+":"+str);
			}
			sc.close();
			}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}


		try{
				URL url=new URL("http://127.0.0.1:8888/24/off");
				URLConnection conn=url.openConnection();
				conn.setUseCaches(false);
				conn.connect();

				InputStream is = conn.getInputStream();
				is.close();
				}
				catch(Exception e)
				{
				
				}



		//here is the 
		while(true)
		{
			String line=br.readLine();
			System.out.println(line);

			if(line.equals("On"))
			{
				try{
				URL url=new URL("http://127.0.0.1:8888/24/on");
				URLConnection conn=url.openConnection();
				conn.setUseCaches(false);
				conn.connect();

				InputStream is = conn.getInputStream();
				is.close();
				}
				catch(Exception e)
				{
				System.out.println("dd");
				}
			}
			else if(line.equals("Off"))
			{
				try
				{
					URL url = new URL("http://127.0.0.1:8888/24/off");
					URLConnection conn = url.openConnection();
					conn.setUseCaches(false);
					conn.connect();
				
					InputStream is = conn.getInputStream();
					is.close();
				}
				catch(Exception e)
				{
				System.out.println("dd");
				}
			}
			else if(line.equals("remove"))
			{
				try
				{
				Process pbe = Runtime.getRuntime().exec("rm info.txt");
				pbe.waitFor();
				}catch(Exception e)
				{
				}
				sock.close();
				System.exit(0);
			}
		}
	}catch(Exception e)
	{
	}
}catch(Exception e)
{}
}
}
