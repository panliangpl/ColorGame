/*
 * �쐬��: 2009/01/09
 *
 * �E�B���h�E - �ݒ� - Java - �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
 */
package myGame;

import java.applet.AudioClip;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import sun.applet.AppletAudioClip;

/**
 * @author hx0308
 * 
 *         �E�B���h�E - �ݒ� - Java - �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
 */
public class ColorClear extends JPanel {

    /**
     * <code>MAX_ROW</code> �̃R�����g
     */
    private static final int MAX_ROW = 15;

    /**
     * <code>MAX_COL</code> �̃R�����g
     */
    private static final int MAX_COL = 18;

    /**
     * <code>_colorList</code> �̃R�����g
     */
    private List _colorList = null;

    /**
     * <code>_btnList</code> �̃R�����g
     */
    private Object[][] _btnList = null;

    /**
     * <code>_btnMap</code> �̃R�����g
     */
    private Map _btnMap = null;

    /**
     * <code>_result</code> �̃R�����g
     */
    private int _result = 0;

    private boolean _isOver = false;

//    private String _classPath = null;

    /**
     * 
     */
    public ColorClear() {
        
        _result = 0;
        _isOver = false;
//        try {
//            _classPath = getClass().getResource("/").getPath();
//        }
//        catch (Exception e) {
//            _classPath = null;
//        }
        setBackground(Color.white);
        setLayout(new GridLayout(MAX_ROW, MAX_COL, 1, 1));
        _colorList = getColorList();
        _btnList = new Object[MAX_ROW][MAX_COL];
        _btnMap = new HashMap();
        creatButton();
    }

    /**
     * @return _colorList
     */
    private List getColorList() {
        List list = new ArrayList();

        list.add("red");
        list.add("yellow");
        list.add("blue");
        list.add("green");
        list.add("mediu");
        return list;
    }

    /**
     * @return icon
     */
    private int getRandomValue() {
        Random random = new Random();
//        try {
//            Thread.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        int value = random.nextInt(_colorList.size());
        return value;
    }

    /**
     * 
     */
    private void creatButton() {
        
        byte[] mm = new byte[1024];
        
        for (int i = 0; i < MAX_ROW; i++) {
            for (int j = 0; j < MAX_COL; j++) {
                int value = getRandomValue();

                JButton btn = null;
                
                try {
                    getClass().getResourceAsStream("/myGame/colorjpg/" + _colorList.get(value) + ".jpg").read(mm);
                } catch (IOException e1) {
                    System.out.println("map read error!!!");
                }
                btn = new JButton("", new ImageIcon(mm));
                btn.setActionCommand(String.valueOf(value));
                btn.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
//                        if (e.getClickCount() == 2) {

                            JButton btn = (JButton) e.getSource();
                            Point point = (Point) _btnMap.get(btn);

                            // getClearList AND clear it
                            Thread thread = new Thread(new ClearThread(point));
                            thread.setName("clearBtn"
                                    + System.currentTimeMillis());
                            thread.start();
//                        }
                    }
                });

                _btnList[i][j] = btn;
                add(btn);

