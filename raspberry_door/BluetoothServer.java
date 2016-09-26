
import java.io.*;
import java.net.*;
import java.util.Scanner;

import javax.bluetooth.*;
import javax.microedition.io.*;
 
/**
* Class that implements an SPP Server which accepts single line of
* message from an SPP client and sends a single line of response to the client.
*/
public class BluetoothServer {

	String errorNum="0";
 
    //start server
    private void startServer() throws IOException{

		 String resetcmd = "sudo hciconfig hci0 reset";
		System.out.println("reset");

		try
		{
		Process pb = Runtime.getRuntime().exec(resetcmd);
		pb.waitFor();

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}




		 String cmdf = "sudo hciconfig hci0 piscan";
		System.out.println("piscan start");

		try
		{
		Process pb = Runtime.getRuntime().exec(cmdf);
		pb.waitFor();
		if(pb.exitValue()!=0)
		{	
			if(errorNum.equals("0"))
			errorNum="1";
		}
		OutputStream out = pb.getOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
		//bw.write("connect\n");
		//bw.flush();
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

        //Create a UUID for SPP
        UUID uuid = new UUID("1101", true);
        //Create the servicve url
        String connectionString = "btspp://localhost:" + uuid +";name=Sample SPP Server";
 
        //open server url
        StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier)Connector.open( connectionString );
 
        System.out.println("\nServer Started. Waiting for clients to connect...");
        StreamConnection connection=streamConnNotifier.acceptAndOpen();
 
        RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
        System.out.println("Remote device address: "+dev.getBluetoothAddress());
        System.out.println("Device is Connected!!");
        InetAddress local = InetAddress.getLocalHost();
        
        
        OutputStream outStream=connection.openOutputStream();
        PrintWriter pWriter=new PrintWriter(new OutputStreamWriter(outStream));
        pWriter.write(local.getHostAddress());
        pWriter.flush();
        
        //pWriter.close();
        //streamConnNotifier.close();
        
        InputStream inStream=connection.openInputStream();
        BufferedReader bReader=new BufferedReader(new InputStreamReader(inStream));
        String lineRead=bReader.readLine();
        System.out.println("door lock MackAddress:"+lineRead);
        
        
        String publicIp=bReader.readLine();
        System.out.println("Received PublicIP:"+publicIp);
        
        String SSID=bReader.readLine();
        System.out.println("Received SSID :"+SSID);
        
        String pwd=bReader.readLine();
        System.out.println("Received pwd:"+pwd);


	   String cmd = "sudo python door3.py " + lineRead;
		System.out.println("start");

		try
		{
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

		
		
		
		conWifi con = new conWifi(SSID,pwd,errorNum,pWriter);
		con.start();
		try{
		con.join();
		}
		catch(Exception e)
		{ e.printStackTrace();}
		errorNum=con.getErrorNum();





		writeDB wd = new writeDB(lineRead,publicIp,errorNum,pWriter);
		wd.start();
		
		try
		{
		wd.join();
		}
		catch(Exception e)
		{e.printStackTrace();}
 		
    }
    
 
 
    public static void main(String[] args) throws IOException {
 
        //display local device address and name
        LocalDevice localDevice = LocalDevice.getLocalDevice();
        System.out.println("Address: "+localDevice.getBluetoothAddress());
        System.out.println("Name: "+localDevice.getFriendlyName());
        
        BluetoothServer sampleSPPServer=new BluetoothServer();
        sampleSPPServer.startServer();
 
    }
}

class conWifi extends Thread
{
	String SSID="";
	String pwd="";
	String errorNum="";
	PrintWriter pWriter;
	conWifi(String SSID,String pwd,String errorNum,PrintWriter pWriter)
	{
		this.SSID=SSID;
		this.pwd=pwd;
		this.errorNum=errorNum;
		this.pWriter=pWriter;
	}

