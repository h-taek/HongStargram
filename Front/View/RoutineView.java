package Front.View;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.text.SimpleDateFormat;

import Front.App.Navigator;
import Front.Resize.Resize;
import Front.Server.*;

// 루틴 추가 다이얼로그
class AddRoutineDialog extends JPanel implements ActionListener {
    JDialog dialog;
    RoundButton btn;
    JTextField titleField;
    JTextArea descField;
    JComboBox<String> colorComboBox;

    public AddRoutineDialog() {
        setPreferredSize(new Dimension(450, 350));
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);

        // 제목 입력
        JLabel titleLabel = new JLabel("루틴 제목");
        titleLabel.setForeground(Color.decode("#262626"));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleField = new JTextField();
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        titleField.setBackground(Color.decode("#FAFAFA"));
        titleField.setForeground(Color.decode("#262626"));
        titleField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#DBDBDB"), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(titleField);
        formPanel.add(Box.createVerticalStrut(15));

        // 설명 입력
        JLabel descLabel = new JLabel("설명");
        descLabel.setForeground(Color.decode("#262626"));
        descLabel.setFont(new Font("Arial", Font.BOLD, 14));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        descField = new JTextArea(4, 20);
        descField.setLineWrap(true);
        descField.setWrapStyleWord(true);
        descField.setBackground(Color.decode("#FAFAFA"));
        descField.setForeground(Color.decode("#262626"));
        descField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#DBDBDB"), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        JScrollPane scrollPane = new JScrollPane(descField);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#DBDBDB"), 1));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        formPanel.add(descLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(scrollPane);
        formPanel.add(Box.createVerticalStrut(15));

        // 색상 선택
        JLabel colorLabel = new JLabel("색상");
        colorLabel.setForeground(Color.decode("#262626"));
        colorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        colorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] colors = { "Blue", "Green", "Red", "Purple", "Orange", "Pink" };
        colorComboBox = new JComboBox<>(colors);
        colorComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        colorComboBox.setBackground(Color.WHITE);

        formPanel.add(colorLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(colorComboBox);

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
        dialog = new JDialog(null, "루틴 추가", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(this);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}

// 루틴 이벤트 추가 다이얼로그
class AddEventDialog extends JPanel implements ActionListener {
    JDialog dialog;
    RoundButton btn;
    JTextField dateField;
    JTextField timeField;
    String routineId;

    public AddEventDialog(String routineId) {
        this.routineId = routineId;

        setPreferredSize(new Dimension(400, 250));
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);

        // 날짜 입력
        JLabel dateLabel = new JLabel("날짜 (YYYY-MM-DD)");
        dateLabel.setForeground(Color.decode("#262626"));
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        dateField = new JTextField();
        dateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        dateField.setBackground(Color.decode("#FAFAFA"));
        dateField.setForeground(Color.decode("#262626"));
        dateField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#DBDBDB"), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateField.setText(sdf.format(new Date()));

        formPanel.add(dateLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(dateField);
        formPanel.add(Box.createVerticalStrut(15));

        // 시간 입력
        JLabel timeLabel = new JLabel("시간 (HH:MM)");
        timeLabel.setForeground(Color.decode("#262626"));
        timeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        timeField = new JTextField("09:00");
        timeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        timeField.setBackground(Color.decode("#FAFAFA"));
        timeField.setForeground(Color.decode("#262626"));
        timeField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#DBDBDB"), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        formPanel.add(timeLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(timeField);

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
        dialog = new JDialog(null, "이벤트 추가", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(this);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}

// 루틴 패널
class RoutinePanel extends JPanel {
    Map<String, Object> routine;
    RoutineServerClient server;
    RoutineListPanel parent;

    RoutinePanel(Map<String, Object> routine, RoutineServerClient server, RoutineListPanel parent) {
        this.routine = routine;
        this.server = server;
        this.parent = parent;

        setLayout(new BorderLayout(10, 10));
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#DBDBDB")));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // 색상 표시
        String color = routine.get("color").toString();
        Color bgColor = getColorFromString(color);
        JPanel colorPanel = new JPanel();
        colorPanel.setPreferredSize(new Dimension(5, 60));
        colorPanel.setBackground(bgColor);
        add(colorPanel, BorderLayout.WEST);

        // 루틴 정보
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));

        JLabel titleLabel = new JLabel(routine.get("title").toString());
        titleLabel.setForeground(Color.decode("#262626"));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel descLabel = new JLabel(routine.get("description").toString());
        descLabel.setForeground(Color.decode("#8E8E8E"));
        descLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(descLabel);

        add(infoPanel, BorderLayout.CENTER);

        // 삭제 버튼
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 10));

        ImageIcon icon = new ImageIcon(Resize.resizeImage("Front/.src/trashbin_icon.png", 20, 20, 0.5f));
        JButton deleteBtn = new JButton(icon);
        deleteBtn.setOpaque(false);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setContentAreaFilled(false);

        deleteBtn.addActionListener(e -> {
            String routineId = routine.get("routine_id").toString();
            if (server.DeleteRoutineRequest(routineId)) {
                parent.refresh();
            }
        });

        btnPanel.add(deleteBtn);
        add(btnPanel, BorderLayout.EAST);
    }

    private Color getColorFromString(String color) {
        switch (color.toLowerCase()) {
            case "blue":
                return Color.decode("#0095F6");
            case "green":
                return Color.decode("#4CAF50");
            case "red":
                return Color.decode("#F44336");
            case "purple":
                return Color.decode("#9C27B0");
            case "orange":
                return Color.decode("#FF9800");
            case "pink":
                return Color.decode("#E91E63");
            default:
                return Color.decode("#0095F6");
        }
    }
}

