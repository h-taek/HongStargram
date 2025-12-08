package Front.View;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import Front.App.Navigator;
import Front.Resize.Resize;
import Front.Server.*;

// 위치 업데이트 다이얼로그
class UpdateLocationDialog extends JPanel implements ActionListener {
    JDialog dialog;
    RoundButton btn;
    JTextField latitudeField;
    JTextField longitudeField;
    JTextField locationNameField;

    public UpdateLocationDialog() {
        setPreferredSize(new Dimension(450, 300));
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);

        // 위도 입력
        JLabel latLabel = new JLabel("위도 (Latitude)");
        latLabel.setForeground(Color.decode("#262626"));
        latLabel.setFont(new Font("Arial", Font.BOLD, 14));
        latLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        latitudeField = new JTextField("37.5665");
        latitudeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        latitudeField.setBackground(Color.decode("#FAFAFA"));
        latitudeField.setForeground(Color.decode("#262626"));
        latitudeField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#DBDBDB"), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        formPanel.add(latLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(latitudeField);
        formPanel.add(Box.createVerticalStrut(15));

        // 경도 입력
        JLabel lonLabel = new JLabel("경도 (Longitude)");
        lonLabel.setForeground(Color.decode("#262626"));
        lonLabel.setFont(new Font("Arial", Font.BOLD, 14));
        lonLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        longitudeField = new JTextField("126.9780");
        longitudeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        longitudeField.setBackground(Color.decode("#FAFAFA"));
        longitudeField.setForeground(Color.decode("#262626"));
        longitudeField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#DBDBDB"), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        formPanel.add(lonLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(longitudeField);
        formPanel.add(Box.createVerticalStrut(15));

        // 위치 이름 입력
        JLabel nameLabel = new JLabel("위치 이름");
        nameLabel.setForeground(Color.decode("#262626"));
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        locationNameField = new JTextField("서울");
        locationNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        locationNameField.setBackground(Color.decode("#FAFAFA"));
        locationNameField.setForeground(Color.decode("#262626"));
        locationNameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#DBDBDB"), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        formPanel.add(nameLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(locationNameField);

        add(formPanel, BorderLayout.CENTER);

        // 확인 버튼
        btn = new RoundButton("확인", 10);
        btn.setBackground(Color.decode("#0095F6"));
        btn.setForeground(Color.WHITE);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialog = new JDialog(null, "위치 업데이트", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(this);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}

// 알람 추가 다이얼로그
class AddAlertDialog extends JPanel implements ActionListener {
    JDialog dialog;
    RoundButton btn;
    JTextField targetUserField;
    JTextField distanceField;

    public AddAlertDialog() {
        setPreferredSize(new Dimension(400, 250));
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);

        // 대상 사용자 ID 입력
        JLabel userLabel = new JLabel("대상 친구 ID");
        userLabel.setForeground(Color.decode("#262626"));
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        targetUserField = new JTextField();
        targetUserField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        targetUserField.setBackground(Color.decode("#FAFAFA"));
        targetUserField.setForeground(Color.decode("#262626"));
        targetUserField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#DBDBDB"), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        formPanel.add(userLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(targetUserField);
        formPanel.add(Box.createVerticalStrut(15));

        // 알람 거리 입력
        JLabel distLabel = new JLabel("알람 거리 (미터)");
        distLabel.setForeground(Color.decode("#262626"));
        distLabel.setFont(new Font("Arial", Font.BOLD, 14));
        distLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        distanceField = new JTextField("500");
        distanceField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        distanceField.setBackground(Color.decode("#FAFAFA"));
        distanceField.setForeground(Color.decode("#262626"));
        distanceField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#DBDBDB"), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        formPanel.add(distLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(distanceField);

        add(formPanel, BorderLayout.CENTER);

        // 확인 버튼
        btn = new RoundButton("확인", 10);
        btn.setBackground(Color.decode("#0095F6"));
        btn.setForeground(Color.WHITE);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialog = new JDialog(null, "알람 추가", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(this);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}

// 현재 위치 패널
class CurrentLocationPanel extends JPanel {
    LocationServerClient server;
    String id;
    JLabel locationLabel;

