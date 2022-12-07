import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.awt.event.KeyEvent;

public class Main {
    public static void main(String[] args) {
        new Main();
    }
    static int horizontal_ratio = 0;
    static int vertical_ratio = 0;
    static int screen_width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    static int screen_height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    static {
        int[] match = match(divisor(screen_width), divisor(screen_height));
        horizontal_ratio = screen_width / match[match.length - 1];
        vertical_ratio = screen_height / match[match.length - 1];
    }
    boolean recording = false;
    public Main() {
        new Thread(new Runnable() {
            int size = 25;
            int show_time = 0;
            List<BufferedImage> recording_images = null;
            @Override public void run() {
                JFrame main_window = new JFrame() {
                    {
                        setTitle("Screen_Cupture - now window");
                        setDefaultCloseOperation(EXIT_ON_CLOSE);
                        setLocationRelativeTo(null);
                        setResizable(false);
                        addKeyListener(new KeyListener() {
                            @Override public void keyTyped(KeyEvent e) { }

                            @Override public void keyPressed(KeyEvent e) { 
                                if (KeyEvent.VK_UP == e.getKeyCode()) {
                                    size += 5;
                                    getContentPane().setPreferredSize(new Dimension((int) (screen_width * (size / 100d)) + 20, (int) (screen_height * (size / 100d)) + 20));
                                    pack();
                                    show_time = 40;
                                } else if (KeyEvent.VK_DOWN == e.getKeyCode() && size > 10) {
                                    size -= 5;
                                    getContentPane().setPreferredSize(new Dimension((int) (screen_width * (size / 100d)) + 20, (int) (screen_height * (size / 100d)) + 20));
                                    pack();
                                    show_time = 40;
                                } else if (KeyEvent.VK_SPACE == e.getKeyCode()) {
                                    if (recording) {
                                        new Thread(new Runnable() {
                                            int size = 25;
                                            int show_time = 0;
                                            List<BufferedImage> recording_data = recording_images;
                                            int data_index = 0;
                                            boolean press_shift = false;
                                            JFrame main_window = null;
                                            @Override public void run() {
                                                main_window = new JFrame() {
                                                    {
                                                        setTitle("Screen_Cupture - preview");
                                                        setLocationRelativeTo(null);
                                                        setResizable(false);
                                                        addKeyListener(new KeyListener() {
                                                            @Override public void keyTyped(KeyEvent e) { }

                                                            @Override public void keyPressed(KeyEvent e) { 
                                                                if (KeyEvent.VK_UP == e.getKeyCode()) {
                                                                    size += 5;
                                                                    getContentPane().setPreferredSize(new Dimension((int) (screen_width * (size / 100d)) + 20, (int) (screen_height * (size / 100d)) + 20));
                                                                    pack();
                                                                    show_time = 40;
                                                                }
                                                                if (KeyEvent.VK_DOWN == e.getKeyCode() && size > 10) {
                                                                    size -= 5;
                                                                    getContentPane().setPreferredSize(new Dimension((int) (screen_width * (size / 100d)) + 20, (int) (screen_height * (size / 100d)) + 20));
                                                                    pack();
                                                                    show_time = 40;
                                                                }
                                                                if (KeyEvent.VK_LEFT == e.getKeyCode() && data_index > 0) {
                                                                    data_index--;
                                                                }
                                                                if (KeyEvent.VK_RIGHT == e.getKeyCode() && data_index < recording_data.size() - 1) {
                                                                    data_index++;
                                                                }
                                                                if (KeyEvent.VK_SPACE == e.getKeyCode()) {
                                                                    if (press_shift) {
                                                                        JFileChooser filechooser = new JFileChooser(System.getProperty("user.home") + "/Desktop");
                                                                        filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                                                                        int selected = filechooser.showSaveDialog(main_window);
                                                                        if (selected == JFileChooser.APPROVE_OPTION) { 
                                                                            try {
                                                                                ImageIO.write(recording_data.get(data_index), "png", new File(filechooser.getSelectedFile().getAbsolutePath() + (filechooser.getSelectedFile().getName().contains(".png") ? "" : ".png")));
                                                                            } catch (IOException e1) {
                                                                                e1.printStackTrace();
                                                                            }
                                                                        }
                                                                    }else {
                                                                        try {
                                                                            ImageIO.write(recording_data.get(data_index), "png", new File(System.getProperty("user.home") + "/Desktop/" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".png"));
                                                                        } catch (IOException e1) {
                                                                            e1.printStackTrace();
                                                                        }
                                                                    }
                                                                } 
                                                                if (KeyEvent.VK_SHIFT == e.getKeyCode()) {
                                                                    press_shift = true;
                                                                }
                                                            }

                                                            @Override public void keyReleased(KeyEvent e) {
                                                                if (KeyEvent.VK_SHIFT == e.getKeyCode()) {
                                                                    press_shift = false;
                                                                }
                                                            }
                                                        });
                                                        add(new JPanel() {
                                                            {
                                                                setPreferredSize(new Dimension((int) (screen_width * (size / 100d)), (int) (screen_height * (size / 100d))));
                                                            }
                                                            @Override public void paintComponent(Graphics g) {
                                                                super.paintComponent(g);
                                                                if (size > 0) {
                                                                    g.drawImage(recording_data.get(data_index), 0, 0, getWidth(), getHeight(), null);
                                                                }
                                                                if (show_time > 0) {
                                                                    g.setColor(new Color(255, 255, 255, Math.min(16, show_time) * 16 - 1));
                                                                    g.drawString(size + "%", getWidth() - 30, getHeight() - 10);
                                                                }
                                                                g.setColor(Color.WHITE);
                                                                g.drawString((data_index + 1) + "/" + recording_data.size(), 0, getHeight() - 10);
                                                                show_time--;
                                                            }
                                                        });
                                                        pack();
                                                        setVisible(true);
                                                    }
                                                };
                                                while (true) {
                                                    main_window.repaint();
                                                }
                                            }
                                        }) {{
                                            start();
                                        }};
                                    } else {
                                        recording_images = new ArrayList<>();
                                    }
                                    recording = !recording;
                                }
                            }

                            @Override public void keyReleased(KeyEvent e) { }
                        });
                        add(new JPanel() {
                            {
                                setPreferredSize(new Dimension((int) (screen_width * (size / 100d)) + 20, (int) (screen_height * (size / 100d)) + 20));
                            }
                            @Override public void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                if (recording) {
                                    g.setColor(new Color(244, 81, 30));
                                } else {
                                    g.setColor(Color.BLACK);
                                }
                                g.fillRect(0, 0, getWidth(), getHeight());
                                if (size > 0) {
                                    g.drawImage(screen_capture(), 10, 10, getWidth() - 20, getHeight() - 20, null);
                                }
                                if (show_time > 0) {
                                    g.setColor(new Color(255, 255, 255, Math.min(16, show_time) * 16 - 1));
                                    g.drawString(size + "%", getWidth() - 30, getHeight() - 10);
                                }
                                if (recording) {
                                    recording_images.add(screen_capture());
                                }
                                show_time--;
                            }
                        });
                        pack();
                        setVisible(true);
                    }
                };
                while (true) {
                    main_window.repaint();
                }
            }
        }) {{
            start();
        }};
    }
    public BufferedImage screen_capture() {
        try {
            return new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        } catch (Exception e) {
            System.out.println("キャプチャの取得に失敗しました。" + e.getMessage());
        }
        return null;
    }
    private static int[] divisor(int n) {
        int i = 0;

        for (int a = 2;a < n - 1;a++) {
            if (n / (double) a == (int) (n / (double) a)) {
                i++;
            }
        }

        int[] divisor = new int[i];

        i = 0;
        for (int a = 2;a < n - 1;a++) {
            if (n / (double) a == (int) (n / (double) a)) {
                divisor[i] = a;
                i++;
            }
        }

        return divisor;
    }

    private static int[] match(int[] a, int[] b) {
        int[] d = null;
        int[] e = null;

        if (a.length < b.length) {
            d = a;
            e = b;
        } else {
            d = b;
            e = a;
        }

        int n = 0;
        for (int c = 0;c != d.length;c++) {
            if (charAt(e, d[c]) != -1) {
                n++;
            }
        }

        int[] m = new int[n];
        n = 0;
        for (int c = 0;c != d.length;c++) {
            if (charAt(e, d[c]) != -1) {
                m[n] = d[c];
                n++;
            }
        }
        return m;
    }
    private static int charAt(int[] a, int b) {
        for (int c = 0;c != a.length;c++) {
            if (a[c] == b) {
                return a[c];
            }
        }
        return -1;
    }
}