import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
    static int L, U;
    static int iMessage = 0;
    static int cnt;
    static int iBit = 0;

    static void input() throws IOException {
        file = new File("/home/hai/Pictures/Picture/pvd.png");
        image = ImageIO.read(file);
    }

    static void init() {
        width = image.getWidth();
        height = image.getHeight();
    }

    static int process(int range) {
        for (int i = 0; i < 16; i++) {
            if (range >= lower[i] && range <= upper[i]) {
                return i;
            }
        }
        return -1;
    }

    static void solve() {
        outerLoop:
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j += 2) {
                //pixel 1
                p1 = image.getRGB(j, i);
                r1 = (p1 >> 16) & 0xff;
                g1 = (p1 >> 16) & 0xff;
                b1 = p1 & 0xff;
                //pixel 2
                p2 = image.getRGB(j + 1, i);
                r2 = (p2 >> 16) & 0xff;
                g2 = (p2 >> 8) & 0xff;
                b2 = p2 & 0xff;
                diff = Math.abs(r1 - r2);
                int index = process(diff);
                L = lower[index];
                U = upper[index];
                m = (int) Math.floor(Math.log(U - L) / Math.log(2));
                if (m != 0) {
                    iMessage += m;
                    int dec = diff - L;
                    for (int k = 0; k < m; k++) {
                        binary.add((dec >> k) & 1);
                    }
                }
                if (iMessage >= 8) {
                    int plain = 0;
                    for (int k = 0; k < 8; k++) {
                        plain += (int) (binary.get(iBit + k) * Math.pow(2, 8 - k - 1));
                    }
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
        input();
        init();
        solve();
    }
}