    CurrentLocationPanel(String id, LocationServerClient server) {
        this.id = id;
        this.server = server;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#DBDBDB")),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel titleLabel = new JLabel("현재 위치");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.decode("#262626"));

        locationLabel = new JLabel("위치 정보 없음");
        locationLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        locationLabel.setForeground(Color.decode("#8E8E8E"));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(locationLabel);

        add(infoPanel, BorderLayout.CENTER);

        refresh();
    }

    void refresh() {
        Map<String, Object> location = server.GetUserLocationRequest(id);

        if (location != null && location.containsKey("latitude")) {
            String lat = location.get("latitude").toString();
            String lon = location.get("longitude").toString();
            String name = location.getOrDefault("location_name", "알 수 없음").toString();

            locationLabel.setText(String.format("<html>%s<br>위도: %s, 경도: %s</html>", name, lat, lon));
        } else {
            locationLabel.setText("위치 정보 없음");
        }
    }
}

// 알람 패널
class AlertPanel extends JPanel {
    Map<String, Object> alert;
    LocationServerClient server;
    InfoServerClient infoServer;
    AlertListPanel parent;

    AlertPanel(Map<String, Object> alert, LocationServerClient server, InfoServerClient infoServer,
            AlertListPanel parent) {
        this.alert = alert;
        this.server = server;
        this.infoServer = infoServer;
        this.parent = parent;

        setLayout(new BorderLayout(10, 10));
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#DBDBDB")));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        // 아이콘
        ImageIcon icon = new ImageIcon(Resize.resizeImage("Front/.src/person_icon_black.png", 40, 40, 1));
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        add(iconLabel, BorderLayout.WEST);

        // 알람 정보
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        String targetUserId = alert.get("target_user_id").toString();
        String targetName = infoServer.GetNNameRequest(targetUserId);
        if (targetName == null)
            targetName = targetUserId;

        JLabel nameLabel = new JLabel(targetName + " (" + targetUserId + ")");
        nameLabel.setForeground(Color.decode("#262626"));
        nameLabel.setFont(new Font("Arial", Font.BOLD, 15));

        String distance = alert.get("alert_distance").toString();
        double distKm = Double.parseDouble(distance) / 1000.0;
        String distStr = distKm >= 1 ? String.format("%.1f km", distKm) : distance + " m";

        JLabel distLabel = new JLabel("알람 거리: " + distStr);
        distLabel.setForeground(Color.decode("#8E8E8E"));
        distLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(distLabel);

        add(infoPanel, BorderLayout.CENTER);

        // 삭제 버튼
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 10));

        ImageIcon trashIcon = new ImageIcon(Resize.resizeImage("Front/.src/trashbin_icon.png", 20, 20, 0.5f));
        JButton deleteBtn = new JButton(trashIcon);
        deleteBtn.setOpaque(false);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setContentAreaFilled(false);

        deleteBtn.addActionListener(e -> {
            String alertId = alert.get("alert_id").toString();
            if (server.DeleteLocationAlertRequest(alertId)) {
                parent.refresh();
            }
        });

        btnPanel.add(deleteBtn);
        add(btnPanel, BorderLayout.EAST);
    }
}

// 알람 목록 패널
class AlertListPanel extends JPanel {
    Navigator nav;
    String id;
    LocationServerClient server;
    InfoServerClient infoServer;
    JPanel listPanel;

    AlertListPanel(Navigator nav, String id, LocationServerClient server, InfoServerClient infoServer) {
        this.nav = nav;
        this.id = id;
        this.server = server;
        this.infoServer = infoServer;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("  알람 목록");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.decode("#262626"));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 5, 10, 0));

        add(titleLabel, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        refresh();
    }

    void refresh() {
        listPanel.removeAll();

        List<Map<String, Object>> alerts = server.GetLocationAlertsRequest(id);

        if (alerts != null && !alerts.isEmpty()) {
            for (Map<String, Object> alert : alerts) {
                AlertPanel panel = new AlertPanel(alert, server, infoServer, this);
                listPanel.add(panel);
            }
        } else {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new GridBagLayout());
            emptyPanel.setBackground(Color.WHITE);
            emptyPanel.setPreferredSize(new Dimension(500, 200));

            JLabel emptyLabel = new JLabel("설정된 알람이 없습니다");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            emptyLabel.setForeground(Color.decode("#8E8E8E"));

            emptyPanel.add(emptyLabel);
            listPanel.add(emptyPanel);
        }

        listPanel.revalidate();
        listPanel.repaint();
    }
}

