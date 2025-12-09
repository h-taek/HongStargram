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

// 달력 그리드 패널
class CalendarGridPanel extends JPanel {
    private Navigator nav;
    private String userId;
    private RoutineServerClient server;
    private Calendar calendar;
    private JLabel monthLabel;
    private JPanel daysPanel;
    private Map<String, List<Map<String, Object>>> eventsMap;

    public CalendarGridPanel(Navigator nav, String userId, RoutineServerClient server) {
        this.nav = nav;
        this.userId = userId;
        this.server = server;
        this.calendar = Calendar.getInstance();
        this.eventsMap = new HashMap<>();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 상단 월/년 표시 및 이동 버튼
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton prevBtn = new JButton("<");
        prevBtn.setFont(new Font("Noto Sans KR", Font.BOLD, 16));
        prevBtn.addActionListener(e -> changeMonth(-1));
        
        JButton nextBtn = new JButton(">");
        nextBtn.setFont(new Font("Noto Sans KR", Font.BOLD, 16));
        nextBtn.addActionListener(e -> changeMonth(1));

        monthLabel = new JLabel();
        monthLabel.setFont(new Font("Noto Sans KR", Font.BOLD, 18));
        monthLabel.setForeground(Color.decode("#262626"));
        monthLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(prevBtn, BorderLayout.WEST);
        headerPanel.add(monthLabel, BorderLayout.CENTER);
        headerPanel.add(nextBtn, BorderLayout.EAST);

        // 요일 헤더
        JPanel weekHeaderPanel = new JPanel(new GridLayout(1, 7));
        weekHeaderPanel.setBackground(Color.WHITE);
        weekHeaderPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        String[] weekDays = {"일", "월", "화", "수", "목", "금", "토"};
        for (int i = 0; i < 7; i++) {
            JLabel dayLabel = new JLabel(weekDays[i], SwingConstants.CENTER);
            dayLabel.setFont(new Font("Noto Sans KR", Font.BOLD, 14));
            dayLabel.setForeground(i == 0 ? Color.RED : (i == 6 ? Color.BLUE : Color.decode("#262626")));
            weekHeaderPanel.add(dayLabel);
        }

        // 날짜 그리드
        daysPanel = new JPanel(new GridLayout(0, 7));
        daysPanel.setBackground(Color.WHITE);
        daysPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        add(headerPanel, BorderLayout.NORTH);
        add(weekHeaderPanel, BorderLayout.CENTER);
        
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(Color.WHITE);
        centerWrapper.add(daysPanel, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(centerWrapper);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        refresh();
    }

    private void changeMonth(int delta) {
        calendar.add(Calendar.MONTH, delta);
        refresh();
    }

    public void refresh() {
        // 이벤트 데이터 로드
        loadEvents();

        // 월/년 라벨 업데이트
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월");
        monthLabel.setText(sdf.format(calendar.getTime()));

        // 날짜 그리드 초기화
        daysPanel.removeAll();

        Calendar cal = (Calendar) calendar.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; // 0: 일요일
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // 이전 달 빈 칸
        for (int i = 0; i < firstDayOfWeek; i++) {
            daysPanel.add(new JLabel(""));
        }

        // 현재 달 날짜
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int day = 1; day <= daysInMonth; day++) {
            cal.set(Calendar.DAY_OF_MONTH, day);
            String dateStr = dateFormat.format(cal.getTime());
            
            DayCell dayCell = new DayCell(day, dateStr, eventsMap.get(dateStr));
            daysPanel.add(dayCell);
        }

        daysPanel.revalidate();
        daysPanel.repaint();
    }

