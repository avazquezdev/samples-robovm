
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Hashtable;

import org.robovm.apple.avfoundation.AVAuthorizationStatus;
import org.robovm.apple.avfoundation.AVCaptureConnection;
import org.robovm.apple.avfoundation.AVCaptureDevice;
import org.robovm.apple.avfoundation.AVCaptureDeviceInput;
import org.robovm.apple.avfoundation.AVCaptureDevicePosition;
import org.robovm.apple.avfoundation.AVCaptureMetadataOutput;
import org.robovm.apple.avfoundation.AVCaptureMetadataOutputObjectsDelegate;
import org.robovm.apple.avfoundation.AVCaptureMetadataOutputObjectsDelegateAdapter;
import org.robovm.apple.avfoundation.AVCaptureOutput;
import org.robovm.apple.avfoundation.AVCaptureSession;
import org.robovm.apple.avfoundation.AVCaptureSessionPreset;
import org.robovm.apple.avfoundation.AVCaptureVideoDataOutput;
import org.robovm.apple.avfoundation.AVCaptureVideoDataOutputSampleBufferDelegateAdapter;
import org.robovm.apple.avfoundation.AVCaptureVideoOrientation;
import org.robovm.apple.avfoundation.AVCaptureVideoPreviewLayer;
import org.robovm.apple.avfoundation.AVLayerVideoGravity;
import org.robovm.apple.avfoundation.AVMediaType;
import org.robovm.apple.avfoundation.AVMetadataObject;
import org.robovm.apple.avfoundation.AVMetadataObjectType;
import org.robovm.apple.avfoundation.AVPixelBufferAttributes;
import org.robovm.apple.coregraphics.CGBitmapContext;
import org.robovm.apple.coregraphics.CGDataProvider;
import org.robovm.apple.coregraphics.CGImage;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.coreimage.CIContext;
import org.robovm.apple.coreimage.CIImage;
import org.robovm.apple.coremedia.CMSampleBuffer;
import org.robovm.apple.corevideo.CVImageBuffer;
import org.robovm.apple.corevideo.CVPixelBuffer;
import org.robovm.apple.corevideo.CVPixelFormatType;
import org.robovm.apple.dispatch.DispatchQueue;
import org.robovm.apple.dispatch.DispatchQueueAttr;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSErrorException;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIInterfaceOrientation;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

public class MyViewController extends UIViewController {

	private static final String DISPATCH_QUEUE_NAME = "cameraQueue";

	private UIViewController cameraPreviewViewController;

	UILabel resultLabel;
	
