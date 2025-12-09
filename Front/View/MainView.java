package Front.View;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.io.*;
import javax.imageio.ImageIO;
import java.nio.file.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import Front.App.Navigator;
import Front.Resize.Resize;
import Front.Server.*;

class PostDialogBtnListener extends JPanel implements ActionListener {
    JDialog dialog;
    RoundButton btn;
    JFileChooser chooser;
    JTextArea area;

    public PostDialogBtnListener() {
        setPreferredSize(new Dimension(500, 400));
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("파일 경로...");
        label.setForeground(Color.decode("#262626"));
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        label.setOpaque(false);

        btn = new RoundButton("파일 선택", 10);
        btn.setBackground(Color.decode("#EFEFEF"));
        btn.setForeground(Color.decode("#262626"));
        chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image File", "jpg", "jpeg", "png");
        btn.addActionListener(e -> {
            chooser.setFileFilter(filter);
            chooser.showOpenDialog(getParent());

            label.setText(chooser.getSelectedFile().getPath());
        });

        panel.add(btn);
        panel.add(label);

        add(panel, BorderLayout.NORTH);

        area = new JTextArea();
        area.setLineWrap(true);
        area.setBackground(Color.decode("#FAFAFA"));
        area.setForeground(Color.decode("#262626"));
        area.setCaretColor(Color.decode("#262626"));
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#DBDBDB"), 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        area.setOpaque(true);

        add(area, BorderLayout.CENTER);

        btn = new RoundButton("확인", 10);
        btn.setBackground(Color.decode("#0095F6"));
        btn.setForeground(Color.WHITE);

        JPanel temp_panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        temp_panel.setOpaque(false);
        temp_panel.add(btn);
        add(temp_panel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialog = new JDialog(null, null, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(this);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}

class Post extends JPanel {
    Map<String, Object> post;
    String id;
    BufferedImage image;

    private class ImagePanel extends JPanel {
        public ImagePanel(String encoded_img) {
            byte[] imageBytes = Base64.getDecoder().decode(encoded_img);

            try (InputStream in = new ByteArrayInputStream(imageBytes)) {
                image = ImageIO.read(in);
            } catch (IOException e) {
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

            if (iw > ih) {
                int y = (int) ((ih - iw) / 2);
                g2d.drawImage(image,
                        0, 0, p, p,
                        0, y, iw, ih - y,
                        null);
            } else {
                int x = (int) ((iw - ih) / 2);
                g2d.drawImage(image,
                        0, 0, p, p,
                        x, 0, iw - x, ih,
                        null);
            }
        }
    }

    Post(Navigator nav, Map<String, Object> post, String id, String nName, PostServerClient server) {
        this.post = post;
        this.id = id;

        setOpaque(true);
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#DBDBDB")),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(true);
        topPanel.setBackground(Color.WHITE);
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 0));

        String user_id = post.get("user_id").toString();
        JLabel label = new JLabel("@ " + user_id);
        label.setOpaque(false);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.decode("#262626"));
        topPanel.add(label, BorderLayout.WEST);

        String post_id = post.get("post_id").toString();
        if (user_id.equals(id)) {
            ImageIcon icon = new ImageIcon(Resize.resizeImage("Front/.src/trashbin_icon.png", 20, 20, 0.5f));
            JButton btn = new JButton(icon);
            btn.setOpaque(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);

            btn.addActionListener(e -> {
                Container parent = getParent();
                server.DeletePostRequest(post_id);
                parent.remove(this);
                parent.revalidate();
                parent.repaint();
            });

            topPanel.add(btn, BorderLayout.EAST);
        }
        add(topPanel, BorderLayout.NORTH);

        // -----------------------------------------------------
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        ImagePanel imagePanel = new ImagePanel(post.get("image").toString());

        panel.add(imagePanel);

        add(panel, BorderLayout.CENTER);

        // -----------------------------------------------------
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setLayout(new BorderLayout());

        JPanel bottom_btn_panel = new JPanel();
        bottom_btn_panel.setOpaque(false);
        bottom_btn_panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 3));