    private void loadEvents() {
        eventsMap.clear();
        
        // 현재 월의 시작일과 종료일
        Calendar startCal = (Calendar) calendar.clone();
        startCal.set(Calendar.DAY_OF_MONTH, 1);
        
        Calendar endCal = (Calendar) calendar.clone();
        endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = sdf.format(startCal.getTime());
        String endDate = sdf.format(endCal.getTime());

        List<Map<String, Object>> events = server.GetRoutineEventsRequest(userId, startDate, endDate);
        
        if (events != null) {
            for (Map<String, Object> event : events) {
                String eventDate = event.get("event_date").toString();
                eventsMap.computeIfAbsent(eventDate, k -> new ArrayList<>()).add(event);
            }
        }
    }

    // 날짜 셀 클래스
    class DayCell extends JPanel {
        private int day;
        private String dateStr;
        private List<Map<String, Object>> events;

        public DayCell(int day, String dateStr, List<Map<String, Object>> events) {
            this.day = day;
            this.dateStr = dateStr;
            this.events = events;

            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.decode("#DBDBDB"), 1));
            setPreferredSize(new Dimension(60, 80));

            // 날짜 라벨
            JLabel dayLabel = new JLabel(String.valueOf(day));
            dayLabel.setFont(new Font("Noto Sans KR", Font.BOLD, 12));
            dayLabel.setForeground(Color.decode("#262626"));
            dayLabel.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 0));

            // 이벤트 표시 패널
            JPanel eventsPanel = new JPanel();
            eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
            eventsPanel.setOpaque(false);

            if (events != null && !events.isEmpty()) {
                for (int i = 0; i < Math.min(3, events.size()); i++) {
                    Map<String, Object> event = events.get(i);
                    
                    // 루틴 정보 가져오기
                    String routineId = event.get("routine_id").toString();
                    List<Map<String, Object>> routines = server.GetRoutinesRequest(userId);
                    String color = "#0095F6";
                    String title = "";
                    
                    if (routines != null) {
                        for (Map<String, Object> routine : routines) {
                            if (routine.get("routine_id").toString().equals(routineId)) {
                                color = routine.get("color").toString();
                                title = routine.get("title").toString();
                                break;
                            }
                        }
                    }

                    JPanel eventBar = new JPanel();
                    eventBar.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 1));
                    eventBar.setOpaque(true);
                    eventBar.setBackground(getColorFromString(color));
                    eventBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));

                    JLabel eventLabel = new JLabel(title.length() > 6 ? title.substring(0, 6) + "..." : title);
                    eventLabel.setFont(new Font("Noto Sans KR", Font.PLAIN, 9));
                    eventLabel.setForeground(Color.WHITE);
                    eventBar.add(eventLabel);

                    eventsPanel.add(eventBar);
                }

                if (events.size() > 3) {
                    JLabel moreLabel = new JLabel("+" + (events.size() - 3));
                    moreLabel.setFont(new Font("Noto Sans KR", Font.PLAIN, 8));
                    moreLabel.setForeground(Color.decode("#8E8E8E"));
                    eventsPanel.add(moreLabel);
                }
            }

            add(dayLabel, BorderLayout.NORTH);
            add(eventsPanel, BorderLayout.CENTER);

            // 클릭 이벤트
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showDayEvents();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(Color.decode("#F5F5F5"));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(Color.WHITE);
                }
            });
        }

        private void showDayEvents() {
            if (events == null || events.isEmpty()) {
                // 이벤트 추가 다이얼로그 표시
                showAddEventDialog();
            } else {
                // 이벤트 상세 보기
                showEventDetails();
            }
        }

        private void showAddEventDialog() {
            JOptionPane.showMessageDialog(null, 
                dateStr + "에 일정을 추가하려면\n루틴 목록에서 루틴을 먼저 생성하세요.",
                "알림", 
                JOptionPane.INFORMATION_MESSAGE);
        }

        private void showEventDetails() {
            StringBuilder sb = new StringBuilder();
            sb.append(dateStr).append(" 일정:\n\n");
            
            for (Map<String, Object> event : events) {
                String routineId = event.get("routine_id").toString();
                List<Map<String, Object>> routines = server.GetRoutinesRequest(userId);
                String title = "루틴";
                
                if (routines != null) {
                    for (Map<String, Object> routine : routines) {
                        if (routine.get("routine_id").toString().equals(routineId)) {
                            title = routine.get("title").toString();
                            break;
                        }
                    }
                }
                
                sb.append("• ").append(title).append(" - ");
                sb.append(event.get("event_time")).append("\n");
            }

            JOptionPane.showMessageDialog(null, sb.toString(), "일정 상세", JOptionPane.INFORMATION_MESSAGE);
        }

        private Color getColorFromString(String color) {
            switch (color.toLowerCase()) {
                case "blue": return Color.decode("#0095F6");
                case "green": return Color.decode("#4CAF50");
                case "red": return Color.decode("#F44336");
                case "purple": return Color.decode("#9C27B0");
                case "orange": return Color.decode("#FF9800");
                case "pink": return Color.decode("#E91E63");
                default: return Color.decode("#0095F6");
            }
        }
    }
}

