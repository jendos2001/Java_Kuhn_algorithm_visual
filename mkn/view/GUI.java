package mkn.view;

import mkn.model.*;
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
    private JMenu picture = new JMenu("Изображение");                                                       //Кнопки меню
    private JMenuItem save = new JMenuItem("Сохранить изображение");
    private JMenu choose = new JMenu("Выбрать отображение");
    private JRadioButtonMenuItem full = new JRadioButtonMenuItem("Полное отображение", true);
    private JRadioButtonMenuItem scale = new JRadioButtonMenuItem("Масштаб. отображение", false);

    private JButton newData = new JButton("Новые данные");                                               //Кнопки на главном окне
    private JButton toStart = new JButton("В начало");
    private JButton toFinish = new JButton("В конец");
    private JButton nextStep = new JButton("Следующий шаг");
    private JButton prevStep = new JButton("Предыдущий шаг");
    private JTextArea info = new JTextArea("Здесь будет ваша реклама гаража, но только после того, как вы мне заплатите");

    private JLabel imageLabel = new JLabel();                                                                //Поля для хранения изображения
    private JScrollPane imageScroll = new JScrollPane(imageLabel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    private Image image;

    private Controller controller;                                                                           //Контроллер для отправки сигналов пользователя

    private enum State { NO_DATA,                                                                            //Состояния приложения в зависимости от итерации алгоритма
                        START_ALGORITHM,
                        MIDDLE_ALGORITHM,
                        END_ALGORITHM};
    private State state = State.NO_DATA;


    public GUI(){
        super("Алгоритм Куна");                             //Название окна
        this.setJMenuBar(makeMenuBar());                         //Добавление меню
        this.setLayout(new GridBagLayout());
        this.setBounds(100, 100, 800, 800);    //Размеры и начальное расположение окна
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(600, 600));

        checkState();
        makeWindowWithWidgets();
    }

    private void makeWindowWithWidgets(){      //Добавления и расстановка виджетов
        GridBagConstraints constraints = new GridBagConstraints();   //Конструктор для расположения виджетов в окне

        constraints.fill = GridBagConstraints.BOTH;                  //Политика размеров виджета (в данном заполнение во все стороны)
        constraints.weightx = 0.8;                                   //Отведенный процент по х для данного виджета
        constraints.gridy = 0;                                       //Расположение по у, начиная сверху
        constraints.gridx = 0;                                       //Расположение по х, начиная слева
        constraints.gridheight = 8;                                  //Количество занимаемых ячеек по высоте
        this.add(imageScroll, constraints);                          //Добавление виджета в окно

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;               //Расположение виджета относительно выделенного для него места (в данном случае сверху)
        constraints.weightx = 0.2;
        constraints.gridy = 0;
        constraints.gridx = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 30, 5);  //Отступы вокруг данного виджета
        newData.addActionListener(new ActionListener() {                    //Добавление реакции на нажатие
            public void actionPerformed(ActionEvent e) {
                newDataAction();
            }
        });
        this.add(newData, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.weightx = 0.2;
        constraints.gridy = 1;
        constraints.gridx = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        toStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.toStart();                                      //Отправка сигнала об нажатии контроллеру
                state = State.START_ALGORITHM;                             //Изменение состояния окна
                checkState();
            }
        });
        this.add(toStart, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.weightx = 0.2;
        constraints.gridy = 2;
        constraints.gridx = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 30, 5);
        toFinish.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.toFinish();
                state = State.END_ALGORITHM;
                checkState();
            }
        });
        this.add(toFinish, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.weightx = 0.2;
        constraints.gridy = 3;
        constraints.gridx = 1;
        constraints.gridheight = 1;
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

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.weightx = 0.2;
        constraints.gridy = 4;
        constraints.gridx = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
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

        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.weighty = 0.2;
        constraints.gridy = 5;
        constraints.gridx = 1;
        constraints.gridheight = 3;
        constraints.insets = new Insets(5, 5, 5, 5);
        info.setPreferredSize(new Dimension(140, 140));
        info.setLineWrap(true);                //Активация переноса текста
        info.setWrapStyleWord(true);           //Перенос по словам
        this.add(info, constraints);
    }

    private void newDataAction(){ //Реакция на "Новые данные"
//        controller.deleteOld();                      //Сигнал об обнулении текущих данных
//        state = State.NO_DATA;                       //Изменение состояния
//        imageLabel.setIcon(new ImageIcon());         //Убирается картинка
        JFileChooser fileOpen = new JFileChooser();  //Диалоговое окно для открытия текстового файла с данным
        fileOpen.setFileFilter(new FileNameExtensionFilter("Текстовый файл, *.txt", "txt"));
        fileOpen.setAcceptAllFileFilterUsed(false);
        int ret = fileOpen.showDialog(null, "Открыть файл");
        if (ret == JFileChooser.APPROVE_OPTION) {
            System.out.println("Файл выбран");
            File file = fileOpen.getSelectedFile();

/*            if(controller.getNewData(file.getAbsolutePath())){
                state = State.START_ALGORITHM;
            };*/
        }
        else{
            System.out.println("Файл не выбран");
        }
//        checkState();
    }

    private JMenuBar makeMenuBar(){    //Инициализация меню
        ButtonGroup bg = new ButtonGroup();
        bg.add(full);
        bg.add(scale);

        // размещаем все в нужном порядке
        choose.add(full);
        choose.add(scale);
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
/*                if (image.getHeight(null) != -1) {           //исправить
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
                }*/
            }
        });
        picture.add(save);
        picture.add(choose);

        JMenuBar bar = new JMenuBar();
        bar.add(picture);
        bar.add(Box.createHorizontalGlue());
        return bar;
    }

    private void checkState(){ //Анализ состояния для контроля активных кнопок
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
    public void setImage(String path) { //Изменение изображения
        File file = new File(path);   //Инициализация файла
        try {
            image = ImageIO.read(file); //Чтение изображения
            imageLabel.setIcon(new ImageIcon(image));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        imageLabel.setPreferredSize(new Dimension(image.getWidth(null), image.getHeight(null)));  //Настройки размеров изображения для работы полосок прокрутки
        imageScroll.setPreferredSize(imageLabel.getSize());
        imageScroll.revalidate();
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }
}
