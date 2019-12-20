/*
 * �쐬��: 2009/01/22
 *
 * �E�B���h�E - �ݒ� - Java - �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
 */
package myGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author hx0308
 *
 * �E�B���h�E - �ݒ� - Java - �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
 */
public class testMsg
{
    /**
     * <code>LISTEN_PORT</code> �̃R�����g
     */
    private static int LISTEN_PORT = 7474;
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Socket socket = null;
        try
        {
            socket = new Socket(InetAddress.getLocalHost().getHostAddress(), LISTEN_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            
            for (int i = 0; i < 2; i++)
            {
                out.println("hello" + i);
            }
            out.flush();

            String str = "";
            String line = null;
            while ((line = in.readLine()) != null)
            {
                str = str + line;
            }

            System.out.println("Client In:" + str);
            in.close();
            out.close();
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                socket.close();
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
    }
}
