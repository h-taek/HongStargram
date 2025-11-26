package Back.DB;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import java.sql.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PostDB {
    // getPost
    public String getPost(int post_id) {
        String sql_post = "SELECT * FROM POSTS WHERE POST_ID = ?";
        String sql_comment = "SELECT * FROM COMMENTS WHERE POST_ID = ?";
        String sql_like = "SELECT * FROM POST_LIKES WHERE POST_ID = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt_post = conn.prepareStatement(sql_post);
             PreparedStatement psmt_comment = conn.prepareStatement(sql_comment);
             PreparedStatement psmt_like = conn.prepareStatement(sql_like)) {

            Map<String, Object> post = new HashMap<>();

            // Post Info
            psmt_post.setInt(1, post_id);
            try (ResultSet rs_post = psmt_post.executeQuery()) {
                if (rs_post.next()) {
                    post.put("post_id", rs_post.getInt("POST_ID"));
                    post.put("user_id", rs_post.getString("USER_ID"));
                    post.put("content", rs_post.getString("CONTENT"));
                    post.put("created_at", rs_post.getTimestamp("CREATED_AT").toString());

                    // image
                    String imagePath = rs_post.getString("IMAGE_PATH");
                    File file = new File(imagePath);
                    if (file.exists()) {
                        String encodedImg = Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
                        post.put("image", encodedImg);
                    }
                }
            }

            // Comments
            psmt_comment.setInt(1, post_id);
            List<Map<String, Object>> comments = new ArrayList<>();
            try (ResultSet rs_comment = psmt_comment.executeQuery()) {
                while (rs_comment.next()) {
                    Map<String, Object> comment = new HashMap<>();
                    comment.put("comment_id", rs_comment.getInt("COMMENT_ID"));
                    comment.put("post_id", rs_comment.getInt("POST_ID"));
                    comment.put("user_id", rs_comment.getString("USER_ID"));
                    comment.put("content", rs_comment.getString("CONTENT"));
                    comments.add(comment);
                }
            }
            post.put("comments", comments);

            // Likes
            psmt_like.setInt(1, post_id);
            List<String> likes = new ArrayList<>();
            try (ResultSet rs_like = psmt_like.executeQuery()) {
                while (rs_like.next()) {
                    String user_id = rs_like.getString("USER_ID");
                    likes.add(user_id);
                }
            }
            post.put("likes", likes);

            Gson gson = new Gson();
            return gson.toJson(post);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("getPost SQL Error");

            return null;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("getPost IO Error");

            return null;
        }
    }

    // getPostList
    public String getPostList(String user_id) {
        String sql = "SELECT POST_ID FROM POSTS WHERE USER_ID = ?";

        try (Connection conn = DBManager.getConnection();
                PreparedStatement psmt = conn.prepareStatement(sql)) {

            psmt.setString(1, user_id);
            List<Integer> post_ids = new ArrayList<>();
            try (ResultSet rs = psmt.executeQuery()) {
                while (rs.next()) {
                    int post_id = rs.getInt("POST_ID");
                    post_ids.add(post_id);
                }
            }

            Gson gson = new Gson();
            return gson.toJson(post_ids);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("getPostList Error");

            return null;
        }
    }

    // sortPostList
    public String sortPostList(String post_ids) {
        String sql = "SELECT POST_ID FROM POSTS WHERE POST_ID IN (?) ORDER BY CREATED_AT DESC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {

            psmt.setString(1, post_ids);
            List<Integer> sorted_post_ids = new ArrayList<>();
            try (ResultSet rs = psmt.executeQuery()) {
                while (rs.next()) {
                    int post_id = rs.getInt("POST_ID");
                    sorted_post_ids.add(post_id);
                }
            }

            Gson gson = new Gson();
            return gson.toJson(sorted_post_ids);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("sortPostList Error");

            return null;
        }
    }

    // getReadablePostList
    public String getReadablePostList(String user_id) {
        String sql = "SELECT POST_ID FROM USER_READABLE_POSTS WHERE USER_ID = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {

            psmt.setString(1, user_id);
            List<String> post_ids = new ArrayList<>();
            try (ResultSet rs = psmt.executeQuery()) {
                while (rs.next()) {
                    String post_id = String.valueOf(rs.getInt("POST_ID"));
                    post_ids.add(post_id);
                }
            }

            Gson gson = new Gson();
            return gson.toJson(post_ids);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("getReadablePostList Error");

            return null;
        }
    }

    // addReadablePost
    public void addReadablePost(String user_id, int post_id) {
        String sql = "INSERT INTO USER_READABLE_POSTS (USER_ID, POST_ID) VALUES (?, ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {

            psmt.setString(1, user_id);
            psmt.setInt(2, post_id);

            psmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("addReadablePost Error");
        }
    }

    // deleteReadablePost
    public void deleteReadablePost(String user_id, int post_id) {
        String sql = "DELETE FROM USER_READABLE_POSTS WHERE USER_ID = ? AND POST_ID = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {

            psmt.setString(1, user_id);
            psmt.setInt(2, post_id);

            psmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("deleteReadablePost Error");
        }
    }

    // deleteAllReadablePost
    public void deleteAllReadablePost(String user_id) {
        String sql = "DELETE FROM USER_READABLE_POSTS WHERE USER_ID = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {

            psmt.setString(1, user_id);

            psmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("deleteAllReadablePost Error");
        }
    }

    // addPost
    public void addPost(String post) {
        String sql_get = "SELECT MAX(POST_ID) AS POST_ID FROM POSTS";
        String sql_add = "INSERT INTO POSTS (POST_ID, USER_ID, CONTENT, IMAGE_PATH, CREATED_AT) VALUES (?, ?, ?, ?, SYSDATE)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt_get = conn.prepareStatement(sql_get);
             PreparedStatement psmt_add = conn.prepareStatement(sql_add)) {

            Gson gson = new Gson();
            Map<String, String> postMap = gson.fromJson(post, new TypeToken<Map<String, String>>() {
            }.getType());

            int post_id;
            try (ResultSet rs = psmt_get.executeQuery()) {
                rs.next();
                post_id = rs.getInt("POST_ID") + 1;
            }

            String encodedImg = postMap.get("image");
            byte[] decodedImg = Base64.getDecoder().decode(encodedImg);
            // String imagePath = "./image/img_" + post_id + ".jpg";
            String imagePath = "./Back/image/img_" + post_id + ".jpg";
            try (FileOutputStream fos = new FileOutputStream(imagePath)) {
                fos.write(decodedImg);
            }

            psmt_add.setInt(1, post_id);
            psmt_add.setString(2, postMap.get("user_id"));
            psmt_add.setString(3, postMap.get("content"));
            psmt_add.setString(4, imagePath);

            psmt_add.executeUpdate();

            // Add readable post to friends
            addReadablePost(postMap.get("user_id"), post_id);

            String friends = new FriendDB().getFriend(postMap.get("user_id"));
            List<String> friendList = gson.fromJson(friends, new TypeToken<List<String>>(){}.getType());
            for (String friend_id : friendList) {
                addReadablePost(friend_id, post_id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("addPost SQL Error");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("addPost IO Error");
        }
    }

    // deletePost
    public void deletePost(int post_id) {
        String sql_get = "SELECT USER_ID FROM POSTS WHERE POST_ID = ?";
        String sql_delete_comments = "DELETE FROM COMMENTS WHERE POST_ID = ?";
        String sql_delete_likes = "DELETE FROM POST_LIKES WHERE POST_ID = ?";
        String sql_delete_post = "DELETE FROM POSTS WHERE POST_ID = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt_get = conn.prepareStatement(sql_get);
             PreparedStatement psmt_delete_comments = conn.prepareStatement(sql_delete_comments);
             PreparedStatement psmt_delete_likes = conn.prepareStatement(sql_delete_likes);
             PreparedStatement psmt_delete_post = conn.prepareStatement(sql_delete_post)) {

            // USER_ID
            psmt_get.setInt(1, post_id);

            String user_id;
            try (ResultSet rs = psmt_get.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Post not found: " + post_id);
                    return;
                }
                user_id = rs.getString("USER_ID");
            }

            // readable posts 삭제
            deleteReadablePost(user_id, post_id);

            String friends = new FriendDB().getFriend(user_id);
            if (friends != null) {
                List<String> friendList = new Gson().fromJson(friends, new TypeToken<List<String>>() {
                }.getType());
                if (friendList != null) {
                    for (String friend_id : friendList) {
                        deleteReadablePost(friend_id, post_id);
                    }
                }
            }

            // 댓글 삭제
            psmt_delete_comments.setInt(1, post_id);
            psmt_delete_comments.executeUpdate();

            // 좋아요 삭제
            psmt_delete_likes.setInt(1, post_id);
            psmt_delete_likes.executeUpdate();

            // POST 삭제
            psmt_delete_post.setInt(1, post_id);
            psmt_delete_post.executeUpdate();

            System.out.println("Post deleted successfully: " + post_id);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("deletePost Error");
        }
    }

    // addComment
    public void addComment(int post_id, String user_id, String content) {
        String sql_add = "INSERT INTO COMMENTS (COMMENT_ID, POST_ID, USER_ID, CONTENT) VALUES (?, ?, ?, ?)";
        String sql_get = "SELECT MAX(COMMENT_ID) AS COMMENT_ID FROM COMMENTS";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt_get = conn.prepareStatement(sql_get);
             PreparedStatement psmt_add = conn.prepareStatement(sql_add)) {

            int comment_id;
            try (ResultSet rs = psmt_get.executeQuery()) {
                rs.next();
                comment_id = rs.getInt("COMMENT_ID") + 1;
            }

            psmt_add.setInt(1, comment_id);
            psmt_add.setInt(2, post_id);
            psmt_add.setString(3, user_id);
            psmt_add.setString(4, content);

            psmt_add.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("addComment Error");
        }
    }

    // deleteCommnet
    public void deleteComment(int comment_id) {
        String sql = "DELETE FROM COMMENTS WHERE COMMENT_ID = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {

            psmt.setInt(1, comment_id);

            psmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("deleteComment Error");
        }
    }

    // addLike
    public void addLike(int post_id, String user_id) {
        String sql = "INSERT INTO POST_LIKES (POST_ID, USER_ID) VALUES (?, ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {

            psmt.setInt(1, post_id);
            psmt.setString(2, user_id);

            psmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("addLike Error");
        }
    }

    // deleteLike
    public void deleteLike(int post_id, String user_id) {
        String sql = "DELETE FROM POST_LIKES WHERE POST_ID = ? AND USER_ID = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {

            psmt.setInt(1, post_id);
            psmt.setString(2, user_id);

            psmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("deleteLike Error");
        }
    }
}
