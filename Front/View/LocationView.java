package Front.View;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.io.*;
import java.nio.file.*;

import Front.App.Navigator;
import Front.Resize.Resize;
import Front.Server.*;

// Naver 지도 HTML 템플릿 생성 클래스
class NaverMapHelper {
    private static final String CLIENT_ID = "jz78vv4dg0";
    
    public static String createMapHTML(double latitude, double longitude, List<Map<String, Object>> markers) {
        StringBuilder markersJS = new StringBuilder();
        
        if (markers != null) {
            for (Map<String, Object> marker : markers) {
                double lat = Double.parseDouble(marker.get("latitude").toString());
                double lon = Double.parseDouble(marker.get("longitude").toString());
                String label = marker.get("label").toString();
                String color = marker.getOrDefault("color", "blue").toString();
                
                markersJS.append(String.format(
                    "    var markerPosition = new naver.maps.LatLng(%f, %f);\n" +
                    "    var marker = new naver.maps.Marker({\n" +
                    "        position: markerPosition,\n" +
                    "        map: map,\n" +
                    "        title: '%s',\n" +
                    "        icon: {\n" +
                    "            content: '<div style=\"background:%s;width:20px;height:20px;border-radius:50%%;border:2px solid white;\"></div>',\n" +
                    "            size: new naver.maps.Size(24, 24),\n" +
                    "            anchor: new naver.maps.Point(12, 12)\n" +
                    "        }\n" +
                    "    });\n",
                    lat, lon, label, color
                ));
            }
        }
        
        return String.format(
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>Naver Map</title>\n" +
            "    <script type=\"text/javascript\" src=\"https://oapi.map.naver.com/openapi/v3/maps.js?ncpClientId=%s\"></script>\n" +
            "    <style>\n" +
            "        body { margin: 0; padding: 0; }\n" +
            "        #map { width: 100%%; height: 100vh; }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div id=\"map\"></div>\n" +
            "    <script>\n" +
            "        var mapOptions = {\n" +
            "            center: new naver.maps.LatLng(%f, %f),\n" +
            "            zoom: 15\n" +
            "        };\n" +
            "        var map = new naver.maps.Map('map', mapOptions);\n" +
            "\n" +
            "        // 지도 클릭 이벤트\n" +
            "        naver.maps.Event.addListener(map, 'click', function(e) {\n" +
            "            var lat = e.coord.lat();\n" +
            "            var lng = e.coord.lng();\n" +
            "            console.log('Clicked: ' + lat + ', ' + lng);\n" +
            "        });\n" +
            "\n" +
            "        // 마커 추가\n" +
            "%s" +
            "    </script>\n" +
            "</body>\n" +
            "</html>",
            CLIENT_ID, latitude, longitude, markersJS.toString()
        );
    }
    
