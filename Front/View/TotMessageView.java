package Front.View;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Front.App.Navigator;
import Front.Resize.Resize;
import Front.Server.*;

class RoundButton2 extends JButton {
    private Icon icon;

    public RoundButton2(Icon icon) {
        this.icon = icon;
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 버튼 상태별 배경색
        if (getModel().isArmed()) {
            g2.setColor(Color.decode("#353535")); // 클릭 시
        } else {
            g2.setColor(Color.decode("#252525")); // 기본
        }

        // 원형 배경
        g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);

        // 외곽선
        g2.setColor(Color.GRAY);
        g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);

        // 아이콘 중앙 배치
        int iconX = (getWidth() - icon.getIconWidth()) / 2;
        int iconY = (getHeight() - icon.getIconHeight()) / 2;
        icon.paintIcon(this, g2, iconX, iconY);

        g2.dispose();
    }

    @Override
    public boolean contains(int x, int y) {
        Ellipse2D circle = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
        return circle.contains(x, y);
    }
}

class JDialogBtnListener extends JPanel implements ActionListener {
    JDialog dialog;
    JButton btn;
    JTextField field;

    String txt;

    public JDialogBtnListener(String txt) {
        this.txt = txt;
        setLayout(new BorderLayout(0,10));
        setBackground(Color.decode("#232323"));
        setBorder(new EmptyBorder(15,40,10,40));

        JLabel label = new JLabel(txt);
        label.setForeground(Color.white);
        label.setBorder(new EmptyBorder(0, 20, 5, 20));
        label.setOpaque(false);
        add(label, BorderLayout.NORTH);

        field = new JTextField();
        field.setBackground(Color.DARK_GRAY);
        field.setForeground(Color.white);

        field.setOpaque(true);

        add(field, BorderLayout.CENTER);

        btn = new JButton("확인");
        btn.setBackground(Color.decode("#1E90FF"));
        btn.setForeground(Color.white);

        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

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

class MessagePanel extends JPanel{
    Navigator nav;
    String sender;
    Map<String, String> receiver;
    String se_nName;

    MessagePanel(Navigator nav, String sender, String se_nName, Map<String, String> receiver) {
        this.nav = nav;
        this.sender = sender;
        this.receiver = receiver;
        this.se_nName = se_nName;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        List<String> re_nName = new ArrayList<>(receiver.values());
        String re_nName_str = String.join(", ", re_nName);
        ImageIcon icon = new ImageIcon(Resize.resizeImage("Front/.src/person_icon.png", 40, 40, 1));
        final JButton btn = new JButton(re_nName_str, icon);

        btn.setBackground(Color.decode("#141414"));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.PLAIN, 20));

        btn.setPreferredSize(new Dimension(0, 60));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY), 
            BorderFactory.createEmptyBorder(0, 10, 0, 0))
        );
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setVerticalTextPosition(SwingConstants.CENTER);
        btn.setIconTextGap(8);  
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setVerticalAlignment(SwingConstants.CENTER);

        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nav.openUserMessage(sender, se_nName, receiver);
            }
        });
        add(btn);
    }
}

class TotMessagePanel extends JPanel {
    private JPanel listPanel;
    private JScrollPane scrollPane;

    private Navigator nav;
    private String id;
    private String nName;
    
    private InfoServerClient server;

    TotMessagePanel(Navigator nav, String id, String nName, InfoServerClient server) {
        this.nav = nav;
        this.id = id;
        this.nName = nName;
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

    private void refreshMessages() {
        // 서버에서 메시지 리스트 요청
        String [] ids = server.ChatListRequest(id);

        listPanel.removeAll(); // 이전 메시지 패널 초기화
        if (ids != null){
            for (String r : ids) {
                List<String> receiver_list = new ArrayList<String>(Arrays.asList(r.split("\\|")));
                receiver_list.remove(id); 

                Map<String, String> receiver_map = new HashMap<>();
                List<String> re_nName_list = new ArrayList<>();
                for (String rec : receiver_list) {
                    String name = server.GetNNameRequest(rec);
                    receiver_map.put(rec, name);
                }
                

                listPanel.add(new MessagePanel(nav, id, nName, receiver_map));
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
}

class TotMessegeBtnPanel extends JPanel {
    Navigator nav;
    String id;
    String nName;

    InfoServerClient server;

    TotMessegeBtnPanel (Navigator nav, String id, String nName, InfoServerClient server) {
        this.nav = nav;
        this.id = id;
        this.nName = nName;
        this.server = server;
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(Color.decode("#242424"));

        Icon back_icon = new ImageIcon(Resize.resizeImage("Front/.src/back_arrow_icon.png", 30, 30, 1));
        JButton back_btn = new JButton(back_icon);
        back_btn.setOpaque(false);
        back_btn.setContentAreaFilled(false);
        back_btn.setBorderPainted(false);
        back_btn.setFocusPainted(false);

        back_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nav.openMain(id, nName);
            }
        });

        add(back_btn);
        add(Box.createHorizontalGlue());


        ImageIcon chat_icon = new ImageIcon(Resize.resizeImage("Front/.src/chat_icon.png", 30, 30, 1));
        JButton chat_btn = new JButton(chat_icon);
        JDialogBtnListener dialog = new JDialogBtnListener("메세지를 보낼 ID를 입력하세요 (, 로 id 구분)");
        chat_btn.setOpaque(false);
        chat_btn.setContentAreaFilled(false);
        chat_btn.setBorderPainted(false);
        chat_btn.setFocusPainted(false);

        dialog.btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String se_nName = server.GetNNameRequest(id);
                
                String [] receiver = dialog.field.getText().replace(" ","").split(",| ");

                Map<String, String> receiver_map = new HashMap<>();
                for(String r : receiver){
                    String name = server.GetNNameRequest(r);
                    if (r.equals(id)) {
                        JOptionPane.showMessageDialog(
                                null,
                                "자기 자신에게는 메세지를 보낼 수 없습니다.",
                                "경고", JOptionPane.WARNING_MESSAGE
                                );
                        return;
                    }

                    if (name != null){
                        receiver_map.put(r, name);
                    } else {
                        JOptionPane.showMessageDialog(
                                null,
                                "존재하지 않는 ID가 포함되어 있습니다.",
                                "경고", JOptionPane.WARNING_MESSAGE
                                );
                        return;
                    }
                }

                nav.openUserMessage(id, se_nName, receiver_map);
                dialog.dialog.dispose();
            }
        });
        chat_btn.addActionListener(dialog);

        add(chat_btn);
    }
}

public class TotMessageView extends JFrame {
    Navigator nav;
    String id;
    String nName;
    private InfoServerClient server = new InfoServerClient();

    public TotMessageView(Navigator nav, String id, String nName) {
        this.nav = nav;
        this.id = id;
        this.nName = nName;

        setTitle("HongStar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 800);
        setLocationRelativeTo(null);
        setBackground(Color.decode("#141414"));
        setLayout(new BorderLayout());

        TotMessagePanel totPanel = new TotMessagePanel(nav, id, nName, server);
        totPanel.setBounds(0, 0, 500, 800);

        TotMessegeBtnPanel btnPanel = new TotMessegeBtnPanel(nav, id, nName, server);
        btnPanel.setBounds(0, 0, 500, 800);

        // 레이어 우선순위 설정
        add(totPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.NORTH);

        setVisible(true);
    }
}