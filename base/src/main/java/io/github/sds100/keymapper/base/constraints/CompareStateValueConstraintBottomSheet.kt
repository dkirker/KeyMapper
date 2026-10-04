package io.github.sds100.keymapper.base.constraints

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.sds100.keymapper.base.R
import io.github.sds100.keymapper.base.compose.KeyMapperTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareStateValueConstraintBottomSheet(viewModel: ChooseConstraintViewModel) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val state = viewModel.compareStateValueState ?: return

    CompareStateValueConstraintBottomSheet(
        sheetState = sheetState,
        state = state,
        onStateKeyChange = viewModel::onCompareStateValueStateKeyChange,
        onOperatorChange = viewModel::onCompareStateValueOperatorChange,
        onValueChange = viewModel::onCompareStateValueValueChange,
        onDismissRequest = viewModel::onDismissCompareStateValue,
        onDoneClick = viewModel::onDoneConfigCompareStateValueClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompareStateValueConstraintBottomSheet(
    sheetState: SheetState,
    state: CompareStateValueSheetState,
    onStateKeyChange: (String) -> Unit = {},
    onOperatorChange: (CompareStateValueOperatorEnum) -> Unit = {},
    onValueChange: (String) -> Unit = {},
    onDismissRequest: () -> Unit = {},
    onDoneClick: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
    ) {
        Column {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.constraint_compare_state_value_bottom_sheet_title),
                style = MaterialTheme.typography.headlineMedium,
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = state.stateKey,
                onValueChange = onStateKeyChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                label = {
                    Text(stringResource(R.string.constraint_compare_state_value_state_key))
                },
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for (operator in CompareStateValueOperatorEnum.entries) {
                    FilterChip(
                        selected = state.operator == operator,
                        onClick = { onOperatorChange(operator) },
                        label = { Text(operator.operator) },
                    )
                }
            }

            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = state.value,
                onValueChange = onValueChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                label = {
                    Text(stringResource(R.string.constraint_compare_state_value_value))
                },
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
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
                    enabled = state.isValid,
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onDoneClick()
                        }
                    },
                ) {
                    Text(stringResource(R.string.pos_done))
                }
            }

            Spacer(Modifier.height(16.dp))
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

        CompareStateValueConstraintBottomSheet(
            sheetState = sheetState,
            state = CompareStateValueSheetState(
                stateKey = "volume_mode",
                operator = CompareStateValueOperatorEnum.IS,
                value = "true"
            ),
        )
    }
}
