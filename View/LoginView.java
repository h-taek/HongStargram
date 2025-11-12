package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import App.Navigator;
import Server.InfoServerClient;
import Resize.Resize;

// 배경 패널
class LoginBacgroudPanel extends JPanel {
    LoginBacgroudPanel() {
        setOpaque(false);
        setLayout(null);
        ImageIcon logo = new ImageIcon(Resize.resizeImage(".src/hongik_emblem_line.png", 250, 250, 1));
        JLabel bgLabel = new JLabel(logo);
        bgLabel.setBounds(125, 100, 250, 250);
        add(bgLabel);
    }
}

// 폼 패널
class LoginFormPanel extends JPanel {
    LoginFormPanel(Navigator nav) {
        setOpaque(false);
        setLayout(null); // 현재 좌표기반 유지 (추후 GridBagLayout 권장)

        JTextField id_field = new JTextField(15);
        id_field.setBounds(130, 400,240, 50);
        id_field.setBackground(Color.darkGray);
        id_field.setForeground(Color.white);
        id_field.setFont(new Font("Arial", Font.PLAIN, 16));
        id_field.setBorder(new EmptyBorder(0, 30, 0, 0));

        JLabel id_label = new JLabel("ID: ");
        id_label.setFont(new Font("Arial", Font.PLAIN, 16));

        id_label.setForeground(Color.gray);
        id_label.setBounds(135, 400,240, 50);

        add(id_label);
        add(id_field);

        JPasswordField pw_field = new JPasswordField(12);
        pw_field.setBounds(130, 455,240, 50);
        pw_field.setBackground(Color.darkGray);
        pw_field.setForeground(Color.white);
        pw_field.setFont(new Font("Arial", Font.PLAIN, 16));
        pw_field.setBorder(BorderFactory.createCompoundBorder(id_field.getBorder(),new EmptyBorder(0, 23, 0, 0)));
        pw_field.setBorder(new EmptyBorder(0, 40, 0, 0));


        JLabel pw_label = new JLabel("PW: ");
        pw_label.setFont(new Font("Arial", Font.PLAIN, 16));

        pw_label.setForeground(Color.gray);
        pw_label.setBounds(135, 455,240, 50);

        add(pw_label);
        add(pw_field);

          
        final JButton loginBtn = new JButton("Log in");
        loginBtn.setBounds(130, 550, 240, 40);
        loginBtn.setBackground(Color.decode("#1E90FF"));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 20));
        loginBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginBtn.setOpaque(true);
        loginBtn.setContentAreaFilled(true);
        loginBtn.setBorderPainted(false);
        loginBtn.setFocusPainted(false);

        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = id_field.getText();
                String pw = new String(pw_field.getPassword());
                InfoServerClient client = new InfoServerClient();

                String nName = client.LoginRequest(id, pw);
                if (nName == null) {JOptionPane.showMessageDialog(
                    null,
                    "사용자 정보를 찾을 수 없습니다.",
                    "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                nav.openMain(id, nName);

            }
        });

        add(loginBtn);
    }
}

// 하단 패널
class LoginBottomPanel extends JPanel {
    final JButton signUpBtn = new JButton("Sign up");

    LoginBottomPanel(Navigator nav) {
        setOpaque(false);
        setLayout(null);

        signUpBtn.setBounds(185, 610, 130, 25);
        signUpBtn.setForeground(Color.decode("#1362B0"));
        signUpBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        signUpBtn.setOpaque(true);
        signUpBtn.setContentAreaFilled(false);
        signUpBtn.setBorderPainted(false);
        signUpBtn.setFocusPainted(false);

        signUpBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nav.openSignup();
            }
        });

        add(signUpBtn);
    }
}

// 프레임 : 서브 패널 조립
public class LoginView extends JFrame {
    public LoginView(Navigator nav){
        final LoginBacgroudPanel loginBacgroudPanel = new LoginBacgroudPanel();
        final LoginFormPanel formPanel = new LoginFormPanel(nav);
        final LoginBottomPanel loginBottomPanel = new LoginBottomPanel(nav);
 
        setTitle("HongStar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 800);

        Container c = getContentPane();
        c.setBackground(Color.decode("#141414"));
        c.setLayout(null); // 현재 구조 유지

        // 각 패널의 배치(현재 절대 좌표이므로 프레임 크기 기준으로 겹쳐 배치)
        loginBacgroudPanel.setBounds(0, 0, 500, 400);
        formPanel.setBounds(0, 0, 500, 800);
        loginBottomPanel.setBounds(0, 0, 500, 800);

        c.add(loginBacgroudPanel);
        c.add(formPanel);
        c.add(loginBottomPanel);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}