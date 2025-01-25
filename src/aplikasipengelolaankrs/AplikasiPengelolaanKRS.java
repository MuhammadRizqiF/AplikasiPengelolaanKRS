import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AplikasiPengelolaanKRS {

    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNim, txtNama, txtKodeMataKuliah, txtNamaMataKuliah;

    private Connection connection;

    public AplikasiPengelolaanKRS() {
        initialize();
        connectToDatabase();
        loadData();
    }

    private void initialize() {
        frame = new JFrame("Aplikasi Pengelolaan KRS");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Table
        String[] columnNames = {"NIM", "Nama", "Kode Mata Kuliah", "Nama Mata Kuliah"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2));

        inputPanel.add(new JLabel("NIM:"));
        txtNim = new JTextField();
        inputPanel.add(txtNim);

        inputPanel.add(new JLabel("Nama:"));
        txtNama = new JTextField();
        inputPanel.add(txtNama);

        inputPanel.add(new JLabel("Kode Mata Kuliah:"));
        txtKodeMataKuliah = new JTextField();
        inputPanel.add(txtKodeMataKuliah);

        inputPanel.add(new JLabel("Nama Mata Kuliah:"));
        txtNamaMataKuliah = new JTextField();
        inputPanel.add(txtNamaMataKuliah);

        JButton btnAdd = new JButton("Tambah");
        btnAdd.addActionListener(e -> addData());
        inputPanel.add(btnAdd);

        JButton btnDelete = new JButton("Hapus");
        btnDelete.addActionListener(e -> deleteData());
        inputPanel.add(btnDelete);

        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/krs_db", "root", "");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Gagal koneksi ke database: " + e.getMessage());
            System.exit(1);
        }
    }

    private void loadData() {
        try {
            tableModel.setRowCount(0); // Clear existing data
            String query = "SELECT * FROM krs";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String nim = rs.getString("nim");
                String nama = rs.getString("nama");
                String kodeMataKuliah = rs.getString("kode_mata_kuliah");
                String namaMataKuliah = rs.getString("nama_mata_kuliah");
                tableModel.addRow(new Object[]{nim, nama, kodeMataKuliah, namaMataKuliah});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Gagal memuat data: " + e.getMessage());
        }
    }

    private void addData() {
        try {
            String query = "INSERT INTO krs (nim, nama, kode_mata_kuliah, nama_mata_kuliah) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, txtNim.getText());
            pstmt.setString(2, txtNama.getText());
            pstmt.setString(3, txtKodeMataKuliah.getText());
            pstmt.setString(4, txtNamaMataKuliah.getText());

            pstmt.executeUpdate();
            loadData();
            JOptionPane.showMessageDialog(frame, "Data berhasil ditambahkan.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Gagal menambah data: " + e.getMessage());
        }
    }

    private void deleteData() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Pilih baris yang akan dihapus.");
            return;
        }

        String nim = tableModel.getValueAt(selectedRow, 0).toString();
        try {
            String query = "DELETE FROM krs WHERE nim = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, nim);

            pstmt.executeUpdate();
            loadData();
            JOptionPane.showMessageDialog(frame, "Data berhasil dihapus.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Gagal menghapus data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AplikasiPengelolanKRS::new);
    }
}
