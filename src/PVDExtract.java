import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class PVDExtract {
    static int[] lower = {0, 2, 6, 12, 20, 30, 42, 56, 72, 90, 110, 132, 156, 182, 210, 240};
    static int[] upper = {1, 5, 11, 19, 29, 41, 55, 71, 89, 109, 131, 155, 181, 209, 239, 255};
    static int width, height;
    static BufferedImage image;
    static File file;
    static ArrayList<Integer> binary = new ArrayList<>();
    static int diff;
    static int p1, r1, g1, b1;
    static int p2, r2, g2, b2;
    static int m;
    static int iMessage;
    static int iBit;
    static String key;
    static int keyLength;
    static int iKey;
    static int keyChar;
    static int color;

    static void input(Scanner scanner) throws IOException {
        file = new File("/home/hai/Pictures/Picture/pvd.jpg");
        image = ImageIO.read(file);
        System.out.print("Key: ");
        key = scanner.nextLine();
        keyLength = key.length();
    }

    static void init() {
        width = image.getWidth();
        height = image.getHeight();
        iMessage = 0;
        iBit = 0;
        iKey = -1;
        color = -1;
    }

    static int process(int range) {
        for (int i = 0; i < 16; i++) {
            if (range >= lower[i] && range <= upper[i]) {
                return i;
            }
        }
        return -1;
    }
    static int decode(int plain){
        iKey = (iKey + 1) % keyLength;
        keyChar = key.charAt(iKey);
        plain = (plain + (256 - keyChar)) % 256;
        return plain;
    }
    static int countBit(int diff) {
        int index = process(diff);
        int L = lower[index];
        int U = upper[index];
        return (int) Math.floor(Math.log(U - L) / Math.log(2));
    }
    static void solve() {
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
                    plain = decode(plain);
                    if ((char) plain == '`') {
                        break outerLoop;
                    }
                    System.out.print((char) plain);
                    iMessage -= 8;
                    iBit += 8;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        input(scanner);
        init();
        solve();
    }
}
