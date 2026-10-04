package io.github.sds100.keymapper.base.actions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.sds100.keymapper.base.R
import io.github.sds100.keymapper.base.compose.KeyMapperTheme
import io.github.sds100.keymapper.base.compose.LocalCustomColorsPalette
import io.github.sds100.keymapper.base.utils.getFullMessage
import io.github.sds100.keymapper.base.utils.ui.compose.KeyMapperSegmentedButtonRow
import io.github.sds100.keymapper.base.utils.ui.compose.RadioButtonText
import io.github.sds100.keymapper.base.utils.ui.compose.filledTonalButtonColorsError
import io.github.sds100.keymapper.common.utils.KMError
import io.github.sds100.keymapper.common.utils.KMResult
import io.github.sds100.keymapper.common.utils.Success
import io.github.sds100.keymapper.system.SystemError
import io.github.sds100.keymapper.system.permissions.Permission
import io.github.sds100.keymapper.system.settings.SettingType
import kotlinx.coroutines.launch

data class SetStateActionBottomSheetState(
    val stateKey: String,
    val value: String,
    val resetValueOnTimeout: Boolean,
    val resetTimeoutMillis: Int,
    val resetValue: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetStateActionBottomSheet(delegate: CreateActionDelegate) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (delegate.setStateActionBottomSheetState != null) {
        SetStateActionBottomSheet(
            sheetState = sheetState,
            state = delegate.setStateActionBottomSheetState!!,
            onDismissRequest = delegate::onDismissSetStateClick,
            onSetStateKeyChange = delegate::onSetStateKeyChange,
            onSetStateValueChange = delegate::onSetStateValueChange,
            onSetStateResetOnTimeoutChange = delegate::onSetStateResetOnTimeoutChange,
            onSetStateResetTimeoutChange = delegate::onSetStateResetTimeoutChange,
            onSetStateResetValueChange = delegate::onSetStateResetValueChange,
            onDoneClick = {
                scope.launch {
                    sheetState.hide()
                    delegate.onDoneSetStateClick()
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetStateActionBottomSheet(
    sheetState: SheetState,
    state: SetStateActionBottomSheetState,
    onDismissRequest: () -> Unit = {},
    onSetStateKeyChange: (String) -> Unit = {},
    onSetStateValueChange: (String) -> Unit = {},
    onSetStateResetOnTimeoutChange: (Boolean) -> Unit = {},
    onSetStateResetTimeoutChange: (Int) -> Unit = {},
    onSetStateResetValueChange: (String) -> Unit = {},
    onDoneClick: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()

    val stateKeyEmptyErrorString = stringResource(R.string.set_state_key_empty_error)
    val stateValueEmptyErrorString = stringResource(R.string.set_state_value_empty_error)
    val resetTimeoutInvalidError = stringResource(R.string.set_state_reset_timeout_invalid_error)
    val resetTimeoutFormatError = stringResource(R.string.set_state_reset_timeout_format_error)
    val resetValueEmptyErrorString = stringResource(R.string.set_state_reset_value_empty_error)

    var stateKeyError: String? by rememberSaveable { mutableStateOf(null) }
    var stateValueError: String? by rememberSaveable { mutableStateOf(null) }
    var resetTimeoutError: String? by rememberSaveable { mutableStateOf(null) }
    var resetValueError: String? by rememberSaveable { mutableStateOf(null) }

    LaunchedEffect(state) {
        if (!state.stateKey.isBlank()) {
            stateKeyError = null
        }

        if (!state.value.isBlank()) {
            stateValueError = null
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.set_state_bottom_sheet_title),
                style = MaterialTheme.typography.headlineMedium,
            )

            OutlinedTextField(
                value = state.stateKey,
                onValueChange = onSetStateKeyChange,
                label = { Text(stringResource(R.string.set_state_key_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                ),
                isError = stateKeyError != null,
                supportingText = {
                    if (stateKeyError != null) {
                        Text(
                            text = stateKeyError!!,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
            )

            OutlinedTextField(
                value = state.value,
                onValueChange = onSetStateValueChange,
                label = { Text(stringResource(R.string.set_state_value_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                ),
                isError = stateValueError != null,
                supportingText = {
                    if (stateValueError != null) {
                        Text(
                            text = stateValueError!!,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.set_state_reset_value_on_timeout_label),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Switch(
                    checked = state.resetValueOnTimeout,
                    onCheckedChange = {
                        onSetStateResetOnTimeoutChange(it)
                    }
                )
            }

            OutlinedTextField(
                value = state.resetTimeoutMillis.toString(),
                onValueChange = {
                    var timeout = -1
                    try {
                        timeout = it.toInt()
                    } catch (_: Exception) {
                        resetTimeoutError = resetTimeoutFormatError
                    }

                    if (timeout <= 0) {
                        resetTimeoutError = resetTimeoutFormatError
                    } else {
                        resetTimeoutError = null
                        onSetStateResetTimeoutChange(timeout)
                    }
                },
                label = { Text(stringResource(R.string.set_state_reset_timeout_millis_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
                isError = resetTimeoutError != null,
                supportingText = {
                    if (resetTimeoutError != null) {
                        Text(
                            text = resetTimeoutError!!,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
            )

            OutlinedTextField(
                value = state.resetValue,
                onValueChange = onSetStateResetValueChange,
                label = { Text(stringResource(R.string.set_state_reset_value_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                ),
                isError = resetValueError != null,
                supportingText = {
                    if (resetValueError != null) {
                        Text(
                            text = resetValueError!!,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
            )

            /*Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                if (state.testResult != null) {
                    val resultText: String = when (state.testResult) {
                        is Success -> stringResource(R.string.test_modify_setting_result_ok)
                        is KMError -> state.testResult.getFullMessage(LocalContext.current)
                    }

                    val textColor = when (state.testResult) {
                        is Success -> LocalCustomColorsPalette.current.green
                        is KMError -> MaterialTheme.colorScheme.error
                    }

                    Text(
                        modifier = Modifier.weight(1f),
                        text = resultText,
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                OutlinedButton(
                    onClick = {
                        var hasError = false

                        if (state.stateKey.isBlank()) {
                            stateKeyError = stateKeyEmptyErrorString
                            hasError = true
                        }

                        if (state.value.isBlank()) {
                            stateValueError = stateValueEmptyErrorString
                            hasError = true
                        }

                        if (!hasError) {
                            onTestClick()
                        }
                    },
                ) {
                    Text(stringResource(R.string.button_test_modify_setting))
                }
            }

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.modify_setting_disclaimer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )*/

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onDismissRequest()
                        }
                    },
                ) {
                    Text(stringResource(R.string.neg_cancel))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (state.stateKey.isBlank()) {
                            stateKeyError = stateKeyEmptyErrorString
                        }

                        if (state.value.isBlank()) {
                            stateValueError = stateValueEmptyErrorString
                        }

                        if (state.resetValueOnTimeout) {
                            if (state.resetTimeoutMillis <= 0) {
                                resetTimeoutError = resetTimeoutInvalidError
                            }

                            if (state.resetValue.isBlank()) {
                                resetValueError = resetValueEmptyErrorString
                            }
                        }

                        if (stateKeyError == null && stateValueError == null) {
                            onDoneClick()
                        }
                    },
                ) {
                    Text(stringResource(R.string.pos_done))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Preview() {
    KeyMapperTheme {
        val sheetState = SheetState(
            skipPartiallyExpanded = true,
            positionalThreshold = { 0f },
            velocityThreshold = { 0f },
        )

        SetStateActionBottomSheet(
            sheetState = sheetState,
            state = SetStateActionBottomSheetState(
                stateKey = "channel_mode",
                value = "1",
                resetValueOnTimeout = true,
                resetTimeoutMillis = 5000,
                resetValue = "0"
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewEmpty() {
    KeyMapperTheme {
        val sheetState = SheetState(
            skipPartiallyExpanded = true,
            positionalThreshold = { 0f },
            velocityThreshold = { 0f },
        )

        SetStateActionBottomSheet(
            sheetState = sheetState,
            state = SetStateActionBottomSheetState(
                stateKey = "",
                value = "",
                resetValueOnTimeout = false,
                resetTimeoutMillis = -1,
                resetValue = ""
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewTestLoading() {
    KeyMapperTheme {
        val sheetState = SheetState(
            skipPartiallyExpanded = true,
            positionalThreshold = { 0f },
            velocityThreshold = { 0f },
        )

        SetStateActionBottomSheet(
            sheetState = sheetState,
            state = SetStateActionBottomSheetState(
                stateKey = "adb_enabled",
                value = "1",
                resetValueOnTimeout = false,
                resetTimeoutMillis = -1,
                resetValue = ""
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewTestSuccess() {
    KeyMapperTheme {
        val sheetState = SheetState(
            skipPartiallyExpanded = true,
            positionalThreshold = { 0f },
            velocityThreshold = { 0f },
        )

        SetStateActionBottomSheet(
            sheetState = sheetState,
            state = SetStateActionBottomSheetState(
                stateKey = "adb_enabled",
                value = "1",
                resetValueOnTimeout = false,
                resetTimeoutMillis = -1,
                resetValue = ""
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewTestError() {
    KeyMapperTheme {
        val sheetState = SheetState(
            skipPartiallyExpanded = true,
            positionalThreshold = { 0f },
            velocityThreshold = { 0f },
        )

        SetStateActionBottomSheet(
            sheetState = sheetState,
            state = SetStateActionBottomSheetState(
                stateKey = "airplane_mode_on",
                value = "1",
                resetValueOnTimeout = false,
                resetTimeoutMillis = -1,
                resetValue = ""
            ),
        )
    }
}
