
import java.io.*;
import java.net.*;
import java.util.Scanner;


public class ip
{
	public static void main(String args[])
	{
		try
		{
		Process pb = Runtime.getRuntime().exec("curl http://ipecho.net/plain");

		
		OutputStream out = pb.getOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

		String line;
		BufferedReader input = new BufferedReader(new InputStreamReader(pb.getInputStream()));
		
		line = input.readLine();
		System.out.println(line);
		}
		catch(Exception e)
		{
		}

		try
		{
		String[] cmd = {"/bin/sh","-c","ifconfig -a | grep inet | grep Bcast: | awk \'{print $2}\' | awk -F: \'{print $2}\'"};
		Process pb = Runtime.getRuntime().exec(cmd);

		
		OutputStream out = pb.getOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

		String line;
		BufferedReader input = new BufferedReader(new InputStreamReader(pb.getInputStream()));
		
		line = input.readLine();
		System.out.println(line);
		}
		catch(Exception e)
		{
		}


		
	}
}