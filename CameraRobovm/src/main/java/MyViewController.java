

import org.robovm.apple.avfoundation.AVAuthorizationStatus;
import org.robovm.apple.avfoundation.AVCaptureDevice;
import org.robovm.apple.avfoundation.AVCaptureDeviceInput;
import org.robovm.apple.avfoundation.AVCaptureDevicePosition;
import org.robovm.apple.avfoundation.AVCaptureSession;
import org.robovm.apple.avfoundation.AVMediaType;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSErrorException;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;

public class MyViewController extends UIViewController {
    private final UIButton button;
    private final UILabel label;
    private int clickCount;
    
    private AVCaptureDevice cameraDevice;
	private AVCaptureSession captureSession;
    private AVCaptureDeviceInput captureDeviceInput;

    public MyViewController() {
        // Get the view of this view controller.
        UIView view = getView();

        // Setup background.
        view.setBackgroundColor(UIColor.white());

        // Setup label.
        label = new UILabel(new CGRect(20, 250, 280, 44));
        label.setFont(UIFont.getSystemFont(24));
        label.setTextAlignment(NSTextAlignment.Center);
        view.addSubview(label);

        // Setup button.
        button = new UIButton(UIButtonType.RoundedRect);
        button.setFrame(new CGRect(110, 150, 100, 40));
        button.setTitle("Click me!", UIControlState.Normal);
        button.getTitleLabel().setFont(UIFont.getBoldSystemFont(22));

        button.addOnTouchUpInsideListener((control, event) -> label.setText("Click Nr. " + (++clickCount)));
        view.addSubview(button);
        
        if(initializeCameraDevice())
			Foundation.log("Back camera found");
		else
			Foundation.log("No cameras.");
        
        onCameraPreviewStarted();
        
        
    }
    private boolean initializeCameraDevice() {
		cameraDevice = AVCaptureDevice.getDefaultDeviceForMediaType(AVMediaType.Video);

        if(cameraDevice != null) return true;
        
        NSArray<AVCaptureDevice> captureDevices = AVCaptureDevice.getDevicesForMediaType(AVMediaType.Video);
        
        for(AVCaptureDevice captureDevice : captureDevices) {
        	//We are only interested in the back camera
            if(captureDevice.getPosition() == AVCaptureDevicePosition.Back) {
                //We found back camera! Yuhu!!
                cameraDevice = captureDevice;
                return true;
            }
        }

        return false;
    }
    public void onCameraPreviewStarted() {
    		captureSession = new AVCaptureSession();
    		
    		try {
				captureDeviceInput = new AVCaptureDeviceInput(cameraDevice);
			} catch (NSErrorException e) {
				//e.printStackTrace();
				Foundation.log(e.getMessage());
			}
    	
    }
}
