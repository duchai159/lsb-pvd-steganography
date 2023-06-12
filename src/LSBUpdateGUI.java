import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LSBUpdateGUI extends JFrame {
    private JLabel imageLabel;
    private JButton selectImageButton;
    private JButton encodeButton;

    private File selectedFile;
    private BufferedImage image;
    private int width, height;
    private String message;
    private int messageLength;
    private String key;
    private int keyLength;
    private int[] bit;
    private int[] pixel;
    private int[] alpha;
    private int[] red;
    private int[] green;
    private int[] blue;
    private int xP, yP;
    private int iKey;
    private int plain;
    private int keyChar;

    public LSBUpdateGUI() {
        setTitle("LSB Update");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        imageLabel = new JLabel();
        selectImageButton = new JButton("Select Image");
        encodeButton = new JButton("Encode");

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(selectImageButton);
        panel.add(encodeButton);

        add(imageLabel, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        selectImageButton.addActionListener(e -> handleSelectImageButton());

        encodeButton.addActionListener(e -> {
            try {
                handleEncodeButton();
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(LSBUpdateGUI.this, "An error occurred while encoding the message.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }

    private void handleSelectImageButton() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            try {
                image = ImageIO.read(selectedFile);
                width = image.getWidth();
                height = image.getHeight();
                displayImage();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while reading the image file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEncodeButton() throws IOException {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Please select an image.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        message = JOptionPane.showInputDialog(this, "Enter the message:");
        if (message == null || message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a message.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        key = JOptionPane.showInputDialog(this, "Enter the key:");
        if (key == null || key.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a key.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        message = message.concat("`");
        messageLength = message.length();
        keyLength = key.length();

        encodeMessage();

        File outputFile = new File("/home/hai/Pictures/Picture/lsb.png");
        ImageIO.write(image, "png", outputFile);
        JOptionPane.showMessageDialog(this, "Image successfully encoded.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void encodeMessage() {
        bit = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        pixel = new int[]{0, 0, 0};
        alpha = new int[]{0, 0, 0};
        red = new int[]{0, 0, 0};
        green = new int[]{0, 0, 0};
        blue = new int[]{0, 0, 0};
        xP = -1;
        yP = 0;
        iKey = -1;

        for (int i = 0; i < messageLength; i++) {
            encodeCharacter(message.charAt(i));
            for (int j = 0; j < 3; j++) {
                hideMessageInPixel(j);
            }
        }

        displayImage();
    }

    private void encodeCharacter(char character) {
        plain = character;
        iKey = (iKey + 1) % keyLength;
        keyChar = key.charAt(iKey);
        plain = (plain + keyChar) % 256;

        for (int j = bit.length - 1; j >= 0; j--) {
            if (plain >= Math.pow(2, j)) {
                bit[j] = 1;
                plain -= Math.pow(2, j);
            } else {
                bit[j] = 0;
            }
        }
    }

    private void hideMessageInPixel(int pixelIndex) {
        xP++;
        if (xP >= width) {
            xP = xP % width;
            yP++;
        }

        pixel[pixelIndex] = image.getRGB(xP, yP);
        alpha[pixelIndex] = (pixel[pixelIndex] >> 24) & 0xff;
        red[pixelIndex] = (pixel[pixelIndex] >> 16) & 0xff;
        green[pixelIndex] = (pixel[pixelIndex] >> 8) & 0xff;
        blue[pixelIndex] = pixel[pixelIndex] & 0xff;

        red[pixelIndex] = ((red[pixelIndex] >> 1) << 1) + bit[pixelIndex * 3];
        green[pixelIndex] = ((green[pixelIndex] >> 1) << 1) + bit[pixelIndex * 3 + 1];
        if (pixelIndex * 3 + 2 < bit.length) {
            blue[pixelIndex] = ((blue[pixelIndex] >> 1) << 1) + bit[pixelIndex * 3 + 2];
        }

        pixel[pixelIndex] = (alpha[pixelIndex] << 24) | (red[pixelIndex] << 16) | (green[pixelIndex] << 8) | blue[pixelIndex];
        image.setRGB(xP, yP, pixel[pixelIndex]);
    }

    private void displayImage() {
        int frameWidth = getContentPane().getWidth();
        int frameHeight = getContentPane().getHeight();

        int scaledWidth, scaledHeight;
        if (width > frameWidth || height > frameHeight) {
            double widthRatio = (double) frameWidth / width;
            double heightRatio = (double) frameHeight / height;
            double scaleRatio = Math.min(widthRatio, heightRatio);
            scaledWidth = (int) (width * scaleRatio);
            scaledHeight = (int) (height * scaleRatio);
        } else {
            scaledWidth = width;
            scaledHeight = height;
        }

        Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        ImageIcon imageIcon = new ImageIcon(scaledImage);
        imageLabel.setIcon(imageIcon);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LSBUpdateGUI::new);
    }
}
