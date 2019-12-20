package myGame;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/*
 * ������: 2009/01/07
 *
 * �E�B���h�E - ���� - Java - �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
 */

/**
 * @author hx0308
 * 
 *         �E�B���h�E - ���� - Java - �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
 */
public class StartMenu extends JFrame implements ActionListener {
    /**
     * <code>HIRO_PATH</code> ���R�����g
     */
    private static final String HIRO_PATH = "C:\\hiro.txt";

    /**
     * <code>HIRO_LIST_SIZE</code> ���R�����g
     */
    public static final int HIRO_LIST_SIZE = 5;

    /**
     * <code>_item1cc</code> ���R�����g
     */
    private JMenuItem _item1cc;

    /**
     * <code>_item1IP</code> ���R�����g
     */
    private JMenuItem _item1Rich;
    
    /**
     * <code>_item1IP</code> ���R�����g
     */
    private JMenuItem _item1Chess;
    
    /**
     * <code>_item1IP</code> ���R�����g
     */
    private JMenuItem _item1IP;

    /**
     * <code>_item2</code> ���R�����g
     */
    private JMenuItem _item2;

    /**
     * <code>_item3</code> ���R�����g
     */
    private JMenuItem _item3;

    /**
     * <code>mainPanel</code> ���R�����g
     */
    private JComponent _mainPanel;

    /**
     * 
     */
    private StartMenu() {
        
        // ColorClear
        _item1cc = new JMenuItem("ColorClear");
        _item1cc.setFont(new Font("", 1, 14));
        _item1cc.setMnemonic('c');
        _item1cc.setAccelerator(KeyStroke.getKeyStroke(113, 0));
        _item1cc.addActionListener(this);
        // _item1cc.setEnabled(false);

        // rich
        _item1Rich = new JMenuItem("DoubleColor");
        _item1Rich.setFont(new Font("", 1, 14));
        _item1Rich.setMnemonic('d');
        _item1Rich.setAccelerator(KeyStroke.getKeyStroke(114, 0));
        _item1Rich.addActionListener(this);
        
//         // tmp
//        _item1IP = new JMenuItem("IPMsg");
//        _item1IP.setFont(new Font("", 1, 14));
//        _item1IP.setMnemonic('i');
//        _item1IP.setAccelerator(KeyStroke.getKeyStroke(115, 0));
//        _item1IP.addActionListener(this);

        // chess
        _item1Chess = new JMenuItem("Chess");
        _item1Chess.setFont(new Font("", 1, 14));
        _item1Chess.setMnemonic('e');
        _item1Chess.setAccelerator(KeyStroke.getKeyStroke(115, 0));
        _item1Chess.addActionListener(this);
        
        // StartGame
        JMenu item = new JMenu("StartGame");
        item.setFont(new Font("", 1, 14));
        item.add(_item1cc);
        item.add(_item1Rich);
        item.add(_item1Chess);
//        item.add(_item1IP);

        // Hiro
        _item2 = new JMenuItem("Hiro");
        _item2.setFont(new Font("", 1, 14));
        _item2.setMnemonic('h');
        _item2.addActionListener(this);

        // Quit
        _item3 = new JMenuItem("Quit");
        _item3.setFont(new Font("", 1, 14));
        _item3.setMnemonic('q');
        _item3.setAccelerator(KeyStroke.getKeyStroke(119, 0));
        _item3.addActionListener(this);

        // meun
        JMenu menu = new JMenu("Menu");
        menu.setFont(new Font("", 1, 16));
        menu.add(item);
        menu.add(_item2);
        menu.addSeparator();
        menu.add(_item3);

        // bar
        JMenuBar bar = new JMenuBar();
        bar.add(menu);
        setJMenuBar(bar);
    }