// 루틴 이벤트 패널
class RoutineEventPanel extends JPanel {
    Map<String, Object> event;
    Map<String, Object> routine;
    RoutineServerClient server;
    RoutineEventListPanel parent;

    RoutineEventPanel(Map<String, Object> event, Map<String, Object> routine, RoutineServerClient server,
            RoutineEventListPanel parent) {
        this.event = event;
        this.routine = routine;
        this.server = server;
        this.parent = parent;

        setLayout(new BorderLayout(10, 10));
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#DBDBDB")));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // 완료 체크박스
        String isCompleted = event.getOrDefault("is_completed", "N").toString();
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected("Y".equals(isCompleted));
        checkBox.setOpaque(false);
        checkBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        checkBox.addActionListener(e -> {
            String eventId = event.get("event_id").toString();
            server.ToggleEventCompletionRequest(eventId);
            parent.refresh();
        });

        add(checkBox, BorderLayout.WEST);

        // 이벤트 정보
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel titleLabel = new JLabel(routine.get("title").toString());
        titleLabel.setForeground(Color.decode("#262626"));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));

        String dateTime = event.get("event_date") + " " + event.get("event_time");
        JLabel timeLabel = new JLabel(dateTime);
        timeLabel.setForeground(Color.decode("#8E8E8E"));
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(timeLabel);

        add(infoPanel, BorderLayout.CENTER);

        // 삭제 버튼
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 10));

        ImageIcon icon = new ImageIcon(Resize.resizeImage("Front/.src/trashbin_icon.png", 18, 18, 0.5f));
        JButton deleteBtn = new JButton(icon);
        deleteBtn.setOpaque(false);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setContentAreaFilled(false);

        deleteBtn.addActionListener(e -> {
            String eventId = event.get("event_id").toString();
            if (server.DeleteRoutineEventRequest(eventId)) {
                parent.refresh();
            }
        });

        btnPanel.add(deleteBtn);
        add(btnPanel, BorderLayout.EAST);
    }
}

// 루틴 목록 패널
class RoutineListPanel extends JPanel {
    Navigator nav;
    String id;
    RoutineServerClient server;
    JPanel listPanel;

