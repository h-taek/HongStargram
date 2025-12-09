package Front.View;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import Front.App.Navigator;
import Front.Resize.Resize;
import Front.Server.*;

class Label extends JLabel {
    public Label(String text) {
        super(text);
    }

    @Override
    public Dimension getMaximumSize() {
        int availW = -1;
        Container p = getParent();
        if (p != null) {
            Insets pin = p.getInsets();
            availW = p.getWidth() - pin.left - pin.right;
        }
        if (availW <= 0) {
            availW = 480;
        } // 초기값
        super.setSize(new Dimension(availW, Short.MAX_VALUE));

        FontMetrics fm = getFontMetrics(getFont());
        int line_height = fm.getHeight();
        Insets in = getInsets();

        int h = in.top + in.bottom + line_height;

        // 폭은 레이아웃에 맡기고, 최소/최대는 상황에 따라 조절
        return new Dimension(availW, h);
    }
}

class TextArea extends JTextArea {
    public TextArea(String text) {
        super(text);
        setLineWrap(true);
        setWrapStyleWord(true);
    }

    @Override
    public Dimension getMaximumSize() {
        int availW = -1;
        Container p = getParent();
        if (p != null) {
            Insets pin = p.getInsets();
            availW = p.getWidth() - pin.left - pin.right;
        }
        if (availW <= 0) {
            availW = 480;
        } // 초기값
        super.setSize(new Dimension(availW, Short.MAX_VALUE));

        FontMetrics fm = getFontMetrics(getFont());
        int lines = Math.max(1, getLineCount());
        int line_height = fm.getHeight();
        Insets in = getInsets();

        int h = in.top + in.bottom + (line_height * lines);

        // 폭은 레이아웃에 맡기고, 최소/최대는 상황에 따라 조절
        return new Dimension(availW, h);
    }
}

class Comment extends JPanel {
    Comment(Map<String, String> comment, PostServerClient server, CommentCenterPanel center) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#DBDBDB")),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        setBackground(Color.WHITE);

        String user_id = comment.get("user_id");
        String msg = comment.get("content");
        String comment_id = comment.get("comment_id");

        JPanel top_panel = new JPanel();
        top_panel.setLayout(new BoxLayout(top_panel, BoxLayout.X_AXIS));
        top_panel.setBackground(Color.WHITE);

        Label id_label = new Label("@ " + user_id);
        id_label.setForeground(Color.decode("#262626"));
        id_label.setFont(new Font("Arial", Font.BOLD, 14));
        id_label.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));

        top_panel.add(id_label);
        top_panel.add(Box.createHorizontalGlue());

        if (center.id.equals(user_id)) {
            ImageIcon icon = new ImageIcon(Resize.resizeImage("Front/.src/trashbin_icon.png", 22, 22, 0.5f));
            JButton btn = new JButton(icon);
            btn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.addActionListener(e -> {
                server.DeleteCommentRequest(comment_id);

                Map<String, Object> post = server.GetPostRequest(center.post_id);
                List<Map<String, String>> comments = (List<Map<String, String>>) post.get("comments");

                center.refresh(comments);
            });

            top_panel.add(btn);
        }
        add(top_panel);

        // ---------------------------------------------------
        TextArea msg_area = new TextArea(msg);
        msg_area.setForeground(Color.decode("#262626"));
        msg_area.setBackground(Color.WHITE);
        msg_area.setFont(new Font("Arial", Font.PLAIN, 14));

        msg_area.setLineWrap(true);
        msg_area.setWrapStyleWord(true);
        msg_area.setEditable(false);
        add(msg_area);
    }
}

class CommentCenterPanel extends JPanel {
    String id;
    String post_id;
    PostServerClient server;

    JPanel listPanel = new JPanel();
    JScrollPane scrollPane;

    CommentCenterPanel(List<Map<String, String>> comments, String id, String post_id, PostServerClient server) {
        this.id = id;
        this.post_id = post_id;
        this.server = server;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        refresh(comments);
    }

    void refresh(List<Map<String, String>> comments) {
        listPanel.removeAll();
        if (comments != null) {
            for (Map<String, String> comment : comments) {
                Comment p = new Comment(comment, server, this);
                listPanel.add(p);
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
}

class CommentTopPanel extends JPanel {
    CommentTopPanel(Navigator nav, String id, String nName) {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#FAFAFA"));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#DBDBDB")));

        ImageIcon icon = new ImageIcon(Resize.resizeImage("Front/.src/left-arrow_line.png", 35, 35, 1));
        JButton btn = new JButton(icon);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

        btn.addActionListener(e -> nav.openMain(id, nName));
        add(btn, BorderLayout.WEST);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(false);

        JLabel label = new JLabel("Comments");
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(Color.decode("#262626"));

        panel.add(label);
        add(panel, BorderLayout.CENTER);
    }
}

class CommentBottomPanel extends JPanel {
    RoundTextField field;
    PostServerClient server;
    CommentCenterPanel center;
    String post_id;

    CommentBottomPanel(CommentCenterPanel center, PostServerClient server, String post_id) {
        this.server = server;
        this.center = center;
        this.post_id = post_id;

        setLayout(new BorderLayout(6, 0));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setBackground(Color.WHITE);

        field = new RoundTextField(15);
        field.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        field.setBackground(Color.decode("#FAFAFA"));
        field.setForeground(Color.decode("#262626"));
        field.setCaretColor(Color.decode("#262626"));
        field.addActionListener(e -> {
            listner();
        });
        add(field, BorderLayout.CENTER);

        RoundButton btn = new RoundButton("확인", 15);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        btn.setBackground(Color.decode("#0095F6"));
        btn.setForeground(Color.WHITE);
        btn.addActionListener(e -> {
            listner();
        });
        add(btn, BorderLayout.EAST);
    }

    void listner() {
        String text = field.getText();
        if (text != null) {
            server.AddCommentRequest(center.post_id, center.id, text);
            field.setText("");
        }

        Map<String, Object> post = server.GetPostRequest(post_id);
        List<Map<String, String>> comments = (List<Map<String, String>>) post.get("comments");
        center.refresh(comments);
    }
}

public class CommentsView extends JFrame {
    PostServerClient server = new PostServerClient();

    public CommentsView(Navigator nav, List<Map<String, String>> comments, String id, String nName, String post_id) {
        setTitle("HongStar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 800);
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        CommentCenterPanel commentCenterPanel = new CommentCenterPanel(comments, id, post_id, server);
        CommentTopPanel commentTopPanel = new CommentTopPanel(nav, id, nName);
        CommentBottomPanel commentBottomPanel = new CommentBottomPanel(commentCenterPanel, server, post_id);

        add(commentTopPanel, BorderLayout.NORTH);
        add(commentCenterPanel, BorderLayout.CENTER);
        add(commentBottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