    /**
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        
        Object obj = e.getSource();
        
        if (obj instanceof JMenuItem) {
            JMenuItem item = (JMenuItem) obj;

            if (_mainPanel != null) {
                
                getContentPane().remove(_mainPanel);
                if (_mainPanel instanceof ColorClear) {
                    ((ColorClear)_mainPanel).destory();
                    _mainPanel = null;
                }
                else{
                    _mainPanel.removeAll();
                    _mainPanel = null;
                }
            }
            
            // cc
            if (item == _item1cc) {
                
                setSize(400, 300);
                _mainPanel = new ColorClear();
                getContentPane().add(_mainPanel, BorderLayout.CENTER);
                _mainPanel.updateUI();
            }
            // Rich
            else if (item == _item1Rich) {

                setSize(400, 300);
                _mainPanel = new MaybeRich();
                getContentPane().add(_mainPanel, BorderLayout.CENTER);
                pack();
                _mainPanel.updateUI();
            }
            // chess
            else if (item == _item1Chess) {
                
                String ip = null;
                boolean flg = true;
                while(flg){
                    try
                    {
                        ip = JOptionPane.showInputDialog("IP:", "172.25.134.65");
                        InetAddress address = InetAddress.getByName(ip);
                        flg = address.isLinkLocalAddress();
                    }
                    catch (IOException e1)
                    {
                        JOptionPane.showMessageDialog(this, "disconnect.");
                    }
                }
                
                String type = JOptionPane.showInputDialog("紅：0,黒：1", "0");
                if (type != null) {
                    flg = Pattern.matches("[0-1]", type);
                }
                else {
                    flg = false;
                }
                while(!flg) {
                    JOptionPane.showMessageDialog(this, "type error.");
                    type = JOptionPane.showInputDialog("紅：0,黒：1", "0");
                    flg = Pattern.matches("[0-1]", type);
                }
                
                if (ip != null && flg) {

                    setSize(505, 610);
                    _mainPanel = new MyChess(ip, Integer.parseInt(type));
                    getContentPane().add(_mainPanel, BorderLayout.CENTER);
                    _mainPanel.updateUI();
                }
            }
//            // IPmsg
//            else if (item == _item1IP) {
//                if (_mainPanel != null) {
//                    _mainPanel.removeAll();
//                    _mainPanel = null;
//                }
//
//                IPMsgInJava ip = new IPMsgInJava();
//                _mainPanel = ip.getPanel();
//                getContentPane().add(_mainPanel, BorderLayout.CENTER);
//                ((IPMsgInJava) _mainPanel).setTextFocus();
//
//                pack();
//                _mainPanel.updateUI();
//            }
            // hiro
            else if (item == _item2) {
                showHiro(-1);
            }
            // quit
            else if (item == _item3) {
                System.gc();
                System.exit(0);
            }
        }
    }

    /**
     * 
     */
    public static void showHiro(int index) {
        List hiroList = getHiroList();
        JDialog dlg = new JDialog(new Frame(), "Hiro", true);
        JTable table = new JTable(hiroList.size(), 2);
        table.setEnabled(false);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setFont(new Font("", 1, 16));

        for (int i = 0; i < hiroList.size(); i++) {
            
            String[] value = (String[]) hiroList.get(i);
            
            table.setValueAt("No." + String.valueOf(i + 1) + ":" + value[0], i,
                    0);
            table.setValueAt("result:" + value[1], i, 1);
            if (index != -1 && i == index) {
                table.setRowSelectionInterval(index, index);
            }
        }

        dlg.getContentPane().add(table);
        dlg.setSize(300, 200);
        dlg.setLocation(100, 100);
        dlg.setVisible(true);
    }

    /**
     * @return
     * 
     */
    public static List getHiroList() {
        FileReader reader = null;
        List hiroList = new ArrayList();

        try {
            reader = new FileReader(HIRO_PATH);
        } catch (FileNotFoundException e1) {
            try {
                new BufferedWriter(new FileWriter(HIRO_PATH));
                reader = new FileReader(HIRO_PATH);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }

        BufferedReader read = new BufferedReader(reader);

        try {
            for (int i = 0; i < HIRO_LIST_SIZE; i++) {
                String line = read.readLine();

                if (line != null && line.length() != 0) {
                    String[] value = line.split("\\s+");
                    hiroList.add(value);
                } else {
                    String[] value = new String[2];
                    value[0] = "--";
                    value[1] = "--";
                    hiroList.add(value);
                }
            }
            read.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        return hiroList;
    }

    /**
     * @param hiroList
     * @return ret
     */
    public static boolean setHiroList(List hiroList) {
        boolean ret = true;
        try {
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(HIRO_PATH));

            for (int i = 0; i < hiroList.size(); i++) {
                String[] value = (String[]) hiroList.get(i);
                writer.write(value[0] + "   " + value[1]);

                writer.newLine();
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            ret = false;
        }

        return ret;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        StartMenu start = new StartMenu();
        start.setTitle("savic-net EV");
        start.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // start.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        // start.setResizable(false);
        start.setSize(400, 300);
        // Rectangle rectangle =
        // GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        // start.setLocation(rectangle.width / 2 - 400, rectangle.height / 2 -
        // 300);
        start.setVisible(true);
    }
}
