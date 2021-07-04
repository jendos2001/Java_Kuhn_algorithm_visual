import javax.swing.*;

import java.awt.event.*;
import java.net.URL;

public class GUI_MenuBar extends JMenuBar
{

    private  final  String[][]  menuFile =
            {{"Файл"     ,  "Ф",  "", ""},
                    {"Открыть файл"  ,  "О", "O", ""},
                    {"Сохранить изображение",  "С", "S", ""},
                    {"Закончить алгоритм",  "З", "C", ""},
                    {"Закрыть программу",  "З", "X", ""}};

    private  final  String[][]  menuEdit =
            {{"Выбрать отображение" , "В",  "", ""},
                    {"Полное отображение"  , "П", "", ""},
                    {"Масштаб. отображение", "М", "", ""}};


    private JMenu createMenuItems(final String[][] items)
    {
        // Создание выпадающего меню
        JMenu menu = new JMenu(items[0][0]);
        menu.setMnemonic(items[0][1].charAt(0));
        for (int i = 1; i < items.length; i++) {
            // пункт меню "Открыть"
            JMenuItem item = new JMenuItem(items[i][0]);
            item.setMnemonic(items[i][1].charAt(0)); // русская буква
            // установим клавишу быстрого доступа (латинская буква)
            item.setAccelerator(KeyStroke.getKeyStroke(items[i][2].charAt(0), KeyEvent.CTRL_MASK));
            if (items[i][3].length() > 0) {
                item.setIcon(new ImageIcon(items[i][3]));
            }
            menu.add(item);
            if(i == 3){
                menu.addSeparator();
            }
        }
        return menu;
    }

    private JMenu createPictureMenu()
    {
        JMenu picture = new JMenu("Изображение");
        // и несколько вложенных меню
        JMenu choose = new JMenu("Выбрать отображение");
        JRadioButtonMenuItem full = new JRadioButtonMenuItem("Полное отображение", true);
        JRadioButtonMenuItem scale = new JRadioButtonMenuItem("Масштаб. отображение", false);

        ButtonGroup bg = new ButtonGroup();
        bg.add(full);
        bg.add(scale);

        // размещаем все в нужном порядке
        choose.add(full);
        choose.add(scale);
        picture.add(choose);
        return picture;
    }

    public GUI_MenuBar() {

        // Создание меню "Файл"
        this.add(createMenuItems(menuFile));

        // Создание меню "Изображение"
        this.add(createPictureMenu());

        this.add(Box.createHorizontalGlue());
    }
}
