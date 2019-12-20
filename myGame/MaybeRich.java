package myGame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

public class MaybeRich extends JSplitPane {

    public JLabel _text = new JLabel();
    public JTextPane _blue = new JTextPane();
    public JTextPane _red = new JTextPane();
    public JScrollPane _textPane = new JScrollPane();
    public JScrollPane _btnPane = new JScrollPane();
    public JCheckBox _box = new JCheckBox();
    public JButton _btn;
    public boolean _goonFlg = false;
    
    public MaybeRich() {
        
//        setBackground(Color.DARK_GRAY);
//        setLayout(new FlowLayout());
        setPreferredSize(new Dimension(400, 250));
        _text.setFont(new Font("", 2,30));
        _text.setForeground(new Color(255,120,240));
        
        _btn = new JButton("GO");
        _btn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                
                if (_box.isSelected()) {
                    goon();
                }
                else
                {
                    if ("".equals(_blue.getText()) || "".equals(_red.getText())) {
                        
                        _text.setText(getNo());
                    }
                    else {
                        _text.setText(String.valueOf(getPayfor(Integer.parseInt(_blue.getText()), Integer.parseInt(_red.getText()))));
                    }
                }
            }
        });
        
        
        _blue.setSize(30, 20);
        _blue.setLocation(50, 0);
        
        _red.setSize(30, 20);
        _red.setLocation(200, 0);
        
        JLabel blue = new JLabel("青い：");
        blue.setSize(50, 20);
        blue.setLocation(0, 0);
        
        JLabel red = new JLabel("赤い：");
        red.setSize(50, 20);
        red.setLocation(150, 0);
        
        JLabel goon = new JLabel("自動採番：");
        goon.setSize(100, 20);
        goon.setLocation(250, 0);
        
        _text.setSize(300, 50);
        _text.setLocation(0, 40);
        
        _box.setSize(30, 20);
        _box.setLocation(300, 0);
        
        JPanel panel = new JPanel();
        panel.setLayout(new FreeLayout());
        panel.add(blue);
        panel.add(_blue);
        panel.add(red);
        panel.add(_red);
        panel.add(goon);
        panel.add(_box);
        panel.add(_text);
        _textPane.getViewport().add(panel);
        _btnPane.getViewport().add(_btn);
        
        setOrientation(JSplitPane.VERTICAL_SPLIT);
        add(_textPane, JSplitPane.TOP);
        add(_btnPane, JSplitPane.BOTTOM);
        setDividerLocation(150);
    }
    
    public void goon() {
        
        if (_goonFlg) {
            _goonFlg = false;
        }
        else {
            _goonFlg = true;
        }
        
        Thread thread = new Thread(){
            public void run() {
                String no = getNo();
                
                while (_goonFlg) {
                    _text.setText(getNo());
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }
    
    public static String getNo() {
        
        String no = new String();
        
        int value = 0;
        int index = 0;
        List<Integer> nolist = new ArrayList<Integer>();
        
        // red
        List<Integer> list = new ArrayList<Integer>(32);
        for (int i = 0; i < 33; i ++) {
            list.add(i+1);
        }
        
        // blue
        List<Integer> blist = new ArrayList<Integer>(16);
        for (int i = 0; i < 16; i ++) {
            blist.add(i+1);
        }
        
        Random dom = new Random();
        
        // 1
        index = dom.nextInt(33);
        value = list.get(index);
        list.remove(index);
        nolist.add(value);
        
        // 2
        index = dom.nextInt(32);
        value = list.get(index);
        list.remove(index);
        nolist.add(value);
        
        // 3
        index = dom.nextInt(31);
        value = list.get(index);
        list.remove(index);
        nolist.add(value);
        
        // 4
        index = dom.nextInt(30);
        value = list.get(index);
        list.remove(index);
        nolist.add(value);
        
        // 5
        index = dom.nextInt(29);
        value = list.get(index);
        list.remove(index);
        nolist.add(value);
        
        // 6
        index = dom.nextInt(28);
        value = list.get(index);
        list.remove(index);
        nolist.add(value);
        
        int size = nolist.size();
        int smallsize = size - 1;
        for (int i = 0 ; i < smallsize; i++) {
            for (int j = i+1 ; j < size; j++) {
                if (nolist.get(i) > nolist.get(j)) {
                    value = nolist.get(i);
                    nolist.set(i, nolist.get(j));
                    nolist.set(j, value);
                }
            }
        }
        
        for (int i : nolist) {
            no+=i+" ";
        }
        
        // blue
        index = dom.nextInt(16);
        value = blist.get(index);
        no += value;
        
        return no;
    }
    
    private long i = 1;
    private boolean isOver = false;
    
    public MaybeRich(@SuppressWarnings("unused") int aaa) {
        
        Thread thread = new Thread(){
            public void run() {
                String no = getNo();
                do {
                    no = getNo();
                    up();
                }
                while (!"4 8 12 17 25 33 15".equals(no));
                over();
                System.out.println("the last no : "+ String.valueOf(i));
            }
        };
        thread.start();
    }
    
    public MaybeRich(final String red, final String blue) {
        
        Thread thread = new Thread(){
            public void run() {
                String no = null;
                String[] redlist = red.split(" ");
                String[] bluelist = blue.split(" ");
                if ("*".equals(bluelist[0])) {
                    bluelist = null;
                }

                do {
                    no = getNo();
                    up();
                }
                while (!multCheck(redlist, bluelist, no));
                over();
                System.out.println("the last no : "+ String.valueOf(i) + " =====>" +no);
            }
        };
        thread.start();
    }
    
    private boolean multCheck(String[] redlist, String[] bluelist, String no) {
        
        boolean isok = false;
        int redFlg = -1;
        String[] tmpList = no.split(" ");
        
        //red check
        int size = tmpList.length - 1;
        for (int i = 0; i < size; i++) {
            for (String red : redlist) {
                if (tmpList[i].equals(red)) {
                    redFlg = i;
                    break;
                }
            }
            
            if (redFlg != i) {
                break;
            }
        }
        
        // blue check
        if (redFlg == 5) {
            if (bluelist != null) {
                String blue = tmpList[tmpList.length - 1];
                for (String tmpblue : bluelist) {
                    if (blue.equals(tmpblue)) {
                        isok = true;
                        break;
                    }
                }
            }
            else {
                isok = true;
            }
        }
        
        return isok;
    }
    
    private void up() {
        i++;
    }
    
    private void over() {
        isOver = true;
    }

    private void showNo() {
        while (!isOver) {
            System.out.println(i);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static long getPayfor(int blue, int red) {
        
        long money = 1;
        
        //******* pay for
        int redsize = blue;
        int bluesize = red;
        
        int loop = redsize - 6;
        
        if (loop > redsize/2) {
            loop = redsize - loop;
        }
        
        int chu = 1;
        for (int i = 0; i < loop; i++) {
            money = (redsize-i) * money;
            chu = chu * (i + 1);
        }
        money = money / chu;
        money = money * bluesize * 2;
        
        return money;
    }
    
    public static void main(String[] args) {
        
        MaybeRich test = new MaybeRich("5 9 11 15 17 21 25 29 33", "*");
//        MaybeRich test = new MaybeRich(1);
        test.showNo();
        
        System.out.println(getPayfor(8, 1));
    }
}
