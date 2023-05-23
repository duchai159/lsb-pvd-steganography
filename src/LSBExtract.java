import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class LSBExtract {
    static int width, height;
    static File file;
    static BufferedImage image;
    static String key;
    static int keyLength;
    static int[] pixel;
    static int[] red;
    static int[] green;
    static int[] blue;
    static int xP, yP, iKey;
    static int plain;
    static int keyChar;

    static void init() {
        width = image.getWidth();
        height = image.getHeight();
        pixel = new int[]{0, 0, 0};
        red = new int[]{0, 0, 0};
        green = new int[]{0, 0, 0};
        blue = new int[]{0, 0, 0};
        xP = -1;
        yP = 0;
        iKey = -1;
    }

    static void input(Scanner scanner) throws IOException {
        file = new File("/home/hai/Pictures/Picture/lsb.jpg");
        image = ImageIO.read(file);
        System.out.print("Key: ");
        key = scanner.nextLine();
        keyLength = key.length();
    }

    static void solve() {
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
            System.out.print((char) plain);
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        input(scanner);
        init();
        solve();
    }
}
