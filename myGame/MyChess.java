package myGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class MyChess extends JPanel{

    private static int LISTEN_PORT = 7474;
    private static int MAX_BYTE_NUM = 65534;
    private static String[] RED_STR = {"車","馬","相","士","師","士","相","馬","車","炮","兵"};
    private static String[] BLACK_STR = {"車","馬","象","仕","将","仕","象","馬","車","炮","卒"};
    private static int EAT = 1;
    private static int SAME = 2;
    
    private String TO_IP = "172.25.134.65";
    private int TYPE = 0;
    private static int MAX_X = 500;
    private static int MAX_Y = 550;
    private static int UNIT_SIZE = 50;
    private static int HALF_UNIT_SIZE = 25;
    private static int CHESS_SIZE = 20;
    private static int HALF_CHESS_SIZE = CHESS_SIZE/2;
    private static int START_X = UNIT_SIZE - HALF_CHESS_SIZE;
    private static int START_Y = START_X;
    private static int END_Y = MAX_Y - UNIT_SIZE - HALF_CHESS_SIZE;
    private static List<Point> list = null;
    private static JLayeredPane panel = null;
    private static String ip = null;
    
    private boolean isGO = true;
    private String _sameName = null;
    private String _moveName = null;
    private Map<String, JButton> _chessMap = Collections.synchronizedMap(new HashMap<String, JButton>());
    private LinkedList<BackDTO> _backList = new LinkedList<BackDTO>();
    private int eatSize = 0;
    private int killSize = 0;
    
    static {
        
        //////////////////////////
        list = new ArrayList<Point>();
        Point point = null;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 9; j ++) {
                point = new Point(50*(j+1), 50*(i+1));
                list.add(point);
            }
        }
        
        ///////////////////
        panel = new JLayeredPane() {
            public void paint(Graphics g)
            {
                Graphics2D g2D = (Graphics2D) g;
                for (int i = 0; i < 10; i++) {
                    g2D.drawLine(50, 50*(i+1), 450, 50*(i+1));
                }
                for (int i = 0; i < 9; i ++) {
                    g2D.drawLine(50*(i+1), 50, 50*(i+1), 500);
                }
                g2D.drawLine(200, 50, 300, 150);
                g2D.drawLine(300, 50, 200, 150);
                g2D.drawLine(200, 400, 300, 500);
                g2D.drawLine(300, 400, 200, 500);
                
                g2D.setColor(new Color(123,221,253));
                g2D.fill3DRect(50, 250, 400, 50, true);
            }
            public void update(Graphics g) {
                paint(g);
            }
            public void repaint() {
                paint(getGraphics());
            }
        };
        panel.setSize(MAX_X, MAX_Y);
        panel.setLayout(new FreeLayout());
        
        ////////////////////
        try
        {
            ip = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        
    }
    
    public MyChess() {

        setPanel();
        new Thread(new Runnable()
        {
            public void run()
            {
                startServer();
            }
        }).start();
    }
    
    public MyChess(String ip, int type) {
        
        TO_IP = ip;
        TYPE = type;
        setPanel();
        new Thread(new Runnable()
        {
            public void run()
            {
                startServer();
            }
        }).start();
    }
    
    /**
     * 
     */
    private void startServer()
    {
        try
        {
            DatagramSocket socket = new DatagramSocket(LISTEN_PORT, InetAddress.getByName(ip));
            
            byte[] getValue = new byte[MAX_BYTE_NUM];
            DatagramPacket pack = new DatagramPacket(getValue, getValue.length);
            
            while (true)
            {
                socket.receive(pack);
                
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(pack.getData(),
                        pack.getOffset(), pack.getLength()));
                
                Object obj = in.readObject();
                
                if (obj instanceof Point) {
                    Point movePoint = (Point)obj;
                    String localBtnName = (String)in.readObject();
                    JButton localBtn = _chessMap.get(localBtnName);
                    Point localPoint = localBtn.getLocation();
                    _backList.add(new BackDTO(localBtnName, localPoint));
                    localBtn.setLocation(localPoint.x-movePoint.x,localPoint.y+movePoint.y);
                    isGO = true;
                    
                    ////////////local eat//////////////
                    java.util.Iterator<Entry<String, JButton>> iterator = _chessMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        JButton bb = _chessMap.get(iterator.next().getKey());
                        if (bb.getLocation().equals(localBtn.getLocation()) && !bb.getName().equals(localBtnName)) {
                            
                            _backList.add(new BackDTO(bb.getName(), bb.getLocation()));
                            bb.setLocation(new Point(10+killSize*20,10));
                            killSize++;
                        }
                    }
                }
                else if (obj instanceof String) {
                    String back = (String)obj;
                    if ("back".equals(back)) {
                        back();
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void setPanel() {
        
        setSize(MAX_X, MAX_Y);
        setLayout(new FreeLayout());
        addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                
                Point point = e.getPoint();
                if (_moveName != null) {
                    for (Point tpoint : list) {
                        if ((point.x>tpoint.x-HALF_UNIT_SIZE) && (point.x<tpoint.x+HALF_UNIT_SIZE) &&
                                (point.y>tpoint.y-HALF_UNIT_SIZE) && (point.y<tpoint.y+HALF_UNIT_SIZE)) {
                            point.x = tpoint.x-HALF_CHESS_SIZE;
                            point.y = tpoint.y-HALF_CHESS_SIZE;
                            JButton btn = _chessMap.get(_moveName);
                            Point oldPoint = btn.getLocation();
                            
                            ///////////////////check//////////////////
                            if (check(oldPoint, point)) {
                                
                                Point rimoto = new Point(oldPoint.x-point.x, oldPoint.y-point.y);
                                int iseat = eat(_moveName, point);
                                if (iseat == EAT) {
                                    _backList.add(new BackDTO(_moveName, oldPoint));
                                    btn.setLocation(point);
                                    sendPoint(_moveName, rimoto);
                                }
                                else if (iseat == SAME) {
                                    _moveName = _sameName;
                                }
                                else if (iseat == 0) {
                                    _backList.add(new BackDTO(_moveName, oldPoint));
                                    btn.setLocation(point);
                                    sendPoint(_moveName, rimoto);
                                }
                            }
                        }
                    }
                }
            }
        });
        
        creatChess();
        creatRollBackBtn();
        add(panel);
    }

    private void creatRollBackBtn() {
        JButton btn = new JButton("悔");
        btn.setSize(20, 50);
        btn.setLocation(470, 250);
        btn.setBorder(null);
        btn.setForeground(new Color(50,95,253));
        btn.setFont(new Font("",1,12));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                
                back();
                sendPoint("back", null);
            }
        });
        add(btn);
    }

    private void back() {
        if (!_backList.isEmpty()) {
            BackDTO dto = _backList.removeLast();
            JButton btn = _chessMap.get(dto.name);
            btn.setLocation(dto.point);
        }
    }
    
    private synchronized boolean check(Point point, Point toPoint) {
        
        boolean ret = false;
        int x = point.x;
        int y = point.y;
        int toX = toPoint.x;
        int toY = toPoint.y;
        
        if (_moveName.contains("馬")) {
            if (x-100==toX || x+100 == toX) {
                if (y-50==toY || y+50==toY) {
                    if (!hasChess(new Point(x-(x-toX)/2,y)))
                    ret = true;
                }
            }
            else if (x-50==toX || x+50 ==toX) {
                if (y-100==toY || y+100==toY) {
                    if (!hasChess(new Point(x,(y-(y-toY)/2)))) {
                        ret = true;
                    }
                }
            }
        }
        else if (_moveName.contains("相") || _moveName.contains("象")) {
            if (toY > 250) {
                if (x-100 == toX || x+100 == toX) {
                    if (y-100 == toY || y + 100 == toY) {
                        if (!hasChess(new Point(x-(x-toX)/2, (y-(y-toY)/2)))) {
                            ret = true;
                        }
                    }
                }
            }
        }
        else if (_moveName.contains("仕") || _moveName.contains("士")) {
            if ((150 < toX && toX < 300) && (350 < toY && toY < 500)) {
                if ((x-50==toX || x+50 ==toX) && (y-50==toY || y+50==toY))  {
                    ret = true;
                }
            }
        }
        else if (_moveName.contains("将") || _moveName.contains("師")) {
            if ((150 < toX && toX < 300) && (350 < toY && toY < 500)) {
                if ((x-50==toX || x+50 ==toX) ^ (y-50==toY || y+50==toY))  {
                    ret = true;
                }
            }
        }
        else if (_moveName.contains("兵") || _moveName.contains("卒")) {
            if (toY > 250) {
                if ((y-50 == toY) && (x==toX)) {
                    ret = true;
                }
            }
            else {
                if (y+50 != toY) {
                    if ((x-50==toX || x+50 ==toX) ^ (y-50==toY || y+50==toY))  {
                        ret = true;
                    }
                }
            }
        }
        else if (_moveName.contains("車")) {
            if (getHasChessSize(point, toPoint) == 0) {
                ret = true;
            }
        }
        else if (_moveName.contains("炮")) {
            int size = getHasChessSize(point, toPoint);
            if (size == 0 || size == 1) {
                ret = true;
            }
        }
        else {
            ret = true;
        }
        
        
        return ret;
    }

    private int getHasChessSize(Point point, Point toPoint) {
        
        int size = 0;
        int x = point.x;
        int y = point.y;
        int toX = toPoint.x;
        int toY = toPoint.y;
        if (y == toY) {
            int from = 0;
            int to = 0;
            if (x > toX) {
                from = toX;
                to = x;
            }
            else {
                from = x;
                to = toX;
            }
            for (int i = from + 50; i < to; i += 50) {
                if (hasChess(new Point(i, y))) {
                    size++;
                }
            }
        }
        else if (x == toX) {
            int from = 0;
            int to = 0;
            if (y > toY) {
                from = toY;
                to = y;
            }
            else {
                from = y;
                to = toY;
            }
            for (int i = from + 50; i < to; i += 50) {
                if (hasChess(new Point(x, i))) {
                    size++;
                }
            }
        }
        else {
            size = -1;
        }
        return size;
    }
    
    private synchronized int eat(String name, Point point) {
        
        int ret = 0;
        boolean aaa = true;
        java.util.Iterator<Entry<String, JButton>> iterator = _chessMap.entrySet().iterator();
        JButton eater = _chessMap.get(name);
        while (iterator.hasNext()) {
            JButton bb = _chessMap.get(iterator.next().getKey());
            
            if (bb.getLocation().equals(point) && !bb.getName().equals(name)){
                if (bb.getName().substring(0, 1).equals(eater.getName().substring(0, 1))) {
                    ret = SAME;
                    _sameName = bb.getName();
                }
                else {
                    
                    if (name.contains("炮")) {
                        if (getHasChessSize(eater.getLocation(), point) == 1) {
                            
                            _backList.add(new BackDTO(bb.getName(), bb.getLocation()));
                            bb.setLocation(new Point(10+eatSize*20,510));
                            ret = EAT;
                            eatSize++;
                            aaa = false;
                        }
                        else {
                            ret = -1;
                        }
                    }
                    else {
                        _backList.add(new BackDTO(bb.getName(), bb.getLocation()));
                        bb.setLocation(new Point(10+eatSize*20,510));
                        ret = EAT;
                        eatSize++;
                    }
                }
            }
        }
        
        if (name.contains("炮") && aaa && getHasChessSize(eater.getLocation(), point) != 0) {
            ret = -1;
        }
        
        return ret;
    }
    
    private synchronized boolean hasChess(Point point) {
        boolean ret = false;
        
        java.util.Iterator<Entry<String, JButton>> iterator = _chessMap.entrySet().iterator();
        while (iterator.hasNext()) {
            JButton bb = _chessMap.get(iterator.next().getKey());
            
            if (bb.getLocation().equals(point)) {
                ret = true;
                break;
            }
        }
        return ret;
    }
    
    private synchronized void sendPoint(String name, Point rimoto) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream dataStream = new ObjectOutputStream(out);
            if (rimoto != null) {
                dataStream.writeObject(rimoto);
            }
            dataStream.writeObject(name);
            DatagramSocket backSockdet = new DatagramSocket();
            DatagramPacket backPack = new DatagramPacket(out.toByteArray(), out.size(), InetAddress.getByName(TO_IP), LISTEN_PORT);
            backSockdet.send(backPack);
            backSockdet.setSoTimeout(50);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        _moveName = null;
    }
    
    private void creatChess() {

        if (TYPE == 0) {

            JButton btn = null;
            // ////////////////red//////////
            for (int i = 0; i < 9; i++) {
                btn = new JButton(RED_STR[i]);
                btn.setLocation(START_X + UNIT_SIZE * i, END_Y);
                setBtn(btn, RED_STR[i] + i, TYPE);
                _chessMap.put(TYPE + RED_STR[i] + i, btn);
            }

            btn = new JButton(RED_STR[9]);
            btn.setLocation(START_X + UNIT_SIZE, END_Y-UNIT_SIZE*2);
            setBtn(btn, RED_STR[9] + 1, TYPE);
            _chessMap.put(TYPE + RED_STR[9] + 1, btn);

            btn = new JButton(RED_STR[9]);
            btn.setLocation(START_X + UNIT_SIZE*7, END_Y-UNIT_SIZE*2);
            setBtn(btn, RED_STR[9] + 2, TYPE);
            _chessMap.put(TYPE + RED_STR[9] + 2, btn);

            for (int i = 0; i < 5; i++) {
                btn = new JButton(RED_STR[10]);
                btn.setLocation(START_X + UNIT_SIZE * 2 * i, END_Y-UNIT_SIZE*3);
                setBtn(btn, RED_STR[10] + i, TYPE);
                _chessMap.put(TYPE + RED_STR[10] + i, btn);
            }

            // ////////////////// black//////////////
            for (int i = 0; i < 9; i++) {
                btn = new JButton(BLACK_STR[i]);
                btn.setLocation(START_X + UNIT_SIZE * i, START_Y);
                setBtn(btn, BLACK_STR[i] + i, 1);
                _chessMap.put(1 + BLACK_STR[i] + i, btn);
            }

            btn = new JButton(BLACK_STR[9]);
            btn.setLocation(START_X + UNIT_SIZE, START_Y+UNIT_SIZE*2);
            setBtn(btn, BLACK_STR[9] + 1, 1);
            _chessMap.put(1 + BLACK_STR[9] + 1, btn);

            btn = new JButton(BLACK_STR[9]);
            btn.setLocation(START_X + UNIT_SIZE*7, START_Y+UNIT_SIZE*2);
            setBtn(btn, BLACK_STR[9] + 2, 1);
            _chessMap.put(1 + BLACK_STR[9] + 2, btn);

            for (int i = 0; i < 5; i++) {
                btn = new JButton(BLACK_STR[10]);
                btn.setLocation(START_X + UNIT_SIZE * 2 * i, START_Y+UNIT_SIZE*3);
                setBtn(btn, BLACK_STR[10] + i, 1);
                _chessMap.put(1 + BLACK_STR[10] + i, btn);
            }
        } else if (TYPE == 1) {

            isGO = false;

            JButton btn = null;
            // ////////////////black//////////
            for (int i = 0; i < 9; i++) {
                btn = new JButton(BLACK_STR[i]);
                btn.setLocation(START_X + UNIT_SIZE * i, END_Y);
                setBtn(btn, BLACK_STR[i] + i, TYPE);
                _chessMap.put(TYPE + BLACK_STR[i] + i, btn);
            }

            btn = new JButton(BLACK_STR[9]);
            btn.setLocation(START_X + UNIT_SIZE, END_Y-UNIT_SIZE*2);
            setBtn(btn, BLACK_STR[9] + 1, TYPE);
            _chessMap.put(TYPE + BLACK_STR[9] + 1, btn);

            btn = new JButton(BLACK_STR[9]);
            btn.setLocation(START_X + UNIT_SIZE*7, END_Y-UNIT_SIZE*2);
            setBtn(btn, BLACK_STR[9] + 2, TYPE);
            _chessMap.put(TYPE + BLACK_STR[9] + 2, btn);

            for (int i = 0; i < 5; i++) {
                btn = new JButton(BLACK_STR[10]);
                btn.setLocation(START_X + UNIT_SIZE * 2 * i, END_Y-UNIT_SIZE*3);
                setBtn(btn, BLACK_STR[10] + i, TYPE);
                _chessMap.put(TYPE + BLACK_STR[10] + i, btn);
            }

            // ////////////////// red//////////////
            for (int i = 0; i < 9; i++) {
                btn = new JButton(RED_STR[i]);
                btn.setLocation(START_X + UNIT_SIZE * i, START_Y);
                setBtn(btn, RED_STR[i] + i, 0);
                _chessMap.put(0 + RED_STR[i] + i, btn);
            }

            btn = new JButton(RED_STR[9]);
            btn.setLocation(START_X + UNIT_SIZE, START_Y+UNIT_SIZE*2);
            setBtn(btn, RED_STR[9] + 1, 0);
            _chessMap.put(0 + RED_STR[9] + 1, btn);

            btn = new JButton(RED_STR[9]);
            btn.setLocation(START_X + UNIT_SIZE*7, START_Y+UNIT_SIZE*2);
            setBtn(btn, RED_STR[9] + 2, 0);
            _chessMap.put(0 + RED_STR[9] + 2, btn);

            for (int i = 0; i < 5; i++) {
                btn = new JButton(RED_STR[10]);
                btn.setLocation(START_X + UNIT_SIZE * 2 * i, START_Y+UNIT_SIZE*3);
                setBtn(btn, RED_STR[10] + i, 0);
                _chessMap.put(0 + RED_STR[10] + i, btn);
            }
        }

        java.util.Iterator<Entry<String, JButton>> iterator = _chessMap
                .entrySet().iterator();
        while (iterator.hasNext()) {
            JButton bb = _chessMap.get(iterator.next().getKey());
            add(bb);
        }
    }
    
    public void setBtn(JButton btn, String name, int type) {
        
        byte[] mm = new byte[2048];
        
        try {
            if (type == 0) {
                getClass().getResourceAsStream("/myGame/chessjpg/red.jpg").read(mm);
            }
            else if (type == 1) {
                getClass().getResourceAsStream("/myGame/chessjpg/black.jpg").read(mm);
            }
        } catch (IOException e1) {
            System.out.println("map read error!!!");
        }
        
        btn.setName(type+name);
        btn.setIcon(new ImageIcon(mm));
        btn.setFont(new Font("",1,CHESS_SIZE-8));
        btn.setForeground(new Color(255,255,255));
        btn.setSize(CHESS_SIZE, CHESS_SIZE);
        btn.setBorder(BorderFactory.createRaisedBevelBorder());
        btn.setIconTextGap(0);
        btn.setHorizontalTextPosition(0);
        btn.setVerticalTextPosition(0);
        btn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JButton bb = (JButton) e.getSource();
                int type = Integer.parseInt(bb.getName().substring(0,1));
                if (_moveName == null && isGO && TYPE == type) {
                    _moveName = bb.getName();
                    isGO = false;
                }
            }
        });
        btn.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                
                if (_moveName != null) {
                    
                    JButton nowBtn = (JButton) e.getSource();
                    Point point = nowBtn.getLocation();
                    
                    if (!point.equals(e.getPoint())) {
                        
                        JButton btn = _chessMap.get(_moveName);
                        Point oldPoint = btn.getLocation();
                        
                        ///////////////////check//////////////////
                        if (check(oldPoint, point)) {
                            
                            Point rimoto = new Point(oldPoint.x-point.x, oldPoint.y-point.y);
                            int iseat = eat(_moveName, point);
                            if (EAT == iseat) {
                                _backList.add(new BackDTO(_moveName, oldPoint));
                                btn.setLocation(point);
                                sendPoint(_moveName, rimoto);
                            }
                            else if (SAME == iseat){
                                _moveName = nowBtn.getName();
                            }
                        }
                    }
                }
            }
        });
    }
    
    private class BackDTO {
        public String name;
        public Point point;
        public BackDTO(String name, Point point) {
            this.name = name;
            this.point = point;
        }
    }
}
