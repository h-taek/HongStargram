package View;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.io.*;
import javax.imageio.ImageIO;

import App.Navigator;
import Resize.Resize;
import Server.*;


class Post extends JPanel {
    Map<String, Object> post;
    String id;
    BufferedImage image;

    private class ImagePanel extends JPanel {
        public ImagePanel(String encoded_img) {
            byte[] imageBytes = Base64.getDecoder().decode(encoded_img);
            
            try (InputStream in = new ByteArrayInputStream(imageBytes)) {
                image = ImageIO.read(in);
            }
            catch (IOException e) {
                e.printStackTrace();
                return;
            }

            setPreferredSize(new Dimension(400, 400));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int p = 400;
            int iw = image.getWidth();
            int ih = image.getHeight();

            if (iw > ih){
                int y = (int)((ih - iw) / 2);
                g2d.drawImage(image, 
                            0, 0, p, p, 
                            0, y, iw, ih - y, 
                            null);
            }
            else {
                int x = (int)((iw - ih) / 2);
                g2d.drawImage(image, 
                            0, 0, p, p, 
                            x, 0, iw - x, ih, 
                            null);
            }
        }
    }

    Post(Navigator nav, Map<String, Object> post, String id, String nName) {
        this.post = post;
        this.id = id;

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5,10,0));


        String user_id = post.get("user_id").toString();
        JLabel label = new JLabel("@ " + user_id);
        label.setOpaque(false);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(Color.white);
        topPanel.add(label, BorderLayout.WEST);

        if (user_id.equals(id)) {
            ImageIcon icon = new ImageIcon(Resize.resizeImage(".src/trashbin_icon.png", 20, 20, 1));
            JButton btn = new JButton(icon);
            btn.setOpaque(false);
            btn.setBorderPainted(false);  
            btn.setFocusPainted(false);
            btn.setOpaque(false);
        }
        add(topPanel, BorderLayout.NORTH);


        // -----------------------------------------------------
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.decode("#141414"));

        ImagePanel imagePanel = new ImagePanel(post.get("image").toString());
        
        panel.add(imagePanel);
        
        add(panel, BorderLayout.CENTER);


        // -----------------------------------------------------
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.decode("#141414"));
        bottomPanel.setLayout(new BorderLayout());

        JPanel bottom_btn_panel = new JPanel();
        bottom_btn_panel.setOpaque(false);
        bottom_btn_panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 3));

        List<String> likes = (List<String>) post.get("likes");
        ImageIcon like_icon;
        if (likes.contains(id)){like_icon = new ImageIcon(Resize.resizeImage(".src/heart_icon.png", 25, 25, 1));}
        else{like_icon = new ImageIcon(Resize.resizeImage(".src/heart_line_icon.png", 25, 25, 1));}        
        
        JButton like_btn = new JButton(like_icon);
        like_btn.setOpaque(false);
        like_btn.setBorderPainted(false);  
        like_btn.setFocusPainted(false);
        like_btn.setOpaque(false);
        JLabel like_count_label = new JLabel(likes.size() + "   ");
        like_count_label.setFont(new Font("Arial", Font.BOLD, 18));
        like_count_label.setForeground(Color.white);
        
        bottom_btn_panel.add(like_btn);
        bottom_btn_panel.add(like_count_label);

        JButton comment_btn = new JButton(new ImageIcon(Resize.resizeImage(".src/comment_icon.png", 25, 25, 1)));
        comment_btn.setOpaque(false);
        comment_btn.setBorderPainted(false);  
        comment_btn.setFocusPainted(false);
        comment_btn.setOpaque(false);
        
        List<Map<String, String>> comments = (List<Map<String, String>>) post.get("comments");
        JLabel comment_count_label = new JLabel(comments.size()+"");
        comment_count_label.setFont(new Font("Arial", Font.BOLD, 18));
        comment_count_label.setForeground(Color.white);

        comment_btn.addActionListener(e -> nav.openComments(comments, id, nName));
        
        bottom_btn_panel.add(comment_btn);
        bottom_btn_panel.add(comment_count_label);

        bottomPanel.add(bottom_btn_panel, BorderLayout.NORTH);

        JTextArea content_area = new JTextArea(post.get("content").toString());
        content_area.setLineWrap(true);
        content_area.setWrapStyleWord(true);
        content_area.setEditable(false);
        content_area.setFont(new Font("Arial", Font.PLAIN, 14));
        content_area.setForeground(Color.white);
        content_area.setBackground(Color.decode("#141414"));
        content_area.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        bottomPanel.add(content_area, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }
    
}

