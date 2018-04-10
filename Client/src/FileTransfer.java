import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;


public class FileTransfer {
	static void transfer(File f)throws Exception{
		Socket socket = new Socket(Client.HOST, 7776);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		FileInputStream fin = new FileInputStream(f);
        byte b[] = new byte[1024];
        while(fin.read(b)!=-1){
        	out.write(b);
        	out.flush();
        }
        fin.close();
        socket.close();
	}
}
