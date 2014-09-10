using UnityEngine;
using System.Collections;
using KiiCorp.Cloud.Unity;

public class DemoInitializeBehaviour : KiiInitializeBehaviour {
	public override void Awake()
	{
		AppID = "f39c2d34";
		AppKey = "2e98ef0bb78a58da92f9ac0709dc99ed";
		Site = KiiCorp.Cloud.Storage.Kii.Site.JP;
		base.Awake();
	}
}