// 근처 친구 패널
class NearbyFriendPanel extends JPanel {
    Map<String, Object> friend;
    InfoServerClient infoServer;

    NearbyFriendPanel(Map<String, Object> friend, InfoServerClient infoServer) {
        this.friend = friend;
        this.infoServer = infoServer;

        setLayout(new BorderLayout(10, 10));
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#DBDBDB")));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // 아이콘
        ImageIcon icon = new ImageIcon(Resize.resizeImage("Front/.src/person_icon_black.png", 40, 40, 1));
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        add(iconLabel, BorderLayout.WEST);

        // 친구 정보
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        String userId = friend.get("user_id").toString();
        String userName = infoServer.GetNNameRequest(userId);
        if (userName == null)
            userName = userId;

        JLabel nameLabel = new JLabel(userName + " (" + userId + ")");
        nameLabel.setForeground(Color.decode("#262626"));
        nameLabel.setFont(new Font("Arial", Font.BOLD, 15));

        Object distObj = friend.get("distance");
        String distStr = "거리 정보 없음";
        if (distObj != null) {
            double distMeters = Double.parseDouble(distObj.toString());
            if (distMeters >= 1000) {
                distStr = String.format("%.2f km 거리", distMeters / 1000.0);
            } else {
                distStr = String.format("%.0f m 거리", distMeters);
            }
        }

        JLabel distLabel = new JLabel(distStr);
        distLabel.setForeground(Color.decode("#8E8E8E"));
        distLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(distLabel);

        add(infoPanel, BorderLayout.CENTER);
    }
}

// 근처 친구 목록 패널
class NearbyFriendsPanel extends JPanel {
    Navigator nav;
    String id;
    LocationServerClient server;
    InfoServerClient infoServer;
    JPanel listPanel;

    NearbyFriendsPanel(Navigator nav, String id, LocationServerClient server, InfoServerClient infoServer) {
        this.nav = nav;
        this.id = id;
        this.server = server;
        this.infoServer = infoServer;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("  근처 친구");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.decode("#262626"));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 5, 10, 0));

        add(titleLabel, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        refresh();
    }

    void refresh() {
        listPanel.removeAll();

        // 5km 이내 친구 검색
        List<Map<String, Object>> nearbyUsers = server.CheckNearbyUsersRequest(id, 5000.0);

        if (nearbyUsers != null && !nearbyUsers.isEmpty()) {
            for (Map<String, Object> user : nearbyUsers) {
                NearbyFriendPanel panel = new NearbyFriendPanel(user, infoServer);
                listPanel.add(panel);
            }
        } else {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new GridBagLayout());
            emptyPanel.setBackground(Color.WHITE);
            emptyPanel.setPreferredSize(new Dimension(500, 200));

            JLabel emptyLabel = new JLabel("근처에 친구가 없습니다 (5km 이내)");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            emptyLabel.setForeground(Color.decode("#8E8E8E"));

            emptyPanel.add(emptyLabel);
            listPanel.add(emptyPanel);
        }

        listPanel.revalidate();
        listPanel.repaint();
    }
}

// 상단 패널
class LocationTopPanel extends JPanel {
    Navigator nav;
    String id;
    String nName;
    LocationServerClient server;
    CurrentLocationPanel currentLocation;
    AlertListPanel alertList;
    NearbyFriendsPanel nearbyFriends;