        List<String> likes = new Gson().fromJson(post.get("likes").toString(), new TypeToken<List<String>>() {
        }.getType());
        ImageIcon like_icon;
        if (likes.contains(id)) {
            like_icon = new ImageIcon(Resize.resizeImage("Front/.src/heart_icon.png", 25, 25, 1));
        } else {
            like_icon = new ImageIcon(Resize.resizeImage("Front/.src/heart_line.png", 25, 25, 1));
        }

        JButton like_btn = new JButton(like_icon);
        like_btn.setOpaque(false);
        like_btn.setBorderPainted(false);
        like_btn.setFocusPainted(false);
        like_btn.setOpaque(false);
        JLabel like_count_label = new JLabel(likes.size() + "   ");
        like_count_label.setFont(new Font("Arial", Font.PLAIN, 14));
        like_count_label.setForeground(Color.decode("#262626"));

        like_btn.addActionListener(e -> {
            if (likes.contains(id)) {
                likes.remove(id);
                ImageIcon icon = new ImageIcon(Resize.resizeImage("Front/.src/heart_line.png", 25, 25, 1));
                like_btn.setIcon(icon);

                like_count_label.setText((likes.size()) + "   ");
                server.LikeRequest(post_id, id, "false");
            } else {
                likes.add(id);
                ImageIcon icon = new ImageIcon(Resize.resizeImage("Front/.src/heart_icon.png", 25, 25, 1));
                like_btn.setIcon(icon);

                like_count_label.setText((likes.size()) + "   ");
                server.LikeRequest(post_id, id, "true");
            }
        });

        bottom_btn_panel.add(like_btn);
        bottom_btn_panel.add(like_count_label);

        JButton comment_btn = new JButton(new ImageIcon(Resize.resizeImage("Front/.src/communication_line.png", 25, 25, 1)));
        comment_btn.setOpaque(false);
        comment_btn.setBorderPainted(false);
        comment_btn.setFocusPainted(false);
        comment_btn.setOpaque(false);

        List<Map<String, String>> comments = (List<Map<String, String>>) post.get("comments");
        JLabel comment_count_label = new JLabel(comments.size() + "");
        comment_count_label.setFont(new Font("Arial", Font.PLAIN, 14));
        comment_count_label.setForeground(Color.decode("#262626"));

        comment_btn.addActionListener(e -> nav.openComments(comments, id, nName, post.get("post_id").toString()));

        bottom_btn_panel.add(comment_btn);
        bottom_btn_panel.add(comment_count_label);

        bottomPanel.add(bottom_btn_panel, BorderLayout.NORTH);

        JTextArea content_area = new JTextArea(post.get("content").toString());
        content_area.setLineWrap(true);
        content_area.setWrapStyleWord(true);
        content_area.setEditable(false);
        content_area.setFont(new Font("Arial", Font.PLAIN, 14));
        content_area.setForeground(Color.decode("#262626"));
        content_area.setBackground(Color.WHITE);
        content_area.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        bottomPanel.add(content_area, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

}

class MainTopPanel extends JPanel {
    Navigator nav;
    String id;
    String nName;
    MainCenterPanel center;

    int s = 30;

