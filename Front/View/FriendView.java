package Front.View;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Front.App.Navigator;
import Front.Server.*;
import Front.Resize.*;

// 상단 메뉴 패널
class FreindTopPanel extends JPanel {
    Navigator nav;
    String id;
    String nName;
    InfoServerClient server;

    public FreindTopPanel(TotFreindPanel totFreindPanel, String nName) {
        this.nav = totFreindPanel.nav;
        this.id = totFreindPanel.id;
        this.nName = nName;
        this.server = totFreindPanel.server;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(Color.decode("#FAFAFA"));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#DBDBDB")));

        Icon icon = new ImageIcon(Resize.resizeImage("Front/.src/left-arrow_line.png", 30, 30, 1));
        JButton back_btn = new JButton(icon);
        back_btn.setBackground(Color.decode("#FAFAFA"));
        back_btn.setBorderPainted(false); // 테두리(border) 없애기
        back_btn.setFocusPainted(false); // 포커스(점선 테두리) 표시 제거

        back_btn.addActionListener(e -> nav.openMain(id, nName));

        add(back_btn);
        add(Box.createHorizontalGlue());

        icon = new ImageIcon(Resize.resizeImage("Front/.src/add_line.png", 30, 30, 1));
        JButton add_friend_btn = new JButton(icon);
        add_friend_btn.setBackground(Color.decode("#FAFAFA"));
        add_friend_btn.setBorderPainted(false); // 테두리(border) 없애기
        add_friend_btn.setFocusPainted(false); // 포커스(점선 테두리) 표시 제거

        JDialogBtnListener dialog = new JDialogBtnListener("친구로 추가할 ID를 입력하세요");
        dialog.btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String receiver = dialog.field.getText();
                String re_nName = server.GetNNameRequest(receiver);
                if (re_nName == null || id.equals(receiver)) {
                    JOptionPane.showMessageDialog(null, "잘못된 요청입니다.",
                            "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Map<String, String[]> request_list = server.GetFriendRequestRequest(id);
                for (String[] arr : request_list.values()) {
                    for (String r_id : arr) {
                        if (r_id.equals(receiver)) {
                            JOptionPane.showMessageDialog(null, "요청/신청이 존재합니다.",
                                    "경고", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                }
                String[] id_list = server.GetFriendListRequest(id);
                if (id_list != null) {
                    for (String id_ : id_list) {
                        if (id_.equals(receiver)) {
                            JOptionPane.showMessageDialog(null, "이미 친구인 사용자입니다.",
                                    "경고", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                }

                server.FriendRequestRequest(id, receiver, true);
                JOptionPane.showMessageDialog(null, "요청 성공", "", JOptionPane.INFORMATION_MESSAGE);
                totFreindPanel.refresh();
                dialog.dialog.dispose();
            }
        });
        add_friend_btn.addActionListener(dialog);
        add(add_friend_btn);

        icon = new ImageIcon(Resize.resizeImage("Front/.src/restart_icon_black.png", 30, 30, 1));
        JButton refresh_btn = new JButton(icon);
        refresh_btn.setBackground(Color.decode("#FAFAFA"));
        refresh_btn.setBorderPainted(false); // 테두리(border) 없애기
        refresh_btn.setFocusPainted(false); // 포커스(점선 테두리) 표시 제거
        refresh_btn.addActionListener(e -> totFreindPanel.refresh());
        add(refresh_btn);
    }
}

class FriendPanel extends JPanel {
    Navigator nav;
    String id;
    String name;

    FriendPanel(Navigator nav, String id, String name) {
        this.nav = nav;
        this.id = id;
        this.name = name;

        setLayout(new BorderLayout(10, 10));
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#DBDBDB")));

        ImageIcon icon = new ImageIcon(Resize.resizeImage("Front/.src/friends_line.png", 40, 40, 1));
        JLabel imgLabel = new JLabel(icon);
        imgLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        imgLabel.setOpaque(false);
        add(imgLabel, BorderLayout.WEST);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(Color.decode("#262626"));
        nameLabel.setOpaque(false);
        nameLabel.setFont(new Font("Noto Sans KR", Font.BOLD, 16));

        JLabel idLabel = new JLabel("@ " + id);
        idLabel.setForeground(Color.decode("#8E8E8E"));
        idLabel.setFont(new Font("Noto Sans KR", Font.PLAIN, 13));
        idLabel.setOpaque(false);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        textPanel.add(nameLabel);
        textPanel.add(idLabel);

        add(textPanel, BorderLayout.CENTER);

        setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
    }

    JPanel makeBtnPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 10));

        return buttonPanel;
    }
}

// 친구 요청 응답
class FriendRequestPanel extends FriendPanel {
    Navigator nav;
    String my_id;
    String your_id;
    String name;
    InfoServerClient server;

    FriendRequestPanel(TotFreindPanel totFreindPanel, String your_id, String name) {
        super(totFreindPanel.nav, your_id, name);
        this.nav = totFreindPanel.nav;
        this.my_id = totFreindPanel.id;
        this.your_id = your_id;
        this.name = name;
        this.server = totFreindPanel.server;

        RoundButton acceptBtn = new RoundButton("수락", 15);
        acceptBtn.setFont(new Font("Noto Sans KR", Font.PLAIN, 12));
        acceptBtn.setMargin(new Insets(0, 0, 0, 0));
        acceptBtn.setBackground(Color.decode("#0095F6"));
        acceptBtn.setForeground(Color.white);
        acceptBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        acceptBtn.addActionListener(e -> {
            server.FriendRequestResponseRequest(my_id, your_id, true);
            totFreindPanel.refresh();
        });

        RoundButton refuseBtn = new RoundButton("거절", 15);
        refuseBtn.setFont(new Font("Noto Sans KR", Font.PLAIN, 12));
        refuseBtn.setMargin(new Insets(0, 0, 0, 0));
        refuseBtn.setBackground(Color.GRAY);
        refuseBtn.setForeground(Color.white);
        refuseBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        refuseBtn.addActionListener(e -> {
            server.FriendRequestResponseRequest(my_id, your_id, false);
            totFreindPanel.refresh();
        });

        JPanel buttonPanel = makeBtnPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.add(acceptBtn);
        buttonPanel.add(refuseBtn);

        add(buttonPanel, BorderLayout.EAST);
    }
}

// 친구 추가
class FriendAddPanel extends FriendPanel {
    Navigator nav;
    String my_id;
    String your_id;
    String name;
    InfoServerClient server;

    FriendAddPanel(TotFreindPanel totFreindPanel, String your_id, String name) {
        super(totFreindPanel.nav, your_id, name);
        this.nav = totFreindPanel.nav;
        this.my_id = totFreindPanel.id;
        this.your_id = your_id;
        this.name = name;
        this.server = totFreindPanel.server;

        RoundButton addBtn = new RoundButton("취소", 15);
        addBtn.setFont(new Font("Noto Sans KR", Font.PLAIN, 12));
        addBtn.setMargin(new Insets(0, 0, 0, 0));
        addBtn.setBackground(Color.GRAY);
        addBtn.setForeground(Color.white);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        addBtn.addActionListener(e -> {
            server.FriendRequestRequest(my_id, your_id, false);
            totFreindPanel.refresh();
        });

        JPanel buttonPanel = makeBtnPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.add(addBtn);

        add(buttonPanel, BorderLayout.EAST);
    }
}

class TotFreindPanel extends JPanel {
    private JPanel listPanel;
    private JScrollPane scrollPane;

    Navigator nav;
    String id;
    InfoServerClient server;

    TotFreindPanel(Navigator nav, String id, InfoServerClient server) {
        this.nav = nav;
        this.id = id;
        this.server = server;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        refresh();
    }

    class ListLabelPanel extends JPanel {
        ListLabelPanel(String text) {
            setLayout(new BorderLayout());
            setOpaque(false);

            JLabel label = new JLabel(text);
            label.setForeground(Color.decode("#262626"));
            label.setFont(new Font("Noto Sans KR", Font.BOLD, 20));
            label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setHorizontalAlignment(SwingConstants.LEFT);

            add(label, BorderLayout.WEST);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, label.getPreferredSize().height));
        }
    }

