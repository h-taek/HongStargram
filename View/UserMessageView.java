package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import App.Navigator;
import Server.*;

class Message {
    final String text;
    final boolean mine;
    Message(String text, boolean mine) {
        this.text = text; 
        this.mine = mine;
    }
}

class RoundButton extends JButton {
    private final int radius;

    public RoundButton(String text, int radius) {
        super(text);
        this.radius = radius;
        setOpaque(false);  
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}

class RoundTextArea extends JTextArea {
    private final int radius;
    private Color bubbleColor = new Color(0x2A2A2A);

    public RoundTextArea(int radius) {
        this.radius = radius;
        setLineWrap(true);
        setWrapStyleWord(true);
        setEditable(false);
        setOpaque(false);
    }

    public void setBubbleColor(Color c) {
        this.bubbleColor = c;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bubbleColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}

class RoundTextField extends JTextField {
    private final int radius;

    public RoundTextField(int radius) {
        this.radius = radius;
        setOpaque(false);
        setBorder(new EmptyBorder(0, 5, 0, 0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}

class BubbleRenderer extends JPanel implements ListCellRenderer<Message> {
    private final RoundTextArea area = new RoundTextArea(10);
    private final Color mineBg = new Color(0x2D76FF);  // 파랑
    private final Color otherBg = new Color(0x2A2A2A); // 진회색

    BubbleRenderer() {
        setLayout(new FlowLayout());

        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        add(area);
    }

    @Override
    public Component getListCellRendererComponent(
        JList<? extends Message> list, Message value, 
        int index, boolean isSelected, boolean cellHasFocus
    ) 
    {
        area.setForeground(Color.WHITE);
        area.setText(value.text);
        area.setBubbleColor(value.mine ? mineBg : otherBg);
        area.setBorder(BorderFactory.createEmptyBorder(5,8,5,8));

        int max_w = (int)Math.round(list.getWidth() * 0.65);
        int min_w = 60;

        FontMetrics fm = area.getFontMetrics(area.getFont());
        String[] lines = value.text.split("\n| ");
        int max_line = 20;
        for (String line : lines) {
            max_line += (fm.stringWidth(line) + fm.stringWidth(" "));
            if (max_line >= max_w) break;
        }

        int width = Math.max(min_w, Math.min(max_w, max_line));
        area.setSize(new Dimension(width, Short.MAX_VALUE));


        ((FlowLayout)getLayout()).setAlignment(value.mine ? FlowLayout.RIGHT : FlowLayout.LEFT);
        setBackground(list.getBackground());
        return this;
    }
}

class UserMessegeTopPanel extends JPanel {
    Navigator nav;
    String sender;
    String se_nName;
    String re_nName;
    

    public UserMessegeTopPanel(Navigator nav, String sender, String se_nName, String re_nName){
        this.nav = nav;
        this.sender = sender;
        this.se_nName = se_nName;
        this.re_nName = re_nName;

        setLayout(new BorderLayout(5, 0));
        setBackground(Color.decode("#242424"));

        Icon icon = new ImageIcon(".src/back_arrow_icon.png");
        JButton btn = new JButton(icon);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nav.openTotMessage(sender, se_nName);
            }
        });
        btn.setContentAreaFilled(false);   // 버튼의 배경을 칠하지 않게    
        btn.setBorderPainted(false);       // 테두리(border) 없애기
        btn.setFocusPainted(false);        // 포커스(점선 테두리) 표시 제거
        btn.setOpaque(false);       // 투명도 완전하게 만들기 (배경색 투명)
        
        add(btn, BorderLayout.WEST);

        JLabel label = new JLabel(re_nName);
        label.setForeground(Color.white);
        label.setFont(new Font("Arial", Font.PLAIN, 20));

        add(label, BorderLayout.CENTER);
    }
}

class ChatListPanel extends JPanel {
    private final DefaultListModel<Message> model = new DefaultListModel<>();
    private final JList<Message> list = new JList<>(model);
    private final JScrollPane scrollPane;
    
