import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class LSB {
    static int width, height;
    static File file;
    static BufferedImage image;
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
    static int xP, yP, iKey;
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
        file = new File("/home/hai/Pictures/Picture/cute.jpg");
        image = ImageIO.read(file);
        System.out.print("Message: ");
        message = scanner.nextLine().concat("`");
        messageLength = message.length();
        System.out.print("Key: ");
        key = scanner.nextLine();
        keyLength = key.length();
    }

    static void solve() throws IOException {
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
                alpha[j] = (pixel[j] >> 24) & 0xff;
                red[j] = (pixel[j] >> 16) & 0xff;
                green[j] = (pixel[j] >> 8) & 0xff;
                blue[j] = pixel[j] & 0xff;
                //Giau tin
                red[j] = ((red[j] >> 1) << 1) + bit[++k];
                green[j] = ((green[j] >> 1) << 1) + bit[++k];
                if (k < 7) {
                    blue[j] = ((blue[j] >> 1) << 1) + bit[++k];
                }
                pixel[j] = (alpha[j] << 24) | (red[j] << 16) | (green[j] << 8) | blue[j];
                image.setRGB(xP, yP, pixel[j]);
            }
        }
        file = new File("/home/hai/Pictures/Picture/lsb.jpg");
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
