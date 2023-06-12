import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LSBExtractUpdateGUI extends JFrame {
    private JTextArea messageTextArea;
    private JButton decodeButton;

    private BufferedImage image;
    private int width, height;
    private String key;

    public LSBExtractUpdateGUI() {
        setTitle("LSB Image Decoder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        messageTextArea = new JTextArea(10, 30);
        messageTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageTextArea);
        add(scrollPane, BorderLayout.CENTER);

        decodeButton = new JButton("Decode");
        decodeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                decodeMessage();
            }
        });
        add(decodeButton, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void decodeMessage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Image");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                image = ImageIO.read(selectedFile);
                width = image.getWidth();
                height = image.getHeight();

                key = JOptionPane.showInputDialog(this, "Enter Key");
                if (key == null || key.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Invalid key!");
                    return;
                }

                decodeMessageFromImage();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void decodeMessageFromImage() {
        StringBuilder message = new StringBuilder();
        int keyLength = key.length();
        int[] pixel = new int[]{0, 0, 0};
        int[] red = new int[]{0, 0, 0};
        int[] green = new int[]{0, 0, 0};
        int[] blue = new int[]{0, 0, 0};
        int xP = -1;
        int yP = 0;
        int iKey = -1;
        int plain;
        int keyChar;

        for (int i = 0; i <= (width * height) / 3; i++) {
            for (int j = 0; j < 3; j++) {
                xP++;
                if (xP >= width) {
                    xP = xP % width;
                    yP++;
                }
                pixel[j] = image.getRGB(xP, yP);
                red[j] = (pixel[j] >> 16) & 0xff;
                green[j] = (pixel[j] >> 8) & 0xff;
                blue[j] = pixel[j] & 0xff;

                plain = 0;
                plain += (red[j] & 1) * Math.pow(2, j * 3);
                plain += (green[j] & 1) * Math.pow(2, j * 3 + 1);
                if (j * 3 + 2 < 7) {
                    plain += (blue[j] & 1) * Math.pow(2, j * 3 + 2);
                }

                iKey = (iKey + 1) % keyLength;
                keyChar = key.charAt(iKey);
                plain = (plain + (256 - keyChar)) % 256;

                if ((char) plain == '`') {
                    break;
                }

                message.append((char) plain);
            }
        }

        messageTextArea.setText(message.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LSBExtractUpdateGUI();
            }
        });
    }
}