class MainTopPanel extends JPanel {
    Navigator nav;
    String id; String nName;
    MainCenterPanel center;

    int s = 30;

    MainTopPanel(Navigator nav, String id, String nName, MainCenterPanel center) {
        this.nav = nav;
        this.id = id;
        this.nName = nName;
        this.center = center;
        
        setBackground(Color.decode("#141414"));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));

        JLabel topLabel = new JLabel("Hongstargram");
        topLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topLabel.setForeground(Color.white);

        add(topLabel, BorderLayout.WEST);
        
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        ImageIcon icon = new ImageIcon(Resize.resizeImage(".src/paper_airplane.png", s+5, s+5, 1));
        JButton messegeBtn = new JButton(icon);
        messegeBtn.setBackground(Color.decode("#141414"));
        messegeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        messegeBtn.setOpaque(true);
        messegeBtn.setContentAreaFilled(true);
        messegeBtn.setBorderPainted(false);
        messegeBtn.setFocusPainted(false);
        
        messegeBtn.addActionListener(e -> {nav.openTotMessage(id, nName);});


        icon = new ImageIcon(Resize.resizeImage(".src/group_icon.png", s, s, 1));
        JButton friendBtn = new JButton(icon);
        friendBtn.setBackground(Color.decode("#141414"));
        friendBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        friendBtn.setOpaque(true);
        friendBtn.setContentAreaFilled(true);
        friendBtn.setBorderPainted(false);
        friendBtn.setFocusPainted(false);
        
        friendBtn.addActionListener(e -> {nav.openFriend(id, nName);});


        icon = new ImageIcon(Resize.resizeImage(".src/restart_icon.png", s-5, s-5, 1));
        JButton refreshBtn = new JButton(icon);
        refreshBtn.setBackground(Color.decode("#141414"));
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
        refreshBtn.setOpaque(true);
        refreshBtn.setContentAreaFilled(true);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);  

        refreshBtn.addActionListener(e -> {center.refresh();});

        btnPanel.add(refreshBtn);
        btnPanel.add(friendBtn);        
        btnPanel.add(messegeBtn);

        add(btnPanel, BorderLayout.EAST);
    }
}

class MainCenterPanel extends JPanel {
    Navigator nav;
    String id; String nName;
    PostServerClient server;


    ScrollableListPanel listPanel = new ScrollableListPanel();
    JScrollPane scrollPane;

    class ScrollableListPanel extends JPanel implements Scrollable {

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return visibleRect.height;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            // 뷰포트 너비에 항상 맞춤 → 프레임 줄이면 같이 줄어듦
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    MainCenterPanel(Navigator nav, String id, String nName, PostServerClient server) {
        this.nav = nav;
        this.id = id; this.nName = nName;
        this.server = server;

        setLayout(new BorderLayout());
        setBackground(Color.decode("#141414"));
        
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.decode("#141414"));

        scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
        
        refresh();
    }

    void refresh() {
        String [] readable_post = server.GetReadablePostRequest(id);
        // System.out.println(Arrays.toString(readable_post));
        listPanel.removeAll();
        if (readable_post != null){
            for (String post_id : readable_post) {
                Map<String, Object> post = server.GetPostRequest(post_id);
                Post p = new Post(nav, post, id, nName);

                listPanel.add(p);
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
}

public class MainView extends JFrame {
    private final Navigator nav;
    PostServerClient server = new PostServerClient();

    public MainView(Navigator nav, String id, String nName){
        this.nav = nav;

        setTitle("HongStar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 800);
        setBackground(Color.decode("#141414"));
        setLayout(new BorderLayout());
    
        MainCenterPanel mainCenterPanel = new MainCenterPanel(nav, id, nName, server);
        MainTopPanel mainTopPanel = new MainTopPanel(nav, id, nName, mainCenterPanel);
 
        add(mainTopPanel, BorderLayout.NORTH);
        add(mainCenterPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}