// 루틴 목록 패널 (간소화)
class SimpleRoutineListPanel extends JPanel {
    Navigator nav;
    String id;
    RoutineServerClient server;
    JPanel listPanel;

    SimpleRoutineListPanel(Navigator nav, String id, RoutineServerClient server) {
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
        add(scrollPane, BorderLayout.CENTER);

        refresh();
    }

    void refresh() {
        listPanel.removeAll();

        List<Map<String, Object>> routines = server.GetRoutinesRequest(id);
        
        if (routines != null && !routines.isEmpty()) {
            for (Map<String, Object> routine : routines) {
                listPanel.add(new SimpleRoutinePanel(routine, server, this));
            }
        } else {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setBackground(Color.WHITE);
            emptyPanel.setPreferredSize(new Dimension(500, 200));

            JLabel emptyLabel = new JLabel("루틴을 추가하세요");
            emptyLabel.setFont(new Font("Noto Sans KR", Font.PLAIN, 16));
            emptyLabel.setForeground(Color.decode("#8E8E8E"));

            emptyPanel.add(emptyLabel);
            listPanel.add(emptyPanel);
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    class SimpleRoutinePanel extends JPanel {
        SimpleRoutinePanel(Map<String, Object> routine, RoutineServerClient server, SimpleRoutineListPanel parent) {
            setLayout(new BorderLayout(10, 10));
            setOpaque(true);
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#DBDBDB")));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            String color = routine.get("color").toString();
            JPanel colorPanel = new JPanel();
            colorPanel.setPreferredSize(new Dimension(5, 50));
            colorPanel.setBackground(getColorFromString(color));
            add(colorPanel, BorderLayout.WEST);

            JLabel titleLabel = new JLabel(routine.get("title").toString());
            titleLabel.setForeground(Color.decode("#262626"));
            titleLabel.setFont(new Font("Noto Sans KR", Font.BOLD, 14));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
            add(titleLabel, BorderLayout.CENTER);

            ImageIcon icon = new ImageIcon(Resize.resizeImage("Front/.src/trashbin_icon.png", 18, 18, 0.5f));
            JButton deleteBtn = new JButton(icon);
            deleteBtn.setOpaque(false);
            deleteBtn.setBorderPainted(false);
            deleteBtn.setFocusPainted(false);
            deleteBtn.setContentAreaFilled(false);

            deleteBtn.addActionListener(e -> {
                if (server.DeleteRoutineRequest(routine.get("routine_id").toString())) {
                    parent.refresh();
                }
            });

            add(deleteBtn, BorderLayout.EAST);
        }

        private Color getColorFromString(String color) {
            switch (color.toLowerCase()) {
                case "blue": return Color.decode("#0095F6");
                case "green": return Color.decode("#4CAF50");
                case "red": return Color.decode("#F44336");
                case "purple": return Color.decode("#9C27B0");
                case "orange": return Color.decode("#FF9800");
                case "pink": return Color.decode("#E91E63");
                default: return Color.decode("#0095F6");
            }
        }
    }
}

// 루틴 추가 간소화 다이얼로그 (기존 코드 재사용)
class SimpleAddRoutineDialog extends JPanel implements ActionListener {
    JDialog dialog;
    RoundButton btn;
    JTextField titleField;
    JTextArea descField;
    JComboBox<String> colorComboBox;

    public SimpleAddRoutineDialog() {
        setPreferredSize(new Dimension(400, 300));
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("루틴 제목");
        titleLabel.setForeground(Color.decode("#262626"));
        titleLabel.setFont(new Font("Noto Sans KR", Font.BOLD, 14));
        
        titleField = new JTextField();
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        titleField.setBackground(Color.decode("#FAFAFA"));

        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(titleField);
        formPanel.add(Box.createVerticalStrut(10));

        JLabel descLabel = new JLabel("설명");
        descLabel.setForeground(Color.decode("#262626"));
        descLabel.setFont(new Font("Noto Sans KR", Font.BOLD, 14));

        descField = new JTextArea(3, 20);
        descField.setLineWrap(true);
        descField.setBackground(Color.decode("#FAFAFA"));
        JScrollPane scrollPane = new JScrollPane(descField);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        formPanel.add(descLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(scrollPane);
        formPanel.add(Box.createVerticalStrut(10));

        JLabel colorLabel = new JLabel("색상");
        colorLabel.setForeground(Color.decode("#262626"));
        colorLabel.setFont(new Font("Noto Sans KR", Font.BOLD, 14));

        String[] colors = {"Blue", "Green", "Red", "Purple", "Orange", "Pink"};
        colorComboBox = new JComboBox<>(colors);
        colorComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        formPanel.add(colorLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(colorComboBox);

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
        dialog = new JDialog(null, "루틴 추가", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(this);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}

// 상단 패널
class RoutineTopPanel extends JPanel {
    RoutineTopPanel(Navigator nav, String id, String nName, RoutineServerClient server, 
                    CalendarGridPanel calendar, SimpleRoutineListPanel routineList) {
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

        ImageIcon addIcon = new ImageIcon(Resize.resizeImage("Front/.src/add_routine.png", 30, 30, 1));
        JButton addRoutineBtn = new JButton(addIcon);
        addRoutineBtn.setOpaque(false);
        addRoutineBtn.setBorderPainted(false);
        addRoutineBtn.setFocusPainted(false);
        addRoutineBtn.setContentAreaFilled(false);

        SimpleAddRoutineDialog addDialog = new SimpleAddRoutineDialog();
        addDialog.btn.addActionListener(e -> {
            String title = addDialog.titleField.getText();
            String desc = addDialog.descField.getText();
            String color = addDialog.colorComboBox.getSelectedItem().toString();

            if (title.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "제목을 입력하세요", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (server.AddRoutineRequest(id, title, desc, color) != null) {
                routineList.refresh();
                calendar.refresh();
                addDialog.dialog.dispose();
            }
        });

        addRoutineBtn.addActionListener(addDialog);
        add(addRoutineBtn);

        ImageIcon refreshIcon = new ImageIcon(Resize.resizeImage("Front/.src/restart_icon_black.png", 25, 25, 1));
        JButton refreshBtn = new JButton(refreshIcon);
        refreshBtn.setOpaque(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setContentAreaFilled(false);
        refreshBtn.addActionListener(e -> {
            calendar.refresh();
            routineList.refresh();
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

        setTitle("HongStar - 루틴 달력");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 800);
        setLocationRelativeTo(null);
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        CalendarGridPanel calendar = new CalendarGridPanel(nav, id, server);
        SimpleRoutineListPanel routineList = new SimpleRoutineListPanel(nav, id, server);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.addTab("📅 달력", calendar);
        tabbedPane.addTab("📋 루틴 목록", routineList);

        RoutineTopPanel topPanel = new RoutineTopPanel(nav, id, nName, server, calendar, routineList);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }
}
