package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import App.Navigator;
import Resize.Resize;
import Server.InfoServerClient;


class SignupButtonListener implements ActionListener {
    private Navigator nav;
    private JTextField idField, nNameField;
    private JPasswordField pwField, pw2Field;

    public SignupButtonListener(Navigator nav, JTextField idField, JPasswordField pwField, JPasswordField pw2Field, JTextField nNameField) {
        this.nav = nav;
        this.idField = idField; this.nNameField = nNameField;
        this.pwField = pwField; this.pw2Field = pw2Field;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String id = idField.getText();
        String pw  = new String(pwField.getPassword());
        String pw2 = new String(pw2Field.getPassword());
        String nName = nNameField.getText();

        if (id.isEmpty() || pw.isEmpty() || nName.isEmpty() ||
            id.contains(" ") || pw.contains(" ") || nName.contains(" ")) {
            JOptionPane.showMessageDialog(null , "빈칸 또는 공백이 포함되어 있습니다.", 
                    "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (id.equals("-1") || nName.equals("-1") || id.equals("/")) {
            JOptionPane.showMessageDialog(null , "사용할 수 없는 문자가 포함되어있습니다.",
                    "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!pw.equals(pw2)) {
            JOptionPane.showMessageDialog(null , "비밀번호가 일치하지 않습니다.", 
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
}

class SignBackgroundPanel extends JPanel {
    SignBackgroundPanel() {
        setOpaque(false);
        setLayout(null);
        ImageIcon logo = new ImageIcon(Resize.resizeImage(".src/hongik_emblem_line.png", 250, 250, 0.25f));
        JLabel bgLabel = new JLabel(logo);
        bgLabel.setBounds(125, 225, 250, 250);
        add(bgLabel);
    }
}

class SignSignupPanel extends JPanel{
    public SignSignupPanel(Navigator nav) {
        setOpaque(false);
        setLayout(null);

        JLabel text = new JLabel("Sign Up");
        text.setFont(new Font("Arial", Font.BOLD, 30));
        text.setForeground(Color.white);
        text.setBounds(25, 50,240, 50);
        add(text);


        JTextField id_field = new JTextField(15);
        id_field.setBounds(100, 125, 300, 50);
        id_field.setBackground(Color.darkGray);
        id_field.setForeground(Color.white);
        id_field.setFont(new Font("Arial", Font.PLAIN, 16));
        id_field.setBorder(new EmptyBorder(0, 10, 0, 0));

        JLabel id_label = new JLabel("ID: ");
        id_label.setForeground(Color.white);
        id_label.setBounds(70, 125,240, 50);
        id_label.setFont(new Font("Arial", Font.PLAIN, 16));

        add(id_label);
        add(id_field);


        JPasswordField pw_field = new JPasswordField(12);
        pw_field.setBounds(100, 200,300, 50);
        pw_field.setBackground(Color.darkGray);
        pw_field.setForeground(Color.white);
        pw_field.setFont(new Font("Arial", Font.PLAIN, 16));
        pw_field.setBorder(new EmptyBorder(0, 10, 0, 0));

        JLabel pw_label = new JLabel("PW: ");
        pw_label.setForeground(Color.white);
        pw_label.setBounds(60, 200,240, 50);
        pw_label.setFont(new Font("Arial", Font.PLAIN, 16));

        add(pw_label);
        add(pw_field);


        JPasswordField pw2_field = new JPasswordField(12);
        pw2_field.setBounds(100, 275,300, 50);
        pw2_field.setBackground(Color.darkGray);
        pw2_field.setForeground(Color.white);
        pw2_field.setFont(new Font("Arial", Font.PLAIN, 16));
        pw2_field.setBorder(new EmptyBorder(0, 10, 0, 0));

        JLabel pw2_label = new JLabel("PW2: ");
        pw2_label.setForeground(Color.white);
        pw2_label.setBounds(50, 275,240, 50);
        pw2_label.setFont(new Font("Arial", Font.PLAIN, 16));

        add(pw2_label);
        add(pw2_field);


        JTextField nName_field = new JTextField(15);
        nName_field.setBounds(100, 350, 300, 50);
        nName_field.setBackground(Color.darkGray);
        nName_field.setForeground(Color.white);
        nName_field.setFont(new Font("Arial", Font.PLAIN, 16));
        nName_field.setBorder(new EmptyBorder(0, 10, 0, 0));

        JLabel nName_label = new JLabel("Nick Name: ");
        nName_label.setForeground(Color.white);
        nName_label.setBounds(10, 350,300, 50);
        nName_label.setFont(new Font("Arial", Font.PLAIN, 16));

        add(nName_label);
        add(nName_field);

        final JButton SignupBtn = new JButton("Sign up");
        SignupBtn.setBounds(300, 430, 100, 40);
        SignupBtn.setBackground(Color.decode("#1E90FF"));
        SignupBtn.setForeground(Color.WHITE);
        SignupBtn.setFont(new Font("Arial", Font.BOLD, 20));
        SignupBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
        SignupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        SignupBtn.setOpaque(true);
        SignupBtn.setContentAreaFilled(true);
        SignupBtn.setBorderPainted(false);
        SignupBtn.setFocusPainted(false);

        SignupBtn.addActionListener(new SignupButtonListener(nav, id_field, pw_field, pw2_field, nName_field));

        add(SignupBtn);
    }
}

public class SignupView extends JFrame{
    public SignupView(Navigator nav) {
        final SignBackgroundPanel signBackgroundPanel = new SignBackgroundPanel();
        final SignSignupPanel signSignupPanel = new SignSignupPanel(nav);

        setTitle("HongStar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 800);

        Container c = getContentPane();
        c.setBackground(Color.decode("#141414"));
        c.setLayout(null); // 현재 구조 유지

        // 각 패널의 배치(현재 절대 좌표이므로 프레임 크기 기준으로 겹쳐 배치)
        signBackgroundPanel.setBounds(0, 0, 500, 800);
        signSignupPanel.setBounds(0, 0, 500, 800);


        c.add(signBackgroundPanel);
        c.add(signSignupPanel);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
