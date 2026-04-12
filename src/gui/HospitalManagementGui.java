package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import entity.Appointment;
import entity.Doctor;
import entity.DoctorSlot;
import entity.Patient;
import notification.SmsNotifier;
import service.HospitalManagementService;
import service.HospitalManagementServiceImpl;

public class HospitalManagementGui extends JFrame {
	private static final long serialVersionUID = 1L;

	private final HospitalManagementService service;
	private final SmsNotifier smsNotifier;
	private final AdminPanel adminPanel;
	private final PatientPanel patientPanel;

	public HospitalManagementGui() {
		this.service = new HospitalManagementServiceImpl();
		this.smsNotifier = new SmsNotifier();
		this.service.initializeDatabase();
		this.adminPanel = new AdminPanel();
		this.patientPanel = new PatientPanel();

		setTitle("Hospital Management System");
		setSize(1050, 650);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Administration", adminPanel);
		tabs.addTab("Patient", patientPanel);
		add(tabs);

		refreshAll();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				new HospitalManagementGui().setVisible(true);
			} catch (RuntimeException e) {
				if (GraphicsEnvironment.isHeadless()) {
					System.err.println(errorMessage(e));
				} else {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, errorMessage(e), "Startup failed", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	private void refreshAll() {
		adminPanel.refresh();
		patientPanel.refresh();
	}

	private void showError(Exception e) {
		e.printStackTrace();
		JOptionPane.showMessageDialog(this, errorMessage(e), "Error", JOptionPane.ERROR_MESSAGE);
	}

	private static String errorMessage(Throwable throwable) {
		if (throwable instanceof java.awt.HeadlessException) {
			return "A graphical display is required to start the Hospital Management System.";
		}
		StringBuilder message = new StringBuilder();
		Throwable cursor = throwable;
		while (cursor != null) {
			String text = cursor.getMessage();
			if (text != null && !text.trim().isEmpty()) {
				if (message.length() > 0) {
					message.append(System.lineSeparator()).append("Cause: ");
				}
				message.append(text.trim());
			}
			cursor = cursor.getCause();
		}
		return message.length() == 0 ? "Unexpected error." : message.toString();
	}

	private java.util.Date parseDate(String value) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			format.setLenient(false);
			return format.parse(value);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Use date format YYYY-MM-DD.");
		}
	}

