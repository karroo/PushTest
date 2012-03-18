package karroo.app.test;

import android.content.Intent;

public interface ActivityResultDelegate {
	public void onActivityResult(int requestCode, int resultCode, Intent data);
}
