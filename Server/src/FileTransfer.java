import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class FileTransfer {
	static void get(File file) throws Exception{
		ServerSocket socket = new ServerSocket(7776);
		Socket client = socket.accept();
		FileOutputStream fout = new FileOutputStream(file);
		DataInputStream in = new DataInputStream(client.getInputStream());
		byte b[] = new byte[1024];
		while(in.read(b)!=-1){
			fout.write(b);
			fout.flush();
		}
		fout.close();
		socket.close();
	}
}
