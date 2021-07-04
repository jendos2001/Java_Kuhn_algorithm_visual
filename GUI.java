import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GUI extends JFrame {
    JButton newData = new JButton("Новые данные");
    JButton toStart = new JButton("В начало");
    JButton toFinish = new JButton("В конец");
    JButton nextStep = new JButton("Следующий шаг");
    JButton prevStep = new JButton("Предыдущий шаг");
    JTextArea info = new JTextArea("Здесь будет ваша реклама гаража, но только после того, как вы мне заплатите");
    JLabel imageLabel = new JLabel();
    JScrollPane imageScroll = new JScrollPane(imageLabel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    Image image;


    public GUI(){
        super("Название");
        this.setJMenuBar(new GUI_MenuBar());
        this.setLayout(new GridBagLayout());
        this.setBounds(100, 100, 800, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(400, 400));

        imageLabel.setPreferredSize(new Dimension(300,400));
        File file = new File("C:\\Users\\2002r\\Desktop\\result.png");
        try {
            image = ImageIO.read(file);
            imageLabel.setIcon(new ImageIcon(image));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.8;
        constraints.gridy = 0  ;  // нулевая ячейка таблицы по вертикали
        constraints.gridx = 0;
        constraints.gridheight = 8;
        imageLabel.setPreferredSize(new Dimension(948, 523));
        imageScroll.setPreferredSize(imageLabel.getSize());
//        imageScroll.revalidate();
        this.add(imageScroll, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.weightx = 0.2;
        constraints.gridx = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        this.add(newData, constraints);

        constraints.gridy = 1;
        this.add(toStart, constraints);

        constraints.gridy = 2;
        this.add(toFinish, constraints);

        constraints.gridy = 3;
        this.add(nextStep, constraints);

        constraints.gridy = 4;
        this.add(prevStep, constraints);

        constraints.gridy = 5;
        constraints.gridheight = 3;
        constraints.weighty = 0.7;
        constraints.fill = GridBagConstraints.BOTH;
//        info.setMaximumSize(new Dimension(1000, 1000));
        info.setPreferredSize(new Dimension(140, 140));
//        info.setMinimumSize(new Dimension(80, 80));
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        this.add(info, constraints);
    }
}
