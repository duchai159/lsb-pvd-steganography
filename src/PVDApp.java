import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class PVDApp extends JFrame {
    String path = null;
    File selectedFile = null;

    JLabel lblTitle1 = new JLabel("Message:");
    JLabel lblTitle2 = new JLabel("Extracted message:");
    JLabel lblTitle11 = new JLabel("");
    JLabel lblTitle12 = new JLabel("");

    JTextArea txtArea = new JTextArea();
    JTextArea txtArea2 = new JTextArea();
    JTextArea txtPass = new JTextArea();
    JTextArea txtPass1 = new JTextArea();

    JButton select = new JButton("Image selection");
    JButton select1 = new JButton("Image selection");
    JButton hiding = new JButton("Hiding");
    JButton extracting = new JButton("Extracting");

    JLabel imgJLabel = new JLabel();
    JLabel imgJLabel1 = new JLabel();

    JLabel keyLabel = new JLabel("Key");
    JLabel keyLabel1 = new JLabel("Key");

    PVDApp() {
        setTitle("LSB Hiding");
        setSize(900, 600);
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        add(keyLabel);
        keyLabel.setBounds(50, 520, 140, 30);

        add(txtPass);
        txtPass.setBounds(100, 525, 250, 20);
        txtPass.setLineWrap(true);
        txtPass.setWrapStyleWord(true);
        txtPass.setEditable(true);

        add(keyLabel1);
        keyLabel1.setBounds(500, 520, 140, 30);

        add(txtPass1);
        txtPass1.setBounds(550, 525, 250, 20);
        txtPass1.setLineWrap(true);
        txtPass1.setWrapStyleWord(true);
        txtPass1.setEditable(true);

        add(lblTitle11);
        add(lblTitle12);
        lblTitle11.setBounds(190, 0, 30, 30);
        lblTitle12.setBounds(640, 0, 30, 30);

        add(select);
        add(select1);
        add(hiding);
        add(extracting);
        select.setBounds(50, 300, 130, 30);
        select1.setBounds(500, 300, 130, 30);
        hiding.setBounds(220, 300, 130, 30);
        extracting.setBounds(670, 300, 130, 30);

        add(lblTitle1);
        add(lblTitle2);
        lblTitle1.setBounds(50, 330, 130, 30);
        lblTitle2.setBounds(500, 270, 300, 150);

        add(txtArea);
        txtArea.setBounds(50, 360, 300, 150);
        txtArea.setLineWrap(true);
        txtArea.setWrapStyleWord(true);
        txtArea.setAutoscrolls(true);
        txtArea.setFocusable(true);

        add(txtArea2);
        txtArea2.setBounds(500, 360, 300, 150);
        txtArea2.setLineWrap(true);
        txtArea2.setWrapStyleWord(true);
        txtArea2.setAutoscrolls(true);
        txtArea2.setEditable(false);

        add(imgJLabel);
        imgJLabel.setBounds(50, 100, 300, 150);

        add(imgJLabel1);
        imgJLabel1.setBounds(500, 100, 300, 150);

        select.addActionListener(a -> {
            JFileChooser chooser = new JFileChooser();
            int option = chooser.showOpenDialog(select);
            if (option == JFileChooser.APPROVE_OPTION) {
                selectedFile = chooser.getSelectedFile();
                path = selectedFile.getAbsolutePath();
//                System.out.println(path);
                ImageIcon imageIcon = new ImageIcon(path);
                Image image = imageIcon.getImage().getScaledInstance(imgJLabel.getWidth(), imgJLabel.getHeight(), Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(image);

                imgJLabel.setIcon(scaledIcon);
                imgJLabel.revalidate();
                imgJLabel.repaint();
            }
        });//select image

        hiding.addActionListener(e -> {
            int width;
            String message;
            int messageLength;
            String key;
            int keyLength;
            int[] bit;
            int[] pixel;
            int[] red;
            int[] green;
            int[] blue;
            int xP, yP;
            int iKey;
            int plain;
            int keyChar;
            // For storing image in RAM
            BufferedImage image;
            // Reading input file
            try {
                image = ImageIO.read(selectedFile);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            width = image.getWidth();
            bit = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
            pixel = new int[]{0, 0, 0};
            red = new int[]{0, 0, 0};
            green = new int[]{0, 0, 0};
            blue = new int[]{0, 0, 0};
            xP = -1;
            yP = 0;
            iKey = -1;
            // Start input message and key
            message = txtArea.getText();
            if(message.isEmpty()){
                JOptionPane.showMessageDialog(PVDApp.this, "Vui long nhap message!", "Loi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            message = message.concat("`");// Ky tu bao hieu ket thuc thong diep
            messageLength = message.length();

            key = txtPass.getText();
            if(key.isEmpty()){
                JOptionPane.showMessageDialog(PVDApp.this, "Vui long nhap khoa!", "Loi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            keyLength = key.length();

            for (int i = 0; i < messageLength; i++) {
                // Ma hoa ky tu
                plain = message.charAt(i);
                iKey = (iKey + 1) % keyLength;
                keyChar = key.charAt(iKey);
                plain = (plain + keyChar) % 256;
                // Chuyen message vao mang bit
                for (int j = bit.length - 1; j >= 0; j--) {
                    if (plain >= Math.pow(2, j)) {
                        bit[j] = 1;
                        plain -= Math.pow(2, j);
                    } else {
                        bit[j] = 0;
                    }
                }
                // Giau tin vao 3 pixel
                int k = -1;
                for (int j = 0; j < 3; j++) {
                    xP++;
                    if (xP >= width) {
                        xP = xP % width;
                        yP++;
                    }
                    //Trich xuat
                    pixel[j] = image.getRGB(xP, yP);
                    red[j] = (pixel[j] >> 16) & 0xff;
                    green[j] = (pixel[j] >> 8) & 0xff;
                    blue[j] = pixel[j] & 0xff;
                    //Giau tin
                    red[j] = ((red[j] >> 1) << 1) + bit[++k];
                    green[j] = ((green[j] >> 1) << 1) + bit[++k];
                    if (k < 7) {
                        blue[j] = ((blue[j] >> 1) << 1) + bit[++k];
                    }
                    pixel[j] = (red[j] << 16) | (green[j] << 8) | blue[j];
                    image.setRGB(xP, yP, pixel[j]);
                }
            }

            // WRITE IMAGE
            String outPath;
            try {
                outPath = path.substring(0, path.lastIndexOf("/") + 1);
                outPath = outPath.concat("lsb.png");
                File file;
                file = new File(outPath);

                // Writing output file
                ImageIO.write(image, "png", file);

            } catch (IOException ex) {
                System.out.println("Error: " + ex);
            }
            JOptionPane.showMessageDialog(PVDApp.this, "Giau tin thanh cong!", "Thong bao", JOptionPane.INFORMATION_MESSAGE);
        });// hiding
        select1.addActionListener(a -> {
            JFileChooser chooser = new JFileChooser();
            int option = chooser.showOpenDialog(select1);
            if (option == JFileChooser.APPROVE_OPTION) {
                selectedFile = chooser.getSelectedFile();
                path = selectedFile.getAbsolutePath();
                ImageIcon imageIcon = new ImageIcon(path);
                Image image = imageIcon.getImage().getScaledInstance(imgJLabel1.getWidth(), imgJLabel1.getHeight(), Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(image);

                imgJLabel1.setIcon(scaledIcon);
                imgJLabel1.revalidate();
                imgJLabel1.repaint();
            }
        });//select image
//
        extracting.addActionListener(e -> {
            String extractMessage = "";
            int width, height;
            String key;
            int keyLength;
            int[] pixel;
            int[] red;
            int[] green;
            int[] blue;
            int xP, yP, iKey;
            int plain;
            int keyChar;
            BufferedImage image;
            // Reading input file
            try {
                image = ImageIO.read(selectedFile);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            key = txtPass1.getText();
            if(key.isEmpty()){
                JOptionPane.showMessageDialog(PVDApp.this, "Vui long nhap khoa!", "Loi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            keyLength = key.length();

            width = image.getWidth();
            height = image.getHeight();
            pixel = new int[]{0, 0, 0};
            red = new int[]{0, 0, 0};
            green = new int[]{0, 0, 0};
            blue = new int[]{0, 0, 0};
            xP = -1;
            yP = 0;
            iKey = -1;

            for (int i = 0; i <= (width * height) / 3; i++) {
                plain = 0;
                int k = -1;
                for (int j = 0; j < 3; j++) {
                    xP++;
                    if (xP >= width) {
                        xP = xP % width;
                        yP++;
                    }
                    //Trich xuat
                    pixel[j] = image.getRGB(xP, yP);
                    red[j] = (pixel[j] >> 16) & 0xff;
                    green[j] = (pixel[j] >> 8) & 0xff;
                    blue[j] = pixel[j] & 0xff;
                    //Tach tin
                    plain += (red[j] & 1) * Math.pow(2, ++k);
                    plain += (green[j] & 1) * Math.pow(2, ++k);
                    if (k < 7) {
                        plain += (blue[j] & 1) * Math.pow(2, ++k);
                    }
                }
                //Giai ma
                iKey = (iKey + 1) % keyLength;
                keyChar = key.charAt(iKey);
                plain = (plain + (256 - keyChar)) % 256;
                if ((char) plain == '`') {
                    break;
                }
                extractMessage = extractMessage.concat(String.valueOf((char) plain));
                txtArea2.setText(extractMessage);
            }
        });// extracting

        SwingUtilities.invokeLater(() -> {
            txtArea.setFont(new Font("Arial", Font.PLAIN, 12));
            FontMetrics metrics = txtArea.getFontMetrics(txtArea.getFont());
            int ascent = metrics.getAscent();
            int height = metrics.getHeight();
            txtArea.setMargin(new Insets(ascent, 0, height - ascent, 0));

            txtArea2.setFont(new Font("Arial", Font.PLAIN, 12));
            metrics = txtArea2.getFontMetrics(txtArea2.getFont());
            ascent = metrics.getAscent();
            height = metrics.getHeight();
            txtArea2.setMargin(new Insets(ascent, 0, height - ascent, 0));
        });
    }// constructor

    public static void main(String[] args) {
        new PVDApp();
    }
}
