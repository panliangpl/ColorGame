/*
 * 作成日: 2009/07/03
 *
 * ウィンドウ - 設定 - Java - コード・スタイル - コード・テンプレート
 */
package myGame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.utility.FreeLayout;

/**
 * @author hx0308
 *
 * ウィンドウ - 設定 - Java - コード・スタイル - コード・テンプレート
 */
public class MyChart extends JFrame
{
    private JPanel _panel = new JPanel();
    private JTextField _text1 = new JTextField("0");
    private JTextField _text2 = new JTextField("0");
    private static final int WIDTH = 300;
    private static final int HEIGHT = 400;
    
    /**
     * 
     */
    public MyChart()
    {
        setTitle("MyChart");
        setResizable(false);
        setLocation(400, 300);
        setSize(WIDTH, HEIGHT);
        getContentPane().add(_panel);
        
        JLabel lab1 = new JLabel("x:");
        lab1.setLocation(0, 0);
        lab1.setSize(20, 20);
        _text1.setLocation(20, 0);
        _text1.setSize(30, 20);
        JLabel lab2 = new JLabel("y:");
        lab2.setLocation(50, 0);
        lab2.setSize(20, 20);
        _text2.setLocation(70, 0);
        _text2.setSize(30, 20);

        JButton btn = new JButton("make");
        btn.setLocation(150, 0);
        btn.setSize(70, 20);
        btn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                int x = Integer.parseInt(_text1.getText());
                int y = Integer.parseInt(_text2.getText());
                
                Graphics2D g = (Graphics2D)_panel.getGraphics();
                
                g.setColor(Color.red);
                g.drawRect((WIDTH-x)/2, (HEIGHT-y)/2, x, y);
                g.fillRect((WIDTH-x)/2, (HEIGHT-y)/2, x, y);
            }
        });
        
        JButton btnclear = new JButton("clear");
        btnclear.setLocation(220, 0);
        btnclear.setSize(70, 20);
        btnclear.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Graphics2D g = (Graphics2D)_panel.getGraphics();
                g.clearRect(0, 0, WIDTH, HEIGHT);
                _panel.updateUI();
            }
        });
        
        _panel.setLayout(new FreeLayout());
        _panel.add(lab1);
        _panel.add(_text1);
        _panel.add(lab2);
        _panel.add(_text2);
        _panel.add(btn);
        _panel.add(btnclear);
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedLookAndFeelException e)
        {
            e.printStackTrace();
        }
        
        MyChart my = new MyChart();
        my.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        my.setVisible(true);
    }
}