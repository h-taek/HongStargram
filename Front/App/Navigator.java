package Front.App;

import java.util.*;

public interface Navigator {
    void openLogin();

    void openSignup();

    void openMain(String id, String Nname);

    void openTotMessage(String id, String Nname);

    void openFriend(String id, String nName);

    void openUserMessage(String sender, String se_nName, Map<String, String> receiver);

    void openComments(List<Map<String, String>> comments, String id, String nName, String post_id);
}
