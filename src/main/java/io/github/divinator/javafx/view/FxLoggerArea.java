package io.github.divinator.javafx.view;

import javafx.scene.control.TextArea;

import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * <h2>Класс описывает форму для вывода логов приложения.</h2>
 */
public class FxLoggerArea extends PrintStream {

    private static FxLoggerArea init;

    /**
     * Метод включает вывод логов приложения в компонент ввода текста.
     *
     * @param textArea Компонент ввода текста.
     * @return Объект формы для вывода логов приложения.
     */
    public static FxLoggerArea in(TextArea textArea) {
        if (init == null) {
            init = new FxLoggerArea(textArea);
        }
        return init;
    }

    private FxLoggerArea(TextArea textArea) {
        this(new LogStream(textArea));
    }

    /**
     * Creates a new print stream.  This stream will not flush automatically.
     *
     * @param out The output stream to which values and objects will be
     *            printed
     * @see PrintWriter#PrintWriter(OutputStream)
     */
    private FxLoggerArea(OutputStream out) {
        super(out);
        onLog();
    }

    /**
     * Метод переводит вывод лога в указанную ранее форму.
     */
    private void onLog() {
        System.setOut(this);
        System.setErr(this);
    }

    private static class LogStream extends PipedOutputStream {

        private final TextArea textArea;

        /**
         * <h2>Конструктор потока обработки логов.</h2>
         *
         * @param textArea Компонент ввода текста.
         */
        public LogStream(TextArea textArea) {
            textArea.getStyleClass().add("logger");
            this.textArea = textArea;
        }

        @Override
        public void write(int i) {
            textArea.appendText(String.valueOf((char) i));
        }

        @Override
        public void write(byte[] bytes, int i, int i1) {
            textArea.appendText(new String(bytes, i, i1));
        }
    }
}
