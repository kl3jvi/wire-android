/*
 * Wire
 * Copyright (C) 2023 Wire Swiss GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */
package com.wire.android.ui.calling.controlbuttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wire.android.R
import com.wire.android.appLogger
import com.wire.android.model.ClickBlockParams
import com.wire.android.ui.common.button.WireButtonState
import com.wire.android.ui.common.button.WireSecondaryButton
import com.wire.android.ui.theme.wireDimensions
import com.wire.android.util.permission.rememberCallingRecordAudioBluetoothRequestFlow
import com.wire.android.util.ui.PreviewMultipleThemes

@Composable
fun StartCallButton(
    onPhoneButtonClick: () -> Unit,
    onPermanentPermissionDecline: () -> Unit,
    isCallingEnabled: Boolean
) {
    val audioBTPermissionCheck = AudioBluetoothPermissionCheckFlow(
        startCall = onPhoneButtonClick,
        onPermanentPermissionDecline = onPermanentPermissionDecline
    )

    WireSecondaryButton(
        onClick = audioBTPermissionCheck::launch,
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_phone),
                contentDescription = stringResource(R.string.content_description_conversation_phone_icon),
            )
        },
        state = if (isCallingEnabled) WireButtonState.Default else WireButtonState.Disabled,
        fillMaxWidth = false,
        minHeight = MaterialTheme.wireDimensions.spacing32x,
        minWidth = MaterialTheme.wireDimensions.spacing40x,
        clickBlockParams = ClickBlockParams(blockWhenSyncing = true, blockWhenConnecting = true),
        shape = RoundedCornerShape(size = MaterialTheme.wireDimensions.corner12x),
        contentPadding = PaddingValues(0.dp)
    )
}

@Composable
private fun AudioBluetoothPermissionCheckFlow(
    startCall: () -> Unit,
    onPermanentPermissionDecline: () -> Unit
) = rememberCallingRecordAudioBluetoothRequestFlow(
    onAudioBluetoothPermissionGranted = {
        appLogger.d("startCall - Permissions granted")
        startCall()
    },
    onAudioBluetoothPermissionDenied = { },
    onAudioBluetoothPermissionPermanentlyDenied = onPermanentPermissionDecline
)

@PreviewMultipleThemes
@Composable
fun PreviewStartCallButton() {
    StartCallButton(
        onPhoneButtonClick = {},
        onPermanentPermissionDecline = {},
        isCallingEnabled = true
    )
}