package mkn.controller;

import java.io.File;

public interface GraphConverter {
    /**
     * Конвертация текстового файла в изображение
     * @param textFile текстовый файл с описанием графа
     * @return путь до файла с изображением графа
     */
    String convert(File textFile);
}
