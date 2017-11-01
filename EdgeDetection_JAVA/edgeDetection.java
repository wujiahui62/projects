import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class edgeDetection {
	public static void main(String[] args) throws IOException{
		String imageName="d:/test.jpg";
		BufferedImage image = ImageIO.read(new File(imageName));
		
		PixImage test=new PixImage(image.getWidth(),image.getHeight());
		test.sobelEdges(image);
		test.setPixel();//output a new image.
	
	
	}
}