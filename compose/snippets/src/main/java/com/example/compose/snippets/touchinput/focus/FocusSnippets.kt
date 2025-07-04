/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.snippets.touchinput.focus

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusDirection.Companion.Down
import androidx.compose.ui.focus.FocusDirection.Companion.Right
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.Cancel
import androidx.compose.ui.focus.FocusRequester.Companion.Default
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component3
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component4
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.navigation.FocusExamplesDestination
import kotlinx.coroutines.launch

@Composable
fun FocusExamples(
    modifier: Modifier = Modifier,
    onNavigation: (FocusExamplesDestination) -> Unit = {}
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(FocusExamplesDestination.entries.toList()) {
            ListItem(
                modifier = Modifier.clickable(onClick = {
                    onNavigation(it)
                }),
                headlineContent = {
                    Text(it.title)
                }
            )
        }
    }
}

@Composable
internal fun Section(
    title: String,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(16.dp),
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    content: @Composable () -> Unit = {}
) {
    Column(
        modifier = modifier.focusGroup(),
        verticalArrangement = verticalArrangement
    ) {
        Text(title, style = style)
        content()
    }
}

@Composable
internal fun SubSection(
    title: String,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    style: TextStyle = MaterialTheme.typography.headlineSmall,
    content: @Composable () -> Unit = {}
) {
    Section(
        title = title,
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        style = style,
        content = content,
    )
}

@Preview
@Composable
private fun BasicSample() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // [START android_compose_touchinput_focus_horizontal]
        Column {
            Row {
                TextButton({ }) { Text("First field") }
                TextButton({ }) { Text("Second field") }
            }
            Row {
                TextButton({ }) { Text("Third field") }
                TextButton({ }) { Text("Fourth field") }
            }
        }
        // [END android_compose_touchinput_focus_horizontal]
    }
}

@Preview
@Composable
private fun BasicSample2() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // [START android_compose_touchinput_focus_vertical]
        Row {
            Column {
                TextButton({ }) { Text("First field") }
                TextButton({ }) { Text("Second field") }
            }
            Column {
                TextButton({ }) { Text("Third field") }
                TextButton({ }) { Text("Fourth field") }
            }
        }
        // [END android_compose_touchinput_focus_vertical]
    }
}

@Preview
@Composable
fun OverrideDefaultOrder() {
    // [START android_compose_touchinput_focus_override_refs]
    val (first, second, third, fourth) = remember { FocusRequester.createRefs() }
    // [END android_compose_touchinput_focus_override_refs]
    // [START android_compose_touchinput_focus_override]
    Column {
        Row {
            TextButton({}, Modifier.focusRequester(first)) { Text("First field") }
            TextButton({}, Modifier.focusRequester(third)) { Text("Third field") }
        }

        Row {
            TextButton({}, Modifier.focusRequester(second)) { Text("Second field") }
            TextButton({}, Modifier.focusRequester(fourth)) { Text("Fourth field") }
        }
    }
    // [END android_compose_touchinput_focus_override]

    // [START android_compose_touchinput_focus_override_use]
    Column {
        Row {
            TextButton(
                {},
                Modifier
                    .focusRequester(first)
                    .focusProperties { next = second }
            ) {
                Text("First field")
            }
            TextButton(
                {},
                Modifier
                    .focusRequester(third)
                    .focusProperties { next = fourth }
            ) {
                Text("Third field")
            }
        }

        Row {
            TextButton(
                {},
                Modifier
                    .focusRequester(second)
                    .focusProperties { next = third }
            ) {
                Text("Second field")
            }
            TextButton(
                {},
                Modifier
                    .focusRequester(fourth)
                    .focusProperties { next = first }
            ) {
                Text("Fourth field")
            }
        }
    }
    // [END android_compose_touchinput_focus_override_use]
}

@Preview
@Composable
fun OverrideTwoDimensionalOrder() {

    val (second, third, fourth) = remember { FocusRequester.createRefs() }

    // [START android_compose_touchinput_focus_override_2d]
    TextButton(
        onClick = {},
        modifier = Modifier
            .focusRequester(fourth)
            .focusProperties {
                down = third
                right = second
            }
    ) {}
    // [END android_compose_touchinput_focus_override_2d]
}

@Composable
private fun FocusGroup() {

    @Composable
    fun FilterChipA() {
    }

    @Composable
    fun FilterChipB() {
    }

    @Composable
    fun FilterChipC() {
    }

    @Composable
    fun SweetsCard(sweets: Int) {
    }

    val chocolates = 0

    // [START android_compose_touchinput_focus_group]
    LazyVerticalGrid(columns = GridCells.Fixed(4)) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(modifier = Modifier.focusGroup()) {
                FilterChipA()
                FilterChipB()
                FilterChipC()
            }
        }
        items(chocolates) {
            SweetsCard(sweets = it)
        }
    }
    // [END android_compose_touchinput_focus_group]
}

@Composable
private fun Focusable() {
    // [START android_compose_touchinput_focus_focusable]
    var color by remember { mutableStateOf(Green) }
    Box(
        Modifier
            .background(color)
            .onFocusChanged { color = if (it.isFocused) Blue else Green }
            .focusable()
    ) {
        Text("Focusable 1")
    }
    // [END android_compose_touchinput_focus_focusable]
}

@Composable
private fun Unfocusable() {
    // [START android_compose_touchinput_focus_unfocusable]
    var checked by remember { mutableStateOf(false) }

    Switch(
        checked = checked,
        onCheckedChange = { checked = it },
        // Prevent component from being focused
        modifier = Modifier
            .focusProperties { canFocus = false }
    )
    // [END android_compose_touchinput_focus_unfocusable]
}

