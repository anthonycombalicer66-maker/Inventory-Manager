import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.*;

public class InventoryApp extends JFrame {

    private static final String DB_HOST = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "inventory_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private final JTextField txtId = new JTextField();
    private final JTextField txtName = new JTextField();
    private final JComboBox<String> cmbCategory =
            new JComboBox<>(new String[]{"Business", "Office", "Home", "Electronics", "Other"});
    private final JTextField txtQuantity = new JTextField();
    private final JTextField txtPrice = new JTextField();

    private final JButton btnSubmit = new JButton("Submit");
    private final JButton btnUpdate = new JButton("Update");
    private final JButton btnDelete = new JButton("Delete");
    private final JButton btnClear = new JButton("Clear");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"id", "product_name", "category", "quantity", "price"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public InventoryApp() {
        setTitle("Mini Inventory Program");
        setSize(1030, 675);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        buildUI();
        addListeners();

        try {
            initDatabase();
            loadTable();
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage()
                    + "\n\nMake sure MySQL is running and the connector jar is on the classpath.");
        }
    }

    private void buildUI() {
        JLabel title = new JLabel("Mini Inventory Program", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setBounds(0, 30, 1020, 40);
        add(title);

        addLabel("Product ID", 30, 110);
        txtId.setBounds(30, 135, 430, 35);
        txtId.setEditable(false);
        add(txtId);

        addLabel("Product Name", 30, 180);
        txtName.setBounds(30, 205, 430, 35);
        add(txtName);

        addLabel("Category", 30, 250);
        cmbCategory.setBounds(30, 275, 430, 35);
        add(cmbCategory);

        addLabel("Quantity", 30, 325);
        txtQuantity.setBounds(30, 350, 430, 35);
        add(txtQuantity);

        addLabel("Price", 30, 395);
        txtPrice.setBounds(30, 420, 430, 40);
        add(txtPrice);

        btnSubmit.setBounds(40, 495, 410, 35);
        add(btnSubmit);

        btnUpdate.setBounds(40, 560, 120, 30);
        btnDelete.setBounds(185, 560, 120, 30);
        btnClear.setBounds(330, 560, 120, 30);
        add(btnUpdate);
        add(btnDelete);
        add(btnClear);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(18);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(500, 120, 500, 495);
        add(scroll);
    }

    private void addLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 200, 20);
        add(lbl);
    }

    private void addListeners() {
        txtQuantity.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });

        txtPrice.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                boolean control = c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE;
                boolean dotOk = c == '.' && !txtPrice.getText().contains(".");
                if (!Character.isDigit(c) && !dotOk && !control) {
                    e.consume();
                }
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int r = table.getSelectedRow();
                txtId.setText(String.valueOf(tableModel.getValueAt(r, 0)));
                txtName.setText(String.valueOf(tableModel.getValueAt(r, 1)));
                cmbCategory.setSelectedItem(String.valueOf(tableModel.getValueAt(r, 2)));
                txtQuantity.setText(String.valueOf(tableModel.getValueAt(r, 3)));
                txtPrice.setText(String.valueOf(tableModel.getValueAt(r, 4)));
            }
        });

        btnSubmit.addActionListener(e -> createProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClear.addActionListener(e -> clearFields());
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_HOST + DB_NAME, DB_USER, DB_PASS);
    }

    private void initDatabase() throws SQLException {
        try (Connection c = DriverManager.getConnection(DB_HOST, DB_USER, DB_PASS);
             Statement st = c.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
        }
        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS products ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "product_name VARCHAR(100) NOT NULL,"
                    + "category VARCHAR(50) NOT NULL,"
                    + "quantity INT NOT NULL,"
                    + "price DECIMAL(10,2) NOT NULL)");
        }
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        String sql = "SELECT id, product_name, category, quantity, price FROM products ORDER BY id";
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getBigDecimal("price")
                });
            }
        } catch (SQLException e) {
            showError("Could not load data: " + e.getMessage());
        }
    }

    private void createProduct() {
        if (!validateInputs()) return;
        String sql = "INSERT INTO products (product_name, category, quantity, price) VALUES (?,?,?,?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, txtName.getText().trim());
            ps.setString(2, (String) cmbCategory.getSelectedItem());
            ps.setInt(3, Integer.parseInt(txtQuantity.getText().trim()));
            ps.setBigDecimal(4, new BigDecimal(txtPrice.getText().trim()));
            ps.executeUpdate();
            loadTable();
            clearFields();
            JOptionPane.showMessageDialog(this, "Product added successfully.");
        } catch (SQLException e) {
            showError("Insert failed: " + e.getMessage());
        }
    }

    private void updateProduct() {
        if (txtId.getText().isEmpty()) {
            showError("Select a row in the table to update.");
            return;
        }
        if (!validateInputs()) return;
        String sql = "UPDATE products SET product_name=?, category=?, quantity=?, price=? WHERE id=?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, txtName.getText().trim());
            ps.setString(2, (String) cmbCategory.getSelectedItem());
            ps.setInt(3, Integer.parseInt(txtQuantity.getText().trim()));
            ps.setBigDecimal(4, new BigDecimal(txtPrice.getText().trim()));
            ps.setInt(5, Integer.parseInt(txtId.getText()));
            ps.executeUpdate();
            loadTable();
            clearFields();
            JOptionPane.showMessageDialog(this, "Product updated successfully.");
        } catch (SQLException e) {
            showError("Update failed: " + e.getMessage());
        }
    }

    private void deleteProduct() {
        if (txtId.getText().isEmpty()) {
            showError("Select a row in the table to delete.");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete the selected product?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM products WHERE id=?")) {
            ps.setInt(1, Integer.parseInt(txtId.getText()));
            ps.executeUpdate();
            loadTable();
            clearFields();
        } catch (SQLException e) {
            showError("Delete failed: " + e.getMessage());
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        cmbCategory.setSelectedIndex(0);
        txtQuantity.setText("");
        txtPrice.setText("");
        table.clearSelection();
        txtName.requestFocus();
    }

    private boolean validateInputs() {
        if (txtName.getText().trim().isEmpty()) {
            showError("Product name is required.");
            return false;
        }
        try {
            Integer.parseInt(txtQuantity.getText().trim());
        } catch (NumberFormatException ex) {
            showError("Quantity must be a whole number.");
            return false;
        }
        try {
            new BigDecimal(txtPrice.getText().trim());
        } catch (NumberFormatException ex) {
            showError("Price must be a valid decimal number.");
            return false;
        }
        return true;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventoryApp().setVisible(true));
    }
}
