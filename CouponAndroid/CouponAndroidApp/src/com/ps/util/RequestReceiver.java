/**
 * this interface exists just to allow the WebserviceHelper to make callbacks.
 */

package com.ps.util;

public interface RequestReceiver {
	void requestFinished(String result);
}
