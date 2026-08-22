import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

public class CurrencyConverterGUI extends JFrame {
    private CurrencyService currencyService;
    private JComboBox<String> fromCurrencyCombo;
    private JComboBox<String> toCurrencyCombo;
    private JTextField amountField;
    private JTextField resultField;
    private JLabel statusLabel;
    private JLabel rateLabel;
    private JLabel rateDisplayLabel;
    private JButton convertButton;
    private JButton swapButton;
    private JButton refreshButton;
    
    // Purple to Orange gradient colors
    private static final Color GRADIENT_TOP = new Color(128, 0, 255);
    private static final Color GRADIENT_BOTTOM = new Color(255, 165, 0);
    
    public CurrencyConverterGUI() {
        currencyService = new CurrencyService();
        initializeGUI();
        loadDefaultRates();
    }
    
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            GradientPaint gradient = new GradientPaint(
                0, 0, GRADIENT_TOP,
                getWidth(), getHeight(), GRADIENT_BOTTOM
            );
            
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    
    private void initializeGUI() {
        setTitle("Currency Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(850, 600));
        
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("Currency Converter", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        
        JLabel subtitleLabel = new JLabel("Real-time Exchange Rates", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(255, 240, 220));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Amount and Result side by side panel
        JPanel amountResultPanel = new JPanel(new GridBagLayout());
        amountResultPanel.setOpaque(false);
        amountResultPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 120), 2, true),
                "Amount / Result",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 15),
                Color.WHITE
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        GridBagConstraints arGbc = new GridBagConstraints();
        arGbc.insets = new Insets(5, 8, 5, 8);
        arGbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Amount label
        JLabel amountLabel = new JLabel("Amount", SwingConstants.CENTER);
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        amountLabel.setForeground(Color.WHITE);
        arGbc.gridx = 0;
        arGbc.gridy = 0;
        arGbc.gridwidth = 1;
        arGbc.weightx = 0.5;
        amountResultPanel.add(amountLabel, arGbc);
        
