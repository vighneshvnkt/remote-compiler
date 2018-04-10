import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class Server {
	public static void main(String arg[]){
		try{
			ServerSocket socket = new ServerSocket(7777);
			File tempDir = new File("temp");
			tempDir.mkdir();
			
			//Thread to stop server
			new Thread(){
				public void run(){
					try{
						System.out.println("Press enter to exit");
						BufferedReader b = new BufferedReader(
								new InputStreamReader(System.in));
						b.read();
						socket.close();
						String[]entries = tempDir.list();
						for(String s: entries){
						    File currentFile = new File(tempDir.getPath(),s);
						    currentFile.delete();
						}
						tempDir.delete();
						System.exit(0);
					}catch(Exception ex){}
				}
			}.start();
			
			//Accept incoming requests
			while(true){
				System.out.println("Waiting for connection");
				Socket client = socket.accept();
				System.out.println("Client connected");
				DataInputStream in = new DataInputStream(client.getInputStream());
				DataOutputStream out = new DataOutputStream(client.getOutputStream());
				String fileName = in.readUTF();
				System.out.println(fileName);

				File file = new File("temp/"+fileName);
				if(!file.exists())file.createNewFile();
				file.deleteOnExit();
				FileTransfer.get(file);

				System.out.println("compiling..");
				
				Process p;
				if(fileName.substring(fileName.lastIndexOf(".")+1).equals("c"))
				p = Runtime.getRuntime().exec("gcc temp/"+fileName +" -o temp/"+
						fileName.substring(0,fileName.lastIndexOf('.'))+" -w");
				else p = Runtime.getRuntime().exec("g++ temp/"+fileName +" -o temp/"+
						fileName.substring(0,fileName.lastIndexOf('.'))+" -w");
				
				//Check if error message has been received
				String s=null,output = "";
				String errMsg="";
				boolean error=false;
				BufferedReader stdError = new BufferedReader(
						new InputStreamReader(p.getErrorStream()));
				while ((s = stdError.readLine()) != null) {
					errMsg+=s+"\n";
					error=true;
				}
				if(error){
					System.out.println("Error: "+errMsg);
					out.writeUTF(errMsg);
				}
				else
				{
					System.out.println("Executing compiled file: "+"temp/"+
							fileName.substring(0,fileName.lastIndexOf('.')));
					//Execute the compiled file
					p=Runtime.getRuntime().exec("temp/"+
							fileName.substring(0,fileName.lastIndexOf('.')));

					//Provide input if any
					PrintWriter writer = new PrintWriter(p.getOutputStream());
					writer.write(in.readUTF());
					writer.close();

					//Get output 
					BufferedReader stdin = new BufferedReader(
							new InputStreamReader(p.getInputStream()));
					while ((s = stdin.readLine()) != null) {
						output+=s+"\n";
					}
					System.out.println("Output:\n"+output);
					out.writeUTF(output);
				}
			}
		}catch(Exception ex){}
	}
}
