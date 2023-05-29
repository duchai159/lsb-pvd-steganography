import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class PVD {
    static int[] lower = {0, 2, 6, 12, 20, 30, 42, 56, 72, 90, 110, 132, 156, 182, 210, 240};
    static int[] upper = {1, 5, 11, 19, 29, 41, 55, 71, 89, 109, 131, 155, 181, 209, 239, 255};
    static int width, height;
    static BufferedImage image;
    static File file;
    static String message;
    static int messageLength;
    static ArrayList<Integer> binary = new ArrayList<>();
    static int numBit;
    static int diff;
    static int diffNew;
    static int p1, r1, g1, b1;
    static int p2, r2, g2, b2;
    static int m;
    static int iMessage;
    static String key;
    static int keyLength;
    static int iKey;
    static int keyChar;
    static int color;

    static void init() {
        width = image.getWidth();
        height = image.getHeight();
        iMessage = 0;
        iKey = -1;
        color = -1;
    }

    static void encode() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < messageLength; i++) {
            int plain = message.charAt(i);
            iKey = (iKey + 1) % keyLength;
            keyChar = key.charAt(iKey);
            plain = (plain + keyChar) % 256;
            result.append((char) plain);
        }
        message = result.toString();
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

    static int process(int range) {
        for (int i = 0; i < 16; i++) {
            if (range >= lower[i] && range <= upper[i]) {
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

    static void solve() throws IOException {
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
                    }
                    else if (color == 1) {
                        c1 = g1;
                        c2 = g2;
                        diff = gDiff;
                    }
                    else {
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
                    }
                    else if (c1 < c2 && diffNew > diff) {
                        c1 = c1 - round;
                        c2 = c2 + floor;
                    }
                    else if (c1 >= c2 && diffNew <= diff) {
                        c1 = c1 - round;
                        c2 = c2 + floor;
                    }
                    else if (c1 < c2 && diffNew <= diff) {
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
        file = new File("/home/hai/Pictures/Picture/pvd.jpg");
        ImageIO.write(image, "png", file);
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        input(scanner);
        init();
        encode();
        solve();
    }
}
