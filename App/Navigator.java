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
    void openUserMessage(String sender, String se_nName, Map<String, String> receiver);
    void openComments(List<Map<String, String>> comments, String id, String nName, String post_id);
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
    public void openUserMessage(String sender, String se_nName, Map<String, String> receiver) {
        SwingUtilities.invokeLater(() -> show (new UserMessageView(this, sender, se_nName, receiver)));
    }

    @Override
    public void openFriend(String id, String nName) {
        show(new FriendView(this, id, nName));
    }

    @Override
    public void openComments(List<Map<String, String>> comments, String id, String nName, String post_id){
        show(new CommentsView(this, comments, id, nName, post_id));
    }
}