    RoutineListPanel(Navigator nav, String id, RoutineServerClient server) {
        this.nav = nav;
        this.id = id;
        this.server = server;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

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

        List<Map<String, Object>> routines = server.GetRoutinesRequest(id);

        if (routines != null && !routines.isEmpty()) {
            for (Map<String, Object> routine : routines) {
                RoutinePanel panel = new RoutinePanel(routine, server, this);
                listPanel.add(panel);
            }
        } else {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new GridBagLayout());
            emptyPanel.setBackground(Color.WHITE);
            emptyPanel.setPreferredSize(new Dimension(500, 400));

            JLabel emptyLabel = new JLabel("등록된 루틴이 없습니다");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            emptyLabel.setForeground(Color.decode("#8E8E8E"));

            emptyPanel.add(emptyLabel);
            listPanel.add(emptyPanel);
        }

        listPanel.revalidate();
        listPanel.repaint();
    }
}

// 루틴 이벤트 목록 패널
class RoutineEventListPanel extends JPanel {
    Navigator nav;
    String id;
    RoutineServerClient server;
    JPanel listPanel;

    RoutineEventListPanel(Navigator nav, String id, RoutineServerClient server) {
        this.nav = nav;
        this.id = id;
        this.server = server;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

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

        // 오늘부터 7일간의 이벤트 조회
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, 7);
        Date endDate = cal.getTime();

        String startDate = sdf.format(today);
        String endDateStr = sdf.format(endDate);

        List<Map<String, Object>> events = server.GetRoutineEventsRequest(id, startDate, endDateStr);

        if (events != null && !events.isEmpty()) {
            // 루틴 정보도 함께 조회
            List<Map<String, Object>> routines = server.GetRoutinesRequest(id);
            Map<String, Map<String, Object>> routineMap = new HashMap<>();
            if (routines != null) {
                for (Map<String, Object> routine : routines) {
                    routineMap.put(routine.get("routine_id").toString(), routine);
                }
            }

            for (Map<String, Object> event : events) {
                String routineId = event.get("routine_id").toString();
                Map<String, Object> routine = routineMap.get(routineId);
                if (routine != null) {
                    RoutineEventPanel panel = new RoutineEventPanel(event, routine, server, this);
                    listPanel.add(panel);
                }
            }
        } else {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new GridBagLayout());
            emptyPanel.setBackground(Color.WHITE);
            emptyPanel.setPreferredSize(new Dimension(500, 300));

            JLabel emptyLabel = new JLabel("다가오는 일정이 없습니다");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            emptyLabel.setForeground(Color.decode("#8E8E8E"));

            emptyPanel.add(emptyLabel);
            listPanel.add(emptyPanel);
        }

        listPanel.revalidate();
        listPanel.repaint();
    }
}

// 상단 패널
class RoutineTopPanel extends JPanel {
    Navigator nav;
    String id;
    String nName;
    RoutineServerClient server;
    RoutineListPanel routineList;
    RoutineEventListPanel eventList;

    RoutineTopPanel(Navigator nav, String id, String nName, RoutineServerClient server, RoutineListPanel routineList,
            RoutineEventListPanel eventList) {
        this.nav = nav;
        this.id = id;
        this.nName = nName;
        this.server = server;
        this.routineList = routineList;
        this.eventList = eventList;

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

        // 루틴 추가 버튼
        ImageIcon addIcon = new ImageIcon(Resize.resizeImage("Front/.src/add_friend_icon.png", 30, 30, 1));
        JButton addRoutineBtn = new JButton(addIcon);
        addRoutineBtn.setBackground(Color.decode("#FAFAFA"));
        addRoutineBtn.setBorderPainted(false);
        addRoutineBtn.setFocusPainted(false);
        addRoutineBtn.setContentAreaFilled(false);

        AddRoutineDialog addDialog = new AddRoutineDialog();
        addDialog.btn.addActionListener(e -> {
            String title = addDialog.titleField.getText();
            String desc = addDialog.descField.getText();
            String color = addDialog.colorComboBox.getSelectedItem().toString();

            if (title.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "제목을 입력하세요", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String result = server.AddRoutineRequest(id, title, desc, color);
            if (result != null) {
                routineList.refresh();
                addDialog.dialog.dispose();
            }
        });

        addRoutineBtn.addActionListener(addDialog);
        add(addRoutineBtn);

        // 새로고침 버튼
        ImageIcon refreshIcon = new ImageIcon(Resize.resizeImage("Front/.src/restart_icon_black.png", 25, 25, 1));
        JButton refreshBtn = new JButton(refreshIcon);
        refreshBtn.setBackground(Color.decode("#FAFAFA"));
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setContentAreaFilled(false);
        refreshBtn.addActionListener(e -> {
            routineList.refresh();
            eventList.refresh();
        });

        add(refreshBtn);
    }
}

public class RoutineView extends JFrame {
    Navigator nav;
    String id;
    String nName;
    RoutineServerClient server = new RoutineServerClient();