    LocationTopPanel(Navigator nav, String id, String nName, LocationServerClient server,
            CurrentLocationPanel currentLocation, AlertListPanel alertList, NearbyFriendsPanel nearbyFriends) {
        this.nav = nav;
        this.id = id;
        this.nName = nName;
        this.server = server;
        this.currentLocation = currentLocation;
        this.alertList = alertList;
        this.nearbyFriends = nearbyFriends;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(Color.decode("#FAFAFA"));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#DBDBDB")));

        // 뒤로가기 버튼
        ImageIcon backIcon = new ImageIcon(Resize.resizeImage("Front/.src/back_arrow_icon_black.png", 30, 30, 1));
        JButton backBtn = new JButton(backIcon);
        backBtn.setBackground(Color.decode("#FAFAFA"));
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.addActionListener(e -> nav.openMain(id, nName));

        add(backBtn);
        add(Box.createHorizontalGlue());

        // 위치 업데이트 버튼
        ImageIcon locIcon = new ImageIcon(Resize.resizeImage("Front/.src/person_icon_black.png", 28, 28, 1));
        JButton updateLocBtn = new JButton(locIcon);
        updateLocBtn.setBackground(Color.decode("#FAFAFA"));
        updateLocBtn.setBorderPainted(false);
        updateLocBtn.setFocusPainted(false);
        updateLocBtn.setContentAreaFilled(false);

        UpdateLocationDialog updateDialog = new UpdateLocationDialog();
        updateDialog.btn.addActionListener(e -> {
            String lat = updateDialog.latitudeField.getText();
            String lon = updateDialog.longitudeField.getText();
            String name = updateDialog.locationNameField.getText();

            try {
                double latitude = Double.parseDouble(lat);
                double longitude = Double.parseDouble(lon);

                if (server.UpdateLocationRequest(id, latitude, longitude, name)) {
                    currentLocation.refresh();
                    nearbyFriends.refresh();
                    updateDialog.dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "위치 업데이트 실패", "에러", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "올바른 숫자를 입력하세요", "경고", JOptionPane.WARNING_MESSAGE);
            }
        });

        updateLocBtn.addActionListener(updateDialog);
        add(updateLocBtn);

        // 알람 추가 버튼
        ImageIcon addIcon = new ImageIcon(Resize.resizeImage("Front/.src/add_friend_icon.png", 30, 30, 1));
        JButton addAlertBtn = new JButton(addIcon);
        addAlertBtn.setBackground(Color.decode("#FAFAFA"));
        addAlertBtn.setBorderPainted(false);
        addAlertBtn.setFocusPainted(false);
        addAlertBtn.setContentAreaFilled(false);

        AddAlertDialog addDialog = new AddAlertDialog();
        addDialog.btn.addActionListener(e -> {
            String targetUser = addDialog.targetUserField.getText();
            String dist = addDialog.distanceField.getText();

            if (targetUser.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "대상 친구 ID를 입력하세요", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double distance = Double.parseDouble(dist);
                String result = server.AddLocationAlertRequest(id, targetUser, distance);

                if (result != null) {
                    alertList.refresh();
                    addDialog.dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "알람 추가 실패", "에러", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "올바른 거리를 입력하세요", "경고", JOptionPane.WARNING_MESSAGE);
            }
        });

        addAlertBtn.addActionListener(addDialog);
        add(addAlertBtn);

        // 새로고침 버튼
        ImageIcon refreshIcon = new ImageIcon(Resize.resizeImage("Front/.src/restart_icon_black.png", 25, 25, 1));
        JButton refreshBtn = new JButton(refreshIcon);
        refreshBtn.setBackground(Color.decode("#FAFAFA"));
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setContentAreaFilled(false);
        refreshBtn.addActionListener(e -> {
            currentLocation.refresh();
            alertList.refresh();
            nearbyFriends.refresh();
        });

        add(refreshBtn);
    }
}

public class LocationView extends JFrame {
    Navigator nav;
    String id;
    String nName;
    LocationServerClient server = new LocationServerClient();
    InfoServerClient infoServer = new InfoServerClient();

    public LocationView(Navigator nav, String id, String nName) {
        this.nav = nav;
        this.id = id;
        this.nName = nName;

        setTitle("HongStar - 위치 알람");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 800);
        setLocationRelativeTo(null);
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // 탭 패널
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(Color.decode("#262626"));

        // 메인 패널 (현재 위치)
        JPanel mainTab = new JPanel(new BorderLayout());
        mainTab.setBackground(Color.WHITE);
        CurrentLocationPanel currentLocation = new CurrentLocationPanel(id, server);

        // 근처 친구 목록 추가
        NearbyFriendsPanel nearbyFriends = new NearbyFriendsPanel(nav, id, server, infoServer);

        mainTab.add(currentLocation, BorderLayout.NORTH);
        mainTab.add(nearbyFriends, BorderLayout.CENTER);

        // 알람 목록 패널
        AlertListPanel alertList = new AlertListPanel(nav, id, server, infoServer);

        tabbedPane.addTab("현재 위치", mainTab);
        tabbedPane.addTab("알람 관리", alertList);

        LocationTopPanel topPanel = new LocationTopPanel(nav, id, nName, server, currentLocation, alertList,
                nearbyFriends);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }
}
