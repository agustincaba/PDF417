import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.util.List;

import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.FaceDetector;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.capture.VideoCapture;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.pdf417.PDF417Reader;

public class TestOpenImaj {

	private static BufferedImage dimage;
	private static int render=0;
	public static void main(String[] args) throws Exception {
		VideoCapture vc=new VideoCapture(1024, 768);
		VideoDisplay<MBFImage> vd=VideoDisplay.createVideoDisplay(vc);
		final FaceDetector<DetectedFace,FImage> fd= new HaarCascadeDetector(40);
		//final FaceDetector<KEDetectedFace, FImage> fd= new FKEFaceDetector();
		
		vd.addVideoListener(new VideoDisplayListener<MBFImage>() {
			
			public void beforeUpdate(MBFImage frame) {
				List<DetectedFace> faces= fd.detectFaces(Transforms.calculateIntensity(frame));
				for(DetectedFace df : faces)  
				{
					frame.drawShape(df.getBounds(), RGBColour.RED);
					
				}
				render++;
				if(render % 60 ==0) {
					try {
						String data = getPDFReader(frame);
						System.out.println(data);
					} catch (NotFoundException e) {
					} catch(Exception e) {
						e.printStackTrace();
						
					}
				}
			}
			
			public void afterUpdate(VideoDisplay<MBFImage> display) {
				// TODO Auto-generated method stub
				
			}
		});

	}
	
	
	public static  String getPDFReader(MBFImage image) throws Exception {
		  
			BufferedImage bufImg = ImageUtilities.createBufferedImageForDisplay(image);
		    final LuminanceSource src= new BufferedImageLuminanceSource(bufImg);
		    final BinaryBitmap bbmap= new BinaryBitmap(new HybridBinarizer(src));
			PDF417Reader pdf=new PDF417Reader();
			Result result = pdf.decode(bbmap);
			return result.getText();
	}
}