	public String getErrorNum()
	{
		return errorNum;
	}
	public void run()
	{
		String conWifi="sudo wpa_cli";
		try
		{
		Process pb = Runtime.getRuntime().exec(conWifi);

		
		OutputStream out = pb.getOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
		//bw.write("connect\n");
		//bw.flush();


		String line;
		BufferedReader input = new BufferedReader(new InputStreamReader(pb.getInputStream()));
		
	
		bw.write("add_network\n");
		bw.flush();
		String networkNumb="";
		while((line=input.readLine())!=null)
		{
			System.out.println(line);
			if(line.equals(""))
				continue;
			if(line.charAt(0)>='0'&&line.charAt(0)<='9')
				{
				networkNumb=line;
				break;
				}
		}
		bw.write("set_network "+networkNumb+" ssid " +SSID+"\n");
		bw.flush();
		bw.write("set_network "+networkNumb+" psk " +"\""+pwd+"\"\n");
		bw.flush();
		bw.write("select_network "+networkNumb+"\n");
		bw.flush();
		
		int count=0;
		int i=0;
		while(i<20)
		{
			i++;
			line=input.readLine();
			System.out.println(line);
			if(line.equals("FAIL"))
			{
				if(errorNum.equals("0"))
				{
					errorNum="3";
				  pWriter.write(errorNum);
        		  pWriter.flush();
				  System.exit(0);
				}
			}
		}
		input.close();

		}
		catch(Exception e)
		{
			if(errorNum.equals("0"))
			errorNum="3";
			e.printStackTrace();
		}
	
	}
}

class writeDB extends Thread
{
	String lineRead="";
	String IpAddress="";
	String errorNum="";
	PrintWriter pWriter;
	String str="";
	String publicip="";
	String privateip="";
	writeDB(String lineRead,String IpAddress,String errorNum,PrintWriter pWriter)
	{
		this.lineRead=lineRead;
		this.IpAddress=IpAddress;
		this.errorNum=errorNum;
		this.pWriter=pWriter;
	}
	public String getErrorNum()
	{
		return errorNum;
	}
	public String getString()
	{
		return str;
	}
	
	public void run()
	{

		try
		{
			sleep(13000);
		}catch(Exception e)
		{
		}

/*
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

		int ok=0;

		try
		{
		Process pbcheck = Runtime.getRuntime().exec("sudo hcitool con");
		

		pbcheck.waitFor();
		
		
		String line;
		BufferedReader input = new BufferedReader(new InputStreamReader(pbcheck.getInputStream()));
		
		

		while((line=input.readLine())!=null)
		{
			System.out.println(line);
			if(line.contains(lineRead))
				ok=1;
		}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		if(ok==0)
		{
			if(errorNum.equals("0"))
			  {
				errorNum="2";
				  pWriter.write(errorNum);
        		  pWriter.flush();
				  System.exit(0);
			  }
		}
*/



		

		
		//get ip part
		//
		//

		

		try
		{
		Process pb = Runtime.getRuntime().exec("curl http://ipecho.net/plain");
		pb.waitFor();
		
		OutputStream out = pb.getOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

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
		String[] cmd = {"/bin/sh","-c","ifconfig -a | grep inet | grep Bcast: | awk \'{print $2}\' | awk -F: \'{print $2}\'"};
		Process pb = Runtime.getRuntime().exec(cmd);
		pb.waitFor();
		
		OutputStream out = pb.getOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

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
		Process pb = Runtime.getRuntime().exec("upnpc -a "+privateip+" 8080 1111 TCP");
		
		OutputStream out = pb.getOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

		}
		catch(Exception e)
		{
		}



		Socket sock;
		OutputStream out;
		InputStream in;
		PrintWriter pw;
		BufferedReader br;

	
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
			while(sc.hasNext())
			{
				str = sc.nextLine();
				System.out.println((line++)+":"+str);
			}
			sc.close();
			}catch (Exception e) {
			if(errorNum.equals("0"))
			{
				errorNum="4";
				pWriter.write(errorNum);
        		  pWriter.flush();
				  System.exit(0);
			}
			// TODO: handle exception
			e.printStackTrace();
		}




		System.out.println("ErrorNumber:"+errorNum);
		
		if(getString().equals("100"))
		{
        pWriter.write(errorNum);
        pWriter.flush();
		}
		else
		{
		System.out.println("already registed!");
		pWriter.write("5");
		pWriter.flush();
		System.exit(0);
		}

		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter("info.txt"));
			System.out.println("MAC"+lineRead);
			writer.write(lineRead+"\n");
			writer.flush();
			writer.write(publicip+"\n");
			writer.flush();
			writer.close();
		}catch(Exception e)
		{
		}



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
				
				}
			}
			
			else if(line.equals("Off"))
			{
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
			}

			else if(line.equals("remove"))
			{
				try
				{
				Process pbe = Runtime.getRuntime().exec("rm info.txt");
				System.out.println("file deleted\n");
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
		
		
	}
}
