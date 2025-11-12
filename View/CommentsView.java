package View;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import App.Navigator;
import Server.*;
import Resize.Resize;

class Comment extends JPanel {
    Comment (Map<String, String> comment, String id, PostServerClient server) {
        setLayout(new BorderLayout());
        setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.darkGray),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            )
        );
        setBackground(Color.decode("#141414"));

        String comment_id = comment.get("id");
        String msg = comment.get("comment");
        
        JPanel top_panel = new JPanel();
        top_panel.setLayout(new BorderLayout());
        top_panel.setBackground(Color.decode("#141414"));

        JLabel id_label = new JLabel("@ " + comment_id); 
        id_label.setForeground(Color.white);
        id_label.setFont(new Font("Arial", Font.BOLD, 16));
        id_label.setBorder(BorderFactory.createEmptyBorder(0,10,5,0));

        top_panel.add(id_label, BorderLayout.WEST);

        if (id.equals(comment_id)) {
            ImageIcon icon = new ImageIcon(Resize.resizeImage(".src/trashbin_icon.png", 22, 22, 0.5f));
            JButton btn = new JButton(icon);
            btn.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);

            top_panel.add(btn, BorderLayout.EAST);
        }
        add(top_panel, BorderLayout.NORTH);


        //---------------------------------------------------
        JTextArea msg_area = new JTextArea(msg);
        msg_area.setForeground(Color.white);
        msg_area.setBackground(Color.decode("#141414"));
        msg_area.setFont(new Font("Arial", Font.PLAIN, 18));
        msg_area.setBorder(BorderFactory.createEmptyBorder(0,8,5,0));
        msg_area.setForeground(Color.WHITE);

        msg_area.setLineWrap(true);
        msg_area.setWrapStyleWord(true);
        msg_area.setEditable(false);
        add(msg_area, BorderLayout.CENTER);
    }
}

class CommentCenterPanel extends JPanel {
    String id;
    PostServerClient server;
    List<Map<String, String>> comments;

    JPanel listPanel = new JPanel();
    JScrollPane scrollPane;

    CommentCenterPanel(List<Map<String, String>> comments, String id, PostServerClient server) {
        this.id = id;
        this.comments = comments;

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
        if (comments != null){
            for (Map<String, String> comment : comments) {
                Comment p = new Comment(comment, id, server);
                listPanel.add(p);
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
}

class CommentTopPanel extends JPanel {
    CommentTopPanel (Navigator nav, String id, String nName) {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#242424"));

        ImageIcon icon = new ImageIcon(Resize.resizeImage(".src/back_arrow_icon.png", 35, 35, 1));
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
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(Color.white);
        
        panel.add(label);
        add(panel, BorderLayout.CENTER);
    }
}

public class CommentsView extends JFrame{
    PostServerClient server = new PostServerClient();

    public CommentsView(Navigator nav, List<Map<String, String>> comments, String id, String nName){
        setTitle("HongStar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 800);
        setBackground(Color.decode("#141414"));
        setLayout(new BorderLayout());
    
        CommentCenterPanel commentCenterPanel = new CommentCenterPanel(comments, id, server);
        CommentTopPanel commentTopPanel = new CommentTopPanel(nav, id, nName);

        add(commentTopPanel, BorderLayout.NORTH);
        add(commentCenterPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
