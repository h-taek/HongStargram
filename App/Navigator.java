package App;

import javax.swing.*;
import java.util.*;

import View.*;

public interface Navigator {
    void openLogin();
    void openSignup();
    void openMain(String id, String Nname);
    void openTotMessage(String id, String Nname);
    void openFriend(String id, String nName);
    void openUserMessage(String sender, String receiver,String se_nName, String re_nName);
    void openComments(List<Map<String, String>> comments, String id, String nName);
}

class App implements Navigator{
    private JFrame current;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().openLogin());
    }

    private void show(JFrame next) {
        if (current != null) current.dispose();
        current = next;
        current.setLocationRelativeTo(null);
        current.setVisible(true);
    }

    @Override
    public void openLogin() {
        show(new LoginView(this));   // ★ Navigator 주입
    }

    @Override
    public void openSignup() {
        show(new SignupView(this)); 
    }

    @Override 
    public void openMain(String id, String nName) {
        show(new MainView(this, id, nName));
    }

    @Override
    public void openTotMessage(String id, String nName) {
        show(new TotMessageView(this, id, nName));
    }

    @Override
    public void openUserMessage(String sender, String receiver,String se_nName, String re_nName) {
        SwingUtilities.invokeLater(() -> show (new UserMessageView(this, sender, receiver, se_nName, re_nName)));
    }

    @Override
    public void openFriend(String id, String nName) {
        show(new FriendView(this, id, nName));
    }

    @Override
    public void openComments(List<Map<String, String>> comments, String id, String nName){
        show(new CommentsView(this, comments, id, nName));
    }
}
