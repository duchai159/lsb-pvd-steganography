import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class LSBApp extends JFrame{
    String path = null;
    String out_path = null;
    File selectedFile = null;

    JLabel lblMsg = new JLabel();
    JLabel lblTitle1 = new JLabel("Message:");
    JLabel lblTitle2 = new JLabel("Extracted message:");
    JLabel lblTitle11 = new JLabel("");
    JLabel lblTitle12 = new JLabel("");

    JTextField txtMsg = new JTextField();
    JTextArea txtArea = new JTextArea();
    JTextArea txtArea2 = new JTextArea();
    JTextArea txtPass = new JTextArea();

    JButton select = new JButton("Image selection");
    JButton select1 = new JButton("Image selection");
    JButton hiding = new JButton("Hidding");
    JButton extracting = new JButton("Extracting");

    JLabel imgJLabel = new JLabel();
    JLabel imgJLabelSte = new JLabel();

    JCheckBox checkBoxPass = new JCheckBox("Enable password");

    LSBApp(){
        setTitle("LSB Hiding");
        setSize(900, 600);
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(checkBoxPass);
        checkBoxPass.setBounds(50, 520, 140, 30);

        add(txtPass);
        txtPass.setBounds(190, 525, 160, 20);
        txtPass.setLineWrap(true);
        txtPass.setWrapStyleWord(true);
        txtPass.setEditable(false);

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
        txtArea2.setEditable(false);

        add(imgJLabelSte);

        checkBoxPass.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent a){

                txtPass.setEditable(checkBoxPass.isSelected());
                if(checkBoxPass.isSelected()==false)
                    txtPass.setText(null);
            }

        });//enable pass

        select.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent a){

                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int option = chooser.showOpenDialog(select);
                if (option == JFileChooser.APPROVE_OPTION) {
                    selectedFile = chooser.getSelectedFile();
                    path = selectedFile.getAbsolutePath();
                    ImageIcon x1 = new ImageIcon(path);
                    add(imgJLabel);
                    imgJLabel.setIcon(x1);
                    imgJLabel.setBounds(70, 30, x1.getIconWidth(), x1.getIconHeight());
                }
            }

        });//select image

        hiding.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                JFileChooser chooser = new JFileChooser();
                chooser.showSaveDialog(hiding);
                // For storing image in RAM
                BufferedImage image = null;
                try {
                    // Reading input file
                    image = ImageIO.read(selectedFile);
                } catch (IOException ex) {
                    System.out.println("Error: "+ex);
                }

                int width = image.getWidth();
                int height = image.getHeight();
                int p[]={0,0,0};
                int r[]={0,0,0};
                int g[]={0,0,0};
                int b[]={0,0,0};
                int a[]={0,0,0};

                //Start input message and key
                Scanner scr = new Scanner(System.in);

                String msg; int msgLegth;
                msg = txtArea.getText();
                msg = msg.concat("`");//Ky tu bao hieu ket thuc thong diep
                msgLegth = msg.length();

                String key; int keyLegth;
                key = txtPass.getText();
                keyLegth = key.length();
                if(keyLegth%2==0)
                    key = key.concat(";");

                int bit[] = {0,0,0,0,0,0,0,0};
                int x=-1, y=0;
                int h=-1;

                int plain;
                int gt;
                int key_char1, key_char2;

                for(int z=0; z<msgLegth; z++){
                    plain = msg.charAt(z);

                    if(checkBoxPass.isSelected()==true){
                        h = (h + 1) % keyLegth;
                        key_char1 = key.charAt(h);
                        h = (h + 1) % keyLegth;
                        key_char2 = key.charAt(h);
                        key_char1 = (key_char1*key_char2)%256;
                        plain = (plain + key_char1)%256;
                    }

                    //char -> bit
                    for(int i=7; i>=0; i--){
                        if(plain >= (int)Math.pow(2,i)){
                            bit[i]=1;
                            plain = plain - (int)Math.pow(2,i);
                        }else
                            bit[i]=0;
                    }
                    //End char -> bit

                    //Take 3 pixel
                    for(int j=0; j<3; j++){
                        x++;
                        if(x>=width){
                            x=x%width;
                            y++;
                        }
                        p[j] = image.getRGB(x, y);
                        a[j] = (p[j]>>24) & 0xff;
                        r[j] = (p[j]>>16) & 0xff;
                        g[j] = (p[j]>>8) & 0xff;
                        b[j] = p[j] & 0xff;

                        //Hidding
                        r[0]=((r[0]>>1)<<1)+bit[0];
                        g[0]=((g[0]>>1)<<1)+bit[1];
                        b[0]=((b[0]>>1)<<1)+bit[2];
                        r[1]=((r[1]>>1)<<1)+bit[3];
                        g[1]=((g[1]>>1)<<1)+bit[4];
                        b[1]=((b[1]>>1)<<1)+bit[5];
                        r[2]=((r[2]>>1)<<1)+bit[6];
                        g[2]=((g[2]>>1)<<1)+bit[7];
                        //End hidding

                        p[j] = (a[j]<<24) | (r[j]<<16) | (g[j]<<8) | b[j];
                        image.setRGB(x, y, p[j]);
                    }//End taking

                }//end for()

                // WRITE IMAGE
                try{
                    out_path = path.substring(0, path.lastIndexOf("\\")+1);
                    out_path = out_path.concat("lsb.png");
                    File f = null;
                    f = new File(out_path);

                    //  Writing output file
                    ImageIO.write(image, "png", f);

                }catch(IOException ex){
                    System.out.println("Error: "+ex);
                }


                BufferedImage xx = null;
                File ff = new File(out_path);
                try {
                    xx = ImageIO.read(ff);
                } catch (IOException ex) {
                    Logger.getLogger("Error: " +ex);
                }
                ImageIcon imgcn = new ImageIcon(xx);
                imgJLabelSte.setIcon(imgcn);
                imgJLabelSte.setBounds(520, 30, imgcn.getIconWidth(), imgcn.getIconHeight());
                lblTitle12.setText("");
                lblTitle11.setText("(1)");
            }
        });//hiding

        extracting.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e){
                String extractMSG = "";
                // For storing image in RAM
                BufferedImage image = null;
                File f = null;
                f = new File(out_path);
                try {
                    // Reading input file
                    image = ImageIO.read(f);
                } catch (IOException ex) {
                    System.out.println("Error: " +ex);
                }

                int width = image.getWidth();
                int height = image.getHeight();
                int p[]={0,0,0};
                int r[]={0,0,0};
                int g[]={0,0,0};
                int b[]={0,0,0};

                int bit[] = {0,0,0,0,0,0,0,0};
                int x=-1, y=0;
                int n;

                for(int z=0; z<width*height; z++){
                    n=0;
                    //Take 3 pixel
                    for(int j=0; j<3; j++){
                        x++;
                        if(x>=width){
                            x=x%width;
                            y++;
                        }
                        p[j] = ((image.getRGB(x, y)%16777216) + 16777216) % 16777216;
                        r[j] = (p[j]>>16) & 0xff;
                        g[j] = (p[j]>>8) & 0xff;
                        b[j] = p[j] & 0xff;
                    }//End taking

                    n = n + (r[0]%2)*(int)Math.pow(2,0);
                    n = n + (g[0]%2)*(int)Math.pow(2,1);
                    n = n + (b[0]%2)*(int)Math.pow(2,2);
                    n = n + (r[1]%2)*(int)Math.pow(2,3);
                    n = n + (g[1]%2)*(int)Math.pow(2,4);
                    n = n + (b[1]%2)*(int)Math.pow(2,5);
                    n = n + (r[2]%2)*(int)Math.pow(2,6);
                    n = n + (g[2]%2)*(int)Math.pow(2,7);

                    if((char)n == '`')//Ky tu bao hieu ket thuc thong diep
                        break;
                    extractMSG = extractMSG.concat(Character.toString((char)n));

                }
                image.flush();
                txtArea2.setText(extractMSG);
                lblTitle11.setText("");
                lblTitle12.setText("(2)");
            }
        });//extracting

    }

    public static void main(String[] args)throws IOException{
        LSBApp test = new LSBApp();

    }
}