        // Result label
        JLabel resultLabel = new JLabel("Result", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resultLabel.setForeground(Color.WHITE);
        arGbc.gridx = 1;
        arGbc.gridy = 0;
        amountResultPanel.add(resultLabel, arGbc);
        
        // Amount field
        amountField = new JTextField("1.00");
        amountField.setFont(new Font("Segoe UI", Font.BOLD, 28));
        amountField.setHorizontalAlignment(JTextField.CENTER);
        amountField.setBackground(Color.WHITE);
        amountField.setForeground(Color.BLACK);
        amountField.setCaretColor(Color.BLACK);
        amountField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        amountField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performConversion();
            }
        });
        arGbc.gridx = 0;
        arGbc.gridy = 1;
        arGbc.weightx = 0.5;
        arGbc.fill = GridBagConstraints.HORIZONTAL;
        amountResultPanel.add(amountField, arGbc);
        
        // Result field (non-editable, shows converted amount)
        resultField = new JTextField("");
        resultField.setFont(new Font("Segoe UI", Font.BOLD, 28));
        resultField.setHorizontalAlignment(JTextField.CENTER);
        resultField.setBackground(new Color(255, 255, 255, 230));
        resultField.setForeground(new Color(0, 100, 0));
        resultField.setEditable(false);
        resultField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        arGbc.gridx = 1;
        arGbc.gridy = 1;
        amountResultPanel.add(resultField, arGbc);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        centerPanel.add(amountResultPanel, gbc);
        
        // From currency
        JPanel fromPanel = createStyledPanel("From");
        fromPanel.setLayout(new BorderLayout(10, 0));
        
        fromCurrencyCombo = new JComboBox<String>();
        fromCurrencyCombo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        fromCurrencyCombo.setBackground(Color.WHITE);
        fromCurrencyCombo.setForeground(Color.BLACK);
        fromCurrencyCombo.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        fromPanel.add(fromCurrencyCombo, BorderLayout.CENTER);
        
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(fromPanel, gbc);
        
        // To currency
        JPanel toPanel = createStyledPanel("To");
        toPanel.setLayout(new BorderLayout(10, 0));
        
        toCurrencyCombo = new JComboBox<String>();
        toCurrencyCombo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        toCurrencyCombo.setBackground(Color.WHITE);
        toCurrencyCombo.setForeground(Color.BLACK);
        toCurrencyCombo.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        toPanel.add(toCurrencyCombo, BorderLayout.CENTER);
        
        gbc.gridx = 1;
        centerPanel.add(toPanel, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.insets = new Insets(5, 15, 5, 15);
        
        swapButton = createStyledButton("Swap", Color.WHITE, Color.BLACK);
        swapButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        swapButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                swapCurrencies();
            }
        });
        btnGbc.gridx = 0;
        btnGbc.gridy = 0;
        buttonPanel.add(swapButton, btnGbc);
        
        convertButton = createStyledButton("Convert", Color.WHITE, Color.BLACK);
        convertButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        convertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performConversion();
            }
        });
        btnGbc.gridx = 1;
        btnGbc.gridy = 0;
        buttonPanel.add(convertButton, btnGbc);
        
        refreshButton = createStyledButton("Refresh", Color.WHITE, Color.BLACK);
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshRates();
            }
        });
        btnGbc.gridx = 2;
        btnGbc.gridy = 0;
        buttonPanel.add(refreshButton, btnGbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(buttonPanel, gbc);
        
        // Rate label
        rateLabel = new JLabel("", SwingConstants.CENTER);
        rateLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rateLabel.setForeground(Color.WHITE);
        rateLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        
        gbc.gridy = 3;
        gbc.weighty = 0;
        centerPanel.add(rateLabel, gbc);
        
        // Exchange rate display
        rateDisplayLabel = new JLabel("", SwingConstants.CENTER);
        rateDisplayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        rateDisplayLabel.setForeground(new Color(255, 240, 220));
        rateDisplayLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        
        gbc.gridy = 4;
        gbc.weighty = 0;
        centerPanel.add(rateDisplayLabel, gbc);
        
        return centerPanel;
    }
    
    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 120), 2, true),
                title,
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 15),
                Color.WHITE
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(14, 30, 14, 30)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(240, 240, 240));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(100, 100, 100), 2, true),
                    BorderFactory.createEmptyBorder(12, 28, 12, 28)
                ));
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(bgColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                    BorderFactory.createEmptyBorder(14, 30, 14, 30)
                ));
            }
        });
        
        return button;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        statusLabel = new JLabel("Initializing...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        statusLabel.setForeground(new Color(255, 240, 220));
        
        bottomPanel.add(statusLabel, BorderLayout.CENTER);
        
        return bottomPanel;
    }
    
    private void loadDefaultRates() {
        statusLabel.setText("Fetching exchange rates...");
        setUIEnabled(false);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return currencyService.fetchRates("USD");
            }
            
            @Override
            protected void done() {
                try {
                    if (get() && currencyService.isReady()) {
                        populateCurrencies();
                        statusLabel.setText("Ready - Exchange rates loaded successfully!");
                        rateLabel.setText("Base: USD");
                        performConversion();
                    } else {
                        statusLabel.setText("Failed to load rates. Check internet.");
                        JOptionPane.showMessageDialog(CurrencyConverterGUI.this,
                            "Failed to fetch exchange rates.\nUsing fallback rates for demo.",
                            "Connection Notice",
                            JOptionPane.WARNING_MESSAGE);
                        populateCurrencies();
                    }
                } catch (Exception e) {
                    statusLabel.setText("Error loading rates: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    setUIEnabled(true);
                }
            }
        };
        worker.execute();
    }
    
    private void populateCurrencies() {
        String[] currencies = currencyService.getAvailableCurrencies();
        if (currencies.length == 0) {
            statusLabel.setText("No currencies available");
            return;
        }
        
        fromCurrencyCombo.removeAllItems();
        toCurrencyCombo.removeAllItems();
        
        Arrays.sort(currencies);
        for (String currency : currencies) {
            fromCurrencyCombo.addItem(currency);
            toCurrencyCombo.addItem(currency);
        }
        
        fromCurrencyCombo.setSelectedItem("USD");
        toCurrencyCombo.setSelectedItem("EUR");
        
        if (currencyService.isReady()) {
            rateLabel.setText("Base: " + currencyService.getBaseCurrency());
        }
    }
    
    private void performConversion() {
        if (!currencyService.isReady()) {
            statusLabel.setText("Please wait for rates to load...");
            return;
        }
        
        try {
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                amountField.setText("1.00");
                amountText = "1.00";
            }
            
            double amount = Double.parseDouble(amountText);
            String fromCurrency = (String) fromCurrencyCombo.getSelectedItem();
            String toCurrency = (String) toCurrencyCombo.getSelectedItem();
            
            if (fromCurrency == null || toCurrency == null) {
                resultField.setText("Select currencies");
                rateDisplayLabel.setText("");
                return;
            }
            
            double result = currencyService.convert(amount, fromCurrency, toCurrency);
            
            if (result < 0) {
                resultField.setText("Conversion failed");
                rateDisplayLabel.setText("Please try again");
                return;
            }
            
            double rate = currencyService.convert(1, fromCurrency, toCurrency);
            
            DecimalFormat amountDf = new DecimalFormat("#,##0.00");
            DecimalFormat rateDf = new DecimalFormat("#,##0.0000");
            
            // Set result in the result field
            resultField.setText(amountDf.format(result) + " " + toCurrency);
            
            // Show exchange rate
            rateDisplayLabel.setText("1 " + fromCurrency + " = " + rateDf.format(rate) + " " + toCurrency);
            
            statusLabel.setText(String.format("Converted %.2f %s to %.2f %s", 
                amount, fromCurrency, result, toCurrency));
            
        } catch (NumberFormatException e) {
            resultField.setText("Invalid number");
            rateDisplayLabel.setText("Please enter a valid amount");
            amountField.requestFocus();
            amountField.selectAll();
        } catch (Exception e) {
            resultField.setText("Error");
            rateDisplayLabel.setText(e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void swapCurrencies() {
        Object from = fromCurrencyCombo.getSelectedItem();
        Object to = toCurrencyCombo.getSelectedItem();
        if (from != null && to != null) {
            fromCurrencyCombo.setSelectedItem(to);
            toCurrencyCombo.setSelectedItem(from);
            performConversion();
        }
    }
    
    private void refreshRates() {
        String currentBase = currencyService.getBaseCurrency();
        statusLabel.setText("Refreshing rates for " + currentBase + "...");
        setUIEnabled(false);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return currencyService.fetchRates(currentBase);
            }
            
            @Override
            protected void done() {
                try {
                    if (get() && currencyService.isReady()) {
                        populateCurrencies();
                        statusLabel.setText("Rates refreshed successfully!");
                        performConversion();
                    } else {
                        statusLabel.setText("Failed to refresh rates. Using cached data.");
                    }
                } catch (Exception e) {
                    statusLabel.setText("Error refreshing rates: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    setUIEnabled(true);
                }
            }
        };
        worker.execute();
    }
    
    private void setUIEnabled(boolean enabled) {
        if (convertButton != null) convertButton.setEnabled(enabled);
        if (swapButton != null) swapButton.setEnabled(enabled);
        if (refreshButton != null) refreshButton.setEnabled(enabled);
        if (fromCurrencyCombo != null) fromCurrencyCombo.setEnabled(enabled);
        if (toCurrencyCombo != null) toCurrencyCombo.setEnabled(enabled);
        if (amountField != null) amountField.setEnabled(enabled);
        if (resultField != null) resultField.setEnabled(enabled);
    }
}