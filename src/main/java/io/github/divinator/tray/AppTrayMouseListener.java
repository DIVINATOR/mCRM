package io.github.divinator.tray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Класс описывает слушателя событий мыши при взаимодействии с иконкой приложения в трее.
 */
public class AppTrayMouseListener implements MouseListener {

    private final Logger LOG = LoggerFactory.getLogger(AppTrayMouseListener.class);

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == 1) {
            LOG.info("Show main window");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
