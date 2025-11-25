package Front.View;

import java.util.*;

import javax.swing.*;
import javax.swing.Timer;

import Front.App.Navigator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Front.Server.*;

class FreindTopPanel extends JPanel {
    Navigator nav;
    String id;
    String nName;
    InfoServerClient server;
    

    public FreindTopPanel(Navigator nav, String id, String nName, InfoServerClient server) {
        this.nav = nav;
        this.id = id;
        this.nName = nName;
        this.server = server;

        setLayout(new BorderLayout(5, 0));
        setBackground(Color.decode("#242424"));

        Icon icon = new ImageIcon("Front/.src/back_arrow_icon.png");
        JButton back_btn = new JButton(icon);
        back_btn.setContentAreaFilled(false);   // 버튼의 배경을 칠하지 않게    
        back_btn.setBorderPainted(false);       // 테두리(border) 없애기
        back_btn.setFocusPainted(false);        // 포커스(점선 테두리) 표시 제거
        back_btn.setOpaque(false);       // 투명도 완전하게 만들기 (배경색 투명)

        back_btn.addActionListener(e -> nav.openMain(id, nName));
        
        add(back_btn, BorderLayout.WEST);

        icon = new ImageIcon("Front/.src/add_friend_icon.png");
        JButton add_friend_btn = new JButton(icon);
        add_friend_btn.setContentAreaFilled(false);   // 버튼의 배경을 칠하지 않게    
        add_friend_btn.setBorderPainted(false);       // 테두리(border) 없애기
        add_friend_btn.setFocusPainted(false);        // 포커스(점선 테두리) 표시 제거
        add_friend_btn.setOpaque(false);       // 투명도 완전하게 만들기 (배경색 투명)

        JDialogBtnListener dialog = new JDialogBtnListener("친구로 추가할 ID를 입력하세요");
        dialog.btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // String se_nName = server.GetNNameRequest(id);
                
                String receiver = dialog.field.getText();
                String re_nName = server.GetNNameRequest(receiver);
                if (re_nName == null || id.equals(receiver)) {
                    JOptionPane.showMessageDialog(null, "잘못된 요청입니다.",
                                "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Map<String, String[]> request_list = server.GetFriendRequestRequest(id);
                for (String [] arr : request_list.values()){
                    for (String r_id : arr){
                        if (r_id.equals(receiver)){
                            JOptionPane.showMessageDialog(null, "요청/신청이 존재합니다.",
                                        "경고", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                }
                String [] id_list = server.GetFriendListRequest(id);
                if (id_list != null){
                    for (String id_ : id_list){
                        if (id_.equals(receiver)){
                            JOptionPane.showMessageDialog(null, "이미 친구인 사용자입니다.",
                                        "경고", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                }

                server.FriendRequestRequest(id, receiver, true);
                JOptionPane.showMessageDialog(null, "요청 성공", 
                            "", JOptionPane.INFORMATION_MESSAGE);
                dialog.dialog.dispose();
            }
        });

        add_friend_btn.addActionListener(dialog);

        add(add_friend_btn, BorderLayout.EAST);
    }
}

class FriendPanel extends JPanel{
    Navigator nav;
    String id;
    String name;

    FriendPanel(Navigator nav, String id, String name) {
        this.nav = nav;
        this.id = id;
        this.name = name;

        setLayout(new BorderLayout(10, 10));
        setOpaque(false);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));

        ImageIcon icon = new ImageIcon("Front/.src/person_icon.png");
        JLabel imgLabel = new JLabel(icon);
        imgLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        imgLabel.setOpaque(false);
        add(imgLabel, BorderLayout.WEST);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(Color.white);
        nameLabel.setOpaque(false);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel idLabel = new JLabel("@ " + id);
        idLabel.setForeground(Color.LIGHT_GRAY);
        idLabel.setFont(new Font("Arial", Font.PLAIN, 12));
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

class FriendRequestPanel extends FriendPanel {
    Navigator nav;
    String my_id;
    String your_id;
    String name;
    InfoServerClient server;

    FriendRequestPanel(Navigator nav, String my_id, String your_id, String name, InfoServerClient server) {
        super(nav, your_id, name);
        this.nav = nav;
        this.my_id = my_id;
        this.your_id = your_id;
        this.name = name;
        this.server = server;

        RoundButton acceptBtn = new RoundButton("수락", 15);
        acceptBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        acceptBtn.setMargin(new Insets(0, 0, 0, 0));
        acceptBtn.setBounds(350, 25, 60, 30);
        acceptBtn.setBackground(Color.decode("#1E90FF"));
        acceptBtn.setForeground(Color.white);
        acceptBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        acceptBtn.addActionListener(e -> server.FriendRequestResponseRequest(my_id, your_id, true));

        RoundButton refuseBtn = new RoundButton("거절", 15);
        refuseBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        refuseBtn.setMargin(new Insets(0, 0, 0, 0));
        refuseBtn.setBounds(420, 25, 60, 30);
        refuseBtn.setBackground(Color.GRAY);
        refuseBtn.setForeground(Color.white);
        refuseBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        refuseBtn.addActionListener(e -> server.FriendRequestResponseRequest(my_id, your_id, false));

        JPanel buttonPanel = makeBtnPanel();
        buttonPanel.add(acceptBtn);
        buttonPanel.add(refuseBtn);
        
        add(buttonPanel, BorderLayout.EAST);
    }
}

class FriendAddPanel extends FriendPanel {
    Navigator nav;
    String my_id;
    String your_id;
    String name;
    InfoServerClient server;

    FriendAddPanel(Navigator nav, String my_id, String your_id, String name, InfoServerClient server) {
        super(nav, your_id, name);
        this.nav = nav;
        this.my_id = my_id;
        this.your_id = your_id;
        this.name = name;
        this.server = server;

        RoundButton addBtn = new RoundButton("취소", 15);
        addBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        addBtn.setMargin(new Insets(0, 0, 0, 0));
        addBtn.setBounds(420, 25, 60, 30);
        addBtn.setBackground(Color.GRAY);
        addBtn.setForeground(Color.white);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addBtn.addActionListener(e -> {
            server.FriendRequestRequest(my_id, your_id, false);
        });
        
        JPanel buttonPanel = makeBtnPanel();
        buttonPanel.add(addBtn);

        add(buttonPanel, BorderLayout.EAST);
    }
}

class TotFreindPanel extends JPanel {
    private JPanel listPanel;
    private JScrollPane scrollPane;

    private Navigator nav;
    private String id;
    
    private InfoServerClient server;

    TotFreindPanel(Navigator nav, String id, InfoServerClient server) {
        this.nav = nav;
        this.id = id;
        this.server = server;

        setLayout(new BorderLayout());
        setBackground(Color.decode("#141414"));

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.decode("#141414"));

        scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
        
        refreshMessages();
        // 3초마다 갱신
        new Timer(3000, e -> refreshMessages()).start();
    }

    class ListLabelPanel extends JPanel {
        ListLabelPanel(String text) {
            setLayout(new BorderLayout());
            setOpaque(false);
    
            JLabel label = new JLabel(text);
            label.setForeground(Color.white);
            label.setFont(new Font("Arial", Font.BOLD, 25));
            label.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setHorizontalAlignment(SwingConstants.LEFT);
    
            add(label, BorderLayout.WEST);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, label.getPreferredSize().height));
        }
    }

    private void refreshMessages() {
        listPanel.removeAll(); 
        
        ListLabelPanel label = new ListLabelPanel("친구 목록");
        listPanel.add(label);
        
        String [] ids = server.GetFriendListRequest(id);
        if (ids != null){
            for (String id : ids){
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
                    FriendRequestPanel friendRequestPanel = new FriendRequestPanel(nav, id, id_, nName, server);
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
                    FriendAddPanel friendRequestPanel = new FriendAddPanel(nav, id, id_, nName, server);
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
    
    public FriendView(Navigator nav, String id, String nName){
        this.nav = nav;
        this.id = id;
        this.nName = nName;        

        setTitle("HongStar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(500, 800);
        setBackground(Color.decode("#141414"));

        final FreindTopPanel top = new FreindTopPanel(nav, id, nName, server);
        add(top, BorderLayout.NORTH);

        final TotFreindPanel friendList = new TotFreindPanel(nav, id, server);
        add(friendList, BorderLayout.CENTER);
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
