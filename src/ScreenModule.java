
    import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Base64;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ScreenModule {
    ///--- GET PHOTO VIA BINARY
    /// Made By : Fernando Relva
    /// Date : 02/10/26
    /// Github: @r3lva
    /// ---////////////////////////
    




public static byte[] tomarCaptura() {
        try {
            Dimension tamanoPantalla = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle areaCaptura = new Rectangle(tamanoPantalla);
            Robot robot = new Robot();
            BufferedImage imagen = robot.createScreenCapture(areaCaptura);

            try (ByteArrayOutputStream bufferRAM = new ByteArrayOutputStream()) {
                ImageIO.write(imagen, "png", bufferRAM);
                return bufferRAM.toByteArray();
            }
        } catch (Exception e) {
            System.err.println("Error al capturar: " + e.getMessage());
            return null;
        }
    }

public ScreenModule() {
    }

public static String binar() {
    String linea = "";
    byte[] captura = tomarCaptura();
    if (captura == null) {
        System.err.println("No se pudo obtener la captura.");
        return linea;
    }
    File flDat = new File("prueba.dat");
    File flJpg = new File("prueba2.jpg");
    try (FileOutputStream fpDat = new FileOutputStream(flDat);
         FileOutputStream fpJpg = new FileOutputStream(flJpg)) {
        
        System.out.println("Reescribiendo imagen en bytes...");
        fpDat.write(captura);
        fpJpg.write(captura);
        
    } catch (Exception e) {
        System.err.println("Error al escribir archivos: " + e.getMessage());
    }

    linea = Arrays.toString(captura);
    return Base64.getEncoder().encodeToString(captura);
}


}
