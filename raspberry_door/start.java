

import java.net.*;
import java.util.Scanner;
import java.io.*;

public class start
{
		static BufferedReader aread;
		public static void main(String args[])
		{


			try
			{
			Process pb12 = Runtime.getRuntime().exec("./mjpg.sh");

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}




			File afile = new File("info.txt");
			if(afile.exists())
			{

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


			System.out.println("Checking isRegisted\nReconnect Start");
			try
			{
			Process pb = Runtime.getRuntime().exec("java reconnect");
			BufferedReader read = new BufferedReader(new InputStreamReader(pb.getInputStream()));
			aread=read;
			}catch(Exception e)
			{}
	
			
			Scanner sc = new Scanner(System.in);

			Thread t = new Thread()
			{
				public void run()
				{
					String l;
					try{
					while((l=aread.readLine())!=null)
					{
					System.out.println(l);
					}
					}catch(Exception e)
					{}
					
				}
			};
			t.start();

			System.out.println("If you want to reset, Press 5");
			while(true)
			{
				int numb=sc.nextInt();
				if(numb==5)
				{
					File aFile = new File("info.txt");

					try
					{
						

						FileReader fileReader = new FileReader(aFile);
						BufferedReader reader = new BufferedReader(fileReader);

						String lineRead = reader.readLine();
						String IpAddress = reader.readLine();
						reader.close();
						System.out.println(lineRead);


						URL url = new URL("http://winlab.incheon.ac.kr/door/db_delete_device.php?MAC="+lineRead);
			
						URLConnection conn =url.openConnection();

						conn.setUseCaches(false);

						InputStream is = conn.getInputStream();
						
						System.out.println("delete URL excute");
						Process pbe = Runtime.getRuntime().exec("rm info.txt");
						pbe.waitFor();
					}
					catch(Exception e)
					{}
					
					System.exit(0);
				}
			}
			

			}





			System.out.println("Not Registed");
			System.out.println("For exit, input 3");
			Scanner sc = new Scanner(System.in);
			int numb;
		
				Thread t = new Thread()
				{
				public void run()
				{
				try
				{
				String cmd = "sudo java -cp bluecove-2.1.0.jar:bluecove-gpl-2.1.0.jar:. BluetoothServer";
				Process pb2 = Runtime.getRuntime().exec(cmd);

				BufferedReader input = new BufferedReader(new InputStreamReader(pb2.getInputStream()));
				String line;
				while((line=input.readLine())!=null)
				{
					System.out.println(line);
				}
				pb2.waitFor();
				}catch(Exception e)
				{}
				}
				};
				t.start();
				System.out.println("If you want to Exit press 3\n");
				while(true)
				{
					numb=sc.nextInt();
					if(numb==3)
					{
						System.exit(0);
					}
				}
			
		}
}
