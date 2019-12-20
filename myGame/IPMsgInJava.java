/*
 *
 */
package myGame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

/**
 * @author hx0308
 *
 */
public class IPMsgInJava extends JSplitPane
{
    /**
     * <code>MY_IP</code> のコメント
     */
    private static String MY_IP = getMyIP();
    
    /**
     * <code>LISTEN_PORT</code> のコメント
     */
    private static int LISTEN_PORT = 7474;
    
    /**
     * <code>MAX_BYTE_NUM</code> のコメント
     */
    private static int MAX_BYTE_NUM = 36607;
    
    /**
     * <code>_splitPanel</code> のコメント
     */
    private JSplitPane _splitPanel = new JSplitPane();
    
    /**
     * <code>_scrollBtn</code> のコメント
     */
    private JScrollPane _scrollBtn = new JScrollPane();
    
    /**
     * <code>_scrollText</code> のコメント
     */
    private JScrollPane _scrollText = new JScrollPane();
    
    /**
     * <code>_scrollList</code> のコメント
     */
    private JScrollPane _scrollList = new JScrollPane();
    
    /**
     * <code>_text</code> のコメント
     */
    private JTextArea _text = new JTextArea();
    
    /**
     * <code>_table</code> のコメント
     */
    private JTable _table;
    
    /**
     * <code>_userName</code> のコメント
     */
    private String _onLineName = null;
    
    /**
     * <code>_userList</code> のコメント
     */
    private List _userList = null;
    
    /**
     * 
     */
    public IPMsgInJava()
    {
        new Thread(new Runnable()
	    {
            public void run()
            {
                startServer();
            }
        }).start();
        
        setScrollBtn();
        setScrollText();
        setScrollList();
        
        _splitPanel.add(_scrollList, JSplitPane.LEFT);
        _splitPanel.add(_scrollText, JSplitPane.RIGHT);
        _splitPanel.setDividerLocation(150);
        
        setOrientation(JSplitPane.VERTICAL_SPLIT);
        add(_splitPanel, JSplitPane.TOP);
        add(_scrollBtn, JSplitPane.BOTTOM);
        setDividerLocation(200);
    }
    
