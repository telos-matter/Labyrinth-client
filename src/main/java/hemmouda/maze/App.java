package hemmouda.maze;

import de.fhac.mazenet.server.generated.ClientRole;
import de.fhac.mazenet.server.generated.LoginMessageData;
import de.fhac.mazenet.server.generated.MazeCom;
import de.fhac.mazenet.server.generated.MazeComMessagetype;
import hemmouda.maze.communication.xml.Serializer;

import java.io.*;
import java.net.Socket;

public interface App {

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 5123);
        System.out.println("Connected");
//        var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        var out = new PrintWriter(socket.getOutputStream());
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        LoginMessageData loginMessageData = new LoginMessageData();
        loginMessageData.setName("PP");
        loginMessageData.setRole(ClientRole.PLAYER);
        MazeCom com = new MazeCom();
        com.setLoginMessage(loginMessageData);
        com.setMessagetype(MazeComMessagetype.LOGIN);
        com.setId(-5);
        Serializer serializer = new Serializer();

        var msg = serializer.messageToXMLString(com);
//        out.write(msg);
//        out.flush();
        dataOutputStream.writeUTF(msg);
        dataOutputStream.flush();

        System.out.println("Sent: " +msg);
//        var line = in.readLine();
        var line = dataInputStream.readUTF();
        System.out.println("Read line");
        System.out.println(line);
    }

}