	private int parseInt(String value, String fieldName) {
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(fieldName + " must be a number.");
		}
	}

	private String todayText() {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}

	private class AdminPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private final JTextField doctorName = new JTextField();
		private final JTextField specialization = new JTextField();
		private final JTextField doctorContact = new JTextField();
		private final JTable doctorTable = new JTable();
		private final JTable slotTable = new JTable();
		private final JTable appointmentTable = new JTable();
		private int selectedDoctorId = 0;

		AdminPanel() {
			setLayout(new BorderLayout(8, 8));

			JPanel top = new JPanel(new GridLayout(1, 1, 8, 8));
			top.add(buildDoctorForm());

			JTabbedPane tables = new JTabbedPane();
			tables.addTab("Doctors", new JScrollPane(doctorTable));
			tables.addTab("Slots", new JScrollPane(slotTable));
			tables.addTab("Appointments", buildAppointmentsPanel());

			add(top, BorderLayout.NORTH);
			add(tables, BorderLayout.CENTER);
			doctorTable.getSelectionModel().addListSelectionListener(event -> {
				if (!event.getValueIsAdjusting() && doctorTable.getSelectedRow() >= 0) {
					int row = doctorTable.getSelectedRow();
					selectedDoctorId = (int) doctorTable.getValueAt(row, 1);
					doctorName.setText(String.valueOf(doctorTable.getValueAt(row, 2)));
					specialization.setText(String.valueOf(doctorTable.getValueAt(row, 3)));
					doctorContact.setText(String.valueOf(doctorTable.getValueAt(row, 4)));
				}
			});
		}

		private JPanel buildDoctorForm() {
			JPanel panel = new JPanel(new BorderLayout());
			JPanel fields = new JPanel(new GridLayout(3, 2, 6, 6));
			fields.add(new JLabel("Doctor name"));
			fields.add(doctorName);
			fields.add(new JLabel("Specialization"));
			fields.add(specialization);
			fields.add(new JLabel("Contact number"));
			fields.add(doctorContact);

			JButton add = new JButton("Add Doctor");
			add.addActionListener(event -> addDoctor());
			JButton update = new JButton("Update Doctor");
			update.addActionListener(event -> updateDoctor());
			JButton remove = new JButton("Remove Doctor");
			remove.addActionListener(event -> removeDoctor());

			JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
			buttons.add(add);
			buttons.add(update);
			buttons.add(remove);

			panel.add(new JLabel("Doctor Management"), BorderLayout.NORTH);
			panel.add(fields, BorderLayout.CENTER);
			panel.add(buttons, BorderLayout.SOUTH);
			return panel;
		}

		private JPanel buildAppointmentsPanel() {
			JPanel panel = new JPanel(new BorderLayout());
			JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JButton approve = new JButton("Approve Selected");
			approve.addActionListener(event -> decideAppointment("approve"));
			JButton reject = new JButton("Reject Selected");
			reject.addActionListener(event -> decideAppointment("reject"));
			JButton cancel = new JButton("Cancel Selected");
			cancel.addActionListener(event -> decideAppointment("cancel"));
			buttons.add(approve);
			buttons.add(reject);
			buttons.add(cancel);
			panel.add(buttons, BorderLayout.NORTH);
			panel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);
			setupTable(appointmentTable);
			return panel;
		}

		private void addDoctor() {
			try {
				service.addDoctor(new Doctor(0, doctorName.getText(), specialization.getText(), doctorContact.getText()));
				clearDoctorFields();
				refreshAll();
			} catch (RuntimeException e) {
				showError(e);
			}
		}

		private void updateDoctor() {
			try {
				if (selectedDoctorId == 0) {
					throw new IllegalArgumentException("Select a doctor from the table first.");
				}
				service.updateDoctor(new Doctor(selectedDoctorId, doctorName.getText(), specialization.getText(), doctorContact.getText()));
				clearDoctorFields();
				refreshAll();
			} catch (RuntimeException e) {
				showError(e);
			}
		}

		private void removeDoctor() {
			try {
				if (selectedDoctorId == 0) {
					throw new IllegalArgumentException("Select a doctor from the table first.");
				}
				service.removeDoctor(selectedDoctorId);
				clearDoctorFields();
				refreshAll();
			} catch (RuntimeException e) {
				showError(e);
			}
		}

		private void decideAppointment(String action) {
			try {
				int row = checkedOrSelectedRow(appointmentTable);
				if (row < 0) {
					throw new IllegalArgumentException("Select an appointment first.");
				}
				int appointmentId = (int) appointmentTable.getValueAt(row, 1);
				String patientName = String.valueOf(appointmentTable.getValueAt(row, 3));
				String patientPhone = String.valueOf(appointmentTable.getValueAt(row, 4));
				String doctorName = String.valueOf(appointmentTable.getValueAt(row, 6));
				String date = String.valueOf(appointmentTable.getValueAt(row, 8));
				String time = String.valueOf(appointmentTable.getValueAt(row, 9));
				boolean changed;
				if ("approve".equals(action)) {
					changed = service.approveAppointment(appointmentId);
				} else if ("reject".equals(action)) {
					changed = service.rejectAppointment(appointmentId);
				} else {
					changed = service.cancelAppointment(appointmentId);
				}
				if (!changed) {
					throw new IllegalArgumentException("Appointment was not updated.");
				}
				if ("approve".equals(action)) {
					sendPatientDecisionSms(patientName, patientPhone, doctorName, date, time, "accepted");
				} else if ("reject".equals(action)) {
					sendPatientDecisionSms(patientName, patientPhone, doctorName, date, time, "rejected");
				}
				refreshAll();
			} catch (RuntimeException e) {
				showError(e);
			}
		}

		private void sendPatientDecisionSms(String patientName, String patientPhone, String doctorName, String date, String time, String decision) {
			smsNotifier.sendAppointmentDecision(patientName, patientPhone, doctorName, date, time, decision);
		}

		void refresh() {
			loadDoctors();
			loadSlots();
			loadAppointments();
		}

		private void loadDoctors() {
			DefaultTableModel model = new CheckBoxTableModel(new Object[] {"Select", "ID", "Name", "Specialization", "Contact"}, 0);
			for (Doctor doctor : service.getDoctors()) {
				model.addRow(new Object[] {Boolean.FALSE, doctor.getDoctorId(), doctor.getFullName(), doctor.getSpecialization(), doctor.getContactNumber()});
			}
			doctorTable.setModel(model);
			setupTable(doctorTable);
		}

		private void loadSlots() {
			DefaultTableModel model = new ReadOnlyTableModel(new Object[] {"No.", "Slot ID", "Doctor ID", "Date", "Start", "End", "Status"}, 0);
			for (DoctorSlot slot : service.getSlots()) {
				model.addRow(new Object[] {model.getRowCount() + 1, slot.getSlotId(), slot.getDoctorId(), slot.getSlotDate(), slot.getStartTime(), slot.getEndTime(), slot.getStatus()});
			}
			slotTable.setModel(model);
			setupTable(slotTable);
		}

		private void loadAppointments() {
			DefaultTableModel model = new CheckBoxTableModel(new Object[] {"Select", "ID", "Patient ID", "Patient Name", "Patient Phone", "Doctor ID", "Doctor Name", "Slot ID", "Date", "Time", "Status", "Description"}, 0);
			for (Appointment appointment : service.getAppointments()) {
				model.addRow(new Object[] {Boolean.FALSE, appointment.getAppointmentId(), appointment.getPatientId(), appointment.getPatientName(), appointment.getPatientContact(),
						appointment.getDoctorId(), appointment.getDoctorName(), appointment.getSlotId(),
						appointment.getAppointmentDate(), appointment.getSlotTime(), appointment.getStatus(), appointment.getDescription()});
			}
			appointmentTable.setModel(model);
			setupTable(appointmentTable);
			appointmentTable.getColumnModel().getColumn(10).setCellRenderer(new StatusCellRenderer());
		}

		private void clearDoctorFields() {
			selectedDoctorId = 0;
			doctorName.setText("");
			specialization.setText("");
			doctorContact.setText("");
			doctorTable.clearSelection();
		}
	}

	private class PatientPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private final JTextField patientName = new PlaceholderTextField("Patient full name");
		private final JTextField age = new PlaceholderTextField("Years");
		private final JTextField patientPhone = new PlaceholderTextField("Phone number");
		private final JComboBox<String> gender = new JComboBox<>(new String[] {"Male", "Female", "Other"});
		private final JTextField appointmentDate = new PlaceholderTextField("YYYY-MM-DD");
		private final JTextField description = new PlaceholderTextField("Symptoms or reason for visit");
		private final JComboBox<DoctorItem> doctors = new JComboBox<>();
		private final JComboBox<SlotItem> availableSlots = new JComboBox<>();
		private final JTable patientTable = new JTable();
		private final JTable patientAppointmentTable = new JTable();
		private int currentPatientId = 0;

		PatientPanel() {
			setLayout(new BorderLayout(8, 8));
			add(buildPatientForm(), BorderLayout.NORTH);
			setupTable(patientTable);
			setupTable(patientAppointmentTable);
			JSplitPane tables = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buildPatientsPanel(), buildPatientAppointmentsPanel());
			tables.setResizeWeight(0.42);
			tables.setBorder(BorderFactory.createEmptyBorder());
			add(tables, BorderLayout.CENTER);
			patientTable.getSelectionModel().addListSelectionListener(event -> {
				if (!event.getValueIsAdjusting() && patientTable.getSelectedRow() >= 0) {
					int row = patientTable.getSelectedRow();
					currentPatientId = (int) patientTable.getValueAt(row, 1);
					loadPatientAppointments();
				}
			});
		}

		private JPanel buildPatientForm() {
			JPanel panel = new JPanel(new BorderLayout());
			panel.setBorder(BorderFactory.createTitledBorder("Patient Registration and Booking"));
			configurePatientInputs();

			JPanel fields = new JPanel(new GridLayout(1, 2, 12, 0));
			JPanel registration = new JPanel(new GridBagLayout());
			registration.setBorder(BorderFactory.createTitledBorder("Patient details"));
			addFormRow(registration, 0, "Full name", patientName, "Enter patient full name");
			addFormRow(registration, 1, "Age", age, "Enter age in years");
			addFormRow(registration, 2, "Phone number", patientPhone, "Enter patient phone number");
			addFormRow(registration, 3, "Gender", gender, "Select gender");

			JPanel booking = new JPanel(new GridBagLayout());
			booking.setBorder(BorderFactory.createTitledBorder("Appointment request"));
			addFormRow(booking, 0, "Doctor", doctors, "Select doctor");
			addFormRow(booking, 1, "Date", appointmentDate, "YYYY-MM-DD");
			addFormRow(booking, 2, "Slot", availableSlots, "Select available slot");
			addFormRow(booking, 3, "Problem", description, "Describe the problem");

			fields.add(registration);
			fields.add(booking);
			appointmentDate.setText(todayText());
			doctors.addActionListener(event -> loadSlotsForSelection());
			appointmentDate.addActionListener(event -> loadSlotsForSelection());

			JButton register = new JButton("Register Patient");
			register.addActionListener(event -> registerPatient());
			JButton request = new JButton("Request Appointment");
			request.addActionListener(event -> requestAppointment());
			JButton loadMine = new JButton("Load My Appointments");
			loadMine.addActionListener(event -> loadPatientAppointments());
			JButton cancelMine = new JButton("Cancel Selected Appointment");
			cancelMine.addActionListener(event -> cancelSelectedAppointment());
			JButton removePatient = new JButton("Remove Selected Patient");
			removePatient.addActionListener(event -> removeSelectedPatient());
			JButton refresh = new JButton("Refresh Slots");
			refresh.addActionListener(event -> refresh());

			JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
			buttons.add(register);
			buttons.add(request);
			buttons.add(loadMine);
			buttons.add(cancelMine);
			buttons.add(removePatient);
			buttons.add(refresh);

			panel.add(fields, BorderLayout.CENTER);
			panel.add(buttons, BorderLayout.SOUTH);
			return panel;
		}

		private void configurePatientInputs() {
			patientName.setColumns(22);
			age.setColumns(8);
			patientPhone.setColumns(14);
			appointmentDate.setColumns(12);
			description.setColumns(28);
			doctors.setPreferredSize(new Dimension(260, doctors.getPreferredSize().height));
			availableSlots.setPreferredSize(new Dimension(360, availableSlots.getPreferredSize().height));
		}

		private void addFormRow(JPanel panel, int row, String labelText, Component field, String helperText) {
			GridBagConstraints labelConstraints = new GridBagConstraints();
			labelConstraints.gridx = 0;
			labelConstraints.gridy = row;
			labelConstraints.anchor = GridBagConstraints.WEST;
			labelConstraints.insets = new Insets(4, 6, 4, 8);
			panel.add(new JLabel(labelText), labelConstraints);

			GridBagConstraints fieldConstraints = new GridBagConstraints();
			fieldConstraints.gridx = 1;
			fieldConstraints.gridy = row;
			fieldConstraints.weightx = 1.0;
			fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
			fieldConstraints.insets = new Insets(4, 0, 4, 6);
			field.setFont(field.getFont().deriveFont(Font.PLAIN));
			field.setForeground(Color.BLACK);
			field.setName(helperText);
			if (field instanceof javax.swing.JComponent) {
				((javax.swing.JComponent) field).setToolTipText(helperText);
			}
			panel.add(field, fieldConstraints);
		}

		private JPanel buildPatientsPanel() {
			JPanel panel = new JPanel(new BorderLayout());
			panel.setBorder(BorderFactory.createTitledBorder("Registered Patients"));
			panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);
			return panel;
		}

		private JPanel buildPatientAppointmentsPanel() {
			JPanel panel = new JPanel(new BorderLayout());
			panel.setBorder(BorderFactory.createTitledBorder("Patient Appointments"));
			panel.add(new JScrollPane(patientAppointmentTable), BorderLayout.CENTER);
			return panel;
		}

		private void registerPatient() {
			try {
				char patientGender = selectedGender();
				Patient patient = new Patient(0, patientName.getText(), parseInt(age.getText(), "Age"), patientGender, patientPhone.getText());
				currentPatientId = service.registerPatient(patient);
				clearPatientFields();
				JOptionPane.showMessageDialog(this, "Patient registered with ID " + currentPatientId + ".");
				refreshAll();
			} catch (RuntimeException e) {
				showError(e);
			}
		}

		private java.util.Date parseOptionalDate(String value) {
			if (value == null || value.trim().isEmpty()) {
				return null;
			}
			return parseDate(value.trim());
		}

		private char selectedGender() {
			String selected = String.valueOf(gender.getSelectedItem());
			if ("Male".equals(selected)) {
				return 'M';
			}
			if ("Female".equals(selected)) {
				return 'F';
			}
			return 'O';
		}

		private void requestAppointment() {
			try {
				int patientRow = checkedOrSelectedRow(patientTable);
				if (patientRow < 0) {
					throw new IllegalArgumentException("Select a registered patient from the table first.");
				}
				currentPatientId = (int) patientTable.getValueAt(patientRow, 1);
				SlotItem item = (SlotItem) availableSlots.getSelectedItem();
				if (item == null) {
					throw new IllegalArgumentException("No available slot selected.");
				}
				int id = service.requestAppointment(currentPatientId, item.slot.getSlotId(), description.getText());
				JOptionPane.showMessageDialog(this, "Appointment request created with ID " + id + ". Administration must approve it.");
				description.setText("");
				refreshAll();
				loadPatientAppointments();
			} catch (RuntimeException e) {
				showError(e);
			}
		}

		void refresh() {
			loadDoctorsDropdown();
			loadSlotsForSelection();
			loadPatientsTable();
			if (currentPatientId != 0) {
				loadPatientAppointments();
			} else {
				patientAppointmentTable.setModel(new CheckBoxTableModel(new Object[] {"Select", "ID", "Doctor Name", "Slot ID", "Date", "Time", "Status", "Description"}, 0));
			}
		}

		private void loadPatientsTable() {
			DefaultTableModel model = new CheckBoxTableModel(new Object[] {"Select", "Patient ID", "Name", "Phone", "Age", "Gender"}, 0);
			int selectedRow = -1;
			for (Patient patient : service.getPatients()) {
				model.addRow(new Object[] {Boolean.FALSE, patient.getPatientId(), patient.getFullName(), patient.getContactNumber(), patient.getAge(), displayGender(patient.getGender())});
				if (patient.getPatientId() == currentPatientId) {
					selectedRow = model.getRowCount() - 1;
				}
			}
			patientTable.setModel(model);
			setupTable(patientTable);
			if (selectedRow >= 0) {
				patientTable.setRowSelectionInterval(selectedRow, selectedRow);
			}
		}

		private void loadDoctorsDropdown() {
			DoctorItem selected = (DoctorItem) doctors.getSelectedItem();
			int selectedId = selected == null ? 0 : selected.doctor.getDoctorId();
			doctors.removeAllItems();
			for (Doctor doctor : service.getDoctors()) {
				doctors.addItem(new DoctorItem(doctor));
			}
			for (int i = 0; i < doctors.getItemCount(); i++) {
				if (doctors.getItemAt(i).doctor.getDoctorId() == selectedId) {
					doctors.setSelectedIndex(i);
					break;
				}
			}
		}

		private void loadSlotsForSelection() {
			try {
				availableSlots.removeAllItems();
				DoctorItem doctor = (DoctorItem) doctors.getSelectedItem();
				if (doctor == null) {
					return;
				}
				List<DoctorSlot> slots = service.getAvailableSlotsForDoctorOnDate(doctor.doctor.getDoctorId(), parseDate(appointmentDate.getText()));
				for (DoctorSlot slot : slots) {
					availableSlots.addItem(new SlotItem(slot, doctor.doctor.getFullName()));
				}
			} catch (RuntimeException e) {
				showError(e);
			}
		}

		private void loadPatientAppointments() {
			try {
				if (currentPatientId == 0) {
					throw new IllegalArgumentException("Select a registered patient first.");
				}
				DefaultTableModel model = new CheckBoxTableModel(new Object[] {"Select", "ID", "Doctor Name", "Slot ID", "Date", "Time", "Status", "Description"}, 0);
				for (Appointment appointment : service.getAppointmentsForPatient(currentPatientId)) {
					model.addRow(new Object[] {Boolean.FALSE, appointment.getAppointmentId(), appointment.getDoctorName(), appointment.getSlotId(),
							appointment.getAppointmentDate(), appointment.getSlotTime(), appointment.getStatus(), appointment.getDescription()});
				}
				patientAppointmentTable.setModel(model);
				patientAppointmentTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());
			} catch (RuntimeException e) {
				showError(e);
			}
		}

		private void cancelSelectedAppointment() {
			try {
				int row = checkedOrSelectedRow(patientAppointmentTable);
				if (row < 0) {
					throw new IllegalArgumentException("Select an appointment first.");
				}
				int appointmentId = (int) patientAppointmentTable.getValueAt(row, 1);
				service.cancelAppointment(appointmentId);
				refreshAll();
				loadPatientAppointments();
			} catch (RuntimeException e) {
				showError(e);
			}
		}

		private void removeSelectedPatient() {
			try {
				int row = checkedOrSelectedRow(patientTable);
				if (row < 0) {
					throw new IllegalArgumentException("Select a patient first.");
				}
				int patientId = (int) patientTable.getValueAt(row, 1);
				if (!service.removePatient(patientId)) {
					throw new IllegalArgumentException("Patient was not removed.");
				}
				if (currentPatientId == patientId) {
					currentPatientId = 0;
				}
				clearPatientFields();
				refreshAll();
			} catch (RuntimeException e) {
				showError(e);
			}
		}

		private String displayGender(char value) {
			if (value == 'M') {
				return "Male";
			}
			if (value == 'F') {
				return "Female";
			}
			if (value == 'O') {
				return "Other";
			}
			return "";
		}

		private void clearPatientFields() {
			patientName.setText("");
			age.setText("");
			patientPhone.setText("");
			gender.setSelectedIndex(0);
			appointmentDate.setText(todayText());
			description.setText("");
		}
	}

	private void setupTable(JTable table) {
		table.setRowHeight(26);
		table.setFillsViewportHeight(true);
		table.setSelectionBackground(new Color(220, 235, 252));
		table.setSelectionForeground(Color.BLACK);
		table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));
		if (table.getColumnCount() > 0 && "Select".equals(table.getColumnName(0))) {
			table.getColumnModel().getColumn(0).setPreferredWidth(60);
			table.getColumnModel().getColumn(0).setMaxWidth(70);
		}
	}

	private int checkedOrSelectedRow(JTable table) {
		if (table.isEditing()) {
			table.getCellEditor().stopCellEditing();
		}
		for (int row = 0; row < table.getRowCount(); row++) {
			if (table.getColumnCount() > 0 && Boolean.TRUE.equals(table.getValueAt(row, 0))) {
				return row;
			}
		}
		return table.getSelectedRow();
	}

	private static class ReadOnlyTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;

		ReadOnlyTableModel(Object[] columns, int rowCount) {
			super(columns, rowCount);
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}

	private static class CheckBoxTableModel extends ReadOnlyTableModel {
		private static final long serialVersionUID = 1L;

		CheckBoxTableModel(Object[] columns, int rowCount) {
			super(columns, rowCount);
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return column == 0;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return columnIndex == 0 ? Boolean.class : Object.class;
		}
	}

	private static class PlaceholderTextField extends JTextField {
		private static final long serialVersionUID = 1L;

		private final String placeholder;

		PlaceholderTextField(String placeholder) {
			this.placeholder = placeholder;
		}

		@Override
		protected void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);
			if (!getText().isEmpty() || isFocusOwner()) {
				return;
			}
			graphics.setColor(new Color(130, 130, 130));
			graphics.setFont(getFont());
			int textY = (getHeight() - graphics.getFontMetrics().getHeight()) / 2
					+ graphics.getFontMetrics().getAscent();
			graphics.drawString(placeholder, getInsets().left + 2, textY);
		}
	}

	private static class StatusCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (isSelected) {
				return component;
			}
			String status = value == null ? "" : value.toString();
			component.setForeground(colorForStatus(status));
			component.setFont(component.getFont().deriveFont(Font.BOLD));
			return component;
		}

		private Color colorForStatus(String status) {
			if ("APPROVED".equalsIgnoreCase(status)) {
				return new Color(28, 128, 76);
			}
			if ("REJECTED".equalsIgnoreCase(status)) {
				return new Color(190, 45, 45);
			}
			if ("PENDING".equalsIgnoreCase(status)) {
				return new Color(31, 102, 185);
			}
			return Color.DARK_GRAY;
		}
	}

	private static class DoctorItem {
		private final Doctor doctor;

		DoctorItem(Doctor doctor) {
			this.doctor = doctor;
		}

		@Override
		public String toString() {
			return doctor.getFullName() + " (" + doctor.getSpecialization() + ")";
		}
	}

	private static class SlotItem {
		private final DoctorSlot slot;
		private final String doctorName;

		SlotItem(DoctorSlot slot, String doctorName) {
			this.slot = slot;
			this.doctorName = doctorName;
		}

		@Override
		public String toString() {
			return "Slot " + slot.getSlotId() + " | " + doctorName + " | " + slot.getSlotDate()
					+ " " + slot.getStartTime() + "-" + slot.getEndTime();
		}
	}
}
