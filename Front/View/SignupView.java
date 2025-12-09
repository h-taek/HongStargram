package Front.View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import Front.App.Navigator;
import Front.Resize.Resize;
import Front.Server.*;


class SignupInputPanel extends JPanel {
    public void signupListener(Navigator nav, PlaceholderTextField idField, PlaceholderPasswordField pwField,
            PlaceholderPasswordField pw2Field, PlaceholderTextField nNameField) {
        String id = idField.getActualText();
        String pw = new String(pwField.getActualText());
        String pw2 = new String(pw2Field.getActualText());
        String nName = nNameField.getActualText();

        if (id.isEmpty() || pw.isEmpty() || nName.isEmpty() ||
                id.contains(" ") || pw.contains(" ") || nName.contains(" ")) {
            JOptionPane.showMessageDialog(null, "빈칸 또는 공백이 포함되어 있습니다.",
                    "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (id.equals("-1") || nName.equals("-1") || id.equals("/")) {
            JOptionPane.showMessageDialog(null, "사용할 수 없는 문자가 포함되어있습니다.",
                    "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!pw.equals(pw2)) {
            JOptionPane.showMessageDialog(null, "비밀번호가 일치하지 않습니다.",
                    "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        InfoServerClient client = new InfoServerClient();
        int code = client.SignupRequest(id, pw2, nName);
        System.out.println("SignupView - SignupButtonListener - code: " + code);

        if (code == 999) {
            JOptionPane.showMessageDialog(null, "이미 존재하는 ID입니다.",
                    "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(null, "회원가입 완료.",
                "", JOptionPane.INFORMATION_MESSAGE);
        nav.openLogin();
    }

    public SignupInputPanel(Navigator nav) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setMaximumSize(new Dimension(300, Integer.MAX_VALUE));

        JLabel text = new JLabel("Sign Up");
        text.setFont(new Font("Noto Sans KR", Font.BOLD, 30));
        text.setForeground(Color.white);
        text.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel id_label = new JLabel("ID");
        id_label.setForeground(Color.white);
        id_label.setFont(new Font("Noto Sans KR", Font.BOLD, 25));
        id_label.setAlignmentX(Component.LEFT_ALIGNMENT);

        PlaceholderTextField id_field = new PlaceholderTextField("ID");
        id_field.setFont(new Font("Noto Sans KR", Font.PLAIN, 16));
        id_field.setBorder(new EmptyBorder(0, 3, 0, 0));
        id_field.setPreferredSize(new Dimension(300, 50));
        id_field.setMaximumSize(new Dimension(300, 50));
        id_field.setMinimumSize(new Dimension(300, 50));
        id_field.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pw_label = new JLabel("PW");
        pw_label.setForeground(Color.white);
        pw_label.setFont(new Font("Noto Sans KR", Font.BOLD, 25));
        pw_label.setAlignmentX(Component.LEFT_ALIGNMENT);

        PlaceholderPasswordField pw_field = new PlaceholderPasswordField("PW");
        pw_field.setFont(new Font("Noto Sans KR", Font.PLAIN, 16));
        pw_field.setBorder(new EmptyBorder(0, 3, 0, 0));
        pw_field.setPreferredSize(new Dimension(300, 50));
        pw_field.setMaximumSize(new Dimension(300, 50));
        pw_field.setMinimumSize(new Dimension(300, 50));
        pw_field.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pw2_label = new JLabel("PW2");
        pw2_label.setForeground(Color.white);
        pw2_label.setFont(new Font("Noto Sans KR", Font.BOLD, 25));
        pw2_label.setAlignmentX(Component.LEFT_ALIGNMENT);

        PlaceholderPasswordField pw2_field = new PlaceholderPasswordField("PW2");
        pw2_field.setFont(new Font("Noto Sans KR", Font.PLAIN, 16));
        pw2_field.setBorder(new EmptyBorder(0, 3, 0, 0));
        pw2_field.setPreferredSize(new Dimension(300, 50));
        pw2_field.setMaximumSize(new Dimension(300, 50));
        pw2_field.setMinimumSize(new Dimension(300, 50));
        pw2_field.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nName_label = new JLabel("Nick Name");
        nName_label.setForeground(Color.white);
        nName_label.setFont(new Font("Noto Sans KR", Font.BOLD, 25));
        nName_label.setAlignmentX(Component.LEFT_ALIGNMENT);

        PlaceholderTextField nName_field = new PlaceholderTextField("Nick Name");
        nName_field.setFont(new Font("Noto Sans KR", Font.PLAIN, 16));
        nName_field.setBorder(new EmptyBorder(0, 3, 0, 0));
        nName_field.setPreferredSize(new Dimension(300, 50));
        nName_field.setMaximumSize(new Dimension(300, 50));
        nName_field.setMinimumSize(new Dimension(300, 50));
        nName_field.setAlignmentX(Component.LEFT_ALIGNMENT);

        id_field.addActionListener(e -> signupListener(nav, id_field, pw_field, pw2_field, nName_field));
        pw_field.addActionListener(e -> signupListener(nav, id_field, pw_field, pw2_field, nName_field));
        pw2_field.addActionListener(e -> signupListener(nav, id_field, pw_field, pw2_field, nName_field));
        nName_field.addActionListener(e -> signupListener(nav, id_field, pw_field, pw2_field, nName_field));

        add(Box.createVerticalStrut(20));
        add(text);
        add(Box.createVerticalStrut(20));
        add(id_label);
        add(Box.createVerticalStrut(5));
        add(id_field);
        add(Box.createVerticalStrut(20));
        add(pw_label);
        add(Box.createVerticalStrut(5));
        add(pw_field);
        add(Box.createVerticalStrut(20));
        add(pw2_label);
        add(Box.createVerticalStrut(5));
        add(pw2_field);
        add(Box.createVerticalStrut(20));
        add(nName_label);
        add(Box.createVerticalStrut(5));
        add(nName_field);
        add(Box.createVerticalStrut(20));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.setMaximumSize(new Dimension(300, 40)); // 입력 필드 너비와 맞춤
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // 패널 자체는 왼쪽 정렬하여 윗줄과 맞춤

        JButton signupBtn = new JButton("Sign up");
        signupBtn.setBackground(Color.decode("#1E90FF"));
        signupBtn.setForeground(Color.WHITE);
        signupBtn.setFont(new Font("Noto Sans KR", Font.BOLD, 20));
        signupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        signupBtn.setOpaque(true);
        signupBtn.setContentAreaFilled(true);
        signupBtn.setBorderPainted(false);
        signupBtn.setFocusPainted(false);

        signupBtn.addActionListener(e -> signupListener(nav, id_field, pw_field, pw2_field, nName_field));

        btnPanel.add(signupBtn);
        add(btnPanel);
    }
}

class SignupBackgroundPanel extends JPanel {
    SignupBackgroundPanel(Navigator nav) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        SignupInputPanel signupInputPanel = new SignupInputPanel(nav);
        signupInputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(signupInputPanel);
        add(Box.createVerticalGlue());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.decode("#141414"));
        g.fillRect(0, 0, getWidth(), getHeight());

        ImageIcon backgroundImage = new ImageIcon(Resize.resizeImage("Front/.src/hongik_emblem_line.png", 400, 400, 0.25f));
        g.drawImage(backgroundImage.getImage(), (getWidth() - 400) / 2, (getHeight() - 400) / 2 , this);
    }
}

public class SignupView extends JFrame {
    public SignupView(Navigator nav) {
        setLayout(new BorderLayout());
        setTitle("HongStar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 800);
        setBackground(Color.decode("#141414"));

        SignupBackgroundPanel signupBackgroundPanel = new SignupBackgroundPanel(nav);

        add(signupBackgroundPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