    public static void saveHTMLToFile(String html, String fileName) throws IOException {
        Path path = Paths.get(fileName);
        Files.write(path, html.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}

// 지도 패널 (JEditorPane 사용)
class MapPanel extends JPanel {
    private JEditorPane editorPane;
    private String userId;
    private LocationServerClient server;
    private InfoServerClient infoServer;
    private double currentLat = 37.5665;
    private double currentLon = 126.9780;

    public MapPanel(String userId, LocationServerClient server, InfoServerClient infoServer) {
        this.userId = userId;
        this.server = server;
        this.infoServer = infoServer;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 안내 패널
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(Color.decode("#FAFAFA"));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("📍 위치 정보");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.decode("#262626"));

        JLabel coordsLabel = new JLabel(String.format("위도: %.4f, 경도: %.4f", currentLat, currentLon));
        coordsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        coordsLabel.setForeground(Color.decode("#8E8E8E"));

        JButton updateBtn = new RoundButton("위치 업데이트", 8);
        updateBtn.setBackground(Color.decode("#0095F6"));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setMaximumSize(new Dimension(150, 35));
        updateBtn.addActionListener(e -> showUpdateDialog(coordsLabel));

        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(coordsLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(updateBtn);

        add(infoPanel, BorderLayout.NORTH);

        // 지도 설명 패널 (JEditorPane 대신 간단한 패널로 대체)
        JPanel mapInfoPanel = new JPanel();
        mapInfoPanel.setLayout(new GridBagLayout());
        mapInfoPanel.setBackground(Color.WHITE);
        mapInfoPanel.setBorder(BorderFactory.createLineBorder(Color.decode("#DBDBDB"), 1));

        JTextArea infoTextArea = new JTextArea();
        infoTextArea.setEditable(false);
        infoTextArea.setOpaque(false);
        infoTextArea.setLineWrap(true);
        infoTextArea.setWrapStyleWord(true);
        infoTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
        infoTextArea.setForeground(Color.decode("#262626"));
        infoTextArea.setText(
            "🗺️ 네이버 지도 통합\n\n" +
            "현재 위치: 서울\n" +
            "위도/경도를 업데이트하면\n" +
            "지도상의 위치가 변경됩니다.\n\n" +
            "실제 지도 표시를 위해서는\n" +
            "웹 브라우저가 필요합니다.\n\n" +
            "Java Swing의 제한으로\n" +
            "간소화된 버전을 표시합니다."
        );

        mapInfoPanel.add(infoTextArea);
        add(mapInfoPanel, BorderLayout.CENTER);

        loadUserLocation();
    }

    private void loadUserLocation() {
        Map<String, Object> location = server.GetUserLocationRequest(userId);
        if (location != null && location.containsKey("latitude")) {
            currentLat = Double.parseDouble(location.get("latitude").toString());
            currentLon = Double.parseDouble(location.get("longitude").toString());
        }
    }

    private void showUpdateDialog(JLabel coordsLabel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextField latField = new JTextField(String.valueOf(currentLat));
        JTextField lonField = new JTextField(String.valueOf(currentLon));
        JTextField nameField = new JTextField("서울");

        panel.add(new JLabel("위도 (Latitude):"));
        panel.add(latField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("경도 (Longitude):"));
        panel.add(lonField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("위치 이름:"));
        panel.add(nameField);

        int result = JOptionPane.showConfirmDialog(null, panel, "위치 업데이트", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                double lat = Double.parseDouble(latField.getText());
                double lon = Double.parseDouble(lonField.getText());
                String name = nameField.getText();

                if (server.UpdateLocationRequest(userId, lat, lon, name)) {
                    currentLat = lat;
                    currentLon = lon;
                    coordsLabel.setText(String.format("위도: %.4f, 경도: %.4f", lat, lon));
                    JOptionPane.showMessageDialog(null, "위치가 업데이트되었습니다!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "올바른 숫자를 입력하세요", "에러", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

// 알람 목록 패널 (기존 코드 간소화)
class SimpleAlertListPanel extends JPanel {
    String id;
    LocationServerClient server;
    InfoServerClient infoServer;
    JPanel listPanel;

    SimpleAlertListPanel(String id, LocationServerClient server, InfoServerClient infoServer) {
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
        add(scrollPane, BorderLayout.CENTER);

        refresh();
    }

    void refresh() {
        listPanel.removeAll();

        List<Map<String, Object>> alerts = server.GetLocationAlertsRequest(id);

        if (alerts != null && !alerts.isEmpty()) {
            for (Map<String, Object> alert : alerts) {
                listPanel.add(new SimpleAlertPanel(alert, server, infoServer, this));
            }
        } else {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
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

    class SimpleAlertPanel extends JPanel {
        SimpleAlertPanel(Map<String, Object> alert, LocationServerClient server, InfoServerClient infoServer, SimpleAlertListPanel parent) {
            setLayout(new BorderLayout(10, 10));
            setOpaque(true);
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#DBDBDB")));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

            ImageIcon icon = new ImageIcon(Resize.resizeImage("Front/.src/person_icon_black.png", 35, 35, 1));
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
            add(iconLabel, BorderLayout.WEST);

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setOpaque(false);
            infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

            String targetUserId = alert.get("target_user_id").toString();
            String targetName = infoServer.GetNNameRequest(targetUserId);
            if (targetName == null) targetName = targetUserId;

            JLabel nameLabel = new JLabel(targetName);
            nameLabel.setForeground(Color.decode("#262626"));
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

            String distance = alert.get("alert_distance").toString();
            JLabel distLabel = new JLabel("알람 거리: " + distance + "m");
            distLabel.setForeground(Color.decode("#8E8E8E"));
            distLabel.setFont(new Font("Arial", Font.PLAIN, 12));

            infoPanel.add(nameLabel);
            infoPanel.add(Box.createVerticalStrut(3));
            infoPanel.add(distLabel);
            add(infoPanel, BorderLayout.CENTER);

            ImageIcon trashIcon = new ImageIcon(Resize.resizeImage("Front/.src/trashbin_icon.png", 18, 18, 0.5f));
            JButton deleteBtn = new JButton(trashIcon);
            deleteBtn.setOpaque(false);
            deleteBtn.setBorderPainted(false);
            deleteBtn.setFocusPainted(false);
            deleteBtn.setContentAreaFilled(false);

            deleteBtn.addActionListener(e -> {
                if (server.DeleteLocationAlertRequest(alert.get("alert_id").toString())) {
                    parent.refresh();
                }
            });

            add(deleteBtn, BorderLayout.EAST);
        }
    }
}

// 간소화된 알람 추가 다이얼로그
class SimpleAddAlertDialog extends JPanel implements ActionListener {
    JDialog dialog;
    RoundButton btn;
    JTextField targetUserField;
    JTextField distanceField;

    public SimpleAddAlertDialog() {
        setPreferredSize(new Dimension(350, 200));
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);

        JLabel userLabel = new JLabel("대상 친구 ID");
        userLabel.setForeground(Color.decode("#262626"));
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));

        targetUserField = new JTextField();
        targetUserField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel distLabel = new JLabel("알람 거리 (미터)");
        distLabel.setForeground(Color.decode("#262626"));
        distLabel.setFont(new Font("Arial", Font.BOLD, 14));

        distanceField = new JTextField("500");
        distanceField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        formPanel.add(userLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(targetUserField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(distLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(distanceField);

        add(formPanel, BorderLayout.CENTER);

        btn = new RoundButton("확인", 10);
        btn.setBackground(Color.decode("#0095F6"));
        btn.setForeground(Color.WHITE);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
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

// 상단 패널
class LocationTopPanel extends JPanel {
    LocationTopPanel(Navigator nav, String id, String nName, SimpleAlertListPanel alertList) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(Color.decode("#FAFAFA"));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#DBDBDB")));

        ImageIcon backIcon = new ImageIcon(Resize.resizeImage("Front/.src/left-arrow_line.png", 30, 30, 1));
        JButton backBtn = new JButton(backIcon);
        backBtn.setOpaque(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.addActionListener(e -> nav.openMain(id, nName));

        add(backBtn);
        add(Box.createHorizontalGlue());

        ImageIcon addIcon = new ImageIcon(Resize.resizeImage("Front/.src/add_friend_icon.png", 30, 30, 1));
        JButton addAlertBtn = new JButton(addIcon);
        addAlertBtn.setOpaque(false);
        addAlertBtn.setBorderPainted(false);
        addAlertBtn.setFocusPainted(false);
        addAlertBtn.setContentAreaFilled(false);

        // Alert 추가 기능은 AlertList에서 직접 처리
        addAlertBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(null,
                "알람 관리 탭에서 알람을 추가하세요",
                "안내",
                JOptionPane.INFORMATION_MESSAGE);
        });

        add(addAlertBtn);

        ImageIcon refreshIcon = new ImageIcon(Resize.resizeImage("Front/.src/restart_icon_black.png", 25, 25, 1));
        JButton refreshBtn = new JButton(refreshIcon);
        refreshBtn.setOpaque(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setContentAreaFilled(false);
        refreshBtn.addActionListener(e -> alertList.refresh());

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

        setTitle("HongStar - 위치 지도");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 800);
        setLocationRelativeTo(null);
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        MapPanel mapPanel = new MapPanel(id, server, infoServer);
        SimpleAlertListPanel alertList = new SimpleAlertListPanel(id, server, infoServer);

        // 알람 추가 버튼을 alertList에 추가
        JPanel alertTabPanel = new JPanel(new BorderLayout());
        alertTabPanel.setBackground(Color.WHITE);
        
        JPanel addAlertBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addAlertBtnPanel.setBackground(Color.WHITE);
        addAlertBtnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        RoundButton addAlertBtn = new RoundButton("+ 알람 추가", 10);
        addAlertBtn.setBackground(Color.decode("#00C853"));
        addAlertBtn.setForeground(Color.WHITE);
        addAlertBtn.setPreferredSize(new Dimension(150, 40));
        
        SimpleAddAlertDialog addDialog = new SimpleAddAlertDialog();
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
        addAlertBtnPanel.add(addAlertBtn);
        
        alertTabPanel.add(alertList, BorderLayout.CENTER);
        alertTabPanel.add(addAlertBtnPanel, BorderLayout.SOUTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.addTab("🗺️ 지도", mapPanel);
        tabbedPane.addTab("🔔 알람", alertTabPanel);

        LocationTopPanel topPanel = new LocationTopPanel(nav, id, nName, alertList);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }
}