    MainTopPanel(Navigator nav, String id, String nName, MainCenterPanel center) {
        this.nav = nav;
        this.id = id;
        this.nName = nName;
        this.center = center;

        setBackground(Color.decode("#FAFAFA"));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#DBDBDB")));

        JLabel topLabel = new JLabel("Hongstargram");
        topLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topLabel.setForeground(Color.decode("#262626"));
        topLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        add(topLabel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        ImageIcon icon = new ImageIcon(Resize.resizeImage("Front/.src/paper_airplane.png", s + 5, s + 5, 1));
        JButton messegeBtn = new JButton(icon);
        messegeBtn.setBackground(Color.decode("#FAFAFA"));
        messegeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        messegeBtn.setOpaque(true);
        messegeBtn.setContentAreaFilled(true);
        messegeBtn.setBorderPainted(false);
        messegeBtn.setFocusPainted(false);

        messegeBtn.addActionListener(e -> {
            nav.openTotMessage(id, nName);
        });

        icon = new ImageIcon(Resize.resizeImage("Front/.src/network_line.png", s, s, 1));
        JButton friendBtn = new JButton(icon);
        friendBtn.setBackground(Color.decode("#FAFAFA"));
        friendBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        friendBtn.setOpaque(true);
        friendBtn.setContentAreaFilled(true);
        friendBtn.setBorderPainted(false);
        friendBtn.setFocusPainted(false);

        friendBtn.addActionListener(e -> {
            nav.openFriend(id, nName);
        });

        icon = new ImageIcon(Resize.resizeImage("Front/.src/restart_icon_black.png", s - 5, s - 5, 1));
        JButton refreshBtn = new JButton(icon);
        refreshBtn.setBackground(Color.decode("#FAFAFA"));
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        refreshBtn.setOpaque(true);
        refreshBtn.setContentAreaFilled(true);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);

        refreshBtn.addActionListener(e -> {
            center.refresh();
        });

        icon = new ImageIcon(Resize.resizeImage("Front/.src/post_line.png", s, s, 1));
        JButton postBtn = new JButton(icon);
        postBtn.setBackground(Color.decode("#FAFAFA"));
        postBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        postBtn.setOpaque(true);
        postBtn.setContentAreaFilled(true);
        postBtn.setBorderPainted(false);
        postBtn.setFocusPainted(false);

        PostDialogBtnListener dialog = new PostDialogBtnListener();
        dialog.btn.addActionListener(e -> {
            String content = dialog.area.getText();
            File img = dialog.chooser.getSelectedFile();
            try {
                String encoded_img = Base64.getEncoder().encodeToString(Files.readAllBytes(img.toPath()));
                center.server.AddPostRequest(id, content, encoded_img);
                center.refresh();
                dialog.dialog.dispose();
            } catch (IOException ec) {
                JOptionPane.showMessageDialog(
                        null,
                        "이미지 파일을 찾을 수 없습니다.",
                        "경고", JOptionPane.WARNING_MESSAGE);
            }
        });

        postBtn.addActionListener(dialog);

        btnPanel.add(postBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(friendBtn);
        btnPanel.add(messegeBtn);

        add(btnPanel, BorderLayout.EAST);
    }
}

class MainCenterPanel extends JPanel {
    Navigator nav;
    String id;
    String nName;
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
        this.id = id;
        this.nName = nName;
        this.server = server;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        refresh();
    }

    void refresh() {
        String[] readable_post = server.GetReadablePostRequest(id);
        // System.out.println(Arrays.toString(readable_post));
        listPanel.removeAll();
        if (readable_post != null && readable_post.length > 0) {
            for (String post_id : readable_post) {
                Map<String, Object> post = server.GetPostRequest(post_id);
                Post p = new Post(nav, post, id, nName, server);

                listPanel.add(p);
            }
        } else {
            // 게시글이 없을 때 안내 메시지 표시
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new GridBagLayout());
            emptyPanel.setBackground(Color.WHITE);
            emptyPanel.setPreferredSize(new Dimension(500, 600));

            JLabel emptyLabel = new JLabel("No Post... :(");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            emptyLabel.setForeground(Color.decode("#8E8E8E"));

            emptyPanel.add(emptyLabel);
            listPanel.add(emptyPanel);
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
}

// FAB (Floating Action Button) 클래스
class FloatingActionButton extends JButton {
    private boolean menuVisible = false;
    private JPanel menuPanel;

