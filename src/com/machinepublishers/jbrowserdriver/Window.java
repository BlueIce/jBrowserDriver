/* 
 * jBrowserDriver (TM)
 * Copyright (C) 2014-2016 Machine Publishers, LLC
 * 
 * Sales and support: ops@machinepublishers.com
 * Updates: https://github.com/MachinePublishers/jBrowserDriver
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.machinepublishers.jbrowserdriver;

class Window implements org.openqa.selenium.WebDriver.Window {
  private final WindowRemote remote;
  private final SocketLock lock;

  Window(WindowRemote remote, SocketLock lock) {
    this.remote = remote;
    this.lock = lock;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public org.openqa.selenium.Point getPosition() {
    try {
      synchronized (lock) {
        return remote.remoteGetPosition().toSelenium();
      }
    } catch (Throwable t) {
      Util.handleException(t);
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public org.openqa.selenium.Dimension getSize() {
    try {
      synchronized (lock) {
        return remote.remoteGetSize().toSelenium();
      }
    } catch (Throwable t) {
      Util.handleException(t);
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void maximize() {
    try {
      synchronized (lock) {
        remote.maximize();
      }
    } catch (Throwable t) {
      Util.handleException(t);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPosition(final org.openqa.selenium.Point point) {
    try {
      synchronized (lock) {
        remote.setPosition(new Point(point));
      }
    } catch (Throwable t) {
      Util.handleException(t);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSize(final org.openqa.selenium.Dimension dimension) {
    try {
      synchronized (lock) {
        remote.setSize(new Dimension(dimension));
      }
    } catch (Throwable t) {
      Util.handleException(t);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void fullscreen() {
    try {
      synchronized (lock) {
        remote.fullscreen();
      }
    } catch (Throwable t) {
      Util.handleException(t);
    }
  }
}