    /**
     * @return IP
     */
    private static String getMyIP()
    {
        String IP = null;
        try
        {
            IP = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        return IP;
    }

    /**
     * 
     */
    private void startServer()
    {
        try
        {
            DatagramSocket socket = new DatagramSocket(LISTEN_PORT, InetAddress.getByName(MY_IP));
            
            byte[] getValue = new byte[MAX_BYTE_NUM];
            DatagramPacket pack = new DatagramPacket(getValue, getValue.length);
            
            while (true)
            {
                socket.receive(pack);
                
                String value = new String(pack.getData());
                
                //receive data clear
                pack = new DatagramPacket(getValue, getValue.length);
                
                String[] dispValue = value.split("#");
                
                if (dispValue.length != 1)
                {
                    String callIP = dispValue[0];
                    
                    String dispStr = dispValue[1].substring(0, dispValue[1].indexOf(new String(new byte[1])));
                    
                    if (dispStr.length() != 0)
                    {
                        JDialog dlg = new JDialog();
                        dlg.setSize(300, 200);
                        dlg.setLocation(400, 500);
                        
                        JTextArea area = new JTextArea();
                        area.setText(new String(dispStr.getBytes("MS932"), "GB2312"));
                        
                        dlg.getContentPane().add(area);
                        dlg.setVisible(true);
                    }
                    
                    DatagramSocket backSockdet = new DatagramSocket();
                    byte[] backValue = InetAddress.getByName(MY_IP).getHostName().getBytes();
                    DatagramPacket backPack = new DatagramPacket(backValue, backValue.length, InetAddress.getByName(callIP), LISTEN_PORT);
                    backSockdet.send(backPack);
                    backSockdet.setSoTimeout(100);
                }
                else
                {
                    _onLineName = value.substring(0, value.indexOf(new String(new byte[1])));
                    System.out.println(_onLineName);
                    
                    if (!_userList.contains(_onLineName))
                    {
                        _userList.add(_onLineName);
                        _userList.add(InetAddress.getByName(_onLineName).getHostAddress());
                        
                        Object[] btn = new Object[2];
                        btn[0] = _onLineName;
                        btn[1] = InetAddress.getByName(_onLineName).getHostAddress();
                        ((DefaultTableModel)_table.getModel()).addRow(btn);
                        _table.updateUI();
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 
     */
    private void setScrollList()
    {
        DefaultTableModel tableModel = new DefaultTableModel();
        String[] headerList = new String[2];
        headerList[0] = "Name";
        headerList[1] = "IPAddress";
        tableModel.setColumnIdentifiers(headerList);
        
        _userList = new ArrayList();        
        try
        {
            _userList.add(InetAddress.getByName(MY_IP).getHostName());
            _userList.add(MY_IP);
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        int size = _userList.size();
        
        for (int i = 0; i < size; i=+2)
        {
            Object[] btn = new Object[2];
            btn[0] = _userList.get(i).toString();
            btn[1] = _userList.get(i + 1).toString();
            tableModel.addRow(btn);
        }
        _table = new JTable();
        _table.setModel(tableModel);
        
        DefaultTableColumnModel columnModel = (DefaultTableColumnModel)_table.getColumnModel();
        
        for (int i = 0; i < 2; i++)
        {
            TableColumn column = columnModel.getColumn(i);
            column.setCellEditor(new TableButtonEditor());
        }
        
        _scrollList.getViewport().add(_table);
        
        setUserList();
    }

    /**
     */
    private void setUserList()
    {
        String myIP = MY_IP.substring(0, MY_IP.lastIndexOf(".") + 1);
        String onLineIP = null;
        
        for (int i = 0; i < 256; i++)
        {
            onLineIP = myIP + i;
            
            if (!MY_IP.equals(onLineIP))
            {
                getOnLineName(onLineIP);
            }
        }
    }

    /**
     * @param onLineIP
     * @return userName
     */
    private String getOnLineName(String onLineIP)
    {
        String userName = null;
        
        try
        {
            InetAddress address = InetAddress.getByName(onLineIP);
            
            byte[] value = (MY_IP + "#").getBytes();
            
        	DatagramPacket packet = new DatagramPacket(value, value.length, address, LISTEN_PORT);  
            DatagramSocket dsocket = new DatagramSocket();
            dsocket.setSoTimeout(10);
            dsocket.send(packet);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        return userName;
    }

    /**
     * 
     */
    protected void sendMsg()
    {
        int row = _table.getSelectedRow();
        int column = _table.getSelectedColumn();
        
        if (row != -1 && column != -1)
        {
            String ip = _table.getModel().getValueAt(row, column).toString();
            
            if (ip.split("\\.").length < 2)
            {
                column++;
                ip = _table.getModel().getValueAt(row, column).toString();
            }
//            ip = "172.16.1.182";
            try
            {
                String value = _text.getText();
                byte[] sendValue = (MY_IP + "#" + value).getBytes("UTF-8");
                
            	DatagramPacket packet = new DatagramPacket(sendValue, sendValue.length, InetAddress.getByName(ip), LISTEN_PORT);  
                DatagramSocket dsocket = new DatagramSocket();
                dsocket.setSoTimeout(10);
                dsocket.send(packet);
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
            _text.setText("");
        }
    }

    /**
     * 
     */
    private void setScrollText()
    {
        String value = "はい";
        try
        {
            System.out.println(System.getProperty("file.encoding"));
            System.out.println(new String(value.getBytes("ISO-8859-1"), "GB2312"));
            System.out.println(new String(value.getBytes("UTF-8"), "GB2312"));
            System.out.println(new String(value.getBytes("GB2312"), "GB2312"));
            System.out.println(new String(value.getBytes("GBK"), "GB2312"));
            System.out.println(new String(value.getBytes("BIG5"), "GB2312"));
            System.out.println(new String(value.getBytes("JIS"), "GB2312"));
            System.out.println(new String(value.getBytes("MS932"), "GB2312"));
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        
        _text.setFont(new Font("", 0, 14));
        _text.addKeyListener(new KeyAdapter()
                {
        		    public void keyPressed(KeyEvent key)
        		    {
        		        if ("Enter".equals(KeyEvent.getKeyText(key.getKeyCode())) && key.isControlDown())
        		        {
        		            sendMsg();
        		        }
        		    }
                });
        
        
        _scrollText.getViewport().add(_text);
    }

    /**
     * 
     */
    private void setScrollBtn()
    {
        JButton btn = new JButton("SendMsg");
        btn.setFont(new Font("", 1, 18));
        btn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                sendMsg();
            }
        });
        
        _scrollBtn.getViewport().add(btn);
    }

    /**
     * 
     */
    public void setTextFocus()
    {
        _text.requestFocusInWindow();
    }
    
    /**
     * @return _panel
     */
    public JComponent getPanel()
    {
        //jyuuyou
        setPreferredSize(new Dimension(400, 250));
        return this;
    }
    
    /**
     * @author hx0308
     *
     */
    private final class TableButtonEditor extends AbstractCellEditor implements TableCellEditor
    {
        /**
         * <code>_row</code> のコメント
         */
        private int _row = 0;
        
        /**
         * <code>_column</code> のコメント
         */
        private int _column = 0;
        
        /**
         * @param table
         * @param value
         * @param isSelected
         * @param row
         * @param column
         * @return JLabel
         */
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
        {
            _row = row;
            _column = column;
            
            JLabel lab = new JLabel();
            lab.setText(value.toString());
            lab.setBackground(new Color(255, 200, 0));
            
            table.setSelectionBackground(new Color(255, 200, 0));
            return lab;
        }

        /**
         * @return Object
         */
        public Object getCellEditorValue()
        {
            return _table.getModel().getValueAt(_row, _column);
        }
    }
}
