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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

class AjaxListener implements Runnable {
  private static final long MAX_WAIT_DEFAULT = 15000;
  private static final int IDLE_COUNT_TARGET = 3;
  private final Integer newStatusCode;
  private final AtomicInteger statusCode;
  private final Map<String, Long> resources;
  private final long timeoutMS;

  AjaxListener(final int newStatusCode,
      final AtomicInteger statusCode,
      final Map<String, Long> resources, final long timeoutMS) {
    this.newStatusCode = newStatusCode;
    this.statusCode = statusCode;
    this.resources = resources;
    this.timeoutMS = timeoutMS <= 0 ? MAX_WAIT_DEFAULT : timeoutMS;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    int size = 0;
    final long start = System.currentTimeMillis();
    long time = start;
    final long sleepMS = Math.max(SettingsManager.settings().ajaxWait() / IDLE_COUNT_TARGET, 0);
    int idleCount = 0;
    int idleCountTarget = sleepMS == 0 ? 1 : IDLE_COUNT_TARGET;
    while (time - start < timeoutMS) {
      try {
        Thread.sleep(sleepMS);
      } catch (InterruptedException e) {
        return;
      }
      time = System.currentTimeMillis();
      synchronized (statusCode) {
        if (Thread.interrupted()) {
          return;
        }
        final Set<String> remove = new HashSet<String>();
        for (Map.Entry<String, Long> entry : resources.entrySet()) {
          if (time - entry.getValue() > SettingsManager.settings().ajaxResourceTimeout()) {
            remove.add(entry.getKey());
          }
        }
        for (String key : remove) {
          resources.remove(key);
        }
        size = resources.size();
      }
      if (size == 0) {
        ++idleCount;
      } else {
        idleCount = 0;
      }
      if (idleCount == idleCountTarget) {
        break;
      }
    }
    synchronized (statusCode) {
      if (Thread.interrupted()) {
        return;
      }
      if (newStatusCode == null) {
        resources.clear();
        statusCode.set(200);
        statusCode.notifyAll();
      } else {
        resources.clear();
        statusCode.set(newStatusCode);
        statusCode.notifyAll();
      }
    }
  }
}