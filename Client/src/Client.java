import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import java.util.Scanner;


public class Client {
	//Format: 
	//java Client MyHost <path-to-file>/add.c 2 5 
	
	static String HOST = "127.0.0.1";
	public static void main(String args[])throws Exception{
		if(args.length>=2)HOST = args[0];
		else {
			System.out.println("ERROR: Provide at least host and path as arguments!");
			System.exit(0);
		}
		Socket socket = new Socket(HOST, 7777);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());
        String path = args[1];
        File f = new File(path);
        out.writeUTF(f.getName());
        Thread.sleep(500);
        FileTransfer.transfer(f);
        String input = "";
        for(int i=2;i<args.length;i++)input+=args[i]+" ";
        out.writeUTF(input);
        System.out.println("wait...");
        String output = in.readUTF();
        System.out.println("Output:");
        System.out.println(output);
        socket.close();
	}
}
