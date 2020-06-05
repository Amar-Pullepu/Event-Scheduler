package eventscheduler;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.*;
import javax.swing.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.table.*;



/*
    Change MYSQL Username and password at line no: 320 & 868.

    MYSQL Schema:
        Table Name: eventscheduler;
            ID intID int,
            DateTime datetime
            Title varchar(50)
            Description varchar(500)

    Table SQL Command : 
        CREATE TABLE eventScheduler(
                                        ID int PRIMARY KEY NOT NULL AUTO_INCREMENT, 
                                        DateTime datetime NOT NULL, 
                                        Title varchar(50) NOT NULL, 
                                        Description varchar(500)
                                                    );
*/

final class DatePicker {
	int month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH);
	int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);;
	JLabel l = new JLabel("", JLabel.CENTER);
	String day = "";
	JDialog d;
	JButton[] button = new JButton[49];
                JButton previous, next;

	public DatePicker(JFrame parent) {
		d = new JDialog();
		d.setModal(true);
		String[] header = { "Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat" };
		JPanel p1 = new JPanel(new GridLayout(7, 7));
		p1.setPreferredSize(new Dimension(430, 120));

		for (int x = 0; x < button.length; x++) {
			final int selection = x;
			button[x] = new JButton();
			button[x].setFocusPainted(false);
			button[x].setBackground(Color.white);
			if (x > 6)
				button[x].addActionListener(new ActionListener() {
                                                                                @Override
					public void actionPerformed(ActionEvent ae) {
						day = button[selection].getActionCommand();
						d.dispose();
					}
				});
			if (x < 7) {
				button[x].setText(header[x]);
				button[x].setForeground(Color.red);
			}
			p1.add(button[x]);
		}
		JPanel p2 = new JPanel(new GridLayout(1, 3));
		previous = new JButton("<< Previous");
		previous.addActionListener(new ActionListener() {
                                                @Override
			public void actionPerformed(ActionEvent ae) {
				month--;
				displayDate();
			}
		});
		p2.add(previous);
		p2.add(l);
		next = new JButton("Next >>");
		next.addActionListener(new ActionListener() {
                                                @Override
			public void actionPerformed(ActionEvent ae) {
				month++;
				displayDate();
			}
		});
		p2.add(next);
		d.add(p1, BorderLayout.CENTER);
		d.add(p2, BorderLayout.SOUTH);
		d.pack();
		d.setLocationRelativeTo(parent);
		displayDate();
		d.setVisible(true);
	}

	public void displayDate() {
		for (int x = 7; x < button.length; x++)
			button[x].setText("");
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"MMMM yyyy");
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.set(year, month, 1);
		int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
		int daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
		for (int x = 7, day = 1; x<49; x++){
                                        if(x >= 6 + dayOfWeek && day <= daysInMonth){
                                            button[x].setForeground(Color.black);
                                            if(month == java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) && day<java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH))
                                                button[x].setEnabled(false);
                                            else
                                                button[x].setEnabled(true);
                                            button[x].setText("" + day++);
                                            button[x].setFont(new Font("Arial", Font.PLAIN, 10));
                                            button[x].setBorder(BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(Color.DARK_GRAY, 1), BorderFactory.createEmptyBorder(5, 5, 10, 10)));
                                        }
                                        else{
                                            button[x].setEnabled(false);
                                        }

                                }
                                if(month == java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)) previous.setEnabled(false);
                                else    previous.setEnabled(true);

		l.setText(sdf.format(cal.getTime()));
		d.setTitle("Date Picker");
	}

	public String setPickedDate() {
		if (day.equals(""))
			return "Null";
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.set(year, month, Integer.parseInt(day));
		return sdf.format(cal.getTime());
	}
}

public class EventScheduler {
    JFrame jf;
    JTabbedPane jtp;
    JPanel cards, datePanel, timePanel, viewPanel, eventsPanel;
    JLabel dateLbl, dateLblDup, HrsLbl, MinsLbl, eventTitleLbl, eventDescLbl, fromLbl, toLbl;
    JButton Add, Modify, Delete, Save, Back, fromBtn, toBtn, viewEventsBtn;
    JTable events, viewEvents;
    JComboBox Hrs, Mins;
    JToggleButton Period;
    JTextField eventTitle;
    JTextArea eventDesc;
    JDialog showEvent;
    DefaultTableModel eventModel, viewModel;
    final static String [] eventHeader=new String[]{"Date", "Time", "Event Title"};
    ArrayList<Integer> id, vid;
    int Id, vId;
    
