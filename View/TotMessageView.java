package View;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;

import App.Navigator;
import Server.*;

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
    String receiver;
    String se_nName;
    String re_nName;

    MessagePanel(Navigator nav, String sender, String receiver, String se_nName, String re_nName) {
        this.nav = nav;
        this.sender = sender;
        this.receiver = receiver;
        this.se_nName = se_nName;
        this.re_nName = re_nName;

        setOpaque(false);
        setLayout(null);

        ImageIcon icon = new ImageIcon(".src/person_icon.png");
        final JButton btn = new JButton(re_nName, icon);

        btn.setBounds(0, 0, 500, 75);
        btn.setBackground(Color.decode("#141414"));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.PLAIN, 25));

        Border line = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY);
        Border pad  = BorderFactory.createEmptyBorder(0, 10, 0, 0);
        btn.setBorder(BorderFactory.createCompoundBorder(line, pad));

        btn.setBorder(BorderFactory.createCompoundBorder(
                btn.getBorder(),                          // 기존 테두리 유지
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setVerticalTextPosition(SwingConstants.CENTER);
        btn.setIconTextGap(8);  
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setVerticalAlignment(SwingConstants.CENTER);

        btn.setMargin(new Insets(0, 20, 0, 0));

        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nav.openUserMessage(sender, receiver, se_nName, re_nName);
            }
        });
        add(btn);

        setPreferredSize(new Dimension(0, 75));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
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
            for (String receiver : ids) {
                String re_nName = server.GetNNameRequest(receiver);
                listPanel.add(new MessagePanel(nav, id, receiver, nName, re_nName));
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
        
        setLayout(null);
        setOpaque(false);
        setBackground(new Color(0,0,0,0));

        Icon back_icon = new ImageIcon(".src/back_arrow_icon.png");
        RoundButton2 back_btn = new RoundButton2(back_icon);
        back_btn.setBounds(20, 700, 50, 50);

        back_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nav.openMain(id, nName);
            }
        });

        add(back_btn);


        Icon chat_icon = new ImageIcon(".src/chat_icon.png");
        RoundButton2 chat_btn = new RoundButton2(chat_icon);
        chat_btn.setBounds(430, 700, 50, 50);
        JDialogBtnListener dialog = new JDialogBtnListener("메세지를 보낼 ID를 입력하세요");
        dialog.btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String se_nName = server.GetNNameRequest(id);
                
                String receiver = dialog.field.getText();
                String re_nName = server.GetNNameRequest(receiver);
                if (re_nName == null || id.equals(receiver)) {
                    JOptionPane.showMessageDialog(
                                null,
                                "잘못된 접근입니다.",
                                "경고", JOptionPane.WARNING_MESSAGE
                                );
                    return;
                }

                nav.openUserMessage(id, receiver, se_nName, re_nName);
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
        setLayout(null);

        TotMessagePanel totPanel = new TotMessagePanel(nav, id, nName, server);
        totPanel.setBounds(0, 0, 500, 800);

        TotMessegeBtnPanel btnPanel = new TotMessegeBtnPanel(nav, id, nName, server);
        btnPanel.setBounds(0, 0, 500, 800);

        // 레이어 우선순위 설정
        JLayeredPane layeredPane = getLayeredPane();
        layeredPane.add(totPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(btnPanel, JLayeredPane.PALETTE_LAYER);

        setVisible(true);
    }
}