    public FloatingActionButton(String text) {
        super(text);
        setPreferredSize(new Dimension(60, 60));
        setFont(new Font("Arial", Font.BOLD, 30));
        setForeground(Color.WHITE);
        setBackground(Color.decode("#0095F6"));
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 그림자 효과
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillOval(3, 3, getWidth() - 6, getHeight() - 6);
        
        // 버튼 배경
        g2.setColor(getBackground());
        g2.fillOval(0, 0, getWidth() - 6, getHeight() - 6);
        
        g2.dispose();
        super.paintComponent(g);
    }

    public void setMenuPanel(JPanel panel) {
        this.menuPanel = panel;
    }

    public void toggleMenu() {
        if (menuPanel != null) {
            menuVisible = !menuVisible;
            menuPanel.setVisible(menuVisible);
        }
    }
}

public class MainView extends JFrame {
    PostServerClient server = new PostServerClient();

    public MainView(Navigator nav, String id, String nName) {
        setTitle("HongStar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 800);
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        MainCenterPanel mainCenterPanel = new MainCenterPanel(nav, id, nName, server);
        MainTopPanel mainTopPanel = new MainTopPanel(nav, id, nName, mainCenterPanel);

        add(mainTopPanel, BorderLayout.NORTH);
        add(mainCenterPanel, BorderLayout.CENTER);

        // FAB와 메뉴를 담을 레이어 패널 생성
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(500, 800));

        // 메인 컨텐츠를 레이어에 추가
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBounds(0, 0, 500, 800);
        contentPanel.add(mainTopPanel, BorderLayout.NORTH);
        contentPanel.add(mainCenterPanel, BorderLayout.CENTER);
        layeredPane.add(contentPanel, JLayeredPane.DEFAULT_LAYER);

        // FAB 메뉴 패널 생성
        JPanel fabMenuPanel = new JPanel();
        fabMenuPanel.setLayout(new BoxLayout(fabMenuPanel, BoxLayout.Y_AXIS));
        fabMenuPanel.setBackground(new Color(255, 255, 255, 240));
        fabMenuPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#DBDBDB"), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        fabMenuPanel.setVisible(false);

        // 루틴 달력 버튼
        RoundButton routineButton = new RoundButton("📅 루틴 달력", 10);
        routineButton.setPreferredSize(new Dimension(150, 45));
        routineButton.setMaximumSize(new Dimension(150, 45));
        routineButton.setBackground(Color.decode("#0095F6"));
        routineButton.setForeground(Color.WHITE);
        routineButton.addActionListener(e -> {
            nav.openRoutine(id, nName);
            fabMenuPanel.setVisible(false);
        });

        // 위치 지도 버튼
        RoundButton locationButton = new RoundButton("🗺️ 위치 지도", 10);
        locationButton.setPreferredSize(new Dimension(150, 45));
        locationButton.setMaximumSize(new Dimension(150, 45));
        locationButton.setBackground(Color.decode("#00C853"));
        locationButton.setForeground(Color.WHITE);
        locationButton.addActionListener(e -> {
            nav.openLocation(id, nName);
            fabMenuPanel.setVisible(false);
        });

        fabMenuPanel.add(routineButton);
        fabMenuPanel.add(Box.createVerticalStrut(10));
        fabMenuPanel.add(locationButton);

        // 메뉴 위치: 오른쪽 하단, 화면 안쪽 여유 확보
        fabMenuPanel.setBounds(300, 620, 170, 120);
        layeredPane.add(fabMenuPanel, JLayeredPane.POPUP_LAYER);

        // FAB 버튼 생성 - 오른쪽 하단, 화면 안쪽 여유 확보
        FloatingActionButton fab = new FloatingActionButton("+");
        fab.setBounds(410, 700, 60, 60);
        fab.setMenuPanel(fabMenuPanel);
        fab.addActionListener(e -> fab.toggleMenu());
        layeredPane.add(fab, JLayeredPane.PALETTE_LAYER);

        // 레이어 패널을 프레임에 추가
        setContentPane(layeredPane);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}