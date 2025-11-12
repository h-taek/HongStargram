package Resize;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Resize extends JFrame {
    public static BufferedImage resizeImage(String path, int newW, int newH, float alpha) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage();

        // BufferedImage 생성 (ARGB는 투명도 포함)
        BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = resized.createGraphics();
        // 이미지를 리사이즈할 때, 픽셀 사이의 색을 계산하는 방법을 지정
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        if (alpha < 0f) alpha = 0f;
        if (alpha > 1f) alpha = 1f;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        
        g2.drawImage(img, 0, 0, newW, newH, null);
        g2.dispose();

        return resized; // 리사이즈된 이미지 객체 반환
    }
}