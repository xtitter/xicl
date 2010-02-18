package ru.icl.dicewars.gui.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.border.Border;

public class RoundedBorder implements Border {

    private final Color background;
    private final Color outline;
    private final Color foreground;
    private final int radius;
    private final int stroke;

    public RoundedBorder(Color background, Color outline, Color foreground, int radius, int stroke) {
        this.background = background;
        this.outline = outline;
        this.foreground = foreground;
        this.radius = radius;
        this.stroke = stroke;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D)g.create();
        //g2d.addRenderingHints(RenderingHints. Icons.RENDERING_HINTS);
        int diameter = radius * 2;

        // background in corners
        g2d.setColor(background);
        //g2d.fillRect(0,              0,               radius, radius);
        //g2d.fillRect(width - radius, 0,               radius, radius);
        //g2d.fillRect(0,              height - radius, radius, radius);
        //g2d.fillRect(width - radius, height - radius, radius, radius);

        // fill corners
        int arcLeft = 0;
        int arcTop = 0;
        int arcRight = width - diameter - 1;
        int arcBottom = height - diameter - 1;
        int arcDiameter = diameter;
        int sideWidth = width - diameter; // + 1;
        int sideHeight = height - diameter; // + 1;
        g2d.setColor(foreground);
        g2d.fillArc(arcLeft,  arcTop,    arcDiameter, arcDiameter, 90, 90);
        g2d.fillArc(arcRight, arcTop,    arcDiameter, arcDiameter, 0, 90);
        g2d.fillArc(arcLeft,  arcBottom, arcDiameter, arcDiameter, 180, 90);
        g2d.fillArc(arcRight, arcBottom, arcDiameter, arcDiameter, 270, 90);
       
        // fill sides
        g2d.fillRect(radius,         0,               sideWidth - 1, radius);
        g2d.fillRect(radius,         height - radius, sideWidth - 1, radius);
        g2d.fillRect(0,              radius,          radius,    sideHeight - 1);
        g2d.fillRect(width - radius, radius,          radius,    sideHeight - 1);

        // prepare the arc lines
        if(stroke > 0) {
            g2d.setColor(outline);
            g2d.setStroke(new BasicStroke(stroke));
            int halfStroke = (stroke) / 2;

            // stroke corners
            int strokeDiameter = diameter - stroke;
            int leftStroke = halfStroke;
            int rightStroke = width - diameter + halfStroke;
            int topStroke = halfStroke;
            int bottomStroke = height - diameter + halfStroke;
            g2d.drawArc(leftStroke,  topStroke,    strokeDiameter, strokeDiameter, 90, 90);
            g2d.drawArc(rightStroke, topStroke,    strokeDiameter, strokeDiameter, 0,  90);
            g2d.drawArc(leftStroke,  bottomStroke, strokeDiameter, strokeDiameter, 180, 90);
            g2d.drawArc(rightStroke, bottomStroke, strokeDiameter, strokeDiameter, 270, 90);

            // stroke sides
            int sideBottom = height - stroke;
            int sideTop = 0;
            int sideLeft = 0;
            int sideRight = width - stroke;
            g2d.fillRect(radius,    sideTop,    sideWidth, stroke);
            g2d.fillRect(radius,    sideBottom, sideWidth, stroke);
            g2d.fillRect(sideLeft,  radius,     stroke,    sideHeight);
            g2d.fillRect(sideRight, radius,     stroke,    sideHeight);
        }
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(radius, radius, radius, radius);
    }

    public boolean isBorderOpaque() {
        return false;
    }
}