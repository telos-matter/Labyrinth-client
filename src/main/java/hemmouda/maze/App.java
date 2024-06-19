package hemmouda.maze;

import de.fhac.mazenet.server.generated.ClientRole;
import de.fhac.mazenet.server.generated.LoginMessageData;
import de.fhac.mazenet.server.generated.MazeCom;
import de.fhac.mazenet.server.generated.MazeComMessagetype;
import de.fhac.mazenet.server.networking.UTFOutputStream;
import de.fhac.mazenet.server.networking.XmlInputStream;
import de.fhac.mazenet.server.networking.XmlOutputStream;
import hemmouda.maze.communication.xml.Serializer;

import javax.xml.stream.XMLOutputFactory;
import java.io.*;
import java.net.Socket;

public interface App {

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 5123);
        System.out.println("Connected");

        var in = new XmlInputStream(socket.getInputStream());
        var out = new XmlOutputStream(socket.getOutputStream());

        LoginMessageData loginMessageData = new LoginMessageData();
        loginMessageData.setName("PP wiiii");
        loginMessageData.setRole(ClientRole.PLAYER);
        MazeCom com = new MazeCom();
        com.setLoginMessage(loginMessageData);
        com.setMessagetype(MazeComMessagetype.LOGIN);
        com.setId(-5);

        out.write(com);
        System.out.println("Sent");

        var response = in.readMazeCom();
        System.out.println("Read: "+response);
        System.out.println(response.getMessagetype());


//        var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        var out = new PrintWriter(socket.getOutputStream());
//        UTFOutputStream utfOutputStream = new UTFOutputStream(socket.getOutputStream());
//        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
//        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
//        Serializer serializer = new Serializer();
//
//        var msg = serializer.messageToXMLString(com);
////        out.write(msg);
////        out.flush();
////        utfOutputStream.writeUTF8(msg);
////        utfOutputStream.flush();
//        dataOutputStream.writeUTF(msg);
//        dataOutputStream.flush();
//
//        System.out.println("Sent: " +msg);
////        var line = in.readLine();
//        var line = dataInputStream.readUTF();
//        System.out.println("Read line");
//        System.out.println(line);
    }

}
