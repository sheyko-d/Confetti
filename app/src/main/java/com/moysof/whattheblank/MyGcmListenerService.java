/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moysof.whattheblank;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String type = data.getString("type");
        Integer assignedNumber = 0;
        if (data.containsKey("assigned_number")) {
            assignedNumber = Integer.parseInt(data.getString("assigned_number"));
        }

        Util.Log("messageReceived = " + data.toString() + "( number = " + assignedNumber + ")");

        if (type != null) {
            if (type.equals(HostLobbyActivity.TYPE_JOINED_GAME)) {
                sendBroadcast(new Intent(HostLobbyActivity.BROADCAST_JOINED_GAME)
                        .putExtra(HostLobbyActivity.EXTRA_NUMBER_PLAYERS, assignedNumber));
            } else if (type.equals(JoinActivity.TYPE_CREATED_GAME)) {
                sendBroadcast(new Intent(JoinActivity.BROADCAST_CREATED_GAME));
            } else if (type.equals(JoinActivity.TYPE_CLOSED_GAME)) {
                sendBroadcast(new Intent(JoinActivity.BROADCAST_CLOSED_GAME));
            }
        }
    }
    // [END receive_message]

}