    public RoutineView(Navigator nav, String id, String nName) {
        this.nav = nav;
        this.id = id;
        this.nName = nName;

        setTitle("HongStar - 루틴 관리");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 800);
        setLocationRelativeTo(null);
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // 탭 패널
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(Color.decode("#262626"));

        RoutineListPanel routineList = new RoutineListPanel(nav, id, server);
        RoutineEventListPanel eventList = new RoutineEventListPanel(nav, id, server);

        // 루틴 탭에 이벤트 추가 기능 포함
        JPanel routineTab = new JPanel(new BorderLayout());
        routineTab.setBackground(Color.WHITE);

        // 루틴 선택 후 이벤트 추가 버튼
        JPanel routineBottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        routineBottomPanel.setBackground(Color.WHITE);
        routineBottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        RoundButton addEventBtn = new RoundButton("선택한 루틴에 일정 추가", 10);
        addEventBtn.setBackground(Color.decode("#0095F6"));
        addEventBtn.setForeground(Color.WHITE);
        addEventBtn.setPreferredSize(new Dimension(200, 40));

        addEventBtn.addActionListener(e -> {
            // 루틴 선택 다이얼로그
            List<Map<String, Object>> routines = server.GetRoutinesRequest(id);
            if (routines == null || routines.isEmpty()) {
                JOptionPane.showMessageDialog(null, "먼저 루틴을 추가하세요", "알림", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] routineTitles = routines.stream()
                    .map(r -> r.get("title").toString())
                    .toArray(String[]::new);

            String selected = (String) JOptionPane.showInputDialog(
                    null,
                    "루틴을 선택하세요:",
                    "일정 추가",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    routineTitles,
                    routineTitles[0]);

            if (selected != null) {
                Map<String, Object> selectedRoutine = null;
                for (Map<String, Object> r : routines) {
                    if (r.get("title").toString().equals(selected)) {
                        selectedRoutine = r;
                        break;
                    }
                }

                if (selectedRoutine != null) {
                    String routineId = selectedRoutine.get("routine_id").toString();
                    AddEventDialog eventDialog = new AddEventDialog(routineId);
                    eventDialog.btn.addActionListener(evt -> {
                        String date = eventDialog.dateField.getText();
                        String time = eventDialog.timeField.getText();

                        if (date.trim().isEmpty() || time.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(null, "날짜와 시간을 입력하세요", "경고", JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        String result = server.AddRoutineEventRequest(routineId, date, time);
                        if (result != null) {
                            eventList.refresh();
                            eventDialog.dialog.dispose();
                        }
                    });

                    eventDialog.actionPerformed(null);
                }
            }
        });

        routineBottomPanel.add(addEventBtn);
        routineTab.add(routineList, BorderLayout.CENTER);
        routineTab.add(routineBottomPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("루틴 목록", routineTab);
        tabbedPane.addTab("다가오는 일정", eventList);

        RoutineTopPanel topPanel = new RoutineTopPanel(nav, id, nName, server, routineList, eventList);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }
}
