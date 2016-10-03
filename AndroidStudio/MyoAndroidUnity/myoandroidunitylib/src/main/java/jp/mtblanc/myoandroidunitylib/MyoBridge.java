package jp.mtblanc.myoandroidunitylib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;
import com.thalmic.myo.XDirection;
import com.unity3d.player.UnityPlayer;

import java.util.ArrayList;

/**
 * Created by shiyamon on 16/09/16.
 */
public class MyoBridge
{
    private static String _callbackObj;
    private static String _orientationCBMethod;
    private static String _poseCBMethod;
    private static String _connectCBMethod;
    private static String _disconnectCBMethod;


    public static boolean InitMyo(Activity activity, boolean lockingPolicy, int myoAttachAllownace)
    {
        Hub hub = Hub.getInstance();

        if( hub.init(activity)) {
            hub.setLockingPolicy( lockingPolicy ? Hub.LockingPolicy.STANDARD : Hub.LockingPolicy.NONE);
            hub.setMyoAttachAllowance(myoAttachAllownace > 0 ? myoAttachAllownace : 1);
            Hub.getInstance().addListener(_hubListener);

            return true;
        }
        else
            return false;
    }


    public static void SetListener(
            String callbackObjName,
            String orientationCallback,
            String poseCallback,
            String connectCallback,
            String disconnectCallback
            )
    {
        _callbackObj = callbackObjName;
        _orientationCBMethod = orientationCallback;
        _poseCBMethod = poseCallback;
        _connectCBMethod = connectCallback;
        _disconnectCBMethod = disconnectCallback;
    }


    public static void ConnectMyoWithAddress(String macAddress)
    {
        Hub.getInstance().attachByMacAddress(macAddress);
    }


    public static String GetConnectedMyo()
    {
        String result = "";
        ArrayList<Myo> myoList = Hub.getInstance().getConnectedDevices();
        for (Myo myo : myoList) {
            result += myo.getMacAddress() + " ";
        }

        return result;
    }

    public static void StartScanActivity(Context context)
    {
        Intent intent = new Intent(context, com.thalmic.myo.scanner.ScanActivity.class);
        context.startActivity(intent);
    }


    public static void LockMyo(String macAddress)
    {
        ArrayList<Myo> myoList = Hub.getInstance().getConnectedDevices();
        for (Myo myo : myoList) {
            if(myo.getMacAddress() == macAddress)
                myo.lock();
        }
    }


    public static void UnlockMyo(String macAddress)
    {
        ArrayList<Myo> myoList = Hub.getInstance().getConnectedDevices();
        for (Myo myo : myoList) {
            if(myo.getMacAddress() == macAddress)
                myo.unlock(Myo.UnlockType.HOLD);
        }
    }


    private static DeviceListener _hubListener = new AbstractDeviceListener() {

        @Override public void onConnect(Myo myo, long timestamp)
        {
            if(_callbackObj == "" || _connectCBMethod == "") return;
            UnityPlayer.UnitySendMessage(_callbackObj, _connectCBMethod, myo.getMacAddress());
        }

        @Override public void onDisconnect(Myo myo, long timestamp)
        {
            if(_callbackObj == "" || _disconnectCBMethod == "") return;
            UnityPlayer.UnitySendMessage(_callbackObj, _disconnectCBMethod, myo.getMacAddress());
        }

        @Override public void onOrientationData(Myo myo, long timestamp, Quaternion rotation)
        {
            if(_callbackObj == "" || _orientationCBMethod == "")  return;

            String msg = String.format("%s %f %f %f %f", myo.getMacAddress(), rotation.x(), rotation.y(), rotation.z(), rotation.w());

            UnityPlayer.UnitySendMessage(_callbackObj, _orientationCBMethod, msg);
        }

        @Override public void onPose(Myo myo, long timestamp, Pose pose)
        {
            Log.d("AND_POSE", pose.toString());

            if(_callbackObj == "" || _poseCBMethod == "")  return;

            String myoAddress = myo.getMacAddress();
            String poseStr = pose.toString();
            String msg = String.format("%s %s", myoAddress, poseStr);

            UnityPlayer.UnitySendMessage(_callbackObj, _poseCBMethod, msg);
        }

        @Override public void onGyroscopeData (Myo myo, long timestamp, Vector3 gyro) {}
        @Override public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {}
        @Override public void onArmUnsync(Myo myo, long timestamp) {}
        @Override public void onUnlock(Myo myo, long timestamp){}
        @Override public void onLock(Myo myo, long timestamp) {}
    };
}
