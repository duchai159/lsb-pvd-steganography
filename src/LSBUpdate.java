import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class LSBUpdate {
    static File file;
    static BufferedImage image;
    static int width, height;
    static String message;
    static int messageLength;
    static String key;
    static int keyLength;
    static int[] bit;
    static int[] pixel;
    static int[] alpha;
    static int[] red;
    static int[] green;
    static int[] blue;
    static int xP, yP;
    static int iKey;
    static int plain;
    static int keyChar;

    static void init() {
        width = image.getWidth();
        height = image.getHeight();
        bit = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        pixel = new int[]{0, 0, 0};
        alpha = new int[]{0, 0, 0};
        red = new int[]{0, 0, 0};
        green = new int[]{0, 0, 0};
        blue = new int[]{0, 0, 0};
        xP = -1;
        yP = 0;
        iKey = -1;
    }

    static void input(Scanner scanner) throws IOException {
        file = new File("/home/hai/Pictures/Picture/tree.png");
        image = ImageIO.read(file);
        System.out.print("Message: ");
        message = scanner.nextLine().concat("`");
        messageLength = message.length();
        System.out.print("Key: ");
        key = scanner.nextLine();
        keyLength = key.length();
    }

    static void encodeCharacter(char character) {
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

    static void hideMessageInPixel(int pixelIndex) {
        xP++;
        if (xP >= width) {
            xP = xP % width;
            yP++;
        }

        pixel[pixelIndex] = image.getRGB(xP, yP);
        red[pixelIndex] = (pixel[pixelIndex] >> 16) & 0xff;
        green[pixelIndex] = (pixel[pixelIndex] >> 8) & 0xff;
        blue[pixelIndex] = pixel[pixelIndex] & 0xff;

        red[pixelIndex] = ((red[pixelIndex] >> 1) << 1) + bit[pixelIndex * 3];
        green[pixelIndex] = ((green[pixelIndex] >> 1) << 1) + bit[pixelIndex * 3 + 1];
        if (pixelIndex * 3 + 2 < bit.length) {
            blue[pixelIndex] = ((blue[pixelIndex] >> 1) << 1) + bit[pixelIndex * 3 + 2];
        }

        pixel[pixelIndex] = (red[pixelIndex] << 16) | (green[pixelIndex] << 8) | blue[pixelIndex];
        image.setRGB(xP, yP, pixel[pixelIndex]);
    }

    static void solve() throws IOException {
        for (int i = 0; i < messageLength; i++) {
            encodeCharacter(message.charAt(i));
            for (int j = 0; j < 3; j++) {
                hideMessageInPixel(j);
            }
        }

        file = new File("/home/hai/Pictures/Picture/lsb.png");
        ImageIO.write(image, "png", file);
        image.flush();
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        input(scanner);
        init();
        solve();
    }
}
