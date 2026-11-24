import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class App {

    ///---  SCREEN HELPDESK
    /// Made By : Fernando Relva
    /// Date : 02/10/26
    /// Github: @r3lva
    /// ---////////////////////////

    private JFrame ventana;
    private PanelPantalla lienzo;
    private BufferedImage frameActual;

    public App() {
        ventana = new JFrame("Screen Helpdesk - Visor HD en Vivo");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(1280, 720);
        ventana.setLocationRelativeTo(null);

        lienzo = new PanelPantalla();
        ventana.add(lienzo, BorderLayout.CENTER);
        ventana.setVisible(true);
    }

    private class PanelPantalla extends JPanel {
        public PanelPantalla() {
            setBackground(Color.BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (frameActual == null) return;

            int panelW = getWidth();
            int panelH = getHeight();
            int imgW = frameActual.getWidth();
            int imgH = frameActual.getHeight();

  
            double escala = Math.min((double) panelW / imgW, (double) panelH / imgH);
            int anchoFinal = (int) (imgW * escala);
            int altoFinal = (int) (imgH * escala);
            int x = (panelW - anchoFinal) / 2;
            int y = (panelH - altoFinal) / 2;

         
            if (anchoFinal == imgW && altoFinal == imgH) {
                g.drawImage(frameActual, x, y, null);
            } else {
                BufferedImage nitida = reducirConAltaNitidez(frameActual, anchoFinal, altoFinal);
                g.drawImage(nitida, x, y, null);
            }
        }
    }


    private BufferedImage reducirConAltaNitidez(BufferedImage img, int targetWidth, int targetHeight) {
        int type = (img.getTransparency() == Transparency.OPAQUE)
                ? BufferedImage.TYPE_INT_RGB
                : BufferedImage.TYPE_INT_ARGB;

        BufferedImage ret = img;
        int w = img.getWidth();
        int h = img.getHeight();

        while (w > targetWidth || h > targetHeight) {
            if (w > targetWidth) {
                w = Math.max(w / 2, targetWidth);
            }
            if (h > targetHeight) {
                h = Math.max(h / 2, targetHeight);
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        }
        return ret;
    }

    public void cargarDesdeBinar(String datosBase64) {
        if (datosBase64 == null || datosBase64.isEmpty()) return;

        try {
            byte[] bytesImagen = Base64.getDecoder().decode(datosBase64);
            try (ByteArrayInputStream bais = new ByteArrayInputStream(bytesImagen)) {
                BufferedImage img = ImageIO.read(bais);
                if (img != null) {
                    frameActual = img;
                    lienzo.repaint(); 
                }
            }
        } catch (Exception e) {
            System.err.println("Error al actualizar frame: " + e.getMessage());
        }
    }

    public void iniciarStreaming(int fps) {
        int pausaMs = 1000 / fps;

        Thread hiloCaptura = new Thread(() -> {
            while (ventana.isDisplayable()) {
                try {
                    String datos = ScreenModule.binar();
                    cargarDesdeBinar(datos);
                    Thread.sleep(pausaMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        hiloCaptura.setDaemon(true);
        hiloCaptura.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.iniciarStreaming(10);
        });
    }
}