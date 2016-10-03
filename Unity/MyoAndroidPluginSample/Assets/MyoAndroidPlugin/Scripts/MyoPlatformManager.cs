using UnityEngine;
using System.Collections;

public class MyoPlatformManager : MonoBehaviour 
{

	void Start()
	{
		#if UNITY_ANDROID && !UNITY_EDITOR
		InitializeForAndroid();
		#else
		ThalmicHub.instance.gameObject.SetActive(true);
		InitializeForPC();
		#endif
	}


	private void InitializeForAndroid()
	{
		// Disable ThalmicMyo script on children of HUB
		ThalmicMyo[] myos = ThalmicHub.instance.transform.GetComponentsInChildren<ThalmicMyo> ();
		foreach (ThalmicMyo myo in myos) {
			myo.enabled = false;
		}

		// Disable HUB
		ThalmicHub.instance.enabled = false;

		// Enable MyoAndroidBridge
		MyoAndroidBridge.Instance.gameObject.SetActive(true);
	}


	private void InitializeForPC()
	{
		ThalmicHub.instance.gameObject.SetActive(true);
		MyoAndroidBridge.Instance.gameObject.SetActive (false);
	}
}