                _btnMap.put(btn, new Point(i, j));
                _btnMap.put(new Point(i, j), btn);
            }
        }
    }

    /**
     * @param result
     */
    private synchronized void setResult(int result) {
        _result = _result + result;

        Thread thread = new Thread() {
            public void run() {
                if (!_isOver) {
                    isOver();
                }
            }
        };
        thread.setName("isOver" + System.currentTimeMillis());
        thread.start();
    }

    /**
     * 
     */
    private synchronized void isOver() {

        if (!_isOver) {
            boolean isover = true;
            List list = new ArrayList();
            Object[][] btnList = _btnList;
            Object point = null;
            for (int i = 0; i < MAX_ROW; i++) {
                for (int j = 0; j < MAX_COL; j++) {
                    point = _btnMap.get(btnList[i][j]);
                    if (point instanceof Point) {
                        setClearList((Point) point, list);
                        isover = list.size() == 1;
                        list.clear();
                    }
                    if (!isover) {
                        i = MAX_ROW;
                        break;
                    }
                }
            }

            _isOver = isover;

            if (_isOver) {
                doOver();

                _result = 0;
                removeAll();
                setVisible(false);
                _btnMap.clear();
                for (int i = 0; i < MAX_ROW; i++) {
                    for (int j = 0; j < MAX_COL; j++) {
                        _btnList[i][j] = null;
                    }
                }
                _btnList = null;
            }
        }
    }

    /**
     * @param point
     */
    private void setClearList(Point point, List clearList) {

        clearList.add(point);

        int size = clearList.size();
        int clearSize = 0;
        do {
            clearSize = clearList.size();
            size = clearList.size();
            for (int i = 0; i < size; i++) {
                getPointList((Point) clearList.get(i), clearList);
            }
        } while (clearSize != clearList.size());
    }

    /**
     * @param point
     * @return clearList
     */
    private void getPointList(Point point, List list) {
        getUpList(point, list);
        getDownList(point, list);
        getLeftList(point, list);
        getRightList(point, list);
    }

    /**
     * @param point
     * @return clearList
     */
    private List getUpList(Point point, List list) {
        List clearList = new ArrayList();

        String color = ((JButton) _btnMap.get(point)).getActionCommand();

        Point nextpoint = new Point((int) point.getX() - 1, (int) point.getY());
        JButton btn = (JButton) _btnMap.get(nextpoint);

        if (btn != null) {
            String nextColor = btn.getActionCommand();
            while (color.equals(nextColor)) {
                if (!list.contains(nextpoint)) {
                    clearList.add(nextpoint);
                    list.add(nextpoint);
                }

                nextpoint = new Point((int) nextpoint.getX() - 1,
                        (int) nextpoint.getY());

                btn = (JButton) _btnMap.get(nextpoint);

                if (btn != null) {
                    nextColor = btn.getActionCommand();
                } else {
                    nextColor = "";
                }
            }
        }
        return clearList;
    }

    /**
     * @param point
     * @return clearList
     */
    private List getDownList(Point point, List list) {
        List clearList = new ArrayList();

        String color = ((JButton) _btnMap.get(point)).getActionCommand();

        Point nextpoint = new Point((int) point.getX() + 1, (int) point.getY());

        JButton btn = (JButton) _btnMap.get(nextpoint);

        if (btn != null) {
            String nextColor = btn.getActionCommand();
            while (color.equals(nextColor)) {
                if (!list.contains(nextpoint)) {
                    clearList.add(nextpoint);
                    list.add(nextpoint);
                }

                nextpoint = new Point((int) nextpoint.getX() + 1,
                        (int) nextpoint.getY());

                btn = (JButton) _btnMap.get(nextpoint);

                if (btn != null) {
                    nextColor = btn.getActionCommand();
                } else {
                    nextColor = "";
                }
            }
        }

        return clearList;
    }

    /**
     * @param point
     * @return clearList
     */
    private List getLeftList(Point point, List list) {
        List clearList = new ArrayList();

        String color = ((JButton) _btnMap.get(point)).getActionCommand();

        Point nextpoint = new Point((int) point.getX(), (int) point.getY() - 1);
        JButton btn = (JButton) _btnMap.get(nextpoint);

        if (btn != null) {
            String nextColor = btn.getActionCommand();
            while (color.equals(nextColor)) {
                if (!list.contains(nextpoint)) {
                    clearList.add(nextpoint);
                    list.add(nextpoint);
                }

                nextpoint = new Point((int) nextpoint.getX(), (int) nextpoint
                        .getY() - 1);

                btn = (JButton) _btnMap.get(nextpoint);

                if (btn != null) {
                    nextColor = btn.getActionCommand();
                } else {
                    nextColor = "";
                }
            }
        }
        return clearList;
    }

    /**
     * @param point
     * @return clearList
     */
    private List getRightList(Point point, List list) {
        List clearList = new ArrayList();

        String color = ((JButton) _btnMap.get(point)).getActionCommand();

        Point nextpoint = new Point((int) point.getX(), (int) point.getY() + 1);
        JButton btn = (JButton) _btnMap.get(nextpoint);

        if (btn != null) {
            String nextColor = btn.getActionCommand();
            while (color.equals(nextColor)) {
                if (!list.contains(nextpoint)) {
                    clearList.add(nextpoint);
                    list.add(nextpoint);
                }

                nextpoint = new Point((int) nextpoint.getX(), (int) nextpoint
                        .getY() + 1);

                btn = (JButton) _btnMap.get(nextpoint);

                if (btn != null) {
                    nextColor = btn.getActionCommand();
                } else {
                    nextColor = "";
                }
            }
        }
        return clearList;
    }

    /**
     */
    private synchronized void clear(List clearList) {

        int size = clearList.size();

        if (size > 1) {

            AudioClip clip = null;
            
//            if (_classPath == null) {
                
                try {
                    byte[] yy = new byte[1024];
                    getClass().getResourceAsStream("/myGame/audio/cc.au").read(yy);
                    clip = new AppletAudioClip(yy);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//            }
//            else {
//                
//                try {
//                    clip = Applet.newAudioClip(new URL("file://localhost"
//                            + _classPath + "/myGame/audio/cc.au"));
//                } catch (MalformedURLException e1) {
//                    e1.printStackTrace();
//                }
//            }
            
            for (int i = 0; i < size; i++) {
                Point point = (Point) clearList.get(i);

                JButton btn = (JButton) _btnMap.get(point);

                if (btn != null) {
                    btn.setVisible(false);
                    btn.removeAll();
                    _btnMap.remove(point);
                    _btnMap.remove(btn);
                    _btnList[point.x][point.y] = null;

                    try {
                        clip.play();

                        updateUI();
                        Thread.sleep(30);

                        clip.stop();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 
     */
    private synchronized void reDraw() {

        int tmpcol = MAX_COL;
        for (int i = 0; i < tmpcol; i++) {
            int empNum = 0;
            for (int e = 0; e < MAX_ROW; e++) {
                JButton btn = (JButton) _btnList[e][i];
                if (btn == null) {
                    empNum++;
                }
            }

            if (empNum != MAX_ROW) {

                // less than one row
                List btnList = new ArrayList();
                for (int emp = 0; emp < empNum; emp++) {
                    btnList.add(null);
                }

                for (int j = 0; j < MAX_ROW; j++) {
                    JButton btn = (JButton) _btnList[j][i];

                    if (btn != null) {
                        btnList.add(btn);
                    }
                }

                for (int j = 0; j < MAX_ROW; j++) {
                    _btnList[j][i] = btnList.get(j);
                }
            } else {
                for (int e = i; e < MAX_COL; e++) {
                    for (int j = 0; j < MAX_ROW; j++) {
                        if (e == MAX_COL - 1) {
                            _btnList[j][e] = null;
                        } else {
                            _btnList[j][e] = _btnList[j][e + 1];
                        }
                    }
                }
                i--;
                tmpcol--;
            }
        }

        removeAll();
        _btnMap.clear();

        ImageIcon map = null;
        byte[] ww = new byte[1024];
        try {
            getClass().getResourceAsStream("/myGame/colorjpg/white.jpg").read(ww);
            map = new ImageIcon(ww);
        } catch (IOException e1) {
            System.out.println("map read error!!!");
        }
        
        // redraw
        for (int i = 0; i < MAX_ROW; i++) {
            for (int j = 0; j < MAX_COL; j++) {
                JButton btn = (JButton) _btnList[i][j];

                if (btn == null) {
                    btn = new JButton("", map);
                    btn.setFocusable(false);
                }
                add(btn);

                _btnMap.put(btn, new Point(i, j));
                _btnMap.put(new Point(i, j), btn);
            }
        }

        updateUI();
    }

    /**
     * @author hx0308
     * 
     *         �E�B���h�E - �ݒ� - Java - �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
     */
    private class ClearThread implements Runnable {

        /** �|�C���g���R�[�h���X�g */
        private Point _point = null;

        /**
         * <code>_clearList</code> �̃R�����g
         */
        private List _clearList = null;

        /**
         * @param point
         */
        public ClearThread(Point point) {
            _point = point;
            _clearList = new ArrayList();
        }

        /**
         * �X���b�h�������s���B
         */
        public void run() {

            setClearList(_point, _clearList);
            sort();
            clear(_clearList);
            reDraw();
            setResult(_clearList.size() * _clearList.size());

            _point = null;
            _clearList.clear();
            _clearList = null;
            
//            long date = System.currentTimeMillis();
//            long passtime = System.currentTimeMillis() - date;
//            Calendar.getInstance().setTimeInMillis(passtime);
//            System.out.println("passtime ====> "
//                    + new SimpleDateFormat("mm:ss:SSS").format(new Date(
//                            passtime)));
        }

        /**
         */
        private void sort() {
            int size = _clearList.size();

            for (int i = 0; i < size && size > 1; i++) {
                int site = -1;
                Point tmpPoint = null;
                Point point2 = null;
                Point point1 = (Point) _clearList.get(i);
                for (int j = i + 1; j < size; j++) {
                    point2 = (Point) _clearList.get(j);

                    if (point1.y > point2.y) {
                        site = j;
                        point1 = point2;
                    } else if (point1.y == point2.y && point1.x > point2.x) {
                        site = j;
                        point1 = point2;
                    }
                }

                if (site != -1) {
                    tmpPoint = (Point) _clearList.get(i);
                    _clearList.set(i, _clearList.get(site));
                    _clearList.set(site, tmpPoint);
                }
            }
        }
    }

    /**
     * doOver
     */
    private void doOver() {

        int theNo = -1;
        List hiroList = StartMenu.getHiroList();
        int size = hiroList.size();
        for (int i = 0; i < size; i++) {
            String result = ((String[]) hiroList.get(i))[1];

            if ("--".equals(result)) {
                theNo = i;
                i = size;
            } else if (Integer.parseInt(result) < _result) {
                theNo = i;
                i = size;
            }
        }

        if (theNo != -1) {
            List tempList = hiroList;
            String name = JOptionPane.showInputDialog("Name:", "UnName");

            if (name == null) {
                name = "UnName";
            }

            boolean flag = Pattern.matches("[a-zA-Z0-9]++", name);
            while (!flag) {
                name = JOptionPane.showInputDialog("Name:", "UnName");
                flag = Pattern.matches("[a-zA-Z0-9]++", name);
                JOptionPane.showMessageDialog(this, "Space can't used.");
            }

            String[] value = new String[2];
            value[0] = name;
            value[1] = String.valueOf(_result);

            // set hiro
            tempList.add(theNo, value);
            tempList.remove(StartMenu.HIRO_LIST_SIZE);

            if (StartMenu.setHiroList(tempList)) {
                StartMenu.showHiro(theNo);
            } else {
                StartMenu.setHiroList(hiroList);
                JOptionPane.showMessageDialog(this, "Name Error");
            }
        } else {
            JOptionPane.showMessageDialog(this, "GAME OVER");
            StartMenu.showHiro(-1);
        }
    }

    /**
     * @return _panel
     */
    public ColorClear getPanel() {
        return this;
    }
    
    public void destory() {
        
        if (_colorList != null) {
            _colorList.clear();
        }
        _btnList = null;
        if (_btnMap != null) {
            _btnMap.clear();
            _btnMap = null;
        }
        removeAll();
    }
}