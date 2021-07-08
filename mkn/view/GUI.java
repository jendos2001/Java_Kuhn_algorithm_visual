package mkn.view;

import mkn.controller.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GUI extends JFrame implements View {
    private JMenu picture = new JMenu("Изображение");
    private JMenuItem save = new JMenuItem("Сохранить изображение");
    private JMenu choose = new JMenu("Выбрать отображение");
    private JRadioButtonMenuItem full = new JRadioButtonMenuItem("Полное отображение", true);
    private JRadioButtonMenuItem scale = new JRadioButtonMenuItem("Масштаб. отображение", false);

    private JButton newData = new JButton("Новые данные");
    private JButton toStart = new JButton("В начало");
    private JButton toFinish = new JButton("В конец");
    private JButton nextStep = new JButton("Следующий шаг");
    private JButton prevStep = new JButton("Предыдущий шаг");
    private JTextArea info = new JTextArea("Здесь будет ваша реклама гаража, но только после того, как вы мне заплатите");

    private JLabel imageLabel = new JLabel();
    private JScrollPane imageScroll = new JScrollPane(imageLabel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    private Image image;

    private Controller controller;

    private enum State { NO_DATA,
                        START_ALGORITHM,
                        MIDDLE_ALGORITHM,
                        END_ALGORITHM};
    private State state = State.NO_DATA;


    public GUI(){
        super("Алгоритм Куна");
        this.setJMenuBar(makeMenuBar());
        this.setLayout(new GridBagLayout());
        this.setBounds(100, 100, 800, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(600, 600));

        JFileChooser fileOpen = new JFileChooser();
        fileOpen.setFileFilter(new FileNameExtensionFilter("Текстовый файл, *.txt", "txt"));
        fileOpen.setAcceptAllFileFilterUsed(false);
        int ret = fileOpen.showDialog(null, "Открыть файл");
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileOpen.getSelectedFile();
            if(controller.getNewData(file.getAbsolutePath())){
                state = State.START_ALGORITHM;
            };
        }
        checkState();
        makeWindowWithWidgets();
    }

    public void setImage(String path) {
        File file = new File(path);
        try {
            image = ImageIO.read(file);
            imageLabel.setIcon(new ImageIcon(image));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        imageLabel.setPreferredSize(new Dimension(image.getWidth(null), image.getHeight(null)));
        imageScroll.setPreferredSize(imageLabel.getSize());
        imageScroll.revalidate();
    }

    private void makeWindowWithWidgets(){
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.8;
        constraints.gridy = 0;  // нулевая ячейка таблицы по вертикали
        constraints.gridx = 0;
        constraints.gridheight = 8;
        this.add(imageScroll, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.weightx = 0.2;
        constraints.gridx = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 30, 5);
        newData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newDataAction();
            }
        });
        this.add(newData, constraints);

        constraints.gridy = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        toStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.toStart();
                state = State.START_ALGORITHM;
                checkState();
            }
        });
        this.add(toStart, constraints);

        constraints.gridy = 2;
        constraints.insets = new Insets(5, 5, 30, 5);
        toFinish.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.toFinish();
                state = State.END_ALGORITHM;
                checkState();
            }
        });
        this.add(toFinish, constraints);

        constraints.gridy = 3;
        constraints.insets = new Insets(5, 5, 5, 5);
        nextStep.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(controller.nextStep())
                    state = State.END_ALGORITHM;
                else
                    state = State.MIDDLE_ALGORITHM;
                checkState();
            }
        });
        this.add(nextStep, constraints);

        constraints.gridy = 4;
        prevStep.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(controller.prevStep())
                    state = State.START_ALGORITHM;
                else
                    state = State.MIDDLE_ALGORITHM;
                checkState();
            }
        });
        this.add(prevStep, constraints);

        constraints.gridy = 5;
        constraints.gridheight = 3;
        constraints.weighty = 0.7;
        constraints.fill = GridBagConstraints.BOTH;
        info.setPreferredSize(new Dimension(140, 140));
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        this.add(info, constraints);
    }

    private void newDataAction(){
        controller.deleteOld();
        state = State.NO_DATA;
        imageLabel.setIcon(new ImageIcon());
        JFileChooser fileOpen = new JFileChooser();
        fileOpen.setFileFilter(new FileNameExtensionFilter("Текстовый файл, *.txt", "txt"));
        fileOpen.setAcceptAllFileFilterUsed(false);
        int ret = fileOpen.showDialog(null, "Открыть файл");
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileOpen.getSelectedFile();
            if(controller.getNewData(file.getAbsolutePath())){
                state = State.START_ALGORITHM;
                /*
                yfgbcfnm
                 */
            };
        }
        checkState();
    }

    private JMenuBar makeMenuBar(){
        ButtonGroup bg = new ButtonGroup();
        bg.add(full);
        bg.add(scale);

        // размещаем все в нужном порядке
        choose.add(full);
        choose.add(scale);
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (image.getHeight(null) != -1) {           //исправить
                    JFileChooser fileSave = new JFileChooser();
                    fileSave.setDialogTitle("Сохранение файла");
                    // Определение режима - только файл
                    fileSave.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fileSave.setFileFilter(new FileNameExtensionFilter("Изображение, *.png", "png"));
                    fileSave.setAcceptAllFileFilterUsed(false);
                    int result = fileSave.showSaveDialog(null);
                    // Если файл выбран, то представим его в сообщении
                    if (result == JFileChooser.APPROVE_OPTION){
                        BufferedImage bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                        Graphics2D bGr = bimage.createGraphics();
                        bGr.drawImage(image, 0, 0, null);
                        bGr.dispose();
                        File outputFile = new File(fileSave.getSelectedFile().getAbsolutePath());
                        try {
                            ImageIO.write(bimage, "PNG", outputFile);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                        JOptionPane.showMessageDialog(null,
                                "Файл '" + fileSave.getSelectedFile() +
                                        " ) сохранен");
                }
            }
        });
        picture.add(save);
        picture.add(choose);

        JMenuBar bar = new JMenuBar();
        bar.add(picture);
        bar.add(Box.createHorizontalGlue());
        return bar;
    }

    private void checkState(){
        switch (state){
            case NO_DATA:
                nextStep.setEnabled(false);
                prevStep.setEnabled(false);
                toStart.setEnabled(false);
                toFinish.setEnabled(false);
                newData.setEnabled(true);
                save.setEnabled(false);
                full.setEnabled(false);
                scale.setEnabled(false);
                break;

            case START_ALGORITHM:
                nextStep.setEnabled(true);
                prevStep.setEnabled(false);
                toStart.setEnabled(false);
                toFinish.setEnabled(true);
                newData.setEnabled(true);
                save.setEnabled(true);
                full.setEnabled(true);
                scale.setEnabled(true);
                break;

            case MIDDLE_ALGORITHM:
                nextStep.setEnabled(true);
                prevStep.setEnabled(true);
                toStart.setEnabled(true);
                toFinish.setEnabled(true);
                newData.setEnabled(true);
                save.setEnabled(true);
                full.setEnabled(true);
                scale.setEnabled(true);
                break;

            case END_ALGORITHM:
                nextStep.setEnabled(false);
                prevStep.setEnabled(true);
                toStart.setEnabled(true);
                toFinish.setEnabled(false);
                newData.setEnabled(true);
                save.setEnabled(true);
                full.setEnabled(true);
                scale.setEnabled(true);
                break;

            default:
                break;
        }
    }

    @Override
    public void setText(String text) {
        info.setText(text);
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }
}
