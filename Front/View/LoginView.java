package Front.View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

import Front.App.Navigator;
import Front.Resize.Resize;
import Front.Server.*;

class PlaceholderTextField extends JTextField {
    private String placeholder;
    private Color placeholderColor = Color.decode("#8E8E8E");
    private Color textColor = Color.decode("#262626");
    private Color backgroundColor = Color.WHITE;
    private Color borderColor = Color.decode("#DBDBDB");

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
        setText(placeholder);
        setBackground(backgroundColor);
        setForeground(placeholderColor);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        setFont(new Font("Noto Sans KR", Font.PLAIN, 14));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (getText().equals(placeholder)) {
                    setText("");
                    setForeground(textColor);
                }
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.decode("#A8A8A8"), 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    setText(placeholder);
                    setForeground(placeholderColor);
                }
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderColor, 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            }
        });
    }

    // 실제 입력된 텍스트만 가져오기
    public String getActualText() {
        String text = getText();
        return text.equals(placeholder) ? "" : text;
    }
}

class PlaceholderPasswordField extends JPasswordField {
    private Color placeholderColor = Color.decode("#8E8E8E");
    private Color textColor = Color.decode("#262626");
    private Color backgroundColor = Color.WHITE;
    private Color borderColor = Color.decode("#DBDBDB");
    private boolean isPlaceholder = true;

    public PlaceholderPasswordField(String placeholder) {
        setText(placeholder);
        setForeground(placeholderColor);
        setBackground(backgroundColor);
        setEchoChar((char) 0); // 플레이스홀더는 평문으로 표시
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        setFont(new Font("Noto Sans KR", Font.PLAIN, 14));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (isPlaceholder) {
                    setText("");
                    setForeground(textColor);
                    setEchoChar('●'); // 비밀번호 마스킹 활성화
                    isPlaceholder = false;
                }
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.decode("#A8A8A8"), 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getPassword().length == 0) {
                    setEchoChar((char) 0); // 마스킹 비활성화
                    setText(placeholder);
                    setForeground(placeholderColor);
                    isPlaceholder = true;
                }
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderColor, 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            }
        });
    }

    public String getActualText() {
        return isPlaceholder ? "" : String.valueOf(getPassword());
    }
}

// 배경 패널
class LoginBacgroudPanel extends JPanel {
    LoginBacgroudPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        ImageIcon logo = new ImageIcon(Resize.resizeImage("Front/.src/hongik_emblem_line.png", 250, 250, 1));
        JLabel bgLabel = new JLabel(logo);
        bgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(bgLabel);
    }
}

// 입력 패널
class LoginFieldPanel extends JPanel {
    void loginListener(Navigator nav, PlaceholderTextField id_field, PlaceholderPasswordField pw_field) {
        String id = id_field.getActualText();
        String pw = new String(pw_field.getActualText());
        InfoServerClient client = new InfoServerClient();

        if (id.isEmpty() || pw.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "ID 또는 PW를 입력해주세요.",
                    "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nName = client.LoginRequest(id, pw);
        if (nName == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "사용자 정보를 찾을 수 없습니다.",
                    "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        nav.openMain(id, nName);
    }

    LoginFieldPanel(Navigator nav) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        PlaceholderTextField id_field = new PlaceholderTextField("ID");
        id_field.setFont(new Font("Noto Sans KR", Font.PLAIN, 16));
        id_field.setPreferredSize(new Dimension(247, 55));
        id_field.setMaximumSize(new Dimension(247, 55));
        id_field.setMinimumSize(new Dimension(247, 55));
        id_field.setAlignmentX(Component.CENTER_ALIGNMENT);

        PlaceholderPasswordField pw_field = new PlaceholderPasswordField("PW");
        pw_field.setFont(new Font("Noto Sans KR", Font.PLAIN, 16));
        pw_field.setPreferredSize(new Dimension(247, 55));
        pw_field.setMaximumSize(new Dimension(247, 55));
        pw_field.setMinimumSize(new Dimension(247, 55));
        pw_field.setAlignmentX(Component.CENTER_ALIGNMENT);

        id_field.addActionListener(e -> loginListener(nav, id_field, pw_field));
        pw_field.addActionListener(e -> loginListener(nav, id_field, pw_field));

        add(id_field);
        add(pw_field);
        add(Box.createVerticalStrut(15));

        final JButton loginBtn = new JButton("Log in");
        loginBtn.setBackground(Color.decode("#0095F6"));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Noto Sans KR", Font.BOLD, 14));
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setBorder(new EmptyBorder(0, 0, 0, 0));

        loginBtn.setPreferredSize(new Dimension(240, 43));
        loginBtn.setMaximumSize(new Dimension(240, 43));
        loginBtn.setMinimumSize(new Dimension(240, 43));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn.setOpaque(true);
        loginBtn.setContentAreaFilled(true);
        loginBtn.setBorderPainted(false);
        loginBtn.setFocusPainted(false);

        loginBtn.addActionListener(e -> loginListener(nav, id_field, pw_field));

        add(loginBtn);
        add(Box.createVerticalStrut(20));

        final JButton signUpBtn = new JButton("Sign up");
        signUpBtn.setForeground(Color.decode("#0095F6"));
        signUpBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        signUpBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        signUpBtn.setOpaque(true);
        signUpBtn.setContentAreaFilled(false);
        signUpBtn.setBorderPainted(false);
        signUpBtn.setFocusPainted(false);

        signUpBtn.addActionListener(e -> nav.openSignup());

        add(signUpBtn);
    }
}

// 프레임 : 서브 패널 조립
public class LoginView extends JFrame {
    public LoginView(Navigator nav) {
        setTitle("HongStar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 800);

        final LoginBacgroudPanel loginBacgroudPanel = new LoginBacgroudPanel();
        final LoginFieldPanel formPanel = new LoginFieldPanel(nav);

        Container c = getContentPane();
        c.setBackground(Color.WHITE);
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

        c.add(Box.createVerticalGlue());
        c.add(loginBacgroudPanel);
        c.add(Box.createVerticalStrut(20));
        c.add(formPanel);
        c.add(Box.createVerticalGlue());

        setLocationRelativeTo(null);
        setVisible(true);
    }
}