	public MyViewController() {
		// Get the view of this view controller.
		UIView view = getView();

		// Setup background.
		view.setBackgroundColor(UIColor.white());

		CameraMobileViewAdapterImpl adapter;
		UIView cameraPreviewView = new UIView();
		adapter = new CameraMobileViewAdapterImpl(cameraPreviewView);
		if (adapter.initializeCameraDevice()) {

			UIViewController cameraPreviewViewController = new UIViewController() {
				@Override
				public void viewWillLayoutSubviews() {
					super.viewWillLayoutSubviews();
					adapter.updateViewPreviewLayerOrientation();
				}

				@Override
				public void willAnimateRotation(UIInterfaceOrientation uiInterfaceOrientation, double v) {
					// Unfortunately this method is deprecated and there isn't binding in RoboVM for
					// any of these replacements...
					// - willTransitionToTraitCollection:withTransitionCoordinator:
					// - viewWillTransitionToSize:withTransitionCoordinator:
					super.willAnimateRotation(uiInterfaceOrientation, v);
					adapter.updateViewPreviewLayerOrientation();
				}
			};

			cameraPreviewViewController.setView(cameraPreviewView);
			// new CGRect(10, 100, 300, 300)
			cameraPreviewView.setFrame(view.getBounds());
			view.addSubview(cameraPreviewView);
			view.addSubview(cameraPreviewViewController.getView());
			try {
				adapter.startPreview();
			} catch (NSErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
			Foundation.log("No cameras.");
		
		resultLabel = new UILabel(new CGRect(0, 0, 500, 80));
		resultLabel.setText("Test");	
		resultLabel.setFont(UIFont.getSystemFont(24));
		view.addSubview(resultLabel);
	}

	public class CameraMobileViewAdapterImpl {

		private final UIView view;
		private AVCaptureVideoPreviewLayer previewLayer;
		private AVCaptureSession session;
		private AVCaptureDevice cameraDevice;
		private CaptureMetadataOutputDelegate captureDelegate;
		private AVCaptureMetadataOutput captureMetadataOutput;


		public CameraMobileViewAdapterImpl(UIView view) {
			this.view = view;
	}

		private boolean initializeCameraDevice() {
			cameraDevice = AVCaptureDevice.getDefaultDeviceForMediaType(AVMediaType.Video);

			if (cameraDevice != null)
				return true;

			NSArray<AVCaptureDevice> captureDevices = AVCaptureDevice.getDevicesForMediaType(AVMediaType.Video);

			for (AVCaptureDevice captureDevice : captureDevices) {
				// We are only interested in the back camera
				if (captureDevice.getPosition() == AVCaptureDevicePosition.Back) {
					// We found back camera! Yuhu!!
					cameraDevice = captureDevice;
					return true;
				}
			}

			return false;
		}

		private AVCaptureVideoOrientation getOrientationForCameraPreviewLayer() {
			switch (UIApplication.getSharedApplication().getStatusBarOrientation()) {
			case LandscapeLeft:
				return AVCaptureVideoOrientation.LandscapeLeft;
			case LandscapeRight:
				return AVCaptureVideoOrientation.LandscapeRight;
			case Portrait:
				return AVCaptureVideoOrientation.Portrait;
			case PortraitUpsideDown:
				return AVCaptureVideoOrientation.PortraitUpsideDown;
			default:
				return AVCaptureVideoOrientation.LandscapeLeft;
			}
		}

		private void updateViewPreviewLayerOrientation() {
			if (previewLayer != null)
				previewLayer.getConnection().setVideoOrientation(getOrientationForCameraPreviewLayer());
		}

		public void startPreview() throws NSErrorException {

			if (initializeCameraDevice())
				Foundation.log("Back camera found");
			else {
				Foundation.log("No cameras.");
				return;
			}
			AVCaptureDeviceInput input = new AVCaptureDeviceInput(cameraDevice);

			AVCaptureVideoDataOutput videoDataOutput = new AVCaptureVideoDataOutput();

			
			DispatchQueue queue = DispatchQueue.create("cameraQueue", DispatchQueueAttr.Serial());
			videoDataOutput.setSampleBufferDelegate(new BufferDelegate(), queue);

			captureMetadataOutput = new AVCaptureMetadataOutput();
			captureDelegate = new CaptureMetadataOutputDelegate(view);
	        captureMetadataOutput.setMetadataObjectsDelegate(captureDelegate, queue);

	        

			AVPixelBufferAttributes attributes = new AVPixelBufferAttributes();
			attributes.setPixelFormatType(CVPixelFormatType._32BGRA);// _32BGRA,_32ABGR
			videoDataOutput.setPixelBufferSettings(attributes);

			session = new AVCaptureSession();
			//session.addOutput(videoDataOutput);
			session.addInput(input);
			session.setSessionPreset(AVCaptureSessionPreset.Size640x480);

			if(session.canAddOutput(captureMetadataOutput)) session.addOutput(captureMetadataOutput);
	        captureMetadataOutput.setMetadataObjectTypes(Arrays.asList(AVMetadataObjectType.QRCode));
	        
			previewLayer = new AVCaptureVideoPreviewLayer(session);
			previewLayer.setVideoGravity(AVLayerVideoGravity.ResizeAspectFill);

			CGSize size = view.getFrame().getSize();
			previewLayer.setFrame(new CGRect(0, 0, size.getWidth(), size.getHeight()));

			AVCaptureConnection connection = previewLayer.getConnection();
			connection.setVideoOrientation(AVCaptureVideoOrientation.LandscapeRight);

			view.getLayer().addSublayer(previewLayer);
			session.startRunning();

		}

		protected UIView getView() {
			return view;
		}

		private class BufferDelegate extends AVCaptureVideoDataOutputSampleBufferDelegateAdapter {
			private CIImage ciImage;
			private CIContext context;
			private CGImage image;
			private byte[] bytes;

			@Override
			public void didOutputSampleBuffer(AVCaptureOutput output, CMSampleBuffer mBuffer,
					AVCaptureConnection connection) {

				// Foundation.log("SampleBuffer");

				try {

					CVPixelBuffer buffer = mBuffer.getPixelBuffer();
					// Get image buffer and lock base address
					CVImageBuffer imageBuffer = mBuffer.getImageBuffer();// CMSampleBufferGetImageBuffer(mBuffer);
					// imageBuffer.g//CVPixelBufferLockBaseAddress(imageBuffer, 0);

					// Get image buffer informations
					// final int baseAddress = (int)
					// imageBuffer.;//VPixelBufferGetBaseAddress(imageBuffer);
					final long width = getUnsignedLong(buffer.getWidth());// CVPixelBufferGetWidth(imageBuffer));
					final long height = getUnsignedLong(buffer.getHeight());// CVPixelBufferGetHeight(imageBuffer));
					final long bytesPerRow = getUnsignedLong(buffer.getBytesPerRow());// CVPixelBufferGetBytesPerRow(imageBuffer));
					final long bytesPerPixel = bytesPerRow / width;
					final long size = width * height * bytesPerPixel;

					ciImage = new CIImage(buffer);
					context = new CIContext();

					image = context.createCGImage(ciImage, new CGRect(0, 0, buffer.getWidth(), buffer.getHeight()));

					bytes = image.getDataProvider().getData().getBytes();

					/*long[] arry = buffer.getExtendedPixels();
					for (int i = 0; i < arry.length; ++i)
						pixels[i] = (int) arry[i];

					int maxY = height;
					for (int y = 0; y < maxY; y++) {
						for (int x = 0; x < width; x++) {
							int offset = bytesPerPixel * ((width * y) + x);
							pixels2[offset] = pixels[offset + 2]; // R
							pixels2[offset + 1] = pixels[offset + 1]; // G
							pixels2[offset + 2] = pixels[offset]; // B
							pixels2[offset + 3] = pixels[offset + 3]; // A
						}
					}
					RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);*/

					 LuminanceSource source = new PlanarYUVLuminanceSource(bytes, (int)
					 image.getWidth(),
					 (int) image.getHeight(), 0, 0, (int) image.getWidth(), (int)
					 image.getHeight(), false);
					BinaryBitmap bbitmap = new BinaryBitmap(new HybridBinarizer(source));
					Result result;
					Hashtable<DecodeHintType, Object> decodeHints = new Hashtable<>();
					decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
					decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);

					try {
						result = new MultiFormatReader().decodeWithState(bbitmap);
						//Foundation.log(result.getText());
						//resultLabel.setText(result.getText());
					} catch (NotFoundException e) {
						//Foundation.log("Unable to Decode");
						//resultLabel.setText("Unable to Decode");
					}

				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

			@Override
			public void didDropSampleBuffer(AVCaptureOutput arg0, CMSampleBuffer arg1, AVCaptureConnection arg2) {
				// System.out.println("drop"); //$NON-NLS-1$
			}
		}
	}

	private static long getUnsignedLong(long x) {
		return x & 0xffffffffL;
	}

	private class CaptureMetadataOutputDelegate extends AVCaptureMetadataOutputObjectsDelegateAdapter {
		private final UIView view;
		public CaptureMetadataOutputDelegate(UIView view) {
			this.view= view;

		}

		@Override
		public void didOutputMetadataObjects(AVCaptureOutput captureOutput, NSArray<AVMetadataObject> metadataObjects,
				AVCaptureConnection connection) {
			if (metadataObjects != null && metadataObjects.size() > 0) {
				AVMetadataObject data = metadataObjects.get(0);
				if (data.getType() == AVMetadataObjectType.QRCode) {
					// Found QR Code TODO - change data.toString()
					Foundation.log("Code: " + data.getKeyValueCoder().getValues("stringValue").toString());
					//label.setText(data.getKeyValueCoder().getValues("stringValue").toString());
					resultLabel.setText(data.getKeyValueCoder().getValues("stringValue").get("stringValue").toString());
					view.addSubview(resultLabel);
				}

				metadataObjects.dispose();
			}
		}
	}

}