@Composable
private fun RequestFocus() {
    // [START android_compose_touchinput_focus_request]
    val focusRequester = remember { FocusRequester() }
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier.focusRequester(focusRequester)
    )
    // [END android_compose_touchinput_focus_request]
}

@Composable
internal fun RequestFocus2() {
    // [START android_compose_touchinput_focus_request2]
    val focusRequester = remember { FocusRequester() }
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier.focusRequester(focusRequester)
    )

    Button(onClick = { focusRequester.requestFocus() }) {
        Text("Request focus on TextField")
    }
    // [END android_compose_touchinput_focus_request2]
}

@Composable
private fun Capture() {
    var text by remember { mutableStateOf("") }
    // [START android_compose_touchinput_focus_capture]
    val textField = FocusRequester()

    TextField(
        value = text,
        onValueChange = {
            text = it

            if (it.length > 3) {
                textField.captureFocus()
            } else {
                textField.freeFocus()
            }
        },
        modifier = Modifier.focusRequester(textField)
    )
    // [END android_compose_touchinput_focus_capture]
}

@Composable
private fun ModifierOrder() {

    val (item1, item2) = remember { FocusRequester.createRefs() }

    // [START android_compose_touchinput_focus_order_1]
    Modifier
        .focusProperties { right = item1 }
        .focusProperties { right = item2 }
        .focusable()
    // [END android_compose_touchinput_focus_order_1]
    // [START android_compose_touchinput_focus_order_2]
    Modifier
        .focusProperties { right = Default }
        .focusProperties { right = item1 }
        .focusProperties { right = item2 }
        .focusable()
    // [END android_compose_touchinput_focus_order_2]
}

// [START android_compose_touchinput_focus_order_overwrite]
@Composable
fun FancyButton(modifier: Modifier = Modifier) {
    Row(modifier.focusProperties { canFocus = false }) {
        Text("Click me")
        Button(onClick = { }) { Text("OK") }
    }
}
// [END android_compose_touchinput_focus_order_overwrite]

@Composable
private fun CallFancyButton() {
// [START android_compose_touchinput_focus_order_overwrite_call]
    FancyButton(Modifier.focusProperties { canFocus = true })
// [END android_compose_touchinput_focus_order_overwrite_call]
}

@Composable
private fun ModifierOrder2() {
    // [START android_compose_touchinput_focus_order_request_1]
    Box(
        Modifier
            .focusable()
            .focusRequester(Default)
            .onFocusChanged {}
    )
    // [END android_compose_touchinput_focus_order_request_1]
    // [START android_compose_touchinput_focus_order_request_2]
    Box(
        Modifier
            .onFocusChanged {}
            .focusRequester(Default)
            .focusable()
    )
    // [END android_compose_touchinput_focus_order_request_2]
    // [START android_compose_touchinput_focus_order_request_3]
    Box(
        Modifier
            .focusRequester(Default)
            .onFocusChanged {}
            .focusable()
    )
    // [END android_compose_touchinput_focus_order_request_3]
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun FocusRedirection() {
    // [START android_compose_touchinput_focus_redirect]
    val otherComposable = remember { FocusRequester() }

    Modifier.focusProperties {
        exit = { focusDirection ->
            when (focusDirection) {
                Right -> Cancel
                Down -> otherComposable
                else -> Default
            }
        }
    }
    // [END android_compose_touchinput_focus_redirect]
}

@Composable
internal fun FocusAdvancing() {
    // [START android_compose_touchinput_focus_advancing]
    val focusManager = LocalFocusManager.current
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier.onPreviewKeyEvent {
            when {
                KeyEventType.KeyUp == it.type && Key.Tab == it.key -> {
                    focusManager.moveFocus(FocusDirection.Next)
                    true
                }

                else -> false
            }
        }
    )
    // [END android_compose_touchinput_focus_advancing]
}

@Composable
private fun ReactToFocus() {
    // [START android_compose_touchinput_focus_react]
    var color by remember { mutableStateOf(White) }
    Card(
        modifier = Modifier
            .onFocusChanged {
                color = if (it.isFocused) Red else White
            }
            .border(5.dp, color)
    ) {}
    // [END android_compose_touchinput_focus_react]
}

// [START android_compose_touchinput_focus_advanced_cues]
private class MyHighlightIndicationNode(private val interactionSource: InteractionSource) :
    Modifier.Node(), DrawModifierNode {
    private var isFocused = false

    override fun onAttach() {
        coroutineScope.launch {
            var focusCount = 0
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is FocusInteraction.Focus -> focusCount++
                    is FocusInteraction.Unfocus -> focusCount--
                }
                val focused = focusCount > 0
                if (isFocused != focused) {
                    isFocused = focused
                    invalidateDraw()
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        if (isFocused) {
            drawRect(size = size, color = White, alpha = 0.2f)
        }
    }
}

// [END android_compose_touchinput_focus_advanced_cues]

// [START android_compose_touchinput_focus_indication]
object MyHighlightIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return MyHighlightIndicationNode(interactionSource)
    }

    override fun hashCode(): Int = -1

    override fun equals(other: Any?) = other === this
}
// [END android_compose_touchinput_focus_indication]

@Composable
private fun ApplyIndication() {
    // [START android_compose_touchinput_focus_apply_indication]
    var interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = MyHighlightIndication,
                enabled = true,
                onClick = { }
            )
    ) {
        Text("hello")
    }
    // [END android_compose_touchinput_focus_apply_indication]
}
