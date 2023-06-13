@file:OptIn(ExperimentalAnimationApi::class)

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        MainTab(tabs = listOf(
            TabComponent("ArtWork List") { ArtListContent() },
            TabComponent("ArtWork") { ArtWork() },
            TabComponent("Dice Roller") { DiceRoller() },
            TabComponent("Lemonade Maker") { LemonadeMaker() },
            TabComponent("Tip Calculator") { TipCalculator() }
        ))
    }
}

@Composable
fun MainTab(modifier: Modifier = Modifier, tabs: List<TabComponent>) {
    var tabIndex by remember { mutableStateOf(0) }
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        ScrollableTabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed() { index, title ->
                Tab(selected = index == tabIndex,
                    onClick = {
                        tabIndex = index
                },
                text = { Text(tabs[index].title, color = Color.White, fontSize = 16.sp) })
            }
        }
        tabs[tabIndex].content()
    }
}

data class TabComponent(val title: String, val content: @Composable () -> Unit) {}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DiceRoller(modifier: Modifier = Modifier) {
    var value by remember { mutableStateOf(1) }

    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(40.dp))
        AnimatedContent(value, modifier = Modifier.animateContentSize()) {
            Text(value.toString(), modifier = Modifier.shadow(2.dp, shape = CircleShape), fontSize = 34.sp)
        }
        Spacer(Modifier.height(20.dp))
        Button(onClick = {
            value = (1..6).random()
        }) {
            Text("Roll")
        }
        Spacer(Modifier.height(40.dp))
    }
}

expect fun getPlatformName(): String