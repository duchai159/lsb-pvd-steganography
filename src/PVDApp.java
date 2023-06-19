import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class PVDApp extends JFrame {
    String path = null;
    File selectedFile = null;
    File selectedFile1 = null;

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

    static int[] lower = {0, 2, 6, 12, 20, 30, 42, 56, 72, 90, 110, 132, 156, 182, 210, 240};
    static int[] upper = {1, 5, 11, 19, 29, 41, 55, 71, 89, 109, 131, 155, 181, 209, 239, 255};


    PVDApp() {
        setTitle("PVD Hiding");
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
            int width, height;

            String message;
            int messageLength;
            ArrayList<Integer> binary = new ArrayList<>();
            int numBit;
            int diff;
            int diffNew;
            int p1, r1, g1, b1;
            int p2, r2, g2, b2;
            int m;
            int iMessage;
            String key;
            int keyLength;
            int iKey;
            int keyChar;
            int color;
            // For storing image in RAM
            BufferedImage image;
            // Reading input file
            if (selectedFile == null) {
                JOptionPane.showMessageDialog(PVDApp.this, "Please select an image!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                image = ImageIO.read(selectedFile);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            //init
            width = image.getWidth();
            height = image.getHeight();
            iMessage = 0;
            iKey = -1;
            color = -1;

            // Start input message and key
            message = txtArea.getText();
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(PVDApp.this, "Please enter message!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            message = message.concat("`");// Ky tu bao hieu ket thuc thong diep
            messageLength = message.length();

            key = txtPass.getText();
            if (key.isEmpty()) {
                JOptionPane.showMessageDialog(PVDApp.this, "Please enter key!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            keyLength = key.length();

            //encode
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < messageLength; i++) {
                int plain = message.charAt(i);
                iKey = (iKey + 1) % keyLength;
                keyChar = key.charAt(iKey);
                plain = (plain + keyChar) % 256;
                result.append((char) plain);
            }
            message = result.toString();

            for (int i = 0; i < messageLength; i++) {
                int temp = message.charAt(i);
                for (int j = 7; j >= 0; j--) {
                    binary.add((temp >> j) & 1);
                }
            }
            numBit = binary.size();
            outerLoop:
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width - 1; j += 2) {
                    m = 0;
                    //pixel 1
                    p1 = image.getRGB(j, i);
                    r1 = (p1 >> 16) & 0xff;
                    g1 = (p1 >> 8) & 0xff;
                    b1 = p1 & 0xff;
                    //pixel 2
                    p2 = image.getRGB(j + 1, i);
                    r2 = (p2 >> 16) & 0xff;
                    g2 = (p2 >> 8) & 0xff;
                    b2 = p2 & 0xff;
                    int rDiff = Math.abs(r1 - r2);
                    int gDiff = Math.abs(g1 - g2);
                    int bDiff = Math.abs(b1 - b2);
                    int mRed = countBit(rDiff);
                    int mGreen = countBit(gDiff);
                    int mBlue = countBit(bDiff);
                    if (mRed > 0) {
                        m = mRed;
                        color = 0;
                    }
                    if (mGreen > 0 && mGreen > m) {
                        m = mGreen;
                        color = 1;
                    }
                    if (mBlue > 0 && mBlue > m) {
                        m = mBlue;
                        color = 2;
                    }
                    if (m != 0) {
                        int c1, c2;
                        if (color == 0) {
                            c1 = r1;
                            c2 = r2;
                            diff = rDiff;
                        } else if (color == 1) {
                            c1 = g1;
                            c2 = g2;
                            diff = gDiff;
                        } else {
                            c1 = b1;
                            c2 = b2;
                            diff = bDiff;
                        }
                        if (m > numBit) {
                            m = numBit;
                        }
                        int dec = 0;
                        for (int k = 0; k < m; k++) {
                            if (binary.get(k + iMessage) == 1) {
                                dec += Math.pow(2, k);
                            }
                        }
                        int index = process(diff);
                        iMessage += m;
                        numBit -= m;
                        diffNew = lower[index] + dec;
                        int round = Math.round((float) Math.abs(diffNew - diff) / 2);
                        int floor = (int) Math.floor((float) Math.abs(diffNew - diff) / 2);
                        if (c1 >= c2 && diffNew > diff) {
                            c1 = c1 + round;
                            c2 = c2 - floor;
                        } else if (c1 < c2 && diffNew > diff) {
                            c1 = c1 - round;
                            c2 = c2 + floor;
                        } else if (c1 >= c2 && diffNew <= diff) {
                            c1 = c1 - round;
                            c2 = c2 + floor;
                        } else if (c1 < c2 && diffNew <= diff) {
                            c1 = c1 + round;
                            c2 = c2 - floor;
                        }
                        if (color == 0) {
                            p1 = (c1 << 16) | (g1 << 8) | b1;
                            p2 = (c2 << 16) | (g2 << 8) | b2;
                        } else if (color == 1) {
                            p1 = (r1 << 16) | (c1 << 8) | b1;
                            p2 = (r2 << 16) | (c2 << 8) | b2;
                        } else {
                            p1 = (r1 << 16) | (g1 << 8) | c1;
                            p2 = (r2 << 16) | (g2 << 8) | c2;
                        }

                        image.setRGB(j, i, p1);
                        image.setRGB(j + 1, i, p2);
                    }
                    if (numBit <= 0) {
                        break outerLoop;
                    }
                }
            }

            // WRITE IMAGE
            String outPath;
            try {
                outPath = path.substring(0, path.lastIndexOf("/") + 1);
                outPath = outPath.concat("pvd.png");
                File file;
                file = new File(outPath);

                // Writing output file
                ImageIO.write(image, "png", file);

            } catch (IOException ex) {
                System.out.println("Error: " + ex);
            }
            JOptionPane.showMessageDialog(PVDApp.this, "Message successfully hidden!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });// hiding
        select1.addActionListener(a -> {
            JFileChooser chooser = new JFileChooser();
            int option = chooser.showOpenDialog(select1);
            if (option == JFileChooser.APPROVE_OPTION) {
                selectedFile1 = chooser.getSelectedFile();
                path = selectedFile1.getAbsolutePath();
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
            ArrayList<Integer> binary = new ArrayList<>();
            int diff;
            int p1, r1, g1, b1;
            int p2, r2, g2, b2;
            int m;
            int iMessage;
            int iBit;
            String key;
            int keyLength;
            int iKey;
            int keyChar;
            int color;
            BufferedImage image;
            if (selectedFile1 == null) {
                JOptionPane.showMessageDialog(PVDApp.this, "Please select an image!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Reading input file
            try {
                image = ImageIO.read(selectedFile1);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            key = txtPass1.getText();
            if (key.isEmpty()) {
                JOptionPane.showMessageDialog(PVDApp.this, "Please enter key!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            keyLength = key.length();

            //init
            width = image.getWidth();
            height = image.getHeight();
            iMessage = 0;
            iBit = 0;
            iKey = -1;
            color = -1;

            //solve
            outerLoop:
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width - 1; j += 2) {
                    m = 0;
                    //pixel 1
                    p1 = image.getRGB(j, i);
                    r1 = (p1 >> 16) & 0xff;
                    g1 = (p1 >> 8) & 0xff;
                    b1 = p1 & 0xff;
                    //pixel 2
                    p2 = image.getRGB(j + 1, i);
                    r2 = (p2 >> 16) & 0xff;
                    g2 = (p2 >> 8) & 0xff;
                    b2 = p2 & 0xff;
                    int rDiff = Math.abs(r1 - r2);
                    int gDiff = Math.abs(g1 - g2);
                    int bDiff = Math.abs(b1 - b2);
                    int mRed = countBit(rDiff);
                    int mGreen = countBit(gDiff);
                    int mBlue = countBit(bDiff);
                    if (mRed > 0) {
                        m = mRed;
                        color = 0;
                    }
                    if (mGreen > 0 && mGreen > m) {
                        m = mGreen;
                        color = 1;
                    }
                    if (mBlue > 0 && mBlue > m) {
                        m = mBlue;
                        color = 2;
                    }
                    if (m != 0) {
                        if(color == 0){
                            diff = rDiff;
                        }
                        else if(color == 1){
                            diff = gDiff;
                        }
                        else{
                            diff = bDiff;
                        }
                        int index = process(diff);
                        iMessage += m;
                        int dec = diff - lower[index];
                        for (int k = 0; k < m; k++) {
                            binary.add((dec >> k) & 1);
                        }
                    }
                    if (iMessage >= 8) {
                        int plain = 0;
                        for (int k = 0; k < 8; k++) {
                            plain += (int) (binary.get(iBit + k) * Math.pow(2, 8 - k - 1));
                        }
                        //decode
                        iKey = (iKey + 1) % keyLength;
                        keyChar = key.charAt(iKey);
                        plain = (plain + (256 - keyChar)) % 256;

                        if ((char) plain == '`') {
                            break outerLoop;
                        }
                        extractMessage = extractMessage.concat(String.valueOf((char) plain));
                        txtArea2.setText(extractMessage);
                        iMessage -= 8;
                        iBit += 8;
                    }
                }
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

    static int process(int diff) {
        for (int i = 0; i < 16; i++) {
            if (diff >= lower[i] && diff <= upper[i]) {
                return i;
            }
        }
        return -1;
    }

    static int countBit(int diff) {
        int index = process(diff);
        int L = lower[index];
        int U = upper[index];
        return (int) Math.floor(Math.log(U - L) / Math.log(2));
    }

    public static void main(String[] args) {
        new PVDApp();
    }
}