    private boolean initialRefreshDone = false;

    ChatListPanel() {
        setLayout(new BorderLayout());

        list.setCellRenderer(new BubbleRenderer());
        list.setBackground(new Color(0x141414));
        list.setFixedCellHeight(-1);
        
        scrollPane = new JScrollPane(list);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(new Color(0x141414));
        
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addMessage(Message m) {
        model.addElement(m);
        autoScroll();
    }

    private void autoScroll() {
        int last = model.getSize() - 1;
        if (last >= 0) list.ensureIndexIsVisible(last);
    }

    public void refreshNow() {
        if (!initialRefreshDone) {
            int n = list.getModel().getSize();
            if (n > 0 && list.getModel() instanceof DefaultListModel<?>) {
                DefaultListModel<Message> m = (DefaultListModel<Message>) list.getModel();
                for (int i = 0; i < n; i++) {
                    Message msg = (Message) m.get(i);
                    m.set(i, msg);
                }
            }
            initialRefreshDone = true;
        }
    }
}

class InputBarPanel extends JPanel {
    ChatListPanel list;
    ChatServerClient c_server;

    InputBarPanel(ChatListPanel list, ChatServerClient c_server) {
        this.list = list;
        this.c_server = c_server;

        RoundTextField input = new RoundTextField(15);
        RoundButton send = new RoundButton("Send", 15);

        send.setBackground(Color.DARK_GRAY);
        send.setForeground(Color.WHITE);

        input.setBackground(Color.DARK_GRAY);
        input.setForeground(Color.WHITE);
        input.setCaretColor(Color.WHITE);
        
        send.addActionListener(e -> sendMessage(input));
        input.addActionListener(e -> sendMessage(input));

        setLayout(new BorderLayout(6,0));
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        setBackground(Color.decode("#141414"));

        add(input, BorderLayout.CENTER);
        add(send, BorderLayout.EAST);
    }

    private void sendMessage(JTextField input){
        Message msg = new Message(input.getText().trim(), true);
        if (msg.text.isEmpty()) return;
        list.addMessage(msg);
        c_server.sendMessage(input.getText().trim());
        
        input.setText("");
    }
}

public class UserMessageView extends JFrame {
    Navigator nav;
    String sender;
    String receiver;
    String re_nName;

    public UserMessageView(Navigator nav, String sender, String receiver, String se_nName, String re_nName) {
        this.nav = nav;
        this.sender = sender;
        this.receiver = receiver;
        this.re_nName = re_nName;
        
        setTitle("HongStar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 800);
        setLocationRelativeTo(null);
        setBackground(Color.decode("#141414"));
        setLayout(new BorderLayout());
        
        UserMessegeTopPanel top = new UserMessegeTopPanel(nav, sender, se_nName, re_nName);
        ChatListPanel chat = new ChatListPanel();
        ChatServerClient chat_server = new ChatServerClient(sender, receiver, new ChatListener() {
            @Override
            public void onMessage(String msg_) {
                Message msg = new Message(msg_, false);
                chat.addMessage(msg);
            }
        });
        InputBarPanel inputBar = new InputBarPanel(chat, chat_server);

        List<Map<String, String>> chat_history = chat_server.chat_log;
        if (chat_history != null){
            for (Map<String, String> c : chat_history) {
                Message msg;
                if (c.get("sender").equals(sender)) {
                    msg = new Message(c.get("msg"), true);
                }
                else {
                    msg = new Message(c.get("msg"), false);
                }
                chat.addMessage(msg);
            }
        }

        add(top, BorderLayout.NORTH);
        add(chat, BorderLayout.CENTER);
        add(inputBar, BorderLayout.SOUTH);

        SwingUtilities.invokeLater(chat::refreshNow);     
        setVisible(true);  
    }
}