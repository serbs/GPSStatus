package mgps.app.gpsstatus;

import android.os.Bundle;

public final class TabInfo {
	public final Class<?> clss;
	public final Bundle args;

	public TabInfo(Class<?> _class, Bundle _args) {
		clss = _class;
		args = _args;
	}

}