    boolean addModifyFlag;
    Connection con;
    ResultSet  rs;
    Statement st;
    PreparedStatement pStmt, uStmt, dStmt;
    
    int Hr = 0, Min = 0;
    int selected = -1;
    int month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH);
    int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
    JLabel l = new JLabel("", JLabel.CENTER);
    String day = "";
    JPanel d;
    JButton[] button = new JButton[49];
    JButton previous, next;

    public void changeSelected(int x){
        selected = x;
    }
    
    public void updateTable(){
        try {
            ArrayList<String> date = new ArrayList<>();
            ArrayList<String> time = new ArrayList<>();
            ArrayList<String> title = new ArrayList<>();
            id = new ArrayList<>();
            while(rs.next() && !dateLbl.getText().equals("Date: Null")){
                LocalDate selectedDate = LocalDate.parse(setPickedDate());
                Timestamp extractedTime = rs.getTimestamp("DateTime");
                LocalDateTime extracted = extractedTime.toLocalDateTime();
                if(extracted.toLocalDate().isEqual(selectedDate)){
                    date.add(extracted.toLocalDate().toString());
                    time.add(extracted.toLocalTime().toString());
                    title.add(rs.getString("Title"));
                    id.add(rs.getInt("ID"));
                }
            }
            while(rs.previous());
            String[][] data = new String[time.size()][3];
            for(int i=0; i<time.size(); i++){
                data[i][0] = date.get(i);
                data[i][1] = time.get(i);
                data[i][2] = title.get(i);
            }
            eventModel = new DefaultTableModel(data, eventHeader);
            events.setModel(eventModel);
            events.getColumnModel().getColumn(0).setMaxWidth(80);
            events.getColumnModel().getColumn(1).setMaxWidth(80);
            events.getColumnModel().getColumn(2).setMaxWidth(240);
        } catch (SQLException ex) {
            Logger.getLogger(EventScheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void DatePicker() {
        d = new JPanel();
        String[] header = { "Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat" };
        JPanel p1 = new JPanel(new GridLayout(7, 7));
        p1.setPreferredSize(new Dimension(300, 120));
        
        for (int x = 0; x < button.length; x++) {
                final int selection = x;
                button[x] = new JButton();
                button[x].setFocusPainted(false);
                button[x].setBackground(Color.white);
                if (x > 6)
                        button[x].addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent ae) {
                                    day = button[selection].getActionCommand();
                                    button[selection].setForeground(Color.red);
                                    dateLbl.setText("Date: "+ setPickedDate());
                                    updateTable();
                                    Add.setEnabled(true);
                                    Modify.setEnabled(false);
                                    Delete.setEnabled(false);
                                    changeSelected(selection);
                                    for (int x = 0; x < button.length; x++) {
                                        if (x > 6 && x != selection )  button[x].setForeground(Color.black);
                                    }
                                }
                        });
                if (x < 7) {
                        button[x].setText(header[x]);
                        button[x].setForeground(Color.red);
                        button[x].setFont(new Font("Arial", Font.BOLD, 10));
                        button[x].setBorder(BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(Color.DARK_GRAY, 1), BorderFactory.createEmptyBorder(5, 5, 10, 10)));
                }
                p1.add(button[x]);
        }
        JPanel p2 = new JPanel(new GridLayout(1, 3));
        previous = new JButton("<< Previous");
        previous.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                        month--;
                        displayDate(true);
                        Modify.setEnabled(false);
                        Delete.setEnabled(false);
                        updateTable();
                }
        });
        p2.add(previous);
        p2.add(l);
        next = new JButton("Next >>");
        next.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                        month++;
                        displayDate(true);
                        Modify.setEnabled(false);
                        Delete.setEnabled(false);
                        updateTable();
                }
        });
        p2.add(next);
        d.add(p1, BorderLayout.CENTER);
        d.add(p2, BorderLayout.SOUTH);
        displayDate(false);
        d.setVisible(true);
    }

    public void displayDate(Boolean selected) {
            if(selected){
                Add.setEnabled(false);
                dateLbl.setText("Date: NULL");
            }
            for (int x = 7; x < button.length; x++)
                    button[x].setText("");
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                            "MMMM yyyy");
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(year, month, 1);
            int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
            int daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
            for (int x = 7, day = 1; x<49; x++){
                    if(x >= 6 + dayOfWeek && day <= daysInMonth){
                        button[x].setForeground(Color.black);
                        if(month == java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) && day<java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH))
                            button[x].setEnabled(false);
                        else
                            button[x].setEnabled(true);
                        button[x].setText("" + day++);
                        button[x].setFont(new Font("Arial", Font.PLAIN, 10));
                        button[x].setBorder(BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(Color.DARK_GRAY, 1), BorderFactory.createEmptyBorder(5, 5, 10, 10)));
                    }
                    else{
                        button[x].setEnabled(false);
                    }
                    
            }
            if(month == java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)) previous.setEnabled(false);
            else    previous.setEnabled(true);
            
            l.setText(sdf.format(cal.getTime()));
    }

    public String setPickedDate() {
            if (day.equals(""))
                    return day;
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                            "yyyy-MM-dd");
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(year, month, Integer.parseInt(day));
            return sdf.format(cal.getTime());
    }
    
    
    EventScheduler(){
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/javademo", "root", "2627");
            st = con.createStatement();
            pStmt = con.prepareStatement("INSERT INTO eventscheduler(DateTime, Title, Description) VALUES (?, ?, ?);");
            uStmt = con.prepareStatement("UPDATE  eventscheduler SET DateTime = ?, Title = ?, Description= ? WHERE ID = ?;");
            dStmt = con.prepareStatement("DELETE FROM eventscheduler WHERE ID = ?");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database not connected, Please restart the application.", "DatabaseError", 0);
            Logger.getLogger(EventScheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            rs = st.executeQuery("SELECT ID, DateTime, Title, Description FROM eventscheduler;");
        } catch (SQLException ex) {
            try {
                st.execute("CREATE TABLE eventScheduler(ID int PRIMARY KEY NOT NULL AUTO_INCREMENT, DateTime datetime NOT NULL, Title varchar(50) NOT NULL, Description varchar(500));");
                rs = st.executeQuery("SELECT ID, DateTime, Title, Description FROM eventscheduler;");
            } catch (SQLException ex1) {
                Logger.getLogger(EventScheduler.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        jf = new JFrame("Event Scheduler");
        
        jtp = new JTabbedPane();
        
        cards = new JPanel();
        cards.setLayout(new CardLayout());
        
        datePanel = new JPanel();
        datePanel.setBackground(Color.RED);
        datePanel.setLayout(null);
        
        DatePicker();
        d.setBounds(10,10,310, 160);
        datePanel.add(d);
        
        dateLbl = new JLabel("Date: Null");
        dateLbl.setBounds(115,180, 100, 30);
        dateLbl.setBackground(Color.BLACK);
        dateLbl.setForeground(Color.WHITE);
        dateLbl.setOpaque(true);
        dateLbl.setHorizontalAlignment(SwingConstants.CENTER);
        datePanel.add(dateLbl);
        
        Add = new JButton("Add Event");
        Add.setEnabled(false);
        Add.setBounds(10 , 215, 100, 30 );
        Add.setHorizontalAlignment(SwingConstants.CENTER);
        Add.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(Color.DARK_GRAY, 1), BorderFactory.createEmptyBorder(5, 5, 10, 10)));
        Add.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addModifyFlag = true;
                CardLayout cardLayout = (CardLayout) cards.getLayout();
                dateLblDup.setText(dateLbl.getText());
                cardLayout.show(cards, "Time");
                Hrs.setSelectedIndex(0);
                Mins.setSelectedIndex(0);
                Period.setSelected(false);
                Period.setText("AM");
                eventTitle.setText("");
                eventDesc.setText("");
                Save.setEnabled(false);
            }
        });
        
        Modify = new JButton("Modify Event");
        Modify.setEnabled(false);
        Modify.setBounds(115 , 215, 100, 30 );
        Modify.setHorizontalAlignment(SwingConstants.CENTER);
        Modify.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(Color.DARK_GRAY, 1), BorderFactory.createEmptyBorder(5, 5, 10, 10)));
        Modify.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addModifyFlag = false;
                CardLayout cardLayout = (CardLayout) cards.getLayout();
                dateLblDup.setText(dateLbl.getText());
                cardLayout.show(cards, "Time");
                Id = id.get(events.getSelectedRow());
                try {
                    while(rs.next()){
                        if(rs.getInt("ID") == Id){
                            Timestamp ts = rs.getTimestamp("DateTime");
                            int index = ts.getHours();
                            if(index>11){
                                index-=12;
                                Period.setSelected(true);
                                Period.setText("PM");
                            }
                            else{
                                Period.setSelected(false);
                                Period.setText("AM");
                            }
                            index-=1;
                            if(index == -1) index = 11;
                            Hrs.setSelectedIndex(index);
                            Mins.setSelectedIndex(ts.getMinutes());
                            eventTitle.setText(rs.getString("Title"));
                            eventDesc.setText(rs.getString("Description"));
                            break;
                        }
                    }
                    while(rs.previous());
                } catch (SQLException ex) {
                    Logger.getLogger(EventScheduler.class.getName()).log(Level.SEVERE, null, ex);
                }
                Save.setEnabled(true);
                Delete.setEnabled(false);
                Modify.setEnabled(false);
            }
        });
        
        Delete = new JButton("Delete Event");
        Delete.setEnabled(false);
        Delete.setBounds(220 , 215, 100, 30 );
        Delete.setHorizontalAlignment(SwingConstants.CENTER);
        Delete.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(Color.DARK_GRAY, 1), BorderFactory.createEmptyBorder(5, 5, 10, 10)));
        Delete.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    dStmt.setInt(1, id.get(events.getSelectedRow()));
                    dStmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Succesfully Deleted Event", "Status", 1);
                    rs = st.executeQuery("SELECT * FROM eventscheduler;");
                    updateTable();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error while Deleting Event", "Status", 0);
                    Logger.getLogger(EventScheduler.class.getName()).log(Level.SEVERE, null, ex);
                }
                Delete.setEnabled(false);
                Modify.setEnabled(false);
            }
        });
        
        datePanel.add(Add);
        datePanel.add(Modify);
        datePanel.add(Delete);
        
        String [][] data=new String[0][0];

        eventModel = new DefaultTableModel(data,eventHeader);
        events = new JTable(eventModel);
        events.getColumnModel().getColumn(0).setMaxWidth(80);
        events.getColumnModel().getColumn(1).setMaxWidth(80);
        events.getColumnModel().getColumn(2).setMaxWidth(240);
        events.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Modify.setEnabled(true);
                Delete.setEnabled(true);
            }
        });
        JScrollPane jsp = new JScrollPane(events); 
        jsp.setBounds(10, 250,310, 160);
        datePanel.add(jsp);
        
        cards.add(datePanel, "Date");
        
        timePanel = new JPanel();
        timePanel.setBackground(Color.BLUE);
        timePanel.setLayout(null);
        
        dateLblDup = new JLabel(dateLbl.getText());
        dateLblDup.setBounds(115, 10, 100, 30);
        dateLblDup.setBackground(Color.BLACK);
        dateLblDup.setForeground(Color.WHITE);
        dateLblDup.setOpaque(true);
        dateLblDup.setHorizontalAlignment(SwingConstants.CENTER);
        timePanel.add(dateLblDup);
        
        HrsLbl = new JLabel(" Hours: ");
        HrsLbl.setBounds(20, 50, 50, 30);
        HrsLbl.setOpaque(true);
        HrsLbl.setBackground(Color.BLACK);
        HrsLbl.setForeground(Color.WHITE);
        timePanel.add(HrsLbl);
        
        Hrs = new JComboBox(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"});
        Hrs.setBounds(75, 50, 50, 30);
        timePanel.add(Hrs);
        
        MinsLbl = new JLabel(" Minutes: ");
        MinsLbl.setBounds(135, 50, 60, 30);
        MinsLbl.setOpaque(true);
        MinsLbl.setBackground(Color.BLACK);
        MinsLbl.setForeground(Color.WHITE);
        timePanel.add(MinsLbl);
        
        String[] s = new String[60];
        for(int i = 0; i < 60; i++){
            if(i<10)    s[i] = "0"+Integer.toString(i);
            else    s[i] = Integer.toString(i);
        }
        Mins = new JComboBox(s);
        Mins.setBounds(200, 50, 50, 30);
        timePanel.add(Mins);
        
        Period = new JToggleButton("AM");
        Period.setBounds(260, 50, 50, 30);
        Period.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(Color.DARK_GRAY, 1), BorderFactory.createEmptyBorder(5, 5, 10, 10)));
        Period.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(Period.isSelected()){
                    Period.setText("PM");
                }
                else{
                    Period.setText("AM");
                }
                /*if(Period.getText().equals("PM"))  Hr = Integer.parseInt(Hrs.getSelectedItem().toString()) + 12;
                Min = Integer.parseInt(Mins.getSelectedItem().toString());
                LocalTime localTime = LocalTime.parse( String.format("%02d", Hr)+":"+String.format("%02d", Min)+":00" );*/
            }
        });
        timePanel.add(Period);
        
        eventTitleLbl = new JLabel("Event Title: ");
        eventTitleLbl.setBounds(20, 100, 80, 25);
        eventTitleLbl.setOpaque(true);
        eventTitleLbl.setBackground(Color.BLACK);
        eventTitleLbl.setForeground(Color.WHITE);
        timePanel.add(eventTitleLbl);
        
        eventTitle = new JTextField(20);
        eventTitle.setBounds(110, 100, 200, 25);
        eventTitle.addKeyListener(new KeyListener(){
            @Override
            public void keyTyped(KeyEvent e) {
                
            }
            @Override
            public void keyPressed(KeyEvent e) {
                
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if(eventTitle.getText().length() == 0)  Save.setEnabled(false);
                else Save.setEnabled(true);
            }
        });
                
        timePanel.add(eventTitle);
        
        eventDescLbl = new JLabel("Description: ");
        eventDescLbl.setBounds(20, 135, 80, 25);
        eventDescLbl.setOpaque(true);
        eventDescLbl.setBackground(Color.BLACK);
        eventDescLbl.setForeground(Color.WHITE);
        timePanel.add(eventDescLbl);
        
        eventDesc = new JTextArea();
        eventDesc.setBounds(110, 135, 200, 200);
        timePanel.add(eventDesc);
        
        Back = new JButton("Back");
        Back.setBounds(20, 360, 135, 25);
        Back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                CardLayout cardLayout = (CardLayout) cards.getLayout();
                cardLayout.show(cards, "Date");
            }
        });
        timePanel.add(Back);
        
        Save = new JButton("Save");
        Save.setBounds(175, 360, 135, 25);
        Save.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Hr = Integer.parseInt(Hrs.getSelectedItem().toString());
                    if(Hr == 12)    Hr = 0;
                    if(Period.isSelected()) Hr+=12;
                    LocalDateTime selectedTime = LocalDateTime.parse(setPickedDate()+"T"+String.format("%02d", Hr)+":"+Mins.getSelectedItem().toString()+":00.000");
                    Timestamp tTime = Timestamp.valueOf(selectedTime);
                    if(selectedTime.isBefore(LocalDateTime.now())){
                        JOptionPane.showMessageDialog(null, "Error ( Incorrect Time, Already Passed. )", "Status", 0);
                        return;
                    }
                    if(addModifyFlag){
                        pStmt.setTimestamp(1, tTime);
                        pStmt.setString(2, eventTitle.getText());
                        pStmt.setString(3, eventDesc.getText());
                        pStmt.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Succesfully Created Event", "Status", 1);
                    }
                    else{
                        uStmt.setTimestamp(1, tTime);
                        uStmt.setString(2, eventTitle.getText());
                        uStmt.setString(3, eventDesc.getText());
                        uStmt.setInt(4, Id);
                        uStmt.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Succesfully Modified Event", "Status", 1);
                    }
                    rs = st.executeQuery("SELECT * FROM eventscheduler;");
                    updateTable();
                } catch (SQLException ex) {
                    if(addModifyFlag)
                        JOptionPane.showMessageDialog(null, "Error while Creating Event", "Status", 0);
                    else
                        JOptionPane.showMessageDialog(null, "Error while Modifing Event", "Status", 0);
                    Logger.getLogger(EventScheduler.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                CardLayout cardLayout = (CardLayout) cards.getLayout();
                cardLayout.show(cards, "Date");
            }
        });
        Save.setEnabled(false);
        timePanel.add(Save);
        
        cards.add(timePanel, "Time");
        
        jtp.add(cards, "Add/Modify Events");
        
        viewPanel = new JPanel();
        viewPanel.setBackground(Color.GREEN);
        viewPanel.setLayout(null);
        
        fromLbl = new JLabel("From: ");
        fromLbl.setBounds(10, 20, 45, 30);
        fromLbl.setBackground(Color.BLACK);
        fromLbl.setForeground(Color.WHITE);
        fromLbl.setOpaque(true);
        fromLbl.setHorizontalAlignment(SwingConstants.CENTER);
        viewPanel.add(fromLbl);
        
        toLbl = new JLabel("To: ");
        toLbl.setBounds(170, 20, 45, 30);
        toLbl.setBackground(Color.BLACK);
        toLbl.setForeground(Color.WHITE);
        toLbl.setOpaque(true);
        toLbl.setHorizontalAlignment(SwingConstants.CENTER);
        viewPanel.add(toLbl);
        
        fromBtn = new JButton("Null");
        fromBtn.setBounds(60, 20, 100, 30);
        fromBtn.setHorizontalAlignment(SwingConstants.CENTER);
        fromBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fromBtn.setText(new DatePicker(jf).setPickedDate());
                if(fromBtn.getText().equals("Null") || toBtn.getText().equals("Null")){
                    viewEventsBtn.setEnabled(false);
                }
                else{
                    viewEventsBtn.setEnabled(true);
                }
            }
        });
        viewPanel.add(fromBtn); 
        
        toBtn = new JButton("Null");
        toBtn.setBounds(220, 20, 100, 30);
        toBtn.setHorizontalAlignment(SwingConstants.CENTER);
        toBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toBtn.setText(new DatePicker(jf).setPickedDate());
                if(fromBtn.getText().equals("Null") || toBtn.getText().equals("Null")){
                    viewEventsBtn.setEnabled(false);
                }
                else{
                    viewEventsBtn.setEnabled(true);
                }
            }
        });
        viewPanel.add(toBtn); 
        
        viewEventsBtn = new JButton("View Events");
        viewEventsBtn.setBounds(95, 70, 150, 30);
        viewEventsBtn.setHorizontalAlignment(SwingConstants.CENTER);
        viewEventsBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LocalDateTime fromDate = LocalDateTime.parse(fromBtn.getText()+"T00:00:00.000");
                LocalDateTime toDate = LocalDateTime.parse(toBtn.getText()+"T23:59:59.999");
                try {
                    ArrayList<String> date = new ArrayList<>();
                    ArrayList<String> time = new ArrayList<>();
                    ArrayList<String> title = new ArrayList<>();
                    vid = new ArrayList<>();
                    while(rs.next()){
                        LocalDateTime extracted = rs.getTimestamp("DateTime").toLocalDateTime();
                        if((extracted.isAfter(fromDate) || extracted.isEqual(fromDate)) && extracted.isBefore(toDate)){
                            date.add(extracted.toLocalDate().toString());
                            time.add(extracted.toLocalTime().toString());
                            title.add(rs.getString("Title"));
                            vid.add(rs.getInt("ID"));
                        }
                    }
                    while(rs.previous());
                    String[][] data = new String[time.size()][3];
                    for(int i=0; i<time.size(); i++){
                        data[i][0] = date.get(i);
                        data[i][1] = time.get(i);
                        data[i][2] = title.get(i);
                    }
                    viewModel = new DefaultTableModel(data, eventHeader);
                    viewEvents.setModel(viewModel);
                    viewEvents.getColumnModel().getColumn(0).setMaxWidth(80);
                    viewEvents.getColumnModel().getColumn(1).setMaxWidth(80);
                    viewEvents.getColumnModel().getColumn(2).setMaxWidth(240);
                } catch (SQLException ex) {
                    Logger.getLogger(EventScheduler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        viewEventsBtn.setEnabled(false);
        viewPanel.add(viewEventsBtn); 
        
        JLabel note = new JLabel("Click on the table contents to view event details");
        note.setBounds(30, 120, 300, 20);
        viewPanel.add(note);
        
        data=new String[0][0];

        viewModel = new DefaultTableModel(data, eventHeader);
        viewEvents = new JTable(viewModel);
        viewEvents.getColumnModel().getColumn(0).setMaxWidth(80);
        viewEvents.getColumnModel().getColumn(1).setMaxWidth(80);
        viewEvents.getColumnModel().getColumn(2).setMaxWidth(240);
        viewEvents.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = viewEvents.getSelectedRow();
                viewModel = (DefaultTableModel)viewEvents.getModel();
                vId = vid.get(selectedRow);
                showEvent = new JDialog();
                showEvent.setTitle("Event Descritption");
                showEvent.setModal(true);
                JPanel p1 = new JPanel();
                p1.setLayout(null);
                p1.setPreferredSize(new Dimension(430, 160));
                String tDate = "", tTime = "", tTitle = "", tDesc = "";
                try {
                    while(rs.next()){
                        if(rs.getInt("ID") == vId){
                            Timestamp ts = rs.getTimestamp("DateTime");
                            tDate = String.format("%04d", ts.getYear()+1900)+"-"+String.format("%02d", ts.getMonth()+1)+"-"+String.format("%02d", ts.getDate());
                            tTime = String.format("%02d", ts.getHours())+":"+String.format("%02d", ts.getMinutes());
                            tTitle = rs.getString("Title");
                            tDesc = rs.getString("Description");
                        }
                    }
                    while(rs.previous());
                } catch (SQLException ex) {
                    Logger.getLogger(EventScheduler.class.getName()).log(Level.SEVERE, null, ex);
                }                
                JLabel dateLbl = new JLabel(" Date: "+ tDate);
                dateLbl.setBounds(10,10, 100, 30);
                dateLbl.setBackground(Color.WHITE);
                dateLbl.setForeground(Color.BLACK);
                dateLbl.setOpaque(true);
                dateLbl.setHorizontalAlignment(SwingConstants.LEFT);
                p1.add(dateLbl);
                
                JLabel timeLbl = new JLabel(" Time: "+ tTime);
                timeLbl.setBounds(320,10, 100, 30);
                timeLbl.setBackground(Color.WHITE);
                timeLbl.setForeground(Color.BLACK);
                timeLbl.setOpaque(true);
                timeLbl.setHorizontalAlignment(SwingConstants.LEFT);
                p1.add(timeLbl);
                
                JLabel eventLbl = new JLabel(" Event Title: "+ tTitle);
                eventLbl.setBounds(10,50, 200, 30);
                eventLbl.setBackground(Color.WHITE);
                eventLbl.setForeground(Color.BLACK);
                eventLbl.setOpaque(true);
                eventLbl.setHorizontalAlignment(SwingConstants.LEFT);
                p1.add(eventLbl);
                
                JTextArea eventDesc = new JTextArea();
                eventDesc.setText(" Description: "+tDesc);
                eventDesc.setBounds(10, 90, 410, 60);
                eventDesc.setEditable(false);
                p1.add(eventDesc);
                
                showEvent.add(p1, BorderLayout.CENTER);
                showEvent.pack();
                showEvent.setLocationRelativeTo(jf);
                showEvent.setResizable(false);
                showEvent.setVisible(true);
            }
        });
        
        JScrollPane jsp2 = new JScrollPane(viewEvents); 
        jsp2.setBounds(10, 150,310, 270);
        viewPanel.add(jsp2);
        
        jtp.add(viewPanel, "View Events");
        
        jf.add(jtp);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setSize(350, 500);
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
        jf.setResizable(false);
        
        Thread reminder = new Thread(()->{
                Connection cone;
                Statement ste = null;
                PreparedStatement uStmte= null, dStmte= null;
                try {
                    cone = DriverManager.getConnection("jdbc:mysql://localhost:3306/javademo", "root", "2627");
                    ste = cone.createStatement();
                    uStmte = con.prepareStatement("UPDATE  eventscheduler SET DateTime = ?, Title = ?, Description= ? WHERE ID = ?;");
                    dStmte = con.prepareStatement("DELETE FROM eventscheduler WHERE ID = ?");
                } catch (SQLException ex) {
                    Logger.getLogger(EventScheduler.class.getName()).log(Level.SEVERE, null, ex);
                }
                while(true){
                    try {
                        ResultSet dupRs = ste.executeQuery("SELECT * FROM eventscheduler;");
                        while(dupRs.next()){
                            LocalDateTime tTime = dupRs.getTimestamp("DateTime").toLocalDateTime(), tTim = LocalDateTime.now();
                            if(tTime.getHour()==tTim.getHour() && tTime.getMinute()==tTim.getMinute() && tTime.toLocalDate().isEqual(tTim.toLocalDate())){
                                String tDesc = dupRs.getString("Description");
                                tDesc+="\n Do you wanna snooze the remainder for 5 minutes?";
                                if (JOptionPane.showConfirmDialog(null, tDesc, dupRs.getString("Title"),JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                                    tTime = tTim.plusMinutes(5);
                                    uStmte.setTimestamp(1, Timestamp.valueOf(tTime));
                                    uStmte.setString(2, dupRs.getString("Title"));
                                    uStmte.setString(3, dupRs.getString("Description"));
                                    uStmte.setInt(4, dupRs.getInt("ID"));
                                    uStmte.executeUpdate();
                                    System.out.println(tTime);
                                }
                                else{
                                    dStmte.setInt(1, dupRs.getInt("ID"));
                                    dStmte.executeUpdate();
                                }
                                rs = st.executeQuery("SELECT * FROM eventscheduler;");
                                updateTable();
                                break;
                            }
                            else if(tTime.isBefore(tTim)){
                                JOptionPane.showMessageDialog(null, "You Missed a Reminder\n"+dupRs.getString("Description")+"\n at "+dupRs.getTimestamp("DateTime"), dupRs.getString("Title"), 1);
                                dStmte.setInt(1, dupRs.getInt("ID"));
                                dStmte.executeUpdate();
                                rs = st.executeQuery("SELECT * FROM eventscheduler;");
                                break;
                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(EventScheduler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
        });
        reminder.start();
        
        try {
            while(rs.next()){
                LocalDateTime tTime = rs.getTimestamp("DateTime").toLocalDateTime(), tTim = LocalDateTime.now();
                if(tTime.toLocalDate().isEqual(tTim.toLocalDate()) || tTime.toLocalDate().isEqual(tTim.toLocalDate().plusDays(1))){
                    showEvent = new JDialog();
                    showEvent.setModal(true);
                    if(tTime.toLocalDate().isEqual(tTim.toLocalDate())) showEvent.setTitle("Today Event:");
                    else    showEvent.setTitle("Tommorow Event:");
                    JPanel p1 = new JPanel();
                    p1.setLayout(null);
                    p1.setPreferredSize(new Dimension(430, 160));
                    String tDate = "", tTimeS = "", tTitle = "", tDesc = "";
                    tDate = String.format("%04d", tTime.getYear()) + "-" + String.format("%02d", tTime.getMonthValue()) + "-" + String.format("%02d", tTime.getDayOfMonth());
                    tTimeS = String.format("%02d", tTime.getHour())+":"+String.format("%02d", tTime.getMinute());
                    tTitle = rs.getString("Title");
                    tDesc = rs.getString("Description");
                    JLabel dateLbl = new JLabel(" Date: "+ tDate);
                    dateLbl.setBounds(10,10, 100, 30);
                    dateLbl.setBackground(Color.WHITE);
                    dateLbl.setForeground(Color.BLACK);
                    dateLbl.setOpaque(true);
                    dateLbl.setHorizontalAlignment(SwingConstants.LEFT);
                    p1.add(dateLbl);

                    JLabel timeLbl = new JLabel(" Time: "+ tTimeS);
                    timeLbl.setBounds(320,10, 100, 30);
                    timeLbl.setBackground(Color.WHITE);
                    timeLbl.setForeground(Color.BLACK);
                    timeLbl.setOpaque(true);
                    timeLbl.setHorizontalAlignment(SwingConstants.LEFT);
                    p1.add(timeLbl);

                    JLabel eventLbl = new JLabel(" Event Title: "+ tTitle);
                    eventLbl.setBounds(10,50, 200, 30);
                    eventLbl.setBackground(Color.WHITE);
                    eventLbl.setForeground(Color.BLACK);
                    eventLbl.setOpaque(true);
                    eventLbl.setHorizontalAlignment(SwingConstants.LEFT);
                    p1.add(eventLbl);

                    JTextArea eventDesc = new JTextArea();
                    eventDesc.setText(" Description: "+tDesc);
                    eventDesc.setBounds(10, 90, 410, 60);
                    eventDesc.setEditable(false);
                    p1.add(eventDesc);

                    showEvent.add(p1, BorderLayout.CENTER);
                    showEvent.pack();
                    showEvent.setLocationRelativeTo(jf);
                    showEvent.setResizable(false);
                    showEvent.setVisible(true);
                }
            }
            while(rs.previous());
        } catch (SQLException ex) {
            Logger.getLogger(EventScheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void main(String[] args) {
        // TODO code application logic here
        new EventScheduler();
    }
}
