/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package libcore.net.event;

import static android.annotation.SystemApi.Client.MODULE_LIBRARIES;

import android.annotation.SystemApi;
import android.compat.annotation.UnsupportedAppUsage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A singleton used to dispatch network events to registered listeners.
 *
 * @hide
 */
@SystemApi(client = MODULE_LIBRARIES)
public final class NetworkEventDispatcher {

  private static final NetworkEventDispatcher instance = new NetworkEventDispatcher();

  private final List<NetworkEventListener> listeners =
      new CopyOnWriteArrayList<NetworkEventListener>();

  /**
   * Returns the shared {@link NetworkEventDispatcher} instance.
   *
   * @return singleton instance of {@link NetworkEventDispatcher}
   *
   * @hide
   */
  @UnsupportedAppUsage
  @SystemApi(client = MODULE_LIBRARIES)
  public static NetworkEventDispatcher getInstance() {
    return instance;
  }

  /** @hide Visible for testing. Use {@link #getInstance()} instead. */
  public NetworkEventDispatcher() {
  }

  /**
   * Registers a listener to be notified when network events occur.
   * It can be deregistered using {@link #removeListener(NetworkEventListener)}
   *
   * @hide
   */
  @UnsupportedAppUsage
  public void addListener(NetworkEventListener toAdd) {
    if (toAdd == null) {
      throw new NullPointerException("toAdd == null");
    }
    listeners.add(toAdd);
  }

  /**
   * De-registers a listener previously added with {@link #addListener(NetworkEventListener)}. If
   * the listener was not previously registered this is a no-op.
   *
   * @hide
   */
  public void removeListener(NetworkEventListener toRemove) {
    for (NetworkEventListener listener : listeners) {
      if (listener == toRemove) {
        listeners.remove(listener);
        return;
      }
    }
  }

  /**
   * Notifies registered listeners of a network configuration change.
   *
   * @hide
   */
  @SystemApi(client = MODULE_LIBRARIES)
  public void dispatchNetworkConfigurationChange() {
    for (NetworkEventListener listener : listeners) {
      try {
        listener.onNetworkConfigurationChanged();
      } catch (RuntimeException e) {
        System.logI("Exception thrown during network event propagation", e);
      }
    }
  }
}
