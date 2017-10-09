

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIAlertController;
import org.robovm.apple.uikit.UIAlertControllerStyle;
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
import org.robovm.apple.uikit.UIControl.OnTouchUpInsideListener;

public class MyViewController extends UIViewController {
    private final UIButton button;
    private final UILabel label;
    private int clickCount;

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

        button.addOnTouchUpInsideListener(new OnTouchUpInsideListener() {
			
			@Override
			public void onTouchUpInside(UIControl arg0, UIEvent arg1) {
				int click_count_tmp = ++clickCount;
				label.setText("Click Nr. " + click_count_tmp);
				displayToast("Click Nr. " + click_count_tmp);				
			}
		});

        view.addSubview(button);
    }
    /**
     * Show toast message.
     *
     * @param message message to be displayed.
     */
    void displayToast(String message) {
    	UIAlertController alert = new UIAlertController("Message!",message,UIAlertControllerStyle.Alert);
    	presentViewController(alert, true, null);
        
    	alert.dismissViewController(true, null);
    }
}