    void refresh() {
        listPanel.removeAll();

        ListLabelPanel label = new ListLabelPanel("친구 목록");
        listPanel.add(label);

        String[] ids = server.GetFriendListRequest(id);
        if (ids != null) {
            for (String id : ids) {
                String nName = server.GetNNameRequest(id);
                FriendPanel friendPanel = new FriendPanel(nav, id, nName);
                listPanel.add(friendPanel);
            }
        }

        Map<String, String[]> id_list = server.GetFriendRequestRequest(id);

        // 요청 목록
        label = new ListLabelPanel("요청 목록");
        listPanel.add(label);

        if (id_list != null && id_list.get("receive_request") != null) {
            ids = id_list.get("receive_request");
            if (ids != null) {
                for (String id_ : ids) {
                    String nName = server.GetNNameRequest(id_);
                    FriendRequestPanel friendRequestPanel = new FriendRequestPanel(this, id_, nName);
                    listPanel.add(friendRequestPanel);
                }
            }
        }

        // 신청 목록
        label = new ListLabelPanel("신청 목록");
        listPanel.add(label);

        if (id_list != null && id_list.get("sent_request") != null) {
            ids = id_list.get("sent_request");
            if (ids != null) {
                for (String id_ : ids) {
                    String nName = server.GetNNameRequest(id_);
                    FriendAddPanel friendRequestPanel = new FriendAddPanel(this, id_, nName);
                    listPanel.add(friendRequestPanel);
                }
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }
}

public class FriendView extends JFrame {
    Navigator nav;
    String id;
    String nName;

    private InfoServerClient server = new InfoServerClient();

    public FriendView(Navigator nav, String id, String nName) {
        this.nav = nav;
        this.id = id;
        this.nName = nName;

        setTitle("HongStar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(500, 800);
        setBackground(Color.WHITE);

        final TotFreindPanel friendList = new TotFreindPanel(nav, id, server);
        add(friendList, BorderLayout.CENTER);

        final FreindTopPanel top = new FreindTopPanel(friendList, nName);
        add(top, BorderLayout.NORTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
